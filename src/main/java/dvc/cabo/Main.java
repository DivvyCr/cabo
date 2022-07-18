package dvc.cabo;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private final static Rectangle DIM = new Rectangle(0, 0, 2560, 1440);
    static {
	DIM.setFill(Color.BLACK);
	DIM.setOpacity(0.6);
    }
    private CardView shownCard;


    @Override
    public void start(Stage primaryStage) throws Exception {
	CardPile deck = new CardPile();
	for (int i = 0; i <= 13; i++) {
	    if (i == 0 || i == 13) {
		deck.addCardToTop(new Card(i, true));
		deck.addCardToTop(new Card(i, true));
	    } else {
		deck.addCardToTop(new Card(i, true));
		deck.addCardToTop(new Card(i, true));
		deck.addCardToTop(new Card(i, true));
		deck.addCardToTop(new Card(i, true));
	    }
	}
	deck.shuffle();

	// ---

	StackPane r = new StackPane();

	GridPane gp = new GridPane();
	gp.setAlignment(Pos.CENTER);
	gp.setVgap(80);
	r.getChildren().add(gp);

	// Deck (on the left)
	DeckView dv = new DeckView(deck);
	gp.add(dv, 0, 0);

	// PLACEHOLDER for Discard (on the right)
	gp.add(new CardView(new Card(9, false)), 2, 0);

	// Local player's hand
	ArrayList<Card> hand = new ArrayList<>();
	hand.add(new Card(7, true));
	hand.add(new Card(1, true));
	hand.add(new Card(11, false));
	HandPane hp = new HandPane(hand);
	gp.add(hp, 1, 1);

	dv.setOnMouseClicked((MouseEvent e) -> {
		r.getChildren().add(DIM);

		Card drawn = deck.drawTopCard();
		drawn.flipCard();
		shownCard = new CardView(drawn);
		StackPane.setAlignment(shownCard, Pos.CENTER);
		r.getChildren().add(shownCard);
	    });

	DIM.setOnMouseClicked((MouseEvent e) -> {
		r.getChildren().remove(DIM);
		r.getChildren().remove(shownCard);
	    });

	primaryStage.setTitle("Hello JavaFX!");
	primaryStage.setScene(new Scene(r, 2560, 1440));
	primaryStage.show();
    }

    public static void main(String[] args) {
	launch(args);

//        Logic logic = new Logic();
//        logic.addPlayerByName("cabo.Player 1");
//        logic.addPlayerByName("cabo.Player 2");
//        logic.startGame();
    }
}

class CardView extends ImageView {

    private Card card;
    private Image seen;

    public CardView(Card card) {
	this.card = card;

	setPreserveRatio(true);
	setFitWidth(200);

	seen = new Image(getClass().getResourceAsStream(card.getValue() + ".jpeg"));
	setImage(seen);

	setOnMouseClicked((MouseEvent e) -> {
		System.out.println("Clicked! (Card)");
		flip();
	    });

	if (card.isFaceDown()) setImage(new Image(getClass().getResourceAsStream("hidden.jpeg")));
	else setImage(seen);
    }

    public void flip() {
	card.flipCard();

	if (card.isFaceDown()) setImage(new Image(getClass().getResourceAsStream("hidden.jpeg")));
	else setImage(seen);
    }

}

class HandPane extends HBox {

    public HandPane(ArrayList<Card> cards) {
	setStyle("-fx-border-color: #DDD; -fx-border-width: 4px; -fx-border-radius: 6px");
	setSpacing(10);
	setPadding(new Insets(15));

	for (Card card : cards) {
	    getChildren().add(new CardView(card));
	}
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
