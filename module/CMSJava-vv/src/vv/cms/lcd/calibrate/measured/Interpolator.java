package vv.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.measure.cp.*;
import vv.cms.measure.cp.MeasureResult;
import shu.cms.util.*;
import shu.math.*;
import shu.util.*;

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
public class Interpolator {
  public static enum Mode {
    Linear(1), Quadratic(2),
    LinearAtJNDI(1), QuadraticAtJNDI(2);

    private Mode(int n) {
      this.n = n;
    }

    private int n;
  }

  public static void main(String[] args) {
    boolean[] bools = new boolean[] {
        true, false, false, false, true, false, true};
//    int i = getCalibratedIndex(false, 2, bools);
    int[] is = getNonInterpolateIndex(false, 5, bools, 2);
    System.out.println(Arrays.toString(is));

    System.out.println(bools[ -1]);
  }

  private Mode mode = Mode.Linear;
  private JNDIInterface jndi;
  private MeasureInterface mi;

  public Interpolator(Mode mode) {
    this.mode = mode;
  }

  public Interpolator() {
    this(Mode.Linear);
  }

  /**
   * 找到n個
   * @param forward boolean
   * @param startIndex int
   * @param interpolate boolean[]
   * @param n int
   * @return int[]
   */
  private final static int[] getNonInterpolateIndex(boolean forward,
      int startIndex,
      boolean[] interpolate, int n) {
    int[] result = new int[n];
    int index = startIndex;
    for (int x = 0; x < n; x++) {
      int v = getNonInterpolateIndex(forward, index, interpolate);
      v = (v < 0 || v >= interpolate.length) ? -1 : v;
      result[x] = v;
      index = forward ? result[x] - 1 : result[x] + 1;
    }
    return result;
  }

  /**
   *
   * @param forward boolean
   * @param startIndex int
   * @param interpolate boolean[]
   * @return int
   */
  private final static int getNonInterpolateIndex(boolean forward,
                                                  int startIndex,
                                                  boolean[] interpolate) {
    int index = 0;
    int step = forward ? -1 : 1;
    for (index = startIndex;
         index >= 0 && index < interpolate.length && interpolate[index] == true;
         index += step) {
    }
    return index;
  }

  private static int[] merge(int[] array1, int[] array2) {
    int size = array1.length + array2.length;
    int[] result = new int[size];
    System.arraycopy(array1, 0, result, 0, array1.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);
    return result;
  }

  private static double interpolateInCode(RGB[] rgbArray, int index,
                                          InterpolateMaterial material,
                                          RGB.Channel ch) {
    double[][] xnyn = getxnyn(ch, rgbArray, material.n, material.indexArray);
    double[] xn = xnyn[0];
    double[] yn = xnyn[1];
    switch (material.mode) {
      case Linear:
        return Interpolation.linear(xn, yn, index);
      case Quadratic:
        return Interpolation.quadraticPolynomail(xn, yn, index);
      default:
        return -1;
    }
  }

  private static double interpolateInJNDI(RGB[] rgbArray, int index,
                                          InterpolateMaterial material,
                                          RGB.Channel ch) {
    /**
     * 將要被內插的code, 先找到他相鄰可用來內插的code, 然後實際去量測他, 得到XYZ值, 再換成JNDI值.
     * 得到JNDI後, 內插然後得到內插的JNDI, 把JNDI推回Y, 再利用Y內插回code. 此code就是最後的內插結果.
     * 問題出在JNDI如何推回Y.
     *
     * 一般內插的方式是用盡可能密集的, 單一頻道的XYZ與JNDI建立對照表. 不過內插的關係, 只有二或四個點可以作內插.
     */
    double[][] xnyn = getxnyn(ch, rgbArray, material.n, material.indexArray);
    double[] indexArray = xnyn[0];
    double[] codeArray = xnyn[1];
    int size = indexArray.length;
//    switch (material.mode) {
//        case Linear:
//          return Interpolation.linear(xn, yn, index);
//        case Cubic:
//          return Interpolation.cubicPolynomial(xn, yn, index);
//        case Quadratic:
//          return Interpolation.quadraticPolynomail(xn, yn, index);
//        default:
//          return -1;
//    }
    return -1;
  }

  private static class InterpolateMaterial {
    private InterpolateMaterial(int[] indexArray, int n, Mode mode) {
      this.indexArray = indexArray;
      this.n = n;
      this.mode = mode;
    }

    int[] indexArray;
    int n;
    Mode mode;
  }

  private static InterpolateMaterial getInterpolateMaterial(Mode mode,
      boolean[] interpolate, int index) {
    int n = mode.n;
    Mode realmode = mode;

    //==========================================================================
    // 抓出相近的index
    //==========================================================================
    //往前找到最近且不用被內插的值(實際存在不需內插)
    int[] preIndexArray = getNonInterpolateIndex(true, index, interpolate, n);
    int preIllegalIndex = Searcher.sequentialSearch(preIndexArray, -1);
    int[] nextIndexArray = getNonInterpolateIndex(false, index, interpolate, n);
    int nextIllegalIndex = Searcher.sequentialSearch(nextIndexArray, -1);
    int[] indexArray = merge(preIndexArray, nextIndexArray);
    if (preIllegalIndex >= 0 || nextIllegalIndex >= 0) {
      if (realmode == Mode.Linear) {
        indexArray = new int[] {
            preIllegalIndex >= 0 ? 0 : preIndexArray[0],
            nextIllegalIndex >= 0 ? 255 : nextIndexArray[0]};
      }
      else {
        n = 1;
        realmode = Mode.Linear;
        indexArray = new int[] {
            preIndexArray[0], nextIndexArray[0]};
      }
    }
    //==========================================================================

    Arrays.sort(indexArray, 0, indexArray.length);
    InterpolateMaterial material = new InterpolateMaterial(indexArray, n,
        realmode);
    return material;
  }

  private RGB interpolate(RGB[] rgbArray, boolean[] interpolate, int index,
                          Mode mode) {
    InterpolateMaterial material = getInterpolateMaterial(mode, interpolate,
        index);
    RGB result = (RGB) rgbArray[index].clone();

    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      switch (material.mode) {
        case Linear:
        case Quadratic: {
          double v = interpolateInCode(rgbArray, index, material, ch);
          result.setValue(ch, v);
          break;
        }
        case LinearAtJNDI:
        case QuadraticAtJNDI: {
          double v = interpolateInJNDI(rgbArray, index, material, ch);
          result.setValue(ch, v);
          break;
        }
      }
    }
    return result;
  }

  private static double[][] getxnyn(RGB.Channel ch, RGB[] rgbArray, int n,
                                    int[] indexArray) {
    int width = n * 2;
    width = width > 3 ? 3 : width;
    double[] xn = new double[width];
    double[] yn = new double[width];
    for (int x = 0; x < width; x++) {
      int index = indexArray[x];
      xn[x] = index;
      yn[x] = rgbArray[index].getValue(ch);
    }

    return new double[][] {
        xn, yn};
  }

  /**
   *
   * @param rgbArray RGB[]
   * @param interpolate boolean[] true=代表要被內插的值
   * @param calibrateBits MaxValue
   * @return RGB[]
   */
  public RGB[] interpolateResult(final RGB[] rgbArray,
                                 final boolean[] interpolate,
                                 RGBBase.MaxValue calibrateBits) {
    return interpolateResult(rgbArray, interpolate, calibrateBits, mode);
  }

  public RGB[] interpolateResult(final RGB[] rgbArray,
                                 final boolean[] interpolate,
                                 RGBBase.MaxValue calibrateBits, Mode mode) {
    if (rgbArray.length != interpolate.length) {
      throw new IllegalArgumentException("rgbArray.length != calibrated.length");
    }
    RGB[] result = RGBArray.deepClone(rgbArray);
    fetchMeasureData(rgbArray, interpolate);

    for (int x = 1; x < result.length - 1; x++) {
      if (true == interpolate[x]) {
        result[x] = interpolate(rgbArray, interpolate, x, mode);
        result[x].quantization(calibrateBits);
      }
    }
    return result;

  }

  private void fetchMeasureData(final RGB[] rgbArray,
                                final boolean[] interpolate) {

    /**
     * 1. 以初始量測資料來做內插
     * 2. 重新量測新資料來做內插, 包括ramp256以及不需內插的值
     */
    if (measureData == MeasureData.ByNewMeasure) {
      RGB[] measureRGB = getMeasureRGBArray(rgbArray, interpolate);
      if (mi == null) {
        throw new IllegalStateException("mi == null");
      }
      else {
        MeasureResult measureResult = mi.measureResult(measureRGB, true, true);
        List<Patch> patchList = measureResult.result;
        rMeasureData = new LinkedList<Patch> ();
        gMeasureData = new LinkedList<Patch> ();
        bMeasureData = new LinkedList<Patch> ();
        Patch.Filter.oneValueChannel(patchList, rMeasureData, RGB.Channel.R);
        Patch.Filter.oneValueChannel(patchList, gMeasureData, RGB.Channel.G);
        Patch.Filter.oneValueChannel(patchList, bMeasureData, RGB.Channel.B);
      }
    }
    else {

    }

  }

  private List<Patch> rMeasureData;
  private List<Patch> gMeasureData;
  private List<Patch> bMeasureData;

  private RGB[] getMeasureRGBArray(final RGB[] rgbArray,
                                   final boolean[] interpolate) {
    int size = interpolate.length;
    Set<RGB> measureRGBSet = new TreeSet<RGB> ();
    for (int x = 0; x < size; x++) {
      if (interpolate[x] == false) {
        for (RGB.Channel ch : RGB.Channel.RGBChannel) {
          RGB rgb = (RGB) rgbArray[x].clone();
          rgb.reserveValue(ch);
          measureRGBSet.add(rgb);
        }
      }
    }
    List<RGB>
        rampRGBList = LCDTargetBase.Instance.get(LCDTargetBase.Number.
                                                 Ramp256RGB_W).filter.rgbList();
    measureRGBSet.addAll(rampRGBList);
    return measureRGBSet.toArray(new RGB[measureRGBSet.size()]);
  }

//  private MeasureData measureData = MeasureData.ByNewMeasure;
  private MeasureData measureData = MeasureData.ByOriginalRamp;
  private static enum MeasureData {
    ByOriginalRamp, ByNewMeasure
  }

  public void setJNDI(JNDIInterface jndi) {
    this.jndi = jndi;
  }

  public void setMeasureInterface(MeasureInterface mi) {
    this.mi = mi;
  }
}
