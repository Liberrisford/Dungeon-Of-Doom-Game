import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Class that deals with the creating of the listener and writer threads for the Bot player of the game, so that it can 
 * both read and write data to the server at the same time. 
 * @author liamberrisford
 * @See BotListener.java, BotWriter.java
 */
public class Bot extends Player {
	private Socket clientSocket;
	
	public static void main(String[] args) throws IOException {
		new Bot().start();
	}
	
	/**
	 * Methods that is ran to set up the threads needed for the bot. 
	 * @throws IOException - Thrown due to the BotListener and BotWriter instances.
	 */
	public void start() throws IOException {
		
		//Connects to the server at the address of localhost and port of 40004.
		try {
			clientSocket = new Socket("localhost", 40004);
		} catch (IOException e) {
			System.err.println("The server did not accept you as a client, please try again later.");
			return;
		}
		System.out.println("The bot will now play the game!");
		
		//Setting up a FIFO queue that will allow message passing between the threads. 
		BlockingQueue<ServerData> botCommunications = new ArrayBlockingQueue<>(100);
		
		//Setting up the two threads.
		BotListener listener = new BotListener(clientSocket, botCommunications);
		BotWriter writer = new BotWriter(clientSocket, botCommunications);
		Thread thread1 = new Thread(listener);
		Thread thread2 = new Thread(writer);
		thread1.start();
		thread2.start();
	}	
}
