package shu.cms.lcd;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;

import org.apache.commons.collections.primitives.*;
import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.adapter.xls.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.measure.*;
import shu.cms.measure.calibrate.*;
import shu.cms.measure.meter.*;
import shu.cms.plot.*;
import shu.cms.reference.monitor.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.operator.*;
import shu.util.*;
import shu.util.log.*;

//import shu.plot.*;

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
public abstract class LCDTargetBase
    extends Target {
  protected LCDTargetBase(List<Patch> patchList) {
    super(patchList);
  }

  public final static class Calibrate {
    public final static void storeCalibration(LCDTarget referenceTarget,
                                              LCDTarget sampleTarget) {
      FourColorCalibrator calibrator = new FourColorCalibrator(referenceTarget,
          sampleTarget);
      String filename = produceCalibrationFilename(sampleTarget);
      Persistence.writeObjectAsXML(calibrator, filename);
    }

    public final static FourColorCalibrator loadFourColorCalibrator(LCDTarget
        sampleTarget) {
      String filename = produceCalibrationFilename(sampleTarget);
      FourColorCalibrator calibrator = (FourColorCalibrator) Persistence.
          readObjectAsXML(filename);
      return calibrator;
    }

    public final static LCDTarget calibrate(LCDTarget sampleTarget) {
      FourColorCalibrator calibrator = loadFourColorCalibrator(sampleTarget);
      calibrator.calibrate(sampleTarget);
      return sampleTarget;
    }

    protected final static String produceCalibrationFilename(LCDTarget
        sampleTarget) {
      LCDTarget.Source source = sampleTarget.measureMetadata.source;
      LCDTarget.TargetIlluminant targetIlluminat = sampleTarget.measureMetadata.
          targetIlluminant;
      LCDMetadata.Backlight backlight = null;
      if (sampleTarget.lcdMetadata != null) {
        backlight = sampleTarget.lcdMetadata.backlight;
      }

      if (backlight != null) {
        return CMSDir.Measure.Calibration + "/" + source + "/" +
            targetIlluminat + "-" + backlight.name() + ".cal";
      }
      else {
        return CMSDir.Measure.Calibration + "/" + source + "/" +
            targetIlluminat +
            ".cal";
      }
    }

    public final static LCDTarget calibrate(LCDTarget referenceTarget,
                                            LCDTarget sampleTarget) {
      FourColorCalibrator.calibrate(referenceTarget, sampleTarget);
      return sampleTarget;
    }

    public final static LCDTarget calibrate(LCDTarget referenceTarget,
                                            LCDTarget sampleTarget,
                                            LCDTarget lcdTarget) {
      FourColorCalibrator calibrator = new FourColorCalibrator(referenceTarget,
          sampleTarget);
      calibrator.calibrate(lcdTarget);
      return lcdTarget;
    }

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   *
   * <p>Copyright: Copyright (c) 2009</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   * @deprecated
   */
  public static enum Device {
    Sony, Dell_M1210, Dell_M1210_2, Dell_M1210_nonvcgt, Dell_M1210_nonvcgt2,
    EIZO_CE240W, EIZO_CG221, EIZO_CG221_2, EIZO_CG221_3, ViewSonic_VX2235WM
        , Dell_2407WFP_HC_normal, Dell_2407WFP_HC_custom, Dell_2407WFP_HC,
    CPT_Demo1, CPT_Demo2, Samsung_NCM, Samsung_Original, CPT_32inch,
    CPT_17inch_Demo2, CPT_37inch, CPT_320WA01C;

  }

  public static enum FileType {
    CGATS("cgats"), CxF("cxf"), VastView("txt"), USB4000(null), Logo("logo"),
    VastViewXLS("xls"), AUOXLS("xls"), AUORampXLS("xls");

    FileType(String extFilename) {
      this.extFilename = extFilename;
    }

    String extFilename;
  }

  public static enum TargetIlluminant {
    D50, D65, D90, D93, Native;
  }

  public static enum TargetType {
    Ramp, Test, XTalk, Complex, Unknow, Camera
  }

  public static class IO {
    public final static void store(LCDTarget lcdTarget, String filename) throws
        IOException {
      Meter meter = lcdTarget.getMeasureMeter();
      meter = (meter == null) ? new DummyMeter() : meter;
      LogoFile logoFile = new LogoFile(filename, lcdTarget.getPatchList(),
                                       meter);
      if (lcdTarget.inverseModeMeasure) {
        logoFile.setHeader(LogoFile.Reserved.InverseModeMeasure, "Yes");
      }
      logoFile.save();
    }

    public final static LCDTarget load(String filename) {
      return LCDTarget.Instance.getFromLogo(filename);
    }

  }

  public static enum Room {
    Dark("darkroom"), Light("lightroom");

    Room(String name) {
      this.name = name;
    }

    public String name;

  }

  public static enum Source {
    PR650(SourceType.Spectrometer), Calibrated(SourceType.Unknow),
    i1display2(SourceType.Colorimeter), i1pro(SourceType.Spectrometer),
    CA210(SourceType.Colorimeter), USB4000(SourceType.Spectrometer),
    Logo(SourceType.Unknow), K10(SourceType.Colorimeter),
    Huey(SourceType.Colorimeter), Dummy(SourceType.Colorimeter), Remote(
        SourceType.Unknow), Platform(SourceType.Unknow);

    Source(SourceType type) {
      this.type = type;
    }

    protected SourceType type;
  }

  static enum SourceType {
    Spectrometer, Colorimeter, Unknow
  }

  final static class MeasureMetadata
      implements Serializable {
    protected LCDTargetBase.Number number;
    protected Source source;
    protected TargetIlluminant targetIlluminant;
    protected Meter meter;
    protected Meter.Instr instrument;
  }

  public final static class Discrete {
    private final static boolean plot = false;
    public final static int[] getDiscreteIndex(LCDTarget lcdTarget,
                                               RGB.Channel ch, double threshold,
                                               int maxFind) {
      List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);
      ArrayIntList intList = new ArrayIntList();
      int size = patchList.size();
      double[][] xyValuesArray = new double[size][];

      for (int x = 0; x < size; x++) {
        Patch p = patchList.get(x);
        CIExyY xyY = new CIExyY(p.getXYZ());
        xyValuesArray[x] = xyY.getxyValues();
      }
      double[][] deltaxyValuesArray = DoubleArray.transpose(xyValuesArray);
      deltaxyValuesArray[0] = Maths.firstOrderDerivatives(deltaxyValuesArray[0]);
      deltaxyValuesArray[1] = Maths.firstOrderDerivatives(deltaxyValuesArray[1]);
      if (plot) {
        Plot2D p = Plot2D.getInstance(ch.name() + " 1st.");
        p.addLinePlot("x", 1, 254, deltaxyValuesArray[0]);
        p.addLinePlot("y", 1, 254, deltaxyValuesArray[1]);
        p.setVisible();
      }
      deltaxyValuesArray[0] = Maths.firstOrderDerivatives(deltaxyValuesArray[0]);
      deltaxyValuesArray[1] = Maths.firstOrderDerivatives(deltaxyValuesArray[1]);
      if (plot) {
        Plot2D p2 = Plot2D.getInstance(ch.name() + " 2nd.");
        p2.addLinePlot("x", 2, 254, deltaxyValuesArray[0]);
        p2.addLinePlot("y", 2, 254, deltaxyValuesArray[1]);
        p2.setVisible();
      }
      int size2 = deltaxyValuesArray[0].length;
      for (int x = size2 - 1; x >= 0; x--) {
        double d0 = deltaxyValuesArray[0][x];
        double d1 = deltaxyValuesArray[1][x];
        if (d0 > threshold || d1 > threshold) {
          int now = x + 2;
          if (intList.size() != 0) {
            int pre = intList.get(intList.size() - 1);
            if (Math.abs(pre - now) == 1) {
              intList.removeElement(pre);
            }
          }
          intList.add(now);
//          if (intList.size() == maxFind) {
//            break;
//          }
        }

      }
      return intList.subList(0, maxFind).toArray();
//      return intList.toArray();
    }

    private final static double[] getDiscretePolarTarget(CIExyY blackxyY,
        CIEXYZ XYZ0, CIEXYZ XYZ1, CIEXYZ XYZ2, CIEXYZ XYZ3) {

      CIExyY[] xyYArray = new CIExyY[4];
      xyYArray[0] = new CIExyY(XYZ0);
      xyYArray[1] = new CIExyY(XYZ1);
      xyYArray[2] = new CIExyY(XYZ2);
      xyYArray[3] = new CIExyY(XYZ3);
      double d0 = Maths.delta(new double[] {xyYArray[0].x, xyYArray[0].y},
                              new double[] {xyYArray[1].x, xyYArray[1].y});
      double d1 = Maths.delta(new double[] {xyYArray[2].x, xyYArray[2].y},
                              new double[] {xyYArray[3].x, xyYArray[3].y});
      //綜合兩者的距離
      double d = (d0 + d1) / 2;
      //取出相對於黑點的xy座標
      double[] dxyValues = xyYArray[2].getDeltaxy(blackxyY);
      double[] polarValues = CIExyY.cartesian2polarCoordinatesValues(dxyValues[
          0], dxyValues[1]);
      polarValues[0] -= d;
      double[] targetValues = CIExyY.polar2cartesianCoordinatesValues(
          polarValues[0], polarValues[1]);
      targetValues = DoubleArray.plus(targetValues, new double[] {blackxyY.x,
                                      blackxyY.y});
      return targetValues;
    }

    public static enum Method {
      Shift, PolarByWhite, PolarByBlack
    }

    private final static CIEXYZ getPureXYZ(CIEXYZ XYZ, int code, RGB.Channel ch,
                                           LCDModel model) {
      RGB.Channel[] channels = RGB.Channel.getBesidePrimaryChannel(ch);
      CIEXYZ pureXYZ = (CIEXYZ) XYZ.clone();
      for (RGB.Channel c : channels) {
        RGB rgb = new RGB();
        rgb.setValue(c, code, RGB.MaxValue.Int8Bit);
        CIEXYZ XYZ0 = model.getXYZ(rgb, true, true);
        pureXYZ = CIEXYZ.minus(pureXYZ, XYZ0);
      }
      return pureXYZ;
    }

    private final static double[] getDiscreteShiftTarget(int discret,
        RGB.Channel ch, MultiMatrixModel model) {
      RGB rgb = new RGB(discret, discret, discret);
      CIEXYZ XYZ = model.getXYZ(rgb, false, true);

      CIEXYZ targetXYZ = model.getNeutralXYZ(discret, false);
      CIEXYZ pureXYZ = getPureXYZ(XYZ, discret, ch, model);
      CIEXYZ pureTargetXYZ = getPureXYZ(targetXYZ, discret, ch, model);
      CIExyY purexyY = new CIExyY(pureXYZ);
      CIExyY pureTargetxyY0 = new CIExyY(pureTargetXYZ);
//      double[] deltaxyValues = pureTargetxyY0.getDeltaxy(purexyY);
      double[] deltaxyValues = purexyY.getDeltaxy(pureTargetxyY0);
      return deltaxyValues;

    }

    private static MultiMatrixModel multiMatrixModel;
    private static LCDTarget lcdTarget;
    private final static void fixDiscreteChromaticityByOffset(LCDTarget
        target, RGB.Channel ch, int ...discretCodes) {
      List<Patch> patchList = target.filter.oneValueChannel(ch);
      if (multiMatrixModel == null || target != lcdTarget) {
        lcdTarget = target;
        multiMatrixModel = new MultiMatrixModel(lcdTarget);
        multiMatrixModel.produceFactor();
      }
      for (int discret : discretCodes) {
        double[] targetxyValues = getDiscreteShiftTarget(discret, ch,
            multiMatrixModel);
        OffsetOperator op = new OffsetOperator(targetxyValues);

        for (int x = 0; x < discret; x++) {
          Patch p = patchList.get(x);
          CIEXYZ XYZ = p.getXYZ();
          CIExyY xyY = new CIExyY(XYZ);
          double[] xyValues = op.getXY(xyY.x, xyY.y);
          xyY.x = xyValues[0];
          xyY.y = xyValues[1];
          XYZ = xyY.toXYZ();
          Patch.Operator.setXYZ(p, XYZ);
        }
      }
    }

    private final static void fixDiscreteChromaticityByPolar(LCDTarget
        lcdTarget, RGB.Channel ch, Method method, int ...discretCodes) {
      Patch originalPatch = (method == Method.PolarByWhite) ?
          lcdTarget.getWhitePatch() : lcdTarget.getBlackPatch();
      CIExyY originalxyY = new CIExyY(originalPatch.getXYZ());
      List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);

      for (int discret : discretCodes) {
        Patch p00 = patchList.get(discret - 2);
        Patch p0 = patchList.get(discret - 1);
        Patch p1 = patchList.get(discret);
        Patch p11 = patchList.get(discret + 1);

        CIEXYZ XYZ0 = p0.getXYZ();

        double[] targetxyValues = getDiscretePolarTarget(originalxyY,
            p00.getXYZ(),
            XYZ0, p1.getXYZ(), p11.getXYZ());

        CIExyY xyY0 = new CIExyY(XYZ0);

        shu.math.operator.Operator op = shu.math.operator.Operator.
            getAdjustOperator(new double[] {originalxyY.x, originalxyY.y},
                              new double[] {xyY0.x, xyY0.y},
                              targetxyValues);
        for (int x = 0; x < discret; x++) {
          Patch p = patchList.get(x);
          CIEXYZ XYZ = p.getXYZ();
          CIExyY xyY = new CIExyY(XYZ);
          double[] xyValues = op.getXY(xyY.x, xyY.y);
          xyY.x = xyValues[0];
          xyY.y = xyValues[1];
          XYZ = xyY.toXYZ();
          Patch.Operator.setXYZ(p, XYZ);
        }
      }
    }

    public final static void fixDiscreteChromaticity(LCDTarget lcdTarget,
        RGB.Channel ch, Method method, int ...discretCodes) {
      switch (method) {
        case Shift:
          fixDiscreteChromaticityByOffset(lcdTarget, ch, discretCodes);
          break;
        case PolarByWhite:
        case PolarByBlack:
          fixDiscreteChromaticityByPolar(lcdTarget, ch, method, discretCodes);
          break;
      }
    }

    public final static void fixDiscreteChromaticity(LCDTarget lcdTarget,
        RGB.Channel ch, int ...discretCodes) {
      Patch originalPatch = lcdTarget.getBlackPatch();
      CIExyY originalxyY = new CIExyY(originalPatch.getXYZ());
      List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);

      for (int discret : discretCodes) {
        Patch p00 = patchList.get(discret - 2);
        Patch p0 = patchList.get(discret - 1);
        Patch p1 = patchList.get(discret);
        Patch p11 = patchList.get(discret + 1);

        CIEXYZ XYZ0 = p0.getXYZ();

        double[] targetxyValues = getDiscretePolarTarget(originalxyY,
            p00.getXYZ(), XYZ0, p1.getXYZ(), p11.getXYZ());

        CIExyY xyY0 = new CIExyY(XYZ0);

        shu.math.operator.Operator op = shu.math.operator.Operator.
            getAdjustOperator(new double[] {originalxyY.x, originalxyY.y},
                              new double[] {xyY0.x, xyY0.y},
                              targetxyValues);
        for (int x = 0; x < discret; x++) {
          Patch p = patchList.get(x);
          CIEXYZ XYZ = p.getXYZ();
          CIExyY xyY = new CIExyY(XYZ);
          double[] xyValues = op.getXY(xyY.x, xyY.y);
          xyY.x = xyValues[0];
          xyY.y = xyValues[1];
          XYZ = xyY.toXYZ();
          Patch.Operator.setXYZ(p, XYZ);
        }
      }
    }

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 操作LCDTarget相關的Class
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public final static class Operator {

    /**
     * 撿查亮度是否遞增
     * @param lcdTarget LCDTarget
     * @return boolean
     */
    public final static boolean checkIncreaseProgressively(LCDTarget lcdTarget) {
      Number number = lcdTarget.getNumber();
      if (number.isRamp()) {
        RGBBase.Channel[] channels = number.channels;
        for (RGBBase.Channel ch : channels) {
          if (false == checkIncreaseProgressively(lcdTarget, ch)) {
            return false;
          }
        }
        return true;
      }
      else {
        throw new IllegalArgumentException(
            "LCDTarget is not ramp, cannot checking.");
      }
    }

    /**
     * 撿查亮度是否遞增
     * @param lcdTarget LCDTarget
     * @param ch Channel
     * @return boolean
     */
    public final static boolean checkIncreaseProgressively(LCDTarget lcdTarget,
        RGBBase.Channel ch) {
      List<Patch> patchList = lcdTarget.filter.grayScalePatch(ch);
      int size = patchList.size();
      for (int x = 0; x < size - 1; x++) {
        CIEXYZ XYZ0 = patchList.get(x).getXYZ();
        CIEXYZ XYZ1 = patchList.get(x + 1).getXYZ();
        if (XYZ0.Y > XYZ1.Y) {
          return false;
        }
      }
      return true;
    }

    /**
     * clip的修正, 用clip點周圍兩點內插出亮度
     * @param lcdTarget LCDTarget
     * @param fixCode int
     */
    public final static void clippingFix(LCDTarget lcdTarget,
                                         int fixCode) {
      for (RGBBase.Channel ch : RGBBase.Channel.RGBYMCWChannel) {
        List<Patch> patchList = lcdTarget.filter.grayScalePatch(ch);
        int size = patchList.size();
        int fixIndex = size - (256 - fixCode);
        if (size != 0 && size != 1) {
          //修正clipping的現象, 作法就是用內插
          Patch clip = patchList.get(fixIndex);
          Patch next = patchList.get(fixIndex + 1);

          Patch pre = patchList.get(fixIndex - 1);
          CIExyY prexyY = new CIExyY(pre.getXYZ());
          CIExyY nextxyY = new CIExyY(next.getXYZ());
          double clipY = (prexyY.Y + nextxyY.Y) / 2;
          CIExyY clipxyY = new CIExyY(clip.getXYZ());
          clipxyY.Y = clipY;
          Patch.Operator.setXYZ(clip, clipxyY.toXYZ());
        }
      }

      recalculateLCDTarget(lcdTarget);
    }

    /**
     * 取得最亮點patch的index
     * @param patchList List
     * @param startIndex int
     * @return int
     */
    private final static int getBrighterIndex(List<Patch> patchList,
        int startIndex) {
      int size = patchList.size();
      double lumi = patchList.get(startIndex).getXYZ().Y;
      for (int x = startIndex + 1; x < size; x++) {
        double Y = patchList.get(x).getXYZ().Y;
        if (Y > lumi) {
          return x;
        }
      }
      return -1;
    }

    /**
     * 修正反轉的問題
     * 主要是修正 1.儀器or面板不穩定造成的反轉 2.面板本身特性
     * @param lcdTarget LCDTarget
     */
    public final static void gradationReverseFix(LCDTarget lcdTarget) {
      Patch whitePatch = lcdTarget.getWhitePatch();
      CIEXYZ whiteXYZ = whitePatch.getXYZ();

      for (RGBBase.Channel ch : RGBBase.Channel.RGBYMCWChannel) {
        List<Patch> patchList = lcdTarget.filter.grayScalePatch(ch);
        int size = patchList.size();
        for (int x = 1; x < size - 1; x++) {
          Patch p = patchList.get(x);
          int preIndex = x - 1;
          Patch pre = patchList.get(preIndex);
          if (p.getXYZ().Y < pre.getXYZ().Y) {
            int brighterIndex = getBrighterIndex(patchList, preIndex);
            double Y = 0;
            //如果找不到, 就設一樣吧
            if (brighterIndex == -1) {
              Y = pre.getXYZ().Y;
            }
            else {
              Patch next = patchList.get(brighterIndex);

              double preCode = pre.getRGB().getValue(ch);
              double nextCode = next.getRGB().getValue(ch);
              double code = p.getRGB().getValue(ch);
              Y = Interpolation.linear(preCode, nextCode, pre.getXYZ().Y,
                                       next.getXYZ().Y, code);
            }

            CIExyY xyY = new CIExyY(p.getXYZ());
            xyY.Y = Y;
            CIEXYZ newXYZ = xyY.toXYZ();

            Patch.Operator.setXYZ(p, newXYZ);
            CIEXYZ newNormalXYZ = (CIEXYZ) newXYZ.clone();
            newNormalXYZ.normalize(whiteXYZ);

            Patch.Operator.setNormalizedXYZ(p, newNormalXYZ);
          }
        }
        if (size != 0 && size != 1) {
          //修正最後一個code會有反轉的問題(gamma反轉)
          Patch end1Patch = patchList.get(size - 1);
          Patch end2Patch = patchList.get(size - 2);
          if (end2Patch.getXYZ().Y > end1Patch.getXYZ().Y) {
            Patch.Operator.setXYZ(end1Patch, end2Patch.getXYZ());
          }
        }
      }

      recalculateLCDTarget(lcdTarget);
//      return lcdTarget;
    }

    /**
     * 重新計算LCDTarget的重要係數
     * @param lcdTarget LCDTarget
     */
    private final static void recalculateLCDTarget(LCDTarget lcdTarget) {
      Patch whitePatch = lcdTarget.getWhitePatch();
      CIEXYZ whiteXYZ = whitePatch.getXYZ();

      lcdTarget.calculatePatchLab(whiteXYZ);
      lcdTarget.luminance = whiteXYZ;
      lcdTarget.calculateNormalizedXYZ();

    }

    /**
     * 設定LCD的Number
     * @param lcdTarget LCDTarget
     * @param number Number
     */
    public final static void setNumber(LCDTarget lcdTarget,
                                       Number number) {
      lcdTarget.setNumber(number);
    }

    /**
     * 結合兩個LCDTarget成一個
     * @param target1 LCDTarget
     * @param target2 LCDTarget
     * @return LCDTarget
     */
    public final static LCDTarget combine(LCDTarget target1,
                                          LCDTarget target2) {

      List<Patch> patchList1 = target1.getPatchList();
      List<Patch> patchList2 = target2.getPatchList();

      int size = patchList1.size() + patchList2.size();
      List<Patch> patchList = new ArrayList<Patch> (size);
      patchList.addAll(patchList1);
      patchList.addAll(patchList2);

      LCDTarget combine = new LCDTarget(patchList,
                                        LCDTargetBase.Number.Unknow, false);
      combine.setDescription(target1.getDescription() + "+" +
                             target2.getDescription());
      combine.setDevice(target1.getDevice() + "+" + target2.getDevice());
      return combine;
    }

    /**
     * 將targets作平均
     * @param targets LCDTarget[]
     * @return LCDTarget
     */
    public final static LCDTarget average(LCDTarget[] targets) {
      int targetSize = targets.length;
      LCDTarget target1 = targets[0];
      int patchSize = target1.size();
      List<Patch> avePatchList = new ArrayList<Patch> (patchSize);

      for (int x = 0; x < patchSize; x++) {
        CIEXYZ aveXYZ = new CIEXYZ();
        for (int y = 0; y < targetSize; y++) {
          CIEXYZ XYZ = targets[y].getPatch(x).getXYZ();
          aveXYZ.X += XYZ.X;
          aveXYZ.Y += XYZ.Y;
          aveXYZ.Z += XYZ.Z;
        }
        aveXYZ.X /= targetSize;
        aveXYZ.Y /= targetSize;
        aveXYZ.Z /= targetSize;
        Patch p = targets[0].getPatch(x);
        Patch copy = new Patch(p.getName(), aveXYZ, null, p.getRGB());
        avePatchList.add(copy);
      }

      LCDTarget average = new LCDTarget(avePatchList, target1.getNumber(), false);
      average.setDescription(getDescription(targets));
      average.setDevice(target1.getDevice());
      return average;
    }

    /**
     * 取得targets所有的description
     * @param targets LCDTarget[]
     * @return String
     */
    private final static String getDescription(LCDTarget[] targets) {
      StringBuilder buf = new StringBuilder();
      for (LCDTarget target : targets) {
        buf.append(target.getDescription());
      }
      return buf.toString();
    }

  }

  public final static class Measured {
    public final static LCDTarget measure(Source source,
                                          LCDTargetBase.Number number,
                                          boolean calibrate) {
      return measure(source, number, calibrate, null);
    }

    public final static LCDTarget measure(Source source,
                                          LCDTargetBase.Number number,
                                          boolean calibrate, DICOM dicom) {
      //取得相對應的meter
      Meter meter = getMeter(source);
      //把meter跟量測程序結合
      MeterMeasurement mm = new MeterMeasurement(meter, dicom, calibrate);
      //設定是否要解決反轉
      mm.setDo255InverseMode(InverseModeMeasure);
      mm.setDoBlankInsert(MeasureBlankInsert);
      mm.setBlankTimes(MeasureBlankTime);
      mm.setWaitTimes(MeasureWaitTime);
      mm.setBlankAndBackground(BlankColor, BackgroundColor);
      return measure(mm, number);
    }

    public final static LCDTarget measure(MeterMeasurement meterMeasurement,
                                          LCDTargetBase.Number number) {
      List<RGB> rgbList = LCDTarget.Instance.getRGBList(number);
      return measure0(meterMeasurement, rgbList, null, number);
    }

    public final static LCDTarget measure(MeterMeasurement meterMeasurement,
                                          List<RGB> rgbList) {
      return measure0(meterMeasurement, rgbList, null,
                      LCDTargetBase.Number.Unknow);
    }

    public final static LCDTarget measure(MeterMeasurement meterMeasurement,
                                          List<RGB> rgbList,
        List<String> patchNameList) {
      return measure0(meterMeasurement, rgbList, patchNameList,
                      LCDTargetBase.Number.Unknow);
    }

    private static RGBLumiComparator rgbComparator = RGBLumiComparator.
        getInstance();

    /**
     * 是否依照RGB的亮度先做排序
     */
    private static boolean SortByRGBLumi = false;
    /**
     * 量測時是否先插黑再量
     */
    private static boolean MeasureBlankInsert = false;
    /**
     * 插黑的時間
     */
    private static int MeasureBlankTime = MeterMeasurement.DefaultBlankTimes;
    /**
     * 量測色塊之間的等待時間
     */
    private static int MeasureWaitTime = MeterMeasurement.DefaultWaitTimes;

    private static Color BlankColor = Color.black;
    private static Color BackgroundColor = Color.black;

    public final static void setBlankColor(Color color) {
      BlankColor = color;
    }

    public final static void setBackgroundColor(Color color) {
      BackgroundColor = color;
    }

    /**
     * 是否針對反轉的面板特別處理
     */
    private static boolean InverseModeMeasure = true;

    public final static void setInverseModeMeasure(boolean inverseMeasure) {
      InverseModeMeasure = inverseMeasure;
    }

    public final static void setMeasureBlankInsert(boolean blankInsert) {
      MeasureBlankInsert = blankInsert;
    }

    public final static void setMeasureBlankTime(int blankTime) {
      MeasureBlankTime = blankTime;
    }

    public final static void setMeasureWaitTime(int waitTime) {
      MeasureWaitTime = waitTime;
    }

    /**
     * 設定是否依照RGB的亮度先做排序
     * @param sortByRGBLumi boolean
     */
    public final static void setSortByRGBLumi(boolean sortByRGBLumi) {
      SortByRGBLumi = sortByRGBLumi;
    }

    private final static LCDTarget measure0(MeterMeasurement meterMeasurement,
                                            List<RGB> rgbList,
        List<String> patchNameList, LCDTargetBase.Number number) {
      if (SortByRGBLumi) {
        List<RGB> list = new ArrayList<RGB> (rgbList);
        Collections.sort(list, rgbComparator);
        rgbList = list;
      }
      //量測
      List<Patch> patchList = meterMeasurement.measure(rgbList, patchNameList);
      if (patchList.size() != rgbList.size()) {
        return null;
      }
      //轉換成LCDTarget
      LCDTarget lcdTarget = LCDTarget.Instance.get(patchList, number,
          InverseModeMeasure);
      Meter meter = meterMeasurement.getMeter();
      lcdTarget.measureMetadata.meter = meter;
      lcdTarget.measureMetadata.instrument = meter.getType();

      //========================================================================
      // 預先儲存起來
      //========================================================================
      String numberName = (lcdTarget.getNumber() != LCDTargetBase.Number.Unknow) ?
          lcdTarget.getNumber().name() : "Unknow";
//      if (lcdTarget.getNumber() != LCDTargetBase.Number.Unknow) {
      saveToLogoLog(lcdTarget,
                    System.currentTimeMillis() + "_" + numberName + ".logo");
//      }
      //========================================================================

      return lcdTarget;
    }

    private final static String LogoLogDir = "logo.log";

    private final static void saveToLogoLog(LCDTarget lcdTarget,
                                            String filename) {
      //========================================================================
      // 目錄不存在就建立目錄
      //========================================================================
      File dir = new File(LogoLogDir);
      if (!dir.exists()) {
        dir.mkdir();
      }
      //========================================================================
      try {
        LogoFile file = new LogoFile(LogoLogDir + "/" + filename, true);
        Meter meter = lcdTarget.getMeasureMeter();
        meter.setLogoFileHeader(file);
        meter.setLogoFileData(file, lcdTarget.getPatchList());
        file.save();
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }

    private static CA210 ca210 = null;
    private static EyeOneDisplay2 i1d2 = null;

    private final static Meter getMeter(Source source) {
      Meter meter = null;
      switch (source) {
        case CA210:
          if (ca210 == null) {
            ca210 = new CA210();
          }
          return ca210;
        case Huey:
          meter = new ArgyllDispMeter();
          break;
        case i1display2:
        case i1pro:
          if (i1d2 == null) {
            i1d2 = new EyeOneDisplay2(EyeOneDisplay2.ScreenType.LCD);
          }
          return i1d2;
        case Dummy:
          meter = new DummyMeter();
          break;
        case Remote:
          meter = RemoteMeter.getDefaultInstance();
          break;
        case Platform:
          meter = new ShareMemoryMeter();
          break;
      }
      return meter;
    }

  }

  final static class Filename {

    /**
     * 藉由source及fileType來產生對應的filename(or dirname)
     * @param device String
     * @param source Source
     * @param room Room
     * @param illuminant TargetIlluminant
     * @param number Number
     * @param fileType FileType
     * @param dirTag String
     * @param fileTag String
     * @return String
     */
    protected final static String produceFilename(String device,
                                                  Source source,
                                                  Room room,
                                                  TargetIlluminant illuminant,
                                                  LCDTargetBase.Number number,
                                                  FileType fileType,
                                                  String dirTag, String fileTag) {
      if (fileType == null) {
        switch (source) {
          case i1display2:
          case i1pro:
            return produceNormalFilename(device, source,
                                         room,
                                         illuminant,
                                         number, FileType.CxF, dirTag,
                                         fileTag);

          case CA210:
          case K10:
            return produceVastViewTxTFilename(device,
                                              source,
                                              room,
                                              illuminant,
                                              number, dirTag, fileTag);

          case USB4000:
            return produceUSB4000DIRName(device, source,
                                         room,
                                         illuminant,
                                         number, dirTag, fileTag);

          case Logo:
            return produceNormalFilename(device, source,
                                         room,
                                         illuminant,
                                         number, FileType.Logo, dirTag,
                                         fileTag);
        }
      }
      else {
        switch (fileType) {
          case CGATS:
          case Logo:
          case CxF:
          case AUOXLS:
          case AUORampXLS:
          case VastViewXLS:
            return produceNormalFilename(device, source, room, illuminant,
                                         number, fileType, dirTag, fileTag);
          case VastView:
            return produceVastViewTxTFilename(device, source, room, illuminant,
                                              number, dirTag, fileTag);
          case USB4000:
            return produceUSB4000DIRName(device, source, room, illuminant,
                                         number, dirTag, fileTag);
        }
      }
      return null;
    }

    private final static String produceUSB4000DIRName(String device,
        Source source, Room room, TargetIlluminant illuminant,
        LCDTargetBase.Number number, String dirTag, String fileTag) {
      return CMSDir.Measure.Monitor + "/" + device + "/" +
          source.name() + "/" +
          room.name + "/" + illuminant.name() + "/" + number.patchCount +
          (fileTag != null ? "-" + fileTag + "/" : "/");
    }

    private final static String produceVastViewTxTFilename(String device,
        Source source,
        Room room,
        TargetIlluminant illuminant,
        LCDTargetBase.Number number, String dirTag, String fileTag) {
      String numberStr = null;
      switch (number) {
        case Ramp7147:
          numberStr = "7168";
          break;
        case Ramp1021:
          numberStr = "1024";
          break;
        default:
          numberStr = Integer.toString(number.patchCount);
      }

      String filename = (fileTag != null && fileTag.length() != 0) ?
          numberStr + "-" + fileTag + ".txt" :
          numberStr + ".txt";
      String dirname = getDirname(dirTag);
      return CMSDir.Measure.Monitor + "/" + device + "/" +
          source.name() + "/" +
          room.name + "/" + illuminant.name() + "/" + dirname + filename;
    }

    private final static String getDirname(String dirTag) {
      String dirname = (dirTag != null && dirTag.length() != 0) ?
          dirTag + "/" : "";
      return dirname;
    }

    /**
     * 依照各個tag產生檔案名稱
     * @param device String
     * @param source Source
     * @param room Room
     * @param illuminant TargetIlluminant
     * @param number Number
     * @param fileType FileType
     * @param dirTag String
     * @param fileTag String
     * @return String
     */
    private final static String produceNormalFilename(String device,
        Source source, Room room, TargetIlluminant illuminant,
        LCDTargetBase.Number number, FileType fileType, String dirTag,
        String fileTag) {
      String numbername = number.isComplex() ? number.referenceFilename :
          Integer.toString(number.patchCount);
      String filename = (fileTag != null && fileTag.length() != 0) ?
          numbername + "-" + fileTag + "." + fileType.extFilename :
          numbername + "." + fileType.extFilename;
      String dirname = getDirname(dirTag);
      return CMSDir.Measure.Monitor + "/" + device + "/" +
          source.name() + "/" + room.name + "/" + illuminant.name() + "/" +
          dirname + filename;
    }

    private final static String[] produceLogoFilenames(String device,
        Source source, Room room, TargetIlluminant illuminant,
        LCDTargetBase.Number number, String dirTag, String fileTag) {

      if (!number.isComplex()) {
        throw new IllegalArgumentException("!number .isComplex()");
      }

      FileType fileType = FileType.Logo;

      Number[] numbers = number.numbers;
      int size = numbers.length;
      String[] filenames = new String[size];
      for (int x = 0; x < size; x++) {
        Number realnumber = numbers[x];

        String numbername = realnumber.isComplex() ?
            realnumber.referenceFilename :
            Integer.toString(realnumber.patchCount);
        String filename = (fileTag != null && fileTag.length() != 0) ?
            numbername + "-" + fileTag + "." + fileType.extFilename :
            numbername + "." + fileType.extFilename;
        String dirname = getDirname(dirTag);
        filenames[x] = CMSDir.Measure.Monitor + "/" + device + "/" +
            source.name() + "/" + room.name + "/" + illuminant.name() + "/" +
            dirname + filename;

      }
      return filenames;
    }

    /**
     * 取得filename裡面的filename(去除掉路徑)
     * @param filename String
     * @return String
     */
    protected final static String getOnlyFilename(String filename) {
      if (filename == null) {
        return null;
      }
      int backslash = filename.lastIndexOf('\\');
      int slash = filename.lastIndexOf('/');
      slash = backslash > slash ? backslash : slash;
      return filename.substring(slash + 1);
    }

  }

  public static class TargetNumber {

  }

  public static enum Number {
    //==========================================================================
    // 少量色塊
    //==========================================================================
    BlackAndWhite(2, "Black&White", TargetType.Unknow, -1, ""),
    FourColor(4, "4Color", TargetType.Unknow, -1, ""),
    FiveColor(5, "5Color", TargetType.Unknow, -1, ""),
    //==========================================================================

    //==========================================================================
    // 規律色塊
    //==========================================================================
    //PLCC
    PLCC(58, "PLCC", TargetType.Unknow, -1, ""),
    //125中 單一與兩兩 from 125
    Patch62(62, null, TargetType.Unknow, -1, ""),
    //8874用的最少量測點
    Patch79(79, "Patch 79", TargetType.Unknow, -1, ""),
    //5*5*5
    Patch125(125, "Lo 125", TargetType.Unknow, 63, ""),
    //單一與兩兩(可從729推)
    Patch218(218, "Lo 218", TargetType.Unknow, 32, ""),
    //==========================================================================

    //==========================================================================
    // 特別或難定義色塊
    //==========================================================================
    Unknow( -1, null, TargetType.Unknow, -1, ""),
    //UGRA
    URGA_ProfileQuality(35, "UGRA-Profile Quality", TargetType.Unknow, -1, ""),
    //WHQL
    WHQL(46, "WHQL", TargetType.Unknow, -1, ""),
    //XRite內建校正螢幕用99色塊
    XRite2_0(99, "LCD Monitor Reference 2.0", TargetType.Unknow,
             -1, "LCD Monitor Reference 2.0"),
    Surface1352(1352, "", TargetType.Unknow, 17, "Surface Color 1352"),
    AUOHue96(96, "", TargetType.Unknow, -1, "AUO Hue 96"),
    //==========================================================================

    //==========================================================================
    // 相機用色塊
    //==========================================================================
//    ColorChecker24(24, "Color Checker 24", TargetType.Camera, -1,
//                   "Color Checker 24"),
    Camera( -1, "Camera Chart", TargetType.Camera, -1, ""),
    //==========================================================================

    //==========================================================================
    // ramp
    //==========================================================================
    //W 0~255,可用來測試儀器穩定度
    Ramp256W(256, "Ramp 256 W", 1, "", RGBBase.Channel.W),
    Ramp256R(256, "Ramp 256 R", 1, "", RGBBase.Channel.R),
    Ramp256G(256, "Ramp 256 G", 1, "", RGBBase.Channel.G),
    Ramp256B(256, "Ramp 256 B", 1, "", RGBBase.Channel.B),
    Ramp256R_W(257, "Ramp 256 R+W", 1, "", RGBBase.Channel.R),
    Ramp256G_W(257, "Ramp 256 G+W", 1, "", RGBBase.Channel.G),
    Ramp256B_W(257, "Ramp 256 B+W", 1, "", RGBBase.Channel.B),
    //step為4
    Ramp256_6Bit(256, "Ramp 256(6bit)", 4, "", true,
                 RGBBase.Channel.RGBWChannel),
    //step為4
    Ramp257_6Bit(257, "Ramp 257(6bit)", 4, "", true,
                 RGBBase.Channel.RGBWChannel),
    //step為4,加上最後四個255: 64*4+1*4=260 from 1024
    Ramp260(260, null, 4, "Ramp 260",
            RGBBase.Channel.RGBWChannel),
    //step為2的R/G/B/Grey from 1021
    Ramp509(509, null, 2, "Ramp 509",
            RGBBase.Channel.RGBWChannel),
    //step為1,僅有RGB的ramp,加上最後的白
    Ramp256RGB_W(767, "Ramp 256 RGB+W", 1, "",
                 RGBBase.Channel.RGBChannel),

    //step為2的R/G/B/C/M/Y/Grey(W) from 1792
    Ramp896(896, null, 2, "Ramp 896",
            RGBBase.Channel.RGBYMCWChannel),
    //step為2的R/G/B/C/M/Y/Grey(W)
    Ramp897(897, "Ramp 897", 2, "",
            RGBBase.Channel.RGBYMCWChannel),
    //step為1的R/G/B/Grey
    Ramp1021(1021, "Ramp 1021", 1, "",
             RGBBase.Channel.RGBWChannel),
    //step為1的R/G/B/Grey (black有重複) (from VastView)
    Ramp1024(1024, "Ramp 1024", 1, "",
             RGBBase.Channel.RGBWChannel),
    //step為1的R/G/B/C/M/Y/Grey(W) (black有重複) (from VastView)
    Ramp1792(1792, "Ramp 1792", 1, "",
             RGBBase.Channel.RGBYMCWChannel),

    //step為.5的R/G/B/Grey(W) (white有重複)
    Ramp2048(2048, null, .5, "", RGBBase.Channel.RGBWChannel),
    //step為.5的R/G/B/C/M/Y/Grey(W)
    Ramp3577(3577, null, .5, "", RGBBase.Channel.RGBYMCWChannel),
    //step為.25的R/G/B/Grey(W)
    Ramp4084(4084, null, .25, "", RGBBase.Channel.RGBWChannel),
    //step為.25的R/G/B/Grey(W) (white有重複)
    Ramp4096(4096, null, .25, "", RGBBase.Channel.RGBWChannel),
    Ramp16320(16320, null, 0.0625, "",
              RGBBase.Channel.RGBWChannel),
    //step為.25的R/G/B/C/M/Y/Grey(W)
    Ramp7147(7147, null, .25, "",
             RGBBase.Channel.RGBYMCWChannel),
    //step為.25的R/G/B/C/M/Y/Grey(W) (white有重複) (from VastView)
    Ramp7168(7168, null, .25, "",
             RGBBase.Channel.RGBYMCWChannel),
    //==========================================================================

    //==========================================================================
    // test
    //==========================================================================
    //8*8*8
    Test512(512, "Lo 512", TargetType.Test, 37, ""),
    //9*9*9
    Test729(729, "Lo 729", TargetType.Test, 32, ""),
    //12*12*12
    AUO729(729, "AUO 729", TargetType.Test, 32, ""),
    //12*12*12
    Test1728(1728, "Test 1728", TargetType.Test, 24, ""),
    //16*16*16
    Test4096(4096, "Test 4096", TargetType.Test, 17, ""),
    //17*17*17
    Test4913(4913, "Test 4913", TargetType.Test, 16, ""),
    //17*17*17 start:8 interval:16
    Test4913_6bit(4913, "Test 4913(6bit)", TargetType.Test, 16,
                  "", true),
    //21*21*21
    Test9261(9261, "Test 9261", TargetType.Test, 13, ""),
    //==========================================================================

    //==========================================================================
    // xtalk
    //==========================================================================
    //16*16*3+white "只有"兩兩有值
    Xtalk769(769, "Xtalk 769", TargetType.XTalk, 16, ""),
    // 6bit專用,32*32*x+white
    Xtalk3073_6Bit(3073, "Xtalk 3073(6bit)", TargetType.XTalk,
                   8, "", true),
    Xtalk589_6Bit(589, "Xtalk 589(6bit)", TargetType.XTalk,
                  20, "", true),
    //38*38*3+white 兩兩有值
    Xtalk4333(4333, "Xtalk 4333", TargetType.XTalk, 7, ""),
    //38*38*3+white "只有"兩兩有值
    Xtalk4108(4108, "Xtalk 4108", TargetType.XTalk, 7, ""),
    Xtalk5548(5548, "Xtalk 5548", TargetType.XTalk, 6, ""),
    Xtalk7804(7804, "Xtalk 7804", TargetType.XTalk, 5, ""),
    Xtalk12289(12289, "Xtalk 12289", TargetType.XTalk, 4, ""),
    //==========================================================================



    //==========================================================================
    // complex
    //==========================================================================
    //1021+4096+4108
    Complex1021_4096_4333(Ramp1021, Test4096, Xtalk4333),
    Complex1021_4096_4108(Ramp1021, Test4096, Xtalk4108),
    Complex1021_4108(Ramp1021, Xtalk4108),
    Complex1021_4096_769(Ramp1021, Test4096, Xtalk769),
    Complex1021_769(Ramp1021, Xtalk769),
    Complex1021_4096_729(Ramp1021, Test4096, Test729),
    Complex257_4913_3073(Ramp257_6Bit, Test4913_6bit, Xtalk3073_6Bit),
    Complex257_589(Ramp257_6Bit, Xtalk589_6Bit),
    Complex729_46_96_1352(AUO729, WHQL, AUOHue96, Surface1352),
    Complex729_46_96(AUO729, WHQL, AUOHue96);

    //==========================================================================

    /**
     * for normal
     * @param patchCount int
     * @param referenceFilename String
     * @param targetType TargetType
     * @param step double
     * @param description String
     */
    private Number(int patchCount,
                   String referenceFilename,
                   TargetType targetType, double step,
                   String description) {
      this(patchCount, referenceFilename, -1, targetType, step,
           description, null, null, false);
    }

    /**
     * for normal(6bit)
     * @param patchCount int
     * @param referenceFilename String
     * @param targetType TargetType
     * @param step double
     * @param description String
     * @param is6Bit boolean
     */
    private Number(int patchCount, String referenceFilename,
                   TargetType targetType, double step, String description,
                   boolean is6Bit) {
      this(patchCount, referenceFilename, -1, targetType, step,
           description, null, null, is6Bit);
    }

    /**
     * for ramp
     * @param patchCount int
     * @param referenceFilename String
     * @param step double
     * @param description String
     * @param channels Channel[]
     */
    private Number(int patchCount, String referenceFilename,
                   double step, String description, RGBBase.Channel ...channels) {
      this(patchCount, referenceFilename, -1, TargetType.Ramp, step,
           description, channels, null, false);
    }

    /**
     * for ramp(6bit)
     * @param patchCount int
     * @param referenceFilename String
     * @param step double
     * @param description String
     * @param is6bit boolean
     * @param channels Channel[]
     */
    private Number(int patchCount, String referenceFilename,
                   double step, String description, boolean is6bit,
                   RGBBase.Channel ...channels) {
      this(patchCount, referenceFilename, -1, TargetType.Ramp, step,
           description, channels, null, is6bit);
    }

    /**
     * for complex
     * @param numbers Number[]
     */
    private Number(Number ...numbers) {
      this(getPatchCount(numbers), getReferenceFilename(numbers),
           numbers.length,
           TargetType.Complex, -1, getDescription(numbers), null, numbers, false);
    }

    private final static String getReferenceFilename(Number ...numbers) {
      StringBuilder buf = null;
      for (Number num : numbers) {
        if (buf == null) {
          buf = new StringBuilder(Integer.toString(num.patchCount));
        }
        else {
          buf.append('+');
          buf.append(Integer.toString(num.patchCount));
        }
      }
      return buf.toString();
    }

    private final static String getDescription(Number ...numbers) {
      StringBuilder buf = null;
      for (Number num : numbers) {
        if (buf == null) {
          buf = new StringBuilder(num.description);
        }
        else {
          buf.append(" + ");
          buf.append(num.description);
        }
      }
      return buf.toString();
    }

    private final static int getPatchCount(Number ...numbers) {
      int count = 0;
      for (Number num : numbers) {
        count += num.patchCount;
      }
      return count;
    }

    /**
     * 0-constructor
     * @param patchCount int
     * @param referenceFilename String
     * @param targetCount int
     * @param targetType TargetType
     * @param step double
     * @param description String
     * @param channels Channel[]
     * @param numbers Number[]
     * @param is6Bit boolean
     */
    private Number(int patchCount, String referenceFilename,
                   int targetCount, TargetType targetType, double step,
                   String description, RGBBase.Channel[] channels,
                   Number[] numbers, boolean is6Bit) {
      this.patchCount = patchCount;
      this.referenceFilename = referenceFilename;
      this.targetType = targetType;
      this.step = step;
      if (description == null || description.length() == 0) {
        this.description = referenceFilename;
      }
      else {
        this.description = description;
      }

      this.channels = channels;
      if (targetCount != -1) {
        targetType = TargetType.Complex;
      }
      this.numbers = numbers;
      this.is6Bit = is6Bit;
    }

    public final boolean isRamp() {
      return targetType == TargetType.Ramp;
    }

    public final boolean isUnknow() {
      return targetType == TargetType.Unknow;
    }

    public final boolean isComplex() {
      return targetType == TargetType.Complex;
    }

    public final boolean isXTalk() {
      return targetType == TargetType.XTalk;
    }

    public final boolean isTest() {
      return targetType == TargetType.Test;
    }

//    public final boolean isUnknow() {
//      return targetType == TargetType.Unknow;
//    }

    public int getPatchCount() {
      return patchCount;
    }

    public TargetType getTargetType() {
      return targetType;
    }

    private TargetType targetType = TargetType.Unknow;
    private RGBBase.Channel[] channels;
    public RGBBase.Channel[] getChannels() {
      return channels;
    }

    private int patchCount;
    private String referenceFilename;
    private String description;
    private double step = -1;
    private Number[] numbers;
    private boolean is6Bit;
    public boolean is6Bit() {
      return is6Bit;
    }

    public final static int[] getIndexInComplex(Number complex, Number number) {
      Number[] numbers = complex.numbers;
      int size = numbers.length;
      for (int x = 0; x < size; x++) {
        Number num = numbers[x];
        if (num.equals(number)) {
          int start = 0;
          for (int y = 0; y < x; y++) {
            start += numbers[y].patchCount;
          }
          int end = start + number.patchCount;
          return new int[] {
              start, end};
        }
      }
      return null;
    }

    public final static Number getRampNumberFromComplex(Number complex) {
      if (!complex.isComplex()) {
        throw new IllegalArgumentException("!complex.isComplex()");
      }
      for (Number num : complex.numbers) {
        if (num.isRamp()) {
          return num;
        }
      }
      return null;
    }

    public final static Number getTestNumberFromComplex(Number complex) {
      if (!complex.isComplex()) {
        throw new IllegalArgumentException("!complex.isComplex()");
      }
      for (Number num : complex.numbers) {
        if (num.isTest()) {
          return num;
        }
      }
      return null;
    }

    public final static Number getXtalkNumberFromComplex(Number complex) {
      if (!complex.isComplex()) {
        throw new IllegalArgumentException("!complex.isComplex()");
      }
      for (Number num : complex.numbers) {
        if (num.isXTalk()) {
          return num;
        }
      }
      return null;
    }

    public Number[] getNumbers() {
      return numbers;
    }

    public String getReferenceFilename() {
      return referenceFilename;
    }

    public String getDescription() {
      return description;
    }

    public double getStep() {
      return step;
    }

    public final static Number getNumber(int number) {
      for (Number n : Number.values()) {
        if (n.patchCount == number) {
          return n;
        }
      }
      return null;
    }

    public final static Number getNumberFromReferenceFilenameAndDescription(
        String str) {
      for (Number number : values()) {
        if (number.referenceFilename != null &&
            number.referenceFilename.equals(str)) {
          return number;
        }
        if (number.description != null &&
            number.description.equals(str)) {
          return number;
        }
      }
      return null;
    }

    /**
     * 從ch來取得對應的ramp256
     * @param ch Channel
     * @param withWhite boolean
     * @return Number
     */
    public static Number getRamp256Number(RGBBase.Channel ch, boolean withWhite) {

      switch (ch) {
        case W:
          return LCDTargetBase.Number.Ramp256W;
        case R:
          if (withWhite) {
            return LCDTargetBase.Number.Ramp256R_W;
          }
          else {
            return LCDTargetBase.Number.Ramp256R;
          }
        case G:
          if (withWhite) {
            return LCDTargetBase.Number.Ramp256G_W;
          }
          else {
            return LCDTargetBase.Number.Ramp256G;
          }
        case B:
          if (withWhite) {
            return LCDTargetBase.Number.Ramp256B_W;
          }
          else {
            return LCDTargetBase.Number.Ramp256B;
          }
        default:
          return null;
      }
    }
  }

  /**
   *
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
  public final static class Instance {
    public final static LCDTarget getInterpolated(LCDTarget target,
                                                  LCDTarget.Number number) {
      if (!target.getNumber().isRamp()) {
        throw new IllegalArgumentException("!target.getNumber().isRamp()");
      }
      Interpolation.Algo[] algos = LCDTargetInterpolator.Find.
          optimumInterpolationType(target,
                                   LCDTargetInterpolator.OptimumType.Max);
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(target,
          algos);
      TargetInterpolatorAdapter adapter = new TargetInterpolatorAdapter(interp,
          number);
      LCDTarget lcdTarget = LCDTarget.Instance.get(adapter);
      return lcdTarget;
    }

    private final static LCDTarget getFromRGBList(List<RGB> rgbList) {
      List<Patch> patchList = Patch.Produce.RGBPatches(rgbList);
      LCDTarget lcdTarget = new LCDTarget(patchList, null, true, false);
      lcdTarget.setDescription("from RGBList");
      return lcdTarget;
    }

    /**
     * 取出部份的target
     * @param device Device
     * @param source Source
     * @param room Room
     * @param illuminant TargetIlluminant
     * @param number Number
     * @return LCDTarget
     */
    protected final static LCDTarget getByPart(String device,
                                               Source source,
                                               Room room,
                                               TargetIlluminant illuminant,
                                               Number number) {

      switch (number) {
        case Patch62: {
          LCDTarget target = get(device, source, room, illuminant,
                                 Number.Patch125, null, null, null);
          return target.targetFilter.getPatch62From125();
        }
        case Patch218: {
          LCDTarget target = get(device, source, room, illuminant,
                                 Number.Test729, null, null, null);
          return target.targetFilter.getPatch218From729();
        }
        case Ramp509: {
          LCDTarget target = get(device, source, room, illuminant,
                                 Number.Ramp1021, null, null, null);
          return target.targetFilter.getRamp509From1021();
        }
        default:
          return null;
      }
    }

    public final static LCDTarget getByPart(LCDTarget target, int start,
                                            int end, Number newNumber) {
      List<Patch> patchList = target.getPatchList().subList(start, end);
      LCDTarget result = new LCDTarget(patchList, newNumber,
                                       target.isRGBPatchOnly,
                                       target.inverseModeMeasure);
      result.setDescription("by part");
      return result;

    }

    /**
     * 濾出某個範圍RGB的色塊,並且產生為LCDTarget
     * @param target LCDTarget
     * @param rgbStartIn255 int
     * @param rgbEndIn255 int
     * @param withWhite boolean
     * @return LCDTarget
     */
    public final static LCDTarget getByPart(LCDTarget target,
                                            int rgbStartIn255,
                                            int rgbEndIn255,
                                            boolean withWhite) {
      double start = -1;
      double end = -1;

      if (target.doRGBNormalize) {
        start = rgbStartIn255 / 255.;
        end = rgbEndIn255 / 255.;
      }
      else {
        start = rgbStartIn255;
        end = rgbEndIn255;
      }

      List<Patch> patchList = target.getPatchList();
      Set<Patch> tmp = new TreeSet<Patch> ();
      Patch.Filter.RGBInRange(patchList, tmp, RGBBase.Channel.R, start, end);
      Patch.Filter.RGBInRange(patchList, tmp, RGBBase.Channel.G, start, end);
      Patch.Filter.RGBInRange(patchList, tmp, RGBBase.Channel.B, start, end);
      Patch.Filter.RGBInRange(patchList, tmp, RGBBase.Channel.W, start, end);
      //如果包含到0,記得把0加回去,因為filterRGBInRange沒辦法考慮到0
      if (rgbStartIn255 == 0) {
        tmp.add(target.getBlackPatch());
      }
      if (withWhite) {
        tmp.add(target.getWhitePatch());
      }
      LCDTarget result = new LCDTarget(new ArrayList<Patch> (tmp), null,
                                       target.isRGBPatchOnly,
                                       target.inverseModeMeasure);
      result.setDescription("by part");
      return result;
    }

    public final static LCDTarget[] getInstancesFromVastView(String dirName,
        Number number) {
      File dir = new File(dirName);
      String[] filenames = dir.list();
      int size = filenames.length;
      LCDTarget[] lcdTargets = new LCDTarget[size];
      for (int x = 0; x < size; x++) {
        String filename = filenames[x];
        lcdTargets[x] = LCDTarget.Instance.getFromVastView(dirName + "/" +
            filename,
            number);
      }
      return lcdTargets;
    }

    /**
     *
     * @param cxfFilename String
     * @return LCDTarget
     */
    public final static LCDTarget getFromRGBCxF(String cxfFilename) {
      CXFOperator cxf = new CXFOperator(cxfFilename);
      List<RGB> rgbList = cxf.getRGBList();
      LCDTarget target = getFromRGBList(rgbList);
      List<String> nameList = cxf.getSampleNameList(0);
      target.setPatchName(nameList);
      setTargetMetadata(target, cxfFilename);
      return target;
    }

    /**
     *
     * @param cxfFilename String
     * @return LCDTarget
     */
    public final static LCDTarget getFromSpectroPhotometerCxF(String
        cxfFilename) {
      CXFOperator cxf = new CXFOperator(cxfFilename);
      List<Spectra> spectra = cxf.getSpectraList();
      List<RGB> rgbList = cxf.getRGBList();
      LCDTarget lcdTarget = new LCDTarget(spectra, rgbList, null, false);
      //=========================================================================
      setTargetMetadata(lcdTarget, cxfFilename);
      return lcdTarget;
    }

    public final static LCDTarget getFromColorimeterCxF(String
        cxfFilename) {
      CXFOperator cxf = new CXFOperator(cxfFilename);
      List<CIEXYZ> XYZList = cxf.getCIEXYZList();
      List<RGB> rgbList = cxf.getRGBList();
      List<Patch> patchList = Patch.Produce.XYZRGBPatches(XYZList, rgbList);
      LCDTarget lcdTarget = new LCDTarget(patchList, null, false);
      //=========================================================================

      setTargetMetadata(lcdTarget, cxfFilename);
      return lcdTarget;
    }

    public final static LCDTarget[] getInstancesFromLogoFiles(String dirName) {
      File dir = new File(dirName);
      String[] filenames = dir.list();

      int size = filenames.length;
      LCDTarget[] lcdTargets = new LCDTarget[size];
      for (int x = 0; x < size; x++) {
        String filename = filenames[x];
        lcdTargets[x] = LCDTarget.Instance.getFromLogo(dirName + "/" +
            filename);
      }
      return lcdTargets;
    }

    public final static LCDTarget getFromLogo(String filename) {
      return getFromLogo(filename, null);
    }

    public final static LCDTarget getFromLogo(String filename, Number number) {
      LogoFileAdapter adapter = new LogoFileAdapter(filename);
      LCDTarget lcdTarget = (number == null) ? get(adapter) :
          get(adapter, number);
      lcdTarget.setCalibrationMatrix(adapter.getCalibrationMatrix());
      String instrumentation = adapter.getInstrumentation();
      if (instrumentation != null) {
        try {
          lcdTarget.measureMetadata.instrument = Meter.Instr.valueOf(
              instrumentation);
        }
        catch (IllegalArgumentException ex) {
          //======================================================================
          // 為了向前的相容性
          //======================================================================
          if ("dummy meter".equals(instrumentation)) {
            lcdTarget.measureMetadata.instrument = Meter.Instr.Dummy;
          }
          else if ("Argyll meter".equals(instrumentation)) {
            lcdTarget.measureMetadata.instrument = Meter.Instr.Argyll;
          }
          //======================================================================
          //找不到就算了
        }
      }
      return lcdTarget;
    }

    public final static LCDTarget getFromVastViewXLS(String filename) {
      VVMeasureXLSAdapter adapter = new VVMeasureXLSAdapter(filename);
      return get(adapter);
    }

    public final static LCDTarget getTest729FromAUOXLS(String filename) {
      LCDTarget target = LCDTarget.Instance.getFromAUOXLS(filename);
      List<Patch> patchList729 = new ArrayList<Patch> (729);

      for (int r = 0; r <= 256; r += 32) {
        for (int g = 0; g <= 256; g += 32) {
          for (int b = 0; b <= 256; b += 32) {
            r = (r == 256) ? 255 : r;
            g = (g == 256) ? 255 : g;
            b = (b == 256) ? 255 : b;
            Patch p = target.getPatch(r, g, b);
            patchList729.add(p);
          }
        }

      }
      LCDTarget result = LCDTarget.Instance.get(patchList729,
                                                LCDTarget.Number.Test729, false);
      result.setDescription(filename);
      return result;
    }

    public final static LCDTarget getFromAUOXLS(String filename) {
      AUOMeasureXLSAdapter adapter = null;
      try {
        adapter = new AUOMeasureXLSAdapter(filename);
      }
      catch (jxl.read.biff.BiffException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
      return get(adapter);
    }

    public final static LCDTarget getFromAUORampXLS(String filename) {
      AUORampXLSAdapter adapter = new AUORampXLSAdapter(filename);
      return get(adapter);
    }

    public final static LCDTarget getFromAUORampXLS(String filename,
        LCDTargetBase.Number number) {
      AUORampXLSAdapter adapter = new AUORampXLSAdapter(filename, number);
      return get(adapter);
    }

    /**
     * 從number取出reference下對應的rgb list
     * @param number Number
     * @return List
     */
    public final static List<RGB> getRGBList(Number number) {
      TargetAdapter adapter = getNumberTargetAdapter(number);
      if (adapter != null) {
        return adapter.getRGBList();
      }
      else {
        return null;
      }
    }

    public final static LCDTarget get(List<Patch> patchList, Number number,
        boolean inverseModeMeasure, String description) {
      LCDTarget lcdTarget = new LCDTarget(patchList, number,
                                          inverseModeMeasure);
      setTargetMetadata(lcdTarget,
                        description == null ? number.name() : description);
      return lcdTarget;

    }

    public final static LCDTarget get(List<Patch> patchList, Number number,
        boolean inverseModeMeasure) {
      return get(patchList, number, inverseModeMeasure, null);
    }

    public final static LCDTarget get(Number number) {
      if (number.referenceFilename == null) {
        return null;
      }
      TargetAdapter adapter = getNumberTargetAdapter(number);
      LCDTarget target = get(adapter);
      return target;
    }

    private final static TargetAdapter getNumberTargetAdapter(Number number) {
      //以計算的方式產生
      LCDTargetNumberAdapter adapter1 = new LCDTargetNumberAdapter(number);
      if (adapter1.getRGBList() != null) {
        return adapter1;
      }
      else {
        //從檔案讀
        String filename = number.referenceFilename + ".logo";
        InputStream is = Monitor.class.getResourceAsStream(number.
            referenceFilename + ".logo");
        if (is != null) {
          LogoFileAdapter adapter2 = new LogoFileAdapter(new InputStreamReader(
              is), filename);
          return adapter2;
        }
        else {
          return null;
        }
      }
    }

    public final static LCDTarget getFromUSB4000(String dirFilename,
                                                 Number number) {
      //==========================================================================
      // 光譜值
      //==========================================================================
      USB4000Adapter adapter = new USB4000Adapter(dirFilename);
      List<Spectra> spectra = adapter.getSpectraList();
      //==========================================================================

      //==========================================================================
      // RGB值,from 參考導具
      //==========================================================================
      List<RGB> rgbList = getRGBList(number);
      //==========================================================================

      LCDTarget lcdTarget = new LCDTarget(spectra, rgbList, number, false);
      //=========================================================================

      setTargetMetadata(lcdTarget, dirFilename);
      return lcdTarget;
    }

    /**
     * 從CA-210取得導具資料,導具的格式以系統內存的為主
     * @param filename String
     * @param number Number
     * @return LCDTarget
     */
    public final static LCDTarget getFromVastView(String filename,
                                                  Number number) {
      List<CIEXYZ> XYZList = null;
      List<RGB> RGBList = null;
      if (number == Number.Ramp7147) {
        return getFromVastView(filename, Number.Ramp7168).targetFilter.
            getRamp7147From7168();
      }
      else if (number == Number.Ramp1024 || number == Number.Ramp1792 ||
               number == Number.Ramp4096 || number == Number.Ramp7168) {
        VastViewAdapter adapter = new VastViewAdapter(filename, number);
        XYZList = adapter.getXYZList();
        RGBList = adapter.getRGBList();
      }
      else if (number == null) {
        VastViewAdapter adapter = new VastViewAdapter(filename);
        XYZList = adapter.getXYZList();
        RGBList = adapter.getRGBList();
        number = adapter.estimateLCDTargetNumber();
      }
      else {
        return null;
      }

      List<Patch>
          patchList = Patch.Produce.XYZRGBPatches(XYZList,
                                                  RGBList);
      LCDTarget lcdTarget = new LCDTarget(patchList, number, false);

      setTargetMetadata(lcdTarget, filename);
      return lcdTarget;
    }

    public final static LCDTarget getFromVastView(String filename) {
      return getFromVastView(filename, null);
    }

    /**
     * 由TargetAdapter來產生導具資料
     * @param adapter TargetAdapter
     * @return LCDTarget
     */
    public final static LCDTarget get(TargetAdapter adapter) {
      return get(adapter, adapter.estimateLCDTargetNumber());
    }

    /**
     * 由TargetAdapter來產生導具資料
     * @param adapter TargetAdapter
     * @param number Number
     * @return LCDTarget
     */
    public final static LCDTarget get(TargetAdapter adapter, Number number) {
      TargetAdapter.Style style = adapter.getStyle();

      LCDTarget target = null;

      switch (style) {
        case RGB:
          target = getFromRGBList(adapter.getRGBList());
          target.setNumber(number);
          break;
        case RGBXYZ:
          target = new LCDTarget(Patch.Produce.XYZRGBPatches(adapter.
              getXYZList(), adapter.getRGBList()), number,
                                 adapter.isInverseModeMeasure());
          break;
        case RGBXYZSpectra:
        case RGBSpectra:
          target = new LCDTarget(adapter.getSpectraList(), adapter.getRGBList(),
                                 number, adapter.isInverseModeMeasure());
          break;
        default:
          throw new IllegalArgumentException(
              "TargetAdapter.Style is unsupported.");
      }

      List<String> patchNameList = adapter.getPatchNameList();
      if (patchNameList != null) {
        target.setPatchName(adapter.getPatchNameList());
      }

      setTargetMetadata(target, adapter);
      return target;
    }

    private final static void setTargetMetadata(LCDTarget lcdTarget,
                                                String filename) {
      String onlyFilename = Filename.getOnlyFilename(filename);
      if (filename != null) {
        filename = filename.replaceAll(CMSDir.Measure.Monitor, "");
        filename = filename.substring(filename.indexOf('/') + 1);
        filename = filename.substring(filename.indexOf('/') + 1);
        filename = filename.replace('/', ' ');
        filename = filename.trim();
      }

      lcdTarget.description = "[" + filename + "]";
      lcdTarget.filename = onlyFilename;
    }

    private final static void setTargetMetadata(LCDTarget lcdTarget,
                                                TargetAdapter adapter) {
      String filename = adapter.getAbsolutePath();
      setTargetMetadata(lcdTarget, filename);
    }

    /**
     * 將complex下原本的多個LCDTarget合併為單一個
     * @param source Source
     * @param number Number
     * @param fileType FileType
     * @param filenames String[]
     * @return LCDTarget
     */
    private final static LCDTarget produceMultiLCDTarget(Source source,
        Number number, FileType fileType, String[] filenames) {
      if (!number.isComplex()) {
        throw new IllegalArgumentException("!number.isComplex()");
      }
      Number[] numbers = number.getNumbers();
      LCDTarget lcdTarget = produceLCDTarget(source, numbers[0], fileType,
                                             filenames[0]);
      List<Patch> patchList = lcdTarget.getPatchList();

      int size = filenames.length;
      for (int x = 1; x < size; x++) {
        LCDTarget target = produceLCDTarget(source, numbers[x], fileType,
                                            filenames[x]);
        lcdTarget.setDescription(lcdTarget.getDescription() + "+" +
                                 target.getDescription());
        patchList.addAll(target.getPatchList());
      }

      lcdTarget.setNumber(number);
      return lcdTarget;
    }

    private final static LCDTarget produceLCDTarget(Source source,
        Number number, FileType fileType, String filename) {
      if (fileType == null) {

        switch (source) {
          case i1display2:
          case i1pro:
            switch (source.type) {
              case Spectrometer:
                return getFromSpectroPhotometerCxF(filename);
              case Colorimeter:
                return getFromColorimeterCxF(filename);
            }
            break;
          case CA210:
          case K10: {
            return getFromVastView(filename, number);
          }
          case USB4000:
            return getFromUSB4000(filename, number);
          case Logo:
            return getFromLogo(filename, number);
        }
      }
      else {
        switch (fileType) {
          case CGATS:
          case Logo:
            return getFromLogo(filename, number);
          case CxF:
            switch (source.type) {
              case Spectrometer:
                return getFromSpectroPhotometerCxF(filename);
              case Colorimeter:
                return getFromColorimeterCxF(filename);
            }
            break;
          case VastView:
            return getFromVastView(filename, number);
          case USB4000:
            return getFromUSB4000(filename, number);
          case VastViewXLS:
            return getFromVastViewXLS(filename);
          case AUOXLS:
            return getFromAUOXLS(filename);
          case AUORampXLS:
            return getFromAUORampXLS(filename);
        }
      }
      return null;
    }

    public final static LCDTarget getFromCA210Logo(String device, Number number,
        String dirTag) {
      return get(device, Source.CA210, number, FileType.Logo, dirTag, "");
    }

    public final static LCDTarget getFromCA210Logo(String device, Number number,
        String dirTag, String fileTag) {
      return get(device, Source.CA210, number, FileType.Logo, dirTag, fileTag);
    }

    public final static LCDTarget get(String device, Source source,
                                      Number number, FileType fileType,
                                      String dirTag, String fileTag) {
      return get(device, source, Room.Dark, TargetIlluminant.Native, number,
                 fileType, dirTag, fileTag);
    }

    public final static LCDTarget get(String device, Source source,
                                      Room room,
                                      TargetIlluminant illuminant,
                                      Number number, FileType fileType,
                                      String dirTag, String fileTag) {
      //=========================================================================
      // 過濾出從檔案產生的色塊檔(取出部分內容)
      //=========================================================================
      LCDTarget part = getByPart(device, source, room, illuminant, number);
      if (part != null) {
        return part;
      }
      //=========================================================================

      //=========================================================================
      // 產生LCDTarget所需各項參數
      //=========================================================================
      String filename = Filename.produceFilename(device, source, room,
                                                 illuminant, number, fileType,
                                                 dirTag, fileTag);
      LCDTarget lcdTarget = null;
      boolean multiTarget = !new File(filename).exists() && number.isComplex();
      if (multiTarget) {
        //如果complex檔案不存在, 則以多檔載入合併處理的方式
        String[] filenames = Filename.produceLogoFilenames(device, source, room,
            illuminant, number, dirTag, fileTag);
        lcdTarget = produceMultiLCDTarget(source, number, fileType, filenames);
      }
      else {
        lcdTarget = produceLCDTarget(source, number, fileType, filename);
      }

      lcdTarget.setDevice(device);
      lcdTarget.setDescription( (multiTarget ? "[Multi]" : "") +
                               (lcdTarget.description != null ?
                                lcdTarget.description :
                                (" [" + source + " " + room + " " +
                                 illuminant + " " + number + "]")));
      lcdTarget.tag = ( (dirTag != null && dirTag.length() != 0) ?
                       "//" + dirTag + "//" : "") + fileTag;
      lcdTarget.setMeasureMetadata(source, illuminant);
      lcdTarget.initLCDMetadata();

      return lcdTarget;
    }

//    private

    public final static LCDTarget get(String device, Source source,
                                      Room room,
                                      TargetIlluminant illuminant,
                                      Number number, String dirTag,
                                      String fileTag) {
      return get(device, source, room, illuminant, number, null, dirTag,
                 fileTag);

    }

  }

  void setMeasureMetadata(Source source,
                          TargetIlluminant targetIlluminant) {
    measureMetadata.source = source;
    measureMetadata.targetIlluminant = targetIlluminant;
  }

  /**
   * 初始化LCD的metadata
   */
  void initLCDMetadata() {
    String filename = CMSDir.Measure.Monitor + "/" + device + "/meta.xml";
    File meta = new File(filename);
    if (meta.exists()) {
      lcdMetadata = (LCDMetadata) Persistence.readObjectAsXML(filename);
    }
  }

  protected LCDMetadata lcdMetadata;
  protected MeasureMetadata measureMetadata = new MeasureMetadata();

  public LCDMetadata getLCDMetadata() {
    return lcdMetadata;
  }

  public static void main(String[] args) {
//    List<RGB> rgbList = LCDTarget.Instance.get(LCDTarget.Number.Patch125).
//        filter.rgbList();
//    List<RGB> rgbList = LCDTarget.Instance.get(LCDTarget.Number.WHQL).
//        filter.rgbList();
    LCDTarget target = DC2LCDTargetAdapter.Instance.getCameraTarget(DCTarget.
        Chart.CC24,
        RGB.ColorSpace.sRGB);
//    target.lu
//    Patch wp = target.getWhitePatch();
    Patch wp = target.getBrightestPatch();
//    target.luminance = wp.getXYZ();
//    target.calculateNormalizedXYZ();
    for (Patch p : target.getPatchList()) {
//      CIEXYZ XYZ = p.getNormalizedXYZ();
//      RGB rgb = new RGB(RGB.ColorSpace.sRGB, XYZ);
//      rgb.changeMaxValue(RGB.MaxValue.Int8Bit);
//      System.out.println(p);
//      System.out.println(p.getRGB() + " " + p.getOriginalRGB());
      RGB rgb = p.getRGB();
      HSV hsv = new HSV(rgb);
      if (hsv.S > 3) {
        System.out.println(hsv);
      }
//      CIExyY xyY = new CIExyY(XYZ);
//      RGB rgb = p.getRGB();
//      double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Int8Bit);
//      System.out.println(p);
//      System.out.println(p.getName() + " " + Arrays.toString(rgbValues));
//      System.out.println(p + " " + xyY);
    }

//
//    for (int r = 0; r <= 256; r += 32) {
//      for (int g = 0; g <= 256; g += 32) {
//        for (int b = 0; b <= 256; b += 32) {
//          RGB rgb = new RGB(r - 1, g - 1, b - 1);
//          rgb.rationalize();
//          System.out.println(index++ +" " + rgb);
//        }
//      }
//    }

//    for (int bstart = 0; bstart <= 32; bstart += 32) {
//      for (int gstart = 0; gstart <= 32; gstart += 32) {
//        for (int rstart = 0; rstart <= 32; rstart += 32) {
//          for (int b = bstart; b <= 256; b += 64) {
//            for (int g = gstart; g <= 256; g += 64) {
//              for (int r = rstart; r <= 256; r += 64) {
//                System.out.println(index++ +" " + r + " " + g + " " + b);
//              }
//            }
//          }
//
//        }
//      }
//    }

//    for (int[] start : new int[][] { {0, 0, 0}, {0, 0, 32}, {0, 32, 0}, {32, 0,
//         0}
//    }) {
//
//      for (int r = start[0]; r <= 256; r += 64) {
//        for (int g = start[1]; g <= 256; g += 64) {
//          for (int b = start[2]; b <= 256; b += 64) {
//            System.out.println(index++ +" " + b + " " + g + " " + r);
//          }
//        }
//      }
//    }

  }

  public final static class SurfaceTarget {
    public final static List<RGB> getSurface(int step) {
      Set<RGB> rgbSet = new TreeSet<RGB> ();
//      List<RGB> rgbList = new LinkedList<RGB> ();
//        List<RGB> rgbList = new t
      rgbSet.addAll(getPlane(RGBBase.Channel.G, RGBBase.Channel.B, 0, step));
      rgbSet.addAll(getPlane(RGBBase.Channel.G, RGBBase.Channel.B, 255, step));
      rgbSet.addAll(getPlane(RGBBase.Channel.R, RGBBase.Channel.G, 0, step));
      rgbSet.addAll(getPlane(RGBBase.Channel.R, RGBBase.Channel.G, 255, step));
      rgbSet.addAll(getPlane(RGBBase.Channel.B, RGBBase.Channel.R, 0, step));
      rgbSet.addAll(getPlane(RGBBase.Channel.B, RGBBase.Channel.R, 255, step));
//      for (RGB rgb : rgbSet) {
//        System.out.println(rgb);
//      }
      List<RGB> rgbList = new LinkedList<RGB> (rgbSet);
      return rgbList;
    }

    private final static List<RGB> getPlane(RGBBase.Channel first,
                                            RGBBase.Channel second,
                                            int thirdValue, int step) {
      RGBBase.Channel third = RGBBase.Channel.getBesidePrimaryChannel(first,
          second);
      List<RGB> rgbList = new LinkedList<RGB> ();

      for (int f = 0; f < 256; f += step) {
        for (int s = 0; s < 256; s += step) {
          RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGB.MaxValue.Int8Bit);
          rgb.setValue(first, f, RGB.MaxValue.Int8Bit);
          rgb.setValue(second, s, RGB.MaxValue.Int8Bit);
          rgb.setValue(third, thirdValue, RGB.MaxValue.Int8Bit);
          rgbList.add(rgb);
        }
      }
      return rgbList;
    }

  }

}
