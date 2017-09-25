import java.util.ArrayList;
import java.util.List;

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

    public static List<Card> shuffleDeck(List<Card> deckToBeShuffled) {
        List<Card> placeholder = new ArrayList<>();
        return placeholder;
    }
}
