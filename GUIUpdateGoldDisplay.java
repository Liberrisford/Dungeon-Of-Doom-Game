import java.util.concurrent.BlockingQueue;
import javax.swing.JLabel;
/**
 * Swing Worker that will update the gold display panel on the GUI.
 * @author liamberrisford
 *
 */
public class GUIUpdateGoldDisplay implements Runnable {
	private JLabel goldDisplay;
	private BlockingQueue<ServerData> goldUpdate;
	private String goldValue;
	
	public GUIUpdateGoldDisplay(JLabel goldDisplay, BlockingQueue<ServerData> goldUpdate) {
		this.goldDisplay = goldDisplay;
		this.goldUpdate = goldUpdate;
	}

	public void run() {
		while(true) {
		ServerData data;
		try {
			data = goldUpdate.take();
			//Will only take the numbers from the string and replace (remove) the rest.
			goldValue = data.getData().replaceAll("[^0-9]", "");
			goldDisplay.setText("Gold Until Win: " + goldValue.charAt(0));
		} catch (InterruptedException e) {
			System.err.println("");
			e.printStackTrace();
		}
		}
		
	}
	
	
	
}
