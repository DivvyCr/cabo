package dvc.cabo.logic;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    private ArrayList<Player> players = new ArrayList<>();
    private boolean gameOver;
    private int currentTurn;

    private CardPile deck;
    private CardPile discardPile;

    public Game() {
	System.out.println("Setting up a new game of CABO...");

	deck = createSortedDeck();
	deck.shuffle();

	discardPile = new CardPile();
	discardPile.addCardToTop(deck.drawTopCard());
	discardPile.getTopCard().flipCard();
    }

    public CardPile getDeck() { return deck; }

    public CardPile getDiscardPile() { return discardPile; }

    public ArrayList<Player> getPlayers() { return players; }

    public void addPlayerByName(String name) { players.add(new Player(name)); }

    private void drawCard(Card drawn, int cardIdx, Player player) {
	Card discard = player.swapOwnCardForNewCard(cardIdx+1, drawn);
	if (discard.isFaceDown()) discard.flipCard(); // All discarded cards are face-up.
	discardPile.addCardToTop(discard);
	player.getHand().set(cardIdx, drawn);
    }

    public void drawFromDeck(int cardIdx, Player player) { drawCard(deck.drawTopCard(), cardIdx, player); }

    public void drawFromDiscard(int cardIdx, Player player) { drawCard(discardPile.drawTopCard(), cardIdx, player); }

    public void useCard() { discardPile.addCardToTop(deck.drawTopCard()); }

    public void startGame() {
	System.out.println("Started a new game of CABO.");

	for (Player player : players) dealCards(player);

	currentTurn = 0;
    }

    public CardPile createSortedDeck() {
	CardPile deck = new CardPile();
	for (int val = 0; val <= 13; val++) {
	    int numCards = (val == 0 || val == 13) ? 2 : 4;
	    for (int i = 0; i < numCards; i++) deck.addCardToTop(new Card(val, true));
	}
	return deck;
    }

    private void dealCards(Player player) {
	ArrayList<Card> hand = new ArrayList<>();
	for (int i = 0; i < 4; i++) {
	    hand.add(deck.drawTopCard());
	}
	player.setHand(hand);
    }
}
