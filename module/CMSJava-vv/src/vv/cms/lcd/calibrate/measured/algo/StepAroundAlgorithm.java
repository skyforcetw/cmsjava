package vv.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.cms.util.*;
//import shu.plot.*;

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
public class StepAroundAlgorithm
    extends AroundAlgorithm {

  protected double maxCode = 255;
  public StepAroundAlgorithm(double maxCode) {
    this.maxCode = maxCode;
  }

  public StepAroundAlgorithm() {
    this(255);
  }

  /**
   * ���ocenterRGB�P�䪺RGB, �Hstep���@��step���
   * �åB��delta�ӨM�w�Y�ǩP��RGB�O�_�n���L
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step) {
    return getAroundRGB(centerRGB, delta, step, false);
  }

  /**
   * �]�w����X�i�Ҧ��O�_�ͮ�
   * �p�G���B�����X�i�Ҧ�, �hRB�u�|��|���I; ����X�i�Ҧ��|��8���I.
   */
  protected boolean chromaticExpandMode = false;

  /**
   * �bR/B�����W, �X�i�@8���I, �]�A:
   * R+1/B+1 R+1/B R+1/B-1 R/B+1 R/B-1 R-1/B+1 R-1/B R-1/B-1
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return List
   */
  protected List<RGB> getChromaticExpandAround(RGB centerRGB, double[] delta,
                                               double step) {
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();
    List<RGB> rgbList = new ArrayList<RGB> ();

    if (chromaticExpandMode) {
      for (double rStep = -step; rStep <= step; rStep += step) {
        for (double bStep = -step; bStep <= step; bStep += step) {
          if (rStep == 0 && bStep == 0) {
            //���������ݥέ���
            continue;
          }

          boolean check =
              checkAdjustable(centerRGB, rStep, maxChannel, RGBBase.Channel.R,
                              delta) &&
              checkAdjustable(centerRGB, bStep, maxChannel, RGBBase.Channel.B,
                              delta);

          if (check) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.R += rStep;
            rgb.B += bStep;
            rgbList.add(rgb);
          }

        }
      }
      return rgbList;
    }
    else {
      return null;
    }
  }

  private List<RGB> rgbAdjust(RGBBase.Channel[] channels,
                              RGBBase.Channel maxChannel,
                              RGB centerRGB, double adjustStep, double[] delta) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    for (RGBBase.Channel ch : channels) {
      if (checkAdjustable(centerRGB, adjustStep, maxChannel, ch, delta)) {
        RGB rgb = (RGB) centerRGB.clone();
        rgb.changeMaxValue(RGB.MaxValue.Double255);
        rgb.addValue(ch, adjustStep);
        rgbList.add(rgb);
      }
    }
    return rgbList;
  }

  private List<RGB> whiteAdjust(RGBBase.Channel maxChannel, RGB centerRGB,
                                double adjustStep) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    if (whiteCheckAdjustable(centerRGB, adjustStep, maxChannel)) {
      RGB rgb = (RGB) centerRGB.clone();
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      rgb.addValues(adjustStep);
      rgbList.add(rgb);
    }
    return rgbList;
  }

  /**
   * ���ocenterRGB�P�䪺RGB, �Hstep���@��step���
   * �åB��delta�ӨM�w�Y�ǩP��RGB�O�_�n���L
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @param involveWhite boolean �P�䪺RGB�n���n�]�A��
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step,
                            boolean involveWhite) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    if (chromaticExpandMode) {
      //======================================================================
      // R & B
      //======================================================================
      rgbList.addAll(getChromaticExpandAround(centerRGB, delta, step));
      //======================================================================

      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        //======================================================================
        // G
        //======================================================================
        rgbList.addAll(rgbAdjust(new RGBBase.Channel[] {RGBBase.Channel.G},
                                 maxChannel,
                                 centerRGB, adjustStep, delta));
        //======================================================================

        //======================================================================
        // W
        //======================================================================
        if (involveWhite) {
          rgbList.addAll(whiteAdjust(maxChannel, centerRGB, adjustStep));
        }
        //======================================================================

        adjustStep = -adjustStep;
      }
    }
    else {
      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        //======================================================================
        // R/G/B
        //======================================================================
        rgbList.addAll(rgbAdjust(RGBBase.Channel.RGBChannel, maxChannel,
                                 centerRGB, adjustStep, delta));
        //=====================================================================

        //======================================================================
        // W
        //======================================================================
        if (involveWhite) {
          rgbList.addAll(whiteAdjust(maxChannel, centerRGB, adjustStep));
        }
        //======================================================================
        adjustStep = -adjustStep;
      }
    }

    return RGBArray.toRGBArray(rgbList);
  }

  public static void main(String[] args) {
    StepAroundAlgorithm algo = new StepAroundAlgorithm();
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new int[] {125, 111, 122});
    algo.setChromaticExpandMode(true);
//    RGB[] cube = algo.getCubeAroundRGB(rgb, 1);
    RGB[] cube = algo.getAroundRGB(rgb, 90, true);
    System.out.println(cube.length);
    for (RGB c : cube) {
      System.out.println(c);
    }
    Plot3D p = Plot3D.getInstance();
    for (RGB r : cube) {
      System.out.println(r);
      p.addColorSpace(r.toString(), r.getColor(), r);
    }
    p.setVisible();
  }

  /**
   * �ߤ��骺�X�i, �]�A�����N�O27���I, �]�N�O�X�i26���I�X�h.
   * @param centerRGB RGB
   * @param step double
   * @return RGB[]
   */
  public RGB[] getCubeAroundRGB(RGB centerRGB, double step) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    for (double rStep = -step; rStep <= step; rStep += step) {
      for (double gStep = -step; gStep <= step; gStep += step) {
        for (double bStep = -step; bStep <= step; bStep += step) {
          if (rStep == 0 && gStep == 0 && bStep == 0) {
            //���������ݥέ���
            continue;
          }

          boolean check =
              checkAdjustable(centerRGB, rStep, maxChannel, RGBBase.Channel.R, null) &&
              checkAdjustable(centerRGB, gStep, maxChannel, RGBBase.Channel.G, null) &&
              checkAdjustable(centerRGB, bStep, maxChannel, RGBBase.Channel.B, null);

          if (check) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.R += rStep;
            rgb.G += gStep;
            rgb.B += bStep;
            rgbList.add(rgb);
          }
        }
      }
    }
    return RGBArray.toRGBArray(rgbList);
  }

  /**
   * ���ocenterRGB�P�䪺RGB, �Hstep���@��step���
   * @param centerRGB RGB
   * @param step double step���
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step) {
    return getAroundRGB(centerRGB, null, step, false);
  }

  /**
   * ���ocenterRGB�P�䪺RGB, �Hstep���@��step���
   * @param centerRGB RGB
   * @param step double
   * @param involeWhite boolean �P�䪺RGB�n���n�]�A��
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step, boolean involeWhite) {
    return getAroundRGB(centerRGB, null, step, involeWhite);
  }

  /**
   * �qdelta�ҵ����ƾ�, �ӨM�w�n���n���LR��B���W��
   */
  protected static boolean ChromaticSkipByDelta = true;

  protected boolean whiteCheckAdjustable(RGB rgb, double step,
                                         RGBBase.Channel maxChannel) {
    if (this.isGreenMode()) {
      return false;
    }
    //�վ�᪺�I�n 1.>=0 2.<=maxcode
    double maxAfterValue = rgb.getValue(maxChannel) + step;
    double minAfterValue = rgb.getValue(rgb.getMinChannel()) + step;
    return maxAfterValue <= maxCode && maxAfterValue >= 0 &&
        minAfterValue <= maxCode && minAfterValue >= 0;
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
    else if (this.isGreenMode()) {
      if (adjustChannel == RGBBase.Channel.G && step != 0) {
        //�u�n�ʨ�G�W�D�N�O����!
        return false;
      }
    }

    if (ChromaticSkipByDelta == true && delta != null) {
      if (adjustChannel == RGBBase.Channel.R && delta[0] * step > 0) {
        /**
         * �P���N�O����
         * du>0����, R�@�w�n���t��. �ҥH�ۭ��@�w<0
         * �p�Gdu*step >0����, �N��
         * 1.du>0�BR�����վ� 2.du<0�BR���t�վ�
         * �H�W���O�h�l���վ�
         */
        return false;
      }
      if (adjustChannel == RGBBase.Channel.B && delta[1] * step < 0) {
        /**
         * �����N�O����
         * dv>0����, B�@�w�n������. �ҥH�ۭ��@�w>0
         * �p�Gdv*step <0����, �N��
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
   * �]�w����X�i�Ҧ��O�_�ͮ�
   * �p�G���B�����X�i�Ҧ�, �hRB�u�|��|���I; ����X�i�Ҧ��|��8���I.
   * @param chromaticExpandMode boolean
   */
  public void setChromaticExpandMode(boolean chromaticExpandMode) {
    this.chromaticExpandMode = chromaticExpandMode;
  }
}
