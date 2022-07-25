package dvc.cabo.app;

import java.util.ArrayList;

import dvc.cabo.logic.Card;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class HandPane extends FlowPane {

    private ArrayList<CardView> cardViews = new ArrayList<>();

    public HandPane() {
	this(new ArrayList<Card>());
    }

    public HandPane(ArrayList<Card> cards) {
	setHgap(5);
	setVgap(5);
	setPrefWrapLength(620); // Placeholder.
	setStyle("-fx-border-color: #DDD; -fx-border-width: 2px; -fx-border-radius: 6px");
	setAlignment(Pos.CENTER);
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

    public void setOnClickCardView(HandFunction test) {
	for (CardView cv : cardViews) {
	    test.set(cv);
	}
    }

}
