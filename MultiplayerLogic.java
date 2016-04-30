import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

/**
 * Class that is used for the logic of the game, it deals with the updating of the map and the players on the map.  
 * @author liamberrisford
 *
 */
public class MultiplayerLogic implements IGameLogic {
	protected HashMap<Integer, Point> playerPositions;
	protected HashMap<Integer, Integer> collectedGold;
	private HashMap<Integer, Boolean> tileRestore;
	private HashMap<Integer, Character> tileToRestore;
	private HashMap<Integer, Point> goldPosition;
	private HashMap<Integer, Integer> onGold;
	private int newPlayerIndex = 0;
	private Map map = null;
	private boolean gameInPlay;
	private int winningPlayer;
	private boolean spawnOnKeyTile;
	private int goldLeftOnMap;
	private boolean notEnoughGold;
	
	/**
	 * Constructor. Sets all of the needed collections and variables for the class.
	 */
	public MultiplayerLogic() {
		super();
		playerPositions = new HashMap<Integer, Point>();
		collectedGold = new HashMap<Integer, Integer>();
		tileRestore = new HashMap<Integer, Boolean>();
		tileToRestore = new HashMap<Integer, Character>();
		goldPosition = new HashMap<Integer, Point>();
		onGold = new HashMap<Integer, Integer>();
		map = new Map();
		gameInPlay = true;
		spawnOnKeyTile = false;
		goldLeftOnMap = map.goldOnMap();
		notEnoughGold = false;
		
	}
	
	protected Map getMap() {
		return map;
	}
	
	public void setMap(File file) {
		getMap().readMap(file);
	}

	/**
	 * Prints how much gold is still required to win!
	 * @param playerIndex - The index of the player
	 */
	public String hello(int playerIndex) {
		return "GOLD: " + (getMap().getWin() - collectedGold.get(playerIndex));
	}

	/**
	 * By proving a character direction from the set of {N,S,E,W} the gamelogic 
	 * checks if this location can be visited by the player. 
	 * If it is true, the player is moved to the new location.
	 * @return If the move was executed Success is returned. If the move could not execute Fail is returned.
	 * @param - The direction the player wants to move. 
	 * @param playerIndex - The index of the player
	 */
	public String move(char direction, int playerIndex) {
		if(notEnoughGold) {
			return "Game Over! Thank you for playing! No one can win the game, the server will now shut down!";
		} else if (!(gameRunning())) {
			return "Game Over! Thank you for playing! The game was won by player " + winningPlayer;
		}
		
		if(spawnOnKeyTile) {
			restoreKeyTile(playerIndex, playerPositions.get(playerIndex));
			spawnOnKeyTile = false;
		}
		
		Point newPosition = (Point) playerPositions.get(playerIndex).clone();
		switch (direction){
		case 'N':
			newPosition.x -=1;
			break;
		case 'E':
			newPosition.y +=1;
			break;
		case 'S':
			newPosition.x +=1;
			break;
		case 'W':
			newPosition.y -=1;
			break;
		default:
			break;
		}
		
		//Checks to make sure that the tile is not a wall or a player. 
		if((getMap().lookAtTile(newPosition.x, newPosition.y) != '#') && (getMap().lookAtTile(newPosition.x, newPosition.y) != 'P')) {
			removePlayerOnMap(playerIndex);
			if(tileRestore.get(playerIndex) == true) {
				restoreKeyTile(playerIndex, playerPositions.get(playerIndex));
			}
			playerPositions.put(playerIndex, newPosition);
			if (checkWin(playerIndex)) {
				return "Congratulations!!! \n You have escaped the Dungeon of Dooom!!!!!! \n" + "Thank you for playing!";
			}
			showPlayerOnMap(playerIndex);
			return "SUCCESS";
		} else {
			showPlayerOnMap(playerIndex);
			return "FAIL";
		}
	}
	
	/**
	 * Method used to pickup a piece of gold if there is one on the current tile. It will restore the gold if it was not picked up.
	 * @param playerIndex - The index of the player
	 */
	public String pickup(int playerIndex) {
		Point playerLocation = playerPositions.get(playerIndex);
		Point goldLocation = goldPosition.get(playerIndex);
		if((onGold.get(playerIndex) == 2)) {
			onGold.put(playerIndex, 1);
			if((playerLocation.x == goldLocation.x) && (playerLocation.y == playerLocation.y)) {
				collectedGold.put(playerIndex, (collectedGold.get(playerIndex)+1));
				getMap().replaceTile(playerLocation.x, playerLocation.y, '.');
				showPlayerOnMap(playerIndex);
				return "SUCCESS, GOLD COINS: " + collectedGold;
			}
		}
		return "FAIL" + "\n" + "There is nothing to pick up...";
	}

	/**
	 * The method shows the dungeon around the player location.
	 * @param playerIndex - The index of the player
	 */
	public String look(int playerIndex) {
		Point playerLocation = playerPositions.get(playerIndex);
		String output = "";
		char [][] lookReply = getMap().lookWindow(playerLocation.x, playerLocation.y, 5);
		lookReply[2][2] = getMap().lookAtTile(playerLocation.x, playerLocation.y);
		
		for (int i=0;i<lookReply.length;i++){
			for (int j=0;j<lookReply[0].length;j++){
				output += lookReply[j][i];
			}
			output += "\n";
		}
		return output;
	}

	/*
	 * Prints the whole map directly to Standard out.
	 */
	public void printMap() {
		getMap().printMap();
	}

	/**
	 * Finds a random position for the player in the map.
	 * @return Return null; if no position is found or a position vector [y,x]
	 * @param playerIndex - The index of the player
	 */
	private Point initiatePlayer(int playerIndex) {
		Point pos = new Point();
		Random rand = new Random();

		pos.x =rand.nextInt(getMap().getMapHeight());
		pos.y =rand.nextInt(getMap().getMapWidth());
		int counter = 1;
		while ((counter < getMap().getMapHeight() * getMap().getMapWidth()) && ((getMap().lookAtTile(pos.x, pos.y) == '#') || (getMap().lookAtTile(pos.x, pos.y) == 'P'))) {
			pos.y= (int) ( counter * Math.cos(counter));
			pos.x=(int) ( counter * Math.sin(counter));
			counter++;
		}
		if ((getMap().lookAtTile(pos.x, pos.y) == 'G') || (getMap().lookAtTile(pos.x, pos.y) == 'E')) {
			if(getMap().lookAtTile(pos.x, pos.y) == 'G') {
				goldPosition.put(playerIndex, pos);
				onGold.put(playerIndex, 2);
			}
			Character tile = new Character(getMap().lookAtTile(pos.x, pos.y));
			tileToRestore.put(playerIndex, tile);
			tileRestore.put(playerIndex, true);
			spawnOnKeyTile = true;
		}
		getMap().replaceTile(pos.x, pos.y, 'P');
		return (getMap().lookAtTile(pos.x, pos.y) == '#') ? null : pos;
	}

	/**
	 * checks if the player collected all GOLD and is on the exit tile
	 * @return True if all conditions are met, false otherwise
	 * @param playerIndex - The index of the player
	 */
	protected boolean checkWin(int playerIndex) {
		Point playerLocation = playerPositions.get(playerIndex);
		if ((collectedGold.get(playerIndex) >= getMap().getWin()) && (getMap().lookAtTile(playerLocation.x, playerLocation.y) == 'E')) {
			gameInPlay = false;
			winningPlayer = playerIndex;
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the player from the map. 
	 * @param playerIndex - The index of the player
	 */
	protected void removePlayerOnMap(int playerIndex) {
		Point position = playerPositions.get(playerIndex);
		if(getMap().lookAtTile(position.x, position.y) == 'P') {
			getMap().replaceTile(position.x, position.y, '.');
		}
	}
	
	/**
	 * Puts the player on the map, and will update the tile the player was on if it was on a current tile.
	 * @param playerIndex - The index of the player
	 */
	protected void showPlayerOnMap(int playerIndex){
		Point newPosition = playerPositions.get(playerIndex);
		if ((getMap().lookAtTile(newPosition.x, newPosition.y) == 'G') || (getMap().lookAtTile(newPosition.x, newPosition.y) == 'E')) {
			if(getMap().lookAtTile(newPosition.x, newPosition.y) == 'G') {
				goldPosition.put(playerIndex, newPosition);
				onGold.put(playerIndex, 2);
			}
			Character tile = new Character(getMap().lookAtTile(newPosition.x, newPosition.y));
			tileToRestore.put(playerIndex, tile);
			tileRestore.put(playerIndex, true);
		}
		getMap().replaceTile(newPosition.x, newPosition.y, 'P');
	}
	
	/**
	 * Used to restore the tile after the player has moved of it.
	 * @param playerIndex - The index of the player.
	 * @param oldPosition - The position the player has just moved off. 
	 */
	protected void restoreKeyTile(int playerIndex, Point oldPosition) {
		if((onGold.get(playerIndex)) == 1) {
			goldLeftOnMap--;
		} else {
			getMap().replaceTile(oldPosition.x, oldPosition.y, tileToRestore.get(playerIndex));
		}
		onGold.put(playerIndex, 0);
		tileRestore.put(playerIndex, false);
	}

	/**
	 * Quits the game when called
	 * @param playerIndex - The index of the player
	 */
	public String quitGame(int playerIndex) {
		System.out.println("Player: " + playerIndex + " has left the game.");
		removePlayerOnMap(playerIndex);
		goldLeftOnMap = map.goldOnMap();
		boolean playerCanWin = true;
		for(int key : collectedGold.keySet()) {
			if((map.getWin() - (goldLeftOnMap + collectedGold.get(key))) <= 0) {
				playerCanWin = false;
			} 
		}
		if(playerCanWin) {
			return "Game Over!";
		}
		gameInPlay = false;
		notEnoughGold = true;
		return "Game Over! Thank you for playing! No one can win the game, the server will now shut down!";
		
	}
	
	/**
	 * Sets up everything that is needed for a new player to join the game. 
	 * @return - The index of the new player.
	 */
	public int createNewPlayer() {
		newPlayerIndex++;
		playerPositions.put(newPlayerIndex, initiatePlayer(newPlayerIndex));
		collectedGold.put(newPlayerIndex, 0);
		tileRestore.put(newPlayerIndex, false);
		onGold.put(newPlayerIndex, 0);
		return newPlayerIndex;
	}

	public boolean gameRunning() {
		return gameInPlay;
	}
}
