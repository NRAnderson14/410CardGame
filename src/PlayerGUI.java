import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PlayerGUI extends JFrame {

	private JPanel frame = new JPanel(); //BOARD CLASS
//	Card [] deck = new Card [52]; //this will be in the game card --- Replace with Player deck
	
	private List<Card> hand;

	private Player player;
	private int[] playerScores;

	private JPanel cardHolder = new JPanel();
	private JPanel topBar = new JPanel();
	private JPanel logArea = new JPanel();
	private JPanel scoreArea = new JPanel();
	private JPanel gameBoard = new JPanel();
	
	private JLabel score = new JLabel();
	private JTextArea log = new JTextArea();
	
	public PlayerGUI (String title, List<Card> hand, Player player) {
		//starting stuff
		this.setTitle(title);
		this.setSize(300, 300);  
		this.setLocation(100, 100);
		this.setVisible(true);

		this.player = player;
		
		Container container = this.getContentPane(); 
		container.add(frame);
		frame.setPreferredSize(new Dimension(1000, 700)); //main window size
		frame.setLayout(new BorderLayout());
		
		//TOP BAR
		topBar.setBackground(Color.red);
		topBar.setPreferredSize(new Dimension(1000, 50));
		topBar.setLayout(new BorderLayout());
		frame.add(topBar, BorderLayout.NORTH);
		
		Font scoreFont = new Font("Helvetica", Font.BOLD, 34);
		Font logFont = new Font("Helvetica", Font.BOLD, 24);
		
				scoreArea.setBackground(Color.yellow);
				scoreArea.setPreferredSize(new Dimension(500, 50));
				scoreArea.setLayout(new BorderLayout());
				topBar.add(scoreArea, BorderLayout.EAST);

				    playerScores = player.getCurrentScores();
				    String scoreString = "P1: " + playerScores[0] + "\t P2: " + playerScores[1] + "\t P3: " + playerScores[2] + "\t\t";
					score.setText(scoreString);
					score.setFont(scoreFont);
					scoreArea.add(score, BorderLayout.EAST);
		
				logArea.setBackground(Color.WHITE);
				logArea.setPreferredSize(new Dimension(500, 50));
//				logArea.setLayout(new BorderLayout());
				topBar.add(logArea, BorderLayout.WEST);
				
					log.setText("Game Started");
					log.setFont(logFont);
					log.setEditable(false);
					logArea.add(log);
				
				
		//bottom CARDHOLDER
		cardHolder.setBackground(Color.BLUE);
		cardHolder.setPreferredSize(new Dimension(1000, 225));
		frame.add(cardHolder, BorderLayout.SOUTH);
		
		//middle 
		gameBoard.setBackground(Color.green);
		gameBoard.setPreferredSize(new Dimension(1000, 385));
		frame.add(gameBoard, BorderLayout.CENTER);
		
		//makes the deck. This method will later be in the game/dealer class
		this.hand = new ArrayList<>(hand);

		for (Card card : hand) {
		    cardHolder.add(card);
		    card.addActionListener(e -> {
                //When the card is played
                player.playCard(card);
                cardHolder.remove(card);
                cardHolder.updateUI();
                gameBoard.add(card);
            });
        }
		
		//final stuff
		this.setResizable(false);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	//Game.
//	public Card[] makeDeck() {
//
//		int value = 2;
//
//		Card [] deck = new Card [52];
//
//		for(int i = 0; i <deck.length; i++) {
//
//			String currentSuit = "S";
//
//			if (i > 12 && i <= 25) {
//
//				currentSuit = "D";
//
//			} else if (i > 25 && i <= 39) {
//
//				currentSuit = "C";
//
//			} else if (i > 39 && i <= 52) {
//
//				currentSuit = "H";
//			}
//
//			if(value == 15 || value == 28 || value == 42) {
//
//				value = 2;
//			}
//			System.out.println("Index: " + i + " | Value: " + value + " | Suit: " + currentSuit);
//			deck[i] = new Card(value, currentSuit);
//
//			value++;
//		}
//
//		return deck;
//	}


	
	public void printDeck (Card[] deck) {
		for(int i = 0; i < deck.length; i++) {
			System.out.println(deck[i].getValue() + " - " + deck[i].getSuit());
		}
	}
	
	public void displayDeck(Card[] deck) {
		
		for(int i = 0; i < deck.length; i++) {
			frame.add(deck[i]);
		}
	}
	
	
}
