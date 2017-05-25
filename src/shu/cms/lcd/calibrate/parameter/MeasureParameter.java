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
   * 是否要以內插代替量測
   */
  public boolean interpolateReplaceMeasure = false;
  /**
   * 內插的基礎單位 (比該單位還要小都靠內插)
   */
  public RGBBase.MaxValue interpolateUnit = RGBBase.MaxValue.Int9Bit;

  /**
   * 每一次量測色塊是否內插一blank
   */
  public boolean measureBlankInsert = false;
  /**
   * blank停留的時間
   */
  public int measureBlankTime = 17;
  /**
   * blank的顏色
   */
  public Color blankColor = Color.white;

  /**
   * 背景的顏色
   */
  public Color backgroundColor = Color.black;

  /**
   * 每一次量測的預留等待時間
   */
  public int measureWaitTime = 300;

  /**
   * 尋找白點時, 是否要插入循序量測
   */
  public boolean whiteSequenceMeasure = false;
  /**
   * 循序量測色塊的個數
   */
  public int sequenceMeasureCount = 3;

  /**
   * 是否要進行反轉量測(針對反轉面板進行特別處理)
   */
  public boolean inverseMeasure = true;

  /**
   * 是否要平行處理
   */
  public boolean parallelExcute = true;

  /**
   * rom download後的wait時間
   */
  public long downloadWaitTime = 0;

  /**
   * 是否要對量測值作buffer (new)
   */
  public boolean bufferMeasure = true;
  /**
   * cp code的可容許差異值 (new)
   */
  public int CPCodeAcceptDifference = 63;
  /**
   * 採用差異量測(考慮到cp code差異值) (new)
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
