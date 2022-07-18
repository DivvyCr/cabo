package dvc.cabo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
	GridPane root = new GridPane();
	root.setAlignment(Pos.CENTER);

	HandPane hp = new HandPane();
	hp.addCard(new Card(7, true));
	hp.addCard(new Card(1, true));
	hp.addCard(new Card(11, false));

	root.add(hp, 0, 0);

	primaryStage.setTitle("Hello JavaFX!");
	primaryStage.setScene(new Scene(root, 2560, 1440));
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
		System.out.println("Clicked!");
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

    public HandPane() {
	setStyle("-fx-border-color: #DDD; -fx-border-width: 4px; -fx-border-radius: 6px");
	setSpacing(10);
	setPadding(new Insets(15));
    }

    public void addCard(Card card) {
	getChildren().add(new CardView(card));
    }

}
