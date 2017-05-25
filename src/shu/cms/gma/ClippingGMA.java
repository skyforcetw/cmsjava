package shu.cms.gma;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.cms.gma.gbp.*;

///import shu.plot.*;

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
public class ClippingGMA
    extends GamutMappingAlgorithm {

  public ClippingGMA(GamutBoundaryPoint gbp, FocalPoint.FocalType focalType) {
    super(gbp, focalType);
  }

  public ClippingGMA(ProfileColorSpace profileColorSpace,
                     FocalPoint.FocalType focalType) {
    super(profileColorSpace, focalType);
  }

  /**
   *
   * @param LCh CIELCh
   * @return CIELCh
   */
  protected CIELCh _gamutMapping(final CIELCh LCh) {
    //==========================================================================
    // 超出的部分的處理
    //==========================================================================
    if (gbd.isOutOfGamut(LCh)) {
      return gbd.getBoundaryLCh(LCh);
    }
    return LCh;
    //==========================================================================
  }

  public static void main(String[] args) {
    ProfileColorSpace adobe = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        AdobeRGB);
    ProfileColorSpace srgb = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    long start = System.currentTimeMillis();
    GamutBoundaryPoint adobeGBP = new GamutBoundaryPoint(adobe);
    GamutBoundaryPoint sGBP = new GamutBoundaryPoint(srgb);
    adobeGBP.calculateGamut();
    sGBP.calculateGamut();
    System.out.println(System.currentTimeMillis() - start);

//    Utils.writeObject(adobeGBP, "adobeGBP");
//    Utils.writeObject(sGBP, "sGBP");

//    GamutBoundaryPoint adobeGBP = (GamutBoundaryPoint) Utils.readObject(
//        "adobeGBP");
//    GamutBoundaryPoint sGBP = (GamutBoundaryPoint) Utils.readObject("sGBP");

    ClippingGMA gma = new ClippingGMA(sGBP, FocalPoint.FocalType.None);

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    try {
      for (double x = 0; x <= 360; x += 1) {
        double hue = x % 360.;
        plot.setTitle(String.valueOf(hue));

        List<CIELCh> adobePlane = adobeGBP.getHuePlane(hue);
        List<CIELCh> sPlane = sGBP.getHuePlane(hue);

        int size = adobePlane.size();
        for (int y = 0; y < size; y++) {
          CIELCh LCh = adobePlane.get(y);
//          LCh.h += .5;
          CIELCh map = gma.gamutMapping(LCh);
          plot.addLinePlot(null, new double[][] { {LCh.L, LCh.C}, {map.L, map.C}
          });
        }

        plot.addLinePlot("A", 0, 100, adobeGBP.getHueLCArray(hue));
        plot.addLinePlot("B", 0, 100, sGBP.getHueLCArray(hue));
        plot.setFixedBounds(1, 0, 200);

        Thread.sleep(500);

        plot.removeAllPlots();
      }
      System.out.println("over");
      System.out.println("UncertainBoundaryCount:" +
                         gma.getUncertainBoundaryCount());
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }
}
