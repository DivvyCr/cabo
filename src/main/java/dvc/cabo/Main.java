package dvc.cabo;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Main extends Application {

    private final static Font instructionFont = Font.font("helvetica", FontWeight.BOLD, 20);

    private ActionPane ap = new ActionPane();
    private DeckView deckView;
    private DeckView discardView;
    private Text instruction = new Text();

    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIdx = 0;
    private CardPile deck;
    private CardPile discardPile;

    private int cardsClicked;
    private int cardIdx;
    private int cardIdx2;

    @Override
    public void start(Stage primaryStage) throws Exception {
	instruction.setFont(instructionFont);
	instruction.setFill(Color.WHITE);

	//
	// Set-up the game.
	//

	deck = new CardPile();
	for (int val = 0; val <= 13; val++) {
	    int numCards = (val == 0 || val == 13) ? 2 : 4;
	    for (int i = 0; i < numCards; i++) deck.addCardToTop(new Card(val, true));
	}
	deck.shuffle();

	discardPile = new CardPile();
	discardPile.addCardToTop(deck.drawTopCard());
	discardPile.getTopCard().flipCard();

	Player player1 = new Player("Player 1");
	players.add(player1);
	Player player2 = new Player("Player 2");
	players.add(player2);

	for (Player p : players) {
	    ArrayList<Card> pHand = new ArrayList<>();
	    for (int i = 0; i < 4; i++) pHand.add(deck.drawTopCard());
	    p.setHand(pHand);
	}

	// ---

	StackPane r = new StackPane();

	GridPane gp = new GridPane();
	gp.setAlignment(Pos.CENTER);
	gp.setVgap(80);
	r.getChildren().add(gp);

	deckView = new DeckView(new CardView(deck.getTopCard()));
	gp.add(deckView, 0, 1);

	discardView = new DeckView(new CardView(discardPile.getTopCard()));
	gp.add(discardView, 2, 1);

	HandPane rootHP1 = new HandPane(player1.getHand());
	gp.add(rootHP1, 1, 2);
	HandPane rootHP2 = new HandPane(player2.getHand());
	gp.add(rootHP2, 1, 0);

	//
	// Rough logic for INITIAL PEEKS:
	//

	instruction.setText("Peek cards:");
	ap.setTop(instruction);

	cardsClicked = 0;

	HandPane hp1 = new HandPane(player1.getHand());
	for (CardView cv : hp1.getCardViews()) {
	    cv.setOnMouseClicked(e -> {
		    cv.setSeen();
		    cardsClicked++;
		});
	}

	ap.setBot(hp1);

	r.getChildren().add(ap);

	ap.setOnMouseClicked(e -> {
		if (cardsClicked == 2) {
		    for (CardView cv : hp1.getCardViews()) cv.setOnMouseClicked(null);

		    Button temp = new Button("Click to proceed.");
		    temp.setOnMouseClicked(ee -> {
			    r.getChildren().remove(ap);
			    ap.clear();

			    instruction.setText("DRAW a card from the deck or the discard pile.");
			    instruction.setFill(Color.BLACK);
			    gp.add(instruction, 1, 1);
			});
		    ap.setTop(temp);
		}
	    });

	//
	// Rough logic for DRAWING CARDS:
	//

	HandPane hp2 = new HandPane(player1.getHand());

	deckView.setOnMouseClicked(e -> {
		Card drawn = deck.drawTopCard();

		for (CardView cv : hp2.getCardViews()) {
		    cv.setOnMouseClicked(ee -> {
			    cardIdx = hp2.getCardViews().indexOf(cv);

			    hp2.setCardViewByIdx(cardIdx, deckView.getTopCardView());
			    gp.add(hp2, 1, 2);

			    Card discard = player1.swapOwnCardForNewCard(cardIdx+1, drawn);
			    if (discard.isFaceDown()) discard.flipCard(); // Discard pile cards are face-up.
			    discardPile.addCardToTop(discard);
			    discardView.setTopCardView(new CardView(discard));
			    deckView.setTopCardView(new CardView(deck.getTopCard()));

			    player1.getHand().set(cardIdx, drawn);

			    r.getChildren().remove(ap);
			    ap.clear();
			});
		}

		CardView drawnCardView = new CardView(drawn);
		drawnCardView.setSeen(); // Does not affect real card state! (Just for viewing, as in real life.)
		ap.setTop(drawnCardView);
		ap.setBot(hp2);

		if (drawn.getAction().equals("PEEK")) {
		    Button actionButton = new Button("Click to " + drawn.getAction());
		    actionButton.setOnMouseClicked(ee -> {
			    ap.clear();
			    cardsClicked = 0;
			    for (CardView cv : hp2.getCardViews()) {
				cv.setOnMouseClicked(eee -> {
					if (cardsClicked < 1) cv.setSeen(); // Check that card isn't already seen.
					else {
					    cv.setHidden(); // Unless check above implemented, could lead to bug.
					    r.getChildren().remove(ap);
					    ap.clear();
					}
					cardsClicked++;
				    });
			    }
			    ap.setMid(hp2);
			});
		    ap.setMid(actionButton);
		}

		if (drawn.getAction().equals("SPY")) {
		    Button actionButton = new Button("Click to " + drawn.getAction());
		    actionButton.setOnMouseClicked(ee -> {
			    ap.clear();
			    cardsClicked = 0;
			    HandPane victimHandPane = new HandPane(player2.getHand());
			    for (CardView cv : victimHandPane.getCardViews()) {
				cv.setOnMouseClicked(eee -> {
					if (cardsClicked < 1) cv.setSeen(); // Check that card isn't already seen.
					else {
					    cv.setHidden(); // Unless check above implemented, could lead to bug.
					    r.getChildren().remove(ap);
					    ap.clear();
					}
					cardsClicked++;
				    });
			    }
			    ap.setMid(victimHandPane);
			});
		    ap.setMid(actionButton);
		}

		if (drawn.getAction().equals("SWAP")) {
		    Button actionButton = new Button("Click to " + drawn.getAction());
		    actionButton.setOnMouseClicked(ee -> {
			    ap.clear();
			    cardsClicked = 0;
			    HandPane victimHandPane = new HandPane(player2.getHand());

			    instruction.setText("Pick opponent's card (ABOVE)");
			    instruction.setFill(Color.WHITE);
			    ap.setMid(instruction);

			    for (CardView cv : victimHandPane.getCardViews()) {
				cv.setOnMouseClicked(eee -> {
					if (cardsClicked == 0) {
					    cardIdx = victimHandPane.getCardViews().indexOf(cv);
					    instruction.setText("Pick own card (BELOW)");
					} else {
					    r.getChildren().remove(ap);
					    ap.clear();
					}
					cardsClicked++;
				    });
			    }

			    for (CardView cv : hp2.getCardViews()) {
				cv.setOnMouseClicked(eee -> {
					if (cardsClicked == 1) {
					    cardIdx2 = hp2.getCardViews().indexOf(cv);

					    Card buffer = player2.getHand().get(cardIdx);
					    player2.getHand().set(cardIdx, player1.getHand().get(cardIdx2));
					    player1.getHand().set(cardIdx2, buffer);

					    HandPane newHP2 = new HandPane(player2.getHand());
					    gp.add(newHP2, 1, 0);
					    HandPane newHP1 = new HandPane(player1.getHand());
					    gp.add(newHP1, 1, 2);

					    r.getChildren().remove(ap);
					    ap.clear();
					}
					cardsClicked++;
				    });
			    }

			    ap.setTop(victimHandPane);
			    ap.setBot(hp2);
			});
		    ap.setMid(actionButton);
		}

		ap.setOnMouseClicked(null);
		r.getChildren().add(ap);
	    });

	discardView.setOnMouseClicked(e -> {
		Card drawn = discardPile.drawTopCard();

		for (CardView cv : hp2.getCardViews()) {
		    cv.setOnMouseClicked(ee -> {
			    cardIdx = hp2.getCardViews().indexOf(cv);

			    hp2.setCardViewByIdx(cardIdx, discardView.getTopCardView());
			    gp.add(hp2, 1, 2);

			    Card discard = player1.swapOwnCardForNewCard(cardIdx+1, drawn);
			    if (discard.isFaceDown()) discard.flipCard(); // Discard pile cards are face-up.
			    discardPile.addCardToTop(discard);
			    discardView.setTopCardView(new CardView(discard));

			    player1.getHand().set(cardIdx, drawn);

			    r.getChildren().remove(ap);
			    ap.clear();
			});
		}

		ap.setTop(new CardView(drawn));
		ap.setBot(hp2);
		ap.setOnMouseClicked(null);
		r.getChildren().add(ap);
	    });

	// ---

	primaryStage.setTitle("DV // Cabo.");
	primaryStage.setScene(new Scene(r, 2560, 1440));
	primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}

class CardView extends ImageView {

    private final Card card;
    private final Image SEEN;
    private final Image HIDDEN;

    public CardView(Card card) {
	SEEN = new Image(getClass().getResourceAsStream(card.getValue() + ".jpeg"));
	HIDDEN = new Image(getClass().getResourceAsStream("hidden.jpeg"));

	setPreserveRatio(true);
	setFitWidth(200);
	if (card.isFaceDown()) setImage(HIDDEN);
	else setImage(SEEN);

	this.card = card;
    }

    public void setSeen() {
	setImage(SEEN);
    }

    public void setHidden() {
	setImage(HIDDEN);
    }

}

class DeckView extends StackPane {

    private CardView topCardView;

    public DeckView(CardView topCardView) {
	this.topCardView = topCardView;
	getChildren().setAll(topCardView);
    }

    public CardView getTopCardView() {
	return topCardView;
    }

    public void setTopCardView(CardView topCardView) {
	this.topCardView = topCardView;
	getChildren().setAll(topCardView);
    }

}

class HandPane extends HBox {

    private ArrayList<CardView> cardViews = new ArrayList<>();

    public HandPane(ArrayList<Card> cards) {
	setStyle("-fx-border-color: #DDD; -fx-border-width: 4px; -fx-border-radius: 6px");
	setSpacing(10);
	setPadding(new Insets(15));

	for (Card card : cards) {
	    CardView cv = new CardView(card);
	    cardViews.add(cv);
	    getChildren().add(cv);
	}
    }

    public ArrayList<CardView> getCardViews() {
	return cardViews;
    }

    public void setCardViewByIdx(int idx, CardView cardView) {
	cardViews.set(idx, cardView);
	getChildren().setAll(cardViews);
    }

}

class ActionPane extends StackPane {

    private final Rectangle DIM;

    private GridPane nodes;
    private Node topNode;
    private Node midNode;
    private Node botNode;

    public ActionPane() {
	DIM = new Rectangle(0, 0, 2560, 1440);
	DIM.setFill(Color.BLACK);
	DIM.setOpacity(0.8);

	nodes = new GridPane();
	nodes.setVgap(30);
	nodes.setAlignment(Pos.CENTER);
	nodes.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

	getChildren().add(DIM);
	getChildren().add(nodes);
    }

    public void setTop(Node node) {
	this.topNode = node;
	GridPane.setHalignment(topNode, HPos.CENTER);
	nodes.add(topNode, 0, 0);
    }

    public void setMid(Node node) {
	this.midNode = node;
	GridPane.setHalignment(midNode, HPos.CENTER);
	nodes.add(midNode, 0, 1);
    }

    public void setBot(Node node) {
	this.botNode = node;
	GridPane.setHalignment(botNode, HPos.CENTER);
	nodes.add(botNode, 0, 2);
    }

    public void clear() {
	nodes.getChildren().clear();
    }

}
