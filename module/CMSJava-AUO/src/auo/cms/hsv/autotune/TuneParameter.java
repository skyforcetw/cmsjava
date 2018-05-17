package auo.cms.hsv.autotune;

import java.io.*;
import java.util.*;

import shu.io.ascii.*;
import shu.math.lut.*;

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
public class TuneParameter {

  public void interpolate(double[] hueOfHSVArray) {
    boolean[] tagArray = new boolean[HUE_COUNT];

    for (double hue : hueOfHSVArray) {
      int hueIndex0 = (int) hue / 15;
      int hueIndex1 = hueIndex0 + 1;
      tagArray[hueIndex0] = true;
      //標記出所有在hueOfHSVArray裡的區間
      tagArray[hueIndex1] = true;
    }
    //==========================================================================
    //計算所有被標記的區間總量
    //==========================================================================
    int tagCount = 2;
    for (boolean tag : tagArray) {
      if (tag) {
        tagCount++;
      }
    }
    //==========================================================================

    double[] hueIndex = new double[tagCount];
    double[] huePrime = new double[tagCount];
    double[] saturationPrime = new double[tagCount];
    double[] valuePrime = new double[tagCount];
    int index = 1;

    for (int x = 0; x < HUE_COUNT; x++) {
      boolean tag = tagArray[x];
      if (tag) {
        //凡是被標記到的地方
        hueIndex[index] = x;
        huePrime[index] = hueAdjustValue[x];
        saturationPrime[index] = saturationAdjustValue[x];
        valuePrime[index] = valueAdjustValue[x];
        index++;
      }
    }

    //==========================================================================
    // 第一個元素的處理
    //==========================================================================
    hueIndex[0] = -1;
    huePrime[0] = huePrime[tagCount - 2];
    huePrime[0] = (huePrime[0] > huePrime[1]) ? huePrime[0] - 768 : huePrime[0];
    saturationPrime[0] = saturationPrime[tagCount - 2];
    valuePrime[0] = valuePrime[tagCount - 2];
    //==========================================================================
    // 最後一個元素的處理
    //==========================================================================
    hueIndex[tagCount - 1] = 24;
    huePrime[tagCount - 1] = huePrime[1];
    huePrime[tagCount - 1] = (huePrime[tagCount - 1] < huePrime[tagCount - 2]) ?
        huePrime[tagCount - 1] + 768 : huePrime[tagCount - 1];
    saturationPrime[tagCount - 1] = saturationPrime[1];
    valuePrime[tagCount - 1] = valuePrime[1];
    //==========================================================================

    //做成對照表
    Interpolation1DLUT saturationLut = new Interpolation1DLUT(hueIndex,
        saturationPrime, Interpolation1DLUT.Algo.LINEAR);
    Interpolation1DLUT valueLut = new Interpolation1DLUT(hueIndex, valuePrime,
        Interpolation1DLUT.Algo.LINEAR);
    byte[] saturationAdjustValue_ = new byte[HUE_COUNT];
    byte[] valueAdjustValue_ = new byte[HUE_COUNT];
    for (int x = 0; x < HUE_COUNT; x++) {
      saturationAdjustValue_[x] = (byte) Math.round(saturationLut.getValue(x));
      valueAdjustValue_[x] = (byte) Math.round(valueLut.getValue(x));
    }
    this.saturationAdjustValue = saturationAdjustValue_;
    this.valueAdjustValue = valueAdjustValue_;
  }

  public final static int HUE_COUNT = 24;

  public final static TuneParameter getInstanceFromFile(String filename) throws
      IOException {
    ASCIIFileFormatParser parser = new ASCIIFileFormatParser(filename);
    ASCIIFileFormat asciiFile = null;
    asciiFile = parser.parse();

    if (asciiFile.size() < HUE_COUNT) {
      throw new IllegalStateException("asciiFile.size() < " + HUE_COUNT);
    }
    short[] hueAdjustValue = new short[HUE_COUNT];
    byte[] saturationAdjustValue = new byte[HUE_COUNT];
    byte[] valueAdjustValue = new byte[HUE_COUNT];

    for (int x = 0; x < HUE_COUNT; x++) {
      ASCIIFileFormat.LineObject lo = asciiFile.getLine(x);
      String[] strings = lo.stringArray;
      hueAdjustValue[x] = Short.parseShort(strings[0]);
      byte s = Byte.parseByte(strings[1]);
      s = (s > 63) ? 0 : s;
      s = (s < -64) ? 0 : s;
      saturationAdjustValue[x] = s;
      byte v = Byte.parseByte(strings[2]);
      v = (v > 63) ? 0 : v;
      v = (v < -64) ? 0 : v;
      valueAdjustValue[x] = v;
    }
    TuneParameter tuneParameter = new TuneParameter(hueAdjustValue,
        saturationAdjustValue, valueAdjustValue);
    return tuneParameter;
  }

  private short[] hueAdjustValue = null;
  private byte[] saturationAdjustValue = null;
  private byte[] valueAdjustValue = null;
  public short[] getHueAdjustValue() {
    return hueAdjustValue;
  }

  public byte[] getSaturationAdjustValue() {
    return saturationAdjustValue;
  }

  public byte[] getValueAdjustValue() {
    return valueAdjustValue;
  }

//  private SingleHueAdjustValue[] hsvAdjustValue = null;

  public TuneParameter(SingleHueAdjustValue[] hsvAdjustValue) {
    init(hsvAdjustValue);
  }

  public final static TuneParameter getByPassInstance() {
    short[] hueAdjustValue = new short[HUE_COUNT];
    for (int x = 0; x < HUE_COUNT; x++) {
      int deg = 360 / HUE_COUNT * x;
      hueAdjustValue[x] = (short) (deg / 360. * 768);
    }
    byte[] saturationAdjustValue = new byte[HUE_COUNT];
    byte[] valueAdjustValue = new byte[HUE_COUNT];

    TuneParameter tuneParameter = new TuneParameter(hueAdjustValue,
        saturationAdjustValue, valueAdjustValue);
    return tuneParameter;
  }

  private void init(SingleHueAdjustValue[] hsvAdjustValue) {
    int size = hsvAdjustValue.length;
    hueAdjustValue = new short[size];
    saturationAdjustValue = new byte[size];
    valueAdjustValue = new byte[size];
    for (int x = 0; x < size; x++) {
      hueAdjustValue[x] = hsvAdjustValue[x].hueAdjustValue;
      saturationAdjustValue[x] = hsvAdjustValue[x].saturationAdjustValue;
      valueAdjustValue[x] = hsvAdjustValue[x].valueAdjustValue;
    }
  }

  public TuneParameter(double[][] adjustValuaes) {
    if (adjustValuaes.length != HUE_COUNT) {
      throw new IllegalArgumentException("adjustValuaes.length != " + HUE_COUNT);
    }
    hueAdjustValue = new short[HUE_COUNT];
    saturationAdjustValue = new byte[HUE_COUNT];
    valueAdjustValue = new byte[HUE_COUNT];
    for (int x = 0; x < HUE_COUNT; x++) {
      hueAdjustValue[x] = (short) adjustValuaes[x][0];
      saturationAdjustValue[x] = (byte) adjustValuaes[x][1];
      valueAdjustValue[x] = (byte) adjustValuaes[x][2];
    }
  }

  public TuneParameter(short[] hueAdjustValue,
                       byte[] saturationAdjustValue,
                       byte[] valueAdjustValue) {
    if (hueAdjustValue.length != saturationAdjustValue.length &&
        saturationAdjustValue.length != valueAdjustValue.length) {
      throw new IllegalArgumentException("size is not equal");
    }
    this.hueAdjustValue = hueAdjustValue;
    this.saturationAdjustValue = saturationAdjustValue;
    this.valueAdjustValue = valueAdjustValue;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return "Hue: " + Arrays.toString(hueAdjustValue) + "\n" +
        "Saturation: " + Arrays.toString(saturationAdjustValue) + "\n" +
        "Value: " + Arrays.toString(valueAdjustValue);
  }

  public String toHSVv1ToolkitFormatString() {
    StringBuilder buf = new StringBuilder();
    int size = hueAdjustValue.length;
    for (int x = 0; x < size; x++) {
      byte s = saturationAdjustValue[x];
      double saturationGain = 1 + s / 63.;
      byte saturationAdjustValueV1 = (byte) Math.floor(saturationGain * 32);

      buf.append(hueAdjustValue[x] + "\t" + saturationAdjustValueV1 + "\t" +
                 valueAdjustValue[x] + "\n");
    }

    return buf.toString();
  }

  public String toToolkitFormatString() {
    StringBuilder buf = new StringBuilder();
    int size = hueAdjustValue.length;
    for (int x = 0; x < size; x++) {
      buf.append(hueAdjustValue[x] + "\t" + saturationAdjustValue[x] + "\t" +
                 valueAdjustValue[x] + "\n");
    }

    return buf.toString();

  }

  public final static TuneParameter loadFromFile(String filename) throws
      IOException {
    File file = new File(filename);
    BufferedReader reader = new BufferedReader(new FileReader(file));
    short[] hue = new short[24];
    byte[] saturation = new byte[24];
    byte[] value = new byte[24];
    int index = 0;
    while (reader.ready()) {
      String line = reader.readLine();
      StringTokenizer tokenizer = new StringTokenizer(line, "\t");
      hue[index] = Short.parseShort(tokenizer.nextToken());
      saturation[index] = Byte.parseByte(tokenizer.nextToken());
      value[index] = Byte.parseByte(tokenizer.nextToken());
      index++;
    }
    TuneParameter tuneParameter = new TuneParameter(hue, saturation, value);
    return tuneParameter;
  }

  public final void writeToFile(String filename) throws IOException {
    File file = new File(filename);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(toToolkitFormatString());
    writer.close();
  }

  public static void main(String[] args) throws Exception {
    short[] h = new short[HUE_COUNT];
    byte[] s = new byte[HUE_COUNT];
    byte[] v = new byte[HUE_COUNT];
    for (int x = 0; x < HUE_COUNT; x++) {
      h[x] = (short) (32 * x);
      s[x] = (byte) 10;
      v[x] = (byte) 0;
    }
    TuneParameter parameter = new TuneParameter(h, s, v);
    System.out.println(parameter);
    double[] hueOfHSVArray = new double[] {
        35.80811701065982, 51.781048228742854, 122.52034203100983,
        202.71132699965887};
    parameter.interpolate(hueOfHSVArray);
    System.out.println(parameter);
//    parameter.interpolate();
    parameter.writeToFile("hsv.lut");
  }

}
