package sky4s.test.wb;

import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class YCRCB {
  private static final double[][] M_RGB = new double[][] {
      {
      0.299, 0.587, 0.114}, {
      -0.169, -0.331, 0.500}, {
      0.500, -0.418, -0.082}
  };

  /**
   *
   * @param rgb RGB
   * @deprecated
   */
  public YCRCB(RGB rgb) {
    int[] rgbValue = rgb.getRGB();
    double[][] arA = new double[][] {
        {
        rgbValue[0]}, {
        rgbValue[1]}, {
        rgbValue[2]}
    };

    Matrix mA = new Matrix(arA);
    Matrix mRGB = new Matrix(M_RGB);
    Matrix mResult = mRGB.times(mA);
    double[][] result = mResult.getArray();

    setYCrCb(new int[] {
             (int) result[0][0], (int) result[1][0],
             (int) result[2][0]}
        );
  }

  /**
   *
   * @param args String[]
   * @deprecated
   */
  public static void main(String[] args) {
    RGB rgb = new RGB(200, 256, 200);
    YCRCB ycrcb = new YCRCB(rgb);
    System.out.println(ycrcb + " " + ycrcb.getChroma());
  }

  private int[] ycrcb = new int[3];

  public YCRCB() {
  }

  public YCRCB(int[] ycrcb) {
    setYCrCb(ycrcb);
  }

  public double getChroma() {
    return Math.sqrt(Math.pow(ycrcb[1], 2) + Math.pow(ycrcb[2], 2));
  }

  public void setYCrCb(int[] ycrcb) {
    this.ycrcb[0] = ycrcb[0];
    this.ycrcb[1] = ycrcb[1];
    this.ycrcb[2] = ycrcb[2];
  }

  public int[] getYCrCb() {
    return this.ycrcb;
  }

  public String toString() {
    return ycrcb[0] + "," + ycrcb[1] + "," + ycrcb[2];
  }
}
