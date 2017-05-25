package shu.cms.colorspace.depend;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.independ.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �]�Ƭ̦ۨ�Ŷ������Ψ禡
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
    //�]�wrgb
    RGB rgb = new RGB(RGB.ColorSpace.AdobeRGB, new int[] {10, 100, 200});
    //�����W��
    rgb.changeMaxValue(RGB.MaxValue.Double1);
    //��ܥ��W�ƫ᪺���G
    System.out.println(rgb);
    //�Nrgb��XYZ
    CIEXYZ XYZ = RGB.toXYZ(rgb);
    //���XYZ
    System.out.println(XYZ);
    //�qXYZ��^rgb
    RGB rgb2 = RGB.fromXYZ(XYZ, RGB.ColorSpace.AdobeRGB);
    //��^��rgb
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
   * �̤j�Ȧ��H�U�X��:
   * Double1 1.0(range:0.0~1.0 �B�I��)
   * Double1 100(range:0.0~100.0 �B�I��)
   * Int8Bit 255(range:0~255 ���)
   * Double255 255(range:0.0~255.0 �B�I��)
   * DoubleUnlimited �L����(range: ?~? �B�I��)
   * Composite �ƦX���A(�pLab,L 0~100,a/b -128.0~127.0)
   *
   * �o�O���F�]�����P�ഫ�禡��,�|�����P�����G,���F��K�ഫ,�ҥ[�����O
   * (MaxValue��getCodeBit? ,CodeBit�OLCDTarget�����Ϊ�,�ҥH���Ӥ�����i��.
   * ���i�檺��k�OCodeBit��getMaxValue)
   */
  public static enum MaxValue {
    Double1(1.), //���W��
    Double100(100.), //���W��
    Double255(255.), //�U��bit�ƪ�RGB code�q��
    Double360(360.),
    Double1020(1020, false, true), //10bit
    Double4080(4080, false, true), //12bit
    DoubleUnlimited(Double.MAX_VALUE), //�L����

    Int5Bit(31., true, false), //5bit
    Int6Bit(63., true, false), //6bit
    Int100(100, true, false),
    Int7Bit(127., true, false), //7bit
    Int8Bit(255., true, true), //�@��`�Ϊ�RGB code
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
     * �Hlevel���⦨��ƪ�MaxValue
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
     * �Hmax���⦨��ƪ�MaxValue
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
     * �i���ɪ�
     */
    public final boolean divisible;
  }

  /**
   * �Ψ��ഫ��ƫ��A��MaxValue,�i�H�קK���W�Ƥp���I�����D
   * @param integerValues double[]
   * @param srcType MaxValue
   * @param destType MaxValue
   * @param roundDown boolean
   * @return double[]
   */
  protected final static double[] changeIntegerMaxValue(double[] integerValues,
      MaxValue srcType, MaxValue destType, boolean roundDown) {
    if (false == srcType.integer && false == destType.integer) {
      //�J�M�O�B�z��ƪ��ഫ, ��M�ܤ֭n���@�ӬO��ư�~
      throw new IllegalArgumentException(
          "false == srcType.integer && false == destType.integer");
    }
    double rate = -1;

    if (true == srcType.integer && false == srcType.divisible &&
        destType.max == 255) {
      //�B�z�p��8bit, �]�N�O�S��k�㰣������
      rate = (destType.max + 1) / srcType.max;
    }
    else if (true == destType.integer && false == destType.divisible &&
             srcType.max == 255) {
      //�B�z�p��8bit, �]�N�O�S��k�㰣������
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
      //�p��8bit���ഫ, �|���W�Lmax�����p, �ҥH�n�@clip
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
