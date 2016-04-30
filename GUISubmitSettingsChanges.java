import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Swing worker that is responsible for updating the GUI with the tasks that would cause the GUI to become unrepsonsive
 * when it was done on the EDT. 
 * @author liamberrisford
 *
 */
public class GUISubmitSettingsChanges extends SwingWorker<Void, Void> {
	private JPanel gameSettings;
	private String[] inputData;
	
	public GUISubmitSettingsChanges(JPanel gameSettings, String inputData){
		this.gameSettings = gameSettings;
		this.inputData = inputData.split("/");
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		//Removes the text input areas and the start game button.
		gameSettings.removeAll();
		
		//And replaces them with 
		JLabel IPAddress = new JLabel("Connected IPAddress: " + inputData[0]);
		JLabel port = new JLabel("Connected port: " + inputData[1]);
		JLabel name = new JLabel("Current Players Name: " + inputData[2]);
		
		
		
		gameSettings.add(IPAddress);
		gameSettings.add(port);
		gameSettings.add(name);
		
		//Used to show the changes on the GUI.
		gameSettings.revalidate();
		gameSettings.repaint();
		return null;
		
	}
}
