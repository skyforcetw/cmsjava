package shu.ui;

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
public class JColorProgressBar {

  public final static JProgressBar getColorJProgressBar(int min, int max,
      Color color) {
    UIManager.put("ProgressBar.foreground", color);
    JProgressBar bar = new JProgressBar(min, max);
    return bar;
  }

}
