package shu.cms.gma.gbd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.math.*;
import shu.cms.gma.gbp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來描述色立體上的色域邊界
 * 是以GamutBoundary2DDescriptor加上3D對映而得
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GamutBoundary3DDescriptor
    extends GamutBoundary2DDescriptor {

  public GamutBoundary3DDescriptor(GamutBoundaryPoint gbp,
                                   FocalPoint.FocalType focalType) {
    //(1)經MFP得到三分區
    super(gbp, focalType);
  }

  /**
   *
   * @param LCh CIELCh
   * @return CIELCh
   */
  public CIELCh getBoundaryLCh(CIELCh LCh) {
//    List<CIELCh> [] nearestHuePlane = gbp.getNearestHuePlane(LCh.h);
    double hue = LCh.h;
    if (100 == LCh.L || 0 == LCh.L) {
      CIELCh boundaryLCh = (CIELCh) LCh.clone();
      boundaryLCh.C = 0;
      return boundaryLCh;
    }
    else if (gbp.isAtPointPlane(hue)) {
      //代表在存在數值的hue平面上,只要以2D的方式解析即可
      return super.getBoundaryLCh(LCh);
    }
    else {
      List<CIELCh> [] nearestHuePlane = gbp.getNearestHuePlane(LCh.h);
      double leftHue = nearestHuePlane[0].get(0).h;
      double rightHue = nearestHuePlane[1].get(0).h;

      CIELCh leftLCh = (CIELCh) LCh.clone();
      leftLCh.h = leftHue;
      CIELCh rightLCh = (CIELCh) LCh.clone();
      rightLCh.h = rightHue;

      CIELCh leftBoundary = super.getBoundaryLCh(leftLCh);
      CIELCh rightBoundary = super.getBoundaryLCh(rightLCh);

      CIELCh[] leftULAndFP = super.get2DUpperLowerNearestAndFocalPoint(
          leftBoundary);
      CIELCh[] rightULAndFP = super.get2DUpperLowerNearestAndFocalPoint(
          rightBoundary);

      CIELCh actualFP = fp.getFocalPoint(LCh, leftULAndFP[2],
                                         rightULAndFP[2]);
      CIELCh cp = getCrossPoint(LCh, actualFP,
                                new CIELCh[] {leftULAndFP[0],
                                leftULAndFP[1]},
                                new CIELCh[] {rightULAndFP[0],
                                rightULAndFP[1]});

      return cp;
    }
  }

  /**
   * 取得三角形
   * @param min3DistIndex int[]
   * @param leftHuePlane List
   * @param rightHuePlane List
   * @return CIELCh[]
   * @deprecated
   */
  protected static CIELCh[] getTriangle(int[] min3DistIndex, List<CIELCh>
      leftHuePlane, List<CIELCh> rightHuePlane) {
    CIELCh[] triangle = new CIELCh[3];
    int index = 0;
    if (min3DistIndex[0] != -1) {
      triangle[index++] = leftHuePlane.get(min3DistIndex[0]);
    }
    if (min3DistIndex[1] != -1) {
      triangle[index++] = leftHuePlane.get(min3DistIndex[1]);
    }
    if (min3DistIndex[2] != -1) {
      triangle[index++] = rightHuePlane.get(min3DistIndex[2]);
    }
    if (min3DistIndex[3] != -1 && index < 3) {
      triangle[index++] = rightHuePlane.get(min3DistIndex[3]);
    }

    return triangle;
  }

  protected static CIELCh[] getTriangle(CIELCh point, CIELCh focalpoint,
                                        CIELCh[] leftNearest,
                                        CIELCh[] rightNearest) {
    CIELCh[] tri0 = new CIELCh[] {
        leftNearest[0], leftNearest[1], rightNearest[0]};
    CIELCh[] tri1 = new CIELCh[] {
        leftNearest[0], leftNearest[1], rightNearest[1]};
    CIELCh[] tri2 = new CIELCh[] {
        rightNearest[0], rightNearest[1], leftNearest[0]};
    CIELCh[] tri3 = new CIELCh[] {
        rightNearest[0], rightNearest[1], leftNearest[1]};
    CIELCh[][] tris = new CIELCh[][] {
        tri0, tri1, tri2, tri3};
    CIELCh[] crosspoints = new CIELCh[4];
    boolean[] inTriangle = new boolean[4];
    //判斷是否全軍覆沒
    boolean hasInside = false;

    for (int x = 0; x < 4; x++) {
      CIELCh[] tri = tris[x];
      crosspoints[x] = CrossPoint.getCrossPoint(point, focalpoint, tri);
      inTriangle[x] = CrossPoint.isInTriangle(crosspoints[x], tri);
      hasInside = hasInside || inTriangle[x];
    }

    double[] dist = getDistance(point, crosspoints);
    if (hasInside) {
      //如果有落在三角型上的點,那把其他沒有落的排除掉
      //最快的方式就是把距離改成超級大
      for (int x = 0; x < 4; x++) {
        dist[x] = inTriangle[x] ? dist[x] : Double.MAX_VALUE;
      }
    }

    int index = Maths.minIndex(dist);
    return tris[index];
  }

  protected int uncertainBoundaryCount = 0;

  /**
   * 找到leftNearest和rightNearest組成的三角形中,形成與point和focalpoint連線的最適交點
   * @param point CIELCh
   * @param focalpoint CIELCh
   * @param twoLeftNearest CIELCh[]
   * @param twoRightNearest CIELCh[]
   * @return CIELCh
   */
  protected CIELCh getCrossPoint(CIELCh point,
                                 CIELCh focalpoint,
                                 CIELCh[] twoLeftNearest,
                                 CIELCh[] twoRightNearest) {
    CIELCh[] tri0 = new CIELCh[] {
        twoLeftNearest[0], twoLeftNearest[1], twoRightNearest[0]};
    CIELCh[] tri1 = new CIELCh[] {
        twoLeftNearest[0], twoLeftNearest[1], twoRightNearest[1]};
    CIELCh[] tri2 = new CIELCh[] {
        twoRightNearest[0], twoRightNearest[1], twoLeftNearest[0]};
    CIELCh[] tri3 = new CIELCh[] {
        twoRightNearest[0], twoRightNearest[1], twoLeftNearest[1]};
    CIELCh[][] tris = new CIELCh[][] {
        tri0, tri1, tri2, tri3};
    CIELCh[] crosspoints = new CIELCh[4];
    boolean[] inTriangle = new boolean[4];
    //判斷是否全軍覆沒
    boolean hasInside = false;

    for (int x = 0; x < 4; x++) {
      CIELCh[] tri = tris[x];
      crosspoints[x] = CrossPoint.getCrossPoint(point, focalpoint, tri);
      inTriangle[x] = CrossPoint.isInTriangle(crosspoints[x], tri);
      hasInside = hasInside || inTriangle[x];
    }

    double[] dist = getDistance(point, crosspoints);
    if (hasInside) {
      //如果有落在三角型上的點,那把其他沒有落的排除掉
      //最快的方式就是把距離改成超級大
      for (int x = 0; x < 4; x++) {
        dist[x] = inTriangle[x] ? dist[x] : Double.MAX_VALUE;
      }
    }
    else {
      uncertainBoundaryCount++;
    }

    int index = Maths.minIndex(dist);
    return crosspoints[index];
  }

  /**
   * 求point與points的距離
   * @param point CIELCh
   * @param points CIELCh[]
   * @return double[]
   */
  protected final static double[] getDistance(CIELCh point, CIELCh[] points) {
    int size = points.length;
    double[] dist = new double[size];
    CIELab pointLab = new CIELab(point);
    for (int x = 0; x < size; x++) {
//      dist[x] = DeltaE.CIEDeltaE(pointLab, new CIELab(points[x]));
      dist[x] = DeltaE.CIE2000DeltaE(pointLab, new CIELab(points[x]));
    }
    return dist;
  }

  /**
   *
   * @param point CIELCh
   * @param focalpoint CIELCh
   * @param leftDistance double[]
   * @param leftHuePlane List
   * @param rightDistance double[]
   * @param rightHuePlane List
   * @return CIELCh[]
   * @deprecated
   */
  protected static CIELCh[] getTriangle(CIELCh point, CIELCh focalpoint,
                                        double[] leftDistance, List<CIELCh>
      leftHuePlane, double[] rightDistance, List<CIELCh> rightHuePlane) {

    int[] leftMin3 = Maths.min3Index(leftDistance);
    int[] rightMin3 = Maths.min3Index(rightDistance);

    List<CIELCh>
        list = new ArrayList<CIELCh> (6);
    list.add(leftHuePlane.get(leftMin3[0]));
    list.add(leftHuePlane.get(leftMin3[1]));
    list.add(leftHuePlane.get(leftMin3[2]));
    list.add(rightHuePlane.get(rightMin3[0]));
    list.add(rightHuePlane.get(rightMin3[1]));
    list.add(rightHuePlane.get(rightMin3[2]));

    for (int x = 0; x < 4; x++) {
      CIELCh p1 = list.get(x);
      for (int y = x + 1; y < 5; y++) {
        CIELCh p2 = list.get(y);
        for (int z = y + 1; z < 6; z++) {
          CIELCh p3 = list.get(z);
          CIELCh[] triangle = new CIELCh[] {
              p1, p2, p3};
          CIELCh cp = CrossPoint.getCrossPoint(point, focalpoint, triangle);
          if (cp != null /*&& isInTriangle(cp, triangle)*/) {
            return triangle;
          }
        }
      }
    }
    return null;
  }

  /**
   * 最小的三個距離的索引值
   * @param leftDist double[]
   * @param rightDist double[]
   * @return int[]
   * @deprecated
   */
  protected static int[] getMin3DistanceIndex(double[] leftDist,
                                              double[] rightDist) {

    int[] leftMin2Index = Maths.min2Index(leftDist);
    int[] rightMin2Index = Maths.min2Index(rightDist);

    int[] distIndex = new int[] {
        leftMin2Index[0], leftMin2Index[1], rightMin2Index[0], rightMin2Index[1]};

    double[] dist = new double[] {
        leftDist[distIndex[0]], leftDist[distIndex[1]],
        rightDist[distIndex[2]], rightDist[distIndex[3]]};
    int maxDistIndex = Maths.maxIndex(dist);
    for (int x = 0; x < dist.length; x++) {
      if (distIndex[x] == maxDistIndex) {
        distIndex[x] = -1;
        break;
      }
    }

    return distIndex;
  }

  public static void main(String[] args) {
    CIELCh p0 = new CIELCh(new double[] {1, 5, .5});
    CIELCh fp = new CIELCh(new double[] {1, 0, .5});

    CIELCh p1 = new CIELCh(new double[] {0, 3, .0});
    CIELCh p2 = new CIELCh(new double[] {3, 3, .0});
    CIELCh p3 = new CIELCh(new double[] {1, 3, .1});
    CIELCh[] tri = new CIELCh[] {
        p1, p2, p3};
    CIELCh cp = CrossPoint.getCrossPoint(p0, fp, tri);
//    cp.h=.5;
    System.out.println(cp);
    boolean r = CrossPoint.isInTriangle(cp, tri);
    System.out.println(r);

//    System.out.println(isInTriangle(new CIELCh(new double[] {.5, .5, 2}),
//                                    new CIELCh[] {
//      new CIELCh(new double[] {0, 0, 0}), new CIELCh(new double[] {1, 0, 0}), new
//          CIELCh(new double[] {.5, 1, 0})
//    }));
  }

  public int getUncertainBoundaryCount() {
    return uncertainBoundaryCount;
  }
}
