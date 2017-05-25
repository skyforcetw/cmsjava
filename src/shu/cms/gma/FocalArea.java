package shu.cms.gma;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �Ψӱԭz���Ϥ��������I�H�Τ��Ϫ���ɭ�
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
   * ���o�����I
   * @return double
   */
  public double getFocalPoint();

  /**
   * ���o�ӰϪ��W�ɭ�
   * @return double
   */
  public double getUpperBoundary();

  /**
   * ���o�ӰϪ��U�ɭ�
   * @return double
   */
  public double getLowerBoundary();
}
