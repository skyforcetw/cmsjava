package shu.cms.colororder;

import shu.math.*;

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
public class OSAUCSColor {
  public double L;
  public double j;
  public double g;
  public double rho;
  public double phi;

  public OSAUCSColor(double L, double j, double g) {
    this(L, j, g, Math.sqrt(Maths.sqr(j) + Maths.sqr(g)), Math.atan(g / j));
  }

  public OSAUCSColor(double L, double j, double g, double rho, double phi) {
    this.L = L;
    this.j = j;
    this.g = g;
    this.rho = rho;
    this.phi = phi;
  }

  public final String toString() {
    return "(" + L + " " + j + " " + g + " " + rho + " " + phi + ")";
  }
}
