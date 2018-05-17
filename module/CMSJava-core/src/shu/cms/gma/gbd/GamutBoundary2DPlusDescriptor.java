package shu.cms.gma.gbd;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.cms.gma.gbp.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來描述色立體上的色域邊界
 * 是以GamutBoundary2DDescriptor加權平均而得.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GamutBoundary2DPlusDescriptor
    extends GamutBoundary2DDescriptor {

  public GamutBoundary2DPlusDescriptor(GamutBoundaryPoint gbp,
                                       FocalPoint.FocalType focalType) {
    super(gbp, focalType);
  }

  public CIELCh getBoundaryLCh(CIELCh LCh) {
    double hue = LCh.h;

    double[] nearestHue = gbp.getNearestHue(LCh.h);
    CIELCh leftLCh = (CIELCh) LCh.clone();
    leftLCh.h = nearestHue[0];
    CIELCh rightLCh = (CIELCh) LCh.clone();
    rightLCh.h = nearestHue[1];

    leftLCh = super.getBoundaryLCh(leftLCh);
    rightLCh = super.getBoundaryLCh(rightLCh);

    CIELCh result = (CIELCh) LCh.clone();
    double[] weight = fp.getWeight(nearestHue[0], hue, nearestHue[1]);
    result.L = fp.getWeightedValue(weight, leftLCh.L, rightLCh.L);
    result.C = fp.getWeightedValue(weight, leftLCh.C, rightLCh.C);

    return result;
  }

  public static void main(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    GamutBoundaryPoint gbp = new GamutBoundaryPoint(pcs);
    gbp.calculateGamut();
    GamutBoundary2DPlusDescriptor gbd = new GamutBoundary2DPlusDescriptor(gbp,
        FocalPoint.FocalType.None);
    CIELCh LCh = new CIELCh();

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    try {

      int HLevel = gbp.HLevel;
      int LLevel = gbp.LLevel;
      double lStep = gbp.getLightnessStep();
      double hueStep = gbp.getHueStep();
//      double[][] rgbLabBoundary = gbp.getRGBLabBoundary();

      for (double l = 88; l <= 88; l += 0.5) {
        plot.setTitle(String.valueOf(l));

        /*for (int h = 0; h < HLevel; h++) {
          int index = l + h * LLevel;
          double[] rgbLab = rgbLabBoundary[index];
          plot.addScatterPlot(null, Color.red, new double[][] { {rgbLab[4]},
                              {rgbLab[5]}
          });
                 }*/

        for (double h = 0; h <= 360; h += 0.1) {
          double lightness = l; //* lStep;
          double hue = h;
          LCh.L = lightness;
          LCh.h = hue;
          LCh.C = 200;
          CIELCh boundary = gbd.getBoundaryLCh(LCh);
          CIELab Lab = new CIELab(boundary);
          plot.addScatterPlot(null, Color.green, new double[][] { {Lab.a},
                              {Lab.b}
          });
        }

        plot.setFixedBounds(0, -128, 127);
        plot.setFixedBounds(1, -128, 127);
        Thread.sleep(1000);
        plot.removeAllPlots();
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }

}
