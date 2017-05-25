package shu.cms.applet.measure.tool;

import java.awt.*;

import shu.cms.lcd.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �q���һݭn���Ҧ��Ѽ�
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
  //�q���e�O�_�n���ե�
  public boolean calibration;
  //�q���ù����ؤo
  public double size;

  /**
   * �q���ɨ�
   */
  public LCDTargetBase.Number targetNumber;
  //�q����delay
  public int delayTimes;
  //�O���O�b�ĤG���ù��@�q��
  public boolean measureDisplay2;
  //�ɨ㪺LCDTarget
  public LCDTarget lcdTarget;
  //255�O�_����
  public boolean inverseMode;
  //�q����meter
  public Meter meter;
  //�O���Obatch�q��
  public boolean batch;
  //batch�q�����O�_�O�Ĥ@��
  public boolean firstBatch;
  //�O�_�n���e���@��q
  public boolean blankInsert = false;
  //���e������
  public Color blank = Color.black;
  //���­n�h�[
  public long blankTimes;
  //�O�_�Ұʰ�bits�q��
  public MeasureBits measureBits = MeasureBits.EightBits;
  //IC��bits��
  public MeasureBits icBits = MeasureBits.TenBits;
}
