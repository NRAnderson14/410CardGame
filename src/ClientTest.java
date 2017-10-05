import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {

        try {
            Player testPlayer = new Player("Fuck off m2");
            System.out.println("Test: name: " + testPlayer.getName());
            Socket p1Socket = new Socket("127.0.0.1", 4100);
            OutputStream os = p1Socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(testPlayer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
