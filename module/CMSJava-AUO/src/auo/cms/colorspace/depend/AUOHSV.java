package auo.cms.colorspace.depend;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.image.*;
import shu.math.*;
import auo.cms.hsvinteger.HSVLUT;
import auo.cms.hsvinteger.IntegerHSVIP;
import auo.cms.hsv.saturation.IntegerSaturationFormula;
import auo.cms.hsv.autotune.TuneParameter;
import auo.cms.hsvinteger.HSVIPComparator;
import shu.image.*;

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
public class AUOHSV {
  private static boolean RGBClipping = true;
  public final static void setRGBClipping(boolean rgbClipping) {
    RGBClipping = rgbClipping;
  }

  public final short[] getRGBValues() {
    return new short[] {
        r, g, b};
  }

  public final RGB toRGB() {
    short[] rgbValues = this.getRGBValues();
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, toDoubleArray(rgbValues),
                      RGB.MaxValue.Int10Bit);
    return rgb;
  }

//  public final static

  public final static short[] getRGBValues(short[] hsvValues3) {
    short gen_hi = (short) (hsvValues3[0] >> 10);
    short gen_f = (short) (hsvValues3[0] & 1023);
    short gen_s = hsvValues3[1];

//    short gen_fs = (short) Math.round(gen_f * gen_s / Math.pow(2, 8));
//    short gen_vfs = (short) Math.round(gen_fs * hsvValues3[2] / Math.pow(2, 12));
//    short gen_vs = (short) Math.round(gen_s * hsvValues3[2] / Math.pow(2, 10));

    short gen_fs = (short) Math.round( (gen_f * gen_s) >> 8);
    short gen_vfs = (short) Math.round( (gen_fs * hsvValues3[2]) >> 12);
    short gen_vs = (short) Math.round( (gen_s * hsvValues3[2]) >> 10);

    short v = hsvValues3[2];

    short gen_p = (short) (v - gen_vs);
    gen_p = gen_p < 0 ? 0 : gen_p;

    short gen_q = (short) (v - gen_vfs);
    gen_q = gen_q < 0 ? 0 : gen_q;

    short gen_t = (short) (gen_vfs + v - gen_vs);
    gen_t = gen_t > 8191 ? 8191 : gen_t;
    short[] rgbValues = new short[3];
    gen_hi = (short) (gen_hi % 6);

    switch (gen_hi) {
      case 0:
        rgbValues[0] = v;
        rgbValues[1] = gen_t;
        rgbValues[2] = gen_p;
        break;
      case 1:
        rgbValues[0] = gen_q;
        rgbValues[1] = v;
        rgbValues[2] = gen_p;
        break;
      case 2:
        rgbValues[0] = gen_p;
        rgbValues[1] = v;
        rgbValues[2] = gen_t;
        break;
      case 3:
        rgbValues[0] = gen_p;
        rgbValues[1] = gen_q;
        rgbValues[2] = v;
        break;
      case 4:
        rgbValues[0] = gen_t;
        rgbValues[1] = gen_p;
        rgbValues[2] = v;
        break;
      case 5:
        rgbValues[0] = v;
        rgbValues[1] = gen_p;
        rgbValues[2] = gen_q;
        break;
    }
    if (RGBClipping) {
      rgbValues[0] = (rgbValues[0] > 1023) ? 1023 : rgbValues[0];
      rgbValues[1] = (rgbValues[1] > 1023) ? 1023 : rgbValues[1];
      rgbValues[2] = (rgbValues[2] > 1023) ? 1023 : rgbValues[2];

      rgbValues[0] = (rgbValues[0] < 0) ? 0 : rgbValues[0];
      rgbValues[1] = (rgbValues[1] < 0) ? 0 : rgbValues[1];
      rgbValues[2] = (rgbValues[2] < 0) ? 0 : rgbValues[2];
    }
    return rgbValues;
  }

  public final static AUOHSV fromHSVValues3(short[] hsvValues3) {
    short[] rgbValues = AUOHSV.getRGBValues(hsvValues3);
    double[] doubleRGBValues = toDoubleArray(rgbValues);
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, doubleRGBValues,
                      RGB.MaxValue.Int10Bit);
    return new AUOHSV(rgb);
  }

  private static double[] toDoubleArray(short[] shortArray) {
    int size = shortArray.length;
    double[] doubleArray = new double[shortArray.length];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = shortArray[x];
    }
    return doubleArray;
  }

  public AUOHSV(RGB rgb) {
    this(fromRGBValues(rgb.get10BitValues()));
  }

  public AUOHSV(HSV hsv) {
    this(hsv.toRGB());
  }

  protected AUOHSV(short[] auoHSVValues) {
    this(auoHSVValues[3], auoHSVValues[0], auoHSVValues[1], auoHSVValues[2],
         auoHSVValues[4], auoHSVValues[5], auoHSVValues[6], auoHSVValues[7],
         auoHSVValues[8]);
  }

  public short zone;
  public short hueIndex;
  public short saturation;
  public short value;
  public short r;
  public short g;
  public short b;
  public short min;
  public short divH;

  protected AUOHSV(short zone, short hueIndex, short saturation, short value,
                   short min, short r, short g, short b, short divH) {
    this.zone = zone;
    this.hueIndex = hueIndex;
    this.saturation = saturation;
    this.value = value;
    this.min = min;
    this.r = r;
    this.g = g;
    this.b = b;
    this.divH = divH;
  }

  private static short getHDividend(short[] rgbValues) {
    short r = rgbValues[0];
    short g = rgbValues[1];
    short b = rgbValues[2];
    if (r >= g && r >= b) { // bmax == r
      return (short) (g >= b ? Math.abs(g - b) :
                      Math.abs(r - g) - Math.abs(g - b));
    }
    else if (r < g && g >= b) { //bmax == g
      return (short) ( (r == b || b > r) ? Math.abs(r - b) :
                      Math.abs(g - b) - Math.abs(r - b));
    }
    else if (b > r && b > g) { //bmax == b
      return (short) (r >= g ? Math.abs(r - g) :
                      Math.abs(r - b) - Math.abs(r - g));
    }
    else {
      return 0;
    }
  }

  private final static short getDiv(short a, short b) {
//    short div = (short) ( ( (double) a) / b * (Math.pow(2, 10)));
    short div = (short) ( ( (double) (a << 10)) / b);
//    short div = (short) ( ( (double) (a << 10)) / b);
    return div;
  }

  protected static short[] fromRGBValues(short[] rgbValues) {
    short max = Maths.max(rgbValues);
    short min = Maths.min(rgbValues);
    short max_min = (short) (max - min);
    short sDivq = getDiv(max_min, max);
    sDivq = (sDivq == 1024) ? 1023 : sDivq;
    short hDividend = getHDividend(rgbValues);
    short divH = getDiv(hDividend, max_min);
    divH = (divH == 1024) ? 1023 : divH;
    short hPre3Divq = (short) (divH >> 7);
    short hDivq = (short) (divH & 255);

    short[] auoHSVValues = new short[NUMBER_BAND];

    auoHSVValues[0] = hDivq;
    auoHSVValues[1] = sDivq;
    auoHSVValues[2] = max;
    auoHSVValues[3] = hPre3Divq;
    auoHSVValues[4] = min;
    auoHSVValues[5] = rgbValues[0];
    auoHSVValues[6] = rgbValues[1];
    auoHSVValues[7] = rgbValues[2];
    auoHSVValues[8] = divH;
    return auoHSVValues;
  }

  public int getHue() {
    int downAddress = this.getDownAddrress();
    int hue = downAddress * 255 + hueIndex;
    return hue;
  }

  public double getHueInDegree() {
    double normal = getHue() / (24. * 255);
    return normal * 360;
  }

  public final static int NUMBER_BAND = 9;
  public short[] getValues() {
    return getValues(new short[NUMBER_BAND]);
  }

  public short[] getValues(short[] values) {
    if (values.length != NUMBER_BAND) {
      throw new IllegalArgumentException("values.length != " + NUMBER_BAND);
    }
    values[0] = hueIndex;
    values[1] = saturation;
    values[2] = value;
    values[3] = zone;
    values[4] = min;
    values[5] = r;
    values[6] = g;
    values[7] = b;
    values[8] = divH;
    return values;
  }

  public final int getDownAddrress() {
    return HSVLUT.getDownAddrO(this);
  }

  public static void main(String[] args) throws Exception {
//    dump(args);

    for (int x = 0; x < 360; x += 15) {
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {x, 50, 50});
      RGB rgb = hsv.toRGB();
      AUOHSV auohsv = new AUOHSV(rgb);
      System.out.println(rgb);
      System.out.println(hsv.H + " " + auohsv.getHueInDegree() + " " +
                         auohsv.getHue());
//      System.out.println(auohsv + "/" + auohsv.getDownAddrress() + " " +
//                         auohsv.getHue());
    }

  }

  public static void dump(String[] args) throws Exception {
    String filename = "pattern24.bmp";
//    String parameterFilename = "condition normal.txt";
//    byte turnPoint = 7;
    String parameterFilename = "condition boundry.txt";
    byte turnPoint = 1;

    //==========================================================================
    // hw read
    //==========================================================================
//    String hardwareDumpDir = "normal/";
//    String hardwareDumpDir = "normal_line_5/";
    String hardwareDumpDir = "boundry/";
    int[] new_R = HSVIPComparator.getDatas(hardwareDumpDir + "new_R.txt");
    int[] new_G = HSVIPComparator.getDatas(hardwareDumpDir + "new_G.txt");
    int[] new_B = HSVIPComparator.getDatas(hardwareDumpDir + "new_B.txt");

//    int[] new_R = HSVIPComparator.getDatas(hardwareDumpDir + "new_R.txt");
//    int[] new_S = HSVIPComparator.getDatas(hardwareDumpDir + "sat_new.txt");
//    int[] new_V = HSVIPComparator.getDatas(hardwareDumpDir + "val_new.txt");
    int[][] hsv_intpol = HSVIPComparator.getDatasArray(hardwareDumpDir +
        "hsv_intpol.txt");
    int[] new_S = HSVIPComparator.getDatas(hardwareDumpDir + "sat_new.txt");
    int[] new_V = HSVIPComparator.getDatas(hardwareDumpDir + "val_new.txt");
    //==========================================================================
    System.out.println("hw load ok");

    //==========================================================================
    // load image
    //==========================================================================
    BufferedImage img = ImageUtils.loadImage(filename);
    WritableRaster raster = img.getRaster();
    final int w = raster.getWidth();
    final int h = raster.getHeight();
//    final int h = 5;
    //==========================================================================

    int[] rgbValues = new int[3];
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    RGB rgb = new RGB(colorspace, new int[3]);

//    BufferedWriter dump = new BufferedWriter(new FileWriter("dump.txt"));
//    BufferedWriter r = new BufferedWriter(new FileWriter("r.txt"));
//    BufferedWriter g = new BufferedWriter(new FileWriter("g.txt"));
//    BufferedWriter b = new BufferedWriter(new FileWriter("b.txt"));
//    BufferedWriter zone = new BufferedWriter(new FileWriter("zone.txt"));
//    BufferedWriter hue = new BufferedWriter(new FileWriter("hue.txt"));
//    BufferedWriter s = new BufferedWriter(new FileWriter("s.txt"));
//    BufferedWriter v = new BufferedWriter(new FileWriter("v.txt"));
//    BufferedWriter[] writers = new BufferedWriter[] {
//        r, g, b, zone, hue, s, v};
    TuneParameter tuneParameter = TuneParameter.getInstanceFromFile(
        parameterFilename);
    HSVLUT hsvLut = new HSVLUT(tuneParameter);
    IntegerSaturationFormula integerSaturationFormula = new
        IntegerSaturationFormula(turnPoint, 4);
    IntegerHSVIP hsvIP = new IntegerHSVIP(integerSaturationFormula,
                                          hsvLut.getTuneParameter());
    int index = 0;
    int errorCount = 0;
//    short newR, newG, newB, newS, newV;
    short[] newRGB = new short[3];
    short[] newSV = new short[2];
    short[] hsvintpol = null;
    int[] new_hsv_intpol = null;
    short[] hsvValues = null;
    short maxDiff = 0;

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
//        if (x == 1365 && y == 2) {
//          System.out.println("");
//        }
        raster.getPixel(x, y, rgbValues);
        rgb.R = rgbValues[0];
        rgb.G = rgbValues[1];
        rgb.B = rgbValues[2];
        AUOHSV auoHSV = new AUOHSV(rgb);
//        System.out.println(y + " " + x + " " + auoHSV.r / 4 + ", " +
//                           auoHSV.g / 4 + ", " + auoHSV.b / 4 + " " + auoHSV);


        if (index == 1) {
          System.out.println("");
        }
//        newSV[0] = (short) new_S[index];
//        newSV[1] = (short) new_V[index];
//        int[] newHSVintpol = hsv_intpol[index];

        hsvintpol = hsvLut.getHSVIntpol(auoHSV);
//        hsvValues = hsvIP.getHSVValues(auoHSV);
        newRGB[0] = (short) new_R[index];
        newRGB[1] = (short) new_G[index];
        newRGB[2] = (short) new_B[index];
//        newR = (short) new_R[index];
//        newG = (short) new_G[index];
//        newB = (short) new_B[index];
        newSV[0] = (short) new_S[index];
        newSV[1] = (short) new_V[index];
        new_hsv_intpol = hsv_intpol[index];

        short[] rgbValues2 = hsvIP.getHSV(auoHSV).getRGBValues();
//        short[] rgbValues2 = AUOHSV.getRGBValues(hsvValues);
//        String str = index++ +" " + auoHSV.zone + " " + auoHSV.hueIndex +
//            " " + auoHSV.saturation + " " + auoHSV.value + " " +
//            +hsvintpol[3] + " " + hsvintpol[0] + " " +
//            hsvintpol[1] + " " + hsvintpol[2] + " " +
//            hsvValues[1] + " " + hsvValues[2] + " " +
//            rgbValues2[0] + " " + rgbValues2[1] + " " +
//            rgbValues2[2] + "\n"; ;
//        dump.write(str);


        if (rgbValues2[0] != newRGB[0] || rgbValues2[1] != newRGB[1] ||
            rgbValues2[2] != newRGB[2]) {
          short rDiff = (short) Math.abs(rgbValues2[0] - newRGB[0]);
          short gDiff = (short) Math.abs(rgbValues2[1] - newRGB[1]);
          short bDiff = (short) Math.abs(rgbValues2[2] - newRGB[2]);
          maxDiff = maxDiff < rDiff ? rDiff : maxDiff;
          maxDiff = maxDiff < gDiff ? gDiff : maxDiff;
          maxDiff = maxDiff < bDiff ? bDiff : maxDiff;
//          if (hsvintpol[1] > 0 && hsvintpol[2] < 0) {
//            System.out.println("");
//          }
          errorCount++;
//          hsvIP.getHSVValues(auoHSV);
        }

        index++;
//        System.out.println(index++ +" " + auoHSV.zone + " " + auoHSV.hueIndex +
//                           " " + auoHSV.saturation + " " + auoHSV.value + " " +
//                           +hsvintpol[3] + " " + hsvintpol[0] + " " +
//                           hsvintpol[1] + " " + hsvintpol[2] + " " +
//                           hsvValues[1] + " " + hsvValues[2] + " " +
//                           rgbValues2[0] + " " + rgbValues2[1] + " " +
//                           rgbValues2[2]);

//        r.write(auoHSV.r + " ");
//        g.write(auoHSV.g + " ");
//        b.write(auoHSV.b + " ");
//        zone.write(auoHSV.zone + " ");
//        hue.write(auoHSV.hueIndex + " ");
//        s.write(auoHSV.saturation + " ");
//        v.write(auoHSV.value + " ");
      }
//      dump.newLine();
//      for (BufferedWriter writer : writers) {
//        writer.newLine();
//      }
    }
//    dump.close();
//    for (BufferedWriter writer : writers) {
//      writer.flush();
//      writer.close();
//    }
    System.out.println("errorCount: " + errorCount);
    System.out.println(maxDiff);
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   * @todo Implement this java.lang.Object method
   */
  public String toString() {
    short[] values = getValues();
//    return Arrays.toString(getValues());
    return "[hue index: " + values[0] + " sat: " + values[1] + " val: " +
        values[2] + " zone: " + values[3] + " min: " + values[4] + " r: " +
        values[5] + " g: " + values[6] + " b:" + values[7] + " divH: " +
        values[8] + "]";
  }

  public String toSimpleString() {
    short[] values = getValues();
//    return Arrays.toString(getValues());
    return "[hue index: " + values[0] + " zone: " + values[3] + " sat: " +
        values[1] + " val: " +
        values[2] + "]";

  }
}
