package shu.cms.devicemodel.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.DCModel.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;

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
public class SpectraBasedModel
    extends DCModel {

  protected IdealDigitalCamera camera;
  protected SpectraEstimator estimator;
  /**
   * 秸俱玒计
   */
  protected double scaleFactor = 1.;
  protected Spectra illuminant;
  protected boolean byWiener = false;

  public SpectraBasedModel(DCTarget dcTarget, IdealDigitalCamera camera) {
    this(dcTarget, camera, false, null);
  }

  public SpectraBasedModel(DCTarget dcTarget, IdealDigitalCamera camera,
                           boolean byWiener, Spectra illuminant) {
    super(dcTarget, false, false);
    this.camera = camera;
    this.byWiener = byWiener;
    this.illuminant = illuminant;
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @return RGB
   * @todo M _getRGB
   */
  protected RGB _getRGB(CIEXYZ XYZ) {
    return null;
  }

  /**
   * 璸衡XYZ
   *
   * @param rgb RGB
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb) {
    Spectra s = estimator.estimateSpectra(rgb.getValues());
    CIEXYZ XYZ = s.getXYZ();
    XYZ.times(scaleFactor);
    return XYZ;
  }

  /**
   * ―玒计
   *
   * @return Factor[]
   * @todo M 蹦ノscaleFactorぃì,常硑ΘY箇代筁
   */
  protected Factor _produceFactor() {
    if (byWiener) {
      estimator = new Wiener(camera, SpectraDatabase.Content.MunsellGlossy, 3,
                             illuminant);
    }
    else {
      estimator = new PseudoInverse(camera,
                                    SpectraDatabase.Content.MunsellGlossy, 3,
                                    illuminant);
    }

    Patch brightestPatch = this.dcTarget.getBrightestPatch();
    CIEXYZ brightestXYZ = brightestPatch.getXYZ();
    CIEXYZ estimatedXYZ = getXYZ(brightestPatch.getRGB());
    scaleFactor = brightestXYZ.Y / estimatedXYZ.Y;
    return new Factor();
  }

  public static class Factor
      extends DCModel.Factor {

  }

  public String getDescription() {
    return "SpectraBased";
  }

  public static void main(String[] args) {

    LightSource.i1Pro lightSource = LightSource.i1Pro.D50;
    Illuminant illuminant = LightSource.getIlluminant(lightSource);
    Spectra spectra = illuminant.getSpectra();

    DCTarget target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource, 1.,
                                            DCTarget.Chart.CCSG);
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.EstimatedD200_1);
//
//    SpectraBasedModel model = new SpectraBasedModel(target, camera);
    SpectraBasedModel model = new SpectraBasedModel(target, camera, true,
        spectra);
    model.produceFactor();

    DeltaEReport[] testReports = model.testTarget(target, false);

    System.out.println("Training: " + target.getDescription());
    System.out.println(Arrays.toString(testReports));

  }

}
