package shu.cms.colorformat.legend;

import java.io.*;
import java.util.*;

import shu.util.log.*;

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
public abstract class Parser {
  protected BufferedReader breader;
  protected boolean fromByteArray = false;
  protected String filename;

  public Parser(byte[] byteArray) {
    String content = new String(byteArray);
    StringReader sr = new StringReader(content);
    breader = new BufferedReader(sr);
    fromByteArray = true;
  }

  public Parser(InputStream in) {
    try {
      InputStreamReader isr = new InputStreamReader(in, "ms950");
      breader = new BufferedReader(isr);
    }
    catch (UnsupportedEncodingException ex) {
      Logger.log.error("", ex);
    }

  }

  public Parser(String filename) {
    this.filename = filename;
    try {
      InputStreamReader isr = new InputStreamReader(new FileInputStream(
          filename), "ms950");
      breader = new BufferedReader(isr);
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }
    catch (UnsupportedEncodingException ex) {
      Logger.log.error("", ex);
    }
  }

  public void close() {
    try {
      breader.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  public static final String DIVISION = "\t";

  /**
   * 將雙引號 " 去掉
   * ex: "SampleID" => SampleID
   * @param str String
   * @return String
   */
  protected static String trimDoubleQuote(String str) {
    return str.replace('"', ' ').trim();
  }

  /**
   * 將divide後的值當作value回傳
   * @param keyValue String
   * @param divide char
   * @return String
   */
  protected static String parseValue(String keyValue, char divide) {
    int index = keyValue.lastIndexOf(divide);
    if (index != -1) {
      return keyValue.substring(keyValue.indexOf(divide) + 1).trim();
    }
    else {
      return null;
    }
  }

  /**
   * 將DIVISION或者':'作分界,拆成key-value pair
   * 丟到map裡,然後回傳.
   * @param list ArrayList
   * @return Map
   */
  public static Map parseKeyValue(ArrayList list) {
    HashMap<String, String> keyValue = new HashMap<String, String> ();
    int size = list.size();
    for (int x = 0; x < size; x++) {
      String line = (String) list.get(x);
      String key, val;

      int divIndex = line.indexOf(DIVISION);
      if (divIndex != -1) {
        key = line.substring(0, divIndex).trim();
        val = trimDoubleQuote(line.substring(divIndex).trim());
        keyValue.put(key, val);
        continue;
      }

      int colonIndex = line.indexOf(":");
      if (colonIndex != -1) {
        //SpectraWin才有 :
        key = line.substring(0, colonIndex).trim();
        val = line.substring(colonIndex + 1).trim();
        keyValue.put(key, val);
        continue;
      }

      keyValue.put(line.trim(), "");

    }
    return keyValue;
  }

  protected abstract void _parsing();

  public void parsing() {
    try {
      _parsing();
      breader.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  protected static Object getValueAndRemove(Map map, Object key) {
    Object val = map.get(key);
    map.remove(key);
    return val;
  }

}
