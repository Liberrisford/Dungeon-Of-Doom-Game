import java.awt.Point;
import java.io.File;

public class RunTestCases  {
	private int playerID;
	private String result;
	private MultiplayerLogic logic;
	
	public static void main(String[] args) {
		RunTestCases tests = new RunTestCases();
	}
	
	public RunTestCases() {
		logic = new MultiplayerLogic();
		logic.setMap(new File(""));
		playerID = logic.createNewPlayer();
		runTests();
	}
	
	public void runTests() {
		testMovement();
	}
	
	public void testMovement() {
		System.out.println("The test for north has: " + testNorth());
		System.out.println("The test for south has: " + testSouth());
		System.out.println("The test for east has: " + testEast());
		System.out.println("The test for west has: " + testWest());
	}
	
	public String testNorth() {
		Point currentPosition = logic.playerPositions.get(playerID);
		while((logic.getMap().lookAtTile((currentPosition.x-1), currentPosition.y)) != '#') {
			logic.move('N', playerID);
		}
		result = logic.move('N', playerID);
		if(result.equals("SUCCESS")) {
			return "failed.";
		}
		return "succeded.";
	}
	
	public String testSouth() {
		Point currentPosition = logic.playerPositions.get(playerID);
		while((logic.getMap().lookAtTile((currentPosition.x+1), currentPosition.y)) != '#') {
			logic.move('S', playerID);	
		}
		result = logic.move('S', playerID);
		if(result.equals("SUCCESS")) {
			return "failed.";
		}
		return "succeded.";
	}
	
	public String testEast() {
		Point currentPosition = logic.playerPositions.get(playerID);
		while((logic.getMap().lookAtTile((currentPosition.x), currentPosition.y-1)) != '#') {
			logic.move('E', playerID);	
		}
		result = logic.move('E', playerID);
		if(result.equals("SUCCESS")) {
			return "failed.";
		}
		return "succeded.";
	}
	
	public String testWest() {
		Point currentPosition = logic.playerPositions.get(playerID);
		while((logic.getMap().lookAtTile((currentPosition.x), currentPosition.y+1)) != '#') {
			logic.move('W', playerID);
		}
		result = logic.move('W', playerID);
		if(result.equals("SUCCESS")) {
			return "failed.";
		}
		return "succeded.";
	}
}
