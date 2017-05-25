package sky4s.test;

import java.io.*;

/**
 * <p>Title: Colour Management System - thesis</p>
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
public class ResourceLoader {

  public static void main(String[] args) throws Exception {
    InputStream is = Class.class.getResourceAsStream("./d200.jpg");
    FileInputStream fis = new FileInputStream("./d200.jpg");
    System.out.println("");
  }
}
