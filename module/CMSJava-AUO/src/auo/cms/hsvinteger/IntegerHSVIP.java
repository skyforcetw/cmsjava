package auo.cms.hsvinteger;

import auo.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.*;
import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsv.value.backup.ValuePrecisionEvaluator;
import java.util.*;
import auo.cms.hsv.HSVVersion;
import auo.cms.hsv.value.ValueFormulaIF;
import auo.cms.hsv.value.OriginalValueFormula;
import auo.cms.hsv.value.FastInegerValueFormula;

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
public class IntegerHSVIP {
  private SaturationFormula saturationFormula;
  private TuneParameter tuneParameter;
  private HSVLUT hsvLUT;
  private boolean hsvClip = true;
  public void setHSVClip(boolean hsvClip) {
    this.hsvClip = hsvClip;
  }

  private static HSVVersion hsvVersion = HSVVersion.v2;
  public static void setHSVVersion(HSVVersion version) {
    hsvVersion = version;
  }

  public static void main(String[] args) {

//    short[] h = new short[] {
//        12, 32, 72, 101, 125, 149, 192, 230, 262, 295, 320, 352, 391, 417, 450,
//        481, 512, 544, 576, 608, 640, 672, 704, 736};
    short[] h = new short[24];
    for (int x = 0; x < 24; x++) {
      h[x] = (short) (32 * x);
    }

//    byte[] s = new byte[] {
//        34, 32, 37, 34, 32, 33, 34, 34, 35, 33, 32, 32, 33, 35, 35, 27, 30, 30,
//        31, 32, 32, 32, 33, 35};
    byte[] s = new byte[24];
    for (int x = 0; x < 24; x++) {
      s[x] = (byte) 20;
    }

//    byte[] v = new byte[] {
//        -7, -6, -4, 1, 2, 0, -7, -3, -3, -4, -6, -3, -4, -6, 5, 1, -3, -8, -12,
//        -6, -3, -2, -2, -4};
    byte[] v = new byte[24];
    TuneParameter parameter = new TuneParameter(h, s, v);
//      OriginalSaturationFormula formula = new OriginalSaturationFormula();
    IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte) 4,
        7);
    IntegerHSVIP hsvip = new IntegerHSVIP(formula, parameter);
//    hsvip.setHSVVersion(HSVVersion.v1);

//    AUOHSV hsv = new AUOHSV(new RGB(255, 0, 0));

    System.out.println(new AUOHSV(new RGB(192, 48, 48)));
    System.out.println(hsvip.getHSV(new AUOHSV(new RGB(192, 48, 48))));

    double[][] rgbValues = new double[][] {
        {
        116, 81, 67}, {
        199, 147, 129}, {
        91, 122, 156}, {
        90, 108, 64}, {
        130, 128, 176}, {
        92, 190, 172}, {
        224, 124, 47}, {
        68, 91, 170}, {
        198, 82, 97}, {
        94, 58, 106}, {
        159, 189, 63}, {
        230, 162, 39}, {
        35, 63, 147}, {
        67, 149, 74}, {
        180, 49, 57}, {
        238, 198, 20}, {
        193, 84, 151}, {
        0, 136, 170}, {
        245, 245, 243}, {
        200, 202, 202}, {
        161, 163, 163}, {
        121, 121, 122}, {
        82, 84, 86}, {
        49, 49, 51}, {
        255, 255, 255}
    };
    int rgbcount = rgbValues.length;
//    RGB[] rgbArray = new RGB[rgbcount];
    for (int x = 0; x < rgbcount; x++) {
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValues[x],
                        RGB.MaxValue.Double255);
      AUOHSV hsv = new AUOHSV(rgb);
      AUOHSV hsv2 = hsvip.getHSV(hsv);
      RGB rgb2 = hsv2.toRGB();
      rgb2.changeMaxValue(RGB.MaxValue.Double255);
//      System.out.println(rgb + " " + rgb2);
//      System.out.println(hsv.toSimpleString() + " " + hsv2.toSimpleString());

    }
  }

  public IntegerHSVIP(SaturationFormula saturationFormula,
                      TuneParameter tuneParameter) {
    this.saturationFormula = saturationFormula;
    this.tuneParameter = tuneParameter;
    this.hsvLUT = new HSVLUT(this.tuneParameter);
    if (saturationFormula instanceof IntegerSaturationFormula) {
      ( (IntegerSaturationFormula) saturationFormula).
          setInterpolateAdjust(true);
    }
    ValuePrecisionEvaluator.setInterpolateOffset(true);
  }

  public AUOHSV getHSV(AUOHSV hsv) {
    short[] hsvValues = getHSVValues(hsv);
    return AUOHSV.fromHSVValues3(hsvValues);
  }

  public short[] getRGBValues(AUOHSV hsv) {
    short[] hsvValues = getHSVValues(hsv);
    short[] rgbValues = AUOHSV.getRGBValues(hsvValues);
    return rgbValues;
  }

  public final static short[] getHSVValues(AUOHSV hsv,
                                           SingleHueAdjustValue
                                           singleHueAdjustValue,
                                           SaturationFormula
                                           saturationFormula,
                                           boolean hsvClip) {
    short[] hsvIntpol = HSVLUT.getHSVIntpol(hsv, singleHueAdjustValue);
    short[] result = getHSVValues(hsv, hsvIntpol, saturationFormula,
                                  hsvClip);
    return result;
  }

  private static ValueFormulaIF valueFormulaIF;
  protected static short[] getHSVValues(AUOHSV hsv, short[] hsvIntpol,
                                        SaturationFormula
                                        saturationFormula,
                                        boolean hsvClip) {
    short[] hsvValues = new short[3];
    hsvValues[0] = hsvIntpol[0];

    hsvValues[1] = saturationFormula.getSaturartion(hsv.saturation,
        hsvIntpol[1]);

    if (HSVVersion.v1 == hsvVersion) {
      if (null == valueFormulaIF) {
        valueFormulaIF = new OriginalValueFormula();
      }
//      short offset = (short) (hsvIntpol[2] / Math.pow(2, 4));
      short offset = (short) (hsvIntpol[2] >> 4);
      hsvValues[2] = valueFormulaIF.getV(hsv.value, hsv.min, offset);
    }
    else if (HSVVersion.v2 == hsvVersion) {
      if (null == valueFormulaIF) {
        valueFormulaIF = new FastInegerValueFormula();
      }

//      short offset = (short) (hsvIntpol[2] / Math.pow(2, 4));
      short offset = (short) (hsvIntpol[2] >> 4);
//      hsvValues[2] = ValuePrecisionEvaluator.getV(hsv.value, hsv.min, offset);
      hsvValues[2] = valueFormulaIF.getV(hsv.value, hsv.min, offset);
    }

    if (hsvClip) {
      hsvValues[1] = hsvValues[1] > 1023 ? 1023 : hsvValues[1];
      hsvValues[1] = hsvValues[1] < 0 ? 0 : hsvValues[1];

      hsvValues[2] = hsvValues[2] > 1023 ? 1023 : hsvValues[2];
      hsvValues[2] = hsvValues[2] < 0 ? 0 : hsvValues[2];
    }
    return hsvValues;

  }

  public short[] getHSVValues(AUOHSV hsv) {
    //ºâ¥X¤º´¡­È
    short[] hsvIntpol = hsvLUT.getHSVIntpol(hsv);
    return getHSVValues(hsv, hsvIntpol, saturationFormula, hsvClip);
  }

}
