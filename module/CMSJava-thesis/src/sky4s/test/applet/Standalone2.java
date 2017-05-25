package sky4s.test.applet;

import java.applet.*;

import java.awt.*;

// StandaloneApplet is an applet that runs either as
// an applet or a standalone application.  To run
// standalone, it provides a main method that creates
// a frame, then creates an instance of the applet and
// adds it to the frame.

public class Standalone2
    extends Applet {
  public void init() {
    add(new Button("Standalone Applet Button"));
  }

  public static void main(String[] args) throws Exception {
    Class c = Class.forName("Live");
    Applet myApplet = (Applet) c.newInstance();
    startApplet(myApplet, 300, 300, "", "./");
  }

  public static void example1(String args[]) throws Exception {
// Create the frame this applet will run in
    Frame appletFrame = new Frame("Some applet");

// The frame needs a layout manager, use the GridLayout to maximize
// the applet size to the frame.
    appletFrame.setLayout(new GridLayout(1, 0));

// Have to give the frame a size before it is visible
    appletFrame.resize(300, 100);

// Make the frame appear on the screen. You should make the frame appear
// before you call the applet's init method. On some Java implementations,
// some of the graphics information is not available until there is a frame.
// If your applet uses certain graphics functions like getGraphics() in the
// init method, it may fail unless there is a frame already created and
// showing.
    appletFrame.show();

// Create an instance of the applet
//      Applet myApplet = new Standalone2();
    Class c = Class.forName("Live");
    Applet myApplet = (Applet) c.newInstance();

// Add the applet to the frame
    appletFrame.add(myApplet);

// Now try to get an applet stub for this class.

    RunAppletStub stub = new RunAppletStub(appletFrame,
                                           myApplet, "standalone-applet",
                                           "http://localhost/");
    myApplet.setStub(stub);

// Initialize and start the applet
    myApplet.init();
    myApplet.start();

  }

  // Creates the frame, sets the stub, starts the applet

  public static void startApplet(Applet applet, int width, int height,
                                 String name, String startDir) {

    // Create the applet's frame
    Frame appletFrame = new Frame(name);

    // Allow room for the frame's borders
    appletFrame.resize(width + 10, height + 20);

    // Use a grid layout to maximize the applet's size
    appletFrame.setLayout(new GridLayout(1, 0));

    // Add the applet to the frame
    appletFrame.add(applet);

    // Show the frame, which makes sure all the graphics info is loaded
    // for the applet to use.

    appletFrame.show();

    // Create and set the stub
    AppletStub stub = new RunAppletStub(appletFrame, applet, name,
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
