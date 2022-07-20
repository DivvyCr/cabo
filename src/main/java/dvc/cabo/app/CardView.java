package dvc.cabo.app;

import dvc.cabo.logic.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {

    private final Image HIDDEN;
    private final Image SEEN;

    public CardView(Card card) {
	setPreserveRatio(true);
	setFitWidth(200);

	SEEN = new Image(getClass().getResourceAsStream(card.getValue() + ".jpeg"));
	HIDDEN = new Image(getClass().getResourceAsStream("hidden.jpeg"));

	if (card.isFaceDown()) setImage(HIDDEN);
	else setImage(SEEN);
    }

    public void setSeen() {
	setImage(SEEN);
    }

    public void setHidden() {
	setImage(HIDDEN);
    }

}
