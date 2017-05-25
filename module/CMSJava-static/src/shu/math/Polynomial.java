package shu.math;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 求各多項式的值
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Polynomial {

  public final static double[] getCoefficients(double[] variables,
                                               Character character) {
    if (variables.length != character.variables) {
      throw new IllegalArgumentException(
          "variables.length != character.variables");
    }
    switch (character.variables) {
      case 1:
      case 2:
      case 3:
      default:
        throw new IllegalArgumentException("Unsupported variables: " +
                                           character.variables);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 一元多項式性質
   *
   * <p>Copyright: Copyright (c) 2009</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public final static class UniCharacter
      extends Character {
    public UniCharacter(int degree, boolean withConstan) {
      super(1, degree, withConstan);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 二元多項式性質
   *
   * <p>Copyright: Copyright (c) 2009</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public final static class BiCharacter
      extends Character {
    public BiCharacter(int degree, boolean withConstan) {
      super(2, degree, withConstan);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 三元多項式性質
   *
   * <p>Copyright: Copyright (c) 2009</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public final static class TriCharacter
      extends Character {
    public TriCharacter(int degree, boolean withConstan) {
      super(3, degree, withConstan);
    }
  }

  public static class Character {
    private int degree;
    private boolean withConstan;
    private int variables;
    public Character(int variables, int degree, boolean withConstan) {
      this.variables = variables;
      this.degree = degree;
      this.withConstan = withConstan;
    }
  }

  protected Polynomial() {
  }

  public static interface COEF {

  }

  public static double[] addCoef3WithConstant(double[] coef3) {
    int size = coef3.length;
    double[] newcoef = new double[size + 1];
    System.arraycopy(coef3, 0, newcoef, 1, size);
    newcoef[0] = 1.;
    return newcoef;
  }

  protected final static class Coef2X {
    public static double[] getCoef3By9(double x, double y) {
      double[] coef = new double[9];
      coef[0] = x;
      coef[1] = y;
      coef[2] = x * y;
      coef[3] = x * x; //x^2
      coef[4] = y * y; //y^2

      coef[5] = x * coef[3]; //(x^3)
      coef[6] = y * coef[4]; //(y^3)

      coef[7] = x * coef[4]; //xy^2
      coef[8] = coef[3] * y; //x^2y
      return coef;
    }

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * coef3 crosstalk
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected final static class Coef3X {
    public static double[] getCoef3By3(double x, double y, double z) {
      double[] coef = new double[3];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      return coef;
    }

    public static double[] getCoef3By6(double x, double y, double z) {
      double[] coef = new double[6];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      return coef;
    }

    public static double[] getCoef3By7(double x, double y, double z) {
      double[] coef = new double[7];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      coef[6] = x * y * z;
      return coef;
    }

    public static double[] getCoef3By9(double x, double y, double z) {
      double[] coef = new double[9];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      coef[6] = x * x;
      coef[7] = y * y;
      coef[8] = z * z;
      return coef;
    }

    public static double[] getCoef3By10(double x, double y, double z) {
      double[] coef = new double[10];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      coef[6] = x * x;
      coef[7] = y * y;
      coef[8] = z * z;
      coef[9] = x * y * z;
      return coef;
    }

    public static double[] getCoef3By13(double x, double y, double z) {
      double[] coef = new double[13];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      coef[6] = x * x;
      coef[7] = y * y;
      coef[8] = z * z;
      coef[9] = x * y * z;
      coef[10] = x * coef[6];
      coef[11] = y * coef[7];
      coef[12] = z * coef[8];
      return coef;
    }

    public static double[] getCoef3By52(double x, double y, double z) {
      double[] coef31 = getCoef3By31(x, y, z);
      double[] coef = new double[52];
      System.arraycopy(coef31, 0, coef, 0, coef31.length);

      coef[31] = x * coef[19]; //x^5
      coef[32] = y * coef[20]; //y^5
      coef[33] = z * coef[21]; //z^5

      coef[34] = coef[19] * y; //x^4y
      coef[35] = coef[19] * z; //x^4z

      coef[36] = coef[20] * x; //y^4x
      coef[37] = coef[20] * z; //y^4z

      coef[38] = coef[21] * x; //z^4x
      coef[39] = coef[21] * y; //z^4y

      coef[40] = coef[22] * y; //x^3y^2
      coef[41] = coef[23] * z; //x^3z^2

      coef[42] = coef[24] * x; //y^3x^2
      coef[43] = coef[25] * z; //y^3z^2

      coef[44] = coef[26] * x; //z^3x^2
      coef[45] = coef[27] * y; //z^3y^2

      coef[46] = coef[22] * z; //x^3yz
      coef[47] = coef[24] * z; //y^3xz
      coef[48] = coef[26] * y; //z^3xy
      coef[49] = coef[28] * z; //x^2y^2z
      coef[50] = coef[29] * x; //y^2z^2x
      coef[51] = coef[30] * y; //z^2x^2y

      return coef;
    }

    public static double[] getCoef3By31(double x, double y, double z) {
      double[] coef19 = getCoef3By19(x, y, z);
      double[] coef = new double[31];
      System.arraycopy(coef19, 0, coef, 0, coef19.length);

      coef[19] = x * coef[10]; //x^4
      coef[20] = y * coef[11]; //y^4
      coef[21] = z * coef[12]; //z^4

      coef[22] = y * coef[10]; //x^3y
      coef[23] = z * coef[10]; //x^3z

      coef[24] = x * coef[11]; //y^3x
      coef[25] = z * coef[11]; //y^3z

      coef[26] = x * coef[12]; //z^3x
      coef[27] = y * coef[12]; //z^3y

      coef[28] = coef[6] * coef[7]; //x^2y^2
      coef[29] = coef[7] * coef[8]; //y^2z^2
      coef[30] = coef[6] * coef[8]; //x^2z^2

      return coef;
    }

    public static double[] getCoef3By19(double x, double y, double z) {
      double[] coef = new double[19];
      coef[0] = x;
      coef[1] = y;
      coef[2] = z;
      coef[3] = x * y;
      coef[4] = y * z;
      coef[5] = z * x;
      coef[6] = x * x; //x^2
      coef[7] = y * y; //y^2
      coef[8] = z * z; //z^2
      coef[9] = x * y * z;

      coef[10] = x * coef[6]; //(x^3)
      coef[11] = y * coef[7]; //(y^3)
      coef[12] = z * coef[8]; //(z^3)

      coef[13] = x * coef[7]; //xy^2
      coef[14] = coef[6] * y; //x^2y
      coef[15] = y * coef[8]; //yz^2
      coef[16] = coef[7] * z; //y^2z
      coef[17] = z * coef[6]; //zx^2
      coef[18] = x * coef[8]; //xz^2
      return coef;
    }

  }

  public enum COEF_3
      implements COEF {
    BY_3(3), BY_3C(3, true), BY_6(6), BY_6C(6, true), BY_7(7), BY_7C(7, true),
    BY_9(9), BY_9C(9, true), BY_10(10), BY_10C(10, true), BY_13(13), BY_13C(13, true),
    BY_19(19), BY_19C(19, true), BY_31(31), BY_31C(31, true), BY_52(52), BY_52C(
        52, true);

    COEF_3(int item) {
      this.item = item;
    }

    COEF_3(int item, boolean constantItem) {
      this.item = constantItem ? item + 1 : item;
      this.withConstant = constantItem;
    }

    public int item;
    public boolean withConstant = false;

    public String toString() {
      return "3" + name();
    }
  }

  public enum COEF_2
      implements COEF {
    BY_9(9), BY_9C(9, true);

    COEF_2(int item) {
      this.item = item;
    }

    COEF_2(int item, boolean constantItem) {
      this.item = constantItem ? item + 1 : item;
      this.withConstant = constantItem;
    }

    public int item;
    public boolean withConstant = false;

    public String toString() {
      return "2" + name();
    }
  }

  public enum COEF_1
      implements COEF {
    BY_1(1, false), BY_1C(1, true), BY_2(2, false), BY_2C(2, true), BY_3(3, false),
    BY_3C(3, true), BY_4(4, false), BY_4C(4, true), BY_5(5, false), BY_5C(5, true),
    BY_6(6, false), BY_6C(6, true), BY_7(7, false), BY_7C(7, true), BY_8(8, false),
    BY_8C(8, true), BY_9(9, false), BY_9C(9, true), BY_10(10, false), BY_10C(10, true),
    BY_11(11, false), BY_11C(11, true), BY_12(12, false), BY_12C(12, true),
    BY_13(13, false), BY_13C(13, true), BY_14(14, false), BY_14C(14, true),
    BY_15(15, false), BY_15C(15, true), BY_16(16, false), BY_16C(16, true),
    BY_17(17, false), BY_17C(17, true), BY_18(18, false), BY_18C(18, true),
    BY_19(19, false), BY_19C(19, true), BY_20(20, false), BY_20C(20, true);

    COEF_1(int item, boolean constantItem) {
      this.item = constantItem ? item + 1 : item;
      this.withConstant = constantItem;
    }

    public int item;
    public boolean withConstant;

    public boolean hasLowerOrder() {
      int i = (withConstant ? item - 2 : item - 1);
      if (i == 0) {
        return false;
      }
      else {
        return true;
      }
    }

    public boolean hasUpperOrder() {
      int i = (withConstant ? item : item + 1);
      if (i > 9) {
        return false;
      }
      else {
        return true;
      }
    }

    public COEF_1 getLowerOrder() {
      int i = (withConstant ? item - 2 : item - 1);
      if (i == 0) {
        throw new IllegalStateException("No lower order.");
      }
      String name = "BY_" + i + (withConstant ? "C" : "");
      return this.valueOf(name);
    }

    public COEF_1 getUpperOrder() {
      int i = (withConstant ? item : item + 1);
      if (i > 9) {
        throw new IllegalStateException("No upper order.");
      }
      String name = "BY_" + i + (withConstant ? "C" : "");
      return this.valueOf(name);
    }

    public String toString() {
      return "1" + name();
    }

    public String toString(double[] coefficients) {
      if (coefficients.length != item) {
        throw new IllegalArgumentException("coefficients.length != item");
      }
      StringBuilder buf = new StringBuilder("y = " +
                                            (withConstant ?
                                             coefficients[0] + " + " :
                                             ""));
      int start = withConstant ? 1 : 0;
      for (int x = start; x < item; x++) {
        buf.append(coefficients[x] + "x" +
                   ( (x >= (start + 1)) ? "^" + Integer.toString(x + (1 - start)) :
                    ""));
        if (x + 1 < item) {
          buf.append(" + ");
        }
      }
      return buf.toString();
    }

  }

  public static double[] getCoefByOrder(double x, int order) {
    double[] coef = new double[order + 1];
    coef[0] = 1.0;
    for (int i = 0; i < order; i++) {
      coef[i + 1] = x * coef[i];
    }
    return coef;
  }

  /**
   * 一元coefs項
   * (如果coefs==1,則只有常數項)
   * @param x double
   * @param coefs int
   * @return double[]
   */
  public static double[] getCoefWithConstant(double x, int coefs) {
    double[] coef = new double[coefs];
    coef[0] = 1.0;
    for (int i = 1; i < coefs; i++) {
      coef[i] = x * coef[i - 1];
    }
    return coef;
  }

  public static double[] getCoef(double[] variables, COEF coefs) {
    if (coefs instanceof COEF_1) {
      if (variables.length == 1) {
        return getCoef(variables[0], (COEF_1) coefs);
      }
      else {
        return getCoef(variables, (COEF_1) coefs);
      }
    }
    else if (coefs instanceof COEF_3) {
      return getCoef(variables, (COEF_3) coefs);
    }
    else if (coefs instanceof COEF_2) {
      return getCoef(variables, (COEF_2) coefs);
    }

    return null;
  }

  public static double[] getCoef(double[] xyz, COEF_3 coefs) {
    if (xyz.length != 3) {
      throw new IllegalArgumentException("xyz.length != 3");
    }
    return getCoef(xyz[0], xyz[1], xyz[2], coefs);
  }

  public static double[] getCoef(double[] xyz, COEF_2 coefs) {
    if (xyz.length != 2) {
//      throw new IllegalArgumentException("xyz.length != 2");
    }
    return getCoef(xyz[0], xyz[1], coefs);
  }

  public static double[] getCoef(double[] xyz, COEF_1 coefs) {
    if (xyz.length != 3) {
      throw new IllegalArgumentException("xyz.length != 3");
    }
    double[] coefx = getCoef(xyz[0], coefs);
    double[] coefy = getCoef(xyz[1], coefs);
    double[] coefz = getCoef(xyz[2], coefs);
    int coefSize = coefx.length;
    int size = coefSize * 3;
    double[] result = new double[size];
    System.arraycopy(coefx, 0, result, 0, coefx.length);
    System.arraycopy(coefy, 0, result, coefSize, coefx.length);
    System.arraycopy(coefz, 0, result, coefSize * 2, coefx.length);
    return result;
  }

  public static double[] getCoef(double x, COEF_1 coefs) {

    double[] coef = getCoefWithConstant(x, coefs.item);
    return coef;
  }

  public static double[] getCoef(double x, double y, double z, COEF_3 coefs) {
    double[] coef3 = null;
    switch (coefs) {
      case BY_3:
      case BY_3C:
        coef3 = Coef3X.getCoef3By3(x, y, z);
        break;
      case BY_6:
      case BY_6C:
        coef3 = Coef3X.getCoef3By6(x, y, z);
        break;
      case BY_7:
      case BY_7C:
        coef3 = Coef3X.getCoef3By7(x, y, z);
        break;
      case BY_9:
      case BY_9C:
        coef3 = Coef3X.getCoef3By9(x, y, z);
        break;
      case BY_10:
      case BY_10C:
        coef3 = Coef3X.getCoef3By10(x, y, z);
        break;
      case BY_13:
      case BY_13C:
        coef3 = Coef3X.getCoef3By13(x, y, z);
        break;
      case BY_19:
      case BY_19C:
        coef3 = Coef3X.getCoef3By19(x, y, z);
        break;
      case BY_31:
      case BY_31C:
        coef3 = Coef3X.getCoef3By31(x, y, z);
        break;
      case BY_52:
      case BY_52C:
        coef3 = Coef3X.getCoef3By52(x, y, z);
        break;
      default:
        return null;
    }
    if (coefs.withConstant) {
      return addCoef3WithConstant(coef3);
    }
    else {
      return coef3;
    }
  }

  public static double[] getCoef(double x, double y, COEF_2 coefs) {
    double[] coef2 = null;
    switch (coefs) {
      case BY_9:
      case BY_9C:
        coef2 = Coef2X.getCoef3By9(x, y);
        break;
    }
    if (coefs.withConstant) {
      return addCoef3WithConstant(coef2);
    }
    else {
      return coef2;
    }
  }

  public static void main(String[] args) {
//    System.out.println(DoubleArray.toString(getCoefWithConstant(3, 3)));
    COEF_1 coef = COEF_1.BY_9;
    System.out.println(coef.toString(getCoef(2, coef)));
  }
}
