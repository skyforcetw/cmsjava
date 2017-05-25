package shu.util.source;

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
public class SourceDirCleaner {
  private boolean svnClean = true;
  private boolean trashFileClean = false;
  public void clean(File file) {
    if (file.isDirectory()) {
      if (svnClean && file.getName().equals(".svn")) {
        file.deleteOnExit();
        deleteDir(file);
      }
      else {
        File[] files = file.listFiles();
        for (int x = 0; x < files.length; x++) {
          clean(files[x]);
        }
      }
    }
    else if (trashFileClean) {
      //========================================================================
      // ÀÉ®×§R°£
      //========================================================================
      for (String extFilename : new String[] {".html", ".jbx"}) {
        if (file.getName().lastIndexOf(extFilename) != -1) {
          System.out.println("Delete file: " + file.getAbsolutePath());
          file.deleteOnExit();
        }
      }
      //========================================================================
    }

  }

  protected boolean deleteDir(File dir) {
    File[] files = dir.listFiles();
    boolean check = true;
    for (int x = 0; x < files.length; x++) {
      if (files[x].isDirectory()) {
        deleteDir(files[x]);
      }
      else {
        check = check && files[x].delete();
      }
    }
    return check && dir.delete();
  }

  public static void main(String[] args) {
    SourceDirCleaner svncleaner = new SourceDirCleaner();
    svncleaner.clean(new File(
        "D:\\skyforce\\CMSJava2\\module\\CMSJava-core\\src"));
//        "C:/Documents and Settings/root/workspace/½Æ»s -CMSJava/"));
//        "C:/Documents and Settings/root/workspace/iccess"));
  }
}
