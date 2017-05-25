package shu.cms.hvs.test;

import flanagan.math.*;
//import flanagan.math.Minimization;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.Illuminant;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.Surround;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PCLTester {

  public static void main(String[] args) {
    double[][] values = new double[][] {

        {
//        133.76, 0.1243, 183.23}, {
//        165.45, 0.0028, 223.82}, {
//        470.21, 0.3948, 238.6}, {
//        287.64, 0.0037, 252.27}, {
//        460.49, 0.9815, 209.21}, {
        287.45, 0.3259, 198.11}
    };
//    ObjectFunc obj = new ObjectFunc(133.76, 0.1243);
//    double max = 133.76;
//    ObjectFunc obj = new ObjectFunc(max, 0.1243, 183.23);
    ObjectFunc2 obj = new ObjectFunc2(values);

    Minimisation min = new Minimisation();
    min.addConstraint(0, -1, 1);
    min.addConstraint(0, 1, 500);
    min.addConstraint(1, -1, 1);
    min.addConstraint(1, 1, 100);

    min.nelderMead(obj, new double[] {10, 20});
    System.out.println(min.getMinimum());
    System.out.println(DoubleArray.toString(min.getParamValues()));
  }
}

class ObjectFunc
    implements MinimisationFunction {
  static CIEXYZ D65;
  static {
    CIEXYZ XYZ = (CIEXYZ) Illuminant.getD65WhitePoint().clone();
    XYZ.normalizeY100();
    D65 = XYZ;
  }

  double white, black, contrast;
  double PCL;
  ObjectFunc(double white, double black, double PCL) {
    this.white = white;
    this.black = black;
    this.contrast = white / black;
    this.PCL = PCL;
  }

  double getPCL(double LA, double Yb) {
    ViewingConditions vc = new ViewingConditions(D65, LA, Yb, Surround.Dark,
                                                 "");
    CIECAM02 cam = new CIECAM02(vc);
    CIEXYZ XYZ = (CIEXYZ) D65.clone();
    double max = cam.forward(XYZ).Q;
    XYZ.times(1. / contrast, false);
    double min = cam.forward(XYZ).Q;
    return max - min;
  }

  public double function(double[] param) {
    double LA = param[0];
    double Yb = param[1];
    double _PCL = getPCL(LA, Yb);
    double delta = Math.abs(_PCL - PCL);
    return delta;
  }
}

class ObjectFunc2
    implements MinimisationFunction {
  static CIEXYZ D65;
  static {
    CIEXYZ XYZ = (CIEXYZ) Illuminant.getD65WhitePoint().clone();
    XYZ.normalizeY100();
    D65 = XYZ;
  }

  double[][] values;
  ObjectFunc2(double[][] values) {
    this.values = values;
  }

  double getPCL(double LA, double Yb, double contrast) {
    ViewingConditions vc = new ViewingConditions(D65, LA, Yb, Surround.Dark,
                                                 "");
    CIECAM02 cam = new CIECAM02(vc);
    CIEXYZ XYZ = (CIEXYZ) D65.clone();
    double max = cam.forward(XYZ).Q;
    XYZ.times(1. / contrast, false);
    double min = cam.forward(XYZ).Q;
    return max - min;
  }

  public double function(double[] param) {
    double LA = param[0];
    double Yb = param[1];
    double total = 0;
    for (double[] v : values) {
      double c = v[0] / v[1];
      double PCL = getPCL(LA, Yb, c);
      total += Math.abs(PCL - v[2]);
    }
    return total;
  }
}
