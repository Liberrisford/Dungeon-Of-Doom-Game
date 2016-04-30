import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
/**
 * Class used by the client to read data from the server and then inform the user of the servers response.
 * @author liamberrisford
 */
public class ClientListener implements Runnable {
	private Socket clientSocket;
	private BufferedReader fromServer;
	private boolean inGame;
	private BlockingQueue<ServerData> serverChangeNotify;
	private BlockingQueue<ServerData> serverResponse;
	private BlockingQueue<ServerData> chatData;
	private BlockingQueue<ServerData> goldUpdate;
	private BlockingQueue<ServerData> GUICommands;
	private boolean initialLOOK;
	
 	
	/**
	 * Consructor. Used to pass all of the needed objects and initials setting for the class.
	 * @param socket - The socket from which data will be read. 
	 * @param queue - The blocking queue which data from the server will be wrote so that it can be read by other threads.
	 * @throws IOException - Thrown when the input stream cant be retrieved from the socket. 
	 */
	public ClientListener(Socket socket, BlockingQueue<ServerData> serverResponse, BlockingQueue<ServerData> chatData, BlockingQueue<ServerData> serverChangeNotify, BlockingQueue<ServerData> goldUpdate, BlockingQueue<ServerData> GUICommands) throws IOException {
		clientSocket = socket;
		fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		inGame = true;
		this.serverChangeNotify = serverChangeNotify;
		this.serverResponse = serverResponse;
		this.chatData = chatData;
		this.goldUpdate = goldUpdate;
		this.GUICommands = GUICommands;
		initialLOOK = true;
		
	}
	
	/**
	 * Ran when the thread is started. 
	 */
	public void run() {
		//While loop will be broken when the game has ended, either through quiting or another player winning. 
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
				
				//If the output is just game over, thank you for playing then another player has won the game, and the output will print which player t was.
				if(output.contains("Game Over!")) {
					if(output.contains("Thank you for playing!")) {
					} else {
						//If it doesnt contain that then they have quit the game.
						System.out.println("You have quit the game!");
					}
					shutdownThread();
				}
				
				//Used to update the look window when the GUI first starts up.
				if(initialLOOK) {
					ServerData firstLOOK = new ServerData("SERVERCHANGE");
					serverChangeNotify.add(firstLOOK);
					initialLOOK = false;
				}
				
				//Will work out which blocking queue, and therefore which thread needs the servers output.
				//The SERVERCHANGE will prompt that the ServerChangeThread should update the graphical pane of the GUI.
				if(output.contains("SERVERCHANGE")) {
					ServerData changeData = new ServerData("SERVERCHANGE");
					serverChangeNotify.add(changeData);
				
				//The CHAT will prompt the ClientChatThread that there is new text to display in the GUI.
				} else if(output.contains("CHAT")) {
					ServerData data = new ServerData(output);
					chatData.add(data);
				
				//If there is gold then it will mean that a HELLO command was sent and the gold display needs to be refreshed incase there is a change.
				} else if(output.contains("GOLD:")) {
					if(output.contains("SUCCESS")) {
						ServerData data = new ServerData("HELLO");
						GUICommands.add(data);
					} else {
						ServerData data = new ServerData(output);
						goldUpdate.add(data);
					}
				} else {
					
					//SUCCESS AND FAIL are ignored as they don't need to be displayed on the GUI.
					if(output.contains("SUCCESS") || output.contains("FAIL")) {
					} else {
						//The only other output is a LOOK command window that will then be used to update the graphic pane.
						ServerData data = new ServerData(output);
						serverResponse.add(data);
					}
				}
			} catch(IOException e) {
				System.err.println("There was an issue reading from the server, please try again.");
				e.printStackTrace();
			}
		}
		
		//Closes the streams when the thread ends.
		try {
			fromServer.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("There was an error closing the socket and buffered reader of the client.");
			e.printStackTrace();
		}
		
	}
	
	public void shutdownThread() {
		inGame = false;
	}
}
