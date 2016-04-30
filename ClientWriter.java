import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
/**
 * Class used to take user input and write it to the server.
 * @author liamberrisford
 *
 */
public class ClientWriter extends Player implements Runnable {
	private Socket clientSocket;
	private PrintWriter toServer;
	private BlockingQueue<ServerData> GUICommands;
	private boolean inGame;
	
	/**
	 * Constructor. Sets the objects and initials values that are needed.
	 * @param socket - The socket to which data is going to be wrote.
	 * @param queue - The blocking queue from which the server output will be wrote. 
	 * @throws IOException - Thrown when the output stream from the socket can't be retrieved.
	 */
	public ClientWriter(Socket socket,  BlockingQueue<ServerData> GUICommands) throws IOException {
		clientSocket = socket;
		toServer = new PrintWriter(clientSocket.getOutputStream(), true);
		this.GUICommands = GUICommands;
		inGame = true;
	}
	
	/**
	 * Run when the thread is first started. It will read the user input and then write to the server, and check the blocking 
	 * queue to see if the game is over and then shut down the thread accordingly.
	 */
	public void run() {
		//Loop where the flow of execution will remain until the game ends, either through quitting or winning the game.
		while(inGame) {
			try {
				ServerData data = GUICommands.take();
				toServer.println(data.getData());
				//if(data.getData().contains("Game Over!")) {
					//shutdownThread();
				//}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//Closes the stream when the thread is ending.
		toServer.close();
		try {
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("There was an issue closing down the socket of the client.");
			e.printStackTrace();
		}
	}
	
	public void shutdownThread() {
		inGame = false;
	}
}
