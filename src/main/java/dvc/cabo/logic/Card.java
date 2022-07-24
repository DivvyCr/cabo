package dvc.cabo.logic;

import java.io.Serializable;

public class Card implements Serializable {
    private int value;
    private String action = "";
    private boolean isFaceDown;

    public Card(int value, boolean isFaceDown) {
	this.value = value;
	this.isFaceDown = isFaceDown;

	if (this.value == 11 || this.value == 12) {
	    this.action = "SWAP";
	}
	if (this.value == 9 || this.value == 10) {
	    this.action = "SPY";
	}
	if (this.value == 7 || this.value == 8) {
	    this.action = "PEEK";
	}
    }

    public void flipCard() {
	this.isFaceDown = !this.isFaceDown;
    }

    public int getValue() {
	return value;
    }

    public String getAction() {
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
