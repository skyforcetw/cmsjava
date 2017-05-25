package shu.cms.gma.gbp;

import javax.vecmath.*;
import shu.math.geometry.Geometry;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import shu.cms.lcd.LCDTargetBase;
import java.util.ArrayList;
import shu.cms.colorspace.independ.CIEXYZ;
import shu.math.Maths;
import java.util.Comparator;
import shu.math.array.DoubleArray;
import java.util.List;
import shu.cms.colorspace.depend.RGB;
import shu.math.geometry.PlaneFunction;
import shu.cms.plot.*;
import java.awt.Color;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public
    class RGBSurfaceAdvanced
    extends Boundary {
  /**
   * RGBSurface
   *
   * @param parent GamutBoundaryPoint
   */
  protected RGBSurfaceAdvanced(GamutBoundaryPoint parent) {
    super(parent);
//    this.parent = parent;
//    this.HLevel = parent.HLevel;
//    this.LLevel = parent.LLevel;
//    //初始化
//    boundaryHLCArray = new double[HLevel][LLevel];
  }

  private Map<Integer, List<double[]>> boundaryMap = new HashMap<Integer, List<double[]>> ();
  private void initBoundaryMap() {
    double[] rgbValues = new double[3];
    List<RGB> surfaceRGBList = LCDTargetBase.SurfaceTarget.getSurface(3);
    CIEXYZ referenceWhite = parent.profileColorSpace.getD65ReferenceWhite();

    for (RGB rgb : surfaceRGBList) {
      rgbValues[0] = (double) rgb.R / 255.;
      rgbValues[1] = (double) rgb.G / 255.;
      rgbValues[2] = (double) rgb.B / 255.;

      //在PCS的LCh做計算
      double[] LChValues = getLChValues(rgbValues, referenceWhite);
      int hue = (int) LChValues[2];
      List<double[]> LChValuesList = boundaryMap.get(hue);
      LChValuesList = (null == LChValuesList) ? new ArrayList<double[]> () :
          LChValuesList;
      LChValuesList.add(LChValues);
      boundaryMap.put(hue, LChValuesList);

//      setBoundaryLChValues(rgb, LChValues);
    }

  }

  /**
   * calculate
   * 更精確的RGBSurfac計算法
   * 1.將所有surface點整理到以hue為索引的map
   */
  protected void calculate() {
    initBoundaryMap();
    calculateBoundaryHLCArray();
  }

  private double[] getDistance(Point2d center, List < double[] > LChValuesList) {
    int size = LChValuesList.size();
    double[] distance = new double[size];
    double hue = center.x;
//    boolean isBound = (0 == hue) || (359 == hue);

    for (int x = 0; x < size; x++) {
      double[] LChValues = LChValuesList.get(x);

      boolean correct = /*isBound && */ Math.abs(LChValues[2] - center.x) > 2;
      if (correct) {
        LChValues = DoubleArray.copy(LChValues);
        LChValues[2] = (LChValues[2] > hue) ? LChValues[2] - 360 :
            LChValues[2] + 360;
      }

      Point2d point = new Point2d(LChValues[2], LChValues[0]);
      double d = Geometry.getDistance(center, point);
      distance[x] = d;
    }
    return distance;
  }

  private int[] getClosetIndex(double[] distance0, double[] distance1) {
    int index0 = Maths.minIndex(distance0);
    int index1 = Maths.minIndex(distance1);
    boolean judge = (distance0[index0] < distance1[index1]);
    int arrayIndex = judge ? 0 : 1;
    return new int[] {
        arrayIndex, judge ? index0 : index1};
  }

  private final static List<double[]> filter(List < double[] > lchValuesList,
                                             int lightness, int tolerance) {
    List<double[]> result = new ArrayList<double[]> ();
    for (double[] lchValues : lchValuesList) {
      double delta = Math.abs(lchValues[0] - lightness);
      if (delta < tolerance) {
        result.add(lchValues);
      }
    }
    return result;
  }

  private double getBoundaryChroma(int hue, int lightness, int[] hueArray) {
//Point2d center = new
    Point2d center = new Point2d(hue, lightness);
    final int tolerance = 10;
    List<double[]> hue0LChValuesList =
        filter(boundaryMap.get(hueArray[0]), lightness, tolerance);
    List<double[]> hue1LChValuesList =
        filter(boundaryMap.get(hueArray[1]), lightness, tolerance);
    List<double[]> hue2LChValuesList =
        filter(boundaryMap.get(hueArray[2]), lightness, tolerance);
    List<double[]> hue3LChValuesList =
        filter(boundaryMap.get(hueArray[3]), lightness, tolerance);

    //==========================================================================
    // 2 hue
    //==========================================================================
    List<double[]>
        hue12LChValuesList = new ArrayList<double[]> (hue1LChValuesList.size() +
        hue2LChValuesList.size());
    hue12LChValuesList.addAll(hue1LChValuesList);
    hue12LChValuesList.addAll(hue2LChValuesList);
    Point3d[] twoHueTriangle = getMinimumAreaTriangle(center,
        hue12LChValuesList);
    double twoHueArea = minimumArea;
    //==========================================================================
    // 4 hue
    //==========================================================================
    List<double[]>
        hue0123LChValuesList = new ArrayList<double[]> (
            hue0LChValuesList.size() + hue1LChValuesList.size() +
            hue2LChValuesList.size() + hue3LChValuesList.size());
    hue0123LChValuesList.addAll(hue0LChValuesList);
    hue0123LChValuesList.addAll(hue1LChValuesList);
    hue0123LChValuesList.addAll(hue2LChValuesList);
    hue0123LChValuesList.addAll(hue3LChValuesList);
    Point3d[] fourHueTriangle = getMinimumAreaTriangle(center,
        hue0123LChValuesList);
    double fourHueArea = minimumArea;
    //==========================================================================
    if (twoHueArea != Double.MAX_VALUE && fourHueArea != Double.MAX_VALUE) {
      PlaneFunction plane = (twoHueArea < fourHueArea) ?
          PlaneFunction.getInstance(twoHueTriangle) :
          PlaneFunction.getInstance(fourHueTriangle);

      double chroma = getChroma(plane, center);
      return chroma;
    }
    else {
      if (Plot3D.getPlotWindowCount() == 0) {
        Plot3D p = Plot3D.getInstance(Integer.toString(hue));
        for (double[] lchValues : hue0LChValuesList) {
          p.addScatterPlot("", Color.red, lchValues);
        }
        for (double[] lchValues : hue1LChValuesList) {
          p.addScatterPlot("", Color.green, lchValues);
        }
        for (double[] lchValues : hue2LChValuesList) {
          p.addScatterPlot("", Color.yellow, lchValues);
        }
        for (double[] lchValues : hue3LChValuesList) {
          p.addScatterPlot("", Color.blue, lchValues);
        }
        p.setVisible();
      }
      return -1;
    }
  }

  private final static double getChroma(PlaneFunction plane, Point2d center) {
    double chroma = (plane.a * center.x + plane.c * center.y + plane.d) /
        -plane.b;
    return chroma;
  }

  private static class DistanceComparator
      implements Comparator {
    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      double[] array1 = (double[]) o1;
      double[] array2 = (double[]) o2;
      return Double.compare(array1[3], array2[3]);
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return false;
    }

  }

  private static DistanceComparator distanceComparator = new DistanceComparator();
  private final static int CandidateTriangleCount = 3;

  private double minimumArea = 0;
  private Point3d[] getMinimumAreaTriangle(Point2d center,
                                           List < double[] > lchValuesList) {
    double[] distance = getDistance(center, lchValuesList);
    int size = lchValuesList.size();
    List<double[]> lchAndDistanceList = new ArrayList<double[]> (size);
    double hue = center.x;

    //==========================================================================
    // 拉出LCh並且計算距離
    //==========================================================================
    for (int x = 0; x < size; x++) {
      double[] lchValues = lchValuesList.get(x);
      double[] lchAndDistance = new double[4];
      System.arraycopy(lchValues, 0, lchAndDistance, 0, 3);
      lchAndDistance[3] = distance[x];
      lchAndDistanceList.add(lchAndDistance);

      boolean correct = /*isBound && */ Math.abs(lchValues[2] - center.x) > 2;
      if (correct) {
        lchAndDistance[2] = (lchAndDistance[2] > hue) ? lchAndDistance[2] - 360 :
            lchAndDistance[2] + 360;
      }
    }
    //==========================================================================

    //依照距離重新排序
    Collections.sort(lchAndDistanceList, distanceComparator);
    Point2d[][] candidateTriangles = new Point2d[CandidateTriangleCount][];
    Point3d[][] candidateTriangles3D = new Point3d[CandidateTriangleCount][];
    int index = 0;

    double[] p0_ = lchAndDistanceList.get(0);
    Point2d p0 = new Point2d(p0_[2], p0_[0]);
    boolean stop = false;
    for (int x = 1; x < size && !stop; x++) {
      double[] p1_ = lchAndDistanceList.get(x);
      Point2d p1 = new Point2d(p1_[2], p1_[0]);

      for (int y = x + 1; y < size && !stop; y++) {
        double[] p2_ = lchAndDistanceList.get(y);
        Point2d p2 = new Point2d(p2_[2], p2_[0]);
        Point2d[] triangles = new Point2d[] {
            p0, p1, p2};
        boolean isInTriangle = Geometry.isInTriangle(center, triangles);

        if (isInTriangle) {
          candidateTriangles[index] = triangles;
          Point3d[] triangles3D = new Point3d[] {
              new Point3d(p0_), new Point3d(p1_), new Point3d(p2_)};
          candidateTriangles3D[index] = triangles3D;
          index++;
          if (CandidateTriangleCount == index) {
            stop = true;
          }
        }
      }
    }

    double[] area = new double[CandidateTriangleCount];
    for (int x = 0; x < CandidateTriangleCount; x++) {
      Point2d[] triangle = candidateTriangles[x];
      area[x] = (triangle != null && triangle.length == 3) ?
          Geometry.getTriangleArea(triangle) :
          Double.MAX_VALUE;
    }
    int minIndex = Maths.minIndex(area);
    minimumArea = area[minIndex];
    return candidateTriangles3D[minIndex];

  }

  private int[] getHueArray(int hue) {
    int[] hueArray = new int[] {
        hue - 2, hue - 1, hue, hue + 1};
    for (int x = 0; x < hueArray.length; x++) {
      hueArray[x] = (hueArray[x] < 0) ? hueArray[x] + 360 : hueArray[x];
    }
    return hueArray;
  }

  private void calculateBoundaryHLCArray() {
    for (int hIndex = 0; hIndex < HLevel; hIndex++) {
      int hue = (int)this.getHue(hIndex);
      int[] hueArray = getHueArray(hue);
      for (int lIndex = 1; lIndex < LLevel - 1; lIndex++) {
        int lightness = (int)this.getLightness(lIndex);
        boundaryHLCArray[hIndex][lIndex] = getBoundaryChroma(hue, lightness,
            hueArray);
      }
    }
  }

}
