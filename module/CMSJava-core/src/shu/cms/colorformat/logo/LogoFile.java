package shu.cms.colorformat.logo;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.measure.meter.*;

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
public class LogoFile
    implements LogoDataExchangeFormat.Reserved {
  protected LogoDataExchangeFormat format;

  public String getFilename() {
    return filename;
  }

  public LogoFile(String filename) throws IOException {
    this(filename, false);
  }

  public LogoFile(String filename, List<Patch> patchList,
      Meter meter) throws IOException {
    this(filename, true);
    //如果傳入的meter是null, 就用DummyMeter
    meter = (meter == null) ? new DummyMeter() : meter;
    meter.setLogoFileHeader(this);
    meter.setLogoFileData(this, patchList);
  }

  public LogoFile(String filename, List<Patch> patchList) throws IOException {
    this(filename, true);
//    this.setDefaultHeader();
    this.addData(patchList);
  }

  public LogoFile(List<Patch> patchList, Meter meter) throws IOException {
    this(null, patchList, meter);
  }

  public final static LogoFile getDefaultInstance(List<Patch> patchList,
      Meter meter) throws IOException {
    LogoFile logofile = new LogoFile(patchList, meter);
    logofile.setDefaultHeader();
    return logofile;
  }

  public LogoFile(String filename, LCDTarget lcdTarget) throws IOException {
    this(filename, lcdTarget.getPatchList(), lcdTarget.getMeasureMeter());
  }

  public LogoFile(String filename, boolean create) throws IOException {
    if (create) {
      format = new LogoDataExchangeFormat();
    }
    else {
      format = new LogoDataExchangeFormat(filename);
    }
    this.filename = filename;
  }

  protected String filename;

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void save(String filename) throws IOException {
    String fmt = format.toString();
    BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
    writer.write(fmt);
    writer.flush();
    writer.close();
  }

  public void save() throws IOException {
    save(filename);
  }

  public LogoFile(Reader reader) throws IOException {
    format = new LogoDataExchangeFormat(reader);
  }

  public String getHeader(Reserved keyword) {
    return format.getHeaderValueAsString(keyword.words);
  }

  public void setHeader(Reserved reserved, String value) {
    format.getHeader().putKeyAndValue(reserved.words, value);
  }

  public String getHeader(String key) {
    return format.getHeaderValueAsString(key);
  }

  public void setHeader(String key, String value) {
    format.getHeader().putKeyAndValue(key, value);
  }

  public static enum Reserved {
    Created("CREATED"), IlluminationName("ILLUMINATION_NAME"), Instrumentation(
        "INSTRUMENTATION"), MeasurementMode("Measurement_mode"),
    MeasurementSource("MEASUREMENT_SOURCE"), ObserverAngle("OBSERVER_ANGLE"),
    R("RGB_R"), G("RGB_G"), B("RGB_B"), X("XYZ_X"), Y("XYZ_Y"), Z("XYZ_Z"),
    SampleName("SAMPLE_NAME"), SampleID("SampleID"), LuminanceXYZ(
        "LUMINANCE_XYZ_CDM2"), InverseModeMeasure("InverseModeMeasure"),
    CalibrationMatrix("CalibrationMatrix");

    Reserved(String words) {
      this.words = words;
    }

    public String toString() {
      return words;
    }

    String words;
  }

  public void setObserverAngle(String observerAngle) {

    format.getHeader().putKeyAndValue(LogoDataExchangeFormat.Reserved.
                                      OBSERVER_ANGLE, observerAngle);
  }

  public int getLGORowLength() {
    return format.getHeaderValueAsInteger(LGOROWLENGTH);
  }

  public void setLGORowLength(int LGORowLength) {
    format.getHeader().putKeyAndValue(LGOROWLENGTH,
                                      Integer.toString(LGORowLength));
  }

  public int getNumberOfFields() {
    return format.getHeaderValueAsInteger(NUMBER_OF_FIELDS);
  }

  public void setNumberOfFields(int numberOfFields) {
    format.getHeader().putKeyAndValue(NUMBER_OF_FIELDS,
                                      Integer.toString(numberOfFields));
  }

  public int getNumberOfSets() {
    int numOfSets = format.getHeaderValueAsInteger(NUMBER_OF_SETS);
    numOfSets = numOfSets == -1 ? format.getDataBlock().size() : numOfSets;
    return numOfSets;
  }

  public void setNumberOfSets(int numberOfSets) {
    format.getHeader().putKeyAndValue(NUMBER_OF_SETS,
                                      Integer.toString(numberOfSets));
  }

  public String[] getData(int index) {
    return format.getDataBlock().getData(index);
  }

  public RGB getDataRGB(int index) {
    double r = Double.valueOf(getDataField(index, Reserved.R.words));
    double g = Double.valueOf(getDataField(index, Reserved.G.words));
    double b = Double.valueOf(getDataField(index, Reserved.B.words));
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g, b},
                      RGB.MaxValue.Double255);
    return rgb;
  }

  public CIEXYZ getDataXYZ(int index) {
    double X = Double.valueOf(getDataField(index, Reserved.X.words));
    double Y = Double.valueOf(getDataField(index, Reserved.Y.words));
    double Z = Double.valueOf(getDataField(index, Reserved.Z.words));
    CIEXYZ XYZ = new CIEXYZ(X, Y, Z);
    return XYZ;
  }

  public void addData(List<Patch> patchList) {
    Meter.setLogoPatchList(this, patchList);
  }

  public void setDefaultHeader() {
    this.setHeader(LogoFile.Reserved.Created, new Date().toString());
    this.setHeader(LogoFile.Reserved.MeasurementSource,
                   "Illumination=Unknown	ObserverAngle=Unknown	WhiteBase=Abs	Filter=Unknown");
    this.setNumberOfFields(8);
    this.addKeyword("SampleID");
    this.addKeyword("SAMPLE_NAME");
    this.setDataFormat(
        "SampleID	SAMPLE_NAME	RGB_R	RGB_G	RGB_B	XYZ_X	XYZ_Y	XYZ_Z");

  }

  public void addData(String[] data) {
    format.getDataBlock().addData(data);
  }

  public void addData(String data) {
    format.getDataBlock().addData(data);
  }

  public Patch getDataPatch(int index) {
    String name = this.getDataField(index, Reserved.SampleName.words);
    RGB rgb = this.getDataRGB(index);
    CIEXYZ XYZ = this.getDataXYZ(index);
    Patch p = new Patch(name, XYZ, null, rgb);
    return p;
  }

  public void addKeyword(String keyword) {
    format.getHeader().addKeyword(keyword);
  }

  public String getDataField(int index, String fieldName) {
    return format.getDataBlock().getDataField(index, fieldName);
  }

  public String getDataFormat() {
    return format.getDataFormat();
  }

  public void setDataFormat(String dataFormat) {
    format.setDataFormat(dataFormat);
  }

  public static void main(String[] args) throws IOException {
//    CGATSFile file = new CGATSFile(CMSDir.Reference.Monitor +
//                                   "/LCD Monitor Reference 2.0.txt");
//
//    int size = file.getNumberOfSets();
//    System.out.println(file.getDataFormat());
//    System.out.println(size);
//    for (int x = 0; x < size; x++) {
//      String[] data = file.getData(x);
//      System.out.println(Arrays.toString(data));
//    }
//
    LogoFile file = new LogoFile("ArgyllMeter.logo.ti3");
    int size = file.getNumberOfSets();
    System.out.println(size);
    for (int x = 0; x < size; x++) {
      String[] s = file.getData(x);
      System.out.println(Arrays.toString(s));
    }

  }

  public void setArgyllEmulated(boolean emulated) {
    this.format.getHeader().setArgyllEmulated(emulated);
  }
}
