package shu.cms.colorformat.ascii;

import java.io.*;
import java.util.*;

import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 可以解析Ascii的檔案格式
 * 基本單位是以Line為單位
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ASCIIFileFormatParser {
  protected LineNumberReader lineReader;
  protected String filename;

  public ASCIIFileFormatParser(Reader reader) {
    lineReader = new LineNumberReader(reader);
  }

  public ASCIIFileFormatParser(String filename) {
    this.filename = filename;
    try {
      InputStreamReader isr = new InputStreamReader(new FileInputStream(
          filename), "ms950");
      lineReader = new LineNumberReader(isr);
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }
    catch (UnsupportedEncodingException ex) {
      Logger.log.error("", ex);
    }
  }

  protected ArrayList<ASCIIFileFormat.LineObject> lineObjectArrayList;

  /**
   * 將line解析成token
   * @param line String
   * @return String[]
   */
  public static String[] tokenizerToString(String line) {

    StringTokenizer tokenizer = new StringTokenizer(line);
    int tokens = tokenizer.countTokens();
    String[] stringArray = new String[tokens];
    int index = 0;
    while (tokenizer.hasMoreTokens()) {
      stringArray[index++] = tokenizer.nextToken();
    }

    return stringArray;
  }

  public static ASCIIFileFormat.LineObject toLineObject(String line) {
    String[] strArray = tokenizerToString(line);
    if (strArray.length == 0) {
      return new ASCIIFileFormat.LineObject( -1, line,
                                            "", strArray);
    }
    else {
      return new ASCIIFileFormat.LineObject( -1, line,
                                            strArray[0], strArray);
    }

  }

  public ASCIIFileFormat parse() throws IOException {
    lineObjectArrayList = new ArrayList<ASCIIFileFormat.LineObject> ();
    while (lineReader.ready()) {
      String line = lineReader.readLine();
      if (line == null) {
        break;
      }
      int lineNumber = lineReader.getLineNumber();
      ASCIIFileFormat.LineObject lo = toLineObject(line);
      lo.lineNumber = lineNumber;
      lineObjectArrayList.add(lo);
    }
    return new ASCIIFileFormat(filename, lineObjectArrayList);
  }

  public void close() {
    try {
      lineReader.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  public static void main(String[] args) {
  }
}
