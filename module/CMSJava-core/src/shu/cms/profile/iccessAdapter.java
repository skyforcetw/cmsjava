package shu.cms.profile;

import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.util.log.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.datatype.ProfileClass;
import tw.edu.shu.im.iccio.tagtype.*;
import shu.math.array.DoubleArray;

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
public class iccessAdapter {
  public static final String COPYRIGHT = "Copyright(C)2007 cms.im.shu.edu.tw";
  public static final String DESCRIPTION =
      "(by skyforce)";
  public static final String CREATOR = "SKY";

  public static void main(String[] args) {
    Profile p = loadProfile("cms_dc.icc");
    System.out.println(DoubleArray.toString(p.getChromaticAdaptation()));
  }

  protected final static double[] toDoubleArray(S15Fixed16ArrayType s15Fixed16) {
    S15Fixed16Number[] array = s15Fixed16.getArray();
    int size = array.length;
    double[] doubleArray = new double[size];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = array[x].doubleValue();
    }
    return doubleArray;
  }

  protected final static CIEXYZ toCIEXYZ(XYZType xyzType) {
    XYZNumber xyzNumber = xyzType.getXYZNumbers()[0];
    double X = xyzNumber.getCIEX().doubleValue();
    double Y = xyzNumber.getCIEY().doubleValue();
    double Z = xyzNumber.getCIEZ().doubleValue();
    CIEXYZ XYZ = new CIEXYZ();
    XYZ.X = X;
    XYZ.Y = Y;
    XYZ.Z = Z;

    return XYZ;
  }

  /**
   *
   * @param filename String
   * @return Profile
   */
  public static Profile loadProfile(String filename) {
    try {
      Profile p = new Profile();

      ICCProfile profile = ICCProfileReader.loadProfile(filename);
      ICCProfileHeader header = profile.getHeader();
      produceHeader(header, p);

      ICCProfileTagTable tagTable = profile.getTagTable();

      XYZType mwp = (XYZType) getTag(tagTable, Tags.mediaWhitePointTag);
      if (mwp != null) {
        p.mediaWhitePoint = toCIEXYZ(mwp);
      }

      XYZType luminance = (XYZType) getTag(tagTable, Tags.luminanceTag);
      if (luminance != null) {
        p.luminance = toCIEXYZ(luminance);
      }

      S15Fixed16ArrayType chromaticAdaptation = (S15Fixed16ArrayType) getTag(
          tagTable, Tags.chromaticAdaptationTag);
      double[] caValue = minusValueUncorrect(toDoubleArray(chromaticAdaptation));
      p.chromaticAdaptation = DoubleArray.to2DDoubleArray(caValue, 3);

      Lut16Type A2B0 = (Lut16Type) getTag(tagTable, Tags.AToB0Tag);
      p.AToB0 = produceColorSpaceConnectedLUT(A2B0, p.isLabPCS());

      Lut16Type B2A0 = (Lut16Type) getTag(tagTable, Tags.BToA0Tag);
      if (B2A0 != null) {
        p.BToA0 = produceColorSpaceConnectedLUT(B2A0, p.isLabPCS());
      }

      Lut16Type A2B1 = (Lut16Type) getTag(tagTable, Tags.AToB1Tag);
      p.AToB1 = produceColorSpaceConnectedLUT(A2B1, p.isLabPCS());

      Lut16Type B2A1 = (Lut16Type) getTag(tagTable, Tags.BToA1Tag);
      if (B2A1 != null) {
        p.BToA1 = produceColorSpaceConnectedLUT(B2A1, p.isLabPCS());
      }

      return p;
    }
    catch (ICCProfileException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public static AbstractTagType getTag(ICCProfileTagTable tagTable,
                                       int tags) {
    int count = tagTable.getTagCount();
    for (int x = 0; x < count; x++) {
      ICCProfileTagEntry entry = tagTable.getTag(x);
      Signature sig = entry.getSignature();

      int sigIntValue = Integer.parseInt(sig.toString());
      if (sigIntValue == tags) {
        return entry.getData();
      }
    }
    return null;
  }

  public static void storeICCProfileLutByLut16(Profile p,
                                               String filename) {
    storeICCProfileLutByLut16(p, filename, "");
  }

  public static void storeICCProfileLutByLut16(Profile p,
                                               String filename,
                                               String description) {
    try {
      ICCProfileHeader header = produceHeader(p);
      ICCProfileTagTable table = new ICCProfileTagTable();
      ICCDisplayProfileLut profile = new ICCDisplayProfileLut(header, table);

      //profileDescriptionTag
      MultiLocalizedUnicodeType pdmt = new MultiLocalizedUnicodeType();
      byte[] uni1 = ICCUtils.enUnicode(p.getProfileDescription() + ' ' +
                                       description +
                                       DESCRIPTION);
      pdmt.addText("EN", "US", uni1);
      profile.setMultiTextTag(Tags.profileDescriptionTag, pdmt);

      //mediaWhitePointTag
      CIEXYZ mwp = p.getMediaWhitePoint();
      if (mwp != null) {
        profile.setWhitePoint(mwp.X, mwp.Y, mwp.Z);
      }

      //luminanceTag
      CIEXYZ luminance = p.getLuminance();
      if (luminance != null) {
        XYZType luminanceTag = new XYZType();
        luminanceTag.addNumbers(new double[][] {luminance.getValues()});
        profile.addTagTypeEntry(Tags.luminanceTag, luminanceTag);
      }

      //copyrightTag
      MultiLocalizedUnicodeType copyrightmt = new MultiLocalizedUnicodeType();
      byte[] unitext = ICCUtils.enUnicode(COPYRIGHT);
      copyrightmt.addText("EN", "US", unitext);
      profile.setMultiTextTag(Tags.copyrightTag, copyrightmt);

      //chromaticAdaptationTag
      double[][] ca = p.getChromaticAdaptation();
      if (ca != null) {
        double[] chromaticAdaptation = DoubleArray.to1DDoubleArray(ca);
        chromaticAdaptation = minusValueCorrect(chromaticAdaptation);
        profile.setChromaticAdaptation(chromaticAdaptation);
      }

      //AToB0Tag
      if (p.AToB0 != null) {
        Lut16Type A2B0 = produceLut16(p.AToB0);
        profile.addTagTypeEntry(Tags.AToB0Tag, A2B0);
      }

      //BToA0Tag
      if (p.BToA0 != null) {
        Lut16Type B2A0 = produceLut16(p.BToA0);
        profile.addTagTypeEntry(Tags.BToA0Tag, B2A0);
      }

      //AToB1Tag
      if (p.AToB1 != null) {
        Lut16Type A2B1 = produceLut16(p.AToB1);
        profile.addTagTypeEntry(Tags.AToB1Tag, A2B1);
      }

      //BToA1Tag
      if (p.BToA1 != null) {
        Lut16Type B2A1 = produceLut16(p.BToA1);
        profile.addTagTypeEntry(Tags.BToA1Tag, B2A1);
      }

      if (p.technology != null) {
        SignatureType technology = new SignatureType();
        switch (p.technology) {
          case DigitalCamera:
            technology.setSignatureData("dcam");
            break;
          case CathodeRayTubeDisplay:
            technology.setSignatureData("CRT");
            break;
          case PassiveMatrixDisplay:
            technology.setSignatureData("PMD");
            break;
          case ActiveMatrixDisplay:
            technology.setSignatureData("AMD");
            break;
        }
        profile.addTagTypeEntry(Tags.technologyTag, technology);
      }

      //========================================================================
      // private tag
      //========================================================================
      //儲存產生Profile過程的紀錄
      if (p.profileProductionRecord != null) {
        TextType ppr = new TextType("ProfileProductionRecord:\n\n" +
                                    p.profileProductionRecord);
        profile.addTagTypeEntry(PrivateTags.profileProductionRecordTag, ppr);
      }

      //同GMB的DevD,Device的量測資料
      if (p.deviceMeasureData != null) {
        TextType dmd = new TextType("DeviceMeasureData:\n\n" +
                                    p.deviceMeasureData);
        profile.addTagTypeEntry(PrivateTags.profileProductionRecordTag, dmd);
      }

      //同GMB的CIED,CIE的量測資料
      if (p.CIEMeasureData != null) {
        TextType Cmd = new TextType("CIEMeasureData:\n\n" +
                                    p.CIEMeasureData);
        profile.addTagTypeEntry(PrivateTags.profileProductionRecordTag, Cmd);
      }
      //========================================================================

      //========================================================================
      // 儲存
      //========================================================================
      ICCFileWriter writer = new ICCFileWriter(filename);
      profile.save(writer);
      writer.close();
      //========================================================================
    }
    catch (ICCProfileException ex) {
      Logger.log.error("", ex);
    }

  }

  protected static UInt16ArrayType toUInt16ArrayType(double[] doubleArray) throws
      ICCProfileException {
    int size = doubleArray.length;
    int[] intArray = new int[size];

    for (int x = 0; x < size; x++) {
      intArray[x] = (int) Math.round(doubleArray[x] * UInt16Number.MAX_VALUE);
    }
    UInt16ArrayType uint16Array = new UInt16ArrayType();
    uint16Array.addNumbers(intArray);
    return uint16Array;
  }

  /**
   *
   * @param doubleArray double[]
   * @param legacyEncoding boolean
   * @return UInt16ArrayType
   * @throws ICCProfileException
   * @deprecated
   */
  protected static UInt16ArrayType toUInt16ArrayType(double[] doubleArray,
      boolean legacyEncoding) throws
      ICCProfileException {
    int size = doubleArray.length;
    int[] intArray = new int[size];
    double correct = 65535. / 65280.;

    if (legacyEncoding) {
      for (int x = 0; x < size; x++) {
        double v = doubleArray[x] * UInt16Number.MAX_VALUE;
        if (v % 3 != 0) {
          v /= correct;
        }
        intArray[x] = (int) Math.round(v);
      }
    }
    else {
      for (int x = 0; x < size; x++) {
        intArray[x] = (int) Math.round(doubleArray[x] * UInt16Number.MAX_VALUE);
      }
    }

    UInt16ArrayType uint16Array = new UInt16ArrayType();
    uint16Array.addNumbers(intArray);
    return uint16Array;
  }

  /**
   * 從iccio的Lut16Type轉到CMSJava的ColorSpaceConnectedLUT
   * @param lut Lut16Type
   * @param doLabCorrect boolean
   * @return ColorSpaceConnectedLUT
   */
  protected static ColorSpaceConnectedLUT produceColorSpaceConnectedLUT(
      Lut16Type lut, boolean doLabCorrect) {
    int inputChannels = lut.getNumInputChannels().intValue();
    int outputChannels = lut.getNumOutputChannels().intValue();
    int numGridPoints = lut.getNumGridPoints().intValue();
    double[][] matrix = getMatrix(lut);

    double[][] inputTables = getIOTables(lut.getInputTables(), outputChannels);
    double[][] outputTables = getIOTables(lut.getOutputTables(), outputChannels);

    double[][] clutOutput = getCLUTOutput(lut.getClutValues(), outputChannels,
                                          doLabCorrect);

//    ColorSpaceConnectedLUT clut = new ColorSpaceConnectedLUT(inputChannels,
//        outputChannels, numGridPoints, clutOutput, inputTables, outputTables);
    ColorSpaceConnectedLUT clut = ColorSpaceConnectedLUT.getXYZCLUTInstance(
        inputChannels, outputChannels, numGridPoints, clutOutput, inputTables,
        outputTables);
    clut.matrix = matrix;
    return clut;
  }

  /**
   * 從iccio的clut轉到CMSJava下
   * @param clutValues UInt16Number[]
   * @param outputChannels int
   * @param doLabCorrect boolean
   * @return double[][]
   */
  protected static double[][] getCLUTOutput(UInt16Number[] clutValues,
                                            int outputChannels,
                                            boolean doLabCorrect) {
    double[] doubleArray = getDoubleArray(clutValues);
    double[][] clutOutput = DoubleArray.to2DDoubleArray(doubleArray,
        outputChannels);
    if (doLabCorrect) {
      int size = clutOutput.length;
      for (int x = 0; x < size; x++) {
        double[] values = clutOutput[x];
        values[0] *= 100;
        values[1] = values[1] * 255 - 128;
        values[2] = values[2] * 255 - 128;
      }
    }
    return clutOutput;
  }

  /**
   * 從iccio的i/o tables轉到CMSJava下
   * @param tables UInt16Number[]
   * @param outputChannels int
   * @return double[][]
   */
  protected static double[][] getIOTables(UInt16Number[] tables,
                                          int outputChannels) {
    double[] doubleArray = getDoubleArray(tables);
    int width = tables.length / outputChannels;
    return DoubleArray.to2DDoubleArray(doubleArray, width);
  }

  protected static double[] getDoubleArray(UInt16Number[] uintArray) {
    int size = uintArray.length;
    double[] doubleArray = new double[size];
    for (int x = 0; x < size; x++) {
      UInt16Number uint = uintArray[x];
      doubleArray[x] = ( (double) uint.intValue()) / UInt16Number.MAX_VALUE;
    }
    return doubleArray;
  }

  protected static void setMatrix(Lut16Type lut16, double[] matrix) throws
      ICCProfileException {
    lut16.setE00(new S15Fixed16Number(matrix[0]));
    lut16.setE01(new S15Fixed16Number(matrix[1]));
    lut16.setE02(new S15Fixed16Number(matrix[2]));
    lut16.setE10(new S15Fixed16Number(matrix[3]));
    lut16.setE11(new S15Fixed16Number(matrix[4]));
    lut16.setE12(new S15Fixed16Number(matrix[5]));
    lut16.setE20(new S15Fixed16Number(matrix[6]));
    lut16.setE21(new S15Fixed16Number(matrix[7]));
    lut16.setE22(new S15Fixed16Number(matrix[8]));
  }

  /**
   * 從iccio Lut16Type的maxtir轉到CMSJava下
   * @param lut16 Lut16Type
   * @return double[][]
   */
  protected static double[][] getMatrix(Lut16Type lut16) {
    double[][] matrix = new double[3][3];
    matrix[0][0] = lut16.getE00().doubleValue();
    matrix[0][1] = lut16.getE01().doubleValue();
    matrix[0][2] = lut16.getE02().doubleValue();
    matrix[1][0] = lut16.getE10().doubleValue();
    matrix[1][1] = lut16.getE11().doubleValue();
    matrix[1][2] = lut16.getE12().doubleValue();
    matrix[2][0] = lut16.getE20().doubleValue();
    matrix[2][1] = lut16.getE21().doubleValue();
    matrix[2][2] = lut16.getE22().doubleValue();
    return matrix;
  }

  protected static Lut16Type produceLut16(ColorSpaceConnectedLUT lut
      ) throws ICCProfileException {

    int inputCh = lut.getInputChannels();
    int outputCh = lut.getOutputChannels();

    Lut16Type lut16 = new Lut16Type();
    lut16.setNumInputChannels(new UInt8Number(inputCh));
    lut16.setNumOutputChannels(new UInt8Number(outputCh));
    lut16.setNumGridPoints(new UInt8Number(lut.getNumberOfGridPoints()));

    //==========================================================================
    // matrix
    //==========================================================================
    double[] matrix = null;
    if (lut.getMatrix() != null) {
      matrix = DoubleArray.to1DDoubleArray(lut.getMatrix());
      matrix = minusValueCorrect(matrix);
    }
    else {
      matrix = new double[] {
          1, 0, 0, 0, 1, 0, 0, 0, 1};
    }

    setMatrix(lut16, matrix);
    //==========================================================================
    // 1d input tables
    //==========================================================================
    UInt16ArrayType inputTable = null;
    if (lut.getInputTables() != null) {
      double[] inputValues = lut.getICCFormatInputTables();
      /*if (doLabCorrect) {
        inputValues = lut.getICCFormatInputTables();
             }
             else {
        inputValues = DoubleArray.to1DDoubleArray(lut.getInputTables());
             }*/
//      if (doLabCorrect) {
      inputTable = toUInt16ArrayType(inputValues);
//      }
//      else {
//         inputTable = toUInt16ArrayType(inputValues,false);
//      }

      lut16.setNumInputEntries(new UInt16Number(inputValues.length / 3));
    }
    else {
      inputTable = toUInt16ArrayType(new double[] {0, 1, 0, 1, 0, 1});
      lut16.setNumInputEntries(new UInt16Number(2));
    }
    lut16.setInputTables(inputTable.getArray());

    //==========================================================================
    // 1d output tables
    //==========================================================================
    UInt16ArrayType outputTable = null;
    if (lut.getOutputTables() != null) {
      double[] outputValues = DoubleArray.to1DDoubleArray(lut.getOutputTables());
//      outputValues = minusValueCorrect(outputValues);
      outputTable = toUInt16ArrayType(outputValues);
      lut16.setNumOutputEntries(new UInt16Number(outputValues.length / 3));
    }
    else {
      outputTable = toUInt16ArrayType(new double[] {0, 1, 0, 1, 0, 1});
      lut16.setNumOutputEntries(new UInt16Number(2));
    }
    lut16.setOutputTables(outputTable.getArray());

    //==========================================================================
    // CLUT
    //==========================================================================
    double[] iccFormatOutput = null;
    UInt16ArrayType clut = null;
//    if (doLabCorrect) {
    iccFormatOutput = lut.getICCFormatOutput();
    clut = toUInt16ArrayType(iccFormatOutput);
//    }
//    else {
//      iccFormatOutput = lut.getICCFormatOutput();
//      clut = toUInt16ArrayType(iccFormatOutput, false);
//    }

//    UInt16ArrayType clut = toUInt16ArrayType(iccFormatOutput, doLabCorrect);

    lut16.setClutValues(clut.getArray());

    return lut16;
  }

  /**
   * 從iccio的header轉到CMSJava的profile
   * @param header ICCProfileHeader
   * @param profile Profile
   */
  protected static void produceHeader(ICCProfileHeader header, Profile profile) {
    ProfileClass deviceClass = header.getDeviceClass_();
    if (deviceClass.isDisplayDevice()) {
      profile.profileClass = Profile.ProfileClass.CLASS_DISPLAY;
    }
    else if (deviceClass.isInputDevice()) {
      profile.profileClass = Profile.ProfileClass.CLASS_INPUT;
    }

    ColorSpace cs = header.getColorSpace_();
    if (cs.isRgbData()) {
      profile.dataColourSpace = Profile.DataColourSpace.rgb;
    }

    ColorSpace pcs = header.getPcs_();
    if (pcs.isXYZData()) {
      profile.pcsType = Profile.PCSType.XYZ;
    }
    else if (pcs.isLabData()) {
      profile.pcsType = Profile.PCSType.Lab;
    }
  }

  protected static ICCProfileHeader produceHeader(Profile profile) {
    ICCProfileHeader header = new ICCProfileHeader();

    try {
      header.setCreatorSignature(CREATOR);
    }
    catch (ICCProfileException ex) {
      Logger.log.error("", ex);
    }

    switch (profile.getProfileClass()) {
      case CLASS_DISPLAY:
        header.setDeviceClass(ProfileClass.DISPLAY_DEVICE);
        break;
      case CLASS_INPUT:
        header.setDeviceClass(ProfileClass.INPUT_DEVICE);
        break;
      case CLASS_COLORSPACECONVERSION:
        header.setDeviceClass(ProfileClass.COLOR_CONV);
        break;
    }

    switch (profile.getDataColourSpace()) {
      case rgb:
        header.setColorSpace(ColorSpace.RGB_DATA);
        break;
    }

    switch (profile.getPCSType()) {
      case XYZ:
        header.setPcs(ColorSpace.XYZ_DATA);
        break;
      case Lab:
        header.setPcs(ColorSpace.LAB_DATA);
        break;
    }

    return header;
  }

  /**
   * 進入iccio前,負值的修正(iccio的問題!?)
   * @param v double[]
   * @return double[]
   */
  protected static double[] minusValueCorrect(double[] v) {
    for (int x = 0; x < v.length; x++) {
      if (v[x] < 0) {
        v[x] = -32768 + Math.abs(v[x]);
      }
    }
    return v;
  }

  /**
   * 從iccio讀入後,負值的修正(iccio的問題!?)
   * @param v double[]
   * @return double[]
   */
  protected static double[] minusValueUncorrect(double[] v) {
    for (int x = 0; x < v.length; x++) {
      if (v[x] < 0) {
        v[x] = - (32768 + v[x]);
      }
    }
    return v;
  }

}

final class PrivateTags {
  public static final int profileProductionRecordTag = 0x70707220; //ppr
  public static final int deviceMeasureDataTag = 0x646d6420; //dmd
  public static final int CIEMeasureDataTag = 0x436d6420; //Cmd
}
