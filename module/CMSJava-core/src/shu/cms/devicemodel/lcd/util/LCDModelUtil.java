package shu.cms.devicemodel.lcd.util;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LCDModelUtil {

  /**
   * �L�o�X��@�W�D��������X
   * @param lcdTarget LCDTarget
   * @param ch Channel
   * @return Set
   * @deprecated LCDTarget.Filter.grayScalePatchSet()
   */
  public final static Set<Patch> producePatchSet(LCDTarget lcdTarget,
                                                 RGBBase.Channel ch) {
    Set<Patch> singleChannelPatch = new TreeSet<Patch> ();
    /**
     * @note �ĥ�blackPatch�����X�z, ���O���i�঳���઺�{�H(��]�b��������t����í�w),
     * �ӥB�p�⵲�G�i��X��.
     * �ĥ�darkestPatch�p�⵲�G�|�������T.
     * ��ĳ�ĥ�darkestPatch
     */
//    Patch darkestPatch = lcdTarget.getDarkestPatch();
    Patch darkestPatch = lcdTarget.getBlackPatch();
    singleChannelPatch.add(darkestPatch);
    List<Patch> oneValueChannel = null;
    if (ch == RGBBase.Channel.W) {
      oneValueChannel = lcdTarget.filter.grayPatch();
    }
    else {
      oneValueChannel = lcdTarget.filter.oneValueChannel(ch);
    }
    singleChannelPatch.addAll(oneValueChannel);
    return singleChannelPatch;
  }

  /**
   * �������oflare���覡
   * @param flareType FlareType
   * @param lcdTarget LCDTarget
   * @return CIEXYZ
   */
  public final static CIEXYZ evaluateFlare(LCDModelBase.FlareType flareType,
                                           LCDTarget lcdTarget) {
    switch (flareType) {
      case Black:
        return lcdTarget.getBlackPatch().getXYZ();
      case Darkest:
        return lcdTarget.getDarkestPatch().getXYZ();
      case Estimate:
        FlareCalculator cal = new FlareCalculator(lcdTarget);
        return cal.getFlare();
      default:
        return null;
    }
  }

  /**
   *
   * @param XYZValues double[]
   * @param maxInverse double[][]
   * @return double[] luminance RGB
   */
  public static final double[] XYZ2RGB(double[] XYZValues,
                                       double[][] maxInverse) {
    double[] relativeXYZValues = XYZValues.clone();
    relativeXYZValues = XYZrationalize(relativeXYZValues);
    return DoubleArray.times(maxInverse, relativeXYZValues);
  }

  protected final static double[] XYZrationalize(double[] values) {
    int size = values.length;
    for (int x = 0; x < size; x++) {
      values[x] = XYZrationalize(values[x]);
    }
    return values;
  }

  protected final static double XYZrationalize(double val) {
    val = val < 0 ? 0 : val;
    return val;
  }

  public static final double[] RGB2XYZ(double[] rgb, double[][] max) {
    return DoubleArray.times(max, rgb);
  }

  protected static boolean isValid(Set<Patch> singleChannelPatch,
      boolean byPower) {
    //�e�@�ӰѦҭ�
    Patch forwardPatch = null;

    for (Patch p : singleChannelPatch) {
      if (forwardPatch == null) {
        forwardPatch = p;
        continue;
      }
      double now = byPower ? p.getXYZ().getPowerByXYZ() : p.getXYZ().Y;
      double pre = byPower ? forwardPatch.getXYZ().getPowerByXYZ() :
          forwardPatch.getXYZ().Y;

      if (now <= pre) {
        //�����઺����
        return false;
      }
      else {
        //���`
        forwardPatch = p;
      }
    }
    return true;
  }

  /**
   * rXYZ+gXYZ+bXYZ-2*black
   * @param rXYZ CIEXYZ
   * @param gXYZ CIEXYZ
   * @param bXYZ CIEXYZ
   * @param black CIEXYZ
   * @return CIEXYZ
   */
  public final static CIEXYZ recover(CIEXYZ rXYZ, CIEXYZ gXYZ, CIEXYZ bXYZ,
                                     CIEXYZ black) {
    return CIEXYZ.minus(CIEXYZ.minus(CIEXYZ.plus(CIEXYZ.plus(rXYZ, gXYZ), bXYZ),
                                     black), black);
  }

  /**
   * rXYZ+gXYZ+bXYZ
   * @param rXYZ CIEXYZ
   * @param gXYZ CIEXYZ
   * @param bXYZ CIEXYZ
   * @return CIEXYZ
   */
  public final static CIEXYZ recover(CIEXYZ rXYZ, CIEXYZ gXYZ, CIEXYZ bXYZ) {
    return CIEXYZ.plus(CIEXYZ.plus(rXYZ, gXYZ), bXYZ);
  }

  public final static double[] fixRGB(double[] rgbValues,
                                      RGBBase.MaxValue maxType) {
    double tolerance = maxType.getStepIn255() / 2.;

    int size = rgbValues.length;
    for (int x = 0; x < size; x++) {
      if (rgbValues[x] < 0) {
        rgbValues[x] = Math.abs(rgbValues[x]) < tolerance ? 0 : rgbValues[x];
      }
      else if (rgbValues[x] > 1) {
        rgbValues[x] = (rgbValues[x] - 1) < tolerance ? 1 : rgbValues[x];
      }
    }
    return rgbValues;
  }

  /**
   * ����Y���X�z��,���X�z�h����.
   * Y���Ӻ������W,�p�G������,�i��O1.���� 2.�ù� �y����
   * �������o�ǲz�פW���X�z���ƭ�,���U��Ҧ������T��
   * @param singleChannelPatch Set
   * @return Set
   * @deprecated
   */
  protected static Set<Patch> validateY(Set<Patch> singleChannelPatch) {
    //�e�@�ӰѦҭ�
    Patch forwardPatch = null;
    Set<Patch> remove = new LinkedHashSet<Patch> ();

    for (Patch p : singleChannelPatch) {
      if (forwardPatch == null) {
        forwardPatch = p;
        continue;
      }
      if (p.getXYZ().Y <= forwardPatch.getXYZ().Y) {
        //�����઺����
//        if (p.getRGB().getSaturationChannels() != 0) {
//          remove.add(forwardPatch);
//        }
//        else {
        remove.add(p);
//        }
      }
      else {
        //���`
        forwardPatch = p;
      }
    }
//    System.out.println(Utils.toString(singleChannelPatch));
    singleChannelPatch.removeAll(remove);
//    System.out.println(Utils.toString(singleChannelPatch));
    return singleChannelPatch;
  }

  public static Set<Patch> validate(Set<Patch> singleChannelPatch,
      boolean byPower) {
    //�e�@�ӰѦҭ�
    Patch forwardPatch = null;
    Set<Patch> remove = new LinkedHashSet<Patch> ();

    for (Patch p : singleChannelPatch) {
      if (forwardPatch == null) {
        forwardPatch = p;
        continue;
      }
      double now = byPower ? p.getXYZ().getPowerByXYZ() : p.getXYZ().Y;
      double pre = byPower ? forwardPatch.getXYZ().getPowerByXYZ() :
          forwardPatch.getXYZ().Y;
      if (now < pre) {
        remove.add(p);
      }
      else {
        //���`
        forwardPatch = p;
      }
    }
    singleChannelPatch.removeAll(remove);
    return singleChannelPatch;
  }

  /**
   *
   * @param secondaryColorChannel Channel
   * @return Channel
   * @deprecated
   */
  public final static RGBBase.Channel getXTalkChannel(RGBBase.Channel
      secondaryColorChannel) {
    if (!secondaryColorChannel.isSecondaryColorChannel()) {
      throw new IllegalArgumentException(
          "!secondaryColorChannel.isSecondaryColorChannel()");
    }
    switch (secondaryColorChannel) {
      case C:
        return RGBBase.Channel.G;
      case M:
        return RGBBase.Channel.B;
      case Y:
        return RGBBase.Channel.R;
      default:
        return null;
    }
  }

}
