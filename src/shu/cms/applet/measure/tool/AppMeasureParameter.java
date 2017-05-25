package shu.cms.applet.measure.tool;

import java.awt.*;

import shu.cms.lcd.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 量測所需要的所有參數
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class AppMeasureParameter {

  public DICOM dicomMode;
  //量測前是否要先校正
  public boolean calibration;
  //量測螢幕的尺寸
  public double size;

  /**
   * 量測導具
   */
  public LCDTargetBase.Number targetNumber;
  //量測的delay
  public int delayTimes;
  //是不是在第二顆螢幕作量測
  public boolean measureDisplay2;
  //導具的LCDTarget
  public LCDTarget lcdTarget;
  //255是否反轉
  public boolean inverseMode;
  //量測的meter
  public Meter meter;
  //是不是batch量測
  public boolean batch;
  //batch量測中是否是第一筆
  public boolean firstBatch;
  //是否要插畫面作放電
  public boolean blankInsert = false;
  //插畫面為黑
  public Color blank = Color.black;
  //插黑要多久
  public long blankTimes;
  //是否啟動高bits量測
  public MeasureBits measureBits = MeasureBits.EightBits;
  //IC的bits數
  public MeasureBits icBits = MeasureBits.TenBits;
}
