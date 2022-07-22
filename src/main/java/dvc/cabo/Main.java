package dvc.cabo;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import dvc.cabo.app.*;
import dvc.cabo.logic.CardPile;
import dvc.cabo.logic.Game;
import dvc.cabo.logic.Player;
import dvc.cabo.network.DataPacket;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static final TurnEndEvent END_EVENT = new TurnEndEvent();
    private StackPane root = new StackPane();

    private Game game;
    private Player player;
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

    @Override
    public void start(Stage stage) throws Exception {
	stage.setTitle("DV // Cabo.");
	stage.show();

	ConnectPane cp = new ConnectPane();
	stage.setScene(new Scene(cp, 500, 500));

	cp.getConnectButton().setOnMouseClicked(event -> {
		connect("localhost", 9966);
		// connect(cp.getIpField().getText(), Integer.parseInt(cp.getPortField().getText()));

		try {
		    DataPacket res;
		    while (true) { // https://stackoverflow.com/questions/12684072/eofexception-when-reading-files-with-objectinputstream
			res = (DataPacket) in.readObject();
			if (res.info.startsWith("$GO:")) {
			    stage.setScene(new Scene(root, 2560, 1440));
			    game = res.game;
			    player = res.game.getPlayers().get(Integer.parseInt(res.info.substring(4)));
			    temp();
			    break;
			}
		    }
		} catch (IOException e) {
		    System.out.println("Exception as control flow..");
		    // e.printStackTrace();
		} catch (ClassNotFoundException e1) {
		    e1.printStackTrace();
		}
	    });

	//
	// Set-up the game.
	//

	// game = new Game();
	// game.addPlayerByName("P1");
	// game.addPlayerByName("P2");
	// game.startGame();



    }

    private void temp() {
	Player player1 = player;
	Player player2 = game.getPlayers().indexOf(player) == 0 ? game.getPlayers().get(1) : game.getPlayers().get(0);

	//
	// Table set-up.
	//

	TablePane tablePane = new TablePane(new HandPane(player1.getHand()),
					    new HandPane(player2.getHand()),
					    new DeckView(new CardView(game.getDeck().getTopCard())),
					    new DeckView(new CardView(game.getDiscardPile().getTopCard())));
	root.getChildren().add(tablePane);

	//
	// Logic for INITIAL PEEKS:
	//

	HandPane initPeeksHand = new HandPane(player1.getHand());
	PeekPane initPeeksPane = new PeekPane();
	initPeeksPane.setHandView(initPeeksHand);
	for (CardView cv : initPeeksHand.getCardViews()) {
	    cv.setOnMouseClicked(e -> {
		    cv.setSeen();
		    numInitPeeks--; // Any better way, without class field?
		    if (numInitPeeks == 0) initPeeksPane.getCue().setText("Click to proceed.");
		});
	}
	initPeeksPane.setOnMouseClicked(e -> {
		if (numInitPeeks < 0) root.getChildren().remove(initPeeksPane);
	    });
	initPeeksPane.getCue().setText("Peek two cards:");
	root.getChildren().add(initPeeksPane);

	//
	// Logic for DRAWING CARDS:
	//

	HandPane playerHand = new HandPane(player1.getHand());
	tablePane.getDeckView().setOnMouseClicked(handleDrawnCard(false, tablePane, playerHand, player1));
	tablePane.getDiscardView().setOnMouseClicked(handleDrawnCard(true, tablePane, playerHand, player1));
    }

    private EventHandler<MouseEvent> handleDrawnCard(boolean isFromDiscard, TablePane tablePane, HandPane playerHand, Player player1) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		CardPile drawFrom = isFromDiscard ? game.getDiscardPile() : game.getDeck();
		CardView drawnCardView = new CardView(drawFrom.getTopCard());
		drawnCardView.setSeen();

		DrawPane dp = new DrawPane(drawnCardView);
		dp.setHandView(playerHand);
		if (!isFromDiscard && !game.getDeck().getTopCard().getAction().equals("")) {
		    // Drawn card will have an ACTION.
		    dp.getActionButton().setText("Click to " + game.getDeck().getTopCard().getAction());
		    dp.getActionButton().setOnMouseClicked(handleAction(tablePane, dp, game.getDeck().getTopCard().getAction(), playerHand));
		    dp.enableActionButton();
		}

		root.getChildren().add(dp);
		dp.addEventHandler(TurnEndEvent.TEST, ee -> {
			root.getChildren().remove(dp);

			CardView discardView = new CardView(game.getDiscardPile().getTopCard());
			discardView.setSeen();
			tablePane.getDiscardView().setTopCardView(discardView);
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

    private EventHandler<MouseEvent> handleAction(TablePane tablePane, DrawPane dp, String action, HandPane hp) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		game.useCard();
		dp.fireEvent(END_EVENT);

		if (action.equals("SWAP")) {
		    HandPane ownHP = new HandPane(game.getPlayers().get(0).getHand());
		    HandPane oppHP = new HandPane(game.getPlayers().get(1).getHand());

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
				    game.getPlayers().get(0).swapCardsWithP(game.getPlayers().get(1), ownIdx+1, temp+1);

				    CardView oppCV = tablePane.getTopHand().getCardViews().get(temp);
				    tablePane.getTopHand().setCardViewByIdx(temp, ownCV);
				    tablePane.getPlayerHand().setCardViewByIdx(ownIdx, oppCV);

				    root.getChildren().remove(sp);
				}
			    });
		    }
		} else if (action.equals("PEEK")) {
		    viewCardFromPlayer(game.getPlayers().get(0));
		} else if (action.equals("SPY")) {
		    viewCardFromPlayer(game.getPlayers().get(1));
		}
	    }
	};
    }

    private void viewCardFromPlayer(Player player) {
	PeekPane pp = new PeekPane();
	HandPane hp = new HandPane(player.getHand());
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
