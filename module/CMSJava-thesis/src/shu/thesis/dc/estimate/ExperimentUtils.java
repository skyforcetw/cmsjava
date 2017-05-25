package shu.thesis.dc.estimate;

import shu.cms.*;
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
public class ExperimentUtils {
  public final static String format(DeltaEReport deltaEReport) {
    StringBuilder buf = new StringBuilder();
    buf.append(Utils.fmt(deltaEReport.meanDeltaE.getCIE2000DeltaE()) + " ");
    buf.append(Utils.fmt(deltaEReport.minDeltaE.getCIE2000DeltaE()) + " ");
    buf.append(Utils.fmt(deltaEReport.maxDeltaE.getCIE2000DeltaE()) + " ");
    buf.append(Utils.fmt(deltaEReport.mixDeltaE.getCIE2000DeltaE()) + " ");
    buf.append(Utils.fmt(deltaEReport.stdDeltaE.getCIE2000DeltaE()) + " ");
    buf.append(Utils.fmt(deltaEReport.meanCIE2000DeltaLCH.getCIE2000DeltaLCh()) +
               " ");

    return buf.toString();
  }
}
