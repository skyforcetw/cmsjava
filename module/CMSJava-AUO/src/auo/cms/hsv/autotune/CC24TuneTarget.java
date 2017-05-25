package auo.cms.hsv.autotune;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.lcd.*;
import shu.math.*;

//import auo.cms.hsv.old.*;

/**
 * <p>Title: Colour Management System</p>
 * 因為不均勻分布, 不建議使用
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
public class CC24TuneTarget
    extends TuneTarget implements SingleTuneTarget {

  public static enum Mode {
    HueShift, Interpolate, InterpolateEach
  }

  /**
   * getTarget
   *
   * @param hue double
   * @param saturation double
   * @param value double
   * @return CIEXYZ
   */
  public CIEXYZ getTarget(double hue, double saturation, double value) {
    HSV hsv = new HSV(RGB.ColorSpace.sRGB_gamma22, new double[] {hue,
                      saturation, value});
    RGB rgb = hsv.toRGB();
    return rgb.toXYZ();
  }

  /**
   * getTarget
   *
   * @param hue double
   * @return CIEXYZ
   */
  public CIEXYZ getTarget(int hue) {
//    HSV hsv = getTuneSpot(hue);
//    return hsv.toRGB().toXYZ();
    throw new UnsupportedOperationException();
  }

  /**
   * getTuneSpot
   *
   * @param hue double
   * @return HSV
   */
  public HSV getTuneSpot(double hue) {
    throw new UnsupportedOperationException();
  }

  /**
   * getTuneSpots
   *
   * @param hue double
   * @return HSV[]
   */
  public HSV[] getTuneSpots(double hue) {
    return multiPatchesMap.get(hue);
  }

  private List<Patch> cc24PatchList;
  private Map<Double, HSV[]> multiPatchesMap;

  private void initCC24PatchList() {
    LCDTarget target = DC2LCDTargetAdapter.Instance.getCameraTarget(DCTarget.
        Chart.CC24, RGB.ColorSpace.sRGB);
//    LCDTarget target = LCDTarget.Instance.getCameraTarget(LCDTarget.Number.
//        ColorChecker24, RGB.ColorSpace.sRGB);
    List<Patch> patchList = target.getPatchList();
    cc24PatchList = new ArrayList<Patch> ();

    for (Patch p : patchList) {
      RGB rgb = p.getRGB();
      HSV hsv = new HSV(rgb);
      if (hsv.S > 3) { //抓3僅僅是經驗值
        if (hsv.S > 100) {
          hsv.S = 100;
          Patch.Operator.setRGB(p, hsv.toRGB());
        }
        cc24PatchList.add(p);
      }
    }

    Collections.sort(cc24PatchList, new HueOfPatchComparator());
//    for (Patch p : cc24PatchList) {
//      RGB rgb = p.getRGB();
//      HSV hsv = new HSV(rgb);
//      System.out.println(hsv);
//    }
  }

  private void setMultiPatchesMap(double hue, int ...indexs) {
    int size = indexs.length;
    HSV[] hsvArray = new HSV[size];
    for (int i = 0; i < size; i++) {
      Patch patch = cc24PatchList.get(indexs[i]);
      HSV hsv = new HSV(patch.getRGB());
      hsv.H = hue;
      hsvArray[i] = hsv;
    }
    multiPatchesMap.put(hue, hsvArray);
  }

//  private void interpolateMultiPatchesMap(double hue,int index1,int index2) {
//
//  }
//  private CIECAM02 ciecam02;
//  private void initCIECAM02() {
//    if (null == ciecam02) {
//      ViewingConditions vc = ViewingConditions.TypicalViewingConditions;
//      ciecam02 = new CIECAM02(vc);
//    }
//  }

  private final HSV interpolateHSV(int index1, int index2) {
    Patch p1 = cc24PatchList.get(index1);
    Patch p2 = cc24PatchList.get(index2);
    HSV hsv1 = new HSV(p1.getRGB());
    HSV hsv2 = new HSV(p2.getRGB());

    double hue = Interpolation.linear(0, 1, hsv1.H, hsv2.H, 0.5);
    HSV newHSV = getInterpolateHSV(hue, hsv1, hsv2);
    return newHSV;
  }

  private final void interpolateMultiPatchesMapFromCC24PatchListAndHSV(int hue,
      int index, int index1, int index2) {

    switch (mode) {
      case Interpolate:
        HSV hsv12 = interpolateHSV(index1, index2);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(hue, index, hsv12);
        break;
      case InterpolateEach:
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(hue, index,
            index1, index2);
        break;
    }

  }

  private void interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(double hue,
      int index, int index1, int index2) {
    HSV hsv = new HSV(cc24PatchList.get(index).getRGB());
    HSV hsv1 = new HSV(cc24PatchList.get(index1).getRGB());
    HSV hsv2 = new HSV(cc24PatchList.get(index2).getRGB());

    HSV newHSV1 = getInterpolateHSV(hue, hsv, hsv1);
    HSV newHSV2 = getInterpolateHSV(hue, hsv, hsv2);
//    HSV hsv1 = interpolateHSV(index, index1);
//    HSV hsv2 = interpolateHSV(index, index2);

    HSV[] hsvArray = new HSV[] {
        newHSV1, newHSV2};
    multiPatchesMap.put(hue, hsvArray);
  }

  private void interpolateMultiPatchesMapFromCC24PatchListAndHSV(double hue,
      int index, HSV hsv) {
    Patch p = cc24PatchList.get(index);
    HSV hsv1 = new HSV(p.getRGB());
    HSV newHSV = getInterpolateHSV(hue, hsv, hsv1);
    HSV[] hsvArray = new HSV[] {
        newHSV};
    multiPatchesMap.put(hue, hsvArray);
  }

  private final HSV getInterpolateHSV(int hue, HSV hsv1, HSV hsv2) {
    return getInterpolateHSV( (double) hue, hsv1, hsv2);
  }

  private final HSV getInterpolateHSV(double hue, HSV hsv1, HSV hsv2) {
    HSV newHSV = new HSV(hsv1.getRGBColorSpace());
    double hsv1Hue = hsv1.H;
    if (hsv1.H > 330 && hsv2.H < 30) {
      hsv1Hue = hsv1.H - 360;
    }
    newHSV.H = hue;
    newHSV.S = Interpolation.linear(hsv1Hue, hsv2.H, hsv1.S, hsv2.S, hue);
    newHSV.V = Interpolation.linear(hsv1Hue, hsv2.H, hsv1.V, hsv2.V, hue);
    return newHSV;
  }

  private void interpolateMultiPatchesMapFromCC24PatchList(double hue,
      int index1,
      int index2) {
    Patch p1 = cc24PatchList.get(index1);
    Patch p2 = cc24PatchList.get(index2);
//    RGB rgb1 = p1.getRGB();
//    RGB rgb2 = p2.getRGB();
    HSV hsv1 = new HSV(p1.getRGB());
    HSV hsv2 = new HSV(p2.getRGB());
//    HSV newHSV = new HSV(rgb1.getRGBColorSpace());
//    newHSV.H = hue;
//    newHSV.S = Interpolation.linear(hsv1.H, hsv2.H, hsv1.S, hsv2.S, hue);
//    newHSV.V = Interpolation.linear(hsv1.H, hsv2.H, hsv1.V, hsv2.V, hue);
    HSV newHSV = getInterpolateHSV(hue, hsv1, hsv2);

    HSV[] hsvArray = new HSV[] {
        newHSV};
    multiPatchesMap.put(hue, hsvArray);
  }

  private void interpolateMultiPatchesMap(double hue, double hue1, double hue2) {
    HSV[] hsvArray1 = multiPatchesMap.get(hue1);
    HSV[] hsvArray2 = multiPatchesMap.get(hue2);
    if (hsvArray1.length != hsvArray2.length && hsvArray1.length != 1) {
      throw new IllegalArgumentException(
          "hsvArray1.length != hsvArray2.length && hsvArray1.length != ");
    }
    HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, 0, 0});
    hsv.S = Interpolation.linear(hue1, hue2, hsvArray1[0].S, hsvArray2[0].S,
                                 hue);
    hsv.V = Interpolation.linear(hue1, hue2, hsvArray1[0].V, hsvArray2[0].V,
                                 hue);

    HSV[] hsvArray = new HSV[] {
        hsv};
    multiPatchesMap.put(hue, hsvArray);
  }

  private void initMultiPatchesMapHueShift() {
    multiPatchesMap = new Hashtable<Double, HSV[]> ();

    setMultiPatchesMap(0, 17);
    setMultiPatchesMap(15, 0, 1);
    setMultiPatchesMap(30, 2);
    setMultiPatchesMap(45, 3, 4);
    setMultiPatchesMap(75, 5);
    setMultiPatchesMap(90, 6);
    setMultiPatchesMap(120, 7);
    setMultiPatchesMap(165, 8);
    setMultiPatchesMap(195, 9);
    setMultiPatchesMap(210, 10);
    setMultiPatchesMap(225, 11, 12);
    setMultiPatchesMap(240, 13);
    setMultiPatchesMap(285, 14);
    setMultiPatchesMap(315, 15);
    setMultiPatchesMap(345, 16);

//interpolate
    interpolateMultiPatchesMap(105, 90, 120);
    interpolateMultiPatchesMap(135, 120, 165);
    interpolateMultiPatchesMap(150, 120, 165);
    interpolateMultiPatchesMap(180, 165, 195);
    interpolateMultiPatchesMap(255, 240, 285);
    interpolateMultiPatchesMap(270, 240, 285);
    interpolateMultiPatchesMap(300, 285, 315);
    interpolateMultiPatchesMap(330, 315, 345);

//最複雜的Hue 60度
    HSV[] hue450HSVArray = multiPatchesMap.get(45.);
    double hue45S = Interpolation.linear(0, 1, hue450HSVArray[0].S,
                                         hue450HSVArray[1].S, 0.5);
    double hue45V = Interpolation.linear(0, 1, hue450HSVArray[0].V,
                                         hue450HSVArray[1].V, 0.5);
    HSV[] hue75HSVArray = multiPatchesMap.get(75.);
    double hue75S = hue75HSVArray[0].S;
    double hue75V = hue75HSVArray[0].V;
    HSV hsv60 = new HSV(RGB.ColorSpace.sRGB, new double[] {60, 0, 0});
    hsv60.S = Interpolation.linear(45, 75, hue45S, hue75S, 60);
    hsv60.V = Interpolation.linear(45, 75, hue45V, hue75V, 60);
    HSV[] hue60HSVArray = new HSV[] {
        hsv60};
    multiPatchesMap.put(60., hue60HSVArray);

  }

  private void initMultiPatchesMapInterpolate() {
    multiPatchesMap = new Hashtable<Double, HSV[]> ();

    //0~60
    switch (mode) {
      case Interpolate:
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(0, 17, 0, 1);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(15, 17, 0, 1);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(30, 2, 3, 4);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(45, 2, 3, 4);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(60, 5, 3, 4);
        break;
      case InterpolateEach:
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(0, 17, 0, 1);
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(15, 17, 0, 1);
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(30, 2, 3, 4);
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(45, 2, 3, 4);
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(60, 5, 3, 4);
    }

    interpolateMultiPatchesMapFromCC24PatchList(75, 6, 7);
    interpolateMultiPatchesMapFromCC24PatchList(90, 7, 8);
    interpolateMultiPatchesMapFromCC24PatchList(105, 7, 8);
    interpolateMultiPatchesMapFromCC24PatchList(120, 7, 8);
    interpolateMultiPatchesMapFromCC24PatchList(135, 7, 8);
    interpolateMultiPatchesMapFromCC24PatchList(150, 7, 8);
    interpolateMultiPatchesMapFromCC24PatchList(165, 8, 9);
    interpolateMultiPatchesMapFromCC24PatchList(180, 8, 9);
    interpolateMultiPatchesMapFromCC24PatchList(195, 9, 10);
    interpolateMultiPatchesMapFromCC24PatchList(210, 10, 11);

    //225~240
    switch (mode) {
      case Interpolate:
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(225, 10, 11, 12);
        interpolateMultiPatchesMapFromCC24PatchListAndHSV(240, 13, 11, 12);
        break;
      case InterpolateEach:
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(225, 10, 11, 12);
        interpolateMultiPatchesMapFromCC24PatchListAndHSVEach(240, 13, 11, 12);
        break;
    }

    interpolateMultiPatchesMapFromCC24PatchList(255, 13, 14);
    interpolateMultiPatchesMapFromCC24PatchList(270, 14, 15);
    interpolateMultiPatchesMapFromCC24PatchList(285, 15, 16);
    interpolateMultiPatchesMapFromCC24PatchList(300, 15, 16);
    interpolateMultiPatchesMapFromCC24PatchList(315, 15, 16);
    interpolateMultiPatchesMapFromCC24PatchList(330, 15, 16);
    interpolateMultiPatchesMapFromCC24PatchList(345, 16, 17);
  }

  private void initMultiPatchesMap(Mode mode) {
    switch (mode) {
      case HueShift:
        initMultiPatchesMapHueShift();
        break;
      case Interpolate:
      case InterpolateEach:
        initMultiPatchesMapInterpolate();
        break;

    }
  }

  private Mode mode;
  public CC24TuneTarget(Mode mode) {
    this.mode = mode;
    initCC24PatchList();
    initMultiPatchesMap(mode);
  }

  static class HueOfPatchComparator
      implements Comparator<Patch> {

    public int compare(Patch patch1, Patch patch2) {
      HSV hsv1 = new HSV(patch1.getRGB());
      HSV hsv2 = new HSV(patch2.getRGB());
      return Double.compare(hsv1.H, hsv2.H);
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      throw new UnsupportedOperationException();
    }

  }

  public static void main(String[] args) {
//    autoTunerTest(args);
//    targetShow(args);
  }

//  public static void targetShow(String[] args) {
////    CC24TuneTarget tuneTarget = new CC24TuneTarget(Mode.InterpolateEach);
//    sRGBTuneTarget tuneTarget = new sRGBTuneTarget(sRGBTuneTarget.Patches.
//        Integrated);
//    Plot3D plot = Plot3D.getInstance();
//    OpponentColorAttribute oca = null;
//    for (int x = 0; x < 360; x += 15) {
//      HSV[] hsvArray = tuneTarget.getTuneSpots(x);
//      for (HSV hsv : hsvArray) {
//        java.awt.Color c = hsv.toRGB().getColor();
//        oca = new OpponentColorAttribute(hsv);
////        oca.getValues();
////        System.out.println(Arrays.toString(oca.getBandNames()));
//        plot.addColorSpace(Integer.toString(x), c, oca);
//        plot.addLinePlot(Integer.toString(x) + "_", c, new double[] {0, 0, 0},
//                         oca.getValues());
//        System.out.print(hsv + " ");
//      }
//      System.out.println("");
//    }
//    plot.setVisible();
//    plot.setFixedBounds(0, 0, 100);
//    plot.setFixedBounds(1, -100, 100);
//    plot.setFixedBounds(2, -100, 100);
//    plot.setAxisLabels(oca.getBandNames());
//  }

//  /**
//   *
//   * @param args String[]
//   * @deprecated
//   */
//  public static void autoTunerTest(String[] args) {
//    Util.produceHSVAdjustProducer();
//    ColorIndex colorIndex = new ColorIndex(Util.model);
//    IntegerSaturationFormula integerSaturationFormula = new
//        IntegerSaturationFormula( (byte) 7, 4);
//    AutoTuner autotuner = new AutoTuner(Util.model, integerSaturationFormula,
//                                        colorIndex);
//    boolean useCC24AsTarget = true;
//    //==========================================================================
//    TuneTarget tuneTarget = null;
//    if (useCC24AsTarget) {
//      tuneTarget = new CC24TuneTarget(Mode.InterpolateEach);
//      autotuner.setFitMode(AutoTuner.FitMode.MultiSpot);
//    }
//    else {
//      tuneTarget = new sRGBTuneTarget(sRGBTuneTarget.Patches.
//                                      Integrated);
//      autotuner.setFitMode(AutoTuner.FitMode.SingleSpot);
//
//    }
////    sRGBTuneTarget tuneTarget = new sRGBTuneTarget(sRGBTuneTarget.Patches.
////        Integrated);
////    autotuner.setFitMode(AutoTuner.FitMode.SingleSpot);
//
////    CC24TuneTarget tuneTarget = new CC24TuneTarget(Mode.InterpolateEach);
////    autotuner.setFitMode(AutoTuner.FitMode.MultiSpot);
//    //==========================================================================
////    for (int x = 0; x < 360; x += 15) {
////      HSV[] hsvs = tuneTarget.getTuneSpots(x);
////      System.out.println(x + " " + hsvs.length + " " + hsvs[0]);
////    }
//
//
//    TuneParameter tuneParameter = autotuner.getTuneParameter(tuneTarget);
//    System.out.println(tuneParameter);
////    System.out.println(Arrays.toString(autotuner.getIndexes()));
//
//    try {
//      tuneParameter.writeToFile("sRGB.lut");
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }
//  }
}

interface SingleTuneTarget {

}
