import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 *  The game's Player class
 *
 *  This class is used for each person playing the game, and stores all of the relevant information
 *  This class also deals with the creation and management of the player's game window
 */
public class Player extends JFrame {   //Split into data and interface
    //Data
    private Card lastCardPlayed;
    private List<Card> hand;
    private List<Card> cardsPlayedByPlayer;     //The cards the player themself have played
    private List<Card> cardsPlayedByOthers;     //The cards that the other players have played
    private int gameScore;
    private int[] currentScores;                //The scores of all the players
    private boolean isCurrentTurn;
    private volatile boolean hasPlayed;
    private String name;
    private String currentPlayer;               //The name of the current player

    //GUI
    private JPanel frame = new JPanel();
    private JPanel cardHolder = new JPanel();       //The bottom of the screen, where all of the cards appear
    private JPanel topBar = new JPanel();           //The top of the screen, where the messages and score appear
    private JPanel logArea = new JPanel();          //Where the messages are displayed
    private JPanel scoreArea = new JPanel();        //Where the scores are displayed
    private JPanel gameBoard = new JPanel();        //The middle of the screen, where the cards that are in play appear
    private JLabel score = new JLabel();
    private JTextArea log = new JTextArea();


    public Player(String name) throws IOException {
        this.name = name;
        this.hand = new ArrayList<>();
        cardsPlayedByPlayer = new ArrayList<>();
        cardsPlayedByOthers = new ArrayList<>();
        gameScore = 0;
        currentScores = new int[] {0, 0, 0};
        isCurrentTurn = false;
        hasPlayed = false;
    }


    /*
     *
     *  Data Methods
     *
     */

    /*
     *  Score methods
     */

    //Gets the player's current score
    public int getScore() {
        return gameScore;
    }

    //Gives the player another win
    public void addWin() {
        ++gameScore;
    }

    //Updates this player's list of all the other player's scores
    public void updateCurrentScores(int[] newScores) {
        currentScores = newScores;
        updatePlayerScores(currentScores);
    }

    /*
     *  Card/Deck methods
     */

    //Handles what to do when the player plays a card
    public void playCard(Card played) {     //TODO: check if the card is in the suit played by others
        Card.Suit suitInPlay = null;

        if (!cardsPlayedByOthers.isEmpty()) {
            suitInPlay = getSuitInPlay();
        }

        //It must be the player's turn in order to play a card and they can only play the same suit
        if (isCurrentTurn) {
            //If they are first, they can play any suit, and if not they must play the suit in play if they have any
            if (played.getSuit() == suitInPlay || !hasSuitInHand(suitInPlay)) {
                cardsPlayedByPlayer.add(played);    //Add the card to the list of cards that the player has played
                hand.remove(played);                //Remove the card played from the cards available to be played
                setLastCardPlayed(played);
                cardHolder.remove(played);          //Remove the card image from the cardHolder
                cardHolder.updateUI();
                hasPlayed = true;                   //The player has now played
                isCurrentTurn = false;              //And as such, it is no longer their turn
            } else {
                setLogText("You must play the same suit");
            }
        } else {
            setLogText("Wait your turn");       //Tell them that they can't play when it is not their turn
        }
    }

    //Gives the player the list of cards that they can play this game
    public void setDeck(List<Card> hand) {
        this.hand = new ArrayList<>(hand);
    }

    private void setLastCardPlayed(Card lastPlayed) {
        lastCardPlayed = lastPlayed;
    }

    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }

    //Returns all of the cards played by the player over the course of the game; Used for tie-breaking
    public List<Card> getCardsPlayedByPlayer() {
        return cardsPlayedByPlayer;
    }

    //Checks to see if the player has the selected suit in their hand
    private boolean hasSuitInHand(Card.Suit suitToCheck) {
        boolean hasSuit = false;

        for (Card card : hand) {
            if (card.getSuit() == suitToCheck) {
                hasSuit = true;
            }
        }

        return hasSuit;
    }

    //Returns the first card in the list of cards played by others
    private Card.Suit getSuitInPlay() {
        Card cardInPlay;
        cardInPlay = cardsPlayedByOthers.get(0);
        return cardInPlay.getSuit();
    }

    /*
     *  Turn methods
     */

    public void setCurrentTurn() {
        isCurrentTurn = true;
    }

    public void setNotCurrentTurn() {
        isCurrentTurn = false;
    }

    public boolean isCurrentTurn() {
        return isCurrentTurn;
    }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    public void setHasNotPlayed() {
        hasPlayed = false;
    }

    //Updates the player's internal list of cards played by the other players this round
    public void updateOthersCards(Card newCard) {
        cardsPlayedByOthers.add(newCard);       //Updates the data first
        updateGameBoard(cardsPlayedByOthers);   //Then the GUI
    }

    //Clears the list of cards played by others; Used at the beginning of each round
    public void clearOthersCards() {
        cardsPlayedByOthers.clear();
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String player) {
        currentPlayer = player;
    }

    public String getName() {
        return name;
    }


    /*
     *
     *  GUI Methods
     *
     */

    //Starts the player's window
    public void startGUI() {
        this.setTitle(name);    //The name of the player
        this.setSize(300, 300);
        this.setLocation(100, 100);
        this.setVisible(true);

        Container container = this.getContentPane();
        container.add(frame);
        frame.setPreferredSize(new Dimension(1000, 700)); //main window size
        frame.setLayout(new BorderLayout());

        setupTopBar();          //Sets up the top,
        setupGameBoard();       //Middle,
        setupCardHolder();      //and Bottom

        for (Card card : hand) {        //Add all of the cards in the player's hand
            cardHolder.add(card);
            card.addActionListener(e -> {   //Add the action to happen when the card is played (clicked)
                playCard(card);
            });
        }

        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    //Sets up the top of the screen, which contains the message log and scores
    private void setupTopBar() {
        topBar.setBackground(Color.red);
        topBar.setPreferredSize(new Dimension(1000, 50));
        topBar.setLayout(new BorderLayout());
        frame.add(topBar, BorderLayout.NORTH);

        setupLogArea();
        setupScoreArea();
    }

    //Sets up where the scores are contained
    private void setupScoreArea() {
        Font scoreFont = new Font("Helvetica", Font.BOLD, 34);

        scoreArea.setBackground(Color.yellow);
        scoreArea.setPreferredSize(new Dimension(500, 50));
        scoreArea.setLayout(new BorderLayout());
        topBar.add(scoreArea, BorderLayout.EAST);

        String scoreString = "P1: " + currentScores[0] + "\t P2: " + currentScores[1] + "\t P3: " + currentScores[2] + "\t\t";
        score.setText(scoreString);
        score.setFont(scoreFont);
        scoreArea.add(score, BorderLayout.EAST);
    }

    //Sets up where the game messages will be displayed
    private void setupLogArea() {
        Font logFont = new Font("Helvetica", Font.BOLD, 24);

        logArea.setBackground(Color.WHITE);
        logArea.setPreferredSize(new Dimension(500, 50));
        topBar.add(logArea, BorderLayout.WEST);

        if (isCurrentTurn()) {
            log.setText("Your turn");
        } else {
            log.setText("Current player: " + getCurrentPlayer());
        }
        log.setFont(logFont);
        log.setEditable(false);
        logArea.add(log);
    }

    //Sets up the middle of the screen, where the cards in play will appear
    private void setupGameBoard() {
        gameBoard.setBackground(Color.green);
        gameBoard.setPreferredSize(new Dimension(1000, 385));
        frame.add(gameBoard, BorderLayout.CENTER);
    }

    //Sets up the bottom of the screen, where the player's hand of cards appears
    private void setupCardHolder() {
        cardHolder.setBackground(Color.BLUE);
        cardHolder.setPreferredSize(new Dimension(1000, 225));
        frame.add(cardHolder, BorderLayout.SOUTH);
    }

    //Clears and adds a list of cards to the game board
    public void updateGameBoard(List<Card> newCards) {
        gameBoard.removeAll();      //Clear the board

        for (Card card : newCards) {    //Add all of the new cards
            gameBoard.add(card);
        }

        gameBoard.updateUI();
    }

    //Clears the board
    public void clearGameBoard() {
        gameBoard.removeAll();
        gameBoard.updateUI();
    }

    //Updates the message informing of the current player
    public void updateLogArea() {
        if (isCurrentTurn()) {
            log.setText("Your turn");
        } else {
            log.setText("Current player: " + getCurrentPlayer());
        }

        log.updateUI();
    }

    //Sets the message to the given String
    public void setLogText(String newText) {
        log.setText(newText);
        log.updateUI();
    }

    //Sets the scores to an array of ints, where index 0 is player 1
    public void updatePlayerScores(int[] newScores) {
        String scoreString = "P1: " + currentScores[0] + "\t P2: " + currentScores[1] + "\t P3: " + currentScores[2] + "\t\t";
        score.setText(scoreString);
        score.updateUI();
    }


    /*
     *
     *  Testing methods
     *
     */

    public List<Card> getCards() {
        return hand;
    }

    public int[] getCurrentScores() {
        return currentScores;
    }

    public List<Card> getOthersCards() {
        return cardsPlayedByOthers;
    }
}
