package dvc.cabo.logic;

import java.util.*;
import java.util.regex.Pattern;

public class Game {
    private Scanner scanner = new Scanner(System.in);
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

    public void startGame() {
	System.out.println("Started a new game of CABO.");
	scanner = new Scanner(System.in);

	for (Player player : players) dealCards(player);

	currentTurn = 0;
	// startTurnCycle();
    }

//    private void playGame() {
//        while (!deck.isFinished()) {
//
//        }
//    }

    private void startTurnCycle() {
	int currentPlayerPositionWithinCycle = 0;
	do {
	    if (discardPile.getTopCard().isFaceDown()) {
		discardPile.getTopCard().flipCard();
	    }
	    Player currentPlayer = players.get(currentPlayerPositionWithinCycle);

	    System.out.println(" === " + currentPlayer.getName().toUpperCase() + "'s turn ===");
	    for (Player p : players) {
		System.out.println(p.getName() + "'s cards: ");
		p.printHand();
	    }
	    System.out.println(" === ");

	    System.out.println("Choose one of the following actions:\n" +
		    "> 1. DRAW from the deck.\n" +
		    "> 2. PICK ( " + discardPile.getTopCard().toString() + " ) from the discard pile.");

	    int action = scanner.nextInt();
	    if (action == 1) {
		drawFromDeck(currentPlayer);
	    } else if (action == 2) {
		drawFromDiscardPile(currentPlayer);
	    } else {
		System.out.println("...");
	    }

	    provisionalConsoleClear();
	    currentTurn++;
	    currentPlayerPositionWithinCycle = currentTurn % players.size();
	} while (currentPlayerPositionWithinCycle != 0);

	if (!gameOver) {
	    startTurnCycle();
	}
    }

    private void drawFromDeck(Player player) {
	Card drawnCard = deck.drawTopCard();

	if (isCardWithAction(drawnCard)) {
	    if (doesPlayerWantToUseAction(player, drawnCard)) {
		useCardAction(player, drawnCard);
		discardPile.addCardToTop(drawnCard);
	    } else {
		usePlainCard(player, drawnCard);
	    }
	} else {
	    usePlainCard(player, drawnCard);
	}
    }

    private void drawFromDiscardPile(Player player) {
	Card drawnCard = discardPile.drawTopCard();
	System.out.println("You have picked up: " + drawnCard);
	swapPlayerCardAndDiscardOldCard(player, drawnCard);
	provisionalConsoleClear();
    }

    private boolean doesPlayerWantToUseAction(Player player, Card card) {
	System.out.println("Would you like to use the card's action? ("+ card.getAction().toUpperCase() +")");
	return letPlayerChooseYorN();
    }

    private void swapPlayerCardAndDiscardOldCard(Player player, Card card) {
	System.out.println("How many cards would you like to swap with? (if you have multiple equal cards in hand)");
	int amountOfCards = letPlayerChooseNumberBetween(1, player.getHandSize());
	ArrayList<Integer> chosenCards = new ArrayList<>();
	int chosenCardPosition = 0;
	for (int i = amountOfCards; i > 0; i--) {
	    System.out.println("Which of your cards would you like to swap?");
	    chosenCardPosition = letPlayerChooseCardPosition(player);
	    if (!chosenCards.contains(chosenCardPosition)) {
		chosenCards.add(chosenCardPosition);
	    } else {
		System.out.println("cabo.Card already chosen.");
		i++;
	    }
	}

	// ???

	Card playersOldCard = player.swapOwnCardForNewCard(chosenCardPosition, card);
	System.out.println("Hey!");
	discardPile.addCardToTop(playersOldCard);
    }

    private int letPlayerChooseNumberBetween(int lowerBound, int upperBound) {
	while(true) {
	    try {
		int number = scanner.nextInt();
		if (number >= lowerBound && number <= upperBound) {
		    return number;
		}
	    } catch (NoSuchElementException e) {
		System.out.println("Enter an integer between " + lowerBound + " and " + upperBound);
	    }
	}
    }

    private void usePlainCard(Player player, Card card) {
	System.out.println("Choose your next action: \n" +
		" 1. Swap with one of your cards; \n" +
		" 2. Discard this card.");

	boolean successful = false;
	while (!successful) {
	    int action = scanner.nextInt();
	    switch (action) {
		case 1:
		    swapPlayerCardAndDiscardOldCard(player, card);
		    successful = true;
		    break;
		case 2:
		    discardPile.addCardToTop(card);
		    successful = true;
		    break;
		default:
		    System.out.println("Type 1 or 2.");
		    break;
	    }
	}
    }

    private void useCardAction(Player player, Card card) {
	switch (card.getAction().toUpperCase()) {
	    case "PEEK":
		player.peekCard();
		break;
	    case "SPY":
		player.spyCardFrom(players);
		break;
	    case "SWAP":
		player.swapCardsWith(players);
		break;
	    default:
		System.out.println("No action selected... Turn skipped.");
		break;
	}

	System.out.println("When done, type DONE.");
	while (!scanner.next().toLowerCase().equals("done")) {}
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

    private void showDrawnCard(Card card) {
	if (card.isFaceDown()) {
	    card.flipCard();
	    System.out.println("Drawn card: " + card);
	    card.flipCard();
	} else {
	    System.out.println("Drawn card: " + card);
	}
    }

    private boolean isCardWithAction(Card card) {
	return !card.getAction().equals("");
    }

    private void provisionalConsoleClear() {
	for (int i = 0; i < 50; i++) {
	    System.out.println();
	}
    }

    private boolean letPlayerChooseYorN() {
	System.out.println("Type Y for yes or N for no.");
	while (true) {
	    String userInput = scanner.next();
	    if (Pattern.matches("[ynYN]", userInput)) {
		return userInput.equals("y");
	    } else {
		System.out.println("Type Y for yes or N for no.");
	    }
	}
    }

    private int letPlayerChooseCardPosition(Player player) {
	int playerHandSize = player.getHandSize();
	System.out.println("Type 1 through " + playerHandSize + ".");

	while (true) {
	    String userInput = scanner.next();
	    try {
		int intUserInput = Integer.parseInt(userInput);
		if (intUserInput > 0 && intUserInput <= player.getHandSize()) {
		    return intUserInput;
		} else {
		    System.out.println("Type 1 through " + playerHandSize + ".");
		}
	    } catch (NumberFormatException e) {
		System.out.println("Type a number.");
	    }

	}
    }
}
