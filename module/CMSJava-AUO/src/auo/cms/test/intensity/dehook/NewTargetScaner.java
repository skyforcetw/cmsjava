package auo.cms.test.intensity.dehook;

import shu.cms.lcd.LCDTarget;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import java.io.FileNotFoundException;
import java.util.List;
import shu.cms.colorspace.independ.CIEXYZ;
import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;
import shu.cms.measure.intensity.Component;
import shu.cms.measure.intensity.ComponentFetcher;
import java.util.Collections;
import shu.cms.colorspace.independ.CIExyY;
import java.util.ArrayList;
import shu.math.Interpolation;
import shu.cms.plot.Plot2D;
import shu.cms.plot.PlotUtils;
import java.awt.Color;
import shu.math.lut.Interpolation1DLUT;
import shu.math.Maths;
import shu.math.array.*;
import shu.math.GammaFinder;

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
public class NewTargetScaner {

  static List<CIEXYZ> getXYZList(List<RGB> rgbList, MultiMatrixModel model) {
    int size = rgbList.size();
    List<CIEXYZ> XYZList = new ArrayList<CIEXYZ> (size);
    for (RGB rgb : rgbList) {
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      XYZList.add(XYZ);
    }
    return XYZList;
  }

  static boolean isAscend(List<RGB> rgbList) {
    int size = rgbList.size();
    for (int x = 50; x < size; x++) {
      RGB rgb0 = rgbList.get(x - 1);
      RGB rgb1 = rgbList.get(x);
      if (rgb0.R >= rgb1.R || rgb0.G >= rgb1.G || rgb0.B >= rgb1.B) {
        return false;
      }

    }
    return true;
  }

  static double getAccumulateB(List<RGB> rgbList) {
    int size = rgbList.size();
    double accumulateB = 0;
    for (int x = 50; x < size; x++) {
      RGB rgb0 = rgbList.get(x);
      accumulateB += rgb0.B;
    }
    return accumulateB;
  }

  static double findBMapDG(List<RGB> rgbList, int b) {
    int size = rgbList.size();
    double[][] keyValues = new double[2][size];

    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      keyValues[0][x] = x;
      keyValues[1][x] = rgb.B;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(keyValues[0], keyValues[1],
        Interpolation1DLUT.Algo.LINEAR);
    return lut.getKey(b);
  }

  public static void main(String[] args) throws FileNotFoundException {
//  main0(args);
    main1(args);
  }

  public static void main1(String[] args) throws FileNotFoundException {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement02.xls");
    LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);
    LCDTarget.Operator.gradationReverseFix(lcdtarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdtarget);
    model.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode2);
    model.setMaxValue(RGB.MaxValue.Double255);
    model.produceFactor();

    CIEXYZ whiteXYZ = model.getXYZ(new RGB(255, 255, 255), false);
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    DGTester.Quantization = true;

    List<CIEXYZ> targetXYZList0 = DGTester.getTargetXYZList(whiteXYZ);
    List<RGB> rgbList0 = getRGBList(targetXYZList0, model, false, 0, false);
    int maxBIntensityIndex = getMaxBIntensityIndex(FirstComponentList);
    System.out.println(maxBIntensityIndex);
    System.out.println(rgbList0.get(253).B + " " + rgbList0.get(254).B + " " +
                       rgbList0.get(255).B);
    System.out.println("");
    double step = 1 / 8.;

    Plot2D rgplot = Plot2D.getInstance("RG");
    int rgCount = 0;
    Plot2D plot = Plot2D.getInstance("Chromaticity Diagram");
    Plot2D plot2 = Plot2D.getInstance("dxy vs B Map DG");
    Plot2D Yplot = Plot2D.getInstance("Luminance");
    Plot2D gammaplot = Plot2D.getInstance("near gamma");
    Plot2D gammaplot2 = Plot2D.getInstance("multi pos gamma");

    double targetGamma = 2.2;
    double gammaDWLimit = 1.5;
    double gammaUPLimit = 3;

    List<RGB> rgbList = new ArrayList<RGB> ();

    for (double g = maxBIntensityIndex + step; g < 255; g += step) {
      for (double r = g + step; r < 255; r += step) {

        RGB rgb = new RGB(r, g, maxBIntensityIndex);

        rgCount++;
        rgplot.addScatterPlot("", Color.black, r, g);
        CIEXYZ XYZ = model.getXYZ(rgb, false);
        CIExyY xyY = new CIExyY(XYZ);
        double[] dxy = xyY.getDeltaxy(whitexyY);
        DoubleArray.abs(dxy);
        if (dxy[0] <= 0.003 && dxy[1] <= 0.003) {

          double normalY = xyY.Y / whitexyY.Y;
          //10^log(n)/2.2 = normal
          double n = GammaFinder.findNormalInput(normalY, targetGamma);

          double mapdg = n * 255.;
          plot.addScatterPlot(rgb.toString() + "_" + mapdg, Color.black, xyY.x,
                              xyY.y);
          double gamma = GammaFinder.findGamma(Math.round(mapdg) / 255.,
                                               normalY);
          double rStep = (255 - rgb.R) / step;
          double gStep = (255 - rgb.G) / step;
          double minStep = Math.min(rStep, gStep);
//          if (minStep < 0) {
//            System.out.println(rgb);
//          }

          System.out.println(rgb + " dxy:" + DoubleArray.toString(dxy) + " Y:" +
                             xyY.Y / whitexyY.Y + " DG:" + mapdg + " r:" +
                             gamma + " R:" + rStep + " G:" + gStep);

          double dist = Math.sqrt(dxy[0] * dxy[0] + dxy[1] * dxy[1]);
          plot2.addScatterPlot("a", Color.black, dist, mapdg);

          Yplot.addScatterPlot("", Color.black, mapdg, normalY);

          if (!Double.isInfinite(gamma)) {
            gammaplot.addScatterPlot("a", Color.black, Math.round(mapdg), gamma);
            rgbList.add(rgb);
          }
          for (int dg = 251; dg <= 254; dg++) {
            double gamma2 = Math.log(normalY) / Math.log(dg / 255.);
            if (!Double.isInfinite(gamma2)) {
              gammaplot2.addScatterPlot("", Color.black, dg,
                                        gamma2);
            }

          }
        }
      }
    }
    plot.addScatterPlot("center", Color.red, whitexyY.x, whitexyY.y);
    plot.setAxisLabels("CIEx", "CIEy");
    plot.setVisible();
    PlotUtils.setAUOFormat(plot);
    plot2.setAxisLabels("Dist", "B Map DG");
    plot2.setVisible();
    PlotUtils.setAUOFormat(plot2);
    gammaplot.setAxisLabels("B Map DG", "Gamma");
    gammaplot.setVisible();
    PlotUtils.setAUOFormat(gammaplot);
    gammaplot2.setAxisLabels("B Map DG", "Gamma");
    gammaplot2.setVisible();
    PlotUtils.setAUOFormat(gammaplot2);

    rgplot.setVisible();
    rgplot.setAxisLabels("R", "G");
    PlotUtils.setAUOFormat(rgplot);
    for (double n = 253; n <= 255; n += 0.0625) {
      double normal = n / 255.;
      double v1 = Math.pow(normal, 2.22);
      double v2 = Math.pow(normal, 2.18);
      Yplot.addCacheScatterLinePlot("2.22", Color.red, n, v1);
      Yplot.addCacheScatterLinePlot("2.18", Color.green, n, v2);
    }
    Yplot.setVisible();
    Yplot.setAxisLabels("B in CCTLUT", "Normalized Y");
    PlotUtils.setAUOFormat(Yplot);

    System.out.println("total: " + rgbList.size());
    System.out.println("rg count: " + rgCount);
    Plot2D sidePlot = Plot2D.getInstance("side plot");
    sidePlot.addScatterPlot("255", Color.black, whitexyY.x, whitexyY.y);

//    for (RGB rgb : rgbList) {
//      for (int n = 251; n < 254; n++) {
//
//      }
//    }
    int totalcount = 0;
    int totalcountNoFilter = 0;
    int usableMotherCount = 0;
    Plot2D mapgammaplot = Plot2D.getInstance("map gamma");
    for (RGB rgb : rgbList) {

      CIEXYZ XYZ0 = model.getXYZ(rgb, false);
      CIExyY xyY0 = new CIExyY(XYZ0);
      double[] dxy0 = xyY0.getDeltaxy(whitexyY);

      double normalY0 = xyY0.Y / whitexyY.Y;
      double gamma0 = GammaFinder.findGamma(253. / 255., normalY0);

      double dgamma0 = gamma0 - 2.2;
      if (gamma0 < gammaDWLimit || gamma0 > gammaUPLimit) {
        continue;
      }

      mapgammaplot.addCacheScatterPlot("253", Color.red, 253, gamma0);

      System.out.print("===" + rgb + "===");

      int count = 0;
      int countNoFilter = 0;
      for (double newb = rgb.B + step; newb < 255; newb += step) {
        for (double newg = rgb.G + step; newg < 255 && newg > newb;
             newg += step) {
          for (double newr = rgb.R + step; newr < 255 && newr > newg;
               newr += step) {
            RGB newrgb = new RGB(newr, newg, newb);
            CIEXYZ XYZ = model.getXYZ(newrgb, false);
            CIExyY xyY = new CIExyY(XYZ);
            double[] dxy = xyY.getDeltaxy(whitexyY);
            DoubleArray.abs(dxy);
            if (dxy[0] <= 0.003 && dxy[1] <= 0.003) {

              double normalY = xyY.Y / whitexyY.Y;
              double gamma = GammaFinder.findGamma(254. / 255.,
                  normalY);
              double dgamma = gamma - 2.2;
              countNoFilter++;
              if (gamma >= gammaDWLimit && gamma <= gammaUPLimit &&
                  dgamma * dgamma0 > 0 /*&& gamma <= gamma0*/) {
//                sidePlot.addScatterPlot("254", Color.green, xyY.x, xyY.y);
//                mapgammaplot.addCacheScatterPlot("254", Color.green, 254, gamma);
                count++;
              }
              else {
//                sidePlot.addScatterPlot("XXX", Color.blue, xyY.x, xyY.y);
              }

            }

          }
        }
      }
      System.out.println(count);
      if (count != 0) {
        sidePlot.addScatterPlot("253", Color.red, xyY0.x, xyY0.y);
        usableMotherCount++;

        RGB middleRGB = new RGB(RGB.ColorSpace.unknowRGB,
                                RGB.MaxValue.Double255);
        middleRGB.R = Interpolation.linear(253, 255, rgb.R, 255, 254);
        middleRGB.G = Interpolation.linear(253, 255, rgb.G, 255, 254);
        middleRGB.B = Interpolation.linear(253, 255, rgb.B, 255, 254);
        CIEXYZ XYZm = model.getXYZ(middleRGB, false);
        CIExyY xyYm = new CIExyY(XYZm);
        sidePlot.addScatterPlot("middle", Color.blue, xyYm.x, xyYm.y);
        double gammam = GammaFinder.findGamma(254. / 255., xyYm.Y / whitexyY.Y);
        mapgammaplot.addCacheScatterPlot("middle", Color.blue, 254, gammam);

      }

      totalcount += count;
      totalcountNoFilter += countNoFilter;

    }
    sidePlot.setVisible();
    sidePlot.setAxisLabels("CIEx", "CIEy");
    mapgammaplot.setVisible();
    mapgammaplot.setAxisLabels("Gray Level", "Gamma");
    System.out.println("usable mother count: " + usableMotherCount);
    System.out.println("total sun count: " + totalcount);
    System.out.println("totalcountNoFilter: " + totalcountNoFilter);
  }

  static List<RGB> getRGBList() {
    return null;
  }

  public static void main0(String[] args) throws FileNotFoundException {

    AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement02.xls");
    LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);
    LCDTarget.Operator.gradationReverseFix(lcdtarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdtarget);
    model.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode2);
    model.setMaxValue(RGB.MaxValue.Double255);
    model.produceFactor();

    CIEXYZ whiteXYZ = model.getXYZ(new RGB(255, 255, 255), false);
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    DGTester.Quantization = true;

    List<CIEXYZ> targetXYZList0 = DGTester.getTargetXYZList(whiteXYZ);
    List<RGB> rgbList0 = getRGBList(targetXYZList0, model, false, 0, true);
    int maxBIntensityIndex = getMaxBIntensityIndex(FirstComponentList);
    System.out.println(maxBIntensityIndex);
    System.out.println(rgbList0.get(253).B + " " + rgbList0.get(254).B + " " +
                       rgbList0.get(255).B);
    /**
     * no multigen@254 244.75
     */

    final boolean T = true;
    final boolean F = false;
    boolean showTargetxy = F;
    boolean showRGBList = F;
    boolean showtpPlot = F;
    boolean showSimxy = F;
    boolean showSimY = F;
    int multigen = 0;
    double offset = 0.000;

    for (double dx = -offset; dx <= offset; dx += 0.001) {
      for (double dy = -offset; dy <= offset; dy += 0.001) {
        //=================================================================
//        if (! (dx <= 0 && dy >= 0 || dx >= 0 && dy >= 0)) { //-+ and ++ ok
//         if (dx != 0.003 || dy != 0.003) {
//          continue;
//        }
        //=================================================================

        String dxdytitle = (dx) + " " + (dy);
        Plot2D tpplot = showtpPlot ? Plot2D.getInstance(dxdytitle) : null;
        //=================================================================
//        for (int tp = 250; tp <= 254; tp++) {
        for (int tp = 254; tp <= 254; tp++) {
          //=================================================================
          double targetx = whitexyY.x + dx;
          double targety = whitexyY.y + dy;
          CIExyY middlexyY = new CIExyY(targetx, targety, 1);

          //=================================================================
          for (int toppos = 254; toppos < 255; toppos++) {
//          for (int toppos = tp + 1; toppos < 255; toppos++) {
            //=================================================================

            //=================================================================
            List<CIEXYZ>
//                targetXYZList = getTargetXYZList(whitexyY, middlexyY, tp,
//                                                 toppos);
                targetXYZList = getTargetXYZList(whitexyY, middlexyY, 254);
            targetXYZList = targetXYZList0;
            //=================================================================
//            for (int y = 0; y < targetXYZList.size(); y++) {
//              System.out.println(targetXYZList.get(y) + "\n" +
//                                 targetXYZList0.get(y));
//            }

            String title = (dx) + " " + (dy) + " (tp)" + tp + " (toppos)" +
                toppos;

            List<RGB>
                rgbList = getRGBList(targetXYZList, model, false, multigen, true);
            RGB lastRGB = rgbList.get(254);
            RGB finalRGB = DeHook3Tester.originalRGB255;
            //=================================================================
            if (!isAscend(rgbList)) {
//            if (!isAscend(rgbList) || lastRGB.B <= 250 || !finalRGB.isWhite()) {
              //=================================================================
//              continue;
            }

//            System.out.println(title + ": (last)" + lastRGB.B + " (255)" +
//                               DeHook3Tester.originalRGB255 + " accuB:" +
//                               getAccumulateB(rgbList));
            System.out.println(title + " " +
                               findBMapDG(rgbList, maxBIntensityIndex) + " " +
                               rgbList.get(254).B);

            if (showTargetxy) {
              Plot2D plot = Plot2D.getInstance(title);
              int x = 0;
              for (CIEXYZ XYZ : targetXYZList) {
                CIExyY xyY = new CIExyY(XYZ);
                plot.addCacheScatterLinePlot("x", x, xyY.x);
                plot.addCacheScatterLinePlot("y", x, xyY.y);
                x++;
              }
              plot.addLegend();
              plot.setVisible();
              plot.setFixedBounds(1, .27, .285);
              plot.setFixedBounds(0, 0, 255);
              plot.setAxisLabels("Gray Level", "CIExy");

              PlotUtils.setAUOFormat(plot);
            }

            if (showRGBList) {
              Plot2D plot = Plot2D.getInstance(title);
              for (int x = 240; x < 256; x++) {
                RGB rgb = rgbList.get(x);
                plot.addCacheScatterLinePlot(x, rgb.getValues());
              }
              plot.setVisible();
              plot.setFixedBounds(0, 240, 255);
              plot.setFixedBounds(1, 220, 255);
            }

            if (showtpPlot) {
              tpplot.addCacheScatterLinePlot("", tp, rgbList.get(254).B);
            }
            List<CIEXYZ> XYZList = getXYZList(rgbList, model);
            if (showSimxy) {
              Plot2D plot = Plot2D.getInstance(title + " xy");
              for (int x = 240; x < 256; x++) {
                CIEXYZ XYZ = XYZList.get(x);
                CIExyY xyY = new CIExyY(XYZ);
                plot.addCacheScatterLinePlot("x", x, xyY.x);
                plot.addCacheScatterLinePlot("y", x, xyY.y);
              }
              plot.setVisible();
              plot.setFixedBounds(0, 240, 255);
              plot.setFixedBounds(1, .27, .279);
            }
            if (showSimY) {
              Plot2D plot = Plot2D.getInstance(title + " Y");
              for (int x = 240; x < 255; x++) {
                CIEXYZ XYZ = XYZList.get(x);
//              System.out.println(XYZ);
                double normal = x / 255.;
//              System.out.println(XYZ.Y / whiteXYZ.Y);
                double gamma = Math.log(XYZ.Y / whiteXYZ.Y) / Math.log(normal);
//              CIExyY xyY = new CIExyY(XYZ);
                plot.addCacheScatterLinePlot("r", x, gamma);
//              System.out.println(gamma);
//              plot.addCacheScatterLinePlot("y", x, xyY.y);
              }
              plot.addLinePlot("2.18", Color.red, 240, 2.18, 255, 2.18);
              plot.addLinePlot("2.22", Color.red, 240, 2.22, 255, 2.22);
              plot.setVisible();
              plot.setFixedBounds(0, 240, 255);
              plot.setFixedBounds(1, 1.8, 2.22);
            }
          }

        }
        if (showtpPlot) {
          tpplot.setVisible();
          tpplot.setFixedBounds(0, 249, 254);
//          tpplot.setFixedBounds(1, 247, 252);
        }

      }
    }
  }

  static List<CIEXYZ> getTargetXYZList(CIExyY white, CIExyY topWhite,
                                       int turnPoint, int toppos) {
    List<CIEXYZ> targetXYZList = new ArrayList<CIEXYZ> ();

    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double gamma = Math.pow(normal, 2.2);
      double luminance = gamma * white.Y;
      if (x >= turnPoint && x < toppos) {
        double targetx = Interpolation.linear(turnPoint, toppos, white.x,
                                              topWhite.x, x);
        double targety = Interpolation.linear(turnPoint, toppos, white.y,
                                              topWhite.y, x);
        CIExyY targetxyY = new CIExyY(targetx, targety, luminance);
        targetXYZList.add(targetxyY.toXYZ());
      }
      else if (x >= toppos) {
        double targetx = Interpolation.linear(toppos, 255, topWhite.x,
                                              white.x, x);
        double targety = Interpolation.linear(toppos, 255, topWhite.y,
                                              white.y, x);
        CIExyY targetxyY = new CIExyY(targetx, targety, luminance);
        targetXYZList.add(targetxyY.toXYZ());
      }
      else {
        CIExyY targetxyY = (CIExyY) white.clone();
        targetxyY.Y = luminance;
        targetXYZList.add(targetxyY.toXYZ());
      }
    }

    return targetXYZList;
  }

  static List<CIEXYZ> getTargetXYZList(CIExyY white, CIExyY middleWhite,
                                       int turnPoint) {
    List<CIEXYZ> targetXYZList = new ArrayList<CIEXYZ> ();
    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double gamma = Math.pow(normal, 2.2);
      double luminance = gamma * white.Y;
      if (x < turnPoint) {
        CIExyY targetxyY = (CIExyY) middleWhite.clone();
        targetxyY.Y = luminance;
        targetXYZList.add(targetxyY.toXYZ());
      }
      else {
        double targetx = Interpolation.linear(turnPoint, 255, middleWhite.x,
                                              white.x, x);
        double targety = Interpolation.linear(turnPoint, 255, middleWhite.y,
                                              white.y, x);
        CIExyY targetxyY = new CIExyY(targetx, targety, luminance);
        targetXYZList.add(targetxyY.toXYZ());
      }
    }

    return targetXYZList;
  }

  static List<Component> FirstComponentList;
  static int getMaxBIntensityIndex(List<Component> componentList) {
    int size = componentList.size();
    for (int x = 0; x < size - 1; x++) {
      Component c0 = componentList.get(x);
      Component c1 = componentList.get(x + 1);
      if (c0.intensity.B > c1.intensity.B) {
        return size - x - 1;
      }
    }
    return -1;
  }

  static List<RGB> getRGBList(List<CIEXYZ> targetXYZList,
      MultiMatrixModel model, boolean drawGamma,
      int multigen, boolean fixFirstWhiteRGB) {

    CIEXYZ rXYZ = model.getXYZ(new RGB(255, 0, 0), false);
    CIEXYZ gXYZ = model.getXYZ(new RGB(0, 255, 0), false);
    CIEXYZ bXYZ = model.getXYZ(new RGB(0, 0, 255), false);
    CIEXYZ whiteXYZ = model.getXYZ(new RGB(255, 255, 255), false);
    MaxMatrixIntensityAnalyzer analyzer = MaxMatrixIntensityAnalyzer.
        getReadyAnalyzer(rXYZ, gXYZ, bXYZ, whiteXYZ);

    List<CIEXYZ> wXYZList = DeHook3Tester.getWXYZList(model);
    Collections.reverse(wXYZList); //逆

    ComponentFetcher fetcher = new ComponentFetcher(analyzer);
    List<Component> componentList = fetcher.fetchComponent(wXYZList);
    FirstComponentList = componentList;

    List<RGB>
        rgbList = DGTester.getRGBList(componentList, analyzer, targetXYZList,
                                      "DG0", false);
//    List<RGB> rgbList = rgbList0;
    List<RGB> result = rgbList;
    RGB whiteRGB = rgbList.get(rgbList.size() - 1);
//    System.out.println(whiteRGB + " " + model.getXYZ(whiteRGB, false) + " " +
//                       whiteXYZ);
//    DeHook3Tester.drawGamma(rgbList, model, "r0");
    if (fixFirstWhiteRGB) { //如果multi-gen + native white, 有開比較好
      whiteRGB.R = whiteRGB.G = whiteRGB.B = 255; //244.75 in B100
      if (drawGamma) {
        DeHook3Tester.drawGamma(rgbList, model, "r1");
      }
    }

    if (true && multigen > 0) {
      List<RGB>
          rgbList2 = DeHook3Tester.multiGen(multigen, rgbList, model, analyzer,
                                            whiteXYZ, true);

      result = rgbList2;
      RGB whiteRGB2 = rgbList2.get(rgbList2.size() - 1);
      RGB whiteRGB0 = DeHook3Tester.originalRGB255;
//      System.out.println(rgbList2.get(rgbList2.size() - 2) + " " + whiteRGB2);
//      drawGamma(rgbList2, model, "r3");
//      whiteRGB2.R = whiteRGB2.G = whiteRGB2.B = 255;
      if (drawGamma) {
        DeHook3Tester.drawGamma(rgbList2, model, "r4");
      }
    }

    return result;
  }
}
