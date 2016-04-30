import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**
 * Class that is used for setting up the threads needed for the client, the server reader and writer. 
 * @author liamberrisford
 *
 */
public class DoDClient {
	private Socket clientSocket;
	private GUIClientView GUI;
	private BlockingQueue<ServerData> GUICommands;
	private BlockingQueue<ServerData> serverResponse;
	private BlockingQueue<ServerData> chatData;
	private BlockingQueue<ServerData> serverChangeNotify;
	private BlockingQueue<ServerData> goldUpdate;

	
	public static void main(String[] args) throws IOException {
		new DoDClient().start();
	}

	/**
	 * Method that will set up the threads needed for the client and then start them.
	 * @throws IOException
	 */
	private void start() throws IOException {
		
		//The blocking queues that are needed to send messages between the threads that are used in the program.
		GUICommands = new ArrayBlockingQueue<>(100);
		serverResponse = new ArrayBlockingQueue<>(100);
		chatData = new ArrayBlockingQueue<>(100);
		serverChangeNotify = new ArrayBlockingQueue<>(100);
		goldUpdate = new ArrayBlockingQueue<>(100);
		
		
		//Makes an instance of the GUIView and will then run it on an EDT.
		GUI = new GUIClientView(GUICommands, serverResponse, chatData, serverChangeNotify, goldUpdate);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI.createAndShowGUI();
				
			}
		});

		try {
			
			//The GUICommands data will contain the settings for the game that the player wants to play
			ServerData data = GUICommands.take();
			//Will disregard any look commands that are sent until the settings have been made for the game and the server connected too.
			while(data.getData().contains("LOOK")) {
				data = GUICommands.take();
			}
			String[] gameSettings = data.getData().split("/");
			clientSocket = new Socket(gameSettings[0], Integer.parseInt(gameSettings[1]));
		} catch (IOException e) {
			System.err.println("The server did not accept you as a client, please try again later.");
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		

		System.out.println("You may now use MOVE, LOOK, QUIT and any other legal commands");
		ClientListener listener = new ClientListener(clientSocket, serverResponse, chatData, serverChangeNotify, goldUpdate, GUICommands);
		ClientWriter writer = new ClientWriter(clientSocket, GUICommands);
		Thread thread1 = new Thread(listener);
		Thread thread2 = new Thread(writer);
		thread1.start();
		thread2.start();

	}
}
