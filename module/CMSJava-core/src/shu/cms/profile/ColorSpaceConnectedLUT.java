package shu.cms.profile;

import java.util.*;

import shu.math.*;
import shu.math.lut.*;
import shu.math.array.*;
import shu.cms.lcd.LCDTarget;
import shu.cms.Patch;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.depend.DeviceDependentSpace;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 儲存對照表以及相關資訊
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ColorSpaceConnectedLUT {
  public static enum Style {
    AToB, BToA, Unknow
  }

  public static enum PCSType {
    XYZ, Lab, Unknow
  }

  public final boolean isA2B() {
    return type == Style.AToB;
  }

  public final boolean isLabPCS() {
    return pcsType == PCSType.Lab;
  }

  protected Style type;
  protected PCSType pcsType;
  protected int inputChannels;
  protected int outputChannels;

  protected int numberOfGridPoints;
  protected double[][][] CLUTData;

  protected double[] inputMaxValue;
  protected double[] inputMinValue;

  public double[] getInputMaxValue() {
    return inputMaxValue;
  }

  public final RGB.MaxValue getMaxValue() {
    switch ( (int) inputMaxValue[0]) {
      case 1:
        return RGB.MaxValue.Double1;
      case 255:
        return RGB.MaxValue.Double255;
      default:
        return null;
    }
  }

  protected double[][] matrix;
  protected double[][] inputTables;
  protected double[][] outputTables;

  /**
   * inputTables與outputTables的長度須一致,而且兩者須同時存在(或者都不存在)
   * @param inputChannels int
   * @param outputChannels int
   * @param numberOfGridPoints int
   * @param input double[][]
   * @param inputMinValue double[]
   * @param inputMaxValue double[]
   * @param output double[][]
   * @param inputTables double[][]
   * @param outputTables double[][]
   * @param type Type
   * @param pcsType PCSType
   */
  public ColorSpaceConnectedLUT(int inputChannels, int outputChannels,
                                int numberOfGridPoints, double[][] input,
                                double[] inputMinValue, double[] inputMaxValue,
                                double[][] output, double[][] inputTables,
                                double[][] outputTables, Style type,
                                PCSType pcsType) {
    if (input[0].length != inputChannels || output[0].length != outputChannels) {
      throw new IllegalStateException("input or output channels is not equal.");
    }
    this.inputChannels = inputChannels;
    this.outputChannels = outputChannels;
    this.numberOfGridPoints = numberOfGridPoints;
    this.CLUTData = produceCLUTData(input, output);
    this.inputMinValue = inputMinValue;
    this.inputMaxValue = inputMaxValue;
    this.inputTables = inputTables;
    this.outputTables = outputTables;
    this.type = type;
    this.pcsType = pcsType;
  }

  public final static ColorSpaceConnectedLUT getXYZCLUTInstance(int
      inputChannels, int outputChannels,
      int numberOfGridPoints, double[][] output,
      double[][] inputTables,
      double[][] outputTables) {
    ColorSpaceConnectedLUT clut = new ColorSpaceConnectedLUT(inputChannels,
        outputChannels, numberOfGridPoints,
        produceInputXYZCLUT(numberOfGridPoints),
        new double[] {0, 0, 0}, new double[] {1, 1, 1}, output, inputTables,
        outputTables, Style.BToA, PCSType.XYZ);
    return clut;
  }

  public int size() {
    return CLUTData.length;
  }

  public double[] getOutput(int index) {
    return CLUTData[index][1];
  }

  protected double[] ICCFormatOutput;
//  protected double[] LabICCFormatOutput;
  protected double[] ICCFormatOutputTables;
  protected double[] ICCFormatInputTables;

  protected final static double LEGACY_CORRECT = 65535. / 65280.;
  protected final static boolean DO_LEGACY_CORRECT = true;

  /**
   * 輸出符合ICC Profile下的CLUT格式
   * @return double[]
   */
  public double[] getICCFormatOutput() {
    if (ICCFormatOutput == null) {
      int size = size();
      ICCFormatOutput = new double[size * outputChannels];

      if (isLabPCS() && isA2B()) {
        for (int x = 0; x < size; x++) {
          double[] output = getOutput(x);
          output[0] /= 100.;
          output[1] = ( (output[1] + 128.) / 255);
          output[2] = ( (output[2] + 128.) / 255);

          if (DO_LEGACY_CORRECT) {
            output[1] /= LEGACY_CORRECT;
            output[2] /= LEGACY_CORRECT;
          }

          System.arraycopy(output, 0, ICCFormatOutput, x * outputChannels,
                           outputChannels);
        }
      }
      else {
        for (int x = 0; x < size; x++) {
          System.arraycopy(getOutput(x), 0, ICCFormatOutput, x * outputChannels,
                           outputChannels);
        }
      }

    }

    return ICCFormatOutput;
  }

  /*public double[] getLabICCFormatOutput() {
    if (LabICCFormatOutput == null) {
      int size = size();
      LabICCFormatOutput = new double[size * outputChannels];

      for (int x = 0; x < size; x++) {
        double[] output = getOutput(x);
        output[0] /= 100.;
        output[1] = (output[1] + 128.) / 255;
        output[2] = (output[2] + 128.) / 255;
        System.arraycopy(output, 0, LabICCFormatOutput, x * outputChannels,
                         outputChannels);
      }
    }

    return LabICCFormatOutput;
     }*/

  /**
   * 將input以及output合併到一個三維的double矩陣
   * @param input double[][]
   * @param output double[][]
   * @return double[][][]
   */
  protected static double[][][] produceCLUTData(double[][] input,
                                                double[][] output) {
    int size = input.length;
    if (size != output.length) {
      throw new IllegalStateException("size is not equal.");
    }
    double[][][] result = new double[size][2][];

    for (int x = 0; x < size; x++) {

      result[x][0] = DoubleArray.copy(input[x]);
      result[x][1] = DoubleArray.copy(output[x]);
    }
    return result;
  }

  public String toString() {
    int size = CLUTData.length;
    StringBuilder builder = new StringBuilder();
    builder.append("in:" + inputChannels + " min:" +
                   Arrays.toString(inputMinValue) + " max:" +
                   Arrays.toString(inputMaxValue) + " out:" + outputChannels +
                   " grid:" + numberOfGridPoints + "\n");
    for (int x = 0; x < size; x++) {
      builder.append(Arrays.toString(CLUTData[x][0]));
      builder.append(" : ");
      builder.append(Arrays.toString(CLUTData[x][1]));
      builder.append('\n');
    }
    return builder.toString();
  }

  public int getInputChannels() {
    return inputChannels;
  }

  public int getOutputChannels() {
    return outputChannels;
  }

  public int getNumberOfGridPoints() {
    return numberOfGridPoints;
  }

  public double[][] getOutputTables() {
    return outputTables;
  }

  /**
   *
   * @return double[]
   * @todo H icc getICCFormatOutputTables
   */
  public double[] getICCFormatOutputTables() {
    if (ICCFormatOutputTables == null) {

    }
    return ICCFormatOutputTables;
  }

  public double[][] getInputTables() {
    return inputTables;
  }

  public double[] getICCFormatInputTables() {
    if (ICCFormatInputTables == null) {
      if (isLabPCS() && !isA2B()) {
        int size = inputTables[0].length;
        ICCFormatInputTables = new double[size * inputChannels];

        for (int x = 0; x < size; x++) {
          ICCFormatInputTables[x] = inputTables[0][x] / 100;
          ICCFormatInputTables[x + size]
              = (inputTables[1][x] + 128.) / 255;
          ICCFormatInputTables[x + size * 2]
              = (inputTables[2][x] + 128.) / 255;

          if (DO_LEGACY_CORRECT) {
            ICCFormatInputTables[x + size] /= LEGACY_CORRECT;
            ICCFormatInputTables[x + size * 2] /= LEGACY_CORRECT;
          }

        }

      }
      else {
        ICCFormatInputTables = DoubleArray.to1DDoubleArray(getInputTables());
      }

      /*for (int x = 0; x < size; x++) {
        double[] output = getOutput(x);
        output[0] /= 100.;
        output[1] = (output[1] + 128.) / 255;
        output[2] = (output[2] + 128.) / 255;
        System.arraycopy(output, 0, LabICCFormatOutput, x * outputChannels,
                         outputChannels);
             }*/
    }
    return ICCFormatInputTables;
  }

  public double[][] getMatrix() {
    return matrix;
  }

  /**
   * 轉換成CubeTable
   * @return CubeTable
   */
  public CubeTable produceCubeTable() {
    return new CubeTable(CLUTData, this.inputMinValue, this.inputMaxValue,
                         numberOfGridPoints);
  }

  public TetrahedralInterpolation produceTetrahedralInterpolation() {
    return new TetrahedralInterpolation(produceCubeTable());
  }

  protected static int getCLUTSize(int grid, int channel) {
    int size = (int) Math.pow(grid, channel);
    return size;
  }

  public final static double[][] produceInputXYZCLUT(int grid) {
    return produceInputCLUT(grid, new double[] {0, 0, 0}, new double[] {1, 1, 1});
    /*double step = 100. / (grid - 1);
         int size = getCLUTSize(grid, 3);
         double[][] XYZGrid = new double[size][3];
         int index = 0;
//產生D50的XYZ
         for (double X = 0; X <= 100.; X += step) {
      for (double Y = 0; Y <= 100.; Y += step) {
        for (double Z = 0; Z <= 100.; Z += step) {
          XYZGrid[index++] = new double[] {
              X / 100., Y / 100., Z / 100.};
        }
      }
         }
         return XYZGrid;*/
  }

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.getFromAUOXLS(
        "hsv/B156HW03/729(CM On).xls");
    List<Patch> patchList = target.getPatchList();
//    for (Patch p : target.getPatchList()) {
//      System.out.println(p);
//    }
    int size = patchList.size();
    double[][] input = new double[size][];
    double[][] output = new double[size][];
//    for (int x = 0; x < size; x++) {
//      Patch p = patchList.get(x);
//      input[x] = p.getRGB().getValues();
//      output[x] = p.getXYZ().getValues();
//    }
    int index = 0;
    for (int r = 0; r <= 256; r += 32) {
      for (int g = 0; g <= 256; g += 32) {
        for (int b = 0; b <= 256; b += 32) {
          r = (r == 256) ? 255 : r;
          g = (g == 256) ? 255 : g;
          b = (b == 256) ? 255 : b;
          Patch p = target.getPatch(r, g, b);
          input[index] = p.getRGB().getValues();
          output[index] = p.getXYZ().getValues();
          index++;
        }
      }

    }
    ColorSpaceConnectedLUT lut = new ColorSpaceConnectedLUT(3, 3, 9, input,
        new double[] {0, 0, 0}, new double[] {255, 255, 255}, output, null, null,
        Style.AToB, PCSType.XYZ);
    TetrahedralInterpolation tetrahedral = lut.produceTetrahedralInterpolation();

    TetrahedralInterpolation.CoordinateIF coordinateIF =
        new TetrahedralInterpolation.CoordinateIF() {

      public double[] getCoordinate(double[] key) {
        double[] gridKey = getGridKey(key);
        double[] stair = stairs;
        double x0 = gridKey[0];
        double y0 = gridKey[1];
        double z0 = gridKey[2];
        double x1 = x0 + stair[0];
        double y1 = y0 + stair[1];
        double z1 = z0 + stair[2];

        double[] keyMax = maxValues;
        if (x0 >= keyMax[0]) {
          x1 = x0;
          x0 = x1 - stair[0];
        }
        if (y0 >= keyMax[1]) {
          y1 = y0;
          y0 = y1 - stair[1];
        }
        if (z0 >= keyMax[2]) {
          z1 = z0;
          z0 = z1 - stair[2];
        }

        return new double[] {
            x0, y0, z0, x1, y1, z1};
      }

      private double[] minValues = new double[] {
          0, 0, 0};
      private double[] maxValues = new double[] {
          255, 255, 255};
      private double[] stairs = new double[] {
          32, 32, 32};
      protected final double[] getGridKey(double[] key) {
        double[] value = DoubleArray.minus(key, minValues);

        double[] remainder = DoubleArray.modulus(value, stairs);
        int size = value.length;
        for (int x = 0; x < size; x++) {
          remainder[x] = (key[x] == 255) ? 0 : remainder[x];
        }

        double[] grid = DoubleArray.minus(key, remainder);
        return grid;
      }

    };

    tetrahedral.registerCoordinateIF(coordinateIF);

    double[] XYZValues = tetrahedral.getValues(new double[] {
                                               0, 0, 255});
    CIEXYZ XYZ = new CIEXYZ(XYZValues);
    CIExyY xyY = new CIExyY(XYZ);
    System.out.println(XYZ + " " + xyY);
//    System.out.println(DoubleArray.toString(tetrahedral.getValues(new double[] {
//        0, 0, 0})));
  }

  public final static double[][] produceInputCLUT(int grid, double[] minValue,
                                                  double[] maxValue) {
    if (minValue.length != 3 || maxValue.length != 3) {
      throw new IllegalArgumentException(
          "minValue.length != 3 || maxValue.length != 3");
    }

    double[] range = new double[3];
    for (int x = 0; x < 3; x++) {
      range[x] = maxValue[x] - minValue[x];
    }
    double[] step = new double[3];
    for (int x = 0; x < 3; x++) {
      step[x] = range[x] / (grid - 1.);
    }

    int size = getCLUTSize(grid, 3);
    double[][] clut = new double[size][];
    int index = 0;

    for (double ch0 = minValue[0]; ch0 <= maxValue[0]; ch0 += step[0]) {
      for (double ch1 = minValue[1]; ch1 <= maxValue[1]; ch1 += step[1]) {
        for (double ch2 = minValue[2]; ch2 <= maxValue[2]; ch2 += step[2]) {
          clut[index++] = new double[] {
              ch0, ch1, ch2};
        }
      }
    }
//    System.out.println(DoubleArray.toString(clut[index-1]));
    return clut;
  }

  public static double[][] produceInputLabCLUT(int grid) {
    return produceInputCLUT(grid, new double[] {0, -128, -128},
                            new double[] {100, 127, 127});
  }
}
