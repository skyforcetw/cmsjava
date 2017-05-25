package shu.thesis.applet.psychophysics;

import java.io.*;
import javax.media.jai.*;

import java.awt.image.*;

import com.sun.media.jai.codec.*;
import com.sun.media.jai.widget.*;

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
public class JAIUtils {
  public final static RenderedOp getRenderedOp(String filename) throws
      IOException {
    FileSeekableStream stream = new FileSeekableStream(filename);
    RenderedOp image1 = JAI.create("stream", stream);
    return image1;
  }

  public final static DisplayJAI getDisplayJAI(RenderedImage im) {
    DisplayJAI display = new DisplayJAI(im);
    return display;
  }
}
