package vv.cms.lcd.calibrate.measured.find;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.hvs.gradient.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.measure.cp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * RGBFinder�O�H�@�Ӱ_�lrgb, �Q�Φ�ץH�ΫG�׳v���G�񪺭��N�t��k, �z�L�q��(�Ϊ̼����q��), �o���
 * �Э�XYZ���@�k.
 *
 * �쥻�ӧ@�k�O���ΦbMeasuredCalibrator, �]�N�O�z�L�q���覡�ե��ù��ҳQ�ϥ�.
 * ���O�Ӥ�k�G��ؼЭȪ����G�}�n, �]���z�LRGBFinder�N�Ӻt��k���U�֤�class�]�˰_��, �åB�¤�
 * ����, �ϱo�L�{���¤Ʊo��ؼ�RGB.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RGBFinder
    implements JNDIInterface {
  private FindingInfo info = new FindingInfo(1);
  private CIExyY targetxyY;
  private IndependentFindingThread findingThread;
  private boolean[] calibrated = new boolean[1];
  private boolean luminanceCalibrate = true;
  private boolean chromaticCalibrate = true;
  protected GSDFGradientModel gm = new GSDFGradientModel();
  private CalibratorInstance calibrator;

  public static void main(String[] args) {
//    String str = "autocp";
//    System.out.println(str.hashCode());
//    System.out.println( (int) 'a' + 'u' + 't' + 'o' + 'c' + 'p');
    System.out.println(Float.MAX_VALUE);
    System.out.println(Double.MAX_VALUE);
  }

  public RGBFinder(CIEXYZ white, RGB.MaxValue targetMaxValue,
                   CPCodeMeasurement cpm) {
    this(white, targetMaxValue, cpm, 255);
  }

  public RGBFinder(CIEXYZ white, RGB.MaxValue targetMaxValue,
                   CPCodeMeasurement cpm, double maxCode) {
    calibrator = new CalibratorInstance(white, (MeasureInterface) cpm, this, null,
                                        maxCode);
    findingThread = new IndependentFindingThread(0, targetMaxValue, info,
                                                 calibrated, luminanceCalibrate,
                                                 chromaticCalibrate, white,
                                                 calibrator);
    findingThread.setEnableTrace(false);
    gm.setHKStrategy(GSDFGradientModel.HKStrategy.None);
    info.init();
  }

  public RGB getRGB(RGB initRGB, CIEXYZ targetXYZ) {
    targetxyY = new CIExyY(targetXYZ);
    info.setInitRGBArray(new RGB[] {initRGB});
    calibrated[0] = false;
    findingThread.run();
//    CPCodeLoader.loadOriginalDummy();
    return info.getCalibratedRGB(0);
  }

  protected class CalibratorInstance
      extends CalibratorAccessAdapter {

    public CalibratorInstance(CIEXYZ white, MeasureInterface mi,
                              JNDIInterface jndi, DeltauvQuadrant quadrant,
                              double maxCode) {
      super(white, mi, jndi, quadrant, maxCode, false);
    }

    public CalibratorInstance(CIEXYZ white, CPCodeMeasurement cpm,
                              JNDIInterface jndi) {
      super(white, cpm, jndi);
    }

    /**
     * getTargetxyY
     *
     * @param index int
     * @return CIExyY
     */
    public CIExyY getTargetxyY(int index) {
      return targetxyY;
    }

    /**
     * getIndexNearestAlogorithm
     *
     * @return NearestAlogorithm
     */
    public NearestAlgorithm getIndexNearestAlogorithm() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * getJNDI
   *
   * @param XYZ CIEXYZ
   * @return double
   */
  public double getJNDI(CIEXYZ XYZ) {
    return gm.getJNDIndex(XYZ);
  }
}
