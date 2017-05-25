package jcolor.applet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.Box;
import com.borland.jbcl.layout.VerticalFlowLayout;
import java.awt.Dimension;
import javax.swing.JTextField;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MeasureToolFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JMenuBar jMenuBar1 = new JMenuBar();
  protected JMenu jMenuFile = new JMenu();
  protected JMenuItem jMenuFileExit = new JMenuItem();
  protected JMenu jMenuHelp = new JMenu();
  protected JMenuItem jMenuHelpAbout = new JMenuItem();
  protected JToolBar jToolBar = new JToolBar();
  protected JButton jButton1 = new JButton();
  protected JButton jButton2 = new JButton();
  protected JButton jButton3 = new JButton();
  protected ImageIcon image1 = new ImageIcon(jcolor.applet.MeasureToolFrame.class.
                                             getResource("openFile.png"));
  protected ImageIcon image2 = new ImageIcon(jcolor.applet.MeasureToolFrame.class.
                                             getResource("closeFile.png"));
  protected ImageIcon image3 = new ImageIcon(jcolor.applet.MeasureToolFrame.class.
                                             getResource("help.png"));
  protected JLabel statusBar = new JLabel();
  protected JTabbedPane jTabbedPane1 = new JTabbedPane();
  protected JPanel jPanel1 = new JPanel();
  public MeasureToolFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(800, 600));
    setTitle("Measure Tool");
    statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuFileExit_actionPerformed(actionEvent);
      }
    });

    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuHelpAbout_actionPerformed(actionEvent);
      }
    });
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
    jButton1.setIcon(image1);
    jButton1.setToolTipText("Open File");
    jButton2.setIcon(image2);
    jButton2.setToolTipText("Close File");
    jButton3.setIcon(image3);
    jButton3.setToolTipText("Help");
    jToolBar.add(jButton1);
    jToolBar.add(jButton2);
    jToolBar.add(jButton3);
//    contentPane.add(jToolBar, BorderLayout.NORTH);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    contentPane.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    jTabbedPane1.add(jPanel1, "jPanel1");
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    MeasureToolFrame_AboutBox dlg = new MeasureToolFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }
}
