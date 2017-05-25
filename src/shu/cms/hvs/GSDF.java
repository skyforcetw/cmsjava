package shu.cms.hvs;

import shu.cms.colorformat.file.ExcelFile;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * DICOM規格裡的GSDF,可用來計算平滑度
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class GSDF {

  public static void main(String[] args) throws Exception {
    GSDF dicom = GSDF.getDICOMInstance();
    System.out.println(dicom.getJNDIndex(225.9));
    System.out.println(dicom.getJNDIndex(0.006));
//    ExcelFile xls = new ExcelFile("jndi.xls", true);
//
//    xls.setCell(0, 0, "Lumi");
//    xls.setCell(1, 0, "JNDI");
//    for (int x = 1; x <= 255; x++) {
//      double jndi = dicom.getJNDIndex(x);
//      xls.setCell(0, x, x);
//      xls.setCell(1, x, jndi);
//    }
//    xls.close();
  }

  public final static double[] getJNDICurve(double[] YCurve, GSDF gsdf) {
    int size = YCurve.length;
    double[] JNDICurve = new double[size];
    for (int x = 0; x < size; x++) {
      JNDICurve[x] = gsdf.getJNDIndex(YCurve[x]);
    }
    return JNDICurve;
  }

  public final static DICOM DICOM = new DICOM();
  public static class DICOM
      extends GSDF {

    /**
     * 是否在公式誤差的範圍內
     * @param luminance1 double
     * @param luminance2 double
     * @return boolean
     */
    public final static boolean isInDerviation(double luminance1,
                                               double luminance2) {
      double log1 = Math.log10(luminance1);
      double log2 = Math.log10(luminance2);
      double dv = (log1 - log2) / Math.max(log1, log2);
//      double dv2 = (log1 - log2) / Math.min(log1, log2);
      return dv < 0.03;
    }

    private final static double a = -1.3011877, b = -2.5840191E-2,
    c = 8.0242636E-2, d = -1.0320229E-1, e = 1.3646699E-1, f = 2.8745620E-2,
    g = -2.5468404E-2, h = -3.1978977E-3, k = 1.2992634E-4, m = 1.3635334E-3;

    private final static double A = 71.498068, B = 94.593053, C = 41.912053,
    D = 9.8247004, E = 0.28175407, F = -1.1878455, G = -0.18014349,
    H = 0.14710899, I = -0.017046845;

    public final double getJNDIndex(double luminance) {
      double log10L = Math.log10(luminance);
      double j = A + B * log10L + C * Math.pow(log10L, 2) +
          D * Math.pow(log10L, 3) +
          E * Math.pow(log10L, 4) + F * Math.pow(log10L, 5) +
          G * Math.pow(log10L, 6) + H * Math.pow(log10L, 7) +
          I * Math.pow(log10L, 8);
      return j;
    }

    public final double getLuminance(double JNDIndex) {
      double j = JNDIndex;
      double Lnj = Math.log(j);
      double log10L = (a + c * Lnj + e * Math.pow(Lnj, 2) + g * Math.pow(Lnj, 3) +
                       m * Math.pow(Lnj, 4)) /
          (1 + b * Lnj + d * Math.pow(Lnj, 2) + f * Math.pow(Lnj, 3) +
           h * Math.pow(Lnj, 4) + k * Math.pow(Lnj, 5));
      double L = Math.pow(10, log10L);
      return L;
    }
  }

  protected GSDF() {

  }

  public abstract double getJNDIndex(double luminance);

  public abstract double getLuminance(double JNDIndex);

  public double[] getLuminanceCurve(double[] JNDIArray) {
    int size = JNDIArray.length;
    double[] YArray = new double[size];
    for (int x = 0; x < size; x++) {
      YArray[x] = getLuminance(JNDIArray[x]);
    }
    return YArray;
  }

  public final static GSDF getDICOMInstance() {
    return DICOM;
  }
}
