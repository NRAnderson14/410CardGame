import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

/*
 *  The game's card class
 *
 *  This class specifies the cards used in the game. The cards are JButtons, which contain an image of the appropriate card.
 *  Each card has a value from 2 to 14, with 11-14 being Jack through Ace.
 *  Each card also has a suit, which is specified in the Suit enum.
 *
 *  The card depends on having the /Cards directory, which contains all of the png images for each card.
 *  The images are in the format of "VS.png", where V is the value 2-14, and S is the suit, C, D, H, or S.
 *
 */
public class Card extends JButton implements Serializable {
    private int value;
    private Suit suit;
    private String imgURL;

    //The card suits
    public enum Suit {CLUBS, DIAMONDS, HEARTS, SPADES}


    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
        imgURL = getURL();  //Get the image URL based on the suit and value

        loadImage();        //Load the image specified in imgURL
    }


    //Returns the value of the card
    public int getValue() {
        return value;
    }

    //Returns the suit of the card, in the Suit enum
    public Suit getSuit() {
        return suit;
    }

    //Used for breaking ties by ordering the suits by values
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

    //Converts the enum to a String
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

    //Converts the enum to a String for use in fetching the image URL
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

    //Constructs the URL for the appropriate card png, and returns that as a String
    private String getURL() {
        String url;

        url = "Cards/" + value + suitToURLString() + ".png";

        return url;
    }

    //Loads the png file
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


    /*
     *
     *  Testing Methods
     *
     */

    //Prints the readable string for testing purposes
    public String toReadable() {
        String res;

        if (value < 2) {
            return "ERROR: Card value must be greater than two";
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

}
