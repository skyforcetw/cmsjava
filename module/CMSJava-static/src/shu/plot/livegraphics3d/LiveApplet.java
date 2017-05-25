package shu.plot.livegraphics3d;

import java.applet.*;

import java.awt.*;

import org.apache.commons.lang.*;
import livegraphics3d.*;
import shu.util.log.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 由於 LiveGraphics3D是顯示之後, 就不能再新增(吧?)
 * 所以再新增plot時, 要偵測是否已經顯示了, 如果顯示了就禁止新增
 *
 * 此外, 如果已經顯示, 又關掉了, 就可以回復新增的狀況.
 * 所以每一次顯示都要 初始化一個新的applet.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class LiveApplet {

  public static void main(String[] args) throws Exception {

//    Class c = Class.forName("Live");
//    Applet myApplet = (Applet) c.newInstance();
//    startApplet(myApplet, 600, 600, "apple name", "./");

    LiveApplet.setVersion(Version.v1_90);
//    LiveApplet.setVersion(Version.v_1_50);
    LiveApplet live = new LiveApplet("applet name", 600, 600);
    live.setInput("Graphics3D[{" +
                  "Line[{{0,0,0},{2,0,0}}]," +
                  "{RGBColor[1,0,0],PointSize[0.06],Point[{2,0,0}]}," +
                  "{RGBColor[0,0,1],PointSize[0.03],Point[{1,0,0}]}" +
                  "},    " +
                  "PlotRange->{{-2,2}, {-2,2}, {-1,1}}," +
                  "Axes -> True, AxesLabel -> {X, Y, Z},  Boxed -> False]"
        );
//    live.setInputFile("mesh.lg3d");
    live.setVisible();
  }

  private String title;
  private int width;
  private int height;
  private Frame frame;
  private final static String StartDir = "./";
  private Applet applet;

  public LiveApplet(String title, int width, int height) {
    this(getFrame(title, width, height), title, width, height);
  }

  /**
   * space to leave around the applet horizontally
   * @param hspace int
   */
  public void setHSpace(int hspace) {
    stub.setParameter("HSPACE", Integer.toString(hspace));
  }

  /**
   * space to leave around the applet vertically
   * @param vspace int
   */
  public void setVSpace(int vspace) {
    stub.setParameter("VSPACE", Integer.toString(vspace));
  }

  /**
   * background color in hexadecimal form
   * @param hexadecimal String
   */
  public void setBackgroundColor(String hexadecimal) {
    stub.setParameter("BGCOLOR", hexadecimal);
  }

  /**
   * file name of a JPEG or GIF image to be displayed as fixed background
   * @param filename String
   */
  public void setBackground(String filename) {
    stub.setParameter("BACKGROUND", filename);
  }

  /**
   * file name of a JPEG or GIF image to be displayed as a cylindrical background
   * @param filename String
   */
  public void setCylindricalBackground(String filename) {
    stub.setParameter("CYLINDRICAL_BACKGROUND", filename);
  }

  /**
   * file name of a JPEG or GIF image to be displayed as a spherical background
   * @param filename String
   */
  public void setSphericalBackground(String filename) {
    stub.setParameter("SPHERICAL_BACKGROUND", filename);
  }

  /**
   * zoom factor (interactively modified by pressing SHIFT and dragging vertically)
   * @param factor double
   */
  public void setMagnification(double factor) {
    stub.setParameter("MAGNIFICATION", Double.toString(factor));
  }

  /**
   * applet reaction initiated by dragging the mouse. (Default is rotating, use
   *  NONE in order to disable rotating, which is useful for 2D graphics.)
   * @param rotate boolean
   * @since 1.00
   */
  public void setMousrDragAction(boolean rotate) {
    if (!rotate) {
      stub.setParameter("MOUSE_DRAG_ACTION", "NONE");
    }
  }

  /**
   * color for edges of all points in hexadecimal form
   * @param hexadecimal String
   */
  public void setPointEdgeColor(String hexadecimal) {
    stub.setParameter("POINT_EDGE_COLOR", hexadecimal);
  }

  /**
   * string (enclosed in double-quotes) containing an InputForm of a Graphics3D
   *  or ShowAnimation object (double-quotes (") within this string should be
   *  replaced by pairs of single quotes (''))
   * @param input String
   */
  public void setInput(String input) {
    stub.setParameter("INPUT", input);
  }

  /**
   * determines which faces of polygons to draw. (Default is to draw both; the
   *  value FRONT will draw only front faces, the value BACK only back faces.)
   * @param front boolean
   * @since -1.40
   */
  public void setVisibleFaces(boolean front) {
    stub.setParameter("VISIBLE_FACES", front ? "FRONT" : "BACK");
  }

  /**
   * file name of a JPEG or GIF image to be displayed while other files are loaded and parsed
   * @param filename String
   * @since -0.70
   */
  public void setPreloadBackground(String filename) {
    stub.setParameter("PRELOAD_BACKGROUND", filename);
  }

  /**
   * name of a file containing an InputForm of a Graphics3D or ShowAnimation
   *  object. (This file may be inside a zip archive, see INPUT_ARCHIVE.)
   * @param inputFile String
   */
  public void setInputFile(String inputFile) {
    stub.setParameter("INPUT_FILE", inputFile);
  }

  public void setStereoDistance(double distance) {
    stub.setParameter("STEREO_DISTANCE", Double.toString(distance));
  }

  /**
   *
   * @param x double
   * @param y double
   * @since -1.55
   */
  public void setInitialRotation(double x, double y) {
    stub.setParameter("INITIAL_ROTATION",
                      Double.toString(x) + "," + Double.toString(y));
  }

  private RunAppletStub stub;
//  private boolean instance = false;
//  private boolean visible = false;

  public static enum Version {
    v_1_50, v1_90
  }

  protected static Version version = Version.v_1_50;
  public final static void setVersion(Version ver) {
    version = ver;
  }

  protected Class c;

  protected Applet init() throws Exception {
    Applet applet = null;
    switch (version) {
      case v_1_50:
        applet = new Live();
        break;
      case v1_90:
        c = Class.forName("Live");
        applet = (Applet) c.newInstance();
        break;
    }

//    frame.removeAll();
    // Add the applet to the frame
    frame.add(applet);

    // Create and set the stub
    stub = new RunAppletStub(frame, applet, title, StartDir);
    applet.setStub(stub);
//    GUIUtils.setGraphics2DAntiAlias( (Graphics2D) applet.getGraphics());
    // initialize the applet
//    applet.init();

    // Make sure the frame shows the applet
    frame.validate();
    return applet;
  }

  public void setVisible() {
    // Show the frame, which makes sure all the graphics info is loaded
    // for the applet to use.
    frame.setVisible(true);
    // Make sure the frame shows the applet
    frame.validate();

    if (initial == false) {
      initial = true;

      // initialize the applet
      applet.init();
      // Start up the applet
      applet.start();
//      test();
    }
  }

  private boolean initial = false;

  protected final static Frame getFrame(String title, int width, int height) {
    // Create the applet's frame
    Frame appletFrame = new Frame(title);

    // Allow room for the frame's borders
    appletFrame.setSize(width + 10, height + 20); ;

    // Use a grid layout to maximize the applet's size
    appletFrame.setLayout(new GridLayout(1, 0));
    return appletFrame;
  }

  public LiveApplet(Frame frame, String title, int width, int height) {
    this.frame = frame;
    this.title = title;
    this.width = width;
    this.height = height;
    try {
      this.applet = init();
    }
    catch (Exception ex) {
      Logger.log.error("", ex);
      throw new IllegalStateException("");
    }
    System.getProperties().setProperty("docbase",
                                       SystemUtils.getUserDir().toURI().
                                       toString());
  }

  /**
   *
   * @param applet Applet
   * @param width int
   * @param height int
   * @param name String
   * @param startDir String
   * @deprecated
   */
  public static void startApplet(Applet applet, int width, int height,
                                 String name, String startDir) {

    // Create the applet's frame
    Frame appletFrame = new Frame(name);

    // Allow room for the frame's borders
    appletFrame.setSize(width + 10, height + 20); ;

    // Use a grid layout to maximize the applet's size
    appletFrame.setLayout(new GridLayout(1, 0));

    // Add the applet to the frame
    appletFrame.add(applet);

    // Show the frame, which makes sure all the graphics info is loaded
    // for the applet to use.

    appletFrame.setVisible(true);

    // Create and set the stub
    RunAppletStub stub = new RunAppletStub(appletFrame, applet, name,
                                           startDir);
    applet.setStub(stub);
    // initialize the applet
    applet.init();

    // Make sure the frame shows the applet
    appletFrame.validate();

    // Start up the applet
    applet.start();

  }

}
