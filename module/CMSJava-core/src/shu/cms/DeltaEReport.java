package shu.cms;

import java.text.*;
import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;

//import shu.plot.*;

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
public final class DeltaEReport
    implements Comparable {
  public ColorDivision colorDivision = ColorDivision.GLOBAL;
  public int patchCount;
  public DeltaE minDeltaE;
  public DeltaE meanDeltaE;
  public DeltaE maxDeltaE;
  public DeltaE stdDeltaE;
  public DeltaE mixDeltaE;
  public DeltaE whitePointDeltaE;
  public String description;

  public DeltaE minCIE2000DeltaLCH;
  public DeltaE maxCIE2000DeltaLCH;
  public DeltaE meanCIE2000DeltaLCH;

  /**
   * 所有的deltaE的list
   */
  private List<DeltaE> deltaEList;
  private List<Patch> originalPatchList;
  private List<Patch> modelPatchList;
  protected boolean reverseReport = false;

  public double[] minDeltauvPrime;
  public double[] maxDeltauvPrime;
  public double[] stdDeltauvPrime;
  public double[] mixDeltauvPrime;
  public double[] meanDeltauvPrime;

//  protected double[][] deltauvprimeList;



  /**
   * 0:0~.99
   * 1:1~1.99
   * 餘類推...
   * 9:>=9
   */
  public int[] deltaEDistribute = null; // new int[10];

  private static DecimalFormat df;
  public final static void setDecimalFormat(DecimalFormat decimalFormat) {
    df = decimalFormat;
  }

  protected static boolean OnlyCountMeasuredDeltaE = true;

  public final static void setOnlyCountMeasuredDeltaE(boolean
      onlyCountMeasuredDeltaE) {
    OnlyCountMeasuredDeltaE = onlyCountMeasuredDeltaE;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<" + colorDivision.getName() + ">\n");
    if (description != null) {
      buf.append(description + '\n');
    }

    if (OnlyCountMeasuredDeltaE) {
      //==========================================================================
      // 只有measured deltaE(預設是CIEDE2000)
      //==========================================================================
      buf.append("#DeltaE Formula: " + DeltaE.getMeasuredDeltaEDescription() +
                 '\n');
      if (df != null) {
        buf.append("mean: " + df.format(meanDeltaE.getMeasuredDeltaE()) + '\n');
        buf.append("min:  " + df.format(minDeltaE.getMeasuredDeltaE()) + '\n');
        buf.append("max:  " + df.format(maxDeltaE.getMeasuredDeltaE()) + '\n');
        buf.append("mix:  " + df.format(mixDeltaE.getMeasuredDeltaE()) + '\n');
        buf.append("std:  " + df.format(stdDeltaE.getMeasuredDeltaE()) + '\n');
      }
      else {
        buf.append("mean: " + meanDeltaE.getMeasuredDeltaE() + '\n');
        buf.append("min:  " + minDeltaE.getMeasuredDeltaE() + '\n');
        buf.append("max:  " + maxDeltaE.getMeasuredDeltaE() + '\n');
        buf.append("mix:  " + mixDeltaE.getMeasuredDeltaE() + '\n');
        buf.append("std:  " + stdDeltaE.getMeasuredDeltaE() + '\n');
      }

      buf.append("dist: ");
      for (int x = 0; x < 10; x++) {
        String range = "[" + (x - 1) + '~' + x + ']';
        range = x == 0 ? "[0~.5]" : range;
        range = x == 1 ? "[.5~1]" : range;
        buf.append(range + deltaEDistribute[x] + ' ');
      }
      buf.append("[9~]" + deltaEDistribute[10] + " (" + patchCount + ")\n");

      buf.append("accdist: ");
      int acc = 0;
      for (int x = 0; x < 10; x++) {
        String range = "[" + (x - 1) + '~' + x + ']';
        range = x == 0 ? "[0~.5]" : range;
        range = x == 1 ? "[.5~1]" : range;
        acc += deltaEDistribute[x];
        buf.append(range + acc + ' ');
      }
      buf.append("[9~]" + (acc + deltaEDistribute[10]) + " (" + patchCount +
                 ")\n");
      //==========================================================================
    }
    else {
      //==========================================================================
      // 全部的deltaE
      //==========================================================================
      buf.append("mean: " + toString(meanDeltaE) + '\n');
      buf.append("min: " + toString(minDeltaE) + '\n');
      buf.append("max:  " + toString(maxDeltaE) + '\n');
      buf.append("mix:  " + toString(mixDeltaE) + '\n');
      buf.append("std:  " + toString(stdDeltaE) + '\n');
      //==========================================================================
    }

    //==========================================================================
    // CIE2000 deltaLCh
    //==========================================================================
    if (df != null) {
      buf.append("mean deltaLCH00) " +
                 DoubleArray.toString(
                     df, meanCIE2000DeltaLCH.getCIE2000DeltaLCh()) + '\n');
      buf.append("min deltaLCH00) " +
                 DoubleArray.toString(
                     df, minCIE2000DeltaLCH.getCIE2000DeltaLCh()) + '\n');
      buf.append("max deltaLCH00) " +
                 DoubleArray.toString(
                     df, maxCIE2000DeltaLCH.getCIE2000DeltaLCh()) + '\n');
    }
    else {
      buf.append("mean deltaLCh00) " + meanCIE2000DeltaLCH + '\n');
      buf.append("min deltaLCh00) " + minCIE2000DeltaLCH + '\n');
      buf.append("max deltaLCh00) " + maxCIE2000DeltaLCH + '\n');
    }
    //==========================================================================

    //==========================================================================
    // delta u'v'
    //==========================================================================
    if (meanDeltauvPrime != null) {
      buf.append("#Delta u'v':\n");
      if (df != null) {
        buf.append("mean: " + DoubleArray.toString(df, this.meanDeltauvPrime) +
                   '\n');
        buf.append("min: " + DoubleArray.toString(df, this.minDeltauvPrime) +
                   '\n');
        buf.append("max: " + DoubleArray.toString(df, this.maxDeltauvPrime) +
                   '\n');
        buf.append("mix: " + DoubleArray.toString(df, this.mixDeltauvPrime) +
                   '\n');
        buf.append("std: " + DoubleArray.toString(df, this.stdDeltauvPrime) +
                   '\n');
      }
      else {
        buf.append("mean: " + DoubleArray.toString(this.meanDeltauvPrime) +
                   '\n');
        buf.append("min: " + DoubleArray.toString(this.minDeltauvPrime) +
                   '\n');
        buf.append("max: " + DoubleArray.toString(this.maxDeltauvPrime) +
                   '\n');
        buf.append("mix: " + DoubleArray.toString(this.mixDeltauvPrime) +
                   '\n');
        buf.append("std: " + DoubleArray.toString(this.stdDeltauvPrime) +
                   '\n');
      }
    }
    //==========================================================================
    buf.append("<" + colorDivision.getName() + "/>\n");
    return buf.toString();
  }

  protected static String toString(DeltaE deltaE) {
    return deltaE.getCIEDeltaE() + " " + deltaE.getCIE94DeltaE() + " " +
        deltaE.getCIE2000DeltaE() + " " +
        deltaE.getCMC11DeltaE() + " " + deltaE.getCMC21DeltaE() + " " +
        deltaE.getBFDDeltaE() + " (cie/94/2000/cmc11/cmc21/bfd)";
  }

  /**
   * Compares this object with the specified object for order.
   *
   * @param o the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *   less than, equal to, or greater than the specified object.
   */
  public int compareTo(Object o) {
    DeltaEReport target = (DeltaEReport) o;
    double thisDeltaE = meanDeltaE.getMeasuredDeltaE();
    double targetDeltaE = target.meanDeltaE.getMeasuredDeltaE();

    return (int) ( (thisDeltaE - targetDeltaE) * 1E17);
  }

  public final static class Instance {
    protected static DeltaEReport[] patchReport(List<Patch> originalPatches,
        List<Patch> modelPatches, ColorDivision.Zone[] divisions,
        double stopWhenTouched) {
      int size = divisions.length;
      DeltaEReport[] reports = new DeltaEReport[size];

      int index = 0;
      for (ColorDivision.Zone type : divisions) {
        ColorDivision cd = ColorDivision.getInstance(type);

        List<Patch> [] ps = cd.filterPatch(originalPatches, modelPatches);
        List<Patch> p1 = ps[0];
        List<Patch> p2 = ps[1];

        if (p1.size() != 0 && p1.size() == p2.size()) {
          reports[index] = getDeltaEReport(Sample.getPatchInstance(p1),
                                           Sample.getPatchInstance(p2),
                                           stopWhenTouched,
                                           STOP_WHEN_TOUCHED_TYPE);
          if (reports[index] == null) {
            return null;
          }
          reports[index].colorDivision = cd;
          reports[index].originalPatchList = originalPatches;
          reports[index].modelPatchList = modelPatches;
          index++;
        }
      }
      return reports;
    }

    /**
     * 對originalPatches和modelPatches的Lab計算色差
     * @param originalPatches List
     * @param modelPatches List
     * @param doColorDividing boolean 是否要作色彩分區
     * @return DeltaEReport[]
     */
    public static DeltaEReport[] patchReport(List<Patch> originalPatches,
        List<Patch> modelPatches, boolean doColorDividing) {
      return patchReport(originalPatches, modelPatches, doColorDividing, 0.0);
    }

    /**
     * 對originalPatches和modelPatches的Lab計算色差
     * 色差到達stopWhenTouched時,停止計算
     * @param originalPatches List
     * @param modelPatches List
     * @param doColorDividing boolean
     * @param stopWhenTouched double
     * @return DeltaEReport[]
     */
    public static DeltaEReport[] patchReport(List<Patch> originalPatches,
        List<Patch> modelPatches, boolean doColorDividing,
        double stopWhenTouched) {
      Patch p1 = originalPatches.get(0);
      Patch p2 = modelPatches.get(0);
      if (p1.getLab() == null) {
        throw new IllegalArgumentException(
            "patches1.getLab() == null");
      }
      if (p2.getLab() == null) {
        throw new IllegalArgumentException(
            "patches2.getLab() == null");
      }
      ColorDivision.Zone[] divisions = null;
      if (doColorDividing) {
        divisions = ColorDivision.Zone.values();
      }
      else {
        divisions = new ColorDivision.Zone[] {
            ColorDivision.Zone.GLOBAL};
      }
      return patchReport(originalPatches, modelPatches, divisions,
                         stopWhenTouched);
    }

    /**
     * 計算DeltaEReport.
     * 當色差大於stopWhenTouched時,停止運算,且回傳null
     * stopWhenTouched的判別以deltaEType設定
     * @param LabList1 List
     * @param LabList2 List
     * @param stopWhenTouched double
     * @param touchedDeltaEType Type
     * @return DeltaEReport
     * @deprecated
     */
    protected static DeltaEReport getDeltaEReport(final List<CIELab> LabList1,
                                                  final List<CIELab> LabList2,
                                                  double stopWhenTouched,
                                                  DeltaE.Formula
                                                  touchedDeltaEType) {
      if (LabList1.size() != LabList2.size()) {
        throw new IllegalArgumentException("labList1.size() != labList2.size()");
      }

      //==========================================================================
      // 資料的準備
      //==========================================================================
      int size = LabList1.size();
      List<DeltaE> deltaEList = new ArrayList<DeltaE> (size);
      double[][] deltaEs = new double[size][];
      double[][] CIE2000DeltaLCHs = new double[size][];
      int[] deltaEDistribute = new int[11];
      //==========================================================================

      //==========================================================================
      // 比較所有色塊
      //==========================================================================
      for (int x = 0; x < size; x++) {
        CIELab Lab1 = LabList1.get(x);
        CIELab Lab2 = LabList2.get(x);
        DeltaE deltaE = new DeltaE(Lab1, Lab2);
        //stopWhenTouched
        if (stopWhenTouched != 0. &&
            deltaE.getDeltaE(touchedDeltaEType) >= stopWhenTouched) {
          return null;
        }

        if (DeltaEReport.OnlyCountMeasuredDeltaE) {
          deltaE.getMeasuredDeltaE();
        }
        else {
          deltaE.calculateAllDeltaE();
        }

        deltaEList.add(deltaE);

        deltaEs[x] = deltaE.deltaE;
        CIE2000DeltaLCHs[x] = deltaE.getCIE2000DeltaLCh();
        double de = deltaE.getMeasuredDeltaE();

        if (de < 0.5) {
          //第一元素是0~0.5
          deltaEDistribute[0]++;
        }
        else {
          int index = (int) Math.floor(de);
          index = index > 9 ? 9 : index;
          deltaEDistribute[index + 1]++;
        }

      }
      //==========================================================================

      DeltaEReport report = new DeltaEReport();
      report.statistics(deltaEs, deltaEDistribute, CIE2000DeltaLCHs);
      report.deltaEList = deltaEList;
      report.patchCount = size;

      return report;
    }

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 用來統整Patch和CIELab的List, 方便產生report
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    private static class Sample {
      boolean isPatch() {
        return isPatch;
      }

      Patch getPatch(int index) {
        if (isPatch) {
          return patchList.get(index);
        }
        else {
          return null;
        }

      }

      CIEXYZ getXYZ(int index) {
        if (isPatch) {
          return patchList.get(index).getXYZ();
        }
        else {
          return labList.get(index).toXYZ();
        }

      }

      CIELab getLab(int index) {
        if (isPatch) {
          return patchList.get(index).getLab();
        }
        else {
          return labList.get(index);
        }
      }

      int size() {
        if (isPatch) {
          return patchList.size();
        }
        else {
          return labList.size();
        }
      }

      Sample() {

      }

      static Sample getLabInstance(List<CIELab> labList) {
        Sample sample = new Sample();
        sample.labList = labList;
        sample.isPatch = false;
        return sample;
      }

      static Sample getPatchInstance(List<Patch> patchList) {
        Sample sample = new Sample();
        sample.patchList = patchList;
        sample.isPatch = true;
        return sample;
      }

      private boolean isPatch = false;
      private List<CIELab> labList;
      private List<Patch> patchList;
    }

    /**
     * 計算DeltaEReport.
     * 當色差大於stopWhenTouched時,停止運算,且回傳null
     * stopWhenTouched的判別以deltaEType設定
     * @param sample1 Sample
     * @param sample2 Sample
     * @param stopWhenTouched double
     * @param touchedDeltaEType Formula
     * @return DeltaEReport
     */
    private static DeltaEReport getDeltaEReport(final Sample sample1,
                                                final Sample sample2,
                                                double stopWhenTouched,
                                                DeltaE.Formula
                                                touchedDeltaEType) {
      if (sample1.size() != sample2.size()) {
        throw new IllegalArgumentException("sample1.size() != sample2.size()");
      }

      //==========================================================================
      // 資料的準備
      //==========================================================================
      int size = sample1.size();
      List<DeltaE> deltaEList = new ArrayList<DeltaE> (size);
      double[][] deltaEs = new double[size][];
      double[][] CIE2000DeltaLCHs = new double[size][];
      int[] deltaEDistribute = new int[11];
      double[][] deltauvpList = new double[size][];
      //==========================================================================

      //==========================================================================
      // 比較所有色塊
      //==========================================================================
      for (int x = 0; x < size; x++) {
        CIELab Lab1 = sample1.getLab(x);
        CIELab Lab2 = sample2.getLab(x);
        CIEXYZ XYZ1 = sample1.getXYZ(x);
        CIEXYZ XYZ2 = sample2.getXYZ(x);
        if (XYZ1 != null && XYZ2 != null) {
          CIExyY xyY1 = new CIExyY(sample1.getXYZ(x));
          CIExyY xyY2 = new CIExyY(sample2.getXYZ(x));
          deltauvpList[x] = xyY1.getDeltauvPrime(xyY2);
        }

        DeltaE deltaE = new DeltaE(Lab1, Lab2);
        deltaE.XYZ1 = XYZ1;
        deltaE.XYZ2 = XYZ2;
        if (stopWhenTouched != 0. &&
            deltaE.getDeltaE(touchedDeltaEType) >= stopWhenTouched) {
          return null;
        }

        if (DeltaEReport.OnlyCountMeasuredDeltaE) {
          deltaE.getMeasuredDeltaE();
        }
        else {
          deltaE.calculateAllDeltaE();
        }

        deltaEList.add(deltaE);

        deltaEs[x] = deltaE.deltaE;
        CIE2000DeltaLCHs[x] = deltaE.getCIE2000DeltaLCh();
        double de = deltaE.getMeasuredDeltaE();

        if (de < 0.5) {
          //第一元素是0~0.5
          deltaEDistribute[0]++;
        }
        else {
          int index = (int) Math.floor(de);
          index = index > 9 ? 9 : index;
          deltaEDistribute[index + 1]++;
        }

      }
      //==========================================================================

      DeltaEReport report = new DeltaEReport();
      report.statistics(deltaEs, deltaEDistribute, CIE2000DeltaLCHs);
      report.statistics(deltauvpList);
      report.deltaEList = deltaEList;
      report.patchCount = size;

      return report;
    }

    protected static DeltaEReport[] CIELabReport(List<CIELab> patches1,
        List<CIELab> patches2, ColorDivision.Zone[] divisions,
        double stopWhenTouched) {
      int size = divisions.length;
      DeltaEReport[] reports = new DeltaEReport[size];

      int index = 0;
      for (ColorDivision.Zone type : divisions) {
        ColorDivision cd = ColorDivision.getInstance(type);

        List<CIELab> [] ps = cd.filterCIELab(patches1, patches2);
        List<CIELab> p1 = ps[0];
        List<CIELab> p2 = ps[1];

        if (p1.size() != 0 && p1.size() == p2.size()) {
//          reports[index] = getDeltaEReport(p1, p2, stopWhenTouched,
//                                           STOP_WHEN_TOUCHED_TYPE);
          reports[index] = getDeltaEReport(Sample.getLabInstance(p1),
                                           Sample.getLabInstance(p2),
                                           stopWhenTouched,
                                           STOP_WHEN_TOUCHED_TYPE);

          if (reports[index] == null) {
            return null;
          }
          reports[index].colorDivision = cd;
          index++;
        }
      }
      return reports;
    }

    public static DeltaEReport[] CIELabReport(List<CIELab> LabList1,
        List<CIELab> LabList2, boolean doColorDividing, double stopWhenTouched) {
      ColorDivision.Zone[] divisions = null;
      if (doColorDividing) {
        divisions = ColorDivision.Zone.values();
      }
      else {
        divisions = new ColorDivision.Zone[] {
            ColorDivision.Zone.GLOBAL};
      }
      return CIELabReport(LabList1, LabList2, divisions, stopWhenTouched);

    }

    public static DeltaEReport CIELabReport(final List<CIELab> LabList1,
                                            final List<CIELab> LabList2) {

//      return getDeltaEReport(LabList1, LabList2, 0, null);
      return getDeltaEReport(Sample.getLabInstance(LabList1),
                             Sample.getLabInstance(LabList2), 0, null);
    }
  }

  protected final static DeltaE.Formula STOP_WHEN_TOUCHED_TYPE = DeltaE.Formula.
      CIE2000;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 將色塊以及該色塊的色差結合為單一物件
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static class PatchDeltaE
      implements Comparable {
    protected DeltaE deltaE;
    /**
     * 原始導具的patch
     */
    protected Patch originalPatch;
    /**
     * model所預測出來的patch
     */
    protected Patch modelPatch;

    public DeltaE getDeltaE() {
      return deltaE;
    }

    public Patch getOriginalPatch() {
      return originalPatch;
    }

    public Patch getModelPatch() {
      return modelPatch;
    }

    public PatchDeltaE(Patch patch, DeltaE deltaE) {
      this(patch, null, deltaE);
    }

    public PatchDeltaE(Patch originalPatch, Patch modelPatch, DeltaE deltaE) {
      this.originalPatch = originalPatch;
      this.modelPatch = modelPatch;
      this.deltaE = deltaE;
    }

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(originalPatch);
      buf.append(' ');
      buf.append(deltaE);
      return buf.toString();
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *   is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
      double thisDeltaE = deltaE.getMeasuredDeltaE();
      double thatDeltaE = ( (PatchDeltaE) o).deltaE.getMeasuredDeltaE();
      return Double.compare(thatDeltaE, thisDeltaE);
//      return (int) (thatDeltaE * 10000000. - thisDeltaE * 10000000.);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
      return super.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *   argument; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
      return compareTo(obj) == 0;
    }

  }

  /**
   * 由於DeltaEReport只有deltaE資訊,無法判讀是哪個色塊的deltaE.
   * 所以為了方便判斷哪些RGB色塊的deltaE是如何,
   * 可以使用此函式將patch與deltaE作組合,以便判讀.
   * @param sort boolean
   * @return List
   */
  protected List<PatchDeltaE> getPatchDeltaEList(
      boolean sort) {
    if (this.originalPatchList == null || this.modelPatchList == null) {
      return null;
    }
    else {
      return getPatchDeltaEList(this.originalPatchList, this.modelPatchList,
                                sort);
    }
  }

  public static enum ReportType {
    LC, Ch, Lh;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 色塊以及該色塊的色差的report
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static class PatchDeltaEReport {

    public Plot3D plot(ReportType reportType) {
      Plot3D plot3D = Plot3D.getInstance();

      //==========================================================================
      // 設定軸
      //==========================================================================
      plot3D.setAxeLabel(2, "DeltaE");
      switch (reportType) {
        case LC:
          plot3D.setAxeLabel(0, "Luminance");
          plot3D.setAxeLabel(1, "Chroma");
          break;
        case Ch:
          plot3D.setAxeLabel(0, "Chroma");
          plot3D.setAxeLabel(1, "Hue");
          break;
        case Lh:
          plot3D.setAxeLabel(0, "Luminance");
          plot3D.setAxeLabel(1, "Hue");
          break;
      }
      //==========================================================================

      int size = size();
      for (int x = 0; x < size; x++) {
        DeltaE deltaE = getDeltaE(x);
        Patch patch = getPatch(x);
        CIELab Lab = patch.getLab();
        CIELCh LCh = new CIELCh(Lab);
        Color c = patch.getRGB().getColor();
        switch (reportType) {
          case LC:
            plot3D.addScatterPlot(null, c, LCh.L, LCh.C,
                                  deltaE.getMeasuredDeltaE());
            break;
          case Ch:
            plot3D.addScatterPlot(null, c, LCh.C, LCh.h,
                                  deltaE.getMeasuredDeltaE());
            break;
          case Lh:
            plot3D.addScatterPlot(null, c, LCh.L, LCh.h,
                                  deltaE.getMeasuredDeltaE());
            break;
        }
      }
      return plot3D;
    }

    public Plot2D plotDeltaEvsDeltauvPrime(boolean vsDeltavPrime) {
      String title = "DeltaE vs Delta " + (vsDeltavPrime ? "v'" : "u'");
      Plot2D plot = Plot2D.getInstance(title);

      int size = size();
      for (int x = 0; x < size; x++) {
        PatchDeltaE patchDeltaE = patchDeltaEList.get(x);
        Patch p = patchDeltaE.getOriginalPatch();
        RGB rgb = p.getRGB();
        DeltaE deltaE = patchDeltaE.getDeltaE();
        double de = deltaE.getMeasuredDeltaE();
        double[] duvp = deltaE.getDeltauvPrime();
        if (vsDeltavPrime) {
          plot.addScatterPlot("", rgb.getColor(), Math.abs(duvp[1]), de);
        }
        else {
          plot.addScatterPlot("", rgb.getColor(), Math.abs(duvp[0]), de);
        }
      }

      plot.setAxeLabel(1, "DeltaE");
      if (vsDeltavPrime) {
        plot.setAxeLabel(0, "dv'");
      }
      else {
        plot.setAxeLabel(0, "du'");
      }
//      plot.drawCachePlot();
      plot.setBackground(Color.black);
      plot.setVisible();
      return plot;
    }

    public Plot3D plot() {
      Plot3D plot3D = Plot3D.getInstance();

      int size = size();

      for (int x = 0; x < size; x++) {
        DeltaEReport.PatchDeltaE patchDeltaE = getPatchDeltaE(x);
        if (patchDeltaE != null) {
          Patch p = patchDeltaE.getOriginalPatch();
          RGB rgb = p.getRGB();
          DeltaE de = patchDeltaE.getDeltaE();
//          plot3D.addColorSpace(rgb + " " + de.getMeasuredDeltaE(), rgb);
          plot3D.addColorSpace(rgb + " " + de.getMeasuredDeltaE(), rgb.getColor(),
                               rgb);
        }
      }

      plot3D.setAxeLabel(0, "R");
      plot3D.setAxeLabel(1, "G");
      plot3D.setAxeLabel(2, "B");

      plot3D.setVisible();
      return plot3D;
    }

    protected static enum Type {
      DeltaE, uvPrime
    }

    private Type type;
    private List<PatchDeltaE> patchDeltaEList;
    private double reportMinimumDeltaE;
    private double[] reportMinimumDeltauvp;
    private boolean reverseReport = false;
    private int sizeOfGreaterThanReportDeltaE = -1;
    public final static boolean SHOW_RGB_IN_255 = true;

    public DeltaE getDeltaE(int index) {
      return patchDeltaEList.get(index).deltaE;
    }

    public Patch getPatch(int index) {
      return patchDeltaEList.get(index).originalPatch;
    }

    public int size() {
      return patchDeltaEList.size();
    }

    public List<PatchDeltaE> getPatchDeltaEList() {
      return patchDeltaEList;
    }

    /**
     * 大於reportMinimumDeltaE的色塊數量
     * @return int
     */
    public int sizeOfGreaterThanReportDeltaE() {
      if (sizeOfGreaterThanReportDeltaE == -1) {
        sizeOfGreaterThanReportDeltaE = 0;
        int size = size();
        for (int x = 0; x < size; x++) {
          PatchDeltaE patchDeltaE = patchDeltaEList.get(x);
          if (isDoReport(patchDeltaE)) {
//          double deltaE = patchDeltaE.deltaE.getMeasuredDeltaE();
//          if (deltaE >= reportMinimumDeltaE) {
            sizeOfGreaterThanReportDeltaE++;
          }
        }
      }
      return sizeOfGreaterThanReportDeltaE;
    }

    protected PatchDeltaEReport(List<PatchDeltaE> patchDeltaEList,
        double reportMinimumDeltaE) {
      this(patchDeltaEList, reportMinimumDeltaE, false);
    }

    protected PatchDeltaEReport(List<PatchDeltaE> patchDeltaEList,
        double reportMinimumDeltaE, boolean reverseModelReport) {
      this.patchDeltaEList = patchDeltaEList;
      this.reportMinimumDeltaE = reportMinimumDeltaE;
      this.reverseReport = reverseModelReport;
      this.type = Type.DeltaE;
    }

    protected PatchDeltaEReport(List<PatchDeltaE> patchDeltaEList,
        double[] reportMinimumDeltauvp) {
      this(patchDeltaEList, reportMinimumDeltauvp, false);
    }

    protected PatchDeltaEReport(List<PatchDeltaE> patchDeltaEList,
        double[] reportMinimumDeltauvp, boolean reverseModelReport) {
      this.patchDeltaEList = patchDeltaEList;
      DoubleArray.abs(reportMinimumDeltauvp);
      this.reverseReport = reverseModelReport;
      this.type = Type.uvPrime;
    }

    protected boolean isDoReport(PatchDeltaE patchDeltaE) {
      switch (type) {
        case uvPrime:
          double[] duvp = patchDeltaE.deltaE.getDeltauvPrime();
          return Math.abs(duvp[0]) >= reportMinimumDeltauvp[0] ||
              Math.abs(duvp[1]) >= reportMinimumDeltauvp[1];
        case DeltaE:
          double deltaE = patchDeltaE.deltaE.getMeasuredDeltaE();
          return deltaE >= reportMinimumDeltaE;
        default:
          return false;
      }
    }

    protected String getFormatRGBString(Patch p) {
      RGB rgb = p.getRGB();
      double[] rgbValues = new double[3];
      if (SHOW_RGB_IN_255) {
        rgbValues = rgb.getValues(rgbValues, RGB.MaxValue.Double255);
      }
      else {
        rgbValues = rgb.getValues(rgbValues);
      }

      if (SHOW_RGB_IN_255) {
//        return DoubleArray.toString("###.##", rgbValues).trim();
        return DoubleArray.toString("%3.2f", rgbValues).trim();
      }
      else {
        return DoubleArray.toString(rgbValues);
      }
    }

    public PatchDeltaE getPatchDeltaE(int index) {
      PatchDeltaE patchDeltaE = patchDeltaEList.get(index);
      if (isDoReport(patchDeltaE)) {
        return patchDeltaE;
      }
      else {
        return null;
      }

    }

    public String toString() {
      int size = patchDeltaEList.size();
      StringBuilder builder = new StringBuilder();
      int count = 0;
      for (int x = 0; x < size; x++) {
        PatchDeltaE patchDeltaE = patchDeltaEList.get(x);
        if (isDoReport(patchDeltaE)) {
          Patch orgPatch = patchDeltaE.originalPatch;
          Patch modelPatch = patchDeltaE.modelPatch;
          double[] deltaLCh = patchDeltaE.deltaE.getCIE2000DeltaLCh();
          count++;

          builder.append("name[");
          builder.append(orgPatch.getName());
          if (reverseReport) {
            builder.append("] originalRGB[");
          }
          else {
            builder.append("] RGB[");
          }

          builder.append(getFormatRGBString(orgPatch));

          if (reverseReport) {
            builder.append("] modelRGB[");
            builder.append(getFormatRGBString(modelPatch));
          }

          double deltaE = patchDeltaE.deltaE.getMeasuredDeltaE();
          builder.append("] dE[");
          if (df != null) {
            builder.append(df.format(deltaE));
            builder.append("] dLCh00[");
            builder.append(DoubleArray.toString(df, deltaLCh));
          }
          else {
            builder.append(deltaE);
            builder.append("] dLCh00[");
            builder.append(DoubleArray.toString(deltaLCh));
          }

          CIEXYZ orgXYZ = orgPatch.getXYZ();
          CIEXYZ modelXYZ = modelPatch.getXYZ();

          CIExyY orgxyY = new CIExyY(orgXYZ);
          CIExyY modelxyY = new CIExyY(modelXYZ);
          double[] duvp = orgxyY.getDeltauvPrime(modelxyY);

          builder.append("] duv[");
          if (df != null) {
            builder.append(DoubleArray.toString(df, duvp));
          }
          else {
            builder.append(DoubleArray.toString(duvp));
          }

          if (df != null) {
            builder.append("] orgXYZ[" +
                           DoubleArray.toString(df, orgXYZ.getValues()));
            builder.append("] modelXYZ[" +
                           DoubleArray.toString(df, modelXYZ.getValues()));
          }
          else {
            builder.append("] orgXYZ[" + DoubleArray.toString(orgXYZ.getValues()));
            builder.append("] modelXYZ[" +
                           DoubleArray.toString(modelXYZ.getValues()));
          }
          builder.append("]\n");

        }
        else if (type == Type.DeltaE) {
          break;
        }
      }
      String conditions = (type == Type.DeltaE) ?
          "DeltaE >=" + this.reportMinimumDeltaE : "CIE u'v' >=" +
          DoubleArray.toString(this.reportMinimumDeltauvp);
      builder.insert(0, "Total " + count + " patch(es): [" + conditions + "]\n");
      return builder.toString();
    }
  }

  /**
   * 取得色塊的deltaE報告,將會報告deltaE在3以上者
   * @return PatchDeltaEReport
   */
  public PatchDeltaEReport getPatchDeltaEReport() {
    return getPatchDeltaEReport(3.);
  }

  public PatchDeltaEReport getPatchDeltaEReport(double[] reportMinimumDeltauvp) {
    List<PatchDeltaE> patchDeltaEList = getPatchDeltaEList(true);
    return new PatchDeltaEReport(patchDeltaEList, reportMinimumDeltauvp,
                                 this.reverseReport);
  }

  /**
   * 取得色塊的deltaE報告,將會報告deltaE在reportMinimumDeltaE以上者
   * @param reportMinimumDeltaE double
   * @return PatchDeltaEReport
   */
  public PatchDeltaEReport getPatchDeltaEReport(double reportMinimumDeltaE) {
    return getPatchDeltaEReport(reportMinimumDeltaE, true);
  }

  public PatchDeltaEReport getPatchDeltaEReport(double reportMinimumDeltaE,
                                                boolean sort) {
    List<PatchDeltaE> patchDeltaEList = getPatchDeltaEList(sort);
    return new PatchDeltaEReport(patchDeltaEList, reportMinimumDeltaE,
                                 this.reverseReport);
  }

  protected List<PatchDeltaE> patchDeltaEList;

  /**
   * 由於DeltaEReport只有deltaE資訊,無法判讀是哪個色塊的deltaE.
   * 所以為了方便判斷哪些RGB色塊的deltaE是如何,
   * 可以使用此函式將rgb與deltaE作組合,以便判讀.
   * @param originalPatchList List
   * @param modelPatchList List
   * @param sort boolean
   * @return List
   */
  protected List<PatchDeltaE> getPatchDeltaEList(List<Patch> originalPatchList,
      List<Patch> modelPatchList, boolean sort) {
    if (patchDeltaEList == null) {
      int size = originalPatchList.size();
      if (size != deltaEList.size()) {
        throw new IllegalArgumentException(
            "targetPatch.size() !=deltaEList.size()");
      }
      patchDeltaEList = new ArrayList<PatchDeltaE> (size);
      for (int x = 0; x < size; x++) {
        Patch orgPatch = originalPatchList.get(x);
        Patch modelPatch = modelPatchList.get(x);
        DeltaE deltaE = deltaEList.get(x);
        PatchDeltaE patchDeltaE = new PatchDeltaE(orgPatch, modelPatch, deltaE);
        patchDeltaEList.add(patchDeltaE);
      }
    }

    if (sort) {
      Collections.sort(patchDeltaEList);
    }
    return patchDeltaEList;
  }

  public enum AnalyzeType {
    Average, Max, Std, Mix;
  }

  /**
   * 分析所採用的準則
   */
  public final static AnalyzeType analyzeType = AnalyzeType.Average;

  public final double getMeasuredDeltaE(AnalyzeType analyzeType) {
    return getMeasuredDeltaE(this, analyzeType);
  }

  public final static double getMeasuredDeltaE(DeltaEReport report,
                                               AnalyzeType analyzeType) {
    double deltaE1 = 0;
    switch (analyzeType) {
      case Average:
        deltaE1 = report.meanDeltaE.getMeasuredDeltaE();
        break;
      case Max:
        deltaE1 = report.maxDeltaE.getMeasuredDeltaE();
        break;
      case Std:
        deltaE1 = report.stdDeltaE.getMeasuredDeltaE();
        break;
      case Mix:
        deltaE1 = report.mixDeltaE.getMeasuredDeltaE();
        break;
    }
    return deltaE1;
  }

  public final static class Analyze {
    /**
     * 分析所採用的準則
     */
    public final static AnalyzeType analyzeType = AnalyzeType.Average;

    /**
     * 傳入兩個DeltaEReport,分析後,回傳色差較小者
     * @param report1 DeltaEReport
     * @param report2 DeltaEReport
     * @return DeltaEReport
     */
    public final static DeltaEReport analyzeDeltaEReport(DeltaEReport
        report1, DeltaEReport report2) {

      return analyzeDeltaEReport(report1, report2, analyzeType);
    }

    /**
     * 傳入兩個DeltaEReport,分析後,回傳色差較小者
     * @param report1 DeltaEReport
     * @param report2 DeltaEReport
     * @param analyzeType AnalyzeType
     * @return DeltaEReport
     */
    public final static DeltaEReport analyzeDeltaEReport(DeltaEReport
        report1, DeltaEReport report2, AnalyzeType analyzeType) {
      return analyzeDeltaEReport(report1, report2, analyzeType, false);
    }

    public final static DeltaEReport analyzeDeltaEReport(DeltaEReport
        report1, DeltaEReport report2, AnalyzeType analyzeType,
        boolean analyzeWhitePoint) {
      double deltaE1 = 0, deltaE2 = 0;
      switch (analyzeType) {
        case Average:
          deltaE1 = report1.meanDeltaE.getMeasuredDeltaE();
          deltaE2 = report2.meanDeltaE.getMeasuredDeltaE();
          break;
        case Max:
          deltaE1 = report1.maxDeltaE.getMeasuredDeltaE();
          deltaE2 = report2.maxDeltaE.getMeasuredDeltaE();
          break;
        case Std:
          deltaE1 = report1.stdDeltaE.getMeasuredDeltaE();
          deltaE2 = report2.stdDeltaE.getMeasuredDeltaE();
          break;
        case Mix:
          deltaE1 = report1.mixDeltaE.getMeasuredDeltaE();
          deltaE2 = report2.mixDeltaE.getMeasuredDeltaE();
          break;
      }

      //如果有白點的色差
      if (analyzeWhitePoint && report1.whitePointDeltaE != null &&
          report2.whitePointDeltaE != null) {
        double white1 = report1.whitePointDeltaE.getMeasuredDeltaE();
        double white2 = report2.whitePointDeltaE.getMeasuredDeltaE();

        if (white1 > 1. ^ white2 > 1.) {
          //如果其中一個白點<1,另外一個>1 (XOR)
          return white2 > 1. ? report1 : report2;
        }
        /*else if (white1 > 1. && white2 > 1.) {
          //如果都>1
          return white1 < white2 ? report1 : report2;
               }*/
      }

      return Maths.sqr(deltaE1) < Maths.sqr(deltaE2) ? report1 : report2;
    }

  }

  protected void statistics(double[][] deltauvprimeList) {
    deltauvprimeList = DoubleArray.transpose(deltauvprimeList);
    for (double[] duvp : deltauvprimeList) {
      if (duvp == null) {
        return;
      }
      int size = duvp.length;
      for (int y = 0; y < size; y++) {
        duvp[y] = Math.abs(duvp[y]);
      }
    }

    double[] mean = new double[2];
    double[] min = new double[2];
    double[] max = new double[2];
    double[] mix = new double[2];
    double[] std = new double[2];
    for (int x = 0; x < 2; x++) {
      mean[x] = Maths.mean(deltauvprimeList[x]);
      min[x] = Maths.min(deltauvprimeList[x]);
      max[x] = Maths.max(deltauvprimeList[x]);
      mix[x] = Math.sqrt(mean[x] * max[x]);
      std[x] = Maths.std(deltauvprimeList[x]);
    }
    this.meanDeltauvPrime = mean;
    this.minDeltauvPrime = min;
    this.maxDeltauvPrime = max;
    this.mixDeltauvPrime = mix;
    this.stdDeltauvPrime = std;
  }

  protected void statistics(double[][] deltaEs,
                            int[] deltaEDistribute,
                            double[][] CIE2000DeltaLCHs) {
    //==========================================================================
    // 色差的統計
    //==========================================================================
    deltaEs = DoubleArray.transpose(deltaEs);
    double[] std = new double[deltaEs.length];
    double[] max = new double[deltaEs.length];
    double[] mean = new double[deltaEs.length];
    double[] mix = new double[deltaEs.length];
    double[] min = new double[deltaEs.length];

    for (int x = 0; x < deltaEs.length; x++) {
      std[x] = Maths.std(deltaEs[x]);
      mean[x] = Maths.mean(deltaEs[x]);
      max[x] = Maths.max(deltaEs[x]);
      mix[x] = Math.sqrt(mean[x] * max[x]);
      min[x] = Maths.min(deltaEs[x]);
    }

    CIE2000DeltaLCHs = DoubleArray.transpose(CIE2000DeltaLCHs);
    double[] maxDeltaLCH = new double[CIE2000DeltaLCHs.length];
    double[] meanDeltaLCH = new double[CIE2000DeltaLCHs.length];
    double[] minDeltaLCH = new double[CIE2000DeltaLCHs.length];

    for (int x = 0; x < CIE2000DeltaLCHs.length; x++) {
      meanDeltaLCH[x] = Maths.mean(CIE2000DeltaLCHs[x]);
      maxDeltaLCH[x] = Maths.max(CIE2000DeltaLCHs[x]);
      minDeltaLCH[x] = Maths.min(CIE2000DeltaLCHs[x]);
    }
    //==========================================================================

    //==========================================================================
    //report數值的設定
    //==========================================================================
    this.maxDeltaE = new DeltaE();
    this.maxDeltaE.deltaE = max;
    this.meanDeltaE = new DeltaE();
    this.meanDeltaE.deltaE = mean;
    this.stdDeltaE = new DeltaE();
    this.stdDeltaE.deltaE = std;
    this.mixDeltaE = new DeltaE();
    this.mixDeltaE.deltaE = mix;
    this.minDeltaE = new DeltaE();
    this.minDeltaE.deltaE = min;
    this.deltaEDistribute = deltaEDistribute;

    this.meanCIE2000DeltaLCH = new DeltaE();
    this.meanCIE2000DeltaLCH.isCIE2000DeltaLCh = true;
    this.meanCIE2000DeltaLCH.CIE2000DeltaLCh = meanDeltaLCH;
    this.maxCIE2000DeltaLCH = new DeltaE();
    this.maxCIE2000DeltaLCH.isCIE2000DeltaLCh = true;
    this.maxCIE2000DeltaLCH.CIE2000DeltaLCh = maxDeltaLCH;
    this.minCIE2000DeltaLCH = new DeltaE();
    this.minCIE2000DeltaLCH.isCIE2000DeltaLCh = true;
    this.minCIE2000DeltaLCH.CIE2000DeltaLCh = minDeltaLCH;
    //==========================================================================
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *   <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    return compareTo(obj) == 0;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return super.hashCode();
  }

  public static void main(String[] args) {
//    double d = 1;
    System.out.println(Double.doubleToLongBits(1));
    System.out.println(Double.doubleToLongBits(2));
  }

  public void setReverseReport(boolean reverseReport) {
    this.reverseReport = reverseReport;
  }

}
