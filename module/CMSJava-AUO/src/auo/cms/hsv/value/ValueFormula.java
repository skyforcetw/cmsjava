package auo.cms.hsv.value;

import auo.cms.hsv.value.backup.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ValueFormula
    implements ValueFormulaIF {
  public short getV(short max, short min, short offset) {
    return ValuePrecisionEvaluator.getV(max, min, offset);
  }

  public byte getAdjustValue(short max, short min, short newValue) {
    return ValuePrecisionEvaluator.getOffset(max, min, newValue);
  }
  public static void setInterpolateOffset(boolean enable) {
   ValuePrecisionEvaluator.setInterpolateOffset(enable);
  }
}
