package dvc.cabo.app;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class DrawPane extends ActionPane {

    private final GridPane paneLayout;

    private CardView cardView;
    private HandPane handPane;

    public DrawPane(CardView drawnCardView) {
	paneLayout = new GridPane();
	paneLayout.setVgap(30);
	paneLayout.setAlignment(Pos.CENTER);

	GridPane.setHalignment(getCue(), HPos.CENTER);
	paneLayout.add(getCue(), 0, 0);

	this.cardView = drawnCardView;
	GridPane.setHalignment(cardView, HPos.CENTER);
	paneLayout.add(cardView, 0, 1);

	handPane = new HandPane();
	GridPane.setHalignment(handPane, HPos.CENTER);
	paneLayout.add(handPane, 0, 2);

	getChildren().add(paneLayout);
    }

    public CardView getCardView() {
	return cardView;
    }

    public void setCardView(CardView cardView) {
	paneLayout.getChildren().remove(this.handPane);
	this.cardView = cardView;
	paneLayout.add(cardView, 0, 1);
    }

    public HandPane getHandView() {
	return handPane;
    }

    public void setHandView(HandPane handPane) {
	paneLayout.getChildren().remove(this.handPane);
	this.handPane = handPane;
	paneLayout.add(handPane, 0, 2);
    }

}
