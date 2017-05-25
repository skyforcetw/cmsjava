package auo.mura;

import auo.mura.CorrectionData;
import jxl.read.biff.BiffException;
import java.io.IOException;

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
public class UnsignedByte {
  public static String toHexString(byte b) {

    if (b >= 0) {
      return Integer.toHexString(b);
    }
    else {
      //-128:128 -127:129 -126:130
      int value = 127 + 129 + b;
      return Integer.toHexString(value);
    }

  }

  public static byte valueOf(String s, int radix) throws
      NumberFormatException {
    short svalue = Short.valueOf(s, radix);
    return (byte) svalue;

  }

  public static void main(String[] args) throws BiffException, IOException {
    for (int x = -128; x <= 127; x++) {
      byte b = (byte) x;
      String hex = UnsignedByte.toHexString(b);
      byte b1 = valueOf(hex, 16);
      if (b != b1) {
        System.out.println(x + " " + hex + " " + valueOf(hex, 16));

      }

    }

    String correctFilename = "24inch No2/1line_2.csv";

    CorrectionData correctiondata = new CorrectionData(correctFilename,
        CorrectionData.Type.AUOHex);

  }
}
