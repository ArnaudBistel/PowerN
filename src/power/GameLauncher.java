import power.*;
import java.util.*;
import java.io.*;

/**
 * Ask the user to type in the path to the configuration file containing the features of the Game.
 * Users also have to type their names.
 * Then the GameLauncher creates an instance of PowerN which will configure the game and run it.
 * @param args, an array of String
 */ 
public class GameLauncher{

	public static void main (String[]args){
		
		// prints the logo on the terminal
		System.out.println(printLogo());
		System.out.println("Bienvenu dans Power N ! \n");
		Scanner in = new Scanner(System.in);
		
		System.out.println("Veuillez-saisir un nom de fichier de configuration ");		
		// if no configuration file is found the usual rules for a Puissance 4 game are used
		System.out.println("ou appuyer simplement sur ENTER pour un puissance 4 classique en solo :\n");		
		String fileName= in.nextLine();
		System.out.println();

		System.out.println("Saisir le nom du player1 (rouge) :\n");		
		String name1=in.nextLine();
		System.out.println();

		System.out.println("Saisir le nom du player2 (jaune) :\n");			
		String name2=in.nextLine();
		System.out.println("\n");
				
		System.out.println("Affichage sur le terminal en plus de l'interface graphique ? Y/N :\n");			
		String terminal=in.nextLine();		
		
		// first class called from PowerN game
		new PowerN(fileName, name1, name2, terminal);
		in.close();
		
	}

	/**
	 * Creates a graphic logo of the Power N game and returns it.
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
}
