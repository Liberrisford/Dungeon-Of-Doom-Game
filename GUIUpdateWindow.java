import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
/**
 * Swing worker that will go through the servers characeter look window and replace it will pixel art images for the GUI.
 * @author liamberrisford
 *
 */
public class GUIUpdateWindow extends SwingWorker<char[][], Void> {
	private char[][] lookWindow = new char[100][100];
	private String window;
	private ArrayList<JLabel> UILookWindow;
	private BlockingQueue<ServerData> serverResponse;
	
	/**
	 * Constructor.
	 * @param UILookWindow - The JLabels that are to be used to be store the image.
	 * @param serverResponse - Blocking queue that contains the character version of the look window.
	 */
	public GUIUpdateWindow(ArrayList<JLabel> UILookWindow, BlockingQueue<ServerData> serverResponse) {
		this.UILookWindow = UILookWindow;
		this.serverResponse = serverResponse;
	}
	
	/**
	 * This method is executed first and when it is finished then done() is ran automatically after.
	 */
	@Override
	protected char[][] doInBackground() throws Exception {
			ServerData data = serverResponse.take();
			window = data.getData();
			//Go through the string and put it into the a 2D char array.
			lookWindow = new char [window.length()] [window.length()];
			int k = 0;
			for(int i = 0; i < 5; i++, k++) {
				for(int j = 0; j < 5; j++, k++) {
					lookWindow[j][i] =  window.charAt(k);
				}
			}
			return lookWindow;
	}
	
	/**
	 * Executed at the end of the doInBAckground() thread. It will go through and set the parameters for all of the boxes
	 * based on what their character is and then get the current size of the JPael, resize the pixel art to fit and then
	 * set that pixel art as the image for the JPanel.
	 */
	protected void done() {
		int imageHeight = (int) UILookWindow.get(12).getSize().getHeight();
		int imageWidth = (int) UILookWindow.get(12).getSize().getWidth();
		try {
			char[][] windowOutput = get();
			int k = 0;
			for(int i = 0; i < UILookWindow.size(); i ++) {
				UILookWindow.get(i).setIcon(null);
				UILookWindow.get(i).setOpaque(false);
				UILookWindow.get(i).repaint();
			}
			for(int j = 0; j < 5; j++) {
				for(int i = 0; i < 5; i++, k++) {
					if(windowOutput[i][j] == '.') {
						UILookWindow.get(k).setOpaque(true);
						UILookWindow.get(k).setBackground(Color.black);
						UILookWindow.get(k).repaint();
					} else {
						BufferedImage image = null;
						try {
					    	image = ImageIO.read(new File(calculateImage(windowOutput[i][j], k)));
						} catch (IOException e) {
					    	e.printStackTrace();
						}
						ImageIcon imageIcon = new ImageIcon(resize(image, imageWidth, imageHeight));
						UILookWindow.get(k).setIcon(imageIcon);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to calculate which .png file is needed to represent the given char inputTile.
	 * @param inputTile - the character in the JPanel.
	 * @param currentJPanel - the index of the current panel.
	 * @return
	 */
	private String calculateImage(char inputTile, int currentJPanel) {
		if(inputTile == 'E') {
			return "exitImage.png";
		} else if(inputTile == 'G') {
			return "goldImage.png";
		} else if (inputTile == '#') {
			return "wallImage.png";
		} else if(inputTile == 'X')  {
			return "cornerTileImage.png";
		} else if(currentJPanel == 12) {
			return "playerImage.png";
		} else {
			return "evilPlayerImage.png";
		} 
	}
	
	/**
	 * Method used to resize the image so that it will fit the given dimension, the dimension given will be the new current
	 * size of the JPanel after it has changed shape.
	 * @param img - the image that is to be resized.
	 * @param newWidth - the new width of the image.
	 * @param newHeigh - the new height of the image.
	 * @return
	 */
	private BufferedImage resize(BufferedImage img, int newWidth, int newHeight) { 
		//Gets the current width and height.
        int currentWidth = img.getWidth();  
        int currentHeight = img.getHeight();  
        //Makes a new buffered image.
        BufferedImage bImg = new BufferedImage(newWidth, newHeight, img.getType());  
        
        //Sets up graphics, draw the image and then disposes and return the buffered image to go into the JPanel.
        Graphics2D g = bImg.createGraphics();  
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, currentWidth, currentHeight, null);  
        g.dispose();  
        return bImg;  
    }  

}