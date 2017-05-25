package shu.cms.colorformat.legend;

import java.io.*;
import java.util.*;

import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來拆解Profilemaker產生的測量資料檔案格式
 * 也可拆解TestChart檔案
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GretagMacbethAsciiParser
    extends Parser {
  public static final String BEGIN_DATA_FORMAT = "BEGIN_DATA_FORMAT";
  public static final String BEGIN_DATA = "BEGIN_DATA";
  public static final String BEGIN = "BEGIN_";
  public static final String END = "END_";
  public static final String END_DATA = "END_DATA";

  protected GretagMacbethAsciiFile.Header header;
  protected ArrayList dataFormat;
  protected GretagMacbethAsciiFile.DataSet dataSet;

  public GretagMacbethAsciiFile getGretagMacbethAsciiFile() {
    parsing();
    GretagMacbethAsciiFile file = new GretagMacbethAsciiFile();
    file.header = this.header;
    file.dataFormat = this.dataFormat;

    file.dataSet = this.dataSet;
    file.dataSet.setMotherFile(file);

    file.testchart = this.testchart;
    file.emission = this.emission;
    return file;
  }

  public GretagMacbethAsciiParser(String filename) {
    super(filename);
  }

  public GretagMacbethAsciiParser(byte[] byteArray) {
    super(byteArray);
  }

  /**
   * 判斷落在Header或DataFormat
   * @param section ArrayList
   */
  protected void judge(ArrayList section) {
    String line = ( (String) section.get(0)).trim();
    if (line.startsWith(BEGIN)) {
      if (line.equals(BEGIN_DATA_FORMAT)) {
        //資料格式區段
        dataFormat = parseDataFormat(section);
      }
      else {
      }
    }
    else {
      //header區段
      header = parseHeader(section);
    }
  }

  /**
   * 拆解資料格式區段
   * @param section ArrayList
   * @return ArrayList
   */
  protected ArrayList parseDataFormat(ArrayList section) {
    ArrayList<String> vec = new ArrayList<String> ();
    String dataFmt = (String) section.get(1);
    StringTokenizer st = new StringTokenizer(dataFmt);

    while (st.hasMoreTokens()) {
      vec.add(st.nextToken());
    }

    return vec;
  }

  protected boolean emission = false;
  protected boolean testchart = false;

  protected int parseInt(String str) {
    if (str == null) {
      return 0;
    }
    else {
      return Integer.parseInt(str);
    }
  }

  /**
   * 拆解標頭區段
   * @param section ArrayList
   * @return GretagMacbethAsciiFile.Header
   */
  protected GretagMacbethAsciiFile.Header parseHeader(ArrayList section) {
    GretagMacbethAsciiFile.Header h = new GretagMacbethAsciiFile.Header();
    Map keyVal = this.parseKeyValue(section);

    if (keyVal.containsKey("Logo TestChart f\uFFFDr Farbmonitore") ||
        keyVal.containsKey("Logo TestChart Designed By Lo &Thomas") ||
        keyVal.containsKey("Logo TestChart") ||
        keyVal.containsKey("Logo TestChart f\uEDB0 Farbmonitore")) {
      this.testchart = true;
    }

    h.lgorowlength = parseInt( (String) getValueAndRemove(keyVal,
        "LGOROWLENGTH"));
    h.measurement_mode = (String) getValueAndRemove(keyVal, "Measurement_mode");
    h.created = (String) getValueAndRemove(keyVal, "CREATED");
    h.instrumentation = (String) getValueAndRemove(keyVal, "INSTRUMENTATION");

    h.measurement_source = (String) getValueAndRemove(keyVal,
        "MEASUREMENT_SOURCE");
    if (h.measurement_source != null &&
        h.measurement_source.startsWith("Emission")) {
      this.emission = true;
    }

    h.illumination_name = (String) getValueAndRemove(keyVal,
        "ILLUMINATION_NAME");
    h.observerAngle = parseInt( (String) getValueAndRemove(keyVal,
        "OBSERVER_ANGLE"));
    h.numberOfFields = parseInt( (String) getValueAndRemove(keyVal,
        "NUMBER_OF_FIELDS"));

    return h;
  }

  /**
   * 將value值拆解出來
   * ex: KEYWORD	"SampleID"
   * 的"SampleID"
   *
   * @param keyValue String
   * @return String
   */
  protected static String getValue(String keyValue) {
    return trimDoubleQuote(keyValue.substring(keyValue.indexOf(DIVISION)).trim());
  }

  public void _parsing() {
    ArrayList<String> sec = new ArrayList<String> ();
    try {
      while (breader.ready()) {
        String line = breader.readLine();
        if (line == null) {
          break;
        }
        if (line.startsWith(BEGIN)) {
          judge(sec);
          sec = new ArrayList<String> ();
        }

        if (! (fromByteArray && line.startsWith(END_DATA))) {
          sec.add(line);
        }

      }

      //data通常被擺在最後
      dataSet = parseData(sec);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

  }

  public static void main(String[] args) {
    GretagMacbethAsciiParser profilemakerparser = new GretagMacbethAsciiParser(
//        "Reference Files/Monitor/LCD Monitor Reference Lo_125.txt");
//        "Reference Files\\Monitor\\LCD Reference_PLCC.txt");
        "2.5BG.txt");
//    profilemakerparser.parsing();
//    System.out.println(profilemakerparser.emission);
//    System.out.println(profilemakerparser.testchart);
    GretagMacbethAsciiFile file = profilemakerparser.getGretagMacbethAsciiFile();
    System.out.println(file.getDataSet());

    /*int count = 0;
         GretagMacbethAsciiFile.DataSet dataSet = file.getDataSet();
         for (int x = 0; x < dataSet.size(); x++) {
      GretagMacbethAsciiFile.TestChartData data = dataSet.getTestChartData(x);
      int[] rgb = data.RGB;
      if (rgb[0] == 0 || rgb[1] == 0 || rgb[2] == 0) {
        count++;
        System.out.println(data.sampleID + " " + data.sampleName);
      }
         }
         System.out.println(count);*/

  }

  /**
   * 拆解資料區段
   * @param section ArrayList
   * @return GretagMacbethAsciiFile.DataSets
   */
  protected GretagMacbethAsciiFile.DataSet parseData(ArrayList section) {
    GretagMacbethAsciiFile.DataSet dataSet = new GretagMacbethAsciiFile.
        DataSet();
    int size = section.size();

    for (int x = 1; x < size - 1; x++) {
      ArrayList<String> data = new ArrayList<String> ();
      String line = (String) section.get(x);
      StringTokenizer st = new StringTokenizer(line);
      while (st.hasMoreTokens()) {
        data.add(st.nextToken());
      }
      dataSet.addData(data);
    }
//    dataSet.numberOfFields = this.header.numberOfFields;
    return dataSet;
  }

}
