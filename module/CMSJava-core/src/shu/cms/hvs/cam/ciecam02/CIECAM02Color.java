package shu.cms.hvs.cam.ciecam02;

import shu.cms.colorspace.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.independ.CIELCh.Style;
import shu.cms.hvs.cam.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CIECAM02Color
    implements CAMColor, LChConvertible {

  public final String toString() {
    return "(J:" + J + " ac:" + ac + " bc:" + bc + " C:" + C + " h:" + h +
        " Q:" + Q + " M:" + M + " S:" + s + " H:" + H + ")";
  }

  public boolean isAdaptedToD65() {
    return false;
  }

  public CIECAM02Color() {

  }

  public CIECAM02Color(double J, double ac, double bc) {
    double[] jacbcValues = new double[] {
        J, ac, bc};
    double[] JChValues = ColorSpace.cartesian2polarCoordinatesValues(
        jacbcValues);
    this.ac = ac;
    this.bc = bc;
    this.J = JChValues[0];
    this.C = JChValues[1];
    this.h = JChValues[2];
  }

  public CIECAM02Color(CIELCh LCh) {
    this.J = LCh.L;
    this.C = LCh.C;
    this.h = LCh.h;
  }

  public double J, C, h; //lightness, chroma, hue angle
  public double Q, M, s, H; //brightness, colorfulness, saturation, hue quadrature
  public double ac, bc; //chroma的座標
  public double as, bs; //saturation的座標
  public double am, bm; //colorfulness的座標
  public CIEXYZ white;

  public double[] getJChValues() {
    return new double[] {
        J, C, h};
  }

  public double[] getJabcValues() {
    return new double[] {
        J, ac, bc};
  }

  public double[] getJabsValues() {
    return new double[] {
        J, as, bs};
  }

  public double[] getJabmValues() {
    return new double[] {
        J, am, bm};
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    return Style.CIECAM02;
  }

  /**
   * getValues
   *
   * @return double[]
   */
  public double[] getValues() {
    return getJabcValues();
  }

  public double[] getValues(double[] values) {
    values[0] = J;
    values[1] = ac;
    values[2] = bc;
    return values;
  }

  /**
   * getWhite
   *
   * @return CIEXYZ
   */
  public CIEXYZ getWhite() {
    return white;
  }
};
