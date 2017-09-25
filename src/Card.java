public class Card {
    private int value;
    private String suit;

    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
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

        suit = suit.substring(0,1).toUpperCase() + suit.substring(1,suit.length());

        res += suit;

        return res;
    }
}
