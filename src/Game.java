import java.util.*;

public class Game {
    //Deck data
    private List<Card> deck;
    private final Card.Suit[] suitsList = {Card.Suit.CLUBS, Card.Suit.DIAMONDS, Card.Suit.HEARTS, Card.Suit.SPADES};

    //Player data
    private List<Player> playerList;
    private int playersPlayed = 0;

    //Game data
    private Player lastRoundWinner;
    private Player currentPlayer;

    /*
     *
     *  General
     *
     */

    public Game(Player p1, Player p2, Player p3) {
        playerList = new ArrayList(3);
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);
    }


    /*
     *
     *  Deck handling methods
     *
     */

    //Generates a deck of 52 cards, in order
    private void generateDeck() {
        List<Card> generatedDeck = new ArrayList(52);

        for (int suit = 0; suit < 4; ++suit) {
            for (int value = 2; value <= 14; ++value) {
                generatedDeck.add(new Card(value, suitsList[suit]));
            }
        }

        deck = generatedDeck;
    }

    //Shuffles the deck
    private void shuffleDeck() {
        Random r = new Random();
        int index;

        for (int i = deck.size()-1; i > 0; --i) {
            index = r.nextInt(i);

            swap(deck, index, i);
        }
    }

    //Swaps i with j
    private void swap(List<Card> list, int i, int j) {
        Card temp;

        temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    //Cuts the deck into 3 equal parts of 17, and throws away the remainder
    private void splitAndDistributeDeck(Player p1, Player p2, Player p3) {
        List<Card> p1Deck;
        List<Card> p2Deck;
        List<Card> p3Deck;

        p1Deck = deck.subList(0,  17);
        p2Deck = deck.subList(17, 34);
        p3Deck = deck.subList(34, 51);

        p1.setDeck(p1Deck);
        p2.setDeck(p2Deck);
        p3.setDeck(p3Deck);
    }

    private void createAndShuffleDeck() {
        generateDeck();
        shuffleDeck();
    }


    /*
     *
     *  Player methods
     *
     */

    //Tells the player that they can play currently
    private void setCurrentPlayer(Player playerToSetCurrent) {
        for (Player player : playerList) {
            if (player == playerToSetCurrent) {
                player.setCurrentTurn();
                currentPlayer = playerToSetCurrent;
            } else {
                player.setNotCurrentTurn();
            }
        }
    }

    private void selectNextPlayer() {
        int currPlayerIndex = playerList.indexOf(currentPlayer);

        try {
            if (playerList.get(currPlayerIndex + 1) != null) {
                currentPlayer = playerList.get(currPlayerIndex+1);
                currentPlayer.isCurrentTurn();
            }
        } catch (IndexOutOfBoundsException throwAway) {     //If we are at the end of the list, go to the beginning
            currentPlayer = playerList.get(0);
            currentPlayer.isCurrentTurn();
        }
    }

    public void updatePlayerCurrentScores() {
        int[] scores;
        int p1Score = playerList.get(0).getWins();
        int p2Score = playerList.get(1).getWins();
        int p3Score = playerList.get(2).getWins();

        scores = new int[] {p1Score, p2Score, p3Score};

        for (Player player : playerList) {
            player.updateCurrentScores(scores);
        }
    }

    private void setCurrentPlayerInList() {
        String playerName = currentPlayer.getName();

        for (Player player : playerList) {
            player.setCurrentPlayer(playerName);
        }
    }


    /*
     *
     *  Gameplay methods
     *
     */

    //Starts the game, ie sets up and initializes everything
    public void playGame() {
        createAndShuffleDeck();
        splitAndDistributeDeck(playerList.get(0), playerList.get(1), playerList.get(2));

        lastRoundWinner = playerList.get(0);   //P1 leads the first round
        currentPlayer = playerList.get(0);
        currentPlayer.setCurrentTurn();
        setCurrentPlayerInList();

        for (Player player : playerList) {
            player.startGUI();
        }

        for (int i = 1; i <= 17; ++i) {     //Play 17 rounds
            playRound();
        }
    }

    //The meat and potatoes of the gameplay
    private void playRound() {
        Player roundWinner = null;
        Card highestCard;
        List<Card> cardsPlayed = new ArrayList<>();
        //lastRoundWinner goes first
        setCurrentPlayer(lastRoundWinner);
        playersPlayed = 1;

        do {
            while (!currentPlayer.hasPlayed()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            cardsPlayed.add(currentPlayer.getLastCardPlayed());
            System.out.println("Got past the while");

            for (Player player : playerList) {
                player.updateOthersCards(currentPlayer.getLastCardPlayed());
            }

            selectNextPlayer();

            for (Player player : playerList) {
                player.updateLogArea("doo");
            }

            ++playersPlayed;
            System.out.println("Reached the bottom of the loop");
        } while (playersPlayed < 3);

        highestCard = getHighestCardFromList(cardsPlayed);  //After the round is over
        for (Player player : playerList) {
            if (player.getLastCardPlayed() == highestCard) {
                roundWinner = player;
                break;
            }
        }

        //Award a win to the highest card
        roundWinner.addWin();
        for (Player player : playerList) {
            updatePlayerCurrentScores();
        }
        //Set lastRoundWinner to the winner so that they go first next round
        lastRoundWinner = roundWinner;
    }

    private Card getHighestCardFromList(List<Card> cardsToCompare) {
        Card currHighest = new Card(2, Card.Suit.CLUBS);
        //Clubs < Diamonds < Hearts < Spades
        //loop through, check values first, then if values ==, check suits
        for (Card card : cardsToCompare) {
            if (card.getValue() > currHighest.getValue()) {
                currHighest = card;
            } else if (card.getValue() == currHighest.getValue()) {
                if (card.suitToValue() > currHighest.suitToValue()) {
                    currHighest = card;
                }   //Otherwise the current highest stays the same
            }
        }

        return currHighest;
    }

    private Card getHighestCardOfTwo(Card c1, Card c2) {
        Card winner;

        if (c1.getValue() > c2.getValue()) {
            winner = c1;
        } else if (c1.getValue() == c2.getValue()) {
            if (c1.suitToValue() > c2.suitToValue()) {
                winner = c1;
            } else {
                winner = c2;
            }
        } else {
            winner = c2;
        }

        return winner;
    }

    private Player getWinner() {
        Player winner = playerList.get(0);

        for (Player player: playerList) {
            if (player.getWins() > winner.getWins()) {
                winner = player;
            } else if (player.getWins() == winner.getWins()) {
                winner = breakTie(player, winner);
            }
        }

        return winner;
    }

    private Player breakTie(Player p1, Player p2) {
        Player tieWinner;
        int p1Wins = 0;
        int p2Wins = 0;
        Card p1Card;
        Card p2Card;

        //Look at all of the cards played, and get their suits and compare
        //clubs < diamonds < hearts < spades
        for (int i = 0; i < 17; ++i) {     //17 rounds
            p1Card = p1.getCardsPlayedByPlayer().get(i);
            p2Card = p2.getCardsPlayedByPlayer().get(i);

            if (getHighestCardOfTwo(p1Card, p2Card) == p1Card) {
                ++p1Wins;
            } else {
                ++p2Wins;
            }
        }

        if (p1Wins > p2Wins) {
            tieWinner = p1;
        } else {
            tieWinner = p2;
        }

        return tieWinner;
    }


    /*
     *
     *  Testing methods
     *
     */

    public int getPlayersPlayed() {
        return playersPlayed;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void testSetCurrScores(int one, int two, int three) {
        int[] scores;

        scores = new int[] {one, two, three};

        for (Player player : playerList) {
            player.updateCurrentScores(scores);
        }
    }

}
