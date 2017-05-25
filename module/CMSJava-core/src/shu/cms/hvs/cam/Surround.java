package shu.cms.hvs.cam;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * SR(Fu & Luo, 2005, p. 1195)= L / YW = Lsw / Ldw
 * = luminance of surround / luminance of reference white
 * = 環境光亮度/參考白亮度
 *
 * The 0.2 coefficient derives from the "gray world" assumption
 * (~18%-20% reflectivity). It tests whether the surround luminance is darker or brighter than medium gray.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public enum Surround {
  /**
   * 均勻光線的環境下, 視角大於4度
   */
  AverageAbove4,
  /**
   * 均勻光線的環境下
   * SR>20%
   */
  Average,
  /**
   * 昏暗的環境
   * SR=0%~20%
   */
  Dim,
  /**
   * 暗室, 或將幻燈片打在牆壁上, 或看電影
   * SR=0%
   */
  Dark,
  /**
   * 幻燈片, 透明片, 擺在燈箱上
   */
  CutSheet;

  public final static Surround getCIECAM02Surround(double surroundWhite,
      double displayWhite) {
    double SR = surroundWhite / displayWhite;
    if (SR >= 0.2) {
      return Surround.Average;
    }
    else if (SR > 0 && SR < 0.2) {
      return Surround.Dim;
    }
    else if (SR == 0) {
      return Surround.Dark;
    }
    return null;
  }

}
