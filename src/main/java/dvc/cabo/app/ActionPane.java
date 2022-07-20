package dvc.cabo.app;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ActionPane extends StackPane {

    private GridPane nodes;
    private Node topNode;
    private Node midNode;
    private Node botNode;

    public ActionPane() {
        Rectangle DIM = new Rectangle(0, 0, 2560, 1440);
        DIM.setFill(Color.BLACK);
        DIM.setOpacity(0.8);
        getChildren().add(DIM);

        nodes = new GridPane();
        nodes.setVgap(30);
        nodes.setAlignment(Pos.CENTER);
        nodes.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        getChildren().add(nodes);
    }

    public void setTop(Node node) {
        this.topNode = node;
        GridPane.setHalignment(topNode, HPos.CENTER);
        nodes.add(topNode, 0, 0);
    }

    public void setMid(Node node) {
        this.midNode = node;
        GridPane.setHalignment(midNode, HPos.CENTER);
        nodes.add(midNode, 0, 1);
    }

    public void setBot(Node node) {
        this.botNode = node;
        GridPane.setHalignment(botNode, HPos.CENTER);
        nodes.add(botNode, 0, 2);
    }

    public void clear() {
        nodes.getChildren().clear();
    }

}
