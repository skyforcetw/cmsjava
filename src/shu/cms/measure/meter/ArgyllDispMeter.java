package shu.cms.measure.meter;

import java.io.*;
import java.util.*;

import javax.swing.*;

import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.measure.meterapi.argyll.*;
import shu.math.array.*;
import shu.util.log.*;
import shu.cms.Spectra;

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
public class ArgyllDispMeter
    extends Meter {
  public ArgyllDispMeter() {
  }

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    return true;
  }

  /**
   * calibrate
   *
   */
  public void calibrate() {
  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
    return "argyll calibration";
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    throw new UnsupportedOperationException();
  }
  /**
   *
   * @return double[]
   * @deprecated
   */
  public double[] triggerMeasurementInSpectrum() {
    throw new UnsupportedOperationException();
  }

  public Spectra triggerMeasurementInSpectra() {
    throw new UnsupportedOperationException();
  }

  public final static String TEMPLATE_FILE = "ArgyllMeter.logo";

  public double[][] triggerMeasurementInXYZ(List<RGB> rgbList) {
    //==========================================================================
    // init
    //==========================================================================
    try {
      LogoFile logo = new LogoFile(TEMPLATE_FILE + ".ti1", true);
      logo.setArgyllEmulated(true);
      logo.setHeader("COLOR_REP", "RGB");
      logo.setDataFormat("SAMPLE_ID RGB_R RGB_G RGB_B");

      int size = rgbList.size();
      double[] rgbValues = new double[3];
      for (int x = 0; x < size; x++) {
        RGB rgb = rgbList.get(x);
        rgb.getValues(rgbValues, RGB.MaxValue.Double1);
        rgbValues = DoubleArray.times(rgbValues, 100.);
        String data = "A" + (x + 1) + " " + rgbValues[0] + " " + rgbValues[1] +
            " " + rgbValues[2];
        logo.addData(data);

      }

      logo.save();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
      return null;
    }
    //==========================================================================

    String exec = Argyll.DIR + "\\dispread -y " +
        (lcdType ? "l" : "c") + " -s -d " + display + " -p 0.5,0.5,1.0 " +
        TEMPLATE_FILE;
    String resultFilename = TEMPLATE_FILE + ".ti3";
    Runtime rt = Runtime.getRuntime();

    //==========================================================================
    // measure
    //==========================================================================
    try {
      new File(resultFilename).delete();
      String cmd = "cmd /K start \"Argyll Window\" " + exec;
      Process p = rt.exec(cmd);
      Thread.sleep(10000);
      JOptionPane.showMessageDialog(null,
                                    "Press ok when Argyll Window close.",
                                    "Attention!",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
      return null;
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
      return null;
    }
    //==========================================================================

    //==========================================================================
    // analyze result
    //==========================================================================
    CGATSFileAdapter cgats = new CGATSFileAdapter(resultFilename);
    List<CIEXYZ> XYZList = cgats.getXYZList();
    int size = XYZList.size();
    double[][] result = new double[size][];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = XYZList.get(x);
      result[x] = XYZ.getValues();
    }
    return result;
    //==========================================================================
  }

  protected int display = 1;

  /**
   * getLastCalibration
   *
   * @return String
   */
  public String getLastCalibration() {
    return "Argyll calibration";
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return "argyll calibration";
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   */
  public void setScreenType(ScreenType screenType) {
    if (screenType == ScreenType.LCD) {
      lcdType = true;
    }
    else {
      lcdType = false;
    }
  }

  protected boolean lcdType = true;

  /**
   * getType
   *
   * @return Instr
   */
  public Instr getType() {
    return Instr.Argyll;
  }

  public void setDisplay(int display) {
    this.display = display;
  }

  public void close() {

  }
}
