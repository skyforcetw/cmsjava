package shu.cms.dc;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.image.*;
import shu.math.*;
import shu.math.lut.*;
import shu.cms.devicemodel.dc.dcam.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 1.在RGB作的話, 就是以R/B對齊G的方式, 計算出一個系數; 並且以內插的方式產生其他沒有gray scale的係數
 * 2.在RGBCAT作的話, 就是以原始RGB轉到XYZ, 再以原始RGB的G相同的值組成的RGB, 取得XYZ';
 *   計算XYZ與XYZ'的色適應矩陣. 至於中間沒有的值, 以內差的方式產生原始RGB, 相對應的也可以產生色適應矩陣.
 *   色適應之後, 再轉回RGB, 替代掉原始RGB.
 * 3.轉到XYZ,再轉到 紅綠 黃藍 色彩空間(Lab,Jab,IPT皆可), 以此空間作 a/b軸的調整
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GrayBalancer {

  public GrayBalancer(List<Patch> grayScale, Style style) {
    this(grayScale, style, RGB.ColorSpace.sRGB);
  }

  /**
   * 初步建議不要用rgbMapping
   * @param grayScale List
   * @param style Style
   * @param rgbColorSpace RGBColorSpace
   */
  public GrayBalancer(List<Patch> grayScale, Style style,
      RGB.ColorSpace rgbColorSpace) {
    this.style = style;
    this.rgbColorSpace = rgbColorSpace;
    this.whiteValues = rgbColorSpace.getReferenceWhiteXYZ().getValues();

    if (style == Style.RGBMap || style == Style.RGBScale) {
      initRGBGrayBalance(grayScale);
    }
    else if (style == Style.Lab) {
      initLabGrayBalance(grayScale);
    }
    else if (style == Style.None) {

    }

  }

  public GrayBalancer(DCTarget dcTarget, Style style) {
    this(dcTarget.filter.grayScale(), style, RGB.ColorSpace.sRGB);
  }

  public GrayBalancer(DCAppearanceModel.LChPair[] LChPairArray) {

  }

  protected Interpolation1DLUT[] rgbLutArray;
  protected Interpolation1DLUT[] abLutArray;
  protected RGB.ColorSpace rgbColorSpace;
  protected double[] whiteValues;

  protected void initLabGrayBalance(List<Patch> grayScale) {
    int size = grayScale.size() + 2;
    double[] input = new double[size];
    double[][] output = new double[2][size];
    double[] rgbValues = new double[3];

    for (int x = 1; x < size - 1; x++) {
      Patch p = grayScale.get(x - 1);
      p.getRGB().getValues(rgbValues, RGB.MaxValue.Double1);
      double[] XYZValues = RGB.toXYZValues(rgbValues, rgbColorSpace);
      double[] LabValues = CIELab.fromXYZValues(XYZValues, whiteValues);

      input[x] = LabValues[0];
      output[0][x] = -LabValues[1];
      output[1][x] = -LabValues[2];
    }
    input[size - 1] = 100;
    abLutArray = new Interpolation1DLUT[2];
    abLutArray[0] = new Interpolation1DLUT(input, output[0],
                                           Interpolation1DLUT.Algo.LINEAR);
    abLutArray[1] = new Interpolation1DLUT(input, output[1],
                                           Interpolation1DLUT.Algo.LINEAR);
  }

  protected void initRGBGrayBalance(List<Patch> grayScale) {
    int size = grayScale.size() + 2;
    double[][] input = new double[2][size];
    double[][] output = new double[2][size];
    for (int x = 1; x < size - 1; x++) {
      Patch p = grayScale.get(x - 1);
      p.getRGB().getValues(tmpValues, RGB.MaxValue.Double255);
      input[0][x] = tmpValues[0];
      input[1][x] = tmpValues[2];
      if (style == Style.RGBScale) {
        output[0][x] = tmpValues[1] / tmpValues[0];
        output[1][x] = tmpValues[1] / tmpValues[2];
      }
      else if (style == Style.RGBMap) {
        output[0][x] = tmpValues[1];
        output[1][x] = tmpValues[1];
      }

    }

    input[0][size - 1] = 255;
    input[1][size - 1] = 255;
    if (style == Style.RGBScale) {
      output[0][size - 1] = 1;
      output[1][size - 1] = 1;
    }
    else {
      output[0][size - 1] = 255;
      output[1][size - 1] = 255;
    }

    rgbLutArray = new Interpolation1DLUT[2];
    rgbLutArray[0] = new Interpolation1DLUT(input[0], output[0],
                                            Interpolation1DLUT.Algo.LINEAR);
    rgbLutArray[1] = new Interpolation1DLUT(input[1], output[1],
                                            Interpolation1DLUT.Algo.LINEAR);
  }

  public static void main(String[] args) throws IOException {
    GMBICCProfileAdapter profile = new GMBICCProfileAdapter(
        "Measurement Files/Camera/d100/d100.icc");

    LightSource.Source lightsource = LightSource.CIE.D65;
    DCChartAdapter chart = new DCChartAdapter(DCTarget.Chart.CC24, lightsource);
    DCTarget target = DCTarget.Instance.get(profile, chart, lightsource,
                                            DCTarget.Chart.CC24);
    GrayBalancer gb = new GrayBalancer(target.filter.grayScale(), Style.Lab);

    BufferedImage img = ImageUtils.loadImage("Image/Profile/d100/DSC_0293.JPG");
    BufferedImage result = gb.grayBalance(img);
    ImageUtils.storeJPEGImage("gb-ab.JPG", result);
  }

  public BufferedImage grayBalance(BufferedImage img) {
    if (style == Style.None) {
      return img;
    }

    BufferedImage clone = ImageUtils.cloneBufferedImage(img);
    int h = clone.getHeight();
    int w = clone.getWidth();
    double[] pixels = new double[3];
    if (style == Style.RGBMap || style == Style.RGBScale) {
      for (int x = 0; x < w; x++) {
        for (int y = 0; y < h; y++) {
          img.getRaster().getPixel(x, y, pixels);
          rgbGrayBalance(pixels);
          clone.getRaster().setPixel(x, y, pixels);
        }
      }
    }
    else if (style == Style.Lab) {
      if (rgbColorSpace == null) {
        throw new IllegalArgumentException("rgbColorSpace == null");
      }

      for (int x = 0; x < w; x++) {
        for (int y = 0; y < h; y++) {
          img.getRaster().getPixel(x, y, pixels);
          Maths.normalize(pixels, 255.);
          double[] XYZValues = RGB.toXYZValues(pixels, rgbColorSpace);
          double[] LabValues = CIELab.fromXYZValues(XYZValues, whiteValues);
          LabGrayBalance(LabValues);
          XYZValues = CIELab.toXYZValues(LabValues, whiteValues);
          double[] result = RGB.fromXYZValues(XYZValues, rgbColorSpace);
          RGB.rationalize(result, RGB.MaxValue.Double1);
          Maths.undoNormalize(result, 255.);
          clone.getRaster().setPixel(x, y, result);
        }
      }
    }

    return clone;
  }

  public RGB grayBalance(RGB rgb) {
    RGB clone = (RGB) rgb.clone();
    clone.getValues(tmpValues, RGB.MaxValue.Double255);
    rgbGrayBalance(tmpValues);
    clone.setValues(tmpValues, RGB.MaxValue.Double255);
    return clone;
  }

  public RGB unGrayBalance(RGB rgb) {
    RGB clone = (RGB) rgb.clone();
    clone.getValues(tmpValues, RGB.MaxValue.Double255);
    rgbUnGrayBalance(tmpValues);
    clone.setValues(tmpValues, RGB.MaxValue.Double255);
    return clone;
  }

  public List<Patch> grayBalance(List<Patch> patchList) {
    int size = patchList.size();
    List<Patch> result = new ArrayList<Patch> (size);
    for (Patch p : patchList) {
      RGB rgb = p.getRGB();
      if (rgb.isLegal()) {
        rgb.getValues(tmpValues, RGB.MaxValue.Double255);
        rgbGrayBalance(tmpValues);
      }
      RGB clone = (RGB) rgb.clone();
      if (rgb.isLegal()) {
        clone.setValues(tmpValues, RGB.MaxValue.Double255);
      }
      Patch gbPatch = new Patch(p.getName(), p.getXYZ(), p.getNormalizedXYZ(),
                                p.getLab(), clone, p.getSpectra(),
                                p.getReflectSpectra());
      result.add(gbPatch);
    }
    return result;
  }

  protected double[] tmpValues = new double[3];

  protected void LabGrayBalance(double[] LabValues) {
    LabValues[1] += abLutArray[0].getValue(LabValues[0]);
    LabValues[2] += abLutArray[1].getValue(LabValues[0]);
  }

  protected void rgbGrayBalance(double[] rgbValues) {
    if (style == Style.RGBScale) {
      rgbValues[0] = rgbLutArray[0].getValue(rgbValues[0]) * rgbValues[0];
      rgbValues[2] = rgbLutArray[1].getValue(rgbValues[2]) * rgbValues[2];
    }
    else if (style == Style.RGBMap) {
      rgbValues[0] = rgbLutArray[0].getValue(rgbValues[0]);
      rgbValues[2] = rgbLutArray[1].getValue(rgbValues[2]);
    }
  }

  protected void rgbUnGrayBalance(double[] rgbValues) {
    if (style == Style.RGBScale) {
      rgbValues[0] = rgbValues[0] / rgbLutArray[0].getValue(rgbValues[0]);
      rgbValues[2] = rgbValues[2] / rgbLutArray[1].getValue(rgbValues[2]);
    }
    else if (style == Style.RGBMap) {
      rgbValues[0] = rgbLutArray[0].getKey(rgbValues[0]);
      rgbValues[2] = rgbLutArray[1].getKey(rgbValues[2]);
    }
  }

  public Interpolation1DLUT[] getRGBLutArray() {
    return rgbLutArray;
  }

  protected Style style;

  public static enum Style {
    //無gb
    None,
    //RGB空間作gb, 修RGB
    RGBMap,
    RGBScale,
    //從RGB空間轉到獨立色空間, 以色適應矩陣作gb. 先不要做
    RGBCAT,
    //在獨立色空間以 紅綠 黃藍 作gb, 修Lab
    Lab
  }
}
