import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerTest {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(4100);
            Socket playerSocket = socket.accept();
            PrintWriter outBound = new PrintWriter(playerSocket.getOutputStream(), true);
            BufferedReader inBound = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));

            String message = "";
            String response;

            Runnable server = new Runnable() {
                @Override
                public void run() {
                    receiveLoop(inBound, outBound);
                }
            };
            new Thread(server).start();

            Player p = null;
            Player pp = null;
            Player ppp = null;
            Game tGame = new Game(p, pp, ppp);
            List<String> dock = tGame.genStrDeck();
            tGame.shuffleDeck2(dock);


            for (int i = 0; i < 17; ++i) {
                outBound.println("handcard~" + dock.get(i));
            }

            outBound.println("updatecurrscores~1|2|3");
            outBound.println("setcurrplayer~self");
            outBound.println("setcurrturn~");

            outBound.println("startgui~");
            outBound.println("getscore~");
            outBound.println("addwin~");
            outBound.println("getscore~");
//            while (true) {
//                outBound.println(message);
//
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                if ((response = inBound.readLine()) != null) {
////                    break;
////                }
//            }

//            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveLoop(BufferedReader inBound, PrintWriter out) {
        String message;

        try {
            while (true) {
                if ((message = inBound.readLine()) != null) {
                    System.out.println(message);
                    executeCommand(message, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void executeCommand(String rawMessage, PrintWriter outBound) {
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
            default:
                break;
        }
    }
}
