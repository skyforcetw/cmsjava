package shu.cms.colorformat.adapter;

import java.util.*;

import shu.cms.colorformat.adapter.TargetAdapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.lcd.LCDTargetBase.Number;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TargetInterpolatorAdapter
    extends TargetAdapter {
  public TargetInterpolatorAdapter(LCDTargetInterpolator interpolator,
                                   LCDTargetBase.Number number) {
    this.interpolator = interpolator;
    this.number = number;
    this.lcdTarget = LCDTargetBase.Instance.get(number);
  }

  private LCDTarget lcdTarget;
  private LCDTargetBase.Number number;
  private LCDTargetInterpolator interpolator;

  /**
   * estimateLCDTargetNumber
   *
   * @return Number
   */
  public Number estimateLCDTargetNumber() {
    return number;
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
    return lcdTarget.getDescription() + " interpolate";
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
  public List getPatchNameList() {
    return lcdTarget.filter.nameList();
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List getRGBList() {
    return lcdTarget.filter.rgbList();
  }

  /**
   * getReflectSpectraList
   *
   * @return List
   */
  public List getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List getSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    return Style.RGBXYZ;
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List getXYZList() {
    int size = lcdTarget.size();

    List<CIEXYZ> XYZList = new ArrayList<CIEXYZ> ();
    List<RGB> rgbList = lcdTarget.filter.rgbList();
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      CIEXYZ XYZ = null;
      if (rgb.isBlack()) {
        XYZ = interpolator.getPatch(RGB.Channel.G, 0).getXYZ();
      }
      else if (rgb.isGray()) {
        XYZ = interpolator.getPatch(RGB.Channel.W, rgb.getValue(RGB.Channel.W)).
            getXYZ();
      }
      else if (rgb.getZeroChannelCount() == 2) {
        RGB.Channel maxch = rgb.getMaxChannel();
        XYZ = interpolator.getPatch(maxch, rgb.getValue(maxch)).getXYZ();
      }
      XYZList.add(XYZ);
    }
    return XYZList;
  }

  /**
   * isInverseModeMeasure
   *
   * @return boolean
   */
  public boolean isInverseModeMeasure() {
    return lcdTarget.isInverseModeMeasure();
  }

  /**
   * probeParsable
   *
   * @return boolean
   * @todo Implement this shu.cms.colorformat.adapter.TargetAdapter method
   */
  public boolean probeParsable() {
    return false;
  }
}
