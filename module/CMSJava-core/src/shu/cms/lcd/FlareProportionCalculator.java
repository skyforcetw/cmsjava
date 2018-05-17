package shu.cms.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 漏光比例計算器
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FlareProportionCalculator
    extends FlareCalculator {

  protected LCDModel model;
  public FlareProportionCalculator(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.model = new PLCCModel(lcdTarget);
  }

  public double function(double[] proportionValue) {
    return getVariance(RGBBase.Channel.R, proportionValue[0]) +
        getVariance(RGBBase.Channel.G, proportionValue[0]) +
        getVariance(RGBBase.Channel.B, proportionValue[0]);

  }

  protected double getVariance(RGBBase.Channel ch, double proportionValue) {
    CIEXYZ flare = model.flare.getBesidesEstimatedFlare(ch, proportionValue);
    return super.getVariance(ch, flare);
  }

  /**
   * 以Variance為基準,從2~20迭代,找到最小的Variance,就是漏光的(最佳)RGB比例
   * @return double
   */
  public double getFlareProportionValue() {
    //Create instance of Minimisation
    /*Minimisation min = new Minimisation();
// initial estimates
         double[] start = new double[] {
        1};
         min.addConstraint(0, -1, 2);
         min.addConstraint(0, +1, 20);
         min.nelderMead(this, start, start, 1);
// get values of y and z at minimum
         double[] param = min.getParamValues();
         return param[0];*/

    double bestProportionValue = 0;
    double minimumVariance = Double.MAX_VALUE;
    double step = this.lcdTarget.getStep();

    for (double proportionValue = 2; proportionValue <= 20;
         proportionValue += step) {
      double variance = this.function(new double[] {proportionValue});
      if (variance < minimumVariance) {
        minimumVariance = variance;
        bestProportionValue = proportionValue;
      }
    }
    return bestProportionValue;
  }
}
