package sky4s.test.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * http://blog.pixnet.net/MylesLittleWolf/post/2836595
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MyCanvasDemo_1
    extends Canvas {
  public MyCanvasDemo_1() {
//設定Canvas元件大小
    setSize(640, 480);
  }

  public void paint(Graphics g) {
//畫一條直線,從座標(100,100)到(150,150)
    g.drawLine(100, 100, 150, 150);
  }

  public static void main(String[] args) {
    Frame frame = new Frame("MyCanvasDemo_1");
    frame.add(new MyCanvasDemo_1());

//設定視窗顯示在螢幕在的位置
    frame.setLocation(100, 100);

//讓視窗右上角的X圖示被按下之後，視窗會關閉
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    frame.pack();
    frame.setVisible(true);
  }
}
