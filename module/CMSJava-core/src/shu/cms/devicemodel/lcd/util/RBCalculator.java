package shu.cms.devicemodel.lcd.util;

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
public class RBCalculator {
  protected LCDModel lcdModel;
  protected CIEXYZ whiteXYZ = null;
  private LCDModelExposure adapter;

  /**
   * 設定計算色差所使用的白點
   * @param whiteXYZ CIEXYZ
   */
  public void setWhitePoint(CIEXYZ whiteXYZ) {
    this.whiteXYZ = whiteXYZ;
  }

  public RBCalculator(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
    this.minLuminanceConstraint = lcdModel.flare.getFlare().Y;
    this.adapter = new LCDModelExposure(lcdModel);
  }

  /**
   *
   * @param xyY CIExyY 目標的xyY值
   * @param g double 目標的G值
   * @param GTolerance double G可容許的偏差值
   * @param relativeXYZ boolean XYZ值是否為相對值
   * @return RGB
   */
  public RGB getRB(CIExyY xyY, double g, double GTolerance,
                   boolean relativeXYZ) {
    return getRGBByLumi(xyY, g, GTolerance, RGBBase.Channel.G, relativeXYZ);
  }

  public RGB getWhiteRGB(CIExyY xyY, double targetValue, double tolerance,
                         boolean relativeXYZ) {
    RGB rb = getRBByLumi(xyY, relativeXYZ, tolerance, targetValue, null, true);
    calculateRBDeltaE(rb, xyY, relativeXYZ);
    return rb;
  }

  /**
   * 已知GB(or RB), 取最接近xyY的RGB組合
   * @param xyY CIExyY 目標xyY
   * @param rgb RGB 已知的GB(or RB)
   * @param tolerance double R(or B)的寬容值
   * @param relativeXYZ boolean XYZ(xyY)是否為相對值
   * @param getB boolean
   * @return RGB
   * @deprecated
   */
  public RGB getRorB(CIExyY xyY, final RGB rgb, double tolerance,
                     boolean relativeXYZ, boolean getB) {
    /**
     * 產生方法
     * 1.先用xyY找到最接近的RGB
     * 2.以RGB的R搭配gb的GB, 計算出XYZ
     * 3.調整R, 降低XYZ與xyY的差距
     */
    double targetG = rgb.getValue(RGBBase.Channel.G, RGB.MaxValue.Double255);
    //起始RGB
    RGB initRGB = this.getRB(xyY, targetG, tolerance, relativeXYZ);
    //替換上目標G&B
    initRGB.G = rgb.G;
    MinimisationFunction func = null;

    if (getB) {
      initRGB.R = rgb.R;
      func = new FunctionRorB(initRGB, xyY.toXYZ(), relativeXYZ,
                              RGBBase.Channel.B);
    }
    else {
      initRGB.B = rgb.B;
      func = new FunctionRorB(initRGB, xyY.toXYZ(), relativeXYZ,
                              RGBBase.Channel.R);
    }

    //Create instance of Minimisation
    Minimisation min = new Minimisation();

    // initial estimates
    double[] start = new double[] {
        initRGB.getValue(RGBBase.Channel.R, RGB.MaxValue.Double255)};
    // initial step sizes
    double[] step = new double[] {
        tolerance};
    // convergence tolerance
    double ftol = tolerance;

    min.addConstraint(0, -1, 0);
    min.addConstraint(0, 1, 255);

    // Nelder and Mead minimisation procedure
    min.nelderMead(func, start, step, ftol);

    // get values of y and z at minimum
    double[] param = min.getParamValues();
    initRGB.setValue(RGBBase.Channel.R, param[0], RGB.MaxValue.Double255);
    calculateRBDeltaE(initRGB, xyY, relativeXYZ);
    return initRGB;
  }

  protected class FunctionRorB
      implements MinimisationFunction {
    private RGB rgb;
    private boolean targetRelativeXYZ;
    private CIEXYZ targetXYZ;
    private RGBBase.Channel ch;

    protected FunctionRorB(RGB initGB, CIEXYZ targetXYZ,
                           boolean targetRelativeXYZ, RGBBase.Channel ch) {
      this.rgb = (RGB) initGB.clone();
      this.targetXYZ = targetXYZ;
      this.targetRelativeXYZ = targetRelativeXYZ;
      this.ch = ch;
    }

    public double function(double[] code) {
      rgb.setValue(ch, code[0], RGB.MaxValue.Double255);
      DeltaE dE = lcdModel.calculateGetRGBDeltaE(rgb, targetXYZ,
                                                 targetRelativeXYZ);
      double deltaE = dE.getCIE2000DeltaE();
      return deltaE;
    }
  }

  public RGB getRGB(CIEXYZ XYZ, final RGB initRGB, double tolerance,
                    RGBBase.Channel channel, boolean relativeXYZ) {
    RGB rgb = (RGB) initRGB.clone();
    MinimisationFunction func = new FunctionRorB(initRGB, XYZ, relativeXYZ,
                                                 channel);

    //Create instance of Minimisation
    Minimisation min = new Minimisation();

    // initial estimates
    double[] start = new double[] {
        rgb.getValue(channel, RGB.MaxValue.Double255)};
    // initial step sizes
    double[] step = new double[] {
        RGB.MaxValue.Int10Bit.getStepIn255()};
    // convergence tolerance
    double ftol = tolerance;

    min.addConstraint(0, -1, 0);
    min.addConstraint(0, 1, 255);

    // Nelder and Mead minimisation procedure
    min.nelderMead(func, start, step, ftol);

    // get values of y and z at minimum
    double[] param = min.getParamValues();
//    System.out.println("min:" + min.getMinimum() + " " + param[0]);
    rgb.setValue(channel, param[0]);
    calculateRBDeltaE(rgb, new CIExyY(XYZ), relativeXYZ);
    return rgb;
  }

  /**
   * 已知目標channel ch, 且已知目標channel對應的值targetValue, 要求相對應的xyY下的RGB
   * @param xyY CIExyY 已知xyY
   * @param targetValue double 已知數值
   * @param tolerance double
   * @param ch Channel 已知數值(targetValue)的channel
   * @param relativeXYZ boolean XYZ(xyY)是否為相對值
   * @return RGB
   */
  public RGB getRGBByLumi(CIExyY xyY, double targetValue, double tolerance,
                          RGBBase.Channel ch, boolean relativeXYZ) {
    RGB rb = getRBByLumi(xyY, relativeXYZ, tolerance, targetValue, ch, false);
    calculateRBDeltaE(rb, xyY, relativeXYZ);
    return rb;
  }

  private void calculateRBDeltaE(RGB rb, CIExyY xyY, boolean relativeXYZ) {
    //==========================================================================
    // 計算inverseLab的deltaE
    //==========================================================================
    _getRBDeltaE = getDeltaE(rb, xyY, relativeXYZ);
    //==========================================================================
  }

  private DeltaE getDeltaE(RGB rb, CIExyY xyY, boolean relativeXYZ) {
    //==========================================================================
    // 計算inverseLab的deltaE
    //==========================================================================
    CIEXYZ XYZ = xyY.toXYZ();
    //因為只要比較色度, 所以將亮度轉到跟計算而得的rb的亮度相同
    CIEXYZ rbXYZ = lcdModel.getXYZ(rb, relativeXYZ);
//    XYZ.scaleY(rbXYZ);
    DeltaE dE = lcdModel.getDeltaE(XYZ, rbXYZ);
    return dE;
    //==========================================================================
  }

  public DeltaE getRBDeltaE() {
    return _getRBDeltaE;
  }

  /**
   * 利用優化的方式調整亮度, 找到最接近的解
   * @param targetxyY CIExyY 目標的xyY
   * @param relativeXYZ boolean 目標XYZ(xyY)是否是相對值
   * @param tolerance double 寬容值
   * @param targetValue double 目標數值
   * @param ch Channel 目標數值的channel
   * @param whiteRGB boolean 是否是whiteRGB;
   * 如果是whiteRGB, 則忽略ch的設定, 目標數值會以最大數值的channel為主,
   *  且每次maxLuminanceConstraint會略減, 為確保一定要找到結果.
   * @return RGB
   */
  private RGB getRBByLumi(final CIExyY targetxyY, boolean relativeXYZ,
                          double tolerance, double targetValue,
                          RGBBase.Channel ch, boolean whiteRGB) {

    double initStep = INIT_STEP;

    //將亮度限制在螢幕的最大亮度
    if (maxLuminanceConstraint == -1) {
      this.luminanceConstraint = this.lcdModel.getLuminance().Y;
    }
    else {
      this.luminanceConstraint = maxLuminanceConstraint;
      initStep = 1;
    }
    touchMaxIterativeTime = false;

    RGB minDeltaGRGB = null;
    double absDelta = Double.MAX_VALUE;
    double minDelta = Double.MAX_VALUE;
    double minDeltaE = Double.MAX_VALUE;
    CIExyY clone = (CIExyY) targetxyY.clone();
    this.targetChannel = ch;
    double dEIndex = Double.MAX_VALUE;

    for (int x = 0;
         x < MAX_ITERATIVE_TIME && (absDelta > tolerance || dEIndex > .25);
         x++, initStep *= 2) {
      RGB rgb = getRB0(clone, relativeXYZ, initStep, targetValue, tolerance,
                       whiteRGB, ch);
      double estimate = 0;
      if (whiteRGB) {
        estimate = rgb.getValue(rgb.getMaxChannel(), RGB.MaxValue.Double255);
      }
      else {
        estimate = rgb.getValue(ch, RGB.MaxValue.Double255);
      }
      absDelta = Math.abs(estimate - targetValue);

      DeltaE ciede = getDeltaE(rgb, targetxyY, relativeXYZ);
//      dEIndex = ciede.getCIE2000DeltaE();
      dEIndex = getDeltaIndex(ciede);

      if (absDelta < minDelta && dEIndex < minDeltaE) {
        minDelta = absDelta;
        minDeltaE = dEIndex;
        minDeltaGRGB = rgb;
      }
      //為了提高找到的機率, 每一次迴圈, 都稍微降低(增加)其亮度為0.9倍
      clone.Y *= 0.9;

      if (whiteRGB && luminanceConstraint != -1) {
        luminanceConstraint *= 0.99;
      }

      if (x == (MAX_ITERATIVE_TIME - 1)) {
        touchMaxIterativeTime = true;
      }
    }
    return minDeltaGRGB;
  }

  protected boolean touchMaxIterativeTime = false;
  public boolean touchMaxIterativeTime() {
    return touchMaxIterativeTime;
  }

  //亮度的起始迭代step
  protected final static double INIT_STEP = 1;
  //最大迭代次數
  protected final static int MAX_ITERATIVE_TIME = 180;
  //亮度限制
  protected double maxLuminanceConstraint = -1;
  //亮度限制
  protected double minLuminanceConstraint = 0;
  /**
   * 供運算用的亮度限制
   */
  private double luminanceConstraint = -1;

  private RGB getRB0(CIExyY targetxyY, boolean relativeXYZ,
                     double initStep, double targetValue, double tolerance,
                     boolean whiteRGB, RGBBase.Channel ch) {

    //Create instance of Minimisation
    Minimisation min = new Minimisation();

    // initial estimates
    double[] start = new double[] {
        whiteRGB ? luminanceConstraint * .95 : targetxyY.Y};

    // initial step sizes
    double[] step = new double[] {
        initStep};

    min.addConstraint(0, -1, minLuminanceConstraint);
    if (luminanceConstraint != -1) {
      min.addConstraint(0, 1, luminanceConstraint);
    }

    FunctionRBLumi func = new FunctionRBLumi( (CIExyY) targetxyY.clone(), ch,
                                             targetValue, relativeXYZ, whiteRGB);

    // Nelder and Mead minimisation procedure
    min.nelderMead(func, start, step, tolerance);

    // get values of y and z at minimum
    double[] param = min.getParamValues();
    targetxyY.Y = param[0];
    CIEXYZ getRBXYZ = CIExyY.toXYZ(targetxyY);
    RGB rgb = adapter.getRGB(getRBXYZ, relativeXYZ, true);
    return rgb;
  }

  protected RGBBase.Channel targetChannel = RGBBase.Channel.G;
  protected DeltaE _getRBDeltaE;

  protected class FunctionRBLumi
      implements MinimisationFunction {
    private CIExyY iterativexyY;
    private RGBBase.Channel targetChannel = RGBBase.Channel.G;
    private double targetValue;
    private boolean targetRelativeXYZ;
    private boolean whiteRGBMode = false;

    protected FunctionRBLumi(CIExyY iterativexyY, RGBBase.Channel targetChannel,
                             double targetValue, boolean targetRelativeXYZ
                             , boolean whiteRGBMode) {
      this.iterativexyY = iterativexyY;
      this.targetChannel = targetChannel;
      this.targetValue = targetValue;
      this.targetRelativeXYZ = targetRelativeXYZ;
      this.whiteRGBMode = whiteRGBMode;
    }

    /**
     * 最佳化目標函數.
     * 用最佳化的方式,在xyY domain上調整亮度,使其亮度對應的G值符合所需.
     * delta為目標函數,不斷調整Y得到最小的delta,為最佳化的過程.
     * @param Y double[]
     * @return double
     */
    public double function(double[] Y) {
      iterativexyY.Y = Y[0];
      CIEXYZ resultXYZ = CIExyY.toXYZ(iterativexyY);
      RGB rgb = adapter.getRGB(resultXYZ, targetRelativeXYZ, true);
      double estimate = 0;
      if (whiteRGBMode) {
        estimate = rgb.getValue(rgb.getMaxChannel(), RGB.MaxValue.Double255);
      }
      else {
        estimate = rgb.getValue(targetChannel, RGB.MaxValue.Double255);
      }
      double delta = Math.abs(targetValue - estimate);

      DeltaE dE = getDeltaE(rgb, iterativexyY, targetRelativeXYZ);
      double result = getDeltaIndex(dE) + delta;
      return result;
    }
  }

  private boolean concernChromaticityOnly = true;

  private double getDeltaIndex(DeltaE dE) {
    if (concernChromaticityOnly) {
      return dE.getCIE2000Deltaab();
    }
    else {
      return dE.getCIE2000DeltaE();
    }
  }

  public static void main(String[] args) {
    ProfileColorSpaceModel model = new ProfileColorSpaceModel(RGB.ColorSpace.
        sRGB);
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);

    RBCalculator calc = new RBCalculator(model);
    CIEXYZ XYZ = model.getXYZ(new RGB(30, 40, 50), false);
    RGB rgb = model.getRGB(XYZ, false);
    RGB rb = calc.getRB(new CIExyY(XYZ), rgb.G + 20, 0.25, false);
    DeltaE de = calc.getRBDeltaE();
    System.out.println(de.getCIE2000DeltaE());
    System.out.println(de.getCIE2000Deltaab());
    System.out.println(de.getCIE2000DeltaL());
    CIEXYZ XYZ1 = model.getXYZ(rgb, false);
    CIEXYZ XYZ2 = model.getXYZ(rb, false);
    System.out.println(new CIExyY(XYZ1));
    System.out.println(new CIExyY(XYZ2));
//    System.out.println("");
  }

  /**
   * 迭代時的最大亮度限制.
   * 對於gamma反轉的面板很有用處.
   * @param maxLuminanceConstraint double
   */
  public void setMaxLuminanceConstraint(double maxLuminanceConstraint) {
    this.maxLuminanceConstraint = maxLuminanceConstraint;
  }
}
