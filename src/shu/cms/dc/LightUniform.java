package shu.cms.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.depend.*;
import shu.math.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �ե����������ë�
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class LightUniform {

  public final static double GRAY_CARD_WIDTH = 25.4;
  public final static double GRAY_CARD_HEIGHT = 20.32;

  public enum LightUniformColorSpace {
    HSB, YUV
  }

  public enum UniformBase {
    Center, TopCenter
  }

  /**
   * HSB���ĪG�u��YUV
   */
  protected final static LightUniformColorSpace LIGHT_UNIFORM_COLOR_SPACE =
      LightUniformColorSpace.HSB;

  protected final static UniformBase UNIFORM_BASE = UniformBase.Center;

  protected List<RGB> rgbList;
  protected CXF rgbCxF;
  protected int lgorowLength;
  protected double[][] regressionCoefficient;
//  protected double[][][] regressionCoefficient;
  protected Polynomial.COEF_3 polynomialCoef;
  protected double rmsd;

  /**
   *
   * @param device Camera
   * @param targetIlluminant Calibrated
   * @param polynomialCoef COEF_3
   * @deprecated
   */
  public LightUniform(DCTarget.Camera device,
                      LightSource.Calibrated targetIlluminant,
                      Polynomial.COEF_3 polynomialCoef) {
    init(device, targetIlluminant);
    this.polynomialCoef = polynomialCoef;

    regressionCoefficient = processRegressionCoefficient(polynomialCoef);
  }

  /**
   * �ѵ{���۰ʴM��̨Ϊ�����
   * @param device Device
   * @param targetIlluminant Type
   * @deprecated
   */
  public LightUniform(DCTarget.Camera device,
                      LightSource.Calibrated targetIlluminant) {
    init(device, targetIlluminant);
    this.polynomialCoef = findBestPolynomialCoef();
    regressionCoefficient = processRegressionCoefficient(polynomialCoef);
  }

  /**
   * ���̾A�h����
   * �p�G�̷Ӧ^�k��rms������,�ҵ������O�^�k���C�@�Ӽ˥����٭��
   * �p�G�^�k��w���Ӽ˥��d��,�p���rg�Ŷ��U���ܲ���,�~������䤣���îե����ĪG
   * �Ӽ˥��ƬƦܥi�H�������쥻��2���ƦܬO4��
   * @return COEF_3
   */
  protected Polynomial.COEF_3 findBestPolynomialCoef() {
    Polynomial.COEF_3 bestPolynomialCoef = null;
    double bestVerifyFactor = Double.MAX_VALUE;

    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      polynomialCoef = c;
      regressionCoefficient = processRegressionCoefficient(polynomialCoef);
      double verifyFactor = getVerifyFactor();
      if (verifyFactor < bestVerifyFactor) {
        bestVerifyFactor = verifyFactor;
        bestPolynomialCoef = c;
      }

    }

    return bestPolynomialCoef;
  }

  /**
   *
   * @param device Camera
   * @param targetIlluminant Calibrated
   * @deprecated
   */
  protected void init(DCTarget.Device source,
                      LightSource.Calibrated targetIlluminant) {
//    String grayCardCxFFilename = DCTarget.Filename.produceRGBCxFFilename(source,
//        targetIlluminant, DCTarget.Chart.GrayCard);
    String grayCardCxFFilename = null;
    rgbCxF = CXFOperator.openCXF(grayCardCxFFilename);
    CXFOperator rgbCxfOperator = new CXFOperator(rgbCxF);
    rgbList = rgbCxfOperator.getRGBList();
    lgorowLength = rgbCxfOperator.getLgorowLength();

  }

  /**
   * ���o���äƫY��
   * @param x double
   * @param y double
   * @param targetBorder double
   * @return double[]
   */
  public double getUniformCoefficient(double x, double y, double targetBorder) {
    return getUniformCoefficient(x, y, targetBorder, 0);
  }

  /**
   * ���o���äƫY��
   * @param x double
   * @param y double
   * @param targetBorder double
   * @param targetXShift double
   * @return double
   */
  public double getUniformCoefficient(double x, double y, double targetBorder,
                                      double targetXShift) {
    double[][] input = new double[][] {
        {
        x + targetXShift, y + targetBorder, 1}
    };
    return PolynomialRegression.getPredict(input, regressionCoefficient,
                                           polynomialCoef)[0][0];
//    return DoublePolynomialRegression.getPredict(input, regressionCoefficient[0],
//                                                 regressionCoefficient[1],
//                                                 polynomialCoef)[0][0];
  }

  public static RGB uniform(RGB input, double uniformCoefficient) {
    RGB result = null;
    RGB.MaxValue maxValue = input.getMaxValue();
    switch (LIGHT_UNIFORM_COLOR_SPACE) {
      case HSB:
        HSV hsb = new HSV(input);
        hsb.V *= uniformCoefficient;
        hsb.V = hsb.V > 100 ? 100 : hsb.V;
        result = hsb.toRGB();
        break;
      case YUV:
        YUV yuv = new YUV(input);
        yuv.Y *= uniformCoefficient;
        yuv.Y = yuv.Y > 1 ? 1 : yuv.Y;
        result = yuv.toRGB();
        break;
    }
    result.changeMaxValue(maxValue);
    return result;
  }

  public DCTarget uniform(DCTarget dcTarget) {
    List<Patch> patchList = dcTarget.getPatchList();
    int size = patchList.size();
    double[] values = new double[3];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();

      double[] xy = processXY(x, size, dcTarget.targetData.lgorowLength,
                              dcTarget.targetData.height,
                              dcTarget.targetData.width);
      RGB result = uniform(rgb, xy[0], xy[1], dcTarget.targetData.bottomBorder);
      rgb.setValues(result.getValues(values));
    }

    return dcTarget;
  }

  /**
   * �i�槡�äƮե�
   * @param input double[]
   * @param x double
   * @param y double
   * @param targetBorder double
   * @return double[]
   */
  public RGB uniform(RGB input, double x, double y,
                     double targetBorder) {
    return uniform(input, getUniformCoefficient(x, y, targetBorder));
  }

  /**
   * �i��^�k�Y�ƪ��B��
   * @param polynomialCoef COEF_3
   * @return double[][]
   */
  protected double[][] processRegressionCoefficient(Polynomial.COEF_3
//  protected double[][][] processRegressionCoefficient(Polynomial.COEF_3
      polynomialCoef) {
    double[][] inputData = processInputData();
    double[][] outputData = processOutputData(rgbList);

    PolynomialRegression polynomialRegression = new PolynomialRegression(
        inputData, outputData, polynomialCoef);
    polynomialRegression.regress();
    rmsd = polynomialRegression.getRMSD();
    return polynomialRegression.getCoefs();

//    DoublePolynomialRegression doublePolynomialRegression = new
//        DoublePolynomialRegression(inputData, outputData, polynomialCoef);
//    doublePolynomialRegression.regress();
//    rms = doublePolynomialRegression.getRMS();
//
//    double[][][] coefficient = new double[][][] {
//        doublePolynomialRegression.getCoefs(),
//        doublePolynomialRegression.getCoefs2()};
//    return coefficient;
  }

  protected double[][] processInputData() {
    int size = rgbList.size();
    double[][] inputData = new double[size][];

    for (int x = 0; x < size; x++) {
      inputData[x] = processXY(x, size, lgorowLength, GRAY_CARD_HEIGHT,
                               GRAY_CARD_WIDTH);
    }

    return inputData;
  }

  /**
   * �Ψ����Ҥ����îե����ĪG.
   * ��k���N�쥻�ǥd��rgb�A�a�J�i��ե�,�M��p���B�Ȫ��зǮt
   * @return double
   */
  protected double getVerifyFactor() {
    int size = rgbList.size();
    double[] bArray = new double[size];

    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      double[] xy = processXY(x, size, lgorowLength, GRAY_CARD_HEIGHT,
                              GRAY_CARD_WIDTH);
      RGB uniformRGB = uniform(rgb, xy[0], xy[1], 0);
      bArray[x] = new HSV(uniformRGB).V;
    }

    return Maths.std(bArray);
  }

  /**
   * ����outputData
   * @param rgbList List
   * @return double[][]
   */
  protected double[][] processOutputData(List<RGB> rgbList) {
    int size = rgbList.size();
    int centerIndex = 0;

    switch (UNIFORM_BASE) {
      case Center: {
        //�Τ��߰����
        int middleLine = size / lgorowLength / 2;
        centerIndex = lgorowLength * middleLine + lgorowLength / 2;
      }
      break;
      case TopCenter: {
        //�γ̤W��̰��
        int middleLine = size / lgorowLength / 2;
        centerIndex = lgorowLength * middleLine;
      }
      break;
    }

    RGB center = rgbList.get(centerIndex);

    double[][] data = new double[size][];
    double centerBrightness = 0;
    switch (LIGHT_UNIFORM_COLOR_SPACE) {
      case HSB:
        centerBrightness = new HSV(center).V;
        break;
      case YUV:
        center.changeMaxValue(RGB.MaxValue.Double1);
        centerBrightness = new YUV(center).Y;
        break;
    }

    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      data[x] = new double[3];
      switch (LIGHT_UNIFORM_COLOR_SPACE) {
        case HSB:

          data[x][0] = centerBrightness / new HSV(rgb).V;
          break;
        case YUV:
          rgb.changeMaxValue(RGB.MaxValue.Double1);
          data[x][0] = centerBrightness / new YUV(rgb).Y;
          break;
      }
      data[x][1] = data[x][2] = 1;

    }
    return data;
  }

  /**
   * �p��ɪ������������
   * @param patchNumber int
   * @param totalPatch int
   * @param lgorowLength int
   * @param targetHeight double
   * @param targetWidth double
   * @return double[]
   */
  protected static double[] processXY(int patchNumber, int totalPatch,
                                      int lgorowLength, double targetHeight,
                                      double targetWidth) {
    int lgorowWidth = totalPatch / lgorowLength;
    double middle = ( (double) lgorowWidth) / 2;

    double patchHeight = targetHeight / lgorowLength;
    double patchWidth = targetWidth / lgorowWidth;

    double y = (lgorowLength - patchNumber % lgorowLength - 0.5) * patchHeight;
    double x = (patchNumber / lgorowLength - middle + 0.5) * patchWidth;

    return new double[] {
        x, y, 1};
  }

  /**
   *
   * @param args String[]
   * @deprecated
   */
  public static void main(String[] args) {

    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      System.out.println(c);
      LightUniform l = new LightUniform(DCTarget.Camera.D70,
                                        LightSource.Calibrated.A,
                                        c);
      System.out.println(l.rmsd);
    }

    LightUniform l = new LightUniform(DCTarget.Camera.D70,
                                      LightSource.Calibrated.A);
    System.out.println(l.polynomialCoef);
  }
}
