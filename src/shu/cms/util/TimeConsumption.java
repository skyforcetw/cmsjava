package shu.cms.util;

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
public final class TimeConsumption {
  protected transient long startTime;
  protected transient long costTime;

  public final long getElapsedTime() {
    return System.currentTimeMillis() - startTime;
  }

  public final long getCostTime() {
    return costTime;
  }

  public void start() {
    startTime = System.currentTimeMillis();
  }

  public void end() {
    costTime = System.currentTimeMillis() - startTime;
  }
}
