import java.util.concurrent.BlockingQueue;
import javax.swing.JTextArea;

/**
 * Class that is used to create a thread that will update the chat window of the GUI with any strings that are put into the
 * chatWindow blocking queue.
 * @author liamberrisford
 *
 */
public class ClientChatThread implements Runnable {
	private BlockingQueue<ServerData> chatData;
	private JTextArea chatWindow;
	
	public ClientChatThread(BlockingQueue<ServerData> chatData, JTextArea chatWindow) {
		this.chatData = chatData;
		this.chatWindow = chatWindow;
	}
	
	
	public void run() {
		//The flow of execution will stay in the while loop until the GUI is closed. 
		while(true) {
			try {
				ServerData data = chatData.take();
				String message = data.getData().replace("CHAT", "");
				chatWindow.append(message + "\n");
			} catch (InterruptedException e) {
				System.err.println("There was an error with the chat feature of the game, there was an Interruption.");
				e.printStackTrace();
			}
			
		}
		
	}

}
