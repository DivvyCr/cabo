package dvc.cabo.app;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

abstract class ActionPane extends StackPane {

    private Text cue;

    public ActionPane() {
	Rectangle DIM = new Rectangle(0, 0, 2560, 1440);
	DIM.setFill(Color.BLACK);
	DIM.setOpacity(0.8);
	getChildren().add(DIM);

	cue = new Text();
	cue.setFont(Font.font("Open Sans", FontWeight.BOLD, 32));
	cue.setFill(Color.WHITE);
    }

    public Text getCue() {
	return cue;
    }

}
