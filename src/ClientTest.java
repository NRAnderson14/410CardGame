import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {

        try {
            Player testPlayer = new Player("testname", "127.0.0.1", 4100);
//            Socket socket = new Socket("127.0.0.1", 4100);
//            PrintWriter outBound = new PrintWriter(socket.getOutputStream(), true);
//            BufferedReader inBound = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            String message;
//            String response = "Got the memo";
//
//            while (true) {
//                if ((message = inBound.readLine()) != null) {
//                    decode(message, testPlayer);
////                    break;
//                }
//                System.out.println(testPlayer.getScore());
//            }



//            outBound.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
