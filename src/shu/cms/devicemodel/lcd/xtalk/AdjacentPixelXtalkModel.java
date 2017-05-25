package shu.cms.devicemodel.lcd.xtalk;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.lcd.*;
import shu.cms.lcd.material.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.util.log.*;
///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �ȦҼ{�F��pixel�y����Xtalk������Model
 * �̧����K��LCD�S�ʪ�model
 *
 * �@�إ[�tXtalk�ѪR����k:
 * 1. �����q��rgb ramp, �إ�multi-matrix model
 * 2. �Q��mmmodel, �@�Xcp code, �H��cp code���R�i��Q�Ψ쪺code�ϰ�
 * 3. �H���ϰ�ʺA�إߥXxtalk��target. �A�H��target����xtalk model.
 * 4. �Q��xtalk model���s���Xcp code.
 * �p���i�H��֫ܦh�ڥ��S�����n�q�����ϰ�.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class AdjacentPixelXtalkModel
    extends ChannelDependentModel {

  public final static AdjacentPixelXtalkModel getRecommendXTalkPropertyModel(
      LCDTarget lcdTarget, LCDTarget xtalkLCDTarget) {
    AdjacentPixelXtalkModel left = new AdjacentPixelXtalkModel(lcdTarget,
        xtalkLCDTarget, XTalkProperty.getLeftXTalkProperty());
    left.produceFactor();

    AdjacentPixelXtalkModel right = new AdjacentPixelXtalkModel(lcdTarget,
        xtalkLCDTarget, XTalkProperty.getRightXTalkProperty());
    right.produceFactor();

    double leftAveDeltaE = left.report.getModelReport(xtalkLCDTarget).
        forwardReport[0].getMeasuredDeltaE(DeltaEReport.AnalyzeType.Average);
    double rightAveDeltaE = right.report.getModelReport(xtalkLCDTarget).
        forwardReport[0].getMeasuredDeltaE(DeltaEReport.AnalyzeType.Average);
    if (leftAveDeltaE < rightAveDeltaE) {
      return left;
    }
    else {
      return right;
    }
  }

  public AdjacentPixelXtalkModel(LCDModelFactor factor) {
    super(factor);
  }

  public AdjacentPixelXtalkModel(LCDTarget lcdTarget, LCDTarget xtalkLCDTarget) {
    super(lcdTarget, xtalkLCDTarget);
  }

  public AdjacentPixelXtalkModel(LCDTarget lcdTarget, LCDTarget xtalkLCDTarget,
                                 XTalkProperty xtalkProperty) {
    this(lcdTarget, xtalkLCDTarget);
    this.property = xtalkProperty;
  }

  /**
   * �p��RGB,�ϱ��Ҧ�
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    RGB rgb = mmModel.getRGB(XYZ, true);
    RGB correct = getCorrectRGB(rgb, true);
    return correct;
  }

  /**
   * �p��XYZ,�e�ɼҦ�
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    RGB correct = getCorrectRGB(rgb, false);
    return mmModel.getXYZ(correct, true);
  }

  protected RGB correctRGB(final RGB rgb, boolean uncorrect) {
    RGB correctRGB = (RGB) rgb.clone();
    for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
      // R/G/B�U�ե��@��
      double selfValues = rgb.getValue(ch);
      double adjValues = rgb.getValue(property.getAdjacentChannel(ch));
      adjValues = RGB.rationalize(adjValues, rgb.getMaxValue());

      //self���ץ���
      double selfCorrectValues = eliminator.getCorrectionValue(ch,
          selfValues, adjValues);

      if (uncorrect) {
        //����xtalk
        correctRGB.setValue(ch, correctRGB.getValue(ch) - selfCorrectValues);
      }
      else {
        //�w��xtalk(�ץ�)
        correctRGB.setValue(ch, correctRGB.getValue(ch) + selfCorrectValues);
      }
    }
    return correctRGB;
  }

  protected RGB getCorrectRGB(final RGB rgb, boolean uncorrect) {
    if (rgb.isPrimaryChannel() || !crosstalkCorrect) {
      //�`�ٮɶ�
      return rgb;
    }

    RGB correctRGB = (RGB) rgb.clone();

    if (correctByLuminance) {
      //�H�G�׮ե�����, �����G��domain
      correct.gammaCorrect(correctRGB);
    }

    correctRGB = correctRGB(correctRGB, uncorrect);
    correctRGB.rationalize();
    if (this.doubleCorrect) {
      correctRGB = correctRGB(correctRGB, uncorrect);
      correctRGB.rationalize();
    }

    if (correctByLuminance) {
      //�H�G�׮ե�����, �n�q�G��domain��^code
      correct.gammaUncorrect(correctRGB);
    }

    return correctRGB;
  }

  protected double[] codeValues;
  private boolean doubleCorrect = false;
  /**
   * �ץ���Ӫ�O�_�HLuminance RGB�x�s
   * �o�˪��ηN�O���F��������, �HLuminance RGB���Ŷ������|�����n�����G,
   *  ���O��ڴ��յ��G�èS���t�Ӧh.
   * @note ���n��
   */
  protected final static boolean correctByLuminance = false;
  protected boolean crosstalkCorrect = true;

  /**
   * �D�Y��
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    mmModel = new MultiMatrixModel(lcdTarget);
    mmModel.produceFactor();
    mmModel.setCovertMode(false);
    mmModel.correct.setCorrectorAlgo(Interpolation1DLUT.Algo.
                                     QUADRATIC_POLYNOMIAL);
    if (correctByLuminance) {
      singleChannel.produceRGBPatch();
      correct.produceGammaCorrector();
    }

    codeValues = getXTalkElementValues(RGBBase.Channel.G);
    reconstructor = new SimpleXTalkReconstructor(mmModel, property);

    double[][] rCorrectLut = getCorrectLUT(RGBBase.Channel.R, codeValues,
                                           codeValues);
    double[][] gCorrectLut = getCorrectLUT(RGBBase.Channel.G, codeValues,
                                           codeValues);
    double[][] bCorrectLut = getCorrectLUT(RGBBase.Channel.B, codeValues,
                                           codeValues);

    eliminator = new XTalkEliminator(codeValues, codeValues, rCorrectLut,
                                     gCorrectLut, bCorrectLut);

    return new Factor[3];
  }

  /**
   *
   * @param selfChannel Channel
   * @param selfChannelValues double[]
   * @param adjacentChannelValues double[]
   * @return double[][]
   */
  protected double[][] getCorrectLUT(RGBBase.Channel selfChannel,
                                     double[] selfChannelValues,
                                     double[] adjacentChannelValues) {
    return getCorrectLUT(selfChannel, selfChannelValues, adjacentChannelValues,
                         property, reconstructor);
  }

  protected double[][] getCorrectLUT(RGBBase.Channel selfChannel,
                                     double[] selfChannelValues,
                                     double[] adjacentChannelValues,
                                     XTalkProperty property,
                                     AbstractXTalkReconstructor reconstructor) {
    RGBBase.Channel adjChannel = property.getAdjacentChannel(selfChannel);
    RGB keyRGB = xtalkLCDTarget.getKeyRGB();
    keyRGB.setColorBlack();
    int selfSize = selfChannelValues.length;
    int adfSize = adjacentChannelValues.length;
    //���B��correctLut����m�Ppatent�W�����P, �w�g��դF
    double[][] correctLut = DoubleArray.fill(adfSize, selfSize, Double.NaN);

    for (int x = 0; x < adfSize; x++) {
      for (int y = 0; y < selfSize; y++) {
        keyRGB.setValue(adjChannel, adjacentChannelValues[x]);
        keyRGB.setValue(selfChannel, selfChannelValues[y]);

        Patch p = xtalkLCDTarget.getPatch(keyRGB);
        if (p == null || !p.getRGB().isSecondaryChannel()) {
          //�p�G���O�G����, �S��k�DXtalk RGB
          if (adjacentChannelValues[x] == 0 || selfChannelValues[y] == 0) {
            correctLut[x][y] = 0;
          }
          continue;
        }

        RGB xtalkRGB = reconstructor.getXTalkRGB(p.getXYZ(), keyRGB, false);
        DeltaE deltaE = reconstructor.getXTalkRGBDeltaE();
        if (correctByLuminance) {
          correct.gammaCorrect(xtalkRGB);
          correct.gammaCorrect(keyRGB);
        }

        double correctValue = xtalkRGB.getValue(selfChannel) -
            keyRGB.getValue(selfChannel);
        if (LoggingDetail) {
          Logger.log.trace(keyRGB + "=>" + xtalkRGB + " " +
                           deltaE.getCIE2000DeltaE() + " " +
                           correctValue);
        }
        correctLut[x][y] = correctValue;
      }
    }

    for (int x = 0; x < adfSize; x++) {
      for (int y = 0; y < selfSize; y++) {
        if (Double.isNaN(correctLut[x][y])) {
          throw new IllegalStateException("Not valid correctLut!");
        }
      }
    }
    return correctLut;
  }

  /**
   * �O�_�nlog��detail
   */
  private final static boolean LoggingDetail = AutoCPOptions.get(
      "XTalkModel_LoggingDetail");

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "AdjacentPixelXtalk";
  }

  public static void main(String[] args) {
    //==========================================================================
    // source
    //==========================================================================
    LCDTarget complexLCDTarget = LCDTargetMaterial.getCPT320WF01SC_0227();
    //==========================================================================

    LCDTarget ramplcdTarget = complexLCDTarget.targetFilter.getRamp();

    //==========================================================================
    LCDTarget testlcdTarget = complexLCDTarget.targetFilter.getTest();
    LCDTarget xtalklcdTarget = complexLCDTarget.targetFilter.getXtalk();
//    List<Patch> oneZeroChannel = testlcdTarget.filter.leastOneZeroChannel();
//    oneZeroChannel.addAll(xtalklcdTarget.getPatchList());
//    LCDTarget newtarget = LCDTarget.Instance.get(oneZeroChannel,
//                                                 LCDTarget.Number.Unknow, false);
    //==========================================================================

//    LCDTarget reportlcdTarget = testlcdTarget;

    LCDModel model = new AdjacentPixelXtalkModel(ramplcdTarget,
                                                 xtalklcdTarget,
//                                                 newtarget,
                                                 XTalkProperty.
                                                 getLeftXTalkProperty());
    model.produceFactor();
    MultiMatrixModel mmodel = new MultiMatrixModel(ramplcdTarget);
    mmodel.produceFactor();

    if (model instanceof AdjacentPixelXtalkModel) {
//      ( (AdjacentPixelXtalkModel) model).setCrosstalkCorrect(false);
//      ( (AdjacentPixelXtalkModel) model).plotCorrectLUT();
    }

//    DeltaEReport.setDecimalFormat(new DecimalFormat("##.####"));
//    System.out.println(model.report.getModelReport(xtalklcdTarget, 1));
//    ModelReport report2 = model.report.getModelReport(testlcdTarget, 1);
//    System.out.println(report2);

    for (int x = 0; x < 100; x += 5) {
      Patch p = testlcdTarget.getPatch(x);
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = p.getXYZ();
      RGB argb = model.getRGB(XYZ, false);
      RGB mrgb = mmodel.getRGB(XYZ, false);
      System.out.println(rgb + " " + argb + " " + mrgb);
    }

  }

  protected final Plot3D plot3DLUT(RGBBase.Channel ch,
                                   double[][] correctLut,
                                   Color color, XTalkProperty property,
                                   String title, String zAxisLabel) {
    RGBBase.Channel adjch = property.getAdjacentChannel(ch);
    Plot3D plot = Plot3D.getInstance(title);
    plot.addGridPlot(ch.name(), color, codeValues, codeValues,
                     correctLut);

    plot.setAxeLabel(0, adjch + " code");

    plot.setAxeLabel(1, ch + " code");

    plot.setAxeLabel(2, zAxisLabel);

    plot.setFixedBounds(0, 0, 255);

    plot.setFixedBounds(1, 0, 255);

    plot.setFixedBounds(2, -10, 10);

    return plot;
  }

  public Plot3D[] plotTouchedLUT() {
    Plot3D[] plots = new Plot3D[3];
    double[][][] touchedLut = eliminator.touchedLut.getTouchedLUT();

    for (int x = 0; x < 3; x++) {
      RGBBase.Channel ch = RGBBase.Channel.getChannelByArrayIndex(x);
      plots[x] = plot3DLUT(ch, touchedLut[x], ch.color, property,
                           ch.name() + " Touched LUT", "Touched times");
      plots[x].setFixedBounds(2, 0, 100);
    }
    PlotUtils.arrange(plots, 3, true);
    PlotUtils.setVisible(plots);
    return plots;
  }

  public Plot3D[] plotCorrectLUT() {
    Plot3D[] plots = new Plot3D[3];
    double[][][] correctLut = eliminator.correctLut;

    for (int x = 0; x < 3; x++) {
      RGBBase.Channel ch = RGBBase.Channel.getChannelByArrayIndex(x);
      plots[x] = plot3DLUT(ch, correctLut[x], ch.color, property,
                           ch.name() + " Correct LUT", "Correct code");
    }
    PlotUtils.arrange(plots, 3, true);
    PlotUtils.setVisible(plots);
    return plots;
  }

  public void setCrosstalkCorrect(boolean crosstalkCorrect) {
    this.crosstalkCorrect = crosstalkCorrect;
  }

  protected XTalkEliminator eliminator;
  // for TN and most type
  protected XTalkProperty property = XTalkProperty.getRightXTalkProperty();
//  protected XTalkProperty property = XTalkProperty.getLeftXTalkProperty();
  protected MultiMatrixModel mmModel;
  protected AbstractXTalkReconstructor reconstructor;

}
