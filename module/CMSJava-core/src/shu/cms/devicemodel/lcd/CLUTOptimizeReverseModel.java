package shu.cms.devicemodel.lcd;

import flanagan.math.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.Polynomial;
import shu.math.lut.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 利用優化演算法, 找到反推的最接近解
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CLUTOptimizeReverseModel
    extends LCDModel implements MinimisationFunction {

  /**
   * CLUTOptimizeReverseModel
   *
   * @param A2BClut ColorSpaceConnectedLUT
   */
  public CLUTOptimizeReverseModel(ColorSpaceConnectedLUT A2BClut) {
    this.A2BClut = A2BClut;
    this.interpolation = new TetrahedralInterpolation(A2BClut.produceCubeTable());
    this.regressionReverseModel = new CLUTRegressionReverseModel(A2BClut,
        Polynomial.COEF_3.BY_19C);
    regressionReverseModel.produceFactor();
    this.flare.setFlare(new CIEXYZ());
    this.targetWhitePoint = new CIEXYZ(interpolation.getValues(RGB.White.
        getValues()));
    this.setMaxValue(A2BClut.getMaxValue());
  }

  private ColorSpaceConnectedLUT A2BClut;
  private TetrahedralInterpolation interpolation;
//  private LCDPolynomialRegressionModel regressionModel;
  private CLUTRegressionReverseModel regressionReverseModel;

  /**
   * 計算RGB,反推模式
   *
   * @param relativeXYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ relativeXYZ, Factor[] factor) {
    CIEXYZ absoluteXYZ = this.toXYZ(relativeXYZ, true);
    targetXYZValues = absoluteXYZ.getValues();

    RGB initRGB = regressionReverseModel.getRGB(absoluteXYZ, false);

    Minimisation min = new Minimisation();
    min.addConstraint(0, -1, 0);
    min.addConstraint(1, -1, 0);
    min.addConstraint(2, -1, 0);
    min.addConstraint(0, 1, 255);
    min.addConstraint(1, 1, 255);
    min.addConstraint(2, 1, 255);
    min.nelderMead(this,
                   initRGB.getValues(new double[3], RGB.MaxValue.Double255));
    double[] rgbValues = min.getParamValues();
    RGB rgb = new RGB(rgbValues[0], rgbValues[1], rgbValues[2]);
    return rgb;
  }

  /**
   * 計算XYZ,前導模式
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    double[] XYZValues = interpolation.getValues(rgb.getValues(new double[3],
        RGB.MaxValue.Double255));
    CIEXYZ XYZ = new CIEXYZ(XYZValues);
    return XYZ;
  }

  /**
   * 求係數
   *
   * @return Factor[]
   * @todo Implement this shu.cms.devicemodel.lcd.LCDModel method
   */
  protected Factor[] _produceFactor() {
    Factor[] factors = new Factor[3];
    return factors;
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "";
  }

  /**
   * function
   *
   * @param param double[]
   * @return double
   * @todo Implement this flanagan.math.MinimizationFunction method
   */
  public double function(double[] param) {
    double[] XYZValues = interpolation.getValues(param);
    double rmsd = Maths.RMSD(XYZValues, targetXYZValues);
    return rmsd;
  }

  private double[] targetXYZValues;
}
