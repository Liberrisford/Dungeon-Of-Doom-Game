import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Class that is used for the creating of a new thread each time a client connects to the server.
 * @author liamberrisford
 *
 */
public class ServerThread implements Runnable{
	private Socket clientConnection;
	private ProtocolC proto;
	private String serverData;
	private String clientData;
	private volatile boolean gameInPlay;
	private HashMap<Integer, Socket> currentClients;
	private ArrayList<ServerThread> currentConnections;
	private PrintWriter toClient;
	private BufferedReader fromClient;
	
	/**
	 * Sets up the instance, and vairbales that are needed for the thread to be pass commands to the game logic.
	 * @param socket - The socket with which data will be read from. 
	 * @param proto - This is the single instance of the game logic that will be played on. 
	 * @throws IOException 
	 */
	public ServerThread(Socket socket, ProtocolC proto, ArrayList<ServerThread> currentConnections) throws IOException {
		clientConnection = socket;
		this.proto = proto;
		gameInPlay = true;
		currentClients = new HashMap<Integer, Socket>();
		this.currentConnections = currentConnections;
		toClient = new PrintWriter(clientConnection.getOutputStream(), true);
		fromClient = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
		}

	/**
	 * Method that will run when each of the server threads are ran. The input from the client is read and then passed to the game 
	 * logic instance which then updates the map accordingly, and then passes the result back to the client.
	 */
	public void run() {
		
		//Assigns the player ID to the thread and will then add it to the hashmap of client socket.
		int playerID = proto.addPlayer();
		currentClients.put(playerID, clientConnection);
		System.out.println("The playerID of the player is: " + playerID);
		
		try{
			//Sets up all of the needed streams for the reading and writing from the client.
			clientData = "";
			
			while(gameInPlay) {
				//while look that will read data and send the response to the client. 
				while((clientData = fromClient.readLine()) != null) {
					if(clientData.contains("CHAT")) {
						broadcastToClients(clientData);
					} else {
						serverData = "";
						serverData = proto.parseCommand(clientData, playerID);
						if(serverData.contains("Thank you for playing!")) {
							toClient.println(serverData);
							shutdown();
							return;
						}
						toClient.println(serverData);
						if(clientData.contains("MOVE")) {
							broadcastToClients("SERVERCHANGE");
						}
					}
				}
			}
		} catch(IOException e) {
			System.err.println("There was an error reading data from the client.");
			e.printStackTrace();
		} 
	}	
	
	//Send a messgae to the current client.
	public void sendMessageToClient(String input) {
		toClient.println(input);
	}
	
	//Send a message to every connected client.
	private void broadcastToClients(String message) throws IOException {
		new Broadcast(currentConnections, message).start();
	}
	
	private void shutdown() {
		gameInPlay = false;
		try {
			fromClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		toClient.close();
	}
 	
	
}
