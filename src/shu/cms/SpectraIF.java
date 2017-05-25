package shu.cms;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 提供光譜相關類別一個共通操作介面
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public interface SpectraIF {
  public double[] getData();

  public int getEnd();

  public int getInterval();

  public int getStart();
}
