package dvc.cabo.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class CardPile implements Serializable {
    ArrayList<Card> cards;

    public CardPile() {
	cards = new ArrayList<>();
    }

    public Card getTopCard() {
	return cards.get(cards.size()-1);
    }

    public Card drawTopCard() {
	Card topCard;
	if (isFinished()) {
	    topCard = cards.get(cards.size()-1);
	    cards.remove(cards.size()-1);
	} else {
	    topCard = null;
	}
	return topCard;
    }

    public void addCardToTop(Card newCard) {
	cards.add(newCard);
    }

    public void shuffle() {
	Collections.shuffle(cards);
    }

    public boolean isFinished() {
	return cards.size() != 0;
    }

}
