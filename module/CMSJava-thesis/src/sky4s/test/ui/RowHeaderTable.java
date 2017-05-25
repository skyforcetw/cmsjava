package sky4s.test.ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.table.*;

public class RowHeaderTable
    extends JFrame {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RowHeaderTable() {
    super("Row Header Test");
    setSize(400, 300);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    TableModel tm = new AbstractTableModel() {
      /**
       *
       */
      private static final long serialVersionUID = 1L;
      String data[] = {
          "", "a", "b", "c", "d", "e"};
      public String headers[] = {
          "Row #", "Column 1", "Column 2", "Column 3",
          "Column 4", "Column 5"};
      public int getColumnCount() {
        return data.length;
      }

      public int getRowCount() {
        return 1000;
      }

      public String getColumnName(int col) {
        return headers[col];
      }

      // Synthesize some entries using the data values & the row #
      public Object getValueAt(int row, int col) {
        return data[col] + row;
      }
    };

    // Create a column model for the main table. This model ignores the first
    // column added, and sets a minimum width of 150 pixels for all others.
    TableColumnModel cm = new DefaultTableColumnModel() {
      /**
       *
       */
      private static final long serialVersionUID = 1L;
      boolean first = true;
      public void addColumn(TableColumn tc) {
        // Drop the first column . . . that'll be the row header
        if (first) {
          first = false;
          return;
        }
        tc.setMinWidth(30); // just for looks, really...
        tc.setHeaderRenderer(new RotatedTableCellRenderer( -90));
        super.addColumn(tc);
      }
    };

    // Create a column model that will serve as our row header table. This
    // model picks a maximum width and only stores the first column.
    TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
      /**
       *
       */
      private static final long serialVersionUID = 1L;
      boolean first = true;
      public void addColumn(TableColumn tc) {
        if (first) {
          tc.setMaxWidth(tc.getPreferredWidth());
          //tc.setHeaderRenderer(new RotatedTableCellRenderer(-90));
          super.addColumn(tc);
          first = false;
        }
        // Drop the rest of the columns . . . this is the header column only
      }
    };

    JTable jt = new JTable(tm, cm);
    Dimension d = jt.getTableHeader().getPreferredSize();
    d.height = 60;
    jt.getTableHeader().setPreferredSize(d);

    // Set up the header column and get it hooked up to everything
    JTable headerColumn = new JTable(tm, rowHeaderModel);
    jt.createDefaultColumnsFromModel();
    headerColumn.createDefaultColumnsFromModel();

    // Make sure that selections between the main table and the header stay
    // in sync (by sharing the same model)
    jt.setSelectionModel(headerColumn.getSelectionModel());

    // Make the header column look pretty
    //headerColumn.setBorder(BorderFactory.createEtchedBorder());
    headerColumn.setBackground(Color.lightGray);
    headerColumn.setColumnSelectionAllowed(false);
    headerColumn.setCellSelectionEnabled(true);

    // Put it in a viewport that we can control a bit
    JViewport jv = new JViewport();
    jv.setView(headerColumn);
    jv.setPreferredSize(headerColumn.getMaximumSize());

    // With out shutting off autoResizeMode, our tables won't scroll
    // correctly (horizontally, anyway)
    jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // We have to manually attach the row headers, but after that, the scroll
    // pane keeps them in sync
    JScrollPane jsp = new JScrollPane(jt);
    jsp.setRowHeader(jv);
    jsp.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
                  headerColumn.getTableHeader());

    getContentPane().add(jsp, BorderLayout.CENTER);
  }

  public static void main(String args[]) {
    RowHeaderTable rht = new RowHeaderTable();
    rht.setVisible(true);
  }
}

class RotatedTableCellRenderer
    extends JLabel implements TableCellRenderer {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  protected int m_degreesRotation = -90;

  public RotatedTableCellRenderer(int degrees) {
    m_degreesRotation = degrees;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
    this.setText(value.toString());
    return this;
  }

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setClip(0, 0, this.getWidth(), this.getHeight());
    g2.setColor(Color.BLACK);
    g2.setFont(this.getFont());
    AffineTransform at = new AffineTransform();
    at.setToTranslation(this.getWidth(), this.getHeight());
    g2.transform(at);
    double radianAngle = ( ( (double) m_degreesRotation) / ( (double) 180)) *
        Math.PI;
    at.setToRotation(radianAngle);
    g2.transform(at);
    g2.drawString(this.getText(), 0.0f, -this.getWidth() / 2);
  }
}
