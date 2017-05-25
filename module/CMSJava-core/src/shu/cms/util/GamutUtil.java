package shu.cms.util;

import javax.vecmath.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.geometry.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class GamutUtil {

  public final static CIExyY[] getPrimaryxyY(LCDTarget target) {
    Patch pr = target.getSaturatedChannelPatch(RGB.Channel.R);
    Patch pg = target.getSaturatedChannelPatch(RGB.Channel.G);
    Patch pb = target.getSaturatedChannelPatch(RGB.Channel.B);
    CIExyY rxyY = new CIExyY(pr.getXYZ());
    CIExyY gxyY = new CIExyY(pg.getXYZ());
    CIExyY bxyY = new CIExyY(pb.getXYZ());
    return new CIExyY[] {
        rxyY, gxyY, bxyY};
  }

  public final static double getGamutPercent(CIExyY[] rgbxyY,
                                             CIExyY[] basedrgbxyY) {
    double compareArea = getArea(getEdgeLength(rgbxyY[0], rgbxyY[1], rgbxyY[2]));
    double basedArea = getArea(getEdgeLength(basedrgbxyY[0], basedrgbxyY[1],
                                             basedrgbxyY[2]));
    return compareArea / basedArea;
  }

  private final static double[] getEdgeLength(CIExyY rxyY, CIExyY gxyY,
                                              CIExyY bxyY) {
    double[] lengthArray = new double[3];
    lengthArray[0] = Geometry.getDistance(getPoint2d(rxyY),
                                          getPoint2d(gxyY));
    lengthArray[1] = Geometry.getDistance(getPoint2d(gxyY),
                                          getPoint2d(bxyY));
    lengthArray[2] = Geometry.getDistance(getPoint2d(rxyY),
                                          getPoint2d(bxyY));
    return lengthArray;
  }

  private final static Point2d getPoint2d(CIExyY xyY) {
    return new Point2d(xyY.x, xyY.y);
  }

  private final static double getArea(double[] edgeLength) {
    double p = (edgeLength[0] + edgeLength[1] + edgeLength[2]) / 2;
    double S = Math.sqrt(p * (p - edgeLength[0]) * (p - edgeLength[1]) *
                         (p - edgeLength[2]));
    return S;
  }

}
