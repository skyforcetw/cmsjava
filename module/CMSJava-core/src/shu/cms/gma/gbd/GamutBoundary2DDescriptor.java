package shu.cms.gma.gbd;

import java.util.*;
import javax.vecmath.*;

import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.math.*;
import shu.math.geometry.*;
import shu.cms.gma.gbp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來描述每一個色相頁上的色域邊界
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GamutBoundary2DDescriptor
    extends GamutBoundaryDescriptor {

  public GamutBoundary2DDescriptor(GamutBoundaryPoint gbp,
                                   FocalPoint.FocalType focalType) {
    super(gbp, focalType);
  }

  /**
   * 取得色相頁上 上面以及下面最靠近的兩點 還有 集中點
   * @param LCh CIELCh
   * @return CIELCh[]
   */
  protected CIELCh[] get2DUpperLowerNearestAndFocalPoint(CIELCh LCh) {
    //取出色相平面
    List<CIELCh> huePlane = getHuePlane(LCh.h);

    //取出集中區
    FocalArea focalArea = fp.getFocalArea(LCh.L, LCh.h);

    //過濾出分區內的色點
    List<CIELCh> filter = fp.filter(huePlane, focalArea);
    CIELCh focalPoint = fp.getFocalPoint(focalArea, LCh);
    CIELCh[] upperAndLower = get2DUpperAndLowerNearest(LCh, focalPoint,
        filter);
    CIELCh[] result = new CIELCh[] {
        upperAndLower[0], upperAndLower[1], focalPoint};
    return result;
  }

  protected CIELCh[] get2DUpperLowerNearestDoubleAndFocalPoint(CIELCh LCh) {
    //取出色相平面
    List<CIELCh> huePlane = getHuePlane(LCh.h);

    //取出集中區
    FocalArea focalArea = fp.getFocalArea(LCh.L, LCh.h);

    //過濾出分區內的色點
    huePlane = fp.filter(huePlane, focalArea);
    CIELCh focalPoint = fp.getFocalPoint(focalArea, LCh);
    CIELCh[] upperAndLower = get2DUpperAndLowerNearestDouble(LCh, focalPoint,
        huePlane);
    CIELCh[] result = new CIELCh[] {
        upperAndLower[0], upperAndLower[1], upperAndLower[2], upperAndLower[3],
        focalPoint};
    return result;
  }

  public CIELCh getBoundaryLCh(CIELCh LCh) {
    CIELCh[] ULAndFP = get2DUpperLowerNearestAndFocalPoint(LCh);
    CIELCh crossPoint = get2DCrossPoint(LCh, ULAndFP[2],
                                        ULAndFP[0],
                                        ULAndFP[1]);
    if (crossPoint.C < 0 || crossPoint.L < 0 || crossPoint.L > 100) {
      crossPoint = LCh;
    }

    return crossPoint;
  }

  /**
   * 計算00與01的直線跟10與11的直線的交點
   * @param p00 CIELCh
   * @param p01 CIELCh
   * @param p10 CIELCh
   * @param p11 CIELCh
   * @return CIELCh
   */
  protected static CIELCh get2DCrossPoint(CIELCh p00, CIELCh p01, CIELCh p10,
                                          CIELCh p11) {
    Point2d gp00 = new Point2d(p00.C, p00.L);
    Point2d gp01 = new Point2d(p01.C, p01.L);
    Point2d gp10 = new Point2d(p10.C, p10.L);
    Point2d gp11 = new Point2d(p11.C, p11.L);

    LinearFunction lf0 = LinearFunction.getInstance(gp00, gp01);
    LinearFunction lf1 = LinearFunction.getInstance(gp10, gp11);
    Point2d crossPoint = Geometry.getCrossPoint(lf0, lf1);
    CIELCh result = new CIELCh(new double[] {crossPoint.y, crossPoint.x, p00.h});
    return result;
  }

  /**
   * 求point與focalArea的集中點所構成的直線,與所有points之間的距離
   * (L=0或者L=100者不取,所以將距離設定成最大)
   * @param point CIELCh
   * @param focalPoint CIELCh
   * @param points List
   * @return double[] 依照points的順序排列的距離
   */
  protected static double[] get2DDistance(CIELCh point, CIELCh focalPoint,
                                          List<CIELCh>
      points) {
    int size = points.size();
    double[] d = new double[size];

    Point2d p0 = new Point2d(focalPoint.C, focalPoint.L);
    Point2d p1 = new Point2d(point.C, point.L);
    //lf代表point與集中點相連的一條直線方程式
    LinearFunction lf = LinearFunction.getInstance(p0, p1);

    for (int x = 0; x < size; x++) {
      CIELCh LCh = points.get(x);
      if (LCh.C == 0 || LCh.L == 0 || LCh.L == 100) {
        d[x] = Double.MAX_VALUE;
      }
      else {
        Point2d p2 = new Point2d(LCh.C, LCh.L);
        d[x] = Geometry.getDistance(p2, lf);
      }

    }
    return d;
  }

  /**
   * 取得色相頁上 上面以及下面最靠近的兩點
   * @param point CIELCh
   * @param focalPoint CIELCh
   * @param points List
   * @return CIELCh[]
   */
  protected static CIELCh[] get2DUpperAndLowerNearest(CIELCh point,
      CIELCh focalPoint, List<CIELCh> points) {

    int size = points.size();

    Point2d p0 = new Point2d(focalPoint.C, focalPoint.L);
    Point2d p1 = new Point2d(point.C, point.L);
    //lf代表point與集中點相連的一條直線方程式
    LinearFunction lf = LinearFunction.getInstance(p0, p1);

    List<CIELCh> upperList = new ArrayList<CIELCh> ();
    List<CIELCh> lowerList = new ArrayList<CIELCh> ();

    //以point到focal point的直線成一線性方程式
    for (int x = 1; x < size - 1; x++) {
      CIELCh LCh = points.get(x); //邊界的所有點
      Point2d p2 = new Point2d(LCh.C, LCh.L); //邊界點
      if (lf.isLower(p2)) { //線如果低於該點
        upperList.add(LCh); //代表這個邊界點亮度高於point
      }
      else if (lf.isUpper(p2)) { //線如果高於該點
        lowerList.add(LCh); //代表這個邊界點亮度低於point
      }
    }

    double[] upperDist = get2DDistance(point, focalPoint, upperList);
    double[] lowerDist = get2DDistance(point, focalPoint, lowerList);

    if (upperList.size() == 0 || lowerList.size() == 0) {
      if (upperList.size() != 0) {
        int[] index = Maths.min2Index(upperDist);
        return new CIELCh[] {
            upperList.get(index[0]), upperList.get(index[1])};
      }
      else {
        int[] index = Maths.min2Index(lowerDist);
        return new CIELCh[] {
            lowerList.get(index[0]), lowerList.get(index[1])};
      }

    }
    else {
      int upperIndex = Maths.minIndex(upperDist);
      int lowerIndex = Maths.minIndex(lowerDist);
      return new CIELCh[] {
          upperList.get(upperIndex), lowerList.get(lowerIndex)};
    }

  }

  protected static CIELCh[] get2DUpperAndLowerNearestDouble(CIELCh point,
      CIELCh focalPoint, List<CIELCh> points) {

    int size = points.size();

    Point2d p0 = new Point2d(focalPoint.C, focalPoint.L);
    Point2d p1 = new Point2d(point.C, point.L);
    //lf代表point與集中點相連的一條直線方程式
    LinearFunction lf = LinearFunction.getInstance(p0, p1);

    List<CIELCh> upperList = new ArrayList<CIELCh> ();
    List<CIELCh> lowerList = new ArrayList<CIELCh> ();

    for (int x = 1; x < size - 1; x++) {
      CIELCh LCh = points.get(x);
      Point2d p2 = new Point2d(LCh.C, LCh.L);
      if (lf.isLower(p2)) {
        upperList.add(LCh);
      }
      else if (lf.isUpper(p2)) {
        lowerList.add(LCh);
      }
    }

    double[] upperDist = get2DDistance(point, focalPoint, upperList);
    double[] lowerDist = get2DDistance(point, focalPoint, lowerList);

    if (upperList.size() == 0 || lowerList.size() == 0) {
      if (upperList.size() != 0) {
        int[] index = Maths.min4Index(upperDist);
        return new CIELCh[] {
            upperList.get(index[0]), upperList.get(index[1]),
            upperList.get(index[2]), upperList.get(index[3])};
      }
      else {
        int[] index = Maths.min4Index(lowerDist);
        return new CIELCh[] {
            lowerList.get(index[0]), lowerList.get(index[1]),
            lowerList.get(index[2]), lowerList.get(index[3])};
      }

    }
    else {
      int[] upperIndex = Maths.min2Index(upperDist);
      int[] lowerIndex = Maths.min2Index(lowerDist);
      return new CIELCh[] {
          upperList.get(upperIndex[0]), upperList.get(upperIndex[1]),
          lowerList.get(lowerIndex[0]), lowerList.get(lowerIndex[1])};
    }

  }
}
