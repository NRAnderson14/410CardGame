import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        Card ace_hearts = new Card(14, Card.Suit.HEARTS);
//        Card five_clubs = new Card(5, Card.Suit.CLUBS);
//        Card queen_diamonds = new Card(12, Card.Suit.DIAMONDS);
//
//        List<Card> cardlist = new ArrayList<>();
//        cardlist.add(ace_hearts);
//        cardlist.add(five_clubs);
//        cardlist.add(queen_diamonds);
//
//
//
//        Player player1 = new Player(cardlist);
//        System.out.println(player1.getCards());
//        player1.removeCard(five_clubs);
//        System.out.println(player1.getCards());
//        player1.addWin();
//        System.out.println(player1.getWins());
//
//        String ooop = cardlist.get(1).toReadable();
//        System.out.println(ooop);
//
//        System.out.println(); //------------------------------------------
//
//        List<Card> testDeck;
//        testDeck = CardProcessing.generateDeck();
//
//        for (int i = 0; i < 52; ++i) {
//            System.out.println(testDeck.get(i).toReadable());
//        }
//
//        System.out.println();
//        CardProcessing.shuffleDeck(testDeck);
//
//        for (Card cd: testDeck) {
//            System.out.println(cd.toReadable());
//        }

//        try {
//            Board b = new Board();
//            Player p = new Player("127.0.0.1");
//            b.broadcastTest();
//            p.receiveData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Threads serverThread = new Threads("serverThread", Threads.threadType.SERVER);
        serverThread.start();
        Threads clientThread = new Threads("clientThread", Threads.threadType.CLIENT);
        clientThread.start();
    }
}
