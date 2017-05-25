package shu.cms.measure.intensity;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.independ.*;
import shu.cms.measure.*;
import shu.math.array.*;

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
public class MaxMatrixIntensityAnayzer
    implements IntensityAnalyzerIF {
  public MaxMatrixIntensityAnayzer(MeterMeasurement mm) {
    this.mm = mm;
  }

  private MeterMeasurement mm;
  private CIEXYZ XYZ;
  private CIEXYZ rXYZ, gXYZ, bXYZ, wXYZ;
  private double[][] inverseMatrix;
  private double[] targetRatio;
  private double[] rgbValues;

  /**
   * getIntensity
   *
   * @param rgb RGB
   * @return RGB
   */
  public RGB getIntensity(RGB rgb) {
    //component: 0~100%
    Patch p = mm.measure(rgb, rgb.toString());
    XYZ = p.getXYZ();

    return getIntensity(XYZ);
  }

  public RGB getIntensity(CIEXYZ XYZ) {
    rgbValues = DoubleArray.times(inverseMatrix, XYZ.getValues());
    DoubleArray.times(rgbValues, 100);
    double[] intensityValues = new double[3];
    intensityValues[0] = rgbValues[0] / targetRatio[0];
    intensityValues[1] = rgbValues[1] / targetRatio[1];
    intensityValues[2] = rgbValues[2] / targetRatio[2];
    RGB intensity = new RGB(RGB.ColorSpace.unknowRGB, intensityValues);
    return intensity;
    /*double2D_ptr color =
        DoubleArray::toDouble2D(1, 3, XYZ - > X, XYZ - > Y, XYZ - > Z);
         rgbValues = DoubleArray::times(inverseMatrix, color);
         ( * rgbValues)[0][0] *= 100;
         ( * rgbValues)[1][0] *= 100;
         ( * rgbValues)[2][0] *= 100;
         double_array intensityValues(new double[3]);

         intensityValues[0] = ( * rgbValues)[0][0] / ( * targetRatio)[0][0];
         intensityValues[1] = ( * rgbValues)[1][0] / ( * targetRatio)[1][0];
         intensityValues[2] = ( * rgbValues)[2][0] / ( * targetRatio)[2][0];

         RGB_ptr intensity(new
                      RGBColor(intensityValues[0],
                               intensityValues[1],
                               intensityValues[2]));
         return intensity;*/
  };

  /**
   * getCIEXYZ
   *
   * @return CIEXYZ
   */
  public CIEXYZ getCIEXYZ() {
    return XYZ;
  }

  /**
   * setupComponent
   *
   * @param ch Channel
   * @param rgb RGB
   */
  public void setupComponent(Channel ch, RGB rgb) {
    Patch p = mm.measure(rgb, rgb.toString());
    CIEXYZ measureXYZ = p.getXYZ();
    setupComponent(ch, measureXYZ);
  }

  public void setupComponent(Channel ch,
                             CIEXYZ measureXYZ) {
    switch (ch) {
      case R:
        rXYZ = measureXYZ;
        break;
      case G:
        gXYZ = measureXYZ;
        break;
      case B:
        bXYZ = measureXYZ;
        break;
      case W:
        wXYZ = measureXYZ;
        break;
    }
    ;
  };
  /**
   * enter
   *
   */
  public void enter() {
    mm.setMeasureWindowsVisible(false);
    if (rXYZ == null || gXYZ == null || bXYZ == null || wXYZ == null) {
      throw new IllegalStateException(
          "Excute setupComponent() with RGBW first.");
    }
    double[][] m = new double[][] {
        rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
    m = DoubleArray.transpose(m);
    inverseMatrix = DoubleArray.inverse(m);
    targetRatio = DoubleArray.times(inverseMatrix, wXYZ.getValues());
//    DoubleArray.times(inverseMatrix,wXYZ.getv)
    /*mm->setMeasureWindowsVisible(false);
         if (rXYZ == null || gXYZ == null || bXYZ == null
        || wXYZ == null) {
        throw IllegalStateException
            ("Excute setupComponent() with RGBW first.");
         }
         double2D_ptr m =
        DoubleArray::toDouble2D(3, 9, rXYZ->X, gXYZ->X, bXYZ->X,
                                rXYZ->Y, gXYZ->Y, bXYZ->Y,
                                rXYZ->Z, gXYZ->Z, bXYZ->Z);

         this->inverseMatrix = DoubleArray::inverse(m);

         double2D_ptr targetWhite =
        DoubleArray::toDouble2D(1, 3, wXYZ->X, wXYZ->Y, wXYZ->Z);
         this->targetRatio =
        DoubleArray::times(inverseMatrix, targetWhite);
         double rR = (*targetRatio)[0][0];
         double rG = (*targetRatio)[1][0];
     double rB = (*targetRatio)[2][0];*/
  }

  /**
   * beginAnalyze
   *
   */
  public void beginAnalyze() {
    mm.setMeasureWindowsVisible(true);
  }

  /**
   * endAnalyze
   *
   */
  public void endAnalyze() {
    mm.setMeasureWindowsVisible(false);
  }

  /**
   * getReferenceColor
   *
   * @return CIEXYZ
   */
  public CIEXYZ getReferenceColor() {
    return XYZ;
  }

}
