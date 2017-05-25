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
   * 以二元搜尋找到左邊最接近的索引值
   * @param a Comparable[]
   * @param key Comparable
   * @return int
   */
  public final static int leftNearBinarySearch(Comparable[] a, Comparable key) {
    int result = Arrays.binarySearch(a, key);
    return leftNearBinarySearch0(a.length, result);
  }

  /**
   * 以二元搜尋找到左邊最接近的索引值
   * @param arrayLength int
   * @param binarySearchResult int
   * @return int
   */
  public final static int leftNearBinarySearch0(int arrayLength,
                                                int binarySearchResult) {
    int result = binarySearchResult;
    if (result < -1) {
      //interstion的場合
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
      //一般的場合
      return result - 1;
    }
    else {
      //為0 or -1的場合
      return result;
    }
  }

  /**
   * 以二元搜尋找到索引值, 要是找不到完全對應, 則是回傳左邊最靠近的索引值
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
   * 以二元搜尋找到索引值, 要是找不到完全對應, 則是回傳左邊最靠近的索引值
   * 與leftNearBinarySearch的不同點在於, leftNearBinarySearch就算找到完全對應, 也一定是
   * 回傳左邊鄰近的那個索引值.
   *
   * @param arrayLength int
   * @param binarySearchResult int
   * @return int
   */
  public final static int leftBinarySearch0(int arrayLength,
                                            int binarySearchResult) {
    int result = binarySearchResult;
    if (result < -1) {
      //interstion的場合
      result = -result;
      if (result > arrayLength) {
        return -1;
      }
      else {
        return result - 2;
      }
    }
    else if (result != 0 && result != -1) {
      //一般的場合
      if (result + 1 >= arrayLength) {
        return result - 1;
      }
      else {
        return result;
      }
    }
    else {
      //為0 or -1的場合
      return result;
    }
  }

  /**
   * 以二元搜尋找到左邊最接近的索引值
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
   * 以二元搜尋找到左邊最接近的索引值
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
   * 以二元搜尋找到左邊最接近的索引值, 同時回傳修正後的索引以及未修正的
   * @param a double[]
   * @param key double
   * @return int[] 第一筆為修正過,第二筆為未修正
   */
  public final static int[] leftNearBinarySearchAll(double[] a, double key) {
    int[] result = new int[2];
    result[1] = Arrays.binarySearch(a, key);
    result[0] = leftNearBinarySearch0(a.length, result[1]);

    return result;
  }

  /**
   * 左邊逼近
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
