package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.array.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class LogoFileAdapter
    extends TargetAdapter {
  public LogoFileAdapter() {

  }

  public LogoFileAdapter(String filename) {
    try {
      logoFile = new LogoFile(filename);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  public List<String> getPatchNameList() {
    if (patchNameList == null) {
      int size = logoFile.getNumberOfSets();
      patchNameList = new ArrayList<String> (size);
      for (int x = 0; x < size; x++) {
        String sampleName = logoFile.getDataField(x, "SAMPLE_NAME");
        sampleName = (sampleName == null) ?
            logoFile.getDataField(x, "Sample_Name") : sampleName;
        sampleName = (sampleName == null) ?
            logoFile.getDataField(x, "Sample_ID") : sampleName;
        sampleName = (sampleName == null) ?
            logoFile.getDataField(x, "SAMPLE_ID") : sampleName;
        patchNameList.add(sampleName);
      }
    }
    return patchNameList;
  }

  public LogoFileAdapter(Reader reader, String resource) {
    try {
      logoFile = new LogoFile(reader);
      logoFile.setFilename(resource);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  public LogoFileAdapter(Reader reader) {
    this(reader, null);
  }

  public final boolean isSpectraDataFormatAvailable() {
    return logoFile.getDataFormat().indexOf("nm") != -1;
  }

  public final boolean isRGBDataFormatAvailable() {
    return logoFile.getDataFormat().indexOf("RGB") != -1;
  }

  public final double[][] getCalibrationMatrix() {
    String header = logoFile.getHeader(LogoFile.Reserved.CalibrationMatrix);
    if (header == null) {
      return null;
    }
    StringTokenizer tokenizer = new StringTokenizer(header);
    int tokens = tokenizer.countTokens();
    if (tokens != 9) {
      return null;
    }
    else {
      double[] matrix = new double[9];
      for (int x = 0; x < 9; x++) {
        String token = tokenizer.nextToken();
        matrix[x] = Double.parseDouble(token);
      }
      return DoubleArray.to2DDoubleArray(matrix, 3);
    }
  }

  public final String getInstrumentation() {
    String instrument = logoFile.getHeader(LogoFile.Reserved.Instrumentation);
    return instrument;
  }

  public final boolean isInverseModeMeasure() {
    String inverse = logoFile.getHeader(LogoFile.Reserved.InverseModeMeasure);
    if (inverse != null && inverse.equals("Yes")) {
      return true;
    }
    else {
      return false;
    }
  }

  public final boolean isXYZDataFormatAvailable() {
    return logoFile.getDataFormat().indexOf("XYZ") != -1;
  }

  protected LogoFile logoFile;
  protected List<RGB> rgbList;
  protected List<CIEXYZ> XYZList;
  protected List<Spectra> spectraList;
  protected List<String> patchNameList;

  public static void main(String[] args) {

    LogoFileAdapter
        logo = new LogoFileAdapter(
            "1230547878156.logo");
    double[][] matrix = logo.getCalibrationMatrix();
    System.out.println(Arrays.deepToString(matrix));
    System.out.println(logo.getInstrumentation());
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {

    if (rgbList == null && logoFile.getDataFormat().indexOf("RGB") != -1) {
      int size = logoFile.getNumberOfSets();
      rgbList = new ArrayList<RGB> (size);
      for (int x = 0; x < size; x++) {
        double r = Double.parseDouble(logoFile.getDataField(x, "RGB_R"));
        double g = Double.parseDouble(logoFile.getDataField(x, "RGB_G"));
        double b = Double.parseDouble(logoFile.getDataField(x, "RGB_B"));
//        int r = Integer.parseInt(logoFile.getDataField(x, "RGB_R"));
//        int g = Integer.parseInt(logoFile.getDataField(x, "RGB_G"));
//        int b = Integer.parseInt(logoFile.getDataField(x, "RGB_B"));

        RGB rgb = new RGB(r, g, b);
        rgbList.add(rgb);
      }

    }
    return rgbList;
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    if (XYZList == null && logoFile.getDataFormat().indexOf("XYZ") != -1) {
      int size = logoFile.getNumberOfSets();
      XYZList = new ArrayList<CIEXYZ> (size);
      for (int x = 0; x < size; x++) {
        String fieldX = logoFile.getDataField(x, "XYZ_X");
        String fieldY = logoFile.getDataField(x, "XYZ_Y");
        String fieldZ = logoFile.getDataField(x, "XYZ_Z");

        double X = (fieldX != null) ? Double.parseDouble(fieldX) : -1;
        double Y = (fieldY != null) ? Double.parseDouble(fieldY) : -1;
        double Z = (fieldZ != null) ? Double.parseDouble(fieldZ) : -1;
        CIEXYZ XYZ = new CIEXYZ(X, Y, Z);
        XYZList.add(XYZ);
      }

    }
    return XYZList;
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    if (spectraList == null && logoFile.getDataFormat().indexOf("nm") != -1) {
      int size = logoFile.getNumberOfSets();
      spectraList = new ArrayList<Spectra> (size);
      Spectra.SpectrumType spectrumType = null;
      String illuminationName = logoFile.getHeader(LogoFile.Reserved.
          IlluminationName);
      if (illuminationName.indexOf("Emission") != -1) {
        spectrumType = Spectra.SpectrumType.EMISSION;
      }
      else {
        spectrumType = Spectra.SpectrumType.NO_ASSIGN;
      }

      for (int x = 0; x < size; x++) {
        double[] spectraPower = new double[36];
        int index = 0;
        for (int nm = 380; nm <= 730; nm += 10) {
          String field = logoFile.getDataField(x, "nm" + nm);
          spectraPower[index++] = (field != null) ? Double.parseDouble(field) :
              -1;
        }

        String name = logoFile.getDataField(x, "SAMPLE_NAME");
        Spectra s = new Spectra(name, spectrumType, 380, 730,
                                10, spectraPower);
        spectraList.add(s);
      }
    }
    return spectraList;
  }

  public List<Spectra> getReflectSpectraList() {
//    throw new UnsupportedOperationException();
    return getSpectraList();
  }

  /**
   * getName
   *
   * @return String
   */
  public String getFilename() {
    return logoFile.getFilename();
  }

  public String getAbsolutePath() {
    return logoFile.getFilename();
  }

  public Style getStyle() {
    boolean RGB = isRGBDataFormatAvailable();
    boolean XYZ = isXYZDataFormatAvailable();
    boolean spectra = isSpectraDataFormatAvailable();
    if (!RGB) {
      return Style.Unknow;
    }
    else if (XYZ && spectra) {
      return Style.RGBXYZSpectra;
    }
    else if (XYZ) {
      return Style.RGBXYZ;

    }
    else if (spectra) {
      return Style.RGBSpectra;
    }
    else {
      return Style.RGB;
    }
  }

  public String getFileNameExtension() {
    return "logo";
  }

  public String getFileDescription() {
    return "Logo File";
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    return LCDTargetBase.Number.getNumber(logoFile.getNumberOfSets());
  }
}
