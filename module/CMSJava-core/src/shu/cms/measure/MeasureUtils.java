package shu.cms.measure;

import java.awt.*;
import javax.swing.*;

import shu.cms.measure.meter.*;

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
public class MeasureUtils {

  public final static void meterCalibrate(MeterMeasurement meterMeasurement) {
    Meter meter = meterMeasurement.meter;
    MeasureWindow measureWindow = meterMeasurement.measureWindow;
    meterCalibrate(measureWindow, meter, measureWindow);
  }

  public final static void meterCalibrate(Component parentComponent,
                                          Meter meter,
                                          MeasureWindow measureWindow) {
    if (!meter.isConnected()) {
      throw new IllegalStateException("!meter.isConnected()");
    }
    else {
      if (measureWindow != null) {
        //show出黑幕, 避免影響校正
        measureWindow.setColor(Color.black);
        measureWindow.setVisible(true);
      }
      JOptionPane.showMessageDialog(parentComponent,
                                    meter.getCalibrationDescription(),
                                    "Calibration",
                                    JOptionPane.INFORMATION_MESSAGE);
      meter.calibrate();

      if (measureWindow != null) {
        //關閉黑幕
        measureWindow.setVisible(false);
      }
      JOptionPane.showMessageDialog(parentComponent,
                                    "End of calibration", "End of calibration",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public final static void meterCalibrate(Component parentComponent,
                                          Meter meter) {
    meterCalibrate(parentComponent, meter, null);
  }

}
