package shu.cms.profile.test;

import java.util.List;

import java.awt.*;

import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.gma.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.cms.gma.gbp.*;

//import shu.plot.*;

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
public class ProfileMakerTester {
  public static void main(String[] args) {

//    LCDModel lcdModel = new SCurveModel1Thread("scurve1.factor");
    LCDModel lcdModel = new LCDPolynomialRegressionModel("polynomial.factor");
    LCDProfileMaker maker = new LCDProfileMaker();
    Profile p = maker.makeXYZProfile(lcdModel,
                                     LCDProfileMaker.LCDProfileType.MatrixLut);

    ColorSpaceConnectedLUT AToB1 = p.getAToB(RenderingIntent.
                                             RelativeColorimetric);

    CLUTRegressionReverseModel reverseModel = new CLUTRegressionReverseModel(
        AToB1,
        Polynomial.COEF_3.BY_3, 32768. / 65535.);
    reverseModel.produceFactor();
    ProfileColorSpace reversePCS = ProfileColorSpace.Instance.get(
        reverseModel, "");

    ProfileColorSpace lcdPCS = ProfileColorSpace.Instance.get(
        lcdModel, "");

//    ProfileColorSpace srgb = ProfileColorSpace.getInstance(RGB.RGBColorSpace.
//        sRGB);
//    GamutBoundaryPoint sGBP = new GamutBoundaryPoint(srgb);
//    sGBP.calculateGamutByLCh();
//    Utils.writeObject(sGBP, "sGBP");
//    GamutBoundaryPoint sGBP = (GamutBoundaryPoint) Utils.readObject("sGBP");

    GamutBoundaryPoint lcdGBP = new GamutBoundaryPoint(lcdPCS);
    lcdGBP.calculateGamut();

    GamutBoundaryPoint reverseGBP = new GamutBoundaryPoint(reversePCS);
//    lcdGBP.calculateGamutByRGB();
    reverseGBP.calculateGamut();

    ClippingGMA gma = new ClippingGMA(lcdGBP,
                                      FocalPoint.FocalType.MultiByKMeans);

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    try {
      for (double x = 0; x <= 360; x += .5) {
//      for (int x = 351; x <= 351; x += 1) {
        double hue = x % 360;
        plot.setTitle(String.valueOf(hue));

        List<CIELCh> LCDPlane = reverseGBP.getHuePlane(hue);
//        List<CIELCh> sPlane = sGBP.getHuePlane(hue);

        int size = LCDPlane.size();
        for (int y = 0; y < size; y++) {
          CIELCh LCh = LCDPlane.get(y);
          CIELCh map = gma.gamutMapping(LCh);
          plot.addLinePlot(null, new double[][] { {LCh.L, LCh.C}, {map.L, map.C}
          });
        }

        plot.addLinePlot("A", Color.red, 0, 100, reverseGBP.getHueLCArray(hue));
        plot.addLinePlot("B", Color.green, 0, 100, lcdGBP.getHueLCArray(hue));
        plot.setFixedBounds(1, 0, 200);

        Thread.sleep(30);

        plot.removeAllPlots();
      }
//      System.out.println("over");
      System.out.println(gma.getUncertainBoundaryCount());
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }
}
