package dvc.cabo.logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private String name;
    private ArrayList<Card> hand = new ArrayList<>(4);

    public Player(String name) {
	this.name = name;
    }

    public void swapCardsWithP(Player opponent, int ownCardIdx, int oppCardIdx) {
	swapOwnCardForNewCard(ownCardIdx, opponent.swapOwnCardForNewCard(oppCardIdx, hand.get(ownCardIdx-1)));
    }

    public Card swapOwnCardForNewCard(int ownCardPosition, Card newCard) {
	Card buffer = hand.get(ownCardPosition-1);
	hand.set(ownCardPosition-1, newCard);
	return buffer;
    }

    public void setHand(ArrayList<Card> hand) {
	this.hand = hand;
    }

    public String getName() {
	return name;
    }

    public ArrayList<Card> getHand() {
	return hand;
    }

    public Card getCardByPosition(int cardPosition) {
	return this.hand.get(cardPosition-1);
    }

    public int getHandSize() {
	return this.hand.size();
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder(this.name + " has the following hand: ");
	for (Card card : hand) {
	    s.append("\n\t").append(card.toString());
	}
	return s.toString();
    }
}
