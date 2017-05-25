package auo.cms.test.intensity.dehook;

import java.io.*;
import java.util.*;

import shu.cms.colorformat.adapter.xls.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.measure.intensity.*;
import shu.cms.plot.*;

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
public class DeHook3Tester {
  static int findMaxBIntensityIndex(List<Component> componentList) {
    int size = componentList.size();
    Component prec = null;
    for (int x = 0; x < size; x++) {
      int index = size - x;
      Component c = componentList.get(x);
      if (null != prec && c.intensity.B < prec.intensity.B) {
        return index;
      }
      prec = c;
    }
    return -1;

  }

  static void drawGamma(List<RGB> rgbList, MultiMatrixModel model, String title) {
    Plot2D plot = Plot2D.getInstance(title);
    int size = rgbList.size();
    RGB whiteRGB = rgbList.get(size - 1);
    CIEXYZ whiteXYZ = model.getXYZ(whiteRGB, false);
    for (int x = 1; x < size - 1; x++) {
      RGB rgb = rgbList.get(x);
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      double normal = x / (size - 1.);
      double gamma = Math.log(XYZ.Y / whiteXYZ.Y) / Math.log(normal);
      plot.addCacheScatterLinePlot("Gamma", x, gamma);
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 2.18, 2.22);
  }

  static List<Component> getComponentList(MultiMatrixModel model, List<RGB>
      rgbList) {
    int size = rgbList.size();
    List<Component> componentList = new ArrayList<Component> (size);
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      Component c = new Component(rgb, null, XYZ);
      componentList.add(c);
    }
    return componentList;
  }

  public static RGB originalRGB255 = null;
  static List<RGB> multiGen(int n, List<RGB> base, MultiMatrixModel model,
      MaxMatrixIntensityAnalyzer analyzer, CIEXYZ whiteXYZ,
      boolean useWhiteXYZ2) {
    List<RGB> result = base;

    for (int x = 0; x < n; x++) {
      Collections.reverse(result);
      List<Component> componentList2 = getComponentList(model, result);
      Component whitec = componentList2.get(0);
      CIEXYZ whiteXYZ2 = (CIEXYZ) whiteXYZ.clone();
      whiteXYZ2.scaleY(whitec.XYZ.Y);

      List<CIEXYZ>
          targetXYZList = DGTester.getTargetXYZList(whiteXYZ, whiteXYZ2, 2.2,
          256, useWhiteXYZ2);
      result = DGTester.getRGBList(componentList2, analyzer, targetXYZList,
                                   "DG" + Integer.toString(x + 1), false);
      RGB lastRGB = result.get(result.size() - 1);
      originalRGB255 = (RGB) lastRGB.clone();
      lastRGB.R = lastRGB.G = lastRGB.B = 255;
    }
    return result;
  }

  public static void main(String[] args) throws FileNotFoundException {
//    main1(args);
    main2(args);
  }

  public static void main2(String[] args) throws FileNotFoundException {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement02.xls");
    LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);
    LCDTarget.Operator.gradationReverseFix(lcdtarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdtarget);
    model.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode2);
    model.setMaxValue(RGB.MaxValue.Double255);
    model.produceFactor();

  }

  static List<CIEXYZ> getWXYZList(MultiMatrixModel model) {
    List<CIEXYZ> XYZList = new ArrayList<CIEXYZ> ();
    for (int x = 0; x < 256; x++) {
      CIEXYZ XYZ = model.getXYZ(new RGB(x, x, x), false);
      XYZList.add(XYZ);
    }
    return XYZList;
  }

  public static void main1(String[] args) throws FileNotFoundException {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement02.xls");
    LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);
    LCDTarget.Operator.gradationReverseFix(lcdtarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdtarget);
    model.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode2);
    model.setMaxValue(RGB.MaxValue.Double255);
    model.produceFactor();
//    System.out.println(model.getXYZ(new RGB(254.375, 254.625, 244.0), false));
//    System.out.println(model.getXYZ(new RGB(254.375, 254.375, 247.0), false));

    int targetB = 255;
    int targetRG = 255;
    CIEXYZ rXYZ = model.getXYZ(new RGB(targetRG, 0, 0), false);
    CIEXYZ gXYZ = model.getXYZ(new RGB(0, targetRG, 0), false);
    CIEXYZ bXYZ = model.getXYZ(new RGB(0, 0, targetB), false);

//    List<CIEXYZ> wXYZList = adapter.getXYZList().subList(0, targetRG + 1); //順
    List<CIEXYZ> wXYZList = getWXYZList(model);
    CIEXYZ whiteXYZ = model.getXYZ(new RGB(targetRG, targetRG, targetB), false);
//    CIEXYZ whiteXYZ = (CIEXYZ) wXYZList.get(255).clone();
//    CIEXYZ whiteXYZ0 = model.getXYZ(RGB.White, false);

    Collections.reverse(wXYZList); //逆

    MaxMatrixIntensityAnalyzer analyzer = MaxMatrixIntensityAnalyzer.
        getReadyAnalyzer(rXYZ, gXYZ, bXYZ, whiteXYZ);
    ComponentFetcher fetcher = new ComponentFetcher(analyzer);
    List<Component> componentList = fetcher.fetchComponent(wXYZList);

    List<CIEXYZ> targetXYZList = DGTester.getTargetXYZList(whiteXYZ);
    DGTester.Quantization = true;
    List<RGB>
        rgbList0 = DGTester.getRGBList(componentList, analyzer, targetXYZList,
                                       "DG0", false);
    List<RGB> rgbList = rgbList0;
    RGB whiteRGB = rgbList.get(rgbList.size() - 1);
    System.out.println(whiteRGB + " " + model.getXYZ(whiteRGB, false) + " " +
                       whiteXYZ);
    drawGamma(rgbList, model, "r0");
    if (true) { //如果multi-gen + native white, 有開比較好
      whiteRGB.R = whiteRGB.G = whiteRGB.B = 255;
      drawGamma(rgbList, model, "r1");
    }
//    showRGBList(rgbList);
    if (true) {
      List<RGB> rgbList2 = multiGen(1, rgbList, model, analyzer, whiteXYZ, true);
      RGB whiteRGB2 = rgbList2.get(rgbList2.size() - 1);
      System.out.println(rgbList2.get(rgbList2.size() - 2) + " " + whiteRGB2);
//      drawGamma(rgbList2, model, "r3");
      whiteRGB2.R = whiteRGB2.G = whiteRGB2.B = 255;
      drawGamma(rgbList2, model, "r4");
//      showRGBList(rgbList2);
    }

  }

  static void showRGBList(List<RGB> rgbList) {
    Plot2D plot = Plot2D.getInstance();
    for (int x = 240; x < 256; x++) {
      RGB rgb = rgbList.get(x);
      plot.addCacheScatterLinePlot(x, rgb.getValues());
    }
    plot.setVisible();
    plot.setFixedBounds(0, 240, 255);
    plot.setFixedBounds(1, 230, 255);
    plot.setAxisLabels("Gray Level", "DG LUT");
    PlotUtils.setAUOFormat(plot);
  }

  public static void main0(String[] args) throws FileNotFoundException {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement00.xls");
    LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);
//    LCDTarget.Operator.gradationReverseFix(lcdtarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdtarget);
    model.setGetRGBMode(MultiMatrixModel.GetRGBMode.Mode2);
    model.setMaxValue(RGB.MaxValue.Double255);
    model.produceFactor();

//    IntensityModel imodel = new IntensityModel(adapter);
//    imodel.produceFactor();
    int targetB = 255;
    int targetRG = 255;
    CIEXYZ rXYZ = model.getXYZ(new RGB(targetRG, 0, 0), false);
    CIEXYZ gXYZ = model.getXYZ(new RGB(0, targetRG, 0), false);
    CIEXYZ bXYZ = model.getXYZ(new RGB(0, 0, targetB), false);

    List<CIEXYZ> wXYZList = adapter.getXYZList().subList(0, targetRG + 1); //順
    int size = wXYZList.size();
//    CIEXYZ whiteXYZ = model.getXYZ(new RGB(targetRG, targetRG, targetB), false);
    CIEXYZ whiteXYZ = (CIEXYZ) wXYZList.get(255).clone();
    CIEXYZ whiteXYZ0 = model.getXYZ(RGB.White, false);
//    whiteXYZ = whiteXYZ0;
//    whiteXYZ = new CIEXYZ(364.15473190251396, 382.4332617913497, 587.8792677543);
//    double targetY = 382.4332617913497;
//    whiteXYZ.scaleY(targetY);
    Collections.reverse(wXYZList); //逆

    MaxMatrixIntensityAnalyzer analyzer = MaxMatrixIntensityAnalyzer.
        getReadyAnalyzer(rXYZ, gXYZ, bXYZ, whiteXYZ);
    ComponentFetcher fetcher = new ComponentFetcher(analyzer);
    List<Component> componentList = fetcher.fetchComponent(wXYZList);
//    int maxBIntensityIndex = findMaxBIntensityIndex(componentList);

//    List<CIEXYZ> targetXYZList = DGTester.getTargetXYZList(whiteXYZ);
//    List<Component> targetComponentList = fetcher.fetchComponent(targetXYZList);
    List<CIEXYZ> targetXYZList = DGTester.getTargetXYZList(whiteXYZ);
    List<RGB>
        rgbList0 = DGTester.getRGBList(componentList, analyzer, targetXYZList,
                                       "DG0", false);
    List<RGB> rgbList = rgbList0;
    RGB whiteRGB = rgbList.get(rgbList.size() - 1);
    System.out.println(whiteRGB + " " + model.getXYZ(whiteRGB, false) + " " +
                       whiteXYZ);
    drawGamma(rgbList, model, "r0");
    if (true) {
      whiteRGB.R = whiteRGB.G = whiteRGB.B = 255;
      drawGamma(rgbList, model, "r1");
    }
//    System.out.println(whiteRGB);
//    double ratio = 255 / whiteRGB.B;
//    for (RGB rgb : rgbList) {
//      rgb.B *= ratio;
//    }

    if (true) {
      List<RGB> rgbList2 = multiGen(1, rgbList, model, analyzer, whiteXYZ, true);
      RGB whiteRGB2 = rgbList2.get(rgbList2.size() - 1);
      System.out.println(rgbList2.get(rgbList2.size() - 2) + " " + whiteRGB2);
//      drawGamma(rgbList2, model, "r3");
      whiteRGB2.R = whiteRGB2.G = whiteRGB2.B = 255;
      drawGamma(rgbList2, model, "r4");
    }
  }

  static void drawxyPlot(CIEXYZ whiteXYZ, List<RGB> rgbList, LCDModel model,
      LCDModel imodel, int maxBIntensityIndex) {
    Plot2D xnyPlot = Plot2D.getInstance();
    Plot2D xyPlot = Plot2D.getInstance();

    for (int maxBIntensityDG = 200; maxBIntensityDG <= 255; maxBIntensityDG++) {
//  int maxBIntensityDG = 250;
      double maxBIntensityDGY = Math.pow(maxBIntensityDG / 255., 2.2) *
          whiteXYZ.Y;

      RGB rgb = rgbList.get(maxBIntensityDG);
      RGB maxBIntensityRGB = (RGB) rgb.clone();
      CIEXYZ XYZ0 = model.getXYZ(maxBIntensityRGB, false);
//    CIExyY xyY0 = new CIExyY(XYZ0);
      maxBIntensityRGB.B = maxBIntensityIndex;
      CIEXYZ XYZ1 = model.getXYZ(maxBIntensityRGB, false);
      CIExyY xyY1 = new CIExyY(XYZ1);
      xyY1.Y = maxBIntensityDGY;
      RGB rgb0 = model.getRGB(XYZ0, false);
      RGB rgb1 = model.getRGB(xyY1.toXYZ(), false);
//      System.out.println(maxBIntensityDG + ": " + rgb0 + " " + rgb1);
      RGB irgb0 = null;
      RGB irgb1 = null;
      try {
        irgb0 = imodel.getRGB(XYZ0, false);
        irgb1 = imodel.getRGB(XYZ1, false);
      }
      catch (IndexOutOfBoundsException ex) {
//        irgb1 = null;
      }
      System.out.println(maxBIntensityDG + ": " + irgb0 + " " + irgb1);

      xnyPlot.addCacheScatterLinePlot("x", maxBIntensityDG, xyY1.x);
      xnyPlot.addCacheScatterLinePlot("y", maxBIntensityDG, xyY1.y);
      xyPlot.addScatterPlot(Integer.toString(maxBIntensityDG), xyY1.x, xyY1.y);

    }
    xnyPlot.setVisible();
    xyPlot.setVisible();

  }
}
