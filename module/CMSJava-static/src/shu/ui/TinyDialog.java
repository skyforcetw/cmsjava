package shu.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
public class TinyDialog {

  public final static Dialog getExitDialogInstance(Frame owner,
      ActionListener exitActionListener) {
    return getExitDialogInstance(owner, "Exit", exitActionListener);
  }

  public final static Dialog getExitDialogInstance(Frame owner,
      String message, ActionListener exitActionListener) {
    Dialog d = new Dialog(owner, message, exitActionListener);
    if (owner != null) {
      d.setLocation(owner.getWidth() - d.getWidth(),
                    owner.getHeight() - d.getHeight());
    }
    return d;
  }

  public final static Dialog getDialogInstance(Frame owner,
                                               String message,
                                               ActionListener
                                               exitActionListener) {
    Dialog d = getDialogInstance(owner, message, exitActionListener, null);
//    if (owner != null) {
//      d.setLocation( (owner.getWidth() - d.getWidth()) / 2,
//                    (owner.getHeight() - d.getHeight()) / 3);
//    }
    return d;
  }

  public final static Dialog getDialogInstance(Frame owner,
                                               String message,
                                               ActionListener
                                               exitActionListener,
                                               int x, int y) {
    return getDialogInstance(owner, message, exitActionListener, new Point(x, y));
  }

  public final static Dialog getDialogInstance(Frame owner,
                                               String message,
                                               ActionListener
                                               exitActionListener,
                                               Point location) {
    Dialog d = new Dialog(owner, message, exitActionListener);
    if (location != null) {
      d.setLocation(location);
    }
    return d;
  }

  public final static Dialog getStartDialogInstance(Frame owner,
      String message, ActionListener exitActionListener) {
    Dialog d = new Dialog(owner, message, exitActionListener);
    if (owner != null) {
      d.setLocation( (owner.getWidth() - d.getWidth()) / 2,
                    (owner.getHeight() - d.getHeight()) / 3);
    }
    return d;
  }

  public final static class Dialog
      extends JDialog {

    protected JPanel panel1 = new JPanel();
    protected BorderLayout borderLayout1 = new BorderLayout();
    protected JButton jButton1 = new JButton();
    protected ActionListener exitActionListener;
    protected WindowFocusListener focusListener;
    protected String message;

    protected Dialog(Frame owner, String message,
                     ActionListener exitActionListener) {
      super(owner, null, false);
      this.message = message;
      this.exitActionListener = exitActionListener;
      try {
        jbInit();
        pack();
      }
      catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    public void setMessage(String message) {
      jButton1.setText(message);
    }

    private void jbInit() throws Exception {
      this.setAlwaysOnTop(true);
      panel1.setLayout(borderLayout1);
      jButton1.setText(message);
      if (exitActionListener != null) {
        jButton1.addActionListener(exitActionListener);
      }
      getContentPane().add(panel1);
      panel1.add(jButton1, java.awt.BorderLayout.CENTER);
      this.setUndecorated(true);
    }

  }

  public static void main(String[] args) {
    /*TinyDialog.Dialog d = TinyDialog.getDialogInstance(null, "title",
        new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(1);
      }
         }, 300, 400);
         d.setVisible(true);*/
    //getStartDialogInstance(null, "", null, true).setVisible(true);

  }

}
