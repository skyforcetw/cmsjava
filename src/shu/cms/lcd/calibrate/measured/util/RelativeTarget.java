package shu.cms.lcd.calibrate.measured.util;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �۹�LCDTarget���p��, �D�n�O�վ�ϫG�׺����۹��
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RelativeTarget {

  /**
   * ���o�۹�G�ת��}�C
   * @param originalTarget LCDTarget ��l���ؼ�target
   * @param measuredTarget LCDTarget �q���o�쪺target
   * @param channel Channel �n���o�۹�G�ת��W�D
   * @return double[]
   */
  private final static double[] getRelativeYArray(LCDTarget originalTarget,
                                                  LCDTarget measuredTarget,
                                                  RGBBase.Channel channel) {
    CIEXYZ black = measuredTarget.getBlackPatch().getXYZ();
    CIEXYZ saturate = null;
    if (channel == RGBBase.Channel.W) {
      //�p�G�O�զ��W�D, �̫G���I�N��RGB���ժ����
      saturate = measuredTarget.getWhitePatch().getXYZ();
    }
    else {
      //�p�G�D�զ��W�D, �̫G���I�N�θ��W�D�̹��M�����(�]�N�O�̫G�����)
      saturate = measuredTarget.getSaturatedChannelPatch(channel).getXYZ();
    }
    CIEXYZ targetBlack = originalTarget.getBlackPatch().getXYZ();
    int size = originalTarget.size();
    //==========================================================================
    // ���ͬ۹�G��
    //==========================================================================
    double[] relTargetYArray = new double[size];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = originalTarget.getPatch(x).getXYZ();
      relTargetYArray[x] = XYZ.Y - targetBlack.Y;
    }
    //���W��
    Maths.normalize(relTargetYArray, relTargetYArray[size - 1]);
    //�٭�
    relTargetYArray = DoubleArray.times(relTargetYArray, saturate.Y - black.Y);
    //�[��
    relTargetYArray = DoubleArray.plus(relTargetYArray, black.Y);
    //==========================================================================
    return relTargetYArray;
  }

  /**
   * ���o�۹�G�ץH�ζq�����I�����I��LCDTarget
   * �HmeasuredTarget�q���쪺�G�׬���¦�@�վ�, ���s���ͬ۹�G�ת�LCDTarget.
   * �åB�NmeasuredTarget�q���쪺���I�@�����I, ���s��������I�����������׮y�Ъ�LCDTarget.
   *
   * �p�G���LCDModel����, �h�٥i�H�w��t�I, ������I�H�U���ؼЦ�ק@�վ�.
   * LCDModel�O�Χ@CCTCurveProducer���s���ͥؼЦ�שһ�.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param turncode int
   * @param model LCDModel
   * @param p Parameter
   * @param wp WhiteParameter
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceAndChromaticityRelativeInstance(
      LCDTarget originalTarget, LCDTarget measuredTarget, int turncode,
      LCDModel model, ColorProofParameter p, WhiteParameter wp) {

    //�p��X�۹�G�ת��}�C
    double[] relTargetYArray = getRelativeYArray(originalTarget,
                                                 measuredTarget,
                                                 RGBBase.Channel.W);
    //���o�ժ�XYZ
    CIEXYZ whiteXYZ = measuredTarget.getWhitePatch().getXYZ();

    //==========================================================================
    // ���s�p��ؼ�xyY Curve
    //==========================================================================
    CIExyY[] xyYcurve = null;
    /**
     * �C�Ƕ��O�_�n���ק@�۹�վ�
     */
    boolean doDimCodeRelChromaticity = false;
    if (model != null && p != null && wp != null) {
      //�p�G��model�B�ѼƳ�������
      doDimCodeRelChromaticity = true;
      CIEXYZ blackXYZ = measuredTarget.getBlackPatch().getXYZ();
      CCTCurveProducer.CCTParameter cctp = CCTCurveProducer.getCCTParameter(
          blackXYZ, whiteXYZ, p, wp);
      xyYcurve = CCTCurveProducer.getxyYCurve(model, cctp, relTargetYArray);
    }
    //==========================================================================

    //==========================================================================
    // �q�۹�G�ײ��ͬ۹�ؼ�
    //==========================================================================
    List<Patch>
        relTargetPatchList = Patch.Produce.copyOf(originalTarget.getPatchList());
    int size = originalTarget.size();
    //����ժ�xyY
    CIExyY whitexyY = new CIExyY(whiteXYZ);

    for (int x = 0; x < size; x++) {
      Patch patch = relTargetPatchList.get(x);
      CIEXYZ XYZ = (CIEXYZ) patch.getXYZ().clone();
      if (relTargetYArray[x] != 0) {
        //�վ�G��, �Ϩ�G�׬��۹�G��
        XYZ.scaleY(relTargetYArray[x]);
      }
      if (x >= turncode) {
        //����I�H�W, �@�ֽվ��׭�, �Ϧ�׭ȻP���I��׬۵�
        CIExyY xyY = new CIExyY(XYZ);
        xyY.x = whitexyY.x;
        xyY.y = whitexyY.y;
        CIEXYZ XYZ2 = xyY.toXYZ();
        XYZ.setValues(XYZ2.getValues());
      }
      else if (doDimCodeRelChromaticity) {
        //��model�Bp�Bwp�~��i��o���
        //����I�H�U, �N���s���ͪ�CCT���u���ͪ���צ��u, ���s�]�w�W
        CIExyY xyY = new CIExyY(XYZ);
        CIExyY cctxyY = xyYcurve[x];
        xyY.x = cctxyY.x;
        xyY.y = cctxyY.y;
        CIEXYZ XYZ2 = xyY.toXYZ();
        XYZ.setValues(XYZ2.getValues());
      }
      Patch.Operator.setXYZ(patch, XYZ);
    }
    LCDTarget relativeTarget = LCDTarget.Instance.get(relTargetPatchList,
        originalTarget.getNumber(), originalTarget.isInverseModeMeasure());
    //==========================================================================

    return relativeTarget;

  }

  /**
   * ���o�۹�G�ץH�ζq�����I�����I��LCDTarget
   * �HmeasuredTarget�q���쪺�G�׬���¦�@�վ�, ���s���ͬ۹�G�ת�LCDTarget.
   * �åB�NmeasuredTarget�q���쪺���I�@�����I, ���s��������I�����������׮y�Ъ�LCDTarget.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param turncode int
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceAndChromaticityRelativeInstance(
      LCDTarget originalTarget, LCDTarget measuredTarget, int turncode) {
    return getLuminanceAndChromaticityRelativeInstance(originalTarget,
        measuredTarget, turncode, null, null, null);
  }

  /**
   * ���o�۹�G�ת�LCDTarget
   * �HmeasuredTarget�q���쪺�G�׬���¦�@�վ�, ���s���ͬ۹�G�ת�LCDTarget.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param channel Channel
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceRelativeInstance(LCDTarget
      originalTarget, LCDTarget measuredTarget, RGBBase.Channel channel) {
    //�p��X�۹�G�ת��x�}
    double[] relTargetYArray = getRelativeYArray(originalTarget,
                                                 measuredTarget, channel);

    //==========================================================================
    // �q�۹�G�ײ��ͬ۹�ؼ�
    //==========================================================================
    List<Patch>
        relTargetPatchList = Patch.Produce.copyOf(originalTarget.getPatchList());
    int size = originalTarget.size();
    for (int x = 0; x < size; x++) {
      Patch p = relTargetPatchList.get(x);
      CIEXYZ XYZ = (CIEXYZ) p.getXYZ().clone();
      Patch.Operator.setXYZ(p, XYZ);
      XYZ.scaleY(relTargetYArray[x]);
    }
    LCDTarget relativeTarget = LCDTarget.Instance.get(relTargetPatchList,
        originalTarget.getNumber(), originalTarget.isInverseModeMeasure());
    //==========================================================================

    return relativeTarget;
  }
}
