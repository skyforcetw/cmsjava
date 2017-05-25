package auo.cms.hsv;

import java.io.*;

import java.awt.image.*;

import auo.cms.hsv.saturation.*;
import auo.cms.hsv.value.*;
import shu.cms.colorspace.depend.*;
import shu.cms.image.*;
import shu.math.array.*;

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
 * @deprecated
 */
public class IPComparator {
  public static void main(String[] args) throws Exception {
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//    imageBase(args);
  }

  /*public static void imageBase(String[] args) throws Exception {
    //==========================================================================
    // setting
    //==========================================================================
    String dir = "hsv/";
    String filename = dir + "rgb_cmy_H.bmp";

    //saturation
    //決定saturation的turn point
    byte turnPoint = 7;
    //決定saturation的turn point的bit數
    byte turnPointBit = 4;

//    int saturationAdjust = 30; //+-63
    //value
//    byte valueOffset = 25;

    //決定h s v是否要處理
    boolean doHueAdjust = true; //基本上本程式沒有對hue做處理, 所以此選項沒有意義
    boolean doSaturationAdjust = true;
    boolean doValuesAdjust = true;

    //決定saturation是否要clip
    boolean doSaturationClip = true;
    //決定value是否要clip
    boolean doValueClip = true;

    //決定是否要dump資料
    boolean doDump = true;
    //決定是否要手動設定lut值
    boolean doManualHSVLut = true;
    //==========================================================================
    // hsv lut setting  (在這邊改設定值)
    //==========================================================================
    HSVLUTInterpolator.ManualHueLut = new short[] {
        1, 34, 67, 100, 133, 166,
        199, 232, 265, 298, 331, 364,
        397, 430, 463, 496, 529, 562,
        595, 628, 661, 694, 727, 760
    };
    HSVLUTInterpolator.ManualSaturationLut = new byte[] {
        5, 5, 6, 6, 7, 7,
        7, 7, 8, 8, 9, 9,
        10, 8, 7, 6, 5, 5,
        6, 7, 8, 7, 6, 5
    };
    HSVLUTInterpolator.ManualValueLut = new byte[] {
        2, -2, -6, -10, -13, -12,
        -11, -10, -8, -10, -12, -14,
        -16, -17, -18, -19, -19, -18,
        -16, -15, -13, -10, -6, -2
    };

    //==========================================================================

    BufferedImage img = ImageUtils.loadImage(filename);
    WritableRaster raster = img.getRaster();
    int width = raster.getWidth();
    int height = raster.getHeight();
    double[] pixel = new double[3];
    double[] pixel10bit = new double[3];

    BufferedWriter rWriter = new BufferedWriter(new FileWriter(dir + "r.txt"));
    BufferedWriter gWriter = new BufferedWriter(new FileWriter(dir + "g.txt"));
    BufferedWriter bWriter = new BufferedWriter(new FileWriter(dir + "b.txt"));
   BufferedWriter h1Writer = new BufferedWriter(new FileWriter(dir + "h1.txt"));
   BufferedWriter h2Writer = new BufferedWriter(new FileWriter(dir + "h2.txt"));
    BufferedWriter sWriter = new BufferedWriter(new FileWriter(dir + "s.txt"));
    BufferedWriter vWriter = new BufferedWriter(new FileWriter(dir + "v.txt"));
   BufferedWriter saWriter = new BufferedWriter(new FileWriter(dir + "sa.txt"));
   BufferedWriter vaWriter = new BufferedWriter(new FileWriter(dir + "va.txt"));
    BufferedWriter h1pWriter = new BufferedWriter(new FileWriter(dir +
        "h1'.txt"));
    BufferedWriter h2pWriter = new BufferedWriter(new FileWriter(dir +
        "h2'.txt")); ;
   BufferedWriter spWriter = new BufferedWriter(new FileWriter(dir + "s'.txt"));
   BufferedWriter vpWriter = new BufferedWriter(new FileWriter(dir + "v'.txt"));
   BufferedWriter rpWriter = new BufferedWriter(new FileWriter(dir + "r'.txt"));
   BufferedWriter gpWriter = new BufferedWriter(new FileWriter(dir + "g'.txt"));
   BufferedWriter bpWriter = new BufferedWriter(new FileWriter(dir + "b'.txt"));

    BufferedWriter[] writers = new BufferedWriter[] {
        rWriter, gWriter, bWriter, h1Writer, h2Writer, sWriter, vWriter,
        saWriter, vaWriter, h1pWriter, h2pWriter, spWriter, vpWriter, rpWriter,
        gpWriter, bpWriter};
    short[] auoHSVValues = new short[3];
    short[] newAUOHSVValues = new short[3];
    double[] rgbpValues = new double[3];

    //==========================================================================
    // setting up formula
    //==========================================================================
    HSVLUTInterpolator hsvLut = HSVLUTInterpolator.getInstance(doManualHSVLut);
    IntegerSaturationFormula sformula = new IntegerSaturationFormula(turnPoint,
        turnPointBit);
    sformula.setInterpolateAdjust(true);
    ValuePrecisionEvaluator.setInterpolateOffset(true);
    //==========================================================================

    {
//      //==========================================================================
//      // dump lut data
//      //==========================================================================
//      double[] sLut = new double[24];
//      double[] vLut = new double[24];
//      for (int x = 0; x < 24; x++) {
//        sLut[x] = ( (int) hsvLut.getSaturationAdjust( (double) x * 15)) >> 5;
//        vLut[x] = ( (int) hsvLut.getValueAdjust( (double) x * 15)) >> 9;
//      }
//      int x = 1;
      //==========================================================================
    }
    int PIECE_OF_HUE = HSV.AUO.PIECE_OF_HUE;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.getPixel(x, y, pixel);
        RGB rgb = new RGB(RGB.ColorSpace.sRGB, pixel, RGB.MaxValue.Double255);
//        rgb.R = 160;
//        rgb.G = 90;
//        rgb.B = 75;
        rgb.getValues(pixel10bit, RGB.MaxValue.Int10Bit);
        HSV hsv = new HSV(rgb);

        //======================================================================
        // 原始HSV
        //======================================================================
        //轉成auo的hsv格式
        HSV.AUO.toHSVValues(hsv, auoHSVValues);
        short h = auoHSVValues[0];
//        System.out.println(h);
        short s = auoHSVValues[1];
        short v = auoHSVValues[2];
        int h1 = (int) hsv.H / 60;
        int h2 = (int) (hsv.H % 60 / 60. * Math.pow(2, 10));
        //======================================================================

        String space = ( (x < (width - 1)) ? " " : "");

        //======================================================================
        short newHue = h;
        if (doHueAdjust) {
//          newHue = hsvLut.getHue(h);
          double newHueDouble = hsvLut.getHueAdjustDouble(hsv.H);
//
          newHueDouble = newHueDouble / PIECE_OF_HUE * 360;
//       System.out.println(hsv.H + " " + newHueDouble);
          int h1p = (int) newHueDouble / 60;
//          int h2p = (int) Math.round(newHueDouble % 60 / 60. * Math.pow(2, 10));
          int h2p = (int) (newHueDouble % 60 / 60. * Math.pow(2, 10));
          newHue = (short) (newHueDouble / 360. * PIECE_OF_HUE);
//          System.out.println(h + " " + newHue);
          h1pWriter.write(Integer.toHexString(h1p) + space);
          h2pWriter.write(Integer.toHexString(h2p) + space);
        }
        //======================================================================
        short newSaturation = s;
        if (doSaturationAdjust) {
          short saturationAdjust = hsvLut.getSaturationAdjust(h);
          saturationAdjust = (short) (saturationAdjust >> 1);
          double doubleS = s / 1023. * 100;
          double resultDoubleS = sformula.getSaturartion(doubleS,
              saturationAdjust);
          newSaturation = (short) Math.round(resultDoubleS / 100. * 1023);
          newSaturation = (newSaturation < 0) ? 0 : newSaturation;
          if (doSaturationClip) {
            newSaturation = (newSaturation > 1023) ? 1023 : newSaturation;
          }
          if (doDump) {
            saWriter.write(Integer.toHexString(Math.abs(saturationAdjust)) +
                           space);
          }
        }
        //======================================================================

        //======================================================================
        short newValue = v;
        if (doValuesAdjust) {
          short valueAdjust = hsvLut.getValueAdjust(h);
          valueAdjust = (short) Math.round(valueAdjust / Math.pow(2, 5));
          short min = (short) DoubleArray.min(pixel10bit);
   newValue = (short) (ValuePrecisionEvaluator.getV(v, min, valueAdjust) +
                              0);
          if (doValueClip) {
            newValue = (newValue > 1023) ? 1023 : newValue;
          }

          if (doDump) {
            vaWriter.write(Integer.toHexString(Math.abs(valueAdjust)) + space);
          }
        }
        //======================================================================
        if (doDump) {
          rWriter.write(Integer.toHexString( (short) pixel10bit[0]) + space);
          gWriter.write(Integer.toHexString( (short) pixel10bit[1]) + space);
          bWriter.write(Integer.toHexString( (short) pixel10bit[2]) + space);
//          hWriter.write(Integer.toHexString(h) + space);
          h1Writer.write(Integer.toHexString(h1) + space);
          h2Writer.write(Integer.toHexString(h2) + space);

          sWriter.write(Integer.toHexString(s) + space);
          vWriter.write(Integer.toHexString(v) + space);
          spWriter.write(Integer.toHexString(newSaturation) + space);
          vpWriter.write(Integer.toHexString(newValue) + space);
        }
//        System.out.println(h + " " + auoHSVValues[0]);
//        newAUOHSVValues[0] = auoHSVValues[0];
        newAUOHSVValues[0] = newHue;
        newAUOHSVValues[1] = newSaturation;
        newAUOHSVValues[2] = newValue;
        HSV hsvp = HSV.AUO.fromHSVValues(newAUOHSVValues);
//        System.out.println(hsvp);
        RGB rgbp = hsvp.toRGB();
        rgbp.clip();
        rgbpValues = rgbp.getValues(rgbpValues,
                                    RGB.MaxValue.Int10Bit);
        if (doDump) {
          rpWriter.write(Integer.toHexString( (short) rgbpValues[0]) + space);
          gpWriter.write(Integer.toHexString( (short) rgbpValues[1]) + space);
          bpWriter.write(Integer.toHexString( (short) rgbpValues[2]) + space);
        }
        //======================================================================
        // 寫成圖
        //======================================================================
        rgbpValues = rgbp.getValues(rgbpValues, RGB.MaxValue.Int8Bit);
        raster.setPixel(x, y, rgbpValues);
        //======================================================================

        //======================================================================

      }
      for (BufferedWriter writer : writers) {
        writer.newLine();
      }
    }

    for (BufferedWriter writer : writers) {
      writer.flush();
      writer.close();
    }
    ImageUtils.storeBMPFImage(dir + "complete.bmp", img);
     }*/
}
