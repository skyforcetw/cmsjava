package shu.util.log.frame;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �إߤ@�ӰT������, �i�H��Logger���T�����۰���ܦb�o������.
 * �C�@��Logger�����W�ߪ�Panel; �Q��Logger����default Logger��, �����[�JLoggerFrameHandler,
 * �o��handler�|�N�T���ǰe��LoggerFrame�W.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class LoggerFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JTabbedPane jTabbedPane1 = new JTabbedPane();

  /**
   * �s�W�@��handler��LoggerFrame���
   * �ҥ��n���u�@�]�A:
   * 1. �إ�JTextArea��JScrollPane
   * 2. �]�wpost�޹D��handler
   * @param handler LoggerFrameHandler
   * @param logName String
   */
  public void addHandler(LoggerFrameHandler handler, String logName) {
    final JTextArea jTextArea1 = new JTextArea();
    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.getViewport().add(jTextArea1);
    handler.setLoggerInterface(new LoggerInterface() {
      public void log(String msg) {
        jTextArea1.append(msg);
        jTextArea1.setCaretPosition(jTextArea1.getText().length());
      }
    });
    jTabbedPane1.add(jScrollPane1, logName);
  }

  public LoggerFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(1200, 600);
    getContentPane().setLayout(borderLayout1);
    this.setTitle("Logger");
    //    jTextArea1.setText("jTextArea1");
    this.getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);
//    jTabbedPane1.add(jScrollPane1, "jScrollPane1");
//    jScrollPane1.getViewport().add(jTextArea1);
  }

}
