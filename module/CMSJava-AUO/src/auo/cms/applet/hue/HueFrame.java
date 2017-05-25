package auo.cms.applet.hue;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.AbstractTableModel;

import shu.ui.*;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import com.sun.media.jai.widget.DisplayJAI;
import java.awt.Dimension;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class HueFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  private String[] columnNames = {
      "H", "S", "V"};
  private Object[][] data = new Object[24][3];

  class MyTableModel
      extends AbstractTableModel {

    MyTableModel() {
      int size = data.length;
      double pieceHue = 360. / size;
      for (int x = 0; x < size; x++) {
        data[x][0] = new Integer( (int) pieceHue * x);
        data[x][1] = new Integer(1);
        data[x][2] = new Integer(0);
      }
    }

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
//      if (col < 2) {
//        return false;
//      }
//      else {
      return true;
//      }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
      data[row][col] = value;
      fireTableCellUpdated(row, col);
    }

  }

  protected JTable jTable1 = new JTable(new MyTableModel());
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JScrollPane jScrollPane2 = new JScrollPane();
//  protected JPanel jPanel1 = new JPanel();
  PlanarImage image = JAI.create("fileload", "7125_10.jpg");
  DisplayJAI jai = new DisplayJAI();
  protected JPanel jPanel1 = new JPanel();
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected JLabel jLabel1 = new JLabel();
  protected JSlider jSlider1 = new JSlider();
  protected JLabel jLabel_Value = new JLabel();
  protected JSlider jSlider2 = new JSlider();
  protected JLabel jLabel3 = new JLabel();
  protected JLabel jLabel4 = new JLabel();
  protected JLabel jLabel5 = new JLabel();
  protected JSlider jSlider3 = new JSlider();
  protected JLabel jLabel6 = new JLabel();
  public static void main(String args[]) {
    HueFrame frame = new HueFrame();
    GUIUtils.runAsApplication(frame, false);
    GUIUtils.defaultScreen(frame);
  }

  public HueFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setSize(800, 800);
    getContentPane().setLayout(borderLayout1);

    jTable1.setPreferredSize(new Dimension(225, 380));
    jScrollPane1.setPreferredSize(new Dimension(225, 424));
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText("Hue");
    jLabel_Value.setText("jLabel2");
    jLabel3.setText("0");
    jLabel4.setText("Saturation");
    jLabel5.setText("jLabel5");
    jLabel6.setText("Value");

    jScrollPane1.getViewport().add(jTable1);

    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);
    jScrollPane2.getViewport().add(jai);

    this.getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);
    jPanel1.add(jLabel_Value, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jSlider2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel5, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jSlider3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jSlider1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jai.set(image);
  }
}
