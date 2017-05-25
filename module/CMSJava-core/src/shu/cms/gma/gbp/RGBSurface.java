package shu.cms.gma.gbp;

import java.util.List;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.CIEXYZ;

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
public class RGBSurface
    extends Boundary {
  /**
   * RGBSurface
   *
   * @param parent GamutBoundaryPoint
   */
  public RGBSurface(GamutBoundaryPoint parent) {
    super(parent);
  }

  /**
   * calculate
   *
   */
  protected void calculate() {
    double[] rgbValues = new double[3];
    List<RGB> surfaceRGBList = LCDTargetBase.SurfaceTarget.getSurface(5);
    CIEXYZ referenceWhite = parent.profileColorSpace.getD65ReferenceWhite();

    for (RGB rgb : surfaceRGBList) {
      rgbValues[0] = (double) rgb.R / 255.;
      rgbValues[1] = (double) rgb.G / 255.;
      rgbValues[2] = (double) rgb.B / 255.;
//      if (rgb.isWhite()) {
//        int x = 1;
//      }
      double[] LChValues = getLChValues(rgbValues, referenceWhite);
      setBoundaryLChValues(rgb, LChValues);
    }
  }

  protected double getMinLightness() {
    return minLightness;
  }

  protected double getMaxLightness() {
    return maxLightness;
  }

  protected double minLightness = Double.MAX_VALUE;
  protected double maxLightness = Double.MIN_VALUE;
  protected void setBoundaryLChValues(RGB rgb, double[] LChValues) {
    int[] LIndices = null;
    if (parent.useNearestLightnessIndex) {
      //計算以最接近的Lightnes進行
      LIndices = getNearestLightnessIndex(LChValues[0]);
    }
    else {
      LIndices = new int[] {
          getLightnessIndex(LChValues[0])};
    }

    int HIndex = parent.getHueIndex(LChValues[2]);
//代表轉了一圈,所以要歸零
    HIndex = (HIndex == HLevel) ? 0 : HIndex;

    //可以做到更新兩個index
    for (int x = 0; x < LIndices.length; x++) {
      int LIndex = LIndices[x];
      //如果C更大,就更新boundary
      if (LChValues[1] > boundaryHLCArray[HIndex][LIndex]) {
        boundaryHLCArray[HIndex][LIndex] = LChValues[1];
        boundaryRGBArray[HIndex][LIndex][0] = (short) rgb.R;
        boundaryRGBArray[HIndex][LIndex][1] = (short) rgb.G;
        boundaryRGBArray[HIndex][LIndex][2] = (short) rgb.B;
      }
      amountArray[HIndex][LIndex]++;

    }

    maxLightness = LChValues[0] > maxLightness ? LChValues[0] :
        maxLightness;
    minLightness = LChValues[0] < minLightness ? LChValues[0] :
        minLightness;

  }

}
