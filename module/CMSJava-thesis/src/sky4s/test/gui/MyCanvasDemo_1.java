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
//�]�wCanvas����j�p
    setSize(640, 480);
  }

  public void paint(Graphics g) {
//�e�@�����u,�q�y��(100,100)��(150,150)
    g.drawLine(100, 100, 150, 150);
  }

  public static void main(String[] args) {
    Frame frame = new Frame("MyCanvasDemo_1");
    frame.add(new MyCanvasDemo_1());

//�]�w������ܦb�ù��b����m
    frame.setLocation(100, 100);

//�������k�W����X�ϥܳQ���U����A�����|����
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    frame.pack();
    frame.setVisible(true);
  }
}
