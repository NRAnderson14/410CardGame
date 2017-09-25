import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardProcessing {
    private static final String[] suitsList = {"Clubs", "Diamonds", "Hearts", "Spades"};

    //Generates a deck of 52 cards, in order
    public static List<Card> generateDeck() {
        List<Card> deck = new ArrayList(52);

        for (int suit = 0; suit < 4; ++suit) {
            for (int value = 2; value <= 14; ++value) {
                deck.add(new Card(value, suitsList[suit]));
            }
        }

        return deck;
    }

    public static void shuffleDeck(List<Card> deckToBeShuffled) {
        Random r = new Random();
        int index;

        for (int i = deckToBeShuffled.size()-1; i > 0; --i) {
            index = r.nextInt(i);

            swap(deckToBeShuffled, index, i);
        }
    }

    private static void swap(List<Card> list, int i, int j) {
        Card temp;

        temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
