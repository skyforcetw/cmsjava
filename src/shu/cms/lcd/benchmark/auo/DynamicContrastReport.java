package shu.cms.lcd.benchmark.auo;

import java.util.List;

import java.awt.*;

//import jxl.read.biff.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.lut.*;
//import shu.plot.*;

/**
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
 */
public class DynamicContrastReport
    extends Benchmark {
//  /**
//   *
//   * @param dccOffFilename String
//   * @param dccOnFilename String
//   * @throws BiffException
//   * @throws IOException
//   * @deprecated
//   */
//  public DynamicContrastReport(String dccOffFilename, String dccOnFilename) throws
//      jxl.read.biff.BiffException, IOException {
//    super(dccOnFilename);
////    this.dccOffFilename = dccOffFilename;
////    this.dccOnFilename = dccOnFilename;
//    title = new File(dccOnFilename).getName();
//  }

  public DynamicContrastReport(LCDTarget dccOffTarget, LCDTarget dccOnTarget) {
    super(dccOffTarget);
    this.dccOnTarget = dccOnTarget;
    this.dccOffTarget = dccOffTarget;
    title = dccOnTarget.getFilename();
  }

  private LCDTarget dccOffTarget;
  private LCDTarget dccOnTarget;
  private String title;
//  private String dccOffFilename;
//  private String dccOnFilename;
  public static void main(String[] args) throws Exception {
    String[] contents = new String[] {
        "High", "Medium", "Low"};
    /*String[] functions = new String[] {
        "High", "Medium", "Low"};*/

    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(
        "D:\\My Documents\\工作\\華山計畫\\Sharp LC-46LX1\\Modes\\PC\\ramp.xls");

    for (String content : contents) {
      //for (String func : functions) {
      LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
          "D:\\My Documents\\工作\\華山計畫\\Sharp LC-46LX1\\Modes\\PC\\動態對比\\" +
          content + ".xls");

      DynamicContrastReport report = new DynamicContrastReport(
          ramp, target);
      report.report();
      report.getPlot();

      //}
    }

//    int index = Arrays.binarySearch(new int[] {1, 2, 3, 4, 5, 5, 5, 5, 5, 6, 6,
//                                    6, 77, 8}, 5);
//    System.out.println(index);
  }

  /**
   *
   * @return String
   */
  public String report() {
    StringBuilder report = new StringBuilder();

    Interpolation1DLUT offLut = getGrayInterpolationLUT(dccOffTarget);

//    LCDTarget on = LCDTarget.Instance.getFromAUORampXLS(dccOnFilename);
    LCDTarget on = dccOnTarget.targetFilter.getRamp256W();
    int startIndex = judgeStartIndexByDrakestJNDI(on, RGB.Channel.W);
    int endIndex = judgeEndIndexByBrightestJNDI(on, RGB.Channel.W);
//    int startIndex = 1;
//    int endIndex = 254;
    report.append(title + " startIndex: " +
                  startIndex + " endIndex: " + endIndex);
    List<Patch> patchList = on.getPatchList();
    int size = endIndex - startIndex + 1;
    input = new double[size];
    output = new double[size];
    int index = 0;

    for (int x = startIndex; x <= endIndex; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = p.getXYZ();
      double mapcode = offLut.getKey(XYZ.Y);
//      System.out.println(rgb.getValue(RGB.Channel.W) + " " + mapcode);
      input[index] = rgb.getValue(RGB.Channel.W);
      output[index] = mapcode;
      index++;
    }
    return report.toString();
  }

  public Plot2D getPlot() {
    Plot2D plot = Plot2D.getInstance(title);
    int size = input.length;
    plot.addLinePlot("", Color.blue, input[0], input[size - 1], output);
    plot.addLinePlot("", Color.black, 0, 0, 255, 255);
    plot.setAxeLabel(0, "input");
    plot.setAxeLabel(1, "output");
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 0, 255);
    plot.setVisible();
    return plot;
  }

  private double[] input;
  private double[] output;

  private final static int judgeStartIndexByDrakestJNDI(LCDTarget target,
      RGB.Channel ch) {
    GSDF dicom = GSDF.getDICOMInstance();
    double blackjndi = dicom.getJNDIndex(target.getDarkestPatch().getXYZ().Y);
    List<Patch> patchList = target.filter.oneValueChannel(ch);
    int size = patchList.size();

    for (int x = 1; x < size - 1; x++) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      double jndi = dicom.getJNDIndex(XYZ.Y);
      if (Math.abs(jndi - blackjndi) > 1) {
        return x - 1;
//        return (int) p.getRGB().getValue(ch) - 1;
      }
    }
    return -1;
  }

  private final static int judgeEndIndexByBrightestJNDI(LCDTarget target,
      RGB.Channel ch) {
    GSDF dicom = GSDF.getDICOMInstance();
    CIEXYZ whiteXYZ = target.getBrightestPatch().getXYZ();
    double whitejndi = dicom.getJNDIndex(whiteXYZ.Y);

    List<Patch> patchList = target.filter.oneValueChannel(ch);
    int size = patchList.size();

    for (int x = size - 2; x > 0; x--) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      double jndi = dicom.getJNDIndex(XYZ.Y);
      if (Math.abs(jndi - whitejndi) > 1) {
//        return (int) p.getRGB().getValue(ch) +
        return x + 1;
      }
    }

    return -1;
  }

  /**
   *
   * @param rampFilename String
   * @return Interpolation1DLUT
   * @deprecated
   */
  private Interpolation1DLUT getGrayInterpolationLUT(String rampFilename) {
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(rampFilename);
    LCDTarget off = target.targetFilter.getRamp256W();
    int startIndex = judgeStartIndexByDrakestJNDI(off, RGB.Channel.W);
    int endIndex = judgeEndIndexByBrightestJNDI(off, RGB.Channel.W);

    List<Patch> validRamp = off.filter.getRange(startIndex, endIndex);
    return getInterpolationLUT(validRamp, RGB.Channel.W);
  }

  private Interpolation1DLUT getGrayInterpolationLUT(LCDTarget rampTarget) {
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(rampFilename);
    LCDTarget off = rampTarget.targetFilter.getRamp256W();
    int startIndex = judgeStartIndexByDrakestJNDI(off, RGB.Channel.W);
    int endIndex = judgeEndIndexByBrightestJNDI(off, RGB.Channel.W);

    List<Patch> validRamp = off.filter.getRange(startIndex, endIndex);
    return getInterpolationLUT(validRamp, RGB.Channel.W);
  }

  private final static Interpolation1DLUT getInterpolationLUT(List<Patch>
      rampPatchList, RGB.Channel ch) {
    int size = rampPatchList.size();
    double[] input = new double[size];
    double[] output = new double[size];
    for (int x = 0; x < size; x++) {
      Patch p = rampPatchList.get(x);
      input[x] = p.getRGB().getValue(ch);
      output[x] = p.getXYZ().Y;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(input, output);
    return lut;
  }
}
