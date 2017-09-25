import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Card ace_hearts = new Card(14, "Hearts");
        Card five_clubs = new Card(5, "Clubs");
        Card queen_diamonds = new Card(12, "Diamonds");

        List<Card> cardlist = new ArrayList<>();
        cardlist.add(ace_hearts);
        cardlist.add(five_clubs);
        cardlist.add(queen_diamonds);

        Player player1 = new Player(cardlist);
        System.out.println(player1.getCards());
        player1.removeCard(five_clubs);
        System.out.println(player1.getCards());
        player1.addWin();
        System.out.println(player1.getWins());

        String ooop = cardlist.get(1).toReadable();
        System.out.println(ooop);

        System.out.println(); //------------------------------------------

        List<Card> testDeck;
        testDeck = CardProcessing.generateDeck();

        for (int i = 0; i < 52; ++i) {
            System.out.println(testDeck.get(i).toReadable());
        }

    }
}
