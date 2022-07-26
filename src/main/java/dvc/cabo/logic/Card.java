package dvc.cabo.logic;

import java.io.Serializable;

public class Card implements Serializable {

    public static enum Action { PEEK, SPY, SWAP, NONE }

    private final int value;
    private final Action action;
    private boolean isFaceDown;

    public Card(int value, boolean isFaceDown) {
	this.value = value;
	this.isFaceDown = isFaceDown;

	if (value == 7 || value == 8) action = Action.PEEK;
	else if (value == 9 || value == 10) action = Action.SPY;
	else if (value == 11 || value == 12) action = Action.SWAP;
	else action = Card.Action.NONE;
    }

    public void flipCard() {
	this.isFaceDown = !this.isFaceDown;
    }

    public int getValue() {
	return value;
    }

    public Action getAction() {
	return action;
    }

    public boolean isFaceDown() { return this.isFaceDown; }

    @Override
    public String toString() {
	String s;
	if (!isFaceDown) {
	    s = String.valueOf(this.value);
	    if (!action.equals("")) {
		s = s + " (" + this.action + ")";
	    }
	} else {
	    s = "??? [" + String.valueOf(this.value) + "]";
	}
	return s;
    }
}
