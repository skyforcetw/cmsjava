package shu.cms.profile;

import java.io.*;

import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.util.*;

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
public final class ProfileUtils {
  public static Profile produceDCProfile(Profile.PCSType pcsType,
                                         CIEXYZ mediaWhitePoint,
                                         double[][]
                                         chromaticAdaptationMatrix,
                                         String profileDescription,
                                         ColorSpaceConnectedLUT AToB0,
                                         ColorSpaceConnectedLUT AToB1,
                                         ColorSpaceConnectedLUT AToB2) {
    Profile profile = new Profile();
    profile.profileClass = Profile.ProfileClass.CLASS_INPUT;
    profile.technology = Profile.Technology.DigitalCamera;

    profile.dataColourSpace = Profile.DataColourSpace.rgb;
    profile.pcsType = pcsType;
    profile.AToB0 = AToB0;
    profile.AToB1 = AToB1;
    profile.AToB2 = AToB2;

    profile.mediaWhitePoint = mediaWhitePoint;
    profile.chromaticAdaptation = chromaticAdaptationMatrix;
    profile.profileDescription = profileDescription;

    return profile;
  }

  public static Profile produceLCDProfile(Profile.PCSType pcsType,
                                          CIEXYZ mediaWhitePoint,
                                          double[][]
                                          chromaticAdaptationMatrix,
                                          String profileDescription,
                                          ColorSpaceConnectedLUT AToB0,
                                          ColorSpaceConnectedLUT AToB1,
                                          ColorSpaceConnectedLUT AToB2,
                                          ColorSpaceConnectedLUT BToA0,
                                          ColorSpaceConnectedLUT BToA1,
                                          ColorSpaceConnectedLUT BToA2,
                                          CIEXYZ luminance
      ) {
    Profile profile = new Profile();
    profile.profileClass = Profile.ProfileClass.CLASS_DISPLAY;
    profile.technology = Profile.Technology.ActiveMatrixDisplay;
    profile.dataColourSpace = Profile.DataColourSpace.rgb;
    profile.pcsType = pcsType;

    profile.AToB0 = AToB0;
    profile.AToB1 = AToB1;
    profile.AToB2 = AToB2;
    profile.BToA0 = BToA0;
    profile.BToA1 = BToA1;
    profile.BToA2 = BToA2;

    profile.mediaWhitePoint = mediaWhitePoint;
    profile.chromaticAdaptation = chromaticAdaptationMatrix;
    profile.profileDescription = profileDescription;

    profile.luminance = luminance;

    return profile;

  }

  public static Profile produceColorSpaceConversionProfile(Profile.PCSType
      pcsType,
      CIEXYZ mediaWhitePoint,
      double[][]
      chromaticAdaptationMatrix,
      String profileDescription,
      ColorSpaceConnectedLUT AToB0,
      ColorSpaceConnectedLUT AToB1,
      ColorSpaceConnectedLUT AToB2,
      ColorSpaceConnectedLUT BToA0,
      ColorSpaceConnectedLUT BToA1,
      ColorSpaceConnectedLUT BToA2,
      CIEXYZ luminance
      ) {
    Profile profile = new Profile();
    profile.profileClass = Profile.ProfileClass.CLASS_COLORSPACECONVERSION;
    profile.technology = Profile.Technology.ActiveMatrixDisplay;
    profile.dataColourSpace = Profile.DataColourSpace.rgb;
    profile.pcsType = pcsType;

    profile.AToB0 = AToB0;
    profile.AToB1 = AToB1;
    profile.AToB2 = AToB2;
    profile.BToA0 = BToA0;
    profile.BToA1 = BToA1;
    profile.BToA2 = BToA2;

    profile.mediaWhitePoint = mediaWhitePoint;
    profile.chromaticAdaptation = chromaticAdaptationMatrix;
    profile.profileDescription = profileDescription;

    profile.luminance = luminance;

    return profile;

  }

  /**
   * 將色外貌的Jab,轉換到vc下實際對應的Lab,並且回傳轉換錯誤的次數
   * @param JabArray double[][]
   * @param vc ViewingConditions
   * @return int 轉換錯誤的次數
   */
  protected final static int JabToXYZArray(double[][] JabArray,
                                           ViewingConditions vc) {
    int size = JabArray.length;
    CIECAM02Color color = new CIECAM02Color();
    int errorCount = 0;
    CIECAM02 ciecam02 = new CIECAM02(vc);

    for (int x = 0; x < size; x++) {
      double[] JabValues = JabArray[x];
      double[] JChValues = CIELCh.fromLabValues(JabValues);

      color.J = JChValues[0];
      color.C = JChValues[1];
      color.h = JChValues[2];

      CIEXYZ XYZ = ciecam02.inverse(color);
      XYZ.times(1. / 100);
      XYZ.getValues(JabArray[x]);
      if (Double.isNaN(JabArray[x][0]) || Double.isNaN(JabArray[x][1]) ||
          Double.isNaN(JabArray[x][2])) {
        errorCount++;
      }
    }
    return errorCount;
  }

  /**
   *
   * @param XYZArray double[][]
   * @param vc ViewingConditions
   * @return int 轉換產生錯誤的次數
   */
  protected final static int XYZToJabArray(double[][] XYZArray,
                                           ViewingConditions vc) {
    int size = XYZArray.length;
    CIEXYZ XYZ = new CIEXYZ();
    int errorCount = 0;
    CIECAM02 ciecam02 = new CIECAM02(vc);

    for (int x = 0; x < size; x++) {
      XYZ.setValues(XYZArray[x]);
      XYZ.times(100.);
      CIECAM02Color cam = ciecam02.forward(XYZ);
      double[] JabValues = cam.getJabcValues();
      XYZArray[x] = JabValues;

      if (Double.isNaN(XYZArray[x][0]) || Double.isNaN(XYZArray[x][1]) ||
          Double.isNaN(XYZArray[x][2])) {
        errorCount++;
      }
    }
    return errorCount;
  }

  private static class LChValuesToXYZTask
      implements ThreadExecutor.ThreadTask {
    public LChValuesToXYZTask(double[] whiteValues, double[][] LChValues) {
      this.whiteValues = whiteValues;
      this.LChValues = LChValues;
    }

    protected double[] whiteValues;
    protected double[][] LChValues;

    public double[] getStartValues() {
      return null;
    }

    public double[] getEndValues() {
      return null;
    }

    public double[] getStepValues() {
      return null;
    }

    public boolean setVariables(double[] variables) {
      return true;
    }

  }

  protected final static double[] findMaxY(double[][] XYZArray) {
    double maxY = Double.MIN_VALUE;
    int size = XYZArray.length;
    int maxIndex = 0;
    for (int x = 0; x < size; x++) {
      double[] XYZValues = XYZArray[x];
      if (XYZValues[1] > maxY) {
        maxY = XYZValues[1];
        maxIndex = x;
      }
    }
    return XYZArray[maxIndex];
  }

  protected final static ColorSpaceConnectedLUT produceRGBToLabCSCLUT(LCDModel
      lcdModel, double[][] input, double[][] output, int grid) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, 0, 0}, new double[] {1, 1, 1},
        output, lcdModel.correct.getInputTables(), null,
        ColorSpaceConnectedLUT.Style.AToB, ColorSpaceConnectedLUT.PCSType.Lab);

    return csclut;
  }

  /**
   * 將double array組成的A2B轉到ColorSpaceConnectedLUT
   * @param lcdModel LCDModel
   * @param input double[][]
   * @param output double[][]
   * @param grid int
   * @return ColorSpaceConnectedLUT
   */
  protected final static ColorSpaceConnectedLUT produceRGBToXYZCSCLUT(LCDModel
      lcdModel, double[][] input, double[][] output, int grid) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, 0, 0}, new double[] {1, 1, 1}, output,
        lcdModel.correct.getInputTables(), null,
        ColorSpaceConnectedLUT.Style.AToB,
        ColorSpaceConnectedLUT.PCSType.XYZ);

    return csclut;
  }

  protected final static ColorSpaceConnectedLUT produceXYZToRGBCSCLUT(LCDModel
      lcdModel, double[][] input, double[][] output, int grid) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, 0, 0}, new double[] {1, 1, 1}, output,
        null, lcdModel.correct.getOutputTables(),
        ColorSpaceConnectedLUT.Style.BToA,
        ColorSpaceConnectedLUT.PCSType.XYZ);

    return csclut;
  }

  protected final static ColorSpaceConnectedLUT produceLabToRGBCSCLUT(LCDModel
      lcdModel, double[][] input, double[][] output, int grid) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, -128, -128}, new double[] {100, 127, 127},
        output, null, lcdModel.correct.getOutputTables(),
        ColorSpaceConnectedLUT.Style.BToA, ColorSpaceConnectedLUT.PCSType.Lab);

    return csclut;
  }

  protected final static ColorSpaceConnectedLUT produceLabToRGBCSCLUT(LCDModel
      lcdModel, double[][] input, double[][] output, int grid,
      double[][] inputTables) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, -128, -128}, new double[] {100, 127, 127},
        output, inputTables, lcdModel.correct.getOutputTables(),
        ColorSpaceConnectedLUT.Style.BToA, ColorSpaceConnectedLUT.PCSType.Lab);

    return csclut;
  }

  protected final static ColorSpaceConnectedLUT produceRGBToLabCSCLUT(DCModel
      dcModel, double[][] input, double[][] output, int grid) {

    ColorSpaceConnectedLUT csclut = new ColorSpaceConnectedLUT(3, 3, grid,
        input, new double[] {0, 0, 0}, new double[] {1, 1, 1},
        output, dcModel.getInputTables(), null,
        ColorSpaceConnectedLUT.Style.AToB, ColorSpaceConnectedLUT.PCSType.Lab);

    return csclut;
  }

//  public static ViewingConditions perceptualIntentViewingConditions;
//
//  public static ViewingConditions dimViewingConditions;
//  static {
//    CIEXYZ D50 = (CIEXYZ) Illuminant.D50WhitePoint.clone();
//    D50.times(100.);
//    perceptualIntentViewingConditions = new ViewingConditions(D50, 31.83, 20,
//        ViewingConditions.Surround.Average, "Perceptual", 1.);
//
//    dimViewingConditions = new ViewingConditions(D50, 0.1, 20,
//                                                 ViewingConditions.Surround.Dim,
//                                                 "Dim");
//  }

  /**
   * 將一些轉換之後,造成錯誤的暗部顏色,修正成黑色
   * @param LabArray double[][]
   * @return int
   */
  protected final static int fixDarkLabValue(double[][] LabArray) {
    int size = LabArray.length;
    int errorCount = 0;

    for (int x = 0; x < size; x++) {
      double[] LabValues = LabArray[x];

      if (Double.isNaN(LabValues[0]) || Double.isNaN(LabValues[1]) ||
          Double.isNaN(LabValues[2])) {
        LabValues[0] = 0;
        LabValues[1] = 0;
        LabValues[2] = 0;
        errorCount++;
      }

      if (LabValues[1] == 127 && LabValues[2] == -128) {
        LabValues[1] = 0;
        LabValues[2] = 0;
        errorCount++;
      }
    }
    return errorCount;
  }

  public static class FactorFilter
      implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return name.endsWith(".factor");
    }
  }
}
