package shu.cms.measure;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.meter.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 會將量過的色塊暫存起來, 如果遇到要重複量測的狀況, 會略過量測直接回傳暫存值
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class BufferedMeterMeasurement
    extends MeterMeasurement {

  /**
   * BufferedMeterMeasurement
   *
   * @param meter Meter
   * @param LCDSize double
   * @param calibration boolean
   */
  public BufferedMeterMeasurement(Meter meter, double LCDSize,
                                  boolean calibration) {
    super(meter, LCDSize, calibration);
  }

  /**
   * BufferedMeterMeasurement
   *
   * @param meter Meter
   * @param calibration boolean
   */
  public BufferedMeterMeasurement(Meter meter, boolean calibration) {
    super(meter, calibration);
  }

  public Patch measure0(RGB rgb, String patchName, String titleNote,
                        String timeNote) {
    if (bufferedMeasure && rgbPatchMap.containsKey(rgb)) {
      //量過就不要再重複量了
      return rgbPatchMap.get(rgb);
    }
    else {
      Patch p = super.measure0(rgb, patchName, titleNote,
                               timeNote);
      rgbPatchMap.put(rgb, p);
      return p;
    }
  }

  /**
   * 量過的要不要重複量
   */
  private boolean bufferedMeasure = true;
  private Map<RGB, Patch> rgbPatchMap = new HashMap<RGB, Patch> ();
}
