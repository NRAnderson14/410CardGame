import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;


import javax.swing.*;

public class MainMenu extends JFrame implements ActionListener {
	//Data
	
	private Player newPlayer;
	private Game game;
	
	
	//GUI Components
	private JPanel frame = new JPanel();
	private JPanel buttons= new JPanel();
	private JPanel topBar = new JPanel();           //The top of the screen, where the messages and score appear
    private JPanel logArea = new JPanel();          //Where the messages are displayed
    private JTextArea log = new JTextArea();
    private JTextField ip= new JTextField("127.0.0.1");
    private JLabel nameLabel=new JLabel("Username");
    private JTextField nameBox = new JTextField("Name");
    private JLabel ipLabel=new JLabel("IP Address");
	private JButton join = new JButton("Join");
	
	
	
	//Display Menu
	public void startMenu() {
        this.setTitle("Bridge");    //The Game Name
        this.setSize(1000, 700);
        this.setLocation(100, 100);
        this.setVisible(true);
       // this.player=gamePlayer;
        
       
        
        Container container = this.getContentPane();
        container.add(frame);
        frame.setPreferredSize(new Dimension(1000, 700)); //main window size
        frame.setLayout(new BorderLayout());
        createButtons();
        setupTopBar();
        setupLogArea();
        addListener();
        
        
        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
}
	 private void createButtons() {
	        buttons.setBackground(Color.green);
	        buttons.setPreferredSize(new Dimension(1000, 385));
	        buttons.add(join);
	        buttons.add(ipLabel);
	        ip.setSize(100, 50);
	        nameBox.setSize(100, 50);
	        buttons.add(nameLabel);
	        buttons.add(nameBox);
	        buttons.add(ipLabel);
	        buttons.add(ip);
	        frame.add(buttons, BorderLayout.CENTER);
	    }
	 //Sets up the top of the screen, which contains the message log and scores
	    private void setupTopBar() {
	        topBar.setBackground(Color.red);
	        topBar.setPreferredSize(new Dimension(1000, 50));
	        topBar.setLayout(new BorderLayout());
	        frame.add(topBar, BorderLayout.NORTH);

	        setupLogArea();
	    }
	    private void setupLogArea() {
	        Font logFont = new Font("Helvetica", Font.BOLD, 24);

	        logArea.setBackground(Color.WHITE);
	        logArea.setPreferredSize(new Dimension(500, 50));
	        topBar.add(logArea, BorderLayout.WEST);
	        log.setFont(logFont);
	        log.setEditable(false);
	        logArea.add(log);
	    }

	    public String getIP(){
	    	if (ip.getText()!=""){
	    	return ip.getText();
	    	}
	    	else return "Please Enter the IP Address";
	    }

	    public String getName(){
	    	 return nameBox.getText();
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton clicked = (JButton) e.getSource();//Source is JButton
			String button = clicked.getText();//Get the Text
			if (button.equals("Join")){
				
				try {
					newPlayer = new Player(getName(), getIP());
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 
				 this.setVisible(false);
				 this.update(getGraphics());
				
				log.setText("Welcome " + newPlayer.getName() + " we are waiting for others!");
				log.updateUI();
			}
				
		}
		
		public void addListener(){
			join.addActionListener(this);
		}
		
		
	    
}
