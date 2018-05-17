package shu.cms;

import java.io.*;

import shu.cms.colorspace.independ.*;
import shu.cms.reference.cie.*;
import shu.cms.plot.Plot2D;
import java.awt.Color;
///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ConeFundamental
    extends ColorMatchingFunction {
  public ConeFundamental(Spectra[] spectraArray) {
    super(spectraArray);
  }

  public static void main(String[] args) {

    Plot2D plot = Plot2D.getInstance();
    plot.addLegend();

    ConeFundamental cf = ConeFundamental.CIE_2007_2DEG_LMS;
    plot.addSpectra("L", Color.red, cf.getSpectra(0));
    plot.addSpectra("M", Color.green, cf.getSpectra(1));
    plot.addSpectra("S", Color.blue, cf.getSpectra(2));
    plot.setLinePlotWidth(2);

    ColorMatchingFunction cmf = ColorMatchingFunction.CIE_1931_2DEG_XYZ;
    plot.addSpectra("X", Color.red, cmf.getSpectra(0));
    plot.addSpectra("Y", Color.green, cmf.getSpectra(1));
    plot.addSpectra("Z", Color.blue, cmf.getSpectra(2));

    plot.setVisible();
  }

  protected static class CFInfo
      extends CMFInfo {
    protected double lconeWeighting, mconeWeighting, sconeWeighting;
    protected CFInfo(String filename, int degree, double lconeWeighting,
                     double mconeWeighting, double sconeWeighting) {
      super(filename, degree);
      this.lconeWeighting = lconeWeighting;
      this.mconeWeighting = mconeWeighting;
      this.sconeWeighting = sconeWeighting;
    }

  }

  private final static CFInfo[] CFInfos = new CFInfo[] {
      new CFInfo("linss2_10e_1_8dp.txt", 2, 0.689903, 0.348322, 0.0371597),
      new CFInfo("linss10e_1_8dp.txt", 10, 0.692839, 0.349676, 0.0554786)
  };

  private static ConeFundamental[] cfArray =
      new ConeFundamental[CFInfos.length];

  /**
   * 初始化CMF
   */
  static {
    for (int x = 0; x < CFInfos.length; x++) {
      CFInfo info = CFInfos[x];
      InputStream is = CIE.class.getResourceAsStream(info.filename);
      if (is != null) {
        ColorMatchingFunction cmf = getColorMatchingFunction(is);
        cfArray[x] = new ConeFundamental(cmf.spectraArray);
        cfArray[x].degree = info.degree;
        cfArray[x].lconeWrighting = info.lconeWeighting;
        cfArray[x].mconeWrighting = info.mconeWeighting;
        cfArray[x].sconeWrighting = info.sconeWeighting;
      }
    }
  }

  public final static ConeFundamental CIE_2007_2DEG_LMS = cfArray[0];
  public final static ConeFundamental CIE_2007_10DEG_LMS = cfArray[1];
  private double lconeWrighting;
  private double mconeWrighting;
  private double sconeWrighting;
  public final double getLConeWeighting() {
    return lconeWrighting;
  }

  public final double getMConeWeighting() {
    return mconeWrighting;
  }

  public final double getSConeWeighting() {
    return sconeWrighting;
  }
}
