package dvc.cabo.logic;

import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private static Scanner scanner = new Scanner(System.in);
    private String name;
    private ArrayList<Card> hand = new ArrayList<>(4);

    public Player(String name) {
	this.name = name;
    }

    public void peekCard() {
	System.out.println("Choose a card to peek. (Press 1 through " + this.hand.size() + ")");
	int cardPosition = scanner.nextInt();

	Card card = hand.get(cardPosition - 1);
	printPlayerCardInPosition(this, card, cardPosition);
    }

    public void spyCardFrom(ArrayList<Player> players) {
	Player chosenPlayer;
	while (true) {
	    System.out.println("Choose a player to spy (cannot spy yourself): ");
	    for (int i = 0; i < players.size(); i++) {
		System.out.println((i + 1) + ". " + players.get(i).getName());
	    }
	    int playerPos = scanner.nextInt();
	    chosenPlayer = players.get(playerPos-1);

	    if (!(this == chosenPlayer)) {
		break;
	    } else {
		System.out.println("Cannot spy yourself.");
	    }
	}

	System.out.println("Choose a card to spy. (Press 1 through " + chosenPlayer.getHandSize() + ")");
	int cardPosition = scanner.nextInt();

	Card card = chosenPlayer.getCardByPosition(cardPosition);
	printPlayerCardInPosition(chosenPlayer, card, cardPosition);

    }

    public void swapCardsWithP(Player opponent, int ownCardIdx, int oppCardIdx) {
	swapOwnCardForNewCard(ownCardIdx, opponent.swapOwnCardForNewCard(oppCardIdx, hand.get(ownCardIdx-1)));
    }

    public void swapCardsWith(ArrayList<Player> players) {
	Player playerToSwapWith;
	while(true) {
	    System.out.println("Choose a player to swap cards with (cannot swap with yourself): ");
	    for (int i = 0; i < players.size(); i++) {
		System.out.println((i + 1) + ". " + players.get(i).getName());
	    }
	    playerToSwapWith = players.get(scanner.nextInt() - 1);

	    if (!(this == playerToSwapWith)) {
		break;
	    } else {
		System.out.println("Cannot swap with yourself.");
	    }
	}

	System.out.println("Choose which of your cards you wish to swap. (Press 1 through " + this.getHandSize() + ")");
	Card ownCard = this.getCardByPosition(scanner.nextInt());
	System.out.println("Choose " + playerToSwapWith.getName() + "'s card. (Press 1 through " + playerToSwapWith.getHandSize() + ")");
	Card theirCard = playerToSwapWith.getCardByPosition(scanner.nextInt());

	Card buffer = theirCard;
	theirCard = ownCard;
	ownCard = buffer;
    }

    public Card swapOwnCardForNewCard(int ownCardPosition, Card newCard) {
	Card buffer = hand.get(ownCardPosition-1);
	hand.set(ownCardPosition-1, newCard);
	return buffer;
    }

    private void printPlayerCardInPosition(Player player, Card card, int cardPosition) {
	if (card.isFaceDown()) {
	    card.flipCard();
	    System.out.println(" > " + player.getName() + "'s card in position " + cardPosition + " is " + card.toString());
	    card.flipCard();
	} else {
	    System.out.println("cabo.Card is visible.");
	}
    }

    public void printHand() {
	System.out.print("\t");
	for (Card card : hand) {
	    System.out.print(card);
	    System.out.print("|");
	}
	System.out.println();
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
