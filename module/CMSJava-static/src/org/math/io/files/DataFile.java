package org.math.io.files;

import java.io.*;

/**
 * BSD License
 *
 * @author Yann RICHET
 */

public abstract class DataFile {

  protected File file;
  protected InputStream stream;

  protected DataFile(File f) {
    this.file = f;
    if (file.exists()) {
      try {
        this.stream = new FileInputStream(f);
      }
      catch (FileNotFoundException ex) {
        throw new IllegalArgumentException("File " + f.getName()
                                           + " is unreadable : " + ex.toString());
      }
    }
  }

  protected DataFile(InputStream stream) {
    this.stream = stream;
  }

  public static void copyFile(File in, File out) throws IOException {
    FileInputStream fis = new FileInputStream(in);
    FileOutputStream fos = new FileOutputStream(out);
    byte[] buf = new byte[1024];
    int i = 0;
    while ( (i = fis.read(buf)) != -1) {
      fos.write(buf, 0, i);
    }
    fis.close();
    fos.close();
  }

}
