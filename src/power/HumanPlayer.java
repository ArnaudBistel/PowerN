package power;
import java.util.*;
import java.io.*;

/**
* Class representing a Human player. Used to asked to the player in which row he wants to place his next coin.
* All rules are found in the readme.txt file.
*/
public class HumanPlayer extends Player{

	// --- CONSTRUCTOR

	/**
	* Constructor of the HumanPlayer
	* Calls the constructor of the abstract class Player.
	* @param name, a String representing the name of the current instance of Player.
	* @param board, an instance of the current Board of the game
	* @param color, an instance of CoinColor representing the player.
	*/
	public HumanPlayer(String name, Board board, CoinColor color){
		// calls the constructor of the abstract class Player
		super(name, board, color);
	}

	// --- METHODS

	/**
	* Asks the user to enter an integer representing the row into which he wants to place his coin.
	* @return an int, index of the row.
	*/
	public int play(){

		// if a problem occured during the input the method will return -1
		int y=-1;

		System.out.println(this.name+ " ("+this.color+") : dans quelle colonne souhaitez-vous placer votre pion ?\n\n");
		
		Scanner input = new Scanner(System.in);
		try{			
			y = input.nextInt();
			
			while(y>=this.board.getWidth() || y<0){
				System.out.println("Numéro de colonne "+y+" trop grand ou trop petit. "+this.name+", saisissez un numero de colonne valide : ");
				y = input.nextInt();
			}
		
		// we want to avoid wrong input like a letter entered instead of an integer	
		}catch (Exception e){
			y = -1;
		}
		
		System.out.println();
		return y;
	}
}
