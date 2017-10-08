import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Example game
        try {
           //Create a new game server
            Game testGame = new Game();
            testGame.startServer();

            //Create and connect all three players
            Player p1 = new Player("p1", "127.0.0.1");
            Player p2 = new Player("p2", "127.0.0.1");
            Player p3 = new Player("p3", "127.0.0.1");

            //Start the game
            testGame.playGame();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
