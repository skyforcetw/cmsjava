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

//�e�@�����u,�q�y��(100,100)��(150,150)
    g.drawLine(100, 100, 150, 150);
  }

//�갵�ۭq����ɡA
//�̦n�мg�ogetPreferredSize�BgetMaximumSize�BgetMinimumSize�T�Ӥ�k
//�]���\�hLayoutManager���i��ѳo�T�Ӥ�k�ӨM�w�����󪺤j�p
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
//�]�w�������~�[
    JFrame.setDefaultLookAndFeelDecorated(true);

    JFrame frame = new JFrame("MyCanvasDemo_2");

//�bSwing��JFrame���󤤡A
//�n�W�[����άO�]�wLayoutManager���ʧ@�A
//�n�����z�LgetContentPane()��k���oRootPane�A
//�~��b�W���i��ʧ@�C
    frame.getContentPane().add(new MyCanvasDemo_2());
    frame.getContentPane().setBackground(Color.WHITE);

//�]�w������ܦb�ù��b����m
    frame.setLocation(100, 100);

//�������k�W����X�ϥܳQ���U����A�����|����
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.pack();
    frame.setVisible(true);
  }
}
