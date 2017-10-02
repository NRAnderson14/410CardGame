import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Testing the game
        try {
            //Do the menu stuff here and assign the players/ and or game server

            //Set up all the players
            Player playerUno  = new Player("playerUno");
            Player playerDos  = new Player("playerDos");
            Player playerTres = new Player("playerTres");

//            //Set up the game
            Game testGame = new Game(playerUno, playerDos, playerTres);
            testGame.testSetCurrScores(7, 21, 97);
            testGame.playGame();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
