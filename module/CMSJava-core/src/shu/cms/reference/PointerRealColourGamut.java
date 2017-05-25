package shu.cms.reference;

import java.awt.*;

import quickhull3d.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;

//import shu.plot.*;

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
public class PointerRealColourGamut {
  private final static int[] LArray = new int[] {
      15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90};
  private final static int[] hArray = new int[] {
      0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160,
      170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310,
      320, 330, 340, 350
  };

  public final static int[] getLh(int xindex, int yindex) {
    return new int[] {
        LArray[xindex], hArray[yindex]};
  }

  private static CIELCh[] gamut;
  protected final static void initGamut() {
    if (gamut == null) {
      int size = data.length * data[0].length;
      gamut = new CIELCh[size];
      int index = 0;
      for (int y = 0; y < data.length; y++) {
        for (int x = 0; x < data[0].length; x++) {
          int C = data[y][x];
          int[] Lh = getLh(x, y);
          CIELCh LCh = new CIELCh(Lh[0], C, Lh[1]);
          gamut[index++] = LCh;
        }
      }
    }
  }

  public static void main(String[] args) {
    CIELCh[] gamut = getGamut();
    int size = gamut.length;
    Point3d[] points = new Point3d[size];
    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.jzy3D);

    for (int x = 0; x < size; x++) {
      CIELCh LCh = gamut[x];
      CIELab Lab = new CIELab(LCh);
      points[x] = new Point3d(Lab.a, Lab.b, Lab.L);
//      plot.addScatterPlot("", Color.black, Lab.a, Lab.b, Lab.L);
    }
    QuickHull3D hull = new QuickHull3D(points);

    plot.addQuickHull3D(Color.green, hull, .5f);
    plot.setVisible();
  }

  public final static CIELCh[] getGamut() {
    initGamut();
    return gamut;
  }

  private final static int[][] data = new int[][] {
      {
      10, 30, 43, 56, 68, 77, 79, 77, 72, 65, 57, 50, 40, 30, 19, 8}, {
      15, 30, 45, 56, 64, 70, 73, 73, 71, 65, 57, 48, 39, 30, 18, 7}, {
      14, 34, 49, 61, 69, 74, 76, 76, 74, 68, 61, 51, 40, 30, 19, 9}, {
      35, 48, 59, 68, 75, 82, 84, 83, 80, 75, 67, 56, 45, 33, 21, 10}, {
      27, 40, 53, 66, 79, 90, 94, 93, 88, 82, 72, 60, 47, 35, 22, 10}, {
      10, 21, 34, 45, 60, 75, 90, 100, 102, 99, 88, 75, 59, 45, 30, 15}, {
      4, 15, 26, 37, 48, 59, 70, 82, 93, 103, 106, 98, 85, 66, 45, 23}, {
      5, 15, 25, 36, 46, 56, 67, 76, 85, 94, 102, 108, 103, 82, 58, 34}, {
      6, 15, 24, 32, 40, 48, 55, 64, 72, 82, 94, 105, 115, 115, 83, 48}, {
      4, 12, 20, 28, 36, 44, 53, 60, 68, 75, 83, 90, 98, 106, 111, 90}, {
      9, 16, 23, 30, 37, 45, 51, 58, 65, 72, 80, 86, 94, 100, 106, 108}, {
      9, 18, 27, 35, 44, 52, 59, 66, 74, 82, 87, 92, 95, 100, 96, 84}, {
      4, 14, 23, 32, 41, 49, 57, 64, 71, 78, 84, 90, 94, 95, 83, 50}, {
      5, 18, 30, 40, 48, 56, 64, 70, 77, 82, 85, 88, 89, 84, 64, 35}, {
      7, 20, 32, 42, 52, 60, 69, 76, 82, 87, 89, 90, 83, 71, 54, 30}, {
      7, 21, 34, 45, 57, 68, 75, 81, 84, 84, 83, 80, 72, 58, 44, 20}, {
      8, 24, 36, 48, 58, 68, 76, 82, 85, 83, 78, 69, 59, 49, 34, 15}, {
      13, 25, 36, 47, 57, 65, 70, 75, 76, 75, 71, 65, 57, 45, 30, 15}, {
      10, 25, 38, 48, 57, 64, 69, 71, 72, 69, 64, 60, 51, 41, 29, 16}, {
      7, 19, 30, 40, 48, 55, 59, 62, 62, 60, 55, 49, 41, 32, 23, 13}, {
      5, 19, 29, 37, 42, 45, 46, 46, 45, 43, 39, 35, 30, 22, 14, 7}, {
      0, 12, 17, 26, 34, 43, 49, 51, 54, 50, 46, 40, 32, 24, 14, 4}, {
      2, 12, 20, 28, 35, 40, 45, 48, 51, 49, 45, 38, 32, 23, 15, 6}, {
      10, 20, 29, 36, 42, 46, 49, 51, 52, 50, 45, 39, 32, 24, 15, 7}, {
      8, 16, 26, 34, 41, 47, 49, 50, 50, 47, 42, 36, 29, 21, 12, 4}, {
      9, 21, 32, 40, 49, 54, 55, 55, 52, 48, 43, 36, 29, 21, 13, 4}, {
      12, 24, 34, 41, 46, 51, 55, 56, 51, 46, 40, 33, 27, 20, 13, 6}, {
      14, 31, 42, 50, 55, 60, 60, 57, 50, 45, 39, 33, 26, 20, 13, 6}, {
      10, 29, 45, 55, 60, 61, 60, 57, 53, 46, 40, 34, 25, 18, 11, 4}, {
      20, 40, 60, 69, 71, 69, 65, 58, 50, 43, 36, 29, 24, 18, 12, 5}, {
      30, 55, 72, 81, 79, 72, 64, 57, 50, 42, 35, 30, 24, 17, 12, 5}, {
      62, 76, 85, 88, 85, 80, 71, 62, 55, 47, 41, 34, 27, 20, 14, 6}, {
      60, 71, 79, 84, 85, 86, 82, 74, 66, 57, 48, 40, 31, 24, 16, 8}, {
      20, 50, 72, 86, 89, 89, 86, 80, 72, 63, 54, 45, 36, 27, 18, 9}, {
      26, 49, 63, 73, 82, 87, 87, 83, 78, 71, 62, 51, 40, 28, 18, 4}, {
      15, 37, 52, 65, 73, 79, 82, 84, 79, 73, 63, 53, 40, 30, 17, 6}
  };

}
