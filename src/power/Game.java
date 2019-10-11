package power;
import java.io.*;
import java.util.*;


//////////////////////////////////////// description du readMe


/**
 * Class reprenting a Game of PowerN.
 * It specifies the main behaviour of the current game in a description file.
 * It creates the 2 players weither they're human or not.
 * And it manages the game, looping between the 2 players, checking requiered moves and the result.
 * This class analyses the result and choose if the game has to continue (no player has won already) or not (a player won or the board is full).
 * All rules are found in the readme.txt file.
 */
public class Game implements IGame{

	// --- ATTRIBUTS

	private Player player1;
	private Player player2;
	private Player current;
	private Board board;
	private Mode mode;
	private int power;
	private PowerN powerN;
	private Player firstPlayer;
	private boolean term;
	
	public Game(String playerName1, String playerName2, int width, int height, int power, Mode mode, PowerN powerN, String terminal){
		// features already testchecked in PowerN class
		board = new Board(width, height, power);
		this.mode=mode;
		this.powerN=powerN;	

		// creation of the readMe.txt file
		System.out.println(this.description());

		// if terminal display is required alongside Graphic interface
		if(terminal.equalsIgnoreCase("Y")){
			this.term=true;
		}else{
			this.term=false;
		}	
		
		// creation of the two players
		if(playerName1 != null && playerName2 != null){
			this.createPlayers(playerName1, playerName2);
		}
		
		// the first player to play is randomly choosen
		this.current = this.getRandomPlayer();
		firstPlayer=this.current;

		// ********************* This will launch the Game **************************
		this.start();
	}
	
	// --- METHODS
	
	/**
	 * Read me of the current PowerN game.
	 * It creates a file readme.txt describing how to play the game.
	 * The specific features of the current game are added to the readme file depending 
	 * on the configuration file passed as parameters at the launch of the game.
	 */ 
	public String description(){
		String ret = null;
		FileWriter fileWriter=null;
		BufferedWriter bfw = null;
		PrintWriter printWriter = null;
	
		// dans le readme on explique comment doit etre présenté le fichier de config
		// dire que pour le mode HA , le j joueur 1 est le joueur humain
		// si width not pair == ajoute une colonne 
		// format du ficher de config
		// IA meilleur en mode calssque
		// suivant le mode de jeu on crée différent instance de Player
		// le joueur1 est toujours rouge et le joueur2 jaune
		// en cas de mode HA, le joueur1 est le joueur Humain (spécifié dans le fichier read me)

		try{
			fileWriter = new FileWriter("../src/data/readMe.txt");
			bfw = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(bfw);
			
			// FINIR DESCRIPTION
			String desc ="POWER N \n\n";
			desc+= "Welcome to this improved version of the Power 4 game. Power N allows you to choose how big the board is and how many coins you have to align in order to win (but be reasonable, we're watching you)\n\n.";
			desc+= "Power N allows you to play against a friend of yours or to play against an AutoPlayer who's been trained for days in order to win every game. But remember he is the best when the power is 4.\n\n";
			desc+= "Here are the rules of how to play Power N : \n";
			desc+="The first player begins by dropping one of his/her discs into the center column of the game board. The two players then alternate turns dropping one of their discs at a time into an unfilled column until one player achieves four in a row -- either horizontally, vertically or diagonally; the player that does so wins the game. However, if the game board fills up before either player achieves four in a row, then the game is a draw. \n\n";
			desc+= "When you launch the game you'll have to enter the adress of a configuration file, this file should follow some formating rules in order to be read correctly by Power N. You can modify the numbers and the mode.";
			desc+= "Available modes : HH for Human versus Human, HA for Human versus AutoPlayer, AA for AutoPlayer versus AutoPlayer";
			desc+="\n------------------ config.txt format exemple:--------------------\n\n";
			desc+="width 7\n";
			desc+="height 6\n";
			desc+="power 4\n";
			desc+="HH;\n\n";
			desc+= " Don't forger the ';' after the mode chosen !";
			desc+= "But if you just want to play regular Power 4 game against the AutoPlayer press enter.\n";
			desc+= "Then you'll have to enter the name of the players. Player 1 will be in red and player 2 in yellow. But the first player to play is randomly choosen.\n";
			desc+= " Finally you'll be asked to choose if you want the game to be display both on the terminal and in the window or just through the window of the app.\n\n";
			desc+="\n-----------------------------------------------------------------\n\n";
			desc+="\nEvery time you launch the game it will create a new ReadMe file that will delete the last one, it will contain the actual configuration of the game, here it is :\n";						
			desc+= this.powerN.printConfiguration()+"\n";
			desc+="And now, it's time to play !\n";			
			printWriter.println(desc);
			

			ret="readMe.txt file created at data/readMe.txt";
			ret+="\n--------------------------------------\n\n";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				printWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ret;
	}
		
		
	/**
	 * Creates the 2 players automatically by calling the constructor of the player
	 * Weither they're an AutoPlayer or a HumanPlayer, depending on the Game mode.
	 * It gives the 2 players the board's reference.
	 * Players1 has the red color, player2 is Yellow.
	 * In HA mode, player1 is the HumanPlayer
	 * @param playerName1, a string representing the name of the player1.
	 * @param playerName2, a string representing the name of the player2.
	 */ 
	private void createPlayers(String playerName1, String playerName2){
		// création du PLAYER1 selon les paramètres renseignés
		if(mode==Mode.HH || mode == Mode.HA){
			player1=new HumanPlayer(playerName1, this.board, CoinColor.RED);
		}
		else if(mode==Mode.AA){
			player1=new AutoPlayer(playerName1, this.board, CoinColor.RED, this);
		}
		
		// création du PLAYER2 selon les paramètres renseignés
		if(mode==Mode.HH){
			player2=new HumanPlayer(playerName2, this.board, CoinColor.YELLOW);
		}
		else if(mode==Mode.AA || mode == Mode.HA){
			player2=new AutoPlayer(playerName2, this.board, CoinColor.YELLOW, this);
		}
	 }	
	 
	
	/**
	 * Switches the current player from player1 to player2 or player2 to player1.
	 */
	private void changeCurrent(){
		if(this.current==this.player1){
			this.current=this.player2;
		}else{
			this.current=this.player1;
		}
	}
		
		
	/**
	* Launches the game and controls it until someone wins or the board is full.
	* Start calls methods to ask the player what is his next move (weither it's a HumanPlayer or an AutoPlayer), 
	* checks if the move is possible and if it's a winning move or if the board is full.
	* Then it ends the game or switch the current player if no ending reached.
	*/
	public void start(){
		boolean win=false;
		int nextMoveQuery;
		int newX;
		
		// begining of match message 
		System.out.println(player1.getName()+" ("+player1.getColor()+") VERSUS "+player2.getName() +" ("+player2.getColor()+")\n");
		System.out.println("Good luck !\n\n");
		System.out.println(this.current.getName() + "à toi de jouer !");

		// if the user asked to display the board on the terminal
		if(term){
			System.out.println(this.board.toString());
		}

		while(!win && !this.board.boardIsFull()){
			
			// will ask the current player what move he wants to play until the row chosen is in the board
			nextMoveQuery = this.current.play();
			while(nextMoveQuery <0 || nextMoveQuery >= this.board.getWidth()){
				nextMoveQuery = this.current.play();
			}			
				
			// ask the board if the move choosen by the current player is authorized
			// if it is the coin is set, if not the player is asked to choose a different move
			newX=this.board.setCoin(nextMoveQuery, this.current.getColor());
			
			while(newX<0){
				System.out.println("Cette colonne est remplie ! Choisissez une autre colonne où placer cette pièce :");
				nextMoveQuery = this.current.play();
				newX=this.board.setCoin(nextMoveQuery, this.current.getColor());
			}
			
			if(term){
				System.out.println(printLogo());
			}

			System.out.println(this.current.getName() +" a joué !\n");

			// if the user asked to display the board on the terminal 
			if(term){
				System.out.println(this.board.toString());
			}

			// checks if the last move has given the victory to the player			
			win=this.board.checkAlignment(this.current.getColor(), newX, nextMoveQuery);
			
			// if no winning move we switch the current player
			if(!win){
				this.changeCurrent();
			}
		}
		// only if someone wins or the board is full
		this.endOfGame(win);
	}
	

	/**
	* Called when the game is over. 
	* If one player won it displays a congratulation text and ask if users want to restart the game.
	* If no one wins and the board is full, the methods just ask if users want to restart the game.
	* @param win, a boolean, true if there's a winner.
	*/ 
	public void endOfGame(boolean win){
		if(win){
			System.out.println(this.current.getName()+" a gagné ! Bravo !");
		}else{
			System.out.println("Match nul !");
		}
		System.out.println("\n\n----------------------------------------------------------------------");
	}


	/**
	* Picks a player randomly between the 2 players and returns him to the calling method.
	* @return a Player
	*/
	private Player getRandomPlayer(){
		Player random=null;
		// 1 chance out of 2 to be picked up as the first player to play
		double i = Math.random();
		if(i<0.5){
			random=this.player1;
		}else{
			random=this.player2;
		}
		return random;
	}
	
	/**
	 * Extract a graphic logo of the Power N game by reading a file containing its ASCII code and returns it as a String.
	 * @returns a String
	 */  
	private static String printLogo(){
		String line;
		String logo="";
		Exception ret=null;
		try{
			Scanner in= new Scanner(new FileReader("../src/data/logo.txt"));
			logo+="\n\n\n";
			while(in.hasNextLine()){
				logo+=in.nextLine()+"\n";
			}
			logo+="\n\n\n";
			// closes the stream
			in.close();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			if(ret==null){
				ret= e;
			}
		}
		return logo;
	}

	// --- GETTERS AND SETTERS

	/** Getters and setters
	*/
	public Player getFirstPlayer(){return this.firstPlayer;}
}




		
	
	
		
		
	
	
				
