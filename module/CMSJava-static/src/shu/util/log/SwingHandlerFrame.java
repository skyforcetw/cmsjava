package shu.util.log;

import java.awt.*;
import javax.swing.*;

import shu.ui.*;

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
public class SwingHandlerFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JTextArea jTextArea1 = new JTextArea();
  private String title;
  private String message;

  public SwingHandlerFrame(String title, String message) {
    this.title = title;
    this.message = message;
    try {
      jbInit();
      myInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void addMessage(String message) {
    this.message = this.message + message;
    this.jTextArea1.setText(this.message);
  }

  private void myInit() throws Exception {
    GUIUtils.halfScreen(this);
    GUIUtils.center(this);
    this.setTitle(title);
    this.jTextArea1.setText(message);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setVisible(true);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
    jTextArea1.setEditable(false);
    jScrollPane1.getViewport().add(jTextArea1);
  }

  public static void main(String[] args) {
    SwingHandlerFrame swinghandlerframe = new SwingHandlerFrame("title",
        "1refdjfjadpfjp3wprfh3pfhwpef1refdjfjadpfjp3wprfh3pfhwpef1refdjfjadpfjp3\n" +
        "wprfh3pfhwpef1refdjfjadpfjp3wprfh3pfhwpef1refdjfjadpfjp3wprfh3pfhwpef1r\n" +
        "efdjfjadpfjp3wprfh3pfhwpef1refdjfjadpfjp3wprfh3pfhwpef1refdjfjadpfjp3wp\n" +
        "rfh3pfhwpef");
//    swinghandlerframe.setVisible(true);
    Logger.log.info("1234");
  }
}
