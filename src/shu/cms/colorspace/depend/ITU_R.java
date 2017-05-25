package shu.cms.colorspace.depend;

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
  BT709(0.2126, 0.0722, 0.2126, 0.0722),
  //NTSC的新版本, 也就是SMPTE-C
  BT601(0.299, 0.114, 0.299, 0.114);

  double Kb, Kr, Kg;
  double Wr, Wg, Wb;

  ITU_R(double Kr, double Kb, double Wr, double Wb) {
    this.Kb = Kb;
    this.Kr = Kr;
    this.Kg = 1 - Kr - Kb;
    this.Wr = Wr;
    this.Wb = Wb;
    this.Wg = 1 - Wr - Wb;
  }

  public static ITU_R getITU_R(RGB.ColorSpace rgbColorSpace) {
    if (rgbColorSpace.equals(RGB.ColorSpace.sRGB)) {
      return BT709;
    }
    else if (rgbColorSpace.equals(RGB.ColorSpace.SMPTE_C)) {
      return BT601;
    }
    throw new IllegalArgumentException("Unsupport colorspace: " +
                                       rgbColorSpace);
  }

  final double[][] getFromRGBMatrix() {
    double[][] m = new double[3][3];
    m[0][0] = Wr;
    m[0][1] = Wg;
    m[0][2] = Wb;

    m[1][0] = 0.436 * ( -Wr) / (1 - Wb);
    m[1][1] = 0.436 * ( -Wg) / (1 - Wb);
    m[1][2] = 0.436 * (1 - Wb) / (1 - Wb);

    m[2][0] = 0.615 * (1 - Wr) / (1 - Wr);
    m[2][1] = 0.615 * ( -Wg) / (1 - Wr);
    m[2][2] = 0.615 * ( -Wb) / (1 - Wr);
    return m;
  }

}
