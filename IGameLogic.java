

import java.io.File;
//Used by all game logics to ensure all command protocols are met. 
public interface IGameLogic {
	public void setMap(File file);
	
	public String hello(int playerIndex);
	
	public String move(char direction, int playerIndex);
	
	public String pickup(int playerIndex);
	
	public String look(int playerIndex);
	
	public boolean gameRunning();
	
	public String quitGame(int playerIndex);
	
}
