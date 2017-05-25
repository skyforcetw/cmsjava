package auo.cms.test.intensity;

import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.Patch;
import java.util.*;
import shu.math.array.*;
import shu.plot.*;
import java.awt.Color;
import shu.math.lut.Interpolation1DLUT;
import shu.math.*;
import shu.cms.plot.PlotUtils;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class IntensityErrorAnalyzer {

  static List<Patch> getWhitePatchList(int maxR, int maxG, int maxB,
                                       MultiMatrixModel model) {
    List<Patch> patchList = new ArrayList<Patch> ();
    for (int x = 0; x < 256; x++) {
      double r = (x / 255.) * maxR;
      double g = (x / 255.) * maxG;
      double b = (x / 255.) * maxB;
      RGB rgb = new RGB(r, g, b);
      CIEXYZ XYZ = model.getXYZ(rgb, false);
//      Patch p = new Patch("", XYZ, null,
//                          new RGB( (double) x, (double) x, (double) x));
      Patch p = new Patch("", XYZ, null, rgb);
      patchList.add(p);
    }
    return patchList;
  }

  static double[] findDG(double[][] intensity, double[][] rgbKeys) {
//    int size = intensity[0].length;
//  double[] keys = new double[size];
//  for (int x = 0; x < size; x++) {
//    keys[x] = x;
//  }
    Interpolation1DLUT r = new Interpolation1DLUT(rgbKeys[0], intensity[0],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
    Interpolation1DLUT g = new Interpolation1DLUT(rgbKeys[1], intensity[1],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
//  double[] bkeys = new double[size];
//  for (int x = 0; x < size; x++) {
//    bkeys[x] = x / (size - 1.) * 247;
//  }
//  bkeys = keys;
    Interpolation1DLUT b = new Interpolation1DLUT(rgbKeys[2], intensity[2],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
    double[] dg = new double[3];
    double rv = r.correctValueInRange(1);
    double gv = g.correctValueInRange(1);
    double bv = b.correctValueInRange(1);
    dg[0] = r.getKey(rv);
    dg[1] = g.getKey(gv);
    dg[2] = b.getKey(bv);
    final double base = 1.;
    if (1 != base) {
      int rr = (int) (dg[0] * base);
      int gg = (int) (dg[1] * base);
      int bb = (int) (dg[2] * base);
      dg[0] = rr / base;
      dg[1] = gg / base;
      dg[2] = bb / base;
    }
    return dg;
  }

  /**
   *
   * @param intensity double[][]
   * @return double[]
   * @deprecated
   */
  static double[] findDG(double[][] intensity) {
    int size = intensity[0].length;
    double[] keys = new double[size];
    for (int x = 0; x < size; x++) {
      keys[x] = x;
    }
    Interpolation1DLUT r = new Interpolation1DLUT(keys, intensity[0],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
    Interpolation1DLUT g = new Interpolation1DLUT(keys, intensity[1],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
    double[] bkeys = new double[size];
    for (int x = 0; x < size; x++) {
      bkeys[x] = x / (size - 1.) * 247;
    }
    bkeys = keys;
    Interpolation1DLUT b = new Interpolation1DLUT(bkeys, intensity[2],
                                                  Interpolation1DLUT.Algo.
                                                  LINEAR);
    double[] dg = new double[3];
    double rv = r.correctValueInRange(1);
    double gv = g.correctValueInRange(1);
    double bv = b.correctValueInRange(1);
    dg[0] = r.getKey(rv);
    dg[1] = g.getKey(gv);
    dg[2] = b.getKey(bv);
    final double base = 1.;
    if (1 != base) {
      int rr = (int) (dg[0] * base);
      int gg = (int) (dg[1] * base);
      int bb = (int) (dg[2] * base);
      dg[0] = rr / base;
      dg[1] = gg / base;
      dg[2] = bb / base;
    }
    return dg;
  }

  static double[][] getIntensity(MaxMatrixIntensityAnalyzer analyzer, List<Patch>
      patchList) {
    int size = patchList.size();
    double[][] intensityArray = new double[size][];
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      RGB intensity = analyzer.getIntensity(XYZ);
      intensityArray[x] = intensity.getValues();
    }
    return DoubleArray.transpose(intensityArray);
  }

  static Plot2D plotIntensity(String title, double[][] intensity) {
    Plot2D plot = Plot2D.getInstance(title);
    plot.addLinePlot("r", Color.red, 0, 255, intensity[0]);
    plot.addLinePlot("g", Color.green, 0, 255, intensity[1]);
    plot.addLinePlot("b", Color.blue, 0, 255, intensity[2]);
    plot.setVisible();
    plot.setAxeLabel(0, "Gray Level");
    plot.setAxeLabel(1, "Intensity");
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 0, 1);
    plot.addLegend();
    return plot;
  }

  final static CIEXYZ[] getTarget(double gamma, CIEXYZ whiteXYZ,
                                  CIEXYZ blackXYZ, int grayCount) {
    CIEXYZ[] targetArray = new CIEXYZ[grayCount];
    CIExyY target = new CIExyY(whiteXYZ);
    double blackY = blackXYZ.Y;
    double whiteY = whiteXYZ.Y;
    for (int x = 0; x < grayCount; x++) {
      double normal = ( (double) x) / (grayCount - 1);
      double g = Math.pow(normal, gamma);
      double Y = g * (whiteY - blackY) + blackY;
      target.Y = Y;
      targetArray[x] = target.toXYZ();
    }
    return targetArray;
  }

  static int findMaxBIntensityIndex(double[][] wIntensity) {
    int size = wIntensity[0].length;
    double preIntensity = -1;
    for (int x = size - 1; x > 0; x--) {
      double intensity = wIntensity[2][x];
      if (preIntensity == -1) {
        preIntensity = intensity;
        continue;
      }
      else if (intensity < preIntensity) {
        return x + 1;
      }
      preIntensity = intensity;
    }
    return size - 1;
  }

  static List<Patch> getPatchList(double[][] dglut, MultiMatrixModel model) {
    List<Patch> patchList = new ArrayList<Patch> ();

    int size = dglut.length;
//CIEXYZ[] XYZArray = new CIEXYZ[size];
    for (int x = 0; x < size; x++) {
      double[] dg = dglut[x];
      RGB rgb = new RGB(dg[0], dg[1], dg[2]);
      CIEXYZ XYZ = model.getXYZ(new RGB(dg[0], dg[1], dg[2]), false);
//  XYZArray[x] = XYZ;
      Patch p = new Patch("", XYZ, null, rgb);
      patchList.add(p);
    }

    return patchList;
  }

  static MaxMatrixIntensityAnalyzer getStandardAnayzer(LCDTarget lcdTarget) {
//  CIEXYZ blackXYZ = lcdTarget.getBlackPatch().getXYZ();
    CIEXYZ whiteXYZ = lcdTarget.getWhitePatch().getXYZ();
    CIEXYZ rXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.R).getXYZ();
    CIEXYZ gXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.G).getXYZ();
    CIEXYZ bXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.B).getXYZ();

    MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();
    analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
    analyzer.setupComponent(RGB.Channel.R, rXYZ);
    analyzer.setupComponent(RGB.Channel.G, gXYZ);
    analyzer.setupComponent(RGB.Channel.B, bXYZ);
    analyzer.enter();
    return analyzer;
  }

  static int findInverseBIntensityIndex(LCDTarget lcdTarget) {
    MaxMatrixIntensityAnalyzer stdAnalyzer = getStandardAnayzer(lcdTarget);
    List<Patch> wPatchList = lcdTarget.filter.grayPatch(true);
    double[][] intensity = getIntensity(stdAnalyzer, wPatchList);
    int size = intensity[0].length;
    double preIntensity = intensity[2][size - 1];
    for (int x = size - 2; x > 0; x--) {
      double i = intensity[2][x];
      if (i < preIntensity) {
        return x + 1;
      }
      preIntensity = i;
    }
    return -1;
  }

  static CIEXYZ[] alterTargetArray(CIEXYZ[] targetArray, double gamma,
                                   CIEXYZ whiteXYZ) {
    int size = targetArray.length;
    CIEXYZ blackXYZ = targetArray[0];
    CIEXYZ[] result = new CIEXYZ[size];
    result[size - 1] = (CIEXYZ) targetArray[size - 1].clone();
    double targetLuminance = whiteXYZ.Y - blackXYZ.Y;
    for (int x = 0; x < size - 1; x++) {
      double normal = x / (size - 1.);
      double luminance = Math.pow(normal, gamma) * targetLuminance + blackXYZ.Y;

      CIEXYZ target = targetArray[x];
      CIExyY xyY = new CIExyY(target);
      xyY.Y = luminance;
      result[x] = xyY.toXYZ();
    }
    return result;
  }

  static void intensityErrorAnalyze(LCDTarget lcdTarget) {
    CIEXYZ whiteXYZ = lcdTarget.getWhitePatch().getXYZ();
    CIEXYZ blackXYZ = lcdTarget.getBlackPatch().getXYZ();
    CIEXYZ rXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.R).getXYZ();
    CIEXYZ gXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.G).getXYZ();
    CIEXYZ bXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.B).getXYZ();
    System.out.println(rXYZ);
    System.out.println(gXYZ);
    System.out.println(bXYZ);

    MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();
    analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
    analyzer.setupComponent(RGB.Channel.R, rXYZ);
    analyzer.setupComponent(RGB.Channel.G, gXYZ);
    analyzer.setupComponent(RGB.Channel.B, bXYZ);
    analyzer.enter();
    analyzer.setTargetRatio(new double[] {1, 1, 1});

    List<Patch> wPatchList = lcdTarget.filter.grayPatch(true);
    Patch p255 = wPatchList.get(255);
    Patch p129 = wPatchList.get(129);
    Patch p128 = wPatchList.get(128);
    Patch p127 = wPatchList.get(127);
    CIEXYZ w128XYZ = p128.getXYZ();
    RGB i255 = analyzer.getIntensity(p255.getXYZ());
    RGB i128 = analyzer.getIntensity(w128XYZ);
    System.out.println("i255 " + i255);
    System.out.println("i129 " + analyzer.getIntensity(p129.getXYZ()));
    System.out.println("i128 " + i128);
    System.out.println("i127 " + analyzer.getIntensity(p127.getXYZ()));

    CIEXYZ rg128XYZ = CIEXYZ.plus(rXYZ.timesAndReturn(i128.R),
                                  gXYZ.timesAndReturn(i128.G));
    CIEXYZ rgb128XYZ = CIEXYZ.plus(rg128XYZ, bXYZ.timesAndReturn(i128.B));
    System.out.println(w128XYZ + "\n" + rgb128XYZ);

    Patch pr128 = lcdTarget.getPatch(128, 0, 0);
    Patch pg128 = lcdTarget.getPatch(0, 128, 0);
    Patch pb128 = lcdTarget.getPatch(0, 0, 128);
    CIEXYZ r128XYZ = pr128.getXYZ();
    CIEXYZ g128XYZ = pg128.getXYZ();
    CIEXYZ b128XYZ = pb128.getXYZ();
    System.out.println("r128 " + r128XYZ);
    System.out.println("g128 " + g128XYZ);
    System.out.println("b128 " + b128XYZ);
    System.out.println("i r128 " + analyzer.getIntensity(r128XYZ));

    double[][] wIntensity = getIntensity(analyzer, wPatchList);
//    System.out.println(DoubleArray.toString(wIntensity));
    plotIntensity("W", wIntensity);
//    plotIntensity("err", errorIntensity).addLegend();

    int size = 256;
    final double gamma = 2.2;
//    CIExyY whitexyY = new CIExyY(whiteXYZ);
//    whitexyY.Y = nativeWhiteXYZ.Y;
//    whiteXYZ = whitexyY.toXYZ();
    CIEXYZ[] targetArray = getTarget(gamma, whiteXYZ, blackXYZ, size);
//    targetArray = alterTargetArray(targetArray, gamma, nativeWhiteXYZ);
    double[][] dglut = getDGLUT(targetArray, wPatchList, null, null, null, rXYZ,
                                gXYZ, bXYZ);
    MultiMatrixModel model = new MultiMatrixModel(lcdTarget);
    model.produceFactor();
    List<Patch> wPatchList2 = getPatchList(dglut, model);
    double[][] wIntensity2 = getIntensity(analyzer, wPatchList2);
    plotIntensity("W2", wIntensity2);
//    double[] dg129 = dglut[129];
//    double[] dg128 = dglut[128];
//    double[] dg127 = dglut[127];
//    System.out.println(Arrays.toString(dg129));
//    System.out.println(Arrays.toString(dg128));
//    System.out.println(Arrays.toString(dg127));
//

//    CIEXYZ XYZ129 = model.getXYZ(new RGB(dg129[0], dg129[1], dg129[2]), false);
//    CIEXYZ XYZ128 = model.getXYZ(new RGB(dg128[0], dg128[1], dg128[2]), false);
//    CIEXYZ XYZ127 = model.getXYZ(new RGB(dg127[0], dg127[1], dg127[2]), false);
//    System.out.println("idg129 " + analyzer.getIntensity(XYZ129));
//    System.out.println("idg128 " + analyzer.getIntensity(XYZ128));
//    System.out.println("idg127 " + analyzer.getIntensity(XYZ127));

//    System.exit(0);
  }

  static List<Patch> getBPatchList(MultiMatrixModel model, int maxB, int size) {
    List<Patch> bPatchList = new ArrayList<Patch> ();
    for (int x = 0; x < size; x++) {
      double b = x / (size - 1.) * maxB;
      RGB rgb = new RGB(0, 0, b);
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      Patch patch = new Patch("", XYZ, null, rgb);
      bPatchList.add(patch);
    }
    return bPatchList;
  }

  public static void main(String[] args) {

    LCDTarget lcdTarget = LCDTargetBase.Instance.getFromAUORampXLS(
        "ramp/Measurement00.xls");

    if (false) {
      intensityErrorAnalyze(lcdTarget);
      return;
    }
//   boolean r= LCDTarget.Operator.checkIncreaseProgressively(lcdTarget);
//    LCDTarget.Operator.gradationReverseFix(lcdTarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdTarget);
    model.produceFactor();
//    double c1 = model.getXYZ(new RGB(255, 255, 255), false).getCCT();
//    double c2 = model.getXYZ(new RGB(255, 255, 249), false).getCCT();
//    double c3 = model.getXYZ(new RGB(255, 255, 241.58591706219934), false).
//        getCCT();
    //==========================================================================
    // setup
    //==========================================================================
    final boolean useBMax = false;
    final boolean useNewWhite = false; //跟useBMax並用才有效果
    final boolean keepMaxWhite = false; //白點是否維持255 255 255
//    final boolean doRemap = false; //開了useBMax或useNewWhite必開
    final int iterationTime = 0;
    int size = 256;
    //==========================================================================
    int maxBIntensity = findInverseBIntensityIndex(lcdTarget);

    int maxB = maxBIntensity;
    final int newWhite = maxBIntensity;
    maxB = newWhite < maxB ? newWhite : maxB;

    CIEXYZ nativeWhiteXYZ = lcdTarget.getWhitePatch().getXYZ();
    CIEXYZ whiteXYZ = lcdTarget.getWhitePatch().getXYZ();
    if (useBMax) {
      whiteXYZ = model.getXYZ(new RGB(255, 255, maxB), false);
      if (useNewWhite) {
        whiteXYZ = model.getXYZ(new RGB(newWhite, newWhite, maxB), false);
      }
    }
    System.out.println("white XYZ:" + whiteXYZ);

    CIEXYZ blackXYZ = lcdTarget.getBlackPatch().getXYZ();
    CIEXYZ rXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.R).getXYZ();
    CIEXYZ gXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.G).getXYZ();
    CIEXYZ bXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.B).getXYZ();
    if (useBMax) {
      bXYZ = lcdTarget.getPatch(RGB.Channel.B, maxB).getXYZ();
      if (useNewWhite) {
        rXYZ = lcdTarget.getPatch(RGB.Channel.R, newWhite).getXYZ();
        gXYZ = lcdTarget.getPatch(RGB.Channel.G, newWhite).getXYZ();
      }
    }
    MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();
    analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
    analyzer.setupComponent(RGB.Channel.R, rXYZ);
    analyzer.setupComponent(RGB.Channel.G, gXYZ);
    analyzer.setupComponent(RGB.Channel.B, bXYZ);
    analyzer.enter();

//    List<Patch> wPatchList = lcdTarget.filter.grayPatch(true);
    List<Patch> wPatchList = getWhitePatchList(255, 255, 255, model);
    if (useBMax) {
      wPatchList = getWhitePatchList(255, 255, maxB, model);
      if (useNewWhite) {
        wPatchList = getWhitePatchList(newWhite, newWhite, maxB, model);
      }
    }
    else {
//      wPatchList = getWhitePatchList(255, 255, 255, model);
    }

    List<Patch> rPatchList = lcdTarget.filter.grayScalePatch(RGB.Channel.R, true);
    List<Patch> gPatchList = lcdTarget.filter.grayScalePatch(RGB.Channel.G, true);
    List<Patch> bPatchList = lcdTarget.filter.grayScalePatch(RGB.Channel.B, true);
    if (useBMax && true) {
      bPatchList = getBPatchList(model, maxB, size);
    }
    double[][] rgbwErrorIntensity = getIntensityError(wPatchList, rPatchList,
        gPatchList, bPatchList, analyzer);
//    for (int y = 1; y < size - 1; y += 2) {
//      rgbwErrorIntensity[0][y] = (rgbwErrorIntensity[0][y - 1] +
//                                  rgbwErrorIntensity[0][y + 1]) / 2;
//      rgbwErrorIntensity[1][y] = (rgbwErrorIntensity[1][y - 1] +
//                                  rgbwErrorIntensity[1][y + 1]) / 2;
//      rgbwErrorIntensity[2][y] = (rgbwErrorIntensity[2][y - 1] +
//                                  rgbwErrorIntensity[2][y + 1]) / 2;
//    }
    plotIntensity("W vs RGB err", rgbwErrorIntensity).setFixedBounds(1,
        -0.06, 0.02);

    double[][] wIntensity = getIntensity(analyzer, wPatchList);
    plotIntensity("W", wIntensity);
//    plotIntensity("err", errorIntensity).addLegend();


    final double gamma = 2.2;
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    whitexyY.Y = nativeWhiteXYZ.Y;
    whiteXYZ = whitexyY.toXYZ();
    CIEXYZ[] targetArray = getTarget(gamma, whiteXYZ, blackXYZ, size);
//    targetArray = alterTargetArray(targetArray, gamma, nativeWhiteXYZ);
    double[][] dglut = getDGLUT(targetArray, wPatchList, null, null, null, rXYZ,
                                gXYZ, bXYZ);
//    double[][] dglut = getDGLUT(targetArray, wPatchList, rPatchList, gPatchList,
//                                bPatchList, rXYZ, gXYZ, bXYZ);

    //==========================================================================
    //iteration
    //==========================================================================
//    getPatchList(dglut,model);
//    targetArray = alterTargetArray(targetArray, 2.2, nativeWhiteXYZ);
    double[][] dglut2 = DoubleArray.copy(dglut);
    for (int x = 0; x < iterationTime; x++) {
      List<Patch> dgPatchList2 = getPatchList(dglut2, model);
      dglut2 = getDGLUT(targetArray, dgPatchList2, null, null, null, rXYZ, gXYZ,
                        bXYZ);
    }
    dglut = dglut2;
    //==========================================================================
    double[] dg255 = dglut[size - 1];
    if (keepMaxWhite) {
      dg255[2] = 255;
    }
//    remap(dglut, 255, 255, 241.58591706219934);


    int index = 0;
    for (double[] dg : dglut) {
      System.out.println( (index++) + " " + DoubleArray.toString(dg));
    }

//    System.out.println(DoubleArray.toString(dg255));

//    CIEXYZ dgWhiteXYZ = result[255];

    List<Patch> dgPatchList = getPatchList(dglut, model);
//    List<Patch> dgPatchList = new ArrayList<Patch> ();
//    for (int x = 0; x < size; x++) {
//      CIEXYZ XYZ = result[x];
//      Patch p = new Patch("", XYZ, null, null);
//      dgPatchList.add(p);
//    }
    double[][] dgIntensity = getIntensity(analyzer, dgPatchList);
    plotIntensity("DG Intensity", dgIntensity);

    CIEXYZ[] result = getXYZ(dglut, model);
    plotGamma(result);
    plotCCT(result);

//    Plot2D p = Plot2D.getInstance("Y");
//    for (int x = 1; x < size; x++) {
//      CIEXYZ XYZ0 = result[x];
//      CIEXYZ XYZ1 = targetArray[x];
//      p.addCacheScatterLinePlot("tar", x, XYZ1.Y);
//      p.addCacheScatterLinePlot("result", x, XYZ0.Y);
//      double gamma0 = Math.log( (XYZ0.Y - blackXYZ.Y) /
//                               (whiteXYZ.Y - blackXYZ.Y)) /
//          Math.log(x / 255.);
//      double gamma1 = Math.log( (XYZ1.Y - blackXYZ.Y) /
//                               (whiteXYZ.Y - blackXYZ.Y)) /
//          Math.log(x / 255.);
//    }
//    p.setVisible();
    //=========================================================================
    //r g b
    if (false) {
      return;
    }

//    for (Patch pp : rPatchList) {
//      CIEXYZ XYZ = pp.getXYZ();
//      XYZ = CIEXYZ.minus(XYZ, blackXYZ);
//      Patch.Operator.setXYZ(pp, XYZ);
//    }
//    plotXYZ("rXYZ", rPatchList);
//    double[][] rgbwErrorIntensity = getIntensityError(wPatchList, rPatchList,
//        gPatchList, bPatchList, analyzer);
//    plotIntensity("W vs RGB err", rgbwErrorIntensity).setFixedBounds(1, -0.06,
//        0.02);

    double[][] rIntensity = getIntensity(analyzer, rPatchList);
    double[][] gIntensity = getIntensity(analyzer, gPatchList);
    double[][] bIntensity = getIntensity(analyzer, bPatchList);
    plotIntensity("W", wIntensity);
    plotIntensity("R", rIntensity).setFixedBounds(1, -0.06, 0.02);
    plotIntensity("G", gIntensity).setFixedBounds(1, -0.06, 0.02);
    plotIntensity("B", bIntensity).setFixedBounds(1, -0.06, 0.02);

//    for (int x = 0; x < size; x++) {
////      rIntensity[0][x] = rIntensity[0][x] - wIntensity[0][x];
////      gIntensity[1][x] = gIntensity[1][x] - wIntensity[1][x];
////      bIntensity[2][x] = bIntensity[2][x] - wIntensity[2][x];
//    }



//    {
//      Plot2D plot = Plot2D.getInstance("W vs RGB");
//      for (int x = 0; x < size; x++) {
//        plot.addCacheScatterLinePlot("R", Color.red, x, rIntensity[0][x]);
//        plot.addCacheScatterLinePlot("G", Color.green, x, gIntensity[1][x]);
//        plot.addCacheScatterLinePlot("B", Color.blue, x, bIntensity[2][x]);
//        plot.addCacheScatterLinePlot("W-R", Color.black, x, wIntensity[0][x]);
//        plot.addCacheScatterLinePlot("W-G", Color.black, x, wIntensity[1][x]);
//        plot.addCacheScatterLinePlot("W-B", Color.black, x, wIntensity[2][x]);
//      }
//
//      plot.setVisible();
//      plot.setAxeLabel(0, "Gray Level");
//      plot.setAxeLabel(1, "Intensity");
//      plot.setFixedBounds(0, 0, 255);
//      plot.setFixedBounds(1, 0, 1);
//      plot.addLegend();
//    }

//    double[][] rgbIntensity = new double[3][size];
//    for (int x = 0; x < size; x++) {
//      rgbIntensity[0][x] = rIntensity[0][x] + gIntensity[0][x] +
//          bIntensity[0][x];
//      rgbIntensity[1][x] = rIntensity[1][x] + gIntensity[1][x] +
//          bIntensity[1][x];
//      rgbIntensity[2][x] = rIntensity[2][x] + gIntensity[2][x] +
//          bIntensity[2][x];
//    }
//    plotIntensity("RGB", rgbIntensity);

//    double[][] errorIntensity = new double[3][size];
//    for (int x = 0; x < size; x++) {
//      errorIntensity[0][x] = wIntensity[0][x] - rgbIntensity[0][x];
//      errorIntensity[1][x] = wIntensity[1][x] - rgbIntensity[1][x];
//      errorIntensity[2][x] = wIntensity[2][x] - rgbIntensity[2][x];
//    }
//    errorIntensity = DoubleArray.transpose(errorIntensity);
//    plotIntensity("r+g+b vs w err", errorIntensity);
//    double[][] pureIntensity = getIntensityInPureColor(dglut, rIntensity,
//        gIntensity, bIntensity);
//    plotIntensity("pure", pureIntensity);

//    double[][] rgbwErrorIntensity = new double[3][size];
//    for (int x = 0; x < size; x++) {
//      rgbwErrorIntensity[0][x] = wIntensity[0][x] - rIntensity[0][x];
//      rgbwErrorIntensity[1][x] = wIntensity[1][x] - gIntensity[1][x];
//      rgbwErrorIntensity[2][x] = wIntensity[2][x] - bIntensity[2][x];
//    }
//    plotIntensity("W vs RGB err", rgbwErrorIntensity).setFixedBounds(1, -0.06,
//        0.02);


    if (true) {
      DoubleArray.abs(rIntensity);
      DoubleArray.abs(gIntensity);
      DoubleArray.abs(bIntensity);
      System.out.println("r:" + DoubleArray.max(rIntensity[1]) + "(g) " +
                         DoubleArray.max(rIntensity[1]) + "(b)");
      System.out.println("g:" + DoubleArray.max(gIntensity[0]) + "(r) " +
                         DoubleArray.max(gIntensity[2]) + "(b)");
      System.out.println("b:" + DoubleArray.max(bIntensity[0]) + "(r) " +
                         DoubleArray.max(bIntensity[1]) + "(g)");
    }
  }

  static double[][] getIntensityError(List<Patch> wPatchList,
      List<Patch> rPatchList, List<Patch> gPatchList, List<Patch> bPatchList,
      MaxMatrixIntensityAnalyzer analyzer) {

    double[][] rIntensity = getIntensity(analyzer, rPatchList);
    double[][] gIntensity = getIntensity(analyzer, gPatchList);
    double[][] bIntensity = getIntensity(analyzer, bPatchList);
    double[][] wIntensity = getIntensity(analyzer, wPatchList);
    int size = rIntensity[0].length;
    double[][] error = new double[3][size];
    for (int x = 0; x < size; x++) {
      error[0][x] = rIntensity[0][x] - wIntensity[0][x];
      error[1][x] = gIntensity[1][x] - wIntensity[1][x];
      error[2][x] = bIntensity[2][x] - wIntensity[2][x];
    }
    return error;
  }

  static List<Patch> getPatchList(double[][] dglut, CIEXYZ[] result) {
    int size = dglut.length;
    List<Patch> dgPatchList = new ArrayList<Patch> ();
    for (int x = 0; x < size; x++) {
      double[] dg = dglut[x];
      RGB rgb = new RGB(dg[0], dg[1], dg[2]);
      CIEXYZ XYZ = result[x];
      Patch p = new Patch("", XYZ, null, rgb);
      dgPatchList.add(p);
    }
    return dgPatchList;

  }

  static Plot2D plotXYZ(String title, List<Patch> patchList) {
    Plot2D plot = Plot2D.getInstance(title);
    int size = patchList.size();
    double[][] XYZArray = new double[3][size];
    int index = 0;
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
      XYZArray[0][index] = XYZ.X;
      XYZArray[1][index] = XYZ.Y;
      XYZArray[2][index] = XYZ.Z;
//      plot.addCacheScatterLinePlot("X", index, XYZ.X);
//      plot.addCacheScatterLinePlot("Y", index, XYZ.Y);
//      plot.addCacheScatterLinePlot("Z", index, XYZ.Z);
      index++;
    }
    XYZArray[0] = Maths.firstOrderDerivatives(XYZArray[0]);
    XYZArray[1] = Maths.firstOrderDerivatives(XYZArray[1]);
    XYZArray[2] = Maths.firstOrderDerivatives(XYZArray[2]);
    plot.addLinePlot("X", 0, 254, XYZArray[0]);
    plot.addLinePlot("Y", 0, 254, XYZArray[1]);
    plot.addLinePlot("Z", 0, 254, XYZArray[2]);
    plot.setVisible();
    return plot;
  }

  static void remap(double[][] dglut, double maxR, double maxG, double maxB) {
    double rgain = maxR / 255.;
    double ggain = maxG / 255.;
    double bgain = maxB / 255.;
    for (double[] dg : dglut) {
      dg[0] *= rgain;
      dg[1] *= ggain;
      dg[2] *= bgain;
    }
  }

  static Plot2D plotGamma(CIEXYZ[] result) {
    int size = result.length;
    CIEXYZ blackXYZ = result[0];
    CIEXYZ whiteXYZ = result[size - 1];
    Plot2D plot = Plot2D.getInstance("Gamma");
    for (int x = 1; x < size - 1; x++) {
      CIEXYZ XYZ = result[x];
      double normal = (XYZ.Y - blackXYZ.Y) / (whiteXYZ.Y - blackXYZ.Y);
      double n = x / (size - 1.);
      double gamma = Math.log(normal) / Math.log(n);
//      System.out.println(gamma);
      plot.addCacheScatterLinePlot("", x, gamma);
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 255);
    plot.setAxeLabel(0, "Gray Level");
    plot.setAxeLabel(1, "Gamma");
    PlotUtils.setAUOFormat(plot);
    plot.setFixedBounds(1, 2, 4);
    return plot;
  }

  static Plot2D plotCCT(CIEXYZ[] result) {
    int size = result.length;
//    CIEXYZ blackXYZ = result[0];
//    CIEXYZ whiteXYZ = result[size - 1];
    Plot2D plot = Plot2D.getInstance("CCT");
    for (int x = 1; x < size - 1; x++) {
      CIEXYZ XYZ = result[x];
      double cct = XYZ.getCCT();
      plot.addCacheScatterLinePlot("", x, cct);
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 10000, 13000);
    return plot;
  }

  static CIEXYZ[] getXYZ(double[][] dglut, MultiMatrixModel model) {
    int size = dglut.length;
    CIEXYZ[] XYZArray = new CIEXYZ[size];
    for (int x = 0; x < size; x++) {
      double[] dg = dglut[x];
      CIEXYZ XYZ = model.getXYZ(new RGB(dg[0], dg[1], dg[2]), false);
      XYZArray[x] = XYZ;
    }
    return XYZArray;
  }

  static double[] getIntensity(double[][] intensityArray, double dg) {
    int size = intensityArray[0].length;
    double[] keys = new double[size];
    for (int x = 0; x < size; x++) {
      keys[x] = x;
    }

    Interpolation1DLUT r = new Interpolation1DLUT(keys, intensityArray[0]);
    Interpolation1DLUT g = new Interpolation1DLUT(keys, intensityArray[1]);
    Interpolation1DLUT b = new Interpolation1DLUT(keys, intensityArray[2]);
    double[] intensity = new double[3];

    intensity[0] = r.getValue(dg);
    intensity[1] = g.getValue(dg);
    intensity[2] = b.getValue(dg);
    return intensity;
  }

  static double[][] getIntensityInPureColor(double[][] dglut,
                                            double[][] rIntensity,
                                            double[][] gIntensity,
                                            double[][] bIntensity) {
    int size = dglut.length;
    double[][] intensityArray = new double[3][size];
    for (int x = 0; x < size; x++) {
      double[] dg = dglut[x];
      double[] fromR = getIntensity(rIntensity, dg[0]);
      double[] fromG = getIntensity(gIntensity, dg[1]);
      double[] fromB = getIntensity(bIntensity, dg[2]);
      double[] fromRG = DoubleArray.plus(fromR, fromG);
      double[] intensity = DoubleArray.plus(fromRG, fromB);
//      intensityArray[x] = intensity;
      intensityArray[0][x] = intensity[0];
      intensityArray[1][x] = intensity[1];
      intensityArray[2][x] = intensity[2];
    }

    return intensityArray;
  }

  static double[][] getDGLUT(CIEXYZ[] targetArray, List<Patch> wPatchList,
      List<Patch> rPatchList, List<Patch> gPatchList, List<Patch> bPatchList,
      CIEXYZ rXYZ, CIEXYZ gXYZ, CIEXYZ bXYZ) {
    int size = targetArray.length;
    double[][] rgbKeys = new double[3][size];
    for (int x = 0; x < wPatchList.size(); x++) {
      Patch p = wPatchList.get(x);
      RGB rgb = p.getRGB();
      rgbKeys[0][x] = rgb.R;
      rgbKeys[1][x] = rgb.G;
      rgbKeys[2][x] = rgb.B;
    }

    double[][] dglut = new double[size][];
    for (int x = 0; x < size; x++) {
      CIEXYZ target = targetArray[x];
      MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();
      analyzer.setupComponent(RGB.Channel.W, target);
      analyzer.setupComponent(RGB.Channel.R, rXYZ);
      analyzer.setupComponent(RGB.Channel.G, gXYZ);
      analyzer.setupComponent(RGB.Channel.B, bXYZ);
      analyzer.enter();

      double[][] rgbwErrorIntensity = null;
      if (null != rPatchList) {
        rgbwErrorIntensity = getIntensityError(wPatchList, rPatchList,
                                               gPatchList, bPatchList,
                                               analyzer);
//        for (int y = 1; y < size - 1; y += 2) {
//          rgbwErrorIntensity[0][y] = (rgbwErrorIntensity[0][y - 1] +
//                                      rgbwErrorIntensity[0][y + 1]) / 2;
//          rgbwErrorIntensity[1][y] = (rgbwErrorIntensity[1][y - 1] +
//                                      rgbwErrorIntensity[1][y + 1]) / 2;
//          rgbwErrorIntensity[2][y] = (rgbwErrorIntensity[2][y - 1] +
//                                      rgbwErrorIntensity[2][y + 1]) / 2;
//        }
//        plotIntensity("W vs RGB err", rgbwErrorIntensity).setFixedBounds(1,
//            -0.06, 0.02);

      }

      double[][] intensity = getIntensity(analyzer, wPatchList);
      if (null != rgbwErrorIntensity) {
        for (int y = 0; y < size; y++) {
          intensity[0][x] += rgbwErrorIntensity[0][x];
          intensity[1][x] += rgbwErrorIntensity[1][x];
          intensity[2][x] += rgbwErrorIntensity[2][x];
        }
      }

      double[] dg = findDG(intensity, rgbKeys);
      dglut[x] = dg;
//  System.out.println(DoubleArray.toString(dg));
    }
    return dglut;
  }
}
