package shu.cms.lcd.calibrate.gsdf;

import java.util.*;

import org.apache.commons.collections.primitives.*;
import shu.cms.hvs.*;
import shu.cms.hvs.gradient.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �NJNDIArray�@lowPass��filter�B�z, �B�i�H����filter��b�~�t��, ��filter����J�����S
 * ���|�P��l��Ʈt�ӻ�.
 *
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GSDFLowPassFilter
    implements Plottable {

  public static void main(String[] args) {
    double[] input = DoubleArray.buildX(0, 255, 256);
    double[] output = GammaFinder.gammaCurve(input, input[input.length - 1], 2.2);
    output = DoubleArray.plus(output, 0.2);
    double[] jndi = GSDF.getJNDICurve(output, GSDF.DICOM);
    double[] jndi2 = DoubleArray.copy(jndi);
    Random random = new Random(1);
    for (int x = 0; x < jndi.length; x++) {
      jndi2[x] = jndi[x] + random.nextDouble() * 1;
    }

    GSDFLowPassFilter filter = new GSDFLowPassFilter();
//    filter.setDeltaThreshold(2);
//    double[] result = filter.lowPass(jndi2, 10);
    double[] result = filter.lowPass(jndi2, false, false, true);
    System.out.println("FilterTimes: " + filter.getFilterTimes());
    System.out.println("1FilterTimes: " + filter.getPart1FilterTimes());
    System.out.println("2FilterTimes: " + filter.getPart2FilterTimes());
    System.out.println("CoverRange: " + filter.getCoverRange());

    double[] jndip = Maths.firstOrderDerivatives(jndi);
    double[] jndi2p = Maths.firstOrderDerivatives(jndi2);
//    double[] lpp = Maths.firstOrderDerivatives(result);

//    FastCosineTransformer fct = new FastCosineTransformer();
//    double[] fctTransform = null;
//    double[] fctInverse = null;
//    double[] copyInverse = null;
//
//    try {
//      double[] source = new double[257];
//      System.arraycopy(jndi2p, 0, source, 0, jndi2p.length);
//      fctTransform = fct.transform2(source);
//      fctInverse = fct.inversetransform2(fctTransform);
//
//      double[] copy = DoubleArray.copy(fctTransform);
//      for (int x = 50; x < copy.length; x++) {
//        copy[x] = 0;
//      }
//      copyInverse = fct.inversetransform2(copy);
//
//      Plot2D plot = Plot2D.getInstance("dct");
//      plot.addLinePlot("fctTransform", 0, fctTransform.length - 1, fctTransform);
//      plot.addLinePlot("copy", 0, copy.length - 1, copy);
//      plot.setVisible();
//
//    }
//    catch (IllegalArgumentException ex) {
//      ex.printStackTrace();
//    }
//    catch (MathException ex) {
//      ex.printStackTrace();
//    }
//
//    Plot2D plot = Plot2D.getInstance();
//    plot.addLinePlot("jndi2'", 0, jndi2p.length - 1, jndi2p);
//    plot.addLinePlot("fctInverse", 0, fctInverse.length - 1, fctInverse);
//    plot.addLinePlot("copyInverse", 0, copyInverse.length - 1, copyInverse);
//    plot.setVisible();

  }

  /**
   * ���ҥ[�t�ת���ƬO�_�ŦX
   * @param originalData double[]
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData) {
    return checkPrimeData(originalData, true, true);
  }

  /**
   * ���ҥ[�t�ת���ƬO�_�ŦX
   * @param originalData double[]
   * @param part1Check boolean ��part1������
   * @param part2Check boolean ��part2������
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                boolean part1Check,
                                                boolean part2Check) {
    //�@���L����T
    double[] primeData = Maths.firstOrderDerivatives(originalData);
    int size = primeData.length;
    //���[�t�׳̤j�I, �H������part1�H��part2
    int maxIndex = Maths.maxIndex(primeData);

    if (maxIndex == 0) {
      return false;
    }

    if (part1Check && !checkPrimeData(originalData, 1, maxIndex, true)) {
      return false;
    }
    if (part2Check && !checkPrimeData(originalData, maxIndex, size, false)) {
      return false;
    }

    return true;
  }

  /**
   * ����prime�ɩү�Ԩ���threshold�j�p
   */
  private static double PrimeCheckThreshold = 0.1;
  /**
   * �]�w����prime�ɩү�Ԩ���threshold�j�p
   * @param primeCheckThreshold double
   */
  public static void setPrimeCheckThreshold(double primeCheckThreshold) {
    PrimeCheckThreshold = primeCheckThreshold;
  }

  /**
   * �ˬd�[�t�ת���T.
   * �bcheckStart��checkEnd���϶�, �ˬd�ͶլO�_�Prasing�ҫ��w���۲�.
   *
   * @param originalData double[]
   * @param checkStart int
   * @param checkEnd int
   * @param rasing boolean �Y��true�h�ˬd�O�_���W��, �Y��false�h�ˬd�O�_���U��.
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                int checkStart, int checkEnd,
                                                boolean rasing) {
    return checkPrimeData(originalData, checkStart, checkEnd, rasing,
                          PrimeCheckThreshold);
  }

  /**
   * �ˬdprime�O�_�ŦX�һ�(rasing �� faling), �åB�bcheckStart�PcheckEnd�϶�.
   * �p�G�ˬd�S��rasing��faling��, �|�A�ˬd��~�t�O�_�W�Lthreshold, �Y�S�W�L�٬O�{�w�ŦX�һ�.
   *
   * @param originalData double[]
   * @param checkStart int
   * @param checkEnd int
   * @param rasing boolean
   * @param threshold double
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                int checkStart, int checkEnd,
                                                boolean rasing,
                                                double threshold) {
    double[] primeData = Maths.firstOrderDerivatives(originalData);
    for (int x = checkStart; x < checkEnd; x++) {
      boolean verifyOK = rasing ? primeData[x] >= primeData[x - 1] :
          primeData[x] <= primeData[x - 1];
      if (!verifyOK) {
        if (threshold != -1 &&
            Math.abs(primeData[x] - primeData[x - 1]) < threshold) {
          continue;
        }
        else {
          return false;
        }
      }
    }
    return true;

  }

  /**
   * �n��jndiArray�i��times��LP
   *
   * @param jndiArray double[] �n�QLP��JNDI�}�C
   * @param times int LP������
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, int times) {
    return lowPass0(jndiArray, times, times, false, CoverRange, false, false);
  }

  /**
   * ��jndiArray�i��LP
   *
   * @param jndiArray double[] �n�QLP��JNDI�}�C
   * @param probeDelta boolean ����delta
   * @param coverRange int ���part���|��code
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          int coverRange) {
    return lowPass0(jndiArray, MaxFilterTimes, MaxFilterTimes, probeDelta,
                    coverRange, false, false);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �ΨӧP�_�O�_�ŦX��檺���O
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected static class Qualifier {
    /**
     * �n�Q�ΨӧP�_���Ӽ�
     */
    private int size;
    /**
     * �O�_�n�w��prime�@�P�_
     */
    private boolean checkPrimeVerified = true;

    protected Qualifier(int size, boolean checkPrimeVerified) {
      this.size = size;
      this.checkPrimeVerified = checkPrimeVerified;
      deltaArray = new double[size];
      primeVerifiedArray = new boolean[size];
    }

    protected Qualifier(int size) {
      this(size, true);
    }

    /**
     * �~�t���}�C
     */
    private double[] deltaArray;
    /**
     * prime���}�C
     */
    private boolean[] primeVerifiedArray;

    /**
     * ���o�~�t
     * @param index int
     * @return double
     */
    protected double getDelta(int index) {
      return deltaArray[index];
    }

    /**
     * �]�w�ΨӧP�_����T
     * @param index int ���ޭ�
     * @param delta double �~�t��
     * @param verified boolean �O�_�q�L����
     */
    protected void setInfomation(int index, double delta, boolean verified) {
      deltaArray[index] = delta;
      primeVerifiedArray[index] = verified;
    }

    /**
     * ���X�檺index
     * 1.�p�G���ˬdprime, �N�O��delta�̤p��
     * 2.�p�G�n�ˬdprime, ������prime�Ҧ�ok��, �A��o����delta�̤p��
     * @return int
     */
    protected int getQualifiedIndex() {
      if (checkPrimeVerified) {
        int[] indexArray = getPrimeVerfiedOkIndexArray();
        if (indexArray.length == 0) {
          return Maths.minIndex(deltaArray);
        }
        double minDelta = Double.MAX_VALUE;
        int qualifiedIndex = -1;
        for (int index : indexArray) {
          double delta = deltaArray[index];
          if (delta < minDelta) {
            minDelta = delta;
            qualifiedIndex = index;
          }
        }
        return qualifiedIndex;
      }
      else {
        return Maths.minIndex(deltaArray);
      }
    }

    /**
     * ���oprime����ok�����������ޭȰ}�C
     * @return int[]
     */
    private int[] getPrimeVerfiedOkIndexArray() {
      IntList list = new ArrayIntList();
      for (int x = 0; x < size; x++) {
        if (primeVerifiedArray[x] == true) {
          list.add(x);
        }
      }
      return list.toArray();
    }
  }

  /**
   * �۰ʧ��̨Ϊ�CoverRange�U�i�檺LP
   * CoverRange����k�O�Hdelta�̤p��.
   *
   * @param jndiArray double[] �n�QLP��JNDI�}�C
   * @param probeDelta boolean ����delta
   * @param probeSmooth boolean ����smooth
   * @param probePrime boolean �����[�t��
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          boolean probeSmooth, boolean probePrime) {
    return lowPass(jndiArray, probeDelta, probeSmooth, probePrime, true, true);
  }

  private void setFilterInfo(int index, LowPassInfo info) {
    int[] filterTimes = info.getFilterTimes(index);
    setFilterInfo(index, filterTimes[0], filterTimes[1], filterTimes[2]);
  }

  private void setFilterInfo(int coverRange, int filterTimes,
                             int part1FilterTimes, int part2FilterTimes) {
    this.coverRange = coverRange;
    this.filterTimes = filterTimes;
    this.part1FilterTimes = part1FilterTimes;
    this.part2FilterTimes = part2FilterTimes;
  }

  private static class LowPassInfo {
    double[][] resultArray;
    int[][] filterTimesArray;
    Qualifier qualifier;

    private int getQualifiedIndex() {
      return qualifier.getQualifiedIndex();
    }

    private LowPassInfo(int size, boolean probePrime) {
      resultArray = new double[size][];
      filterTimesArray = new int[size][];
      qualifier = new Qualifier(size, probePrime);
    }

    private double[] getResult(int index) {
      return resultArray[index];
    }

    private void setResult(int index, double[] result) {
      resultArray[index] = result;
    }

    private void setInfomation(int index, double delta, boolean verified) {
      qualifier.setInfomation(index, delta, verified);
    }

    private void setInfomation(int index, double delta, boolean verified,
                               int filterTimes,
                               int part1FilterTimes, int part2FilterTimes) {
      qualifier.setInfomation(index, delta, verified);
      setFilterTimes(index, filterTimes, part1FilterTimes, part2FilterTimes);
    }

    private int[] getFilterTimes(int index) {
      return filterTimesArray[index];
    }

    private void setFilterTimes(int index, int filterTimes,
                                int part1FilterTimes, int part2FilterTimes) {
      filterTimesArray[index] = new int[] {
          filterTimes, part1FilterTimes, part2FilterTimes};
    }

    private double getDelta(int index) {
      return qualifier.getDelta(index);
    }
  }

  /**
   * �۰ʧ��̨Ϊ�CoverRange�U�i�檺LP
   * CoverRange����k�O�Hdelta�̤p��.
   *
   * @param jndiArray double[] �n�QLP��JNDI�}�C
   * @param probeDelta boolean ����delta
   * @param probeSmooth boolean ����smooth
   * @param probePrime boolean �����[�t��
   * @param part1LowPass boolean part1�@LP
   * @param part2LowPass boolean part2�@LP
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          boolean probeSmooth, boolean probePrime,
                          boolean part1LowPass, boolean part2LowPass) {
    //==========================================================================
    // �B��򥻸�T
    //==========================================================================
    int maxIndex = getMaxAccelerationIndex(jndiArray);
    int maxCoverRange = maxIndex < MaxCoverRange ? maxIndex : MaxCoverRange;
    LowPassInfo info = new LowPassInfo(maxCoverRange, probePrime);
    int part1Times = part1LowPass ? MaxFilterTimes : 0;
    int part2Times = part2LowPass ? MaxFilterTimes : 0;
    //==========================================================================

    for (int range = 0; range < maxCoverRange; range++) {
      //lowPass�ʧ@
      double[] result = lowPass0(jndiArray, part1Times,
                                 part2Times, probeDelta, range, probeSmooth,
                                 probePrime);

      info.setResult(range, result);
      //�p���l�H��lowpass���᪺�`�~�t
      double delta = Maths.sum(getDeltaJNDI(jndiArray, result));
      boolean checkPrime = checkPrimeData(result);
      info.setInfomation(range, delta, checkPrime, this.filterTimes,
                         this.part1FilterTimes,
                         this.part2FilterTimes);
    }
    //���delta�̤p���èM�w��
    int index = info.getQualifiedIndex();
    setFilterInfo(index, info);

    double[] result = info.getResult(index);
    plot(jndiArray, result, info.getDelta(index));
    return result;
  }

  private void plot(double[] jndiArray, double[] result, double delta) {
    if (plotting) {
      Plot2D plot = Plot2D.getInstance("LowPass Filter");
      double[] orgp = Maths.firstOrderDerivatives(jndiArray);
      double[] resultp = Maths.firstOrderDerivatives(result);
      System.out.println("check: " + checkPrimeData(result));
      plot.addLinePlot("org'", 0, orgp.length - 1, orgp);
      plot.addLinePlot("LP'", 0, resultp.length - 1, resultp);
      plot.addLegend();
      plot.setVisible();
      GSDFGradientModel gm = new GSDFGradientModel(result, false);
      System.out.println("smooth: " + gm.isSmooth());
      System.out.println("FilterTimes: " + getFilterTimes());
      System.out.println("1FilterTimes: " + getPart1FilterTimes());
      System.out.println("2FilterTimes: " + getPart2FilterTimes());
      System.out.println("CoverRange: " + getCoverRange());
      System.out.println("TotalDelta: " + delta);
    }
  }

  private boolean plotting = true;

  /**
   * ���̤j�[�t�ת�index
   * @param jndiArray double[]
   * @return int
   */
  protected final static int getMaxAccelerationIndex(double[] jndiArray) {
    double[] gsdfp = Maths.firstOrderDerivatives(jndiArray);
    int maxIndex = Maths.maxIndex(gsdfp);
    return maxIndex;
  }

  /**
   * part1��part2���|���Ӽ�
   */
  private int coverRange = 0;
  private final static int MaxCoverRange = 20;
  /**
   * LP���`����
   */
  private int filterTimes = 0;
  /**
   * part1 LP������
   */
  private int part1FilterTimes = 0;
  /**
   * part2 LP������
   */
  private int part2FilterTimes = 0;

  private double[] convole(double[] data, double[] kernel, int start, int end) {
    double[] result = Convolution.convole(data, kernel, start, end);
//    try {
//      Thread.sleep(16);
//    }
//    catch (InterruptedException ex) {
//    }
//    Plot2D p = Plot2D.getStaticInstance();
//    p.removeAllPlots();
//    p.addLinePlot("org", 0, 1, data);
//    p.addLinePlot("result", 0, 1, result);
//    p.setVisible();

    return result;
  }

  /**
   * low pass���֤ߵ{��
   *
   * @param jndiArray double[] �n�Q�i��low pass��JNDI�}�C
   * @param part1Times int part1�n�QLP������
   * @param part2Times int part2�n�QLP������
   * @param probeDelta boolean ����delta
   * @param coverRange int part1��part2���|���Ӽ�
   * @param probeSmooth boolean �����O�_smooth
   * @param probePrime boolean ����prime�O�_smooth
   * @return double[]
   */
  protected double[] lowPass0(final double[] jndiArray, final int part1Times,
                              final int part2Times, boolean probeDelta,
                              int coverRange, boolean probeSmooth,
                              boolean probePrime) {
    double[] gsdfp = Maths.firstOrderDerivatives(jndiArray);
    //���[�t�ת�����I
    int maxIndex = Maths.maxIndex(gsdfp);
    double[] lowPass = gsdfp;
    //filter�����k�s
    filterTimes = part1FilterTimes = part2FilterTimes = 0;

    //==========================================================================
    // �]�w�⦸low-pass���Ѽ�
    //==========================================================================
    int parameter0end = maxIndex + coverRange;
    int[][] parameters = new int[][] {
        new int[] {
        part1Times, 0, parameter0end <= 255 ? parameter0end : 255},
        new int[] {
        part2Times, maxIndex - coverRange, lowPass.length}
    };
    double[][] kernels = new double[][] {
        kernel1, kernel2};
    //==========================================================================

    int[] partFilterTimes = new int[2];
    //�P�_�[�t�׬Orasing or faling
    boolean rasing = true;

    //==========================================================================
    // ��part1��part2�@low-pass
    //==========================================================================
    for (int x = 0; x < 2; x++) {
      int[] parameter = parameters[x];
      double[] kernel = kernels[x];

      for (int y = 0; y < parameter[0]; y++) {
        int start = parameter[1];
        int end = parameter[2];
        double[] result = lowPass;
        result = convole(result, kernel, start, end);

        if (probeDelta && !probeDeltaThreshold(jndiArray, result)) {
          //�p�Gdelta�W�Lthreshold����, �N����
          break;
        }
        double[] lowPassGSDF = recoverGSDF(jndiArray, result);
        if (probeSmooth && probeSmooth(lowPassGSDF)) {
          //�p�G�w�gsmooth, �N����, �]���S���~��LP�����n�F
          //�i�����O, �o�䪺smooth�O���ϰ쪺smooth, ���骺smooth�٬O�L�k�P�O
          break;
        }
        if (probePrime &&
            this.checkPrimeData(lowPassGSDF, start + 1, end, rasing)) {
          break;
        }

        filterTimes++;
        lowPass = result;
        partFilterTimes[x]++;
      }
      rasing = !rasing;
    }
    //==========================================================================

    part1FilterTimes = partFilterTimes[0];
    part2FilterTimes = partFilterTimes[1];

    double[] finalResult = recoverGSDF(jndiArray, lowPass);
    return finalResult;
  }

  /**
   * ����delta�O�_�W�Xthreshold
   * @param jndiArray double[]
   * @param lowPassJNDIPrime double[]
   * @return boolean
   */
  protected boolean probeDeltaThreshold(double[] jndiArray,
                                        double[] lowPassJNDIPrime) {
    double[] lpJNDIArray = recoverGSDF(jndiArray, lowPassJNDIPrime);
    boolean under = isUnderDeltaThreshold(jndiArray, lpJNDIArray);
    return under;
  }

  /**
   * �����O�_smooth
   * @param lowPassJNDIPrime double[]
   * @return boolean
   */
  protected boolean probeSmooth(double[] lowPassJNDIPrime) {
    GSDFGradientModel gm = new GSDFGradientModel(lowPassJNDIPrime, false);
    return gm.isSmooth();
  }

  private final static int MaxFilterTimes = 1000;
  private final static int CoverRange = 1;

  /**
   * �p��jndiArray1�PjndiArray2��delta�O�_�W�Lthreshold
   * @param jndiArray1 double[]
   * @param jndiArray2 double[]
   * @return boolean
   */
  protected boolean isUnderDeltaThreshold(double[] jndiArray1,
                                          double[] jndiArray2) {
    double[] delta = getDeltaJNDI(jndiArray1, jndiArray2);
    double maxDelta = Maths.max(delta);
    return maxDelta < deltaThreshold;
  }

  /**
   * LP�ɳ̤j�౵����delta
   */
  private double deltaThreshold = 1;

  /**
   * �p��jndiArray1�PjndiArray2������delta JNDI
   * @param jndiArray1 double[]
   * @param jndiArray2 double[]
   * @return double[]
   */
  protected final static double[] getDeltaJNDI(double[] jndiArray1,
                                               double[] jndiArray2) {
    double[] delta = DoubleArray.minus(jndiArray1, jndiArray2);
    DoubleArray.abs(delta);
    return delta;
  }

  /**
   * �q��l��GSDF�H��GSDF���w���[�t��, �٭�XGSDF.
   * �ɥ�GSDFPredicter.recoverGSDF.
   *
   * @param originalGSDF double[]
   * @param predcitGSDFPrime double[]
   * @return double[]
   */
  protected final static double[] recoverGSDF(double[] originalGSDF,
                                              double[] predcitGSDFPrime) {
    double[] rgsdf = GSDFPredicter.recoverGSDF(originalGSDF, predcitGSDFPrime);
    rgsdf = GSDFPredicter.adjustPredictDataAlign(originalGSDF, rgsdf);
    return rgsdf;
  }

  public void setKernel1(double[] kernel1) {
    this.kernel1 = kernel1;
  }

  public void setKernel2(double[] kernel2) {
    this.kernel2 = kernel2;
  }

  public void setDeltaThreshold(double deltaThreshold) {
    this.deltaThreshold = deltaThreshold;
  }

  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

  public int getFilterTimes() {
    return filterTimes;
  }

  public int getPart1FilterTimes() {
    return part1FilterTimes;
  }

  public int getPart2FilterTimes() {
    return part2FilterTimes;
  }

  public int getCoverRange() {
    return coverRange;
  }

  private double[] kernel1 = new double[] {
      1, 10, 1};
  private double[] kernel2 = new double[] {
      1, 2, 1};
}
