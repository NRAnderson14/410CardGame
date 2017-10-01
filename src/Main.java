import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Testing the game
        try {
            //Set up all the players
            Player playerUno  = new Player();
            Player playerDos  = new Player();
            Player playerTres = new Player();

            //Set up the game
            Game testGame = new Game(playerUno, playerDos, playerTres);
            testGame.playGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
