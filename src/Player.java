import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player {   //Split into data and interface
    private List<Card> deck;
    private List<Card> playedCards;
    private int wins;
    private boolean isCurrentTurn = false;
    private boolean hasPlayed = false;
    private Card lastCardPlayed;

    private final int PORT = 4100;
//    private final String HOST;

    public Player() throws IOException {
        this.deck = new ArrayList<>();
        wins = 0;
//        HOST = host;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        ++wins;
    }

    public void setDeck(List<Card> deck) {
        this.deck = new ArrayList<>(deck);
    }

    public List<Card> getCards() {
        return deck;
    }

    public void removeCard(Card cardToBeRemoved) {
        deck.remove(cardToBeRemoved);
    }

    public void setCurrentTurn() {
        isCurrentTurn = true;
    }

    public void setNotCurrentTurn() {
        isCurrentTurn = false;
    }

    public boolean isCurrentTurn() {
        return isCurrentTurn;
    }

    private void setLastCardPlayed(Card lastPlayed) {
        lastCardPlayed = lastPlayed;
    }

    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }

    public List<Card> getCardsPlayed() {
        return playedCards;
    }

    public void playCard(Card played) {
        playedCards.add(played);
        deck.remove(played);
        setLastCardPlayed(played);
    }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    //Not used, only for network purposes if needed later
//    public void receiveData() throws IOException {
//        Socket socket = new Socket(HOST, PORT);
//        PrintWriter outBound = new PrintWriter(socket.getOutputStream(), true);
//        BufferedReader inBound = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        String message;
//        String response = "Got the memo";
//
//        try {
//            while (true) {
//                if ((message = inBound.readLine()) != null) {
//                    System.out.println(message);
//                    break;
//                }
//            }
//            outBound.println(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
