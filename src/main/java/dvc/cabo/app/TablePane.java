package dvc.cabo.app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class TablePane extends BorderPane {

    private final BorderPane centerSpace; // Area for deck, discard, and cue.

    // Currently, limiting to 1v1, for simplicity:
    private final HandPane playerHand, topHand; // , leftHand, rightHand;
    private final DeckView deckView, discardView;
    private final Text cue;

    public TablePane(HandPane playerHand, HandPane topHand, DeckView deckView, DeckView discardView) {
	this.playerHand = playerHand;
	this.topHand = topHand;
	this.deckView = deckView;
	this.discardView = discardView;

	cue = new Text("...");
	cue.setFont(Font.font("Open Sans", FontWeight.BOLD, 24));
	cue.setFill(Color.BLACK);

	centerSpace = new BorderPane(cue, null, discardView, null, deckView);
	setCenter(centerSpace);

	setAlignment(playerHand, Pos.CENTER);
	setBottom(playerHand);

	setAlignment(topHand, Pos.CENTER);
	setTop(topHand);

	setPadding(new Insets(200));
    }

    public HandPane getPlayerHand() {
	return playerHand;
    }

    public HandPane getTopHand() {
	return topHand;
    }

    public DeckView getDeckView() {
	return deckView;
    }

    public DeckView getDiscardView() {
	return discardView;
    }

    public Text getCue() {
	return cue;
    }

}
