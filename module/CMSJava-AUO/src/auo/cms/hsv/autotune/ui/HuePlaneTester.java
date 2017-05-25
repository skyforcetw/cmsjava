package auo.cms.hsv.autotune.ui;

import java.beans.*;

import auo.cms.hsv.autotune.*;
import auo.cms.hsv.autotune.test.*;
import auo.cms.hsv.saturation.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.plot.*;
import shu.cms.profile.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class HuePlaneTester {

  public static void main(String[] args) {
    TuneTarget tuneTarget = PreferredColorTuneTargetTest.getTestTuneTarget(false);
    IntegerSaturationFormula integerSaturationFormula = new
        IntegerSaturationFormula( (byte) 7, 4);

    MultiMatrixModel model = new MultiMatrixModel(PreferredColorTuneTargetTest.
                                                  RampTarget);
    model.produceFactor();
    ProfileColorSpace lcdpcs = ProfileColorSpace.Instance.get(model, "");
//    ProfileColorSpaceModel pcsModel = new ProfileColorSpaceModel(RGB.ColorSpace.
//        sRGB_gamma22);
    ProfileColorSpace tvpcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFrom729Target(PreferredColorTuneTargetTest.
                                          PreferredTarget);

//    ProfileColorSpaceModel pcsModel = new ProfileColorSpaceModel(tvpcs);
    ProfileColorSpaceModel pcsModel = new ProfileColorSpaceModel(lcdpcs);
    pcsModel.produceFactor();

    SingleHueAdjustValue singleHueAdjustValue = new SingleHueAdjustValue( (short)
        0, (byte) 0, (byte) 0);
    final HuePlanePlotter inspector = new HuePlanePlotter(pcsModel,
        singleHueAdjustValue,
        tuneTarget, integerSaturationFormula);
//    for (int hue = 0; hue < 15; hue += 15) {
    final Plot3D plot = Plot3D.getInstance();
//    final int hue = 0;
//    inspector.plotModelHuePlane(hue, plot, "", HSV.getLineColor(hue));
    inspector.plotHSVIPHuePlane(0, plot, "", HSV.getLineColor(0));
    plot.setVisible();

    final HSVAdjustFrame frame1 = new HSVAdjustFrame();
    frame1.setVisible(true);
    frame1.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        int h = frame1.jSlider1.getValue();
        int s = frame1.jSlider2.getValue();
        int v = frame1.jSlider3.getValue();
        int hue = frame1.jSlider4.getValue();
        SingleHueAdjustValue singleHueAdjustValue = new SingleHueAdjustValue( (short)
            h, (byte) s, (byte) v);
        inspector.hsvAdjustValue = singleHueAdjustValue;
        plot.removeAllPlots();

        inspector.plotHSVIPHuePlane(hue, plot, "",
                                    HSV.getLineColor( (int) (h / 768. * 360)));
      }
    });
  }
}
