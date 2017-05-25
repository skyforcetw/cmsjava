package auo.cms.hsvinteger.test;

import auo.cms.hsv.autotune.TuneParameter;
import java.io.*;
import auo.cms.hsvinteger.IntegerHSVIP;
import auo.cms.hsv.saturation.IntegerSaturationFormula;
import shu.cms.colorspace.depend.*;
import auo.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class IPTester {
  public IPTester() {
    super();
  }

  public static void main(String[] args) {
    TuneParameter tp = null;
    try {

      tp = TuneParameter.loadFromFile("LUT.txt");
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    IntegerSaturationFormula isf = new IntegerSaturationFormula( (byte) 7, 4);
    IntegerHSVIP ip = new IntegerHSVIP(isf, tp);
    RGB rgb = new RGB(34, 83, 129);
    AUOHSV hsv = new AUOHSV(rgb);
    AUOHSV hsv2 = ip.getHSV(hsv);
    RGB rgb2 = hsv2.toRGB();
    System.out.println(rgb2);
    rgb2.changeMaxValue(RGB.MaxValue.Int8Bit);
    System.out.println(rgb2);
  }
}
