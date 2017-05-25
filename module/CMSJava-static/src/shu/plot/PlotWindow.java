package shu.plot;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import shu.ui.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class PlotWindow {
  protected JFrame frame;
  protected JFrame getJFrame() {
    return frame;
  }

  public void fullScreen() {
    GUIUtils.fullScreen(frame);
  }

  public void toBack() {
    frame.toBack();
  }

  public void setVisible(boolean b) {
    frame.setVisible(b);
  }

  public void setVisible() {
    this.setVisible(true);
  }

  public boolean isVisible() {
    return frame.isVisible();
  }

  public void dispose() {
    _dispose();
    this.frame.dispose();
    plotList.remove(this);
  }

  public abstract void _dispose();



  protected PlotWindow(String title, int width, int height) {
    frame = new JFrame(title);
    frame.setSize(width, height);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  protected PlotWindow(JFrame frame) {
    this.frame = frame;
  }

  public void setLocation(int x, int y) {
    frame.setLocation(x, y);
  }

  public void setSize(Dimension d) {
    frame.setSize(d);
  }

  public void setSize(int width, int height) {
    frame.setSize(width, height);
  }

  public Dimension getSize() {
    return frame.getSize();
  }

  public Point getLocation() {
    return frame.getLocation();
  }

  public void setContentPane(Container contentPane) {
    frame.setContentPane(contentPane);
  }

  public void setTitle(String title) {
    frame.setTitle(title);
  }

  public String getTitle() {
    return frame.getTitle();
  }





  public abstract void setBackground(Color bg);

  private final static List<PlotWindow> plotList = new LinkedList<PlotWindow> ();
  public final static int getPlotWindowCount() {
    return plotList.size();
  }

  protected final static void getInstance0(PlotWindow plot) {
    plotList.add(plot);
  }

  public abstract void toGraphicFile(File file);
}
