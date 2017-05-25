package shu.cms.dc.estimate;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �P�����󪺥��Х��ϬM��ƪ��w��
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class SensorResponseEstimator {

  /**
   * �̷�sensorResponse��target, �NsensorResponse���w�����G��@�X�w�����i
   * @param sensorResponse Spectra[]
   * @param target DCTarget
   * @return EstimatorReport
   */
  public final static EstimatorReport getEstimatorReport(Spectra[]
      sensorResponse, DCTarget target) {
    double[][] estimatedValues = getEstimatedValues(sensorResponse, target);
    double[][] realValues = getRealValues(target);
    EstimatorReport report = EstimatorReport.getInstance(estimatedValues,
        realValues);
    return report;
  }

  /**
   * �Q��sensorResponse���S��, �H��target������, �p��XRGB
   * R=responseR * SPD
   * G=responseG * SPD
   * B=responseB * SPD
   * @param sensorResponse Spectra[]
   * @param target DCTarget
   * @return double[][] �Ptarget��patch�ƶq�ۦP��RGBValues
   */
  private final static double[][] getEstimatedValues(Spectra[] sensorResponse,
      DCTarget target) {
    IdealDigitalCamera dc = new IdealDigitalCamera(sensorResponse,
        "SensorResponse");
    int size = target.size();
    double[][] result = new double[size][];

    for (int m = 0; m < size; m++) {
      Patch p = target.getPatch(m);
      Spectra s = p.getSpectra();
      double[] RGBValues = dc.capture(s);
//      double[] orgRGBValues = dc.getOriginalOutputRGBValues( (double[])
//          RGBValues.clone());
//
//      result[m] = orgRGBValues;
      result[m] = RGBValues;
    }
    return result;
  }

  /**
   * ��Xtarget�C�@��patch��RGB��ڭ�
   * @param target DCTarget
   * @return double[][]
   */
  private final static double[][] getRealValues(DCTarget target) {
    int size = target.size();
    double[][] result = new double[size][];

    for (int m = 0; m < size; m++) {
      Patch p = target.getPatch(m);
      result[m] = p.getRGB().getValues(new double[3], RGB.MaxValue.Double1);
    }
    return result;

  }
}
