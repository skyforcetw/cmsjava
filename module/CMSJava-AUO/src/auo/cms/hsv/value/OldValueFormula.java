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
public class OldValueFormula
    implements ValueFormulaIF {
  public OldValueFormula() {
    super();
  }

  public static void main(String[] args) {
    OldValueFormula oldvalueformula = new OldValueFormula();
  }

  /**
   * getV
   *
   * @param max short
   * @param min short
   * @param adjustValue short
   * @return short
   * @todo Implement this auo.cms.hsv.value.ValueFormulaIF method
   */
  public short getV(short max, short min, short adjustValue) {
    int chroma = max-min;
//    adjustValue*chroma*chroma*(1023-max)*(1+
    return 0;
  }

  /**
   * getAdjustValue
   *
   * @param max short
   * @param min short
   * @param newValue short
   * @return byte
   * @todo Implement this auo.cms.hsv.value.ValueFormulaIF method
   */
  public byte getAdjustValue(short max, short min, short newValue) {
    return 0;
  }
}
