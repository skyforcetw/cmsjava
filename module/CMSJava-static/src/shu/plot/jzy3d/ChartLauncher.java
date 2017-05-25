package shu.plot.jzy3d;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import org.jzy3d.chart.*;
import org.jzy3d.chart.controllers.mouse.*;
import org.jzy3d.chart.controllers.thread.*;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * <p>Title: Colour Management System - static</p>
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
public class ChartLauncher {
  public static JFrame openChart(Chart chart) {
    return openChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d", true);
  }

  public static JFrame openChart(Chart chart, String title) {
    return openChart(chart, new Rectangle(0, 0, 800, 600), title, true);
  }

  public static JFrame openChart(Chart chart, Rectangle bounds,
                                 String title) {
    return openChart(chart, bounds, title, true);
  }

  public static JFrame openChart(Chart chart, Rectangle bounds,
                                 String title,
                                 boolean
                                 allowSlaveThreadOnDoubleClick) {
    return openChart(chart, bounds, title, allowSlaveThreadOnDoubleClick, false);
  }

  public static JFrame openChart(final Chart chart,
                                 Rectangle bounds,
                                 final String title,
                                 boolean
                                 allowSlaveThreadOnDoubleClick,
                                 boolean startThreadImmediatly) {
    // Setup chart controllers and listeners
    ChartMouseController mouse = new ChartMouseController();
    /*mouse.addControllerEventListener(new ControllerEventListener(){
            public void controllerEventFired(ControllerEvent e) {
                    if(e.getType()==ControllerType.ROTATE){
                            //System.out.println("Mouse[ROTATE]:" + (Coord3d)e.getValue());
                    }
            }
                       });*/
    chart.addController(mouse);

    if (allowSlaveThreadOnDoubleClick) {
      ChartThreadController thread = new ChartThreadController();
      mouse.addSlaveThreadController(thread);
      chart.addController(thread);
      if (startThreadImmediatly) {
        thread.start();
      }
    }
    // trigger screenshot on 's' letter
    chart.getCanvas().addKeyListener(new KeyListener() {
//      @Override
      public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
          case 's':
            try {
              org.jzy3d.ui.ChartLauncher.screenshot(chart,
                  "./data/screenshots/" + title + ".png");
            }
            catch (IOException e1) {
              e1.printStackTrace();
            }
            default:
              break;
        }
      }

//      @Override
      public void keyReleased(KeyEvent e) {
      }

//      @Override
      public void keyPressed(KeyEvent e) {
      }
    });
    chart.render();

    // Open it in a window
    JFrame frame = new FrameSwing(chart, bounds, title);
    //thread.start();
    return frame;
  }

  /*******************************************************/
  static class FrameSwing
      extends JFrame {
    public FrameSwing(Chart chart, Rectangle bounds, String title) {
      this.chart = chart;

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Container contentPane = getContentPane();
      BorderLayout layout = new BorderLayout();
      contentPane.setLayout(layout);

      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          FrameSwing.this.remove( (java.awt.Component) FrameSwing.this.chart.
                                 getCanvas());
          FrameSwing.this.chart.dispose();
          FrameSwing.this.chart = null;
          FrameSwing.this.dispose();
        }
      });

      JPanel panel3d = new JPanel();
      panel3d.setLayout(new java.awt.BorderLayout());
      panel3d.add( (JComponent) chart.getCanvas());

      contentPane.add( (JComponent) chart.getCanvas(), BorderLayout.CENTER);
//      setVisible(true);
//      setTitle(title + "[Swing]");
      setTitle(title);
      pack();
      setBounds(bounds);
    }

    private Chart chart;
    private static final long serialVersionUID = 6474157681794629264L;
  }

  /*******************************************************/


}
