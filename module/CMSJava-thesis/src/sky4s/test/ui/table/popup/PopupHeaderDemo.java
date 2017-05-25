package sky4s.test.ui.table.popup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Created this prototype to see how difficult/easy it would be to put a popup
 * menu (JPopupMenu) on the header of a JTable. While I was in the
 * neighborhood I also put the popup menu on the JTable proper.
 *
 * Note -- Some of this is inspired from
 * "http://www.jroller.com/page/santhosh/20050609#let_user_choose_their_favourite".
 */
public class PopupHeaderDemo
    extends JFrame {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane theScrollPane = new JScrollPane();
  JTable myJTable = new JTable(new DefaultTableModel(
      new String[] {"Col 1", "Col 2", "Col 3", "Col 4", "Col 5"}, 5));

  JPopupMenu popupMenu = new JPopupMenu();
  private static String INSERT_CMD = "Insert New Column";

  public PopupHeaderDemo() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("Right Click Column Header Demo");
    contentPane.add(theScrollPane, BorderLayout.CENTER);
    theScrollPane.getViewport().add(myJTable, null);

    JMenuItem menuItem = new JMenuItem(INSERT_CMD);
    menuItem.addActionListener(new InsertRowsActionAdapter(this));
    popupMenu.add(menuItem);

    // add the listener to the jtable
    MouseListener popupListener = new PopupListener();
    // add the listener specifically to the header
    myJTable.addMouseListener(popupListener);
    myJTable.getTableHeader().addMouseListener(popupListener);
  }

  class PopupListener
      extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      showPopup(e);
    }

    private void showPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  void insertColumn(ActionEvent e) {
    JOptionPane.showMessageDialog(this,
                                  "Would prompt you to enter a new column name here.");
    // do something here
  }

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

} // end of class

class InsertRowsActionAdapter
    implements ActionListener {
  PopupHeaderDemo adaptee;

  InsertRowsActionAdapter(PopupHeaderDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.insertColumn(e);
  }
}
