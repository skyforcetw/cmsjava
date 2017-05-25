package shu.cms.measure.verify;

import shu.cms.*;
import shu.cms.lcd.*;
import shu.cms.measure.util.StabilityAnalyzer.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 儀器重複性的驗證
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class Repeatability {

  protected LCDTarget[] targets;

  public Repeatability(LCDTarget[] targets) {
    analyzeMultiLCDTarget(targets);
    this.targets = targets;
  }

  /**
   * 分析可供用來計算mcdm的色塊數量及開始的索引值
   * @param lcdTarget LCDTarget
   * @return int[]
   */
  protected int[] analyzeAvailableSizeAndStartIndex(LCDTarget lcdTarget) {
    int size = lcdTarget.size();

    int availableSize = 0;
    int startIndex = -1;
    for (int x = 0; x < size; x++) {
      Patch p = lcdTarget.getPatch(x);
      if (p.getXYZ().Y > 0 && p.getRGB().isGray()) {
        availableSize++;
        startIndex = startIndex == -1 ? x : startIndex;
      }

    }
    return new int[] {
        availableSize, startIndex};
  }

  protected void analyzeMultiLCDTarget(LCDTarget[] targets) {
    MultiCIExyYStabilityAnalyzer msa = new MultiCIExyYStabilityAnalyzer(
        targets);
    MultiStabilityAnalyzer msa2 = new MultiStabilityAnalyzer(targets,
        DeltaE.Formula.CIE2000);
    int patchSize = msa.size();

    double[] xRepeatability = new double[patchSize];
    double[] yRepeatability = new double[patchSize];
    double[] YRepeatability = new double[patchSize];
    double[] xStd = new double[patchSize];
    double[] yStd = new double[patchSize];
    double[] YStd = new double[patchSize];
    double[] xMCDM = new double[patchSize];
    double[] yMCDM = new double[patchSize];
    double[] YMCDM = new double[patchSize];
    double[] xMean = new double[patchSize];
    double[] yMean = new double[patchSize];
    double[] YMean = new double[patchSize];
    double[] luminance = new double[patchSize];
    double[] deltaEMCDMArray = new double[patchSize];

    for (int x = 0; x < patchSize; x++) {
      Patch fp = msa.getFirstPatch(x);

      luminance[x] = fp.getXYZ().Y;
      xRepeatability[x] = msa.getMax(x, CIExyYStabilityAnalyzer.Target.x);
      yRepeatability[x] = msa.getMax(x, CIExyYStabilityAnalyzer.Target.y);
      YRepeatability[x] = msa.getMax(x, CIExyYStabilityAnalyzer.Target.Y);

      xStd[x] = msa.getSTD(x, CIExyYStabilityAnalyzer.Target.x);
      yStd[x] = msa.getSTD(x, CIExyYStabilityAnalyzer.Target.y);
      YStd[x] = msa.getSTD(x, CIExyYStabilityAnalyzer.Target.Y);

      xMCDM[x] = msa.getMCDM(x, CIExyYStabilityAnalyzer.Target.x);
      yMCDM[x] = msa.getMCDM(x, CIExyYStabilityAnalyzer.Target.y);
      YMCDM[x] = msa.getMCDM(x, CIExyYStabilityAnalyzer.Target.Y);

      xMean[x] = msa.getMean(x, CIExyYStabilityAnalyzer.Target.x);
      yMean[x] = msa.getMean(x, CIExyYStabilityAnalyzer.Target.y);
      YMean[x] = msa.getMean(x, CIExyYStabilityAnalyzer.Target.Y);
      deltaEMCDMArray[x] = msa2.getMCDM(x);
    }

    xMaxInterpolation = new Interpolation(luminance, xRepeatability);
    yMaxInterpolation = new Interpolation(luminance, yRepeatability);
    YMaxInterpolation = new Interpolation(luminance, YRepeatability);

    xStdInterpolation = new Interpolation(luminance, xStd);
    yStdInterpolation = new Interpolation(luminance, yStd);
    YStdInterpolation = new Interpolation(luminance, YStd);

    xMCDMInterpolation = new Interpolation(luminance, xMCDM);
    yMCDMInterpolation = new Interpolation(luminance, yMCDM);
    YMCDMInterpolation = new Interpolation(luminance, YMCDM);

    xMeanInterpolation = new Interpolation(luminance, xMean);
    yMeanInterpolation = new Interpolation(luminance, yMean);
    YMeanInterpolation = new Interpolation(luminance, YMean);

    deltaEMCDMInterpolation = new Interpolation(luminance, deltaEMCDMArray);
    meanDeltaEMCDM = Maths.mean(deltaEMCDMArray);
  }

  protected double meanDeltaEMCDM;
  protected Interpolation deltaEMCDMInterpolation;
  protected Interpolation xMaxInterpolation;
  protected Interpolation yMaxInterpolation;
  protected Interpolation YMaxInterpolation;

  protected Interpolation xStdInterpolation;
  protected Interpolation yStdInterpolation;
  protected Interpolation YStdInterpolation;

  protected Interpolation xMCDMInterpolation;
  protected Interpolation yMCDMInterpolation;
  protected Interpolation YMCDMInterpolation;

  protected Interpolation xMeanInterpolation;
  protected Interpolation yMeanInterpolation;
  protected Interpolation YMeanInterpolation;

  /**
   * 以實際測量的數值為基準所推算的重複性(max)
   * @param luminance double
   * @return double[] CIExyY
   */
  public final double[] getxyYMax(double luminance) {

    double[] xyY = new double[3];
    xyY[0] = xMaxInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[1] = yMaxInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[2] = YMaxInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[0] = xyY[0] < 0 ? 0 : xyY[0];
    xyY[1] = xyY[1] < 0 ? 0 : xyY[1];
    xyY[2] = xyY[2] < 0 ? 0 : xyY[2];
    return xyY;
  }

  /**
   * 取得該luminance下的xyY std(1 sigma)
   * @param luminance double
   * @return double[]
   */
  public final double[] getxyYSTD(double luminance) {

    double[] xyY = new double[3];
    xyY[0] = xStdInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[1] = yStdInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[2] = YStdInterpolation.interpolate(luminance,
                                           Interpolation.Algo.Linear);
    xyY[0] = xyY[0] < 0 ? 0 : xyY[0];
    xyY[1] = xyY[1] < 0 ? 0 : xyY[1];
    xyY[2] = xyY[2] < 0 ? 0 : xyY[2];
    return xyY;
  }

  public final double getMeanDeltaEMCDM() {
    return meanDeltaEMCDM;
  }

  public final double getDeltaEMCDM(double luminance) {
    return deltaEMCDMInterpolation.interpolate(luminance,
                                               Interpolation.Algo.Linear);
  }

  /**
   * 取得該luminance下的xyY mcdm(均值色差的平均)
   * @param luminance double
   * @return double[]
   */
  public final double[] getxyYMCDM(double luminance) {

    double[] xyY = new double[3];
    xyY[0] = xMCDMInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[1] = yMCDMInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[2] = YMCDMInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[0] = xyY[0] < 0 ? 0 : xyY[0];
    xyY[1] = xyY[1] < 0 ? 0 : xyY[1];
    xyY[2] = xyY[2] < 0 ? 0 : xyY[2];
    return xyY;
  }

  /**
   * 取得該luminance下的xyY mean
   * @param luminance double
   * @return double[]
   */
  public final double[] getxyYMean(double luminance) {

    double[] xyY = new double[3];
    xyY[0] = xMeanInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[1] = yMeanInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[2] = YMeanInterpolation.interpolate(luminance,
                                            Interpolation.Algo.Linear);
    xyY[0] = xyY[0] < 0 ? 0 : xyY[0];
    xyY[1] = xyY[1] < 0 ? 0 : xyY[1];
    xyY[2] = xyY[2] < 0 ? 0 : xyY[2];
    return xyY;
  }

}
