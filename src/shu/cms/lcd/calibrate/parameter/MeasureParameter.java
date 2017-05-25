package shu.cms.lcd.calibrate.parameter;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;

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
public class MeasureParameter
    implements Parameter {
  /**
   * �O�_�n�H�����N���q��
   */
  public boolean interpolateReplaceMeasure = false;
  /**
   * ��������¦��� (��ӳ���٭n�p���a����)
   */
  public RGBBase.MaxValue interpolateUnit = RGBBase.MaxValue.Int9Bit;

  /**
   * �C�@���q������O�_�����@blank
   */
  public boolean measureBlankInsert = false;
  /**
   * blank���d���ɶ�
   */
  public int measureBlankTime = 17;
  /**
   * blank���C��
   */
  public Color blankColor = Color.white;

  /**
   * �I�����C��
   */
  public Color backgroundColor = Color.black;

  /**
   * �C�@���q�����w�d���ݮɶ�
   */
  public int measureWaitTime = 300;

  /**
   * �M����I��, �O�_�n���J�`�Ƕq��
   */
  public boolean whiteSequenceMeasure = false;
  /**
   * �`�Ƕq��������Ӽ�
   */
  public int sequenceMeasureCount = 3;

  /**
   * �O�_�n�i�����q��(�w����ୱ�O�i��S�O�B�z)
   */
  public boolean inverseMeasure = true;

  /**
   * �O�_�n����B�z
   */
  public boolean parallelExcute = true;

  /**
   * rom download�᪺wait�ɶ�
   */
  public long downloadWaitTime = 0;

  /**
   * �O�_�n��q���ȧ@buffer (new)
   */
  public boolean bufferMeasure = true;
  /**
   * cp code���i�e�\�t���� (new)
   */
  public int CPCodeAcceptDifference = 63;
  /**
   * �ĥήt���q��(�Ҽ{��cp code�t����) (new)
   */
  public boolean useDifferenceMeasure = false;
  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {

    StringBuilder buf = new StringBuilder();
    buf.append("[Interpolate]");
    buf.append("\ninterpolateReplaceMeasure: " + interpolateReplaceMeasure);
    buf.append(" (Unit: " + interpolateUnit + ")");
    buf.append("\n[Blank]");
    buf.append("\nmeasureBlankInsert: " + measureBlankInsert);
    buf.append(" (Color: " + blankColor + ")");
    buf.append("\n[Measure]");
    buf.append("\nRamp LCDTArget: " + rampLCDTarget);
    buf.append("\nXtalk LCDTArget: " + xtalkLCDTarget);
    buf.append("\nbackgroundColor: " + backgroundColor);
    buf.append("\nmeasureWaitTime: " + measureWaitTime);
    buf.append("\nwhiteSequenceMeasure: " + whiteSequenceMeasure);
    buf.append(" (Count: " + sequenceMeasureCount + ")");
    buf.append("\ninverseMeasure: " + inverseMeasure);
    buf.append("\nparallelExcute: " + parallelExcute);
    buf.append("\nbufferMeasure: " + bufferMeasure);
    buf.append("\n[CP Code Loader]");
    buf.append("\nuseDifferenceMeasure: " + useDifferenceMeasure);
    buf.append(" (AcceptDifference: " + CPCodeAcceptDifference + ")");
    buf.append("\ndownloadWaitTime: " + downloadWaitTime);

    return buf.toString();
  }

  public LCDTargetBase.Number rampLCDTarget = LCDTargetBase.Number.Ramp1021;
  public LCDTargetBase.Number xtalkLCDTarget = LCDTargetBase.Number.Xtalk769;
}
