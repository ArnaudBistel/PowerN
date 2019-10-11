package power;

// finir toString
/*public static String getRouge() {
    return "\033[31m";
}
 
public static String getVert() {
    return "\033[32m";
}
*/


/**
 * Class representing a Square from the grid of the PowerN game. It creates it by setting its coordinates and color.
 */ 
public class Square{
	private int x;
	private int y;
	private CoinColor color;
	
	/**
	 * Constructor of the class Square.
	 * Initialises its coordinates in the board and its initial color (CoinColor.NONE) because the Square is empty at the begining.
	 * @param x, an integer representing the X coordinate of the current Square.
	 * @param y, an integer representing the Y coordinate of the current Square.
	 */
	 public Square(int x, int y){
		if(x>=0 && y>=0){ 
			this.x=x;
			this.y=y;
		}
		this.color=CoinColor.NONE;
	}
	
	/**
	 * Returns false if the current Square is not free, meaning that a coin has been put in it and that the Square has now a color.
	 * Returns true if no coin is linked to this Square.
	 * @return a boolean
	 */
	 public boolean isFree(){
		 return color==CoinColor.NONE;
	}
	
	/**
	 * Returns a representation of the current instance of Square, 
	 * it represents the color of coin the Square contains by displaying a cercle of the right color.
	 * @return a String
	 */	
	 public String toString(){
	 	String ret ="";
	 	// si une couleur est affectée à la case on affiche le nom de cette couleur 
	 	// sinon on met du vide pour signifier que la case est vide
	 	if(this.color==CoinColor.YELLOW){
	 		// permet d'avoir un affichage de cercles rouge, si erreur, désactiver cette ligne et activer la ligne suivante qui affiche YELLOW	 		
	 		// permet d'avoir un affichage de cercles jaune
	 		ret+="| \u001B[33m?\u001B[0m |";
	 		//ret+="| "+this.color+" |";
	 	}	
	 	if(this.color==CoinColor.RED){
	 		// permet d'avoir un affichage de cercles rouge, si erreur, désactiver cette ligne et activer la ligne suivante qui affiche RED
	  		ret+="| \u001B[31m?\u001B[0m |";
	 		//ret+="| "+this.color+" |";
	 	}	
	 	if(this.color==CoinColor.NONE){
	 		ret+="|   |";
	 	}
		return ret; 


	/** Getters and setters.
	*/
	public CoinColor getColor(){return this.color;}
	public void setColor(CoinColor color){this.color=color;}
	public int getX(){return this.x;}
	public int getY(){return this.y;}
}
