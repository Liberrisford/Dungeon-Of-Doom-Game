/**
 * Deals with the protocol for the C code such as calling the relevant methods and dealing with their output.
 * @author liamberrisford
 *
 */

public class ProtocolC {
	private DoDJNI jni;
	
	public ProtocolC() {
		jni = new DoDJNI();
	}
	

	public void selectMap(String mapName){
		jni.setMap(mapName);
	}
	
	/**
	 * Parsing and Evaluating the User Input.
	 * @param readUserInput input the user generates
	 * @return answer of GameLogic
	 */
	protected synchronized String parseCommand(String readUserInput, int playerIndex) {
		String answer = jni.parseCommand(readUserInput, playerIndex);
		return answer;
	}
	
	public int addPlayer() {
		return jni.addPlayer();
	}
	
	public boolean playerWon() {
		int won = jni.playerWon();
		if(won == 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
