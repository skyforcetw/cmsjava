package shu.cms.colorformat.adapter;

import java.util.*;

import javax.swing.filechooser.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

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
public abstract class TargetAdapter {
  public static enum Style {
    RGB, RGBXYZ, RGBSpectra, RGBXYZSpectra, Spectra, XYZSpectra, Unknow, XYZ
  }

  public abstract Style getStyle();

  public abstract List<RGB> getRGBList();

  public abstract List<CIEXYZ> getXYZList();

  public abstract List<Spectra> getSpectraList();

  public abstract List<Spectra> getReflectSpectraList();

  public abstract List<String> getPatchNameList();

  public abstract String getFilename();

  public abstract String getAbsolutePath();

  public abstract String getFileNameExtension();

  public abstract String getFileDescription();

  public abstract LCDTargetBase.Number estimateLCDTargetNumber();

  public abstract boolean probeParsable();

  protected FileNameExtensionFilter filter = getFileNameExtension() != null ?
      new FileNameExtensionFilter(
          getFileDescription() + " (*." +
          getFileNameExtension() + ")",
          getFileNameExtension()) : null;

  public FileNameExtensionFilter getFileNameExtensionFilter() {
    return filter;
  }

  public abstract boolean isInverseModeMeasure();

}
