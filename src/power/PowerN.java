package power;
import java.util.*;
import java.io.*;

/**
 * The class PowerN receives a configuration file when it is created in GameLauncher.
 * It extracts the parameters from this configuration file in order to configure the size of the grid and the number of coins to align in order to win.
 * PowerN also starts a new game by creating an instance of the class Game.
 * All rules are found in the readme.txt file.
 * @author Arnaud Bistel
 * @version 1.1.0
 */
public class PowerN{

	// --- ATTRIBUTS
	
	private String fileName;
	private int width;
	private int height;
	private int power;
	private Mode mode;
	private Game game;

	
	// --- CONSTRUCTOR
	
	/**
	 * Constructor of PowerN.
	 * Checks if the parameters received about the configuration file and the names of the players are valid.
	 * Then it calls the configure() method to initialize the board and launch a new game.
	 * @param fileName, a String representing the path to the file containing the board's features.
	 * @param playerName1, a String representing the name of the player number 1
	 * @param playerName2, a String representing the name of the player number 2
	 */ 
	public PowerN(String fileName, String playerName1, String playerName2, String terminal){
		if(fileName!=null && playerName1 !=null && playerName2!=null){
			this.fileName=fileName;
			// methods which extracts data from a file and then initialise the current game
			this.configure(this.fileName);
			// displays the current game's rules
			System.out.println(this.printConfiguration());
			// Game will set the game and launch it
			this.game=new Game(playerName1, playerName2, this.width, this.height, this.power, this.mode, this, terminal);
		}
	}

	
	// --- METHODS 
	
	
	/**
	 * Reads the file received as a parameter and extracts the configuration information from it. 
	 * It is needed in order to initialize the board.
	 * If the configure method can't find all the data needed it will set the features as default features.
	 * Default features : width = 7, height = 6, power = 4, mode = HA.
	 * @param fileName, a String representing the path to the file containing the board's features.
	 */ 
	public void configure(String fileName){
		String line;
		String feature;
		Exception ret=null;
		try{
			Scanner in= new Scanner(new FileReader(fileName));
			while(in.hasNextLine()){
				line=in.nextLine();
				int i=0;
				while(!Character.isDigit(line.charAt(i)) && line.charAt(i)!=';' ){
					i++;
				}
				feature=line.substring(0,i).trim();
				
				// each line from the configuration file is read in order to found a feature name and its value
				if(feature.equalsIgnoreCase("width")){
					// extracts the value and puts it in the right attribut
					this.width=(int)Integer.parseInt(line.substring(i).trim());
				}
				if(feature.equalsIgnoreCase("height")){
					this.height=(int)Integer.parseInt(line.substring(i).trim());
				}				
				if(feature.equalsIgnoreCase("power")){
					this.power=(int)Integer.parseInt(line.substring(i).trim());	
				}
				if(feature.equalsIgnoreCase("HA")){
					this.mode=Mode.HA;
				}
				if(feature.equalsIgnoreCase("AA")){
					this.mode=Mode.AA;
				}
				if(feature.equalsIgnoreCase("HH")){
					this.mode=Mode.HH;
				}
			}
			// closes the stream
			in.close();
		}catch(FileNotFoundException e){
			System.out.println("File not found : '"+ fileName+"'\n\n");
			ret = e;
		}catch(Exception e){
			e.printStackTrace();
			if(ret==null){
				ret= e;
			}
		}
		
		// if one value is wrong or not found all features are set to the usual rules for a Power 4 
		if(this.width<=0){
			this.width=7;
			this.height=6;
			this.power=4;
			this.mode=Mode.HA;
			System.out.println("configure : width error, all set to the Power 4 default features.\n\n");
		}
		if(this.width%2==0){
			this.width++;
		}
		if(this.height<=0){
			this.width=7;
			this.height=6;
			this.power=4;
			this.mode=Mode.HA;
			System.out.println("configure : height error, all set to the Power 4 default features.\n\n");
		}
		if(this.power<=0){
			this.width=7;
			this.height=6;
			this.power=4;
			this.mode=Mode.HA;
			System.out.println("configure : power error, all set to the Power 4 default features.\n\n");		
		}
		// checks that the power needed to win is not too big for the board features. Corrects it if needed.
		while(this.power>this.width-1){
			this.power--;
		}
		if(this.mode==null){
			this.width=7;
			this.height=6;
			this.power=4;
			this.mode=Mode.HA;
			System.out.println("configure : mode error, all set to the Power 4 default features.\n\n");		
		}
	}
	

	/**
	 * Returns a String containing the features of the board extract from the configuration file.
	 * printConfiguration is called automatically after the config file has been read.
	 * @return a String
	 */ 
	public String printConfiguration(){
		String printConfig="\n-------- Game configuration --------\n\n";
		printConfig+="Size of the grid : "+this.width+" x "+this.height+"\n";
		printConfig+="Power : "+this.power+"\n";
		printConfig+="Mode : "+this.mode+"\n\n";
		printConfig+="Have fun !\n";
		printConfig+="\n--------------------------------------";
		return printConfig;
	}

	// --- GETTERS AND SETTERS
	
	/** Getters and setters for PowerN attributes.
	*/
	public int getWidth(){return this.width;}
	public int getHeight(){return this.height;}
	public int getPower(){return this.power;}
	public Mode getMode(){return this.mode;}
}
	
			
				
