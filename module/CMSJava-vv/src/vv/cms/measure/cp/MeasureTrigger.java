package vv.cms.measure.cp;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來確認是否要繼續做觸發的物件
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
   * 是否還存有更多的量測?
   * @return boolean 是否還存有更多的量測?
   */
  public boolean hasNextMeasure();

  /**
   * 尚未完成校正的數量
   * @return int
   */
  public int getUncalibratedCount();
}
