emacs magit test

# Cabo
Cabo is a card game by Melissa Limes, where you aim to reduce the nominal sum of your hand by exchanging cards from the deck or other players. The twist is that you only know 2 of your cards at the beginning, and you must remember them (all cards are face-down, by default). Hovewer, you are able to 'Peek' your other cards or swap them for known ones, which is outlined below.

## How to play?

### Pre-Round
  All cards in the deck are shuffled, and each player is given 4 cards (placed face-down in-front of each player); one card is placed face-up in the discard pile. After this is set-up, each player may peek 2 of their cards and memorise them, before returning them to their original position, face-down.
  
### Player Turn
  After pre-game, the turns rotate between players. Each turn you may:
  * Draw from the deck.
    * If the card has an action (see below), you may use it. After which, you must discard the card.
    * If the card doesn't have an action, or you don't wish to use it, you may:
      * Substitute one (or more, see below) of your cards for it. This card should be placed face-down.
      * Discard it.
  * Draw from the discard pile.
    * Substitute one (or more, see below) of your cards for it. This card should be placed face-up.
  * Call CABO, without playing any cards.
    * This gives all other players 1 more turn, after which the round ends.

#### Substituting multiple cards for one.
  If you believe that you have 2 or more cards of the same nominal value, you may swap both of them for the card that you picked from the deck or the discard pile). If the cards do not have the same nominal value, their are placed face-up, and you keep the new card, too.
  
Examples:
  * Initial hand: 5, 7, 5, 2
    * Pick a card from the deck: 4
    * Swap two 5s for the 4: 4, 7, 2
  
  * Initial hand: 1, 3, 3, 3
    * Pick a card from the deck: 5
    * Try to swap three 3s for the 5, but choose the wrong cards: 1, 3, 3, 3, 5 (the two picked cards are face-up)

### Card Actions
  Some cards have special actions attached to them:
  * Cards 7 and 8: PEEK
    * These cards let you peek one of your cards.
  * Cards 9 and 10: SPY
    * These cards let you spy one of another player's cards.
  * Cards 11 and 12: SWAP
    * These cards let you swap one of your cards for one of another player's cards.
    
### Post-Round
  The round ends when either a player called CABO and every other player had 1 last turn, or when the deck ends. At this point, the scores are tallied up:
  * if any player got a cumulative total over 100, the game ends, and the player with the lowest cumulative total wins.
  * if any player got exactly 100, they are reset to 50. This is once per game.

### Scoring
  The primary scoring convention is simply the sum of each player's hand, which is added to their cumulative total.
  If a player called CABO and had the lowest sum on their hands, they get 0 points. However, if that player didn't have the lowest sum, they are given an additional 10 points on top of their sum. (e.g. I called CABO, with 1 and 4 in my hand, but someone else had 2 and 2, so my score is +15, while their score is just +4)
  A rule called 'Kamikaze' comes into action if any player managed to get a hand consisting of 12, 12, 13, 13. If that happens, they get +0, while everyone else gets +50 to their cumulative scores.
  
## Set of Cards
  There are 4 of each card, except 0 and 13, which there are 2 of each.
