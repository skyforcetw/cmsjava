package shu.cms.measure;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.meter.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �|�N�q�L������Ȧs�_��, �p�G�J��n���ƶq�������p, �|���L�q�������^�ǼȦs��
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
      //�q�L�N���n�A���ƶq�F
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
   * �q�L���n���n���ƶq
   */
  private boolean bufferedMeasure = true;
  private Map<RGB, Patch> rgbPatchMap = new HashMap<RGB, Patch> ();
}
