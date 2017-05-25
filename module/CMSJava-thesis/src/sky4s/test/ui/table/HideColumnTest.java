package sky4s.test.ui.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class HideColumnTest {
  public static void main(String[] args) {
    final JTable table = new JTable(10, 5);
    table.getColumnModel().getColumn(2).setMinWidth(0);

    JButton button = new JButton("Toggle Column 2");
    button.addActionListener(new ActionListener() {
      // the old size of the column
      private SizeRequirements size = new SizeRequirements();
      public void actionPerformed(ActionEvent e) {
        TableColumn col = table.getColumnModel().getColumn(2);
        // get the current size of the column
        SizeRequirements temp = new SizeRequirements();
        temp.preferred = col.getPreferredWidth();
        temp.minimum = col.getMinWidth();
        temp.maximum = col.getMaxWidth();
        // change the column size to the old size (or 0 if the first time)
        col.setMinWidth(size.minimum);
        col.setMaxWidth(size.maximum);
        col.setPreferredWidth(size.preferred);
        // save the old size
        size = temp;
      }
    });

    JPanel content = new JPanel(new BorderLayout(0, 10));
    content.add(button, BorderLayout.NORTH);
    content.add(new JScrollPane(table));

    JFrame f = new JFrame("Hide Column Test");
    f.setContentPane(content);
    f.pack();
    f.setLocationRelativeTo(null);
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
}
