# Cabo
Cabo is a card game by Melissa Limes, where you aim to reduce the nominal sum of your hand by
exchanging cards from the deck or other players. The twist is that all player cards are face-down
and you are only allowed to peek 2 of your cards at the start of the game. Then, each turn allows
you to gain information about some cards on the table or exchange one of your own for another.

Below is a demo of my implementation of the game with two players, and further down are the rules.

<p align="center"><img src="Cabo_Demo.gif" width="80%" /></p>

## How to play?
The game is played in rounds. The goal is to have the **lowest** number of points by the end of the
game, which happens when one of the players crosses 100 points. The points are gained at the end of
each round, the number added is equal to the nominal sum of your hand.

### Pre-Round
All cards in the deck are shuffled, and each player is dealt 4 cards, face-down. One card is placed
face-up - this is the discard pile; the remaining deck is placed next to the discard pile. At the
start of the round, all players may peek 2 cards from their hand before returning them to their
original position (face-down).

### Player Turn
After pre-round play, the turns rotate between players. Each turn you may:
  * Draw from the deck.
	* If the card has an action (see below), you may use it. After which, you must discard the card.
	* If the card doesn't have an action, or you don't wish to use it, you may:
	  * Substitute one (or more, see below) of your cards for it. This card should be placed
		face-down.
	  * Discard it.
  * Draw from the discard pile.
	* Substitute one (or more, see below) of your cards for it. This card should be placed face-up.
  * Call CABO, without playing any cards.
	* This gives all other players 1 more turn, after which the round ends.

#### Substituting multiple cards for one
**NOTE:** This is not yet implemented.

If you believe that you have 2 or more cards of the same nominal value, you may swap both of them
for the card that you picked from the deck or the discard pile). If the cards do not have the same
nominal value, their are placed face-up, and you keep the new card, too.

Examples:
  * Initial hand: 5, 7, 5, 2
	* Pick a card from the deck: 4
	* Swap two 5s for the 4: 4, 7, 2

  * Initial hand: 1, 3, 3, 3
	* Pick a card from the deck: 5
	* Try to swap three 3s for the 5, but choose the wrong cards: 1, 3, 3, 3, 5 (the two picked
	  cards are face-up)

### Card Actions
Some cards have special actions attached to them:
  * Cards 7 and 8 allow you to **PEEK** one of your cards.
  * Cards 9 and 10 allow you to **SPY** one of another player's cards.
  * Cards 11 and 12 allow you to **SWAP** one of your cards for one of another player's cards.

### Scoring
The primary scoring convention is simply the sum of each player's hand, which is added to their
cumulative total. If a player called CABO and had the lowest sum on their hands, they get 0
points. However, if that player didn't have the lowest sum, they are given an additional 10 points
on top of their sum. (e.g. I called CABO, with 1 and 4 in my hand, but someone else had 2 and 2, so
my score is +15, while their score is just +4) A rule called 'Kamikaze' comes into action if any
player managed to get a hand consisting of 12, 12, 13, 13. If that happens, they get +0, while
everyone else gets +50 to their cumulative scores.
