package shu.cms;

import java.io.*;

import shu.cms.colorformat.file.*;
import shu.cms.reference.cie.*;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import shu.cms.colorspace.independ.DeviceIndependentSpace;
import shu.cms.colorspace.independ.CIEXYZ;
///import shu.plot.*;

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
public class ColorMatchingFunction
    implements SpectraIF {

  /*private final static String[] cmfFilenames = new String[] {
      "ciexyz64_1.txt", "ciexyz31_1.txt", "ciexyzj.txt", "ciexyzjv.txt",
      "linss2_10e_1_8dp.txt", "linss10e_1_8dp.txt"};*/

  protected static class CMFInfo {
    protected String filename;
    protected int degree;
    protected CMFInfo(String filename, int degree) {
      this.filename = filename;
      this.degree = degree;
    }
  }

  private final static CMFInfo[] CMFInfos = new CMFInfo[] {
      new CMFInfo("ciexyz64_1.txt", 10), new CMFInfo("ciexyz31_1.txt", 2),
      new CMFInfo("ciexyzj.txt", 2), new CMFInfo("ciexyzjv.txt", 2),
  };

  private static ColorMatchingFunction[] cmfArray =
      new ColorMatchingFunction[CMFInfos.length];

  /**
   * 初始化CMF
   */
  static {
    for (int x = 0; x < CMFInfos.length; x++) {
      CMFInfo info = CMFInfos[x];
      InputStream is = CIE.class.getResourceAsStream(info.filename);
      if (is != null) {
        cmfArray[x] = getColorMatchingFunction(is);
        cmfArray[x].degree = info.degree;
      }
    }
  }

  protected int degree;
  public final int getDegree() {
    return degree;
  }

  public final static ColorMatchingFunction CIE_1964_10DEG_XYZ = cmfArray[0];
  public final static ColorMatchingFunction CIE_1931_2DEG_XYZ = cmfArray[1];
  public final static ColorMatchingFunction JUDD_1951_2DEG_XYZ = cmfArray[2];
  public final static ColorMatchingFunction JUDD_VOS_1978_2DEG_XYZ = cmfArray[3];
//  public final static ColorMatchingFunction CIE_2007_2DEG_LMS = cmfArray[4];
//  public final static ColorMatchingFunction CIE_2007_10DEG_LMS = cmfArray[5];

  protected static ColorMatchingFunction getColorMatchingFunction(InputStream
      in) {

    ColorMatchingFunctionFile parser = new ColorMatchingFunctionFile(in);
    parser.parsing();
    return parser.getColorMatchingFunction();
  }

  private int index = 0;

  public int getInterval() {
    return spectraArray[index].interval;
  }

  public int getStart() {
    return spectraArray[index].start;
  }

  public int getEnd() {
    return spectraArray[index].end;
  }

  public double[] getData() {
    return spectraArray[index].data;
  }

  public double[] getData(int index) {
    return spectraArray[index].data;
  }

  protected Spectra[] spectraArray;

  public ColorMatchingFunction(Spectra[] spectraArray) {
    this.spectraArray = spectraArray;
  }

  public Spectra getSpectra(int index) {
    return spectraArray[index];
  }

  /**
   * 重製CMF
   * @param start int
   * @param end int
   * @param interval int
   * @return ColorMatchingFunction
   */
  public ColorMatchingFunction getColorMatchingFunction(int start, int end,
      int interval) {
    int size = (end - start) / interval + 1;
    Spectra[] newSpectraArray = new Spectra[3];
    for (int x = 0; x < 3; x++) {
      double[] spectraData = new double[size];
      Spectra s = spectraArray[x];
      Spectra s2 = s.fillAndInterpolate(start, end, interval);
      for (int y = 0; y < size; y++) {
        spectraData[y] = s2.getData(y * interval + start);
      }
      newSpectraArray[x] = new Spectra(s.name, s.spectraType, start, end,
                                       interval, spectraData);
    }

    return new ColorMatchingFunction(newSpectraArray);
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public static void main(String[] args) {
    Plot2D p = Plot2D.getInstance();

    ColorMatchingFunction cmf2 = ColorMatchingFunction.JUDD_1951_2DEG_XYZ.
        getColorMatchingFunction(380, 730, 5);
//    ColorMatchingFunction cmf2 = ColorMatchingFunction.JUDD_VOS_1978_2DEG_XYZ.
//        getColorMatchingFunction(380, 730, 5);

    p.addSpectra("X'", Color.red, cmf2.getSpectra(0));
    p.addSpectra("Y'", Color.green, cmf2.getSpectra(1));
    p.addSpectra("Z'", Color.blue, cmf2.getSpectra(2));
    p.setLineType(Plot2D.LineType.Dotted);

    ColorMatchingFunction cmf = ColorMatchingFunction.CIE_1931_2DEG_XYZ.
        getColorMatchingFunction(380, 730, 5);
    p.addSpectra("X", Color.red, cmf.getSpectra(0));
    p.addSpectra("Y", Color.green, cmf.getSpectra(1));
    p.addSpectra("Z", Color.blue, cmf.getSpectra(2));

    p.addLegend();
    p.setVisible();
  }

  public CIEXYZ getXYZFill(Spectra spectra) {
    Spectra modifySpectra = spectra.fillAndInterpolate(getStart(), getEnd(),
        getInterval());

    double[] XYZValues = new double[3];
    for (int x = 0; x < 3; x++) {
      Spectra cmfSpectra = getSpectra(x);
      XYZValues[x] = .683002 *
          Spectra.sigma(cmfSpectra.start, cmfSpectra.end, cmfSpectra.interval,
                        modifySpectra.data, cmfSpectra.data) *
          cmfSpectra.interval;
    }
    CIEXYZ result = new CIEXYZ(XYZValues);
    result.setDegree(DeviceIndependentSpace.Degree.getDegree(getDegree()));
    return result;
  }

}
