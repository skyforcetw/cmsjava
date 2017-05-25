package shu.cms.grabber;

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
public interface ValueProperty
    extends Cloneable {
  public double[] getValues();

  public void setValues(double ...values);

  public Object clone();
}
