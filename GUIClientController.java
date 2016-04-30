import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This is the controller class for the MVC architecture, it is responsible for creating the threads that deal with the
 * updating of the GUI (View) from the data from the server (Model). 
 * @author liamberrisford
 *
 */
public class GUIClientController {
	private BlockingQueue<ServerData> GUICommands;
	private BlockingQueue<ServerData> serverResponse;
	private BlockingQueue<ServerData> chatData;
	private BlockingQueue<ServerData> serverChangeNotify;
	private BlockingQueue<ServerData> goldUpdate;
	private String playerName;
	private ArrayList<JLabel> lookWindow;
	
	/**
	 * Constructor. The blocking queues are used for message passing between the threads. And the lookWindow is the graphic
	 * pane that is to be updated when a look function is sent.
	 */
	public GUIClientController(BlockingQueue<ServerData> GUICommands, BlockingQueue<ServerData> serverResponse, BlockingQueue<ServerData> chatData, BlockingQueue<ServerData> serverChangeNotify, BlockingQueue<ServerData> goldUpdate, ArrayList<JLabel> lookWindow) {
		this.GUICommands = GUICommands;
		this.serverResponse = serverResponse;
		this.chatData = chatData;
		this.serverChangeNotify = serverChangeNotify;
		this.lookWindow = lookWindow;
		this.goldUpdate = goldUpdate;
	}
	
	/**
	 * Method that was used to send a second LOOK command, even though this is not needed when this client is on
	 * the paired server, as advanced feature 2 was done, this needs to be called when a move is done to update the
	 * graphic pane with other peoples servers.
	 */
	public void redundantLOOK() {
		ServerData data = new ServerData("SERVERCHANGE");
		serverChangeNotify.add(data);
	}
	
	/**
	 * Called when the quit game button is called.
	 */
	public void quitGame() {
		commandToServer("QUIT");
	}
	
	/**
	 * Used when the player presses the pickup button.
	 * @param goldDisplay - The JLabel that will display the amount of gold that is still needed until they can win.
	 */
	public void hello(JLabel goldDisplay) {
		commandToServer("HELLO");
	}
	
	/**
	 * Sends the desired direction.
	 * @param direction - North/South/East/West is sent depending on the button clicked.
	 */
	public void sendDirection(String direction) {
		commandToServer(direction);
	}
	
	//Used to send PICKUP to the server.
	public void pickup() {
		commandToServer("PICKUP");
	}
	
	/**
	 * The method will add the needed meta data to the actual message so that the server knows how to deal with the data.
	 * @param message - The actual message that the user wants to send to other players.
	 */
	public void sendChatMessage(String message) {
		commandToServer("CHAT" + playerName + " : " + message);
	}
	
	public void goldUpdateThread(JLabel goldDisplay){
		GUIUpdateGoldDisplay updateGold = new GUIUpdateGoldDisplay(goldDisplay, goldUpdate);
		Thread updateGoldThread = new Thread(updateGold);
		updateGoldThread.start();
		
	}
	
	/**
	 * Sets up the chat thread that will update the chat window with all of the chat messages that the client gets from the server.
	 * @param chatWindow
	 */
	public void chatThread(JTextArea chatWindow) {
		ClientChatThread clientChat = new ClientChatThread(chatData, chatWindow);
		Thread clientThread = new Thread(clientChat);
		clientThread.start();
	}
	
	/**
	 * Sets up the thread that will monitor when the server sends a SERVERCHANGE.
	 */
	public void serverChangeThread() {
		ServerChangeThread serverChange = new ServerChangeThread(serverChangeNotify, GUICommands, lookWindow, serverResponse);
		Thread serverChangeThread = new Thread(serverChange);
		serverChangeThread.start();
	}
	
	/**
	 * Sets up the name of the current player.
	 * @param name - users name.
	 */
	public void setName(String name) {
		playerName = name;
	}
	
	/**
	 * Sends a command to the server by putting it in the commands blocking queue to be sent by the client writer.
	 * @param input
	 */
	private void commandToServer(String input) {
		ServerData data = new ServerData(input);
		try {
			GUICommands.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that will send the data needed to connect to the server and start a swing worker thread that will update the GUI to display the settings.
	 * @param settings - The string containing the settings.
	 * @param gameSettings - The JPanel where the settings will be displayed on the GUI.
	 */
	public void newGameSettings(String settings, JPanel gameSettings) {
		ServerData data = new ServerData(settings);
		try {
			GUICommands.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new GUISubmitSettingsChanges(gameSettings, settings).execute();
		
	}
	
}
