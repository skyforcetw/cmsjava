package shu.cms.measure.meterapi.argyll;

import java.io.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用在沒有校色器的機器上, 可以模擬Argyll的spotread的動作(必要功能的模擬而已)
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SpotreadSimulator {
  private final static String msg =
      "Place instrument on spot to be measured,\n" +
      "and hit [A-Z] to read white and setup FWA compensation (keyed to letter)\n" +
      "[a-z] to read and make FWA compensated reading from keyed reference\n" +
      "'r' to take previous reading as the reference\n" +
      "Hit ESC, ^C or Q to exit, any other key to take a reading:";

  private final static String measure =
      "\n\nResult is XYZ: 11.970904 15.523371 2.838526, D50 Lab: 46.342852 -19.284640 42.435694\n\n";
  private final static String measure1 = "\n\nResult is XYZ: ";
  private final static String measure2 = ", D50 Lab: ";
  private final static String measure3 = "\n\n";

  private final static String quit =
      "\n\nSpot read stopped at user request!\n" +
      "Hit Esc, ^C or Q to give up, any other key to retry:\n";

  public static void main(String[] args) {
//    System.out.println(args.length +" "+args[0]);
    if (args.length < 1 || !args[0].equals("-yl")) {
      System.out.println("Either CRT or LCD must be selected");
      return;
    }

    CIEXYZ white = (CIEXYZ) Illuminant.D50WhitePoint.clone();
    white.normalizeY100();

    boolean showmsg = true;
    try {
      while (true) {
        if (showmsg) {
          System.out.print(msg);
        }
        showmsg = true;

        int ch = System.in.read();
        if (ch == 'q') {
          ch = System.in.read();
          if (ch == 'q') {
            System.out.print(quit);
            return;
          }
        }
        if (ch == Character.LETTER_NUMBER) {
          double X = Math.random() * 100;
          double Y = Math.random() * 100;
          double Z = Math.random() * 100;
          CIEXYZ XYZ = new CIEXYZ(X, Y, Z, NormalizeY.Normal100);
          CIELab Lab = new CIELab(XYZ, white);
          System.out.print(measure1 + XYZ.X + " " + XYZ.Y + " " + XYZ.Z +
                           measure2 + Lab.L + " " + Lab.a + " " + Lab.b +
                           measure3);
        }
        else {
          showmsg = false;
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
