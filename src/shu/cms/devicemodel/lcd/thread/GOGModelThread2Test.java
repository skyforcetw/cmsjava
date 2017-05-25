package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �~�Ӧ�GOG,���ΦA�W�ߥX��
 * �Noffset�Pgain�j��,�H�T�O���I�����T��
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GOGModelThread2Test
    extends GOGModelThread {

  public GOGModelThread2Test(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  /**
   * �ϥμҦ�
   * @param factor LCDModelFactor
   */
  public GOGModelThread2Test(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public GOGModelThread2Test(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

//  public CoefficientsRange getNewCoefficientsRange(LCDModel.Factor factor,
//      SimpleThreadCalculator.CoefficientsRange old) {
//    return new CoefficientsRange( (Factor) factor, (CoefficientsRange) old);
//  }

  public static class Factor
      extends GOGModelThread.Factor {
//    RGBBase.Channel channel;
//    public double gain;
//    public double offset;
//    public double gamma;

//    public GammaCorrector rCorrector;

//    public double[] getVariables() {
//      return new double[] {
//          gain, offset, gamma};
//    }

//    public String toString() {
//      return "[" + channel + "] gain[" + gain + "] offset[" + offset +
//          "] gamma[" +
//          gamma + "]";
//    }

    public Factor() {

    }

    public Factor(double[] variables, RGBBase.Channel channel) {
      super(variables, channel);
//      this.gain = variables[0];
      this.offset = - (gain - 1.);
//      this.gamma = variables[1];
//      this.channel = channel;
    }

    public Factor(double[] variables, GammaCorrector rCorrector,
                  RGBBase.Channel channel) {
//      this(variables, channel);
//      this.rCorrector = rCorrector;
      super(variables, rCorrector, channel);
      this.offset = - (gain - 1.);
    }

//    public Factor(GammaCorrector rCorrector) {
//      this.rCorrector = rCorrector;
//    }

  }

  public static void main(String[] args) {
    /*LCDTarget.Device device = LCDTarget.Device.Dell_M1210;
         LCDTarget lcdTarget = LCDTarget.Instance.getInstance(device,
                                                LCDTarget.Source.i1pro,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch1021);*/
//    LCDTarget lcdTarget = LCDTarget.Instance.getInstanceFromCA210("RGBCMYW_original.txt",
//        CA210Adapter.Type.RGBCMYW_256);

    LCDTarget lcdTarget = LCDTarget.Instance.get("CPT_Demo1",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1792, null, null);
    //dell��1021�D�o���ĹL���t,�]�C�ܦh(�ŦX���󪺦�����h)
    //729���̨ε��G

    GOGModelThread2Test model = new GOGModelThread2Test(lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * ���B�Y�ƥH �ǦC�� �B�z,���ϥ�XML�x�s�O�]���i���B�ztag :P
     * �[�WFactor�����~�����Y,�ثe�����DXML-Java�u��O�_�i�H�ܦn���B�z.
     */
    model.store.modelFactorFile(lcdModelFactor, "gog.factor");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    /*LCDTarget lcdTestTarget = LCDTarget.Instance.getInstance(device,
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Patch4096);*/
    LCDTarget lcdTestTarget = lcdTarget;

    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
//    System.out.println(model.getWhite());
  }

  public String getDescription() {
    return "GOG2";
  }

  public LCDModelBase.Factor[] getFactors(double[] variables,
                                          RGBBase.Channel channel) {
    Factor[] factors = new Factor[] {
        new Factor(), new Factor(), new Factor()};
    factors[channel.getArrayIndex()] = new Factor(variables, channel);
    return factors;
  }

  /**
   * ��l�ƭ��a�Y��
   * �i�H�����p�վ㦹�Y�ƭ�,���Ҧ�����n���B���{
   */
  public void initCoefficientsRange() {
    CoefficientsRange coefR = new CoefficientsRange(.5, 3, 0, 0, 0, 2.8,
        RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(.5, 3, 0, 0, 0, 2.8,
        RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(.5, 3, 0, 0, 0, 2.8,
        RGBBase.Channel.B);

    coefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};

  }
}
