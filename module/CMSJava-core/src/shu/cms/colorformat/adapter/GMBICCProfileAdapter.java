package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import java.awt.color.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將GMB(現在被XRite合併了)產品所產生的ICC Profile,當作Target來使用的adapter.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GMBICCProfileAdapter
    extends TargetAdapter {
  public GMBICCProfileAdapter(String filename) {
    this.filename = filename;
    this.parsing();
  }

  public GMBICCProfileAdapter() {

  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  /**
   * device data(RGB)
   */
  public final static int DevD_TAG = 0x44657644;
  /**
   * CIE data(CIEXYZ,CIELab,Spectra
   */
  public final static int CIED_TAG = 0x43494544;

  public String getDeviceData() {
    return getTag(DevD_TAG);
  }

  public String getCIEData() {
    return getTag(CIED_TAG);
  }

  public List<String> getPatchNameList() {
    return patchNameList;
  }

  protected String getTag(int tag) {
    if (profile == null) {
      try {
        profile = ICC_Profile.getInstance(filename);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
        return null;
      }
    }
    byte[] data = profile.getData(tag);
    return new String(data);
  }

  protected void parsing() {

    String devD = getTag(DevD_TAG);
    if (devD != null) {
      LogoFileAdapter adapter = new LogoFileAdapter(new StringReader(devD));
      rgbList = adapter.getRGBList();
      patchNameList = adapter.getPatchNameList();
    }

    String cieD = getTag(CIED_TAG);
    if (cieD != null) {
      LogoFileAdapter adapter = new LogoFileAdapter(new StringReader(cieD));
      XYZList = adapter.getXYZList();
      spectraList = adapter.getSpectraList();
    }
  }

  public static void main(String[] args) {
    String filename =
        "Measurement Files/Camera/HTC Legend/日光.icc";
    GMBICCProfileAdapter adapter = new GMBICCProfileAdapter(filename);
    for (RGB rgb : adapter.getRGBList()) {
      System.out.println(rgb);
    }
//    LCDTarget lcdTarget = LCDTarget.Instance.get(new GMBICCProfileAdapter(
//        filename));
//    for (Patch p : lcdTarget.getPatchList()) {
//      System.out.println(p);
//    }
  }

//  protected File file;
  protected ICC_Profile profile;
  protected String filename;
  protected List<RGB> rgbList;
  protected List<CIEXYZ> XYZList;
  protected List<String> patchNameList;
  protected List<Spectra> spectraList;
  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    return rgbList;
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    return XYZList;
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List getSpectraList() {
    return spectraList;
  }

  public List<Spectra> getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getName
   *
   * @return String
   */
  public String getFilename() {
    return filename;
  }

  public String getAbsolutePath() {
    return filename;
  }

  public Style getStyle() {
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

  public String getFileNameExtension() {
    return "icc";
  }

  public String getFileDescription() {
    return "GMB ICC Profile";
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    return LCDTargetBase.Number.getNumber(rgbList.size());
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
