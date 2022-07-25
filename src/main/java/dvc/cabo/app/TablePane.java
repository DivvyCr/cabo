package dvc.cabo.app;

import java.util.ArrayList;

import dvc.cabo.logic.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class TablePane extends BorderPane {

    private final GridPane centerSpace; // Area for deck, discard, and cue.

    // Currently, limiting to 1v1, for simplicity:
    private final HandPaneH playerHand, topHand;
    private final HandPaneV leftHand, rightHand;
    private final DeckView deckView, discardView;
    private final Text cue;

    public TablePane(Player player, ArrayList<Player> opponents, DeckView deckView, DeckView discardView) {
	this.deckView = deckView;
	this.discardView = discardView;

	playerHand = new HandPaneH(player.getHand());
	switch (opponents.size()) {
	case 1:
	    leftHand = null;
	    topHand = new HandPaneH(opponents.get(0).getHand());
	    rightHand = null;
	    break;
	case 2:
	    leftHand = new HandPaneV(opponents.get(0).getHand());
	    topHand = null;
	    rightHand = new HandPaneV(opponents.get(1).getHand());
	    break;
	case 3:
	    leftHand = new HandPaneV(opponents.get(0).getHand());
	    topHand = new HandPaneH(opponents.get(1).getHand());
	    rightHand = new HandPaneV(opponents.get(2).getHand());
	    break;
	default:
	    leftHand = null;
	    topHand = null;
	    rightHand = null;

	    System.out.println("Unknown number of opponents?");
	    System.exit(-1);
	}

	cue = new Text("...");
	cue.setFont(Font.font("Open Sans", FontWeight.BOLD, 24));
	cue.setFill(Color.BLACK);

	centerSpace = new GridPane();
	centerSpace.add(deckView, 0, 0);
	centerSpace.add(cue, 1, 0);
	centerSpace.add(discardView, 2, 0);
	centerSpace.setAlignment(Pos.CENTER);
	setCenter(centerSpace);

	setAlignment(playerHand, Pos.CENTER);
	setBottom(playerHand);

	if (leftHand != null) {
	    setAlignment(leftHand, Pos.CENTER);
	    setLeft(leftHand);
	}

	if (topHand != null) {
	    setAlignment(topHand, Pos.CENTER);
	    setTop(topHand);
	}

	if (rightHand != null) {
	    setAlignment(rightHand, Pos.CENTER);
	    setRight(rightHand);
	}

	setPadding(new Insets(200));
    }

    public HandPaneH getPlayerHand() {
	return playerHand;
    }

    public HandPaneV getLeftHand() {
	return leftHand;
    }

    public HandPaneH getTopHand() {
	return topHand;
    }

    public HandPaneV getRightHand() {
	return rightHand;
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
