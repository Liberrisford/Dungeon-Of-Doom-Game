
import java.util.Scanner;

/**
 * Class that contains methods for reading input from the user. 
 * @author liamberrisford
 *
 */
public class Player {
	private Scanner userInput;
	
	/*
	 * Constructor. Sets up the scanner.
	 */
	public Player() {
		userInput = new Scanner(System.in);
	}
	
	public String readUserInput(){
		return userInput.nextLine();
	}
	
	public void update(){
		String answer = "";
		while (true){
			answer = readUserInput();
			printAnswer(answer);
		}
	}
	
	protected void printAnswer(String answer) {
		System.out.println(answer);
	}	
}
