import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Card extends JButton {
    private int value;
    private Suit suit;
    private String imgURL;

    //The card suits
    public enum Suit {CLUBS, DIAMONDS, HEARTS, SPADES}

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
        imgURL = getURL();

        loadImage();
    }

    public int getValue() {
        return value;
    }

    public Suit getSuit() {
        return suit;
    }

    public int suitToValue() {
        int val = 0;

        switch (suit) {
            case CLUBS:
                val = 1;
                break;
            case DIAMONDS:
                val = 2;
                break;
            case HEARTS:
                val = 3;
                break;
            case SPADES:
                val = 4;
                break;
        }

        return val;
    }

    //Is this needed?
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

    //Again, is this needed?
    private String suitToString(Suit suit) {
        String res = "ERROR";

        switch (suit) {
            case CLUBS:
                res =  "Clubs";
                break;
            case DIAMONDS:
                res =  "Diamonds";
                break;
            case HEARTS:
                res =  "Hearts";
                break;
            case SPADES:
                res =  "Spades";
                break;
        }

        return res;
    }

    private String suitToURLString() {
        String res = "";

        switch (suit) {
            case CLUBS:
                res =  "C";
                break;
            case DIAMONDS:
                res =  "D";
                break;
            case HEARTS:
                res =  "H";
                break;
            case SPADES:
                res =  "S";
                break;
        }

        return res;
    }

    private String getURL() {
        String url;

        url = "Cards/" + value + suitToURLString() + ".png";

        return url;
    }

    private void loadImage() {
        int ratio = 8;

        try {
            Image img = ImageIO.read(getClass().getResource(imgURL));
            Image resizedImg = img.getScaledInstance(500/ratio, 726/ratio, java.awt.Image.SCALE_SMOOTH);
            this.setIcon(new ImageIcon(resizedImg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
