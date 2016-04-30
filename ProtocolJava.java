import java.io.File;
/**
 * Class that is used for the protocol of the game. This ensures that every command from the specification will be dealt with on 
 * the server.
 * @author liamberrisford
 *
 */
public class ProtocolJava {
	protected MultiplayerLogic logic;
	
	public ProtocolJava() {
		logic = new MultiplayerLogic();
	}
	
	public void selectMap(String mapName){
		logic.setMap(new File(mapName));
	}
	
	/**
	 * Parsing and Evaluating the User Input.
	 * @param readUserInput input the user generates
	 * @return answer of GameLogic
	 */
	protected synchronized String parseCommand(String readUserInput, int playerIndex) {
		String [] command = readUserInput.trim().split(" ");
		String answer = "FAIL";
		switch (command[0].toUpperCase()){
		case "HELLO":
			answer = hello(playerIndex);
			break;
		case "MOVE":
			if (command.length == 2 )
			answer = move(command[1].charAt(0), playerIndex);
		break;
		case "PICKUP":
			answer = pickup(playerIndex);
			break;
		case "LOOK":
			answer = look(playerIndex);
			break;
		case "QUIT":
			answer = logic.quitGame(playerIndex);
			break;
		default:
			logic.printMap();
			answer = "FAIL";
		}
		return answer;
	}
	
	//Methods that are called that will call the relevant method within the logic class. 
	public String hello(int playerIndex) {
		return logic.hello(playerIndex);
	}

	public String move(char direction, int playerIndex) {
		return logic.move(direction, playerIndex);
	}

	public String pickup(int playerIndex) {
		return logic.pickup(playerIndex);
	}

	public String look(int playerIndex) {
		return logic.look(playerIndex);
	}
	
	public int addPlayer() {
		return logic.createNewPlayer();
	}
	
	public boolean playerWon() {
		return logic.gameRunning();
	}
	
 
}
