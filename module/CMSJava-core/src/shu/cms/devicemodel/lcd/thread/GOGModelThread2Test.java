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
 * 繼承自GOG,不用再獨立出來
 * 將offset與gain綁住,以確保白點的正確性
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
   * 使用模式
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
    //dell用1021求得的效過較差,也慢很多(符合條件的色塊較多)
    //729有最佳結果

    GOGModelThread2Test model = new GOGModelThread2Test(lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * 此處係數以 序列化 處理,不使用XML儲存是因為懶的處理tag :P
     * 加上Factor間有繼承關係,目前不知道XML-Java工具是否可以很好的處理.
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
   * 初始化迭帶係數
   * 可以視情況調整此係數值,讓模式有更好的運算表現
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
