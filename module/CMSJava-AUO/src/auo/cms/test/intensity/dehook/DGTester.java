package auo.cms.test.intensity.dehook;

import shu.cms.colorformat.adapter.xls.AUOCPTableXLSAdapter;
import jxl.read.biff.BiffException;
import java.io.IOException;
import java.util.List;
import shu.cms.colorspace.independ.*;
import shu.cms.measure.intensity.AdvancedDGLutGenerator;
import shu.cms.colorformat.adapter.xls.AUOPropertyExtractor;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.intensity.ComponentFetcher;
import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;
import shu.cms.measure.intensity.Component;
import java.util.ArrayList;
import shu.cms.plot.Plot2D;

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
public class DGTester {

  public static List<CIEXYZ> getTargetXYZList(CIEXYZ whiteXYZ, CIEXYZ whiteXYZ2,
                                              double gamma, int count,
                                              boolean useWhiteXYZ2) {
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    List<CIEXYZ> targetXYZList = new ArrayList(count);
    int n = useWhiteXYZ2 ? count - 1 : count;
    for (int x = 0; x < n; x++) {
      double normal = x / 255.;
      double normalGamma = Math.pow(normal, gamma);
      CIExyY targetxyY = (CIExyY) whitexyY.clone();
      targetxyY.Y = normalGamma * whiteXYZ.Y;
      targetXYZList.add(targetxyY.toXYZ());
    }
    if (useWhiteXYZ2) {
      targetXYZList.add(whiteXYZ2);
    }
//    CIExyY targetxyY = (CIExyY) whitexyY.clone();
//    targetxyY.Y = whiteXYZ2.Y;
//    targetXYZList.add(targetxyY.toXYZ());
//     targetXYZList.add(whiteXYZ);
//    targetXYZList.add(whiteXYZ2);
    return targetXYZList;
  }

  public static List<CIEXYZ> getTargetXYZList(CIEXYZ whiteXYZ) {
    int count = 256;
    double gamma = 2.2;
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    List<CIEXYZ> targetXYZList = new ArrayList(count);
    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double normalGamma = Math.pow(normal, gamma);
      CIExyY targetxyY = (CIExyY) whitexyY.clone();
      targetxyY.Y = normalGamma * whiteXYZ.Y;
      targetXYZList.add(targetxyY.toXYZ());
    }
    return targetXYZList;
  }

  /**
   *
   * @param analyzer MaxMatrixIntensityAnalyzer
   * @param XYZList List
   * @param whiteXYZ CIEXYZ
   * @param showPlot boolean
   * @return List
   * @deprecated
   */
  public static List<RGB> getRGBList(MaxMatrixIntensityAnalyzer analyzer,
                                     List<CIEXYZ> XYZList, CIEXYZ whiteXYZ,
      boolean showPlot) {

    List<CIEXYZ> targetXYZList = getTargetXYZList(whiteXYZ);

    return getRGBList(analyzer, XYZList, targetXYZList, "", showPlot);

  }

  /**
   *
   * @param analyzer MaxMatrixIntensityAnalyzer
   * @param XYZList List
   * @param targetXYZList List
   * @param title String
   * @param showPlot boolean
   * @return List
   * @deprecated
   */
  public static List<RGB> getRGBList(MaxMatrixIntensityAnalyzer analyzer,
                                     List<CIEXYZ> XYZList,
      List<CIEXYZ> targetXYZList, String title, boolean showPlot) {

    ComponentFetcher fetcher = new ComponentFetcher(analyzer);
    List<Component> componentList = fetcher.fetchComponent(XYZList);

    AdvancedDGLutGenerator advDGLutGen = new AdvancedDGLutGenerator(
        componentList, fetcher);

    advDGLutGen.setTargetXYZList(targetXYZList);
    List<RGB> rgbList = advDGLutGen.produce();
    Plot2D dglutPlot = Plot2D.getInstance(title);
    int x = 0;
    double[] values = new double[3];
    for (RGB rgb : rgbList) {
      rgb.R = Double.isNaN(rgb.R) ? 0 : rgb.R;
      rgb.G = Double.isNaN(rgb.G) ? 0 : rgb.G;
      rgb.B = Double.isNaN(rgb.B) ? 0 : rgb.B;
      dglutPlot.addCacheScatterLinePlot(x++, rgb.getValues(values));
    }
    if (showPlot) {
      dglutPlot.setVisible();
      dglutPlot.setFixedBounds(0, 0, 255);
      dglutPlot.setFixedBounds(1, 0, 255);
    }
    return rgbList;
  }

  public static List<CIEXYZ> targetXYZList;
  public static List<RGB> getRGBList(List<Component>
      componentList, MaxMatrixIntensityAnalyzer analyzer, CIEXYZ whiteXYZ,
      String title, boolean showPlot) {
    targetXYZList = getTargetXYZList(whiteXYZ);
    return getRGBList(componentList, analyzer, targetXYZList, title, showPlot);
  }

  public static List<RGB> getRGBList(List<Component>
      componentList, MaxMatrixIntensityAnalyzer analyzer, CIEXYZ whiteXYZ,
      CIEXYZ whiteXYZ2,
      String title, boolean showPlot) {
    List<CIEXYZ> targetXYZList = getTargetXYZList(whiteXYZ);
    CIEXYZ targetWhite = targetXYZList.get(targetXYZList.size() - 1);
    targetWhite.scaleY(whiteXYZ2.Y);
    return getRGBList(componentList, analyzer, targetXYZList, title, showPlot);
  }

  public static List<RGB> getRGBList(List<Component> componentList,
      MaxMatrixIntensityAnalyzer analyzer, List<CIEXYZ> targetXYZList,
      String title, boolean showPlot) {
    CIEXYZ white = targetXYZList.get(targetXYZList.size() - 1);
    analyzer = MaxMatrixIntensityAnalyzer.getReadyAnalyzer(analyzer, white);

    ComponentFetcher fetcher = new ComponentFetcher(analyzer);

    AdvancedDGLutGenerator advDGLutGen = new AdvancedDGLutGenerator(
        componentList, fetcher);
    advDGLutGen.setPlotIncomingIntensity(false);
    advDGLutGen.setPlotTargetIntensity(false);

    advDGLutGen.setTargetXYZList(targetXYZList);
    List<RGB> rgbList = advDGLutGen.produce();

    Plot2D dglutPlot = showPlot ? Plot2D.getInstance(title) : null;
    int x = 0;
    double[] values = new double[3];
    for (RGB rgb : rgbList) {
      rgb.R = Double.isNaN(rgb.R) ? 0 : rgb.R;
      rgb.G = Double.isNaN(rgb.G) ? 0 : rgb.G;
      rgb.B = Double.isNaN(rgb.B) ? 0 : rgb.B;
      if (Quantization) {
        rgb.quantization(RGB.MaxValue.Int11Bit);
      }
      if (showPlot) {
        dglutPlot.addCacheScatterLinePlot(x++, rgb.getValues(values));
      }
    }
    RGB whiteRGB = rgbList.get(rgbList.size() - 1);
    if (showPlot) {
      dglutPlot.setVisible();
      dglutPlot.setFixedBounds(0, 0, 255);
      dglutPlot.setFixedBounds(1, 0, 255);
    }
    return rgbList;
  }

  public static boolean Quantization = false;

  static void collatz(int n) {
//    do {
//      System.out.println(n);
//      n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
//    }
//    while (n != 1);
    int x = 1;
    System.out.println(x++ +": " + n);
    for (; n != 1; x++) {
      System.out.println(x + ": " + (n = (n % 2 == 0) ? n / 2 : 3 * n + 1));
    }

  }

  public static void main(String[] args) throws BiffException, IOException {
    collatz(3);
    AUOCPTableXLSAdapter xls = new AUOCPTableXLSAdapter("hook/debug.xls");
    AUOPropertyExtractor property = new AUOPropertyExtractor(xls);
    List<CIEXYZ> XYZList = xls.getXYZList();
    CIEXYZ whiteXYZ = XYZList.get(0);
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    CIExyY rxyY = property.getNativePrimaryColor(RGB.Channel.R);
    CIExyY gxyY = property.getNativePrimaryColor(RGB.Channel.G);
    CIExyY bxyY = property.getNativePrimaryColor(RGB.Channel.B);

    MaxMatrixIntensityAnalyzer analyzer = MaxMatrixIntensityAnalyzer.
        getReadyAnalyzer(rxyY, gxyY, bxyY, whitexyY);
//    analyzer.setupComponent(RGB.Channel.R, rxyY.toXYZ());
//    analyzer.setupComponent(RGB.Channel.G, gxyY.toXYZ());
//    analyzer.setupComponent(RGB.Channel.B, bxyY.toXYZ());
//    analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
//    analyzer.enter();


    getRGBList(analyzer, XYZList, whiteXYZ, true);
//    ComponentFetcher fetcher = new ComponentFetcher(analyzer);
//    List<Component> componentList = fetcher.fetchComponent(XYZList);
//    AdvancedDGLutGenerator advDGLutGen = new AdvancedDGLutGenerator(
//        componentList, fetcher);
//
//    int count = 256;
//    double gamma = 2.2;
////    CIExyY whitexyY = new CIExyY(whiteXYZ);
//    List<CIEXYZ> targetXYZList = new ArrayList(count);
//    for (int x = 0; x < 256; x++) {
//      double normal = x / 255.;
//      double normalGamma = Math.pow(normal, gamma);
//      CIExyY targetxyY = (CIExyY) whitexyY.clone();
//      targetxyY.Y = normalGamma * whiteXYZ.Y;
//      targetXYZList.add(targetxyY.toXYZ());
//    }
//    advDGLutGen.setTargetXYZList(targetXYZList);
//    List<RGB> rgbList = advDGLutGen.produce();
//    Plot2D dglutPlot = Plot2D.getInstance();
//    int x = 0;
//    double[] values = new double[3];
//    for (RGB rgb : rgbList) {
//      rgb.R = Double.isNaN(rgb.R) ? 0 : rgb.R;
//      rgb.G = Double.isNaN(rgb.G) ? 0 : rgb.G;
//      rgb.B = Double.isNaN(rgb.B) ? 0 : rgb.B;
//      dglutPlot.addCacheScatterLinePlot(x++, rgb.getValues(values));
//    }
//    dglutPlot.setVisible();
//    dglutPlot.setFixedBounds(0, 0, 255);
  }
}
