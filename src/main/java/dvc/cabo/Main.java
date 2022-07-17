package dvc.cabo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

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
