package view;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;
import power.Square;


public class GridTableFrame extends SimpleFrame {
  private final int rowHeight = 55;  //pixel

  public GridTableFrame(Square[][] grid) {
    // set the grid size
    this.setSize(rowHeight*grid.length,rowHeight*grid[0].length);
    GridTableModel otmodel = new GridTableModel(grid);
    JTable tab = new JTable(otmodel);
    // to adjust some parameters
    tab.setShowGrid(true);
    // color for the grid lines
    tab.setGridColor(Color.BLUE);
    tab.setRowHeight(rowHeight);

    JScrollPane SP = new JScrollPane(tab);
    this.getContentPane().add(SP);

  }

 
}