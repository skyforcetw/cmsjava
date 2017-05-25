package sky4s.test.imageio;

import java.io.*;
import javax.imageio.*;

import java.awt.image.*;

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
public class ImageIOTester {

  public static void main(String[] args) {
    File f = new File("jai.jpg");
    try {
      BufferedImage bi = ImageIO.read(f);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
