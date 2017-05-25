package sky4s.test.flanagan;

import flanagan.io.*;

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
public class DialogueTester {

  public static void main(String[] args) {
    FileChooser chooser = new FileChooser();
    chooser.setExtension("jpg,gif");
//    chooser.setExtension("gif");
    System.out.println(chooser.selectFile());
//    chooser.getFileName();
//    chooser.
  }
}
