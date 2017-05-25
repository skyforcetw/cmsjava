package auo.cms.hsv.autotune;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 以色差當作index
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ColorIndex
    implements Index {
  private LCDModel lcdModel;
  private CIEXYZ modelWhiteXYZ;
  public ColorIndex(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
    modelWhiteXYZ = lcdModel.getWhiteXYZ();
  }

  private CIEXYZ[] targetXYZArray;
  private HSV[] tuneSpots;
  private CIEXYZ targetWhiteXYZ;
  public void setTargetWhiteXYZ(CIEXYZ targetWhiteXYZ) {
    this.targetWhiteXYZ = targetWhiteXYZ;
  }

  public void setTargetCIEXYZ(CIEXYZ[] targetXYZArray) {
    this.targetXYZArray = targetXYZArray;
  }

  public void setTuneSpots(HSV[] tuneSpots) {
    this.tuneSpots = tuneSpots;
  }

  /**
   * getIndex
   *
   * @return double
   */
  public double getIndex() {
    if (targetXYZArray.length != tuneSpots.length) {
      throw new IllegalStateException("");
    }

    int size = tuneSpots.length;
    double totalDeltaE00 = 0;

    for (int x = 0; x < size; x++) {
      HSV hsv = tuneSpots[x];
      RGB rgb = hsv.toRGB();
      rgb.changeMaxValue(RGB.MaxValue.Double255);

      CIEXYZ tuneXYZ = lcdModel.getXYZ(rgb, false);
      CIEXYZ targetXYZ = targetXYZArray[x];

      CIELab tuneLab = new CIELab(tuneXYZ, modelWhiteXYZ);
      CIELab targetLab = new CIELab(targetXYZ, targetWhiteXYZ);
      DeltaE deltaE = new DeltaE(targetLab, tuneLab);
      double de00 = (Klch == null) ? deltaE.getCIE2000DeltaE() :
          deltaE.getCIE2000DeltaE(Klch[0], Klch[1], Klch[2]);

      totalDeltaE00 += de00;
    }

    double index = totalDeltaE00 / size;
    return index;
  }

  private double[] Klch = null;
  public void setKlch(double Kl, double Kc, double Kh) {
    Klch = new double[] {
        Kl, Kc, Kh};
  }

}
