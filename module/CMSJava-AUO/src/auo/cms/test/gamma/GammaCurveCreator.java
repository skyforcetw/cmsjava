package auo.cms.test.gamma;

import shu.math.lut.Interpolation1DLUT;

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
public class GammaCurveCreator {
  public GammaCurveCreator() {
    super();
  }

  public static void main(String[] args) {
    double[] keys = new double[] {
        0,
        16,
        32,
        48,
        64,
        80,
        96,
        112,
        128,
        144,
        160,
        176,
        192,
        208,
        224,
        240,
        255
    };
    double[] gammaValueArray = new double[] {
        2.075671913,
        2.075671913,
        2.167325819,
        2.197229486,
        2.213190007,
        2.22366349,
        2.229779376,
        2.234644702,
        2.241337698,
        2.242800909,
        2.237160695,
        2.235802918,
        2.241807519,
        2.237704454,
        2.203054312,
        2.162589129,
        2.162589129
    };

    Interpolation1DLUT lut = new Interpolation1DLUT(keys, gammaValueArray,
        Interpolation1DLUT.Algo.LINEAR);
//    double max = 388.303466796875;
//    double min = 0.416817188262939;

    for (int x = 255; x >= 0; x--) {
      double gamma = lut.getValue(x);
      double normal = x / 255.;
      double lumi = Math.pow(normal, gamma);
      System.out.println(x + " " + lumi);
    }

  }
}
