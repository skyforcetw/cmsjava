package shu.cms.hvs.cam.ciecam02;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.*;
import shu.math.array.*;
import shu.math.lut.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ��CIECAM02�ӭp��JND Index.
 * ������O�HCIECAM02, �إߤ��ʦ�������׻P�G�ת����Y.
 * �p���N�i�H���ݭn�z�LXYZ�ӶȻ�Y�N�i�H������Lightness, �ϹL�ӥ�P.
 *
 * CIECAM02�p�󤶤JHK����? �z�פW�ӻ�CIECAM02�O���Ҽ{��HK������.
 * ���O��CIECAM02���ӬO�ΨӸѨM V' ���~�����D(�a!? �q����)
 *
 * �D���ʦ���A�p����o�P���ʦ���B�ۦP���פU���G��
 *
 * 1. �����إߤ��ʦ���Ƕ��U, ���׻P�G�ת����Y Lightness<->Luminance
 * 2. �Q��CIECAM02, �HA���G��Y�DA������L. �NA�����קQ�ΨB�J1�����Y, �i�H�o��۹������G��Y'.
 *  Y�PY'���ӬO���ۦP��.
 * 3. �ҥH�D�D���ʦ⪺A��JNDI, ��ڤW�O�D�XY', �A��Y'�a�XJNDIndex.
 * 4. ���P�D���ʦ�, �ۦ����P��Y->JNDIndex, �ϹL��JNDIndex->Y��P.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public final class CIECAM02JNDIndex {
  protected CIECAM02 cam;
  protected double whiteJNDIndex;
  protected CIEXYZ white;
  protected Interpolation1DLUT jndi2JLut;
  protected Interpolation1DLUT Y2JLut;

  public CIECAM02JNDIndex(CIECAM02 cam, CIEXYZ absoluteWhite) {
    this.cam = cam;
    this.white = absoluteWhite;
    init();
  }

  /**
   * ����\��, ����ĳ�ϥ�
   * @param color CIECAM02Color
   * @return double[]
   * @deprecated
   */
  public final double[] getJabcJNDIndex(CIECAM02Color color) {
    double jIndex = getJNDIndex(color.J);
    double aIndex = getJNDIndex(color.J + color.ac) - jIndex;
    double bIndex = getJNDIndex(color.J + color.bc) - jIndex;
    return new double[] {
        jIndex, aIndex, bIndex};
  }

  /**
   * �qJ�ഫ��JND Index (J->JNDI)
   * @param lightness double
   * @return double
   */
  public final double getJNDIndex(double lightness) {
    double value = jndi2JLut.correctValueInRange(lightness);
    return jndi2JLut.getKey(value);
  }

  /**
   * �qJNDIndex���J (JNDI->J)
   * @param JNDIndex double
   * @return double
   */
  public final double getLightness(double JNDIndex) {
    double key = jndi2JLut.correctKeyInRange(JNDIndex);
    return jndi2JLut.getValue(key);
  }

  /**
   * �H���ר��o�����۹������G�׭�
   * @param lightness double
   * @return double
   */
  public double getMonochromeLuminance(double lightness) {
    if (Y2JLut == null) {
      throw new IllegalStateException("MonochromeLUT is not yet setup.");
    }
    double value = Y2JLut.correctValueInRange(lightness);
    return Y2JLut.getKey(value);
  }

  /**
   * �إ߳�����J�P�G�פ��������Y
   * @param XYZArray CIEXYZ[]
   */
  public void setupMonochromeLUT(CIEXYZ[] XYZArray) {
    int size = XYZArray.length;
    double[] YArray = new double[size];
    double[] JArray = new double[size];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = (CIEXYZ) XYZArray[x].clone();
      YArray[x] = XYZ.Y;
      XYZ.normalize(white);
      XYZ.normalize(NormalizeY.Normal100);
      CIECAM02Color color = cam.forward(XYZ);
      JArray[x] = color.J;
    }
    Y2JLut = new Interpolation1DLUT(YArray, JArray,
                                    Interpolation1DLUT.Algo.
                                    QUADRATIC_POLYNOMIAL);
  }

  /**
   * ��l��, �إߥ��I��Y�PJ���������Y
   */
  protected void init() {
    //�ժ�jndi
    whiteJNDIndex = GSDF.DICOM.getJNDIndex(white.Y);
    int size = ( (int) whiteJNDIndex) + 2;
    double[] JNDIndexArray = new double[size];
    double[] lightnessArray = new double[size];
    CIEXYZ XYZ = (CIEXYZ) white.clone();

    for (int x = 1; x < size; x++) {
      JNDIndexArray[x] = x;
      //jndi�����쪺Y
      double Y = GSDF.DICOM.getLuminance(x);
      XYZ.scaleY(Y);
      XYZ.normalize(white);
      XYZ.normalize(NormalizeY.Normal100);
      CIECAM02Color color = cam.forward(XYZ);
      //Y������J
      lightnessArray[x] = color.J;
    }

    jndi2JLut = new Interpolation1DLUT(JNDIndexArray, lightnessArray,
                                       Interpolation1DLUT.Algo.
                                       QUADRATIC_POLYNOMIAL);

  }

  public static void main(String[] args) {
    CIECAM02 cam = new CIECAM02(ViewingConditions.DimViewingConditions);
    CIEXYZ white = Illuminant.getD50WhitePoint();
    white.scaleY(800);

    CIECAM02Color c = new CIECAM02Color(new CIELCh(2, 0.25, 45));
    CIEXYZ XYZ = cam.inverse(c);
    CIECAM02Color jch = cam.forward(XYZ);

    CIECAM02JNDIndex jndi = new CIECAM02JNDIndex(cam, white);
//    System.out.println(jndi.getJNDIndex(2));
//    System.out.println(jndi.getLightness(706));
//    System.out.println(jndi.getLightness(0));
    double[] jchjndi = jndi.getJabcJNDIndex(jch);
    System.out.println(DoubleArray.toString(jch.getJabcValues()));
    System.out.println(DoubleArray.toString(jchjndi));
  }

}
