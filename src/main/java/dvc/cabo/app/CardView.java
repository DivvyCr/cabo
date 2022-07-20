package dvc.cabo.app;

import dvc.cabo.logic.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {

    private final Card card;
    private final Image SEEN;
    private final Image HIDDEN;

    public CardView(Card card) {
        SEEN = new Image(getClass().getResourceAsStream(card.getValue() + ".jpeg"));
        HIDDEN = new Image(getClass().getResourceAsStream("hidden.jpeg"));

        setPreserveRatio(true);
        setFitWidth(200);
        if (card.isFaceDown()) setImage(HIDDEN);
        else setImage(SEEN);

        this.card = card;
    }

    public void setSeen() {
        setImage(SEEN);
    }

    public void setHidden() {
        setImage(HIDDEN);
    }

}
