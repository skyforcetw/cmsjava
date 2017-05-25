package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.cms.util.*;
///import shu.plot.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �H��׬���¦���ͩP��RGB�I
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ChromaticAroundAlgorithm
    extends StepAroundAlgorithm {

  public static void main(String[] args) {
    ChromaticAroundAlgorithm algo = new ChromaticAroundAlgorithm();

    RGB center = new RGB(RGB.ColorSpace.unknowRGB, new int[] {123, 63, 111});
//    algo.expandMode = true;
    Plot3D p = Plot3D.getInstance();
    algo.setChromaticExpandMode(true);
    RGB[] around = algo.getAroundRGB(center, 1);
    for (RGB rgb : around) {
      System.out.println(rgb);
      p.addColorSpace(rgb.toString(), rgb.getColor(), rgb);
    }
    p.setVisible();
  }

  /**
   * �ˬd�O�_�i�H�Q�վ�
   * @param rgb RGB �Q�վ㪺rgb
   * @param step double �վ㪺�T��
   * @param maxChannel Channel �̤j���W�D (whitePointMode�P�_�һ�)
   * @param adjustChannel Channel �n�Q�վ㪺�W�D
   * @param delta double[]
   * @return boolean
   */
  protected boolean checkAdjustable(RGB rgb, double step,
                                    RGBBase.Channel maxChannel,
                                    RGBBase.Channel adjustChannel,
                                    double[] delta) {
    if (this.isWhitePointMode()) {
      //�p�G�O�b���I�Ҧ�
      if (adjustChannel == maxChannel) {
        //�u�n�ʨ�̤j�W�D�N�O����!
        return false;
      }
    }
    else {
      //�p�G���O�b���I�Ҧ�
      //�u�n�ʨ�G�N�O����!
      if (adjustChannel == RGBBase.Channel.G) {
        return false;
      }
    }

    if (ChromaticSkipByDelta == true && delta != null) {
      if (adjustChannel == RGBBase.Channel.R && delta[0] * step > 0) {
        /**
         * �P���N�O����
         * du>0����, R�@�w�n���t��. �ҥH�ۭ��@�w<0
         * �p�Gdu*step >0����, �N��:
         * 1.du>0�BR�����վ� 2.du<0�BR���t�վ�
         * �H�W���O�h�l���վ�
         */
        return false;
      }
      if (adjustChannel == RGBBase.Channel.B && delta[1] * step < 0) {
        /**
         * �����N�O����
         * dv>0����, B�@�w�n������. �ҥH�ۭ��@�w>0
         * �p�Gdv*step <0����, �N��:
         * 1.dv>0�BB���t�վ� 2.dv<0�BB�����վ�
         * �H�W���O�h�l���վ�
         */
        return false;
      }
    }

    //�վ�᪺�I�n 1.>=0 2.<=maxcode
    double afterValue = rgb.getValue(adjustChannel) + step;
    return afterValue <= maxCode && afterValue >= 0;
  }

  /**
   *
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    if (this.chromaticExpandMode) {
      // RB�P���ܰ�
      rgbList.addAll(this.getChromaticExpandAround(centerRGB, delta, step));
    }
    else {
      // R/G/B�U���ܰ�
      //========================================================================
      // �o2�h�j�骺�ܰʦ�
      // 1. ���t��: +step �� -step���t�O
      // 2. RGB�W�D: RGB�W�D�U�۰��@��step���վ�
      //========================================================================
      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
          //�ܴ����P��channel
          if (checkAdjustable(centerRGB, adjustStep, maxChannel, ch, delta)) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.addValue(ch, adjustStep);
            rgbList.add(rgb);
          }
        }
        //�����t
        adjustStep = -adjustStep;
      }
    }
    //========================================================================

    return RGBArray.toRGBArray(rgbList);
  }

  public ChromaticAroundAlgorithm() {
    super();
  }

  public ChromaticAroundAlgorithm(double maxCode) {
    super(maxCode);
  }

  /**
   *
   * @param centerRGB RGB
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step) {
    return getAroundRGB(centerRGB, null, step);
  }

}
