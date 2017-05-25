package shu.io.ascii;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ASCIIFileFormat {

  protected String filename;
  protected Map<String, LineObject> map;
  protected LineObject[] lineObjectArray;

  public String toString() {
    int size = lineObjectArray.length;
    StringBuilder builder = new StringBuilder();
    for (int x = 0; x < size; x++) {
      builder.append(lineObjectArray[x].line);
      builder.append('\n');
    }
    return builder.toString();
  }

  protected ASCIIFileFormat(String filename,
                            ArrayList < ASCIIFileFormat.LineObject >
                            lineObjectArrayList) {
    this.filename = filename;
    initMap(lineObjectArrayList);
  }

  public static class LineObject {
    public LineObject(int lineNumber, String line, String firstField,
                      String[] stringArray) {
      this.lineNumber = lineNumber;
      this.line = line;
      this.firstField = firstField;
      this.stringArray = stringArray;
    }

    public int lineNumber;
    public String line;
    public String firstField;
    public String[] stringArray;
  }

  /**
   * 可依照該行的第一組字元,檢索line
   * @param firstField String
   * @return LineObject
   */
  public LineObject getLine(String firstField) {
    return map.get(firstField);
  }

  protected void initMap(ArrayList < ASCIIFileFormat.LineObject >
                         lineObjectArrayList) {
    map = new HashMap<String, LineObject> ();
    int size = lineObjectArrayList.size();
    lineObjectArray = new LineObject[size];
    for (int x = 0; x < size; x++) {
      LineObject lo = lineObjectArrayList.get(x);
      lineObjectArray[x] = lo;
      if (!map.containsKey(lo.firstField)) {
        map.put(lo.firstField, lo);
      }
    }
  }

  public LineObject getLine(int index) {
    return lineObjectArray[index];
  }

  public int size() {
    return lineObjectArray.length;
  }

  public String getFilename() {
    return filename;
  }

  public static void main(String[] args) {
    ASCIIFileFormatParser parser = new ASCIIFileFormatParser(
        "C://Documents and Settings//skyforce//My Documents//Case//宏瀨科技//LED.ProcSpec");
    ASCIIFileFormat format = null;
    try {
      format = parser.parse();
      int size = format.size();
      for (int x = 0; x < size; x++) {
        LineObject lo = format.getLine(x);
        String[] stringArray = lo.stringArray;
        for (String s : stringArray) {
          System.out.print(s + " ");
        }
        System.out.println("");
      }

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    System.out.println(format);
  }
}
