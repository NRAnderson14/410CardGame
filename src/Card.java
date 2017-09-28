public class Card {
    private int value;
    private Suit suit;

    public enum Suit {CLUBS, DIAMONDS, HEARTS, SPADES}

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toReadable() {
        String res;

        if (value < 2) {
            return "ERROR: Card value must be greater than two";  //TODO: make this an actual exception, and check when value is assigned
        }

        switch (value) {
            case(11):
                res = "Jack of ";
                break;
            case(12):
                res = "Queen of ";
                break;
            case(13):
                res = "King of ";
                break;
            case(14):
                res = "Ace of ";
                break;
            default:
                res = value + " of ";
                break;
        }

        res += suitToString(suit);

        return res;
    }

    private String suitToString(Suit suit) {
        String res = "ERROR";

        switch (suit) {
            case CLUBS:
                res =  "Clubs";
            case DIAMONDS:
                res =  "Diamonds";
            case HEARTS:
                res =  "Hearts";
            case SPADES:
                res =  "Spades";
        }

        return res;
    }
}
