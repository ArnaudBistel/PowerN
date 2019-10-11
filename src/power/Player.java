package power;
import java.util.*;

/**
* Abstract class representing a Player.
* All rules are found in the readme.txt file.
*/
public abstract class Player{

	// --- ATTRIBUTS

	protected String name;
	protected Board board;
	protected CoinColor color;

	// --- CONSTRUCTOR

	/**
	* Constructor of the class Player
	* @param name, a String representing the name of the current instance of Player.
	* @param board, an instance of the current Board of the game
	* @param color, an instance of CoinColor representing the player.
	*/
	public Player(String name, Board board, CoinColor color){
		this.name=name;
		this.board=board;
		this.color=color;
	}

	// --- METHODS

	/**
	* Abstract method used to represent the actual moves from a player.
	* @return an integer
	*/
	abstract int play();

	// --- GETTERS AND SETTERS

	/** Getters and setters
	*/ 
	public String getName(){return this.name;}
	public Board getBoard(){return this.board;}
	public CoinColor getColor(){return this.color;}
}
