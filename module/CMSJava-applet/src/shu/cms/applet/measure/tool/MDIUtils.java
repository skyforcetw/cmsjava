package shu.cms.applet.measure.tool;

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
public class MDIUtils {
  public MDIUtils(JDesktopPane desktopPane) {
    this.desktopPane = desktopPane;
  }

  private final int maxFrameOfDesktopPane = 10;
  private int internalFrameCount = 0;
  public int getInternalFrameWidth() {
    return (int) (desktopPane.getWidth() /
                  ( (9. + maxFrameOfDesktopPane) / maxFrameOfDesktopPane));
  }

  public int getInternalFramHeight() {
    return (int) (desktopPane.getHeight() /
                  ( (9. + maxFrameOfDesktopPane) / maxFrameOfDesktopPane));
  }

  /**
   * 取得InternalFrame的座標
   * @return int[]
   */
  public int[] getInternalFrameBounds() {
    int x = (int) (desktopPane.getWidth() /
                   (9. + maxFrameOfDesktopPane) * internalFrameCount);
    int y = (int) (desktopPane.getHeight() /
                   (9. + maxFrameOfDesktopPane) * internalFrameCount);
    internalFrameCount = (internalFrameCount + 1) % 10;
    return new int[] {
        x, y};
  }

  private JDesktopPane desktopPane;
}
