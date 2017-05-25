package vv.cms.measure.cp.msg;

import java.awt.*;
import javax.swing.*;

import shu.util.log.frame.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CPMMessageFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JScrollPane jScrollPane2 = new JScrollPane();
  protected JTextArea jTextArea1 = new JTextArea(29, 50);
  protected JTextArea jTextArea2 = new JTextArea(29, 50);
  protected Box hbox1 = Box.createHorizontalBox();
  protected JLabel jLabel1 = new JLabel();
  protected JLabel jLabel2 = new JLabel();
  public CPMMessageFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public LoggerInterface getMeasureLoggerInterface() {
    return new LoggerInterface() {
      public void log(String msg) {
        addMeasureInfo(msg);
      }
    };
  }

  public LoggerInterface getTriggerLoggerInterface() {
    return new LoggerInterface() {
      public void log(String msg) {
        addTriggerInfo(msg);
      }
    };
  }

  private int measureIndex = 0;
  private int triggerIndex = 0;

  public void addMeasureInfo(String msg) {
//    String text = jTextArea1.getText();
//    jTextArea1.setText(text + '(' + measureIndex++ +") " + msg);
    jTextArea1.append("(" + measureIndex++ +") " + msg);
    jTextArea1.setCaretPosition(jTextArea1.getText().length());
  }

  public void addTriggerInfo(String msg) {
//    String text = jTextArea2.getText();
//    jTextArea2.setText(text + '(' + triggerIndex++ +") " + msg);
    jTextArea2.append("(" + triggerIndex++ +") " + msg);
    jTextArea2.setCaretPosition(jTextArea2.getText().length());
  }

  private void jbInit() throws Exception {
    this.setSize(1200, 600);
    getContentPane().setLayout(borderLayout1);
    jTextArea1.setMargin(new Insets(0, 0, 0, 0));
    jTextArea1.setText("");
    jTextArea2.setText("");
    jLabel1.setText("Measure Info");
    jLabel2.setText("Trigger Info");
    this.setTitle("CPM Message");
    hbox1.add(jPanel1);
    jPanel1.add(jLabel1);
    jPanel1.add(jScrollPane1);
    hbox1.add(jPanel2);
    jPanel2.add(jLabel2);
    jPanel2.add(jScrollPane2);
    this.getContentPane().add(hbox1, java.awt.BorderLayout.CENTER);

    jScrollPane1.getViewport().add(jTextArea1);
    jScrollPane2.getViewport().add(jTextArea2);
    jScrollPane1.setAutoscrolls(true);
    jScrollPane2.setAutoscrolls(true);
//    jScrollPane1.setvers
//    jTextArea1.getDocument().getLength()
  }

  public static void main(String[] args) {
    new CPMMessageFrame().setVisible(true);
  }
}
