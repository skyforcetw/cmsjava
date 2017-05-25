package shu.cms.lcd.calibrate.tester;

import java.util.*;

import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.parameter.*;

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
public class TestTaskProducer {
  public TestTaskProducer(ParameterProducer producer) {
    this.producer = producer;
  }

  private ParameterProducer producer;

  public void printParameters() {

  }

  private ColorProofParameter[] getCPParameter() {
    //==========================================================================
    // parameter·Ç³Æ
    //==========================================================================
    ColorProofParameter.CCTCalibrate[] cctCalArray = producer.
        getCCTCalibrateArray();
    int[] tuneCodeArray = producer.getTuneCodeArray();
    double[] gammarray = producer.getGammaArray();

    int cctCalSize = cctCalArray.length;
    int turnCodeSize = tuneCodeArray.length;
    int gammaSize = gammarray.length;

    List<ColorProofParameter> paraList = new ArrayList<ColorProofParameter> ();
    for (int x = 0; x < turnCodeSize; x++) {
      for (int y = 0; y < cctCalSize; y++) {
        for (int z = 0; z < gammaSize; z++) {
          ColorProofParameter cpp = producer.getInitCPParameter();
          cpp.turnCode = tuneCodeArray[x];
          cpp.cctCalibrate = cctCalArray[y];
          cpp.customGamma = gammarray[z];
          paraList.add(cpp);
          for (int m = 1; m < cpp.runCount; m++) {
            paraList.add(cpp);
          }

        }
      }
    }
    //==========================================================================
    return paraList.toArray(new ColorProofParameter[paraList.size()]);
  }

  public CalibrateTester.TestTask[] getTaskArray() {
    ColorProofParameter[] pArray = getCPParameter();
    int parameterSize = pArray.length;

    LCDModel lcdModel = producer.getLCDModel();
    AdjustParameter ap = producer.getAdjustParameter();
    ViewingParameter vp = producer.getViewingParameter();
    MeasureParameter mp = producer.getMeasureParameter();

    WhiteParameter[] wpArray = producer.getWhiteParameterArray();
    int wpSize = wpArray.length;
    int size = parameterSize * wpSize;
    CalibrateTester.TestTask[] taskArray = new CalibrateTester.TestTask[
        size];
    int index = 0;
    for (int x = 0; x < parameterSize; x++) {
      for (int y = 0; y < wpSize; y++) {
        Parameters ps = new Parameters(lcdModel, wpArray[y], vp, pArray[x], ap,
                                       mp);
        taskArray[index++] = new CalibrateTester.TestTask(ps);
      }
    }

    return taskArray;
  }
}
