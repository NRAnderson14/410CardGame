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
    private ObjectOutputStream outputStream;
    private ObjectInputStream  inputStream;
    private int numConnections;
    private List<ClientConnection> clients;   //List of player connections


    public Game() {
//        playerList = new ArrayList(3);
//        playerList.add(p1);
//        playerList.add(p2);
//        playerList.add(p3);


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
//                        receiveLoop(inBound, outBound);
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

    private void receiveLoop(BufferedReader inBound, PrintWriter outBound) {
        String message;

        try {
            while (true) {
                if ((message = inBound.readLine()) != null) {
                    System.out.println(message);
                    executeCommand(message, outBound);
                }
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

    public void startUpTest() {
        List<String> strDeck = genStrDeck();
        shuffleDeck2(strDeck);
        sendDeckOverNet(strDeck);
        getClientNames();

        for (ClientConnection client : clients) {
            client.outBound.println("setlogtext~Hi Bud");
            client.outBound.println("startgui~");
        }
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

    public List<String> genStrDeck() {
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
    private void shuffleDeck() {
        Random r = new Random();
        int index;

        for (int i = deck.size()-1; i > 0; --i) {
            index = r.nextInt(i);

            swap(deck, index, i);
        }
    }

    public void shuffleDeck2(List<String> dock) {
        Random r = new Random();
        int index;

        for (int i = dock.size()-1; i > 0; --i) {
            index = r.nextInt(i);

            swapDock(dock, index, i);
        }
    }       //Used in net

    private void swapDock(List<String> list, int i, int j) {
        String temp;

        temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }   //Used in net

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
    private void setCurrentPlayer(ClientConnection playerToSetCurrent) {
//        for (Player player : playerList) {
//            if (player == playerToSetCurrent) {
//                player.setCurrentTurn();
//                currentPlayer = playerToSetCurrent;
//            } else {
//                player.setNotCurrentTurn();
//            }
//        }

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
//        int currPlayerIndex = playerList.indexOf(currentPlayer);
        int currPlayerIndex = clients.indexOf(netCurrentPlayer);

//        try {
//            if (playerList.get(currPlayerIndex + 1) != null) {
//                currentPlayer = playerList.get(currPlayerIndex+1);
//                currentPlayer.setCurrentTurn();
//            }
//        } catch (IndexOutOfBoundsException throwAway) {     //If we are at the end of the list, go to the beginning
//            currentPlayer = playerList.get(0);
//            currentPlayer.setCurrentTurn();
//        }

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
//        int[] scores;
//        int p1Score = playerList.get(0).getScore();      //Get the score from all of the players
//        int p2Score = playerList.get(1).getScore();
//        int p3Score = playerList.get(2).getScore();
//
//        currentScores = new int[] {p1Score, p2Score, p3Score};
//
//        //Send the scores to all of the players
//        for (Player player : playerList) {
//            player.updateCurrentScores(currentScores);
//        }

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
        List<String> strDeck = genStrDeck();
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

//        for (Player player : playerList) {      //Reset the data and GUI for all of the players     TODO: Network
//            player.setHasNotPlayed();
//            player.clearOthersCards();
//            player.clearGameBoard();
//            player.setCurrentPlayer(currentPlayer.getName());
//            player.updateLogArea();
//        }

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
//                player.updateOthersCards(currentPlayer.getLastCardPlayed());
                System.out.println("in update loop: " + netCard);
                client.outBound.println("updtotherscards~" + cardPlayed.toNetString());

                /* So this next part is funny. The adding the cards to the panel was not working, except for player 3.
                 *  I tested it, and the data was there, just not the card button. So as it worked out, the program
                 *  was trying to access the png image for the card at the same time, so the last access got the file,
                 *  so that was why only player three was getting the image. Pausing the thread for just enough time to
                 *  let each player load the image works, and that is what you see below.
                 */
//                try {
//                    Thread.sleep(2);    //2ms is the fastest it can be
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            selectNextPlayer();

//            for (Player player : playerList) {      //Tell all of the players who the current player now is
//                player.setCurrentPlayer(currentPlayer.getName());
//                player.updateLogArea();
//            }

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

//        for (Player player: playerList) {
//            //If this player has a higher score than the current highest, they are now the highest scorer
//            if (player.getScore() > highestScore) {
//                highestScore = player.getScore();
//                winner = player;
//            } else if (player.getScore() == highestScore) {     //In case of a tie
//                winner = breakTie(player, winner);
//            }
//        }

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

//        for (Player player : playerList) {      //Tell all of the players whether or not they won
//            if (player == winner) {
//                player.setLogText("YOU WIN!");
//            } else {
//                player.setLogText("You lose");
//            }
//            player.clearGameBoard();
//        }

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

    private void executeCommand(String rawMessage, PrintWriter outBound) {
        String command;
        String data;

        if (!rawMessage.contains("~")) {    //Not a command
            return; //Parse any data here
        }

        command = rawMessage.substring(0, rawMessage.indexOf("~"));
        data = rawMessage.substring(rawMessage.indexOf("~")+1);

        switch (command) {
            case ("playcard"):
                outBound.println("updtotherscards~" + data);
                break;
            case ("endofround"):    //End of round update needed?
                outBound.println(getScoreString());     //Update scores
                outBound.println("setcurrplayer~" + currentPlayer.getName());
                outBound.println("clrboard~");
                outBound.println("clrcards~");
            default:
                break;
        }
    }

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
