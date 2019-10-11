package power;
import view.GridTableFrame;

/**
 * Class representing the board of the current Power game.
 * It initialises the squares and provides methods used to check if a coin can be put at a certain spot.
 * It also enable the display of the Game using View models of display.
 * All rules are found in the readme.txt file.
 */ 
public class Board{

	// --- ATTRIBUTS

	private int width;
	private int height;
	private Square[][]grid;
	private int power;
	private GridTableFrame otframe;
	
	// --- CONSTRUCTOR
	
	/**
	 * Constructor of the class Board.
	 * His main goals are to initialise the grid of Square at the beginning of the game following the features set in the class PowerN
	 *  and to create the Graphic Interface that will display the game.
	 * @param height, an integer representing the height (number of square on the X-axis of the Board.
	 * @param width, an integer representing the width (number of square on the Y-axix)
	 * @param power, an integer representing the power of this current play.
	 */
	public Board(int width, int height, int power){
		this.width=width;
		this.height=height;
		this.power=power;
		
		// will create a grid of Square 
		this.initialiseGrid();
		
		//create and show the graphical grid
		otframe = new GridTableFrame(this.getGrid());
		otframe.showIt();
	}
		
	// --- METHODS	
		
	/**
	* Initialises the grid for this current game. 
	* The grid is a 2 dimensions array containing height x width instances of Square with no color (CoinColor.NONE)
	*/
	protected void initialiseGrid(){
		// creation of the grid
		grid=new Square[this.height][this.width];
		
		// filling the grid with new empty Square (no color)
		for(int i = 0; i<this.height; i++){
			for(int j=0; j<this.width; j++){
				grid[i][j]= new Square(i,j);
			}
		}
	}
	
	
	/**
	 * Checks the alignment of the coins for a specific color. 
	 * It checks the alignment in the 3 possibles directions by calling checkHAlignment, checkVAlignment, checkDAlignment.
	 * If a wining alignment is found the method stops seeking and return true.
	 * @param color, the CoinColor, coins of which are going to be analysed.
	 * @param x, int representing the X coordinate of the newly added coin.
	 * @param y, int representing the Y coordinate of the newly added coin.
	 * @return a boolean, true if there's a victory
	 */
	 public boolean checkAlignment(CoinColor color, int x, int y){
		boolean win=false;
		// checks for an horizontal alignment first then vertical alignment and finally an alignment on diagonal lines
		// if an alignment of coins is >= power with stop checking for the other
		win=checkHAlignment(color, x, y);
		if(!win){
			win=checkVAlignment(color, x, y);
		}
		if(!win){
			win=checkDAlignment(color, x, y);
		}
		return win;					 
	}
	
	
	/**
	* Checks the horizontal alignment of the coins.
	* The methods proceeds by using a counter. If the account equals the power needed it's a win.
	* @param color, a CoinColor, color of the coins which are going to be analysed.
	* @param x, the x coordinate of the newly added coin.
	* @param y, the y coordinate of the newly added coin.
	* @return a boolean, true if there's a victory	
	*/
	private boolean checkHAlignment(CoinColor color, int x, int y){
		boolean aligneDroite=true;
		boolean aligneGauche=true;
		boolean ret=false;
		int i= y+1;
		int j= y-1;
		
		// counter for coins alignment
		int cptAligne=1;
		CoinColor testColor=color;
		
		// loop that adds one to the counter everytime the coin on the right is of the same color as the one received as parameter.
		// the loop stop if it reaches a coin of a different color
		while(i<this.width && aligneDroite){
			if(this.grid[x][i].getColor()==testColor){
				 cptAligne++;
				 i++;
			}else{
				aligneDroite=false;
			}
		}	
		
		// loop accounting the coins on the left side of initial coin		
		while(j>=0 && aligneGauche && cptAligne<this.power){	
			if(this.grid[x][j].getColor()==testColor){
				 cptAligne++;
				 j--;
			}else{
				aligneGauche=false;
			}
		}	
		
		// if count is higher or equals to the power it's a win
		if(cptAligne>=this.power){
			ret=true;
		}
		return ret;
	}
	
	
	/**
	 * Checks the vertical alignment of the coins.
	 * The methods proceeds by using a counter. If the account equals the power needed it's a win.
	 * @param color, a CoinColor representing the color of the coins which are going to be analysed.
	 * @param x, an integer representing the x coordinate of the newly added coin.
	 * @param y, an integer representing the y coordinate of the newly added coin.
	 * @return a boolean, true if there's a victory	 
	 */
	 private boolean checkVAlignment(CoinColor color, int x, int y){
		boolean aligneBas=true;
		boolean aligneHaut=true;
		boolean ret=false;
		int i= x+1;
		int j= x-1;
		
		// counter for coins alignment	
		int cptAligne=1;
		CoinColor testColor=color;
		
		// loop that adds one to the counter everytime the coin below is of the same color as the one received as parameter.
		// the loop stop if it reaches a coin of a different color
		while(i<this.height && aligneBas){
			if(this.grid[i][y].getColor()==testColor){
				 cptAligne++;
				 i++;
			}else{
				aligneBas=false;
			}
		}	
		
		// loop accounting the coins upper the initial coin
		// the loop stop if it reaches a coin of a different color
		while(j>=0 && aligneHaut && cptAligne<this.power){	
			if(this.grid[j][y].getColor()==testColor){
				 cptAligne++;
				 j--;
			}else{
				aligneHaut=false;
			}
		}	
		
		// if count is higher or equals to the power it's a win	
		if(cptAligne>=this.power){
			ret=true;
		}
		return ret;
	}	
	

	/**
	 * Checks the alignment in diagonal of the coins.
	 * The methods proceeds by using a counter. If the account equals the power needed it's a win.
	 * @param color, a CoinColor representing the color of the coins which are going to be analysed.
	 * @param x, an integer representing the x coordinate of the newly added coin.
	 * @param y, an integer representing the y coordinate of the newly added coin.
	 * @return a boolean, true if there's a victory	 
	 */
	 private boolean checkDAlignment(CoinColor color, int x, int y){

	 	// ****************************** check of the diagonal y=-x ******************************
		boolean aligneBasDroite=true;
		boolean aligneHautGauche=true;
		boolean ret=false;
		int i= x+1;
		int j= y+1;
		
		// counter for coins alignment on the diagonale y = -x
		int cptAligneDiagX=1;
		CoinColor testColor=color;
		
		// loop that adds one to the counter everytime the coin on the same diagonale is of the same color as the one received as parameter.
		// the loop stop if it reaches a coin of a different color	
		while(i<this.height && j<this.width && aligneBasDroite){
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 i++;
				 j++;
			}else{
				aligneBasDroite=false;
			}
		}	
		// boucle qui décompte l'alignement en haut à gauche du nouveau pion
		// the loop stop if it reaches a coin of a different color		
		i=x-1;
		j=y-1;
		while(j>=0 && i>=0 && aligneHautGauche && cptAligneDiagX<this.power){	
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 j--;
				 i--;
			}else{
				aligneHautGauche=false;
			}
		}
		
	 	// ****************************** check of the diagonal y = x ******************************

		boolean aligneHautDroite=true;
		boolean aligneBasGauche=true;
		i= x-1;
		j= y+1;
		// counter for coins alignment on the diagonale y = -x		
		int cptAligneDiagMinusX=1;
		
		// if no win on the first diagonale
		if(cptAligneDiagMinusX<this.power){
			
			// boucle qui décompte l'alignement en haut à droite du nouveau pion
			// the loop stop if it reaches a coin of a different color	
			while(i>=0 && j<this.width && aligneHautDroite){
				if(this.grid[i][j].getColor()==testColor){
					 cptAligneDiagMinusX++;
					 i--;
					 j++;
				}else{
					aligneHautDroite=false;
				}
			}	
			
			i=x+1;
			j=y-1;
			// boucle qui décompte l'alignement en bas à gauche du nouveau pion	
			// the loop stop if it reaches a coin of a different color		
			while(j>=0 && i<this.height && aligneBasGauche && cptAligneDiagMinusX<this.power){	
				if(this.grid[i][j].getColor()==testColor){
					 cptAligneDiagMinusX++;
					 j--;
					 i++;
				}else{
					aligneBasGauche=false;
				}
			}
		}
	
		// if count is higher or equals to the power it's a win		
		if(cptAligneDiagX>=this.power || cptAligneDiagMinusX>=this.power){
			ret=true;
		}
		return ret;
	}
	

	/**
	* setCoin analyses the grid to put the newly placed coin at its right place in the row it's been put.
	* The coin can only go in the square right upper the last non-free square in the row.
	* After placing the coin, setCoin refreshes the Frame.
	* @param y, an integer representing the index of the row it must be placed in in the board.
	* @param color, an instance of CoinColor, representing the color and by extension the player whose coin it is.
	* @return an integer, the coordinate on the Y-axis of the coin after it's been set in the grid. -1 if the row selected is full.
	*/
	int setCoin(int y, CoinColor color){
		int i = this.height-1;
		// loop seeking the first free Square from the bottom
		while(i>=0 && grid[i][y].getColor()!=CoinColor.NONE){
			i--;
		}
		if(i>-1){
			// the coin is set to a Square by setting its color to the Square
			grid[i][y].setColor(color);
			// refresh the JFram used to display the board
			otframe.repaint();
		}
		// returns the X coordinate of the Square into which the coin has been set
		return i;
	}

	/**
	* Returns a String which is a representaion of the board as it is currently each time a coin has been added to it.
	* @return a String
	*/
	 public String toString(){
		String ret="\n";

		int l =0;
		ret+="-";
		while(l<this.width){
			ret+="-----";
			l++;
		}
		ret+="\n";		
		for(int i =0; i< this.height;i++){
			ret+=i;
			for(int j=0; j< this.width; j++){
				ret+=grid[i][j].toString();
				if(j==this.width-1){
					ret+="\n";
				}
			}
			l=0;
			ret+=" ";		
			while(l<this.width){
				ret+="-----";
				l++;
			}
			ret+="\n";	
		}
		ret+=" ";			
		for(int k=0; k<this.width; k++){
			ret+="| "+k+" |";	
		}
		ret+="\n";		
		l=0;
		ret+="-";
		while(l<this.width){
			ret+="-----";
			l++;
		}

		ret+="\n";		
		return ret; 
	}
	

	/**
	* Returns true if the board is full meaning each square has a color (red or yellow).
	* @return a boolean
	*/
	public boolean boardIsFull(){
		boolean full = true;
		int i= 0;
		int j=0;
		
		// if a Square on the first line (board[0]) is empty the board is not full
		while(full && j<this.width){
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				full=false;
			}
			j++;
		}
		return full;		
	}

	// --- GETTERS AND SETTERS

	/**Getters and setters.
	*/
	public int getWidth(){return this.width;}
	public int getHeight(){return this.height;}
	public Square[][] getGrid(){return this.grid;}
	public int getPower(){return this.power;}
}
