import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends JFrame {   //Split into data and interface
    //Data
    private Card lastCardPlayed;
    private List<Card> hand;
    private List<Card> cardsPlayedByPlayer;
    private List<Card> cardsPlayedByOthers;
    private int wins;
    private int[] currentScores;
    private boolean isCurrentTurn = false;
    private volatile boolean hasPlayed = false;
    private String name;
    private String currentPlayer;

    //GUI
    private JPanel frame = new JPanel();
    private JPanel cardHolder = new JPanel();
    private JPanel topBar = new JPanel();
    private JPanel logArea = new JPanel();
    private JPanel scoreArea = new JPanel();
    private JPanel gameBoard = new JPanel();
    private JLabel score = new JLabel();
    private JTextArea log = new JTextArea();


    public Player(String name) throws IOException {
        this.name = name;
        this.hand = new ArrayList<>();
        cardsPlayedByPlayer = new ArrayList<>();
        cardsPlayedByOthers = new ArrayList<>();
        wins = 0;
    }


    /*
     *
     *  Data Methods
     *
     */

    public int getWins() {
        return wins;
    }

    public void addWin() {
        ++wins;
    }

    public void setDeck(List<Card> hand) {
        this.hand = new ArrayList<>(hand);
    }

    public List<Card> getCards() {
        return hand;
    }

    public void removeCard(Card cardToBeRemoved) {
        hand.remove(cardToBeRemoved);
    }

    public void setCurrentTurn() {
        isCurrentTurn = true;
    }

    public void setNotCurrentTurn() {
        isCurrentTurn = false;
    }

    public boolean isCurrentTurn() {
        return isCurrentTurn;
    }

    private void setLastCardPlayed(Card lastPlayed) {
        lastCardPlayed = lastPlayed;
    }

    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }

    public List<Card> getCardsPlayedByPlayer() {
        return cardsPlayedByPlayer;
    }

    public void playCard(Card played) {
        if (isCurrentTurn) {
            cardsPlayedByPlayer.add(played);
            hand.remove(played);
            setLastCardPlayed(played);
            cardHolder.remove(played);
            cardHolder.updateUI();
            hasPlayed = true;
            isCurrentTurn = false;
            System.out.println(hasPlayed);
        } else {
            updateLogArea("Wait your turn");
        }
    }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    public void updateCurrentScores(int[] newScores) {
        currentScores = newScores;
        updatePlayerScores(currentScores);
    }

    public int[] getCurrentScores() {
        return currentScores;
    }

    public void updateOthersCards(Card newCard) {
        cardsPlayedByOthers.add(newCard);
        updateGameBoard(cardsPlayedByOthers);
    }

    public void clearOthersCards() {
        cardsPlayedByOthers.clear();
    }

    public List<Card> getOthersCards() {
        return cardsPlayedByOthers;
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
    public void startGUI() {
        this.setTitle(name);
        this.setSize(300, 300);
        this.setLocation(100, 100);
        this.setVisible(true);

        Container container = this.getContentPane();
        container.add(frame);
        frame.setPreferredSize(new Dimension(1000, 700)); //main window size
        frame.setLayout(new BorderLayout());

        setupTopBar();
        setupGameBoard();
        setupCardHolder();

        for (Card card : hand) {
            cardHolder.add(card);
            card.addActionListener(e -> {   //When the card is played (clicked)
                //Need to get the cards played by other players, and update GUI
                playCard(card);
            });
        }

        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Top of the window, holds the event log and player scores
    private void setupTopBar() {
        topBar.setBackground(Color.red);
        topBar.setPreferredSize(new Dimension(1000, 50));
        topBar.setLayout(new BorderLayout());
        frame.add(topBar, BorderLayout.NORTH);

        setupLogArea();
        setupScoreArea();
    }

    //Holds the player scores
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

    //Holds the event log
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

    //Middle of the window, holds the cards played in the round
    private void setupGameBoard() {
        gameBoard.setBackground(Color.green);
        gameBoard.setPreferredSize(new Dimension(1000, 385));
        frame.add(gameBoard, BorderLayout.CENTER);
    }

    //Bottom of the window, holds the player's hand
    private void setupCardHolder() {
        cardHolder.setBackground(Color.BLUE);
        cardHolder.setPreferredSize(new Dimension(1000, 225));
        frame.add(cardHolder, BorderLayout.SOUTH);
    }

    public void updateGameBoard(List<Card> newCards) {
        gameBoard.removeAll();
        System.out.println("Removed all");

        for (Card card : newCards) {
            gameBoard.add(card);
            System.out.println("Added " + card.toReadable());
        }

        gameBoard.updateUI();
        System.out.println("Updated UI");
    }

    public void updateLogArea(String newText) {
        log.setText(newText);
        log.updateUI();
    }

    public void updatePlayerScores(int[] newScores) {
        String scoreString = "P1: " + currentScores[0] + "\t P2: " + currentScores[1] + "\t P3: " + currentScores[2] + "\t\t";
        score.setText(scoreString);
        score.updateUI();
    }

}
