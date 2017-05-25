package vv.cms.test.hirose;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.independ.*;
import shu.math.*;

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
public class LEDSpectraProducer {

  public static void main(String[] args) throws IOException {
    SpectraSuiteFile file = new SpectraSuiteFile("LED.ProcSpec");
    file.parse();
    Spectra s = file.getSpectra(380, 730, 10, Interpolation.Algo.Linear);
    System.out.println("LED:");
    System.out.println(s);

    LogoFileAdapter cc24 = new LogoFileAdapter("ColorChecker 24.txt");
    List<Spectra> list = cc24.getSpectraList();
    System.out.println("CC24:");
    for (Spectra c : list) {
      Spectra ill = c.timesAndReturn(s);
      System.out.println(c.getXYZ().Y + " " + ill);
    }

    Spectra x = ColorMatchingFunction.CIE_1931_2DEG_XYZ.getSpectra(0);
    Spectra y = ColorMatchingFunction.CIE_1931_2DEG_XYZ.getSpectra(1);
    Spectra z = ColorMatchingFunction.CIE_1931_2DEG_XYZ.getSpectra(2);
    x = x.reduce(380, 730, 10);
    y = y.reduce(380, 730, 10);
    z = z.reduce(380, 730, 10);
    System.out.println("XYZ CMF:");
    System.out.println(x);
    System.out.println(y);
    System.out.println(z);

    System.out.println("CC24 XYZ");
    for (Spectra c : list) {
      Spectra ill = c.timesAndReturn(s);
//  System.out.println(ill);
      Spectra xill = ill.timesAndReturn(x);
      Spectra yill = ill.timesAndReturn(y);
      Spectra zill = ill.timesAndReturn(z);
      CIEXYZ XYZ = ill.getXYZ();
      System.out.println(XYZ.X + " " + xill);
      System.out.println(XYZ.Y + " " + yill);
      System.out.println(XYZ.Z + " " + zill);
    }

  }
}
