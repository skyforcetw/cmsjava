package shu.cms.hvs.cam;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * SR(Fu & Luo, 2005, p. 1195)= L / YW = Lsw / Ldw
 * = luminance of surround / luminance of reference white
 * = ���ҥ��G��/�ѦҥիG��
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
   * ���å��u�����ҤU, �����j��4��
   */
  AverageAbove4,
  /**
   * ���å��u�����ҤU
   * SR>20%
   */
  Average,
  /**
   * ���t������
   * SR=0%~20%
   */
  Dim,
  /**
   * �t��, �αN�ۿO�����b����W, �άݹq�v
   * SR=0%
   */
  Dark,
  /**
   * �ۿO��, �z����, �\�b�O�c�W
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
