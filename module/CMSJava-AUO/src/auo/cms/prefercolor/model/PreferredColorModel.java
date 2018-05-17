package auo.cms.prefercolor.model;

import java.util.*;

import java.awt.image.*;

import auo.cms.prefercolor.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.gbd.*;
import shu.image.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.util.*;
import shu.cms.lcd.LCDTarget;
import shu.cms.lcd.LCDTargetBase;
import java.io.InputStream;
import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import java.io.*;
import shu.util.log.Logger;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PreferredColorModel {
//  private MemoryColorInterface memoryColorInterface;
  private MemoryColorInterface original;
  private MemoryColorInterface preffered;
  private GamutBoundaryDescriptor gbd;
  private static double ChromaPercent = 1.;
  public final static void setChromaPercent(double _percent) {
    ChromaPercent = _percent;
  }

  /**
   *
   * @param memoryColorInterface MemoryColorInterface 實驗產生的memory color
   * @param gbd GamutBoundaryDescriptor
   */

  /**
   *
   * @param original MemoryColorInterface 初始記憶色
   * @param preffered MemoryColorInterface 調整後記憶色, 為喜好色
   * @param gbd GamutBoundaryDescriptor
   */
  public PreferredColorModel(MemoryColorInterface original,
                             MemoryColorInterface preffered,
                             GamutBoundaryDescriptor gbd) {
    this.original = original;
    this.preffered = preffered;
    this.gbd = gbd;
    init();
  }

  private final static boolean T = true;
  private final static boolean F = false;
  //是否要做hue mapping
  private boolean doHueProcess = F;

  //============================================================================
  // LCh
  //============================================================================
  private boolean doLChProcess = F;
  private boolean doGlobalLightnessProcess = T;
  private boolean plotGlobalLightness = F;
  private boolean doLocalLightnessProcess = T;
  private boolean plotLocalLightness = F;
  private boolean doChromaProcess = T;
  //============================================================================

  //============================================================================
  // HSV
  //============================================================================
  private boolean doGlobalValueProcess = T;
  private boolean doLocalValueProcess = T;
  private boolean doSaturationProcess = T;
  //============================================================================

  /**
   * 取得喜好色的對應, 會在HSV空間作線性對應
   * @param hsv HSV
   * @return HSV
   */
  public HSV getHSV(HSV hsv) {
    //hue
    double hue = hueOfHSVLUT.getValue(hsv.H);

    //global lightness
    double valuePrime = globalValueLUT.getValue(hsv.V);
    //lightness by hue
    double valuePrime2 = localValueLUT.getValuePrime(hue,
        valuePrime);

    //==========================================================================
    // chroma
    //==========================================================================
    double saturation = hsv.S;
    saturation = (saturation > 100) ? 100 : saturation;
    double saturationPrime = saturationLUT.getValuePrime(hue, saturation);
    HSV result = new HSV(hsv.getRGBColorSpace(), new double[] {hue,
                         saturationPrime, valuePrime2});
    return result;
  }

  /**
   * 取得喜好色的對應 (會在LCh空間作線性對應)
   * @param LCh CIELCh
   * @return CIELCh
   */
  public CIELCh getLCh(CIELCh LCh) {
    return getLCh(LCh, false);
  }

  /**
   * 取得喜好色的對應 (會在LCh空間作線性對應)
   * @param LCh CIELCh
   * @param neutral boolean
   * @return CIELCh
   */
  private CIELCh getLCh(CIELCh LCh, boolean neutral) {
    if (!doLChProcess) {
      return null;
    }

    //hue
    double huePrime = hueOfLChLUT.getValue(LCh.h);

    //global lightness
    double lightness = globalLightnessLUT.correctKeyInRange(LCh.L);
    double lightnessPrime = globalLightnessLUT.getValue(lightness);
    //lightness by hue
    double lightnessPrime2 = localLightnessLUT.getValuePrime(huePrime,
        lightnessPrime);

    //==========================================================================
    // chroma
    //==========================================================================
    double chromaPrime = LCh.C;
    CIELCh clone = (CIELCh) LCh.clone();
    clone.h = huePrime;
    clone.L = lightnessPrime;
    //chroma by hue
    if (!neutral) {
      CIELCh boundary = gbd.getBoundaryLCh(clone);
      //此處使用到GBD
      if (!gbd.isOutOfGamut(clone, boundary)) {
        double normalizedChroma = LCh.C / boundary.C;
        double normalizedChromaPrime = chromaLUT.getValuePrime(
            huePrime,
            normalizedChroma);
        chromaPrime = normalizedChromaPrime * boundary.C;
      }
      else {
        chromaPrime = boundary.C;
      }
    }

    //==========================================================================

    //total
    CIELCh result = new CIELCh(lightnessPrime2, chromaPrime, huePrime,
                               LCh.getType());
    return result;
  }

  private void init() {
    //==========================================================================
    // 前期資料處裡
    //==========================================================================
    // 處理喜好色資訊(喜好色必然是存在獨立色空間的)
    CIELab[] referenceMemoryColorArray = getLabArray(original);
    CIELab[] prefferedMemoryColorArray = getLabArray(preffered);

    CIEXYZ white = preffered.getReferenceWhiteXYZ();
    //撈出亮度資料
    double[][] luminancePairArray = getLuminancePairArray(
        referenceMemoryColorArray,
        prefferedMemoryColorArray);
    CIELCh[][] LChPairArray = getLChPairArray(referenceMemoryColorArray,
                                              prefferedMemoryColorArray, white);
    Arrays.sort(LChPairArray, new HueOfLChComparator());
    //==========================================================================
    // 為了應用在硬體實現上, 把獨立色的喜好色轉換到設備色的
    double[][] valuePairArray = getValuePairArray(referenceMemoryColorArray,
                                                  prefferedMemoryColorArray);
    HSV[][] hsvPairArray = getHSVPairArray(referenceMemoryColorArray,
                                           prefferedMemoryColorArray,
                                           RGB.ColorSpace.sRGB);
    Arrays.sort(hsvPairArray, new HueOfHSVComparator());
    //==========================================================================

    //==========================================================================
    // 對照表生成
    //==========================================================================
    // LCh 對照表的生成
    if (doLChProcess) {
      initGlobalLightness(luminancePairArray, white);
      initLocalLightness(LChPairArray, globalLightnessLUT);
      initHueOfLCh(LChPairArray);
      initChromaLUT(LChPairArray);
    }
    //==========================================================================
    // HSV 對照表的生成
    initGlobalValue(valuePairArray);
    initLocalValue(hsvPairArray, globalValueLUT);
    initHueOfHSV(hsvPairArray);
    initSaturation(hsvPairArray);
    //==========================================================================
  }

  public final static CIELab[] getLabArray(MemoryColorInterface
                                           memoryColorInterface) {
    CIELab[] LabArray = new CIELab[6];
    LabArray[0] = memoryColorInterface.getBanana();
    LabArray[1] = memoryColorInterface.getFoliage();
    LabArray[2] = memoryColorInterface.getGrass();
    LabArray[3] = memoryColorInterface.getOrange();
    LabArray[4] = memoryColorInterface.getSkin();
    LabArray[5] = memoryColorInterface.getSky();
    return LabArray;
  }

  //=============================================================================
  // HSV
  private Interpolation1DLUT globalValueLUT;
  private Interpolation1DLUT hueOfHSVLUT;
  private DataInterpolator localValueLUT;
  private DataInterpolator saturationLUT;
  //=============================================================================

  //=============================================================================
  // LCh
  private Interpolation1DLUT globalLightnessLUT;
  private Interpolation1DLUT hueOfLChLUT;
  private DataInterpolator chromaLUT;
  private DataInterpolator localLightnessLUT;
  //=============================================================================

  private double[] getNormalInOutput(CIELCh[] LChPair) {
    //根據gbd找到這兩個顏色的boundary
    CIELCh boundary0 = gbd.getBoundaryLCh(LChPair[0]);
    CIELCh boundary1 = gbd.getBoundaryLCh(LChPair[1]);
    //找比較大的當作共同的boundary
    CIELCh boundary = (boundary0.C > boundary1.C) ? boundary0 : boundary1;
    //以此boundary做正規化
    double normalInput = LChPair[0].C / boundary.C;
    double normalOutput = LChPair[1].C / boundary.C;
    return new double[] {
        normalInput, normalOutput};
  }

  private void initHueOfHSV(HSV[][] hsvPairArray) {
    double[] inputHueArray = new double[16];
    double[] outputHueArray = new double[16];
    int index = 0;

    inputHueArray[0] = 0;
    outputHueArray[0] = 0;

    HSV[] hsvPair = hsvPairArray[index++];
    inputHueArray[1] = hsvPair[1].H - 15;
    outputHueArray[1] = hsvPair[1].H - 15;

    inputHueArray[2] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[2] = hsvPair[1].H;

    hsvPair = hsvPairArray[index++];
    inputHueArray[3] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[3] = hsvPair[1].H;

    inputHueArray[4] = hsvPair[1].H + 10;
    outputHueArray[4] = hsvPair[1].H + 10;

    hsvPair = hsvPairArray[index++];
    inputHueArray[5] = hsvPair[1].H - 10;
    outputHueArray[5] = hsvPair[1].H - 10;

    inputHueArray[6] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[6] = hsvPair[1].H;

    inputHueArray[7] = hsvPair[1].H + 15;
    outputHueArray[7] = hsvPair[1].H + 15;

    hsvPair = hsvPairArray[index++];
    inputHueArray[8] = hsvPair[1].H - 15;
    outputHueArray[8] = hsvPair[1].H - 15;

    inputHueArray[9] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[9] = hsvPair[1].H;

    hsvPair = hsvPairArray[index++];
    inputHueArray[10] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[10] = hsvPair[1].H;

    inputHueArray[11] = hsvPair[1].H + 15;
    outputHueArray[11] = hsvPair[1].H + 15;

    hsvPair = hsvPairArray[index++];
    inputHueArray[12] = hsvPair[1].H - 15;
    outputHueArray[12] = hsvPair[1].H - 15;

    inputHueArray[13] = doHueProcess ? hsvPair[0].H : hsvPair[1].H;
    outputHueArray[13] = hsvPair[1].H;

    inputHueArray[14] = hsvPair[1].H + 15;
    outputHueArray[14] = hsvPair[1].H + 15;

    inputHueArray[15] = 360;
    outputHueArray[15] = 360;

    hueOfHSVLUT = new Interpolation1DLUT(inputHueArray, outputHueArray,
                                         Interpolation1DLUT.Algo.LINEAR);
  }

  private void initSaturation(HSV[][] hsvPairArray) {
    int size = hsvPairArray.length - 1;
    double[] saturationHueArray = new double[size];
//    hueOfHSVArray = saturationHueArray;
    double[][][] saturationArray = new double[size][2][];
    int index = 0;

    for (int x = 0; x < hsvPairArray.length; x++) {
//      if (x == 1 || x == 4) {
      if (x == 4) {
        continue;
      }

      HSV[] hsvPair = hsvPairArray[x];
      double normalInput = hsvPair[0].S;
      double normalOutput = hsvPair[1].S;
      double[] input = null;
      double[] output = null;
//      if (x == 0) {
//        HSV[] hsvhPair2 = hsvPairArray[x + 1];
//        saturationHueArray[index] = (hsvPair[1].H + hsvhPair2[1].H) / 2;
//        double normalInput2 = hsvhPair2[0].S;
//        double normalOutput2 = hsvhPair2[1].S;
//
//        input = new double[] {
//            0, normalInput, normalInput2, 100};
//        output = new double[] {
//            0, normalOutput, normalOutput2, 100};
//      }
//
//      else {
      saturationHueArray[index] = hsvPair[1].H;
      input = new double[] {
          0, normalInput, 100};
      output = new double[] {
          0, normalOutput, 100};
//      }

      saturationArray[index][0] = input;
      saturationArray[index][1] = doSaturationProcess ? output : input;
      index++;
    }
    saturationLUT = new DataInterpolator(saturationHueArray, saturationArray);
  }

  private void initChromaLUT(CIELCh[][] LChPairArray) {
    int size = LChPairArray.length - 2;
    double[] chromaHueArray = new double[size];
    double[][][] chromaArray = new double[size][2][];
    int index = 0;

    for (int x = 0; x < LChPairArray.length; x++) {
      if (x == 1 || x == 4) {
        continue;
      }

      CIELCh[] LChPair = LChPairArray[x];
      double[] inoutput = getNormalInOutput(LChPair);
      double normalInput = inoutput[0];
      double normalOutput = inoutput[1];
      double[] input = null;
      double[] output = null;
      if (x == 0) {
        CIELCh[] LChPair2 = LChPairArray[x + 1];
        double[] inoutput2 = getNormalInOutput(LChPair2);
        double normalInput2 = inoutput2[0];
        double normalOutput2 = inoutput2[1];

        input = new double[] {
            0, normalInput, normalInput2, 1};
        output = new double[] {
            0, normalOutput, normalOutput2, 1};
      }

      else {
        input = new double[] {
            0, normalInput, 1};
        output = new double[] {
            0, normalOutput, 1};
      }

      chromaHueArray[index] = LChPair[1].h;
      chromaArray[index][0] = input;
      chromaArray[index][1] = doChromaProcess ? output : input;
      index++;
    }
    chromaLUT = new DataInterpolator(chromaHueArray, chromaArray);
//    chromaLUT.plot();
  }

  private CIELCh[][] getLChPairArray(CIELab[] LabArray1, CIELab[] LabArray2,
                                     CIEXYZ white) {
    if (LabArray1.length != LabArray2.length) {
      throw new IllegalArgumentException("LabArray1.length != LabArray2.length");
    }
    int size = LabArray1.length;
    CIELCh[][] LChPairArray = new CIELCh[size][];
    boolean inIPT = false;

    for (int x = 0; x < size; x++) {
      //找到Lab
      CIELab Lab1 = LabArray1[x];
      CIELab Lab2 = LabArray2[x];
      CIELCh LCh1 = null, LCh2 = null;

      if (inIPT) {
        //退回XYZ
        CIEXYZ XYZ1 = CIELab.toXYZ(Lab1, white);
        CIEXYZ XYZ2 = CIELab.toXYZ(Lab2, white);
        //再轉成IPT
        IPT ipt1 = IPT.fromXYZ(XYZ1, white);
        IPT ipt2 = IPT.fromXYZ(XYZ2, white);
        //scale 到Lab後
        ipt1.scaleToCIELab();
        ipt2.scaleToCIELab();
        //才轉成LCh
        LCh1 = new CIELCh(ipt1);
        LCh2 = new CIELCh(ipt2);
      }
      else {
        LCh1 = new CIELCh(Lab1);
        LCh2 = new CIELCh(Lab2);
      }

      //調整chroma值域
      double adoptChroma = ChromaPercent * (LCh2.C - LCh1.C) + LCh1.C;
      LCh2.C = adoptChroma;
      LChPairArray[x] = new CIELCh[] {
          LCh1, LCh2};
    }

    return LChPairArray;
  }

  private HSV[][] getHSVPairArray(CIELab[] LabArray1, CIELab[] LabArray2,
                                  RGB.ColorSpace colorspace) {
    if (LabArray1.length != LabArray2.length) {
      throw new IllegalArgumentException("LabArray1.length != LabArray2.length");
    }
    int size = LabArray1.length;
    CIEXYZ white = colorspace.getReferenceWhiteXYZ();
    HSV[][] hsvPairArray = new HSV[size][];

    for (int x = 0; x < size; x++) {
      //找到Lab
      CIELab Lab1 = LabArray1[x];
      CIELab Lab2 = LabArray2[x];
      CIEXYZ XYZ1 = CIELab.toXYZ(Lab1, white);
      CIEXYZ XYZ2 = CIELab.toXYZ(Lab2, white);
      RGB rgb1 = new RGB(colorspace, XYZ1);
      RGB rgb2 = new RGB(colorspace, XYZ2);
      HSV hsv1 = new HSV(rgb1);
      HSV hsv2 = new HSV(rgb2);
      hsvPairArray[x] = new HSV[] {
          hsv1, hsv2};
    }

    return hsvPairArray;
  }

  private void initHueOfLCh(CIELCh[][] LChPairArray) {
    double[] inputHueArray = new double[16];
    double[] outputHueArray = new double[16];
    int index = 0;

    inputHueArray[0] = 0;
    outputHueArray[0] = 0;

    CIELCh[] LChPair = LChPairArray[index++];
    inputHueArray[1] = LChPair[1].h - 15;
    outputHueArray[1] = LChPair[1].h - 15;

    inputHueArray[2] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[2] = LChPair[1].h;

    LChPair = LChPairArray[index++];
    inputHueArray[3] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[3] = LChPair[1].h;

    inputHueArray[4] = LChPair[1].h + 10;
    outputHueArray[4] = LChPair[1].h + 10;

    LChPair = LChPairArray[index++];
    inputHueArray[5] = LChPair[1].h - 10;
    outputHueArray[5] = LChPair[1].h - 10;

    inputHueArray[6] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[6] = LChPair[1].h;

    inputHueArray[7] = LChPair[1].h + 15;
    outputHueArray[7] = LChPair[1].h + 15;

    LChPair = LChPairArray[index++];
    inputHueArray[8] = LChPair[1].h - 15;
    outputHueArray[8] = LChPair[1].h - 15;

    inputHueArray[9] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[9] = LChPair[1].h;

    LChPair = LChPairArray[index++];
    inputHueArray[10] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[10] = LChPair[1].h;

    inputHueArray[11] = LChPair[1].h + 15;
    outputHueArray[11] = LChPair[1].h + 15;

    LChPair = LChPairArray[index++];
    inputHueArray[12] = LChPair[1].h - 15;
    outputHueArray[12] = LChPair[1].h - 15;

    inputHueArray[13] = doHueProcess ? LChPair[0].h : LChPair[1].h;
    outputHueArray[13] = LChPair[1].h;

    inputHueArray[14] = LChPair[1].h + 15;
    outputHueArray[14] = LChPair[1].h + 15;

    inputHueArray[15] = 360;
    outputHueArray[15] = 360;

    hueOfLChLUT = new Interpolation1DLUT(inputHueArray, outputHueArray,
                                         Interpolation1DLUT.Algo.LINEAR);
  }

  private RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
  private double[][] getValuePairArray(CIELab[] LabArray1,
                                       CIELab[] LabArray2) {
    if (LabArray1.length != LabArray2.length) {
      throw new IllegalArgumentException("LabArray1.length != LabArray2.length");
    }
    int size = LabArray1.length;
    double[][] valuePairArray = new double[size][];
    CIEXYZ white = (CIEXYZ) preffered.getReferenceWhiteXYZ().clone();
    white.normalizeY();
    for (int x = 0; x < size; x++) {
      CIELab Lab1 = LabArray1[x];
      CIELab Lab2 = LabArray2[x];
      CIEXYZ XYZ1 = CIELab.toXYZ(Lab1, white);
      CIEXYZ XYZ2 = CIELab.toXYZ(Lab2, white);
      RGB rgb1 = new RGB(colorspace, XYZ1);
      RGB rgb2 = new RGB(colorspace, XYZ2);
      HSV hsv1 = new HSV(rgb1);
      HSV hsv2 = new HSV(rgb2);
      valuePairArray[x] = new double[] {
          hsv1.V, hsv2.V};
    }
    return valuePairArray;
  }

  private double[][] getLuminancePairArray(CIELab[] LabArray1,
                                           CIELab[] LabArray2) {
    if (LabArray1.length != LabArray2.length) {
      throw new IllegalArgumentException("LabArray1.length != LabArray2.length");
    }
    int size = LabArray1.length;
    double[][] luminancePairArray = new double[size][];
    CIEXYZ white = preffered.getReferenceWhiteXYZ();
    for (int x = 0; x < size; x++) {
      CIELab Lab1 = LabArray1[x];
      CIELab Lab2 = LabArray2[x];
      CIEXYZ XYZ1 = CIELab.toXYZ(Lab1, white);
      CIEXYZ XYZ2 = CIELab.toXYZ(Lab2, white);
      luminancePairArray[x] = new double[] {
          XYZ1.Y, XYZ2.Y};
    }
    return luminancePairArray;
  }

  public double[] getHueOfHSVArray() {
    return hueOfHSVArray;
  }

  private double[] hueOfHSVArray;
  private void initLocalValue(HSV[][] hsvPairArray,
                              Interpolation1DLUT valueLUT) {
    int size = hsvPairArray.length - 2;
    double[] valueHueArray = new double[size];
    hueOfHSVArray = valueHueArray;
    double[][][] valueArray = new double[size][2][];
    int index = 0;
    for (int x = 0; x < hsvPairArray.length; x++) {
      HSV[] hsvPair = hsvPairArray[x];

      if (x == 3) {
        HSV[] hsvPair2 = hsvPairArray[x + 1];
        valueHueArray[index] = (hsvPair[1].H + hsvPair2[1].H) / 2;

        double[] input = new double[4];
        double[] output = new double[4];
        input[0] = 0;
        input[1] = valueLUT.getValue(hsvPair[0].V) / 100;
        input[2] = valueLUT.getValue(hsvPair2[0].V) / 100;
        input[3] = 1;

        output[0] = 0;
        output[1] = hsvPair[1].V / 100;
        output[2] = hsvPair2[1].V / 100;
        output[3] = 1;

        double gamma = doLocalValueProcess ? GammaFinder.findGamma(input,
            output) : 1;
        int valueLength = 101;
        double[][] gammaValurArray = getNormalGammaCurve(gamma, valueLength,
            100);
        valueArray[index] = gammaValurArray;
      }
      else if (x == 1 || x == 4) {
        continue;
      }
      else {
        valueHueArray[index] = hsvPair[1].H;
        double valuePrime = valueLUT.getValue(hsvPair[0].V);
        double targetValue = doLocalValueProcess ? hsvPair[1].V :
            valuePrime;

        valueArray[index][0] = new double[] {
            0, valuePrime, 100};
        valueArray[index][1] = new double[] {
            0, targetValue, 100};
      }

      index++;
    }

    localValueLUT = new DataInterpolator(valueHueArray, valueArray);
  }

  private void initLocalLightness(CIELCh[][] LChPairArray,
                                  Interpolation1DLUT lightnessLUT) {
    int size = LChPairArray.length;
    double[] lightnessHueArray = new double[size];
    double[][][] lightnessArray = new double[size][2][];
//    hueOfHSVArray = lightnessHueArray;
    Plot2D plot = plotLocalLightness ? Plot2D.getInstance() : null;

    for (int x = 0; x < size; x++) {
      CIELCh[] LChPair = LChPairArray[x];
      lightnessHueArray[x] = LChPair[1].h;

      if (x == 1 || x == 4) {
        lightnessArray[x][0] = lightnessArray[x - 1][0];
        lightnessArray[x][1] = lightnessArray[x - 1][1];
      }

      else {
        double lightnessPrime = lightnessLUT.getValue(LChPair[0].L);
        double targetLightness = doLocalLightnessProcess ? LChPair[1].L :
            lightnessPrime;

        lightnessArray[x][0] = new double[] {
            0, lightnessPrime, 100};
        lightnessArray[x][1] = new double[] {
            0, targetLightness, 100};
      }

      if (plotLocalLightness) {
        CIELab Lab = new CIELab(LChPair[0]);
        RGB rgb = new RGB(RGB.ColorSpace.sRGB, Lab.toXYZ());
        plot.addLinePlot(Double.toString(LChPair[0].h), rgb.getColor(),
                         new double[][] {lightnessArray[x][0],
                         lightnessArray[x][1]});
      }
    }

    if (plotLocalLightness) {
      plot.setVisible();
    }

    localLightnessLUT = new DataInterpolator(lightnessHueArray, lightnessArray);
  }

  private void initGlobalLightness(double[][] luminancePairArray, CIEXYZ white) {
    int size = luminancePairArray.length;
    double[] inputLuminanceArray = new double[size + 2];
    double[] outputLuminanceArray = new double[size + 2];

    double whiteLuminance = white.Y;

    //==========================================================================
    // 撈出亮度資料
    //==========================================================================
    for (int x = 0; x < size; x++) {
      double Y1 = luminancePairArray[x][0] / whiteLuminance;
      double Y2 = luminancePairArray[x][1] / whiteLuminance;
      inputLuminanceArray[x] = Y1;
      outputLuminanceArray[x] = Y2;
    }
    inputLuminanceArray[size] = outputLuminanceArray[size] = 0;
    inputLuminanceArray[size + 1] = outputLuminanceArray[size + 1] = 1;
    double gamma = GammaFinder.findGamma(inputLuminanceArray,
                                            outputLuminanceArray);
    CIEXYZ normalWhite = (CIEXYZ) white.clone();
    normalWhite.normalizeY();
    double[] whiteValues = white.getValues();
    CIEXYZ XYZ = new CIEXYZ(white.getValues(), white);
    int lightnessLength = 1001;
    double[] inputLightnessArray = new double[lightnessLength];
    double[] outputLightnessArray = new double[lightnessLength];

    for (int x = 0; x < 1000; x++) {
      double normal = x / 1000.;
      double normalGamma = GammaFinder.gamma(normal, gamma);

      XYZ.setValues(whiteValues);
      XYZ.times(normal);
      CIELab Lab = new CIELab(XYZ, white);
      inputLightnessArray[x] = Lab.L;

      XYZ.setValues(whiteValues);
      XYZ.times(normalGamma);
      CIELab Lab2 = new CIELab(XYZ, white);
      outputLightnessArray[x] = doGlobalLightnessProcess ? Lab2.L : Lab.L;
    }
    inputLightnessArray[1000] = 100;
    outputLightnessArray[1000] = 100;

    if (plotGlobalLightness) {
      Plot2D plot = Plot2D.getInstance("Global Lightness");

      plot.addLinePlot("Global Lightness", new double[][] {inputLightnessArray,
                       outputLightnessArray});
      for (int x = 0; x < size; x++) {
        plot.addCacheScatterPlot("", inputLuminanceArray[x] * 100,
                                 outputLuminanceArray[x] * 100);
      }

      plot.setVisible();
      plot.setFixedBounds(0, 0, 100);
      plot.setFixedBounds(1, 0, 100);
      plot.setAxisLabels("input Lightness", "output Lightness");

    }

    globalLightnessLUT = new Interpolation1DLUT(inputLightnessArray,
                                                outputLightnessArray,
                                                Interpolation1DLUT.Algo.LINEAR);

  }

  private final static double[][] getNormalGammaCurve(double gamma, int length,
      double max) {
    double[][] gammaCurve = new double[2][length];
    for (int x = 0; x < length; x++) {
      double normal = ( (double) x) / (length - 1);
      double normalxGamma = GammaFinder.gamma(normal, gamma);
      gammaCurve[0][x] = normal * max;
      gammaCurve[1][x] = normalxGamma * max;
    }
    return gammaCurve;
  }

  private void initGlobalValue(double[][] valuePairArray) {
    int size = valuePairArray.length;
    double[] inputValueArray = new double[size + 2];
    double[] outputValueArray = new double[size + 2];

    //==========================================================================
    // 撈出亮度資料
    //==========================================================================
    for (int x = 0; x < size; x++) {
      inputValueArray[x] = valuePairArray[x][0] / 100.;
      outputValueArray[x] = valuePairArray[x][1] / 100.;
    }
    inputValueArray[size] = outputValueArray[size] = 0;
    inputValueArray[size + 1] = outputValueArray[size + 1] = 1;
    double gamma = doGlobalValueProcess ?
        GammaFinder.findGamma(inputValueArray,
                                 outputValueArray) : 1;
    int valueLength = 101;
    double[][] gammaValurArray = getNormalGammaCurve(gamma, valueLength, 100);

    globalValueLUT = new Interpolation1DLUT(gammaValurArray[0],
                                            gammaValurArray[1],
                                            Interpolation1DLUT.Algo.LINEAR);

  }

  public final BufferedImage processImage(BufferedImage image,
                                          ProfileColorSpace pcs) {
    BufferedImage result = ImageUtils.cloneBufferedImage(image);
    WritableRaster raster = result.getRaster();
    int w = raster.getWidth();
    int h = raster.getHeight();
    double[] rgbValues = new double[3];
    double[] whiteValues = pcs.getReferenceWhite().getValues();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        raster.getPixel(x, y, rgbValues);
        rgbValues = DoubleArray.times(rgbValues, 1 / 255.);
        double[] XYZValues = pcs.toD65CIEXYZValues(rgbValues);
        double[] LChValues = CIELCh.XYZ2LChabValues(XYZValues, whiteValues);
        boolean neutral = rgbValues[0] == rgbValues[1] &&
            rgbValues[1] == rgbValues[2];
        CIELCh LCh = this.getLCh(new CIELCh(LChValues), neutral);
        double[] XYZValuesPrime = CIELCh.LChab2XYZValues(LCh.getValues(),
            whiteValues);
        double[] rgbValuesPrime = pcs.fromD65CIEXYZValues(XYZValuesPrime);
        rgbValuesPrime = DoubleArray.times(rgbValuesPrime, 255);
        RGB.rationalize(rgbValuesPrime, RGB.MaxValue.Double255);
        raster.setPixel(x, y, rgbValuesPrime);
      }
    }
    return result;

  }

  public final static BufferedImage processImage(BufferedImage image,
                                                 TetrahedralInterpolation
                                                 interpolation) {
    BufferedImage result = ImageUtils.cloneBufferedImage(image);
    WritableRaster raster = result.getRaster();
    int w = raster.getWidth();
    int h = raster.getHeight();
    double[] rgbValues = new double[3];

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        raster.getPixel(x, y, rgbValues);
        double[] resultValues = interpolation.getValues(rgbValues);
        raster.setPixel(x, y, resultValues);
      }
    }
    return result;
  }

  public void setReferenceWhite(CIEXYZ referenceWhite) {
    this.referenceWhite = referenceWhite;
    this.referenceWhiteValues = referenceWhite.getValues();
  }

  private CIEXYZ referenceWhite;
  private double[] referenceWhiteValues;
  private double[] normalizedRGBValues = new double[3];

  private double[] getXYZPrimeValues(double[] rgbValues, ProfileColorSpace pcs) {
    normalizedRGBValues[0] = rgbValues[0] / 255;
    normalizedRGBValues[1] = rgbValues[1] / 255;
    normalizedRGBValues[2] = rgbValues[2] / 255;

    double[] XYZValues = pcs.toD65CIEXYZValues(normalizedRGBValues);
    double[] LChValues = CIELCh.XYZ2LChabValues(XYZValues,
                                                referenceWhiteValues);
    CIELCh LCh = new CIELCh(LChValues);
    boolean neutral = normalizedRGBValues[0] == normalizedRGBValues[1] &&
        normalizedRGBValues[1] == normalizedRGBValues[2];
    CIELCh LChPrime = this.getLCh(LCh, neutral);

    double[] XYZValuesPrime = CIELCh.LChab2XYZValues(LChPrime.getValues(),
        referenceWhiteValues);
    return XYZValuesPrime;
  }

  private double[] getRGBValues(double[] rgbValues, ProfileColorSpace pcs) {
    double[] XYZValuesPrime = getXYZPrimeValues(rgbValues, pcs);
    double[] normalizedRGBValuesPrime = pcs.fromD65CIEXYZValues(
        XYZValuesPrime);
    normalizedRGBValuesPrime[0] *= 255;
    normalizedRGBValuesPrime[1] *= 255;
    normalizedRGBValuesPrime[2] *= 255;
    RGB.rationalize(normalizedRGBValuesPrime, RGB.MaxValue.Double255);
    return normalizedRGBValuesPrime;
  }

  public ProfileColorSpace produceLChPreferredProfileColorSpaceInsRGB() {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.
        ColorSpace.sRGB);
    TetrahedralInterpolation tetrahedralInterpolation =
        produceLChA2BTetrahedralInterpolation(pcs, 51);
    CIEXYZ white = pcs.getReferenceWhite();
    ProfileColorSpace preferredProfileColorSpace = ProfileColorSpace.Instance.
        get(tetrahedralInterpolation, null, white, "PreferredColorModel");
    return preferredProfileColorSpace;
  }

  public ProfileColorSpace produceHSVPreferredProfileColorSpaceInsRGBSignal() {
    ProfileColorSpace signalSource = ProfileColorSpace.Instance.get(RGB.
        ColorSpace.sRGB);
    return produceHSVPreferredProfileColorSpace(signalSource);
  }

  public ProfileColorSpace produceHSVPreferredProfileColorSpace(
      ProfileColorSpace signalSource) {
    TetrahedralInterpolation tetrahedralInterpolation =
        produceHSVA2BTetrahedralInterpolation(signalSource, 51);
    CIEXYZ white = signalSource.getReferenceWhite();
    ProfileColorSpace preferredProfileColorSpace = ProfileColorSpace.Instance.
        get(tetrahedralInterpolation, null, white, "PreferredColorModel");
    return preferredProfileColorSpace;
  }

  public TetrahedralInterpolation produceHSVA2ATetrahedralInterpolation(
      ProfileColorSpace pcs, int step) {
    int level = 255 / step;
    int grid = step + 1;

    double[][][] lut = new double[grid * grid * grid][2][];
    int index = 0;
    CIEXYZ referenceWhite = pcs.getD65ReferenceWhite();
    setReferenceWhite(referenceWhite);

    for (double r = 0; r <= 255; r += level) {
      for (double g = 0; g <= 255; g += level) {
        for (double b = 0; b <= 255; b += level) {
          lut[index][0] = new double[] {
              r, g, b};

          if (r == 0 && g == 0 && b == 0 || r == 255 && g == 255 && b == 255) {
            lut[index][1] = new double[] {
                r, g, b};
          }
          else {

            RGB rgb = new RGB(r, g, b);
            HSV hsv = new HSV(rgb);
            HSV hsv2 = this.getHSV(hsv);
            RGB rgb2 = hsv2.toRGB();
            lut[index][1] = rgb2.getValues(new double[3],
                                           RGB.MaxValue.Double255);

          }
          index++;
        }
      }
    }
    CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                        new double[] {255, 255, 255}, grid);
    TetrahedralInterpolation interpolator = new TetrahedralInterpolation(
        cubeTable);
    return interpolator;
  }

  /**
   * RGB-> HSV -> HSV' ->RGB - >XYZ
   * ^^^A  ^^^^^^^^^^^Prefer    ^^^B
   *
   * 四面體對照表的範圍固定在RxGxB 0~255x0~255x0~255的範圍內
   * 不過PreferredColor以HSV的形式做對應(為了對應硬體的方式)
   * 所以先在HSV取出Preferred Color後, 再以signalSource轉出對應的XYZ, 再產生出對照表
   * 此處的signalSource代表著訊號的預定色彩空間.
   *
   * 以上是種方式, 另外一路是走LCh對應, 比較有通用性
   * RGB-> XYZ-> LCh -> LCh'-> XYZ'
   * ^^^^^^^^^1  ^^^^^^^^^^^2
   * 1. 以signalSource轉換
   * 2. 以Preferred Color轉換
   *
   * @param signalSource ProfileColorSpace
   * @param step int
   * @return TetrahedralInterpolation
   */
  public TetrahedralInterpolation produceHSVA2BTetrahedralInterpolation(
      ProfileColorSpace signalSource, int step) {
    int level = 255 / step;
    int grid = step + 1;

    double[][][] lut = new double[grid * grid * grid][2][];
    int index = 0;
    //取white
    CIEXYZ referenceWhite = signalSource.getD65ReferenceWhite();
    setReferenceWhite(referenceWhite);
    double[] rgbValues = new double[3];

    for (double r = 0; r <= 255; r += level) {
      for (double g = 0; g <= 255; g += level) {
        for (double b = 0; b <= 255; b += level) {
          //A
          lut[index][0] = new double[] {
              r, g, b};

          RGB rgb = new RGB(r, g, b);
          HSV hsv = new HSV(rgb);
          HSV hsv2 = this.getHSV(hsv);
          RGB rgb2 = hsv2.toRGB();
          rgb2.getValues(rgbValues, RGB.MaxValue.Double1);

          //取XYZ
          double[] XYZValues = signalSource.toD65CIEXYZValues(rgbValues);
          //B
          lut[index][1] = XYZValues;
          index++;
        }
      }
    }
    CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                        new double[] {255, 255, 255}, grid);
    TetrahedralInterpolation interpolator = new TetrahedralInterpolation(
        cubeTable);
    return interpolator;
  }

  public TetrahedralInterpolation produceLChA2ATetrahedralInterpolation(
      ProfileColorSpace pcs, int step) {
    int level = 255 / step;
    int grid = step + 1;

    double[][][] lut = new double[grid * grid * grid][2][];
    int index = 0;
    CIEXYZ referenceWhite = pcs.getD65ReferenceWhite();
    setReferenceWhite(referenceWhite);
    double[] rgbValues = new double[3];

    for (double r = 0; r <= 255; r += level) {
      for (double g = 0; g <= 255; g += level) {
        for (double b = 0; b <= 255; b += level) {
          lut[index][0] = new double[] {
              r, g, b};

          if (r == 0 && g == 0 && b == 0 || r == 255 && g == 255 && b == 255) {
            lut[index][1] = new double[] {
                r, g, b};
          }
          else {
            rgbValues[0] = r;
            rgbValues[1] = g;
            rgbValues[2] = b;
            double[] rgbValuesPrime = getRGBValues(rgbValues, pcs);
            lut[index][1] = rgbValuesPrime;
          }
          index++;
        }
      }
    }
    CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                        new double[] {255, 255, 255}, grid);
    TetrahedralInterpolation interpolator = new TetrahedralInterpolation(
        cubeTable);
    return interpolator;
  }

  public TetrahedralInterpolation produceLChA2BTetrahedralInterpolation(
      ProfileColorSpace pcs, int step) {
    int level = 255 / step;
    int grid = step + 1;

    double[][][] lut = new double[grid * grid * grid][2][];
    int index = 0;
    CIEXYZ referenceWhite = pcs.getD65ReferenceWhite();
    setReferenceWhite(referenceWhite);
    double[] rgbValues = new double[3];

    for (double r = 0; r <= 255; r += level) {
      for (double g = 0; g <= 255; g += level) {
        for (double b = 0; b <= 255; b += level) {
          lut[index][0] = new double[] {
              r, g, b};
          rgbValues[0] = r;
          rgbValues[1] = g;
          rgbValues[2] = b;
          double[] XYZPrimeValues = getXYZPrimeValues(rgbValues, pcs);
          lut[index][1] = XYZPrimeValues;
          index++;
        }
      }
    }
    CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                        new double[] {255, 255, 255}, grid);
    TetrahedralInterpolation interpolator = new TetrahedralInterpolation(
        cubeTable);
    return interpolator;
  }

  /**
   * 產生PreferredColorModel在limitColorSpace的限制內
   * @param limitColorSpace ProfileColorSpace 限制在此色域內
   * @return PreferredColorModel
   */
  public final static PreferredColorModel getInstance(ProfileColorSpace
      limitColorSpace) {
    //==========================================================================

    AUORampXLSAdapter adapter = null;
    try {
      adapter = new AUORampXLSAdapter("psychophysics/eizo ramp.xls");
    }
    catch (FileNotFoundException ex) {
      Logger.log.error(ex);
      return null;
    }

    LCDTarget eizoTarget = LCDTarget.Instance.get(adapter);
    LCDTarget.Operator.gradationReverseFix(eizoTarget);
    //==========================================================================

    //==========================================================================
    // get MemoryColorInterface
    //==========================================================================
    ExperimentAnalyzer analyzer = new ExperimentAnalyzer(eizoTarget,
        "psychophysics/data");
    analyzer.analyze();
    MemoryColorInterface memoryColor = analyzer.getMemoryColorInterface();
    //==========================================================================
    //gbd由limitColorSpace產生而來
    GamutBoundaryDescriptor gbd = GamutBoundaryRGBDescriptor.getInstance(
        GamutBoundaryRGBDescriptor.Style.D65Threshold, limitColorSpace);

    PreferredColorModel preferredcolormodel = new PreferredColorModel(
        MemoryColorPatches.Korean.getInstance(), memoryColor, gbd);

    preferredcolormodel.setReferenceWhite(limitColorSpace.getReferenceWhite());
    return preferredcolormodel;
  }
}

class DataInterpolator {
  DataInterpolator(double[] hueArray, double[][][] dataArray) {
    this.hueArray = initHueArray(hueArray);
    this.lut = initLUT(dataArray);
  }

//  private void plotRawData(double[] hueArray, double[][][] dataArray) {
//    Plot3D plot = Plot3D.getInstance();
//    int size = hueArray.length;
//    for (int x = 0; x < size; x++) {
//      double hue = hueArray[x];
//      double[][] data = dataArray[x];
//    }
//
//    plot.setVisible();
//  }

  private double[] initHueArray(double[] hueArray) {
    int hueSize = hueArray.length;
    double[] newHueArray = new double[hueSize + 2];
    System.arraycopy(hueArray, 0, newHueArray, 1, hueSize);
    newHueArray[0] = hueArray[hueSize - 1] - 360;
    newHueArray[hueSize + 1] = hueArray[0] + 360;
    return newHueArray;
  }

  private Interpolation1DLUT[] initLUT(double[][][]
                                       dataArray) {
    int size = dataArray.length;
    Interpolation1DLUT[] luts = new Interpolation1DLUT[size + 2];
    for (int x = 0; x < size; x++) {
      Interpolation1DLUT lut = new Interpolation1DLUT(dataArray[x][0],
          dataArray[x][1], Interpolation1DLUT.Algo.LINEAR);
      luts[x + 1] = lut;
    }
    luts[0] = luts[size];
    luts[size + 1] = luts[1];
    return luts;
  }

  void plot() {
    Plot3D plot = Plot3D.getInstance();
    double[] valueArray = lut[0].getValueArray();
    double max = valueArray[valueArray.length - 1];
    for (double h = 0; h < 360; h += 5) {
      java.awt.Color c = HSV.getLineColor(h);
      for (int x = 0; x < 101; x++) {
        double normal = x / 100.;
        double y = max * normal;
        double prime = getValuePrime(h, y);
        plot.addScatterPlot("", c, h, y, prime);
      }
    }
    plot.setVisible();
  }

  public double getValuePrime(double hue, double value) {
    int left = Searcher.leftNearBinarySearch(hueArray, hue);
    double leftHue = hueArray[left];
    double rightHue = hueArray[left + 1];
    double leftValue = lut[left].getValue(value);
    double rightValue = lut[left + 1].getValue(value);
    double result = Interpolation.linear(leftHue, rightHue,
                                         leftValue, rightValue, hue);
    return result;
  }

  private Interpolation1DLUT[] lut;
  private double[] hueArray;
}

class HueOfLChComparator
    implements Comparator {
  /**
   * Compares its two arguments for order.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *   argument is less than, equal to, or greater than the second.
   */
  public int compare(Object o1, Object o2) {
    CIELCh[] LChPair1 = (CIELCh[]) o1;
    CIELCh[] LChPair2 = (CIELCh[]) o2;
    return Double.compare(LChPair1[0].h, LChPair2[0].h);
  }

  /**
   * Indicates whether some other object is &quot;equal to&quot; this
   * comparator.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> only if the specified object is also a
   *   comparator and it imposes the same ordering as this comparator.
   * @todo Implement this java.util.Comparator method
   */
  public boolean equals(Object obj) {
    return false;
  }

}

class DoubleArrayComparator
    implements Comparator {
  /**
   * Compares its two arguments for order.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *   argument is less than, equal to, or greater than the second.
   */
  public int compare(Object o1, Object o2) {
    double v1 = ( (double[]) o1)[0];
    double v2 = ( (double[]) o2)[0];
    return Double.compare(v1, v2);
  }

  /**
   * Indicates whether some other object is &quot;equal to&quot; this
   * comparator.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> only if the specified object is also a
   *   comparator and it imposes the same ordering as this comparator.
   * @todo Implement this java.util.Comparator method
   */
  public boolean equals(Object obj) {
    return false;
  }

}

class HueOfHSVComparator
    implements Comparator {
  /**
   * Compares its two arguments for order.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *   argument is less than, equal to, or greater than the second.
   */
  public int compare(Object o1, Object o2) {
    HSV[] hsvPair1 = (HSV[]) o1;
    HSV[] hsvPair2 = (HSV[]) o2;
    return Double.compare(hsvPair1[0].H, hsvPair2[0].H);
  }

  /**
   * Indicates whether some other object is &quot;equal to&quot; this
   * comparator.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> only if the specified object is also a
   *   comparator and it imposes the same ordering as this comparator.
   * @todo Implement this java.util.Comparator method
   */
  public boolean equals(Object obj) {
    return false;
  }

}
