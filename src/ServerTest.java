import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerTest {
    public static void main(String[] args) {
        try {
//            ServerSocket socket = new ServerSocket(4100);
            Socket playerSocket = null;
            int numConnected = 0;

            Player p = null;
            Player pp = null;
            Player ppp = null;
            Game tGame = new Game();
            tGame.startServer();
            tGame.playGame();
//            List<String> dock = tGame.genStrDeck();
//            tGame.shuffleDeck2(dock);
//
//            List<String> dock1 = dock.subList(0, 17);
//            List<String> dock2 = dock.subList(17, 34);
//            List<String> dock3 = dock.subList(34, 51);
//            List<List<String>> dockList = new ArrayList<>();
//            dockList.add(dock1);
//            dockList.add(dock2);
//            dockList.add(dock3);
//
//            while (numConnected < 3) {
//                try {
//                    playerSocket = socket.accept();
//                    PrintWriter outBound = new PrintWriter(playerSocket.getOutputStream(), true);
//                    BufferedReader inBound = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
//                    int innerdex = numConnected;
//
//                    Runnable server = new Runnable() {
//                        @Override
//                        public void run() {
//                            initializePlayer(outBound, dockList.get(innerdex));
//                            receiveLoop(inBound, outBound);
//                        }
//                    };
//                    new Thread(server).start();
//
//                    ++numConnected;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//            Runnable server = new Runnable() {
//                @Override
//                public void run() {
//                    receiveLoop(inBound, outBound);
//                }
//            };
//            new Thread(server).start();



//            for (int i = 0; i < 17; ++i) {
//                outBound.println("handcard~" + dock.get(i));
//            }
//
//            outBound.println("updatecurrscores~1|2|3");
//            outBound.println("setcurrplayer~self");
//            outBound.println("setcurrturn~");
//
//            outBound.println("startgui~");
//            outBound.println("getscore~");
//            outBound.println("addwin~");
//            outBound.println("getscore~");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void receiveLoop(BufferedReader inBound, PrintWriter out) {
        String message;

//        initializePlayer(out);

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

    public static void initializePlayer(PrintWriter outBound, List<String> deck) {

        for (String card : deck) {
            outBound.println("handcard~" + card);
        }

        outBound.println("updatecurrscores~1|2|3");
        outBound.println("setcurrplayer~self");
        outBound.println("setcurrturn~");

        outBound.println("startgui~");
        outBound.println("getscore~");
        outBound.println("addwin~");
        outBound.println("getscore~");
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
