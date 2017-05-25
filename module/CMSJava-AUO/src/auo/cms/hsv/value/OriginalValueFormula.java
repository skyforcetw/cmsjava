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
public class OriginalValueFormula
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
    double offset = adjustValue * (max - min) / 128;
    short shortOffset = (short) Math.round(offset);
    short result = (short) (max + shortOffset);
    return result;
  }

  /**
   * getOffset
   *
   * @param max short
   * @param min short
   * @param newValue short
   * @return byte
   */
  public byte getAdjustValue(short max, short min, short newValue) {
    int offset = newValue - max;
    double originalValue = (offset / 1.) / ( ( (max - min) / 1.) / 128);
    byte adjustValue = (byte) Math.round(originalValue);
    return adjustValue;
  }

  public static void main(String[] args) {
    short max = 200;
    short min = 40;
    short newvalue = 205;
    OriginalValueFormula v = new OriginalValueFormula();
    byte adj = v.getAdjustValue(max, min, newvalue);
    short newv = v.getV(max, min, adj);
    System.out.println(newv);
  }
}
