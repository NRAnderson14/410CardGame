import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Testing the game
        try {
            //Do the menu stuff here and assign the players/ and or game server

            //Set up all the players
            Player playerUno  = new Player("playerUno", "127.0.0.1", 4100);
            Player playerDos  = new Player("playerDos", "127.0.0.1", 4100);
            Player playerTres = new Player("playerTres", "127.0.0.1", 4100);

//            //Set up the game
//            Game testGame = new Game(playerUno, playerDos, playerTres);
//            testGame.playGame();

            //Testing the win functionality
//            testGame.testSetCurrScores(7, 0, 0);
//            testGame.startAllGUIs();
//            testGame.testGetWinner();

//            System.out.println("here");
//            Socket p1Socket = null;
//            Socket p2Socket = null;
//            Socket p3Socket = null;
//
//            List<Socket> persList = new ArrayList<>(3);
//            persList.add(p1Socket);
//            persList.add(p2Socket);
//            persList.add(p3Socket);
//
//            for (Socket socket : persList) {
//                socket = new Socket("127.0.0.1", 4100);
//            }

            Game testGame = new Game(playerUno, playerDos, playerTres);
            List<String> dock = testGame.genStrDeck();
            testGame.shuffleDeck2(dock);

            for (String card : dock) {
                System.out.println(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
