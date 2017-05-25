package shu.cms.colorspace.depend;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.independ.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 設備相依色空間的公用函式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class DeviceDependentSpace
    extends ColorSpace {

  public static void example(String[] args) {
    //設定rgb
    RGB rgb = new RGB(RGB.ColorSpace.AdobeRGB, new int[] {10, 100, 200});
    //做正規化
    rgb.changeMaxValue(RGB.MaxValue.Double1);
    //顯示正規化後的結果
    System.out.println(rgb);
    //將rgb轉XYZ
    CIEXYZ XYZ = RGB.toXYZ(rgb);
    //顯示XYZ
    System.out.println(XYZ);
    //從XYZ轉回rgb
    RGB rgb2 = RGB.fromXYZ(XYZ, RGB.ColorSpace.AdobeRGB);
    //轉回之rgb
    System.out.println(rgb2);

  }

  public final static RGB XYZ2LinearRGB(CIEXYZ XYZ,
                                        RGB.ColorSpace colorSpace) {
    double[] rgbValues = XYZ2LinearRGBValues(XYZ.getValues(), colorSpace);
    RGB rgb = new RGB(colorSpace, rgbValues, RGB.MaxValue.Double1);
    return rgb;
  }

  public final static double[] XYZ2LinearRGBValues(double[] XYZValues,
      RGBBase.ColorSpace colorSpace) {
    double[][] aM = colorSpace.toRGBMatrix;
    double[] rgbValues = DoubleArray.times(XYZValues, aM);

    return rgbValues;
  }

  public static void main(String[] args) {
    /*int a = 4;
         System.out.println(getBit(a, 3));
         int b = setBit(a, 1, 1);
         System.out.println( b );
         b = setBit(b, 3, 0);
         System.out.println( b );*/
    int a = 0;
    for (int x = 1; x <= 8; x++) {
      a = setBit(a, x, 1);
    }
    System.out.println(a);
  }

  static int setBit(int source, int bit, int data) {
    return data << (bit - 1) & 255 | ~ (1 << (bit - 1)) & source;
  }

  static int getBit(int source, int bit) {
    return source >> (bit - 1) & 1;
  }

  /**
   * 最大值有以下幾種:
   * Double1 1.0(range:0.0~1.0 浮點數)
   * Double1 100(range:0.0~100.0 浮點數)
   * Int8Bit 255(range:0~255 整數)
   * Double255 255(range:0.0~255.0 浮點數)
   * DoubleUnlimited 無限制(range: ?~? 浮點數)
   * Composite 複合型態(如Lab,L 0~100,a/b -128.0~127.0)
   *
   * 這是為了因應不同轉換函式間,會有不同的結果,為了方便轉換,所加的註記
   * (MaxValue裡getCodeBit? ,CodeBit是LCDTarget內部用的,所以應該比較不可行.
   * 較可行的方法是CodeBit裡getMaxValue)
   */
  public static enum MaxValue {
    Double1(1.), //正規化
    Double100(100.), //正規化
    Double255(255.), //各種bit數的RGB code通用
    Double360(360.),
    Double1020(1020, false, true), //10bit
    Double4080(4080, false, true), //12bit
    DoubleUnlimited(Double.MAX_VALUE), //無限制

    Int5Bit(31., true, false), //5bit
    Int6Bit(63., true, false), //6bit
    Int100(100, true, false),
    Int7Bit(127., true, false), //7bit
    Int8Bit(255., true, true), //一般常用的RGB code
    Int360(360, true, false),
    Int9Bit(510., true, true), //9bit
    Int10Bit(1020, true, true), //10bit
    Int11Bit(2040, true, true), //11bit
    Int12Bit(4080, true, true), //12bit
    Int13Bit(8160, true, true), //13bit
    Int14Bit(16320, true, true), //14bit
    Int15Bit(32640, true, true), //15bit
    Int16Bit(65280, true, true), //16bit
    Int20Bit(1044480, true, true), //20bit
    Int24Bit(16711680, true, true), //24bit
    Int31Bit(2139095040, true, true); //31bit

    /**
     * 以level換算成整數的MaxValue
     * @param level int
     * @return MaxValue
     */
    public final static MaxValue getIntegerMaxValueByLevel(int level) {

      for (MaxValue maxValue : MaxValue.values()) {
        if (maxValue.integer && maxValue.max == (level - 1)) {
          return maxValue;
        }
      }
      return null;
    }

    /**
     * 以max換算成整數的MaxValue
     * @param max int
     * @return MaxValue
     */
    public final static MaxValue getIntegerMaxValueByMax(int max) {

      for (MaxValue maxValue : MaxValue.values()) {
        if (maxValue.integer && maxValue.max == max) {
          return maxValue;
        }
      }
      return null;
    }

    MaxValue(double max, boolean integer, boolean divisible) {
      this.max = max;
      this.integer = integer;
      this.divisible = divisible;
    }

    MaxValue(double max) {
      this(max, false, false);
    }

    public final double getStepIn255() {
      return 255. / this.max;
    }

    public final double max;
    public final boolean integer;
    /**
     * 可除盡的
     */
    public final boolean divisible;
  }

  /**
   * 用來轉換整數型態的MaxValue,可以避免正規化小數點的問題
   * @param integerValues double[]
   * @param srcType MaxValue
   * @param destType MaxValue
   * @param roundDown boolean
   * @return double[]
   */
  protected final static double[] changeIntegerMaxValue(double[] integerValues,
      MaxValue srcType, MaxValue destType, boolean roundDown) {
    if (false == srcType.integer && false == destType.integer) {
      //既然是處理整數的轉換, 當然至少要有一個是整數啦~
      throw new IllegalArgumentException(
          "false == srcType.integer && false == destType.integer");
    }
    double rate = -1;

    if (true == srcType.integer && false == srcType.divisible &&
        destType.max == 255) {
      //處理小於8bit, 也就是沒辦法整除的部份
      rate = (destType.max + 1) / srcType.max;
    }
    else if (true == destType.integer && false == destType.divisible &&
             srcType.max == 255) {
      //處理小於8bit, 也就是沒辦法整除的部份
      rate = (destType.max + 1) / (srcType.max + 1);
    }
    else {
      rate = destType.max / srcType.max;
    }
    int size = integerValues.length;

    for (int x = 0; x < size; x++) {
      integerValues[x] *= rate;
      integerValues[x] = destType.integer ?
          (roundDown ? (int) integerValues[x] : Math.round(integerValues[x])) :
          integerValues[x];
      //小於8bit的轉換, 會有超過max的狀況, 所以要作clip
      integerValues[x] = (integerValues[x] > destType.max) ? destType.max :
          integerValues[x];
    }

    return integerValues;
  }

  private final static double[] changeMaxValue(double[] normal100,
                                               MaxValue type,
                                               boolean integerRoundDown) {
    int size = normal100.length;
    if (type.integer == true) {
      for (int x = 0; x < size; x++) {
        normal100[x] /= (100. / type.max);
        normal100[x] = integerRoundDown ? (int) normal100[x] :
            Math.round(normal100[x]);
      }
    }
    else {
      for (int x = 0; x < size; x++) {
        normal100[x] /= (100. / type.max);
      }
    }
    return normal100;
  }

  protected final static double[] normalizeTo100(double[] values,
                                                 MaxValue maxValue) {
    double max = maxValue.max;
    int size = values.length;
    for (int x = 0; x < size; x++) {
      values[x] = values[x] / max * 100;
    }
    return values;
  }

  protected final static void changeMaxValue(double[] values, MaxValue srcType,
                                             MaxValue destType,
                                             boolean integerRoundDown) {
    if (srcType.integer == true) {
      changeIntegerMaxValue(values, srcType, destType, integerRoundDown);
    }
    else {
      values = normalizeTo100(values, srcType);
      changeMaxValue(values, destType, integerRoundDown);
    }
  }

}
