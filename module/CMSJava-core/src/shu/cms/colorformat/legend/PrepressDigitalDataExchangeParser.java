package shu.cms.colorformat.legend;

import java.io.*;
import java.util.*;

import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.trans.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * SOCS資料格式的解析器
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PrepressDigitalDataExchangeParser
    extends GretagMacbethAsciiParser {
  public PrepressDigitalDataExchangeParser(String filename) {
    super(filename);
  }

  public static final String BEGIN_RECORD = "SPECTRUM";
  protected boolean parseHeader = false;
  protected boolean parseFormat = false;
  protected String sampleID;
  protected String sampleName;
  protected PrepressDigitalDataExchangeFile.Header pddeHeader;
  protected PrepressDigitalDataExchangeFile.DataSet pddeDataSet;

  protected void parseSample(ArrayList section) {
//    System.out.println(section);
    sampleID = ( (String) section.get(2)).substring(10).replace('"', ' ').trim();
    sampleName = ( (String) section.get(3)).substring(10).replace('"', ' ').
        trim();
  }

  public void _parsing() {
    ArrayList<String> sec = new ArrayList<String> ();

    try {
      while (breader.ready()) {
        String line = breader.readLine();
        if (line == null) {
          break;
        }
        line = line.trim();

        if (line.startsWith(BEGIN_RECORD) && sec.size() != 0) {
          pddeDataSet = parseData(sec, pddeDataSet);
        }
        else if (line.startsWith(BEGIN)) {
          judge(sec);
          sec = new ArrayList<String> ();
        }

        if (! (line.startsWith(END_DATA) || line.startsWith("#"))) {
          sec.add(line);
        }

      }
      if (sec.size() != 0) {
//        System.out.println(sec);
        pddeDataSet = parseData(sec, pddeDataSet);
      }

    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

  }

  /**
   *
   * @param section ArrayList
   * @param dataSet DataSet
   * @return DataSet
   */
  protected PrepressDigitalDataExchangeFile.DataSet parseData(ArrayList
      section,
      PrepressDigitalDataExchangeFile.DataSet dataSet) {
    if (dataSet == null) {
      dataSet = new PrepressDigitalDataExchangeFile.DataSet();
    }

    int size = section.size();
    ArrayList<String> data = new ArrayList<String> ();
    data.add(sampleID);
    data.add(sampleName);

    for (int x = 1; x < size; x++) {
      String line = (String) section.get(x);
      int index = line.indexOf("\t");
      data.add(line.substring(index).trim());
    }
    dataSet.addData(data);

    return dataSet;
  }

  /**
   * 判斷落在Header或DataFormat
   * @param section ArrayList
   */
  protected void judge(ArrayList section) {
    String line = ( (String) section.get(0)).trim();
    if (line.startsWith(BEGIN)) {
      if (line.equals(BEGIN_DATA_FORMAT) && !parseFormat) {
        //資料格式區段
        dataFormat = parseDataFormat(section);
        parseFormat = true;
      }
      if (line.equals(BEGIN_DATA_FORMAT)) {
        parseSample(section);
      }
    }
    else
    if (!parseHeader) {
      //header區段
      pddeHeader = parseHeader(section);
      parseHeader = true;
    }
  }

  /**
   * 拆解標頭區段
   * @param section ArrayList
   * @return GretagMacbethAsciiFile.Header
   */
  protected PrepressDigitalDataExchangeFile.Header parseHeader(ArrayList
      section) {
    PrepressDigitalDataExchangeFile.Header h = new
        PrepressDigitalDataExchangeFile.Header();
    Map keyVal = this.parseKeyValue(section);

//    h.lgorowlength = parseInt( (String) getValueAndRemove(keyVal,
//        "LGOROWLENGTH"));
    h.measurement_mode = (String) getValueAndRemove(keyVal, "Measurement_mode");
    h.created = (String) getValueAndRemove(keyVal, "CREATED");
    h.originator = (String) getValueAndRemove(keyVal, "ORIGINATOR");
    h.descriptor = (String) getValueAndRemove(keyVal, "DESCRIPTOR");

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

  public PrepressDigitalDataExchangeFile getPrepressDigitalDataExchangeFile() {
    parsing();
    PrepressDigitalDataExchangeFile file = new PrepressDigitalDataExchangeFile();
    file.pddeHeader = this.pddeHeader;
    file.dataFormat = this.dataFormat;

    file.pddeDataSet = this.pddeDataSet;
    file.pddeDataSet.setMotherFile(file);

    file.testchart = this.testchart;
    file.emission = this.emission;
    file.filename = this.filename.substring(filename.lastIndexOf("\\") + 1);
    return file;
  }

  public static void main(String[] args) {
    PrepressDigitalDataExchangeParser parser = new
        PrepressDigitalDataExchangeParser(
            "Reference Files/SOCS/int/paints/pa_a.int");
    PrepressDigitalDataExchangeFile file = parser.
        getPrepressDigitalDataExchangeFile();
//    System.out.println(file.getDataSet());
    CXF cxf = CxFTransformer.PDDEToCxF(file);
    CXFOperator.saveCXF(cxf, "pdde.cxf");

  }
}
