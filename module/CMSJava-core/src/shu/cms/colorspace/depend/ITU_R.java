package shu.cms.colorspace.depend;

import shu.math.array.DoubleArray;

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
public enum ITU_R {
  //HDTV的標準, 相當於sRGB
  BT709(0.212656, 0.072186),
  //NTSC的新版本, 也就是SMPTE-C
  SMPTE_C(0.212395, 0.086556),
  //NTSC
  BT601(0.298839, 0.114350),
  Simplify(0.25, 0.25);

//    BT601(0.299, 0.114);


  public final double Kb, Kr, Kg;
//    double Wr, Wg, Wb;

  ITU_R(double Kr, double Kb /*, double Wr, double Wb*/) {
    this.Kb = Kb;
    this.Kr = Kr;
    this.Kg = 1 - Kr - Kb;
//        this.Wr = Wr;
//        this.Wb = Wb;
//        this.Wg = 1 - Wr - Wb;
  }

  public static ITU_R getITU_R(RGB.ColorSpace rgbColorSpace) {
    if (rgbColorSpace.equals(RGB.ColorSpace.sRGB)) {
      return BT709;
    }
    else if (rgbColorSpace.equals(RGB.ColorSpace.SMPTE_C)) {
      return SMPTE_C;
    }
    else if (rgbColorSpace.equals(RGB.ColorSpace.NTSCRGB)) {
      return BT601;
    }
    else if (rgbColorSpace.equals(RGB.ColorSpace.unknowRGB)) {
      return Simplify;
    }

    throw new IllegalArgumentException("Unsupport colorspace: " +
                                       rgbColorSpace);
  }

  public final double[][] getToRGBMatrix() {
    if (this != Simplify) {
      return DoubleArray.inverse(getFromRGBMatrix());
    }
    else {
      throw new java.lang.UnsupportedOperationException();
    }
  }

  public final double[][] getFromRGBMatrix() {
    double[][] m = new double[3][3];
    if (this != Simplify) {

      m[0][0] = Kr;
      m[0][1] = Kg;
      m[0][2] = Kb;

//        m[1][0] = 0.436 * ( -Wr) / (1 - Wb);
//        m[1][1] = 0.436 * ( -Wg) / (1 - Wb);
//        m[1][2] = 0.436 * (1 - Wb) / (1 - Wb);
//
//        m[2][0] = 0.615 * (1 - Wr) / (1 - Wr);
//        m[2][1] = 0.615 * ( -Wg) / (1 - Wr);
//        m[2][2] = 0.615 * ( -Wb) / (1 - Wr);

      m[1][0] = 0.5 * ( -Kr) / (1 - Kb);
      m[1][1] = 0.5 * ( -Kg) / (1 - Kb);
      m[1][2] = 0.5 * (1 - Kb) / (1 - Kb);

      m[2][0] = 0.5 * (1 - Kr) / (1 - Kr);
      m[2][1] = 0.5 * ( -Kg) / (1 - Kr);
      m[2][2] = 0.5 * ( -Kb) / (1 - Kr);
    }
    else {
      throw new java.lang.UnsupportedOperationException();
    }

    return m;
  }

}
