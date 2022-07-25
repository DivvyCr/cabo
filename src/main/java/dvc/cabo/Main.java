package dvc.cabo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import dvc.cabo.app.*;
import dvc.cabo.logic.CardPile;
import dvc.cabo.logic.Game;
import dvc.cabo.logic.Player;
import dvc.cabo.network.DataPacket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static final TurnEndEvent END_EVENT = new TurnEndEvent();
    private StackPane root = new StackPane();
    private TablePane tablePane;

    private Game game;
    private int myIdx;
    private Player player;
    private ArrayList<Player> opponents;

    private int numInitPeeks = 2;
    private int temp = 0;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private void connect(String ipAddress, int portNumber) {
	try {
	    socket = new Socket(ipAddress, portNumber);
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
	} catch (UnknownHostException e) {
	    System.err.println("Unknown host: " + ipAddress);
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Unable to connect to " + ipAddress);
	    System.exit(1);
	}
    }

    private void listenForGo(Stage stage) {
	new Thread() {
	    public void run() {
		try {
		    DataPacket res;
		    while (true) { // https://stackoverflow.com/questions/12684072/eofexception-when-reading-files-with-objectinputstream
			res = (DataPacket) in.readObject();
			if (res.info.startsWith("$GO:")) {
			    game = res.game;
			    myIdx = Integer.parseInt(res.info.substring(4));
			    break;
			}
		    }
		} catch (IOException e) {
		    System.out.println("Exception as control flow..");
		} catch (ClassNotFoundException e1) {
		    e1.printStackTrace();
		}

		Platform.runLater(() -> {
			stage.setScene(new Scene(root, 1600, 1600));
			initialPeeks();
		    });
	    }
	}.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
	stage.setTitle("DV // Cabo.");
	stage.show();

	ConnectPane cp = new ConnectPane();
	stage.setScene(new Scene(cp, 500, 500));

	cp.getConnectButton().setOnMouseClicked(event -> {
		connect(cp.getIpField().getText(), Integer.parseInt(cp.getPortField().getText()));
		listenForGo(stage);
	    });
    }

    private void initialPeeks() {
	HandPaneH initPeeksHand = new HandPaneH(game.getPlayers().get(myIdx).getHand());
	PeekPane initPeeksPane = new PeekPane();
	initPeeksPane.setHandView(initPeeksHand);
	initPeeksPane.getCue().setText("Peek two cards:");
	root.getChildren().add(initPeeksPane);

	for (CardView cv : initPeeksHand.getCardViews()) {
	    cv.setOnMouseClicked(e -> {
		    cv.setSeen();
		    numInitPeeks--; // Any better way, without class field?
		    if (numInitPeeks == 0) initPeeksPane.getCue().setText("Click to proceed.");
		});
	}

	initPeeksPane.setOnMouseClicked(e -> {
		if (numInitPeeks < 0) {
		    root.getChildren().remove(initPeeksPane);

		    Thread t = new Thread(() -> waitForPacket());
		    t.setDaemon(true);
		    t.start();
		}
	    });
    }

    private void setup() {
	player = game.getPlayers().get(myIdx);

	CardView discardView = new CardView(game.getDiscardPile().getTopCard());
	discardView.setSeen();

	opponents = (ArrayList<Player>) game.getPlayers().clone();
	opponents.remove(myIdx);

	tablePane = new TablePane(player, opponents,
				  new DeckView(new CardView(game.getDeck().getTopCard())),
				  new DeckView(discardView));
	root.getChildren().add(tablePane);
    }

    private void playTurn() {
	HandPaneH playerHand = new HandPaneH(player.getHand());
	tablePane.getDeckView().setOnMouseClicked(handleDrawnCard(false, playerHand, player));
	tablePane.getDiscardView().setOnMouseClicked(handleDrawnCard(true, playerHand, player));
    }

    private EventHandler<MouseEvent> handleDrawnCard(boolean isFromDiscard, HandPaneH playerHand, Player player1) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		CardPile drawFrom = isFromDiscard ? game.getDiscardPile() : game.getDeck();
		CardView drawnCardView = new CardView(drawFrom.getTopCard());
		drawnCardView.setSeen();

		DrawPane dp = new DrawPane(drawnCardView);
		dp.setHandView(playerHand);
		// dp.getCaboButton().setOnMouseClicked(ee -> {
		//	game.callCabo();
		//	dp.fireEvent(END_EVENT);
		//     });

		if (isFromDiscard) {
		    dp.getDiscardButton().setOnMouseClicked(eee -> dp.fireEvent(END_EVENT)); // Drawing from discard and discarding it makes no change.
		}
		else {
		    dp.getDiscardButton().setOnMouseClicked(eee -> {
			    game.useCard();
			    dp.fireEvent(END_EVENT);
			});

		    if (!game.getDeck().getTopCard().getAction().equals("")) {
			// Card is drawn from the deck and has an action (ie. action is available).
			dp.getActionButton().setText("Click to " + game.getDeck().getTopCard().getAction());
			dp.getActionButton().setOnMouseClicked(handleAction(dp, game.getDeck().getTopCard().getAction(), playerHand));
			dp.enableActionButton();
		    }
		}

		root.getChildren().add(dp);
		dp.addEventHandler(TurnEndEvent.TEST, ee -> {
			root.getChildren().remove(dp);

			CardView discardView = new CardView(game.getDiscardPile().getTopCard());
			discardView.setSeen();
			tablePane.getDiscardView().setTopCardView(discardView);

			tablePane.getDeckView().setOnMouseClicked(null);
			tablePane.getDiscardView().setOnMouseClicked(null);
			endTurnAndWait();
		    });

		for (CardView cv : playerHand.getCardViews()) {
		    cv.setOnMouseClicked(ee -> {
			    int cardIdx = playerHand.getCardViews().indexOf(cv);

			    if (isFromDiscard) {
				game.drawFromDiscard(cardIdx, player1);

				tablePane.getPlayerHand().setCardViewByIdx(cardIdx, tablePane.getDiscardView().getTopCardView());
			    } else {
				game.drawFromDeck(cardIdx, player1);

				tablePane.getPlayerHand().setCardViewByIdx(cardIdx, tablePane.getDeckView().getTopCardView());
				tablePane.getDeckView().setTopCardView(new CardView(game.getDeck().getTopCard()));

				drawnCardView.setHidden();
			    }

			    playerHand.setCardViewByIdx(cardIdx, drawnCardView);

			    dp.fireEvent(END_EVENT);
			});
		}
	    }
	};
    }

    private void waitForPacket() {
	try {
	    DataPacket res;
	    while (true) { // https://stackoverflow.com/questions/12684072/eofexception-when-reading-files-with-objectinputstream
		res = (DataPacket) in.readObject();
		if (res.info.startsWith("$NEXT")) {
		    game = res.game;
		    Platform.runLater(() -> setup());
		    break;
		} else if (res.info.startsWith("$WAIT")) {
		    game = res.game;
		    Platform.runLater(() -> setup());
		}
	    }
	} catch (IOException ex) {
	    System.out.println("Exception as control flow..");
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace();
	}

	Platform.runLater(() -> playTurn());
    }

    private void endTurnAndWait() {
	Thread t = new Thread(() -> {
		try {
		    out.writeObject(new DataPacket("$DONE", game));
		} catch (IOException e) { e.printStackTrace(); }

		waitForPacket();
	});
	t.setDaemon(true);
	t.start();
    }

    private void performAction(DrawPane dp, String action, Player player, Player chosenOpponent, String tpSide) {
	if (action.equals("SWAP")) {
	    HandPaneH ownHP = new HandPaneH(player.getHand());
	    HandPaneH oppHP = new HandPaneH(chosenOpponent.getHand());

	    SwapPane sp = new SwapPane(oppHP, ownHP);
	    root.getChildren().add(sp);

	    temp = -1;
	    sp.getCue().setText("Select opponent's card (ABOVE)");
	    for (CardView oppCV : oppHP.getCardViews()) {
		oppCV.setOnMouseClicked(ee -> {
			if (temp < 0) {
			    temp = oppHP.getCardViews().indexOf(oppCV);
			    sp.getCue().setText("Select own card (BELOW)");
			}
		    });
	    }
	    for (CardView ownCV : ownHP.getCardViews()) {
		ownCV.setOnMouseClicked(ee -> {
			if (temp >= 0) {
			    int ownIdx = ownHP.getCardViews().indexOf(ownCV);
			    player.swapCardsWithP(chosenOpponent, ownIdx, temp);

			    CardView oppCV = oppHP.getCardViews().get(temp);
			    if (tpSide.equals("L")) tablePane.getLeftHand().setCardViewByIdx(temp, ownCV);
			    else if (tpSide.equals("T")) tablePane.getTopHand().setCardViewByIdx(temp, ownCV);
			    else if (tpSide.equals("R")) tablePane.getRightHand().setCardViewByIdx(temp, ownCV);
			    tablePane.getPlayerHand().setCardViewByIdx(ownIdx, oppCV);

			    root.getChildren().remove(sp);
			    dp.fireEvent(END_EVENT);
			}
		    });
	    }
	} else if (action.equals("SPY")) {
	    viewCardFromPlayer(chosenOpponent);
	    dp.fireEvent(END_EVENT); // BUG: Breaks UI, since END_EVENT triggers `setup()`
	}
    }

    private EventHandler<MouseEvent> handleAction(DrawPane dp, String action, HandPaneH hp) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		game.useCard();

		if (action.equals("PEEK")) { // Don't need to select an opponent to peek OWN hand.
		    viewCardFromPlayer(player);
		    dp.fireEvent(END_EVENT); // BUG: Breaks UI, since END_EVENT triggers `setup()`
		} else {
		    HBox opponentSelection = new HBox();
		    Button bLeft;
		    Button bRight;
		    Button bTop;
		    switch (opponents.size()) {
		    case 1:
			performAction(dp, action, player, opponents.get(0), "T");
			break;
		    case 2:
			bLeft = new Button("Left");
			bLeft.setOnMouseClicked(ee -> performAction(dp, action, player, opponents.get(0), "L"));
			bRight = new Button("Right");
			bRight.setOnMouseClicked(ee -> performAction(dp, action, player, opponents.get(1), "R"));
			opponentSelection.getChildren().setAll(bLeft, bRight);
			dp.getPaneLayout().add(opponentSelection, 0, 4);
			break;
		    case 3:
			bLeft = new Button("Left");
			bLeft.setOnMouseClicked(ee -> performAction(dp, action, player, opponents.get(0), "L"));
			bTop = new Button("Top");
			bTop.setOnMouseClicked(ee -> performAction(dp, action, player, opponents.get(1), "T"));
			bRight = new Button("Right");
			bRight.setOnMouseClicked(ee -> performAction(dp, action, player, opponents.get(2), "R"));
			opponentSelection.getChildren().setAll(bLeft, bTop, bRight);
			dp.getPaneLayout().add(opponentSelection, 0, 4);
			break;
		    }
		}
	    }
	};
    }

    private void viewCardFromPlayer(Player player) {
	PeekPane pp = new PeekPane();
	HandPaneH hp = new HandPaneH(player.getHand());
	pp.setHandView(hp);
	root.getChildren().add(pp);

	temp = 0;
	for (CardView cv : hp.getCardViews()) {
	    cv.setOnMouseClicked(e -> {
		    if (temp < 0) root.getChildren().remove(pp);
		    cv.setSeen();
		    temp--;
		});
	}
    }

    public static void main(String[] args) { launch(args); }
}
