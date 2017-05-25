package shu.cms;

import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.cms.plot.LocusPlot;
import shu.cms.plot.Plot2D;

///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 計算色溫用
 * 測試中
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CorrelatedColorTemperature {
  private CorrelatedColorTemperature() {

  }

  /**
   * 計算CCT2xyY所需
   */
  private final static double DBL_MIN = 1E-37;

  /**
   * 計算CCT2xyY所需
   */
  private final static double[] rt = new double[] {
      /* reciprocal temperature (K) */
      DBL_MIN, 10.0e-6, 20.0e-6, 30.0e-6, 40.0e-6, 50.0e-6,
      60.0e-6, 70.0e-6, 80.0e-6, 90.0e-6, 100.0e-6, 125.0e-6,
      150.0e-6, 175.0e-6, 200.0e-6, 225.0e-6, 250.0e-6, 275.0e-6,
      300.0e-6, 325.0e-6, 350.0e-6, 375.0e-6, 400.0e-6, 425.0e-6,
      450.0e-6, 475.0e-6, 500.0e-6, 525.0e-6, 550.0e-6, 575.0e-6,
      600.0e-6
  };

  /**
   * 計算CCT2xyY所需
   */
  private final static UVT[] uvt = new UVT[] {
      new UVT(0.18006, 0.26352, -0.24341),
      new UVT(0.18066, 0.26589, -0.25479),
      new UVT(0.18133, 0.26846, -0.26876),
      new UVT(0.18208, 0.27119, -0.28539),
      new UVT(0.18293, 0.27407, -0.30470),
      new UVT(0.18388, 0.27709, -0.32675),
      new UVT(0.18494, 0.28021, -0.35156),
      new UVT(0.18611, 0.28342, -0.37915),
      new UVT(0.18740, 0.28668, -0.40955),
      new UVT(0.18880, 0.28997, -0.44278),
      new UVT(0.19032, 0.29326, -0.47888),
      new UVT(0.19462, 0.30141, -0.58204),
      new UVT(0.19962, 0.30921, -0.70471),
      new UVT(0.20525, 0.31647, -0.84901),
      new UVT(0.21142, 0.32312, -1.0182),
      new UVT(0.21807, 0.32909, -1.2168),
      new UVT(0.22511, 0.33439, -1.4512),
      new UVT(0.23247, 0.33904, -1.7298),
      new UVT(0.24010, 0.34308, -2.0637),
      new UVT(0.24792, 0.34655, -2.4681),
      new UVT(0.25591, 0.34951, -2.9641),
      new UVT(0.26400, 0.35200, -3.5814),
      new UVT(0.27218, 0.35407, -4.3633),
      new UVT(0.28039, 0.35577, -5.3762),
      new UVT(0.28863, 0.35714, -6.7262),
      new UVT(0.29685, 0.35823, -8.5955),
      new UVT(0.30505, 0.35907, -11.324),
      new UVT(0.31320, 0.35968, -15.628),
      new UVT(0.32129, 0.36011, -23.325),
      new UVT(0.32931, 0.36038, -40.770),
      new UVT(0.33724, 0.36051, -116.45)
  };

  /**
   * LERP(a,b,c) = linear interpolation macro, is 'a' when c == 0.0 and 'b' when c == 1.0
   * @param a double
   * @param b double
   * @param c double
   * @return double
   */
  private final static double lerp(double a, double b, double c) {
    return Interpolation.linear(0.0, 1.0, a, b, c);
//    return ( ( (b) - (a)) * (c) + (a));

  }

  public static void main(String[] args) {
    Illuminant d65 = Illuminant.getDaylightByTemperature(6500);
    System.out.println(CorrelatedColorTemperature.CCT2DIlluminantxyY(6500));
    Spectra b65 = CorrelatedColorTemperature.getSpectraOfBlackbodyRadiator(6500);
    System.out.println(new CIExyY(d65.getSpectra().getXYZ()));
    System.out.println(new CIExyY(b65.getXYZ()));

//    System.out.println(CorrelatedColorTemperature.XYZ2CCTByRobertson(b65.getXYZ()));
  }

  public static void cctTest(String[] args) {
    CIEXYZ d65XYZ = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    CIExyY d65xyY = new CIExyY(d65XYZ);

    double[] B11_1_uvpValues = new CIExyY(0.300542027, 0.320985019, 181.3851013
        ).
        getuvPrimeValues();
    double[] B11_2_uvpValues = new CIExyY(0.299684852, 0.321013302, 243.5544128

        ).getuvPrimeValues();
    double[] B11_3_uvpValues = new CIExyY(0.301040232, 0.322474271, 239.1182251

        ).getuvPrimeValues();
    double[] B11_4_uvpValues = new CIExyY(0.304731548, 0.327298343, 237.3434753

        ).getuvPrimeValues();
    double[] B11_5_uvpValues = new CIExyY(0.303875834, 0.325664043, 238.0677948
        ).getuvPrimeValues();
    double[] d65uvpValues = d65xyY.getuvPrimeValues();

//    System.out.println(new CIExyY(0.30155158, 0.324824303, 262.1062317).getCCT());
//    System.out.println(new CIExyY(0.304731548, 0.327298343, 237.34347534).
//                       getCCT());
//    System.out.println(new CIExyY(0.303875834, 0.325664043, 238.0677948).getCCT());

    LocusPlot locus = new LocusPlot();
    locus.drawCIEuvPrimeLocus(true);
    Plot2D plot = locus.getPlot2D();

//    plot.addScatterPlot("B15", B15uvpValues[0], B15uvpValues[1]);
    plot.addScatterPlot("B11_1", B11_1_uvpValues[0], B11_1_uvpValues[1]);
    plot.addScatterPlot("B11_2", B11_2_uvpValues[0], B11_2_uvpValues[1]);
    plot.addScatterPlot("B11_3", B11_3_uvpValues[0], B11_3_uvpValues[1]);
    plot.addScatterPlot("B11_4", B11_4_uvpValues[0], B11_4_uvpValues[1]);
    plot.addScatterPlot("B11_5", B11_5_uvpValues[0], B11_5_uvpValues[1]);
    plot.addScatterPlot("D65", d65uvpValues[0], d65uvpValues[1]);

    for (int CCT = 5000; CCT <= 9300; CCT += 100) {
      CIExyY xyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(CCT);
      double[] uvpValues = xyY.getuvPrimeValues();
      plot.addCacheScatterLinePlot("D", uvpValues[0], uvpValues[1]);
    }

    plot.setVisible();

//    CIExyY xyY = CCT2DIlluminantxyY(51000);
//    CIExyY xyY = CCT2BlackbodyxyY(50000);
//    for (CCTMethod method : CCTMethod.values()) {
//      System.out.println(method + " " + xy2CCT(xyY, method));
//    }
  }

  public final static CIExyY CCT2BlackbodyxyY(int tempK) {
    Spectra blackbody = getSpectraOfBlackbodyRadiator(tempK);
    CIEXYZ XYZ = blackbody.getXYZ();
    XYZ.normalizeY();
    CIExyY xyY = new CIExyY(XYZ);
    return xyY;
  }

  /**
   * from lcms
   * 色溫轉D系列xyY座標
   * @param tempK int
   * @return CIExyY
   */
  public final static CIExyY CCT2DIlluminantxyY(double tempK) {
    double x = 0.0, y;
    double T, T2, T3;
    // double M1, M2;

    // No optimization provided.
    T = tempK;
    T2 = T * T; // Square
    T3 = T2 * T; // Cube

    // For correlated color temperature (T) between 4000K and 7000K:

    if (T >= 4000. && T <= 7000.) {
      x = -4.6070 * (1E9 / T3) + 2.9678 * (1E6 / T2) + 0.09911 * (1E3 / T) +
          0.244063;
    }
    else
    // or for correlated color temperature (T) between 7000K and 25000K:

    if (T > 7000.0 && T <= 25000.0) {
      x = -2.0064 * (1E9 / T3) + 1.9018 * (1E6 / T2) + 0.24748 * (1E3 / T) +
          0.237040;
    }
    else {
      throw new IllegalArgumentException(
          "invalid temp: " + tempK + ", tempK must in 4000~25000K");
    }

    // Obtain y(x)
    y = -3.000 * (x * x) + 2.870 * x - 0.275;

    // wave factors (not used, but here for futures extensions)

    // M1 = (-1.3515 - 1.7703*x + 5.9114 *y)/(0.0241 + 0.2562*x - 0.7341*y);
    // M2 = (0.0300 - 31.4424*x + 30.0717*y)/(0.0241 + 0.2562*x - 0.7341*y);

    // Fill WhitePoint struct
    CIExyY xyY = new CIExyY(new double[] {x, y, 1.0});

    return xyY;
  }

  public static enum CCTMethod {
    McCamyInt, McCamyFloat, Exp, ExpCCTOver50k, Robertson
  }

  public final static double xy2CCT(CIExyY xyY, CCTMethod method) {
    switch (method) {
      case McCamyInt:
        return xy2CCTByMcCamy(xyY);
      case McCamyFloat:
        return xy2CCTByMcCamyFloat(xyY);
      case Exp:
        return xy2CCTByExp(xyY, false);
      case ExpCCTOver50k:
        return xy2CCTByExp(xyY, true);
      case Robertson:
        return XYZ2CCTByRobertson(xyY.toXYZ());
      default:
        return -1;
    }
  }

  /**
   * 根據VESA/McCamy的formula所計算(非CIE)
   * @param xyY CIExyY
   * @return double
   */
  public final static double xy2CCTByMcCamy(CIExyY xyY) {
    double n = (xyY.x - 0.332) / (0.1858 - xyY.y);
    double sqr = Maths.sqr(n);
    double cct = 437D * sqr * n + 3601D * sqr + 6831D * n + 5517;
    return cct;
  }

  public final static double xy2CCTByMcCamyFloat(CIExyY xyY) {
    double n = (xyY.x - 0.332) / (xyY.y - 0.1858);
    double sqr = Maths.sqr(n);
    double cct = -449 * sqr * n + 3525 * sqr - 6823.3 * n + 5520.33;
    return cct;
  }

  public final static double xy2CCTByExp(CIExyY xyY, boolean CCTOver50k) {
    double[] c = CCTOver50k ? getExpCCTOver50kConstant() :
        getExpNormalConstant();
    double n = (xyY.x - c[0]) / (xyY.y - c[1]);
    double cct = c[2] + c[3] * Math.exp( -n / c[4]) + c[5] * Math.exp( -n / c[6]);
    double tail = c[7] * Math.exp( -n / c[8]);
    cct = CCTOver50k ? cct : cct + tail;
    return cct;
  }

  private final static double[] getExpNormalConstant() {
    return new double[] {
        0.3366, 0.1735, -949.86315, 6253.80338, 0.92159, 28.70599, 0.20039,
        0.00004, 0.07125
    };
  }

  private final static double[] getExpCCTOver50kConstant() {
    return new double[] {
        0.3356, 0.1691, 36284.48953, 0.00228, 0.07861, 5.4535E-36, 0.01543, 0,
        0
    };
  }

  /*
   *      Name:   XYZtoCorColorTemp.c
   *
   *      Author: Bruce Justin Lindbloom
   *
   *      Copyright (c) 2003 Bruce Justin Lindbloom. All rights reserved.
   *
   *      Input:  xyz = pointer to the input array of X, Y and Z color components (in that order).
   *              temp = pointer to where the computed correlated color temperature should be placed.
   *
   *      Output: *temp = correlated color temperature, if successful.
   *                    = unchanged if unsuccessful.
   *
   *      Return: 0 if successful, else -1.
   *
   *      Description:
   *              This is an implementation of Robertson's method of computing the correlated color
   *              temperature of an XYZ color. It can compute correlated color temperatures in the
   *              range [1666.7K, infinity].
   *
   *      Reference:
   *              "Color Science: Concepts and Methods, Quantitative Data and Formulae", Second Edition,
   *              Gunter Wyszecki and W. S. Stiles, John Wiley & Sons, 1982, pp. 227, 228.

   * from http://www.brucelindbloom.com/
   * XYZ轉CCT

   * @param XYZ CIEXYZ
   * @return double
   */
  public final static double XYZ2CCTByRobertson(CIEXYZ XYZ) {
    double us, vs, p, di = 0.0, dm;
    int i;

    if ( (XYZ.X < 1.0e-20) && (XYZ.Y < 1.0e-20) && (XYZ.Z < 1.0e-20)) {
      return ( -1); /* protect against possible divide-by-zero failure */
    }
    us = (4.0 * XYZ.X) / (XYZ.X + 15.0 * XYZ.Y + 3.0 * XYZ.Z);
    vs = (6.0 * XYZ.Y) / (XYZ.X + 15.0 * XYZ.Y + 3.0 * XYZ.Z);
    dm = 0.0;
    for (i = 0; i < 31; i++) {
      di = (vs - uvt[i].v) - uvt[i].t * (us - uvt[i].u);
      if ( (i > 0) &&
          ( ( (di < 0.0) && (dm >= 0.0)) || ( (di >= 0.0) && (dm < 0.0)))) {
        break; /* found lines bounding (us, vs) : i-1 and i */
      }
      dm = di;
    }
    if (i == 31) {
      return ( -1); /* bad XYZ input, color temp would be less than minimum of 1666.7 degrees, or too far towards blue */
    }
    di = di / Math.sqrt(1.0 + uvt[i].t * uvt[i].t);
    dm = dm / Math.sqrt(1.0 + uvt[i - 1].t * uvt[i - 1].t);
    p = dm / (dm - di); /* p = interpolation parameter, 0.0 : i-1, 1.0 : i */
    p = 1.0 / (lerp(rt[i - 1], rt[i], p));
    return p;
  }

  public static boolean isCCTMeaningful(CIExyY xyY) {
    double duv = getduvWithBlackbody(xyY.toXYZ());
    return duv <= 5e-2;
  }

  private final static class UVT {
    public UVT(double[] uvt) {
      u = uvt[0];
      v = uvt[1];
      t = uvt[2];
    }

    public UVT(double u, double v, double t) {
      this.u = u;
      this.v = v;
      this.t = t;
    }

    double u, v, t;
  }

  public final static double[] getdudvWithDIlluminant(CIEXYZ XYZ) {
    double cct = XYZ2CCTByRobertson(XYZ);
    CIExyY dxyY = CCT2DIlluminantxyY( (int) cct);
    CIExyY xyY = new CIExyY(XYZ);
    double[] duv = dxyY.getDeltauv(xyY);
    return duv;
  }

  public final static double getduvWithDIlluminant(CIEXYZ XYZ) {
    double[] duv = getdudvWithDIlluminant(XYZ);
    double d = Math.sqrt(Maths.sqr(duv[0]) + Maths.sqr(duv[1]));
    if (duv[0] < 0 || duv[1] > 0) {
      d = -d;
    }
    return d;
  }

  public final static double[] getdudvWithBlackbody(CIEXYZ XYZ) {
    double cct = XYZ2CCTByRobertson(XYZ);
    CIEXYZ blackbodyXYZ = getSpectraOfBlackbodyRadiator( (int) cct).getXYZ();
    CIExyY bbxyY = CIExyY.fromXYZ(blackbodyXYZ);
    CIExyY xyY = CIExyY.fromXYZ(XYZ);
    double[] duv = bbxyY.getDeltauv(xyY);
    return duv;
  }

  public final static double getduvWithBlackbody(CIEXYZ XYZ) {
    double[] duv = getdudvWithBlackbody(XYZ);
    double d = Math.sqrt(Maths.sqr(duv[0]) + Maths.sqr(duv[1]));
    if (duv[0] < 0 || duv[1] > 0) {
      d = -d;
    }
    return d;
  }

  /**
   * 計算黑體輻射的光譜
   * @param tempK int
   * @return Spectra
   */
  public final static Spectra getSpectraOfBlackbodyRadiator(int tempK) {
    return getSpectraOfBlackbodyRadiator(tempK, 360, 830, 1);
  }

  public final static Spectra getSpectraOfBlackbodyRadiator(int tempK,
      int start, int end, int interval) {
    int size = (end - start) / interval + 1;
    double[] data = new double[size];
    for (int x = 0; x < size; x++) {
      double lambda = (start + x * interval) * 1E-9;
      data[x] = c1 /
          (Math.pow(lambda, 5) * (Math.pow(Math.E, c2 / (tempK * lambda)) - 1));
    }
    Spectra spectra = new Spectra("Blackbody " + tempK + "K",
                                  Spectra.SpectrumType.EMISSION,
                                  start, end, interval, data);
    spectra.normalizeDataToMax();
    return spectra;

  }

  /**
   * 黑體輻射相關常數
   */
  private final static double c = 2.99792458E8;
  private final static double h = 6.626176E-34;
  private final static double k = 1.380662E-23;
  private final static double c1 = 2 * Math.PI * h * Maths.sqr(c);
  private final static double c2 = h * c / k;
}
