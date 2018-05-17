package shu.cms.devicemodel.lcd.thread;

import java.util.*;
import java.util.concurrent.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class YYModelThread
    extends ChannelDependentModel {

  public YYModelThread(LCDTarget lcdTarget,
                       LCDTarget rCorrectorLCDTarget) {
    super(lcdTarget);
//    this.flareXYZ = flareXYZ;
    this.rCorrectorLCDTarget = rCorrectorLCDTarget;
//    this.doRGBRational = true;
  }

//  protected CIEXYZ flareXYZ;
  protected LCDTarget rCorrectorLCDTarget;

  public static void main(String[] args) {
    LCDTarget lcdTarget1 = LCDTarget.Instance.get("Dell_M1210",
                                                  LCDTarget.Source.i1pro,
                                                  LCDTarget.Room.Dark,
                                                  LCDTarget.TargetIlluminant.
                                                  D65,
                                                  LCDTargetBase.Number.Test729, null, null);

    LCDTarget lcdTarget2 = LCDTarget.Instance.get("Dell_M1210",
                                                  LCDTarget.Source.i1pro,
                                                  LCDTarget.Room.Dark,
                                                  LCDTarget.TargetIlluminant.
                                                  D65,
                                                  LCDTargetBase.Number.
                                                  Ramp1021, null, null);

    /*for (Patch p : lcdTarget2.getPatchList()) {
      RGB rgb = p.getRGB();
      if (rgb.hasOnlyOneValueChannel() &&
          (rgb.R == 255 || rgb.G == 255 || rgb.B == 255) || rgb.isBlack()) {
        System.out.println(p);
        Spectra s=p.getSpectra();
        System.out.println(s.getPeak());
        System.out.println(Arrays.toString(s.getData()));
      }
         }*/

    /*LCDTarget lcdTarget1 = LCDTarget.Instance.getInstance(LCDTarget.
                                                 Device.Sony,
                                                 LCDTarget.Source.Calibrated,
                                                 LCDTarget.Room.Dark,
     LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch729);

         LCDTarget lcdTarget2 = LCDTarget.Instance.getInstance(LCDTarget.
                                                 Device.Sony,
                                                 LCDTarget.Source.Calibrated,
                                                 LCDTarget.Room.Dark,
     LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch58);*/

    YYModelThread model = new YYModelThread(lcdTarget1, lcdTarget2);
    model.setCrossTalkRemove(true);

    double start = System.currentTimeMillis();
    LCDModel.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    System.out.println(factors[0]);
    System.out.println(factors[1]);
    System.out.println(factors[2]);

//    System.out.println(model.getIterativeReport().deltaEReport);

    LCDTarget lcdTestTarget = LCDTarget.Instance.get("Dell_M1210",
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);

    System.out.println("Training1: " + lcdTarget1.getDescription());
    System.out.println("Training2: " + lcdTarget2.getDescription());
    System.out.println(Arrays.toString(testReports));

    System.out.println("negtiveDeltaCount: " + model.negtiveDeltaCount);
    System.out.println("negtiveKCoefsCount: " + model.negtiveKCoefsCount);
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor) {
    return null;
  }

  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor) {
    SCurveModel1Thread model1 = produceSCurveModel1Thread(factor);
    return _getXYZ(rgb, factor, model1);
  }

  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor,
                           SCurveModel1Thread model1) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double[] rgbValues = rgb.getValues();
    double[] GS_RGBValue = this.produceGSValue(rgbValues, model1);

    double[] resultRGBValues = GS_RGBValue;
    if (crossTalkRemove) {
      RGB _GS_RGBValues = model1.f_(new RGB(rgb.getRGBColorSpace(), GS_RGBValue),
                                    model1.getModelFactors());

      double sR = RGBRationalize(_GS_RGBValues.R *
                                 (factorG.k * GS_RGBValue[1] +
                                  factorB.k * GS_RGBValue[2]));
      double sG = RGBRationalize(_GS_RGBValues.G *
                                 (factorR.k * GS_RGBValue[0] +
                                  factorB.k * GS_RGBValue[2]));
      double sB = RGBRationalize(_GS_RGBValues.B *
                                 (factorR.k * GS_RGBValue[0] +
                                  factorG.k * GS_RGBValue[1]));

      /**
       * 如果某一頻道已經達到max,就不用考慮頻道交互影響的問題了
       * 因為已經到最大值,再怎麼影響也不會更大.
       */
      double maxValue = rgb.getMaxValue().max;
      sR = rgb.R == maxValue ? 0 : sR;
      sG = rgb.G == maxValue ? 0 : sG;
      sB = rgb.B == maxValue ? 0 : sB;

      RGB sRGB = new RGB(rgb.getRGBColorSpace(), new double[] {sR, sG, sB});
      sRGB = model1.getOriginalRGB(sRGB, model1.getModelFactors());
      sRGB = rationalize(sRGB);

      double[] deltaRGBValues = correct.gammaCorrect(sRGB.getValues());
      resultRGBValues[0] += deltaRGBValues[0];
      resultRGBValues[1] += deltaRGBValues[1];
      resultRGBValues[2] += deltaRGBValues[2];
    }

    double[] modelRGBValues = new double[3];
    //cross talk removal後的rgb
    modelRGBValues[0] = RGBRationalize(resultRGBValues[0]);
    modelRGBValues[1] = RGBRationalize(resultRGBValues[1]);
    modelRGBValues[2] = RGBRationalize(resultRGBValues[2]);

    double[][] RGB2XYZ = factorR.RGB2XYZMatrix;
    double[] XYZValues = DoubleArray.times(RGB2XYZ, modelRGBValues);
    XYZValues = DoubleArray.plus(XYZValues, flare.getFlareValues());
    CIEXYZ XYZ = new CIEXYZ(XYZValues);
    return XYZ;
  }

  protected final static RGB rationalize(RGB rgb) {
    rgb.R = RGBRationalize(rgb.R);
    rgb.G = RGBRationalize(rgb.G);
    rgb.B = RGBRationalize(rgb.B);
    return rgb;
  }

  /**
   * 原始版的
   * @param rgb RGB
   * @param factor Factor[]
   * @param model1 SCurveModel1Thread
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ_(RGB rgb, LCDModel.Factor[] factor,
                            SCurveModel1Thread model1) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double[] rgbValues = rgb.getValues();
    double[] GS_RGBValue = this.produceGSValue(rgbValues, model1);

    RGB _GS_RGBValues = model1.f_(new RGB(rgb.getRGBColorSpace(), GS_RGBValue),
                                  model1.getModelFactors());

    double sR = RGBRationalize(_GS_RGBValues.R *
                               (factorG.k * GS_RGBValue[1] +
                                factorB.k * GS_RGBValue[2]));
    double sG = RGBRationalize(_GS_RGBValues.G *
                               (factorR.k * GS_RGBValue[0] +
                                factorB.k * GS_RGBValue[2]));
    double sB = RGBRationalize(_GS_RGBValues.B *
                               (factorR.k * GS_RGBValue[0] +
                                factorG.k * GS_RGBValue[1]));

    RGB sRGB = new RGB(rgb.getRGBColorSpace(), new double[] {sR, sG, sB});
    sRGB = model1.getOriginalRGB(sRGB, model1.getModelFactors());
    double[] deltaRGBValues = correct.gammaCorrect(sRGB.getValues());

    double deltaR = deltaRGBValues[0];
    double deltaG = deltaRGBValues[1];
    double deltaB = deltaRGBValues[2];

    double[] modelRGBValues = new double[3];
    //cross talk removal後的rgb
    modelRGBValues[0] = RGBRationalize(GS_RGBValue[0]);
    modelRGBValues[1] = RGBRationalize(GS_RGBValue[1]);
    modelRGBValues[2] = RGBRationalize(GS_RGBValue[2]);

//    modelRGBValues[0] = limit(GS_RGBValue[0] + deltaR);
//    modelRGBValues[1] = limit(GS_RGBValue[1] + deltaG);
//    modelRGBValues[2] = limit(GS_RGBValue[2] + deltaB);

//    modelRGBValues[0] = limit(rgbValues[0] + deltaR);
//    modelRGBValues[1] = limit(rgbValues[1] + deltaG);
//    modelRGBValues[2] = limit(rgbValues[2] + deltaB);
    //gamma校正
//    modelRGBValues = gammaCorrect(modelRGBValues);

//    deltaRGBValues = gammaCorrect(deltaRGBValues);
//    modelRGBValues[0] = limit(rgbValues[0] + deltaRGBValues[0]);
//    modelRGBValues[1] = limit(rgbValues[1] + deltaRGBValues[1]);
//    modelRGBValues[2] = limit(rgbValues[2] + deltaRGBValues[2]);

    double[][] RGB2XYZ = factorR.RGB2XYZMatrix;
    double[] XYZValues = DoubleArray.times(RGB2XYZ, modelRGBValues);
    XYZValues = DoubleArray.plus(XYZValues, flare.getFlareValues());
    CIEXYZ XYZ = new CIEXYZ(XYZValues);
    return XYZ;

//    RGB newRGB = new RGB(rgb.getRGBColorSpace(),rgbValues);
//    return RGB2XYZ(newRGB);
  }

  protected SCurveModel1Thread produceSCurveModel1Thread(LCDModel.Factor[]
      factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    SCurveModel1Thread.Factor[] model1Factors = new SCurveModel1Thread.Factor[] {
        factorR.SCurveModel1ThreadFactor, factorG.SCurveModel1ThreadFactor,
        factorB.SCurveModel1ThreadFactor};

    LCDModelFactor lcdModelFactor = this.produceLCDModelFactor(model1Factors);
    SCurveModel1Thread model1 = new SCurveModel1Thread(lcdModelFactor);
    return model1;
  }

  protected void produceRGBSingleChannelPatch(SCurveModel1Thread model1) {
    singleChannel.produceRGBPatch();
    produceSCurveModelIValues(singleChannel.rChannelPatch, model1);
    produceSCurveModelIValues(singleChannel.gChannelPatch, model1);
    produceSCurveModelIValues(singleChannel.bChannelPatch, model1);
  }

  /**
   * 將色塊的RGB值經S-curveI處理
   * @param singleChannelPatch Set
   * @param model1 SCurveModel1Thread
   */
  protected static void produceSCurveModelIValues(Set<Patch> singleChannelPatch,
      SCurveModel1Thread model1) {
    for (Patch p : singleChannelPatch) {
      RGB originalRGB = p.getRGB();
      RGB newRGB = model1.getLuminanceRGB(p.getRGB());
      originalRGB.setValues(newRGB.getValues());
    }
  }

  protected Factor[] _produceFactor() {
    //==========================================================================
    // 計算model1參數
    //==========================================================================
    SCurveModel1Thread model1 = new SCurveModel1Thread(this.rCorrectorLCDTarget);
    model1.produceFactor();
    //==========================================================================

    //==========================================================================
    // 計算3x3 matrix
    //==========================================================================
//    produceRGBSingleChannelPatch(); //ok
    produceRGBSingleChannelPatch(model1);
    //計算gamma correction
    correct.produceGammaCorrector(); //ok
    Set<Patch> singleChannelPatchSet = singleChannel.getPatchSet();
    double[][] RGB2XYZ = produceRGB2XYZ_3x3(singleChannelPatchSet); //ok
    //==========================================================================

    /**
     * k係數的求法
     * 首先透過兩兩頻道的色塊,分別求得不同頻道的k係數
     * 所有色塊的k係數取得其平均,以此為推導的起點
     *
     * 設定好k係數的範圍,以所有的色塊去評估k係數
     * 找到差距(rms)最小的k係數
     */
//    double[][] kCoefs = produceKCoefficient(RGB2XYZ, model1); //ok
    double[][] kCoefs = new double[][] {
        {
        .001, .001, .001}
    };

    temporaryFactors = makeFactor(model1, new double[3], RGB2XYZ);

    //==========================================================================
    // 由收集到的k範圍尋找最佳k
    //==========================================================================
    double[] bestK = null;
    if (crossTalkRemove) {
      bestK = findBestKCoefficientByCoefRange(kCoefs);
    }
    else {
      bestK = new double[3];
    }
    //==========================================================================

    Factor[] factors = makeFactor(model1, bestK, RGB2XYZ);
//    this.setTheModelFactors(factors);
    return factors;
  }

  protected Factor[] makeFactor(SCurveModel1Thread model1, double[] k,
                                double[][] RGB2XYZ) {
    LCDModel.Factor[] model1Factors = model1.getModelFactors();

    Factor factorR = new Factor(RGBBase.Channel.R,
                                (SCurveModel1Thread.Factor) model1Factors[0],
                                k[0], correct._RrCorrector);
    factorR.RGB2XYZMatrix = RGB2XYZ;

    Factor factorG = new Factor(RGBBase.Channel.G,
                                (SCurveModel1Thread.Factor) model1Factors[1],
                                k[1], correct._GrCorrector);

    Factor factorB = new Factor(RGBBase.Channel.B,
                                (SCurveModel1Thread.Factor) model1Factors[2],
                                k[2], correct._BrCorrector);

    Factor[] factors = new Factor[] {
        factorR, factorG, factorB};

    return factors;
  }

  //負值的delta次數(負值越多,代表3x3 matrix越不準確)
  protected int negtiveDeltaCount;

  //產生負值k係數的次數
  protected int negtiveKCoefsCount;

  /**
   * 產生k係數
   * 分為1.兩兩頻道 2.三個頻道 有值
   * 第一種狀況可直接求得k值
   * 第二種狀況透過解聯立方程式即可求得k值
   * @param RGB2XYZ double[][]
   * @param model1 SCurveModel1Thread
   * @return double[][]
   */
  protected final double[][] produceKCoefficient(double[][] RGB2XYZ,
                                                 SCurveModel1Thread model1) {
    List<Patch> XYZRGBPatches = lcdTarget.getPatchList();
    int size = XYZRGBPatches.size();

    double[][] XYZ2RGB = new Matrix(RGB2XYZ).inverse().getArray();

    int negtiveDeltaHappen = 0;
    int negtiveKHappen = 0;
    ArrayList<double[]> arrayList = new ArrayList<double[]> (size);
//    System.out.println(size);

    for (int x = 0; x < size; x++) {
      //=======================================================================
      // init
      //=======================================================================
      Patch p = XYZRGBPatches.get(x);
      RGB rgb = p.getRGB();

      if (rgb.isBlack() || rgb.isPrimaryChannel()) {
        //黑色可以略過 只有單一頻道值的也略過(因為不存在像素間的電容交互影響)
        continue;
      }

      //=====================================================================
      // 採用解聯立方程式的方式求k值
      // 但是先決條件是R G B皆非0值才可求?
      //=====================================================================
//      double[] deltaRGB = calculateDeltaRGB(rgbValues,
//                                            p.getXYZ().getValues(), aXYZ2RGB);
      double[] deltaRGB = calculateDeltaRGB(rgb, p.getXYZ().getValues(),
                                            XYZ2RGB, model1);

      if ( (deltaRGB[0] < 0 || deltaRGB[1] < 0 || deltaRGB[2] < 0) &&
          (deltaRGB[0] < -1. / 512 || deltaRGB[1] < -1. / 512 ||
           deltaRGB[2] < -1. / 512)) {
        /**
         * delta應該為正值,因為鄰近像素的電容影響,產生的應該是"多餘"的效應,所以delta為正值.
         * 非正值可能是3x3 matrix的誤差
         *
         * 誤差在 1/512才計入,是因為r/g/b的範圍為0~255,共256階
         * 誤差在不足0.5個單位的r/g/b時,其實是沒有影響的(會被四捨五入去除)
         */
//        System.out.println(rgb + " " + Arrays.toString(deltaRGB));
        negtiveDeltaHappen++;
        continue;
      }

      //=======================================================================

      //=======================================================================
      //取得GS的運算結果
      //=======================================================================
      double[] rgbValues = rgb.getValues();
      double[] GS_RGBValues = produceGSValue(rgbValues, model1);
      double[] SGinvDeltaRGBValues = this.produceSGinvValue(deltaRGB, model1);

      RGB _GS_RGBValues = model1.f_(new RGB(rgb.getRGBColorSpace(),
                                            GS_RGBValues),
                                    model1.getModelFactors());

      double[] answerRGB = new double[] {
          SGinvDeltaRGBValues[0] / _GS_RGBValues.R,
          SGinvDeltaRGBValues[1] / _GS_RGBValues.G,
          SGinvDeltaRGBValues[2] / _GS_RGBValues.B};
//      System.out.println(rgb + " " + Arrays.toString(SGinvDeltaRGBValues) + " " +
//                         _GS_RGBValues);
//      System.out.println(Arrays.toString(answerRGB));

      double[] result = solve(rgbValues, GS_RGBValues, answerRGB);
      if (result == null) {
        continue;
      }
      else if (result[0] < 0 || result[1] < 0 || result[2] < 0 ||
               Double.isNaN(result[0]) || Double.isNaN(result[1]) ||
               Double.isNaN(result[2])) {
        //k應該為0 or 正值
        negtiveKHappen++;
        continue;
      }
//      System.out.println(Arrays.toString(result));
      arrayList.add(result);
    }
    negtiveDeltaCount = negtiveDeltaHappen;
    negtiveKCoefsCount = negtiveKHappen;

    double[][] kCoefsArray = new double[arrayList.size()][];
    arrayList.toArray(kCoefsArray);

    return kCoefsArray;
  }

  /**
   * 先經過model1的運算後,再經過gamma校正所得的值
   * 簡稱為GS值
   * @param rgbValue double[]
   * @param model1 SCurveModel1Thread
   * @return double[]
   */
  protected double[] produceGSValue(double[] rgbValue,
                                    SCurveModel1Thread model1) {
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValue,
                      RGB.MaxValue.Double1);
    double[] values = model1.getLuminanceRGB(rgb).getValues();
//    value[0] = limit(value[0]);
//    value[1] = limit(value[1]);
//    value[2] = limit(value[2]);
    values = RGBRationalize(values);
    values = correct.gammaCorrect(values);

//    value[0] = RrCorrector.correct(limit(value[0]));
//    value[1] = GrCorrector.correct(limit(value[1]));
//    value[2] = BrCorrector.correct(limit(value[2]));

    return values;
  }

  /**
   *
   * @param values double[]
   * @return double[]
   * @deprecated
   */
  protected final static double[] RGBRationalize(double[] values) {
    int size = values.length;
    for (int x = 0; x < size; x++) {
      values[x] = RGBRationalize(values[x]);
    }
    return values;
  }

  /**
   *
   * @param val double
   * @return double
   * @deprecated
   */
  protected final static double RGBRationalize(double val) {
    val = RGB.rationalize(val, RGB.MaxValue.Double1);
    return val;
  }

  /**
   * 經過Gamma反校正,然後再透過SCurveI處理
   * @param rgbValue double[]
   * @param model1 SCurveModel1Thread
   * @return double[]
   */
  protected double[] produceSGinvValue(double[] rgbValue,
                                       SCurveModel1Thread model1) {
    double[] value = new double[3];
    value[0] = correct._RrCorrector.uncorrect(rgbValue[0]);
    value[1] = correct._GrCorrector.uncorrect(rgbValue[1]);
    value[2] = correct._BrCorrector.uncorrect(rgbValue[2]);

    RGB newRGB = new RGB(RGB.ColorSpace.unknowRGB, value,
                         RGB.MaxValue.Double1);

//    LCDModel.Factor[] model1Factors = model1.getModelFactors();
    value = model1.getLuminanceRGB(newRGB).getValues();

    return value;
  }

  /**
   * 解kr kg kb
   * @param originalRGB double[]
   * @param modelRGB double[]
   * @param answerRGB double[]
   * @return double[]
   */
  protected double[] solve(double[] originalRGB, double[] modelRGB,
                           double[] answerRGB) {
    //=========================================================================
    // 尋找0值所在的channel
    //=========================================================================
    int zeroIndex = -1;
    for (int x = 0; x < originalRGB.length; x++) {
      if (originalRGB[x] == 0) {
        zeroIndex = x;
        break;
      }
    }
    //=========================================================================

    //==========================================================================
    if (zeroIndex != -1) {
      //=======================================================================
      // 兩兩頻道有直的狀況: 直接可得k值
      //=======================================================================
      double kr = 0, kg = 0, kb = 0;
      switch (zeroIndex) {
        case 0:

          //R為0
          kr = 0;
          kg = answerRGB[2] / modelRGB[1];
          kb = answerRGB[1] / modelRGB[2];
          break;
        case 1:

          //G為0
          kr = answerRGB[2] / modelRGB[0];
          kg = 0;
          kb = answerRGB[0] / modelRGB[2];
          break;
        case 2:

          //B為0
          kr = answerRGB[1] / modelRGB[0];
          kg = answerRGB[0] / modelRGB[1];
          kb = 0;

          break;
      }
      return new double[] {
          kr, kg, kb};
      //=======================================================================
    }
    else {
      //=======================================================================
      // 三頻道皆有值的狀況: 解聯立方程式
      //=======================================================================
      double[] funcR = new double[] {
          0, modelRGB[1], modelRGB[2]};
      double[] funcG = new double[] {
          modelRGB[0], 0, modelRGB[2]};
      double[] funcB = new double[] {
          modelRGB[0], modelRGB[1], 0};

      double[][] func = new double[][] {
          funcG, funcB, funcR};
      double[][] ans = new double[][] {
          {
          answerRGB[1], answerRGB[2], answerRGB[0]}
      };

      ans = DoubleArray.transpose(ans);

      Matrix mFunc = new Matrix(func);
      Matrix mAns = new Matrix(ans);
      if (!mFunc.isNonsingular()) {
        //singular matrix無法求解
        return null;
      }
      Matrix result = mFunc.solve(mAns).transpose();
      double[] k = result.getArray()[0];

      return new double[] {
          k[2], k[0], k[1]};
      //=======================================================================
    }
  }

  protected final double[] calculateDeltaRGB(RGB originalRGB,
                                             double[] targetXYZValues,
                                             double[][] XYZ2RGB,
                                             SCurveModel1Thread model1) {
    targetXYZValues = DoubleArray.minus(targetXYZValues, flare.getFlareValues());
    double[] targetRGBValues = DoubleArray.times(XYZ2RGB, targetXYZValues);

    targetRGBValues = RGBRationalize(targetRGBValues);
    targetRGBValues = this.correct.gammaUncorrect(targetRGBValues);
    RGB targetRGB = new RGB(originalRGB.getRGBColorSpace(), targetRGBValues);

    targetRGB.R = RGBRationalize(targetRGB.R);
    targetRGB.G = RGBRationalize(targetRGB.G);
    targetRGB.B = RGBRationalize(targetRGB.B);

    targetRGB = model1.getOriginalRGB(targetRGB, model1.getModelFactors());
    targetRGB.getValues(targetRGBValues);

    targetRGBValues = RGBRationalize(targetRGBValues);

    double[] deltaRGB = new double[] {
        targetRGBValues[0] - originalRGB.R,
        targetRGBValues[1] - originalRGB.G,
        targetRGBValues[2] - originalRGB.B};

    double maxValue = originalRGB.getMaxValue().max;
    deltaRGB[0] = originalRGB.R == maxValue ? 0 : deltaRGB[0];
    deltaRGB[1] = originalRGB.G == maxValue ? 0 : deltaRGB[1];
    deltaRGB[2] = originalRGB.B == maxValue ? 0 : deltaRGB[2];

    deltaRGB[0] = originalRGB.R == 0 ? 0 : deltaRGB[0];
    deltaRGB[1] = originalRGB.G == 0 ? 0 : deltaRGB[1];
    deltaRGB[2] = originalRGB.B == 0 ? 0 : deltaRGB[2];

    return deltaRGB;
  }

  protected static double[] normalize(double[] original) {
    double max = Maths.max(original);
    double[] normal = null;
    if (max > 1.) {
      normal = DoubleArray.copy(original);
      Maths.normalize(normal, max);
    }
    else {
      normal = original;
    }
    return normal;
  }

  private class CoefficientsRange {

    Range kr;
    Range kg;
    Range kb;

    CoefficientsRange(double krS,
                      double krE,
                      double kgS,
                      double kgE,
                      double kbS,
                      double kbE) {

      kr = new Range(krS, krE, getStepRate());
      kg = new Range(kgS, kgE, getStepRate());
      kb = new Range(kbS, kbE, getStepRate());

    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return "kr[" + kr + "] kg[" + kg + "] kb[" + kb + "]";
    }

  }

  protected CoefficientsRange coefficientsRange = null;

  protected void initCoefficientsRange(double[][] kCoefs) {
    //==========================================================================
    // 計算迭代係數範圍
    //==========================================================================
    double krMax = Double.MIN_VALUE,
        kgMax = Double.MIN_VALUE,
        kbMax = Double.MIN_VALUE;

    for (double[] kCoef : kCoefs) {
      double rMax = Math.max(krMax, kCoef[0]);
      double gMax = Math.max(kgMax, kCoef[1]);
      double bMax = Math.max(kbMax, kCoef[2]);
      krMax = rMax > 1 ? krMax : rMax;
      kgMax = gMax > 1 ? krMax : gMax;
      kbMax = bMax > 1 ? krMax : bMax;
    }
    //==========================================================================
    coefficientsRange = new CoefficientsRange(0, krMax, 0, kgMax, 0, kbMax);
  }

  /**
   * 以迭代的方式求最佳解
   * @param kCoefs double[][]
   * @return double[]
   */
  protected final double[] findBestKCoefficientByCoefRange(double[][] kCoefs) {
    //初始化迭代係數
    initCoefficientsRange(kCoefs);
//    System.out.println(coefficientsRange);

    //==========================================================================
    // 產生訓練用色塊
    //==========================================================================
    patchList = lcdTarget.getLabPatchList();
    whitePatch = lcdTarget.getWhitePatch();
    //==========================================================================

    //與ThreadCalculator共作計算
    IterativeReport bestIterativeReport = ThreadCalculator.
        produceBestIterativeReport(this);

    iterativeReport = bestIterativeReport;
    coefficientsRange = null;
    Factor[] bestFactors = toFactorArray(bestIterativeReport.factors);
    return new double[] {
        bestFactors[0].k, bestFactors[1].k, bestFactors[2].k};
  }

  private Factor[] toFactorArray(LCDModel.Factor[] factors) {
    int size = factors.length;
    Factor[] result = new Factor[size];
    System.arraycopy(factors, 0, result, 0, size);
    return result;
  }

  public void modifyCoefficientsRange(LCDModel.Factor[] LCDModelFactors,
                                      int iterateIndex) {

    Factor[] factors = toFactorArray(LCDModelFactors);

    coefficientsRange.kr = Range.determineRange(factors[0].k,
                                                coefficientsRange.kr, this);
    coefficientsRange.kg = Range.determineRange(factors[1].k,
                                                coefficientsRange.kg, this);
    coefficientsRange.kb = Range.determineRange(factors[2].k,
                                                coefficientsRange.kb, this);
  }

  /**
   * 控制運算執行緒的數目
   */
  protected int threadCount = 1; //ThreadCalculator.THREAD_COUNT;

  protected static class IterateCoefficient
      implements ThreadCalculator.IterateCoefficient {
    public IterateCoefficient(CoefficientsRange
                              iterativeCoefficient,
                              Factor[] tmpFactors
        ) {
      this.iterativeCoefficient = iterativeCoefficient;
      this.tmpFactors = tmpFactors;
    }

    CoefficientsRange
        iterativeCoefficient;
    Factor[] tmpFactors;
  }

  protected boolean crossTalkRemove = true;

  protected List<Patch> patchList;
  protected Patch whitePatch;
  Factor[] temporaryFactors;

  public IterateCoefficient produceIterateCoefficient() {
    IterateCoefficient coef = new IterateCoefficient(
        coefficientsRange, temporaryFactors);

    return coef;
  }

  public IterativeReport iterateAndReport(ThreadCalculator.IterateCoefficient
                                          coefficient) {
    IterateCoefficient coef = (IterateCoefficient) coefficient;

    //==========================================================================
    //產生係數
    //==========================================================================
    double krS = coef.iterativeCoefficient.kr.start;
    double krE = coef.iterativeCoefficient.kr.end;
    double krStep = coef.iterativeCoefficient.kr.step;
    //==========================================================================

    //==========================================================================
    //設定執行緒
    //==========================================================================
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    List<Future<IterativeReport>>
        futureList = new LinkedList<Future<IterativeReport>> ();
    //==========================================================================

    //==========================================================================
    //多執行緒運算
    //==========================================================================
    for (double kr = krS; kr <= krE; kr += krStep) {
      IterativeFactorThread task = new IterativeFactorThread(kr,
          coef.iterativeCoefficient, coef.tmpFactors, this);
      //將運算工作放到Thread pool中輪替執行
      Future<IterativeReport> future = executorService.submit(task);
      //將運算結果放到List中
      futureList.add(future);
    }
    //==========================================================================
    //==========================================================================
    //將所有運算結果中,找出最佳結果
    //==========================================================================
    return ThreadCalculator.getBestIterativeReport(futureList, executorService);
  }

  private class IterativeFactorThread
      extends ThreadCalculator.IterativeFactorThread {
    double kr;
    double kgS;
    double kgE;
    double kgStep;
    double kbS;
    double kbE;
    double kbStep;

    Factor[] factors;

    public IterativeFactorThread(double kr,
                                 CoefficientsRange iterativeCoefficient,
                                 Factor[] tmpFactors, LCDModel lcdModel) {
      super(lcdModel);

      //=========================================================================
      //為了避免執行緒間互相搶資源的問題,所以直接copy一份下來
      //=========================================================================
      int size = tmpFactors.length;
      this.factors = new Factor[size];
      System.arraycopy(tmpFactors, 0, this.factors, 0, size);

      //=========================================================================
      //設定係數
      //=========================================================================
      this.kr = kr;
      this.kgS = iterativeCoefficient.kg.start;
      this.kgE = iterativeCoefficient.kg.end;
      this.kgStep = iterativeCoefficient.kg.step;
      this.kbS = iterativeCoefficient.kb.start;
      this.kbE = iterativeCoefficient.kb.end;
      this.kbStep = iterativeCoefficient.kb.step;
      //=========================================================================
    }

    public IterativeReport call() {
      IterativeReport bestReport = null;
      factors[RGBBase.Channel.R.getArrayIndex()].k = kr;
//      System.out.println(kgS + " " + kgE +" "+kgStep+ " " + kbS + " " + kbE+" "+kbStep);
      //=========================================================================
      //迴圈迭代
      //=========================================================================
      for (double kg = kgS; kg <= kgE; kg += kgStep) {
        for (double kb = kbS; kb <= kbE; kb += kbStep) {

          //==========================================================
          //設定參數
          //==========================================================
          factors[RGBBase.Channel.G.getArrayIndex()].k = kg;
          factors[RGBBase.Channel.B.getArrayIndex()].k = kb;
          //==========================================================
//          System.out.println(kg+" " +kb);
          bestReport = getBestIterativeReport(factors, patchList, whitePatch,
                                              DeltaEReport.AnalyzeType.Average,
                                              bestReport);
        }
      }
      //========================================================================

      return bestReport;
    }
  }

  public static class Factor
      extends LCDModel.Factor {

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    Factor() {

    }

    Factor(RGBBase.Channel ch, SCurveModel1Thread.Factor model1Factor, double k,
           GammaCorrector gammaCorrector) {
      this.channel = ch;
      this.SCurveModel1ThreadFactor = model1Factor;
      this.k = k;
      this.gammaCorrector = gammaCorrector;
    }

    Factor(RGBBase.Channel ch, SCurveModel1Thread.Factor model1Factor, double k,
           GammaCorrector gammaCorrector, Spectra opticalSpectra) {
      this.channel = ch;
      this.SCurveModel1ThreadFactor = model1Factor;
      this.k = k;
      this.gammaCorrector = gammaCorrector;
      this.opticalSpectra = opticalSpectra;
    }

    RGBBase.Channel channel;
    SCurveModel1Thread.Factor SCurveModel1ThreadFactor;
    double k;
    GammaCorrector gammaCorrector;
    double[][] RGB2XYZMatrix;
    Spectra opticalSpectra;

    public String toString() {
      return "[" + channel + "] SCurve1[" + SCurveModel1ThreadFactor + "] k[" +
          k +
          "]";
    }

  }

  /*protected static double rCorrect(GammaCorrector rCorrector, double value) {
    double max = RGB.MaxValue.Double255.max;
    double correct = rCorrector.correct(value * max);
    return correct / max;
     }

   protected static double rUncorrect(GammaCorrector rCorrector, double value) {
    double max = RGB.MaxValue.Double255.max;
    double uncorrect = rCorrector.uncorrect(value * max);
    return uncorrect / max;
     }*/

  /**
   *
   * @param model1 SCurveModel1Thread
   * @deprecated
   */
  protected void produceRGBInverseGammaSCurve(SCurveModel1Thread model1) {
    double[] input = new double[256];
    double[] rOutput = new double[256];
    double[] gOutput = new double[256];
    double[] bOutput = new double[256];
    LCDModel.Factor[] factors = model1.getModelFactors();

    //以單一頻道有值的色塊放到SCurveI,所得到的model結果
    for (int x = 0; x < 256; x++) {
      double d = (double) x / 255;

      RGB r = new RGB(RGB.ColorSpace.unknowRGB, new double[] {d, 0, 0},
                      RGB.MaxValue.Double1);
      RGB newR = model1.getLuminanceRGB(r); //, factors);
      RGB g = new RGB(RGB.ColorSpace.unknowRGB, new double[] {0, d, 0},
                      RGB.MaxValue.Double1);
      RGB newG = model1.getLuminanceRGB(g); //, factors);
      RGB b = new RGB(RGB.ColorSpace.unknowRGB, new double[] {0, 0, d},
                      RGB.MaxValue.Double1);
      RGB newB = model1.getLuminanceRGB(b); //, factors);

      input[x] = d;

      rOutput[x] = correct._RrCorrector.correct(RGBRationalize(newR.R));
      gOutput[x] = correct._GrCorrector.correct(RGBRationalize(newG.G));
      bOutput[x] = correct._BrCorrector.correct(RGBRationalize(newB.B));
    }
  }

  /**
   *
   * @param singleChannelPatch Collection
   * @return double[][]
   */
  protected final double[][] produceRGB2XYZ_3x3(Collection<Patch>
      singleChannelPatch) {
    //==========================================================================
    // 轉成rgb和XYZ的double陣列
    //==========================================================================
    int size = singleChannelPatch.size();
    double[][] rgbArray = new double[size][3];
    double[][] XYZArray = new double[size][3];
    Patch[] patches = new Patch[size];
    singleChannelPatch.toArray(patches);

    for (int x = 0; x < size; x++) {
      Patch p = patches[x];
      p.getRGB().getValues(rgbArray[x]);
      p.getXYZ().getValues(XYZArray[x]);
      rgbArray[x] = correct.gammaCorrect(rgbArray[x]);
      XYZArray[x] = DoubleArray.minus(XYZArray[x], flare.getFlareValues());
    }
    //==========================================================================

    Regression regression = new Regression(rgbArray, XYZArray);
    regression.regress();
    return regression.getCoefs();
  }

  public int getMaxIterativeTimes() {
    return 15;
  }

  public boolean isCrossTalkRemove() {
    return crossTalkRemove;
  }

  public void setCrossTalkRemove(boolean crossTalkRemove) {
    this.crossTalkRemove = crossTalkRemove;
  }

  public String getDescription() {
    return "YY";
  }

}
