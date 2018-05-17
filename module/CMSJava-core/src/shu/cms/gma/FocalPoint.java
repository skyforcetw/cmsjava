package shu.cms.gma;

import java.util.*;

import shu.cms.colorspace.independ.*;
import shu.math.mda.*;
import shu.cms.gma.gbp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 壓縮集中點的運算,藉由kmeans所得
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class FocalPoint {

  /**
   * 用來表示邊緣區域的集中方式.
   * 集中到該區中央或者集中到該區邊緣(也是向著中心區域的邊緣)
   */
  public static enum SideAreaFocalType {
    Center, Boundary;
  }

  /**
   * 對應的方法:
   * Multi 三點集中
   * Single 單點集中
   * None 無集中點(彩度壓縮/裁切)
   */
  public static enum FocalType {
    MultiByKMeans, Single, None, Multi
  }

  /**
   * 集中點的數量
   */
  public final static int FOCAL_POINT_AMOUNT = 3;
  /**
   * 不要更動,供kmeans使用
   */
  protected final static int K = FOCAL_POINT_AMOUNT;

  protected GamutBoundaryPoint gbp;
  protected TheFocalArea[][] focalPointArray;

  protected KMeansClustering kMeansClustering;
  protected SideAreaFocalType sideAreaFocalType;
  protected FocalType focalType;

  private FocalPoint(GamutBoundaryPoint gamutPointDescriptor,
                     FocalType focalType) {
    this.gbp = gamutPointDescriptor;
    this.focalType = focalType;
    sideAreaFocalType = SideAreaFocalType.Center;
    if (focalType == FocalType.MultiByKMeans) {
      focalPointArray = new TheFocalArea[gbp.getHueLevel()][];
    }
  }

  private FocalPoint(FocalType focalType) {
    this.focalType = focalType;
    sideAreaFocalType = SideAreaFocalType.Center;
    if (focalType == FocalType.MultiByKMeans) {
      throw new UnsupportedOperationException(
          "focalType = FocalType.MultiByKMeans");
    }
  }

  /**
   * 主要使用在支援MultiByKMeans的計算方式
   * @param gbp GamutBoundaryPoint
   * @param focalType FocalType
   * @return FocalPoint
   */
  public final static FocalPoint getInstance(GamutBoundaryPoint
                                             gbp,
                                             FocalType focalType) {
    return new FocalPoint(gbp, focalType);
  }

  public final static FocalPoint getInstance(FocalType focalType) {
    return new FocalPoint(focalType);
  }

  protected TheFocalArea[] constantFocalArea;

  /**
   *
   * @param hue double
   * @return double[]
   */
  protected TheFocalArea[] getFocalArea(double hue) {
    switch (focalType) {
      case Single:
        if (constantFocalArea == null) {
          constantFocalArea = produceFocalAreaByL50();
        }
        return constantFocalArea;

      case MultiByKMeans:
        int hueIndex = gbp.getHueIndex(hue);
        TheFocalArea[] focalPoint = focalPointArray[hueIndex];
        if (focalPoint == null) {
          focalPointArray[hueIndex] = produceFocalAreaByKMeans(hue);
          focalPoint = focalPointArray[hueIndex];
        }
        return focalPoint;

      case Multi:
        if (constantFocalArea == null) {
          constantFocalArea = produceFocalAreaByL10L50L90();
        }
        return constantFocalArea;
      default:
        return null;
    }

  }

  protected TheFocalArea noneFocalArea;
  /**
   * 由色相以及明度找到FocalArea
   * @param lightness double
   * @param hue double
   * @return TheFocalArea
   */
  public TheFocalArea getFocalArea(double lightness, double hue) {
    if (focalType == FocalPoint.FocalType.None) {
      if (noneFocalArea == null) {
        noneFocalArea = new TheFocalArea(lightness, 0, 100);
      }
      noneFocalArea.center = lightness;
      return noneFocalArea;
    }
    else {
      TheFocalArea[] focalPointArray = getFocalArea(hue);
      int size = focalPointArray.length;
      for (int x = 0; x < size; x++) {
        TheFocalArea focalPoint = focalPointArray[x];
        if (lightness >= focalPoint.lowerBoundary &&
            lightness <= focalPoint.upperBoundary) {
          return focalPoint;
        }
      }
      return null;
    }
  }

  /**
   * 利用kmeans計算集中點
   * @param hue double
   * @return TheFocalArea[]
   */
  protected TheFocalArea[] produceFocalAreaByKMeans(double hue) {
    int[] amountArray = gbp.getAmount(hue);
    int LLevel = gbp.getLightnessLevel();
    double[][] KMeansData = produceKMeansData(amountArray, LLevel);

    kMeansClustering = new KMeansClustering(KMeansData, K);
    kMeansClustering.clustering();

    double[][] centers = kMeansClustering.getClusterCenters();
    int[] assignmen = kMeansClustering.getAssignment();
    double[] boundary = produceFocalBoundary(KMeansData, assignmen, K);

    TheFocalArea[] focalPointArray = new TheFocalArea[K];
    focalPointArray[0] = new TheFocalArea(centers[0][0], 0, boundary[0]);
    focalPointArray[1] = new TheFocalArea(centers[1][0], boundary[0],
                                          boundary[1]);
    focalPointArray[2] = new TheFocalArea(centers[2][0], boundary[1], 100);
//    if (hue == 194) {
//      System.out.println("");
//    }
    return focalPointArray;
  }

  /**
   * 固定採用L50為壓縮集中點
   * @return TheFocalArea[]
   */
  protected TheFocalArea[] produceFocalAreaByL50() {
    TheFocalArea[] focalPointArray = new TheFocalArea[1];
    focalPointArray[0] = new TheFocalArea(50, 0, 100);
    return focalPointArray;
  }

  protected TheFocalArea[] produceFocalAreaByL10L50L90() {
    TheFocalArea[] focalPointArray = new TheFocalArea[3];
    focalPointArray[0] = new TheFocalArea(10, 0, 20);
    focalPointArray[1] = new TheFocalArea(50, 20, 80);
    focalPointArray[2] = new TheFocalArea(90, 80, 100);
    return focalPointArray;
  }

  /**
   * 計算出邊界
   * @param KMeansData double[][]
   * @param assignment int[]
   * @param k int
   * @return double[]
   */
  protected final static double[] produceFocalBoundary(double[][] KMeansData,
      int[] assignment, int k) {
    double assign = assignment[0];
    int size = assignment.length;
    double[] boundary = new double[k - 1];
    int boundaryIndex = 0;

    for (int x = 1; x < size; x++) {
      if (assignment[x] != assign) {
        assign = assignment[x];
        boundary[boundaryIndex++] = (KMeansData[x][0] + KMeansData[x - 1][0]) /
            2;
      }
    }

    return boundary;
  }

  protected final static double[][] produceKMeansData(int[] amountArray,
      int lightnessStep) {
    int size = amountArray.length;
    double step = ( (double) size) / lightnessStep;
    int totalAmount = 0;

    for (int x = 0; x < size; x++) {
      totalAmount += amountArray[x];
    }
    double[][] KMeansData = new double[totalAmount][1];
    int dataIndex = 0;

    for (int x = 0; x < size; x++) {
      int amount = amountArray[x];
      for (int y = 0; y < amount; y++) {
        KMeansData[dataIndex++][0] = x * step;
      }
    }
    return KMeansData;
  }

  public static void main(String[] args) {
    int[] aa = new int[] {
        3, 5, 3, 7, 1, 4, 3, 1, 6, 8, 3, 7, 11, 3, 9, 8, 9};
    double[][] d = produceKMeansData(aa, aa.length);
    System.out.println(Arrays.deepToString(d));

    KMeansClustering kmc = new KMeansClustering(d, 3);
    kmc.clustering();
    System.out.println(Arrays.deepToString(kmc.getClusterCenters()));
    System.out.println(Arrays.toString(kmc.getCardinality()));
    System.out.println(Arrays.toString(kmc.getAssignment()));

    double[] b = produceFocalBoundary(d, kmc.getAssignment(), 3);
    System.out.println(Arrays.toString(b));

  }

  public SideAreaFocalType
      getSideAreaFocalType() {
    return sideAreaFocalType;
  }

  public class TheFocalArea
      implements FocalArea {
    public TheFocalArea(double center, double lowerBoundary,
                        double upperBoundary
        ) {
      this.center = center;
      this.upperBoundary = upperBoundary;
      this.lowerBoundary = lowerBoundary;
    }

    public double getFocalPoint() {
      if (sideAreaFocalType == SideAreaFocalType.Center) {
        return center;
      }
      else if (sideAreaFocalType == SideAreaFocalType.Boundary) {
        if (lowerBoundary == 0) {
          return upperBoundary;
        }
        else if (upperBoundary == 100) {
          return lowerBoundary;
        }
        else {
          return center;
        }
      }
      /*else if (sideAreaFocalType == null) {
        return center;
             }*/
      throw new IllegalStateException("no FocalPoint exception!");
    }

    public double getUpperBoundary() {
      return upperBoundary;
    }

    public double getLowerBoundary() {
      return lowerBoundary;
    }

    protected double center;
    protected double upperBoundary;
    protected double lowerBoundary;

    public String toString() {
      return "FocalArea[" + lowerBoundary + "/" + center + "/" + upperBoundary +
          "]";
    }
  }

  /**
   * (2)取出P所在分區的點
   * @param list List
   * @param focalArea FocalArea
   * @return List
   */
  public final static List<CIELCh> filter(List<CIELCh> list,
      FocalArea focalArea) {
    int size = list.size();
    List<CIELCh> filter = new ArrayList<CIELCh> ();
    for (int x = 0; x < size; x++) {
      CIELCh LCh = list.get(x);

      if (LCh.L > focalArea.getLowerBoundary() &&
          LCh.L < focalArea.getUpperBoundary()) {
        filter.add(LCh);
      }
    }

    return filter;
  }

  public CIELCh getFocalPoint(FocalArea focalArea, CIELCh LCh) {
    CIELCh focalPoint = new CIELCh();
    focalPoint.h = LCh.h;

    if (focalType == FocalType.None) {
      focalPoint.L = LCh.L;
    }
    else {
      double fp = focalArea.getFocalPoint();
      focalPoint.L = fp;
    }
    return focalPoint;
  }

  /**
   * 以hue做加權計算所得的focalPoint
   * @param LCh CIELCh
   * @param leftFocalPoint CIELCh
   * @param rightFocalPoint CIELCh
   * @return CIELCh
   */
  public CIELCh getFocalPoint(CIELCh LCh, CIELCh leftFocalPoint,
                              CIELCh rightFocalPoint) {
    double hue = LCh.h;
    CIELCh fp = new CIELCh();
    fp.h = hue;

    if (focalType == FocalType.None) {
      fp.L = LCh.L;
    }
    else {
      double[] weight = getWeight(leftFocalPoint.h, hue, rightFocalPoint.h);
      fp.L = getWeightedValue(weight, leftFocalPoint.L, rightFocalPoint.L);
    }
    return fp;
  }

  public final static double getWeightedValue(double[] weight,
                                              double leftValue,
                                              double rightValue) {
    double lw = weight[0];
    double rw = weight[1];
    double w = lw + rw;
    double result = (leftValue * lw + rightValue * rw) / w;
    return result;
  }

  public final static double[] getWeight(double leftHue, double hue,
                                         double rightHue) {
    double hueDiff = Math.abs(leftHue - rightHue);
    double rw = Math.abs(leftHue - hue) / hueDiff;
    double lw = Math.abs(rightHue - hue) / hueDiff;

    return new double[] {
        lw, rw};
  }
}
