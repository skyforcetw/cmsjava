package auo.cms.hsv.autotune;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 以clip程度當作index
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ClipIndex
    implements Index {
  private CIEXYZ[] originalXYZArray;
  private CIEXYZ[] alteredXYZArray;
  private double[] originalDeltaEArray;
  private double[] alteredDeltaEArray;

  public double[] getOriginalDeltaEArray() {
    return originalDeltaEArray;
  }

  public double[] getAlteredDeltaEArray() {
    return alteredDeltaEArray;
  }

  private CIEXYZ referenceWhite;
  public ClipIndex(CIEXYZ referenceWhite) {
    this.referenceWhite = referenceWhite;
    ciecam02 = new CIECAM02(ViewingConditions.
                            getTypicalViewingConditions(
                                referenceWhite));

  }

  private CIECAM02 ciecam02;

  private double[] getDeltaEArray(CIEXYZ[] XYZArray) {
    int size = XYZArray.length;
    double[] deltaEArray = new double[size - 1];
    for (int x = 0; x < size - 1; x++) {
      CIECAM02Color JCh0 = ciecam02.forward(XYZArray[x]);
      CIECAM02Color JCh1 = ciecam02.forward(XYZArray[x + 1]);
      CIECAM02DeltaE de = new CIECAM02DeltaE(JCh0, JCh1,
                                             CIECAM02DeltaE.Style.SCD);
      deltaEArray[x] = de.getCIECAM02DeltaE();
    }
    return deltaEArray;
  }

  public void setOriginalXYZArray(CIEXYZ[] XYZArray) {
    this.originalXYZArray = XYZArray;
    originalDeltaEArray = getDeltaEArray(XYZArray);
  }

  private int fromIndex = 200;
  public void setFromIndex(int index) {
    this.fromIndex = fromIndex;
  }

  public void setAlteredXYZArray(CIEXYZ[] XYZArray) {
    this.alteredXYZArray = XYZArray;
    alteredDeltaEArray = getDeltaEArray(XYZArray);
  }

  /**
   * getIndex
   *
   * @return double
   */
  public double getIndex() {
    int biggerDifferenceCount = 0;
    int size = alteredDeltaEArray.length;
    for (int x = fromIndex; x < size; x++) {
      biggerDifferenceCount += (alteredDeltaEArray[x] < originalDeltaEArray[x]) ?
          1 : 0;
    }
    double index = ( (double) biggerDifferenceCount) / (size - fromIndex);
    return index;
  }
}
