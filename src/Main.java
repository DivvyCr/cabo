package com.company;

public class Main {
    public static void main(String[] args) {
        Game_Cabo game = new Game_Cabo();
        game.addPlayerByName("Player 1");
        game.addPlayerByName("Player 2");
        game.startGame();
    }
}
