package shu.jai;

import java.io.*;
import javax.media.jai.*;

import java.awt.image.*;

import com.sun.media.jai.codec.*;
import com.sun.media.jai.widget.*;

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
public final class JAIUtils {
  private JAIUtils() {

  }

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
