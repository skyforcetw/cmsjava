package shu.cms.colorspace.independ;

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
public interface LChConvertible {
  public CIELCh.Style getStyle();

  public double[] getValues();

  public double[] getValues(double[] values);

  public CIEXYZ getWhite();

}
