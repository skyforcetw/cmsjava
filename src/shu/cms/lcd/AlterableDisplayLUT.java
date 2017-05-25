package shu.cms.lcd;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.util.*;
import shu.math.array.*;

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
public class AlterableDisplayLUT
    extends DisplayLUT {
  public boolean addOutputValue(RGBBase.Channel ch, int index, double addValue) {
    checkSync();
    canUndo = false;
    undoChannel = ch;
    undoIndex = index;
    undoAddValue = addValue;
    if (ch == RGBBase.Channel.W) {
      //========================================================================
      // 調白色, 也就是R/G/B一起動
      //========================================================================
      double[] results = new double[3];
      results[0] = rgbOutput[0][index] + addValue;
      results[1] = rgbOutput[1][index] + addValue;
      results[2] = rgbOutput[2][index] + addValue;
      for (int x = 0; x < 3; x++) {
        if (results[x] > 255 || results[x] < 0) {
          //超出可調整範圍
          return false;
        }
      }

      for (int x = 0; x < 3; x++) {
        rgbOutput[x][index] = results[x];
        outputRGBArray[index].setValue(ch.getChannelByArrayIndex(x), results[x],
                                       RGB.MaxValue.Double255);
      }
      //========================================================================
    }
    else {
      //========================================================================
      // 調單色
      //========================================================================
      double result = rgbOutput[ch.getArrayIndex()][index] + addValue;
      if (result > 255 || result < 0) {
        //超出可調整範圍
        return false;
      }
      rgbOutput[ch.getArrayIndex()][index] = result;
      outputRGBArray[index].setValue(ch, result, RGB.MaxValue.Double255);
      canUndo = true;
      //========================================================================
    }

    return true;
  }

  /**
   * 返回上一次的修正
   * @return boolean
   */
  public boolean undo() {
    if (canUndo) {
      if (!addOutputValue(undoChannel, undoIndex, -undoAddValue)) {
        //如果undo失敗
        canUndo = false;
        return false;
      }
      canUndo = false;
      return true;
    }
    else {
      return false;
    }
  }

  private double undoAddValue;
  private RGBBase.Channel undoChannel;
  private int undoIndex;
  private boolean canUndo = false;

  public boolean minusOutputValue(RGBBase.Channel ch, int index,
                                  double addValue) {
    return addOutputValue(ch, index, -addValue);
//    rgbOutput[ch.getArrayIndex()][index] -= addValue;
  }

  private double[][] memoryRGBOutput;
  private RGB[] memoryOutputRGBArray;

  /**
   * 將對照表現況記憶起來
   * LUT -> memory
   */
  public void storeToMemory() {
    memoryRGBOutput = DoubleArray.copy(rgbOutput);
    int size = outputRGBArray.length;
//    memoryOutputRGBArray = new RGB[size];
    memoryOutputRGBArray = RGBArray.deepClone(outputRGBArray);
//    for (int x = 0; x < size; x++) {
//      memoryOutputRGBArray[x] = (RGB) outputRGBArray[x].clone();
//    }
  }

  /**
   * 從記憶撈出並且載入到對照表
   * memory -> LUT
   */
  public void loadFromMemory() {
    if (memoryRGBOutput == null || memoryOutputRGBArray == null) {
      return;
    }

    int rgbOutputSize = rgbOutput.length;
    for (int x = 0; x < rgbOutputSize; x++) {
      DoubleArray.copy(memoryRGBOutput[x], rgbOutput[x]);
//      int outputsize = memoryRGBOutput[x].length;
//      for (int y = 0; y < outputsize; y++) {
//        rgbOutput[x][y] = memoryRGBOutput[x][y];
//      }
//      outputRGBArray[x] = (RGB) memoryOutputRGBArray[x].clone();
    }

//    int rgbArraySize = outputRGBArray.length;
    outputRGBArray = RGBArray.deepClone(memoryOutputRGBArray);
//    for (int x = 0; x < rgbArraySize; x++) {
//      outputRGBArray[x] = (RGB) memoryOutputRGBArray[x].clone();
//    }
  }

  protected void checkSync() {
    if (!isSync()) {
      throw new IllegalStateException(
          "rgbOutput and outputRGBArray is not sync!");
    }
  }

  /**
   * rgbOutput與outputRGBArray是否同步
   * @return boolean
   */
  public boolean isSync() {
    int size = outputRGBArray.length;
    for (int x = 0; x < size; x++) {
      double[] values = new double[] {
          rgbOutput[0][x], rgbOutput[1][x], rgbOutput[2][x]};
      RGB rgb = outputRGBArray[x];
      double[] rgbValues = rgb.getValues();
      boolean eq = Arrays.equals(values, rgbValues);
      if (!eq) {
        return false;
      }
    }
    return true;
  }

  protected void printLut() {
    super.printLut();
    if (!isSync()) {
      System.out.println("rgbOutput and outputRGBArray is not sync!");
    }
  }
}
