
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that is used to store and deal with the map for the game.
 * @author liamberrisford
 */
public class Map {
	
	private char[][] map;
	private String mapName;
	private int totalGoldOnMap;
	private String mapString;
	private int goldLeftOnMap;
	
	/**
	 * No parameter constructor. Sets the default field values. 
	 */
	public Map(){
		map = null;
		mapName = "";
		totalGoldOnMap = -1;
		mapString = "";
	}
	
	/**
	 * Constructor. Will run the no parameter constructor aswell as the readMap method.
	 * @param mapFile - File that contains the map to be used in the game. 
	 */
	public Map(File mapFile){
		this();
		readMap(mapFile);
	}
	
	/**
	 * Reads a map from a given file with the format:
	 * name <mapName> 
	 * win <totalGold>
	 *  
	 * @par A map a File pointed to a correctly formatted map file.
	 * @exception e - Thrown if the map file was not found. 
	 * @exception e1 - Thrown if the default map can't be found, after the other map file cant be found.
	 * @excpetion e2 - Thrown if the file can't be accessed.
	 * @exception e3 - Thrown if the reader can't be closed.
	 */
	public void readMap(File mapFile) {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mapFile));
		} catch (FileNotFoundException e) {
			try {
				reader = new BufferedReader(new FileReader(new File("maps","example_map.txt")));
			} catch (FileNotFoundException e1) {
				System.err.println("no valid map name given and default file example_map.txt not found");
				System.exit(-1);
			}
		}
		
		try {
			map = loadMap(reader);
		} catch (IOException e2){
			System.err.println("map file invalid or wrongly formatted");
			System.exit(-1);
		} finally {
			try {
				reader.close();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		}	
	}
	
	/**
	 * Method. Used to put the map into the 
	 * @param reader - Takes in the map file that is to be stored in the return value. 
	 * @return - A 2D char array that holds the map for the game. 
	 * @throws IOException
	 */
	private char[][] loadMap(BufferedReader reader) throws IOException { 
		boolean error = false;
		 
		ArrayList<char[]> tempMap = new ArrayList<char[]>();
		 
		int width = -1;
		 
		String in = reader.readLine();
		if (in.startsWith("name")){
			error = setName(in);
		}
		 
		in = reader.readLine();
		if (in.startsWith("win")){
				error = setWin(in);
		}
		 
		in = reader.readLine();
		if (in.charAt(0) == '#' && in.length() > 1) {
			width = in.trim().length();
		}
		
		while (in != null && !error) {
			
			char[] row = new char[in.length()];
			if  (in.length() != width)
				error = true;
			
			for (int i = 0; i < in.length(); i++)
			{
				row[i] = in.charAt(i);
			}

			tempMap.add(row);

			in = reader.readLine();
		}
		
		if (error) {
			setName("");
			setWin("");
			return null;
		}
		
		char[][] map = new char[tempMap.size()][width];
	 
		for (int i=0;i<tempMap.size();i++){
			map[i] = tempMap.get(i);
		}
	 
		return map;
	}
	
	/**
	 * Method. Sets the win conditions for the game. 
	 * @param in - String that contains the win condition for the map. 
	 * @exception n - Thrown if there is no integer in the string.
	 * @return - False returned if the win coniditon has successivly been passed. 
	 */
	private boolean setWin(String in) { 
		if (!in.startsWith("win ")) {
			return true;
		}
		
		int win = 0; 
		try { win = Integer.parseInt(in.split(" ")[1].trim());
		} catch (NumberFormatException n){
			System.err.println("the map does not contain a valid win criteria!");
		}
		if (win < 0) 
			return true;
		this.totalGoldOnMap = win;
		
		return false;
	}
	
	/**
	 * Method. Sets the name of the dungeon being played.
	 * @param in - The name of the map from the file. 
	 * @return - False is returned if the map name has been set correctly. 
	 */
	private boolean setName(String in) {
		if (!in.startsWith("name ") && in.length() < 4)
			return true;
		
		String name = in.substring(4).trim();
		 
		if (name.length() < 1) {
			return true;
		}
		
		this.mapName = name;
		
		return false;
	}
	
	/**
	 * The method replaces a char at a given position of the map with a new char
	 * @param y the vertical position of the tile to replace
	 * @param x the horizontal position of the tile to replace
	 * @param tile the char character of the tile to replace
	 * @return The old character which was replaced will be returned.
	 */
	protected char replaceTile(int y, int x, char tile) {
		char output = map[y][x];
		map[y][x] = tile;
		return output;
	}
	
	/**
	 * Method. Used to show the whole of the map, and print to the console. 
	 */
	protected void printMap(){
		mapString = ""; 
		for (int y = 0; y < getMapHeight(); y++) {
			for (int x = 0; x < getMapWidth(); x++) {
				mapString += map[y][x];
				System.out.print(map[y][x]);
			}
			System.out.println();
		}
	}
	
	/**
	 * The method returns the Tile at a given location. The tile is not removed.
	 * @param y the vertical position of the tile to replace
	 * @param x the horizontal position of the tile to replace
	 * @param tile the char character of the tile to replace
	 * @return The old character which was replaced will be returned.
	 */
	protected char lookAtTile(int y, int x) {
		if (y < 0 || x < 0 || y >= map.length || x >= map[0].length)
			return '#';
		 
		char output = map[y][x];
		
		return output;
	}
	
	/**
	 * This method is used to retrieve a map view around a certain location.
	 * The method should be used to get the look() around the player location.
	 * @param y Y coordinate of the location
	 * @param x X coordinate of the location
	 * @param radius The radius defining the area which will be returned. 
	 * Without the usage of a lamp the standard value is 5 units.
	 * @return - Part of the map that can be seen by the player. 
	 */
	protected char[][] lookWindow(int y, int x, int radius) { 
		char[][] reply = new char[radius][radius];
		
		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) { 
				int posX = x + j - radius/2;
				int posY = y + i - radius/2;
				if (posX >= 0 && posX < getMapWidth() && 
						posY >= 0 && posY < getMapHeight())
					reply[j][i] = map[posY][posX];
				else
					reply[j][i] = '#';
			}
		}
		
		reply[0][0] = 'X';
		reply[radius-1][0] = 'X';
		reply[0][radius-1] = 'X';
		reply[radius-1][radius-1] = 'X';
		 
		return reply;
	}
	
	public int goldOnMap() {
		goldLeftOnMap = 0;
		for(int i = 0; i < mapString.length(); i++) {
			if(mapString.charAt(i) == 'G') {
				goldLeftOnMap++;
			}
		}
		return goldLeftOnMap;
		
	}

	public int getWin() {
		return totalGoldOnMap;
	}

	public String getMapName() {
		return mapName;
	}

	protected int getMapWidth() {
		return map[0].length;
	}

	protected int getMapHeight() {
		return map.length;
	}

}
