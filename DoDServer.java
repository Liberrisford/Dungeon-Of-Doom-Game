import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Overall class for the multi threaded server, it will set up the instance of protocol, and let the map be choosen. 
 * @author liamberrisford
 *
 */
public class DoDServer {
	private ProtocolC proto;
	private ServerSocket servSocket;
	private Scanner in;
	private boolean gameWon;
	private ArrayList<ServerThread> clientConnections; 
	
	public static void main(String[] args) throws IOException {
		new DoDServer().start();
	}
	
	/**
	 * Method that will set up the protocol, decide the map and then open a server socket.
	 * @throws IOException
	 */
	private void start() throws IOException {
		in = new Scanner(System.in);
		proto = new ProtocolC();
		System.out.println("Do you want to load a specitic map?");
		System.out.println("Press enter for default map or 'tempMap.txt' if you want to reload a previous game");
		proto.selectMap(in.nextLine());
		servSocket = new ServerSocket(40004);
		gameWon = false;
		clientConnections = new ArrayList<ServerThread>();
		
		//While the game has not been won the server will wait for a client to connnect and then set them a thread up.
		while(gameWon == false) {
			Socket clientSocket = servSocket.accept();
			ServerThread server = new ServerThread(clientSocket, proto, clientConnections);
			clientConnections.add(server);
			Thread thread = new Thread(server);
			thread.start();
			
			//If the game has been won then the server will wait for a client to connect, and then shut down.
			if(proto.playerWon() == false) {
				gameWon = true;
			}
		}
		in.close();
		
		
		//The server console will then be informed that a client tried to connect after the game was won and then shut down.
		System.out.println("The server will now shut down as another client has attempted to join the game, but a player has already won.");
		System.exit(-1);
	}
}
	

		
