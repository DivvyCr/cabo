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

    private ArrayList<Player> players = new ArrayList<>();
    private CardPile deck;
    private CardPile discardPile;

    private int cardsClicked;

    @Override
    public void start(Stage primaryStage) throws Exception {
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

	Player mainPlayer = new Player("Player 1");
	players.add(mainPlayer);

	ArrayList<Card> mainHand = new ArrayList<>();
	for (int i = 0; i < 4; i++) mainHand.add(deck.drawTopCard());
	mainPlayer.setHand(mainHand);

	// ---

	StackPane r = new StackPane();

	GridPane gp = new GridPane();
	gp.setAlignment(Pos.CENTER);
	gp.setVgap(80);
	r.getChildren().add(gp);

	DeckView dv = new DeckView(deck);
	gp.add(dv, 0, 1);
	gp.add(new CardView(discardPile.getTopCard()), 2, 1); // PLACEHOLDER.

	HandPane hp = new HandPane(mainHand);
	gp.add(hp, 1, 2);

	// ---

	Text instruction = new Text("Peek cards:");
	instruction.setFont(instructionFont);
	instruction.setFill(Color.WHITE);
	ap.setTop(instruction);

	cardsClicked = 0;
	
	HandPane hpCopy = new HandPane(mainHand);
	for (CardView cv : hpCopy.getCardViews()) {
	    cv.setOnMouseClicked(e -> {
		    cv.setSeen();
		    cardsClicked++;
		});
	}

	ap.setBot(hpCopy);

	r.getChildren().add(ap);

	ap.setOnMouseClicked(e -> {
		if (cardsClicked == 2) {
		    for (CardView cv : hpCopy.getCardViews()) cv.setOnMouseClicked(null);

		    Button temp = new Button("Click to proceed.");
		    temp.setOnMouseClicked(ee -> {
			    r.getChildren().remove(ap);
			});
		    ap.setTop(temp);
		}
	    });

	// ---

	primaryStage.setTitle("Hello JavaFX!");
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

}

class DeckView extends ImageView {

    private CardPile deck;

    public DeckView(CardPile deck) {
	this.deck = deck;

	setPreserveRatio(true);
	setFitWidth(200);

	setImage(new Image(getClass().getResourceAsStream("hidden.jpeg")));

	// setOnMouseClicked((MouseEvent e) -> {
	//	System.out.println("Clicked! (Deck)");
	//	Card drawn = deck.drawTopCard();
	//	setImage(new Image(getClass().getResourceAsStream(drawn.getValue() + ".jpeg")));
	//     });
    }

}

class ActionPane extends StackPane {
    
    private final Rectangle DIM;

    private GridPane nodes;
    private Node topNode;
    private Node botNode;

    public ActionPane() {
	DIM = new Rectangle(0, 0, 2560, 1440);
	DIM.setFill(Color.BLACK);
	DIM.setOpacity(0.8);
	
	nodes = new GridPane();
	nodes.setVgap(50);
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

    public void setBot(Node node) {
	this.botNode = node;
	GridPane.setHalignment(botNode, HPos.CENTER);
	nodes.add(botNode, 0, 1);
    }

}
