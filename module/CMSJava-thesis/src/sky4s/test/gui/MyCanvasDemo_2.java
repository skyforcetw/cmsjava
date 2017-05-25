package sky4s.test.gui;

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
public class MyCanvasDemo_2
    extends JComponent {
  public MyCanvasDemo_2() {

  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

//畫一條直線,從座標(100,100)到(150,150)
    g.drawLine(100, 100, 150, 150);
  }

//實做自訂元件時，
//最好覆寫這getPreferredSize、getMaximumSize、getMinimumSize三個方法
//因為許多LayoutManager都可能由這三個方法來決定此物件的大小
  public Dimension getPreferredSize() {
    return new Dimension(640, 480);
  }

  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public static void main(String[] args) {
//設定視窗的外觀
    JFrame.setDefaultLookAndFeelDecorated(true);

    JFrame frame = new JFrame("MyCanvasDemo_2");

//在Swing的JFrame元件中，
//要增加元件或是設定LayoutManager等動作，
//要間接透過getContentPane()方法取得RootPane，
//才能在上面進行動作。
    frame.getContentPane().add(new MyCanvasDemo_2());
    frame.getContentPane().setBackground(Color.WHITE);

//設定視窗顯示在螢幕在的位置
    frame.setLocation(100, 100);

//讓視窗右上角的X圖示被按下之後，視窗會關閉
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.pack();
    frame.setVisible(true);
  }
}
