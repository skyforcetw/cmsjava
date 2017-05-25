package auo.mura.test;

import shu.math.array.IntArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GridGame {

  static boolean check(int[][] grid) {
    int[] result = new int[12];
    for (int x = 0; x < 5; x++) {
      result[0] += grid[0][x];
      result[1] += grid[1][x];
      result[2] += grid[2][x];
      result[3] += grid[3][x];
      result[4] += grid[4][x];

      result[5] += grid[x][0];
      result[6] += grid[x][1];
      result[7] += grid[x][2];
      result[8] += grid[x][3];
      result[9] += grid[x][4];
      result[10] += grid[x][x];
      result[11] += grid[4 - x][4 - x];
    }
    int count = 0;
    for (int x = 0; x < 12; x++) {
      if (result[x] != 65) {
//        return false;
      }
      else {
        count++;
      }
    }
//    if (count > 5) {
//      System.out.println(count);
//    }
    if (count != 12) {
      return false;
    }
    else {
      return true;
    }
  }

  static int[] random(int[] num) {
    int length = num.length;
    int[] result = new int[length];

    int n = length;
    for (int x = 0; x < length; x++) {
      int index = (int) (Math.random() * n);
      result[x] = num[index];
      num[index] = num[n - 1];
      n--;
    }
    return result;
  }

  static boolean checkRandom(int[] random) {
    java.util.Arrays.sort(random);
    int n = random.length;
    for (int x = 0; x < n - 1; x++) {
      if (random[x] == random[x + 1]) {
        return false;
      }
    }
    return true;
  }

  static void solve(int[][] m, int[] num) {
    int[] rr = random(num);
//    if (!checkRandom(random)) {
//      int a = 1;
//    }
    int n = 0;
    m[0][0] = rr[n++];
    m[0][2] = rr[n++];
    m[0][3] = rr[n++];
    m[0][4] = rr[n++];

    m[1][0] = rr[n++];
    m[1][1] = rr[n++];
    m[1][2] = rr[n++];
    m[1][3] = rr[n++];

    for (int y = 2; y < 4; y++) {
      for (int x = 0; x < 5; x++) {
        if (2 == x && 2 == y) {
          continue;
        }
        m[y][x] = rr[n++];
      }
    }

    m[4][1] = rr[n++];
    m[4][3] = rr[n++];
  }

  public static void main(String[] args) {
    int[][] m = new int[5][5];
    m[0][1] = 16;
    m[1][4] = 2;
    m[4][0] = 17;
    m[4][2] = 12;
    m[4][4] = 22;
    m[2][2] = 13;
    int[] num = new int[] {
        1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 15, 18, 19, 20, 21, 23, 24, 25};
    int[][] m1 = null;
    long index = 0;
    do {
      m1 = IntArray.copy(m);
      int[] num2 = IntArray.copy(num);
      solve(m1, num2);
      if (index % 1000000 == 0) {
        System.out.println(index);
//        System.out.println(IntArray.toString(m1));
      }
      index++;
    }
    while (!check(m1));
    System.out.println(IntArray.toString(m1));
//    System.out.println(check(m1));
  }
}
