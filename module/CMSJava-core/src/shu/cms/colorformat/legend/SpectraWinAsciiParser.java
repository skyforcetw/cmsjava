package shu.cms.colorformat.legend;

import java.io.*;
import java.util.*;

import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將SpectraWin軟體產生的.txt資料檔對應到SpectraWinAsciiFile物件
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SpectraWinAsciiParser
    extends Parser {
  public static final String SEC_HEADER = "[HEADER]";
  public static final String SEC_SPECTRAL = "[SPECTRAL]";
  public static final String SEC_CALCULATED = "[CALCULATED]";
  public static final String END_OF_SECTION = "[END OF SECTION]";

  protected SpectraWinAsciiFile.Header header;
  protected SpectraWinAsciiFile.Spectral spectral;
  protected SpectraWinAsciiFile.Calculated calculated;

  protected SpectraWinAsciiFile file;
  public SpectraWinAsciiFile getSpectraWinAsciiFile() {
    if (file == null) {
      parsing();
      file = new SpectraWinAsciiFile();
      file.header = header;
      file.spectral = spectral;
      file.calculated = calculated;
    }
    return file;
  }

  public SpectraWinAsciiParser(String filename) {
    super(filename);
  }

  public final static boolean DECODING_STRING = true;

  /**
   *
   * @param str String
   * @return String
   */
  protected static String decodingString(String str) {
    if (DECODING_STRING) {
      try {
        return new String(str.getBytes("ISO-8859-1"), "ms950");
//        return new String(str.getBytes("" ), "ms950");
      }
      catch (UnsupportedEncodingException ex) {
        Logger.log.error("", ex);
        return null;
      }
    }
    else {
      return str;
    }

  }

  protected SpectraWinAsciiFile.Header parseHeader(ArrayList section) {
    SpectraWinAsciiFile.Header h = new SpectraWinAsciiFile.Header();

    Map keyVal = this.parseKeyValue(section);
    h.title = (String) getValueAndRemove(keyVal, "Title");
    h.description = (String) getValueAndRemove(keyVal, "Description");
    h.modelNum = (String) getValueAndRemove(keyVal, "Model Num.");
//    h.dateTime = (String) getValueAndRemove(keyVal, "Date / Time");
    h.dateTime = decodingString( (String) getValueAndRemove(keyVal,
        "Date / Time"));

    h.info = keyVal;

    return h;
  }

  protected SpectraWinAsciiFile.Spectral parseSpectral(ArrayList section) {
    SpectraWinAsciiFile.Spectral s = new SpectraWinAsciiFile.Spectral();
    int size = section.size();

    for (int x = 1; x < size; x++) {
      String line = (String) section.get(x);
      switch (x) {
        case 1:
          s.start = (int) Double.parseDouble(line);
          break;
        case 2:
          s.end = (int) Double.parseDouble(line);
          break;
        case 3:
          s.interval = (int) Double.parseDouble(line);
          break;
        default:
          s.info.add(Double.parseDouble(line));
          break;
      }
    }
    return s;
  }

  protected SpectraWinAsciiFile.Calculated parseCalculated(ArrayList section) {
    SpectraWinAsciiFile.Calculated c = new SpectraWinAsciiFile.Calculated();

    //格式太亂,parseKeyValue難以處理,所以只好用if else
    int size = section.size();
    for (int x = 1; x < size; x++) {
      String line = ( (String) section.get(x)); //.trim();
      String val = parseValue(line, ':');
      if (val == null) {
        val = parseValue(line, DIVISION.charAt(0));
      }
      if (val == null || val.length() == 0) {
        continue;
      }

      if (line.startsWith("L*")) {
        c._Lab[0] = Double.parseDouble(val);
      }
      else if (line.startsWith("a*")) {
        c._Lab[1] = Double.parseDouble(val);
      }
      else if (line.startsWith("b*")) {
        c._Lab[2] = Double.parseDouble(val);
      }
      else if (line.startsWith("C*")) {
        c.ch[0] = Double.parseDouble(val);
      }
      else if (line.startsWith("H*")) {
        c.ch[1] = Double.parseDouble(val);
      }
      else if (line.startsWith("Luminance")) {
        c.luminance[0] = Double.parseDouble(val);
      }
      else if (line.startsWith(" 	")) {
        c.luminance[1] = Double.parseDouble(line.trim());
      }
      else if (line.startsWith("X")) {
        c.X = Double.parseDouble(val);
      }
      else if (line.startsWith("Z")) {
        c.Z = Double.parseDouble(val);
      }
      else if (line.startsWith("Radiance")) {
        c.radiance = Double.parseDouble(val);
      }
      else if (line.startsWith("Photon Rad")) {
        c.photonRad = Double.parseDouble(val);
      }
      else if (line.startsWith("CCT")) {
        c.CCT = Integer.parseInt(val);
      }
      else if (line.startsWith("Illuminant")) {
        c.illuminant = val;
      }
      else if (line.startsWith("x")) {
        c.x = Double.parseDouble(val);
      }
      else if (line.startsWith("y")) {
        c.y = Double.parseDouble(val);
      }
      else if (line.equals("")) {
        //沒東西就略過
      }
      else {
        c.info.add(line.trim());
      }
    }
    return c;
  }

  public static void main(String[] args) throws Exception {
    SpectraWinAsciiParser pr650parser = new SpectraWinAsciiParser("tmp/A1.txt");
    System.out.println(pr650parser.getSpectraWinAsciiFile());
  }

  protected void judge(ArrayList section) {
    String line = (String) section.get(0);
    if (line.equals(SEC_HEADER)) {
      header = parseHeader(section);
    }
    else if (line.equals(SEC_SPECTRAL)) {
      spectral = parseSpectral(section);
    }
    else if (line.equals(SEC_CALCULATED)) {
      calculated = parseCalculated(section);
    }
  }

  public void _parsing() {
    ArrayList<String> section = new ArrayList<String> ();
    try {
      while (breader.ready()) {
        String line = breader.readLine();
        if (line == null) {
          break;
        }
        if (line.equals(END_OF_SECTION)) {
          //代表到了整個文件的END
          if (section.size() == 0) {
            break;
          }

          judge(section);
          section = new ArrayList<String> ();
          continue;
        }
        section.add(line);
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

}
