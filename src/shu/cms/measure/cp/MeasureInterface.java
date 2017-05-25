package shu.cms.measure.cp;

import shu.cms.*;
import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 提供基本的量測功能.
 * 實際上為CPCodeMeasurement (cpm)的簡化版本, 提供最底限的量測功能, 避免曝露過多cpm的功能,
 * 造成難以預期的使用.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface MeasureInterface {
  public MeasureResult measureResult(RGB[] rgbArray, boolean forceTrigger,
                                     boolean trigger);

  public Patch measure(RGB rgb, boolean forceTrigger, boolean trigger);

  public void triggerMeasure(MeasureTrigger measureTrigger);

  public Patch measure(RGB rgb);

  public int[] getMeasureCount();

  public void reset();
}
