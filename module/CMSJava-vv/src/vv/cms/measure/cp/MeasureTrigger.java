package vv.cms.measure.cp;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �ΨӽT�{�O�_�n�~��Ĳ�o������
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public interface MeasureTrigger {
  /**
   * �O�_�٦s����h���q��?
   * @return boolean �O�_�٦s����h���q��?
   */
  public boolean hasNextMeasure();

  /**
   * �|�������ե����ƶq
   * @return int
   */
  public int getUncalibratedCount();
}
