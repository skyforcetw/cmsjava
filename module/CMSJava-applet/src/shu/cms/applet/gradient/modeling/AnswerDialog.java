package shu.cms.applet.gradient.modeling;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

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
public class AnswerDialog
    extends JDialog {
  protected JPanel panel1 = new JPanel();
  protected FlowLayout flowLayout1 = new FlowLayout();
  JButton jButton_accept = new JButton();
  protected JButton jButton_unaccept = new JButton();
  JButton jButton_cannotsee = new JButton();

  protected JLabel jLabel1 = new JLabel();
  protected JButton jButton_reset = new JButton();
  protected TitledBorder titledBorder1 = new TitledBorder("");
  protected TitledBorder titledBorder2 = new TitledBorder("");
  public AnswerDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setCode(int code) {
    jLabel1.setText(Integer.toString(code));
  }

  public AnswerDialog() {
    this(new Frame(), "Dialog1", false);
  }

  public static void main(String[] args) {
    AnswerDialog d = new AnswerDialog();
    d.setVisible(true);
  }

  public void setActionListener(ActionListener l) {
    jButton_accept.removeActionListener(l);
    jButton_unaccept.removeActionListener(l);
    jButton_cannotsee.removeActionListener(l);
    jButton_reset.removeActionListener(l);

    jButton_accept.addActionListener(l);
    jButton_unaccept.addActionListener(l);
    jButton_cannotsee.addActionListener(l);
    jButton_reset.addActionListener(l);
  }

  private void jbInit() throws Exception {
    this.setAlwaysOnTop(true);
    this.setUndecorated(true); //不要邊框
    panel1.setLayout(flowLayout1);
    jButton_accept.setSelectedIcon(null);
    jButton_accept.setText("可接受");
    jButton_unaccept.setText("不可接受");
    jButton_cannotsee.setSelectedIcon(null);
    jButton_cannotsee.setText("無法辨識");
    jLabel1.setText("999");
    jButton_reset.setAlignmentX( (float) 0.0);
    jButton_reset.setBorder(null);
    jButton_reset.setActionCommand("重來");
    jButton_reset.setText("重來");
    getContentPane().add(panel1);
    panel1.add(jLabel1);
    panel1.add(jButton_cannotsee);
    panel1.add(jButton_accept);
    panel1.add(jButton_unaccept);
    panel1.add(jButton_reset);
  }

}
