package shu.cms.hvs.gradient;

import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �w�����ե~����model
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GSDFGradientModel
    extends GradientModel {

  private final static boolean supportLCDTarget(LCDTarget lcdTarget) {
    if (lcdTarget.getNumber() != LCDTargetBase.Number.Ramp1021 &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp1024 &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp256W &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp256R_W &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp256G_W &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp256B_W) {
      return false;
    }
    else {
      return true;
    }

  }

  /**
   * ���_lcdTarget�U��ch�����h�O�_����
   * @param lcdTarget LCDTarget
   * @param ch Channel
   * @return boolean
   */
  public final static boolean isSmooth(LCDTarget lcdTarget, RGBBase.Channel ch) {
    if (!supportLCDTarget(lcdTarget)) {
      throw new IllegalArgumentException(
          "lcdTarget's number:" + lcdTarget.getNumber() + " is not support.");
    }
    //2�׵����O��SCIELAB�Ϊ�, ���O�o��ä��ĥ�SCIELAB, �ҥH�H�K���@�ӭ�
    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);

    return gm.isSmooth(ch);
  }

  public final static boolean isSmooth(double[] YArray) {
    GSDFGradientModel gm = new GSDFGradientModel(YArray);
    return gm.isSmooth();
  }

  /**
   * ���_lcdTarget�����h�O�_����
   * @param lcdTarget LCDTarget
   * @return boolean
   */
  public final static boolean isSmooth(LCDTarget lcdTarget) {
    if (!supportLCDTarget(lcdTarget)) {
      throw new IllegalArgumentException(
          "lcdTarget's number:" + lcdTarget.getNumber() + " is not support.");
    }
    //2�׵����O��SCIELAB�Ϊ�, ���O�o��ä��ĥ�SCIELAB, �ҥH�H�K���@�ӭ�
    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);
    boolean smooth = true;

    for (RGBBase.Channel ch : RGBBase.Channel.RGBWChannel) {
      smooth = smooth && gm.isSmooth(ch);
    }

    return smooth;
  }

  public GSDFGradientModel(LCDTarget target) {
    super(target);
  }

  public GSDFGradientModel(LCDTarget target, CIEXYZ white) {
    super(target, white);
  }

  public GSDFGradientModel(LCDModel lcdModel) {
    super(lcdModel);
  }

  public GSDFGradientModel(LCDModel lcdModel, CIEXYZ white) {
    super(lcdModel, white);
  }

  public GSDFGradientModel(double[] YArray) {
    super(YArray);
  }

  public GSDFGradientModel() {

  }

  /**
   *
   * @param dataArray double[]
   * @param isLuminance boolean ��ƪ����O true:�O�G�� false:�OJNDI
   */
  public GSDFGradientModel(double[] dataArray, boolean isLuminance) {
    super(dataArray, isLuminance);
  }

  protected double[] getFirstOrderSignal() {
    double[] jndiCurve = this.getJNDIndexCurve();
    double[] firstOrder = Maths.firstOrderDerivatives(jndiCurve);
    return firstOrder;
  }

  /**
   * �N�����Z�I��signal�[�`����score, �ΨӧP�_�����{��
   * @param patternIndexAndSignal double[][]
   * @return double
   */
  protected final static double getPatternScore(double[][]
                                                patternIndexAndSignal) {
    double score = 0;
    for (double[] signal : patternIndexAndSignal) {
      score += signal[1];
    }
    return score;
  }

  protected static double getMaxHeight(double[] patternArray) {
    return Maths.max(patternArray);
  }

  /**
   * �w�]�ĥΪ�pattern���t��
   */
  private PatternSign patternSign = PatternSign.Threshold;

  /**
   * ���o������Pattern�H�Ψ�smooth������
   * @return PatternAndScore
   */
  public final PatternAndScore getPatternAndScore() {
    List<Pattern> patternList = getPatternIndex(patternSign);
    double score = getPatternScore(patternList);
    double overScore = getOverScore(patternList);
    double[] signal = this.getJNDIndexCurve();
    double[] deltaaArray = this.getDeltaAccelArray();
    PatternAndScore ps = new PatternAndScore(patternList, score, overScore,
                                             signal, deltaaArray);
    return ps;
  }

  public static class Parameter {
    public GSDFThresholdModel.Threshold thresholdType = GSDFThresholdModel.
        Threshold.Acceptable;
    public PatternSign patternSign = PatternSign.Positive;
    public double thresholdPercent = 10;
    public double signalFixedThreshold = 2;
  }

  /**
   * �]�w�������Ѽ�
   * @param p Parameter
   */
  public void setParameter(Parameter p) {
    this.setThresholdType(p.thresholdType);
    this.setThresholdPercent(p.thresholdPercent);
    this.setSignalFixedThreshold(p.signalFixedThreshold);
  }

//  private PatternType patternType = PatternType.DVary;
  private GSDFThresholdModel.Threshold thresholdType = GSDFThresholdModel.
      Threshold.Acceptable;

//  public static enum PatternType {
//    //�̨S�����D��pattern
//    DVary
//  }

  /**
   * �p�⥭���G��
   * @return double
   */
  protected final double getMeanLuminance() {
    //���oJNDI��curve
    double[] signal = this.getJNDIndexCurve();
    int signalsize = signal.length;
    double total = 0;
    for (int x = 0; x < signalsize; x++) {
      double s = signal[x];
      double Y = this.getLuminance(s);
      total += Y;
    }
    double mean2 = total / signalsize;
    return mean2;
  }

  public List<Pattern> getAllPatternIndex() {
    return getPatternIndex(PatternSign.All);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �ĥΪ�Pattern�����t��
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum PatternSign {
    /**
     * ���ץ��t���Mthreshold �����C�X��
     */
    All,
    /**
     * �W�Xthreshold�B�O����
     */
    Positive,
    /**
     * �W�Xthreshold�B�O�t��
     */
    Negative,
    /**
     * �W�Xthreshold�B���ץ��t��
     */
    Threshold
  }

  /**
   * �p�⤣��pattern
   * @param type PatternSign
   * @return List
   */
  public List<Pattern> getPatternIndex(PatternSign type) {
    double[] signal = this.getJNDIndexCurve();
    double[] firstOrderSignal = Maths.firstOrderDerivatives(signal);
    double[] secondOrderSignal = Maths.firstOrderDerivatives(firstOrderSignal);
    int size = secondOrderSignal.length - 1;

    double meanY = getMeanLuminance();
    patternArray = new double[size];
    List<Pattern> patternList = new ArrayList<Pattern> (size);

    for (int x = 0; x < size; x++) {
      int signalIndex = x + 1;
      double pattern = secondOrderSignal[x];
      patternArray[x] = pattern;
      double JNDIndex = signal[signalIndex];
      double threshold = GSDFThresholdModel.getThreshold(thresholdType,
          JNDIndex, meanY, thresholdPercent);
      if (type == PatternSign.All || //����
          (type == PatternSign.Positive && pattern > threshold) || //����
          (type == PatternSign.Negative && pattern < -threshold) || //�t��
          (type == PatternSign.Threshold && //�u�n�W�Xthreshold, ���ץ��t
           (pattern > threshold || pattern < -threshold))) {
        //�p�Gpattern > threshold
        double[] accel = new double[] {
            firstOrderSignal[x], firstOrderSignal[x + 1]};
        float overRatio = (float) (pattern / threshold * 100.);
        Pattern p = new Pattern(signalIndex, JNDIndex, pattern, accel,
                                overRatio, pattern > 0 ? threshold : -threshold);
        patternList.add(p);
      }
    }
    return patternList;
  }

  protected double[] patternArray;

  public static void main(String[] args) {
    //==========================================================================
    // xtalk LCD model
    //==========================================================================
    LCDTarget.setRGBNormalize(false);
    String device = "hisense_TLM26V68";
    String dirtag = "���G";
    String tag = "";
    LCDTarget.FileType fileType = LCDTarget.FileType.AUORampXLS;
    LCDTarget.Source source = LCDTarget.Source.CA210;

    LCDTarget lcdTarget = LCDTarget.Instance.get(device, source,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1024,
                                                 fileType, dirtag, tag);

    //==========================================================================

//    LCDTarget lcdTarget2 = LCDTarget.Instance.getFromLogo("9300.logo");
//    CIExyY[] targetxyY = lcdTarget2.filter.xyYArray();

    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);
    gm.setPatternSign(PatternSign.Threshold);
    PatternAndScore ps = gm.getPatternAndScore();
    for (Pattern p : ps.patternList) {
      System.out.println(p);
    }

//    gm.setTargetxyYArray(targetxyY);
    gm.plotPatternAccelInfo(null, true, true);

//    double[] YArray = lcdTarget2.filter.YArray();
//    gm = new GSDFGradientModel(YArray);
    System.out.println(gm.isSmooth());

  }

  /**
   * �]�w�ؼЪ��G�צ��u
   * @param targetxyYCurve CIExyY[]
   */
  public void setTargetxyYArray(CIExyY[] targetxyYCurve) {
    this.targetxyYCurve = targetxyYCurve;
  }

  /**
   * �]�w�ؼЪ��G�צ��u(�ĥ�xyY����]�b��, �n�Ҽ{HK����, �ҥH�n����O�d��שM�G�ת���T)
   */
  private CIExyY[] targetxyYCurve;

  /**
   * �ؼЪ��[�t�ת��}�C
   */
  private double[] targetAccelArray;

  public double[] getTargetAccelArray() {
    return targetAccelArray;
  }

  /**
   * �ؼЪ�JNDI�}�C
   */
  private double[] targetJNDIArray;
  /**
   * ��ڪ��[�t�ת��}�C
   */
  private double[] actualAccelArray;
  /**
   * �[�t�׮t�}�C
   */
  private double[] deltaAccelArray;
  /**
   * JNDI�t���}�C
   */
  private double[] deltaJNDIArray;

  public void statistics() {
    if (patternArray == null) {
      throw new IllegalStateException("patternArray == null");
    }

    actualAccelArray = this.getFirstOrderSignal();

    if (targetxyYCurve != null) {
      targetJNDIArray = getJNDIndexCurve(targetxyYCurve);
      targetAccelArray = Maths.firstOrderDerivatives(targetJNDIArray);

      deltaAccelArray = DoubleArray.minus(actualAccelArray, targetAccelArray);
      double[] signal = this.getJNDIndexCurve();
      deltaJNDIArray = DoubleArray.minus(signal, this.targetJNDIArray);
    }

  }

  /**
   * ø�Xpattern�[�t�ת���T
   * @param plotAccelPrime boolean
   * @param plotDeltaTarget boolean
   * @return Plot2D
   */
  public Plot2D plotPatternAccelInfo(boolean plotAccelPrime,
                                     boolean plotDeltaTarget) {
    return plotPatternAccelInfo(null, plotAccelPrime, plotDeltaTarget);
  }

  /**
   * ø�Xpattern�[�t�ת���T
   * @param plot Plot2D
   * @param plotAccelPrime boolean
   * @param plotDeltaTarget boolean
   * @return Plot2D
   */
  public Plot2D plotPatternAccelInfo(Plot2D plot, boolean plotAccelPrime,
                                     boolean plotDeltaTarget) {
    if (plot == null) {
      plot = Plot2D.getInstance("Pattern Acceleration Info");
    }
    plot.addLegend();

    List<Pattern> patternList = getAllPatternIndex();
    statistics();

    int size = patternList.size();

    if (targetxyYCurve != null) {
      plot.addLinePlot("target a", Color.blue, 1, targetAccelArray.length,
                       targetAccelArray);
      if (plotDeltaTarget) {
        plot.addLinePlot("delta a", Color.magenta, 1, deltaAccelArray.length,
                         deltaAccelArray);
        plot.addLinePlot("delta", Color.black, 0,
                         this.deltaJNDIArray.length - 1,
                         deltaJNDIArray);
      }
    }

    plot.addLinePlot("(init)a", Color.red, 1, this.actualAccelArray.length,
                     actualAccelArray);

    for (int x = 0; x < size; x++) {
      Pattern pattern = patternList.get(x);
      int index = pattern.index;
      if (plotAccelPrime) {
        double th = Math.abs(pattern.threshold);
        plot.addCacheScatterLinePlot("th", Color.lightGray, index, th);
        plot.addCacheScatterLinePlot("-th", Color.lightGray, index, -th);
        plot.addCacheScatterLinePlot("a'", Color.green, index, pattern.pattern);
      }
    }

    plot.setVisible();
    plot.setLinePlotDrawDot(true);
    plot.setFixedBounds(0, 0, size);
    plot.setAxeLabel(0, "code");
    plot.setAxeLabel(1, "JNDI");
    return plot;
  }

  /**
   * �Npattern�e�X
   * @param absolute boolean �O�_�n�Npattern���ƭȥ[�W�����
   * @return Plot2D
   */
  public Plot2D plotPattern(boolean absolute) {
    Plot2D plot = Plot2D.getInstance("Pattern");
    plot.addLegend();

    getAllPatternIndex();
    double[] pattern = patternArray;
    if (absolute) {
      DoubleArray.abs(pattern);
    }
    plot.addLinePlot("pattern", 1, pattern.length, pattern);
    plot.setFixedBounds(0, 0, pattern.length);
    plot.setLinePlotDrawDot(true);
    plot.setVisible(true);
    return plot;

  }

  /**
   * �]�wthreshold��type (�i�P���Υi����)
   * @param thresholdType Threshold
   */
  public void setThresholdType(GSDFThresholdModel.Threshold thresholdType) {
    this.thresholdType = thresholdType;
  }

  /**
   * �]�wpattern�����t��
   * @param patternSign PatternSign
   */
  public void setPatternSign(PatternSign patternSign) {
    this.patternSign = patternSign;
  }

  /**
   * �[�t�׮t�}�C
   * @return double[]
   */
  public double[] getDeltaAccelArray() {
    return deltaAccelArray;
  }

  public double getTotalDeltaa() {
    if (deltaAccelArray != null) {
      double[] absDelta = DoubleArray.copy(deltaAccelArray);
      DoubleArray.abs(absDelta);
      return Maths.sum(absDelta);
    }
    else {
      return -1;
    }
  }

  /**
   * JNDI�t���}�C
   * @return double[]
   */
  public double[] getDeltaJNDIArray() {
    return deltaJNDIArray;
  }

  /**
   * �[�t�װ}�C
   * @return double[]
   */
  public double[] getActualAccelArray() {
    return actualAccelArray;
  }
}
