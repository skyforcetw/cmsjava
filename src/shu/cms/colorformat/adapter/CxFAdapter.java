package shu.cms.colorformat.adapter;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.TargetAdapter.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

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
public class CxFAdapter
    extends TargetAdapter {
  public CxFAdapter() {

  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  public CxFAdapter(String filename) {
    this.filename = filename;
    cxf = new CXFOperator(filename);
    spectraList = cxf.getSpectraList();
    spectrumType = spectraList.get(0).getSpectraType();
  }

  protected String filename;
  protected Spectra.SpectrumType spectrumType;
  protected CXFOperator cxf;

  /**
   * estimateNumber
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    return LCDTargetBase.Number.getNumber(getRGBList().size());
  }

  /**
   * getAbsolutePath
   *
   * @return String
   */
  public String getAbsolutePath() {
    throw new UnsupportedOperationException();
  }

  /**
   * getFileDescription
   *
   * @return String
   */
  public String getFileDescription() {
    return "Color eXchange Format";
  }

  /**
   * getFileNameExtension
   *
   * @return String
   */
  public String getFileNameExtension() {
    return "cxf";
  }

  /**
   * getFilename
   *
   * @return String
   */
  public String getFilename() {
    return filename;
  }

  /**
   * getPatchNameList
   *
   * @return List
   */
  public List<String> getPatchNameList() {
    return cxf.getSampleNameList(0);
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    if (rgbList == null) {
      rgbList = cxf.getRGBList();
    }
    return rgbList;
  }

  protected List<RGB> rgbList;

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    if (spectrumType != null &&
        (spectrumType.equals(Spectra.SpectrumType.EMISSION) ||
         spectrumType.equals(Spectra.SpectrumType.AMBIENTLIGHT))) {
//      spectraList = cxf.getSpectraList();
      return spectraList;
    }
    else {
      return null;
    }
  }

  public List<Spectra> getReflectSpectraList() {
    if (spectrumType != null &&
        spectrumType.equals(Spectra.SpectrumType.REFLECTANCE)) {
      return spectraList;
    }
    else {
      return null;
    }
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    getRGBList();
    getXYZList();
    getSpectraList();
    boolean RGB = rgbList != null;
    boolean XYZ = XYZList != null;
    boolean spectra = spectraList != null;
    if (!RGB) {
      return Style.Unknow;
    }
    else if (XYZ && spectra) {
      return Style.RGBXYZSpectra;
    }
    else if (XYZ) {
      return Style.RGBXYZ;
    }
    else if (spectra) {
      return Style.RGBSpectra;
    }
    else {
      return Style.RGB;
    }
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    if (XYZList == null) {
      XYZList = cxf.getCIEXYZList();
    }
    return XYZList;
  }

  protected List<CIEXYZ> XYZList;
  protected List<Spectra> spectraList;

  public static void main(String[] args) {
    CxFAdapter cxfadapter = new CxFAdapter("LCD Monitor Reference 2.0.cxf");
    System.out.println(cxfadapter.getFilename());
    cxfadapter.getRGBList();
    cxfadapter.getXYZList();
    cxfadapter.getSpectraList();
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
