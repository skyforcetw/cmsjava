package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import shu.cms.lcd.material.*;
import shu.cms.lcd.material.Material;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �e�ɼҦ�:
 * �h�����|���v�T��,���]LCD�[���ʨ��¦���,�i��XYZ���ۥ[
 * �ϱ��Ҧ�:
 * �Q�Φh�Ӥ��PRGB��XYZ�ȩҲ��ͪ�max matrix,���_���ϱ�RGB,����i�e�\����
 *
 * @note 1:FlareType�ȥ��]��Darkest,�O���F�O�ҩҦ�relative XYZ���O����.
 * �n�O��relative XYZ�O�t��,getRGB�p���,���ͪ�maxInverMatrix�|�����D,�y���p��~�t�ܤj.
 * �]���n�O��XYZ������,�ȥ��]�w�ѼƬ�Darkest.
 *
 * @note 2:��ثe����(08/03/22)�Ұ��������ǽTLCD Model
 * @note 3:�ƦW�ܧ�, �����ǽTLCD Model(08/05/16)
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MultiMatrixModel
    extends ChannelIndependentModel {

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * gamma�ե�����k.
   * (����ĳ��ByPower)
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public static enum GammaCorrectMethod {

    ByPower, ByLuminance
  }

  private GammaCorrectMethod gammaCorrectMethod = GammaCorrectMethod.
      ByLuminance;

  /**
   * �ϥμҦ�
   * @param factor LCDModelFactor
   */
  public MultiMatrixModel(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  /**
   * �D�ȼҦ�
   * @param lcdTarget LCDTarget
   */
  public MultiMatrixModel(LCDTarget lcdTarget) {
    this(lcdTarget, lcdTarget, GammaCorrectMethod.ByLuminance);
  }

  static {
//    setFlareType(FlareType.Darkest);
  }

  /**
   * �D�ȼҦ�
   * @param lcdTarget LCDTarget
   * @param rCorrectLCDTarget LCDTarget
   * @param gammaCorrectMethod GammaCorrectMethod
   */
  public MultiMatrixModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget,
                          GammaCorrectMethod gammaCorrectMethod) {
    super(lcdTarget, rCorrectLCDTarget);

    integerMaxValue = RGB.MaxValue.getIntegerMaxValueByLevel(lcdTarget.getLevel());
    if (integerMaxValue == null) {
      integerMaxValue = RGB.MaxValue.Int8Bit;
    }

    this.gammaCorrectMethod = gammaCorrectMethod;
    this.rational.setDoRGBRational(true);

  }

  private LCDTargetInterpolator interpolator;
  /**
   * �ĥ�multi-matrix�D�Ѯ�,�̦h�����N(�D��)����
   */
  public final static int MAX_ITERATIVE_TIMES = 50;
  private boolean touchMaxIterativeTimes = false;

  /**
   * ���m touch max iterative���� ���p��
   */
  protected void resetTouchMaxIterativeTimes() {
    touchMaxIterativeTimes = false;
  }

  /**
   * �]�wmax iterative������
   * @param times int
   */
  protected void setTouchMaxIterativeTimes(int times) {
    touchMaxIterativeTimes = (times == MAX_ITERATIVE_TIMES - 1);
  }

  public static enum GetRGBMode {
    //��¨ϥΤUMode1��Mode2�Ӫ���

    Mode1(true, false), Mode2(false, true),
    //���O��̷f�t�ϥΤS�|���W�ϥΧ�Ӫ���, ���O��ƭȷ|�����smooth
    Mode12(true, true);

    private GetRGBMode(boolean mode1, boolean mode2) {
      this.mode1 = mode1;
      this.mode2 = mode2;
    }

    private boolean mode1;
    private boolean mode2;
  }

  /**
   * �]�wgetRGB�ɱĥΪ��Ҧ�, mode1����í�w
   */
  private GetRGBMode getRGBMode = GetRGBMode.valueOf(AutoCPOptions.getString(
      "MultiMatrix_GetRGBMode"));

  public void setGetRGBMode(GetRGBMode mode) {
    this.getRGBMode = mode;
  }

  /**
   * �p��RGB,�ϱ��Ҧ�
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    CIEXYZ rationalXYZ = (CIEXYZ) XYZ.clone();
    rational.XYZRationalize(rationalXYZ);

    RGB rgb1 = null, rgb2 = null;

    //==========================================================================
    // �Q�Ψ�ؤ��P���t��k, �p��X��t�̤p��
    // �o�O�@�� 1+1 >2 ���̦n�Ҥl, ���ɯ��I, �����u�I
    //==========================================================================
    if (getRGBMode.mode1) {
      rgb1 = getRGBByMultiMatrixRange(rationalXYZ, false);
    }
    if (getRGBMode.mode2) {
      rgb2 = getRGBByMultiMatrixRange(rationalXYZ, true);
    }
    if (getRGBMode.mode1 == true && getRGBMode.mode1 == getRGBMode.mode2) {
      double deltaE1 = calculateGetRGBDeltaE(rgb1, rationalXYZ, true).
          getCIE2000DeltaE();
      double deltaE2 = calculateGetRGBDeltaE(rgb2, rationalXYZ, true).
          getCIE2000DeltaE();
      return (deltaE1 < deltaE2) ? rgb1 : rgb2;
    }
    else {
      return getRGBMode.mode1 ? rgb1 : rgb2;
    }

    //==========================================================================
  }

  private double[] minLuminanceRGBValues;
  private boolean avoidLessThenZero = true;

  private void avoidLessThenZero(RGB rgb) {
    if (avoidLessThenZero) {
      double maxChannelValue = rgb.getValue(rgb.getMaxChannel());
      maxChannelValue = maxChannelValue <= 0 ? 1 : maxChannelValue;
      for (RGB.Channel ch : RGB.Channel.RGBChannel) {
        if (rgb.getValue(ch) <= 0) {
          rgb.setValue(ch, maxChannelValue);
          rgb.setFixed(true);
        }
      }
//      rgb.R = (rgb.R <= 0) ? maxChannelValue : rgb.R;
//      rgb.G = (rgb.G <= 0) ? maxChannelValue : rgb.G;
//      rgb.B = (rgb.B <= 0) ? maxChannelValue : rgb.B;
    }
  }

  /**
   *
   * @param luminanceRoughRGB RGB
   * @param XYZ CIEXYZ
   * @return RGB �s��Luminance RGB
   */
  protected RGB getRGBByMultiMatrix(final RGB luminanceRoughRGB,
                                    final CIEXYZ XYZ) {
    //==========================================================================
    // �ץ���DAC Value
    //==========================================================================
    RGB rgb = (RGB) luminanceRoughRGB.clone();
    double[] luminanceRGBValues = new double[3];
    rgb.getValues(luminanceRGBValues, RGB.MaxValue.Double1);
    correct.gammaUncorrect(rgb);
    //==========================================================================

    //���ͳ̤j�ȯx�}�̦n���n��0��
    avoidLessThenZero(rgb);
    double[][] inverseMatrix = getXYZInverseMatrix(rgb);
    if (inverseMatrix == null) {
      //�����^�ǭ쥻��luminanceRoughRGB�Y�i
      return luminanceRoughRGB;
    }
    else {
      //���ɦ^�Ǫ�RGB��luminance RGB�����
      RGB newRGB = matries.XYZToRGBByMatrix(XYZ, inverseMatrix);

      if (newRGB == null) {
        return luminanceRoughRGB;
      }
      //========================================================================
      // �o��ӨB�J�������n! �����n�NRGB�@���W�ƨ�0~1,�~�୼�W�쥻��RGB luminance��
      // �p��X�Ӫ��~�O���T�s��RGB luminance
      //========================================================================

      newRGB.changeMaxValue(RGB.MaxValue.Double1);
      double[] rgbValues = new double[3];

      rgbValues[0] = newRGB.R * luminanceRGBValues[0];
      rgbValues[1] = newRGB.G * luminanceRGBValues[1];
      rgbValues[2] = newRGB.B * luminanceRGBValues[2];
      //========================================================================

      rgbValues[0] = luminanceRGBValues[0] < minLuminanceRGBValues[0] ? 0
          : rgbValues[0];
      rgbValues[1] = luminanceRGBValues[1] < minLuminanceRGBValues[1] ? 0
          : rgbValues[1];
      rgbValues[2] = luminanceRGBValues[2] < minLuminanceRGBValues[2] ? 0
          : rgbValues[2];
      rgb.setValues(rgbValues, RGB.MaxValue.Double1);
      changeMaxValue(rgb);
      rgbValues = null;
      return rgb;
    }
  }

  /**
   * �NluminanceRGB��^DAC Value RGB��,�A�q��,�ݬO�_�۵�
   * @param luminanceRGB1 RGB
   * @param luminanceRGB2 RGB
   * @param maxValue MaxValue
   * @return boolean
   */
  private boolean equalsAfterQuantization(RGB luminanceRGB1,
                                          RGB luminanceRGB2,
                                          RGB.MaxValue maxValue) {
    RGB rgb1 = (RGB) luminanceRGB1.clone();
    RGB rgb2 = (RGB) luminanceRGB2.clone();
    correct.gammaUncorrect(rgb1);
    correct.gammaUncorrect(rgb2);
    return rgb1.equalsAfterQuantization(rgb2, maxValue);
  }

  private RGB.MaxValue integerMaxValue;
  private matries2 matries2 = new matries2();

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * ���G�׳̬۪�white��XYZ, �H��XYZ��@ �̤j�ȯx�} �h�i��matrix�B��, �o��Ĥ@��RGB��
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected class matries2
      implements MatriesInterface {

    protected Set<Patch> wChannelPatch;
    protected GammaCorrector _WrCorrector;

    protected matries2() {
      wChannelPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.W);
      _WrCorrector = GammaCorrector.getLUTInstance(wChannelPatch,
          RGBBase.Channel.W);
    }

    /**
     * ���ۦP�G�ת��զ��W�Dcode
     * @param XYZ CIEXYZ
     * @return double
     */
    protected double getWhiteDACValues(CIEXYZ XYZ) {
      CIEXYZ normalXYZ = (CIEXYZ) XYZ.clone();
      if (normalXYZ.getNormalizeY() == NormalizeY.Not) {
        normalXYZ.normalize(getLuminance());
      }
      else {
        normalXYZ.normalize(NormalizeY.Normal1);
      }
      double whiteDACValues = _WrCorrector.uncorrect(normalXYZ.Y);
      return whiteDACValues;
    }

    /**
     *
     * @param XYZ CIEXYZ
     * @return RGB
     */
    public final RGB XYZToRGBByMaxMatrix(CIEXYZ XYZ) {
      double w = getWhiteDACValues(XYZ);
      RGB luminanceRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {w,
                                 w, w}, RGB.MaxValue.Double1);
      changeMaxValue(luminanceRGB);
      RGB newRGB = getRGBByMultiMatrix(luminanceRGB, XYZ);
      return newRGB;
    }

    public CIEXYZ RGBToXYZByMaxMatrix(RGB rgb) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * �NRGB�ƭȧ@�X�i,���o�̤p����t�@�^��
   * (getRGBByMultiMatrix���i����)
   * @param XYZ CIEXYZ
   * @param byMatries2 boolean �ĥ�byMatries2�p��
   * @return RGB
   */
  protected RGB getRGBByMultiMatrixRange(final CIEXYZ XYZ, boolean byMatries2) {
    RGB rgb = null;
    if (byMatries2) {
      rgb = matries2.XYZToRGBByMaxMatrix(XYZ);
    }
    else {
      rgb = matries.XYZToRGBByMaxMatrix(XYZ);
    }
    rgbResult.firstRGB = (RGB) rgb.clone();
    avoidLessThenZero(rgb);
    RGB oldrgb = rgb;

    for (int x = 0; x < MAX_ITERATIVE_TIMES; x++) {
      RGB newRGB = getRGBByMultiMatrix(rgb, XYZ);

      if ( (newRGB.equals(rgb) || newRGB.equals(oldrgb))
          && isRGBClosely(newRGB, rgb)) {
        //RGB�۵�,RGB�O�_������,newRGB�O�_�P�W�W��RGB�۵�
        break;
      }

      if (equalsAfterQuantization(newRGB, rgb, integerMaxValue)) {
        //�q�ƫ�۵�
        break;
      }

      oldrgb = rgb;
      rgb = newRGB;
      setTouchMaxIterativeTimes(x);
    }

    correct.gammaUncorrect(rgb);
    //�N���W�ƪ�DAC RGB��^��l�j�p
    changeMaxValue(rgb);

    rational.RGBRationalize(rgb);
    return rgb;
  }

  /**
   * step���@�b
   */
  private final double halfStep = lcdTarget.getStep() / 2.;
//  private boolean rangeRGBWork = false;
//
//  public final boolean isRangeRGBWork() {
//    return rangeRGBWork;
//  }

  /**
   * ����first�Psecond��̬O�_�O�X�z��RGB(�O�_�۪�)
   * @param first RGB
   * @param second RGB
   * @return boolean
   */
  private final boolean isRGBClosely(RGB first, RGB second) {
    //���t�����P������
    int diffSignTimes = 0;
    //�t���F��⭿������
    int doubleTimes = 0;

    for (RGBBase.Channel c : RGBBase.Channel.RGBChannel) {
      double v1 = first.getValue(c);
      double v2 = second.getValue(c);
      double t = v1 * v2;
      double min = Math.min(v1, v2);
      //���t�����P������, ���t�����P�|�� t<0,
      diffSignTimes += (t < 0)
          ? //�p�G�b�@�b��step�H�U�S����, �N���̨��ܱ���0, �i�����o�Ӱ��D
          (Math.abs(min) < halfStep ? 0 : 1) : 0;
      t = (Math.max(v1, v2) / min);
      //�t���F��⭿
      doubleTimes += (!Double.isInfinite(t) && t >= 2.) ? 1 : 0;

      if (diffSignTimes >= 1 && doubleTimes >= 1) {
        return false;
      }
    }

    return true;
  }

  /**
   * �p��XYZ�Ȫ��ϯx�}
   * @param rgb RGB
   * @return double[][] �^��
   */
  private final double[][] getXYZInverseMatrix(RGB rgb) {
    CIEXYZ rXYZ = this.getXYZ(rgb.R, 0, 0);
    CIEXYZ gXYZ = this.getXYZ(0, rgb.G, 0);
    CIEXYZ bXYZ = this.getXYZ(0, 0, rgb.B);

    double[][] maxMatrix = matries.getMaxMatrix(rXYZ, gXYZ, bXYZ);
    DoubleArray.abs(maxMatrix);
    if (!DoubleArray.isNonsingular(maxMatrix)) {
      return null;
    }
    else {
      return DoubleArray.inverse(maxMatrix);
    }
  }

  /**
   * �p��XYZ,�e�ɼҦ�
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    CIEXYZ XYZ = getXYZ(rgb.R, rgb.G, rgb.B);
    return XYZ;
  }

  /**
   * �ھ�r/g/b�����XXYZ
   * @param r double
   * @param g double
   * @param b double
   * @return CIEXYZ relative XYZ,��K�����B���
   */
  protected CIEXYZ getXYZ(double r, double g, double b) {
    return getXYZ(r, g, b, true);
  }

  protected CIEXYZ getXYZ(double r, double g, double b, boolean relativeXYZ) {
    //interpolator�ҭp��X�Ӫ�XYZ���O�����
    CIEXYZ rXYZ = interpolator.getPatch(RGBBase.Channel.R, r).getXYZ();
    CIEXYZ gXYZ = interpolator.getPatch(RGBBase.Channel.G, g).getXYZ();
    CIEXYZ bXYZ = interpolator.getPatch(RGBBase.Channel.B, b).getXYZ();

    return recoverAbsoluteOrRelative(rXYZ, gXYZ, bXYZ, relativeXYZ);
  }

  public CIEXYZ getNeutralXYZ(double code, boolean relativeXYZ) {
    CIEXYZ XYZ = interpolator.getPatch(RGBBase.Channel.W, code).getXYZ();
    //�]��interpolator�X�ӬO�����, �ҥH�Ϧӭn�ۤϳB�z
    //�ҥH�o��n�ĥ�fromXYZ�ӫDtoXYZ, �ӥB�n��relativeXYZ�I�H�t�޿�
    XYZ = fromXYZ(XYZ, !relativeXYZ);
    return XYZ;
  }

  /**
   * �NrXYZ+gXYZ+bXYZ��absolute�����relative or absolute
   * @param rXYZ CIEXYZ
   * @param gXYZ CIEXYZ
   * @param bXYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return CIEXYZ
   */
  private final CIEXYZ recoverAbsoluteOrRelative(CIEXYZ rXYZ, CIEXYZ gXYZ,
                                                 CIEXYZ bXYZ,
                                                 boolean relativeXYZ) {
    CIEXYZ flare = this.flare.getFlare();
    CIEXYZ recover = LCDModelUtil.recover(rXYZ, gXYZ, bXYZ, flare);
    if (relativeXYZ) {
      //recover�����G,�|�Ѥ@��flare,�A��L����,�^�Ǭ۹�ȵ�LCDModel�h�B�z
      recover = CIEXYZ.minus(recover, flare);
    }
    return recover;
  }

  /**
   * �D�Y��
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    Interpolation.Algo[] interpolationType = LCDTargetInterpolator.
        InterpolationLinear;
    interpolator = LCDTargetInterpolator.Instance.get(lcdTarget,
        interpolationType);

    switch (gammaCorrectMethod) {
      case ByPower:
        singleChannel.produceRGBPowerPatch();
        correct.producePowerCorrector();
        break;
      case ByLuminance:
        singleChannel.produceRGBPatch();
        correct.produceGammaCorrector();
        break;
    }

    //�p��̤p�G�׭�
    double minValue = lcdTarget.getStep() / lcdTarget.getMaxValue().max;
    double[] minValues = new double[] {
        minValue, minValue, minValue};
    minLuminanceRGBValues = correct.gammaCorrect(minValues);

    Factor[] factors = new Factor[] {
        null, null, null};
    return factors;
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "MultiMatrix";
  }

  public static void main(String[] args) {

    LCDTarget lcdTarget = LCDTarget.Instance.getFromAUORampXLS("Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
        LCDTarget.Number.Ramp1024);
    LCDTarget.Operator.gradationReverseFix(lcdTarget);
//    LCDTarget lcdTarget = Material.getStoredLCDTarget();
    MultiMatrixModel.setFlareType(MultiMatrixModel.FlareType.Black);
    MultiMatrixModel mm = new MultiMatrixModel(lcdTarget);
    mm.produceFactor();
    System.out.println(lcdTarget.getBlackPatch());
    System.out.println(lcdTarget.getDarkestPatch());
    System.out.println(mm.getXYZ(0, 0, 0, true));
  }

  /**
   * �W�@����getRGB�p��,�O�_�F��MaxIterativeTimes
   * @return boolean
   */
  public boolean isTouchMaxIterativeTimes() {
    return touchMaxIterativeTimes;
  }
}
