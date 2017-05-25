package shu.ui;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.commons.lang.*;
import shu.util.log.*;
import java.applet.Applet;

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
public final class GUIUtils {
    private GUIUtils() {

    }

    public final static void setUndecorated(JFrame jframe) {
        jframe.setUndecorated(true); //不要邊框
    }

    public final static void mouseWheelMoved(MouseWheelEvent e,
                                             JComboBox comboBox) {
        int length = comboBox.getItemCount();
        int index = comboBox.getSelectedIndex();
        System.out.println(length + " " + index);
        if (e.getWheelRotation() == 1) {
            //下
            index += 1;
            index = (index >= length) ? length - 1 : index;
        } else if (e.getWheelRotation() == -1) {
            //上
            index -= 1;
            index = (index < 0) ? 0 : index;
        }
        System.out.println(index);
        comboBox.setSelectedIndex(index);
    }

    public final static void setLookAndFeel() {
        if (SystemUtils.IS_JAVA_1_6) {
            try {
                UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.log.error("", ex);
            } catch (IllegalAccessException ex) {
                Logger.log.error("", ex);
            } catch (InstantiationException ex) {
                Logger.log.error("", ex);
            } catch (ClassNotFoundException ex) {
                Logger.log.error("", ex);
            }
        }
    }

    public final static void fullScreen(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocation(0, 0);
    }

    public final static void defaultScreen(JFrame frame) {
        halfScreen(frame);
        center(frame);
    }

    public final static void quarterScreen(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width / 2, screenSize.height / 2);
    }

    public final static void halfScreen(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) (screenSize.width * .7),
                      (int) (screenSize.height * .7));
    }

    public final static void center(JFrame frame) {
//    // Center the window
//    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    Dimension frameSize = frame.getSize();
//    if (frameSize.height > screenSize.height) {
//      frameSize.height = screenSize.height;
//    }
//    if (frameSize.width > screenSize.width) {
//      frameSize.width = screenSize.width;
//    }
//    frame.setLocation( (screenSize.width - frameSize.width) / 2,
//                      (screenSize.height - frameSize.height) / 2);
        frame.setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
        Properties ps = System.getProperties();
        for (Object o : ps.keySet()) {
            System.out.print(o + " ");
            String p = ps.getProperty((String) o);
            System.out.println(p);
        }
    }

    final protected static RenderingHints AALIAS = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    final protected static RenderingHints QUALITY = new RenderingHints(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
    public final static void setGraphics2DAntiAlias(Graphics2D g) {
        g.setRenderingHints(AALIAS);
        g.addRenderingHints(QUALITY);
    }

    public final static void runAsApplication(final JFrame frame,
                                              final boolean packFrame) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.
                                             getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                // Validate frames that have preset sizes
                // Pack frames that have useful preferred size info, e.g. from their layout
                if (packFrame) {
                    frame.pack();
                } else {
                    frame.validate();
                }

                // Center the window
                Dimension screenSize = Toolkit.getDefaultToolkit().
                                       getScreenSize();
                Dimension frameSize = frame.getSize();
                if (frameSize.height > screenSize.height) {
                    frameSize.height = screenSize.height;
                }
                if (frameSize.width > screenSize.width) {
                    frameSize.width = screenSize.width;
                }
                frame.setLocation((screenSize.width - frameSize.width) / 2,
                                  (screenSize.height - frameSize.height) / 2);
                frame.setVisible(true);
            }
        });

    }

    public final static JFrame startAppletAsApplicaiton(Applet applet,
            int width,
            int height) {
        JFrame frame = new JFrame();
        frame.add(applet);
        Dimension d = new Dimension(width, height);
        frame.setSize(d);
        applet.setSize(d);
        applet.setPreferredSize(d);
        applet.init();
        frame.pack();
        frame.setVisible(true);
        return frame;

    }

    public final static JFrame startAppletAsApplicaiton(Applet applet) {
        return startAppletAsApplicaiton(applet, 600, 600);
    }

}
