import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Class that is responsible for reading data from the server and then putting it to the blocking queue so it can be accessed 
 * by the writer class.
 * @author liamberrisford
 */
public class BotListener implements Runnable {
	private Socket clientSocket;
	private BufferedReader fromServer;
	private boolean inGame;
	private BlockingQueue<ServerData> botCommunications;
	
	/**
	 * Constructor. Used to set the fields of the given thread.
	 * @param socket - The socket with which the thread has connected to the server.
	 * @param queue - The queue that data from the server will be wrote to.
	 * @throws IOException - Occurs when the buffered reader can't get the input stream from the socket.
	 */
	public BotListener(Socket socket, BlockingQueue<ServerData> botCommunications) throws IOException {
		clientSocket = socket;
		this.botCommunications = botCommunications;
		fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		inGame = true;
	}

	/**
	 * Method. Will be run when the thread is started. Will deal with the reader of data from the server, and the passing 
	 * of the data to the writer thread. 
	 */
	public void run() {
		//Loop will run while the game has not been won by someone else. 
		while(inGame) {
			try {
				String output = "";
				
				//Will read the next (up to the new line) and then put a new line in, and continue until there is no more data 
				//to be read from the socket.
				do{
					output += fromServer.readLine();
					
					//If the output is null then the server is no longer reachable, and the error is printed and the server shut down.
					if(output.contains("null")) {
						System.out.println("The server is no longer reachable, please try again.");
						shutdownThread();
						output = "Game Over!";
					}
					output += System.getProperty("line.separator");
				} while(fromServer.ready());
				
			
				if(output.contains("Game Over!")) {
					inGame = false;
				}
				
				//If the output contains SERVERCHANGE then it is ignored as it is not intended for the Bot.
				if(output.contains("SERVERCHANGE")) {
					
				} else {
					//Puts the output from the server to the blocking queue to be read by other threads.
					ServerData data = new ServerData(output);
					botCommunications.put(data);
				}
				
				
			} catch (IOException e) {
				System.err.println("There was an issue reading from the server, please restart the client.");
				e.printStackTrace();
			} catch(InterruptedException e) {
				System.err.println("There was an issue retrieving data from the server, please restart the client");
				e.printStackTrace();
			}
		}
		try {
			//Closes all of the streams used by the method when the thread shutdowns.  
			fromServer.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("There was an issue in closing the buffered reader and client socket.");
			e.printStackTrace();
		}
		
	}
	
	public void shutdownThread() {
		inGame = false;
	}
	
	
}
