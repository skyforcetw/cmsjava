package shu.cms.measure.meter;

import shu.cms.measure.meter.Meter.*;
import shu.cms.Spectra;
import shu.util.log.Logger;
import com.gretagmacbeth.eyeone.EyeOneException;
import shu.math.array.FloatArray;
import shu.cms.measure.meterapi.i1api.EyeOneAPI;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EyeOnePro
    extends EyeOneDisplay2 {
  public EyeOnePro(ScreenType screenType) {
    super(screenType);
    try {
      _EYE_ONE.setMeasurementMode(EyeOneAPI.MeasurementMode.SingleEmission);
    }
    catch (EyeOneException ex) {
    }
  }

  public String getCalibrationDescription() {
    return "Place i1 pro on plain surface";
  }

  public Instr getType() {
    return Instr.i1Pro;
  }

  public Spectra triggerMeasurementInSpectra() {
    try {
      float[] spectrum = _EYE_ONE.triggerSpectrumMeasurement();
      double[] data = FloatArray.toDoubleArray(spectrum);
      Spectra s = new Spectra("", Spectra.SpectrumType.NO_ASSIGN, 380, 730, 10,
                              data);
      return s;
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }
}
