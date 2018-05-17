package auo.cms.hsvinteger;

import auo.cms.hsv.autotune.TuneParameter;
import auo.cms.colorspace.depend.AUOHSV;
import auo.cms.hsv.autotune.SingleHueAdjustValue;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class HSVLUT {
  private short[] hueAdjustValue;
  private byte[] saturationAdjustValue;
  private byte[] valueAdjustValue;

  private final static int getMaxIdx(AUOHSV auoHSV) {
    short r = auoHSV.r;
    short g = auoHSV.g;
    short b = auoHSV.b;
    if ( (r - g) >= 0 && (r - b) >= 0) {
      return 0;
    }
    else if ( (r - g) < 0 && (g - b) >= 0) {
      return 1;
    }
    else if ( (r - b) < 0 && (g - b) < 0) {
      return 2;
    }
    else {
      return 0;
    }
  }

  final static short[] getHSVIntpol(AUOHSV auoHSV,
                                    SingleHueAdjustValue
                                    singleHueAdjustValue) {
    short[] hsvIntpol = new short[4];
    short[] adjustValue = singleHueAdjustValue.getAdjustShortArray();
    hsvIntpol[0] = getHueIntpol(auoHSV, adjustValue[0], adjustValue[0]);
    hsvIntpol[1] = getSatIntpol(auoHSV, adjustValue[1], adjustValue[1]);
    hsvIntpol[2] = getLumIntpol(auoHSV, adjustValue[2], adjustValue[2]);
    return hsvIntpol;
  }

  public short[] getHSVIntpol(AUOHSV auoHSV) {
    int[] downAndUpperAddr = getDownAndUpperAddr(auoHSV);
    short[] hsvIntpol = new short[4];
    hsvIntpol[0] = getHueIntpol(auoHSV, downAndUpperAddr);
    hsvIntpol[1] = getSatIntpol(auoHSV, downAndUpperAddr);
    hsvIntpol[2] = getLumIntpol(auoHSV, downAndUpperAddr);
    hsvIntpol[3] = (short) downAndUpperAddr[0];
    return hsvIntpol;
  }

  private short getHueIntpol(AUOHSV auoHSV, int[] downAndUpperAddr) {
    int down = hueAdjustValue[downAndUpperAddr[0]];
    int up = hueAdjustValue[downAndUpperAddr[1]];
    return getHueIntpol(auoHSV, down, up);
  }

  private final static short getHueIntpol(AUOHSV auoHSV, int down, int up) {
    up = up < down ? up + 768 : up;

    int down_3 = down << 3;
    int up_down = up - down;
    int up_down_index = up_down * auoHSV.hueIndex;
    double up_down_index_double = ( (double) up_down_index) / Math.pow(2, 5);
    up_down_index = (int) Math.round(up_down_index_double);
    int hueIntpol = up_down_index + down_3;
    return (short) hueIntpol;
  }

  private short getSatIntpol(AUOHSV auoHSV, int[] downAndUpperAddr) {
    int down = saturationAdjustValue[downAndUpperAddr[0]];
    int up = saturationAdjustValue[downAndUpperAddr[1]];
    return getSatIntpol(auoHSV, down, up);
  }

  private final static short getSatIntpol(AUOHSV auoHSV, int down, int up) {
    int down_up = up - down;
    int sat_intval = down_up * auoHSV.hueIndex;

    int sat_intval_4 = sat_intval / 16;

    int down_4 = down * 16;
    int sat_intpol = sat_intval_4 + down_4;

    return (short) sat_intpol;
  }

  private short getLumIntpol(AUOHSV auoHSV, int[] downAndUpperAddr) {
    int down = valueAdjustValue[downAndUpperAddr[0]];
    int up = valueAdjustValue[downAndUpperAddr[1]];
    return getLumIntpol(auoHSV, down, up);
  }

  private final static short getLumIntpol(AUOHSV auoHSV, int down, int up) {
    int down_up = up - down;
    int lum_intval = down_up * auoHSV.hueIndex;

    int down_256 = down * 256;
    int lum_intpol = lum_intval + down_256;

    return (short) lum_intpol;
  }

  private final static int[] getDownAndUpperAddr(AUOHSV auoHSV) {
    int[] downAndUpperAddr = new int[2];
    downAndUpperAddr[0] = getDownAddrO(auoHSV);
    downAndUpperAddr[1] = downAndUpperAddr[0] + 1;
    downAndUpperAddr[1] = (downAndUpperAddr[1] == 24) ? 0 : downAndUpperAddr[1];
    return downAndUpperAddr;
  }

  /**
   * 從zone和max index找到低端的address
   * @param auoHSV AUOHSV
   * @return int
   */
  public final static int getDownAddrO(AUOHSV auoHSV) {
    int maxIdx = getMaxIdx(auoHSV);
    short r = auoHSV.r;
    short g = auoHSV.g;
    short b = auoHSV.b;

    int zone = auoHSV.zone >> 1; //0~7 ==> 0~3 (4)
    switch (maxIdx) {
      case 0:
        return ( (g - b) >= 0) ? zone : 20 + zone; //0~3 : 20~23
      case 1:
        return ( (r - b) <= 0) ? 8 + zone : ( (r == b) ? 8 : 4 + zone); //8~11 : 4~7
      case 2:
        return ( (r - g) >= 0) ? 16 + zone : 12 + zone; // 16~19 : 12~15
      case 3:
        return 0;
    }
    return -1;
  }

  private void init(short[] hueAdjustValue,
                    byte[] saturationAdjustValue,
                    byte[] valueAdjustValue) {
    this.hueAdjustValue = hueAdjustValue;
    this.saturationAdjustValue = saturationAdjustValue;
    this.valueAdjustValue = valueAdjustValue;
  }

  private TuneParameter tuneParameter;
  public TuneParameter getTuneParameter() {
    return tuneParameter;
  }

  public HSVLUT(TuneParameter tuneParameter) {
    init(tuneParameter.getHueAdjustValue(),
         tuneParameter.getSaturationAdjustValue(),
         tuneParameter.getValueAdjustValue());
    this.tuneParameter = tuneParameter;
  }

  public static void main(String[] args) {
    //==========================================================================
    //manual
    short[] hueAdjustValue = new short[] {
        1, 40, 75, 108, 139, 165,
        194, 225, 254, 285, 316, 352,
        387, 425, 466, 506, 540, 566,
        591, 616, 640, 665, 697, 730
    };
    byte[] saturationAdjustValue = new byte[] {
        3, 4, 6, 7, 11, 9,
        8, 7, 7, 6, 5, 4,
        3, 2, 2, 1, 10, 10,
        11, 11, 11, 9, 6, 4};
    byte[] valueAdjustValue = new byte[] {
        11, 8, 6, 3, 19, 20,
        22, 23, 26, 21, 16, 8,
        5, 9, 13, 16, 20, 18,
        17, 16, 15, 14, 12, 12};
    TuneParameter tuneParameter = new TuneParameter(hueAdjustValue,
        saturationAdjustValue, valueAdjustValue);
    //==========================================================================
    HSVLUT hsvLut = new HSVLUT(tuneParameter);

  }

  public final static HSVLUT getTestInstance() {
    //==========================================================================
//manual
    short[] hueAdjustValue = new short[] {
        1, 40, 75, 108, 139, 165,
        194, 225, 254, 285, 316, 352,
        387, 425, 466, 506, 540, 566,
        591, 616, 640, 665, 697, 730
    };
    byte[] saturationAdjustValue = new byte[] {
        3, 4, 6, 7, 11, 9,
        8, 7, 7, 6, 5, 4,
        3, 2, 2, 1, 10, 10,
        11, 11, 11, 9, 6, 4};
    byte[] valueAdjustValue = new byte[] {
        11, 8, 6, 3, 19, 20,
        22, 23, 26, 21, 16, 8,
        5, 9, 13, 16, 20, 18,
        17, 16, 15, 14, 12, 12};
    TuneParameter tuneParameter = new TuneParameter(hueAdjustValue,
        saturationAdjustValue, valueAdjustValue);
//==========================================================================
    HSVLUT hsvLut = new HSVLUT(tuneParameter);
    return hsvLut;
  }

}
