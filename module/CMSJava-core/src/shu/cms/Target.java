package shu.cms;

import java.io.*;
import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 代表導具
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class Target
    implements Serializable {
  protected String tag;
  public String getTag() {
    return tag;
  }

  private static boolean RGBNormalize = false;

  /**
   * 設定Target內的RGB是否進行正規化.
   * 設定normalize為false時,則RGB不會正規化為0~1
   * @param normalize boolean
   */
  public final static void setRGBNormalize(boolean normalize) {
    RGBNormalize = normalize;
  }

  public final static boolean getRGBNormalize() {
    return RGBNormalize;
  }

  protected Target(List<Patch> patchList) {
    this(patchList, RGBNormalize);
  }

  public RGB.MaxValue getMaxValue() {
    return maxValue;
  }

  public void changeMaxValue(RGB.MaxValue maxValue) {
    this.maxValue = maxValue;
    for (Patch p : this.patchList) {
      RGB rgb = p.getRGB();
      RGB originalRGB = p.getOriginalRGB();
      if (rgb != null) {
        rgb.changeMaxValue(maxValue);
      }
      if (originalRGB != null) {
        originalRGB.changeMaxValue(maxValue);
      }

    }
  }

  protected Target(List<Patch> patchList, boolean RGBNormalizing
      ) {
    this.patchList = patchList;
    if (RGBNormalizing) {
      doRGBNormalize = true;
      normalizeRGB2Double1();
      maxValue = RGB.MaxValue.Double1;
    }
    else {
      maxValue = patchList.get(0).getRGB().getMaxValue();
    }
    patchMap = processPatchMap(patchList);
  }

  protected boolean doRGBNormalize = false;
  protected Map<RGB, Patch> patchMap;
  protected List<Patch> patchList;
  protected String description;
  protected String device;
  protected static ColorMatchingFunction defaultCMF = ColorMatchingFunction.
      CIE_1931_2DEG_XYZ;

  protected RGB.MaxValue maxValue = null;
  protected String filename;

  /**
   * 從patch List產生出patch Map
   * @param patchList List
   * @return Map
   * @todo H 對於一些有重複的target,沒辦法同時存在!
   */
  private final static Map<RGB, Patch> processPatchMap(List<Patch> patchList) {
    Map<RGB, Patch> patchMap = new HashMap<RGB, Patch> (patchList.size());
    for (Patch p : patchList) {
      RGB rgb = p.getOriginalRGB();
      patchMap.put(rgb, p);
    }
    return patchMap;
  }

  private final static LinkedHashMap<RGB,
                                     Patch> processPatchLinkedMap(List<Patch>
      patchList) {
    int size = patchList.size();
    LinkedHashMap<RGB, Patch> map = new LinkedHashMap<RGB, Patch> (size);
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      map.put(p.getRGB(), p);
    }

    return map;
  }

  public final int size() {
    return patchList.size();
  }

  /**
   * 從rgb找到對應的patch
   * @param rgb RGB
   * @return Patch
   */
  public final Patch getPatch(RGB rgb) {
    return this.patchMap.get(rgb);
  }

  protected RGB keyRGB = null;

  public final Patch getPatch(RGBBase.Channel channel, double value) {
    return getPatch(channel, value, this.maxValue);
  }

  public final Patch getPatch(RGBBase.Channel channel, double value,
                              RGB.MaxValue type) {
    if (keyRGB == null) {
      keyRGB = getKeyRGB();
    }
    keyRGB.setColorBlack();
    keyRGB.setValue(channel, value, type);
    return this.getPatch(keyRGB);
  }

  public final Patch getPatch(double r, double g, double b) {
    if (keyRGB == null) {
      keyRGB = getKeyRGB();
    }
    keyRGB.setValues(new double[] {r, g, b});
    return this.getPatch(keyRGB);
  }

  /**
   *
   * @param keyRGB RGB
   * @return Patch
   * @deprecated
   */
  public final Patch getPatchByKeyRGB(RGB keyRGB) {
    return this.patchMap.get(keyRGB);
  }

  /**
   * 利用此RGB物件當作getPatch(RGB)的key值
   * @return RGB
   */
  public final RGB getKeyRGB() {
    return (RGB)this.patchList.get(0).getOriginalRGB().clone();
  }

  public final Patch getPatch(int index) {
    return patchList.get(index);
  }

  public final List<Patch> getPatchList() {
//    List<Patch> copy = new ArrayList<Patch>(this.patchList);
    return patchList;
  }

  protected final void setDescription(String description) {
    this.description = description;
  }

  public final String getDescription() {
    return description + ( (tag != null) ? "-" + tag : "");
  }

  /**
   * 取得預設的CMF
   * @return ColorMatchingFunction
   */
  public final static ColorMatchingFunction getDefaultCMF() {
    return defaultCMF;
  }

  public final CIEXYZ getLuminance() {
    return luminance;
  }

  public final String getDevice() {
    return device;
  }

  public String getFilename() {
    return filename;
  }

  public final static void setDefaultCMF(ColorMatchingFunction cmf) {
    defaultCMF = cmf;
  }

  protected final void setDevice(String device) {
    this.device = device;
  }

  /**
   * 將每個色塊的RGB正規化到1.0
   */
  private void normalizeRGB2Double1() {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();
      rgb.changeMaxValue(RGB.MaxValue.Double1);
    }
  }

  protected CIEXYZ luminance;
  protected boolean normalized = false;

  public final void calculateNormalizedXYZ() {
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
      CIEXYZ normalizedXYZ = (CIEXYZ) XYZ.clone();
      normalizedXYZ.normalize(luminance);
      Patch.Operator.setNormalizedXYZ(p, normalizedXYZ);
    }
    normalized = true;
  }

  /**
   * 轉成Lab的Patch List
   * @return List
   */
  public abstract List<Patch> getLabPatchList();

  /**
   * 將Patch name替換成RGB的code
   */
  public final void replacePatchNameByRGB() {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      String name = p.getRGB().toString();
      p.name = name;
    }

  }

  protected void setPatchName(List<String> patchNameList) {
    if (this.patchList.size() != patchNameList.size()) {
      throw new IllegalArgumentException(
          "this.patchList.size() != patchNameList.size()");
    }
    int size = this.size();

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      String name = patchNameList.get(x);
      Patch.Operator.setName(p, name);
    }
  }

  public static void main(String[] args) {
//    List l = new ArrayList();
//    Persistence.writeObject(l, "test.obj");
    Map<RGB, Patch> patchMap = new HashMap<RGB, Patch> ();
    Persistence.writeObject(patchMap, "test.obj");
  }
}
