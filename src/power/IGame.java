package power;

/**
 * Interface specifying the main behaviour of a game.
 * It contains 3 methods : description, start and endOfGame
 * All rules are found in the readme.txt file.
 */
public interface IGame{

	// --- METHODS WITH NO CODE

	/**
	 * Should be used to create a readme file or text describing the rules and features of a game.
	 * @return a String
	 */ 
	public String description();
	
	/**
	* Method used to launch a game.
	*/  
	public void start();
	
	/**
	 * Method to apply to/at the end of the game.
	 * @param win, a boolean representing the win or lost
	 */
	public void endOfGame(boolean win);
}
