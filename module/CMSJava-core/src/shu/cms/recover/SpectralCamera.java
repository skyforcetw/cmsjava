package shu.cms.recover;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.cms.reference.spectra.*;
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
public class SpectralCamera {
  protected RGB.ColorSpace colorSpace;
  protected Wiener wiener;
  protected IdealDigitalCamera camera;
  protected CIEXYZ white;
  protected double spectraAdjustFactor = -1;
  protected Spectra illuminantSpectra;

  public SpectralCamera(RGB.ColorSpace colorSpace,
                        SpectraDatabase.Content source) {
    this(colorSpace, colorSpace.referenceWhite, source);
    this.colorSpace = colorSpace;
    mode = Mode.RGB;
  }

  private SpectralCamera(RGB.ColorSpace colorSpace, Illuminant illuminant,
                         SpectraDatabase.Content source) {
    illuminantSpectra = illuminant.getSpectra().reduce(source.start,
        source.end, source.interval);
    camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ, illuminant);
    white = new CIEXYZ(camera.captureOriginalOutput(illuminantSpectra));
    //k=5效果最好
    wiener = new Wiener(camera, source, 5, illuminantSpectra);
    if (colorSpace != null) {
      spectraAdjustFactor = 1. /
          getSpectra(new RGB(colorSpace, Color.white)).getXYZ().Y;
    }
    else {
//      spectraAdjustFactor = 1. /
//          getSpectra(white).getXYZ().Y;
      spectraAdjustFactor = 1. / white.Y;
    }

  }

  private static enum Mode {
    RGB, XYZ
  }

  private Mode mode;

  public SpectralCamera(RGB.ColorSpace colorSpace) {
    this(colorSpace, SpectraDatabase.Content.MunsellGlossyPrecise);
  }

  public SpectralCamera(Illuminant illuminant) {
    this(null, illuminant, SpectraDatabase.Content.MunsellGlossyPrecise);
    mode = Mode.XYZ;
  }

  public RGB getRGB(Spectra spectra) {
    CIEXYZ XYZ = new CIEXYZ(camera.captureOriginalOutput(spectra));
    XYZ.normalize(white);
    RGB rgb = new RGB(colorSpace, XYZ);
    return rgb;
  }

  public CIEXYZ getXYZ(Spectra spectra) {
    CIEXYZ XYZ = spectra.getXYZ();
    XYZ.times(spectraAdjustFactor);
    return XYZ;
  }

  public CIEXYZ getCIE1964XYZ(Spectra spectra) {
    CIEXYZ XYZ = spectra.getCIE1964XYZ();
    XYZ.times(spectraAdjustFactor);
    return XYZ;
  }

  public Spectra getSpectra(CIEXYZ XYZ) {
    Spectra spectra = wiener.estimateSpectra(XYZ.getValues());
    return spectra;
  }

  public Spectra getSpectra(RGB rgb) {
    if (mode == Mode.XYZ) {
      throw new UnsupportedOperationException();
    }
    CIEXYZ XYZ = null;
    if (rgb.getRGBColorSpace() != null) {
      XYZ = new CIEXYZ(rgb.toXYZ().getValues());
    }
    else {
      XYZ = new CIEXYZ(RGB.toXYZValues(rgb.getValues(new double[3],
          RGB.MaxValue.Double1), colorSpace));
    }
    XYZ.times(white.Y);
    return getSpectra(XYZ);
  }

  public Spectra getReflectSpectra(RGB rgb) {
    if (mode == Mode.XYZ) {
      throw new UnsupportedOperationException();
    }
    Spectra powerSpectra = getSpectra(rgb);
    return powerSpectra.getSpectralReflectance(illuminantSpectra);
  }

  public Spectra getReflectSpectra(CIEXYZ XYZ) {
    Spectra powerSpectra = getSpectra(XYZ);
    return powerSpectra.getSpectralReflectance(illuminantSpectra);
  }

  public static void main(String[] args) {
//    SpectralCamera camera = new SpectralCamera(RGB.ColorSpace.AdobeRGBD50,
//                                               SpectraDatabase.Content.
//                                               MunsellGlossy);
    SpectralCamera camera = new SpectralCamera(Illuminant.A);
//    RGB rgb = new RGB(RGB.ColorSpace.AdobeRGBD50, new int[] {255, 255, 255});
//    Spectra ref = camera.getReflectSpectra(rgb);
//    Spectra s = camera.getSpectra(rgb);
    Spectra ref = camera.getReflectSpectra(camera.getWhite());
    Spectra s = camera.getSpectra(camera.getWhite());
    Plot2D p = Plot2D.getInstance();
    p.setVisible();
    p.addLegend();
    p.addSpectra("ref", ref);
    p.addSpectra("s", s);
  }

  public CIEXYZ getWhite() {
    return white;
  }
}
