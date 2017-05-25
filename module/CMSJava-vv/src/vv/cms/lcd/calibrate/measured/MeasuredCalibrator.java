package vv.cms.lcd.calibrate.measured;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.gradient.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import vv.cms.measure.cp.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.util.log.*;
import vv.cms.lcd.material.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 基於量測流程的校正程序
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class MeasuredCalibrator
    implements CalibratorConst, Plottable {

  protected String rootDir = ".";
  public final void setRootDir(String root) {
    this.rootDir = root;
  }

  /**
   *
   * @param logoLCDTaget LCDTarget
   * @param mc MeasuredCalibrator
   */
  protected MeasuredCalibrator(LCDTarget logoLCDTaget,
                               MeasuredCalibrator mc) {
    this(logoLCDTaget, mc.mm, mc.cp, mc.wp, mc.ap, mc.mp);
    this.rootDir = mc.rootDir;
  }

  /**
   *
   * @param logoLCDTaget LCDTarget
   * @param meterMeasurement MeterMeasurement
   * @param p ColorProofParameter
   * @param wp WhiteParameter
   * @param ap GreenAdjustParameter
   * @param mp MeasureParameter
   */
  public MeasuredCalibrator(LCDTarget logoLCDTaget,
                            MeterMeasurement meterMeasurement,
                            ColorProofParameter p,
                            WhiteParameter wp, AdjustParameter ap,
                            MeasureParameter mp) {
    this.cp = p;
    this.wp = wp;
    this.ap = ap;
    this.mp = mp;

    this.mm = meterMeasurement;
    this.ic = p.icBits;
    this.maxValue = p.calibrateBits;
    this.logoLCDTaget = logoLCDTaget;
    this.mi = this.cpm = getCPCodeMeasurement(meterMeasurement, ic, mp);

    List<RGB> cpcodeList = logoLCDTaget.filter.rgbList();
    this.cpcodeRGBArray = RGBArray.toRGBArray(cpcodeList);
    //target的RGB則為0~255間隔為1, 為了是可以在未知cp code的狀況下取得對應的XYZ值
    this.originalCalTarget = LCDTargetUtils.getLCDTargetWithLinearRGB(
        logoLCDTaget, LCDTarget.Number.Ramp256W);
    this.targetxyYArray = originalCalTarget.filter.xyYArray();
    this.gm = new GSDFGradientModel(originalCalTarget);
    if (!this.considerHKEffect) {
      this.gm.setHKStrategy(GSDFGradientModel.HKStrategy.None);
    }
    this.wcc = new WhiteCodeCalculator(cpcodeRGBArray);
  }

  /**
   * 取得cpm並且做設定
   * @param meterMeasurement MeterMeasurement
   * @param icBits MaxValue
   * @param mp MeasureParameter
   * @return CPCodeMeasurement
   */
  final static CPCodeMeasurement getCPCodeMeasurement(MeterMeasurement
      meterMeasurement, RGBBase.MaxValue icBits, MeasureParameter mp) {
    CPCodeMeasurement cpm = CPCodeMeasurement.getInstance(meterMeasurement,
        icBits, cpmBufferFilename, mp);
    cpm.setBufferMeasure(mp.bufferMeasure);
    cpm.setAcceptDifference(mp.CPCodeAcceptDifference);

    return cpm;
  }

  protected MeasureInterface mi;

  private static String cpmBufferFilename = null;
  public final static void setCPMBufferFilename(String filename) {
    cpmBufferFilename = filename;
  }

  public void storeCPCodeMeasurement() {
    cpm.storeBuffer("cpm.buf");
  }

  /**
   * 累積的量測次數
   * @return int
   */
  public int getAccumulateMeasureCount() {
    return cpm.getAccumulateMeasureCount();
  }

  /**
   * 取得校正過程的資訊
   * @return String
   */
  public abstract String getCalibratedInfomation();

  /**
   * 執行校正, 內部呼叫用
   * @return RGB[]
   */
  protected abstract RGB[] _calibrate();

  /**
   * 執行校正
   * @return RGB[]
   */
  public RGB[] calibrate() {
    if (this.maxValue != null) {
      produceStart();
      RGB[] result = _calibrate();
      produceEnd();
      return Arrays.copyOf(result, result.length);
    }
    else {
      throw new IllegalStateException("this.maxValue == null");
    }
  }

  /**
   * 花費的總時間
   * @return long
   */
  public final long getCostTime() {
    return timeConsumption.getCostTime();
  }

  protected void produceStart() {
    timeConsumption.start();
    Logger.log.trace(this.getClass().getName() + " start");
  }

  protected void produceEnd() {
    timeConsumption.end();
    Logger.log.trace(this.getClass().getName() + " end");
    Logger.log.info("costTime: " + timeConsumption.getCostTime());
  }

  protected transient TimeConsumption timeConsumption = new TimeConsumption();

  /**
   * 回傳校正結果, 包過RGB以及目標XYZ等
   * @return List
   */
  public abstract List<Patch> getCalibratedPatchList();

  /**
   * 取得原始的LCDTarget
   * @return LCDTarget
   */
  protected LCDTarget getOriginalTarget() {
    return originalCalTarget;
  }

  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

  protected boolean plotting = false;
  private Plot2D plot;
  private GSDFGradientModel gm;
  protected MeterMeasurement mm;
  /**
   * 將rgb替換為ramp的目標值
   */
  private LCDTarget originalCalTarget;
  private LCDTarget relativeCalTarget;
  private RGB[] cpcodeRGBArray;
  protected RGB.MaxValue ic;
  protected RGB.MaxValue maxValue;
  /**
   * 校正目標值
   */
  protected LCDTarget logoLCDTaget;
  private CPCodeMeasurement cpm;

  //============================================================================
  // 校正參數
  //============================================================================
  protected MeasureParameter mp;
  protected AdjustParameter ap;
  protected ColorProofParameter cp;
  protected WhiteParameter wp;
  //============================================================================

  /**
   * 每次量測的暫存結果
   */
  private LCDTarget measuredLCDTarget;

  /**
   * 目標xyY的陣列
   */
  private CIExyY[] targetxyYArray;

  /**
   * 白點code計算器
   */
  protected WhiteCodeCalculator wcc;

  protected RGB getWhiteCPCode() {
    return cpcodeRGBArray[cpcodeRGBArray.length - 1];
  }

  /**
   * 由targetLogoFilename所取出的原始cp code
   * @return RGB[]
   */
  protected RGB[] getCPCodeRGBArray() {
    return cpcodeRGBArray;
  }

  /**
   * 取得目標xyY的陣列
   * @return CIExyY[]
   */
  protected CIExyY[] getTargetxyYArray() {
    return this.targetxyYArray;
  }

  /**
   * 取得白點的目標xyY
   * @return CIExyY
   */
  protected CIExyY getWhitexyY() {
    int size = targetxyYArray.length;
    return targetxyYArray[size - 1];
  }

  /**
   * 色度是否要做相對調整
   */
  private boolean chromaticityRelative = AutoCPOptions.get(
      "MeasuredCalibrator_ChromaticityRelative");
  /**
   * 是否要考慮HK效應? 暫時先關閉.
   * 因為有些面板的白點不穩定, 當有時量測到的值比白點還要大時, 此時在CIECAM02產生的對照表中,
   * 由於超出最大值, 所以會有clip現象, 這樣的clip現象會讓兩個應該有差異的數值變成相同.
   * 而在找尋最接近值時, 發現兩者相同的情況下, 就會停止繼續找尋.
   */
  private boolean considerHKEffect = AutoCPOptions.get(
      "MeasuredCalibrator_ConsiderHKEffect");

  /**
   * 產生亮度的相對目標值
   * @param measure LCDTarget
   */
  protected final void initRelativeTarget(LCDTarget measure) {
    initRelativeTarget(measure, null);
  }

  protected LCDTarget getWhiteRelativeTarget(LCDTarget measureTarget) {
    return RelativeTarget.
        getLuminanceAndChromaticityRelativeInstance(
            originalCalTarget, measureTarget, cp.turnCode);
  }

  /**
   * 初始化相對Target
   * @param measureTarget LCDTarget 用來產生相對Target的參考量測Target
   * @param channel Channel 產生相對Target的channel
   */
  private final void initRelativeTarget(LCDTarget measureTarget,
                                        RGBBase.Channel channel) {
    if (!this.useRelativeTarget) {
      //如果不採用相對Target就直接結束
      return;
    }
    if (true == chromaticityRelative && cp != null &&
        //色度也做相對調整的狀況
        (channel == RGBBase.Channel.W || channel == null)) {
      //白色的處理
      relativeCalTarget = getWhiteRelativeTarget(measureTarget);
    }
    else {
      //R/G/B頻道的處理
      if (channel != null && channel != RGBBase.Channel.W) {
        relativeCalTarget = RelativeTarget.getLuminanceRelativeInstance(
            originalCalTarget, measureTarget, channel);
      }
      else {
        relativeCalTarget = RelativeTarget.getLuminanceRelativeInstance(
            originalCalTarget, measureTarget, RGBBase.Channel.W);
      }
    }

    LCDTarget.Number number = MeasuredUtils.getMeasureNumber(channel, true, false);
    relativeCalTarget = LCDTargetUtils.getReplacedLCDTarget(relativeCalTarget,
        number);

    //==========================================================================
    // 儲存起來
    //==========================================================================
    try {
      String className = this.getClass().getSimpleName();
      String filename = rootDir + "/" + MeaRelativeLogoFilename + "-[" +
          className + "].logo";
      LCDTarget.IO.store(relativeCalTarget, filename);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================
    //==========================================================================
    this.targetxyYArray = relativeCalTarget.filter.xyYArray();
  }

  protected void loadCPCode(RGB[] rgbArray) {
    CPCodeLoader.load(rgbArray, this.ic);
  }

  /**
   * 將cp code target載到lut裡
   */
  protected void loadCPCodeTarget() {
    loadCPCode(this.cpcodeRGBArray);
  }

  /**
   * 是否產生相對目標值來作校正
   */
  private boolean useRelativeTarget = AutoCPOptions.get(
      "MeasuredCalibrator_UseRelativeTarget");
  protected LCDTarget getCalibratedTarget() {
    return useRelativeTarget ? relativeCalTarget : originalCalTarget;
  }

  protected JNDI jndi = new JNDI();

  public class JNDI
      implements JNDIInterface {

    protected double[] plotDeltaJNDI(String name, boolean plot, List<Patch>
        patchList) {
      double[] delta = calculateDeltaJNDICurve(patchList);
      if (plot && plotting) {
        plotDeltaJNDI(delta, name);
      }
      return delta;
    }

    /**
     * 將measure的Target計算delta JNDI
     * @param measure LCDTarget
     * @return double[]
     */
    protected double[] calculateDeltaJNDICurve(LCDTarget measure) {
      CIEXYZ[] XYZArray = measure.filter.XYZArray();
      if (measure.getNumber() == LCDTargetBase.Number.Ramp256R_W ||
          measure.getNumber() == LCDTargetBase.Number.Ramp256G_W ||
          measure.getNumber() == LCDTargetBase.Number.Ramp256B_W) {
        XYZArray = Arrays.copyOf(XYZArray, XYZArray.length - 1);
      }
      return calculateDeltaJNDICurve(XYZArray);
    }

    /**
     * 從patchList計算delta JNDI
     * @param patchList List
     * @return double[]
     */
    protected double[] calculateDeltaJNDICurve(List<Patch>
        patchList) {
      List<CIEXYZ> XYZList = Patch.Filter.XYZList(patchList);
      CIEXYZ[] XYZArray = XYZList.toArray(new CIEXYZ[XYZList.size()]);
      double[] deltaJNDICurve = calculateDeltaJNDICurve(XYZArray);
      return deltaJNDICurve;
    }

    /**
     * 從XYZArray計算delta JNDI
     * @param XYZArray CIEXYZ[]
     * @return double[]
     */
    protected double[] calculateDeltaJNDICurve(CIEXYZ[] XYZArray) {
      double[] measureJNDICurve = gm.getJNDIndexCurve(XYZArray);
      double[] deltaJNDICurve = DoubleArray.minus(measureJNDICurve,
                                                  getTargetJNDICurve());
      return deltaJNDICurve;

    }

    private double[] targetJNDICurve;

    protected double[] getJNDICurve(LCDTarget lcdTarget) {
      return gm.getJNDIndexCurve(lcdTarget.filter.XYZArray());
    }

    protected double[] getTargetJNDICurve() {
      if (targetJNDICurve == null) {
        targetJNDICurve = getJNDICurve(getCalibratedTarget());
      }
      return targetJNDICurve;
    }

    protected void setTargetJNDICurve(double[] targetJNDICurve) {
      this.targetJNDICurve = targetJNDICurve;
    }

    protected final double getJNDI(LCDTargetInterpolator interp,
                                   double code,
                                   RGBBase.Channel channel,
                                   GSDFGradientModel gm) {
      Patch patch = interp.getPatch(channel, code);
      CIEXYZ XYZ = patch.getXYZ();
      return gm.getJNDIndex(XYZ);
    }

    /**
     * 計算JNDI, 搭配GSDFGradientModel計算出JNDI
     * @param XYZ CIEXYZ
     * @return double
     */
    public double getJNDI(CIEXYZ XYZ) {
      return gm.getJNDIndex(XYZ);
    }

    /**
     * 計算JNDI
     * @param lcdTarget LCDTarget
     * @param code int
     * @return double
     */
    protected double getJNDI(LCDTarget lcdTarget, int code) {
      Patch patch = lcdTarget.getPatch(RGBBase.Channel.W, code,
                                       RGB.MaxValue.Int8Bit);
      return getJNDI(patch.getXYZ());
    }

    protected double getJNDI(LCDTargetInterpolator interp, double code,
                             RGBBase.Channel channel) {
      Patch patch = interp.getPatch(channel, code);
      return getJNDI(patch.getXYZ());
    }

    protected Plot2D plotDeltaJNDI(double[] deltaJNDICurve,
                                   String name) {
      if (!plotting) {
        return null;
      }
      if (plot == null) {
        plot = Plot2D.getInstance("delta JNDI");
      }

      plot.addLinePlot(name, 0, deltaJNDICurve.length - 1, deltaJNDICurve);

      plot.setAxeLabel(0, "code");
      plot.setAxeLabel(1, "deltaJNDI");
      plot.addLegend();
      plot.setVisible();
      plot.setFixedBounds(0, 0, 255);
      return plot;
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 用來搭配觸發量測的物件
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected static class Trigger
      implements MeasureTrigger {
    private boolean[] calibrated;
    protected Trigger(boolean[] calibrated) {
      this.calibrated = calibrated;
    }

    /**
     * hasNextMeasure
     * 是否還有更多量測需要進行
     *
     * @return boolean
     */
    public boolean hasNextMeasure() {
      return getUncalibratedCount() != 0;
    }

    public int getUncalibratedCount() {
      int count = 0;
      for (boolean cal : calibrated) {
        if (cal == false) {
          count++;
        }
      }
      return count;
    }

  }

  /**
   * 校正的初始化
   * 初始化作了:
   * 1. 載入基礎cp code
   * 2. 量測256色或者四色
   * 3. 依照上一步驟的量測, 產生 相對LCDTarget, 此為新的目標Target
   * 相對LCDTarget的用意在於, 為了降低LCD亮度的變化影響到校正結果.
   *
   * @param channel Channel
   * @param ramp256Measure boolean 是否要對256灰階全量測?
   * @return LCDTarget
   */
  protected final LCDTarget initMeasure(RGBBase.Channel channel,
                                        boolean ramp256Measure) {
    cpm.reset();
    //==========================================================================
    // 初始量測
    //==========================================================================
    LCDTargetBase.Number number = MeasuredUtils.getMeasureNumber(channel,
        ramp256Measure, false);
    LCDTarget ramp = null;
    if (number == LCDTargetBase.Number.FiveColor) {
      //只量測五色, 包含RGBWK. 由cpcode產生出對應的RGBWK陣列
      RGB[] wrgbk = new RGB[5];
      for (int x = 0; x < 4; x++) {
        //w r g b
        wrgbk[x] = (RGB) cpcodeRGBArray[cpcodeRGBArray.length - 1].clone();
      }
      for (int x = 1; x < 4; x++) {
        //r g b
        RGBBase.Channel ch = RGBBase.Channel.getChannel(x);
        wrgbk[x].reserveValue(ch);
      }
      //black
      wrgbk[4] = (RGB) cpcodeRGBArray[0].clone();
      ramp = this.measure(wrgbk);
//      ramp = this.measureWhite(wrgbk);
    }
    else if (number == LCDTargetBase.Number.BlackAndWhite) {
      RGB[] bw = new RGB[2];
      bw[0] = (RGB) cpcodeRGBArray[0].clone();
      bw[1] = (RGB) cpcodeRGBArray[cpcodeRGBArray.length - 1].clone();
      ramp = mp.whiteSequenceMeasure ? this.measureWhite(bw) : this.measure(bw);
    }
    else {
      //直接將cpcode全部進行量測
      ramp = this.measure(this.cpcodeRGBArray, channel, null, false, true);
      number = ramp.getNumber();
    }

    ramp = LCDTargetUtils.getLCDTargetWithLinearRGB(ramp, number);

    this.initRelativeTarget(ramp, channel);
    return ramp;
    //==========================================================================
  }

  protected void setChromaticityRelative(boolean chromaticityRelative) {
    this.chromaticityRelative = chromaticityRelative;
  }

  /**
   * 是否要log最detail
   */
  private final static boolean LoggingDetail = AutoCPOptions.get(
      "MeasuredCalibrator_LoggingDetail");

  /**
   * log最detail的資訊
   * @param msg String
   */
  protected final static void traceDetail(String msg) {
    if (LoggingDetail) {
      Logger.log.trace(msg);
    }
  }

  /**
   * 把RGB設定為流水號RGB
   * @param patchList List
   * @param channel Channel
   */
  private void setSerialIntegerRGB(List<Patch> patchList,
      RGBBase.Channel channel) {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch patch = patchList.get(x);
      RGB rgb = (RGB) patch.getRGB().clone();
      RGB orgRGB = (RGB) patch.getOriginalRGB().clone();
      rgb.setValue(channel, x);
      orgRGB.setValue(channel, x);
      Patch.Operator.setRGB(patch, rgb);
      Patch.Operator.setOriginalRGB(patch, orgRGB);
    }
  }

  /**
   * 非量測灰階時使用
   *
   * @param rgbArray RGB[]
   * @return LCDTarget
   */
  protected LCDTarget measure(RGB[] rgbArray) {
    List<Patch> patchList = cpm.measure(rgbArray, true);
    LCDTarget measureLCDTarget = LCDTarget.Instance.get(patchList,
        LCDTarget.Number.Unknow, this.mm.isDo255InverseMode());
    this.measuredLCDTarget = measureLCDTarget;
    return measureLCDTarget;
  }

  protected LCDTarget measureWhite(RGB[] whiteRGBArray) {
    int sequenceMeasureCount = mp.sequenceMeasureCount;
    RGB[] measureRGBArray = getMeasureWhiteRGBArray(whiteRGBArray,
        sequenceMeasureCount);
    List<Patch>
        patchList = cpm.directMeasureResult(RGBArray.toRGBList(measureRGBArray)).
        result;
    int width = sequenceMeasureCount + 1;
    int realSize = patchList.size() / width;
    List<Patch> realPatchList = new ArrayList<Patch> (realSize);
    for (int x = 0; x < realSize; x++) {
      int index = -1 + width * (x + 1);
      Patch p = patchList.get(index);
      realPatchList.add(p);
    }
    LCDTarget measureLCDTarget = LCDTarget.Instance.get(realPatchList,
        LCDTarget.Number.Unknow, this.mm.isDo255InverseMode());
    return measureLCDTarget;
  }

  final static RGB[] getMeasureWhiteRGBArray(RGB[] whiteRGBArray,
                                             int sequenceMeasureCount) {
    int size = whiteRGBArray.length;
    RGB[] result = new RGB[size * (1 + sequenceMeasureCount)];
    int index = 0;
    for (int x = 0; x < size; x++) {
      RGB measurergb = whiteRGBArray[x];
      for (int c = 0; c < sequenceMeasureCount; c++) {
        RGB rgb = (RGB) measurergb.clone();
        rgb.addValues( - (sequenceMeasureCount - c), RGB.MaxValue.Double255);
        rgb.rationalize();
        result[index++] = rgb;
      }
      result[index++] = measurergb;
    }
    return result;
  }

  /**
   * 依照設定並且量測rgbArray的內容, 並回傳為LCDTarget
   * 量測灰階時使用
   *
   * @param rgbArray RGB[]
   * @param channel Channel
   * @param whiteRGB RGB 在非量測W的時候, 要採用的white RGB code. 如果設定為null,
   *  則自動採用rgbArray的最後一個當作white
   * @param serialIntegerRGB boolean 量測後將RGB替換成整數RGB(模擬LCDTarget的ramp量測效果)
   * @param withWhite boolean 如果沒有白點的時候是否要帶白
   * @return LCDTarget
   */
  protected LCDTarget measure(final RGB[] rgbArray, RGBBase.Channel channel,
                              RGB whiteRGB, boolean serialIntegerRGB,
                              boolean withWhite) {
    /**
     * @todo H acp 改成不要過濾出要校正的頻道, 而是在送出RGB的時候只送要的頻道
     */
    RGB[] measureRGBArray = MeasuredUtils.getMeasureRGBArray(rgbArray,
        channel);
    List<Patch> patchList = cpm.measure(measureRGBArray, true);
    if (serialIntegerRGB) {
      //是否把RGB替換為連續的整數RGB
      setSerialIntegerRGB(patchList, channel);
    }
    if (whiteRGB == null) {
      whiteRGB = rgbArray[rgbArray.length - 1];
    }
    if (channel != RGBBase.Channel.W && withWhite) {
      //如果沒有白點又需要帶白點
      Patch white = cpm.measure(whiteRGB, true);
      if (serialIntegerRGB) {
        //替換掉RGB
        Patch.Operator.setRGB(white, RGB.White);
        Patch.Operator.setOriginalRGB(white, RGB.White);
      }
      patchList.add(white);
    }
    //判斷適當的nuber
    LCDTarget.Number number = MeasuredUtils.getMeasureNumber(channel, true,
        withWhite);
    //形成LCDTarget
    LCDTarget measureLCDTarget = LCDTarget.Instance.get(patchList, number,
        this.mm.isDo255InverseMode());
    this.measuredLCDTarget = measureLCDTarget;
    return measureLCDTarget;
  }

  protected LCDTarget getMeasuredLCDTarget() {
    return measuredLCDTarget;
  }

}
