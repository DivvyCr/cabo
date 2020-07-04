package com.company;

public class Card {
    private static int cardCount = 0;
    private int cardID;
    private int value;
    private String action = "";
    private boolean isFaceDown;

    public Card(int value, boolean isFaceDown) {
        cardCount++;
        cardID = cardCount * 508721;

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

    public int getID() {
        return cardID;
    }

    public void flipCard() {
        this.isFaceDown = !this.isFaceDown;
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
            s = "???";
        }
        return s;
    }
}
