package shu.math.lut;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public interface LookUpTable {
  public double[] getValues(double[] keys);

  public double[] getKeys(double[] values);
}
