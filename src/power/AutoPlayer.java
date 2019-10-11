package power;
import java.util.*;

/**
* Class representing a Human player. Used to asked to the player in which row he wants to place his next coin.
*/
public class AutoPlayer extends Player{

	//--- ATTRIBUTS

	// true while AutoPlayer has not made its first move of the game
	private boolean first=true;
	private Game game;
	private Square[][]grid;
	private int width;
	private int height;
	private CoinColor colorEnemy;
	private int power;
	// boolean that contains true if a move can give the victory to the AutoPlayer
	private boolean v3bool = false;
	private boolean future3 = false;
	private boolean future2 = false;
	
	// containing the IA's current coins
	private ArrayList<Square>possibleVictory;

	// arraylist containing the free squares in which the IA should play to align 2 consecutives coins
	private ArrayList<Square>solutionsV1;
	// arraylist containing the free squares in which the IA should play to align 3 consecutives coins
	private ArrayList<Square>solutionsV2;
	// arraylist containing the free squares in which the IA should play to align 4 consecutives coins	
	private ArrayList<Square>solutionsV3;

	// list of coins that could win, filled by the solutionsV lists before the last test
	private ArrayList<Square>solutionsV;
		
	// arraylist containing the index of the row in which the IA should play AFTER the latest analysis	
	private ArrayList<Integer>v1;
	private ArrayList<Integer>v2;	
	private ArrayList<Integer>v3;	
	
	// ArrayList containing moves that would make an alignment possible later 
	private ArrayList<Integer>futureV3;	
	private ArrayList<Integer>futureV2;	
	
	// arraylist containing the squares in which the IA should not play
	private ArrayList<Integer>forbidden;
	private ArrayList<Square>interdit;
	// arraylist that will contain every enemy's coins
	private ArrayList<Square>possibleDanger; 
	// arraylist containing the solutions to counter the enemy's possible victory
	private ArrayList<Square>solutions;

	
	//--- CONSTRUCTOR

	/**
	* Constructor of the AutoPlayer.
	* Calls the constructor of the abstract class Player.
	* 
	*/
	public AutoPlayer(String name, Board board, CoinColor color, Game game){

		// initialises current game configuration attributs
		super(name, board, color);
		this.game=game;
		this.grid=this.board.getGrid();
		this.width=this.board.getWidth();
		this.height=this.board.getHeight();
		this.power=this.board.getPower();
		if(this.color==CoinColor.RED){
			this.colorEnemy=CoinColor.YELLOW;
		}else{
			this.colorEnemy=CoinColor.RED;		
		}
		
		// creation of all computing attributs
		this.interdit=new ArrayList<Square>();
		this.v1=new ArrayList<Integer>();
		this.v2=new ArrayList<Integer>();
		this.v3=new ArrayList<Integer>();
		this.futureV3=new ArrayList<Integer>();
		this.futureV2=new ArrayList<Integer>();
		solutions=new ArrayList<Square>();
		this.possibleDanger=new ArrayList<Square>();
		this.possibleVictory=new ArrayList<Square>();
		this.solutionsV=new ArrayList<Square>();
		this.solutionsV1=new ArrayList<Square>();
		this.solutionsV2=new ArrayList<Square>();
		this.solutionsV3=new ArrayList<Square>();
	}

	//--- METHODS

	/**
	 * Method called by the class Game when it is this current AutoPlayer's turn.
	 * Has 2 different moves possible. If it's the first time this AUtoPlayer plays it will call firstMove, 
	 * otherwise it will look for possible danger from the enemy and possible victory move to play by calling a set of methods.
	 * @return an int, representing a row
	 */
	public int play(){
		int ret=-1;
		boolean winningMove = false;
		// first move of the GAme freom this current AutoPlayer
		if(first){
			ret=firstMove();
			//will show that the first has been played
			first=false;
		}
		

		// --- FOR THE REST OF THE GAME


		// look for imminent danger, meaning an alignment of enemy's coins of a power-1 length
		if(ret==-1){
			ret=this.imminentDanger();
		}
		
		// compute the possible moves that could help the Autoplayer to win
		this.imminentVictory();	
		
		// if there's an imminent victorious move it will have priority
		if(getV3Bool()){
			winningMove=true;
			ret=this.v3.get(0);
			this.v3.remove(0);
		}

		// if a move can create an opprotunity to align 4 coins at next turn it will be play
		if(future3&& ret==-1){
			ret=this.futureV3.get(0);
		}

		// if no move found so far a move that could align 3 coins will be choosen if there's one available
		if(ret==-1){
			if(!this.v2.isEmpty()){
				ret=this.v2.get(0);
				this.v2.remove(0);
			}
			else if(future2&& ret==-1){
				ret=this.futureV2.get(0);
		}
			// if no move found so far a move that could align 2 coins will be choosen if there's one available
			else if(!this.v1.isEmpty()){
				ret=this.v1.get(0);
				this.v1.remove(0);
			}
		}	

		// checks if the move chosen will not help the enemy to win during his next turn by comparing it to the forbidden moves
		// except if it's a winining move
		for(int m=0; m<this.interdit.size();m++){
			if((ret==this.interdit.get(m).getY()) && !winningMove){
				ret=-1;
			}
		}
		

		// checks if the move chosen will not help the enemy to win during his next turn, 
		// if true, it will try to find a new good move, else it will set the move to -1
		for(int m=0; m<this.interdit.size();m++){
			if((ret==this.interdit.get(m).getY()) && !winningMove){
				if(!this.v2.isEmpty()){
					ret=this.v2.get(0);
					this.v2.remove(0);
					m=0;
			}	
			// if no move found so far a move that could align 2 coins will be choosen if there's one available
				else if(!this.v1.isEmpty()){
					ret=this.v1.get(0);
					this.v1.remove(0);
					m=0;
				}else{
					ret=-1;
				}
			}
		}

		// if no move found so far, the AutoPlayer looks to play upside an enemy's coin 
		if(ret==-1){
			ret=this.searchTopEnemy();
		}

		// checks if the move chosen will not help the enemy to win during his next turn
		for(int m=0; m<this.interdit.size();m++){
			if((ret==this.interdit.get(m).getY()) && !winningMove){
				ret=-1;
			}
		}

		// if no good move, a random move will be chosen
		if(ret==-1){
			ret=(int)(Math.random()*(this.width));
			int cptTentativeNewRet=0;
			while(cptTentativeNewRet<5 ){
				for(int m=0; m<this.interdit.size();m++){
					if((ret==this.interdit.get(m).getY()) && !winningMove){
						ret=(int)(Math.random()*(this.width));
					}
				}
				cptTentativeNewRet++;
			}
		}

		// checks that the choosen move is not in a full row
		if(this.firstFreeSquareInRow(ret)<0){
			ret=(int)(Math.random()*(this.width));
			while(this.firstFreeSquareInRow(ret)<0){
				ret=(int)(Math.random()*(this.width));
			}
		}
	
		return ret;
	}


	/**
	* Method used for the first move of the IA. 
	* Depending on if it plays first or second and on the move of the other player the IA chooses what will be its first move.
	* @return an integer, coordinate of the move the IA chooses to do.
	*/
	private int firstMove(){
		int ret=-1;
		
		// si cette IA est le premier joueur à jouer son place le premier jeton au centre
		if(this==this.game.getFirstPlayer()){
			ret = (int)(this.width/2);
		}else{
			// si cette IA joue en 2ème son jeton est au centre si le centre est vide
			if(this.grid[this.height-1][(int)(this.width/2)].isFree()){
				ret=(int)(this.width/2);
			}else{
				// si le centre est occupé l'IA joue aléatoirement dans la colonne à droite ou à gauche du centre
				double i = Math.random();
				if(i<0.5){
					ret=((int)(this.width/2))+1;
				}else{
					ret=((int)(this.width/2))-1;
				}
			}
		}
		return ret;
	}


	 /**
	 * Analyses the whole board in order to store the enemy's coins in the arraylist possibleDanger, 
	 * and the current AutoPlayer's coins in the arraylist possibleVictory.
	 * If it finds out that a complete line is empty it stops because the all coins have been checked.
	 */
	 public void searchPossibleDanger(){
		// empty both the arraylist containing all of the enemt's coins and the current IA's coins
		this.possibleDanger.clear();
		this.possibleVictory.clear();
		// counter of empty Square in the same line
		int cptVide=0;
		
		// the analysis start from the bottom of the board
		int i=this.height-1;	
		while(i>=0 && cptVide!=this.width){
			cptVide=0;
			int j=this.width-1;			
			
			// adds the coins to one arraylist or the other depending on its color
			while(j>=0 && cptVide!=this.width){
				if(this.grid[i][j].getColor()==colorEnemy){
					this.possibleDanger.add(this.grid[i][j]);
				}else if(this.grid[i][j].getColor()==this.color){
					this.possibleVictory.add(this.grid[i][j]);
				}
				// if cptVide equals the width of the board, every non-free sqaures has been checked
				else{
					cptVide++;
				}
				j--;
			}
			i--;
		}
	}


	/**
	* Looks for the first free square in the row starting from the bottom of the board.
	* @param y, the row to analyse
	* @return an int, the X coordinate of the first free Square in the row.
	*/
	private int firstFreeSquareInRow(int y){
		// start from the top of the board
		int i=this.height-1;
		if(i>=0){
			while(i>=0 && (this.grid[i][y].getColor()!=CoinColor.NONE)){
				i--;
			}
		}
		return i;
	}





	/**
	 * Methods that seeks a free Square right upside an enemy's coin.
	 * It is called if no interesting moves has been found (imminent danger or alignment moves)
	 * @return an int, the row in which to play.
	*/
	private int searchTopEnemy(){
		
		int ret=-1;
		boolean trouveSolution = false;
		boolean trouveSommet = false;
		
		// starts looking near the middle row because the center is important in this game
		// starts on the left side of the centrer and checks until it reaches the right side of the board
		int j=(int)this.width/3;
		
		// loop searching the first enemy's coin with a free SQaure on its top, stops when found one
		while(j<this.width && !trouveSolution){
			int i = 0;
			trouveSommet=false;
			while(i<this.height && !trouveSommet){
				if(this.grid[i][j].getColor()!=CoinColor.NONE){
					trouveSommet=true;
					// si la couleur du pion le plus haut est ennemi la recherche s'arrete
					if(this.grid[i][j].getColor()==colorEnemy){
						ret=j;
						trouveSolution = true;
					}
				}
				i++;
			}
			j++;
		}

		// if no solution found, checks the rows which have not been checks on the left of the board
		j=(int)this.width/3;
		while(j<this.width && !trouveSolution){
			int i = 0;
			trouveSommet=false;			
			while(i<this.height && !trouveSommet){
				if(this.grid[i][j].getColor()!=CoinColor.NONE){
					trouveSommet=true;
					if(this.grid[i][j].getColor()==colorEnemy){
						ret=j;
						trouveSolution = true;
					}
				}
				i++;
			}
			j--;
		}
		return ret;
	}


	// --------------------------------- METHODS TRYING TO FIND AN IMMINENT DANGER -----------------------------------

	/**
	* Looks for an alignment of a length equals to power-1.
	* Firstly it calls the searchPossibleDanger and then it analyses the results in order to find dangerous alingments of coins.
	* An imminent danger is return if found as an integer representing the row in the board.
	* @return an int
	*/
	private int imminentDanger(){
		boolean foundSolution = false;
		int ret=-1;
		// recherche des cases ennemies et de l'Autoplayer qui sont stockés dans les ArrayList possibleDanger et dans possibleVictory
		this.searchPossibleDanger();
		
		// puis pour chaque case ennemies on regarde si elle est insérée dans une ligne de longueur POWER-1 jetons ennemis consécutifs
		int i=0;
		while(ret==-1 && i<this.possibleDanger.size()){
			// on vide l'arraylsit de solutions trouvées au tour précédent
			this.solutions.clear();

			// méthode qui recherche les alignements de pions adverses et les stocks dans l'arraylist solutions
			this.computeDanger(possibleDanger.get(i));
								
			// recherche si il y a des cases vides pour contrer des alignements adverse de longueur POWER-1
			if(!this.solutions.isEmpty()){
				int j=0;
				for(int n=0; n<this.solutions.size(); n++){
					// si en jouant dans la colonne de cette solution on arrive dans la case juste en dessous de celle visée
					// cette colonne devient interdite car en jouant dedans on entrainerait une victoire de l'adversaire au tour suivant
					if((this.solutions.get(n).getX()+1)==this.firstFreeSquareInRow(this.solutions.get(n).getY())){
						//this.forbidden.add(this.solutions.get(n).getY());
						this.interdit.add(this.solutions.get(n));
					}
				}

				while(j<this.solutions.size() && !foundSolution){
					// si en jouant dans la colonne de cette solution on arrive dans la case visée car toutes les autres en dessous sont occupées
					// la solution est choisie et on arrête la recherche d'un danger imminent car on en a trouvé un
					if(this.solutions.get(j).getX()==this.firstFreeSquareInRow(this.solutions.get(j).getY())){
						foundSolution=true;
						ret=this.solutions.get(j).getY();				
					}
					j++;
				}
			}
			i++;
		}
		return ret;
	}


	/**
	* Checks the horizontal alignment of the coins of the enemy to look for an imminent danger.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the enemy.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it stops the counting and the free square is stored as a possible danger.
	* If the next Square after the gap is of the enemy the counting continues.
	* If the alignment equals the length of power-1 , the solutions are stored in the arraylist solutions.
	* @param x, the x coordinate of the analysed coin.
	* @param y, the y coordinate of the analysed coin.
	*/
	private void checkHDanger(int x, int y){
		boolean aligneDroite=true;
		boolean aligneGauche=true;
		int i= y+1;
		int j= y-1;
		// compteur de pions de la même couleur alignés horizontalement
		int cptAligne=1;
		CoinColor testColor=this.colorEnemy;

		
		// boucle qui décompte l'alignement à droite du pion
		// on ajoute un à chaque fois que le pion à droite est de la même couleur que le pion initial
		// le décompte s'arrête quand on atteint une couleur différente.
		while(i<this.width && aligneDroite){

			// si on atteint une case vide
			if(this.grid[x][i].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutions.contains(this.grid[x][i])){solutions.add(this.grid[x][i]);}
				int k=i+1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion ennemi on poursuit le décompte
				while(k<this.width && suite){
					if(this.grid[x][k].getColor()==testColor){
						cptAligne++;
					}else{
						suite=false;
					}
					k++;
				}
				aligneDroite=false;
			}

			// si de notre couleur l'analyse s'arrête
			if(this.grid[x][i].getColor()==this.color){
				aligneDroite=false;
			}
			// si de couleur ennemie on ajoute 1 au décompte
			if(this.grid[x][i].getColor()==testColor){
				cptAligne++;
				i++;
			}			
		}	

		// boucle qui décompte l'alignement à gauche du pion		
		while(j>=0 && aligneGauche){	
			// si on atteint une case vide
			if(this.grid[x][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutions.contains(this.grid[x][j])){solutions.add(this.grid[x][j]);}
				int k=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion ennemi on poursuit le décompte
				while(k>=0 && suite){
					if(this.grid[x][k].getColor()==testColor){
						cptAligne++;
					}else{
						suite=false;
					}
					k--;
				}				
				aligneGauche=false;
			// si de notre couleur l'analyse s'arrête
			}if(this.grid[x][j].getColor()==this.color){
				aligneGauche=false;
			// on ajoute un à chaque fois que le pion à gauchee est de la couleur ennemie
			}if(this.grid[x][j].getColor()==testColor){
				 cptAligne++;
				 j--;
			}
		}	
		// si le décompte n'est pas égale à POWER-1 on vide l'arraylist de solutions sauvegardées car elles ne sont pas des dangers imminents
		if(cptAligne<power-1){
			solutions.clear();
		}
	}

	/**
	* Methods whose only role is to call the checkAlignment methods for a specific Square received as a paramater.
	* @param dangerous, a Square that will be analysed to see if it's the start of an alignment.
	*/
	private void computeDanger(Square dangerous){
		checkHDanger(dangerous.getX(), dangerous.getY());

		if(this.solutions.isEmpty()){checkVDanger(dangerous.getX(), dangerous.getY());}

		if(this.solutions.isEmpty()){checkDADanger(dangerous.getX(), dangerous.getY());}
	}


	/**
	* Checks the vertical alignment of the coins of the enemy to look for an imminent danger.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the enemy.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it stops the counting and the free square is stored as a possible danger.
	* If the next Square after the gap is of the enemy the counting continues.
	* If the alignment equals the length of power-1 , the solutions are stored in the arraylist solutions.
	* @param x, the x coordinate of the analysed coin.
	* @param y, the y coordinate of the analysed coin.
	*/
	 private void checkVDanger(int x, int y){
		ArrayList<Square>solutionVert=new ArrayList<Square>();
		boolean aligneBas=true;
		boolean aligneHaut=true;
		int i= x+1;
		int j= x-1;
		// compteur de pion de la même couleur alignés verticalement	
		int cptAligne=1;
		CoinColor testColor=this.colorEnemy;
		
		// boucle qui décompte l'alignement vers le bas du nouveau pion
		// on ajoute un à chaque fois que le pion en dessous du précédent est de la même couleur que le pion initial
		// le décompte s'arrête quand on atteint une couleur différente.
		while(i<this.height && aligneBas){
			// si on atteint une case vide
			if(this.grid[i][y].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution et on arrête la recherche dans ce sens
				if(!solutionVert.contains(this.grid[i][y])){solutionVert.add(this.grid[i][y]);}
				aligneBas=false;
			// si de la couleur de l'AutoPlayer on arrête la recherche dans ce sens
			}if(this.grid[i][y].getColor()==this.color){
				aligneBas=false;
			}	
			// si appartient à l'ennemi on ajoute un au décompte		
			if(this.grid[i][y].getColor()==testColor){
				 cptAligne++;
				 i++;
			}	
		}
		// boucle qui décompte l'alignement vers le haut du pion
		// on ajoute un à chaque fois que le pion en dessous du précédent est de la même couleur que le pion initial
		// le décompte s'arrête quand on atteint une couleur différente.
		while(j>=0 && aligneHaut){	
			// si on atteint une case vide
			if(this.grid[j][y].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution et on arrête la recherche dans ce sens
				solutionVert.add(this.grid[j][y]);
				aligneHaut=false;
			}
			// si de la couleur de l'AutoPlayer on arrête la recherche dans ce sens
			if(this.grid[j][y].getColor()==this.color){
				aligneHaut=false;
			}
			// si appartient à l'ennemi on ajoute un au décompte
			if(this.grid[j][y].getColor()==testColor){
				 cptAligne++;
				 j--;
			}			
		}	

		// si le décompte est égale à power-1 on ajoute les solutions à celles trouvées précédemment
		if(cptAligne==power-1){
			for(Square sq : solutionVert){
				this.solutions.add(sq);

			}
		}
		// vide la liste de solutions provisoires
		solutionVert.clear();
	}	


	/**
	* Checks the alignment in all possible diagonals starting from the coins of the enemy which coordinates have been received as parameters.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the current player.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it continues the counting and the square is stored as a possible Victory move.
	* If the next Square after the gap is of the enemy the counting stops and the gap is stored as possible Victory.
	* If the alignment equals the length of power-1 , the solutions are stored in the arraylist solutions.
	* @param x, the x coordinate of a Square.
	* @param y, the y coordinate of a Square.
	*/
	 private void checkDADanger(int x, int y){

	 	// ****************************** partie test de la diagonale y=-x ******************************
		
		// lieu de stockage provisoire des solutons possibles
		ArrayList<Square>solutionDiag=new ArrayList<Square>();
		boolean aligneBasDroite=true;
		boolean aligneHautGauche=true;
		int i= x+1;
		int j= y+1;
		// compteur de pion de la même couleur alignés sur la diagonale y = -x (la diagonale qui part de l'angle en haut à gauche)
		int cptAligneDiagX=1;
		CoinColor testColor=this.colorEnemy;
		
		// boucle qui décompte l'alignement vers le bas à droite en aprtant du pion initial
		// on ajoute un à chaque fois que le pion dans la diagonale du précédent est de couleur ennemie
		// le décompte s'arrête quand on atteint une couleur différente.		
		while(i<this.height && j<this.width && aligneBasDroite){
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution et on arrête la recherche dans ce sens
				if(!solutionDiag.contains(this.grid[i][j])){solutionDiag.add(this.grid[i][j]);}
				int k=i+1;
				int l=j+1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion ennemi on poursuit le décompte
				while(k<this.height && l<this.width && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagX++;
					}else{
						suite=false;
					}
					k++;
					l++;
				}
				aligneBasDroite=false;
			// si de la couleur del'AutoPlayer on arrête la recherche dans ce sens
			}if(this.grid[i][j].getColor()==this.color){
				aligneBasDroite=false;
			}
			// si appartient à l'ennemi on ajoute un au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 i++;
				 j++;
			}
		}	
		// boucle qui décompte l'alignement en haut à gauche du nouveau pion		
		i=x-1;
		j=y-1;
		while(j>=0 && i>=0 && aligneHautGauche){	
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution et on arrête la recherche dans ce sens
				if(!solutionDiag.contains(this.grid[i][j])){solutionDiag.add(this.grid[i][j]);}

				int k=i-1;
				int l=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion ennemi on poursuit le décompte
				while(l>=0 && k>=0 && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagX++;
					}else{
						suite=false;
					}
					k--;
					l--;
				}
				aligneHautGauche=false;
			}
			// si de la couleur de l'AutoPlayer on arrête la recherche dans ce sens
			if(this.grid[i][j].getColor()==this.color){
				aligneHautGauche=false;
			}
			// si appartient à l'ennemi on ajoute un au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 j--;
				 i--;
			}
		}

		// si le décompte est égale à power-1 on ajoute les solutions à celles trouvées précédemment
		if(cptAligneDiagX==power-1){
			for(Square sq : solutionDiag){
				this.solutions.add(sq);
			}
		}
		// vide la liste de solutions provisoires
		solutionDiag.clear();
		
	 	// ****************************** partie test de la diagonale y=x (diagonale qui part de l'angle en abs à gauche) ******************************

		boolean aligneHautDroite=true;
		boolean aligneBasGauche=true;
		i= x-1;
		j= y+1;
		// compteur de pion de la même couleur alignés sur la diagonale y = x		
		int cptAligneDiagMinusX=1;
		
		// boucle qui décompte l'alignement vers le bas à droite en aprtant du pion initial
		// on ajoute un à chaque fois que le pion dans la diagonale du précédent est de couleur ennemie
		// le décompte s'arrête quand on atteint une couleur différente.
		while(i>=0 && j<this.width && aligneHautDroite){
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution et on arrête la recherche dans ce sens
				if(!solutionDiag.contains(this.grid[i][j])){solutionDiag.add(this.grid[i][j]);}
				int k=i-1;
				int l=j+1;
				boolean suite=true;
				while(k>=0 && l<this.width && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagMinusX++;
					}else{
						suite=false;
					}
					k--;
					l++;
				}

				aligneHautDroite=false;
			// si de la couleur de l'AutoPlayer on arrête la recherche dans ce sens			
			}if(this.grid[i][j].getColor()==this.color){
				aligneHautDroite=false;
			}
			// si appartient à l'ennemi on ajoute un au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagMinusX++;
				 i--;
				 j++;
			}
		}	
		
		i=x+1;
		j=y-1;
		// boucle qui décompte l'alignement en bas à gauche du nouveau pion			
		while(j>=0 && i<this.height && aligneBasGauche){	
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				if(!solutionDiag.contains(this.grid[i][j])){solutionDiag.add(this.grid[i][j]);}
				int k=i+1;
				int l=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion ennemi on poursuit le décompte
				while(l>=0 && k<this.height && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagMinusX++;
					}else{
						suite=false;
					}
					k++;
					l--;
				}
				aligneBasGauche=false;
			// si de la couleur del'AutoPlayer on arrête la recherche dans ce sens
			}if(this.grid[i][j].getColor()==this.color){
				aligneBasGauche=false;
			}
			// si appartient à l'ennemi on ajoute un au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagMinusX++;
				 j--;
				 i++;
			}
		}
		
		// si le décompte est égale à power-1 on ajoute les solutions à celles trouvées précédemment		
		if(cptAligneDiagMinusX==power-1){
			for(Square sq : solutionDiag){
				this.solutions.add(sq);
			}
		}
		solutionDiag.clear();
	}


	/**
	 * Searches if the row in which the current AutoPlayer is supposed to play is forbidden. 
	 * A forbidden row is a row which first free square is below a Square that
	 *  could complete an enemy's alignment of the power required.
	 * @param y, index of the row in which the AutoPLayer is supposed to play.
	 * @return an int, -1 if the row is forbidden
	 */
	 private int searchForbiddenRow(int y){
		int ret = y;
		this.solutions.clear();
		int possibleForbidden = this.firstFreeSquareInRow(y);
		this.computeDanger(this.grid[possibleForbidden][y]);
		if(!this.solutions.isEmpty()){
			ret=-1;
		}
		return ret;
	}




	// +++++++++++++++++++++++++++++++++ METHODS TRYING TO FIND A VICTORIOUS MOVE ++++++++++++++++++++++++++++++++++++


	/**
	 * Methods that centralises all the method looking for the best possible moves that could help the IA to align its own coins.
	 * Firstly it seeks every coins played by the AutoPlayer.
	 * Then it will compute every moves that could align the current Autoplayer coins in solutionsV1, solutionsV2, solutionsV3 depending on how good the move is.
	 * After that it gathers all interesting free Squares in an ArrayList solutionsV.
	 * Those Squares are analysed one by one. 
	 * if the analysis confirm they're good and possible moves their Y coordinate is stored in an ArrayList v1, v2 or v3 depending on how good the move is.
	 * If the move will not align the coins right then but will make an alignment possible later, the move is stored in a the ArrayList this.future
	 */ 
	private void imminentVictory(){
		
		// vide toutes les arraylist qui servent de stockages de solutions possibles
		// réniitialise les booleens et les compteurs
		this.futureV3.clear();
		this.futureV2.clear();
		this.solutionsV.clear();
		this.solutionsV1.clear();
		this.solutionsV2.clear();
		this.solutionsV3.clear();
		this.interdit.clear();
		this.v1.clear();
		this.v2.clear();
		this.v3.clear();
		this.interdit.clear();
		boolean foundSolution = false;
		boolean future2 = false;
		boolean future3 = false;
		int ret=-1;
		this.v3bool=false;
		boolean v2bool=false;
		boolean v1bool=false;
		
		// compteurs du nombres de solutions possibles suivant le niveau d'importance de la solution
		int cptV3=0;
		int cptV2=0;
		int cptV1=0;

		int i=0;
		// pour chaque pion appartenant à l'AutoPlayer on recherche les alignements de ses pions
		while(i<this.possibleVictory.size()){
			// méthode qui recherche les alignements de pions adverses de longueurs = POWER -1 
			this.computeVictory(possibleVictory.get(i));
			i++;
		}	

		// on stocke les solutions de placement intéressants dans une arraylist commune solutionV
		// les premières solution stockées sont les prioritaires (v3>v2>v1)
		if(!this.solutionsV3.isEmpty()){
			for(Square sq : this.solutionsV3){
				if(!solutionsV.contains(sq)){
					solutionsV.add(sq);
					cptV3++;
				}	
			}
		} 
		if(!this.solutionsV2.isEmpty()){
			for(Square sq : this.solutionsV2){
				if(!solutionsV.contains(sq)){
					solutionsV.add(sq);
					cptV2++;
				}					
			}
		}
		if(!this.solutionsV1.isEmpty()){
			for(Square sq : this.solutionsV1){
				if(!solutionsV.contains(sq)){
					this.solutionsV.add(sq);
					cptV1++;
				}
			}
		}

		
		for(Square sqDanger : this.solutions){
			for(Square sq : this.solutionsV){
				if(sqDanger.getY() == sq.getY()){
					if((sqDanger.getX()+1==this.firstFreeSquareInRow(sq.getY()))){
						if(!interdit.contains(sq)){
							this.interdit.add(sq);
						}
					}
				}
			}	
		}

		
		// si il y a des cases vides pour contrer des alignements adverse de longueur POWER-1
		// on cherche si en placant un pion dans la colonne on atteindra cette case ou bien si on sera trop bas
		if(!this.solutionsV.isEmpty()){
			int j=0;
			int q=0;
			// vérification d'abord pour les plus grandes chances de victoires
			// tant que le compteur de grandes solutions n'est pas atteint et qu'aucune solution n'est trouvée
			while((q<cptV3) && (!this.v3bool && !foundSolution)){
				// si la case visée sera atteinte la solution est stockée dasn v3
				if(this.solutionsV.get(q).getX()==this.firstFreeSquareInRow(this.solutionsV.get(q).getY())){
					this.v3.add(this.solutionsV.get(q).getY());	
					this.v3bool=true;
					foundSolution=true;
				// si la case ne peut être atteinte immédiatement , on enregistre la colonne comme une solution possible si rien de mieux
				}else if(this.solutionsV.get(q).getX()<this.firstFreeSquareInRow(this.solutionsV.get(q).getY())){
					this.futureV3.add(this.solutionsV.get(q).getY());
					this.future3=true;
				}				
				q++;
			}
			int r=0;
			// vérification pour des possibilités d'aligner 3 jetons
			while(r<cptV2){
				// si la case visée sera atteinte la solution est stockée dasn v2
				if(this.solutionsV.get(r).getX()==this.firstFreeSquareInRow(this.solutionsV.get(r).getY())){
					this.v2.add(this.solutionsV.get(r).getY());	
					v2bool=true;
					foundSolution=true;
				}
				// si la case ne peut être atteinte immédiatement , on enregistre la colonne comme une solution possible si rien de mieux
				else if(this.solutionsV.get(r).getX()<this.firstFreeSquareInRow(this.solutionsV.get(r).getY())){
					this.futureV2.add(this.solutionsV.get(r).getY());
					this.future2=true;
				}
				r++;
			}
			int s=0;
			// vérification pour des possibilités d'aligner 2 jetons
			while(s<cptV1){
				// si la case ne peut être atteinte immédiatement , on enregistre la colonne comme une solution possible si rien de mieux
				if(this.solutionsV.get(s).getX()==this.firstFreeSquareInRow(this.solutionsV.get(s).getY())){
					this.v1.add(this.solutionsV.get(s).getY());	
					foundSolution=true;
					v1bool=true;
				}
				s++;
			}
		}
	}


	/**
	* Methods whose only role is to call the checkAlignment methods for a specific Square received as a paramater.
	* @param victorious, a Square that will be analysed to see if it's the start of an alignment.
	*/
	private void computeVictory(Square victorious){
		checkHVictory(victorious .getX(), victorious .getY());

		checkVVictory(victorious .getX(), victorious .getY());

		checkDAVictory(victorious .getX(), victorious .getY());
	}


	/**
	* Checks the horizontal alignment of the coins of the current AutoPlayer to look for alignment.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the current player.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it continues the counting and the square is stored as a possible Victory move.
	* If the next Square after the gap is of the enemy the counting stops and the gap is stored as possible Victory.
	* Depending on the length of the alignment the solutions founds are stored in arraylist solutionsV1, solutionsV2 ou solutionsV3.
	* @param x, the x coordinate of a Square.
	* @param y, the y coordinate of a Square.
	*/
	private void checkHVictory(int x, int y){
		boolean aligneDroite=true;
		boolean aligneGauche=true;
		int i= y+1;
		int j= y-1;
		// arraylist de stockage provisoire des solutions possibles
		ArrayList<Square>solutionsHD =new ArrayList<Square>();
		ArrayList<Square>solutionsHG =new ArrayList<Square>();

		// compteur de pions de la même couleur alignés horizontalement
		int cptAligne=1;
		CoinColor testColor=this.color;
		// boucle qui décompte l'alignement à droite du pion
		// on ajoute un à chaque fois que le pion à droite est de la même couleur que l'Autoplayer
		// le décompte s'arrête quand on atteint une couleur différente.
		while(i<this.width && aligneDroite){
			// si on atteint une case vide
			if(this.grid[x][i].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionsHD.contains(this.grid[x][i])){solutionsHD.add(this.grid[x][i]);}
				int k=i+1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(k<this.width && suite){
					if(this.grid[x][k].getColor()==testColor){
						cptAligne++;
					}else{
						suite=false;
					}
					k++;
				}
				aligneDroite=false;
			}
			// si de couleur ennemie l'analyse s'arrête
			if(this.grid[x][i].getColor()==this.colorEnemy){
				aligneDroite=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[x][i].getColor()==testColor){
				cptAligne++;
				i++;
			}			
		}	

		if(!solutionsHD.isEmpty()){
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			if(solutionsHD.get(0).getY()+(power-(cptAligne+1))>=this.width){
				solutionsHD.clear();
			}
		}


		// boucle qui décompte l'alignement à gauche du pion initial	
		while(j>=0 && aligneGauche){	
			// si on atteint une case vide
			if(this.grid[x][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionsHG.contains(this.grid[x][j])){solutionsHG.add(this.grid[x][j]);}
				int k=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(k>=0 && suite){
					if(this.grid[x][k].getColor()==testColor){
						cptAligne++;
					}else{
						suite=false;
					}
					k--;
				}				
				aligneGauche=false;
			// si de couleur ennemie l'analyse s'arrête
			}if(this.grid[x][j].getColor()==this.colorEnemy){
				aligneGauche=false;
			// si de couleur de l'IA on ajoute 1 au décompte
			}if(this.grid[x][j].getColor()==testColor){
				 cptAligne++;
				 j--;
			}
		}


		if(!solutionsHG.isEmpty()){
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			for(int a=0; a<solutionsHG.size();a++){
				if(solutionsHG.get(a).getY()-(power-(cptAligne+1))<0){
					solutionsHG.remove(a);
				}
			}

		}


		// suivant le nombre de pion de aligné on stocke la solution dans solutionV1, solutionV2 ou solutionV3
		if(cptAligne==1){
			for(Square sq : solutionsHD){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}
			for(Square sq : solutionsHG){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}
		}	
		if(cptAligne==2){
			for(Square sq : solutionsHD){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
			for(Square sq : solutionsHG){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
		}
		if(cptAligne>=3){
			for(Square sq : solutionsHD){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}
			for(Square sq : solutionsHG){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}
		}
		
		// on vide les arraylist de solutons provisoires
		solutionsHD.clear();
		solutionsHG.clear();
	}



	/**
	* Checks the vertical alignment of the coins of the current AutoPlayer to look for alignment.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the current player.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it continues the counting and the square is stored as a possible Victory move.
	* If the next Square after the gap is of the enemy the counting stops and the gap is stored as possible Victory.
	* Depending on the length of the alignment the solutions founds are stored in arraylist solutionsV1, solutionsV2 ou solutionsV3.	
	* @param x, the x coordinate of a Square.
	* @param y, the y coordinate of a Square. 
	 */
	 private void checkVVictory(int x, int y){
			// arraylist de stockage provisoire des solutions possibles
		ArrayList<Square>solutionVertB=new ArrayList<Square>();
		ArrayList<Square>solutionVertH=new ArrayList<Square>();
		boolean aligneBas=true;
		boolean aligneHaut=true;
		int i= x+1;
		int j= x-1;
		// compteur de pion de la même couleur de l'IA alignés verticalement	
		int cptAligne=1;
		CoinColor testColor=this.color;
		
		// boucle qui décompte l'alignement vers le bas du pion analysé
		// on ajoute un à chaque fois que le pion en dessous du précédent est de la couleur de l'IA
		// le décompte s'arrête quand on atteint une couleur différente.
		while(i<this.height && aligneBas){
			// si on atteint une case vide
			if(this.grid[i][y].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionVertB.contains(this.grid[i][y])){solutionVertB.add(this.grid[i][y]);}
				aligneBas=false;
			// si de couleur ennemie l'analyse s'arrête
			}if(this.grid[i][y].getColor()==this.colorEnemy){
				aligneBas=false;
			}			
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[i][y].getColor()==testColor){
				 cptAligne++;
				 i++;
			}	
		}

		if(!solutionVertB.isEmpty()){
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			if(solutionVertB.get(0).getX()-(power-(cptAligne+1))<0){
				solutionVertB.clear();
			}
		}

		// boucle qui décompte l'alignement vers le haut du nouveau pion
		while(j>=0 && aligneHaut){	
			// si on atteint une case vide
			if(this.grid[j][y].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionVertH.contains(this.grid[j][y])){solutionVertH.add(this.grid[j][y]);}
				aligneHaut=false;
			}
			// si de couleur ennemie l'analyse s'arrête
			if(this.grid[j][y].getColor()==this.colorEnemy){
				aligneHaut=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[j][y].getColor()==testColor){
				 cptAligne++;
				 j--;
			}			
		}	

		if(!solutionVertH.isEmpty()){
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			if(solutionVertH.get(0).getX()-(power-(cptAligne+1))<0){
				solutionVertH.clear();
			}
		}

		// suivant le nombre de pion de aligné on stocke la solution dans solutionV1, solutionV2 ou solutionV3
		if(cptAligne==1){
			for(Square sq : solutionVertB){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}
			for(Square sq : solutionVertH){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}			
		}	
		if(cptAligne==2){
			for(Square sq : solutionVertB){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
			for(Square sq : solutionVertH){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
		}
		if(cptAligne>=3){
			for(Square sq : solutionVertB){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}
			for(Square sq : solutionVertH){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}			
		}
		// vide els arraylist de solutions provisoires
		solutionVertB.clear();
		solutionVertH.clear();
	}	

		
	/**
	* Checks the alignment in all possible diagonals starting from the coins of the current AutoPlayer which coordinates have been received as parameters.
	* The method compute the alignment by adding 1 to a counter every time it meets a Square owned by the current player.
	* If it finds out an empty gap it checks the next Square. If the Square is the autoplayer's coin it continues the counting and the square is stored as a possible Victory move.
	* If the next Square after the gap is of the enemy the counting stops and the gap is stored as possible Victory.
	* Depending on the length of the alignment the solutions founds are stored in arraylist solutionsV1, solutionsV2 ou solutionsV3.
	* @param x, the x coordinate of a Square.
	* @param y, the y coordinate of a Square.
	*/
	 private void checkDAVictory(int x, int y){

	 	// ****************************** partie test de la diagonale y=-x (diagonale qui part du de haut en à gauche vers bas droite) ******************************
		// arraylist de stockage provisoire des solutions possibles
		ArrayList<Square>solutionDiagBD=new ArrayList<Square>();
		ArrayList<Square>solutionDiagBG=new ArrayList<Square>();
		boolean aligneBasDroite=true;
		boolean aligneHautGauche=true;
		boolean solEstTrou=false;
		int i= x+1;
		int j= y+1;
		// compteur de pion de la même couleur alignés sur la diagonale y = -x
		int cptAligneDiagX=1;
		CoinColor testColor=this.color;
		
		// boucle qui décompte l'alignement vers le bas à droite nouveau pion
		// on ajoute un à chaque fois que le pion dans la diagonale du précédent est de la même couleur que le pion initial
		// le décompte s'arrête quand on atteint une couleur différente.		
		while(i<this.height && j<this.width && aligneBasDroite){
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionDiagBD.contains(this.grid[i][j])){solutionDiagBD.add(this.grid[i][j]);}
				int k=i+1;
				int l=j+1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(k<this.height && l<this.width && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagX++;
						solEstTrou=true;
					}else{
						suite=false;
					}
					k++;
					l++;
				}
				aligneBasDroite=false;
			// si de couleur ennemie l'analyse s'arrête
			}if(this.grid[i][j].getColor()==this.colorEnemy){
				aligneBasDroite=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 i++;
				 j++;
			}
		}

		// boucle qui décompte l'alignement en haut à gauche du nouveau pion		
		i=x-1;
		j=y-1;
		while(j>=0 && i>=0 && aligneHautGauche){	
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionDiagBD.contains(this.grid[i][j])){solutionDiagBD.add(this.grid[i][j]);}

				int k=i-1;
				int l=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(l>=0 && k>=0 && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagX++;
						solEstTrou=true;
					}else{
						suite=false;
					}
					k--;
					l--;
				}
				aligneHautGauche=false;
			}
			// si de couleur ennemie l'analyse s'arrête
			if(this.grid[i][j].getColor()==this.colorEnemy){
				aligneHautGauche=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagX++;
				 j--;
				 i--;
			}
		}
		if(!solEstTrou){
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			if(!solutionDiagBD.isEmpty()){
				if((solutionDiagBD.get(0).getX()-(power-(cptAligneDiagX+1))<0) ||(solutionDiagBD.get(0).getY()-(power-(cptAligneDiagX+1))<0) ){
					solutionDiagBD.clear();
				}
			}
		}
		
		// suivant le nombre de pion de aligné on stocke la solution dans solutionV1, solutionV2 ou solutionV3 (V3 étant les plus grands alignements)
		if(cptAligneDiagX==1){
			for(Square sq : solutionDiagBD){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}
		}	
		if(cptAligneDiagX==2){
			for(Square sq : solutionDiagBD){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
		}
		if(cptAligneDiagX>=3){
			for(Square sq : solutionDiagBD){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}
		}

		// vide l'arraylist de solutions provisoires
		solutionDiagBD.clear();
		
	 	// ****************************** partie test de la diagonale y=x (part du abs à gauche vers le haut à droite )******************************
		solEstTrou=false;
		boolean aligneHautDroite=true;
		boolean aligneBasGauche=true;
		i= x-1;
		j= y+1;
		// compteur de pion de la même couleur alignés sur la diagonale y = x		
		int cptAligneDiagMinusX=1;
		
		// on vérifie que la première diagonale testée ne donne pas déjà une victoire
		// boucle qui décompte l'alignement en haut à droite du nouveau pion	
		while(i>=0 && j<this.width && aligneHautDroite){
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionDiagBG.contains(this.grid[i][j])){solutionDiagBG.add(this.grid[i][j]);}

				int k=i-1;
				int l=j+1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(k>=0 && l<this.width && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagMinusX++;
						solEstTrou=true;
					}else{
						suite=false;
					}
					k--;
					l++;
				}
				aligneHautDroite=false;
			// si de couleur ennemie l'analyse s'arrête
			}if(this.grid[i][j].getColor()==this.colorEnemy){
				aligneHautDroite=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagMinusX++;
				 i--;
				 j++;
			}
		}	

		i=x+1;
		j=y-1;
		// boucle qui décompte l'alignement en bas à gauche du nouveau pion			
		while(j>=0 && i<this.height && aligneBasGauche){	
			// si on atteint une case vide
			if(this.grid[i][j].getColor()==CoinColor.NONE){
				//on sauvegarde la case comme possible solution
				if(!solutionDiagBG.contains(this.grid[i][j])){solutionDiagBG.add(this.grid[i][j]);}
				int k=i+1;
				int l=j-1;
				boolean suite=true;
				// on analyse la case suivante, si elle contient un pion de l'IA on poursuit le décompte
				while(l>=0 && k<this.height && suite){
					if(this.grid[k][l].getColor()==testColor){
						cptAligneDiagMinusX++;
						solEstTrou=true;
					}else{
						suite=false;
					}
					k++;
					l--;
				}

				aligneBasGauche=false;
			// si de couleur ennemie l'analyse s'arrête
			}if(this.grid[i][j].getColor()==this.colorEnemy){
				aligneBasGauche=false;
			}
			// si de couleur de l'IA on ajoute 1 au décompte
			if(this.grid[i][j].getColor()==testColor){
				 cptAligneDiagMinusX++;
				 j--;
				 i++;
			}
		}
		if(!solEstTrou){		
			// vérifie qu'il y a assez de place pour aligner les jetons nécessaires, sinon, la solution est retirée
			if(!solutionDiagBG.isEmpty()){
				if((solutionDiagBG.get(0).getX()-(power-(cptAligneDiagX+1))<0) ||(solutionDiagBG.get(0).getY()+(power-(cptAligneDiagX+1))>=this.width) ){
					solutionDiagBG.clear();
				}
			}
		}
		// si le décompte total donne un nombre supérieur ou égal à la puissance il y a victoire de la couleur	
		// suivant le nombre de pion de aligné on stocke la solution dans solutionV1, solutionV2 ou solutionV3 (V3 étant les plus grands alignements)	
		if(cptAligneDiagMinusX==1){
			for(Square sq : solutionDiagBG){
				if(!solutionsV1.contains(sq)){this.solutionsV1.add(sq);}
			}
		}	
		if(cptAligneDiagMinusX==2){
			for(Square sq : solutionDiagBG){
				if(!solutionsV2.contains(sq)){this.solutionsV2.add(sq);}
			}
		}
		if(cptAligneDiagMinusX>=3){
			for(Square sq : solutionDiagBG){
				if(!solutionsV3.contains(sq)){this.solutionsV3.add(sq);}
			}
		}
		
		// vide l'arraylist de solutions provisoires
		solutionDiagBG.clear();
	}


	// --- GETTERS AND SETTERS

	/**
	 * Returns true if a move from the AutoPlayer will give him the win
	 * @return a boolean
	*/
	private boolean getV3Bool(){return this.v3bool;}


}
