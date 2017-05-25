package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.lcd.*;
import shu.cms.util.*;
import shu.math.lut.*;

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
public class PLCCModel
    extends ChannelIndependentModel {

  public PLCCModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * ¨Ï¥Î¼Ò¦¡
   * @param factor LCDModelFactor
   */
  public PLCCModel(LCDModelFactor factor) {
    super(factor);
  }

  public PLCCModel(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  public static class Factor
      extends LCDModelBase.Factor {
    GammaCorrector rCorrector;
    RGBBase.Channel channel;

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    public Factor() {

    }

    public Factor(GammaCorrector rCorrector, RGBBase.Channel channel) {
      this.rCorrector = rCorrector;
      this.channel = channel;
    }
  }

  protected Factor[] makeFactor() {
    Factor[] factors = new Factor[] {
        new Factor(correct._RrCorrector, RGBBase.Channel.R),
        new Factor(correct._GrCorrector, RGBBase.Channel.G),
        new Factor(correct._BrCorrector, RGBBase.Channel.B)};
    return factors;
  }

  protected Factor[] _produceFactor() {
    singleChannel.produceRGBPatch();
    correct.produceGammaCorrector();
    Factor[] factors = makeFactor();
    return factors;
  }

  protected static Interpolation1DLUT produceSingleChannelPLCC_LUT(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch) {
    int size = singleChannelPatch.size();
    double[][] keyValue = new double[2][size];
    int index = 0;

    for (Patch p : singleChannelPatch) {
      keyValue[0][index] = p.getRGB().getValue(ch);
      keyValue[1][index] = p.getXYZ().Y;
      index++;
    }

    Interpolation1DLUT lut = new Interpolation1DLUT(keyValue[0], keyValue[1]);
    return lut;
  }

  /**
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  public CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor) {
    RGB newRGB = getLuminanceRGB(rgb, factor);
    getXYZRGB = newRGB;
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor) {
    RGB rgb = matries.XYZToRGBByMaxMatrix(XYZ);

    double[] originalRGBValues = rgb.getValues(new double[3],
                                               RGB.MaxValue.Double1);
    originalRGBValues = correct.gammaUncorrect(originalRGBValues);
    RGB originalRGB = new RGB(rgb.getRGBColorSpace(), originalRGBValues);
    return originalRGB;
  }

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
        LCDTarget.Number.Ramp1021, "091225");
    LCDTarget.Operator.gradationReverseFix(target);
    PLCCModel model = new PLCCModel(target);
    model.produceFactor();
    List<Patch> patchList = target.filter.grayPatch(true);
    int size = patchList.size();
    for (int x = size - 1; x >= 0; x--) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      RGB rgb = model.matries.XYZToRGBByMaxMatrix(XYZ);
      rgb.changeMaxValue(RGB.MaxValue.Double100);
      System.out.println(x + " " + rgb);
    }
  }

  public String getDescription() {
    return "PLCC";
  }

  /**
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB getLuminanceRGB(RGB rgb, LCDModel.Factor[] factor) {
//    correct._RrCorrector = ( (Factor) factor[0]).rCorrector;
//    correct._GrCorrector = ( (Factor) factor[1]).rCorrector;
//    correct._BrCorrector = ( (Factor) factor[2]).rCorrector;

    double[] correctValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    correctValues = correct.gammaCorrect(correctValues);

    return new RGB(rgb.getRGBColorSpace(), correctValues);
  }
}
