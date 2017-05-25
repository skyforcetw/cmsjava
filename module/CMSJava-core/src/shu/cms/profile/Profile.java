package shu.cms.profile;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * Profile主要是相容於ICC Profile的內容,
 * 以記錄為主要目標,所以盡量減少函式的實作.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Profile {
  protected ProfileClass profileClass;
  protected Date calibrationDateTime;
  protected String deviceManufacturerDesc;
  protected String deviceModelDesc;
  protected Technology technology;

  protected String viewingCondDesc;
  protected ViewingConditions viewingConditions;

  protected PCSType pcsType;
  protected CIEXYZ mediaWhitePoint;
  protected DataColourSpace dataColourSpace;
  protected double[][] chromaticAdaptation;
  protected CIEXYZ luminance;

  protected String profileDescription;

  protected String deviceMeasureData;
  protected String CIEMeasureData;
  protected String profileProductionRecord;

  public String getProfileDescription() {
    return profileDescription;
  }

  public CIEXYZ getLuminance() {
    return luminance;
  }

  public DataColourSpace getDataColourSpace() {
    return dataColourSpace;
  }

  public static enum MeasurementFlare {
    Percent0, Percent100
  }

  public static enum MeasurementGeometry {
    Unknown, Degree045or450, Degree0dord0
  }

  public static enum ProfileClass {
    CLASS_DISPLAY, CLASS_INPUT, CLASS_OUTPUT, CLASS_COLORSPACECONVERSION
  }

  public static enum StandardObserver {
    CIE1931, CIE1964
  }

  public static enum Technology {
    DigitalCamera, CathodeRayTubeDisplay, PassiveMatrixDisplay,
    ActiveMatrixDisplay
  }

  public static class ViewingConditions {
    public CIEXYZ illuminant;
    public CIEXYZ surround;
    public MeasurementType illuminantType;
  }

  public static enum StandardIlluminant {
    Unknown, D50, D65, D93, F2, D55, A, E, F8;
  }

  public final static Illuminant getCIEIlluminant(StandardIlluminant
                                                  illuminant) {
    switch (illuminant) {
      case D50:
        return Illuminant.D50;
      case D65:
        return Illuminant.D65;
      case D55:
        return Illuminant.D55;
      case A:
        return Illuminant.A;
      case F8:
        return Illuminant.F8;
      default:
        return null;
    }
  }

  public static class MeasurementType {
    public StandardObserver standardObserver;
    public MeasurementGeometry measurementGeometry;
    public MeasurementFlare measurementFlare;
    public StandardIlluminant standardIlluminant;
    public CIEXYZ measurementBacking;
  }

  public static enum PCSType {
    XYZ, Lab
  }

  public static enum DataColourSpace {
    XYZ, Lab, rgb, cmy, cmyk
  }

  public Date getCalibrationDateTime() {
    return calibrationDateTime;
  }

  public String getDeviceManufacturerDesc() {
    return deviceManufacturerDesc;
  }

  public String getViewingCondDesc() {
    return viewingCondDesc;
  }

  public String getDeviceModelDesc() {
    return deviceModelDesc;
  }

  public ProfileClass getProfileClass() {
    return profileClass;
  }

  public Technology getTechnology() {
    return technology;
  }

  public ViewingConditions getViewingConditions() {
    return viewingConditions;
  }

  public PCSType getPCSType() {
    return pcsType;
  }

  public boolean isLabPCS() {
    return pcsType == PCSType.Lab;
  }

  public CIEXYZ getMediaWhitePoint() {
    return mediaWhitePoint;
  }

  public double[][] getChromaticAdaptation() {
    return chromaticAdaptation;
  }

  protected ColorSpaceConnectedLUT AToB0;
  protected ColorSpaceConnectedLUT BToA0;

  protected ColorSpaceConnectedLUT AToB1;
  protected ColorSpaceConnectedLUT BToA1;

  protected ColorSpaceConnectedLUT AToB2;
  protected ColorSpaceConnectedLUT BToA2;

  public ColorSpaceConnectedLUT getAToB(RenderingIntent
                                        intent) {
    switch (intent) {
      case RelativeColorimetric:
        return AToB1;
      case AbsoluteColorimetric:
        return null;
      case Perceptual:
        return AToB0;
      case Saturation:
        return AToB2;
      default:
        return null;
    }
  }

  public ColorSpaceConnectedLUT getBToA(RenderingIntent
                                        intent) {
    switch (intent) {
      case RelativeColorimetric:
        return BToA1;
      case AbsoluteColorimetric:
        return null;
      case Perceptual:
        return BToA0;
      case Saturation:
        return BToA2;
      default:
        return null;
    }
  }

}
