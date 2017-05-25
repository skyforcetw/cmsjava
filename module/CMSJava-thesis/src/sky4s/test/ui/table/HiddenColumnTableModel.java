package sky4s.test.ui.table;

import java.util.*;

import javax.swing.table.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class HiddenColumnTableModel
    extends AbstractTableModel {

  /** Vector of Object[], this are the datas of the table */
  Vector datas = new Vector();

  /** Indicates which columns are visible */
  boolean[] columnsVisible = new boolean[4];

  /** Column names */
  String[] columnsName = {
      "0", "1", "2", "3"
  };

  /** Constructor */
  public HiddenColumnTableModel() {
    columnsVisible[0] = true;
    columnsVisible[1] = true;
    columnsVisible[2] = true;
    columnsVisible[3] = true;
  }

  /**
   * This functiun converts a column number in the table
   * to the right number of the datas.
   */
  protected int getNumber(int col) {
    int n = col; // right number to return
    int i = 0;
    do {
      if (! (columnsVisible[i])) {
        n++;
      }
      i++;
    }
    while (i < n);
    // If we are on an invisible column,
    // we have to go one step further
    while (! (columnsVisible[n])) {
      n++;
    }
    return n;
  }

  // *** TABLE MODEL METHODS ***

  public int getColumnCount() {
    int n = 0;
    for (int i = 0; i < 4; i++) {
      if (columnsVisible[i]) {
        n++;
      }
    }
    return n;
  }

  public int getRowCount() {
    return datas.size();
  }

  public Object getValueAt(int row, int col) {
    Object[] array = (Object[]) (datas.elementAt(row));
    return array[getNumber(col)];
  }

  public String getColumnName(int col) {
    return columnsName[getNumber(col)];
  }
}
