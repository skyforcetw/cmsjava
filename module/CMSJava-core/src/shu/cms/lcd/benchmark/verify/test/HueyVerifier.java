package shu.cms.lcd.benchmark.verify.test;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
//import shu.plot.*;

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
public class HueyVerifier {

  public static void main(String[] args) {
    LCDTarget target1 = LCDTarget.Instance.get("eizo_ce240w",
                                               LCDTarget.Source.CA210,
                                               LCDTarget.Room.Dark,
                                               LCDTarget.TargetIlluminant.
                                               Native,
                                               LCDTargetBase.Number.XRite2_0,
                                               LCDTarget.FileType.Logo,
                                               null, null);

    LCDTarget target2 = LCDTarget.Instance.get("eizo_ce240w",
                                               LCDTarget.Source.i1pro,
                                               LCDTarget.Room.Dark,
                                               LCDTarget.TargetIlluminant.
                                               Native,
                                               LCDTargetBase.Number.XRite2_0,
                                               LCDTarget.FileType.Logo,
                                               null, null);

    LCDTarget target3 = LCDTarget.Instance.get("eizo_ce240w",
                                               LCDTarget.Source.Huey,
                                               LCDTarget.Room.Dark,
                                               LCDTarget.TargetIlluminant.
                                               Native,
                                               LCDTargetBase.Number.XRite2_0,
                                               LCDTarget.FileType.Logo,
                                               null, null);

    LCDTarget[] targets = new LCDTarget[] {
        target1, target2, target3};
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    for (LCDTarget target : targets) {
      System.out.println(target.getDescription());
      List<Patch> grayScale = target.filter.grayScalePatch();
      for (Patch p : grayScale) {
        double cct = p.getXYZ().getCCT();
        double duv = CorrelatedColorTemperature.getduvWithBlackbody(p.getXYZ());
        System.out.println(p.getRGB().getValue(RGBBase.Channel.W,
                                               RGB.MaxValue.Int8Bit) + " " +
                           cct + " " + duv);

      }
    }
  }
}
