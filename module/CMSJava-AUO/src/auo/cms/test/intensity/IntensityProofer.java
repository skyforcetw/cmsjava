package auo.cms.test.intensity;

import java.util.List;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
//import shu.cms.lcd.test.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.cms.devicemodel.lcd.spectra.*;

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
public class IntensityProofer {

  public final static SimpleLCDModel getLCDModelInstance() {
    RGB.ColorSpace cs = RGB.ColorSpace.sRGB;
    CIEXYZ blackXYZ = new RGB(cs, new int[] {2, 2, 4}).toXYZ();
    CIEXYZ rXYZ = new RGB(cs, new int[] {255, 0, 0}).toXYZ();
    CIEXYZ gXYZ = new RGB(cs, new int[] {0, 255, 0}).toXYZ();
    CIEXYZ bXYZ = new RGB(cs, new int[] {0, 0, 255}).toXYZ();
//CIExyY blackxyY = new CIExyY(blackXYZ);

//CIEXYZ blackXYZ2 = (CIEXYZ) blackXYZ.clone();
//blackXYZ2.times(2);

//原始的色度座標
    CIExyY rxyY = new CIExyY(rXYZ);
    CIExyY gxyY = new CIExyY(gXYZ);
    CIExyY bxyY = new CIExyY(bXYZ);

//偏移後的色度座標
    CIExyY rxyY2 = (CIExyY) rxyY.clone();
    rxyY2.x += 0.01;
    CIExyY gxyY2 = (CIExyY) gxyY.clone();
    gxyY2.x += 0.01;
    gxyY2.y -= 0.005;
    CIExyY bxyY2 = (CIExyY) bxyY.clone();
    bxyY2.x += 0.004;
    bxyY2.y += 0.015;
    SimpleLCDModel model = new SimpleLCDModel(rxyY, rxyY2, gxyY, gxyY2, bxyY,
                                              bxyY2,
                                              blackXYZ);

    return model;
  }

//  private static MultiMatrixModel mmodel;

  static CIEXYZ[] getTargetXYZArray(CIEXYZ panelWhiteXYZ, CIEXYZ panelBlackXYZ,
                                    double[][] recommendRatio) {
    //==========================================================================
    // target
    //==========================================================================
    CIEXYZ[] targetXYZArray = new CIEXYZ[256];
    targetXYZArray[0] = panelBlackXYZ;
    CIEXYZ blackBaseXYZ = (CIEXYZ) panelWhiteXYZ.clone();
    blackBaseXYZ.normalizeY();
    blackBaseXYZ.times(panelBlackXYZ.Y);
    CIExyY panelBlackxyY = new CIExyY(panelBlackXYZ);
    CIExyY panelWhitexyY = new CIExyY(panelWhiteXYZ);

    for (int x = 50; x < 256; x++) {
      double normal = x / 255.;
      double power = Math.pow(normal, 2.2);
      CIEXYZ baseXYZ = CIEXYZ.minus(panelWhiteXYZ, blackBaseXYZ);
      baseXYZ.times(power);
      CIEXYZ targetXYZ = CIEXYZ.plus(baseXYZ, blackBaseXYZ);
      targetXYZArray[x] = targetXYZ;
    }
    for (int x = 0; x < 50; x++) {
      double[] recommenddxdy = recommendRatio[x];
      double normal = x / 255.;
      double power = Math.pow(normal, 2.2);
      CIEXYZ baseXYZ = CIEXYZ.minus(panelWhiteXYZ, blackBaseXYZ);
      baseXYZ.times(power);
      CIEXYZ targetXYZ = CIEXYZ.plus(baseXYZ, blackBaseXYZ);
      CIExyY targetxyY = new CIExyY(targetXYZ);
      targetxyY.x = Interpolation.linear(0, 1, panelBlackxyY.x,
                                         panelWhitexyY.x, recommenddxdy[0]);
      targetxyY.y = Interpolation.linear(0, 1, panelBlackxyY.y,
                                         panelWhitexyY.y, recommenddxdy[1]);
      targetXYZArray[x] = targetxyY.toXYZ();
    }
    //==========================================================================
    return targetXYZArray;
  }

  static CIEXYZ[] getTargetXYZArray(CIEXYZ panelWhiteXYZ, CIEXYZ panelBlackXYZ,
                                    double gamma, double xgamma, double ygamma) {
    //==========================================================================
    // target
    //==========================================================================
    CIEXYZ[] targetXYZArray = new CIEXYZ[256];
    targetXYZArray[0] = panelBlackXYZ;
    CIEXYZ blackBaseXYZ = (CIEXYZ) panelWhiteXYZ.clone();
    blackBaseXYZ.normalizeY();
    blackBaseXYZ.times(panelBlackXYZ.Y);
    CIExyY panelBlackxyY = new CIExyY(panelBlackXYZ);
    CIExyY panelWhitexyY = new CIExyY(panelWhiteXYZ);

    for (int x = 50; x < 256; x++) {
      double normal = x / 255.;
      double power = Math.pow(normal, gamma);
      CIEXYZ baseXYZ = CIEXYZ.minus(panelWhiteXYZ, blackBaseXYZ);
      baseXYZ.times(power);
      CIEXYZ targetXYZ = CIEXYZ.plus(baseXYZ, blackBaseXYZ);
      targetXYZArray[x] = targetXYZ;
    }
    for (int x = 0; x < 50; x++) {
      double normal = x / 255.;
      double power = Math.pow(normal, gamma);
      CIEXYZ baseXYZ = CIEXYZ.minus(panelWhiteXYZ, blackBaseXYZ);
      baseXYZ.times(power);
      CIEXYZ targetXYZ = CIEXYZ.plus(baseXYZ, blackBaseXYZ);
      CIExyY targetxyY = new CIExyY(targetXYZ);
      double normalx = x / 50.;
      double realx = Math.pow(normalx, xgamma) * 50;
      double realy = Math.pow(normalx, ygamma) * 50;
      targetxyY.x = Interpolation.linear(0, 50, panelBlackxyY.x,
                                         panelWhitexyY.x, realx);
      targetxyY.y = Interpolation.linear(0, 50, panelBlackxyY.y,
                                         panelWhitexyY.y, realy);
      targetXYZArray[x] = targetxyY.toXYZ();
    }
    //==========================================================================
    return targetXYZArray;

  }

  static CIEXYZ[] getRampXYZArray(SimpleLCDModelIF model, boolean smooth,
                                  int smoothTimes) {
    int size = 256;
    CIEXYZ[] rampXYZArray = new CIEXYZ[size];
    //==========================================================================
    // 生成panel ramp
    //==========================================================================
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = model.getXYZ(new double[] {x, x, x});
      rampXYZArray[x] = XYZ;
    }

    //smooth
    if (smooth) {
      boolean smoothAtXYZ = false;
      boolean smoothByCubic = false;
      boolean smoothAtDelta = true;

      if (smoothAtDelta) {
        double[] xdata = new double[size];
        double[] ydata = new double[size];
        double[] Ydata = new double[size];
        double[] deltax = new double[size];
        double[] deltay = new double[size];
        double[] deltaY = new double[size];
        CIExyY xyY = new CIExyY(rampXYZArray[0]);
        xdata[0] = xyY.x;
        ydata[0] = xyY.y;
        Ydata[0] = xyY.Y;

        for (int x = 1; x < size; x++) {
          CIExyY xyY0 = new CIExyY(rampXYZArray[x - 1]);
          CIExyY xyY1 = new CIExyY(rampXYZArray[x]);
          double[] dxy = xyY1.getDeltaxy(xyY0);

          deltax[x] = dxy[0];
          deltay[x] = dxy[1];
          deltaY[x] = xyY1.Y - xyY0.Y;
          xdata[x] = xyY1.x;
          ydata[x] = xyY1.y;
          Ydata[x] = xyY1.Y;
        }
        ChromaticityModel.smoothByCubic(deltax, smoothTimes);
        ChromaticityModel.smoothByCubic(deltay, smoothTimes);
        ChromaticityModel.smoothByCubic(deltaY, smoothTimes);
        double[] smoothx = ChromaticityModel.getSmoothCurve(xdata, deltax);
        double[] smoothy = ChromaticityModel.getSmoothCurve(ydata, deltay);
        double[] smoothY = ChromaticityModel.getSmoothCurve(Ydata, deltaY);

        for (int x = 0; x < size; x++) {
          CIEXYZ XYZ = rampXYZArray[x];
          xyY = new CIExyY(XYZ);
          xyY.x = smoothx[x];
          xyY.y = smoothy[x];
          xyY.Y = smoothY[x];
          rampXYZArray[x] = xyY.toXYZ();
        }
      }
      else {
        for (int s = 0; s < smoothTimes; s++) {
          for (int x = 1; x < 255; x++) {
            CIEXYZ XYZ0 = rampXYZArray[x - 1];
            CIEXYZ XYZ1 = rampXYZArray[x];
            CIEXYZ XYZ2 = rampXYZArray[x + 1];
            CIEXYZ XYZ = null;
            if (smoothAtXYZ) {
              XYZ = new CIEXYZ();
              XYZ.X = Interpolation.linear(0, 1, XYZ0.X, XYZ2.X, 0.5);
              XYZ.Y = Interpolation.linear(0, 1, XYZ0.Y, XYZ2.Y, 0.5);
              XYZ.Z = Interpolation.linear(0, 1, XYZ0.Z, XYZ2.Z, 0.5);
            }
            else {
              CIExyY xyY0 = new CIExyY(XYZ0);
              CIExyY xyY1 = new CIExyY(XYZ1);
              CIExyY xyY2 = new CIExyY(XYZ2);

              if (smoothByCubic && x < 254) {
                CIEXYZ XYZ3 = rampXYZArray[x + 2];
                CIExyY xyY3 = new CIExyY(XYZ3);
                double[] xn = new double[] {
                    xyY0.x, xyY2.x, xyY3.x};
                double[] yn = new double[] {
                    xyY0.y, xyY2.y, xyY3.y};
                double[] gn = new double[] {
                    0, 2, 3};
                xyY1.x = Interpolation.cubic(gn, xn, 1);
                xyY1.y = Interpolation.cubic(gn, yn, 1);
              }
              else {
                xyY1.x = Interpolation.linear(0, 1, xyY0.x, xyY2.x, 0.5);
                xyY1.y = Interpolation.linear(0, 1, xyY0.y, xyY2.y, 0.5);
                //有Y的smooth效果會更慘!
//            xyY1.Y = Interpolation.linear(0, 1, xyY0.Y, xyY2.Y, 0.5);
              }
              XYZ = xyY1.toXYZ();
            }

            rampXYZArray[x] = XYZ;
          }

        }
      }
    }
    //==========================================================================
    return rampXYZArray;
  }

  public static void main(String[] args) {
    //=========================================================================
    // settting
    //=========================================================================
//    RGB.MaxValue bitDepth = RGB.MaxValue.Int11Bit;
//    RGB.MaxValue bitDepth = RGB.MaxValue.Int12Bit;
//    RGB.MaxValue bitDepth = RGB.MaxValue.Int31Bit;
    RGB.MaxValue bitDepth = RGB.MaxValue.Double255;
//    boolean sourceDataSmooth = true;
//    int sourceDataSmoothTimes = 1;
    boolean grayRampSmooth = false;

    //analyze
    boolean originalAnalyze = true;

    boolean doAlterTarget = false;
    boolean doFeedback = false;
    double gamma = 2.4;
    //=========================================================================
//    SimpleLCDModelIF model = SimpleLCDModel.getSimpleLCDModel();
//    SimpleLCDModelIF model = SimpleLCDModel.getSimpleLCDModel2(sourceDataSmooth,
//                                                sourceDataSmoothTimes);
//    SimpleLCDModelIF model = SimpleLCDModel.getSimpleLCDModel3(); //LC Characterization
    SimpleLCDModelIF model = SimpleLCDModel.getSimpleLCDModel4();
    CIEXYZ panelBlackXYZ = model.getBlackXYZ();
    CIExyY panelBlackxyY = new CIExyY(panelBlackXYZ);

    Plot2D cplot = Plot2D.getInstance("chromaticity");
    Plot2D.setSkin(PlotUtils.AUOSkin);

    double[][] invm = model.getXYZ2RGBMatrix();

    //==========================================================================
    // 生成panel ramp
    //==========================================================================

    CIEXYZ[] panelRampXYZArray4CCT = getRampXYZArray(model, grayRampSmooth, 1);
    CIEXYZ[] panelRampXYZArray = getRampXYZArray(model, false, 1);
//    panelRampXYZArray4CCT = panelRampXYZArray;
    CIEXYZ panelWhiteXYZ = panelRampXYZArray[255];
    CIExyY panelWhitexyY = new CIExyY(panelWhiteXYZ);
    //plot
    for (int x = 0; x < 256; x++) {
      CIEXYZ rXYZ = model.getXYZ(new double[] {x, 0, 0});
      CIEXYZ gXYZ = model.getXYZ(new double[] {0, x, 0});
      CIEXYZ bXYZ = model.getXYZ(new double[] {0, 0, x});
      CIExyY rr = new CIExyY(rXYZ);
      CIExyY gg = new CIExyY(gXYZ);
      CIExyY bb = new CIExyY(bXYZ);

      cplot.addCacheScatterLinePlot("orgR", Color.red, rr.x, rr.y);
      cplot.addCacheScatterLinePlot("orgG", Color.green, gg.x, gg.y);
      cplot.addCacheScatterLinePlot("orgB", Color.blue, bb.x, bb.y);

      if (x > 0) {
        CIExyY rxyY0 = new CIExyY(CIEXYZ.minus(rXYZ, panelBlackXYZ));
        CIExyY gxyY0 = new CIExyY(CIEXYZ.minus(gXYZ, panelBlackXYZ));
        CIExyY bxyY0 = new CIExyY(CIEXYZ.minus(bXYZ, panelBlackXYZ));

        cplot.addCacheScatterLinePlot("orgR0", Color.black, rxyY0.x, rxyY0.y);
        cplot.addCacheScatterLinePlot("orgG0", Color.black, gxyY0.x, gxyY0.y);
        cplot.addCacheScatterLinePlot("orgB0", Color.black, bxyY0.x, bxyY0.y);
      }
    }
    //==========================================================================


    //==========================================================================
    // target
    //==========================================================================
    CIEXYZ[] targetXYZArray = getTargetXYZArray(panelWhiteXYZ, panelBlackXYZ,
                                                gamma, 1, 1);
    //==========================================================================

    double[][][] intensityArray4CCT = getIntensityArray(invm,
        panelRampXYZArray4CCT, targetXYZArray);
//    double[][][] intensityArray = getIntensityArray(invm,
//        panelRampXYZArray, targetXYZArray);

    //=========================================================================
    //算dg lut
    //=========================================================================
    RGB[] dglut = getDGLut(intensityArray4CCT, bitDepth);
    analyzeDGByPlot(dglut, model, invm);
    //=========================================================================

    //=========================================================================
    // 分析dg完的誤差
    //=========================================================================
    CIExyY[] dgWhitexyYArray = getDGWhitexyYArray(model, dglut);
    if (originalAnalyze) {
      analyzeByPlot(dgWhitexyYArray, panelRampXYZArray, targetXYZArray);
    }

    for (int x = 0; x < 256; x++) {
      CIExyY dgxyY = dgWhitexyYArray[x];
      CIEXYZ orgXYZ = panelRampXYZArray[x];
      CIExyY orgxyY = new CIExyY(orgXYZ);
      cplot.addCacheScatterPlot("dgw", Color.blue, dgxyY.x, dgxyY.y);
      cplot.addCacheScatterPlot("orgw", Color.black, orgxyY.x, orgxyY.y);
    }
    //=========================================================================

    //=========================================================================
    // plot cct
    //=========================================================================
//    for (int cct = 10000; cct <= 25000; cct += 500) {
//      CIExyY xyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(cct);
//      cplot.addCacheScatterPlot("D", Color.red, xyY.x, xyY.y);
//      CIExyY bxyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(cct);
//      cplot.addCacheScatterPlot("B", Color.red, bxyY.x, bxyY.y);
//    }
    //=========================================================================

    cplot.addScatterPlot("W", Color.black, panelWhitexyY.x, panelWhitexyY.y);
    cplot.addScatterPlot("K", Color.black, panelBlackxyY.x, panelBlackxyY.y);
    cplot.addLegend();
    cplot.setVisible();
    cplot.setFixedBounds(0, 0.253, 0.286);
    cplot.setFixedBounds(1, 0.254, 0.293);
    cplot.setAxisLabels("CIEx", "CIEy");

    //=========================================================================
    // acc
    //=========================================================================
    LCDModelChromaticityAdjustEstimator adjustEstimator = new
        LCDModelChromaticityAdjustEstimator(model, dglut, bitDepth);
    double[][] dxOfRdyOfGArray = getdxOfRdyOfGArray(adjustEstimator);
    int size = dxOfRdyOfGArray.length;
    double[][] accdxOfRdyOfGArray = DoubleArray.copy(dxOfRdyOfGArray);
    for (int x = 1; x < size; x++) {
      accdxOfRdyOfGArray[x][0] += accdxOfRdyOfGArray[x - 1][0];
      accdxOfRdyOfGArray[x][1] += accdxOfRdyOfGArray[x - 1][1];
    }

    Plot2D dxRdyGplot = Plot2D.getInstance("dxOfR&dyofG");

    for (int x = 0; x < size; x++) {
      dxRdyGplot.addCacheScatterLinePlot("dxOfR", x, dxOfRdyOfGArray[x][0]);
      dxRdyGplot.addCacheScatterLinePlot("dyOfG", x, dxOfRdyOfGArray[x][1]);
    }
    dxRdyGplot.addLegend();
    dxRdyGplot.setVisible();
    dxRdyGplot.setFixedBounds(1, 0, 0.0018);

    double[] dxdyOfWK = panelWhitexyY.getDeltaxy(panelBlackxyY);
    double[] dxdyPiece = DoubleArray.divide(dxdyOfWK, 50);
    dxRdyGplot.addLinePlot("ave dx", 0, dxdyPiece[0], size - 1, dxdyPiece[0]);
    dxRdyGplot.addLinePlot("ave dy", 0, dxdyPiece[1], size - 1, dxdyPiece[1]);

    Plot2D accdxRdyGPlot = Plot2D.getInstance("acc dxOfR&dyOfG");
    double[][] accdxOfRdyOfGArrayT = DoubleArray.transpose(accdxOfRdyOfGArray);
    accdxRdyGPlot.addLinePlot("dx", 0, 50, accdxOfRdyOfGArrayT[0]);
    accdxRdyGPlot.addLinePlot("dy", 0, 50, accdxOfRdyOfGArrayT[1]);
    accdxRdyGPlot.addLegend();
    accdxRdyGPlot.setVisible();
    //=========================================================================


    //=========================================================================
    // alter
    //=========================================================================

    if (doAlterTarget) {
//      double[][] recommendRatio = getRecommendRatioOfdxdy(accdxOfRdyOfGArrayT);
//      CIEXYZ[] recommendTargetXYZArray = getTargetXYZArray(panelWhiteXYZ,
//          panelBlackXYZ, recommendRatio);
//
//      double[][][] intensityArray2 = getIntensityArray(invm, panelRampXYZArray,
//          recommendTargetXYZArray);
//
//      //=========================================================================
//      //算dg lut
//      //=========================================================================
//      RGB[] dglut2 = getDGLut(intensityArray2, bitDepth);
//      CIExyY[] dgWhitexyYArray2 = getDGWhitexyYArray(model, dglut2);
//      analyzeByPlot(dgWhitexyYArray2, panelRampXYZArray,
//                    recommendTargetXYZArray);
    }
    //=========================================================================



    if (doFeedback) {
      //=========================================================================
      //算dg lut
      //=========================================================================
      RGB[] dglut2 = feedback(dglut, model, bitDepth, 0.00015);
//      RGB[] dglut2 = getDGLut(intensityArray2, bitDepth);
      CIExyY[] dgWhitexyYArray2 = getDGWhitexyYArray(model, dglut2);
      analyzeByPlot(dgWhitexyYArray2, panelRampXYZArray,
                    targetXYZArray);
    }
  }

  static RGB[] feedback(RGB[] dglut, SimpleLCDModelIF model,
                        RGB.MaxValue bitDepth, double threshold) {

    RGB[] clone = RGBArray.deepClone(dglut);
    double[][] dxdy = getDeltaxy(clone, model);
    int start = 1, end = 50;
    int feedbackTimes = 0;
    Plot2D plot = Plot2D.getInstance("defect of feedback");
    plot.setVisible();
    Plot2D cplot = Plot2D.getInstance("chromaticity feedback");
    cplot.setVisible();

    while (getReverseIndex(dxdy[0], start, end, threshold) != -1 ||
           getReverseIndex(dxdy[1], start, end, threshold) != -1) {
      for (RGB.Channel ch : new RGB.Channel[] {RGB.Channel.R, RGB.Channel.G}) {
        int index = getReverseIndex(RGB.Channel.R == ch ? dxdy[0] : dxdy[1],
                                    start, end, threshold);
        if ( -1 == index) {
          continue;
        }
        double[] deltaOfBase = RGB.Channel.R == ch ? dxdy[0] : dxdy[1];

        int y = index;
//        double delta = (deltaOfBase)[y];
        double deltaU1 = (deltaOfBase)[y + 1];
        double deltaD1 = (deltaOfBase)[y - 1];

        RGB rgb;
        double value = -1;
        if (deltaU1 > deltaD1) {
          //若上方的delta比較大, 代表比較大的調整空間, 所以往上調
          rgb = (clone)[y];
          value = rgb.getValue(ch, bitDepth) + 1;
          System.out.println(y + " " + ch + " +1");
        }
        else {
          //反之, 就是往下調
          rgb = (clone)[y - 1];
          value = rgb.getValue(ch, bitDepth) - 1;
          System.out.println( (y - 1) + " " + ch + " -1");
        }
        rgb.setValue(ch, value, bitDepth);
        feedbackTimes++;
      }
      dxdy = getDeltaxy(clone, model);
      plot.removeAllPlots();
      plot.addLinePlot("dx", 0, 50, dxdy[0]);
      plot.addLinePlot("dy", 0, 50, dxdy[1]);

      double[][] CIExy = getCIExy(clone, model);
      cplot.removeAllPlots();
      cplot.addScatterPlot("", CIExy);
      try {
        Thread.sleep(500);
      }
      catch (InterruptedException ex) {
      }
    }
    return clone;
  }

  static double[][] getCIExy(RGB[] dglut, SimpleLCDModelIF model) {
    double[][] CIExy = new double[51][];
//    double[] xValues = null, yValues = null;
//    xValues = new double[51];
//    yValues = new double[51];
//    xValues[0] = yValues[0] = 0;
    for (int x = 0; x <= 50; x++) {
      CIEXYZ XYZ1 = model.getXYZ(dglut[x]);
//      CIEXYZ XYZ0 = model.getXYZ(dglut[x - 1]);
      CIExyY xyY1 = new CIExyY(XYZ1);
//      CIExyY xyY0 = new CIExyY(XYZ0);
//      double[] dxy = xyY1.getDeltaxy(xyY0);
//      xValues[x] = xyY1.x;
//      yValues[x] = xyY1.y;
      CIExy[x] = xyY1.getxyValues();
    }
//    double[][] result = new double[][] {
//        xValues, yValues};
    return CIExy;
  }

  static double[][] getDeltaxy(RGB[] dglut, SimpleLCDModelIF model) {
    double[] dx = null, dy = null;
    dx = new double[51];
    dy = new double[51];
    dx[0] = dy[0] = 0;
    for (int x = 1; x <= 50; x++) {
      CIEXYZ XYZ1 = model.getXYZ(dglut[x]);
      CIEXYZ XYZ0 = model.getXYZ(dglut[x - 1]);
      CIExyY xyY1 = new CIExyY(XYZ1);
      CIExyY xyY0 = new CIExyY(XYZ0);
      double[] dxy = xyY1.getDeltaxy(xyY0);
      dx[x] = dxy[0];
      dy[x] = dxy[1];
    }
    double[][] result = new double[][] {
        dx, dy};
    return result;
  }

  static int getReverseIndex(double[] deltaArray, int start, int end,
                             double threshold) {
    for (int x = start; x < end; x++) {
      if (deltaArray[x] < threshold) {
        return x;
      }
    }
    return -1;
  }

  static void analyzeDGByPlot(RGB[] dglut, SimpleLCDModelIF model,
                              double[][] invm) {
    Plot2D dgplot = Plot2D.getInstance("dg lut");
//    Plot2D iplot = Plot2D.getInstance("intensity");
    Plot2D diplot = Plot2D.getInstance("delta intensity");
//=========================================================================
//算dg lut
//=========================================================================

    for (int x = 1; x < 256; x++) {
      RGB rgb = dglut[x];

      dgplot.addCacheScatterLinePlot("r", Color.red, x, rgb.R);
      dgplot.addCacheScatterLinePlot("g", Color.green, x, rgb.G);
      dgplot.addCacheScatterLinePlot("b", Color.blue, x, rgb.B);

      CIEXYZ XYZ = model.getXYZ(dglut[x]);
      double[] intensity = getIntensity(invm, XYZ, new double[] {
                                        1, 1, 1});
//      iplot.addCacheScatterLinePlot("r", x, intensity[0]);
//      iplot.addCacheScatterLinePlot("g", x, intensity[1]);
//      iplot.addCacheScatterLinePlot("b", x, intensity[2]);

      diplot.addCacheScatterLinePlot("dr", x,
                                     (intensity[0] - intensity[1]) * 100);
      diplot.addCacheScatterLinePlot("db", x,
                                     (intensity[2] - intensity[1]) * 100);

    }
//=========================================================================
    dgplot.setVisible();
//    iplot.setVisible();
    diplot.setVisible();

  }

  static void analyzeByPlot(CIExyY[] dgWhitexyYArray,
                            CIEXYZ[] panelRampXYZArray, CIEXYZ[] targetXYZArray) {

    Plot2D cctplot = Plot2D.getInstance("CCT of Panel White");

    Plot2D xyplot = Plot2D.getInstance("xy of Panel White");
    Plot2D dxyplot = Plot2D.getInstance("dxy of Target");
    Plot2D defectplot = Plot2D.getInstance("defect of Target");
    //=========================================================================
    // 分析dg完的誤差
    //=========================================================================
    for (int x = 0; x < 256; x++) {

      CIExyY dgxyY = dgWhitexyYArray[x];
      CIEXYZ dgXYZ = dgxyY.toXYZ();
      if (x >= 1 && x <= 50) {
        double[] dxy = dgWhitexyYArray[x].getDeltaxy(dgWhitexyYArray[x - 1]);
        defectplot.addCacheScatterLinePlot("dx", Color.red, x, dxy[0]);
        defectplot.addCacheScatterLinePlot("dy", Color.green, x, dxy[1]);
      }

      CIEXYZ orgXYZ = panelRampXYZArray[x];
      CIExyY orgxyY = new CIExyY(orgXYZ);
      CIEXYZ targetXYZ = targetXYZArray[x];
      CIExyY targetxyY = new CIExyY(targetXYZ);

      double cctOfPanelWhite = orgxyY.getCCT();
      double cctOfDG = dgXYZ.getCCT();
      cctplot.addCacheScatterLinePlot("Native", x, cctOfPanelWhite);
      cctplot.addCacheScatterLinePlot("DG", x, cctOfDG);

      xyplot.addCacheScatterLinePlot("target x", Color.black, x, targetxyY.x);
      xyplot.addCacheScatterLinePlot("target y", Color.black, x, targetxyY.y);
      xyplot.addCacheScatterLinePlot("actual x", Color.red, x, dgxyY.x);
      xyplot.addCacheScatterLinePlot("actual y", Color.green, x, dgxyY.y);

      dxyplot.addCacheScatterLinePlot("dx", Color.red, x, dgxyY.x - targetxyY.x);
      dxyplot.addCacheScatterLinePlot("dy", Color.green, x,
                                      dgxyY.y - targetxyY.y);
    }
    //=========================================================================


    cctplot.setVisible();
    cctplot.setAxisLabels("Gray Level", "CCT");
    xyplot.setVisible();
    xyplot.setAxisLabels("Gray Level", "CIE Chromaticity");
    xyplot.setFixedBounds(0, 0, 50);
    dxyplot.setVisible();
    dxyplot.setAxisLabels("Gray Level", "Delta Chromaticity Coordinator");
    dxyplot.setFixedBounds(0, 0, 50);
    dxyplot.setFixedBounds(1, -0.001, 0.001);

    defectplot.setVisible();
    defectplot.setFixedBounds(1, 0, 0.002);

    defectplot.addLegend();
    defectplot.setAxisLabels("Gray Level", "delta");

    Plot2D cctvplot = Plot2D.getInstance("CCT Vector of Panel White");
    double[][] cctVector = new double[255][];
    for (int x = 0; x < 255; x++) {
      CIEXYZ XYZ0 = panelRampXYZArray[x];
      CIEXYZ XYZ1 = panelRampXYZArray[x + 1];
      double[] xyValues0 = XYZ0.getxyValues();
      double[] xyValues1 = XYZ1.getxyValues();
      cctvplot.addCacheScatterPlot("cct", xyValues0[0], xyValues0[1]);
      cctVector[x] = DoubleArray.minus(xyValues1, xyValues0);
    }
    cctvplot.setVisible();
    int index = cctvplot.getCachePlotIndex("cct");
    cctvplot.addVectortoPlot(index, cctVector);

  }

  static Plot2D getDefectPlot(CIExyY[] dgWhitexyYArray) {
    Plot2D defectplot = Plot2D.getInstance("defect of Target");
    //=========================================================================
    // 分析dg完的誤差
    //=========================================================================

    for (int x = 0; x < 256; x++) {

//      CIExyY dgxyY = dgWhitexyYArray[x];
      if (x >= 1 && x <= 50) {
        double[] dxy = dgWhitexyYArray[x].getDeltaxy(dgWhitexyYArray[x - 1]);
        defectplot.addCacheScatterLinePlot("dx", x, dxy[0]);
        defectplot.addCacheScatterLinePlot("dy", x, dxy[1]);
      }

    }
    //=========================================================================

    defectplot.setVisible();
    defectplot.setFixedBounds(1, 0, 0.002);
    defectplot.addLegend();
    defectplot.setAxisLabels("Gray Level", "delta");
    return defectplot;
  }

  static double[][] getRecommendRatioOfdxdy(double[][] accdxOfRdyOfGArrayT) {
    double[] dx = DoubleArray.minus(accdxOfRdyOfGArrayT[0],
                                    accdxOfRdyOfGArrayT[0][0]);
    double[] dy = DoubleArray.minus(accdxOfRdyOfGArrayT[1],
                                    accdxOfRdyOfGArrayT[1][0]);
    int size = dx.length;
//    double[] dxRatio = new double[size-1];
    double[][] recommendRatio = new double[size][2];
    for (int x = 0; x < size; x++) {
      recommendRatio[x][0] = dx[x] / dx[size - 1];
      recommendRatio[x][1] = dy[x] / dy[size - 1];
    }
    return recommendRatio;
  }

  static double[][] getdxOfRdyOfGArray(
      LCDModelChromaticityAdjustEstimator adjustEstimator) {
    double[][] dxdyArrayOfR = getdxdyArray(adjustEstimator, RGB.Channel.R);
    double[][] dxdyArrayOfG = getdxdyArray(adjustEstimator, RGB.Channel.G);
    int size = dxdyArrayOfR.length;
    double[][] dxOfRdyOfGArray = new double[size][];
    for (int x = 0; x < size; x++) {
      dxOfRdyOfGArray[x] = new double[] {
          dxdyArrayOfR[x][0], dxdyArrayOfG[x][1]};
    }
    return dxOfRdyOfGArray;
  }

  static double[][] getdxdyArray(ChromaticityAdjustEstimatorIF estimator,
                                 RGB.Channel ch) {
    double[][] dxdyArray = new double[51][];
    for (int x = 0; x <= 50; x++) {
      dxdyArray[x] = estimator.getdxdy(ch, x);
    }
    return dxdyArray;
  }

  static CIExyY[] getDGWhitexyYArray(SimpleLCDModelIF model, RGB[] dglut) {
    CIExyY[] dgWhitexyYArray = new CIExyY[256];
    for (int x = 0; x < 256; x++) {
      CIEXYZ dgXYZ = model.getXYZ(dglut[x]);
      CIExyY dgxyY = new CIExyY(dgXYZ);
      dgWhitexyYArray[x] = dgxyY;
    }
    return dgWhitexyYArray;
  }

//  static RGB[] getDGLut(CIEXYZ[] targetXYZArray, RGB.MaxValue bitDepth) {
//    int size = targetXYZArray.length;
//    RGB[] dglut = new RGB[size];
//    mmodel.setCovertMode(true);
//    mmodel.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode1);
//
//    for (int x = 0; x < size; x++) {
//      RGB rgb = mmodel.getRGB(targetXYZArray[x], false);
//      rgb.changeMaxValue(bitDepth);
//      dglut[x] = rgb;
//    }
//    return dglut;
//  }

  static RGB[] getDGLut(double[][][] intensityArray, RGB.MaxValue bitDepth) {
//    double[][] dglut = new double[256][];
//    dglut[0] = new double[] {
//        0, 0, 0};
    RGB[] dglut = new RGB[256];
    dglut[0] = new RGB(0., 0., 0.);
    double[] keys = new double[256];
    for (int x = 0; x < 256; x++) {
      keys[x] = x;
    }
    for (int x = 1; x < 256; x++) {
      double[][] intensitys = intensityArray[x];
      double[][] intensitysT = DoubleArray.transpose(intensitys);
      Interpolation1DLUT rlut = new Interpolation1DLUT(keys, intensitysT[0],
          Interpolation1DLUT.Algo.LINEAR);
      Interpolation1DLUT glut = new Interpolation1DLUT(keys, intensitysT[1],
          Interpolation1DLUT.Algo.LINEAR);
      Interpolation1DLUT blut = new Interpolation1DLUT(keys, intensitysT[2],
          Interpolation1DLUT.Algo.LINEAR);
      double rvalue = rlut.correctValueInRange(1);
      double gvalue = glut.correctValueInRange(1);
      double bvalue = blut.correctValueInRange(1);
      double r = rlut.getKey(rvalue);
      double g = glut.getKey(gvalue);
      double b = blut.getKey(bvalue);
      dglut[x] = new RGB(r, g, b);
      dglut[x].quantization(bitDepth);
    }
    return dglut;
  }

  static double[][][] getIntensityArray(
      double[][] invm, CIEXYZ[] wXYZArray,
      CIEXYZ[] targetXYZArray) {
    double[][][] intensityArray = new double[256][][];

    for (int x = 255; x >= 1; x--) {
      CIEXYZ targetXYZ = targetXYZArray[x];
      double[] factor = getFactor(invm, targetXYZ);
      double[][] intensitys = new double[256][];

      //算intensity
      for (int y = 255; y >= 0; y--) {
        CIEXYZ XYZ = wXYZArray[y];
        double[] intensity = getIntensity(invm, XYZ, factor);
        intensitys[y] = intensity;
      }
      intensityArray[x] = intensitys;
    }
    return intensityArray;
  }

  static double[] getIntensity(double[][] invm, CIEXYZ XYZ, double[] factor) {
    double[] intensity = DoubleArray.times(invm, XYZ.getValues());
    intensity[0] *= factor[0];
    intensity[1] *= factor[1];
    intensity[2] *= factor[2];
    return intensity;
  }

  /**
   * 校正Target White的Intensity為100%
   * @param invm double[][]
   * @param targetXYZ CIEXYZ
   * @return double[]
   */
  static double[] getFactor(double[][] invm, CIEXYZ targetXYZ) {
    double[] wintensity = DoubleArray.times(invm, targetXYZ.getValues());
    double[] factor = DoubleArray.divide(new double[] {1, 1, 1}, wintensity);
    return factor;
  }

}

interface SimpleLCDModelIF {
  double[][] getRGB2XYZMatrix();

  CIEXYZ getXYZ(double[] rgb);

  CIEXYZ getXYZ(RGB rgb);

  double[][] getXYZ2RGBMatrix();

  CIEXYZ getBlackXYZ();
}

class SimpleLCDModel
    implements SimpleLCDModelIF {
  public CIEXYZ getBlackXYZ() {
    return blackXYZ;
  }

  private static CIExyY getxyY(CIExyY rxyY, CIExyY rxyY2, CIEXYZ blackXYZ,
                               double x) {
    double rY = Interpolation.linear(0, 255, 0, rxyY.Y, x);
    double rx = Interpolation.linear(0, 255, rxyY.x, rxyY2.x, x);
    double ry = Interpolation.linear(0, 255, rxyY.y, rxyY2.y, x);

    CIExyY rr = new CIExyY(rx, ry, rY);
    rr = new CIExyY(CIEXYZ.plus(rr.toXYZ(), blackXYZ));
    return rr;
  }

  public double[][] getRGB2XYZMatrix() {
    CIEXYZ rXYZ = rxyY.toXYZ();
    CIEXYZ gXYZ = gxyY.toXYZ();
    CIEXYZ bXYZ = bxyY.toXYZ();
    double[][] m = new double[][] {
        rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
    m = DoubleArray.transpose(m);
    return m;
//      double[][] invm = DoubleArray.inverse(m);
  }

  public double[][] getXYZ2RGBMatrix() {
    double[][] m = getRGB2XYZMatrix();
    double[][] invm = DoubleArray.inverse(m);
    return invm;
  }

  private CIExyY rxyY, rxyY2, gxyY, gxyY2,
  bxyY, bxyY2;
  private CIEXYZ blackXYZ;
  public SimpleLCDModel(CIExyY rxyY, CIExyY rxyY2, CIExyY gxyY, CIExyY gxyY2,
                        CIExyY bxyY, CIExyY bxyY2, CIEXYZ blackXYZ) {
    this.rxyY = rxyY;
    this.rxyY2 = rxyY2;
    this.gxyY = gxyY;
    this.gxyY2 = gxyY2;
    this.bxyY = bxyY;
    this.bxyY2 = bxyY2;
    this.blackXYZ = blackXYZ;
  }

  public CIEXYZ getXYZ(RGB rgb) {
    return getXYZ(rgb.getValues(new double[3], RGB.MaxValue.Double255));
  }

  public CIEXYZ getXYZ(double[] rgb) {
    double r = rgb[0];
    double g = rgb[1];
    double b = rgb[2];
    CIExyY rr = getxyY(rxyY, rxyY2, blackXYZ, r);
    CIExyY gg = getxyY(gxyY, gxyY2, blackXYZ, g);
    CIExyY bb = getxyY(bxyY, bxyY2, blackXYZ, b);
    CIEXYZ rrXYZ = rr.toXYZ();
    CIEXYZ ggXYZ = gg.toXYZ();
    CIEXYZ bbXYZ = bb.toXYZ();

    CIEXYZ XYZ = rgbToWhite(rrXYZ, ggXYZ, bbXYZ, blackXYZ);
    return XYZ;
  }

  static CIEXYZ rgbToWhite(CIEXYZ r, CIEXYZ g, CIEXYZ b, CIEXYZ k) {
    CIEXYZ rg = CIEXYZ.plus(r, g);
    CIEXYZ rgb = CIEXYZ.plus(rg, b);
    CIEXYZ wXYZ = CIEXYZ.minus(rgb, k);
    return wXYZ;
  }

  public final static SimpleLCDModelIF getSimpleLCDModel2(boolean smooth,
      int smoothTimes) {
//    LCDTarget target = LCDTarget.Instance.getFromLogo("Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\091225\\1021.logo",
//        LCDTarget.Number.Ramp1021);
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("../../../workdir/Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
        LCDTarget.Number.Ramp1024);
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("../../../workdir/Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\111011\\Measurement09_.xls",
//        LCDTarget.Number.Ramp1024);
    LCDTarget.Operator.gradationReverseFix(target);

    if (smooth) {
      for (RGB.Channel ch : RGB.Channel.RGBChannel) {
        List<Patch> patchList = target.filter.grayScalePatch(ch);
        int size = patchList.size();
        for (int times = 0; times < smoothTimes; times++) {
          for (int x = 1; x < size - 2; x++) {
            CIEXYZ XYZ0 = patchList.get(x - 1).getXYZ();
            CIEXYZ XYZ2 = patchList.get(x + 1).getXYZ();
            Patch p = patchList.get(x);
            CIEXYZ XYZ1 = CIEXYZ.plus(XYZ0, XYZ2);
            XYZ1.times(0.5);
            Patch.Operator.setXYZ(p, XYZ1);
          }
        }
      }
    }

//    target.setMax
    final MultiMatrixModel mmodel = new MultiMatrixModel(target);
    mmodel.produceFactor();
    mmodel.setMaxValue(RGB.MaxValue.Double255);
    mmodel.setAutoRGBChangeMaxValue(true);

    return getInstance(mmodel);
  }

  public final static SimpleLCDModelIF getSimpleLCDModel() {
    RGB.ColorSpace cs = RGB.ColorSpace.sRGB;
    CIEXYZ blackXYZ = new RGB(cs, new int[] {2, 2, 4}).toXYZ();
    CIEXYZ rXYZ = new RGB(cs, new int[] {255, 0, 0}).toXYZ();
    CIEXYZ gXYZ = new RGB(cs, new int[] {0, 255, 0}).toXYZ();
    CIEXYZ bXYZ = new RGB(cs, new int[] {0, 0, 255}).toXYZ();
//    CIExyY blackxyY = new CIExyY(blackXYZ);

//    CIEXYZ blackXYZ2 = (CIEXYZ) blackXYZ.clone();
//    blackXYZ2.times(2);

//原始的色度座標
    CIExyY rxyY = new CIExyY(rXYZ);
    CIExyY gxyY = new CIExyY(gXYZ);
    CIExyY bxyY = new CIExyY(bXYZ);

//偏移後的色度座標
    CIExyY rxyY2 = (CIExyY) rxyY.clone();
    rxyY2.x += 0.02;
    CIExyY gxyY2 = (CIExyY) gxyY.clone();
    gxyY2.x += 0.018;
    gxyY2.y -= 0.005;
    CIExyY bxyY2 = (CIExyY) bxyY.clone();
    bxyY2.x += 0.003;
    bxyY2.y += 0.015;
    SimpleLCDModel model = new SimpleLCDModel(rxyY, rxyY2, gxyY, gxyY2, bxyY,
                                              bxyY2, blackXYZ);
    return model;
  }

  //  public static
  public final static SimpleLCDModelIF getSimpleLCDModel3() {
//    final LCCharacterization lc = LCCharacterization.getInstance();
//
//    SimpleLCDModelIF model = new SimpleLCDModelIF() {
//
//      public double[][] getRGB2XYZMatrix() {
//        CIEXYZ rXYZ = lc.getSpectra(255, 0, 0).getXYZ();
//        CIEXYZ gXYZ = lc.getSpectra(0, 255, 0).getXYZ();
//        CIEXYZ bXYZ = lc.getSpectra(0, 0, 255).getXYZ();
//        double[][] m = new double[][] {
//            rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
//        m = DoubleArray.transpose(m);
//
//        return m;
//      }
//
//      public CIEXYZ getXYZ(double[] rgbValues) {
////    RGB rgb = new RGB(RGB.ColorSpace.sRGB, rgbValues,
////                      RGB.MaxValue.Double255);
////double[] rgbValues=    rgb.getValues(new double[3],RGB.MaxValue.Double255);
//        Spectra s = lc.getSpectra(rgbValues[0], rgbValues[1], rgbValues[2]);
//        return s.getXYZ();
//      }
//
//      public CIEXYZ getXYZ(RGB rgb) {
//        return getXYZ(rgb.getValues(new double[3], RGB.MaxValue.Double255));
//      }
//
//      public double[][] getXYZ2RGBMatrix() {
//        double[][] m = getRGB2XYZMatrix();
//        double[][] invm = DoubleArray.inverse(m);
//        return invm;
//      }
//
//      public CIEXYZ getBlackXYZ() {
//        return lc.getSpectra(0, 0, 0).getXYZ();
//      }
//
//    };
//    return model;
    return null;
  }

  public final static SimpleLCDModelIF getInstance(final LCDModel lcdmodel) {
    SimpleLCDModelIF model = new SimpleLCDModelIF() {

      public double[][] getRGB2XYZMatrix() {
        CIEXYZ rXYZ = lcdmodel.getXYZ(new RGB(255, 0, 0), false);
        CIEXYZ gXYZ = lcdmodel.getXYZ(new RGB(0, 255, 0), false);
        CIEXYZ bXYZ = lcdmodel.getXYZ(new RGB(0, 0, 255), false);
        double[][] m = new double[][] {
            rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
        m = DoubleArray.transpose(m);

        return m;
      }

      public CIEXYZ getXYZ(double[] rgbValues) {
        RGB rgb = new RGB(RGB.ColorSpace.sRGB, rgbValues,
                          RGB.MaxValue.Double255);
        return lcdmodel.getXYZ(rgb, false);
      }

      public CIEXYZ getXYZ(RGB rgb) {
        return getXYZ(rgb.getValues(new double[3], RGB.MaxValue.Double255));
      }

      public double[][] getXYZ2RGBMatrix() {
        double[][] m = getRGB2XYZMatrix();
        double[][] invm = DoubleArray.inverse(m);
        return invm;
      }

      public CIEXYZ getBlackXYZ() {
        return lcdmodel.getXYZ(new RGB(0, 0, 0), false);
      }

    };
    return model;
  }

  public final static SimpleLCDModelIF getSimpleLCDModel4() {
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("../../../workdir/Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
//        LCDTarget.Number.Ramp1024);
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("../../../workdir/Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\111011\\Measurement09_.xls",
        LCDTarget.Number.Ramp1024);

//    target = target.targetFilter.getPatch79From1024();
    LCDTarget.Operator.gradationReverseFix(target);

//    Spectra[] rgbColorFilter = SpectraModel.getRGBColorFilter(
//        "../../../workdir/Reference Files/ColorFilter.xls");
//    LCTransmission lct = new LCTransmission(.3, true, 0.01, 2, 0.01);
//    Spectra backlight = Illuminant.F11.getSpectra();
//    final SpectraModel mmodel = new SpectraModel(lct, rgbColorFilter, backlight,
//                                                 1, 21);
    ChromaticityModel.setFlareType(LCDModel.FlareType.Black);
//    MultiMatrixModel.setFlareType(LCDModel.FlareType.Black);
//    final MultiMatrixModel model = new MultiMatrixModel(target);
//    model.produceFactor();

    final ChromaticityModel model = new ChromaticityModel(target);
//    mmodel.setSmoothTimes(3);
//    mmodel.setSmoothByCubic(false);

//    final SCurveModel mmodel = new SCurveModel(target);
//    final PLCCModel mmodel = new PLCCModel(target);
    model.produceFactor();
    model.setMaxValue(RGB.MaxValue.Double255);
    model.setAutoRGBChangeMaxValue(true);

//    System.out.println(mmodel.getXYZ(new RGB(0, 0, 0), true));
//    System.out.println(model.getXYZ(new RGB(0, 0, 0), true));

    return getInstance(model);
  }
}
