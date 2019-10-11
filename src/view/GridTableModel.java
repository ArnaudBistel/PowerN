package view;

import javax.swing.table.AbstractTableModel;
import javax.swing.ImageIcon;
import power.Square;
import power.CoinColor;

public class GridTableModel extends AbstractTableModel {

  private int noOfRows, noOfCols;
  private Square[][] grid;
  private static final String PATH = "../images/"; 
  private String imageFree= "blanc.png";
  private String imageYellow= "jaune.png";
  private String imageRed= "rouge.png";
    
  
 /*
  * Constructor
  * @param grid : the table to display
  */
  public GridTableModel(Square[][] grid) {
    this.grid = grid;
    noOfRows = this.grid.length;
    noOfCols = this.grid[0].length;
  }

// Implementing the tree abstract methods:
  
   public int getRowCount() {
    return(noOfRows);
  }
  public int getColumnCount() {
    return(noOfCols);
  }

  public Object getValueAt(int r,int c) {
    Object result = new Object();
    Square sq = grid[r][c];
    if (sq.isFree()) result= new ImageIcon(PATH + imageFree);
    else if (sq.getColor() ==CoinColor.YELLOW) result= new ImageIcon(PATH + imageYellow);
    else result= new ImageIcon(PATH + imageRed);
    return result;
  }
 
 
  /**
   * get the name of the column
   * @param c : the number of the column
   * @return a String for the name of the column
   */
  public String getColumnName(int c){
    return (new Integer(c).toString());
  }
  
   /**
   * get the class of the object at column c
   * @param c : the number of the column
   * @return the class of the object at column c
   */
   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }
}