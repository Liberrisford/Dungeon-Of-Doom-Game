/**
 * Class that is used in the passing of data between threads.
 * @author liamberrisford
 *
 */

public class ServerData {
	private String serverData;
	
	public ServerData(String data) {
		serverData = data;
	}
	
	public String getData() {
		return serverData;
	}
}	
