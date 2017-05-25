package shu.cms.colorspace.independ;

import java.util.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.hvs.cam.*;
import shu.cms.plot.*;
import shu.math.array.*;
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
 * @author skyforce
 * @version 1.0
 */
public class CIELMS
    extends DeviceIndependentSpace {
  public double L, M, S;
  public double getLuminance() {
    return cf.getLConeWeighting() * L + cf.getMConeWeighting() * M;
  }

  public double[] getlsYValues() {
    double[] lmYValues = new double[3];
    double[] lmValues = getChromaticityCoordinates();
    lmYValues[0] = lmValues[0];
    lmYValues[1] = lmValues[1];
    lmYValues[2] = getLuminance();
    return lmYValues;
  }

  public double[] getChromaticityCoordinates() {
    double[] lmValues = new double[2];
    double Y = getLuminance();
    lmValues[0] = cf.getLConeWeighting() * L / Y;
    lmValues[1] = cf.getSConeWeighting() * S / Y;
    return lmValues;
  }

  public CIELMS(Spectra spectra) {
    this(spectra, ConeFundamental.CIE_2007_2DEG_LMS);
  }

  public CIELMS(Spectra spectra, ConeFundamental cf) {
    super(spectra.getLMSValues(cf));
    setDegree(DeviceIndependentSpace.Degree.getDegree(cf.getDegree()));
    this.cf = cf;
  }

  public CIELMS(double[] values, ConeFundamental cf) {
    super(values);
    this.cf = cf;
  }

  /*public CIELMS(double[] values, CIELMS white) {
    this(values, white, null, false);
     }

     public CIELMS(double[] values) {
    super(values);
    this.cf = ConeFundamental.CIE_2007_2DEG_LMS;
     }

     public CIELMS(double value1, double value2, double value3) {
    this(value1, value2, value3, null);
     }

     public CIELMS(double value1, double value2, double value3, CIELMS white) {
    this(new double[] {value1, value2, value3}, white, null, false,
         ConeFundamental.CIE_2007_2DEG_LMS);
     }

     public CIELMS(double value1, double value2, double value3, CIELMS white,
                CIELMS originalWhite) {
    this(new double[] {value1, value2, value3}, white, originalWhite, false,
         ConeFundamental.CIE_2007_2DEG_LMS);
     }

     public CIELMS(double[] values, CIELMS white, CIELMS originalWhite,
                boolean adaptedToD65, ConeFundamental cf) {
    super(values);
    this.white = white;
    this.originalWhite = originalWhite;
    this.adaptedToD65 = adaptedToD65;
    this.cf = cf;
     }*/

//  protected CIELMS white;
//  protected CIELMS originalWhite;
  protected final ConeFundamental cf;

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = L;
    values[1] = M;
    values[2] = S;
    return values;
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    L = values[0];
    M = values[1];
    S = values[2];

  }

  /**
   * getBandNames
   *
   * @return String[]
   */
  public String[] getBandNames() {
    return new String[] {
        "L", "M", "S"};
  }

  /**
   * toXYZ
   *
   * @return CIEXYZ
   */
  public CIEXYZ toXYZ() {
    throw new UnsupportedOperationException();
  }

  public static void locusExample() {
    Plot2D plot2D = Plot2D.getInstance();
    double[] lastxy = null;
    double[] data = new double[441];

    for (int x = 390; x <= 830; x += 1) {
      Arrays.fill(data, 0);
      data[x - 390] = 1;
      Spectra s = new Spectra("", Spectra.SpectrumType.EMISSION, 390, 830,
                              1, data);
      CIELMS lms = new CIELMS(s);
      double[] xy = lms.getlsYValues();
//      if (Double.isNaN(xy[0]) || Double.isNaN(xy[0])) {
//        continue;
//      }
      System.out.println(x + " " + xy[0] + " " + xy[1]);
//      CIEXYZ XYZ = s.getXYZ();
//      double[] xy = transfer.getxyCoordinate(XYZ);
      if (lastxy == null) {
        lastxy = xy;
      }
      else {

//        CIExyY xyY = CIExyY.fromXYZ(XYZ);
//        xyY.Y = 1;
//        RGB rgb = RGB.fromXYZ(CIExyY.toXYZ(xyY), RGB.ColorSpace.skyRGB);
//        rgb.rationalize();
//        Color c = rgb.getColor();

        plot2D.addLinePlot(null, Color.black, lastxy[0],
                           xy[0],
                           new double[] {lastxy[1], xy[1]});
        lastxy = xy;
      }
    }
    plot2D.setVisible();
  }

  public static void main(String[] args) {
//    locusExample();
//    Illuminant[] illuminants = new Illuminant[] {
//        Illuminant.D50, Illuminant.D65, Illuminant.D75, Illuminant.D93};
    Illuminant[] illuminants = new Illuminant[] {
        Illuminant.E};
    for (Illuminant i : illuminants) {
      System.out.println(i.getSpectra().getName());
      Spectra s = i.getSpectra();
      CIEXYZ XYZ = s.getXYZ();
      CIELMS LMS = new CIELMS(s);

      System.out.println("XYZ: " + XYZ + " " +
                         DoubleArray.toString(XYZ.getxyValues()));
      System.out.println("LMS: " + LMS + " " +
                         DoubleArray.toString(LMS.getlsYValues()));
      System.out.println("Y/LM: " + XYZ.Y + "/" + LMS.getLuminance());
      for (CAMConst.CATType type : CAMConst.CATType.values()) {
        LMS lms = new LMS(XYZ, type);
        System.out.println(type.name() + " " + lms);
      }

    }

  }
}
