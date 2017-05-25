package sky4s.test.icc;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.color.*;

import shu.cms.plot.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 取出ICC Profile中的vcgt tag,然後解譯顯示出來
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class vcgtTagViewer {
  public static void main(String[] args) {

    try {
      ICC_Profile profile = ICC_Profile.getInstance(
          "Profile/Monitor from ProfileMaker/M1210_2.icc");
      byte[] vcgt = profile.getData(0x76636774);

      byte[] tagRawdata = Arrays.copyOfRange(vcgt, 0, 8);
      byte[] gammaTypeRawdata = Arrays.copyOfRange(vcgt, 8, 12);
      byte[] chRawdata = Arrays.copyOfRange(vcgt, 12, 14);
      byte[] entryCountRawdata = Arrays.copyOfRange(vcgt, 14, 16);
      byte[] entrySizeRawdata = Arrays.copyOfRange(vcgt, 16, 18);

//      System.out.println("tagRawdata: " + Arrays.toString(tagRawdata));
//      System.out.println("gammaTypeRawdata: " +
//                         Arrays.toString(gammaTypeRawdata));
//      System.out.println("chRawdata: " + Arrays.toString(chRawdata));
//      System.out.println("entryCountRawdata: " +
//                         Arrays.toString(entryCountRawdata));
//      System.out.println("entrySizeRawdata: " +
//                         Arrays.toString(entrySizeRawdata));

      int ch = byteToInteger(chRawdata);
      int entryCount = byteToInteger(entryCountRawdata);
      int entrySize = byteToInteger(entrySizeRawdata);

//      int size = ch * entryCount * entrySize;
//      System.out.println("ch: " + ch);
//      System.out.println("entryCount: " + entryCount);
//      System.out.println("entrySize: " + entrySize);
//      System.out.println("size: " + size);

      int[][] table = new int[ch][entryCount];
      int rawDataIndex = 18;
      for (int chIndex = 0; chIndex < ch; chIndex++) {
        for (int code = 0; code < entryCount; code++) {
          byte[] value = Arrays.copyOfRange(vcgt, rawDataIndex,
                                            rawDataIndex + entrySize);
          rawDataIndex += entrySize;
          int data = byteToInteger(value);
          table[chIndex][code] = data;
        }
      }

      double[][] doubleTable = IntArray.toDoubleArray(table);

      System.out.println(Arrays.toString(table[0]));
      System.out.println(Arrays.toString(table[1]));
      System.out.println(Arrays.toString(table[2]));

      Plot2D p = Plot2D.getInstance();
      p.setVisible(true);
      p.addLinePlot("R", Color.red, 0, 255, doubleTable[0]);
      p.addLinePlot("G", Color.green, 0, 255, doubleTable[1]);
      p.addLinePlot("B", Color.blue, 0, 255, doubleTable[2]);
      p.setFixedBounds(0, 0, 255);
      p.setFixedBounds(1, 0, 65535);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  static int byteToInteger(byte[] raw) {
    int[] fix = new int[2];
    fix[0] = raw[0] < 0 ? 256 + raw[0] : raw[0];
    fix[1] = raw[1] < 0 ? 256 + raw[1] : raw[1];
    return (fix[0] << 8) + fix[1];
  }
}
