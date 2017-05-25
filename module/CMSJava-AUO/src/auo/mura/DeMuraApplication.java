package auo.mura;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DeMuraApplication {
  protected boolean packFrame = false;

  /**
   * Construct and show the application.
   */
  public DeMuraApplication() {
    DeMuraFrame frame = new DeMuraFrame();
    // Validate frames that have preset sizes
    // Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }

    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation( (screenSize.width - frameSize.width) / 2,
                      (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  /**
   * Application entry point.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
//    GraphicsEnvironment ge = GraphicsEnvironment.
//        getLocalGraphicsEnvironment();
//    GraphicsDevice[] gs = ge.getScreenDevices();
//    for (int j = 0; j < gs.length; j++) {
//      GraphicsDevice gd = gs[j];
//      GraphicsConfiguration[] gc =
//          gd.getConfigurations();
//      for (int i = 0; i < gc.length; i++) {
//        JFrame f = new
//            JFrame(gs[j].getDefaultConfiguration());
//        Canvas c = new Canvas(gc[i]);
//        Rectangle gcBounds = gc[i].getBounds();
//        int xoffs = gcBounds.x;
//        int yoffs = gcBounds.y;
//        f.getContentPane().add(c);
//        f.setLocation( (i * 50) + xoffs, (i * 60) + yoffs);
//        f.show();
//      }
//    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        new DeMuraApplication();
      }
    });
  }
}
