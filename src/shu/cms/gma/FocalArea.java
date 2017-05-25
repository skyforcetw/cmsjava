package shu.cms.gma;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來敘述分區中的集中點以及分區的邊界值
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public interface FocalArea {
  /**
   * 取得集中點
   * @return double
   */
  public double getFocalPoint();

  /**
   * 取得該區的上界限
   * @return double
   */
  public double getUpperBoundary();

  /**
   * 取得該區的下界限
   * @return double
   */
  public double getLowerBoundary();
}
