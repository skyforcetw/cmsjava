package shu.cms.plot;

import shu.cms.colorspace.independ.*;
import shu.cms.Spectra;

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
public interface xyCoordinateTransfer {

  public double[] getxyCoordinate(CIEXYZ XYZ);

  public double[] getxyCoordinate(Spectra spectra);

  public String[] getxyCoordinateNames();

  public boolean isSpectraOnly();
}
