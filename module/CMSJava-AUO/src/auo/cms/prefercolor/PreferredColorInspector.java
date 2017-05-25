package auo.cms.prefercolor;

import shu.cms.Patch;
import shu.cms.lcd.LCDTarget;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import auo.cms.hsv.autotune.ProfileColorSpaceUtils;
import shu.cms.profile.ProfileColorSpace;
import java.util.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.CIECAM02Color;
import shu.cms.plot.*;
import shu.math.array.*;
import auo.cms.plot.PlotUtils;
import java.awt.Color;
import shu.plot.plots.GridPlot2D;
import shu.math.Maths;
import org.math.plot.plots.VectorLayerPlot;
import shu.cms.colorspace.ColorSpace;
import shu.cms.devicemodel.lcd.CLUTOptimizeReverseModel;
import shu.cms.profile.ColorSpaceConnectedLUT;
import shu.cms.DeltaE;
import auo.cms.hsv.autotune.PreferredColorSpace;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來分析各家的preferred color
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PreferredColorInspector {
  private ProfileColorSpace pcs;
  private CIEXYZ whiteXYZ;
  private CIEXYZ relativeWhiteXYZ;
  private CIEXYZ blackXYZ;
  private CIECAM02 ciecam02;
  private CIECAM02 sRGBCIECAM02;
  private final static RGB.ColorSpace sRGBColorSpace = RGB.ColorSpace.
      sRGB_gamma22;

  private CIEXYZ getPCSXYZ(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double255);
    double[] XYZValues = pcs.toCIEXYZValues(rgbValues);
    CIEXYZ XYZ = new CIEXYZ(XYZValues, whiteXYZ, NormalizeY.Not);
    return XYZ;
  }

  public PreferredColorInspector(ProfileColorSpace pcs) {
    this.pcs = pcs;
    whiteXYZ = getPCSXYZ(RGB.White);
    blackXYZ = getPCSXYZ(RGB.Black);
    relativeWhiteXYZ = CIEXYZ.minus(whiteXYZ, blackXYZ);
    ViewingConditions vc = ViewingConditions.getDimViewingConditions(whiteXYZ);
    ciecam02 = new CIECAM02(vc);
    sRGBCIECAM02 = new CIECAM02(ViewingConditions.getDimViewingConditions(
        sRGBColorSpace.getReferenceWhiteXYZ()));
  }

  private CIECAM02Color getCIECAM02Color(RGB rgb) {
    CIEXYZ XYZ = getPCSXYZ(rgb);
    XYZ.normalizeWhite();
    return ciecam02.forward(XYZ);
  }

  private CIELCh getCIELCh(RGB rgb) {
    CIEXYZ XYZ = getPCSXYZ(rgb);
    CIELCh LCh = new CIELCh(new CIELab(CIEXYZ.minus(XYZ, blackXYZ),
                                       relativeWhiteXYZ));
    return LCh;
  }

  private CIECAM02Color getsRGBCIECAM02Color(RGB rgb) {
    CIEXYZ XYZ = rgb.toXYZ(sRGBColorSpace);
    XYZ.normalizeWhite();
    return sRGBCIECAM02.forward(XYZ);
  }

  private CIELCh getsRGBCIELCh(RGB rgb) {
    CIEXYZ XYZ = rgb.toXYZ(sRGBColorSpace);
    CIELCh LCh = new CIELCh(new CIELab(XYZ, RGB.toXYZ(RGB.White, sRGBColorSpace)));
    return LCh;
  }

  private double[] getacbcJValues(CIECAM02Color cam02Color) {
    return new double[] {
        cam02Color.ac, cam02Color.bc, cam02Color.J};
  }

  private double[][] getOriginalModifiedAndVector(RGB rgb) {
    CIECAM02Color cam02Color = getCIECAM02Color(rgb);
    CIECAM02Color sRGBCAM02Color = getsRGBCIECAM02Color(rgb);
    double[] acbcJValues = getacbcJValues(cam02Color);
    double[] sRGBacbcJValues = getacbcJValues(sRGBCAM02Color);
    double[] vector = DoubleArray.minus(acbcJValues, sRGBacbcJValues);
    double[][] result = new double[][] {
        sRGBacbcJValues, acbcJValues, vector};
    return result;
  }

  public Plot3D plot3DWithCIECAM02(List<RGB> rgbList) {
    Plot3D plot = Plot3D.getInstance();
    for (RGB rgb : rgbList) {
      double[][] originalModifiedAndVector = getOriginalModifiedAndVector(rgb);
      plot.addVectortoPlot(rgb.toString(), rgb.getColor(),
                           originalModifiedAndVector[0],
                           originalModifiedAndVector[2]);
//      CIECAM02Color cam02Color = getCIECAM02Color(rgb);
//      CIECAM02Color sRGBCAM02Color = getsRGBCIECAM02Color(rgb);
//      double[] acbcJValues = getacbcJValues(cam02Color);
//      double[] sRGBacbcJValues = getacbcJValues(sRGBCAM02Color);
//      double[] vector = DoubleArray.minus(acbcJValues, sRGBacbcJValues);
    }
    plot.setVisible();

    plot.setAxisLabels("ac", "bc", "J");
    plot.setFixedBounds(0, -100, 100);
    plot.setFixedBounds(1, -100, 100);
    plot.setFixedBounds(2, 0, 100);
    plot.rotateToAxis(2);
//    plot.rotateToAxis(1);
    plot.zoom(130, 130);
    return plot;
  }

  public static class HuePlaneData {
    double[][] orgCArray;
    double[][] orgLArray;
    double[][] newCArray;
    double[][] newLArray;
    public HuePlaneData(double[][] orgCArray,
                        double[][] orgLArray,
                        double[][] newCArray,
                        double[][] newLArray
        ) {
      this.orgCArray = orgCArray;
      this.orgLArray = orgLArray;
      this.newCArray = newCArray;
      this.newLArray = newLArray;
    }

    public double[][] getRepresentScatterData() {
      double[][] scatterData = new double[6][2];
      scatterData[0][0] = orgCArray[1][5];
      scatterData[0][1] = orgLArray[1][5];
      scatterData[1][0] = orgCArray[5][5];
      scatterData[1][1] = orgLArray[5][5];
      scatterData[2][0] = orgCArray[8][5];
      scatterData[2][1] = orgLArray[8][5];
      scatterData[3][0] = orgCArray[1][8];
      scatterData[3][1] = orgLArray[1][8];
      scatterData[4][0] = orgCArray[5][8];
      scatterData[4][1] = orgLArray[5][8];
      scatterData[5][0] = orgCArray[8][8];
      scatterData[5][1] = orgLArray[8][8];
      return scatterData;
    }

    public double[][] getRepresentVectorData() {
      double[][] zoneVector = getZoneVector();
      double[][] vectorData = new double[6][2];
      for (int x = 0; x < 6; x++) {
        vectorData[x] = zoneVector[x + 3];
      }
      return vectorData;
    }

    private double[][] getZoneVector() {
      double[][] zoneVector = new double[9][];
      double[][] chromaVector = getChromaVector();
      double[][] lightnessVector = getLightnessVector();
      int j0 = 0, j1 = 3; //for Value of HSV, 低亮度

      zoneVector[0] = getSubMatrixAndAverage(chromaVector, lightnessVector, 0,
                                             3, j0, j1);
      zoneVector[1] = getSubMatrixAndAverage(chromaVector, lightnessVector, 3,
                                             7, j0, j1);
      zoneVector[2] = getSubMatrixAndAverage(chromaVector, lightnessVector, 7,
                                             10, j0, j1);

      j0 = 3; //中亮度
      j1 = 7;
      zoneVector[3] = getSubMatrixAndAverage(chromaVector, lightnessVector, 0,
                                             3, j0, j1);
      zoneVector[4] = getSubMatrixAndAverage(chromaVector, lightnessVector, 3,
                                             7, j0, j1);
      zoneVector[5] = getSubMatrixAndAverage(chromaVector, lightnessVector, 7,
                                             10, j0, j1);
      j0 = 7; //高亮度
      j1 = 10;
      zoneVector[6] = getSubMatrixAndAverage(chromaVector, lightnessVector, 0,
                                             3, j0, j1);
      zoneVector[7] = getSubMatrixAndAverage(chromaVector, lightnessVector, 3,
                                             7, j0, j1);
      zoneVector[8] = getSubMatrixAndAverage(chromaVector, lightnessVector, 7,
                                             10, j0, j1);

      return zoneVector;
    }

    private final static double getSubMatrixAndAverage(double[][] matrix,
        int i1, int i2, int j1, int j2) {
      double[][] subMatrix = DoubleArray.getSubMatrixRangeCopy(matrix,
          i1, i2, j1, j2);
      double mean = Maths.mean(subMatrix);
      return mean;
    }

    private final static double[] getSubMatrixAndAverage(double[][]
        chromaVector, double[][] lightnessVector,
        int i1, int i2, int j1, int j2) {
      return new double[] {
          getSubMatrixAndAverage(chromaVector, i1, i2, j1, j2),
          getSubMatrixAndAverage(lightnessVector, i1, i2, j1, j2)};
    }

    public double[][] getChromaVector() {
      return DoubleArray.minus(newCArray, orgCArray);
    }

    public double[][] getLightnessVector() {
      return DoubleArray.minus(newLArray, orgLArray);
    }

  }

  public final static List<RGB> getHuePlaneRGBList(int sStep,
      int vStep, int ...hueArray) {
    List<RGB> result = new ArrayList<RGB> ();
    for (int hue : hueArray) {
      List<RGB> rgbList = getHuePlaneRGBList(hue, sStep, vStep);
      result.addAll(rgbList);
    }
    return result;
  }

  public final static List<RGB> getHuePlaneRGBList(int hue, int sStep,
      int vStep) {
    int sLevel = (100 / sStep);
    int vLevel = (100 / vStep) + 1;
    List<RGB> rgbList = new ArrayList<RGB> (sLevel * vLevel);
    for (int s = sStep; s <= 100; s += sStep) {
      for (int v = 0; v <= 100; v += vStep) {
        HSV hsv = new HSV(sRGBColorSpace, new double[] {hue, s, v});
        RGB rgb = hsv.toRGB();
        rgbList.add(rgb);
      }
    }
    return rgbList;
  }

  private HuePlaneData getHuePlaneData(int hue, boolean plotWithCAM02) {
    double[][] orgCArray = new double[11][11];
    double[][] orgLArray = new double[11][11];
    double[][] newCArray = new double[11][11];
    double[][] newLArray = new double[11][11];

    for (int s = 0; s <= 100; s += 10) {
      for (int v = 0; v <= 100; v += 10) {
        HSV hsv = new HSV(sRGBColorSpace, new double[] {hue, s, v});
        RGB rgb = hsv.toRGB();
        int sIndex = s / 10;
        int vIndex = v / 10;
        if (plotWithCAM02) {
          CIECAM02Color cam02Color = getCIECAM02Color(rgb);
          CIECAM02Color sRGBCAM02Color = getsRGBCIECAM02Color(rgb);

          newCArray[sIndex][vIndex] = cam02Color.C;
          newLArray[sIndex][vIndex] = cam02Color.J;
          orgCArray[sIndex][vIndex] = sRGBCAM02Color.C;
          orgLArray[sIndex][vIndex] = sRGBCAM02Color.J;
        }
        else {
          CIELCh LCh = getCIELCh(rgb);
          CIELCh sRGBLCh = getsRGBCIELCh(rgb);

          newCArray[sIndex][vIndex] = LCh.C;
          newLArray[sIndex][vIndex] = LCh.L;
          orgCArray[sIndex][vIndex] = sRGBLCh.C;
          orgLArray[sIndex][vIndex] = sRGBLCh.L;
        }
      }
    }
    HuePlaneData huePlaneData = new HuePlaneData(orgCArray, orgLArray,
                                                 newCArray, newLArray);
    return huePlaneData;
  }

  public Plot2D plotHuePlaneWithCIECAM02(int hue, boolean plotVector) {
    return plotHuePlane(hue, plotVector, true);
  }

  public Plot2D plotHuePlaneWithCIELCh(int hue, boolean plotVector) {
    return plotHuePlane(hue, plotVector, false);
  }

  public Plot2D plotHuePlane(int hue, boolean plotVector, boolean plotWithCM02) {
    HuePlaneData huePlaneData = getHuePlaneData(hue, plotWithCM02);
    Color color = HSV.getLineColor(hue);
    Plot2D plot2D = Plot2D.getInstance(Integer.toString(hue));

    if (plotVector) {
      GridPlot2D fromGrid = new GridPlot2D("", color, huePlaneData.orgCArray,
                                           huePlaneData.orgLArray);
      GridPlot2D toGrid = new GridPlot2D("", color, huePlaneData.newCArray,
                                         huePlaneData.newLArray);
      PlotUtils.addScatterPlotAndVectortoPlot(plot2D, "", color, fromGrid,
                                              toGrid);
    }
    else {
      plot2D.addGridPlot("", color, huePlaneData.newCArray,
                         huePlaneData.newLArray);
    }

    //==========================================================================
    // represent data
    //==========================================================================
    double[][] scatterData = huePlaneData.getRepresentScatterData();
    double[][] vectorData = huePlaneData.getRepresentVectorData();
    Color representColor = hue != 240 ? Color.black : Color.red;
    int num = plot2D.addScatterPlot("Represent", representColor, scatterData);
    plot2D.addVectortoPlot(num, vectorData);
    //==========================================================================

    plot2D.setVisible();
    plot2D.setAxisLabels("C", "J");
    return plot2D;
  }

  public static void main(String[] args) {
    String[] modes = new String[] {
//        "Standard", "demo", "Movie", "Picture"}; //chimei
        "Movie", "Vivid"}; //chimei
//        "srgb", "Picture", "movie"}; //eizo
//        "std", "Movie", "Dynamic"}; //sony
//        "standard", "Dynamic"}; //sony
//    for (String mode : modes) {
//      inspect(new String[] {mode});
//    }
//    inspect(args);
//    plot2DHuePlane(args);
    reverseTest(args);
  }

  public static void reverseTest(String[] args) {
    String mode = "Standard";
    LCDTarget preferredTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/Sharp LC-46LX1/Modes/" + mode + "/871.xls");
    CIEXYZ whiteXYZ = preferredTarget.getWhitePatch().getXYZ();
    ColorSpaceConnectedLUT clut = ProfileColorSpaceUtils.
        toColorSpaceConnectedLUT(
            preferredTarget);

    CLUTOptimizeReverseModel model = new CLUTOptimizeReverseModel(clut);
    model.produceFactor();
    for (int x = 0; x < 1000; x++) {
      RGB rgb = new RGB( (int) (Math.random() * 255),
                        (int) (Math.random() * 255),
                        (int) (Math.random() * 255));
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      RGB rgb2 = model.getRGB(XYZ, false);
      double d = RGB.Delta.deltaRG(rgb, rgb2);
      if (d > 0.01) {
//        DeltaE de = model.getRGBDeltaE();
        CIEXYZ XYZ1 = model.getXYZ(rgb, false);
        CIEXYZ XYZ2 = model.getXYZ(rgb2, false);
        DeltaE de = new DeltaE(XYZ1, XYZ2, whiteXYZ);
        de.getCIE2000DeltaE();
        System.out.println(rgb + " " + rgb2 + ": " + d + "/" +
                           de.getCIE2000DeltaE());
      }
    }
  }

  public static void plot2DHuePlane(String[] args) {
    String mode = "Standard";
    LCDTarget preferredTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/Sharp LC-46LX1/Modes/" + mode + "/871.xls");
    CIEXYZ whiteXYZ = preferredTarget.getWhitePatch().getXYZ();
//    ProfileColorSpace pcs = ProfileColorSpaceUtils.
//        getPreferredProfileColorSpace(RGB.ColorSpace.
//                                      sRGB, preferredTarget, 100, whiteXYZ);
    PreferredColorSpace preferred = ProfileColorSpaceUtils.
        getPreferredColorSpacee(RGB.ColorSpace.
                                sRGB, preferredTarget, 100, whiteXYZ);
    ProfileColorSpace pcs = preferred.pcs;
    PreferredColorInspector inspector = new PreferredColorInspector(pcs);
    show2DHuePlane(inspector, false, false);

    double[] XYZValues = pcs.toD65CIEXYZValues(new double[] {0.5, 0.3, .2});
    double[] rgbValues = pcs.fromD65CIEXYZValues(XYZValues);
    System.out.println(Arrays.toString(rgbValues));

//    LCDTarget nbTarget = LCDTarget.Instance.getTest729FromAUOXLS(
//        "prefered/B156HW03/Dell 729.xls");
//    ProfileColorSpace nbpcs = Utils.getProfileColorSpace(nbTarget);
//    PreferColorInspector nbinspector = new PreferColorInspector(nbpcs);
//    show2DHuePlane(nbinspector, false, false);
  }

  public static void inspect(String[] args) {

//    String mode = "Standard";
//    String mode = "demo";
    String mode = "Movie";
//    String mode = "Picture";
//    String mode = "Cinema";
//    String mode = "Natural";
//    String mode = "Vivid";
//    String mode = "sRGB";
//    String mode = "色彩提升-關閉";
//    String mode = "色彩提升-鮮豔";
//    String mode = "色彩提升-綠色-藍色";
//    String mode = "色彩提升-綠色-膚色";

    if (args.length != 0) {
      mode = args[0];
    }

    LCDTarget test729Target = LCDTarget.Instance.getTest729FromAUOXLS(
//        "prefered/CHIMEI 22GH/729colors_" + mode + ".xls"); //廣色域
//        "prefered/CHIMEI TCM32/" + mode + "_729.xls"); //阿摘?
        "prefered/EIZO S2031W/729color_" + mode + ".xls"); //阿摘?
//        "prefered/LG 42SL90QD/" + mode + "/871.xls");
//        "prefered/Samsung PAVV/3DLUT729_Dynamic_DNIE Off_HDR Off.xls");
//        "prefered/Sharp LC-46LX1/Modes/" + mode + "/871.xls");
//        "prefered/Sony 70x7000/729color_" + mode + ".xls");
//        "prefered/SONY KDL-40ZX1/729colors_" + mode + ".xls");
//        "prefered/VIZIO VF551XVT-T/標準/" + mode + "/871.xls");

    boolean show2DHuePlane = true;
    boolean plot2DHueVector = false;

    boolean showComplex3DHuePlane = false;

    ProfileColorSpace pcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFrom729Target(test729Target);
    PreferredColorInspector inspector = new PreferredColorInspector(pcs);

    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    List<Patch> memoryPatchList = MemoryColorPatches.getAllsRGBPatchList();
    rgbList = Patch.Filter.rgbList(memoryPatchList);
    Plot3D plot = inspector.plot3DWithCIECAM02(rgbList);
    plot.setTitle(mode);

    for (Patch patch : memoryPatchList) {
      RGB rgb = patch.getRGB();
      double[][] originalModifiedAndVector = inspector.
          getOriginalModifiedAndVector(rgb);
      double[] original = originalModifiedAndVector[0];
      double[] modified = originalModifiedAndVector[1];
      double[] vector = originalModifiedAndVector[2];
      double[] originalPolarValues = ColorSpace.
          cartesian2polarCoordinatesValues(original[0], original[1]);
      double[] modifiedPolarValues = ColorSpace.
          cartesian2polarCoordinatesValues(modified[0], modified[1]);
      System.out.println(patch.getName() + "\tdJ: " + vector[2] +
                         " dC: " +
                         (modifiedPolarValues[0] - originalPolarValues[0]) +
                         " dh: " +
                         (modifiedPolarValues[1] - originalPolarValues[1]));
    }

    if (showComplex3DHuePlane) {
      Plot3D plot2 = inspector.plot3DWithCIECAM02(getHuePlaneRGBList(25, 25, 0,
          //        30, 60, 90, 120, 150, 180, 210, 240, 270, 300));
          60, 120, 180, 240, 300));
      plot2.setTitle(mode);
    }

    if (show2DHuePlane) {
      show2DHuePlane(inspector, plot2DHueVector, false);
    }
  }

  public final static void show2DHuePlane(PreferredColorInspector inspector,
                                          boolean plot2DHueVector,
                                          boolean plotWithCAM02) {
    shu.plot.PlotWindow[] plotWindows = new shu.plot.PlotWindow[6];
    for (int x = 0; x < 360; x += 60) {
      Plot2D plot2D = inspector.plotHuePlane(x, plot2DHueVector, plotWithCAM02);
      plotWindows[x / 60] = plot2D;
      plot2D.setFixedBounds(0, 0, 120);
      plot2D.setFixedBounds(1, 0, 100);
    }
    shu.cms.plot.PlotUtils.arrange(plotWindows, 3, 2);

  }
}
