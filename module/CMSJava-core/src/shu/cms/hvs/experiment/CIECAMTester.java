package shu.cms.hvs.experiment;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 測試將Lab轉到一個RGB色彩空間中,確定這些Lab顏色100%為人眼可見.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CIECAMTester {
  public static void main(String[] args) {
    CIEXYZ D50 = Illuminant.D50WhitePoint;
    double LStep = (100 - 0) / 32.;
    double abStep = (127. - ( -128.)) / 32;
//    CIEXYZ XYZ = new CIEXYZ();
    CIELab Jab = new CIELab();
    CIELab Lab = new CIELab();

    ViewingConditions vc =
        ViewingConditions.PerceptualIntentViewingConditions;
    CIECAM02 ciecam02 = new CIECAM02(vc);

    int illegalJabCount = 0;
    int illegalXYZCount = 0;
    int total = 0;

    for (double L = 0; L <= 100; L += LStep) {
      for (double a = -128; a <= 127; a += abStep) {
        for (double b = -128; b <= 127; b += abStep) {
          total++;

          Lab.L = L;
          Lab.a = a;
          Lab.b = b;
          CIEXYZ XYZ = Lab.toXYZ(Lab, D50);
          XYZ.rationalize(D50);

          RGB rgb = RGB.XYZ2LinearRGB(XYZ, RGB.ColorSpace.BetaRGB);
          if (!rgb.isLegal()) {
            rgb.rationalize();
            XYZ = rgb.toXYZ(rgb);
          }

          CIECAM02Color color = ciecam02.forward(XYZ);
          Jab.setValues(color.getJabcValues());

          if (!Jab.isLegal()) {
//            Debugger.debug(Lab + " " + XYZ + " " + Jab + " " + CIExyY.XYZ2xyY(XYZ));
            illegalJabCount++;
          }
          CIEXYZ invXYZ = ciecam02.inverse(color);
          if (!invXYZ.isLegal(D50)) {
            illegalXYZCount++;
          }
        }
      }
    }

    System.out.println("illegalJabCount:" + illegalJabCount);
    System.out.println("illegalXYZCount:" + illegalXYZCount);
    System.out.println("total:" + total);
  }
}
