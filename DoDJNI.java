import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class that specifies all of the native methods that are to be in the c code, and where the shared library is. 
 * @author liamberrisford
 *
 */

public class DoDJNI {
	static {
		System.load(System.getProperty("user.dir")+"/jni/libDoDJNI.so");
	}
	
	public native void setMap(String mapName);
	
	public native String parseCommand(String command, int playerIndex);
	
	public native int addPlayer();
	
	public native int playerWon();
}
