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
public interface ValueFormulaIF {
  public short getV(short max, short min, short adjustValue);

  public byte getAdjustValue(short max, short min, short newValue);

}
