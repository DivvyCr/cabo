package com.company;

// DUPLICATE CARDS ARE CONSIDERED THE SAME OBJECT.
// i.e. Java considers the 4 cards of the same value equal objects. Therefore, if one is flipped, all others are visible.

public class Main {
    public static void main(String[] args) {
        Game_Cabo game = new Game_Cabo();
        game.addPlayerByName("Andrei");
        game.addPlayerByName("Sveta");
        game.startGame();
    }
}
