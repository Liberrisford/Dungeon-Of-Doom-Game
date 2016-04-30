import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;

/**
 * Thread that will monitor the serverChangeNotify blocking queue so that the server will know when it need to send a look
 * so that it can update the graphics pane of the GUI without the user needing to do anything.
 * @author liamberrisford
 *
 */
public class ServerChangeThread implements Runnable {
	private BlockingQueue<ServerData> serverChangeNotify;
	private BlockingQueue<ServerData> GUICommands;
	private BlockingQueue<ServerData> serverResponse;
	private ArrayList<JLabel> lookWindow;

	/**
	 * Constructor. Used to store all of the blocking queues that are needed for this thread and the GUIUpdate widow thread. 
	 * @param lookWindow - JLabels that make up the graphic pane that will be updated.
	 */
	public ServerChangeThread(BlockingQueue<ServerData> serverChangeNotify, BlockingQueue<ServerData> GUICommands, ArrayList<JLabel> lookWindow, BlockingQueue<ServerData> serverResponse) {
		this.serverChangeNotify = serverChangeNotify;
		this.GUICommands = GUICommands;
		this.lookWindow = lookWindow;
		this.serverResponse = serverResponse;
	}
	
	/**
	 * The flow of execution will stay in the while loop and will send a look command to the blocking queue to be sent to 
	 * the server and then the GUIUpdateWindow swing worker will update the graphic pane.
	 */
	public void run() {
		while(true) {
			ServerData data;
			try {
				data = serverChangeNotify.take();
				if(data.getData().contains("SERVERCHANGE")) {
					ServerData GUIData = new ServerData("LOOK");
					GUICommands.add(GUIData);
					new GUIUpdateWindow(lookWindow, serverResponse).execute();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
