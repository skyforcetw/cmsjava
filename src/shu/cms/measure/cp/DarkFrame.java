package shu.cms.measure.cp;

import java.awt.*;
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
public class DarkFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();

  public DarkFrame(Color color) {
    try {
      jbInit();
      afterInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    this.background = color;
  }

  private Color background = Color.black;
  public void setColor(Color color) {
    this.getContentPane().setBackground(color);
  }

  private void afterInit() {
    this.setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化
    this.setResizable(false); //不能改變大小
    this.setUndecorated(true); //不要邊框
    this.setAlwaysOnTop(true);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.getContentPane().setBackground(background);
  }
}
