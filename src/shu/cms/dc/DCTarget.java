package shu.cms.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.ideal.*;

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
public class DCTarget
    extends DCTargetBase {

  protected Chart chart;
  protected TargetData targetData;
  protected Illuminant illuminant;

  public Chart getType() {
    return chart;
  }

  public static void main(String[] args) {

    String dirname = "Measurement Files/camera/htc legend";
    String[] lightsources = new String[] {
        "日光", "陰天", "螢光燈", "鎢絲燈"};
    int size = lightsources.length;
    DCTarget[] targets = new DCTarget[size];
    for (int x = 0; x < size; x++) {
      targets[x] = DCTarget.Instance.get(LightSource.CIE.F8, 1,
                                         Chart.MiniCC24,
                                         DCTarget.FileType.ICC,
                                         dirname + "/" + lightsources[x] +
                                         ".icc");
      List<Patch> grayScale = targets[x].filter.grayScale();
      for (Patch p : grayScale) {
        System.out.println(p.getRGB() + " " + p.getXYZ());
      }
      System.out.println("");
    }

  }

  protected DCTarget(List<Spectra> spectraPowerList,
      List<Spectra> reflectSpectraList, Illuminant illuminant,
      IdealDigitalCamera camera) {
    this(spectraPowerList, reflectSpectraList,
         camera.produceRGBList(spectraPowerList), illuminant);
  }

  /**
   * 所有的色塊會依照光源的的亮度作正規化
   * @param patchList List
   * @param illuminant Illuminant
   */
  protected DCTarget(List<Patch> patchList, Illuminant illuminant) {
    super(patchList);
    this.illuminant = illuminant;
    //luminance以光源為白比較適當, 要是以色塊白當做光源, 那當拍到比色塊白還要亮的影像, 可能會被clip
    this.luminance = illuminant.getSpectra().getXYZ();
    calculateNormalizedXYZ();
  }

  protected DCTarget(List<Spectra> spectraList,
      List<Spectra> reflectSpectraList,
      List<RGB> rgbList, Illuminant illuminant) {
    this(Patch.Produce.XYZRGBPatches(spectraList, reflectSpectraList,
                                     rgbList, defaultCMF), illuminant);
  }

  public Illuminant getIlluminant() {
    return illuminant;
  }

  public Filter filter = new Filter();
  public final class Filter {
    /**
     * 取環繞在導表四周的灰階以外的色塊
     * 是為了製作Profile使用.
     * @return List
     */
    public List<Patch> patchListWithoutSurroundingGrayScale() {
      if (chart != Chart.CCSG && chart != Chart.CCDC) {
        throw new IllegalStateException(
            "There is no gray scale surround target.");
      }
      int lgorowLength = targetData.lgorowLength;
      List<Patch>
          centerPatchList = patchList.subList(0,
                                              patchList.size() - lgorowLength);
      centerPatchList = centerPatchList.subList(lgorowLength,
                                                centerPatchList.size());

      List<Patch> patchListWithoutSurroundingGrayScale = new ArrayList<Patch> ();

      int size = centerPatchList.size();
      int bottomGrayIndex = lgorowLength - 1;
      for (int x = 0; x < size; x++) {
        if (x % lgorowLength == 0 || x % lgorowLength == bottomGrayIndex) {
          continue;
        }

        Patch p = centerPatchList.get(x);
        patchListWithoutSurroundingGrayScale.add(p);
      }
      return patchListWithoutSurroundingGrayScale;
    }

    public List<Patch> getCC24LabPatchListFromCCSG() {
      if (chart != Chart.CCSG) {
        throw new IllegalStateException("type != Chart.CCSG");
      }
      List<Patch> cc24 = new ArrayList<Patch> (24);
      List<Patch> ccsg = getLabPatchList();
      int[] CC24InCCSGIndex = TargetData.CC24InCCSGIndex;
      for (int x = 0; x < CC24InCCSGIndex.length; x++) {
        int index = CC24InCCSGIndex[x];
        cc24.add(ccsg.get(index));
      }
      return cc24;
    }

    /**
     * 取得可以用來製作Profile的導表
     * (通常來說,CCDC和CCSG是去掉周圍的灰階色塊,其他導具的話則是直接將所有色塊回傳)
     * @return List
     */
    public List<Patch> patchListForProfile() {
      switch (chart) {
        case CCSG:
        case CCDC:
          return patchListWithoutSurroundingGrayScale();

        default:
          return getPatchList();
      }
    }

    public List<Patch> patchListForIT8Profile() {
      if (chart != Chart.IT8) {
        throw new IllegalStateException(
            "chart != Chart.IT8");
      }
      return null;
    }

    public List<Patch> subTarget() {
      if (subTarget == null) {
        if (targetData.subTargetEnd != 0) {
          int start = targetData.subTargetStart;
          int end = targetData.subTargetEnd;
          int size = end - start + 1;
          subTarget = new ArrayList<Patch> (size);
          for (int x = start; x < end + 1; x++) {
            subTarget.add(patchList.get(x));
          }
        }
      }
      return subTarget;
    }

    private List<Patch> subTarget = null;
    private List<Patch> grayScale = null;

    /**
     * 取得灰階導表
     * @return List
     */
    public List<Patch> grayScale() {
      if (grayScale == null) {
        int[] grayIndex = targetData.grayScaleIndex;
        grayScale = new ArrayList<Patch> (grayIndex.length);
        for (int index : grayIndex) {
          grayScale.add(patchList.get(index));
        }
      }

      return grayScale;
    }
  }

  public int[] getGrayScaleIndex() {
    return targetData.grayScaleIndex;
  }

  public Validate validate = new Validate();

  public class Validate {
    /**
     *
     * @return boolean
     * @todo L icc 均勻度驗證
     * 白色色塊的RGB值為210-245
     * 避免過曝
     * 黑色色塊要小於23
     * 中間和周邊的白色色塊差距需小於12
     * 周圍的色塊差距需小於15
     */
    public boolean validateLightUniform() {
      List<Patch> surrounding = getSurroundingGrayScale();

      return false;
    }

    /**
     * 全反射警告
     * 會判斷色塊是否有三頻道皆等於255
     * @return boolean
     */
    public boolean totalInternalReflectionWarning() {
      for (Patch p : patchList) {
        if (p.getRGB().isWhite()) {
          return true;
        }
      }
      return false;
    }

    /**
     * 是否有色塊已經有頻道飽和(有頻道達到最大值)
     * @return boolean
     */
    public boolean validateChannelSaturation() {
      for (Patch p : patchList) {
        if (p.getRGB().hasChannelSaturation()) {
          return true;
        }
      }
      return false;

    }

    /**
     * 驗證GrayScale是否順暢沒有反轉
     * @return boolean
     */
    public boolean validateGrayScale() {
      List<Patch> grayScale = filter.grayScale();
      double r = 0, g = 0, b = 0;
      double[] values = new double[3];
      for (Patch p : grayScale) {
        p.getRGB().getValues(values);
        if (values[0] < r || values[1] < g || values[2] < b) {
          return false;
        }
        else {
          r = values[0];
          g = values[1];
          b = values[2];
        }
      }
      return true;
    }

  }

  protected List<Patch> getSurroundingGrayScale() {
    List<Patch> surrounding = new ArrayList<Patch> (patchList);
    surrounding.removeAll(filter.patchListWithoutSurroundingGrayScale());
    return surrounding;
  }

  /**
   * 轉成Lab的Patch List
   * @return List
   */
  public List<Patch> getLabPatchList() {
    CIEXYZ whitePoint = this.luminance;
    List<Patch>
        labPatchList = Patch.Produce.LabPatches(patchList, whitePoint);
    return labPatchList;
  }

  /**
   * 將RGB正規化
   * 因為IdealDigitalCamera的RGB範圍不滿足0~1,因此找尋到R/G/B各自的maximum,使所有RGB正規化.
   * @param camera IdealDigitalCamera
   */
  protected void normalizeIdealDigitalCameraRGB(IdealDigitalCamera camera) {
    Patch firsPatch = patchList.get(0);
    int start = firsPatch.getSpectra().getStart();
    int interval = firsPatch.getSpectra().getInterval();
    int end = firsPatch.getSpectra().getEnd();
    Spectra lightSource = illuminant.getSpectra().reduce(start, end,
        interval);
    double[] whiteValues = camera.capture(lightSource);
    double[] rgbValues = new double[3];

    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      p.getRGB().getValues(rgbValues);
      rgbValues = normalize(rgbValues, whiteValues);
      p.getRGB().setValues(rgbValues);
    }

  }

  /**
   * 將導表的rgb以normal進行正規化
   * @param normal double[]
   */
  public void normalizeRGB(double[] normal) {
    double[] rgbValues = new double[3];
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();
      rgb.getValues(rgbValues);
      normalize(rgbValues, normal);
      rgb.setValues(rgbValues);
      rgb.rationalize();
    }
  }

  protected final static double[] normalize(double[] original, double[] normal) {
    int size = original.length;
    for (int x = 0; x < size; x++) {
      original[x] /= normal[x];
    }
    return original;
  }

  protected Patch brightestPatch;

  /**
   *  取得導具中最亮的色塊
   * @return Patch
   */
  public Patch getBrightestPatch() {
    if (brightestPatch == null) {
      int size = patchList.size();
      double maxY = 0;

      for (int x = 0; x < size; x++) {
        Patch p = patchList.get(x);
        double Y = p.getXYZ().Y;
        if (Y > maxY) {
          maxY = Y;
          brightestPatch = p;
        }
      }
    }
    return brightestPatch;
  }

  public shu.cms.dc.DCTarget.Chart getChart() {
    return chart;
  }

  public shu.cms.dc.DCTarget.TargetData getTargetData() {
    return targetData;
  }

}
