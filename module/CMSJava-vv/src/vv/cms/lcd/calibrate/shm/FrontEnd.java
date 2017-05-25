package vv.cms.lcd.calibrate.shm;

import java.io.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import vv.cms.lcd.material.*;
import vv.cms.lcd.material.Material;
import shu.cms.measure.meter.*;
import shu.cms.util.*;
import shu.util.log.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.parameter.*;
import vv.cms.lcd.calibrate.tester.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * FrontEnd用來銜接AutoCP演算法以及AutoCP GUI所用。
 *
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class FrontEnd
    implements ShareMemoryConnector.ParameterCallback {
  private ShareMemoryConnector connector = ShareMemoryConnector.getInstance();

  private final static long WaitTime = 500;
  public FrontEnd() throws IOException {
    connector.sendAck(true);
    Logger.log.info("Waiting " + WaitTime + " msec for ack.");
    try {
      Thread.sleep(WaitTime);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    Logger.log.info("Stop waiting.");
    connector.setParameterCallback(this);
    Logger.log.info("FrontEnd initialized.");
  }

  /**
   * 將Parameter組合為Parameters.
   * 由於Parameters還需要LCDModel, 因此在此步驟產生Model
   *
   * @param parameterArray Parameter[]
   * @return Parameters
   */
  private Parameters getParameters(Parameter[] parameterArray) {
    LCDModel model = getMeasuredLCDModel();
    Parameters parameters = new Parameters(model,
                                           (WhiteParameter) parameterArray[0],
                                           (ViewingParameter) parameterArray[1],
                                           (ColorProofParameter)
                                           parameterArray[2],
                                           (AdjustParameter)
                                           parameterArray[3],
                                           (MeasureParameter) parameterArray[4]);
    return parameters;
  }

  private CalibrateTester getCalibrateTester(final Parameters parameters) {
    CalibrateTester tester = new CalibrateTester(new InfoAdapter(parameters.
        colorProofParameter, parameters.measureParameter,
        parameters.adjustParameter) {
      public Meter getMeter() {
        Material.setColorProofParameter(parameters.colorProofParameter);
        Material.setMeasureParameter(parameters.measureParameter);
//        return new PlatformMeter();
        return Material.getMeter();
      }

      public LCDTarget getOriginalRamp1021Target() {
        return Material.getRamp1021Target();
      }

      public boolean isLCDModelCalibrate() {
        return true;
      }

      public boolean isMeasuredCalibrate() {
        return parameters.adjustParameter.luminanceBasedCalibrate ||
            parameters.adjustParameter.whiteBasedCalibrate ||
            parameters.adjustParameter.greenBasedCalibrate ||
            parameters.adjustParameter.luminanceBased2Calibrate;
      }

    });
    return tester;
  }

  private final LCDModel getMeasuredLCDModel() {
    LCDModel model = null;
    if (AutoCPOptions.get("MeasuredModel")) {
      model = Material.getMeasuredLCDModel( (MeasureParameter)
                                           parameterArray[4],
                                           (ColorProofParameter)
                                           parameterArray[2],
                                           LCDTarget.Source.Remote.Platform);
    }
    else {
      //test
      model = Material.getStoreLCDModel();
    }
    return model;
  }

  private Parameter[] parameterArray;

  public void callback(Parameter[] parameterArray) {
    Logger.log.trace("callback.");

    this.parameterArray = parameterArray;
    Parameters parameters = getParameters(parameterArray);
    int runCount = parameters.colorProofParameter.runCount;

    CalibrateTester.TestTask task = new CalibrateTester.TestTask(parameters);
    CalibrateTester tester = getCalibrateTester(parameters);
    for (int x = 0; x < runCount; x++) {
      tester.addTask(task);
    }
    tester.excute();
    RGBBase.MaxValue icBits = parameters.colorProofParameter.icBits;

    RGB[][][] rgbResult = new RGB[runCount][][];
    for (int x = 0; x < runCount; x++) {
      CalibratedResult result = tester.getCalibratedResult(x);
      RGB[][] rgb2Array = new RGB[][] {
          result.modelResult == null ?
          RGBArray.getNullRGBArray() : result.modelResult,
          result.luminanceBasedResult == null ?
          RGBArray.getNullRGBArray() :
          result.luminanceBasedResult,
          result.whiteBasedResult == null ?
          RGBArray.getNullRGBArray() :
          result.whiteBasedResult,
          result.greebBasedResult == null ?
          RGBArray.getNullRGBArray() :
          result.greebBasedResult,
          result.luminanceBased2Result == null ?
          RGBArray.getNullRGBArray() :
          result.luminanceBased2Result
      };
      rgbResult[x] = rgb2Array;
      boolean ack = connector.sendDownloadCode2(rgb2Array, icBits, x + 1);
    }

//    connector.sendDownloadCode(rgbResult, icBits);
  }

  public static void main(String[] args) {
    System.out.println(
        "AutoCP FrontEnd v091021 2009(C)VastView Technology, Inc.");
    System.out.println("FrontEnd [-options] [-help]");
    System.out.println("\tnon-parameter\t: wait GUI calling.");
    System.out.println("\toptions\t\t: produce options file \"autocp.xml\"");
    System.out.println("\thelp\t\t: show this infomation.");

    if (args.length != 0) {
      if (args[0].equals("-options")) {
        AutoCPOptions.main(args);
      }
//      else if (args[0].equals("-help")) {
//
//      }
      return;
    }

    Logger.log.info("Initialize...");
    try {
      new RGB(RGB.ColorSpace.sRGB);
      Illuminant.getD65WhitePoint();

      new FrontEnd();
      Logger.log.info("Start!");
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
      ex.printStackTrace();
    }
  }
}
