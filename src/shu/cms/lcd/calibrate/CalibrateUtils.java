package shu.cms.lcd.calibrate;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;

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
public class CalibrateUtils {
  public static enum CrashFixType {
    Linear,
    SPline
  }

  public static class Crash {

    /**
     * 修正code有crash現象的狀況
     * @param rgbArray RGB[]
     * @param crashStartIndex int
     * @param type CrashFixType
     * @return boolean
     */
    public final static boolean crashFix(RGB[] rgbArray, int crashStartIndex,
                                         CrashFixType type) {
      boolean crashed = false;

      for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
        crashed = crashed || crashFix(rgbArray, ch, crashStartIndex, type);
      }
      return crashed;
    }

    public final static boolean crashFix(RGB[] rgbArray, RGB.Channel ch,
                                         int crashStartIndex,
                                         CrashFixType type) {
      boolean crashed = false;

      for (int x = crashStartIndex; x > 0; x--) {
        RGB rgb = rgbArray[x];
        RGB nextrgb = rgbArray[x + 1];
        if (rgb.getValue(ch) == 0) {
          crashed = true;
          double interp = 0;
          double next = nextrgb.getValue(ch);

          switch (type) {
            case SPline: {
              double next2 = rgbArray[x + 2].getValue(ch);
              double next3 = rgbArray[x + 3].getValue(ch);
              interp = Interpolation.spline2(new double[] {0, x + 1,
                                             x + 2, x + 3}, new double[] {0,
                                             next, next2, next3}, x);
              break;
            }
            case Linear: {
              interp = Interpolation.linear2(new double[] {0,
                                             x + 1},
                                             new double[] {0, next},
                                             x);
              break;
            }
          }

          rgb.setValue(ch, interp);
        }
      }
      return crashed;
    }

    /**
     * 找到crash的起始索引值 (除了第一個code以外的code有0即為crash)
     * @param rgbArray RGB[]
     * @return int
     */
    private final static int getCrashStartIndex(RGB[] rgbArray) {
      int size = rgbArray.length;
      //由尾找到頭, 如果有0值就是crash
      for (int x = size - 1; x > 0; x--) {
        RGB rgb = rgbArray[x];
        if (rgb.hasZeroChannel()) {
          return x;
        }
      }
      return -1;
    }

    private final static int getCrashStartIndex(RGB[] rgbArray, RGB.Channel ch) {
      int size = rgbArray.length;
      //由尾找到頭, 如果有0值就是crash
      for (int x = size - 1; x > 0; x--) {
        RGB rgb = rgbArray[x];
        if (rgb.getValue(ch) == 0) {
          return x;
        }
      }
      return -1;
    }

    /**
     * 修正code有crash現象的狀況.
     * crash定義為: 由尾找到頭, 如果有0值就是crash(當然頭常為0, 因此頭略過)
     * @param rgbArray RGB[]
     * @return boolean
     */
    public final static boolean crashFix(RGB[] rgbArray) {
      int index = getCrashStartIndex(rgbArray);
      return crashFix(rgbArray, index, CrashFixType.Linear);
    }

    public final static boolean crashFix(RGB[] rgbArray, RGB.Channel ch) {
      int index = getCrashStartIndex(rgbArray, ch);
      return crashFix(rgbArray, ch, index, CrashFixType.Linear);
    }
  }

  /**
   * 量化崩潰修復
   * 如果發現有量化崩潰的情形(前後兩個code相等), 將崩潰點後的code加一個單位.
   * @param rgbArray RGB[]
   * @param maxValue MaxValue
   * @return RGB[]
   */
  public final static RGB[] quantization(RGB[] rgbArray, RGB.MaxValue maxValue) {
    int size = rgbArray.length;
    RGB[] quantize = RGBArray.deepClone(rgbArray);
    for (int x = 0; x < size; x++) {
      RGB rgb = quantize[x];
      rgb.quantization(maxValue);
      quantize[x] = rgb;
    }
    return quantize;
  }

  public static void main(String[] args) {
    RGB[] rgbArray = new RGB[] {
        new RGB(0, 0, 0), new RGB(1, 1, 1), new RGB(1, 1, 1), new RGB(1, 1, 1),
        new RGB(1, 1, 1), new RGB(1, 1, 1), new RGB(1, 7, 1)
    };
    quantizationCollapseFix(rgbArray, RGB.Channel.G, RGB.MaxValue.Int8Bit, true, true,
                            RGB.MaxValue.Int8Bit.max);
    for (RGB rgb : rgbArray) {
      System.out.println(rgb.G);
    }

  }

  /**
   * 量化崩潰修復
   * 如果發現有量化崩潰的情形(前後兩個code相等), 將崩潰點後的code加一個單位.
   * @param rgbArray RGB[]
   * @param maxValue MaxValue
   * @param concernFixable boolean 是否要考量可修復性
   * @param max double
   */
  public final static void quantizationCollapseFix(RGB[] rgbArray,
      RGB.MaxValue maxValue, boolean concernFixable, double max) {
    quantizationCollapseFix(rgbArray, RGB.Channel.R, maxValue, false,
                            concernFixable, max);
    quantizationCollapseFix(rgbArray, RGB.Channel.G, maxValue, false,
                            concernFixable, max);
    quantizationCollapseFix(rgbArray, RGB.Channel.B, maxValue, false,
                            concernFixable, max);
  }

  public final static void quantizationCollapseFix(RGB[] rgbArray,
      RGB.MaxValue maxValue, boolean concernFixable, double[] maxArray) {
    quantizationCollapseFix(rgbArray, RGB.Channel.R, maxValue, false,
                            concernFixable, maxArray[0]);
    quantizationCollapseFix(rgbArray, RGB.Channel.G, maxValue, false,
                            concernFixable, maxArray[1]);
    quantizationCollapseFix(rgbArray, RGB.Channel.B, maxValue, false,
                            concernFixable, maxArray[2]);
  }

  public final static void quantizationCollapseFix(RGB[] rgbArray,
      RGBBase.Channel ch, RGB.MaxValue maxValue, boolean force8BitFix,
      boolean concernFixable, double max) {
    if (!ch.isPrimaryColorChannel()) {
      throw new IllegalArgumentException("!ch.isPrimaryColorChannel()");
    }
    if (!force8BitFix && maxValue.max == RGB.MaxValue.Int8Bit.max) {
      //8bit要崩潰是常有的事情, 所以不修正
      return;
    }
    int size = rgbArray.length - 1;

    for (int x = 0; x < size - 1; x++) {
      int nextIndex = x + 1;
      RGB now = rgbArray[x];
      RGB next = rgbArray[nextIndex];
      double nowv = now.getValue(ch, maxValue);
      double nextv = next.getValue(ch, maxValue);

      if (nowv == nextv) {
        RGB next2 = rgbArray[nextIndex + 1];
        double next2v = next2.getValue(ch, maxValue);
        boolean canFix = (! (nextv == next2v || (nextv + 1) == next2v)) ||
            !concernFixable;
        if (canFix) {
          //崩潰點
          for (int y = x + 1; y < size; y++) {
            RGB rgb = rgbArray[y];
            double v = rgb.getValue(ch, maxValue);
            if (nextv == v || (nextv + 1) == v) {
              v++;
              if (v >= max) {
                continue;
              }
              rgb.setValue(ch, v, maxValue);
            }
            else {
              break;
            }
          }
        }
      }
    }
  }

  /**
   * 儲存成二進位檔案, 主要是供CPLoader使用
   * @param rgbArray RGB[]
   * @param filename String
   * @param maxValue MaxValue
   */
  public final static void storeRGBArrayBinaryFile(RGB[] rgbArray,
      String filename, RGB.MaxValue maxValue) {
    String path = RootDir + "/" + filename;
    RGBArray.storeBinaryFile(rgbArray, path, maxValue);
  }

  private static String RootDir = ".";
  public final static void setRootDir(String root) {
    RootDir = root;
  }

  /**
   * 儲存校正後的結果為excel檔
   * @param rgbArray RGB[]
   * @param filename String
   */
  public final static void storeRGBArrayExcel(RGB[] rgbArray,
                                              String filename) {
    storeRGBArrayExcel(rgbArray, filename, ColorProofParameter.Format.VastView,
                       RGB.MaxValue.Double255);
  }

  public final static void storeRGBArrayExcel(RGB[] rgbArray,
                                              String filename,
                                              ColorProofParameter cp) {
    storeRGBArrayExcel(rgbArray, filename, cp.outputFileFormat, cp.outputBits);
  }

  public final static void storeRGBArrayExcel(RGB[] rgbArray,
                                              String filename,
                                              ColorProofParameter.Format format,
                                              RGB.MaxValue outputBits) {
    String path = RootDir + "/" + filename;
    switch (format) {
      case VastView:
        RGBArray.storeVVExcel(rgbArray, path, outputBits);
        break;
      case AUO:
        RGBArray.storeAUOExcel(rgbArray, path, outputBits);
        break;
    }
  }

  public static class Delta {
    /**
     * 取得target與reference在xyY上的距離
     * @param target CIExyY
     * @param reference CIExyY
     * @return double
     */
    public static double getDeltaxyYDistance(CIExyY target, CIExyY reference) {
      double[] dxy = target.getDeltaxy(reference);
      double dY = target.Y - reference.Y;
      double dist = Math.sqrt(Maths.sqr(dxy[0]) + Maths.sqr(dxy[1]) +
                              Maths.sqr(dY));
      return dist;
    }

    /**
     * 取得target與reference在xy上的距離
     * @param target CIExyY
     * @param reference CIExyY
     * @return double
     */
    public static double getDeltaxyDistance(CIExyY target, CIExyY reference) {
      double[] dxy = target.getDeltaxy(reference);
      double dist = Math.sqrt(Maths.sqr(dxy[0]) + Maths.sqr(dxy[1]));
      return dist;
    }

    /**
     * 從patchList找到與targetxyY在xyY空間上最近距離的patch
     * @param targetxyY CIExyY
     * @param patchList List
     * @return Patch
     */
    protected final static Patch getNearestxyYPatch(CIExyY targetxyY,
        List<Patch>
        patchList) {
      int size = patchList.size();
      double minDist = Double.MAX_VALUE;
      Patch minDistPatch = null;

      for (int x = 0; x < size; x++) {
        Patch p = patchList.get(x);
        CIExyY xyY = new CIExyY(p.getXYZ());
        double dist = getDeltaxyYDistance(xyY, targetxyY);
        if (dist < minDist) {
          minDistPatch = p;
          minDist = dist;
        }
      }

      return minDistPatch;
    }

    /**
     * 找到u'v'Y空間上最接近targetxyY的patch
     * @param targetxyY CIExyY
     * @param patchList List
     * @return Patch
     */
    public final static Patch getNearestuvPrimeYPatch(CIExyY targetxyY,
        List<Patch> patchList) {
      int size = patchList.size();
      double minDist = Double.MAX_VALUE;
      Patch minDistPatch = null;

      for (int x = 0; x < size; x++) {
        Patch p = patchList.get(x);
        CIExyY xyY = new CIExyY(p.getXYZ());
        double dist = getDeltauvprimeYDistance(xyY, targetxyY);
        if (dist < minDist) {
          minDistPatch = p;
          minDist = dist;
        }
      }

      return minDistPatch;
    }

    /**
     * 計算target與reference之間在uv'上的距離
     * @param target CIExyY
     * @param reference CIExyY
     * @return double
     */
    public static double getDeltauvprimeDistance(CIExyY target,
                                                 CIExyY reference) {
      double[] duvp = target.getDeltauvPrime(reference);
      double dist = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]));
      return dist;
    }

    /**
     * 取得u'v'Y空間上的距離
     * @param target CIExyY
     * @param reference CIExyY
     * @return double
     */
    public static double getDeltauvprimeYDistance(CIExyY target,
                                                  CIExyY reference) {
      double[] duvp = target.getDeltauvPrime(reference);
      double dY = target.Y - reference.Y;
      double dist = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]) +
                              Maths.sqr(dY));
      return dist;
    }
  }

  /**
   * 採用線性內插擴展資料量為原始的兩倍
   * @param data double[]
   * @return double[]
   */
  private static double[] interpolation(final double[] data) {
    int size = data.length;
    int interpsize = size * 2 - 1;
    double[] interpdata = new double[interpsize];

    for (int x = 0; x < size - 1; x++) {
      interpdata[x * 2 + 1]
          = Interpolation.linear(0, 2, data[x], data[x + 1], 1);
    }
    for (int x = 0; x < size; x++) {
      interpdata[x * 2] = data[x];
    }
    return interpdata;
  }

  /**
   * 將data以algo的方式內插得到預測值
   * @param data double[]
   * @param algo Algo
   * @return double[]
   */
  public static double[] predictDataByInterpolation(final double[] data,
      Interpolation.Algo algo) {
    int size = data.length;
    double[] interpdata = interpolation(data);
    double[] predict = new double[size];

    //頭尾不用內插
    predict[0] = data[0];
    predict[size - 1] = data[size - 1];

    int interp2Size = size - 1;
    double[] interpdata2 = new double[interp2Size];
    for (int x = 0; x < size - 1; x++) {
      interpdata2[x] = interpdata[2 * x + 1];
    }

    double[] xn = DoubleArray.buildX(0, interp2Size - 1, interp2Size);
    Interpolation interp = new Interpolation(xn, interpdata2);

    for (int x = 0; x < size - 2; x++) {
      predict[x + 1] = interp.interpolate(x + 0.5, algo);
    }
    return predict;
  }

  /**
   * 檢查目錄是否存在, 不存在則mkdir
   * @param dirname String
   * @return boolean
   */
  public final static boolean checkDir(String dirname) {
    File dir = new File(dirname);
    if (!dir.exists()) {
      return dir.mkdir();
    }
    else if (!dir.isDirectory()) {
      throw new IllegalStateException(dir +
                                      " is exists but is not a directory.");
    }
    return true;
  }

}
