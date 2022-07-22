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

    public Card drawBottomCard() {
	Card bottomCard;
	if (isFinished()) {
	    bottomCard = cards.get(0);
	    cards.remove(0);
	} else {
	    bottomCard = null;
	}
	return bottomCard;
    }

    public void addMultipleCardsOfEqualValue(int value, boolean isFaceDown, int amount) {
	for (int amountLeft = amount; amountLeft > 0; amountLeft--) {
	    addCardToTop(new Card(value, isFaceDown));
	}
    }

    public void addCardToTop(Card newCard) {
	cards.add(newCard);
    }

    public void addCardToBottom(Card newCard) {
	cards.add(0, newCard);
    }

    public void shuffle() {
	Collections.shuffle(cards);
    }

    public boolean isFinished() {
	return cards.size() != 0;
    }

    public void print() {
	for (Card c : cards) {
	    if (c.isFaceDown()) {
		c.flipCard();
	    }
	    System.out.println(c + ": " + c.getID());
	}
    }
}
