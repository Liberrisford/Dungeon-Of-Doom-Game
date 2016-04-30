
import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
/**
 * Class that is responsible for the reading of data from the server, and then decideding what the next move of the Bot should be. 
 * @author liamberrisford
 *
 */
public class BotWriter extends Player implements Runnable{
	private Socket clientSocket;
	private PrintWriter toServer;
	private BlockingQueue<ServerData> serverDataQueue;
	private Random random;
	private static final char [] DIRECTIONS = {'N','S','E','W'};
	private char[][] lookWindow;
	private Point goldLocation;
	private Point playerLocation = new Point(3,2);
	private int goldNeeded;
	private int checkMap;
	private boolean inGame;
	
	/**
	 * Constructor. Sets all of the initial values needed for the instance of the object.
	 * @param socket - The socket that is to be wrote too.
	 * @param queue - The blocking queue from which the server output will be read from.
	 * @throws IOException - Thrown when the output stream from the socket can't be obtained.
	 */
	public BotWriter(Socket socket, BlockingQueue<ServerData> botCommunication) throws IOException {
		clientSocket = socket;
		toServer = new PrintWriter(clientSocket.getOutputStream(), true);
		serverDataQueue = botCommunication;
		random = new Random();
		checkMap = 0;
		inGame = true;
	}
	
	/**
	 * Method that will run when the thread is started. Writes the next command of the bot to the server, and then decides
	 * on the next move based on the server response. 
	 */
	public void run() {
		try {
			//The first command is hello, so that it can set how much gold it needs to find.
			String answer = "HELLO";
			String output = "";
			ServerData data;
			System.out.println(answer);
			toServer.println(answer);
			
			//The loop will run while no one else has won the game. 
			while(inGame) {
				data = serverDataQueue.take();
				output = data.getData();
				System.out.println(output);
				
				if(output.contains("Game Over!")) {
					shutdownThread();
				}
				
				if(answer.equals("HELLO")) {
					setNeededGold(output);
				}
				
				answer = botAction(answer);
				System.out.println(answer);
				
				//Used to make sure that the G in the word is not being picked up when the bot is looking for a G
				//on the map in the game. 
				output = output.replace("GOLD", "");
				
				//Decides which key tile is being looked for.
				if(goldNeeded == 0) {
					updateLookWindow(output, "E");
				} else {
					if(output.contains("G")) {
						updateLookWindow(output, "G");
					}
				}
				if(output.contains("Game Over!")) {
					inGame = false;
				}
				toServer.println(answer);
				Thread.sleep(4000);
			}
		} catch (InterruptedException e) {
			System.out.println("There was an interrupted exception sending data to the seerver or with the thread sleep, please try again.");
			e.printStackTrace();
		} finally {
			//Closes all of the streams used by the method when the thread shutdowns.
			toServer.close();
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("The client was not able to close the socket.");
			}
		}
	}
	
	
	/**
	 * Method. Used to keep track of what the next move for the Bot should be. 
	 * @param lastAnswer - The last answer that was done by the bot.
	 * @return - What the command for the Bot should be next round. 
	 */
	private String botAction(String lastAnswer){
		//Checks the surroundings of the bot every 5 moves.
		if(checkMap == 5) {
			checkMap = 0;
			return "LOOK";
		}
		switch (lastAnswer.split(" ")[0]){
		case "":
			return "HELLO";
		default:
			checkMap++;
			return "MOVE " + DIRECTIONS[random.nextInt(4)];
		}
	}
	
	/**
	 * Method that is used for looking at the surroundings of the bot and looking for key tiles.. Once it has
	 * found them it will then calculate the moves needed to get to the tile. When it finds a key element it will store
	 * it.
	 * @param window - A string of the characters that make up the look window.
	 * @param desiredTile - The tile that is being looked for by the Bot.
	 * @throws InterruptedException - Refer to the goTo method.
	 */
	public void updateLookWindow(String window, String desiredTile) throws InterruptedException {
		String newLine = System.getProperty("line.separator");
		
		//2D array where the look window is to be stored. 
		lookWindow = new char [window.length()] [window.length()];
		//Puts the window into a 1D to then be split up into the 2D version.
		char[] character = window.toCharArray();
		
		int j = 0;
		int k = 0;
		for(int i = 0; i < character.length; i++, k++) {
			String input = Character.toString(character[i]);
			
			//Will start a new row if the current character is a new line character, otherwise it puts the character
			//in the next position in the row. 
			if(input.equals(newLine)) {
				j++;
				k = 0;
			} else {
				
				//When the character being looked at is the needed tile it will then store the location. 
				if(input.equals(desiredTile)) {
					System.out.println("The position of the gold is " + k + " " + j);
					goldLocation = new Point(k,j);
					
				//It will also store the position of the player.	
				} else if(input.equals("P")) {
					System.out.println("The position of the player is " + k + " " + j);
				}
				lookWindow[k][j] = character[i];
			}
		}
		goTo();
	}
	
	/**
	 * Method used to calculate the moves needed to move the player from their current position to the current tile.
	 * @throws InterruptedException - Refer to the getResponse method. 
	 */
	public void goTo() throws InterruptedException {
		int xDifference = goldLocation.x - playerLocation.x;
		int yDifference = goldLocation.y - playerLocation.y;
		System.out.println("The difference in x is " + xDifference + ", the difference in y is " + yDifference);
		
		//Calculates the moves needed to make the xDifference equal to zero, it will then execute the commands needed.
		if(xDifference > 0) {
			for(int i = 0; i < xDifference; i++) {
				toServer.println("MOVE E");
				System.out.println(getResponse());
			}
		} else {
			for(int i = 0; i > xDifference; i-- ) {
				toServer.println("MOVE W");
				System.out.println(getResponse());
			}
		}
		
		//Does the same as the xDifference but for the y axis.
		if(yDifference > 0) {
			for(int i = 0; i < yDifference; i++) {
				toServer.println("MOVE S");
				System.out.println(getResponse());
			}
		} else {
			for(int i = 0; i > yDifference; i-- ) {
				toServer.println("MOVE N");
				System.out.println(getResponse());
			}
		}
		
		//Pickups the gold the player is now on. 
		toServer.println("PICKUP");
		System.out.println(getResponse());
		
		//Then updates the amount of gold that is now needed.
		toServer.println("HELLO");
		String result = getResponse();
		setNeededGold(result);
		System.out.println(result);
		
		toServer.println("LOOK");
		System.out.println(getResponse());
	}
	
	//Gets what the output from the server was through the blocking queue.
	public String getResponse() throws InterruptedException {
		ServerData data = serverDataQueue.take();
		return data.getData();
	}
	
	//Takes the output from the hello command and stores the gold needed.
	public void setNeededGold(String output) {
		goldNeeded = Integer.parseInt("" + output.charAt(6));
	}
	
	public void shutdownThread() {
		inGame = false;
		System.out.println("The Bot is now going to shutdown. Thank you for playing!");
	}
}
