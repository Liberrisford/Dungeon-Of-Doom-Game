import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * View part of the MVC architecture. This class is within the EDT thread, and will use thread and swing workers to update and deal
 * with computational heavy tasks.It is responsible for creating the GUI components that the user will see, and attaching all of the 
 * relevant action listeners to components. 
 * @author liamberrisford
 *
 */

public class GUIClientView implements ActionListener {
	private ArrayList<JPanel> JPanels = new ArrayList<JPanel>();
	private ArrayList<JLabel> lookWindow = new ArrayList<JLabel>();
	private BlockingQueue<ServerData> serverChangeNotify;
	private GUIClientController controller;
	//The reason for the variables below is due to XMing, when running on my mac it allowed me to declare then
	//when created but otherwise it would not compile on LCPU.
	private Timer timer;
	private JFrame myFrame;
	private JLabel goldDisplay;
	private JLabel IPAddress;
	private JTextField IPAddressInput;
	private JLabel name;
	private JTextField nameInput;
	private JLabel port;
	private JTextField portInput;
	private JButton startNewGame;
	private JButton northButton;
	private JButton westButton;
	private JButton eastButton;
	private JButton southButton;
	private JButton pickupButton;
	private JTextArea chatWindow;
	private JScrollPane scroller;
	private JTextField chatText;
	private JButton chatSubmit;
	private JButton quitGame;
	private JPanel chatInput;
	private JPanel goldDisplayPanel;
	private JPanel underChatInput;
	private ComponentAdapter frameResizeListener;
	private KeyListener keyInputListener;

	/**
	 * Constructor. The only parameter that is used in this class is the serverChangeNotify blocking queue the others are used for setting up
	 * the controller.
	 */
	public GUIClientView(BlockingQueue<ServerData> GUICommands, BlockingQueue<ServerData> serverResponse, BlockingQueue<ServerData> chatData, BlockingQueue<ServerData> serverChangeNotify, BlockingQueue<ServerData> goldUpdate) {
		this.serverChangeNotify = serverChangeNotify;
		controller = new GUIClientController(GUICommands, serverResponse, chatData, serverChangeNotify, goldUpdate, lookWindow);
	}
	
	/**
	 * Method that is used to create the and show, encompising designing the GUI itself, and calling method to set up the action listeners.
	 */
	public void createAndShowGUI() {
		
		//Settings
		myFrame = new JFrame("Dungeon of Dooom!!!");
		myFrame.setPreferredSize(new Dimension(650,550));
		myFrame.setMinimumSize(new Dimension(450,350));
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//JPanels is used for the panels that make up base layout of the GUI, as they share similar properties they are
		//kept in an array list so that it can be iterated through to reduce code, and then the index used for specific operations.
		JPanels.add(new JPanel());
		JPanels.get(0).setPreferredSize(new Dimension(250,350));
		
		
		for(int i = 1; i < 8; i++) { 
			JPanels.add(new JPanel());
		}
		
		JPanels.get(0).setBackground(Color.black);
		JPanels.get(1).setBackground(Color.white);
		
		//JPanels 5,6,7 make up the command buttons, and added to JPanel 4.
		JPanels.get(5).setBackground(Color.black);
		JPanels.get(6).setBackground(Color.black);
		JPanels.get(7).setBackground(Color.black);
		
		//JPanel 0 is the panel where the graphic pane is kept.
		myFrame.getContentPane().add(JPanels.get(0), BorderLayout.CENTER);
		//JPanel 1 is the panel where the settings are displayed.
		myFrame.getContentPane().add(JPanels.get(1), BorderLayout.PAGE_START);
		
		
		//Initilizing all of the layout that are used, and button color.
		BoxLayout directions = new BoxLayout(JPanels.get(4), BoxLayout.Y_AXIS);
		FlowLayout westAndEast = new FlowLayout(FlowLayout.CENTER);
		FlowLayout settings = new FlowLayout(FlowLayout.CENTER);
		GridLayout lookMiddlePane = new GridLayout(0,5);
		BoxLayout chatWindowInput = new BoxLayout(JPanels.get(3), BoxLayout.Y_AXIS);
		Color buttonColor = new Color(204,204,204);
		//SETTINGS 
		
		JPanels.get(1).setLayout(settings);
		
		IPAddress = new JLabel("IP Address: ");
		IPAddress.setBackground(Color.white);
		IPAddressInput = new JTextField("localhost");
		IPAddressInput.setPreferredSize(new Dimension(70,25));
		
		name = new JLabel("Players name: ");
		name.setBackground(Color.white);
		nameInput = new JTextField("Input here");
		nameInput.setPreferredSize(new Dimension(70,25));
		
		port = new JLabel("Connecting Port: ");
		port.setBackground(Color.white);
		portInput = new JTextField("40004");
		portInput.setPreferredSize(new Dimension(70,25));
		
		startNewGame = new JButton("Start Game");
		
		JPanels.get(1).add(IPAddress);
		JPanels.get(1).add(IPAddressInput);
		JPanels.get(1).add(port);
		JPanels.get(1).add(portInput);
		JPanels.get(1).add(name);
		JPanels.get(1).add(nameInput);
		JPanels.get(1).add(startNewGame);
		
		
		//COMMAND PANEL - DIRECTION AND PICKUP
		
		//JPanel 6 has a layout as it has three buttons as opposed to one, as 5 and 7.
		JPanels.get(6).setLayout(westAndEast);
		JPanels.get(4).setLayout(directions);
		
		
		northButton = new JButton("North");
		westButton = new JButton("West");
		eastButton = new JButton("East");
		southButton = new JButton("South");
		pickupButton = new JButton("Pickup Gold");
		
		
		northButton.setBackground(buttonColor);
		northButton.setOpaque(true);
		westButton.setBackground(buttonColor);
		westButton.setOpaque(true);
		eastButton.setBackground(buttonColor);
		eastButton.setOpaque(true);
		southButton.setBackground(buttonColor);
		southButton.setOpaque(true);
		pickupButton.setBackground(buttonColor);
		pickupButton.setOpaque(true);
		
		
		JPanels.get(5).add(northButton);
		JPanels.get(6).add(westButton);
		JPanels.get(6).add(pickupButton);
		JPanels.get(6).add(eastButton);
		JPanels.get(7).add(southButton);
		
		JPanels.get(4).add(JPanels.get(5));
		JPanels.get(4).add(JPanels.get(6));
		JPanels.get(4).add(JPanels.get(7));
		
		//GRAPHIC PANEL - DISPLAYS LOOK WINDOW
		
		JPanels.get(0).setLayout(lookMiddlePane);
		
		//One JLabel is used to represent one character from the look window.
		for(int i = 0; i < 25; i++) {
			lookWindow.add(new JLabel());
			lookWindow.get(i).setPreferredSize(new Dimension(50,50));
			JPanels.get(0).add(lookWindow.get(i));
		}
		
		
		//CHAT WINDOW and GOLD DISPLAY 
		
		
		chatWindow = new JTextArea(10,1);
		chatWindow.setEnabled(false);
		chatWindow.setLineWrap(true);
		
		scroller = new JScrollPane(chatWindow);

		chatText = new JTextField("Input Text Here");
		
		chatSubmit = new JButton("Enter");
		chatSubmit.setBackground(buttonColor);
		chatSubmit.setOpaque(true);
		
		quitGame = new JButton("Quit Game");
		quitGame.setBackground(buttonColor);
		quitGame.setOpaque(true);
		
		chatText.setPreferredSize(new Dimension(110,25));
		
		chatInput = new JPanel();
		chatInput.add(chatText);
		chatInput.add(chatSubmit);
		
		goldDisplayPanel = new JPanel();
		underChatInput = new JPanel();
		
		goldDisplay = new JLabel("Gold Until Win: ");
		goldDisplayPanel.add(goldDisplay);
		
		JPanels.get(3).setLayout(chatWindowInput);
		JPanels.get(3).add(scroller);
		JPanels.get(3).add(chatInput);
		JPanels.get(3).add(goldDisplayPanel);
		JPanels.get(3).add(underChatInput);
		
		//ACTION LISTENERS
  
		//Action listeners for buttons 
		northButton.addActionListener(this);
		eastButton.addActionListener(this);
		southButton.addActionListener(this);
		westButton.addActionListener(this);
		pickupButton.addActionListener(this);
		chatSubmit.addActionListener(this);
		quitGame.addActionListener(this);       
		startNewGame.addActionListener(this);
		
		//Calls methods for creating resizing and keyboard input listeners, and attaching them to the frame.
		createKeyInputListener();
		createFrameResizeListener();
		myFrame.addComponentListener(frameResizeListener);
		myFrame.addKeyListener(keyInputListener);
		
		//Creates threads to monitor server outputs.
		controller.chatThread(chatWindow);
		controller.serverChangeThread();
		controller.goldUpdateThread(goldDisplay);
		
		//Used to set up the java metal look so that across all of the platform possible the GUI should have consistency.
		try {
          
        UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			System.err.println("While trying to set up the Java Metal look there was an error, as it was unsupported.");
		}
		catch (ClassNotFoundException e) {
			System.err.println("While trying to set up the Java Metal look there was an error, as not class was found.");
		}
		catch (InstantiationException e) {
			System.err.println("While trying to set up the Java Metal look there was an error, with the instatntiation");
		}
		catch (IllegalAccessException e) {
			System.err.println("While trying to set up the Java Metal look there was an error.");
		}
		
		//Made focusable so that keyboard inputs can be used, and then makes the GUI visible to the user.
		myFrame.setFocusable(true);
		myFrame.pack();
		myFrame.setVisible(true);
		myFrame.setResizable(true);
		
	}
	
	/**
	 * Sets up the listener so that when the frame is resized it will send a SERVERCHANGE message prompting the thread to
	 * update the graphic pane based on the new look window. 
	 */
	private void createFrameResizeListener() {
		frameResizeListener = new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                timer = new Timer(200, new AbstractAction() {
                	@Override
                    public void actionPerformed(ActionEvent e) {
                		if (timer.isRunning()) {
                        } else {
                            ServerData data = new ServerData("SERVERCHANGE");
                            serverChangeNotify.add(data);
                        }
                        }
                });
                timer.setRepeats(false);
                timer.start();

            }
        };
	}
	
	/**
	 * Method need when implementing actionListener
	 */
	private void createKeyInputListener() {
		keyInputListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				processKeyInput("The key event was executed", e);
			}

			@Override
			public void keyReleased(KeyEvent e) {	
			}
			
		};
	}
	
	/**
	 * Method called that will idetify the key that was types and execute the relevant method.
	 * @param str
	 * @param e
	 */
	private void processKeyInput(String str, KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch( keyCode ) { 
        case KeyEvent.VK_UP:
            controller.sendDirection("MOVE N");
            break;
        case KeyEvent.VK_DOWN:
        	controller.sendDirection("MOVE S");
            break;
        case KeyEvent.VK_LEFT:
        	controller.sendDirection("MOVE W");
            break;
        case KeyEvent.VK_RIGHT :
        	controller.sendDirection("MOVE E");
            break;
        case KeyEvent.VK_SPACE :
        	controller.pickup();
        	controller.hello(goldDisplay);
     }
	}

	/**
	 * Method that is used whenever a button is clicked, the button that is clicked is then identified and the relevant code executed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		//Direction button code, with the redundantLOOK method being called.
		if(src == northButton) {
			controller.sendDirection("MOVE N");
			controller.redundantLOOK();
		} else if(src == eastButton) { 
			controller.sendDirection("MOVE E");
			controller.redundantLOOK();
		} else if(src == southButton) { 
			controller.sendDirection("MOVE S");
			controller.redundantLOOK();
		} else if(src == westButton) { 
			controller.sendDirection("MOVE W");
			controller.redundantLOOK();
			
		//Pickup button for the code.
		} else if(src == pickupButton) { 
			controller.pickup();
			controller.hello(goldDisplay);
			
		//The startnewGame button will set up a thread to remove gui components and then add others.
		} else if(src == startNewGame) {
			//All the settings are compiled as a single string with a slash seperating each part.
			String output = IPAddressInput.getText();
			output += "/";
			output += portInput.getText();
			output += "/";
			output += nameInput.getText();
			
			controller.setName(nameInput.getText());
			controller.newGameSettings(output, JPanels.get(1));
			
			//Adds the quit game button now the client has now connected.
			underChatInput.add(quitGame);
			
			//Updates the gold needed to win the game.
			controller.hello(goldDisplay);
			//JPanel 4 is where the command buttons go such as directions and pickup.
			myFrame.getContentPane().add(JPanels.get(4), BorderLayout.PAGE_END);
			//JPanel 3 is where the chat window is kept.
			myFrame.getContentPane().add(JPanels.get(3), BorderLayout.LINE_END);
		} else if(src == chatSubmit) {
			controller.sendChatMessage(chatText.getText());
			chatText.setText("");
			
			//Tells the server it is quiting the game, shuts down the client threads and will then close the GUI.
		} else if(src == quitGame) {
			controller.quitGame();
			myFrame.dispose();
			System.exit(0);
		}
		
	}
}
