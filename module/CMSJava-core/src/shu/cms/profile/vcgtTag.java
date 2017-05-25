package shu.cms.profile;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.color.*;

import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.DoubleArray;
import shu.math.array.IntArray;

//import shu.plot.*;

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
public class vcgtTag {
  public vcgtTag(byte[] rawdata) {
    parse(rawdata);
  }

  public vcgtTag(String iccFilename) throws IOException {
    this(ICC_Profile.getInstance(iccFilename));
  }

  public vcgtTag(ICC_Profile iccProfile) {
    this(iccProfile.getData(0x76636774));
  }

  public static void main(String[] args) {
//    String filename = "Profile/Monitor from ProfileMaker/M1210_2.icc";
//    try {
//      vcgtTag vcgt = new vcgtTag(filename);
//      vcgt.print();
//      vcgt.plot();
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }

    try {
      int[][] lut = loadLUTFromLUTFile(
          "Profile/Monitor from ProfileMaker/a.lut");
//      FileInputStream fis = new FileInputStream(
//          "Profile/Monitor from ProfileMaker/a.lut");
//      byte[] data = new byte[1535];
//      fis.read(data);
//      getTable(data, 0, 3, 256, 2, true);
    }
    catch (FileNotFoundException ex1) {
    }
    catch (IOException ex2) {
    }
  }

  protected void parse(byte[] rawdata) {
    byte[] tagRawdata = Arrays.copyOfRange(rawdata, 0, 8);
//    String s = new String(tagRawdata);
//    System.out.println(s);
    byte[] gammaTypeRawdata = Arrays.copyOfRange(rawdata, 8, 12);
//    System.out.println("tagRawdata: " + Arrays.toString(tagRawdata));
//    System.out.println("gammaTypeRawdata: " +
//                       Arrays.toString(gammaTypeRawdata));

    byte[] chRawdata = Arrays.copyOfRange(rawdata, 12, 14);
    byte[] entryCountRawdata = Arrays.copyOfRange(rawdata, 14, 16);
    byte[] entrySizeRawdata = Arrays.copyOfRange(rawdata, 16, 18);
    channels = byteToInteger(chRawdata);
    entryCount = byteToInteger(entryCountRawdata);
    entrySize = byteToInteger(entrySizeRawdata);

    table = getTable(rawdata, 18, channels, entryCount, entrySize, false);
//    table = new int[channels][entryCount];
//    int rawDataIndex = 18;
//    for (int chIndex = 0; chIndex < channels; chIndex++) {
//      for (int code = 0; code < entryCount; code++) {
//        byte[] value = Arrays.copyOfRange(rawdata, rawDataIndex,
//                                          rawDataIndex + entrySize);
//        rawDataIndex += entrySize;
//        int data = byteToInteger(value);
//        table[chIndex][code] = data;
//      }
//    }
  }

  public final static int[][] loadLUTFromLUTFile(String filename) throws
      FileNotFoundException, IOException {
    FileInputStream fis = new FileInputStream(filename);
    byte[] data = new byte[1535];
    fis.read(data);
    return getTable(data, 0, 3, 256, 2, true);
  }

  public final static void storeLUTToLUTFile(int[][] lut, String filename) {

  }

  public final static int[][] getTable(byte[] rawdata, int start, int channels,
                                       int entryCount, int entrySize,
                                       boolean littleEndian) {
    int[][] table = new int[channels][entryCount];
    int rawDataIndex = start;
    for (int chIndex = 0; chIndex < channels; chIndex++) {
      for (int code = 0; code < entryCount; code++) {
        byte[] value = Arrays.copyOfRange(rawdata, rawDataIndex,
                                          rawDataIndex + entrySize);
        rawDataIndex += entrySize;
        int data = byteToInteger(value, littleEndian);
        table[chIndex][code] = data;
      }
    }
    return table;
  }

  public Plot2D plot() {
    Plot2D p = Plot2D.getInstance();
    p.setVisible(true);
    double[][] doubleTable = IntArray.toDoubleArray(table);
    p.addLinePlot("R", Color.red, 0, 255, doubleTable[0]);
    p.addLinePlot("G", Color.green, 0, 255, doubleTable[1]);
    p.addLinePlot("B", Color.blue, 0, 255, doubleTable[2]);
    p.setFixedBounds(0, 0, 255);
    p.setFixedBounds(1, 0, 65535);
    return p;
  }

  public void print() {
    System.out.println(Arrays.toString(table[0]));
    System.out.println(Arrays.toString(table[1]));
    System.out.println(Arrays.toString(table[2]));
  }

  private int[][] table;
  private int channels;
  private int entryCount;
  private int entrySize;
  protected static int byteToInteger(byte[] raw) {
    return byteToInteger(raw, false);
  }

  protected static int byteToInteger(byte[] raw, boolean reverse) {

    int[] fix = new int[2];
    fix[0] = raw[0] < 0 ? 256 + raw[0] : raw[0];
    fix[1] = raw[1] < 0 ? 256 + raw[1] : raw[1];
    if (reverse) {
      return (fix[1] << 8) + fix[0];
    }
    else {
      return (fix[0] << 8) + fix[1];
    }

  }

  public int getChannels() {
    return channels;
  }

  public int getEntryCount() {
    return entryCount;
  }

  public int getEntrySize() {
    return entrySize;
  }
}
