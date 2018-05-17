package shu.cms.dc;

import java.io.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將相機拍攝下來的raw+jpg 流水號檔名轉成系統可以辨識的檔名
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class ImageFilenameCorrector {

  public static void main(String[] args) {
    correcting("C:\\Documents and Settings\\root\\workspace\\CMSJava\\workdir\\Camera Files\\D70\\F");
  }

  public final static void correcting(String path) {
    File dir = new File(path);
    File[] files = dir.listFiles(new Filter());

    if (files.length != correctNames.length * 2) {
      System.out.println("檔案數目不符合59個");
      return;
    }

    for (int x = 0; x < correctNames.length * 2; x++) {
      File file = files[x];
      String filename = file.getName();
      String endFilename = filename.substring(filename.lastIndexOf('.'));
      String newFilename = correctNames[x / 2] + endFilename;
      File newFile = new File(file.getParent(), newFilename);

      if (!file.renameTo(newFile)) {
        throw new IllegalStateException("!file.renameTo(newFile)");
      }
    }
  }

  private final static String[] correctNames = {
      //Kodak Gray Card
      "GrayCard",
      //ColorChecker Series
      "CC24",
      "CCDC",
      "CCSG",
      //Munsell Color Book
      "2.5R",
      "5R",
      "7.5R",
      "10R",
      "2.5YR",
      "5YR",
      "7.5YR",
      "10YR",
      "2.5Y",
      "5Y",
      "7.5Y",
      "10Y",
      "2.5GY",
      "5GY",
      "7.5GY",
      "10GY",
      "2.5G",
      "5G",
      "7.5G",
      "10G",
      "R-Y",
      "GY-G",
      "R-YR",
      "Y-G",
      "2.5BG",
      "5BG",
      "7.5BG",
      "10BG",
      "2.5B",
      "5B",
      "7.5B",
      "10B",
      "2.5PB",
      "5PB",
      "7.5PB",
      "10PB",
      "2.5P",
      "5P",
      "7.5P",
      "10P",
      "2.5RP",
      "5RP",
      "7.5RP",
      "10RP",
      "BG-PB",
      "P-RP",
      "PB-RP",
      //GATF Quality Control Photographs
      "Kids",
      "Portrait",
      "Grays",
      "Fruit",
      "Couch",
      "Wedding",
      "Bridge",
      //Agfa IT8
      "IT8"
  };

  protected static class Filter
      implements FilenameFilter {
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be included in
     *   the file list; <code>false</code> otherwise.
     */
    public boolean accept(File dir, String name) {
      if (name.endsWith("JPG") || name.endsWith("NEF")) {
        return true;
      }
      else {
        return false;
      }
    }

  }

}
