package shu.cms.colorformat.legend;

import java.util.*;

import shu.math.array.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 檔案有五種可能
 * 1.TestChart
 * 2.Reflection Lab
 * 3.Reflection Spectra
 * 4.Emission Lab
 * 5.Emission Spectra
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GretagMacbethAsciiFile
    extends AsciiFile {
  GretagMacbethAsciiFile() {
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(this.header);
    buf.append('\n');
    buf.append(this.dataFormat);
    buf.append('\n');
    buf.append(this.dataSet);

    return buf.toString();
  }

  protected GretagMacbethAsciiFile.Header header;
  protected ArrayList dataFormat;
  protected GretagMacbethAsciiFile.DataSet dataSet;

  protected boolean testchart = false;
  protected boolean emission = false;

  public GretagMacbethAsciiFile.DataSet getDataSet() {
    return dataSet;
  }

  public GretagMacbethAsciiFile.Header getHeader() {
    return header;
  }

  public ArrayList getDataFormat() {
    return dataFormat;
  }

  public static class SpectraData {
    public int sampleID;
    public String sampleName;
    public int[] RGB;
    public double[] spectra;

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("SampleID: ");
      buf.append(sampleID);
      buf.append('\n');
      buf.append("SampleName: ");
      buf.append(sampleName);
      if (RGB != null) {
        buf.append("RGB: ");
        buf.append(Utils.toString(RGB));
      }
      buf.append("\nSpectraData: ");
      buf.append(DoubleArray.toString(spectra));

      return buf.toString();
    }
  }

  public static class LabData {
    public int sampleID;
    public String sampleName;
    public int[] RGB;
    public double[] XYZ;
    public double[] _Lab;

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("SampleID: ");
      buf.append(sampleID);
      buf.append('\n');
      buf.append("SampleName: ");
      buf.append(sampleName);
      if (RGB != null) {
        buf.append("RGB: ");
        buf.append(Utils.toString(RGB));
      }
      buf.append("\nXYZ: ");
      buf.append(DoubleArray.toString(XYZ));
      buf.append("\nLab: ");
      buf.append(DoubleArray.toString(_Lab));

      return buf.toString();
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來表示導表資料
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static class TestChartData {
    public int sampleID;
    public String sampleName;
    public double[] RGB;

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("SampleID: ");
      buf.append(sampleID);
      buf.append('\n');
      buf.append("SampleName: ");
      buf.append(sampleName);
      buf.append("\nXYZ: ");
      buf.append(DoubleArray.toString(RGB));

      return buf.toString();
    }
  }

  public static class DataSet {
    protected GretagMacbethAsciiFile motherFile;
    protected int numberOfFields;

    protected boolean isEmission() {
      return motherFile.emission;
    }

    public void setMotherFile(GretagMacbethAsciiFile file) {
      this.motherFile = file;
      this.numberOfFields = motherFile.header.numberOfFields;
    }

    public boolean isSpectraData() {
      return numberOfFields == 38 ||
          numberOfFields == 41;
    }

    public boolean isLabData() {
      return numberOfFields == 8 || numberOfFields == 11;
    }

    public boolean isTestChartData() {
      return motherFile.testchart;
    }

    public TestChartData getTestChartData(int index) {
      if (!isTestChartData()) {
        return null;
      }
      ArrayList dataFormat = motherFile.getDataFormat();
      int sampleNameIndex = dataFormat.indexOf("Sample_ID");
      sampleNameIndex = sampleNameIndex == -1 ? dataFormat.indexOf("SampleID") :
          sampleNameIndex;
      int rgbRIndex = dataFormat.indexOf("RGB_R");

      TestChartData testChartData = new TestChartData();
      ArrayList data = (ArrayList) dataSet.get(index);
      testChartData.sampleID = index + 1;
      testChartData.sampleName = ( (String) data.get(sampleNameIndex)).trim();

      double[] RGB = new double[3];
      for (int x = 0; x < 3; x++) {
        RGB[x] = Double.parseDouble( ( (String) data.get(x + rgbRIndex)).trim());
//        RGB[x] = Integer.parseInt( ( (String) data.get(x + rgbRIndex)).trim());
      }
      testChartData.RGB = RGB;

      return testChartData;
    }

    public SpectraData getSpectraData(int index) {
      if (!isSpectraData()) {
        return null;
      }
      SpectraData spectraData = new SpectraData();
      ArrayList data = (ArrayList) dataSet.get(index);
      spectraData.sampleID = Integer.parseInt( ( (String) data.get(0)).
                                              trim());
      spectraData.sampleName = ( (String) data.get(1)).trim();
      int count = 2;

      if (isEmission()) {
        int[] RGB = new int[3];
        for (int x = 0; x < 3; x++) {
          RGB[x] = (int) Double.parseDouble( ( (String) data.get(x + count)).
                                            trim());
        }
        spectraData.RGB = RGB;
        count += 3;
      }

      double[] spectra = new double[36];
      for (int x = 0; x < 36; x++) {
        spectra[x] = Double.parseDouble( ( (String) data.get(x + count)).trim());
      }
      spectraData.spectra = spectra;

      return spectraData;
    }

    public LabData getLabData(int index) {
      if (!isLabData()) {
        return null;
      }
      LabData labData = new LabData();
      ArrayList data = (ArrayList) dataSet.get(index);
      labData.sampleID = Integer.parseInt( ( (String) data.get(0)).
                                          trim());
      labData.sampleName = ( (String) data.get(1)).trim();
      int count = 2;

      if (isEmission()) {
        int[] RGB = new int[3];
        for (int x = 0; x < 3; x++) {
          RGB[x] = (int) Double.parseDouble( ( (String) data.get(x + count)).
                                            trim());
//          RGB[x] = Integer.parseInt( ( (String) data.get(x + count)).trim());
        }
        labData.RGB = RGB;
        count += 3;
      }

      double[] XYZ = new double[3];
      for (int x = 0; x < 3; x++) {
        XYZ[x] = Double.parseDouble( ( (String) data.get(x + count)).trim());
      }
      labData.XYZ = XYZ;
      count += 3;

      double[] LAB = new double[3];
      for (int x = 0; x < 3; x++) {
        LAB[x] = Double.parseDouble( ( (String) data.get(x + count)).trim());
      }
      labData._Lab = LAB;

      return labData;
    }

//    ArrayList dataFormat;
//    int dataFields;
    ArrayList<ArrayList<String>> dataSet = new ArrayList<ArrayList<String>> ();

    public int size() {
      return dataSet.size();
    }

    public void addData(ArrayList<String> data) {
      this.dataSet.add(data);
    }

    static String dataSet2String(ArrayList dataSet) {
      StringBuilder buf = new StringBuilder();
      int size = dataSet.size();

      for (int x = 0; x < size; x++) {
        ArrayList data = (ArrayList) dataSet.get(x);
        buf.append(data.toString());
        if (x != size - 1) {
          buf.append('\n');
        }
      }
      return buf.toString();
    }

    public String toString() {
      return dataSet2String(dataSet);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來表示GMB Ascii file的檔頭
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static class Header {
    public int lgorowlength;
    public String measurement_mode;
    public String created;
    public String instrumentation;
    public String measurement_source;
    public String illumination_name;
    public int observerAngle;

    public int numberOfFields;
//    public ArrayList keywords = new ArrayList();

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("LGOROWLENGTH: ");
      buf.append(lgorowlength);
      buf.append('\n');
      buf.append("Measurement_mode: ");
      buf.append(measurement_mode);
      buf.append('\n');
      buf.append("CREATED: ");
      buf.append(created);
      buf.append('\n');
      buf.append("INSTRUMENTATION: ");
      buf.append(instrumentation);
      buf.append('\n');
      buf.append("MEASUREMENT_SOURCE: ");
      buf.append(measurement_source);
      buf.append('\n');
      buf.append("ILLUMINATION_NAME: ");
      buf.append(illumination_name);
      buf.append('\n');
      buf.append("OBSERVER_ANGLE: ");
      buf.append(observerAngle);
      buf.append('\n');
//      buf.append("KEYWORD: ");
//      buf.append(keywords.toString());
//      buf.append('\n');
      buf.append("NUMBER_OF_FIELDS: ");
      buf.append(numberOfFields);
//      buf.append('\n');

      return buf.toString();
    }
  }

}
