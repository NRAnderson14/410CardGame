import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private List<Card> deck;
    private int wins;
    private final int PORT = 4100;
    private final String HOST;

    public Player(String host) throws IOException {
        this.deck = new ArrayList<>();
        wins = 0;
        HOST = host;
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

    public void receiveData() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        PrintWriter outBound = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader inBound = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message;
        String response = "Got the memo";

        try {
            while ((message = inBound.readLine()) == null) {
            }
            System.out.println(message);
            outBound.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
