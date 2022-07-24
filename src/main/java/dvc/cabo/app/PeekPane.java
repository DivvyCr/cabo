package dvc.cabo.app;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class PeekPane extends ActionPane {

    private final GridPane paneLayout;

    private HandPaneH handView;

    public PeekPane() {
	paneLayout = new GridPane();
	paneLayout.setVgap(30);
	paneLayout.setAlignment(Pos.CENTER);

	GridPane.setHalignment(getCue(), HPos.CENTER);
	paneLayout.add(getCue(), 0, 0);

	handView = new HandPaneH();
	GridPane.setHalignment(handView, HPos.CENTER);
	paneLayout.add(handView, 0, 1);

	getChildren().add(paneLayout);
    }

    public HandPaneH getHandView() {
	return handView;
    }

    public void setHandView(HandPaneH handView) {
	paneLayout.getChildren().remove(this.handView);
	this.handView = handView;
	paneLayout.add(handView, 0, 1);
    }

}
