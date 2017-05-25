package shu.util.source;

import java.io.*;
import java.util.*;

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
public class NewSourceFinder
    extends SourceDirCleaner {

  public void clean(File file) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (int x = 0; x < files.length; x++) {
        clean(files[x]);
      }
    }
    else if (after != null && file.getName().lastIndexOf(".java") != -1) {
      if (afterTime == null) {
        afterTime = after.getTime();
      }
      long lastModified = file.lastModified();
      Date d = new Date(lastModified);
      if (d.after(afterTime)) {
        System.out.println(file + " [" + d + "]");
      }
    }
  }

  private Date afterTime;
  private Calendar after;

  public static void main(String[] args) {
    NewSourceFinder finder = new NewSourceFinder();
    Calendar c = Calendar.getInstance();
    c.set(2008, 5, 17);
    finder.setAfter(c);
    finder.clean(new File(
        "../lib/Print/src/"));
  }

  public void setAfter(Calendar after) {
    this.after = after;
  }
}
