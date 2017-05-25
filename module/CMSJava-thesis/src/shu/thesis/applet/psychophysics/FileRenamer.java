package shu.thesis.applet.psychophysics;

import java.io.*;

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
public class FileRenamer {

  public final static void addPicNumber(String dirname, String picNumber) {
    File dir = new File(dirname);
    for (File f : dir.listFiles()) {
      String newFilename = f.getParent() + "/" + picNumber + "_" + f.getName();
      if (!f.renameTo(new File(newFilename))) {
        throw new IllegalStateException("!f.renameTo(new File(newFilename)");
      }
    }
  }

  public static void main(String[] args) {
    String dir = "C:/Documents and Settings/skyforce/орн▒/temp";
    addPicNumber(dir, "06");
  }
}
