package shu.cms.hvs.gradient;

import java.awt.*;

import shu.cms.plot.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �g�Ѥ߲z���z����ұo�쪺���G
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GSDFThresholdModel {
  public static enum Threshold {
    //�i�P��
    Perceptible,
    //�i����
    Acceptable
  }

  /**
   * ��J�������Ѽ�, �p��X�H�� (�i�����Υi�P��)
   * @param thresholdType Threshold �i�����Υi�P��
   * @param JNDIndex double �ӫG�׹�����H����JNDI
   * @param meanLuminance double �����G��(in nits)
   * @param percent double �H�ȫإ߰�Ǫ��ʤ���
   * @return double
   */
  public final static double getThreshold(Threshold thresholdType,
                                          double JNDIndex,
                                          double meanLuminance,
                                          double percent) {
    switch (thresholdType) {
      case Perceptible:
        return getPerceptibleThreshold(JNDIndex, percent);
      case Acceptable:
        return getAcceptableThreshold(JNDIndex, meanLuminance, percent);
      default:
        return -1;
    }

  }

  /**
   * ��J�������Ѽ�, �p��X�i�������H��
   * @param JNDIndex double �ӫG�׹�����H����JNDI
   * @param meanLuminance double �����G��(in nits)
   * @param percent double �H�ȫإ߰�Ǫ��ʤ���
   * @return double
   */
  public final static double getAcceptableThreshold(double JNDIndex,
      double meanLuminance, double percent) {
    double c = 0.006227886852165916 * meanLuminance +
        0.012661733849871506 * percent;
    double a = -2.043562016066678E-6 * meanLuminance +
        4.8126642412383265E-5 * percent;
    double y = c + a * JNDIndex;
    return y;
  }

  /**
   * ��J�������Ѽ�, �p��X�i�P�����H��
   * @param JNDIndex double �ӫG�׹�����H����JNDI
   * @param percent double �H�ȫإ߰�Ǫ��ʤ���
   * @return double
   */
  public final static double getPerceptibleThreshold(double JNDIndex,
      double percent) {
    double c = 0.010918565886534823 * percent;
    double a = 3.088669791236479E-5 * percent;
    double y = c + a * JNDIndex;
    return y;
  }

  public static void main(String[] args) {
    Plot2D p2 = Plot2D.getInstance();
    double[] meanLArray = new double[] {
//        50, 100, 150, 200};
        50};
    Color[] colorArray = new Color[] {
        Color.red, Color.green, Color.blue, Color.black};
    for (int x = 0; x < meanLArray.length; x++) {
      double meanL = meanLArray[x];
      Color c = colorArray[x];

      for (double per : new double[] {25, 50, 75}) {
//        for (double per : new double[] { 50}) {
        for (double jndi = 40; jndi <= 700; jndi += 10) {
          double pt = GSDFThresholdModel.getThreshold(
              Threshold.Perceptible, jndi, meanL, per);
          double at = GSDFThresholdModel.getThreshold(
              Threshold.Acceptable, jndi, meanL, per);
          p2.addCacheScatterLinePlot("o" + meanL + " " + per + "%", c, jndi, pt);
//          p2.addCachexyLinePlot("x" + meanL + " " + per + "%", c, jndi, at);
        }
      }
    }
    p2.setAxeLabel(0, "JNDI");
    p2.setAxeLabel(1, "delta JNDI\"");
    p2.addLegend();
    p2.setVisible();
  }
}
