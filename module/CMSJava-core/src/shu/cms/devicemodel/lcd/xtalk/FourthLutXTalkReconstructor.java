package shu.cms.devicemodel.lcd.xtalk;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class FourthLutXTalkReconstructor
    extends AbstractXTalkReconstructor {
  /**
   * FourLutXTalkReconstructor
   *
   * @param mmModel MultiMatrixModel
   * @param xtalkProperty XTalkProperty
   */
  public FourthLutXTalkReconstructor(MultiMatrixModel mmModel,
                                     XTalkProperty xtalkProperty) {
    super(mmModel, xtalkProperty);
  }

  /**
   * getXTalkRGB
   *
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @param relativeXYZ boolean
   * @return RGB
   */
  public RGB getXTalkRGB(CIEXYZ XYZ, RGB originalRGB, boolean relativeXYZ) {
    if (!originalRGB.isSecondaryChannel()) {
      //originalRGB一定只能是二次色
      return null;
    }

    //根據relativeXYZ處理XYZ
    CIEXYZ fromXYZ = adapter.fromXYZ(XYZ, relativeXYZ);
    fromXYZ.rationalize();

    RGBBase.Channel positiveChannel = xtalkProperty.getSelfChannel(originalRGB.
        getSecondaryChannel());
    RGBBase.Channel negativeChannel = xtalkProperty.getAdjacentChannel(
        positiveChannel);

    RGB rgb = recover.getXTalkRGB(fromXYZ, originalRGB, positiveChannel,
                                  negativeChannel);
    return rgb;
  }

  protected FourLutXTalkRGBRecover recover = new FourLutXTalkRGBRecover();

  protected class FourLutXTalkRGBRecover
      implements MinimisationFunction {

    public RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                           RGBBase.Channel positiveChannel,
                           RGBBase.Channel negativeChannel) {
      this.positiveChannel = positiveChannel;
      this.negativeChannel = negativeChannel;
      this.measureXYZ = XYZ;
      this.recoverRGB = (RGB) originalRGB.clone();

      //Create instance of Minimisation
      Minimisation min = new Minimisation();

      // initial estimates
      double[] start = new double[] {
          originalRGB.getValue(positiveChannel),
          originalRGB.getValue(negativeChannel)};

      double[] step = new double[] {
          mmModel.getLCDTarget().getStep(), mmModel.getLCDTarget().getStep()};
      min.addConstraint(0, -1, start[0]);
      min.addConstraint(1, 1, start[1]);
      // Nelder and Mead minimisation procedure
      min.nelderMead(this, start, step);

      // get values of y and z at minimum
      double[] param = min.getParamValues();
      recoverRGB.setValue(positiveChannel, param[0]);
      recoverRGB.setValue(negativeChannel, param[1]);
      return recoverRGB;
    }

    protected RGB recoverRGB;
    protected RGBBase.Channel positiveChannel;
    protected RGBBase.Channel negativeChannel;
    protected CIEXYZ measureXYZ;

    /**
     *
     * @param values double[]
     * @return double
     */
    public double function(double[] values) {
      //調整xtalkChannel的數值
      recoverRGB.setValue(positiveChannel, values[0]);
      recoverRGB.setValue(negativeChannel, values[1]);
      //使deltaE達到最小
      DeltaE de = mmModel.calculateGetRGBDeltaE(recoverRGB, measureXYZ, true);
      return de.getCIE2000DeltaE();
    }

  }
}
