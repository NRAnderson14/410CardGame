import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


import javax.swing.*;

public class MainMenu extends JFrame implements ActionListener {
	//Data
	
	private Player newPlayer;
	
	//GUI Components
	private JPanel frame = new JPanel();
	private JPanel buttons= new JPanel();
	private JPanel topBar = new JPanel();           //The top of the screen, where the messages and score appear
    private JPanel logArea = new JPanel();          //Where the messages are displayed
    private JTextArea log = new JTextArea();
    private JTextField ip= new JTextField("");
    private JLabel nameLabel=new JLabel("Username");
    private JTextField nameBox = new JTextField("Name");
    private JLabel ipLabel=new JLabel("IP Address");
	private JButton join = new JButton("Join");
	private JButton host= new JButton("Host");
	
	
	
	//Display Menu
	public void startMenu() throws UnknownHostException {
        this.setTitle("Bridge");    //The Game Name
        this.setSize(1000, 700);
        this.setLocation(100, 100);
        this.setVisible(true);
        
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
	//Creates the buttons and places them accordingly
	 private void createButtons() throws UnknownHostException {
	        buttons.setBackground(Color.green);
	        buttons.setPreferredSize(new Dimension(1000, 385));
	        buttons.add(host);
	        buttons.add(join);
	        buttons.add(ipLabel);
	        ip.setText(InetAddress.getLocalHost().getHostAddress());
	        ip.setPreferredSize(new Dimension(100, 20));
	        nameBox.setPreferredSize(new Dimension(100, 20));
	        buttons.add(nameLabel);
	        buttons.add(nameBox);
	        buttons.add(ipLabel);
	        buttons.add(ip);
	        frame.add(buttons, BorderLayout.CENTER);
	    }
	 //Sets up the top of the screen, which contains the message log
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
	    	return ip.getText();
	    }
	    public String getName(){
	    	 return nameBox.getText();
	    }
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton clicked = (JButton) e.getSource();//Source is JButton
			String button = clicked.getText();//Get the Text
			//Have a Player Join a Server
			if (button.equals("Join")){try {
					newPlayer = new Player(getName(), getIP());
					log.setText("Welcome " + newPlayer.getName() + " we are waiting for others!");
					log.updateUI();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
			//Have a player create the Server and Join the Game
			if (button.equals("Host")){try {
					Runnable hostThread= new Runnable(){
						public void run(){
							 Game tGame = new Game();
							 tGame.startServer();
							 tGame.playGame();
						}
					};
					new Thread(hostThread).start();
					 newPlayer = new Player(getName(), InetAddress.getLocalHost().getHostAddress());
						log.setText("Welcome " + newPlayer.getName() + " we are waiting for others!");
						log.updateUI();
						
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 
				
			}
				
		}
		//Add Listeners to buttons
		public void addListener(){
			join.addActionListener(this);
			host.addActionListener(this);
		}
		
		
	    
}
