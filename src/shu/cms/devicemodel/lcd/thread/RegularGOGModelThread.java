package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 先依據頻道計算出Gamma,再以迭代的方式計算Gain與offset
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RegularGOGModelThread
    extends GOGModelThread {

  /**
   *
   * @param flare CIEXYZ
   * @param lcdTarget LCDTarget
   * @deprecated
   */
  public RegularGOGModelThread(CIEXYZ flare, LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  public RegularGOGModelThread(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public RegularGOGModelThread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  public RegularGOGModelThread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

//  public RegularGOGModelThread(String modelFactorFilename) {
//  this( (LCDModelFactor) loadModelFactorFile(modelFactorFilename));
//}


  protected double rGamma, gGamma, bGamma;

  protected void produceGammaCorrector() {
    if (singleChannel.rChannelPatch == null || singleChannel.gChannelPatch == null ||
        singleChannel.bChannelPatch == null) {
      throw new IllegalStateException(
          "call produceRGBSingleChannelPatch() first.");
    }
    correct._RrCorrector = GammaCorrector.getExponentInstance(singleChannel.
        rChannelPatch, RGBBase.Channel.R);
    correct._GrCorrector = GammaCorrector.getExponentInstance(singleChannel.
        gChannelPatch, RGBBase.Channel.G);
    correct._BrCorrector = GammaCorrector.getExponentInstance(singleChannel.
        bChannelPatch, RGBBase.Channel.B);

    rGamma = correct._RrCorrector.getExponent().getGamma();
    gGamma = correct._GrCorrector.getExponent().getGamma();
    bGamma = correct._BrCorrector.getExponent().getGamma();
  }

  protected Factor[] _produceFactor() {
    singleChannel.produceRGBPatch();
    correct.produceGammaCorrector();
    initCoefficientsRange();

    //==========================================================================
    //多執行緒運算
    //==========================================================================
    IterativeReport[] reports = SimpleThreadCalculator.
        produceBestIterativeReport(
            coefficientsRange, this);
    //==========================================================================
    Factor[] factors = new Factor[] {
        (Factor) reports[0].factor, (Factor) reports[1].factor,
        (Factor) reports[2].factor};

//    this.setTheModelFactors(factors);
    this.iterativeReports = reports;
    coefficientsRange = null;

    return factors;
  }

  public static void main(String[] args) {

    /*LCDTarget lcdTarget = LCDTarget.Instance.getInstance(LCDTarget.
                                                Device.Sony,
                                                LCDTarget.Source.Calibrated,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch125);*/

    LCDTarget lcdTarget = LCDTarget.Instance.get("EIZO_CG221",
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Ramp1021, null, null);

    /*LCDTarget lcdTarget = LCDTarget.Instance.getInstance(LCDTarget.
                                                Device.Dell,
                                                LCDTarget.Source.i1display2,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch58);*/

    RegularGOGModelThread model = new RegularGOGModelThread(lcdTarget.
        getBlackPatch().
        getXYZ(),
        lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * 此處係數以 序列化 處理,不使用XML儲存是因為懶的處理tag :P
     * 加上Factor間有繼承關係,目前不知道XML-Java工具是否可以很好的處理.
     */
    model.store.modelFactorFile(lcdModelFactor, "t.tmp");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    /*LCDTarget lcdTestTarget = LCDTarget.Instance.getInstance(LCDTarget.
        Device.Dell,
        LCDTarget.Source.i1display2,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Patch4096);*/

    LCDTarget lcdTestTarget = LCDTarget.Instance.get("Sony",
        LCDTarget.Source.Calibrated,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test729, null, null);

//    DeltaEReport[] testReports = model.testTarget(lcdTestTarget, factors, true);
    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    RGB rgb = this.matries.XYZToRGBByMaxMatrix(XYZ);

    double r = GOGInverse(rgb.R, factorR.gain, factorR.offset, factorR.gamma);
    double g = GOGInverse(rgb.G, factorG.gain, factorG.offset, factorG.gamma);
    double b = GOGInverse(rgb.B, factorB.gain, factorB.offset, factorB.gamma);

    RGB originalRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g,
                              b});
    return originalRGB;
  }

  public CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {

    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double R = GOG(rgb.R, factorR.gain, factorR.offset, factorR.gamma);
    double G = GOG(rgb.G, factorG.gain, factorG.offset, factorG.gamma);
    double B = GOG(rgb.B, factorB.gain, factorB.offset, factorB.gamma);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B});
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  /**
   * 初始化迭帶係數
   * 可以視情況調整此係數值,讓模式有更好的運算表現
   */
  public void initCoefficientsRange() {
    CoefficientsRange coefR = new CoefficientsRange(.5, 3., -2., 1., rGamma,
        rGamma, RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(.5, 3., -2., 1., gGamma,
        gGamma, RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(.5, 3., -2., 1., bGamma,
        bGamma, RGBBase.Channel.B);

    coefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};

  }

  /**
   *
   * @param coefRange CoefficientsRange
   * @param patchList List
   * @param whitePatch Patch
   * @param bestReport IterativeReport
   * @return IterativeReport
   * @deprecated
   */
  public IterativeReport iterateAndReport(
      SimpleThreadCalculator.CoefficientsRange coefRange,
      List<Patch> patchList,
      Patch whitePatch, IterativeReport bestReport) {
    CoefficientsRange coef = (CoefficientsRange) coefRange;

    double gainS = coef.gain.start;
    double gainE = coef.gain.end;
    double gainStep = coef.gain.step;

    double offsetS = coef.offset.start;
    double offsetE = coef.offset.end;
    double offsetStep = coef.offset.step;

    double gamma = coef.gamma.start;
//    double gammaE = coef.gamma.end;
//    double gammaStep = coef.gamma.step;

    Factor[] factors = new Factor[] {
        new Factor(), new Factor(), new Factor()};

    //迭代
    for (double gain = gainS; gain <= gainE; gain += gainStep) {
      for (double offset = offsetS; offset <= offsetE; offset += offsetStep) {
//        for (double gamma = gammaS; gamma <= gammaE; gamma += gammaStep) {
        factors[coef.channel.getArrayIndex()] = new Factor(gain, offset, gamma,
            coef.channel);
        bestReport = getBestIterativeReport(factors, patchList, whitePatch,
                                            bestReport, coef.channel);

//        }
      }
    }
    iterativeReports[coef.channel.getArrayIndex()] = bestReport;
    return bestReport;
  }

}
