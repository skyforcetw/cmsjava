package shu.cms.applet.gradient;

import java.awt.event.*;

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
public interface PixelSelectedListener {
  public void actionPerformed(int code);

  public void actionPerformed(int code, MouseEvent e);
}
