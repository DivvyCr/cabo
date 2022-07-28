package dvc.cabo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import dvc.cabo.app.CardView;
import dvc.cabo.app.ConnectPane;
import dvc.cabo.app.DeckView;
import dvc.cabo.app.DrawPane;
import dvc.cabo.app.HandPane;
import dvc.cabo.app.PeekPane;
import dvc.cabo.app.SwapPane;
import dvc.cabo.app.TablePane;
import dvc.cabo.logic.Card;
import dvc.cabo.logic.CardPile;
import dvc.cabo.logic.Game;
import dvc.cabo.logic.Player;
import dvc.cabo.network.DataPacket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

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

    private Stage stage;

    private void connect(String ipAddress, int portNumber, String name) {
	try {
	    socket = new Socket(InetAddress.getByName(ipAddress), portNumber);
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());

	    out.writeObject(new DataPacket("$NAME:" + name, null));
	} catch (UnknownHostException e) {
	    System.err.println("Unknown host: " + ipAddress);
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Unable to connect to " + ipAddress);
	    System.exit(1);
	}
    }

    @Override
    public void start(Stage stage) throws Exception {
	this.stage = stage;
	stage.setTitle("DV // Cabo.");
	stage.show();

	ConnectPane cp = new ConnectPane();
	stage.setScene(new Scene(cp, 500, 500));

	cp.getConnectButton().setOnMouseClicked(event -> {
		connect(cp.getIpField().getText(),
			Integer.parseInt(cp.getPortField().getText()),
			cp.getNameField().getText());
		waitForGo(stage);
	    });
    }

    private void renderInitPeeks() {
	HandPane initPeeksHand = new HandPane(game.getPlayers().get(myIdx).getHand());
	PeekPane initPeeksPane = new PeekPane();
	initPeeksPane.setHandView(initPeeksHand);
	initPeeksPane.getCue().setText("Peek two cards:");
	root.getChildren().add(initPeeksPane);

	initPeeksHand.setOnClickCardView(cv -> {
		cv.setSeen();
		numInitPeeks--; // Any better way, without class field?
		if (numInitPeeks == 0) initPeeksPane.getCue().setText("Click to proceed.");
	    });

	initPeeksPane.setOnMouseClicked(e -> {
		if (numInitPeeks < 0) {
		    root.getChildren().remove(initPeeksPane);

		    Thread t = new Thread(() -> waitTurnWhileUpdating());
		    t.setDaemon(true);
		    t.start();
		}
	    });
    }

    private void renderTable() {
	tablePane = new TablePane(player, opponents,
				  new DeckView(new CardView(game.getDeck().getTopCard())),
				  new DeckView(new CardView(game.getDiscardPile().getTopCard())));
	root.getChildren().setAll(tablePane); // Could be a problem with using `setAll()`?
    }

    private void playTurn() {
	tablePane.getDeckView().setOnMouseClicked(handleDrawnCard(false));
	tablePane.getDiscardView().setOnMouseClicked(handleDrawnCard(true));
	tablePane.getCaboButton().setOnMouseClicked(event -> {
		Thread t = new Thread(() -> {
			try {
			    out.writeObject(new DataPacket("$CABO", game));
			} catch (IOException e) { e.printStackTrace(); }

			waitTurnWhileUpdating();
		});
		t.setDaemon(true);
		t.start();
	    });
    }

    private void renderLeaderboard() {
	GridPane leaderboard = new GridPane();
	leaderboard.setAlignment(Pos.CENTER);
	leaderboard.setHgap(3);
	leaderboard.setVgap(5);

	int lbCount = 0;
	for (Map.Entry<Player, Integer> lbEntry : game.getScores().entrySet()) {
	    Text playerName = new Text(lbEntry.getKey().getName() + ":");
	    Text playerScore = new Text(Integer.toString(lbEntry.getValue()));
	    GridPane.setHalignment(playerName, HPos.RIGHT);
	    leaderboard.add(playerName, 0, lbCount);
	    leaderboard.add(playerScore, 1, lbCount);
	    lbCount++;
	}

	stage.setScene(new Scene(leaderboard, 500, 500));
    }

    private EventHandler<MouseEvent> handleDrawnCard(boolean isFromDiscard) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent deckViewEvent) {
		HandPane playerHand = new HandPane(player.getHand());

		CardPile drawFrom = isFromDiscard ? game.getDiscardPile() : game.getDeck();
		CardView drawnCardView = new CardView(drawFrom.getTopCard());
		drawnCardView.setSeen();

		DrawPane dp = new DrawPane(drawnCardView);
		dp.setHandView(playerHand);
		root.getChildren().add(dp);

		if (isFromDiscard) {
		    dp.getDiscardButton().setOnMouseClicked(e -> endTurn(dp)); // Drawing from discard and discarding it makes no change.
		} else {
		    dp.getDiscardButton().setOnMouseClicked(e -> {
			    game.useCard();
			    endTurn(dp);
			});

		    if (game.getDeck().getTopCard().getAction() != Card.Action.NONE) {
			// Card is drawn from the deck and has an action (ie. action is available).
			dp.enableActionButton();
			dp.getActionButton().setText("Click to " + game.getDeck().getTopCard().getAction());
			dp.getActionButton().setOnMouseClicked(handleAction(dp, game.getDeck().getTopCard().getAction(), playerHand));
		    }
		}

		playerHand.setOnClickCardView(cv -> {
			int cardIdx = playerHand.getCardViews().indexOf(cv);
			if (isFromDiscard) {
			    game.drawFromDiscard(cardIdx, player);
			} else {
			    game.drawFromDeck(cardIdx, player);
			}
			// Instantly render the change locally, to provide feedback on turn's end:
			tablePane.getPlayerHand().setCardViewByIdx(cardIdx, ((DeckView) deckViewEvent.getSource()).getTopCardView());
			// While other re-rendering is done when server sends next instruction, via `renderTable()`:
			endTurn(dp);
		    });
	    }
	};
    }

    private void endTurn(DrawPane dp) {
	root.getChildren().remove(dp);

	CardView discardView = new CardView(game.getDiscardPile().getTopCard());
	discardView.setSeen();
	tablePane.getDiscardView().setTopCardView(discardView);

	tablePane.getDeckView().setOnMouseClicked(null);
	tablePane.getDiscardView().setOnMouseClicked(null);
	endTurnAndWait();
    }

    private void waitForGo(Stage stage) {
	Thread t = new Thread(() -> {
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
			stage.setScene(new Scene(root, 1550, 1550)); // 1550 makes a nice border around ActionPane's 1500
			renderInitPeeks();
		    });
	});
	t.setDaemon(true);
	t.start();
    }

    @SuppressWarnings("unchecked") // Hide annoying warning about type-casting the cloned `ArrayList`.
    private void waitTurnWhileUpdating() {
	try {
	    DataPacket res;
	    while (true) { // https://stackoverflow.com/questions/12684072/eofexception-when-reading-files-with-objectinputstream
		res = (DataPacket) in.readObject();
		game = res.game;
		player = game.getPlayers().get(myIdx);

		opponents = (ArrayList<Player>) game.getPlayers().clone();
		opponents.remove(myIdx);

		Platform.runLater(() -> renderTable());

		if (res.info.startsWith("$NEXT")) {
		    Platform.runLater(() -> playTurn());
		    break;
		} else if (res.info.startsWith("$END")) {
		    Platform.runLater(() -> renderLeaderboard());
		    break;
		}
	    }
	} catch (IOException ex) {
	    System.out.println("Exception as control flow..");
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace();
	}
    }

    private void endTurnAndWait() {
	Thread t = new Thread(() -> {
		try {
		    out.writeObject(new DataPacket("$DONE", game));
		} catch (IOException e) { e.printStackTrace(); }

		waitTurnWhileUpdating();
	});
	t.setDaemon(true);
	t.start();
    }

    private void performAction(DrawPane dp, Card.Action action, Player chosenOpponent, HandPane tableOppHP) {
	if (action == Card.Action.SWAP) {
	    HandPane ownHP = new HandPane(player.getHand());
	    HandPane oppHP = new HandPane(chosenOpponent.getHand());

	    SwapPane sp = new SwapPane(oppHP, ownHP);
	    root.getChildren().add(sp);

	    temp = -1;
	    sp.getCue().setText("Select opponent's card (ABOVE)");
	    oppHP.setOnClickCardView(oppCV -> {
		    if (temp < 0) {
			temp = oppHP.getCardViews().indexOf(oppCV);
			sp.getCue().setText("Select own card (BELOW)");
		    }
		});
	    ownHP.setOnClickCardView(ownCV -> {
		    if (temp >= 0) {
			int ownIdx = ownHP.getCardViews().indexOf(ownCV);
			player.swapCardsWithP(chosenOpponent, ownIdx, temp);

			CardView oppCV = tableOppHP.getCardViews().get(temp);
			tableOppHP.setCardViewByIdx(temp, ownCV);
			tablePane.getPlayerHand().setCardViewByIdx(ownIdx, oppCV);

			root.getChildren().remove(sp);
			endTurn(dp);
		    }
		});
	} else if (action == Card.Action.SPY) {
	    viewCardFromPlayer(dp, chosenOpponent);
	}
    }

    private EventHandler<MouseEvent> handleAction(DrawPane dp, Card.Action action, HandPane hp) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		game.useCard();

		if (action == Card.Action.PEEK) { // Don't need to select an opponent to peek OWN hand.
		    viewCardFromPlayer(dp, player);
		} else {
		    root.getChildren().remove(dp);

		    if (opponents.size() == 1) performAction(dp, action, opponents.get(0), tablePane.getTopHand());
		    else {
			root.getChildren().remove(dp); // In order to choose an opposing hand, we must hide the DrawPane.
			tablePane.getDeckView().setVisible(false);    // Reset via `renderTable()`
			tablePane.getDiscardView().setVisible(false); // ^
			tablePane.getCaboButton().setVisible(false);  // ^
			tablePane.getCue().setText("Select an opponent's hand.");
			tablePane.setOnClickHandPane((oppHP, oppIdx) -> performAction(dp, action, opponents.get(oppIdx), oppHP));
		    }
		}
	    }
	};
    }

    private void viewCardFromPlayer(DrawPane dp, Player player) {
	PeekPane pp = new PeekPane();
	HandPane hp = new HandPane(player.getHand());
	pp.setHandView(hp);
	root.getChildren().add(pp);

	temp = 0;
	hp.setOnClickCardView(cv -> {
		if (temp < 0) {
		    root.getChildren().remove(pp);
		    endTurn(dp);
		}
		cv.setSeen();
		temp--;
	    });
    }

    public static void main(String[] args) { launch(args); }
}
