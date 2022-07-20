package dvc.cabo;

import dvc.cabo.app.*;
import dvc.cabo.logic.CardPile;
import dvc.cabo.logic.Game;
import dvc.cabo.logic.Player;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private StackPane root = new StackPane();

    private Game game;
    private int numInitPeeks = 2;
    private int temp = 0;

    @Override
    public void start(Stage stage) throws Exception {
	//
	// Set-up the game.
	//

	game = new Game();
	game.addPlayerByName("P1");
	game.addPlayerByName("P2");
	game.startGame();

	Player player1 = game.getPlayers().get(0);
	Player player2 = game.getPlayers().get(1);

	//
	// Table set-up.
	//

	TablePane tablePane = new TablePane(new HandPane(player1.getHand()),
					    new HandPane(player2.getHand()),
					    new DeckView(new CardView(game.getDeck().getTopCard())),
					    new DeckView(new CardView(game.getDiscardPile().getTopCard())));
	root.getChildren().add(tablePane);

	stage.setTitle("DV // Cabo.");
	stage.setScene(new Scene(root, 2560, 1440));
	stage.show();

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

	//
	// Rough logic for DRAWING ACTION CARDS:
	// (to be incorporated into the above...)
	//

	//	if (drawn.getAction().equals("SWAP")) {
	//	    Button actionButton = new Button("Click to " + drawn.getAction());
	//	    actionButton.setOnMouseClicked(ee -> {
	//		    ap.clear();
	//		    cardsClicked = 0;
	//		    HandPane victimHandPane = new HandPane(player2.getHand());

	//		    instruction.setText("Pick opponent's card (ABOVE)");
	//		    instruction.setFill(Color.WHITE);
	//		    ap.setMid(instruction);

	//		    for (CardView cv : victimHandPane.getCardViews()) {
	//			cv.setOnMouseClicked(eee -> {
	//				if (cardsClicked == 0) {
	//				    cardIdx = victimHandPane.getCardViews().indexOf(cv);
	//				    instruction.setText("Pick own card (BELOW)");
	//				} else {
	//				    root.getChildren().remove(ap);
	//				    ap.clear();
	//				}
	//				cardsClicked++;
	//			    });
	//		    }

	//		    for (CardView cv : hp2.getCardViews()) {
	//			cv.setOnMouseClicked(eee -> {
	//				if (cardsClicked == 1) {
	//				    int ownIdx = hp2.getCardViews().indexOf(cv);

	//				    Card buffer = player2.getHand().get(cardIdx);
	//				    player2.getHand().set(cardIdx, player1.getHand().get(ownIdx));
	//				    player1.getHand().set(ownIdx, buffer);

	//				    HandPane newHP2 = new HandPane(player2.getHand());
	//				    gp.add(newHP2, 1, 0);
	//				    HandPane newHP1 = new HandPane(player1.getHand());
	//				    gp.add(newHP1, 1, 2);

	//				    root.getChildren().remove(ap);
	//				    ap.clear();
	//				}
	//				cardsClicked++;
	//			    });
	//		    }

	//		    ap.setTop(victimHandPane);
	//		    ap.setBot(hp2);
	//		});
	//	    ap.setMid(actionButton);
	//	}

	//	ap.setOnMouseClicked(null);
	//	root.getChildren().add(ap);
	//     });

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

			    CardView discardView = new CardView(game.getDiscardPile().getTopCard());
			    discardView.setSeen();
			    tablePane.getDiscardView().setTopCardView(discardView);

			    playerHand.setCardViewByIdx(cardIdx, drawnCardView);

			    root.getChildren().remove(dp);
			});
		}
	    }
	};
    }

    private EventHandler<MouseEvent> handleAction(TablePane tablePane, DrawPane dp, String action, HandPane hp) {
	return new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		root.getChildren().remove(dp);

		if (action.equals("PEEK")) {
		    viewCardFromPlayer(game.getPlayers().get(0));
		} else if (action.equals("SPY")) {
		    viewCardFromPlayer(game.getPlayers().get(1));
		}

		game.useCard();

		CardView discardView = new CardView(game.getDiscardPile().getTopCard());
		discardView.setSeen();
		tablePane.getDiscardView().setTopCardView(discardView);
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
