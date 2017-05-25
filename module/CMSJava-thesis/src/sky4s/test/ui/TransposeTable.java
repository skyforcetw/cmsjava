package sky4s.test.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class TransposeTable {
  private JTable table;
  private AbstractTableModel model;
  private JScrollPane jsp;

  public TransposeTable() {
    table = new JTable();
    setModel(getTranposeModelData());
    JTable headerTable = new JTable(getRowHeaderModel());
    headerTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        JLabel label = new JLabel(value.toString());
        label.setOpaque(true);
        label.setBackground(Color.lightGray);
        label.setBorder(BorderFactory.createRaisedBevelBorder());
        return label;
      }
    });
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(headerTable, BorderLayout.WEST);
    panel.add(table, BorderLayout.CENTER);

    jsp = new JScrollPane(panel);
  }

  private TableModel getModel() {
    model = new AbstractTableModel() {
      public int getRowCount() {
        return 10;
      }

      public int getColumnCount() {
        return 5;
      }

      public Object getValueAt(int row, int column) {
        return "( " + row + "," + column + " )";
      }

      public boolean isCellEditable(int row, int column) {
        return false;
      }

    };
    return model;
  }

  private TableModel getTranposeModelData() {
    model = new AbstractTableModel() {
      public int getRowCount() {
        return 5;
      }

      public int getColumnCount() {
        return 10;
      }

      public Object getValueAt(int row, int column) {
        return "( " + column + "," + row + " )";
      }

      public boolean isCellEditable(int row, int column) {
        return false;
      }

    };
    return model;
  }

  private TableModel getRowHeaderModel() {
    TableModel tabModel = new AbstractTableModel() {
      public int getRowCount() {
        return 5;
      }

      public int getColumnCount() {
        return 1;
      }

      public Object getValueAt(int row, int column) {
        return "( " + column + "," + row + " )";
      }

      public boolean isCellEditable(int row, int column) {
        return false;
      }

    };
    return tabModel;

  }

  public JScrollPane getScrollPane() {
    return jsp;
  }

  public void setModel(TableModel model) {
    table.setModel(model);
  }

  public static void main(String[] args) {
    TransposeTable tt = new TransposeTable();
    JFrame frame = new JFrame("Test Table");
    frame.getContentPane().add(tt.getScrollPane());
    frame.pack();
    frame.setVisible(true);
  }
}
