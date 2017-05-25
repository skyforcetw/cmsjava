package auo.cms.test;

import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import java.util.List;
import shu.math.regress.Regression;
import shu.math.array.*;
import Jama.LUDecomposition;
import Jama.Matrix;

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
public class CMTester {
  public static void main(String[] args) {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);

    double[][] a = new double[46][];
    double[][] b = new double[46][];
    for (int x = 0; x < 46; x++) {
      RGB rgb = rgbList.get(x);
      CIEXYZ XYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
      RGB argb = new RGB(RGB.ColorSpace.AdobeRGB, XYZ);
      a[x] = rgb.getValues();
      b[x] = argb.getValues(new double[3], RGB.MaxValue.Int8Bit);
    }
    Regression regress = new Regression(a, b);
    regress.regress();
    System.out.println(DoubleArray.toString(regress.getCoefs()));

    double[][] aT = DoubleArray.transpose(a);
    double[][] aTa = DoubleArray.times(aT, a);
    double[][] aTb = DoubleArray.times(aT, b);

//    System.out.println(DoubleArray.toString(aTa));
//    System.out.println(DoubleArray.toString(aTb));
    double[][] mR = new double[3][4];
    double[][] mG = new double[3][4];
    double[][] mB = new double[3][4];
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        mR[x][y] = aTa[x][y];
        mG[x][y] = aTa[x][y];
        mB[x][y] = aTa[x][y];
      }
      mR[x][3] = aTb[x][0];
      mG[x][3] = aTb[x][1];
      mB[x][3] = aTb[x][2];
    }

//    System.out.println(DoubleArray.toString(mR));
//    System.out.println(DoubleArray.toString(mG));
//    System.out.println(DoubleArray.toString(mB));

    LUDecomposition lu = DoubleArray.LU(aTa);
    Matrix ans = lu.solve(new Matrix(aTb));
    System.out.println(DoubleArray.toString(DoubleArray.transpose(ans.getArray())));
//    LUDecomposition lu = DoubleArray.LU(DoubleArray.transpose(mR));
//    System.out.println(DoubleArray.toString(lu.getL().getArray()));
//    System.out.println(DoubleArray.toString(lu.getU().getArray()));
  }
}
