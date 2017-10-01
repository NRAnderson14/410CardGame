import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

//Not used, only if needed for network purposes later
public class Board {
    private List<Card> deck;
    private final int PORT = 4100;

    public Board() throws IOException {
        deck = new ArrayList<>();
    }

    public void checkNet(String subnet) {
        int timeout = 1000;
        String host = "";
        for (int i = 1; i < 255; ++i) {
            host = subnet + "." + i;
            try {
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    System.out.println(host + " is reachable.");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void broadcastTest() throws IOException {
        ServerSocket socket = new ServerSocket(PORT);
        Socket playerSocket = socket.accept();
        PrintWriter outBound = new PrintWriter(playerSocket.getOutputStream(), true);
        BufferedReader inBound = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));

        String message = "Hello, Client!";
        String response;

        try {
            while (true) {
                outBound.println(message);
                if ((response = inBound.readLine()) != null) {
                    break;
                }
            }
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
