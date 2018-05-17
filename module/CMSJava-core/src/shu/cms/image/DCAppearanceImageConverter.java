package shu.cms.image;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.gma.gbd.*;
import shu.math.*;
import shu.math.lut.*;
import shu.util.*;
import shu.math.array.DoubleArray;
import shu.cms.devicemodel.dc.dcam.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DCAppearanceImageConverter {
  public static enum LUTType {
    TriCubicSpline, Tetrahedral} ;

    protected final static LUTType LUT_TYPE = LUTType.Tetrahedral;
    protected GMBICCProfileAdapter fromProfile;
    protected GMBICCProfileAdapter toProfile;
    protected DCAppearanceModel fromModel;
    protected DCAppearanceModel toModel;

    public DCAppearanceImageConverter(GMBICCProfileAdapter fromProfile,
                                      GMBICCProfileAdapter toProfile) {
      this(fromProfile, toProfile, null, null);
    }

    public DCAppearanceImageConverter(GMBICCProfileAdapter fromProfile,
                                      GMBICCProfileAdapter toProfile,
                                      int[] fromIgnoreIndex,
                                      int[] toIgnoreIndex) {
      this.fromProfile = fromProfile;
      this.toProfile = toProfile;
      this.fromIgnoreIndex = fromIgnoreIndex;
      this.toIgnoreIndex = toIgnoreIndex;
//      init();
    }

    public DCAppearanceImageConverter(String fromInterpolationFilename,
                                      String toInterpolationFilename) {
      CubeTable fromLUT = (CubeTable) Persistence.readObject(
          fromInterpolationFilename);
      CubeTable toLUT = (CubeTable) Persistence.readObject(
          toInterpolationFilename);
      switch (LUT_TYPE) {
        case Tetrahedral:
          fromProfileLUT = new TetrahedralInterpolation(fromLUT);
          toProfileLUT = new TetrahedralInterpolation(toLUT);
          break;
        case TriCubicSpline:
          fromProfileLUT = new TriCubicSplineInterpolation(fromLUT);
          toProfileLUT = new TriCubicSplineInterpolation(toLUT);
          break;
      }
    }

    protected DCAppearanceModel.Style style = DCAppearanceModel.Style.IPT;
    protected RGB.ColorSpace rgbColorSpace = RGB.ColorSpace.sRGB;
    protected boolean onLineCalculate = false;
    protected DCChartAdapter fromChart;
    protected DCChartAdapter toChart;

    public void setFromDCChart(DCChartAdapter fromChart) {
      this.fromChart = fromChart;
    }

    public void setToDCChart(DCChartAdapter toChart) {
      this.toChart = toChart;
    }

    /**
     * 初始化
     */
    public void init() {
      LightSource.Source lightsource = LightSource.CIE.D65;

      //========================================================================
      // from
      //========================================================================
      DCTarget.Chart fromChartType = DCTarget.Chart.getChart(fromProfile.
          getFilename());
      if (fromChartType == null) {
        fromChartType = DCTarget.Chart.CC24;
      }
      if (fromChart == null) {
        fromChart = new DCChartAdapter(fromChartType,
                                       lightsource);
      }

      DCTarget fromTarget = DCTarget.Instance.get(fromProfile, fromChart,
                                                  lightsource, fromChartType);
      //========================================================================

      //========================================================================
      // to
      //========================================================================
      DCTarget.Chart toChartType = DCTarget.Chart.getChart(toProfile.
          getFilename());
      if (toChartType == null) {
        toChartType = DCTarget.Chart.CC24;
      }
      if (toChart == null) {
        toChart = new DCChartAdapter(toChartType, lightsource);
      }

      DCTarget toTarget = DCTarget.Instance.get(toProfile, toChart, lightsource,
                                                toChartType);
      //========================================================================

      if (onLineCalculate) {
        fromModel = new DCAppearanceModel(fromTarget, style,
                                          rgbColorSpace);
        toModel = new DCAppearanceModel(toTarget, style,
                                        rgbColorSpace);
      }
      else {
        GamutBoundaryRGBDescriptor gbd = DCAppearanceModel.
            getGBDDescriptorInstance(rgbColorSpace, style);
        fromModel = new DCAppearanceModel(fromTarget, style,
                                          rgbColorSpace, gbd);
        toModel = new DCAppearanceModel(toTarget, style,
                                        rgbColorSpace, gbd);
      }

      fromModel.setIgnoreIndex(this.fromIgnoreIndex);
      toModel.setIgnoreIndex(this.toIgnoreIndex);

      fromModel.produceFactor();
//      fromModel.setDoLightnessCorrect(false);
//      fromModel.setDoLightnessCorrectInHue(false);
//      fromModel.setDoChromaCorrect(false);
//      fromModel.setDoHueCorrect(false);
//      fromModel.setDoGrayBalance(false);

      toModel.produceFactor();
//      toModel.setDoLightnessCorrect(false);
//      toModel.setDoLightnessCorrectInHue(false);
//      toModel.setDoChromaCorrect(false);
//      toModel.setDoHueCorrect(false);
//      toModel.setDoGrayBalance(false);
    }

    protected int[] fromIgnoreIndex = null;
    protected int[] toIgnoreIndex = null;

    public BufferedImage getFromProfileOriginalImage(BufferedImage image) {
      BufferedImage originalImg = fromModel.inverseImage(image);
      return originalImg;
    }

    public BufferedImage getToProfileOriginalImage(BufferedImage image) {
      BufferedImage originalImg = toModel.inverseImage(image);
      return originalImg;
    }

    public BufferedImage convert(BufferedImage image) {
      BufferedImage result = null;
      if (fromProfileLUT != null && toProfileLUT != null) {
        result = ImageUtils.cloneBufferedImage(image);
        int w = result.getWidth();
        int h = result.getHeight();
        double[] pixels = null;
        double[] source = new double[3];

        for (int x = 0; x < w; x++) {
          for (int y = 0; y < h; y++) {
            //取出pixel,是0~255
            result.getRaster().getPixel(x, y, source);
            //正規化
            pixels = DoubleArray.times(source, 1. / 255);
            //from Profile的運算
            pixels = fromProfileLUT.getValues(pixels);
            //合理化
            pixels = RGB.rationalize(pixels, RGB.MaxValue.Double1);
            //to Profile的運算
            pixels = toProfileLUT.getValues(pixels);
            //合理化
            pixels = RGB.rationalize(pixels, RGB.MaxValue.Double1);
            //從0~1轉回0~255
            pixels = DoubleArray.times(pixels, 255);
            //回存
            result.getRaster().setPixel(x, y, pixels);
          }
        }
      }
      else {
        BufferedImage originalImg = fromModel.inverseImage(image);
        result = toModel.forwardImage(originalImg);
      }

      return result;
    }

    public static void main(String[] args) {
      GMBICCProfileAdapter src = new GMBICCProfileAdapter(
          "Measurement Files/Camera/d300/d300-std.icc");
//        "Measurement Files/Camera/S5Pro/s5p-std.icc");

      GMBICCProfileAdapter dest = new GMBICCProfileAdapter(
          "Measurement Files/Camera/s5pro/s5p-f2.icc");
//          "Measurement Files/Films/Velvia/it8.icc");

      DCAppearanceImageConverter converter = new DCAppearanceImageConverter(
          src, dest);
//      converter.setToDCChart(new DCChartAdapter(CMSDir.Reference.Camera +
//                                                "/IT8 E3199808.cxf",
//                                                LightSource.CIE.D65));

//      DCAppearanceImageConverter converter = new DCAppearanceImageConverter(
//          "d200-normal.lut",
//          "s5p-std.lut");
      converter.init();
//      converter.produceLUT();
//      converter.storeLUT();

      try {
        long start = System.currentTimeMillis();
        BufferedImage img = ImageUtils.loadImage(
//            "Image/Profile/d200/_DSC5705.JPG");
            "Image/Profile/d300/1_standard.jpg");
//            "DSC_0516.jpg");
        BufferedImage result = converter.convert(img);
//      BufferedImage result = converter.getFromProfileOriginalImage(img);
        ImageUtils.storeJPEGImage("result.JPG", result);
        System.out.println(System.currentTimeMillis() - start);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    protected Asymmetric3DLUT fromProfileLUT;
    protected Asymmetric3DLUT toProfileLUT;

    protected final static String getFilenameOnly(String pathname) {
      return pathname.substring(pathname.lastIndexOf('/') + 1,
                                pathname.indexOf(".icc"));
    }

    public void storeLUT() {
      String from = getFilenameOnly(fromProfile.getFilename()) + ".lut";
      String to = getFilenameOnly(toProfile.getFilename()) + ".lut";
//      from = from.substring(from.lastIndexOf('/') + 1, from.indexOf(".icc")) +
//          ".lut";
//      to = to.substring(to.lastIndexOf('/') + 1, to.indexOf(".icc")) + ".lut";
      storeInterpolation(from, to);
    }

    public void storeInterpolation(String fromInterpolationFilename,
                                   String toInterpolationFilename) {
      Persistence.writeObject(fromProfileLUT.getKeyValuePairs(),
                              fromInterpolationFilename);
      Persistence.writeObject(toProfileLUT.getKeyValuePairs(),
                              toInterpolationFilename);
    }

    public void produceLUT() {
      fromProfileLUT = produceFromProfileInterpolation();
      toProfileLUT = produceToProfileInterpolation();
    }

    protected Asymmetric3DLUT produceFromProfileInterpolation() {
      int step = RGB_STEP;
      int grid = (255 / step) + 1;
      int size = (int) Math.pow(grid, 3);
      double[][][] lut = new double[size][2][];
      int index = 0;
      RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double1);

      for (int r = 0; r < 256; r += step) {
        for (int g = 0; g < 256; g += step) {
          for (int b = 0; b < 256; b += step) {
            rgb.R = r / 255.;
            rgb.G = g / 255.;
            rgb.B = b / 255.;
            CIEXYZ XYZ = fromModel.getXYZ(rgb, true);
            RGB rgb2 = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB);
            rgb2.rationalize();
            lut[index][0] = rgb.getValues();
            lut[index][1] = rgb2.getValues();
            index++;
          }
        }
      }
      CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                          new double[] {1, 1, 1}, grid);

//      return new TriCubicSplineInterpolation(cubeTable);
//    return new TetrahedralInterpolation(cubeTable);
      switch (LUT_TYPE) {
        case Tetrahedral:
          return new TetrahedralInterpolation(cubeTable);
        case TriCubicSpline:
          return new TriCubicSplineInterpolation(cubeTable);
        default:
          return null;
      }
    }

    protected Asymmetric3DLUT produceToProfileInterpolation() {
      int step = RGB_STEP;
      int grid = (255 / step) + 1;
      int size = (int) Math.pow(grid, 3);
      double[][][] lut = new double[size][2][];
      int index = 0;
      RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double1);

      for (int r = 0; r < 256; r += step) {
        for (int g = 0; g < 256; g += step) {
          for (int b = 0; b < 256; b += step) {
            rgb.R = r / 255.;
            rgb.G = g / 255.;
            rgb.B = b / 255.;
            CIEXYZ XYZ = rgb.toXYZ();
            RGB rgb2 = toModel.getRGB(XYZ, true);
            rgb2.rationalize();
            lut[index][0] = rgb.getValues();
            lut[index][1] = rgb2.getValues();
            index++;
          }
        }
      }
      CubeTable cubeTable = new CubeTable(lut, new double[] {0, 0, 0},
                                          new double[] {1, 1, 1}, grid);
//      return new TriCubicSplineInterpolation(cubeTable);
//    return new TetrahedralInterpolation(cubeTable);
      switch (LUT_TYPE) {
        case Tetrahedral:
          return new TetrahedralInterpolation(cubeTable);
        case TriCubicSpline:
          return new TriCubicSplineInterpolation(cubeTable);
        default:
          return null;
      }
    }

    public void setFromIgnoreIndex(int[] fromIgnoreIndex) {
      this.fromIgnoreIndex = fromIgnoreIndex;
    }

    public void setToIgnoreIndex(int[] toIgnoreIndex) {
      this.toIgnoreIndex = toIgnoreIndex;
    }

    public void setOnLineCalculate(boolean onLineCalculate) {
      this.onLineCalculate = onLineCalculate;
    }

    protected final static int RGB_STEP = 5;
  }
