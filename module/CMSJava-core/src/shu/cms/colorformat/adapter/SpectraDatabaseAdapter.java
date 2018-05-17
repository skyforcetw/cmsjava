package shu.cms.colorformat.adapter;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.TargetAdapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.LCDTargetBase.Number;
import shu.cms.reference.spectra.*;

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
public class SpectraDatabaseAdapter
    extends TargetAdapter {
  protected SpectraDatabase.Content content;
  protected Illuminant illuminant;
  protected RGB.ColorSpace rgbColorSpace;
  public SpectraDatabaseAdapter(SpectraDatabase.Content content) {
    this(content, null, null);
  }

  public SpectraDatabaseAdapter(SpectraDatabase.Content content,
                                Illuminant illuminant,
                                RGB.ColorSpace rgbColorSpace) {
    this.content = content;
    this.illuminant = illuminant;
    this.rgbColorSpace = rgbColorSpace;
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
    return null;
  }

  /**
   * getFileDescription
   *
   * @return String
   */
  public String getFileDescription() {
    return content.name();
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
    return null;
  }

  /**
   * getPatchNameList
   *
   * @return List
   */
  public List<String> getPatchNameList() {
    return null;
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    if (rgbColorSpace == null) {
      throw new UnsupportedOperationException();
    }
    if (rgbList == null && illuminant != null) {
      List<CIEXYZ> XYZList = getXYZList();
      int size = XYZList.size();
      rgbList = new ArrayList<RGB> (size);
      for (int x = 0; x < size; x++) {
        CIEXYZ XYZ = XYZList.get(x);
        RGB rgb = RGB.fromXYZ(XYZ, rgbColorSpace);
//        rgb.rationalize();
        rgbList.add(rgb);
      }
    }
    return rgbList;
  }

  protected List<RGB> rgbList = null;

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    if (spectraList == null && illuminant != null) {
      spectraList = Spectra.produceSpectraPowerList(getReflectSpectraList(),
          illuminant, true);
    }
    return spectraList;
  }

  public List<Spectra> getReflectSpectraList() {
    if (reflectSpectraList == null) {
      reflectSpectraList = SpectraDatabase.getSpectraList(content);
    }
    return reflectSpectraList;
  }

  protected List<Spectra> reflectSpectraList = null;
  protected List<Spectra> spectraList = null;

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    if (rgbColorSpace != null) {
      return Style.RGBXYZSpectra;
    }
    else {
      return Style.XYZSpectra;
    }
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    if (XYZList == null && illuminant != null) {
      List<Spectra> spectralList = getSpectraList();
      int size = spectralList.size();
      XYZList = new ArrayList<CIEXYZ> (spectralList.size());
      CIEXYZ white = illuminant.getSpectra().getXYZ();

      for (int x = 0; x < size; x++) {
        Spectra s = spectralList.get(x);
        CIEXYZ XYZ = s.getXYZ();
        XYZ.normalize(white);
        XYZList.add(XYZ);
      }
    }
    return XYZList;

  }

  protected List<CIEXYZ> XYZList = null;

  public RationalRGBAdapter getRationalRGBAdapter() {
    return new RationalRGBAdapter(this);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 只回傳在RGB色彩空間色域內的色塊
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected static class RationalRGBAdapter
      extends SpectraDatabaseAdapter {

    public RationalRGBAdapter(SpectraDatabaseAdapter full) {
      super(full.content, full.illuminant,
            full.rgbColorSpace);
      this.full = full;
      List<RGB> rgbList = full.getRGBList();
      int size = rgbList.size();
      rationalArray = new boolean[size];
      for (int x = 0; x < size; x++) {
        rationalArray[x] = rgbList.get(x).isLegal();
      }
    }

    protected SpectraDatabaseAdapter full;

    protected boolean[] rationalArray;

    public List<RGB> getRGBList() {
      if (rationalRGBList == null) {
        List<RGB> list = full.getRGBList();
        rationalRGBList = new ArrayList<RGB> ();
        for (int x = 0; x < rationalArray.length; x++) {
          if (rationalArray[x]) {
            rationalRGBList.add(list.get(x));
          }
        }
      }
      return rationalRGBList;
    }

    public List<Spectra> getReflectSpectraList() {
      if (rationalReflectSpectraList == null) {
        List<Spectra> list = full.getReflectSpectraList();
        rationalReflectSpectraList = new ArrayList<Spectra> ();
        for (int x = 0; x < rationalArray.length; x++) {
          if (rationalArray[x]) {
            rationalReflectSpectraList.add(list.get(x));
          }
        }
      }
      return rationalReflectSpectraList;
    }

    public List<Spectra> getSpectraList() {
      if (rationalSpectraList == null) {
        List<Spectra> list = full.getSpectraList();
        rationalSpectraList = new ArrayList<Spectra> ();
        for (int x = 0; x < rationalArray.length; x++) {
          if (rationalArray[x]) {
            rationalSpectraList.add(list.get(x));
          }
        }
      }
      return rationalSpectraList;
    }

    public List<CIEXYZ> getXYZList() {
      if (rationalXYZList == null) {
        List<CIEXYZ> list = full.getXYZList();
        rationalXYZList = new ArrayList<CIEXYZ> ();
        for (int x = 0; x < rationalArray.length; x++) {
          if (rationalArray[x]) {
            rationalXYZList.add(list.get(x));
          }
        }
      }
      return rationalXYZList;

    }

    protected List<RGB> rationalRGBList = null;

    protected List<Spectra> rationalReflectSpectraList = null;
    protected List<Spectra> rationalSpectraList = null;
    protected List<CIEXYZ> rationalXYZList = null;
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
