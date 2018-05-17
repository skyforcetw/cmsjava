package shu.cms.colorformat.adapter;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.TargetAdapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.ideal.*;
import shu.cms.lcd.LCDTargetBase.Number;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008 skyforce</p>
 * 以IdealDigitalCamera照到的spectraPowerList,產生出RGB
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class IdealDigitalCameraAdapter
    extends TargetAdapter {
  public IdealDigitalCameraAdapter(IdealDigitalCamera camera, List<Spectra>
      spectraPowerList) {
    this.camera = camera;
    this.spectraPowerList = spectraPowerList;
  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  protected IdealDigitalCamera camera;
  protected List<Spectra> spectraPowerList;

  /**
   * estimateLCDTargetNumber
   *
   * @return Number
   */
  public Number estimateLCDTargetNumber() {
    throw new UnsupportedOperationException();
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
    return "Ideal DigitalCamera";
  }

  /**
   * getFileNameExtension
   *
   * @return String
   */
  public String getFileNameExtension() {
    return null;
  }

  /**
   * getFilename
   *
   * @return String
   */
  public String getFilename() {
    return camera.getName();
  }

  /**
   * getPatchNameList
   *
   * @return List
   */
  public List<String> getPatchNameList() {
    return Utils.filterNameList(spectraPowerList);
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    return camera.produceRGBList(spectraPowerList);
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    return spectraPowerList;
  }

  public List<Spectra> getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    return Style.RGBSpectra;
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    throw new UnsupportedOperationException();
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
