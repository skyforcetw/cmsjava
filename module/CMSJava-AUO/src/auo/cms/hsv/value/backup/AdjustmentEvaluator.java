/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auo.cms.hsv.value.backup;

import java.awt.*;

import org.math.plot.*;
import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.Plot2D;
import shu.cms.plot.Plot3D;
import shu.math.*;
import shu.math.Polynomial;
import shu.math.array.*;
import shu.math.lut.*;
import shu.math.regress.*;
import shu.plot.*;

///import shu.plot.*;

/**
 *
 * @author SkyforceShen
 */
public class AdjustmentEvaluator {
  //========================================================================
  // precision setting
  //========================================================================
  static final int PIECE = 31;
  static final int SKIP_PIECE = 5;
  static final int END_HUE = 360;
  static final int HUE_STEP = 60;
  static final boolean SKIP_NEUTRAL = true;
  static final boolean SKIP_HEAD_TAIL = true;
  static Font FONT = new Font("Arial", Font.PLAIN, 24);
  static Font LIGHTFONT = new Font("Arial", Font.PLAIN, 22);
  //========================================================================

  //========================================================================
  // fitting setting
  //========================================================================
  //多項式的3D fitting
  static boolean poly3DFit = false;
  static boolean simpleFit = true;
  static boolean plotting2D = false;
  static boolean plotting3D = true;
  static boolean plotting3DSaturation = false;
  static boolean plotting3DInterpolate = false;
  static boolean plotting2DInterpolate = false;

  static boolean showcoef = true;
  static boolean showOrg = false;
  static boolean showOrg2D = false;
  //是否要旋轉視角
  static boolean rotate2Axis = true;
  static int rotateAxis = 1;
  //第二軸是否要auto scale
  static boolean autoScaleAxis2 = false;
  //========================================================================

  static void initHueData(int h, RGB.ColorSpace targetColorspace,
                          LCDModel model,
                          double[][] griddata, double[][] griddataSaturation,
                          CIEXYZ[][] targetXYZData) {
    double luminance = model.getWhiteXYZ(false).Y;
    for (int vv = 0; vv <= (PIECE - 1); vv++) {
      double v = ( (double) vv) / (PIECE - 1) * 255;
      double[] grayDeltaValue = new double[PIECE];
      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        HSV hsv = new HSV(targetColorspace, new double[] {h, s,
                          v / 2.55});
        RGB rgb = hsv.toRGB();
        CIEXYZ targetXYZ = rgb.toXYZ(targetColorspace);
        if (targetXYZData != null) {
          targetXYZData[vv][ss] = targetXYZ;
        }
        targetXYZ.times(luminance);
        RGB panelRGB = model.getRGB(targetXYZ, false);
        HSV panelhsv = new HSV(panelRGB);

        double deltaV = (panelhsv.V - hsv.V) * 2.55;
        double deltaS = panelhsv.S - hsv.S;
        if (griddataSaturation != null) {
          griddataSaturation[vv][ss] = deltaS;
        }
        if (ss == 0 && SKIP_NEUTRAL) {
          //是否略過中性色
          grayDeltaValue[vv] = deltaV;
        }
        deltaV = deltaV - grayDeltaValue[vv];
        if (SKIP_HEAD_TAIL && (vv == 0 || vv == (PIECE - 1))) {
          //是否去頭去尾
          deltaV = 0;
        }

        griddata[vv][ss] = deltaV;

      }
    }
  }

  static void initHueData(int h, RGB.ColorSpace targetColorspace,
                          LCDModel model,
                          double[][] griddata, double[][] input3D,
                          double[][] output3D, double[] input_V,
                          double[] outputDeltaV,
                          double[][] oneCaseCurves4Interp,
                          int oneCaseCurveIndex, double[] inputHSV_Chroma,
                          double[][] griddataSaturation,
                          CIEXYZ[][] targetXYZData) {

    Plot2D orgPlot2d = Plot2D.getInstance(Integer.toString(h));
    int index = 0;

    initHueData(h, targetColorspace, model, griddata, griddataSaturation,
                targetXYZData);

    for (int vv = 0; vv <= (PIECE - 1); vv++) {
      double v = ( (double) vv) / (PIECE - 1) * 255;

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s,
                          v / 2.55});

        double deltaV = griddata[vv][ss];

        if (showOrg && showOrg2D) {
          orgPlot2d.addCacheScatterLinePlot(Double.toString(s), Color.red,
                                            v, s + deltaV);
        }

        input3D[index][0] = hsv.S;
        input3D[index][1] = hsv.V * 2.55;
        input3D[index][2] = 0;
        output3D[index][0] = deltaV;

        input_V[index] = hsv.V * 2.55;
        outputDeltaV[index] = deltaV;
        if (ss == (PIECE - 1)) {
          oneCaseCurves4Interp[oneCaseCurveIndex][vv] = deltaV;
        }

        double chroma = hsv.S * hsv.V * 2.55 / 100.;
        inputHSV_Chroma[index] = chroma;

        index++;
      }
    }

    if (showOrg && showOrg2D) {
      orgPlot2d.setVisible();
      orgPlot2d.setFixedBounds(0, 0, 255);
      orgPlot2d.setFixedBounds(1, 0, 100);
      orgPlot2d.setAxeLabel(0, "Value");
      orgPlot2d.setAxeLabel(1, "Saturation");
    }
  }

  public static void showCurve(String[] args) {
    double[] sArray = new double[PIECE];
    double[] vArray = new double[PIECE];
    for (int x = 0; x < PIECE; x++) {
      sArray[x] = ( (double) x) / (PIECE - 1) * 100;
      vArray[x] = ( (double) x) / (PIECE - 1) * 255;
    }

    double[][] gridData = new double[PIECE][PIECE];
    double[][] gridDataOld = new double[PIECE][PIECE];
//    int gridsize = PIECE * PIECE;
    for (int ss = 0; ss <= (PIECE - 1); ss++) {
      double s = ( (double) ss) / (PIECE - 1) * 100;
      for (int vv = 0; vv <= (PIECE - 1); vv++) {
        double v = ( (double) vv) / (PIECE - 1) * 255;

        double deltaV = ModifiedHSV.getDeltaValue(63, s, v, false);
        gridData[ss][vv] = deltaV;
        double deltaVOld = getOldDeltaValue(63, s, v, false);
        gridDataOld[ss][vv] = deltaVOld;
      }
    }

    Plot3D plot = Plot3D.getInstance();
    plot.addGridPlot("New", sArray, vArray, gridData);
//    plot.addGridPlot("Old", sArray, vArray, gridDataOld);
    plot.setAxisLabels("Saturation", "Value", "dValue");
    plot.setVisible();
    plot.setFixedBounds(0, 0, 100);
    plot.setFixedBounds(1, 0, 255);
    plot.setFixedBounds(2, 0, 128);
  }

  public static void main(String[] args) {
//    caseSimulate(args);
    showCurve(args);
  }

  public static double getOldDeltaValue(double offset, double s, double v,
                                        boolean doubleMax) {
    double chroma = s * v / 100.;
    double t = chroma * (255 - v) * v;
    double deltaV = offset * chroma / 128;
    if (doubleMax) {
      deltaV = deltaV * v / 256;
    }
    return deltaV;
  }

  public static void caseSimulate(String[] args) {
    //========================================================================
    // panel setting
    //========================================================================
    LCDTarget targets[] = new LCDTarget[] {
        LCDTarget.Instance.getFromAUORampXLS(
            "sRGB Adjust Evaluation/45% DG On.xls"),
//        LCDTarget.Instance.getFromAUORampXLS(
//            "sRGB Adjust Evaluation/60% DG On.xls",
//            LCDTarget.Number.Ramp256_6Bit),

    };
    //========================================================================

    //========================================================================
    // target color space setting
    //========================================================================
    RGB.ColorSpace targetColorspaces[] = new RGB.ColorSpace[] {
        RGB.ColorSpace.sRGB_gamma22
//        , RGB.ColorSpace.AdobeRGB
    };
    //========================================================================

    int caseCount = targets.length * targetColorspaces.length;
    int hueSize = END_HUE / HUE_STEP;
    int totalHueCount = caseCount * hueSize;
    double[][] totalCurves = new double[totalHueCount][PIECE];
    int totalCurveIndex = 0;
    double hsvRMSDTotal = 0;
    double hsvModifiedRMSDTotal = 0;
    double hsvModifiedDoubleRMSDTotal = 0;

    double[] sArray = new double[PIECE];
    double[] vArray = new double[PIECE];
    for (int x = 0; x < PIECE; x++) {
      sArray[x] = ( (double) x) / (PIECE - 1) * 100;
      vArray[x] = ( (double) x) / (PIECE - 1) * 255;
    }
    int gridsize = PIECE * PIECE;

    for (LCDTarget target : targets) {
      LCDTarget.Operator.gradationReverseFix(target);
      target.changeMaxValue(RGB.MaxValue.Int8Bit);
      //========================================================================
      // lcd model
      //========================================================================
      MultiMatrixModel model = new MultiMatrixModel(target);
      model.produceFactor();
      //========================================================================

      model.setAutoRGBChangeMaxValue(true);
      for (RGB.ColorSpace targetColorspace : targetColorspaces) {
        //======================================================================
        // show color space info
        //======================================================================
        String colorSpaceName = null;

        if (targetColorspace == RGB.ColorSpace.sRGB_gamma22) {
          colorSpaceName = "sRGB g2.2";
        }
        else if (targetColorspace == RGB.ColorSpace.AdobeRGB) {
          colorSpaceName = "AdobeRGB";
        }
        String caseName = target.getFilename() + " " + colorSpaceName;
        System.out.println("\n" + caseName);
        double eachRMSDTotal = 0;
        double eachModifiedRMSDTotal = 0;
        double eachModifiedDoubleRMSDTotal = 0;
        //======================================================================

        //======================================================================
        // single case curve
        //======================================================================
        double[][] oneCaseCurves4Poly = new double[hueSize][PIECE];
        double[][] oneCaseCurves4Interp = new double[hueSize][PIECE];
        int oneCaseCurveIndex = 0;
        //======================================================================

        for (int h = 0; h < END_HUE; h += HUE_STEP) {
//          Plot2D orgPlot2d = Plot2D.getInstance(Integer.toString(h));
          Plot3D orgPlot3d = Plot3D.getInstance(Integer.toString(h) + " 3D",
                                                800, 800);

          //========================================================================
          // 產生
          //========================================================================
          double[][] griddata = new double[PIECE][PIECE];
          double[][] griddataSaturation = new double[PIECE][PIECE];
          double[][] input3D = new double[gridsize][3];
          double[][] output3D = new double[gridsize][1];
          //V
          double[] input_V = new double[gridsize];
          //delta V
          double[] outputDeltaV = new double[gridsize];
          //chroma
          double[] inputHSV_Chroma = new double[gridsize];
          CIEXYZ[][] targetXYZData = new CIEXYZ[PIECE][PIECE];

          initHueData(h, targetColorspace, model, griddata, input3D, output3D,
                      input_V, outputDeltaV, oneCaseCurves4Interp,
                      oneCaseCurveIndex, inputHSV_Chroma, griddataSaturation,
                      targetXYZData);
          //========================================================================

          HSV hueHSV = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 100});
          Color c = hueHSV.toRGB().getColor();

          //=====================================================================
          // draw saturation
          //=====================================================================
//            boolean plot3DSaturation = true;
          if (plotting3DSaturation) {
            Plot3D plot3dSaturation = Plot3D.getInstance(Integer.toString(h) +
                " Saturation", 800, 800, Plot3D.Instance.JMathPlot3D);
            plot3dSaturation.addGridPlot(Integer.toString(h), c, sArray,
                                         vArray,
                                         griddataSaturation);
            plot3dSaturation.setAxisLabels("Saturation", "Value",
                                           "dSaturation");
            plot3dSaturation.setVisible();
            plot3dSaturation.rotateToAxis(0);
          }
          //=====================================================================

          //========================================================================
          // 3D
          //========================================================================
          if (showOrg) {
            orgPlot3d.addGridPlot(Integer.toString(h), c, sArray, vArray,
                                  griddata);
            orgPlot3d.setVisible();
            orgPlot3d.setAxeLabel(0, "Saturation");
            orgPlot3d.setAxeLabel(1, "Value");
            orgPlot3d.setAxeLabel(2, "dValue");
            orgPlot3d.setFixedBounds(1, 0, 255);
            orgPlot3d.setFixedBounds(2, -10, 10);
          }
          //3D Setting
          if (poly3DFit) {
            Polynomial.COEF_2 coef = Polynomial.COEF_2.BY_9;
            Plot3D plot3d = Plot3D.getInstance(Integer.toString(h) + " " +
                                               coef.name(), 800, 800);
            Plot2D plot2d = Plot2D.getInstance(Integer.toString(h) + " " +
                                               coef.name(), 800, 800);
            Plot2D plot2derr = Plot2D.getInstance(Integer.toString(h) + " " +
                                                  coef.name() + " err", 800,
                                                  800);
            Plot2D plot2dtest = Plot2D.getInstance(Integer.toString(h) + " " +
                coef.name() + " test", 800, 800);

            plot3d.addGridPlot(Integer.toString(h), c, sArray, vArray,
                               griddata);
            PolynomialRegression regress = new PolynomialRegression(input3D,
                output3D, coef);
            regress.regress();

            System.out.println(Integer.toString(h) + " " + coef + " " +
                               regress.getRMSD());
            double[][] coefs = regress.getCoefs();
            System.out.println(DoubleArray.toString(coefs));

            double[][] griddata3D = new double[PIECE][PIECE];
            int index = 0;
            for (int vv = 0; vv <= (PIECE - 1); vv++) {
              double v = ( (double) vv) / (PIECE - 1) * 255;
              //            double[] grayDeltaValue = new double[piece];
              for (int ss = 0; ss <= (PIECE - 1); ss++) {
                double s = ( (double) ss) / (PIECE - 1) * 100;

                double[] predict = regress.getPredict(new double[][] { {s, v,
                    0}
                })[0];
                double deltaV = predict[0];
                griddata3D[vv][ss] = deltaV;
                double orgDeltaV = output3D[index++][0];
                plot2d.addCacheScatterLinePlot(Double.toString(s), Color.red,
                                               v, s + orgDeltaV);
                plot2d.addCacheScatterLinePlot(Double.toString(s) + "_",
                                               Color.black,
                                               v, s + deltaV);

                plot2derr.addCacheScatterLinePlot(Double.toString(s),
                                                  Color.black,
                                                  v, deltaV - orgDeltaV);

                plot2dtest.addCacheScatterLinePlot(Double.toString(v),
                    Color.black,
                    v, s);
                plot2dtest.addCacheScatterLinePlot(Double.toString(v) + "_",
                    Color.red,
                    v + orgDeltaV, s);
              }
            }
            plot3d.addGridPlot(Integer.toString(h) + "_", Color.gray, sArray,
                               vArray, griddata3D);

            plot3d.addLegend();
            plot3d.setVisible();
            plot3d.setAxeLabel(0, "Saturation");
            plot3d.setAxeLabel(1, "Value");
            plot3d.setAxeLabel(2, "dValue");
            plot3d.setFixedBounds(1, 0, 255);
            plot3d.setFixedBounds(2, -10, 10);

            plot2d.setVisible();
            plot2d.setFixedBounds(0, 0, 255);
            plot2d.setFixedBounds(1, 0, 100);
            plot2d.setAxeLabel(0, "Value");
            plot2d.setAxeLabel(1, "Saturation");

            plot2derr.setVisible();
            plot2derr.setFixedBounds(0, 0, 255);
            plot2derr.setFixedBounds(1, -20, 20);
            plot2derr.setAxeLabel(0, "Value");
            plot2derr.setAxeLabel(1, "Saturation");

            plot2dtest.setVisible();
            plot2dtest.setFixedBounds(0, 0, 255);
            plot2dtest.setFixedBounds(1, 0, 100);
            plot2dtest.setAxeLabel(0, "Value");
            plot2dtest.setAxeLabel(1, "Saturation");

          }
          //========================================================================


          if (simpleFit) {
            Polynomial.COEF_1 bestcoef = Polynomial.COEF_1.BY_3;
            PolynomialRegression regress2 = new PolynomialRegression(
                inputHSV_Chroma,
                outputDeltaV, Polynomial.COEF_1.BY_1);
            regress2.regress();
            double oldOffset = regress2.getCoefs()[0][0];

            PolynomialRegression regress = new PolynomialRegression(input_V,
                outputDeltaV, bestcoef);
            regress.regress();
            System.out.println(Integer.toString(h) + " poly3: " +
                               regress.getRMSD() +
                               ": " +
                               bestcoef.toString(regress.getCoefs()[0]));

            Plot2D plot2d = Plot2D.getInstance(Integer.toString(h) + " " +
                                               bestcoef.name(), 800, 800);

            //====================================================================
            // Bezier2
            //====================================================================
//              Bezier2 bezier2 = new Bezier2(griddata);
            Minimisation min = new Minimisation();
//              min.nelderMead(bezier2, new double[] {0.5, 1});
//              double[] params2 = min.getParamValues();

            //          Bezier2 mbezier2 = new Bezier2(griddata, true);
            //          min.nelderMead(mbezier2, new double[] {0.5, 1});
            //          double[] params2m = min.getParamValues();

            //===================================================================
            // Bezier3
            //====================================================================
            //          Bezier3 minfunc3 = new Bezier3(griddata);
            //          Minimisation min = new Minimisation();
            //          min.addConstraint(0,  1, 0);
            //          min.addConstraint(0,  -1, 1);
            //          min.nelderMead(minfunc3, new double[] {0.5, 1, 0.6, 1});
            //          double[] params3 = min.getParamValues();
            //          System.out.println("Min: " + min.getMinimum() + "/ " +
            //                             DoubleArray.toString(params2));
            //====================================================================
            // Poly3
            //====================================================================
            Polynomial3_2D minfuncpoly = new Polynomial3_2D(griddata);
            //          min.addConstraint(new int[] {0, 1, 2},
            //                            new double[] {102, 102 * 102, 102 * 102 * 102}, 0,
            //                            0);
            //          int xx = 192;
            //          min.addConstraint(new int[] {0, 1, 2},
            //                            new double[] {xx, xx * xx, xx * xx * xx}, 0,
            //                            0);

            min.nelderMead(minfuncpoly, new double[] {1, 1, 1});
            double[] paramspoly = min.getParamValues();
            double[] p = paramspoly;
            //====================================================================
            // Special Poly3
            //====================================================================
            //          Polynomial3 minfuncpolys = new Polynomial3(griddata);
            //          min.nelderMead(minfuncpoly, new double[] {1, 1, 1});
            //          double[] paramspolys = min.getParamValues();
            //          double[] sp = paramspoly;

            //====================================================================
            // Modified HSV
            //====================================================================
            ModifiedHSV modified = new ModifiedHSV(griddata, false);
            min.nelderMead(modified, new double[] {1});
            double[] paramsmhsv = min.getParamValues();
            //====================================================================
            //====================================================================
            // Modified Double HSV
            //====================================================================
            ModifiedHSV modifieddouble = new ModifiedHSV(griddata, true);
            min.nelderMead(modifieddouble, new double[] {1});
            double[] paramsmhsvDouble = min.getParamValues();
            //====================================================================


            //          double[][] polygriddata = new double[piece][piece];
            double[][] poly3griddata = new double[PIECE][PIECE];
            double[][] hsvgriddata = new double[PIECE][PIECE];
            double[][] modifiedHSVGriddata = new double[PIECE][PIECE];
            double[][] modifiedDoubleHSVGriddata = new double[PIECE][PIECE];

            CIEXYZ[][] panelXYZDataHSV = new CIEXYZ[PIECE][PIECE];
            CIEXYZ[][] panelXYZDataModifiedHSV = new CIEXYZ[PIECE][PIECE];
            CIEXYZ[][] panelXYZDataModifiedDoubleHSV = new CIEXYZ[PIECE][PIECE];
            //          double[][] mbezier2griddata = new double[piece][piece];
//              double[][] bezier2griddata = new double[PIECE][PIECE];
            //          double[][] bezier3griddata = new double[piece][piece];

            for (int ss = 0; ss <= (PIECE - 1); ss++) {
              double s = ( (double) ss) / (PIECE - 1) * 100;
              for (int vv = 0; vv <= (PIECE - 1); vv++) {
                double v = ( (double) vv) / (PIECE - 1) * 255;
                //original adjust data
                double deltaV = griddata[vv][ss];
                plot2d.addCacheScatterLinePlot(Double.toString(s),
                                               Color.red, v,
                                               deltaV);
//                  double predict = regress.getPredict(new double[] {v})[0] * s /
//                      100.;
                //              polygriddata[vv][ss] = predict;
                //              System.out.println(s / 100. + " " + v);

                HSV hsv = new HSV(targetColorspace, new double[] {h, s,
                                  v / 2.55});

                //old method

                double oldAdjust = s / 100. * v * oldOffset / 256.;
                hsvgriddata[vv][ss] = oldAdjust;
                HSV oldAdjustHSV = (HSV) hsv.clone();
                oldAdjustHSV.V += oldAdjust / 2.55;
                panelXYZDataHSV[vv][ss] = model.getXYZ(oldAdjustHSV.toRGB(), false);

                //modified hsv method
                double modifiedAdjust = ModifiedHSV.getDeltaValue(paramsmhsv[
                    0], s, v, false);
                modifiedHSVGriddata[vv][ss] = modifiedAdjust;
                HSV modifiedAdjustHSV = (HSV) hsv.clone();
                modifiedAdjustHSV.V += modifiedAdjust / 2.55;
                panelXYZDataModifiedHSV[vv][ss] = model.getXYZ(
                    modifiedAdjustHSV.toRGB(), false);

                //modified double hsv method
                double modifiedDoubleAdjust = ModifiedHSV.getDeltaValue(
                    paramsmhsvDouble[0], s, v, true);
                modifiedDoubleHSVGriddata[vv][ss] = modifiedDoubleAdjust;
                HSV modifiedDoubleAdjustHSV = (HSV) hsv.clone();
                modifiedDoubleAdjustHSV.V += modifiedDoubleAdjust / 2.55;
                panelXYZDataModifiedDoubleHSV[vv][ss] = model.getXYZ(
                    modifiedDoubleAdjustHSV.toRGB(), false);

                //panel CIEXYZ adjust
//                panelXYZData[vv][ss]

//                  double modifiedAdjust = s / 100. * v * oldOffset / 256.;

//                  double bezier2Adjust = bezier2.bezier(v / 255., params2[0],
//                      params2[1]) * s / 100.;
//                  bezier2griddata[vv][ss] = bezier2Adjust;

                //              double mbezier2Adjust = mbezier2.bezier(v / 255., params2m[0],
                //                  params2m[1]) * s / 100.;
                //              mbezier2griddata[vv][ss] = mbezier2Adjust;

                //              double bezier3Adjust = minfunc3.bezier(v / 255., params3[0],
                //                  params3[1], params3[2], params3[3]) * s / 100.;
                //              bezier3griddata[vv][ss] = bezier3Adjust;

                //poly 3D fitting
//                  double poly3Adjust = (p[0] * v + p[1] * v * v +
//                                        p[2] * v * v * v) * s / 100.;
                double poly3Adjust = Polynomial3_2D.getDeltaValue(p[0], p[1],
                    p[2], s, v);
                poly3griddata[vv][ss] = poly3Adjust;

              }
            }

            //==================================================================
            // prepare data for fitting
            //==================================================================
            for (int vv = 0; vv <= (PIECE - 1); vv++) {
              double v = ( (double) vv) / (PIECE - 1) * 255;

              double poly3 = poly3griddata[vv][PIECE - 1];
              plot2d.addCacheScatterLinePlot("Poly3", Color.blue, v, poly3);
              totalCurves[totalCurveIndex][vv] = poly3;
              oneCaseCurves4Poly[oneCaseCurveIndex][vv] = poly3;
            }
            //==================================================================

            //====================================================================
//            boolean plotting2D = false;
            if (plotting2D) {
              plot2d.setVisible();
            }
            plot2d.setFixedBounds(0, 0, 255);
            plot2d.setFixedBounds(1, -5, 50);
            plot2d.setAxeLabel(0, "Value");
            plot2d.setAxeLabel(1, "dValue");
            //====================================================================

//            boolean plotting3D = true;
            if (plotting3D) {
              Plot3D plot3d = Plot3D.getInstance(Integer.toString(h) + " " +
                                                 caseName
                                                 /* + " " +
                                                 coef.name()*/, 800, 800,
                                                 Plot3D.Instance.JMathPlot3D);
              plot3d.addGridPlot(Integer.toString(h), c, sArray, vArray,
                                 griddata);
              //          plot3d.addGridPlot(Integer.toString(h) + "_poly", Color.black, sArray,
              //                             vArray, polygriddata);
//              plot3d.addGridPlot(Integer.toString(h) + "_poly3",
//                                 Color.lightGray,
//                                 sArray,
//                                 vArray, poly3griddata);
              plot3d.addGridPlot(Integer.toString(h) + "_old", Color.magenta,
                                 sArray,
                                 vArray, hsvgriddata);
              plot3d.addGridPlot(Integer.toString(h) + "modifed", Color.pink,
                                 sArray, vArray, modifiedHSVGriddata);

//              plot3d.addGridPlot(Integer.toString(h) + "modifed\"", Color.gray,
//                                 sArray, vArray, modifiedDoubleHSVGriddata);


//              double[][] delta = DoubleArray.minus(modifiedHSVGriddata,
//                  griddata);
//              plot3d.addGridPlot(Integer.toString(h) + "delta", Color.black,
//                                 sArray,
//                                 vArray, delta);

              //          plot3d.addGridPlot(Integer.toString(h) + "_b2", Color.green, sArray,
              //                             vArray, bezier2griddata);
              //          plot3d.addGridPlot(Integer.toString(h) + "_mb2", Color.green, sArray,
              //                             vArray, mbezier2griddata);
              //          plot3d.addGridPlot(Integer.toString(h) + "_b3", Color.blue, sArray,
              //                             vArray, bezier3griddata);

              //====================================================================
//              plot3d.rotate(79, -79);
//              plot3d.rotate(235, -79)
              if (rotate2Axis) {
                plot3d.rotateToAxis(rotateAxis);
              }
              PlotPanel plotpanel = (PlotPanel) plot3d.getPlotPanel();

              for (int x = 0; x < 3; x++) {
                plotpanel.getAxis(x).setLightLabelFont(LIGHTFONT);
                plotpanel.getAxis(x).setLabelFont(FONT);
              }
              plotpanel.getAxis(0).setLightLabelAngle(Math.PI / 2);
              plotpanel.getAxis(1).setLightLabelAngle(Math.PI / 2);

              plot3d.setAxisLabels("Saturation", "Value", "dValue");
              plot3d.setVisible();
              plot3d.setFixedBounds(1, 0, 255);
              if (autoScaleAxis2) {
                plot3d.setFixedBounds(2, -40, 40);
              }
              //=====================================================================

            }

            //==================================================================
            // RMSD calculate
            //==================================================================
            System.out.println("poly3 RMSD: " +
                               Maths.RMSD(griddata, poly3griddata));
            if (showcoef) {
              System.out.println(DoubleArray.toString(p) + "\n");
            }
            //==================================================================
            double rmsd = Maths.RMSD(griddata, hsvgriddata);
            hsvRMSDTotal += rmsd;
            eachRMSDTotal += rmsd;
            System.out.println("hsv RMSD: " + rmsd);
            if (showcoef) {
              System.out.println(oldOffset + "\n");
            }
            //==================================================================
            rmsd = Maths.RMSD(griddata, modifiedHSVGriddata);
            hsvModifiedRMSDTotal += rmsd;
            eachModifiedRMSDTotal += rmsd;
            System.out.println("hsv' RMSD: " +
                               Maths.RMSD(griddata, modifiedHSVGriddata));
            if (showcoef) {
              System.out.println(paramsmhsv[0] + "\n");
            }
            //==================================================================
            rmsd = Maths.RMSD(griddata, modifiedDoubleHSVGriddata);
            hsvModifiedDoubleRMSDTotal += rmsd;
            eachModifiedDoubleRMSDTotal += rmsd;
//            hsvModifiedRMSDTotal += rmsd;
//            eachModifiedRMSDTotal += rmsd;
            System.out.println("hsv\" RMSD: " +
                               Maths.RMSD(griddata, modifiedDoubleHSVGriddata));
            if (showcoef) {
              System.out.println(paramsmhsvDouble[0] + "\n");
            }
            //==================================================================

            //==================================================================
            // deltaE calculate
            //==================================================================
            CIEXYZ targetWhute = model.getWhiteXYZ(false);
//            double luminance = targetWhute.Y;
            double totalHSVDE = 0, totalModifiedDE = 0,
                totalDoubleModifiedDE = 0;
            int index = 0;
            for (int ss = 0; ss <= (PIECE - 1); ss++) {
              for (int vv = 0; vv <= (PIECE - 1); vv++) {
                index++;
                CIEXYZ targetXYZ = targetXYZData[vv][ss];

                CIEXYZ hsvXYZ = panelXYZDataHSV[vv][ss];
//                hsvXYZ.times(luminance);

                CIEXYZ modifiedXYZ = panelXYZDataModifiedHSV[vv][ss];
//                modifiedXYZ.times(luminance);

                CIEXYZ doubleModifiedXYZ = panelXYZDataModifiedDoubleHSV[vv][ss];
//                doubleModifiedXYZ.times(luminance);

//                System.out.println(targetXYZ + " " + hsvXYZ);
                DeltaE hsvDE = new DeltaE(targetXYZ, hsvXYZ, targetWhute);
                DeltaE modifiedDE = new DeltaE(targetXYZ, modifiedXYZ,
                                               targetWhute);
                DeltaE doubleModifiedDE = new DeltaE(targetXYZ,
                    doubleModifiedXYZ, targetWhute);
                if (vv > SKIP_PIECE) {
                  totalHSVDE += hsvDE.getCIE2000DeltaL();
                  totalModifiedDE += modifiedDE.getCIE2000DeltaL();
                  totalDoubleModifiedDE += doubleModifiedDE.getCIE2000DeltaL();

//                  totalHSVDE += hsvDE.getCIE2000DeltaE();
//                  totalModifiedDE += modifiedDE.getCIE2000DeltaE();
//                  totalDoubleModifiedDE += doubleModifiedDE.getCIE2000DeltaE();
                }
                else {
//                  System.out.println(hsvDE.getCIE2000DeltaE() + " " +
//                                     modifiedDE.getCIE2000DeltaE());
                }
              }
            }
            System.out.println("HSV dE: " + totalHSVDE / index);
            System.out.println("HSV' dE: " + totalModifiedDE / index);
            System.out.println("HSV\" dE: " +
                               totalDoubleModifiedDE / index);
            //==================================================================
          } //simple fit
          totalCurveIndex++;
          oneCaseCurveIndex++;
        }
        //======================================================================
        // single case curve
        //======================================================================

//        polyProcess(caseName, oneCaseCurves4Poly);
        interpolationProcess(caseName, oneCaseCurves4Interp, targetColorspace,
                             model,
                             hueSize, sArray, vArray);
//

//
        System.out.println("HSV RMSD: " + eachRMSDTotal / hueSize);
        System.out.println("HSV' RMSD: " +
                           eachModifiedRMSDTotal / hueSize);
        System.out.println("HSV\" RMSD: " +
                           eachModifiedDoubleRMSDTotal / hueSize);
        System.out.println("==================================================");
      } //color space
    } //target

//    polyProcess("Total", totalCurves);

    System.out.println("HSV RMSD: " + hsvRMSDTotal / totalHueCount);
    System.out.println("HSV' RMSD: " + hsvModifiedRMSDTotal / totalHueCount);
  }

  static void interpolationProcess(String title,
                                   double[][] oneCaseCurves4Interp,
                                   RGB.ColorSpace targetColorspace,
                                   LCDModel model, int hueSize, double[] sArray,
                                   double[] vArray) {

    //======================================================================
    // interpolation, 產生出對照表填的基礎值
    //======================================================================
    Plot2D testPlot = Plot2D.getInstance("Interpolation " + title);
    int index = 0;
    for (double[] curve : oneCaseCurves4Interp) {

      double hue = (index * HUE_STEP) % 360;
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, 100, 100});
      Color c = hsv.toRGB().getColor();

      double normal = 0;
      boolean negative = false;

      double[] copy = DoubleArray.getRangeCopy(curve, SKIP_PIECE + 1,
                                               curve.length - 1);
      if (Math.abs(Maths.max(copy)) > Math.abs(Maths.min(copy))) {
        normal = Maths.max(copy);
      }
      else {
        normal = Math.abs(Maths.min(copy));
        negative = true;
      }

      Maths.normalize(curve, normal);
      if (negative) {
        for (int x = 0; x < curve.length; x++) {
          curve[x] = -curve[x];
        }
      }
      testPlot.addLinePlot(Double.toString(hue), c, 0, 255, curve);

      index++;
    }

    double[][] transposeCurve = DoubleArray.transpose(oneCaseCurves4Interp);
    int length = transposeCurve.length;
    double[] finalCurve = new double[length];
    for (int x = 0; x < length; x++) {
      finalCurve[x] = Maths.mean(transposeCurve[x]);
    }
    testPlot.addLinePlot("Final", Color.black, 0, 255, finalCurve);
    if (plotting2DInterpolate) {
      testPlot.setVisible();
    }
    //======================================================================
    for (int lutgrid = 6; lutgrid <= 8; lutgrid += 2) {
      double totalRMSD = 0;

      for (int hue = 0; hue < END_HUE; hue += HUE_STEP) {

        double[][] griddata = new double[PIECE][PIECE];
        double[][] fitgriddata = null;
        initHueData(hue, targetColorspace, model, griddata, null, null);
        Minimisation min = new Minimisation();

        InterpolateLUT minfunc = new InterpolateLUT(griddata, finalCurve,
            lutgrid);

        min.nelderMead(minfunc, new double[] {1});
        double minRMSD = min.getMinimum();
//            System.out.println("LUT " + h + ": " + minRMSD + " " +
//                               min.getParamValues()[0]);
        fitgriddata = minfunc.fitgriddata;
        totalRMSD += minRMSD;

        if (plotting3DInterpolate) {
//          double hue = (h ) % 360;
//          System.out.println(h+" "+hue+" "+h * HUE_STEP);
          HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, 100, 100});
          Color c = hsv.toRGB().getColor();

          Plot3D plot = Plot3D.getInstance(Integer.toString(hue) + " LUT " +
                                           lutgrid, 800, 800);
          plot.addGridPlot(Integer.toString(hue) + "_org",
                           c,
                           sArray,
                           vArray, griddata);

          plot.addGridPlot(Integer.toString(hue) + "_lut",
                           Color.lightGray,
                           sArray,
                           vArray, fitgriddata);
          plot.setAxisLabels("Saturation", "Value");
          if (rotate2Axis) {
            plot.rotateToAxis(rotateAxis);
          }
          plot.setVisible();
          plot.setFixedBounds(0, 0, 100);
          plot.setFixedBounds(1, 0, 255);
          if (autoScaleAxis2) {
            plot.setFixedBounds(2, -40, 40);
          }
        }

      }
//


      System.out.println("LUT" + lutgrid + " RMSD: " + totalRMSD / hueSize);
    }

  }

  static void polyProcess(String title, double[][] totalCurves) {
    System.out.println("======================================================");
    System.out.println(title);
    System.out.println("======================================================");
    //==========================================================================
    // poly process
    //==========================================================================
    Plot2D curveplot = Plot2D.getInstance(title);
    int index = 0;
    int length = totalCurves.length;
    double[][] normalCurves = new double[length][];

    for (double[] curve : totalCurves) {
      double hue = (index * HUE_STEP) % 360;
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, 100, 100});
      Color c = hsv.toRGB().getColor();
      //未正規化的曲線
//      curveplot.addLinePlot(Integer.toString(index), c, 0, 255, curve);

      double max = Maths.max(curve);
      double min = Maths.min(curve);

      double maxDeltaValue = Math.max(Math.abs(max), Math.abs(min));
      normalCurves[index] = DoubleArray.copy(curve);
      Maths.normalize(normalCurves[index], maxDeltaValue);
      if (Math.abs(min) > Math.abs(max)) {
        normalCurves[index] = DoubleArray.times(normalCurves[index], -1); ;
      }
      curveplot.addLinePlot(Integer.toString(index), c, 0, 255,
                            normalCurves[index]);
      index++;
    }

    int normalCurveSize = normalCurves.length * normalCurves[0].length;
    double[] normalInput = new double[normalCurveSize];
    double[] normalOutput = new double[normalCurveSize];
    index = 0;
    for (double[] curve : totalCurves) {
      for (int vv = 0; vv <= (PIECE - 1); vv++) {
        double v = ( (double) vv) / (PIECE - 1) * 255;
        normalInput[index] = v;
        normalOutput[index] = curve[vv];
        index++;
      }
    }

    //Minimization
    Polynomial3_1D minfunc = new Polynomial3_1D(totalCurves);
    //    Polynomial3_1D minfunc = new Polynomial3_1D(normalInput, normalOutput);
    Minimisation min = new Minimisation();
    min.nelderMead(minfunc, new double[] {1, 1, 1});
    double[] params = min.getParamValues();

    //==========================================================================
    //SVD
    //==========================================================================
    PolynomialRegression regress = new PolynomialRegression(normalInput,
        normalOutput, Polynomial.COEF_1.BY_3C);
    regress.regress();
    double[] coefs = regress.getCoefs()[0];

    double[] predictCurve = new double[PIECE];
    double[] minimizationCurve = new double[PIECE];
    for (int vv = 0; vv <= (PIECE - 1); vv++) {
      double v = ( (double) vv) / (PIECE - 1) * 255;
      double predict = coefs[1] * v + coefs[2] * v * v + coefs[3] * v * v * v;
      predictCurve[vv] = predict;

      double minimization = params[0] * v + params[1] * v * v +
          params[2] * v * v * v;
      minimizationCurve[vv] = minimization;
    }
    double svdNormalMax = Maths.max(predictCurve);
    coefs = DoubleArray.getRangeCopy(coefs, 1, coefs.length - 1);
    coefs = DoubleArray.times(coefs, 1. / svdNormalMax);

    double miniNormalMax = Maths.max(minimizationCurve);
    params = DoubleArray.times(params, 1. / miniNormalMax);

    //==========================================================================


    for (int vv = 0; vv <= (PIECE - 1); vv++) {
      double v = ( (double) vv) / (PIECE - 1) * 255;
      double predict = coefs[0] * v + coefs[1] * v * v + coefs[2] * v * v * v;
      predictCurve[vv] = predict;
      double minimization = params[0] * v + params[1] * v * v +
          params[2] * v * v * v;
      minimizationCurve[vv] = minimization;
    }

    System.out.println("SVD: " + Polynomial.COEF_1.BY_3.toString(coefs));
    System.out.println("Optimize: " + DoubleArray.toString(params));
    System.out.println("SVD Normal: " + svdNormalMax);
    //==========================================================================
    // SVD RMSD
    //==========================================================================
//    for (int ss = 0; ss <= (PIECE - 1); ss++) {
//      double s = ( (double) ss) / (PIECE - 1) * 100;
//      for (int vv = 0; vv <= (PIECE - 1); vv++) {
//        double v = ( (double) vv) / (PIECE - 1) * 255;
//        double deltaV = getDeltaValue(x1, x2, x3, s, v);
//        fitgriddata[vv][ss] = deltaV;
//      }
//    }
////clean
//    clean(fitgriddata_);
//    fitgriddata_ = filterData(fitgriddata, fitgriddata_);
//    return Maths.RMSD(griddata_, fitgriddata_);
    //==========================================================================

    System.out.println("Optimize Normal: " + miniNormalMax);
    System.out.println("Optimize RMSD: " + min.getMinimum());
    Plot2D bestcurveplot = Plot2D.getInstance(title + " Fit");

    bestcurveplot.addLinePlot("SVD", Color.black, 0, 255, predictCurve);
    bestcurveplot.addLinePlot("Minimization", Color.magenta, 0, 255,
                              minimizationCurve);

    bestcurveplot.setVisible();
    bestcurveplot.setFixedBounds(0, 0, 255);
    bestcurveplot.setFixedBounds(1, 0, 1);
    curveplot.setVisible();
    curveplot.setFixedBounds(0, 0, 255);
    curveplot.setFixedBounds(1, 0, 1);
    //==========================================================================

    //poly要求出一個趨勢

  }

  static class Bezier3
      implements MinimisationFunction {
    private double[][] griddata;
    private double[][] fitgriddata;
    public Bezier3(double[][] griddata) {
      this.griddata = griddata;
      fitgriddata = new double[PIECE][PIECE];
    }

    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      double p1x = param[0];
      double p1y = param[1];
      double p2x = param[2];
      double p2y = param[3];

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1);
          double deltaV = bezier(v, p1x, p1y, p2x, p2y) * s / 100.;
          fitgriddata[vv][ss] = deltaV;
        }
      }
      return Maths.RMSD(griddata, fitgriddata);
    }

    private double bezier(double t, double p1x, double p1y, double p2x,
                          double p2y) {
      double x = 3 * p1x * Maths.sqr(1 - t) + 3 * p2x * t * t * (1 - t) +
          t * t * t;
      double y = 3 * p1y * Maths.sqr(1 - x) + 3 * p2y * x * x * (1 - x);
      return y;
    }

  }

  static class Bezier2
      implements MinimisationFunction {
    private double[][] griddata;
    private double[][] fitgriddata;
    private boolean modified = false;
    public Bezier2(double[][] griddata, boolean modified) {
      this.griddata = griddata;
      fitgriddata = new double[PIECE][PIECE];
      this.modified = modified;
    }

    public Bezier2(double[][] griddata) {
      this(griddata, false);
    }

    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      double p1x = param[0];
      double p1y = param[1];

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1);
          double deltaV = bezier(v, p1x, p1y) * s / 100.;
          fitgriddata[vv][ss] = deltaV;
        }
      }
      return Maths.RMSD(griddata, fitgriddata);
    }

    private double bezier(double t, double p1x, double p1y) {
      if (modified) {
        double x2 = (1 - t) * (t * (2 * p1x - 1) + 1);
        double y2 = 2 * x2 * (1 - x2) * p1y;
        return y2;
      }
      else {
        double x2 = 2 * t * (1 - t) * p1x + t * t;
        double y2 = 2 * x2 * (1 - x2) * p1y;
        return y2;
      }
    }

  }

  public static class ModifiedHSV
      extends Polynomial3_2D {
    private boolean doubleMax;
    public ModifiedHSV(double[][] griddata, boolean doubleMax) {
      super(griddata);
      this.doubleMax = doubleMax;
    }

    /**
     *
     * @param offset double
     * @param s double
     * @param v double
     * @param doubleMax boolean
     * @return double
     */
    public static double getDeltaValue(double offset, double s, double v,
                                       boolean doubleMax) {
      double chroma = s * v / 100.;
//      double t = chroma * (255 - v) * v;
      double deltaV = offset * chroma * (255 - v) * v / 128 / 128 / 128;
      if (doubleMax) {
        deltaV = deltaV * v / 256;
      }
      return deltaV;
    }

    public double function(double[] param) {
      double offset = param[0];

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1) * 255;
          double deltaV = getDeltaValue(offset, s, v, doubleMax);
          fitgriddata[vv][ss] = deltaV;
        }
      }
      //clean
      clean(fitgriddata_);
      fitgriddata_ = filterData(fitgriddata, fitgriddata_);
      return Maths.RMSD(griddata_, fitgriddata_);
    }

  }

  static class Polynomial3_2D
      implements MinimisationFunction {
    protected double[][] griddata;
    protected double[][] fitgriddata;
    protected double[][] griddata_;
    protected double[][] fitgriddata_;

    public Polynomial3_2D(double[][] griddata) {
      this.griddata = griddata;
      int sSize = griddata.length;
      int vSize = griddata[0].length - SKIP_PIECE;

      griddata_ = filterData(this.griddata, new double[vSize][sSize]);
      fitgriddata = new double[PIECE][PIECE];
      fitgriddata_ = new double[vSize][sSize];
    }

    public static double getDeltaValue(double x1, double x2, double x3,
                                       double s, double v) {
      double deltaV = (x1 * v + x2 * v * v + x3 * v * v * v) * s / 100.;
      return deltaV;
    }

    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      double x1 = param[0];
      double x2 = param[1];
      double x3 = param[2];

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1) * 255;
          double deltaV = getDeltaValue(x1, x2, x3, s, v);
          fitgriddata[vv][ss] = deltaV;
        }
      }
      //clean
      clean(fitgriddata_);
      fitgriddata_ = filterData(fitgriddata, fitgriddata_);
      return Maths.RMSD(griddata_, fitgriddata_);
    }

  }

  static class InterpolateLUT
      implements MinimisationFunction {
//    private double[] sourceData;
    Interpolation1DLUT lut;
    protected double[][] griddata;
    protected double[][] fitgriddata;
    protected double[][] griddata_;
    protected double[][] fitgriddata_;

    public InterpolateLUT(double[][] griddata, double[] sourceData, int piece) {
      this.griddata = griddata;
      int sSize = griddata.length;
      int vSize = griddata[0].length - SKIP_PIECE;

      griddata_ = filterData(this.griddata, new double[vSize][sSize]);
      fitgriddata = new double[PIECE][PIECE];
      fitgriddata_ = new double[vSize][sSize];

//      this.sourceData = sourceData;
      int length = sourceData.length;
      double[] input = new double[length];
      for (int x = 0; x < length; x++) {
        double v = ( (double) x) / (length - 1) * 255;
        input[x] = v;
      }
      Interpolation1DLUT tmplut = new Interpolation1DLUT(input, sourceData);
      double[] pieceIn = new double[piece];
      double[] pieceOut = new double[piece];
      for (int x = 0; x < piece; x++) {
        double v = ( (double) x) / (piece - 1) * 255;
        pieceIn[x] = v;
        pieceOut[x] = tmplut.getValue(v);
      }
      lut = new Interpolation1DLUT(pieceIn, pieceOut,
                                   Interpolation1DLUT.Algo.LINEAR);
    }

    public double function(double[] param) {
      double offset = param[0];

      for (int ss = 0; ss <= (PIECE - 1); ss++) {
        double s = ( (double) ss) / (PIECE - 1) * 100;
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1) * 255;
//          double chroma = s * v / 100.;
//          double deltaV = offset * chroma * chroma * (255 - v) *
//              (1.15625 - s / 100.) * 4 / 128 / 128 / 128;
          double deltaV = getDeltaValue(offset, s, v);
          fitgriddata[vv][ss] = deltaV;
        }
      }
      //clean
      clean(fitgriddata_);
      fitgriddata_ = filterData(fitgriddata, fitgriddata_);
      return Maths.RMSD(griddata_, fitgriddata_);
    }

    public double getDeltaValue(double offset, double s, double v) {
      double deltaValue = lut.getValue(v);
      double chroma = s * v / 100.;
      deltaValue = offset * chroma * deltaValue / 128;
      return deltaValue;
    }

  }

  static class Polynomial3_1D
      implements MinimisationFunction {
    protected double[][] griddata;
    protected double[][] fitgriddata;
//    protected double[][] griddata_;
//    protected double[][] fitgriddata_;

//    protected static double[][] filterData(double[][] data,
//                                           double[][] filterData) {
//      int length = data.length;
//
//      for(int x=0;x<length;x++) {
//
//      }
//      for (int ss = 0; ss <= (PIECE - 1); ss++) {
//        for (int vv = SKIP_PIECE + 1; vv <= (PIECE - 1); vv++) {
//          filterData[vv - SKIP_PIECE][ss] = data[vv][ss];
//        }
//      }
//      return filterData;
//    }

    public Polynomial3_1D(double[][] griddata) {
      this.griddata = griddata;
      fitgriddata = new double[griddata.length][griddata[0].length];
    }

    public static double getDeltaValue(double x1, double x2, double x3,
                                       double v) {
      double deltaV = (x1 * v + x2 * v * v + x3 * v * v * v);
      return deltaV;
    }

    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      double x1 = param[0];
      double x2 = param[1];
      double x3 = param[2];

      for (int index = 0; index < fitgriddata.length; index++) {
        for (int vv = 0; vv <= (PIECE - 1); vv++) {
          double v = ( (double) vv) / (PIECE - 1) * 255;
          double deltaV = getDeltaValue(x1, x2, x3, v);
          fitgriddata[index][vv] = deltaV;
        }
      }
      //clean
      return Maths.RMSD(griddata, fitgriddata);
    }

  }

  private static void clean(double[][] data) {
    for (int x = 0; x < data.length; x++) {
      for (int y = 0; y < data[0].length; y++) {
        data[x][y] = 0;
      }
    }
  }

  private static double[][] filterData(double[][] data,
                                       double[][] filterData) {
    for (int ss = 0; ss <= (PIECE - 1); ss++) {
      for (int vv = SKIP_PIECE + 1; vv <= (PIECE - 1); vv++) {
        filterData[vv - SKIP_PIECE][ss] = data[vv][ss];
      }
    }
    return filterData;
  }

}
