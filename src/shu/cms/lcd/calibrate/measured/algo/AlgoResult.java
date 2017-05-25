package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.util.*;

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
public class AlgoResult {

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("allQualifyNonPass: " + allQualifyNonPass + "\n");
    buf.append("passAllQualify: " + passAllQualify + "\n");
    buf.append("indexInArray: " + indexInArray + "\n");
    buf.append("nearestRGB: " + nearestRGB + "\n");
    buf.append("practicalMeasureCount: " + practicalMeasureCount + "\n");
    buf.append("aroundXYZ: " + Arrays.toString(aroundXYZ) + "\n");
    buf.append("indexes: " + Arrays.toString(indexes) + "\n");
    buf.append("nearestList: " + nearestList + "\n");
    buf.append("totalList: " + totalList + "\n");

    return buf.toString();
  }

  /**
   * 設定演算法運算結束後的相關資訊
   * @param nearestRGB RGB 演算法得到的最接近RGB
   * @param nearestList List 一次運算過程中得到的所有RGB
   * @param totalList List 整個運算的過程得到的RGB
   */
  public void setInfomation(RGB nearestRGB, List<RGB> nearestList,
      List<RGB> totalList) {
    this.setRedundant(nearestRGB);
    this.nearestList = nearestList;
    this.totalList = totalList;
  }

  /**
   *
   * @param nearestRGB RGB 最接近的RGB
   * @param indexes double[] 評斷用的索引值
   * @param aroundXYZ CIEXYZ[] 評斷參予的XYZ
   * @param indexInArray int 最接近RGB對應到的陣列索引
   * @param aroundRGB RGB[] 評斷參予的RGB
   */
  public AlgoResult(RGB nearestRGB, double[] indexes,
                    RGB[] aroundRGB, CIEXYZ[] aroundXYZ, int indexInArray) {
    this.nearestRGB = nearestRGB;
    this.indexes = indexes;
    this.aroundXYZ = aroundXYZ;
    this.indexInArray = indexInArray;
    DuplicateLinkedList total = new DuplicateLinkedList<RGB> ();
    total.addAll(aroundRGB);
    this.totalList = total;
  }

  /**
   *
   * @param nearestRGB RGB 最接近的RGB
   * @param indexes double[] 評斷用的索引值
   * @param aroundXYZ CIEXYZ[] 評斷參予的XYZ
   * @param indexInArray int 最接近RGB對應到的陣列索引
   * @param aroundRGB RGB[] 評斷參予的RGB
   * @param practicalMeasureCount int 實際進行量測的次數
   */
  public AlgoResult(RGB nearestRGB, double[] indexes,
                    RGB[] aroundRGB, CIEXYZ[] aroundXYZ, int indexInArray,
                    int practicalMeasureCount) {
    this.nearestRGB = nearestRGB;
    this.indexes = indexes;
    this.aroundXYZ = aroundXYZ;
    this.indexInArray = indexInArray;
    DuplicateLinkedList total = new DuplicateLinkedList<RGB> ();
    total.addAll(aroundRGB);
    this.totalList = total;
    this.practicalMeasureCount = practicalMeasureCount;
  }

  public CIEXYZ getNearestXYZ() {
    return aroundXYZ[indexInArray];
  }

  /**
   * 在array中的索引值
   */
  public int indexInArray;
  /**
   * 取得評斷用的索引值
   * @return double
   */
  public double getIndex() {
    return indexes[indexInArray];
  }

  /**
   * 評斷用的索引值
   */
  public double[] indexes;
  /**
   * 最接近的RGB
   */
  public RGB nearestRGB;
  /**
   * 候選的最接近的RGB (預設是cubeCheckInOneJNDI的結果)
   */
  public RGB candilateNearestRGB;
  /**
   * 量測所得的的aroundRGB對應的XYZ
   */
  public CIEXYZ[] aroundXYZ;
  /**
   * 產生此result的所有合格經過
   */
  public List<RGB> nearestList;
  /**
   * 產生此result的所有量測經過
   */
  public List<RGB> totalList;
  /**
   * 通過所有的qualify(資格)
   */
  public boolean passAllQualify = false;
  /**
   * 所有的資格都沒有通過
   */
  public boolean allQualifyNonPass = false;

  /**
   * 多餘量測的次數
   */
  private int redundantMeasure;
  /**
   * 是否為多餘的結果
   */
  private boolean redundance = false;
  /**
   * 實際上的量測次數 (已經扣掉由buffer中取出而不需量測的次數)
   */
  private int practicalMeasureCount;

  /**
   * 是否為一次冗餘(多餘)的運算結果
   * @return boolean
   */
  public boolean isRedundance() {
    return redundance;
  }

  /**
   * 取得冗餘(多餘)的量測次數
   * @return int
   */
  public int getRedundantMeasure() {
    return redundantMeasure;
  }

  public int getRedundantMeasure(RGB initRGB) {
    setRedundant(initRGB);
    return getRedundantMeasure();
  }

  /**
   * 設定是否為多餘的運算結果
   * @param initRGB RGB
   */
  private void setRedundant(RGB initRGB) {
    redundance = initRGB.equals(nearestRGB);
    if (redundance) {
      redundantMeasure = practicalMeasureCount;
    }
  }

  public void setRedundantMeasure(int redundantMeasure) {
    this.redundantMeasure = redundantMeasure;
  }
}
