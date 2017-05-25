package auo.cms.hsv.value;

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
public class FastInegerValueFormula
    implements ValueFormulaIF {
  /**
   * getV
   *
   * @param max short
   * @param min short
   * @param adjustValue short
   * @return short
   */
  public short getV(short max, short min, short adjustValue) {
    short v = (short) getV(max, min, adjustValue, (short) 1023, 1, 8, 10);
    return v;
  }

  static long _offsetXmax_min20Xmax_bar10;
  static long _offsetXmax_minXmax_bar30;
  static int _max, _min;
  static long staticResult;
  static long staticShiftResult;

  public static int getV(short max, short min, short offset, short minusMax,
                         int bit1, int bit2, int bit3) {
    int offset10 = true ? offset : offset << 4; //6+4=10
    boolean negative = offset < 0;
    offset10 = Math.abs(offset10);
    int max_min10 = max - min; //10bit
    int max_bar10 = minusMax - max; //10bit bar的取法會導致最後結果為10 or 11bit

    // V * (1 + offset*(max-min)*V)

    long offsetXmax_min20 = (offset10 * max_min10) >> bit3; // 20 - bit3 bit

    long offsetXmax_min20Xmax_bar10 = offsetXmax_min20 * max_bar10; //最大只到 20 - bit3 + 7 bit
    _offsetXmax_min20Xmax_bar10 = offsetXmax_min20Xmax_bar10 >
        _offsetXmax_min20Xmax_bar10 ? offsetXmax_min20Xmax_bar10 :
        _offsetXmax_min20Xmax_bar10; //以bit3=10來說, 為17bit

    long offsetXmax_minXmax_bar30 = (offsetXmax_min20Xmax_bar10) >> bit2; //28 - bit2 -bit3 bit
    _offsetXmax_minXmax_bar30 = offsetXmax_minXmax_bar30 >
        _offsetXmax_minXmax_bar30 ? offsetXmax_minXmax_bar30 :
        _offsetXmax_minXmax_bar30; //以bit2 = 8, bit3 = 10來說, 為9bit

    long theone = (long) Math.pow(2, 29 - bit2 - bit3); //29 - bit2 - bit3

    long originalGain = negative ? theone - offsetXmax_minXmax_bar30 :
        theone + offsetXmax_minXmax_bar30;

    long gain32 = originalGain >> bit1;

    long result = (max * gain32); //10+11

    final int totalBit = 29 - bit1 - bit2 - bit3;

    //最後輸出bit數, 受bit 1/2/3影響
    //固定輸出為10bit 0~1023
    int shiftResult = (int) (result >> (totalBit));

    staticResult = (result > staticResult) ? result : staticResult;
    if (shiftResult > staticShiftResult) {
      staticShiftResult = shiftResult;
      _max = max;
      _min = min;
    }
    return shiftResult; //可以達11bit
  }

  /**
   * getAdjustValue
   *
   * @param max short
   * @param min short
   * @param newValue short
   * @return byte
   * @deprecated
   */
  public byte getAdjustValue(short max, short min, short newValue) {
    return 0;
  }

}
