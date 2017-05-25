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
   * �]�w�t��k�B�⵲���᪺������T
   * @param nearestRGB RGB �t��k�o�쪺�̱���RGB
   * @param nearestList List �@���B��L�{���o�쪺�Ҧ�RGB
   * @param totalList List ��ӹB�⪺�L�{�o�쪺RGB
   */
  public void setInfomation(RGB nearestRGB, List<RGB> nearestList,
      List<RGB> totalList) {
    this.setRedundant(nearestRGB);
    this.nearestList = nearestList;
    this.totalList = totalList;
  }

  /**
   *
   * @param nearestRGB RGB �̱���RGB
   * @param indexes double[] ���_�Ϊ����ޭ�
   * @param aroundXYZ CIEXYZ[] ���_�Ѥ���XYZ
   * @param indexInArray int �̱���RGB�����쪺�}�C����
   * @param aroundRGB RGB[] ���_�Ѥ���RGB
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
   * @param nearestRGB RGB �̱���RGB
   * @param indexes double[] ���_�Ϊ����ޭ�
   * @param aroundXYZ CIEXYZ[] ���_�Ѥ���XYZ
   * @param indexInArray int �̱���RGB�����쪺�}�C����
   * @param aroundRGB RGB[] ���_�Ѥ���RGB
   * @param practicalMeasureCount int ��ڶi��q��������
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
   * �barray�������ޭ�
   */
  public int indexInArray;
  /**
   * ���o���_�Ϊ����ޭ�
   * @return double
   */
  public double getIndex() {
    return indexes[indexInArray];
  }

  /**
   * ���_�Ϊ����ޭ�
   */
  public double[] indexes;
  /**
   * �̱���RGB
   */
  public RGB nearestRGB;
  /**
   * �Կ諸�̱���RGB (�w�]�OcubeCheckInOneJNDI�����G)
   */
  public RGB candilateNearestRGB;
  /**
   * �q���ұo����aroundRGB������XYZ
   */
  public CIEXYZ[] aroundXYZ;
  /**
   * ���ͦ�result���Ҧ��X��g�L
   */
  public List<RGB> nearestList;
  /**
   * ���ͦ�result���Ҧ��q���g�L
   */
  public List<RGB> totalList;
  /**
   * �q�L�Ҧ���qualify(���)
   */
  public boolean passAllQualify = false;
  /**
   * �Ҧ�����泣�S���q�L
   */
  public boolean allQualifyNonPass = false;

  /**
   * �h�l�q��������
   */
  private int redundantMeasure;
  /**
   * �O�_���h�l�����G
   */
  private boolean redundance = false;
  /**
   * ��ڤW���q������ (�w�g������buffer�����X�Ӥ��ݶq��������)
   */
  private int practicalMeasureCount;

  /**
   * �O�_���@�����l(�h�l)���B�⵲�G
   * @return boolean
   */
  public boolean isRedundance() {
    return redundance;
  }

  /**
   * ���o���l(�h�l)���q������
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
   * �]�w�O�_���h�l���B�⵲�G
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
