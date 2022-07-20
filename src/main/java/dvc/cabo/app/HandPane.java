package dvc.cabo.app;

import java.util.ArrayList;

import dvc.cabo.logic.Card;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class HandPane extends HBox {

    private ArrayList<CardView> cardViews = new ArrayList<>();

    public HandPane() {
	this(new ArrayList<Card>());
    }

    public HandPane(ArrayList<Card> cards) {
	setStyle("-fx-border-color: #DDD; -fx-border-width: 4px; -fx-border-radius: 6px");
	setSpacing(10);
	setPadding(new Insets(15));
	setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

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
