package shu.cms.lcd;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.math.lut.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來模擬CP table或者顯卡的LUT.
 * 由於DisplayLUT內部是用Interpolation1DLUT實作, 而Interpolation1DLUT是以double[]產生.
 * 所以如果想要使外部數據與DisplayLUT內部的Interpolation1DLUT同步,
 * 就要用DisplayLUT(double[][] rgbOutput)這隻建構式
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DisplayLUT {
  private Interpolation1DLUT[] lutArray;
  protected double[][] rgbOutput;
//  private double[] tmpValues = new double[3];
  protected RGB[] outputRGBArray;

  /**
   * 從RGBArray更新到rgbOutput
   */
  public void updateFromRGBArray() {
    if (outputRGBArray != null) {
      double[] values = new double[3];
      for (int x = 0; x < 256; x++) {
        RGB rgb = outputRGBArray[x];
        rgb.getValues(values, RGB.MaxValue.Double255);
        rgbOutput[0][x] = values[0];
        rgbOutput[1][x] = values[1];
        rgbOutput[2][x] = values[2];
      }
    }
    else {
      throw new IllegalStateException("outputRGBArray == null");
    }
  }

  /**
   *
   * @return double[][]
   */
  public final double[][] getRGBOutputCopy() {
    return DoubleArray.copy(rgbOutput);
  }

  /**
   * 具同步效果的建構式
   * @param rgbOutput double[][] 以double[3][256]的陣列初始化對照表
   */
  public DisplayLUT(double[][] rgbOutput) {
    this(RGBArray.getOriginalRGBDoubleArray(), rgbOutput);
  }

  /**
   * 具同步效果的建構式
   * @param rgbInput double[][]
   * @param rgbOutput double[][]
   */
  public DisplayLUT(double[][] rgbInput, double[][] rgbOutput) {
    if (rgbInput.length != 3 || rgbOutput.length != 3 ||
        rgbInput[0].length != 256 || rgbOutput[0].length != 256) {
      throw new IllegalArgumentException("rgbInput or rgbOutput is invalid.");
    }

    lutArray = Interpolation1DLUT.getRGBInterpLUT(rgbInput, rgbOutput);
    this.rgbOutput = rgbOutput;
    initOutputRGBArray();
    externSynchronal = true;
  }

  protected DisplayLUT() {
    this(RGBArray.getOriginalRGBDoubleArray());
  }

  /**
   * 是否具有與外部同步的功能
   */
  private boolean externSynchronal;

  public boolean isExternSynchronal() {
    return externSynchronal;
  }

  /**
   * 不具同步效果的建構式
   * @param outputArray RGB[]
   */
  public DisplayLUT(RGB[] outputArray) {
    this(RGBArray.getDoubleArray(outputArray));
    this.outputRGBArray = outputArray;
    externSynchronal = false;
  }

  public DisplayLUT(RGB[] inputArray, RGB[] outputArray) {
    this(RGBArray.getDoubleArray(inputArray),
         RGBArray.getDoubleArray(outputArray));
    this.outputRGBArray = outputArray;
    externSynchronal = false;
  }

//  public final static Interpolation1DLUT[] getRGBInterpLUT(RGB[] rgbOutput) {
//    return Interpolation1DLUT.getRGBInterpLUT(produceDefaultRGBInput(),
//                                              RGBArray.getDoubleArray(rgbOutput));
//  }

  public double[] getInputValues(double[] outputRGBValues) {
    if (outputRGBValues.length != 3) {
      throw new IllegalArgumentException("outputRGBValues.length !=3");
    }
    double[] inputValues = new double[3];
    for (int x = 0; x < 3; x++) {
      inputValues[x] = lutArray[x].getKey(outputRGBValues[x]);
    }
    return inputValues;
  }

  private void initOutputRGBArray() {
    if (outputRGBArray == null) {
      outputRGBArray = new RGB[256];
      for (int x = 0; x < 256; x++) {
        double[] rgbValues = new double[] {
            rgbOutput[0][x], rgbOutput[1][x], rgbOutput[2][x]};
        outputRGBArray[x] = new RGB(RGB.ColorSpace.unknowRGB, rgbValues,
                                    RGB.MaxValue.Double255);
      }
    }
  }

  public RGB[] getOutputRGBArray() {
    return outputRGBArray;
  }

  public RGB getOutputRGB(final RGB inputRGB) {
    double[] values = new double[3];
    inputRGB.getValues(values, RGB.MaxValue.Double255);
    double[] outputValues = getOutputValues(values);
    RGB output = new RGB(inputRGB.getRGBColorSpace(), outputValues,
                         RGB.MaxValue.Double255);
    output.changeMaxValue(inputRGB.getMaxValue());
    return output;
  }

  public double getOutputValue(final double inputValue, RGBBase.Channel channel) {
    int index = channel.getArrayIndex();
    return lutArray[index].getValue(inputValue);
  }

  public double getInputValue(final double outputValue, RGBBase.Channel channel) {
    int index = channel.getArrayIndex();
    return lutArray[index].getKey(outputValue);
  }

  public RGB getInputRGB(final RGB outputRGB) {
    double[] values = new double[3];
    outputRGB.getValues(values, RGB.MaxValue.Double255);
    double[] inputValues = getInputValues(values);
    RGB input = new RGB(outputRGB.getRGBColorSpace(), inputValues,
                        RGB.MaxValue.Double255);
    input.changeMaxValue(outputRGB.getMaxValue());
    return input;
  }

  public double[] getOutputValues(double[] inputRGBValues) {
    if (inputRGBValues.length != 3) {
      throw new IllegalArgumentException("inputRGBValues.length !=3");
    }
    double[] outputValues = new double[3];
    for (int x = 0; x < 3; x++) {
      outputValues[x] = lutArray[x].getValue(inputRGBValues[x]);
    }
    return outputValues;
  }

  public final static class Instance {
    public final static DisplayLUT getLinearMap(RGB whiteRGB) {
      double[][] rgbOutput = RGBArray.getOriginalRGBDoubleArray();
      double[] whiteRGBValues = whiteRGB.getValues(new double[3],
          RGB.MaxValue.Double255);

      for (int x = 0; x < 3; x++) {
        double[] output = rgbOutput[x];
        int size = output.length;
        for (int y = 0; y < size; y++) {
          output[y] = (output[y] / 255.) * whiteRGBValues[x];
        }
      }
      DisplayLUT lut = new DisplayLUT(rgbOutput);
      return lut;
    }

  }

  protected void printLut() {
    if (rgbOutput != null) {
      System.out.println("rgbOutput:");
      for (double[] rgb : rgbOutput) {
        System.out.println(Arrays.toString(rgb));
      }
    }
    if (outputRGBArray != null) {
      System.out.println("outputRGBArray:");
      for (RGB rgb : outputRGBArray) {
        System.out.println(rgb);
      }
    }
  }

  public static void main(String[] args) {
    DisplayLUT lut = new DisplayLUT();
    System.out.println(lut.isExternSynchronal());

//    DisplayLUT lut2 = new DisplayLUT(lut.lutArray);
//    System.out.println(lut2.isExternSynchronal());
//    lut2.printLut();
  }
}
