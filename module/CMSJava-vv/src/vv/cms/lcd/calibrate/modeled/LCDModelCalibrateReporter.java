package vv.cms.lcd.calibrate.modeled;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.math.*;
import shu.math.array.*;

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
public class LCDModelCalibrateReporter {
  public LCDModelCalibrateReporter(LCDModelCalibrator calibrator) {
    this.calibrator = calibrator;
    this.model = calibrator.model;
  }

  private LCDModel model;
  private LCDModelCalibrator calibrator;

  /**
   * 取得delta uv'狀況的報告
   * @return String
   */
  public final String getDeltauvPrimeReport() {
    return getDeltauvPrimeReport(calibrator.calibrateRGBArray);
  }

  private final static String getDeltauvPrimeReport(double[][] duvpArray,
      int turncode) {
    int size = duvpArray.length - turncode;
    double[][] reportduvpArray = new double[size][];
    System.arraycopy(duvpArray, turncode, reportduvpArray, 0, size);

    double[] dup = new double[size];
    double[] dvp = new double[size];

    for (int x = 0; x < size; x++) {
      double[] duvp = reportduvpArray[x];
      dup[x] = duvp[0];
      dvp[x] = duvp[1];
    }

    DoubleArray.abs(dup);
    DoubleArray.abs(dvp);

    StringBuilder buf = new StringBuilder("delta uv' report:\n");
    buf.append("mean: " + Maths.mean(dup) + " " + Maths.mean(dvp) + "\n");
    buf.append("max: " + Maths.max(dup) + " " + Maths.max(dvp) + "\n");
    buf.append("std: " + Maths.std(dup) + " " + Maths.std(dvp) + "\n");
    buf.append("total: " + Maths.sum(dup) + " " + Maths.sum(dvp) + "\n");

    return buf.toString();

  }

  private final String getDeltauvPrimeReport(RGB[] rgbArray) {
    double[][] duvpArray = getEstimateDeltauvPrime(rgbArray);
    return getDeltauvPrimeReport(duvpArray, calibrator.cp.turnCode);
  }

  final double[][] getEstimateDeltauvPrime() {
    return this.getEstimateDeltauvPrime(calibrator.calibrateRGBArray);
  }

  /**
   * 從rgbArray預測delta u'v'
   * @param rgbArray RGB[]
   * @return double[][]
   */
  private final double[][] getEstimateDeltauvPrime(RGB[] rgbArray) {
    int size = rgbArray.length;
    RGB whitergb = rgbArray[size - 1];
    whitergb.changeMaxValue(model.getMaxValue());
    CIEXYZ whiteXYZ = model.getXYZ(whitergb, false);
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    double[][] duvpArray = new double[size][];

    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      model.changeMaxValue(rgb);
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      CIExyY actualxyY = new CIExyY(XYZ);
      double[] duvp = actualxyY.getDeltauvPrime(whitexyY);
      duvpArray[x] = duvp;
    }
    return duvpArray;
  }
}
