package shu.cms.measure.cp;

import shu.cms.*;
import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ���Ѱ򥻪��q���\��.
 * ��ڤW��CPCodeMeasurement (cpm)��²�ƪ���, ���ѳ̩������q���\��, �קK�n�S�L�hcpm���\��,
 * �y�����H�w�����ϥ�.
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
