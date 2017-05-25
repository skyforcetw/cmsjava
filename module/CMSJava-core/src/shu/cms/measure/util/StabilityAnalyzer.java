package shu.cms.measure.util;

import shu.cms.*;
import shu.cms.colorspace.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 目標:Lab的穩定度/
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class StabilityAnalyzer {
  protected DeltaE.Formula deltaEType;
  protected CIELab[] LabArray;
  protected CIELab aveLab;
  protected double[] deltaEs;

  /**
   * 分析可供用來計算mcdm的色塊數量及開始的索引值
   * @param lcdTarget LCDTarget
   * @return int[]
   */
  protected final static int[] analyzeAvailableSizeAndStartIndex(LCDTarget
      lcdTarget) {
    return new int[] {
        lcdTarget.size(), 0};
//    int size = lcdTarget.size();
//
//    int availableSize = 0;
//    int startIndex = -1;
//    for (int x = 0; x < size; x++) {
//      Patch p = lcdTarget.getPatch(x);
//
//      Patch nextP = null;
//      if (x + 1 != size) {
//        nextP = lcdTarget.getPatch(x + 1);
//      }
//      if (p.getXYZ().Y > 0 && p.getRGB().isGray() &&
//          (nextP != null ? nextP.getRGB().isGray() : true)) {
//        availableSize++;
//        startIndex = startIndex == -1 ? x : startIndex;
//      }
//
//    }
//    return new int[] {
//        availableSize, startIndex};
  }

  public StabilityAnalyzer(CIELab[] LabArray, DeltaE.Formula deltaEType) {
    this.LabArray = LabArray;
    this.deltaEType = deltaEType;
    aveLab = calculateAverage(LabArray);
    deltaEs = calculateDeltaEs(LabArray, aveLab, deltaEType);
  }

  public StabilityAnalyzer(CIEXYZ[] XYZArray, CIEXYZ white,
                           DeltaE.Formula deltaEType) {
    this(XYZToLabArray(XYZArray, white), deltaEType);

  }

  public double getMCDM() {
    return Maths.mean(deltaEs);
  }

  public double getSTD() {
    return Maths.std(deltaEs);
  }

  public double getMax() {
    return Maths.max(deltaEs);
  }

  protected final static double[] calculateDeltaEs(CIELab[] Labs,
      CIELab average,
      DeltaE.Formula deltaEType) {
    int size = Labs.length;
    double[] deltaEs = new double[size];
    for (int x = 0; x < size; x++) {
      deltaEs[x] = new DeltaE(Labs[x], average).getDeltaE(deltaEType);
    }
    return deltaEs;
  }

  protected final static CIELab calculateAverage(CIELab[] Labs) {
    CIELab ave = new CIELab();
    for (int x = 0; x < Labs.length; x++) {
      ave.L += Labs[x].L;
      ave.a += Labs[x].a;
      ave.b += Labs[x].b;
    }
    ave.L /= Labs.length;
    ave.a /= Labs.length;
    ave.b /= Labs.length;

    return ave;
  }

  protected final static CIELab[] XYZToLabArray(CIEXYZ[] XYZArray, CIEXYZ white) {
    int size = XYZArray.length;
    CIELab[] LabArray = new CIELab[size];
    for (int x = 0; x < size; x++) {
      LabArray[x] = CIELab.fromXYZ(XYZArray[x], white);
    }
    return LabArray;
  }

  public static class MultiStabilityAnalyzer {
    protected LCDTarget[] targets;
    protected int startIndex;
    protected StabilityAnalyzer[] analyzerArray;

    protected final static CIELab[][] targetToTransposeLabArray(LCDTarget[]
        lcdTarget, int[] sizeAndIndex) {
      int targetSize = lcdTarget.length;
//      int patchSize = lcdTarget[0].size();
      int patchSize = sizeAndIndex[0];

      CIELab[][] LabArray = new CIELab[patchSize][targetSize];
      for (int x = 0; x < patchSize; x++) {
        int index = sizeAndIndex[1] + x;
        for (int y = 0; y < targetSize; y++) {
          LabArray[x][y] = lcdTarget[y].getPatch(index).getLab();
        }
      }
      return LabArray;
    }

    public MultiStabilityAnalyzer(LCDTarget[] targets,
                                  DeltaE.Formula deltaEType) {
      this.targets = targets;
      int[] sizeAndIndex = analyzeAvailableSizeAndStartIndex(targets[0]);
      startIndex = sizeAndIndex[1];
      int size = sizeAndIndex[0];

      analyzerArray = new StabilityAnalyzer[size];
      CIELab[][] LabArray = targetToTransposeLabArray(targets, sizeAndIndex);
      for (int x = 0; x < size; x++) {
        analyzerArray[x] = new StabilityAnalyzer(LabArray[x], deltaEType);
      }
    }

    public double getMCDM(int index) {
      return analyzerArray[index].getMCDM();
    }

    public double getSTD(int index) {
      return analyzerArray[index].getSTD();
    }

    public double getMax(int index) {
      return analyzerArray[index].getMax();
    }

    public int size() {
      return analyzerArray.length;
    }

    public Patch getFirstPatch(int index) {
      return targets[0].getPatch(startIndex + index);
    }

  }

  public static class MultiCIExyYStabilityAnalyzer {
    protected CIExyYStabilityAnalyzer[] analyzerArray;
    protected LCDTarget[] targets;
    protected int startIndex;

    public MultiCIExyYStabilityAnalyzer(LCDTarget[] targets) {
      this.targets = targets;
      int[] sizeAndIndex = analyzeAvailableSizeAndStartIndex(targets[0]);
      startIndex = sizeAndIndex[1];
      int size = sizeAndIndex[0];

      analyzerArray = new CIExyYStabilityAnalyzer[size];
      CIExyY[][] xyYArray = targetToTransposexyYValueArray(targets,
          sizeAndIndex);
      for (int x = 0; x < size; x++) {
        analyzerArray[x] = new CIExyYStabilityAnalyzer(xyYArray[x]);
      }
    }

    public double getMCDM(int index, CIExyYStabilityAnalyzer.Target type) {
      return analyzerArray[index].getMCDM(type);
    }

    public double getMean(int index, CIExyYStabilityAnalyzer.Target type) {
      return analyzerArray[index].getMean(type);
    }

    public double getSTD(int index, CIExyYStabilityAnalyzer.Target type) {
      return analyzerArray[index].getSTD(type);
    }

    public double getMax(int index, CIExyYStabilityAnalyzer.Target type) {
      return analyzerArray[index].getMax(type);
    }

    public Patch getFirstPatch(int index) {
      return targets[0].getPatch(startIndex + index);
    }

    public int size() {
      return analyzerArray.length;
    }

    protected final static CIExyY[][] targetToTransposexyYValueArray(
        LCDTarget[] lcdTarget, int[] sizeAndIndex) {
      int targetSize = lcdTarget.length;
      int patchSize = sizeAndIndex[0];

      CIExyY[][] xyYArray = new CIExyY[patchSize][targetSize];
      for (int x = 0; x < patchSize; x++) {
        int index = sizeAndIndex[1] + x;
        for (int y = 0; y < targetSize; y++) {
          CIEXYZ XYZ = lcdTarget[y].getPatch(index).getXYZ();
          xyYArray[x][y] = CIExyY.fromXYZ(XYZ);
        }
      }
      return xyYArray;
    }

  }

  public static class CIExyYStabilityAnalyzer {
    public static enum Target {
      x, y, Y;
    }

    protected double[][] xyYValues;
    //與平均的誤差
    protected double[][] transposeDelta;
    protected double[] mean;

    public CIExyYStabilityAnalyzer(CIExyY[] xyYArray) {
      transferData(xyYArray);
      mean = calculateMean(xyYValues);
      double[][] delta = calculateDeltaFromMean(xyYValues, mean);
      transposeDelta = DoubleArray.transpose(delta);
    }

    public double getMean(Target type) {
      switch (type) {
        case x:
          return mean[0];
        case y:
          return mean[1];
        case Y:
          return mean[2];
        default:
          return -1;
      }
    }

    /**
     * Mean color differences from the mean.
     * 與平均值所計算的色差的平均值
     * @param type Type
     * @return double
     */
    public double getMCDM(Target type) {
      switch (type) {
        case x:
          return Maths.mean(transposeDelta[0]);
        case y:
          return Maths.mean(transposeDelta[1]);
        case Y:
          return Maths.mean(transposeDelta[2]);
        default:
          return -1;
      }
    }

    public double getSTD(Target type) {
      switch (type) {
        case x:
          return Maths.std(transposeDelta[0]);
        case y:
          return Maths.std(transposeDelta[1]);
        case Y:
          return Maths.std(transposeDelta[2]);
        default:
          return -1;
      }
    }

    public double getMax(Target type) {
      switch (type) {
        case x:
          return Maths.max(transposeDelta[0]);
        case y:
          return Maths.max(transposeDelta[1]);
        case Y:
          return Maths.max(transposeDelta[2]);
        default:
          return -1;
      }
    }

    protected void transferData(ColorSpace[] csArray) {
      int size = csArray.length;
      xyYValues = new double[size][3];
      for (int x = 0; x < size; x++) {
        csArray[x].getValues(xyYValues[x]);
      }
    }

    protected final static double[] calculateMean(double[][] data) {
      int size = data.length;
      double[] total = new double[3];
      for (int x = 0; x < size; x++) {
        total[0] += data[x][0];
        total[1] += data[x][1];
        total[2] += data[x][2];
      }
      total[0] /= size;
      total[1] /= size;
      total[2] /= size;
      return total;
    }

    protected final static double[][] calculateDeltaFromMean(double[][] data,
        double[] mean) {
      int size = data.length;
      double[][] delta = new double[size][3];

      for (int x = 0; x < size; x++) {
        delta[x][0] = Math.abs(data[x][0] - mean[0]);
        delta[x][1] = Math.abs(data[x][1] - mean[1]);
        delta[x][2] = Math.abs(data[x][2] - mean[2]);
      }

      return delta;
    }

  }

  public StabilityAnalyzer(CIELab[] LabArray) {
//    transferData(LabArray);
  }

  public StabilityAnalyzer(CIEXYZ[] XYZArray) {
//    transferData(XYZArray);
  }

  public static void main(String[] args) {
    CIExyY[] xyYArray = new CIExyY[4];
    xyYArray[0] = new CIExyY(.33, .34, 100);
    xyYArray[1] = new CIExyY(.32, .31, 105);
    xyYArray[2] = new CIExyY(.32, .31, 105);
    xyYArray[3] = new CIExyY(.33, .31, 100);
    CIExyYStabilityAnalyzer xyYsa = new CIExyYStabilityAnalyzer(xyYArray);
//    xyYsa.get
  }
}
