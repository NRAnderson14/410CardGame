import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
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
    private int[] currentScores;

    //Game data
    private Player lastRoundWinner;
    private ClientConnection netLastRoundWinner;
    private Player currentPlayer;
    private ClientConnection netCurrentPlayer;

    //Server data
    private ServerSocket serverSocket;
    private List<ClientConnection> clients;   //List of player connections


    public Game() {

    }

    public void startServer() {
        clients = new ArrayList<>(3);
        Socket socket;

        try {
            serverSocket = new ServerSocket(4100);      //CS 410(0)
            socket = null;
            int numConnected = 0;

            while (numConnected < 3) {
                socket = serverSocket.accept();
                PrintWriter outBound = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inBound = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                clients.add(new ClientConnection(socket, outBound, inBound));

                Runnable server = new Runnable() {
                    @Override
                    public void run() {

                    }
                };
                new Thread(server).start();

                ++numConnected;
                System.out.println("connected: " + numConnected);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String receiveMessage(BufferedReader inBound) {
        String message = "";

        try {
            while (true) {
                if ((message = inBound.readLine()) != null) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }

    private void getClientNames() {
        for (ClientConnection client : clients) {
            client.outBound.println("getname~");

            String name = receiveMessage(client.inBound);
            client.setName(name);
        }
    }



    /*
     *
     *  Deck methods
     *
     */

    //Generates the deck, in order
    public List<String> generateDeck() {
        List<String> genDeck = new ArrayList<>(52);

        for (int suit = 0; suit < 4; ++suit) {
            for (int value = 2; value <= 14; ++value) {
                String strCard = "";
                switch (suit) {
                    case (0):
                        strCard = "c";
                        break;
                    case (1):
                        strCard = "d";
                        break;
                    case (2):
                        strCard = "h";
                        break;
                    case (3):
                        strCard = "s";
                        break;
                }
                genDeck.add(value + "|" + strCard);
            }
        }

        return genDeck;
    }   //Used in net

    //Shuffles the deck
    public void shuffleDeck2(List<String> dock) {
        Random r = new Random();
        int index;

        for (int i = dock.size()-1; i > 0; --i) {
            index = r.nextInt(i);

            swap(dock, index, i);
        }
    }

    //Swaps i and j
    private void swap(List<String> list, int i, int j) {
        String temp;

        temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }   //Used in net

    //Cuts the deck into 3 equal parts of 17, and throws away the remainder, and sends them to the players
    private void sendDeckOverNet(List<String> fullDeck) {
        List<String> p1Deck = fullDeck.subList(0,  17);
        List<String> p2Deck = fullDeck.subList(17, 34);
        List<String> p3Deck = fullDeck.subList(34, 51);
        List<List<String>> deckList = new ArrayList<>();
        deckList.add(p1Deck);
        deckList.add(p2Deck);
        deckList.add(p3Deck);
        int player = 0;

        for (ClientConnection client : clients) {
            for (String card : deckList.get(player)) {
                client.outBound.println("handcard~" + card);

            }
            ++player;
        }
    }

    private boolean currentPlayerHasPlayed() {
        netCurrentPlayer.outBound.println("hasplayed~");
        String played = receiveMessage(netCurrentPlayer.inBound);
        return Boolean.parseBoolean(played);
    }


    /*
     *
     *  Player methods
     *
     */

    //Looks at the player list, and if the player matches the one specified, they are set to be the current player
    private void setCurrentPlayer(ClientConnection playerToSetCurrent) {
        for (ClientConnection client : clients) {
            if (client == playerToSetCurrent) {
                client.outBound.println("setcurrturn~");
                netCurrentPlayer = client;
            } else {
                client.outBound.println("setnotturn~");
            }
        }
    }

    //Selects the next player to play in the round
    private void selectNextPlayer() {
        int currPlayerIndex = clients.indexOf(netCurrentPlayer);

        try {
            if (clients.get(currPlayerIndex+1) != null) {
                netCurrentPlayer = clients.get(currPlayerIndex + 1);
                netCurrentPlayer.outBound.println("setcurrturn~");
            }
        } catch (IndexOutOfBoundsException ignored) {
            netCurrentPlayer = clients.get(0);
            netCurrentPlayer.outBound.println("setcurrturn~");
        }
    }

    //Sends the updated scores to all of the players
    private void updatePlayerCurrentScores() {

        clients.get(0).outBound.println("getscore~");
        int p1Score = Integer.parseInt(receiveMessage(clients.get(0).inBound));
        clients.get(1).outBound.println("getscore~");
        int p2Score = Integer.parseInt(receiveMessage(clients.get(1).inBound));
        clients.get(2).outBound.println("getscore~");
        int p3Score = Integer.parseInt(receiveMessage(clients.get(2).inBound));

        currentScores = new int[] {p1Score, p2Score, p3Score};

        for (ClientConnection client : clients) {
            client.outBound.println(getScoreString());
        }
    }

    //Goes through the player list, and sends each player the name of the current player
    private void setCurrentPlayerInList() {
        String playerName = netCurrentPlayer.getName();

        for (ClientConnection client : clients) {
            client.outBound.println("setcurrplayer~" + playerName);
        }
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
        List<String> strDeck = generateDeck();
        shuffleDeck2(strDeck);
        sendDeckOverNet(strDeck);

        getClientNames();

        netLastRoundWinner = clients.get(0);
        netCurrentPlayer = clients.get(0);
        netCurrentPlayer.outBound.println("setcurrturn~");
        setCurrentPlayerInList();

        for (ClientConnection client : clients) {       //Start each player's gui
            client.outBound.println("startgui~");
        }

        for (int i = 1; i <= 17; ++i) {     //Play 17 rounds
            playRound();
        }
//
        getWinner();    //Gets the game's winner
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
        System.out.println("in round");
        ClientConnection roundWinner = null;  //No one has won yet
        Card highestCard;
        List<Card> cardsPlayed = new ArrayList<>();     //The cards that have been played in this round

        setCurrentPlayer(netLastRoundWinner);  //The winner of the last round goes first
        playersPlayed = 0;      //No one has played yet

        System.out.println("before reset");
        for (ClientConnection client : clients) {
            client.outBound.println("sethasnotplayed~");
            client.outBound.println("clrboard~");
            client.outBound.println("clrcards~");
            client.outBound.println("setcurrplayer~" + netCurrentPlayer.getName());
            client.outBound.println("updatelog~");
        }


        do {
            while (!currentPlayerHasPlayed()) {    //Wait until the current player has played their card
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Player has palayed");

            netCurrentPlayer.outBound.println("getlastcard~");
            String netCard = receiveMessage(netCurrentPlayer.inBound);
            netCard = receiveMessage(netCurrentPlayer.inBound);
            System.out.println(netCard);
            Card cardPlayed = new Card(Integer.parseInt(netCard.substring(0, netCard.indexOf("|"))), netCard.substring(netCard.indexOf("|")+1));
            cardsPlayed.add(cardPlayed);     //Add the card that was played to the list

            for (ClientConnection client : clients) {
                //Send the list of cards that have been played to all of the players
                System.out.println("in update loop: " + netCard);
                client.outBound.println("updtotherscards~" + cardPlayed.toNetString());
            }

            selectNextPlayer();

            for (ClientConnection client : clients) {
                client.outBound.println("setcurrplayer~" + netCurrentPlayer.getName());
                client.outBound.println("updatelog~");
            }

            ++playersPlayed;    //One more player has played

        } while (playersPlayed < 3);    //Stop once all three have played

        //After the round is over
        highestCard = getHighestCardFromList(cardsPlayed);      //Get the winning card

        for (ClientConnection client : clients) {      //Find out who played the winning card
            client.outBound.println("getlastcard~");
            String cardPlayed = receiveMessage(client.inBound);

            if (cardPlayed.equals(highestCard.toNetString())) {
                roundWinner = client;
                break;
            }
        }

        //Award a win to the highest card
        try {
            roundWinner.outBound.println("addwin~");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Send the new scores out to all of the players
        updatePlayerCurrentScores();

        //Set lastRoundWinner to the winner so that they go first next round
        netLastRoundWinner = roundWinner;
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
        ClientConnection winner = clients.get(0);
        int highestScore = 0;   //The current winning score

        for (ClientConnection client : clients) {
            client.outBound.println("getscore~");
            int score = Integer.parseInt(receiveMessage(client.inBound));

            if (score > highestScore) {
                highestScore = score;
                winner = client;
            } else if (score == highestScore) {
                winner = netBreakTie(client, winner);
            }
        }

        for (ClientConnection client : clients) {
            if (client == winner) {
                client.outBound.println("setlogtext~YOU WIN!");
            } else {
                client.outBound.println("setlogtext~You lose");
            }

            client.outBound.println("clrboard~");
        }


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

    private ClientConnection netBreakTie(ClientConnection c1, ClientConnection c2) {
        ClientConnection tieWinner;
        int p1Wins = 0;
        int p2Wins = 0;
        Card p1Card;
        Card p2Card;

        for (int i = 0; i < 17; ++i) {
            c1.outBound.println("getcardplayed~" + i);
            String p1NetCard = receiveMessage(c1.inBound);
            p1Card = new Card(Integer.parseInt(p1NetCard.substring(0, p1NetCard.indexOf("|"))), p1NetCard.substring(p1NetCard.indexOf("|")+1));
            c2.outBound.println("getcardplayed~" + i);
            String p2NetCard = receiveMessage(c2.inBound);
            p2Card = new Card(Integer.parseInt(p2NetCard.substring(0, p2NetCard.indexOf("|"))), p2NetCard.substring(p2NetCard.indexOf("|")+1));

            if (getHighestCardOfTwo(p1Card, p2Card) == p1Card) {
                ++p1Wins;
            } else {
                ++p2Wins;
            }
        }

        if (p1Wins > p2Wins) {
            tieWinner = c1;
        } else {
            tieWinner = c2;
        }

        return tieWinner;
    }


    /*
     *
     *  Network methods
     *
     */

    private String getScoreString() {
        String ss = "updatecurrscores~";

        ss += currentScores[0] + "|" + currentScores[1] + "|" + currentScores[2];

        return ss;
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
