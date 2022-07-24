package dvc.cabo.app;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class SwapPane extends ActionPane {

    private final GridPane paneLayout;

    private HandPaneH topHand;
    private HandPaneH bottomHand;

    public SwapPane(HandPaneH topHand, HandPaneH bottomHand) {
	paneLayout = new GridPane();
	paneLayout.setVgap(50);
	paneLayout.setAlignment(Pos.CENTER);

	this.topHand = topHand;
	GridPane.setHalignment(topHand, HPos.CENTER);
	paneLayout.add(topHand, 0, 0);

	GridPane.setHalignment(getCue(), HPos.CENTER);
	paneLayout.add(getCue(), 0, 1);

	this.bottomHand = bottomHand;
	GridPane.setHalignment(bottomHand, HPos.CENTER);
	paneLayout.add(bottomHand, 0, 2);

	getChildren().add(paneLayout);
    }

    public HandPaneH getTopHand() {
	return topHand;
    }

    public HandPaneH getBottomHand() {
	return bottomHand;
    }

}
