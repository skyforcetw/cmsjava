package vv.cms.lcd.calibrate.parameter;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import vv.cms.lcd.calibrate.*;
import shu.math.array.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �x�s�������I�������ե��Ѽ�.
 * �]���ȥΨӥ洫���, �ҥH�X�G�Ҧ���Ʀ������Opublic��
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class WhiteParameter
    implements Parameter {
  /**
   * ���I������xyY
   */
  private CIExyY whitexyY;
  /**
   * ���w��whiteCode
   */
  public RGB whiteCode;

  /**
   * LCDModel
   */
  public LCDModel model;
  /**
   * �̤j��code���b�h��
   */
  public double maxWhiteCode = 255;
  public RGB maxRGB = new RGB(255, 255, 255);
  /**
   * ����CCT���u���覡
   */
  public CCTStyle cctStyle = CCTStyle.Unknow;

  private static enum WhitePoint {
    xyY, CCT, RGB
  }

  public final boolean isxyYWhitePoint() {
    return whitePoint == WhitePoint.xyY;
  }

  public boolean isDoWhiteCalibrate() {
    return isCCTWhitePoint() || isxyYWhitePoint();
  }

  public final boolean isCCTWhitePoint() {
    return whitePoint == WhitePoint.CCT;
  }

  public final boolean isRGBWhitePoint() {
    return whitePoint == WhitePoint.RGB;
  }

  public WhitePoint getWhitePoint() {
    return whitePoint;
  }

  private WhitePoint whitePoint;

  public String getDescription() {
    switch (whitePoint) {
      case CCT:
        return CCT + "k";
      case RGB:
        double[] rgbValues = whiteCode.getValues(new double[3],
                                                 RGB.MaxValue.Double255);
        return "(" + DoubleArray.toString(rgbValues) + ")";
      case xyY:
        return whitexyY.toString();
      default:
        return null;
    }
  }

  public String toString() {
    switch (whitePoint) {
      case CCT:
        return "CCT: " + CCT + "\n" +
            "CCTStyle: " + cctStyle + "\n" +
            "MaxCode: " + maxWhiteCode + "\n" +
            "MaxRGB: " + maxRGB + "\n" +

            "LCDModel: " + model + "\n" +
            "WhitexyY: " + whitexyY;
      case RGB:
        return "WhitePoint: " + whitePoint + "\n" +
            "WhiteCode: " + whiteCode + "\n" +

            "LCDModel: " + model + "\n" +
            "WhitexyY: " + whitexyY;
      case xyY:
        return "MaxCode: " + maxWhiteCode + "\n" +
            "MaxRGB: " + maxRGB + "\n" +

            "LCDModel: " + model + "\n" +
            "WhitexyY: " + whitexyY;
      default:
        return null;
    }

  }

  public WhiteParameter(RGB whiteCode) {
    this.whiteCode = whiteCode;
    this.maxWhiteCode = whiteCode.getValue(whiteCode.getMaxChannel());
    whitePoint = WhitePoint.RGB;
    Logger.log.trace("whiteCode: " + whiteCode + "\n" +
                     "maxCode: " + maxWhiteCode);
  }

  /**
   * �HwhiteCode�Mmodel�ӹw���ժ� Y
   * @return CIExyY
   */
  public CIExyY getWhitexyY() {
    if (this.isRGBWhitePoint() && model != null) {
      model.changeMaxValue(whiteCode);
      CIEXYZ XYZ = model.getXYZ(whiteCode, false);
      whitexyY = new CIExyY(XYZ);
    }
    return whitexyY;
  }

  public int getCCT() {
    return CCT;
  }

  public WhiteParameter(CIExyY xyY) {
    this.whitexyY = xyY;
    whitePoint = WhitePoint.xyY;
    Logger.log.trace("whitexyY: " + whitexyY);
  }

  private int CCT;

  public WhiteParameter(int CCT, double maxCode) {
    this(CCT, CCTStyle.DIlluminant, maxCode);
  }

  public WhiteParameter(int CCT, RGB maxRGB) {
    this(CCT, CCTStyle.DIlluminant, maxRGB);
  }

  public WhiteParameter(int CCT) {
    this(CCT, CCTStyle.DIlluminant, 255);
  }

  public WhiteParameter(int CCT, CCTStyle style) {
    this(CCT, style, 255);
  }

  public WhiteParameter(int CCT, CCTStyle style, double maxCode) {
    this.CCT = CCT;
    whitexyY = CCTCurveProducer.CCT2xyY(CCT, style);
    this.cctStyle = style;
    whitePoint = WhitePoint.CCT;
    this.maxWhiteCode = maxCode;
    Logger.log.trace("whitexyY: " + whitexyY + "\n" +
                     "CCTStyle: " + cctStyle);
  }

  public WhiteParameter(int CCT, CCTStyle style, RGB maxRGB) {
    this.CCT = CCT;
    whitexyY = CCTCurveProducer.CCT2xyY(CCT, style);
    this.cctStyle = style;
    whitePoint = WhitePoint.CCT;
    this.maxRGB = maxRGB;
    Logger.log.trace("whitexyY: " + whitexyY + "\n" +
                     "CCTStyle: " + cctStyle);
  }
}
