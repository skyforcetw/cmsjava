package shu.util;

import java.util.*;

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
public final class Searcher {
  private Searcher() {

  }

  /**
   * �H�G���j�M��쥪��̱��񪺯��ޭ�
   * @param a Comparable[]
   * @param key Comparable
   * @return int
   */
  public final static int leftNearBinarySearch(Comparable[] a, Comparable key) {
    int result = Arrays.binarySearch(a, key);
    return leftNearBinarySearch0(a.length, result);
  }

  /**
   * �H�G���j�M��쥪��̱��񪺯��ޭ�
   * @param arrayLength int
   * @param binarySearchResult int
   * @return int
   */
  public final static int leftNearBinarySearch0(int arrayLength,
                                                int binarySearchResult) {
    int result = binarySearchResult;
    if (result < -1) {
      //interstion�����X
      result = -result;
      if (result > arrayLength) {
//        return result - 1;
        return -1;
      }
      else {
        return result - 2;
      }
    }
    else if (result != 0 && result != -1) {
      //�@�몺���X
      return result - 1;
    }
    else {
      //��0 or -1�����X
      return result;
    }
  }

  /**
   * �H�G���j�M�����ޭ�, �n�O�䤣�짹������, �h�O�^�ǥ���̾a�񪺯��ޭ�
   * @param a double[]
   * @param key double
   * @return int
   */
  public final static int leftBinarySearch(double[] a, double key) {
    int result = Arrays.binarySearch(a, key);
    return leftBinarySearch0(a.length, result);
  }

  public final static int leftBinarySearch(int[] a, int key) {
    int result = Arrays.binarySearch(a, key);
    return leftBinarySearch0(a.length, result);
  }

  public final static int leftBinarySearch(short[] a, short key) {
    int result = Arrays.binarySearch(a, key);
    return leftBinarySearch0(a.length, result);
  }

  /**
   * �H�G���j�M�����ޭ�, �n�O�䤣�짹������, �h�O�^�ǥ���̾a�񪺯��ޭ�
   * �PleftNearBinarySearch�����P�I�b��, leftNearBinarySearch�N���짹������, �]�@�w�O
   * �^�ǥ���F�񪺨��ӯ��ޭ�.
   *
   * @param arrayLength int
   * @param binarySearchResult int
   * @return int
   */
  public final static int leftBinarySearch0(int arrayLength,
                                            int binarySearchResult) {
    int result = binarySearchResult;
    if (result < -1) {
      //interstion�����X
      result = -result;
      if (result > arrayLength) {
        return -1;
      }
      else {
        return result - 2;
      }
    }
    else if (result != 0 && result != -1) {
      //�@�몺���X
      if (result + 1 >= arrayLength) {
        return result - 1;
      }
      else {
        return result;
      }
    }
    else {
      //��0 or -1�����X
      return result;
    }
  }

  /**
   * �H�G���j�M��쥪��̱��񪺯��ޭ�
   * @param a double[]
   * @param key double
   * @return int
   */
  public final static int leftNearBinarySearch(double[] a, double key) {
    int result = Arrays.binarySearch(a, key);
    return leftNearBinarySearch0(a.length, result);
  }

  public final static int leftNearBinarySearch(int[] a, int key) {
    int result = Arrays.binarySearch(a, key);
    return leftNearBinarySearch0(a.length, result);
  }

  public final static int leftNearBinarySearch(short[] a, short key) {
    int result = Arrays.binarySearch(a, key);
    return leftNearBinarySearch0(a.length, result);
  }

  /**
   * �H�G���j�M��쥪��̱��񪺯��ޭ�
   * @param a T[]
   * @param key T
   * @param c Comparator
   * @return int
   */
  public static <T> int leftNearBinarySearch(T[] a, T key,
                                             Comparator<? super T> c) {
    int result = Arrays.binarySearch(a, key, c);
    return leftNearBinarySearch0(a.length, result);
  }

  /**
   * �H�G���j�M��쥪��̱��񪺯��ޭ�, �P�ɦ^�ǭץ��᪺���ޥH�Υ��ץ���
   * @param a double[]
   * @param key double
   * @return int[] �Ĥ@�����ץ��L,�ĤG�������ץ�
   */
  public final static int[] leftNearBinarySearchAll(double[] a, double key) {
    int[] result = new int[2];
    result[1] = Arrays.binarySearch(a, key);
    result[0] = leftNearBinarySearch0(a.length, result[1]);

    return result;
  }

  /**
   * ����G��
   * @param a double[]
   * @param key double
   * @return int
   */
  public final static int leftNearSequentialSearch(double[] a, double key) {
    if (key < a[0]) {
      return -1;
    }
    for (int x = 1; x < a.length; x++) {
      if (a[x] >= key) {
        return x - 1;
      }
    }
    return a.length - 1;
  }

  public final static int sequentialSearch(double[] a, double key) {
    for (int x = 0; x < a.length; x++) {
      if (a[x] == key) {
        return x;
      }
    }
    return -1;
  }

  public final static int sequentialSearch(int[] a, int key) {
    for (int x = 0; x < a.length; x++) {
      if (a[x] == key) {
        return x;
      }
    }
    return -1;
  }

  public static void main(String[] args) {
    double[] numbers = new double[] {
        1, 3, 5, 6};
    int[] r = leftNearBinarySearchAll(numbers, 5);
    System.out.println(r[0]);
    System.out.println(r[1]);

  }
}
