package dvc.cabo.app;

import java.util.ArrayList;

import dvc.cabo.logic.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {

    private static final ImageLoader imageLoader = new ImageLoader();

    private final Image HIDDEN;
    private final Image SEEN;

    public CardView(Card card) {
	setPreserveRatio(true);
	setFitWidth(150);

	SEEN = imageLoader.getCardImageByValue(card.getValue());
	HIDDEN = imageLoader.getHiddenCardImage();

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

class ImageLoader { // Pre-loading all images is KEY TO PERFORMANCE.

    private static final ArrayList<Image> images = new ArrayList<>();

    public ImageLoader() {
	for (int i = 0; i < 14; i++) images.add(i, new Image(getClass().getResourceAsStream(i + ".jpeg")));
	images.add(14, new Image(getClass().getResourceAsStream("hidden.jpeg")));
    }

    public Image getCardImageByValue(int cardValue) {
	return images.get(cardValue);
    }

    public Image getHiddenCardImage() {
	return images.get(14);
    }

}
