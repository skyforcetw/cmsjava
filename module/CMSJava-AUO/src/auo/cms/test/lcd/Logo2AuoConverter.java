package auo.cms.test.lcd;

import shu.cms.colorformat.file.AUORampXLSFile;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.independ.*;
import shu.cms.measure.calibrate.FourColorCalibrator;
import shu.cms.Patch;
import shu.cms.colorspace.depend.RGB;

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
public class Logo2AuoConverter {
  public static void main(String[] args) throws Exception {
//    original(args);
    calibrate(args);
  }

  public static void calibrate(String[] args) throws Exception {
    CIExyY w = new CIExyY(0.29903419962744876, 0.32040264814025193,
                          183.8326793200417);
    CIExyY r = new CIExyY(0.5882066282430808, 0.3359730472669514,
                          37.75702827195511);
    CIExyY g = new CIExyY(0.32990467799624323, 0.5484708608404032,
                          109.76090396622727);
    CIExyY b = new CIExyY(0.15175379842231776, 0.13973166946989451,
                          36.80463212396597);
    CIEXYZ[] refRGBW = new CIEXYZ[] {
        w.toXYZ(), r.toXYZ(), g.toXYZ(), b.toXYZ()};
    CIEXYZ w2 = new CIEXYZ(179.6402435, 196.0948639, 236.5884247);
    CIEXYZ r2 = new CIEXYZ(71.82941437, 42.78905869, 8.769180298);
    CIEXYZ g2 = new CIEXYZ(66.22563171, 116.7566223, 22.95518112);
    CIEXYZ b2 = new CIEXYZ(42.03279495, 36.57069397, 206.2040405);
    CIEXYZ[] sampleRGBW = new CIEXYZ[] {
        w2, r2, g2, b2};
    FourColorCalibrator cal = new FourColorCalibrator(refRGBW, sampleRGBW);

//    LCDTarget target = LCDTarget.Instance.getFromLogo("ramp257(6bit).logo");
    LCDTarget target = LCDTarget.Instance.getFromLogo("auo-729.logo");
    cal.calibrate(target);

    for (int x = 0; x < target.size(); x++) {
      Patch p = target.getPatch(x);
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = p.getXYZ();
      System.out.println(p.getName() + " " + rgb.R + " " + rgb.G + " " + rgb.B +
                         " " + XYZ.X + " " + XYZ.Y + " " + XYZ.Z);
    }

//    AUORampXLSFile ramp = new AUORampXLSFile("ramp257(6bit)_cal.xls", target);
//    ramp.save();
  }

  public static void original(String[] args) throws Exception {
    LCDTarget target = LCDTarget.Instance.getFromLogo("ramp257(6bit).logo");
    AUORampXLSFile ramp = new AUORampXLSFile("ramp257(6bit).xls", target);
    ramp.save();
  }
}
