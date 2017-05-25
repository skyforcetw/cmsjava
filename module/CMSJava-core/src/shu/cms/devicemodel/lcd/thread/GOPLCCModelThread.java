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
 * Gain+Offset+PLCC(�HPLCC�N��Gamma)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GOPLCCModelThread
    extends GOGModelThread {

//  protected CoefficientsRange[] coefficientsRange = null;


  /**
   * �ϥμҦ�
   * @param factor LCDModelFactor
   */
  public GOPLCCModelThread(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public GOPLCCModelThread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  public GOPLCCModelThread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  protected Factor[] _produceFactor() {
    singleChannel.produceRGBPatch();
    correct.produceGammaCorrector();
    initCoefficientsRange();

    //==========================================================================
    //�h������B��
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

    /*LCDTarget lcdTarget = LCDTarget.Instance.getInstance(LCDTarget.
                                                Device.Sony,
                                                LCDTarget.Source.Calibrated,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch58);*/

    LCDTarget lcdTarget = LCDTarget.Instance.get("Dell_M1210",
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Ramp1021, null, null);

    //dell��1021�D�o���ĹL���t,�]�C�ܦh(�ŦX���󪺦�����h)
    //729���̨ε��G

    GOPLCCModelThread model = new GOPLCCModelThread(
        lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * ���B�Y�ƥH �ǦC�� �B�z,���ϥ�XML�x�s�O�]���i���B�ztag :P
     * �[�WFactor�����~�����Y,�ثe�����DXML-Java�u��O�_�i�H�ܦn���B�z.
     */
    model.store.modelFactorFile(lcdModelFactor, "goplcc.factor");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    LCDTarget lcdTestTarget = LCDTarget.Instance.get("Dell_M1210",
        LCDTarget.Source.i1display2,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

    /*LCDTarget lcdTestTarget = LCDTarget.Instance.getInstance(LCDTarget.
        Device.Sony,
        LCDTarget.Source.Calibrated,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Patch729);*/

//    DeltaEReport[] testReports = model.testTarget(lcdTestTarget, factors, true);
    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE()); ;
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

    rgb.R = correct._RrCorrector.uncorrect(rgb.R);
    rgb.G = correct._GrCorrector.uncorrect(rgb.G);
    rgb.B = correct._BrCorrector.uncorrect(rgb.B);

    double r = GOInverse(rgb.R, factorR.gain, factorR.offset);
    double g = GOInverse(rgb.G, factorG.gain, factorG.offset);
    double b = GOInverse(rgb.B, factorB.gain, factorB.offset);

    RGB originalRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g,
                              b});
    return originalRGB;
  }

  public CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {

    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double R = GO(rgb.R, factorR.gain, factorR.offset);
    double G = GO(rgb.G, factorG.gain, factorG.offset);
    double B = GO(rgb.B, factorB.gain, factorB.offset);

    R = correct._RrCorrector.correct(R);
    G = correct._GrCorrector.correct(G);
    B = correct._BrCorrector.correct(B);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B});
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  protected static double GOInverse(double fx, double gain, double offset) {
    return (fx - offset) / gain;
  }

  protected static double GO(double d, double gain, double offset) {
    if ( (gain * d + offset) < 0) {
      return 0;
    }
    else {
      return gain * d + offset;
    }
  }

  /**
   *
   * @param variables double[]
   * @param channel Channel
   * @return Factor[]
   */
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

  public CoefficientsRange getNewCoefficientsRange(LCDModelBase.Factor factor,
      SimpleThreadCalculator.CoefficientsRange old) {
    return new CoefficientsRange( (Factor) factor, (CoefficientsRange) old);
  }

  /**
   * ��l�ƭ��a�Y��
   * �i�H�����p�վ㦹�Y�ƭ�,���Ҧ�����n���B���{
   */
  public void initCoefficientsRange() {
    CoefficientsRange coefR = new CoefficientsRange(.5, 3., -2., 0., 0, 0,
        RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(.5, 3., -2., 0., 0, 0,
        RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(.5, 3., -2., 0., 0, 0,
        RGBBase.Channel.B);

    coefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};

  }

  /**
   *
   * @return IterativeReport
   * @deprecated
   */
  public IterativeReport iterateAndReport() {
    return null;
  }
}
