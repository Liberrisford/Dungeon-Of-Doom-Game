import java.util.ArrayList;

/**
 * Class that is used to create a thread that is used to send a message to all of the connected clients on the server.
 * @author liamberrisford
 *
 */
public class Broadcast extends Thread {
	private ArrayList<ServerThread> clientWriters;
	private ServerThread st;
	private String clientMessage;
	
	public Broadcast(ArrayList<ServerThread> clientWriters, String message) {
		this.clientWriters = clientWriters;
		clientMessage = message;
	}
	
	public void run() {
		//It will iterate through an arraylist that contains each of the threads for eahc client, and call a method that
		//will send the client the desired message
		for(int i = 0; i < clientWriters.size(); i++) {
			st = clientWriters.get(i);
			st.sendMessageToClient(clientMessage);
		}
	}
	
	
}
