package auo.cms.applet.hsvdump;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
public class HSVDumpFrame_AboutBox
    extends JDialog implements ActionListener {
  protected JPanel panel1 = new JPanel();
  protected JPanel panel2 = new JPanel();
  protected JPanel insetsPanel1 = new JPanel();
  protected JPanel insetsPanel2 = new JPanel();
  protected JPanel insetsPanel3 = new JPanel();
  protected JButton button1 = new JButton();
  protected JLabel imageLabel = new JLabel();
  protected JLabel label1 = new JLabel();
  protected JLabel label2 = new JLabel();
  protected JLabel label3 = new JLabel();
  protected JLabel label4 = new JLabel();
  protected ImageIcon image1 = new ImageIcon();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected FlowLayout flowLayout1 = new FlowLayout();
  protected GridLayout gridLayout1 = new GridLayout();
  protected String product = "Colour Management System";
  protected String version = "1.0";
  protected String copyright = "Copyright (c) 2009";
  protected String comments = "a Colour Management System by Java";

  public HSVDumpFrame_AboutBox(Frame parent) {
    super(parent);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public HSVDumpFrame_AboutBox() {
    this(null);
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    image1 = new ImageIcon(auo.cms.applet.hsvdump.HSVDumpFrame.class.
                           getResource("about.png"));
    imageLabel.setIcon(image1);
    setTitle("About");
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(product);
    label2.setText(version);
    label3.setText(copyright);
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText("OK");
    button1.addActionListener(this);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
    setResizable(true);
  }

  /**
   * Close the dialog on a button event.
   *
   * @param actionEvent ActionEvent
   */
  public void actionPerformed(ActionEvent actionEvent) {
    if (actionEvent.getSource() == button1) {
      dispose();
    }
  }
}