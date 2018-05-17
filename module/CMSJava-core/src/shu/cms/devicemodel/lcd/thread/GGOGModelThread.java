package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 先以gamma校正過後,再進入GOG (不用加入比較)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GGOGModelThread
    extends GOGModelThread {

  public GGOGModelThread(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * 使用模式
   * @param factor LCDModelFactor
   */
  public GGOGModelThread(LCDModelFactor factor) {
    super(factor);
  }

  public GGOGModelThread(String modelFactorFilename) {
    super(modelFactorFilename);
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

  public CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {

    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    this.correct._RrCorrector = factorR.rCorrector;
    this.correct._GrCorrector = factorG.rCorrector;
    this.correct._BrCorrector = factorB.rCorrector;

    double[] rgbValues = this.correct.gammaCorrect(rgb.getValues());

    rgbValues[0] = GOG(rgbValues[0], factorR.gain, factorR.offset,
                       factorR.gamma);
    rgbValues[1] = GOG(rgbValues[1], factorG.gain, factorG.offset,
                       factorG.gamma);
    rgbValues[2] = GOG(rgbValues[2], factorB.gain, factorB.offset,
                       factorB.gamma);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), rgbValues);
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  protected RGB _getRGB(CIEXYZ XYZ, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    this.correct._RrCorrector = factorR.rCorrector;
    this.correct._GrCorrector = factorG.rCorrector;
    this.correct._BrCorrector = factorB.rCorrector;

    RGB rgb = this.matries.XYZToRGBByMaxMatrix(XYZ);

    rgb.R = GOGInverse(rgb.R, factorR.gain, factorR.offset, factorR.gamma);
    rgb.G = GOGInverse(rgb.G, factorG.gain, factorG.offset, factorG.gamma);
    rgb.B = GOGInverse(rgb.B, factorB.gain, factorB.offset, factorB.gamma);

    double[] rgbValues = this.correct.gammaUncorrect(rgb.getValues());

    RGB originalRGB = new RGB(RGB.ColorSpace.unknowRGB, rgbValues);
    return originalRGB;
  }

  public LCDModelBase.Factor[] getFactors(double[] variables,
                                          RGBBase.Channel channel) {
    Factor[] factors = new Factor[] {
        new Factor(correct._RrCorrector), new Factor(correct._GrCorrector),
        new Factor(correct._BrCorrector)};
    GammaCorrector rCorrector = correct.getGammaCorrector(channel);
    factors[channel.getArrayIndex()] = new Factor(variables, rCorrector,
                                                  channel);
    return factors;
  }

  /**
   *
   * @return IterativeReport
   * @deprecated
   */
  public IterativeReport iterateAndReport() {
    return null;
  }

  public static void main(String[] args) {

    /*LCDTarget lcdTarget = LCDTarget.Instance.getInstance(LCDTarget.
                                                Device.Sony,
                                                LCDTarget.Source.Calibrated,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch125);*/

    LCDTarget lcdTarget = LCDTarget.Instance.get("EIZO_CE240W",
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Ramp1021, null, null).
        targetFilter.getRamp509From1021();

    /*LCDTarget lcdTarget = LCDTarget.Instance.getInstance(LCDTarget.
                                                Device.Dell,
                                                LCDTarget.Source.i1display2,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch58);*/

    //dell用1021求得的效過較差,也慢很多(符合條件的色塊較多)
    //729有最佳結果

    GGOGModelThread model = new GGOGModelThread(lcdTarget);
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

    LCDTarget lcdTestTarget = LCDTarget.Instance.get("Device.EIZO_CE240W",
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

//    DeltaEReport[] testReports = model.testTarget(lcdTestTarget, factors, true);
    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
  }

}
