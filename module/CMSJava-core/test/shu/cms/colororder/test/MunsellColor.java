package shu.cms.colororder.test;

import java.util.*;

import shu.cms.*;
import shu.cms.colororder.pxl.MunsellBookOfColors;

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
public class MunsellColor {

  private String notation;

  private String hue;
  private String basicHue;
  private double hueValue;
  private double value;
  private double chroma;

  // hueIndex = 0, ..., 39
  private int hueIndex;

  // basicHueIndex = 0, ..., 9
  private int basicHueIndex;

  // valueIndex = 1, ..., 9
  // 1=2.5, 2=3.0, ..., 7=8.0, 8=8.5, 9=9
  private int valueIndex;

  // chromaIndex = 1, 2, 4, 6, ..., 14
  // is the integer value of a valid chroma level
  private int chromaIndex;

  public static void main(String[] args) {
    MunsellColor mc = new MunsellColor("2.5GY 9/10");
    System.out.println(mc.toString());
  }

  /** Create a Munsell color named by the given argument using
      standard Munsell notation.
       @param mc Munsell color notation.
   */
  public MunsellColor(String mc) {
    parseMunsellNotation(mc);
    hueIndex = MunsellBookOfColors.findHueIndex(hue);
    basicHueIndex = MunsellBookOfColors.findBasicHueIndex(basicHue);
    valueIndex = MunsellBookOfColors.findValueIndex(value);
    chromaIndex = MunsellBookOfColors.findChromaIndex(hueIndex, valueIndex,
        chroma);
  }

  /** Create a Munsell color with the given properties.
      @param h Munsell Hue index. Admissible Hue index values are
      from 0 to 39 corresponding '2.5R' to '10RP'.
      @param v Munsell Value between 0.0 and 10.0
      @param c Munsell Chroma.
   */
  public MunsellColor(int h, double v, double c) {
    hueIndex = h;
    value = v;
    chroma = c;
    valueIndex = MunsellBookOfColors.findValueIndex(value);
    chromaIndex = MunsellBookOfColors.findChromaIndex(hueIndex, valueIndex,
        chroma);
  }

  public MunsellColor(int h, int v, int c) {
    hueIndex = h;
    value = MunsellBookOfColors.getValueLevel(v);
    chroma = (double) c;
    valueIndex = v;
    chromaIndex = c;
    // System.out.println("MunsellColor(int, int, int): " + toString());
  }

  /**
   * Get this color's Hue index. Hue index values range from 0 to 39.
   * @return int
   */
  public int getHueIndex() {
    return (hueIndex);
  }

  /**
   * Get this color's Hue notation.
   * @return String
   */
  public String getHue() {
    return (hue);
  }

  /**
   * Get this color's Value.
   * @return double
   * /
      public double getValue() {
    return (value);
     }
     /** Get this color's Value index. */
   public int getValueIndex() {
     return (valueIndex);
   }

  /**
   * Get this color's Chroma.
   * @return double
   */
  public double getChroma() {
    return (chroma);
  }

  /**
   * Get this color's Chroma index.
   * @return int
   */
  public int getChromaIndex() {
    return (chromaIndex);
  }

  public boolean decrementChroma() {
    boolean m = true;
    if (chromaIndex <= 1) {
      m = false;
    }
    else {
      int oldChromaIndex = chromaIndex;
      if (chromaIndex > 2) {
        chromaIndex -= 2;
      }
      else if (chromaIndex == 2) {
        chromaIndex--;
      }
      if (hasSpectra()) {
        chroma = chromaIndex;
      }
      else {
        chromaIndex = oldChromaIndex;
        m = false;
      }
    }
    return m;
  }

  /**
   * Return this color's standard Munsell notation.
   * @return String
   */
  public String toString() {
    notation = MunsellBookOfColors.getHueName(hueIndex) + " "
        + MunsellBookOfColors.getValueName(valueIndex) + "/" + chromaIndex;
    return (notation);
  }

  /**
   * Analyze the given string as a standard Munsell notation.
   * @param mn String
   */
  private void parseMunsellNotation(String mn) {
    StringTokenizer st = new StringTokenizer(mn, " /");
    try {
      hue = st.nextToken();
      char[] a = hue.toCharArray();
      int i;
      for (i = 0; (i < a.length) && !Character.isLetter(a[i]); i++) {
        ;
      }
      hueValue = Double.valueOf(hue.substring(0, i)).doubleValue();
      basicHue = hue.substring(i);
      value = Double.valueOf(st.nextToken()).doubleValue();
      chroma = Double.valueOf(st.nextToken()).doubleValue();
    }
    catch (NumberFormatException e) {
      throw new RuntimeException("Invalid Munsell notation: " + mn);
    }
  }

  /**
   * Return this Munsell color's reflectance spectrum.
   * @return SpectralDistribution
   */
//  public SpectralDistribution getSpectralDistribution() {
//    SpectralDistribution sd = null;
//    try {
//      sd = SpectralDistributionFactory.instance(this.toString());
//    }
//    catch (SpectrumNotFoundException snfe) {
//      new FileError(snfe.getMessage());
//      sd = new SpectralDistribution(380, 720, 5, 0.0F);
//    }
//    return sd;
//  }

  public Spectra getSpectra() {
    return null;
  }

  /**
   * Returns true if we know this Munsell color's reflectance spectrum.
   * @return boolean
   */
//  public boolean hasSpectralDistribution() {
//    return (SpectralDistributionFactory.contains(this.toString()));
//  }

  public boolean hasSpectra() {
    return false;
  }
}
