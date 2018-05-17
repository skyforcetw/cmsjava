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
 * 用CIECAM02來計算JND Index.
 * 本物件是以CIECAM02, 建立中性色塊的明度與亮度的關係.
 * 如此就可以不需要透過XYZ而僅需Y就可以對應到Lightness, 反過來亦同.
 *
 * CIECAM02如何介入HK效應? 理論上來說CIECAM02是不考慮到HK效應的.
 * 但是用CIECAM02應該是用來解決 V' 錯誤的問題(吧!? 猜測的)
 *
 * 非中性色色塊A如何取得與中性色色塊B相同明度下的亮度
 *
 * 1. 首先建立中性色塊灰階下, 明度與亮度的關係 Lightness<->Luminance
 * 2. 利用CIECAM02, 以A的亮度Y求A的明度L. 將A的明度利用步驟1的關係, 可以得到相對應的亮度Y'.
 *  Y與Y'應該是不相同的.
 * 3. 所以求非中性色的A的JNDI, 實際上是求出Y', 再由Y'帶出JNDIndex.
 * 4. 不同非中性色, 自有不同的Y->JNDIndex, 反過來JNDIndex->Y亦同.
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
   * 實驗功能, 不建議使用
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
   * 從J轉換到JND Index (J->JNDI)
   * @param lightness double
   * @return double
   */
  public final double getJNDIndex(double lightness) {
    double value = jndi2JLut.correctValueInRange(lightness);
    return jndi2JLut.getKey(value);
  }

  /**
   * 從JNDIndex轉到J (JNDI->J)
   * @param JNDIndex double
   * @return double
   */
  public final double getLightness(double JNDIndex) {
    double key = jndi2JLut.correctKeyInRange(JNDIndex);
    return jndi2JLut.getValue(key);
  }

  /**
   * 以明度取得單色光相對應的亮度值
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
   * 建立單色光的J與亮度之間的關係
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
   * 初始化, 建立白點的Y與J之間的關係
   */
  protected void init() {
    //白的jndi
    whiteJNDIndex = GSDF.DICOM.getJNDIndex(white.Y);
    int size = ( (int) whiteJNDIndex) + 2;
    double[] JNDIndexArray = new double[size];
    double[] lightnessArray = new double[size];
    CIEXYZ XYZ = (CIEXYZ) white.clone();

    for (int x = 1; x < size; x++) {
      JNDIndexArray[x] = x;
      //jndi對應到的Y
      double Y = GSDF.DICOM.getLuminance(x);
      XYZ.scaleY(Y);
      XYZ.normalize(white);
      XYZ.normalize(NormalizeY.Normal100);
      CIECAM02Color color = cam.forward(XYZ);
      //Y對應的J
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
