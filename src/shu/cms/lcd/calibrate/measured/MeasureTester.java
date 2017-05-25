package shu.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.Material;
import shu.cms.measure.*;
import shu.cms.measure.cp.*;
import shu.cms.measure.meter.*;
import shu.cms.util.*;

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
public class MeasureTester {
  public static void main(String[] args) {
    int waitTime = 1000;
    LCDTarget.Measured.setMeasureWaitTime(waitTime);
    LCDTarget rampTarget = LCDTarget.Measured.measure(LCDTarget.Source.
        Remote,
        LCDTargetBase.Number.Ramp256W, false);
    System.out.println("whitest: " + rampTarget.getBrightestPatch().getXYZ());
    System.out.println("254: " + rampTarget.getPatch(254).getXYZ());

    rampTarget = LCDTarget.Measured.measure(LCDTarget.Source.
                                            Remote,
                                            LCDTargetBase.Number.Ramp256W, false);
    System.out.println("whitest: " + rampTarget.getBrightestPatch().getXYZ());
    System.out.println("254: " + rampTarget.getPatch(254).getXYZ());

    Meter meter = Material.getMeter();
    MeterMeasurement mm = new MeterMeasurement(meter, false);

    for (int x = 1; x < 50; x++) {

      MeasureParameter mp = new MeasureParameter();
      mp.whiteSequenceMeasure = true;
      mp.sequenceMeasureCount = x;
      mp.measureWaitTime = waitTime;

      MeasureTester mt = new MeasureTester(mm, mp);
      LCDTarget target = mt.measureWhite(new RGB[] {new RGB(254, 254, 254)}, x);
      System.out.println(x + " " + target.getBrightestPatch().getXYZ());
    }
  }

  MeasureTester(MeterMeasurement mm, MeasureParameter mp) {
    this.mm = mm;
    cpm = MeasuredCalibrator.getCPCodeMeasurement(mm, RGB.MaxValue.Int10Bit,
                                                  mp);
  }

  protected MeterMeasurement mm;
  private CPCodeMeasurement cpm;
  protected LCDTarget measureWhite(RGB[] whiteRGBArray,
                                   int sequenceMeasureCount) {
    RGB[] measureRGBArray = MeasuredCalibrator.getMeasureWhiteRGBArray(
        whiteRGBArray,
        sequenceMeasureCount);
    List<Patch>
        patchList = cpm.directMeasureResult(RGBArray.toRGBList(
            measureRGBArray)).
        result;
    int width = sequenceMeasureCount + 1;
    int realSize = patchList.size() / width;
    List<Patch> realPatchList = new ArrayList<Patch> (realSize);
    for (int x = 0; x < realSize; x++) {
      int index = -1 + width * (x + 1);
      Patch p = patchList.get(index);
      realPatchList.add(p);
    }
    LCDTarget measureLCDTarget = LCDTarget.Instance.get(realPatchList,
        LCDTarget.Number.Unknow, this.mm.isDo255InverseMode());
    return measureLCDTarget;
  }

}
