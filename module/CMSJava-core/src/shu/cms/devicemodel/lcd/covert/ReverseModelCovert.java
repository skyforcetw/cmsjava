package shu.cms.devicemodel.lcd.covert;

import java.util.*;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
//import vv.cms.lcd.calibrate.measured.find.*;
//import vv.cms.measure.cp.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ノ玡旧家Α箇代(ず础)は崩家Α干は崩家Αぃ非絋┦.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ReverseModelCovert {
  private LCDModel lcdModel;
  private LCDModelExposure adapter;
  private double step = RGB.MaxValue.Int12Bit.getStepIn255();
  private double max;
  private double[] steps;
  private Mode mode = Mode.Minimisation;

  public static enum Mode {
    Around, Minimisation, Best, NonCovert
  }

  public ReverseModelCovert(LCDModel lcdModel) {
    this(lcdModel, Mode.Minimisation);
  }

  /**
   *
   * @param lcdModel LCDModel
   * @param mode Mode
   * @deprecated
   */
  public ReverseModelCovert(LCDModel lcdModel, Mode mode) {
    this.lcdModel = lcdModel;
    this.adapter = new LCDModelExposure(lcdModel);
    this.mode = mode;
    max = lcdModel.getMaxValue().max;

//    switch (mode) {
//      case Around:
//        aroundCovert = new AroundCovert();
//        break;
//      case Best:
//        aroundCovert = new AroundCovert();
//        break;
//    }

    if (lcdModel instanceof ChannelDependentModel) {
      double[] elements = adapter.
          getXTalkElementValues(RGBBase.Channel.G);
      //xtalkstep畉钵
      double diff = Math.abs(elements[0] - elements[1]);
      //rgbstepsパstep舱Θ
      steps = DoubleArray.fill(1, 3, diff)[0];
    }
    else {
      double targetStep = lcdModel.getLCDTarget().getStep();
      targetStep = (targetStep == -1) ? 1 : targetStep;
      steps = DoubleArray.fill(1, 3, targetStep * 17)[0];
    }
  }

//  private CovertIF minimumCovert = null;
  private CovertIF aroundCovert = null;
  private CovertIF nonCovert = new NonCovert();
//  protected RGB initRGB;

  protected class NonCovert
      implements CovertIF {
    /**
     * getRGB
     *
     * @param XYZ CIEXYZ
     * @param relativeXYZ boolean
     * @return CovertResult
     */
    public CovertResult getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
      RGB rgb = adapter.getRGB(XYZ, relativeXYZ, false);
      CovertResult result = new CovertResult(rgb, false, Mode.NonCovert);
      return result;
    }

  }

//  protected class AroundCovert
//      implements CovertIF {
//    private RGBFinder finder;
//    protected AroundCovert() {
//
//      CIEXYZ white = lcdModel.getWhiteXYZ(false);
//      CPCodeMeasurement cpm = CPCodeMeasurement.getInstance(lcdModel, true);
//      RGB whiteRGB = lcdModel.getWhiteRGB();
//      finder = new RGBFinder(white, RGB.MaxValue.Int12Bit, cpm,
//                             whiteRGB.getValue(whiteRGB.getMaxChannel()));
//    }
//
//    /**
//     *
//     * @param XYZ CIEXYZ
//     * @param relativeXYZ boolean
//     * @return CovertResult
//     */
//    public final CovertResult getRGB(final CIEXYZ XYZ,
//                                     final boolean relativeXYZ) {
//      RGB initRGB = adapter.getRGB(XYZ, relativeXYZ);
//      RGB rgb = finder.getRGB(initRGB, XYZ);
//      boolean coverting = isCoverting(initRGB.getValues(new double[3],
//          RGB.MaxValue.Double255), rgb.getValues(new double[3],
//                                                 RGB.MaxValue.Double255));
//      CovertResult result = new CovertResult(rgb, coverting, Mode.Around);
//      return result;
//    }
//  }

  protected class MinimumCovert
      implements MinimisationFunction, CovertIF {
    private RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, lcdModel.getMaxValue());
    private CIEXYZ targetXYZ;
    private boolean relativeXYZ;

    private Minimisation min = null;

    protected void initMinimisation() {
      if (min == null) {
        min = new Minimisation();
        min.addConstraint(0, -1, 0);
        min.addConstraint(1, -1, 0);
        min.addConstraint(2, -1, 0);
        min.addConstraint(0, 1, max);
        min.addConstraint(1, 1, max);
        min.addConstraint(2, 1, max);
        if (integerMode) {
          min.setTolerance(1);
        }
      }
    }

    public final CovertResult getRGB(final CIEXYZ XYZ,
                                     final boolean relativeXYZ) {
      //砞﹚ヘ夹XYZ
      this.setTargetXYZ(XYZ, relativeXYZ);
      //癬﹍RGB
      RGB initRGB = adapter.getRGB(XYZ, relativeXYZ);
      double[] initRGBValues = initRGB.getValues(new double[3],
                                                 RGB.MaxValue.Double255);
      double[] start = DoubleArray.copy(initRGBValues);

      if (whiteRGB != null) {
        double[] whiteRGBValues = whiteRGB.getValues();
        min.addConstraint(0, 1, whiteRGBValues[0]);
        min.addConstraint(1, 1, whiteRGBValues[1]);
        min.addConstraint(2, 1, whiteRGBValues[2]);
      }
      min.addConstraint(0, -1, 0);
      min.addConstraint(1, -1, 0);
      min.addConstraint(2, -1, 0);

      min.nelderMead(this, start, steps, step);
      //粇畉程RGB
      double[] param = min.getParamValues();
      //琌癬coverノ硚
      boolean coverting = isCoverting(param, initRGBValues);
      RGB rgb = (RGB) initRGB.clone();
      rgb.setValues(param, RGB.MaxValue.Double255);
      CovertResult result = new CovertResult(rgb, coverting, Mode.Minimisation);
//      if(coverting) {
//        System.out.println("");
//      }
      return result;
    }

    protected void setTargetXYZ(CIEXYZ XYZ, boolean relativeXYZ) {
      this.targetXYZ = XYZ;
      this.relativeXYZ = relativeXYZ;
    }

    protected MinimumCovert() {
      initMinimisation();
    }

    /**
     * function
     *
     * @param rgbValues double[]
     * @return double CIEDE2000
     */
    public double function(double[] rgbValues) {
//      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, lcdModel.getMaxValue());
//      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,rgbValues, lcdModel.getMaxValue());
      rgb.setValues(rgbValues);
      CIEXYZ XYZ = lcdModel.getXYZ(rgb, relativeXYZ);
      DeltaE dE = lcdModel.getDeltaE(XYZ, targetXYZ);
      double de00 = dE.getCIE2000DeltaE();
      return de00;
    }

  }

  /**
   * 蹦ノ俱计家Α?
   */
  private boolean integerMode = false;

  public final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
    return getRGB(XYZ, relativeXYZ, mode);
  }

  private RGB whiteRGB;

  private final CovertResult getBestResult(CIEXYZ XYZ, boolean relativeXYZ,
                                           CovertIF covert) {
    CovertResult miniResult = covert.getRGB(XYZ, relativeXYZ);
    CovertResult nonResult = nonCovert.getRGB(XYZ, relativeXYZ);
    CovertResult[] results = new CovertResult[] {
        miniResult, nonResult};

    double[] deltaEs = new double[2];
    for (int x = 0; x < 2; x++) {
      CovertResult result = results[x];
      deltaEs[x] = lcdModel.calculateGetRGBDeltaE(result.rgb, XYZ, relativeXYZ).
          getCIE2000DeltaE();
    }

    int minIndex = Maths.minIndex(deltaEs);
//    if (minIndex == 0 && deltaEs[0] != deltaEs[1]) {
//      System.out.println("");
//    }
    return results[minIndex];
  }

  private final CovertResult getBestResult(CIEXYZ XYZ, boolean relativeXYZ) {
    CovertResult aroundResult = aroundCovert.getRGB(XYZ, relativeXYZ);
    CovertResult miniResult = new MinimumCovert().getRGB(XYZ, relativeXYZ);
    CovertResult nonResult = nonCovert.getRGB(XYZ, relativeXYZ);
    CovertResult[] results = new CovertResult[] {
        aroundResult, miniResult, nonResult};

    double[] deltaEs = new double[3];
    for (int x = 0; x < 3; x++) {
      CovertResult result = results[x];
      deltaEs[x] = lcdModel.calculateGetRGBDeltaE(result.rgb, XYZ, relativeXYZ).
          getCIE2000DeltaE();
    }

    int minIndex = Maths.minIndex(deltaEs);
    return results[minIndex];
  }

  public final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ,
                          ReverseModelCovert.Mode mode) {
    CovertResult result = null;
    switch (mode) {
      case NonCovert:
        result = nonCovert.getRGB(XYZ, relativeXYZ);
        break;
      case Minimisation:
        result = getBestResult(XYZ, relativeXYZ, new MinimumCovert());
        break;
      case Around:
        result = getBestResult(XYZ, relativeXYZ, aroundCovert);
        break;
      case Best:
        result = getBestResult(XYZ, relativeXYZ);
        this.bestMode = result.mode;
        break;
      default:
        return null;
    }
    this.coverting = result.converting;
    return result.rgb;
  }

  /**
   * 琌癬coverノ硚
   * @return boolean
   */
  public final boolean isCoverting() {
    return coverting;
  }

  private boolean isCoverting(double[] rgbValues1, double[] rgbValues2) {
    return!Arrays.equals(rgbValues1, rgbValues2);
  }

  public Mode getBestMode() {
    return bestMode;
  }

  /**
   * 琌蹦ノ俱计家Α?
   * 俱计家Α, 度穦т程钡俱计RGB
   *
   * @param integerMode boolean
   */
  public void setIntegerMode(boolean integerMode) {
    this.integerMode = integerMode;
  }

  public void setWhiteRGB(RGB whiteRGB) {
    this.whiteRGB = whiteRGB;
  }

  /**
   * 癬coverノ硚?
   */
  private boolean coverting = false;
  private Mode bestMode;
}
