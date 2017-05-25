package shu.cms.applet.gradient.measure;

import java.awt.*;
import javax.swing.*;

import shu.cms.plot.*;
//import shu.plot.*;

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
public class SmoothMeasureInternalFrame
    extends JInternalFrame {

  protected BorderLayout borderLayout1 = new BorderLayout();
  private Plot2D plot2D;

  public SmoothMeasureInternalFrame(Plot2D plot2D) {
    super(plot2D.getTitle(), true, true, true);
    this.plot2D = plot2D;
    try {
      jbInit();
      myInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  private void myInit() throws Exception {
    JPanel panel = plot2D.getPlotPanel();
    getContentPane().add(panel);
    this.setSize(panel.getSize());
//    this.setSize(800,800);
//    this.pack();
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
  }

}
