package shu.cms.colororder;

import shu.cms.colorspace.independ.*;
import shu.math.array.*;
import shu.math.*;

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
public class OSAUniformColorScales {
  public static void main(String[] args) {
    CIExyY xyY = new CIExyY(.2926, .2878, 48.63);
//    CIExyY xyY = new CIExyY(.2529, .2238, 13.44);
    xyY.setDegree(CIEXYZ.Degree.Ten);
    System.out.println(forward(xyY));
//    System.out.println(OSAUniformColorScales.forward(xyY.toXYZ()));
  }

  public final static OSAUCSColor forward(CIExyY tenDegxyY) {
    return forward(tenDegxyY.toXYZ());
  }

  public final static OSAUCSColor forward(CIEXYZ tenDegXYZ) {
    if (tenDegXYZ.getDegree() != CIEXYZ.Degree.Ten) {
      throw new IllegalArgumentException(
          "tenDegXYZ.getDegree() != CIEXYZ.Degree.Ten");
    }

    CIExyY xyY = new CIExyY(tenDegXYZ);
    double Ybar = xyY.Y *
        (4.4934 * Maths.sqr(xyY.x) + 4.3034 * Maths.sqr(xyY.y) -
         4.276 * xyY.x * xyY.y - 1.3744 * xyY.x - 2.56439 * xyY.y + 1.8103);
    double Ybarcr_23 = Maths.cubeRoot(Ybar) - (2. / 3);
    double Ybar_30 = Ybar - 30;
    double Ybar_30_cr = Maths.cubeRoot(Ybar_30);
    double lamda = 0;
    if (Ybar <= 30) {
      lamda = 5.9 * (Ybarcr_23 - 0.042 * Ybar_30_cr);
    }
    else {
      lamda = 5.9 * (Ybarcr_23 + 0.042 * Ybar_30_cr);
    }
    double L = (lamda - 14.4) / Math.sqrt(2);

    double[] XYZValues = tenDegXYZ.getValues();
    double[] rgbValues = DoubleArray.times(
        M.square(0.799, 0.4194, -0.1648,
                 -0.4493, 1.3265, 0.0927,
                 -0.1149, 0.3394, 0.717)
        , XYZValues);
    double[] rgbcrValues = new double[] {
        Maths.cubeRoot(rgbValues[0]), Maths.cubeRoot(rgbValues[1]),
        Maths.cubeRoot(rgbValues[2])};
    double C = lamda / (5.9 * Ybarcr_23);
    double g = C *
        DoubleArray.times(new double[] { -13.7, 17.7, -4}, rgbcrValues);
    double j = C *
        DoubleArray.times(new double[] {1.7, 8, -9.7}, rgbcrValues);
    OSAUCSColor color = new OSAUCSColor(L, j, g);
    return color;
  }

}
