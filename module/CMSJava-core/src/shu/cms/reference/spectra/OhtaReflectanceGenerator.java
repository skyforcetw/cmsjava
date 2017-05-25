package shu.cms.reference.spectra;

import java.util.*;

import quickhull3d.*;
import shu.cms.*;
import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
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
public class OhtaReflectanceGenerator {
  private int start;
  private int end;
  private int interval;
  private Random random;

  public OhtaReflectanceGenerator(int start, int end, int interval) {
    this.start = start;
    this.end = end;
    this.interval = interval;
//    this.random = new Random(1);
    this.random = new Random();
  }

  protected final double getNextRho(double rho0, double rho1,
                                    double delta) {
    delta = random.nextBoolean() ? delta : -delta;
    double rho = 2 * (rho1 + delta) - rho0;
    rho = checkReflectance(rho, rho1);
    return rho;
  }

  protected final static double Delta = 0.03;
  protected double getDelta() {
    double delta = random.nextDouble() * Delta;
    delta = random.nextBoolean() ? delta : -delta;
//    double delta = random.nextDouble() * Delta*2;
//    delta -= Delta;
    return delta;
  }

  protected double checkReflectance(double r, double rho1) {

    if (r < 0 || r > 1) {
      double delta = (r - rho1) * random.nextDouble() * random.nextDouble();
      return rho1 - delta;
    }
//    r = r > 1 ? 1 : r;
//    r = r < 0 ? 0 : r;
    return r;
  }

  public static boolean isPassHypothesis(Spectra s) {
    double[] data = s.getData();
    int size = data.length;
    for (int x = 1; x < size - 1; x++) {
      double delta = (data[x + 1] - data[x - 1]) / 2 - data[x];
      if (delta > Delta) {
        return false;
      }
    }
    return true;
  }

  public Spectra generate() {
//    random = new Random(System.nanoTime());
    int size = (end - start) / interval + 1;
    double[] data = new double[size];
    data[0] = random.nextDouble();
    double delta = getDelta();
    data[1] = getNextRho(data[0], data[0], delta);

    for (int x = 2; x < size; x++) {
      delta = getDelta();
      double r = getNextRho(data[x - 2], data[x - 1], delta);
      data[x] = r;
    }
    Spectra s = new Spectra("", Spectra.SpectrumType.REFLECTANCE, start, end,
                            interval, data);
    return s;
  }

  protected final static RGB2ColorSpaceTransfer CIELabRGBCST = new
      RGB2ColorSpaceTransfer() {

    private RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    private CIEXYZ white = Illuminant.D65.getSpectra().getXYZ();

    public ColorSpace _getColorSpace(RGB rgb) {
      throw new UnsupportedOperationException();
    }

    public RGB getRGB(double[] colorspaceValues) {
      CIELab Lab = new CIELab(colorspaceValues, white);
      CIEXYZ XYZ = Lab.toXYZ();
      XYZ.normalizeWhite();
      RGB rgb = new RGB(colorspace, XYZ);
      rgb.rationalize();
      return rgb;
    }
  };

  public static void main(String[] args) {

    OhtaReflectanceGenerator g = new OhtaReflectanceGenerator(400, 700, 10);
    CIEXYZ white = Illuminant.D65.getSpectra().getXYZ();
    int size = 1000000;
//    int size = 100;
    Point3d[] vertices = new Point3d[size];
    Plot3D p = Plot3D.getInstance();
    Spectra ill = Illuminant.D65.getSpectra().fillAndInterpolate(400, 700, 10);

    for (int x = 0; x < size; x++) {
      Spectra s = g.generate();
      CIEXYZ XYZ = s.getXYZ(ill);
      CIELab Lab = new CIELab(XYZ, white);
      vertices[x] = new Point3d(Lab.a, Lab.b, Lab.L);
    }
    QuickHull3D q = new QuickHull3D(vertices);
    q.triangulate();
    p.addQuickHull3D(CIELabRGBCST, q, .3f);
    p.setVisible();
  }
}
