package shu.cms.gma.gbp;

import java.util.*;

import quickhull3d.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.cms.plot.*;

///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 凸包演算法
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ConvexHull {
  protected QuickHull3D quickHull = new QuickHull3D();
  protected double[][] boundaryLabValues;
  protected Point3d[] point3dArray;

  public ConvexHull(double[][] boundaryLabValues) {
    this.boundaryLabValues = boundaryLabValues;
  }

  /**
   *
   * @param quickHull QuickHull3D
   * @return List
   */
  protected final static List<CIELab[]> produceFaceList(QuickHull3D quickHull) {
    Point3d[] vertices = quickHull.getVertices();
    int[][] faceIndices = quickHull.getFaces();

    int verticesSize = vertices.length;
    CIELab[] verticesCIELab = new CIELab[verticesSize];

    Plot3D p = Plot3D.getInstance();
    p.setVisible(true);

    for (int x = 0; x < verticesSize; x++) {
      Point3d v = vertices[x];
      CIELab Lab = new CIELab();
      Lab.L = v.x;
      Lab.a = v.y;
      Lab.b = v.z;
      verticesCIELab[x] = Lab;

      p.addColorSpace(null, Lab);
    }

    int faceSize = faceIndices.length;
    List<CIELab[]> faceList = new ArrayList<CIELab[]> (faceSize);
    for (int x = 0; x < faceSize; x++) {
      int[] indices = faceIndices[x];
      CIELab[] face = new CIELab[3];
      face[0] = verticesCIELab[indices[0]];
      face[1] = verticesCIELab[indices[1]];
      face[2] = verticesCIELab[indices[2]];
      faceList.add(face);
    }

    return faceList;
  }

  public double[][] getBoundaryHLCArray(Boundary boundary) {
    point3dArray = getPoint3dArray(boundaryLabValues);
    quickHull.build(point3dArray);

    List<CIELab[]> faceList = produceFaceList(quickHull);
    int faceSize = faceList.size();

    int HLevel = boundary.parent.getHueLevel();
    int LLevel = boundary.parent.getLightnessLevel();
    double[][] HLCArray = new double[HLevel][LLevel];
    CIELCh p0 = new CIELCh(), p4 = new CIELCh();

    for (int H = 0; H < HLevel; H++) {
      double hue = boundary.getHue(H);
      for (int L = 0; L < LLevel; L++) {
        double lightness = boundary.getLightness(L);
        p0.L = p4.L = lightness;
        p0.h = p4.h = hue;
        p0.C = 100;
        CIELab p0Lab = new CIELab(p0);
        CIELab p4Lab = new CIELab(p4);

        for (int x = 0; x < faceSize; x++) {
          CIELab[] face = faceList.get(x);
          CIELab cp = CrossPoint.getCrossPoint(p0Lab, p4Lab, face);
          CIELCh cpLCh = new CIELCh(cp);
          double hueDiff = Math.abs(p0.h - cpLCh.h);
          if (hueDiff < boundary.parent.getHueStep() && cp.L >= 0 &&
              cp.L <= 100 &&
              cpLCh.C >= 0 && CrossPoint.isInTriangle(cp, face)) {
            HLCArray[H][L] = cpLCh.C;
            break;
          }
        }

      }
    }

    return HLCArray;
  }

  /**
   *
   * @param gbp GamutBoundaryPoint
   * @return double[][]
   * @deprecated
   */
  public double[][] getBoundaryHLCArray(GamutBoundaryPoint gbp) {
    throw new UnsupportedOperationException();
//    point3dArray = getPoint3dArray(boundaryLabValues);
//    quickHull.build(point3dArray);
//
//    List<CIELab[]> faceList = produceFaceList(quickHull);
//    int faceSize = faceList.size();
//
//    int HLevel = gbp.getHueLevel();
//    int LLevel = gbp.getLightnessLevel();
//    double[][] HLCArray = new double[HLevel][LLevel];
//    CIELCh p0 = new CIELCh(), p4 = new CIELCh();
//
//    for (int H = 0; H < HLevel; H++) {
//      double hue = gbp.getHue(H);
//      for (int L = 0; L < LLevel; L++) {
//        double lightness = gbp.getLightness(L);
//        p0.L = p4.L = lightness;
//        p0.h = p4.h = hue;
//        p0.C = 100;
//        CIELab p0Lab = new CIELab(p0);
//        CIELab p4Lab = new CIELab(p4);
//
//        for (int x = 0; x < faceSize; x++) {
//          CIELab[] face = faceList.get(x);
//          CIELab cp = CrossPoint.getCrossPoint(p0Lab, p4Lab, face);
//          CIELCh cpLCh = new CIELCh(cp);
//          double hueDiff = Math.abs(p0.h - cpLCh.h);
//          if (hueDiff < gbp.getHueStep() && cp.L >= 0 && cp.L <= 100 &&
//              cpLCh.C >= 0 && CrossPoint.isInTriangle(cp, face)) {
//            HLCArray[H][L] = cpLCh.C;
//            break;
//          }
//        }
//
//      }
//    }
//
//    return HLCArray;
  }

  protected final static Point3d[] getPoint3dArray(double[][] pointData) {
    int size = pointData.length;
    Point3d[] point3dArray = new Point3d[size];

    for (int x = 0; x < size; x++) {
      double[] d = pointData[x];
      point3dArray[x] = new Point3d(d[0], d[1], d[2]);
    }

    return point3dArray;
  }

}
