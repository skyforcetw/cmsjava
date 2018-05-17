package shu.cms.devicemodel.lcd;

import java.util.*;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.devicemodel.lcd.thread.*;
import shu.cms.lcd.*;
import shu.math.*;
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
public class SCurveModel
    extends ChannelIndependentModel {

  public SCurveModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * 計算RGB,反推模式
   *
   * @param relativeXYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ relativeXYZ, Factor[] factor) {
    throw new UnsupportedOperationException();
  }

  /**
   * 計算XYZ,前導模式
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    rgbValues = rgb.getValues(rgbValues, RGB.MaxValue.Double1);
    CIEXYZ[] rgbXYZ = new CIEXYZ[3];
    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      int index = ch.getArrayIndex();
      int pindex = index * 3;
      rgbXYZ[index] = new CIEXYZ();
      rgbXYZ[index].X = getPointOfSCurve(parameters[pindex],
                                         rgbValues[index]);
      rgbXYZ[index].Y = getPointOfSCurve(parameters[pindex + 1],
                                         rgbValues[index]);
      rgbXYZ[index].Z = getPointOfSCurve(parameters[pindex + 2],
                                         rgbValues[index]);
      rgbXYZ[index].X *= pureMaximum[pindex];
      rgbXYZ[index].Y *= pureMaximum[pindex + 1];
      rgbXYZ[index].Z *= pureMaximum[pindex + 2];
    }
    CIEXYZ result = CIEXYZ.plus(rgbXYZ[0], rgbXYZ[1]); //r+g
    result = CIEXYZ.plus(result, rgbXYZ[2]); //r+g+b
    return result;
  }

  private double[] rgbValues = new double[3];
  private double[][] parameters = new double[9][];
  private double[] pureMaximum = new double[9];
  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    CIEXYZ falreXYZ = this.flare.getFlare(); //k
    double[] blackXYZValues = falreXYZ.getValues();
    RGB.Channel[] chs = RGB.Channel.RGBChannel;
    int index = 0;

    for (RGB.Channel ch : chs) {
      List<Patch> grayScalePatch = lcdTarget.filter.grayScalePatch(ch, true);
      double[][] curve = produceXYZCurve(grayScalePatch);
      for (int x = 0; x < 3; x++) {
        curve[x] = DoubleArray.minus(curve[x],
                                     blackXYZValues[x]);
        double max = curve[x][curve[x].length - 1];
        Maths.normalize(curve[x], max);
        double[] param = estimateSCurveParameter(curve[x]);
//        double[] c = getSCurve(param, 256);

        parameters[index] = param;
        pureMaximum[index] = max;
        index++;
      }
    }

    return new Factor[3];
  }

  public static double[] getSCurve(double[] parameter, int piece) {
    return getSCurve(parameter[0], parameter[1], parameter[2], parameter[3],
                     piece);
  }

  static double getPointOfSCurve(double[] p, double x) {
    return SCurveModel1Thread.f(p[0], x, p[1], p[2], p[3]);
  }

  public static double[] getSCurve(double A, double alpha, double beta,
                                   double C, int piece) {
    double[] curve = new double[piece];
    for (int x = 0; x < piece; x++) {
      double normal = x / (piece - 1.);
      curve[x] = SCurveModel1Thread.f(A, normal, alpha, beta, C);
    }
    return curve;
  }

  public static double[] estimateSCurveParameter(final double[] curve) {
    final int size = curve.length;
    MinimisationFunction func = new MinimisationFunction() {
      public double function(double[] param) {

        double alpha = param[0];
        double beta = param[1];
        double C = param[2];
        double A = 1 + C;

        double[] scurve = getSCurve(A, alpha, beta, C, size);
        return Maths.RMSD(curve, scurve);
      };
    };

    Minimisation minimisation = new Minimisation();
    minimisation.addConstraint(0, -1, 0);
    minimisation.addConstraint(1, -1, 0);
    minimisation.addConstraint(2, -1, 0);
    minimisation.nelderMead(func, new double[] {1, 1, 0.1});
    double[] param = minimisation.getParamValues();
    //A alpha beta C
    double[] parameter = new double[] {
        1 + param[2], param[0], param[1], param[2]};
    return parameter;
  }

  private double[][] produceXYZCurve(List<Patch> grayScalePatch) {
    int size = grayScalePatch.size();
    double[][] XYZCurve = new double[3][size];
    for (int x = 0; x < size; x++) {
      Patch p = grayScalePatch.get(x);
      CIEXYZ XYZ = p.getXYZ();
      XYZCurve[0][x] = XYZ.X;
      XYZCurve[1][x] = XYZ.Y;
      XYZCurve[2][x] = XYZ.Z;
    }
    return XYZCurve;
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "";
  }

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
        LCDTarget.Number.Ramp1024);
    CIEXYZ rXYZ = target.getPatch(RGB.Channel.R, 255, RGB.MaxValue.Double255).
        getXYZ();
    System.out.println(rXYZ);
//    target = target.targetFilter.getPatch79From1024();

    LCDTarget.Operator.gradationReverseFix(target);
    SCurveModel model = new SCurveModel(target);
    model.produceFactor();
    CIEXYZ XYZ = model.getXYZ(new RGB(255, 0, 0), false);
    System.out.println(XYZ);
    DeltaEReport report = model.testForwardModel(target, false)[0];
    System.out.println(report);
    System.out.println(report.getPatchDeltaEReport());
//    model.getmodelre
//    ModelReport report = model.report.getModelReport(target);
//    model.plotModelReport(report);
//    SCurveModel1Thread.f()
    //A * x^a/ ( x^b+C)
  }
}
