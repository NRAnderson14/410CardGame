import java.util.*;

//scores
//log player info/events
//distribute the cards
//Enforce rules/ award win
//turns

public class Game {
    //Deck data
    private List<Card> deck;
    private final Card.Suit[] suitsList = {Card.Suit.CLUBS, Card.Suit.DIAMONDS, Card.Suit.HEARTS, Card.Suit.SPADES};

    //Player data
    private List<Player> playerList;
    private short playersPlayed = 0;

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
            }
        } catch (IndexOutOfBoundsException throwAway) {     //If we are at the end of the list, go to the beginning
            currentPlayer = playerList.get(0);
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
            while (!currentPlayer.hasPlayed())
            cardsPlayed.add(currentPlayer.getLastCardPlayed());
            selectNextPlayer();
            ++playersPlayed;
        } while (playersPlayed < 3);

        highestCard = getHighestCard(cardsPlayed);  //After the round is over
        for (Player player : playerList) {
            if (player.getLastCardPlayed() == highestCard) {
                roundWinner = player;
                break;
            }
        }

        //Award a win to the highest card
        roundWinner.addWin();
        //Set lastRoundWinner to the winner so that they go first next round
        lastRoundWinner = roundWinner;
    }

    private Card getHighestCard(List<Card> cardsToCompare) {
        //Clubs < Diamonds < Hearts < Spades
        return new Card(12, Card.Suit.DIAMONDS); //Placeholder
    }

    private Player getWinner() {
        Player winner = playerList.get(0);

        for (Player player: playerList) {    //Can't use the built-in List forEach method because lambdas want values, not variables for some darn reason
            if (player.getWins() > winner.getWins()) {  //Honestly why though
                winner = player;
            }
        }

        return winner;
    }


    /*
     *
     *  Testing methods
     *
     */

    public short getPlayersPlayed() {
        return playersPlayed;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
}
