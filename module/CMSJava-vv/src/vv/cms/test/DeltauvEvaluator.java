package vv.cms.test;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.math.array.DoubleArray;
import shu.io.files.ExcelFile;

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
public class DeltauvEvaluator {

  public static void main(String[] args) throws Exception {
//    apong(null);
//    CIEXYZ XYZ = new CIEXYZ(208.88, 220.56, 227.56);
    CIEXYZ XYZ = new CIEXYZ(0.9505, 1.0000, 1.0890);
    double[] uvp = new CIExyY(XYZ).getuvPrimeValues();
    System.out.println(DoubleArray.toString(uvp));
  }

  public static void apong(String[] args) throws Exception {
    ExcelFile xls = new ExcelFile("VTI_sRGB raw data.xls");
    int rows = xls.getRows();
    CIEXYZ[] measureXYZs = new CIEXYZ[rows];
    CIEXYZ[] normalMeasureXYZs = new CIEXYZ[rows];

    for (int x = 0; x < rows; x++) {
      double X = xls.getCell(1, x);
      double Y = xls.getCell(2, x);
      double Z = xls.getCell(3, x);
      measureXYZs[x] = new CIEXYZ(X, Y, Z);
    }
    CIEXYZ measureWhite = measureXYZs[rows - 1];
    CIEXYZ measureBlack = measureXYZs[0];
    CIEXYZ pureMeasureWhite = CIEXYZ.minus(measureWhite, measureBlack);
    for (int x = 0; x < rows; x++) {
      normalMeasureXYZs[x] = CIEXYZ.minus(measureXYZs[x], measureBlack);
      normalMeasureXYZs[x].rationalize();
    }
    for (int x = 0; x < rows; x++) {
      normalMeasureXYZs[x].normalize(pureMeasureWhite);
    }

    RGBBase.Channel[] channels = new RGBBase.Channel[] {
        RGBBase.Channel.R, RGBBase.Channel.G, RGBBase.Channel.B,
        RGBBase.Channel.C,
        RGBBase.Channel.M, RGBBase.Channel.Y, RGBBase.Channel.W};

    List<Patch> measurePatchList = new ArrayList<Patch> (rows);
    List<Patch> targetPatchList = new ArrayList<Patch> (rows);
    int index = 0;

    for (RGBBase.Channel ch : channels) {
      for (int x = 0; x < 256; x++) {
        RGB rgb = new RGB(RGB.ColorSpace.sRGB);
        rgb.setValue(ch, x, RGB.MaxValue.Int8Bit);
        CIEXYZ XYZ = rgb.toXYZ();
        Patch targetPatch = new Patch("", XYZ, null, null, rgb);
        targetPatchList.add(targetPatch);

        CIEXYZ measureXYZ = measureXYZs[index];
        CIEXYZ normalMeasureXYZ = normalMeasureXYZs[index];
        index++;
        Patch measurePatch = new Patch("", measureXYZ, normalMeasureXYZ, null,
                                       rgb);
        measurePatchList.add(measurePatch);
      }
    }
    CIEXYZ targetWhite = targetPatchList.get(targetPatchList.size() - 1).getXYZ();
//    CIEXYZ normalTargetWhite = targetPatchList.get(targetPatchList.size() - 1).
//        getNormalizedXYZ();

    for (int x = 0; x < rows; x++) {
      Patch targetPatch = targetPatchList.get(x);
      RGB rgb = targetPatch.getRGB();
      CIEXYZ targetXYZ = targetPatch.getXYZ();

      Patch measurePatch = measurePatchList.get(x);
      CIEXYZ measureXYZ = measurePatch.getXYZ();
      CIEXYZ normalMeasureXYZ = measurePatch.getNormalizedXYZ();

      CIExyY measurexyY = new CIExyY(normalMeasureXYZ);
      CIExyY targetxyY = new CIExyY(targetXYZ);
      double[] deltauvprime = measurexyY.getDeltauvPrime(targetxyY);
      double deltauvp = Math.sqrt(Maths.sqr(deltauvprime[0]) +
                                  Maths.sqr(deltauvprime[1]));

      CIELuv measureLuv = new CIELuv(normalMeasureXYZ, targetWhite);
      CIELuv targetLuv = new CIELuv(targetXYZ, targetWhite);
      double[] deltauvstart = measureLuv.getDeltauvStar(targetLuv);
      double deltauvs = Math.sqrt(Maths.sqr(deltauvstart[0]) +
                                  Maths.sqr(deltauvstart[1]));
      rgb.changeMaxValue(RGB.MaxValue.Int8Bit);

      DeltaE de = new DeltaE(normalMeasureXYZ, targetXYZ, targetWhite, false);
      double de00 = de.getCIE2000DeltaE();
      double dab00 = de.getCIE2000Deltaab();

      System.out.print(rgb);
      System.out.print(" " + measureXYZ.toString(df3) + " " +
                       normalMeasureXYZ);
      System.out.print(" " + targetXYZ);
      System.out.print(" " + measureLuv.toString(df3) + " " +
                       targetLuv.toString(df3));
      System.out.print(" " + df3.format(deltauvstart[0]) + " " +
                       df3.format(deltauvstart[1]) + " " + df3.format(deltauvs));

      System.out.println(" " + df3.format(deltauvp) + " " + df3.format(de00) +
                         " " + df3.format(dab00));

    }
//    System.out.println(rows);
  }

  private final static DecimalFormat df3 = new DecimalFormat("####.###");
}
