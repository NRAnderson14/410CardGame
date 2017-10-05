import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/*
 *  The game's Game class
 *
 *  This class is used for running the actual game.
 *  Game contains the deck used in the game, and keeps track of players round wins and player hands.
 *
 *  Game is split into 3 main parts: The Deck, the Players and the Game.
 *
 *  The Deck deals with the construction and distribution of the deck of cards used in the game.
 *  The Players deals with the access and management of the players in the game.
 *  The Game deals with the gameplay, and everything else that doesn't fall under either of the other parts.
 *
 *
 *
 */

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

    //Server data
    private ServerSocket serverSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream  inputStream;
    private int numConnections;
    private List<Socket> clients;


    public Game(Player p1, Player p2, Player p3) {
        playerList = new ArrayList(3);
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);


    }

    private void startServer() {
        clients = new ArrayList<>(3);

        try {
            serverSocket = new ServerSocket(4100);      //CS 410(0)
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Socket sock = serverSocket.accept();
                new CommunicationThread(sock).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
     *
     *  Deck methods
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

    //Self-explanatory
    private void createAndShuffleDeck() {
        generateDeck();
        shuffleDeck();
    }


    /*
     *
     *  Player methods
     *
     */

    //Looks at the player list, and if the player matches the one specified, they are set to be the current player
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

    //Selects the next player to play in the round
    private void selectNextPlayer() {
        int currPlayerIndex = playerList.indexOf(currentPlayer);

        try {
            if (playerList.get(currPlayerIndex + 1) != null) {
                currentPlayer = playerList.get(currPlayerIndex+1);
                currentPlayer.setCurrentTurn();
            }
        } catch (IndexOutOfBoundsException throwAway) {     //If we are at the end of the list, go to the beginning
            currentPlayer = playerList.get(0);
            currentPlayer.setCurrentTurn();
        }
    }

    //Sends the updated scores to all of the players
    private void updatePlayerCurrentScores() {
        int[] scores;
        int p1Score = playerList.get(0).getScore();      //Get the score from all of the players
        int p2Score = playerList.get(1).getScore();
        int p3Score = playerList.get(2).getScore();

        scores = new int[] {p1Score, p2Score, p3Score};

        //Send the scores to all of the players
        for (Player player : playerList) {
            player.updateCurrentScores(scores);
        }
    }

    //Goes through the player list, and sends each player the name of the current player
    private void setCurrentPlayerInList() {
        String playerName = currentPlayer.getName();

        for (Player player : playerList) {
            player.setCurrentPlayer(playerName);
        }
    }

    //Sends the player back to a client
    private void sendPlayerOverNetwork(Player playerToSend) {

    }


    /*
     *
     *  Gameplay methods
     *
     */

    /*
     *  The method to play the game
     *
     *  Creates and shuffles the deck, splits it and gives it to the players.
     *  Initializes player1 to be the current winner, so that they play first
     *  Opens the actual windows for all of the players
     *  Loops and calls playRound 17 times, one for each round in the game
     *  After all of the rounds are finished, gets the winner of the game
     *
     */
    public void playGame() {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        };
        new Thread(server).start();

        createAndShuffleDeck();
        splitAndDistributeDeck(playerList.get(0), playerList.get(1), playerList.get(2));    //TODO: network this

        lastRoundWinner = playerList.get(0);   //P1 leads the first round
        currentPlayer = playerList.get(0);
        currentPlayer.setCurrentTurn();
        setCurrentPlayerInList();

        //Send over network

        for (Player player : playerList) {      //Start the GUI for each player     TODO: this will be done locally now
            player.startGUI();
        }

        for (int i = 1; i <= 17; ++i) {     //Play 17 rounds
            playRound();
        }

        getWinner();    //Gets the game's winner    TODO: over network
    }

    /*
     *  This method is the bulk of the gameplay
     *
     *  It first resets all of the round data
     *  It then waits until the first player has played, and distributes the card that they played to the other players
     *  After that, it repeats another two times until both other players have played their cards
     *  Once all players have played, it calculates who won the round, and sends that data to all of the players
     *  Finally, the winner is set up to play first in the next round
     *
     */
    private void playRound() {
        Player roundWinner = null;  //No one has won yet
        Card highestCard;
        List<Card> cardsPlayed = new ArrayList<>();     //The cards that have been played in this round

        setCurrentPlayer(lastRoundWinner);  //The winner of the last round goes first
        playersPlayed = 0;      //No one has played yet

        for (Player player : playerList) {      //Reset the data and GUI for all of the players     TODO: Network
            player.setHasNotPlayed();
            player.clearOthersCards();
            player.clearGameBoard();
            player.setCurrentPlayer(currentPlayer.getName());
            player.updateLogArea();
        }

        //Send out the update over network here

        do {
            while (!currentPlayer.hasPlayed()) {    //Wait until the current player has played their card
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            cardsPlayed.add(currentPlayer.getLastCardPlayed());     //Add the card that was played to the list

            for (Player player : playerList) {
                //Send the list of cards that have been played to all of the players
                player.updateOthersCards(currentPlayer.getLastCardPlayed());

                /* So this next part is funny. The adding the cards to the panel was not working, except for player 3.
                 *  I tested it, and the data was there, just not the card button. So as it worked out, the program
                 *  was trying to access the png image for the card at the same time, so the last access got the file,
                 *  so that was why only player three was getting the image. Pausing the thread for just enough time to
                 *  let each player load the image works, and that is what you see below.
                 */
                try {
                    Thread.sleep(2);    //2ms is the fastest it can be
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            selectNextPlayer();

            for (Player player : playerList) {      //Tell all of the players who the current player now is
                player.setCurrentPlayer(currentPlayer.getName());
                player.updateLogArea();
            }

            ++playersPlayed;    //One more player has played

        } while (playersPlayed < 3);    //Stop once all three have played

        //After the round is over
        highestCard = getHighestCardFromList(cardsPlayed);      //Get the winning card

        for (Player player : playerList) {      //Find out who played the winning card
            if (player.getLastCardPlayed() == highestCard) {
                roundWinner = player;
                break;
            }
        }

        //Award a win to the highest card
        try {
            roundWinner.addWin();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Send the new scores out to all of the players
        updatePlayerCurrentScores();

        //Set lastRoundWinner to the winner so that they go first next round
        lastRoundWinner = roundWinner;
    }

    //Returns the highest Card from the given List
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

    //Returns the highest Card from two given Cards
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

    //Computes the winner of the game, and informs the players
    private void getWinner() {
        Player winner = playerList.get(0);
        int highestScore = 0;   //The current winning score

        for (Player player: playerList) {
            //If this player has a higher score than the current highest, they are now the highest scorer
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
                winner = player;
            } else if (player.getScore() == highestScore) {     //In case of a tie
                winner = breakTie(player, winner);
            }
        }

        for (Player player : playerList) {      //Tell all of the players whether or not they won
            if (player == winner) {
                player.setLogText("YOU WIN!");
            } else {
                player.setLogText("You lose");
            }
            player.clearGameBoard();
        }

        //Send over network

    }

    //Returns which player wins a tie out of two
    private Player breakTie(Player p1, Player p2) {
        Player tieWinner;
        int p1Wins = 0;
        int p2Wins = 0;
        Card p1Card;
        Card p2Card;

        //Look at all of the cards played, and get their suits and compare
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

    //Sets the current player scores
    public void testSetCurrScores(int one, int two, int three) {
        int[] scores;

        scores = new int[] {one, two, three};

        for (Player player : playerList) {
            player.updateCurrentScores(scores);
        }
    }

    //Starts all of the player GUIs without having to start the game
    public void startAllGUIs() {
        for (Player player : playerList) {
            player.startGUI();
        }
    }

    //Public wrapper for getWinner
    public void testGetWinner() {
        getWinner();
    }

}
