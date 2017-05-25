package shu.cms.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

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
public class DitherCanvas
    extends JComponent {
  private BufferedImage img;

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (img != null) {
      int w = getSize().width;
      int h = getSize().height;
      g.drawImage(img, 0, 0, w, h, this);
    }
  }

  public void update(Graphics g) {
    paint(g);
  }

  public BufferedImage getBufferedImage() {
    return img;
  }

  public void setBufferedImage(BufferedImage img) {
    this.img = img;
    repaint();
  }
}
