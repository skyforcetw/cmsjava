package shu.cms.gma.gbp;

import java.io.*;
import java.util.*;

import shu.cms.colorspace.independ.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.DoubleArray;
import java.util.*;

import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �Ψӱԭz�������I
 *
 * �i�H �W�ߦ�v�� �Ϊ� ProfileColorSpace
 * �i�����I���p��.
 * ��쪺�j�p�N�HD50�U��XYZ���p����.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GamutBoundaryPoint
    implements Serializable {
  public double getLightnessStep() {
    double step = 101. / LLevel;
    return step;
  }

  /**
   * hue�O�_��n��b�㦳����I��hue���W
   * @param hue double
   * @return boolean
   */
  public boolean isAtPointPlane(double hue) {
    return (hue % (360. / HLevel)) == 0;
  }

  public double[] getHueLCArray(double hue) {
    return boundary.getHueLCArray(hue);
  }

  public double[] getNearestHue(double hue) {
    double step = getHueStep();
    double mod = hue % step;

    double lowerHue = hue - mod;
    double upperHue = hue + (step - mod);
    return new double[] {
        lowerHue, upperHue};
  }

  public double getHueStep() {
    double step = 360. / HLevel;
    return step;
  }

  public int getHueIndex(double hue) {
    hue = (hue == 360) ? 0 : hue;
    return (int) Math.floor(hue * (HLevel) / 360.);
  }

  public double[] getLightnessHCArray(double lightness) {
    return null;
  }

//  public static enum Type {
//    RGBCube, Image, RGBSurface
//  }

  public static enum BoundaryType {
    ColorSpace, Image
  }

  private BoundaryType boundaryType;

  public static enum LChType {
    LChab, IPTLCh, CIECAM02JCh
  }

  public final static int DEFAULT_RGB_STEP = 4;
  public final static int DEFAULT_L_LEVEL = 101;
  public final static int DEFAULT_H_LEVEL = 360;

//  public final static void setLChType(LChType lchType) {
//    LChType_ = lchType;
//  }
//
//  protected static LChType LChType_ = LChType.LChab;

  protected ProfileColorSpace profileColorSpace;
  protected DeviceIndependentImage image;

  protected int rgbStep = DEFAULT_RGB_STEP;
  public int LLevel = DEFAULT_L_LEVEL;
  public int HLevel = DEFAULT_H_LEVEL;

  /**
   * �����ɤW���I��C��
   */
//  protected double[][] boundaryHLCArray;

  /**
   * �D�����ɮ�,�ΨӼȦsRGB��
   */
//  protected int[][][] boundaryRGBArray;

  /**
   * �p��v������,�b�@�Ӧ�ۭ��W���P�G�ת������ƶq
   */
//  protected int[][] amountArray;

  protected double minLightness = Double.MAX_VALUE;
  protected double maxLightness = Double.MIN_VALUE;

  /**
   * �p��O�O�_�H�̱���Lightnes�i��
   */
  protected boolean useNearestLightnessIndex = true;
//  protected ChromaticAdaptation chromaticAdaptation;

  /**
   * ø�ϥ�,����ĳ�ϥ�,�]�����O�Ŷ�
   * @return double[][]
   */
  public double[][] getRGBLabBoundary() {
    return boundary.boundaryHLC2RGBLab();
  }

  public double getBoundary(double lightness, double hue) {
//    int L = (int) Math.round(lightness / (101. / LLevel));
//    int h = (int) Math.round(hue / (360. / HLevel));
//    return boundaryHLCArray[h][L];
    return boundary.getBoundary(lightness, hue);
  }

  /**
   * ���o�Phue�̱���HuePlane
   * @param hue double
   * @return List
   */
  public List<CIELCh> getHuePlane(double hue) {
    List<CIELCh> [] LChList = getNearestHuePlane(hue);
    if (LChList.length == 2) {
      //�S�����b�����W, �^�ǤF��ӥ�����data
      return interpolate(LChList[0], LChList[1], hue);
    }
    else if (LChList.length == 1) {
      //���b�����W, �^�ǤF�@�ӥ�����data
//      return getHuePlane(hue, getHueLCArray(hue));
      return LChList[0];
    }
    else {
      return null;
    }
  }

  private List<CIELCh> interpolate(List<CIELCh> list1, List<CIELCh> list2,
      double hue) {
    double hue1 = list1.get(0).h;
    double hue2 = list2.get(0).h;
    int size = list1.size();
    List<CIELCh> result = new ArrayList<CIELCh> (size);
    for (int x = 0; x < size; x++) {
      CIELCh LCh1 = list1.get(x);
      CIELCh LCh2 = list2.get(x);
      double L = Interpolation.linear(hue1, hue2, LCh1.L, LCh2.L, hue);
      double C = Interpolation.linear(hue1, hue2, LCh1.C, LCh2.C, hue);
      CIELCh LCh = new CIELCh(L, C, hue);
      result.add(LCh);
    }
    return result;
  }

  /**
   * ���o�Phue�̱��񪺨��HuePlane
   * @param hue double
   * @return List[]
   */
  @SuppressWarnings( {"unchecked"})
  public List<CIELCh>[] getNearestHuePlane(double hue) {
    return boundary.getNearestHuePlane(hue);
//    if (isAtPointPlane(hue)) {
//      //�Y��n���b�����W, �^�Ǥ@�ӥ�������(���v�֤��S�֧a)
//      return new List[] {
//          getHuePlane(hue, getHueLCArray(hue))};
//    }
//    else {
//      //�Y�S�����b�����W, �N�^�Ǩ�ӥ�������
//      double[] nearestHue = getNearestHue(hue);
//      double lowerHue = nearestHue[0];
//      double upperHue = nearestHue[1];
//
//      return new List[] {
//          getHuePlane(lowerHue, getHueLCArray(lowerHue)),
//          getHuePlane(upperHue, getHueLCArray(upperHue))};
//    }
  }

  public double getMinLightness() {
    return minLightness;
  }

  public double getMaxLightness() {
    return maxLightness;
  }

  public int getLightnessLevel() {
    return LLevel;
  }

  public int getHueLevel() {
    return HLevel;
  }

  public GamutBoundaryPoint(ProfileColorSpace profileColorSpace) {
    this(profileColorSpace, DEFAULT_RGB_STEP, DEFAULT_L_LEVEL, DEFAULT_H_LEVEL);
  }

  protected GamutBoundaryPoint(ProfileColorSpace profileColorSpace, int rgbStep,
                               int LLevel, int HLevel) {
    this.profileColorSpace = profileColorSpace;
    init(rgbStep, LLevel, HLevel);
  }

  public GamutBoundaryPoint(DeviceIndependentImage deviceIndependentImage) {
    this(deviceIndependentImage, DEFAULT_RGB_STEP, DEFAULT_L_LEVEL,
         DEFAULT_H_LEVEL);
  }

  protected GamutBoundaryPoint(DeviceIndependentImage deviceIndependentImage,
                               int rgbStep, int LLevel, int HLevel) {
    this.image = deviceIndependentImage;
    this.boundaryType = BoundaryType.Image;
    init(rgbStep, LLevel, HLevel);
  }

  private Boundary boundary;
  public void setBoundary(Boundary boundary) {
    this.boundary = boundary;
  }

  protected void init(int rgbStep, int LLevel, int HLevel) {
    this.rgbStep = rgbStep;
    this.LLevel = LLevel;
    this.HLevel = HLevel;
    boundary = new RGBSurface(this);

  }

//  protected GamutBoundaryPoint(int rgbStep, int LLevel, int HLevel) {
//    this.rgbStep = rgbStep;
//    this.LLevel = LLevel;
//    this.HLevel = HLevel;
//    boundary = new RGBSurface(this);
////    boundary = new RGBSurfaceAdvanced(this);
//  }

  /**
   * ���̤j�m��
   * @param pcs ProfileColorSpace
   * @return double
   */
  protected final static double determineMaxChroma(ProfileColorSpace pcs) {
    CIEXYZ r = new CIEXYZ(pcs.toPCSCIEXYZValues(new double[] {1, 0, 0}),
                          pcs.getPCSReferenceWhite());
    CIEXYZ g = new CIEXYZ(pcs.toPCSCIEXYZValues(new double[] {0, 1, 0}),
                          pcs.getPCSReferenceWhite());
    CIEXYZ b = new CIEXYZ(pcs.toPCSCIEXYZValues(new double[] {0, 0, 1}),
                          pcs.getPCSReferenceWhite());
    CIEXYZ white = pcs.getPCSReferenceWhite();
    CIELab r_Lab = CIELab.fromXYZ(r, white);
    CIELab g_Lab = CIELab.fromXYZ(g, white);
    CIELab b_Lab = CIELab.fromXYZ(b, white);
    CIELCh r_LCh = new CIELCh(r_Lab);
    CIELCh g_LCh = new CIELCh(g_Lab);
    CIELCh b_LCh = new CIELCh(b_Lab);
    return Maths.max(new double[] {r_LCh.C, g_LCh.C, b_LCh.C}) + 1;
  }

  /**
   * �HRGB�p����j�p,�����ɱN�i�णsmooth.
   * ����ĳ�ϥ�
   */
  public void calculateGamut() {
    boundary.calculate();

    if (PATCH_BOUNDARY) {
      if (PATCH_BOUNDARY_IN_CONVEX_HULL) {
        boundary.doBoundaryConvexHull();
      }
      boundary.interpolateBoundary();
      boundary.smoothHuePlaneBoundaryBySlope();
      boundary.smoothHuePlaneBoundaryDouble();
      boundary.smoothLightnessPlaneBoundaryDouble();

    }
  }

  /**
   * �O�_�ץ�boundary
   */
  protected final static boolean PATCH_BOUNDARY = true;
  protected final static boolean PATCH_BOUNDARY_IN_CONVEX_HULL = false;

  /**
   * ���o�@�i�v��,�b�@�Ӧ�ۭ��W���P�G�ת������ƶq
   * @param hue double
   * @return int[]
   */
  public int[] getAmount(double hue) {
    return boundary.getAmount(hue);
//    if (type != Type.Image) {
//      throw new UnsupportedOperationException();
//    }
//    return amountArray[this.getHueIndex(hue)];
  }

  public CIEXYZ getReferenceWhite() {
    switch (boundaryType) {
      case ColorSpace:
        return profileColorSpace.getReferenceWhite();
      case Image:
        return image.getReferenceWhite();
      default:
        return null;
    }
  }

  /**
   *
   * @return double
   */
  public double getVolume() {
    return boundary.getVolume();
  }

  private final static boolean isValidRGBValues(double[] RGBValues) {
    if (RGBValues == null) {
      return false;
    }
    else {
      return RGBValues[0] >= 0 && RGBValues[0] <= 1 && RGBValues[1] >= 0 &&
          RGBValues[1] <= 1 && RGBValues[2] >= 0 && RGBValues[2] <= 1;
    }
  }

}

abstract class Boundary {

  protected double[] getLChValues(double[] rgbValues, CIEXYZ referenceWhite) {
    double[] LChValues = vendor.getLChValues(rgbValues, referenceWhite);
    return LChValues;
  }

  /**
   * �ثe��volumn���p��O���~��
   * @return double
   */
  protected double getVolume() {
    return DoubleArray.sum(DoubleArray.sum(boundaryHLCArray));
  }

  /**
   * @deprecated
   */
  protected void smoothLightnessPlaneBoundaryBySlope() {
    for (int L = 1; L < LLevel - 1; L++) {
      double lastSlope = Double.NaN;
      int changeHIndex = -1;

      //========================================================================
      // ���V
      //========================================================================
      for (int H = 1; H < HLevel - 1; H++) {
        double slope = boundaryHLCArray[H][L] - boundaryHLCArray[H - 1][L];
        if (Double.isNaN(lastSlope)) {
          lastSlope = slope;
          continue;
        }

        if (slope * lastSlope < 0 && changeHIndex == -1) {
          //�W�ɬ�M�ܤU���Ϊ̤U����M�ܤW��
          changeHIndex = H;
        }
        else if (changeHIndex != -1) {

          if (boundaryHLCArray[H][L] > boundaryHLCArray[changeHIndex][L]) {
            double[] xn = new double[] {
                //�W��
                changeHIndex - 2, changeHIndex - 1, H, H + 1};
            double[] yn = new double[] {
                boundaryHLCArray[ (int) xn[0]][L],
                boundaryHLCArray[ (int) xn[1]][L],
                boundaryHLCArray[ (int) xn[2]][L],
                boundaryHLCArray[ (int) xn[3]][L]};
            for (int x = changeHIndex; x < H; x++) {
              boundaryHLCArray[x][L] = Interpolation.lagrange(xn, yn, x);
            }
            changeHIndex = -1;
          }
        }

        lastSlope = slope;
      }
      //========================================================================

      lastSlope = Double.NaN;
      changeHIndex = -1;

      //========================================================================
      // �f�V
      //========================================================================
      for (int H = HLevel - 2; H > 0; H--) {
        double slope = boundaryHLCArray[H][L] - boundaryHLCArray[H + 1][L];
        if (Double.isNaN(lastSlope)) {
          lastSlope = slope;
          continue;
        }

        if (slope * lastSlope < 0 && changeHIndex == -1) {
          //�W�ɬ�M�ܤU���Ϊ̤U����M�ܤW��
          changeHIndex = L;
        }
        else if (changeHIndex != -1) {

          if (boundaryHLCArray[H][L] > boundaryHLCArray[changeHIndex][L]) {
            double[] xn = new double[] {
                changeHIndex + 2, changeHIndex + 1, H, H - 1};
            double[] yn = new double[] {
                boundaryHLCArray[ (int) xn[0]][L],
                boundaryHLCArray[ (int) xn[1]][L],
                boundaryHLCArray[ (int) xn[2]][L],
                boundaryHLCArray[ (int) xn[3]][L]};
            for (int x = changeHIndex; x > H; x--) {
              boundaryHLCArray[x][L] = Interpolation.lagrange(xn, yn, x);
            }
            changeHIndex = -1;
          }
        }

        lastSlope = slope;
      }
      //========================================================================
    }

  }

  protected void smoothLightnessPlaneBoundary() {
    for (int L = 0; L < LLevel; L++) {
      for (int H = 0; H < HLevel; H++) {
        double xn0 = H - 2;
        double xn1 = H - 1;
        double xn2 = H + 1;
        double xn3 = H + 2;

        xn0 = xn0 < 0 ? HLevel + xn0 : xn0;
        xn1 = xn1 < 0 ? HLevel + xn1 : xn1;
        xn2 = xn2 < 0 ? HLevel + xn2 : xn2;
        xn3 = xn3 < 0 ? HLevel + xn3 : xn3;

        xn0 = xn0 >= HLevel ? xn0 - HLevel : xn0;
        xn1 = xn1 >= HLevel ? xn1 - HLevel : xn1;
        xn2 = xn2 >= HLevel ? xn2 - HLevel : xn2;
        xn3 = xn3 >= HLevel ? xn3 - HLevel : xn3;

        double[] xn = new double[] {
            xn0, xn1, xn2, xn3};
        double[] yn = new double[] {
            boundaryHLCArray[ (int) xn0][L],
            boundaryHLCArray[ (int) xn1][L],
            boundaryHLCArray[ (int) xn2][L],
            boundaryHLCArray[ (int) xn3][L]};
        double interpolate = Interpolation.interpolate(xn, yn, H,
            Interpolation.Algo.Lagrange);
        boundaryHLCArray[H][L] = boundaryHLCArray[H][L] < interpolate ?
            interpolate : boundaryHLCArray[H][L];
      }
    }
  }

  protected void smoothLightnessPlaneBoundaryDouble() {
    smoothLightnessPlaneBoundary();
    smoothLightnessPlaneBoundary();
  }

  /**
   * �w��ײv,�Nboundary�����ƳB�z
   */
  protected void smoothHuePlaneBoundaryBySlope() {
    for (int H = 0; H < HLevel; H++) {
      double lastSlope = Double.NaN;
      int changeLIndex = -1;
      for (int L = 1; L < LLevel - 1; L++) {
        double slope = boundaryHLCArray[H][L] - boundaryHLCArray[H][L - 1];
        if (Double.isNaN(lastSlope)) {
          lastSlope = slope;
          continue;
        }

        if (slope * lastSlope < 0 && changeLIndex == -1) {
          //�W�ɬ�M�ܤU���Ϊ̤U����M�ܤW��
          changeLIndex = L;
        }
        else if (changeLIndex != -1) {

          if (boundaryHLCArray[H][L] > boundaryHLCArray[H][changeLIndex]) {
            double[] xn = new double[] {
                //�W��
                changeLIndex - 2, changeLIndex - 1, L, L + 1};
            double[] yn = new double[] {
                boundaryHLCArray[H][ (int) xn[0]],
                boundaryHLCArray[H][ (int) xn[1]],
                boundaryHLCArray[H][ (int) xn[2]],
                boundaryHLCArray[H][ (int) xn[3]]};
            for (int x = changeLIndex; x < L; x++) {
              boundaryHLCArray[H][x] = Interpolation.lagrange(xn, yn, x);
            }
            changeLIndex = -1;
          }
        }

        lastSlope = slope;
      }

      lastSlope = Double.NaN;
      changeLIndex = -1;
      for (int L = LLevel - 2; L > 0; L--) {
        double slope = boundaryHLCArray[H][L] - boundaryHLCArray[H][L + 1];
        if (Double.isNaN(lastSlope)) {
          lastSlope = slope;
          continue;
        }

        if (slope * lastSlope < 0 && changeLIndex == -1) {
          //�W�ɬ�M�ܤU���Ϊ̤U����M�ܤW��
          changeLIndex = L;
        }
        else if (changeLIndex != -1) {

          if (boundaryHLCArray[H][L] > boundaryHLCArray[H][changeLIndex]) {
            double[] xn = new double[] {
                changeLIndex + 2, changeLIndex + 1, L, L - 1};
            double[] yn = new double[] {
                boundaryHLCArray[H][ (int) xn[0]],
                boundaryHLCArray[H][ (int) xn[1]],
                boundaryHLCArray[H][ (int) xn[2]],
                boundaryHLCArray[H][ (int) xn[3]]};
            for (int x = changeLIndex; x > L; x--) {
              boundaryHLCArray[H][x] = Interpolation.lagrange(xn, yn, x);
            }
            changeLIndex = -1;
          }
        }

        lastSlope = slope;
      }

    }

  }

  /**
   * �s���⦸��ɥ���
   */
  protected void smoothHuePlaneBoundaryDouble() {
    smoothHuePlaneBoundary();
    smoothHuePlaneBoundary();
  }

  /**
   * �Nboundary�����ƳB�z
   */
  protected void smoothHuePlaneBoundary() {
    for (int H = 0; H < HLevel; H++) {
      for (int L = 2; L < LLevel - 2; L++) {
        double[] xn = new double[] {
            //�W��
            L - 2, L - 1, L + 1, L + 2};
        double[] yn = new double[] {
            boundaryHLCArray[H][ (int) xn[0]],
            boundaryHLCArray[H][ (int) xn[1]],
            boundaryHLCArray[H][ (int) xn[2]],
            boundaryHLCArray[H][ (int) xn[3]]};
        double interpolate = Interpolation.interpolate(xn, yn, L,
            Interpolation.Algo.Lagrange);
        boundaryHLCArray[H][L] = boundaryHLCArray[H][L] < interpolate ?
            interpolate : boundaryHLCArray[H][L];
      }
    }
  }

  /**
   * �ĥαNlagrange��boundary�����ȳB�z
   */
  protected void interpolateBoundary() {
//    for (int H = 0; H < HLevel; H++) {
//      for (int L = 1; L < LLevel - 1; L++) {
//        System.out.print(boundaryHLCArray[H][L] + " ");
//      }
//      System.out.println("");
//    }

    for (int H = 0; H < HLevel; H++) {
      for (int L = 1; L < LLevel - 1; L++) {

        int[] rgb = boundaryRGBArray[H][L];
        //���RGB��0���ƭ�
        if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0) {
          int tail = L + 1;
          for (; tail < LLevel - 1; tail++) {
            int[] rgb2 = boundaryRGBArray[H][tail];
            //���Ĥ@��RGB����0���ƭ�
            if (rgb2[0] != 0 && rgb2[1] != 0 && rgb2[2] != 0) {
              break;
            }
          }

          //�H�Ĥ@��RGB����0���ƭȥH�Ϋe�@�Ӽƭȶi�椺��,�ɻ��o�Ӫů�
          //�ѩ�O�bL�b�W�M��, �ҥH�O�@�����u�ʤ���

          //���F���o�󥭶������G, �i�H�[�JHue�b�W����, �M��N��Ӥ������G�A����,
          //���ӥi�H���o�̾A�����G
          double[] xn = new double[] {
              L - 1, tail};
          double[] yn = new double[] {
              boundaryHLCArray[H][L - 1], boundaryHLCArray[H][tail]};
          boundaryHLCArray[H][L] = Interpolation.lagrange(xn, yn, L);

        }
      }
    }

//    for (int H = 0; H < HLevel; H++) {
//      for (int L = 1; L < LLevel - 1; L++) {
//        System.out.print(boundaryHLCArray[H][L] + " ");
//      }
//      System.out.println("");
//    }

  }

  protected double getBoundary(double lightness, double hue) {
    int L = (int) Math.round(lightness / (101. / LLevel));
    int h = (int) Math.round(hue / (360. / HLevel));
    return boundaryHLCArray[h][L];
  }

  /**
   * ���o�@�i�v��,�b�@�Ӧ�ۭ��W���P�G�ת������ƶq
   * @param hue double
   * @return int[]
   */
  protected int[] getAmount(double hue) {
    return amountArray[parent.getHueIndex(hue)];
  }

  /**
   * ���o�Phue�̱��񪺨��HuePlane
   * @param hue double
   * @return List[]
   */
  @SuppressWarnings( {"unchecked"})
  protected List<CIELCh>[] getNearestHuePlane(double hue) {
    if (parent.isAtPointPlane(hue)) {
      //�Y��n���b�����W, �^�Ǥ@�ӥ�������(���v�֤��S�֧a)
      return new List[] {
          getHuePlane(hue, getHueLCArray(hue))};
    }
    else {
      //�Y�S�����b�����W, �N�^�Ǩ�ӥ�������
      double[] nearestHue = parent.getNearestHue(hue);
      double lowerHue = nearestHue[0];
      double upperHue = nearestHue[1];

      return new List[] {
          getHuePlane(lowerHue, getHueLCArray(lowerHue)),
          getHuePlane(upperHue, getHueLCArray(upperHue))};
    }
  }

  protected List<CIELCh> getHuePlane(double hue, double[] LCArray) {
    int size = LCArray.length;
    List<CIELCh> huePlane = new ArrayList<CIELCh> (size);
    double[] LChValues = new double[3];

    for (int x = 0; x < LLevel; x++) {
      double lightness = getLightness(x);
      LChValues[0] = lightness;
      LChValues[1] = LCArray[x];
      LChValues[2] = hue;
      huePlane.add(new CIELCh(LChValues));
    }
    return huePlane;
  }

  /**
   * ø�ϥ�,����ĳ�ϥ�,�]�����O�Ŷ�:P
   * @return double[][]
   */
  protected final double[][] boundaryHLC2RGBLab() {
    int size = HLevel * LLevel;
    int index = 0;
    double[][] boundaryRGBLab = new double[size][6];
    double[] LChValues = new double[3];

    for (int h = 0; h < HLevel; h++) {
      double hue = getHue(h);
      for (int L = 0; L < LLevel; L++) {
        boundaryRGBLab[index][0] = boundaryRGBArray[h][L][0];
        boundaryRGBLab[index][1] = boundaryRGBArray[h][L][1];
        boundaryRGBLab[index][2] = boundaryRGBArray[h][L][2];

        double lightness = getLightness(L);
        double chroma = boundaryHLCArray[h][L];
        LChValues[0] = lightness;
        LChValues[1] = chroma;
        LChValues[2] = hue;
        double[] LabValues = CIELCh.toLabValues(LChValues);
        System.arraycopy(LabValues, 0, boundaryRGBLab[index], 3, 3);
        index++;
      }
    }
    return boundaryRGBLab;
  }

  /**
   * @deprecated
   */
  protected void doBoundaryConvexHull() {
    //�����ͮy���I
    double[][] boundaryLab = getBoundaryLabValues();
    ConvexHull convexHull = new ConvexHull(boundaryLab);
    boundaryHLCArray = convexHull.getBoundaryHLCArray(this);
  }

  public final double getLightness(int lightnessIndex) {
    return lightnessIndex * (101. / LLevel);
  }

  protected final double[][] getBoundaryLabValues() {
    int size = HLevel * LLevel;
    int index = 0;
    double[][] boundaryLab = new double[size][];
    double[] LChValues = new double[3];

    for (int x = 0; x < HLevel; x++) {
      double hue = getHue(x);
      for (int y = 0; y < LLevel; y++) {

        double lightness = getLightness(y);
        double chroma = boundaryHLCArray[x][y];

        LChValues[0] = lightness;
        LChValues[1] = chroma;
        LChValues[2] = hue;
        double[] LabValues = CIELCh.toLabValues(LChValues);
        boundaryLab[index] = LabValues;

        index++;
      }
    }

    return boundaryLab;
  }

  /**
   *
   * @param hue double
   * @return double[]
   * @todo L gma �B�b���hue������?
   */
  protected double[] getHueLCArray(double hue) {
    int hueIndex = parent.getHueIndex(hue);
    double[] hueLCArray = boundaryHLCArray[hueIndex];
    return hueLCArray;
  }

  protected double[] getNearestLightness(double lightness) {
    double step = parent.getLightnessStep();
    double mod = lightness % step;

    double lowerLightness = lightness - mod;
    double upperLightness = lightness + (step - mod);
    return new double[] {
        lowerLightness, upperLightness};
  }

  public final double getHue(int hueIndex) {
    return hueIndex * (360. / HLevel);
  }

  protected double[] getLightnessHCArray(double lightness) {
    int LIndex = getLightnessIndex(lightness);
    double[] HCArray = new double[HLevel];
    for (int h = 0; h < HLevel; h++) {
      double hue = getHue(h);
      double[] LCArray = getHueLCArray(hue);
      HCArray[h] = LCArray[LIndex];
    }
    return HCArray;
  }

  /**
   *
   * @param lightness double
   * @return int
   */
  protected int getLightnessIndex(double lightness) {
    int result = (int) Math.round(lightness * LLevel / 101.);
    result = result == LLevel ? LLevel - 1 : result;
    return result;
  }

  protected int[] getNearestLightnessIndex(double lightness) {
    double step = parent.getLightnessStep();
    double remainder = lightness % step;
    if (remainder == 0) {
      return new int[] {
          getLightnessIndex(lightness)};
    }
    else {
      return new int[] {
          getLightnessIndex(lightness - remainder),
          getLightnessIndex(lightness + (step - remainder))};
    }
  }

  public final GamutBoundaryPoint parent;
  private LChVendor vendor;
  public void setLChVendor(LChVendor vendor) {
    this.vendor = vendor;
  }

  /**
   * �����ɤW���I��C��
   */
  protected double[][] boundaryHLCArray;

  /**
   * �D�����ɮ�,�ΨӼȦsRGB��
   */
  protected int[][][] boundaryRGBArray;

  /**
   * �p��v������,�b�@�Ӧ�ۭ��W���P�G�ת������ƶq
   */
  protected int[][] amountArray;
  protected final int HLevel, LLevel;
  protected Boundary(GamutBoundaryPoint parent) {
    this.parent = parent;
    vendor = new LChabVendor(parent.profileColorSpace);

    this.HLevel = parent.HLevel;
    this.LLevel = parent.LLevel;
    //��l��
    boundaryHLCArray = new double[HLevel][LLevel];
    amountArray = new int[HLevel][LLevel];
    boundaryRGBArray = new int[HLevel][LLevel][3];
  }

  protected abstract void calculate();

//  double[][] getBoundaryHLCArray(Boundary boundary) {
//    point3dArray = getPoint3dArray(boundaryLabValues);
//    quickHull.build(point3dArray);
//
//    List<CIELab[]> faceList = produceFaceList(quickHull);
//    int faceSize = faceList.size();
//
//    int HLevel = boundary.parent.getHueLevel();
//    int LLevel = boundary.parent.getLightnessLevel();
//    double[][] HLCArray = new double[HLevel][LLevel];
//    CIELCh p0 = new CIELCh(), p4 = new CIELCh();
//
//    for (int H = 0; H < HLevel; H++) {
//      double hue = boundary.getHue(H);
//      for (int L = 0; L < LLevel; L++) {
//        double lightness = boundary.getLightness(L);
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
//          if (hueDiff < boundary.parent.getHueStep() && cp.L >= 0 &&
//              cp.L <= 100 &&
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
//  }

}
