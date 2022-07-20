package dvc.cabo.app;

import javafx.scene.layout.StackPane;

public class DeckView extends StackPane {

    private CardView topCardView;

    public DeckView(CardView topCardView) {
        this.topCardView = topCardView;
        getChildren().setAll(topCardView);
    }

    public CardView getTopCardView() {
        return topCardView;
    }

    public void setTopCardView(CardView topCardView) {
        this.topCardView = topCardView;
        getChildren().setAll(topCardView);
    }

}
