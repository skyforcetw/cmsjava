package shu.cms;

import java.io.*;
import java.util.*;

import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.util.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * patch代表一個色塊
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Patch
    implements Comparable, NameIF, Serializable {

  protected String name;
  protected CIELab _Lab = null;
  protected CIEXYZ XYZ = null;
  protected CIEXYZ normalizedXYZ = null;
  protected RGB rgb = null;
  protected RGB originalRGB = null;
  protected Spectra spectra = null;
  protected Spectra reflectSpectra = null;

  public static class Produce {

    public static List<Patch> copyOf(List<Patch> patchList) {
      int size = patchList.size();
      List<Patch> clonePatchList = new ArrayList<Patch> (size);
      try {
        for (Patch p : patchList) {
          Patch clone = (Patch) p.clone();
          clonePatchList.add(clone);
        }
      }
      catch (Exception ex) {
        Logger.log.error("", ex);
      }

      return clonePatchList;
    }

    /**
     * 從光譜能量分佈配合人眼配色函數,計算出XYZ,再填入rgbList內的數值到Patch
     * @param powerSpectraList List
     * @param reflectSpectraList List
     * @param rgbList List
     * @param cmf ColorMatchingFunction
     * @return List
     */
    public static List<Patch> XYZRGBPatches(List<Spectra> powerSpectraList,
        List<Spectra> reflectSpectraList, List<RGB> rgbList,
        ColorMatchingFunction cmf) {
      if (powerSpectraList.size() != rgbList.size()) {
        throw new IllegalArgumentException(
            "spectraList.size() != rgbList.size()");
      }
      int size = powerSpectraList.size();
      List<Patch>
          patchList = XYZPatches(powerSpectraList, reflectSpectraList, cmf);

      //設定RGB
      for (int x = 0; x < size; x++) {
        RGB rgb = rgbList.get(x);
        Patch p = patchList.get(x);
        p.rgb = rgb;
        p.originalRGB = (RGB) rgb.clone();
      }

      return patchList;
    }

    /**
     * 將XYZ和rgb合併到Patch裡
     * @param XYZList List
     * @param rgbList List
     * @return List
     */
    public static List<Patch> XYZRGBPatches(List<CIEXYZ> XYZList,
        List<RGB> rgbList) {
      if (XYZList.size() != rgbList.size()) {
        throw new IllegalArgumentException("XYZList.size() != rgbList.size()");
      }
      int size = rgbList.size();
      List<Patch> patchList = new ArrayList<Patch> (size);
      //設定RGB
      for (int x = 0; x < size; x++) {
        CIEXYZ XYZ = XYZList.get(x);
        RGB RGB = rgbList.get(x);
        Patch p = new Patch(String.valueOf(x), XYZ, null, RGB);
        patchList.add(p);
      }

      return patchList;
    }

    /**
     * 從光譜能量分佈配合人眼配色函數,計算出XYZ
     * @param powerSpectraList List
     * @param reflectSpectraList List
     * @param cmf ColorMatchingFunction
     * @return List
     */
    public static List<Patch> XYZPatches(List<Spectra> powerSpectraList,
        List<Spectra> reflectSpectraList,
        ColorMatchingFunction cmf) {
      if (!powerSpectraList.get(0).isPowerSpectrum()) {
        throw new IllegalArgumentException(
            "!powerSpectraList.get(0).isPowerSpectrum() (" +
            powerSpectraList.get(0).getSpectraType() + ")");
      }
      int size = powerSpectraList.size();
      if (reflectSpectraList != null && size != reflectSpectraList.size()) {
        throw new IllegalArgumentException(
            "spectraList.size != reflectSpectraList.size");
      }
      List<Patch> patches = new ArrayList<Patch> (powerSpectraList.size());

      for (int x = 0; x < size; x++) {
        Spectra s = powerSpectraList.get(x);
        Spectra r = null;
        if (reflectSpectraList != null) {
          r = reflectSpectraList.get(x);
        }
        CIEXYZ XYZ = s.getXYZ(cmf);
        Patch patch = new Patch(s.getName(), XYZ, null, null, null, s, r);
        patches.add(patch);
      }

      return patches;
    }

    /**
     * 計算白點
     * 將光源白點的Y調整到跟white一致
     * 產生出來的L值就會以LCD色塊的白點為100
     * 但是ab卻不會受到影響
     * @param cmf ColorMatchingFunction
     * @param illuminant CIEIlluminant
     * @param white CIEXYZ
     * @return CIEXYZ
     * @deprecated
     */
    public final static CIEXYZ whitePoint(ColorMatchingFunction cmf,
                                          Illuminant illuminant,
                                          CIEXYZ white) {
      CIEXYZ whitePoint = illuminant.getSpectra().getXYZ(cmf);
      whitePoint.scaleY(white);
      return whitePoint;
    }

    /**
     *
     * @param rgbList List
     * @return List
     */
    public static List<Patch> RGBPatches(List<RGB> rgbList) {
//    return produceXYZRGBPatches(null,rgbList);
      int size = rgbList.size();
      List<Patch> patchList = new ArrayList<Patch> (rgbList.size());

      //設定RGB
      for (int x = 0; x < size; x++) {
        RGB RGB = rgbList.get(x);
        Patch p = new Patch(null, null, null, RGB);
        patchList.add(p);
      }

      return patchList;
    }

    /**
     * 會以whiteSpectra作白點基準作Lab的計算
     * @param spectraList List
     * @param cmf ColorMatchingFunction
     * @param whiteSpectra Spectra
     * @return List
     */
    public static List<Patch> LabPatches(List<Spectra> spectraList,
        ColorMatchingFunction cmf,
        Spectra whiteSpectra) {
      List<Patch> patchList = XYZPatches(spectraList, null, cmf);
      CIEXYZ white = whiteSpectra.getXYZ(cmf);

      return LabPatches(patchList, white);

    }

    /**
     * 從XYZ Patch計算Lab
     * @param XYZPatchList List
     * @param white CIEXYZ
     * @return List
     */
    public static List<Patch> LabPatches(List<Patch>
        XYZPatchList, CIEXYZ white) {

      Patch firstPatch = XYZPatchList.get(0);

      if (firstPatch.getXYZ() == null) {
        throw new IllegalArgumentException(
            "Patch.getXYZ() == null");
      }

      for (Patch p : XYZPatchList) {
        CIEXYZ XYZ = p.getXYZ();
        CIELab Lab = CIELab.fromXYZ(XYZ, white);
        p._Lab = Lab;
      }

      return XYZPatchList;
    }

    /**
     * 會以illuminant為白點基準,但是以whiteSpectra為L=100作Lab的計算
     * @param spectraList List
     * @param cmf ColorMatchingFunction
     * @param illuminant CIEIlluminant
     * @param whiteSpectra Spectra
     * @return List
     * @deprecated
     */
    public static List<Patch> LabPatches(List<Spectra> spectraList,
        ColorMatchingFunction cmf,
        Illuminant illuminant, Spectra whiteSpectra) {

      List<Patch> patchList = XYZPatches(spectraList, null, cmf);
      CIEXYZ white = whiteSpectra.getXYZ(cmf);
      CIEXYZ whitePoint = whitePoint(cmf, illuminant, white);

      return LabPatches(patchList, whitePoint);
    }

  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("name[");
    buf.append(name);
    if (rgb != null) {
      buf.append("] RGB[");
      buf.append(rgb);
    }

    if (XYZ != null) {
      buf.append("] XYZ[");
      buf.append(XYZ);
    }

    if (normalizedXYZ != null) {
      buf.append("] normalizedXYZ[");
      buf.append(normalizedXYZ);
    }

    if (_Lab != null) {
      buf.append("] Lab[");
      buf.append(_Lab);
    }
    buf.append("]");
    return buf.toString();
  }

  /**
   *
   * @param name String
   * @param XYZ CIEXYZ
   * @param Lab CIELab
   * @param rgb RGB
   * @param spectra Spectra
   * @deprecated
   */
  public Patch(String name, CIEXYZ XYZ, CIELab Lab, RGB rgb, Spectra spectra) {
    this(name, XYZ, null, Lab, rgb, spectra, null);
  }

  public Patch(String name, CIEXYZ XYZ, CIEXYZ normalizedXYZ, CIELab Lab,
               RGB rgb, Spectra spectra) {
    this(name, XYZ, normalizedXYZ, Lab, rgb, spectra, null);
  }

  protected static boolean UseContentMap = false;

  protected Map<String, NameIF> contentMap = null;
  /**
   *
   * @param name String
   * @param XYZ CIEXYZ
   * @param Lab CIELab
   * @param rgb RGB
   * @param spectra Spectra
   * @param reflectSpectra Spectra
   * @deprecated
   */
  public Patch(String name, CIEXYZ XYZ, CIELab Lab, RGB rgb, Spectra spectra,
               Spectra reflectSpectra) {
    this(name, XYZ, null, Lab, rgb, spectra, reflectSpectra);
  }

  public Patch(String name, CIEXYZ XYZ, CIEXYZ normalizedXYZ, CIELab Lab,
               RGB rgb, Spectra spectra, Spectra reflectSpectra) {
    this.name = name;
    this.XYZ = XYZ;
    this.normalizedXYZ = normalizedXYZ;
    this._Lab = Lab;
    if (rgb != null) {
      this.rgb = (RGB) rgb.clone();
    }

    this.originalRGB = rgb;
    this.spectra = spectra;
    this.reflectSpectra = reflectSpectra;
    if (UseContentMap) {
      contentMap = new HashMap<String, NameIF> (6);
      putInContentMap(XYZ);
      putInContentMap(Lab);
      putInContentMap(rgb);
      putInContentMap(spectra);
      putInContentMap(reflectSpectra);
    }
  }

  public Patch(String name, CIEXYZ XYZ, CIEXYZ normalizedXYZ, CIELab Lab,
               RGB rgb, Spectra spectra, Spectra reflectSpectra,
               NameIF ...otherContent) {
    this(name, XYZ, normalizedXYZ, Lab, rgb, spectra, reflectSpectra);
    if (UseContentMap && otherContent != null) {
      for (NameIF content : otherContent) {
        contentMap.put(content.getName(), content);
      }
    }
  }

  protected void putInContentMap(NameIF nameIF) {
    if (nameIF != null && contentMap != null) {
      contentMap.put(nameIF.getName(), nameIF);
    }
  }

  public NameIF getContent(String contentName) {
    if (contentMap != null) {
      return contentMap.get(contentName);
    }
    else {
      return null;
    }
  }

  public NameIF[] getContents() {
    if (contentMap != null) {
      Collection<NameIF> collect = contentMap.values();
      NameIF[] nameIFArray = new NameIF[collect.size()];
      return contentMap.values().toArray(nameIFArray);
    }
    else {
      return null;
    }

  }

  /**
   *
   * @param name String
   * @param XYZ CIEXYZ
   * @param Lab CIELab
   * @param rgb RGB
   */
  public Patch(String name, CIEXYZ XYZ, CIELab Lab, RGB rgb) {
    this(name, XYZ, null, Lab, rgb, null, null);
  }

  public Patch(String name, CIEXYZ XYZ, CIEXYZ normalizedXYZ, CIELab Lab,
               RGB rgb) {
    this(name, XYZ, normalizedXYZ, Lab, rgb, null, null);
  }

  public static void main(String[] args) {
//    CXF cxf = CXFUtil.openCXF(
//        "Measurement Files/Calibration/lino/Training_29.cxf");
    CXFOperator cxf = new CXFOperator(
        "Measurement Files/Camera/D200Raw/D65/CCSG.cxf");
    List<Spectra> spectraList = cxf.getSpectraList();

//    List<Patch> patches =
//        producePatches(spectraList,
//                       ColorMatchingFunction.CIE_1931_2DEG_XYZ,spectraList.get(26));

    List<Spectra>
        powerSpectraList = Spectra.produceSpectraPowerList(spectraList,
        Illuminant.D65); //Spectral power obtained by Illuminant*Reflectance

    List<Patch> patches =
        Produce.LabPatches(powerSpectraList,
                           ColorMatchingFunction.CIE_1931_2DEG_XYZ,
                           powerSpectraList.get(0));

    for (Patch patch : patches) {
      System.out.println(patch.getName() + " " + patch.getLab());
//      for (NameIF n : patch.getContents()) {
//        System.out.print(n + " ");
//      }
//      System.out.println("");
    }

    List<Patch> cloneList = Patch.Produce.copyOf(patches);
    Patch p1 = patches.get(0);
    Patch p2 = cloneList.get(0);
    System.out.println("");
  }

  public CIELab getLab() {
    return _Lab;
  }

  /**
   * 將色塊作轉置的處理
   * @param patchList List
   * @param lgorowLength int
   * @return List
   */
  public static List<Patch> transpose(List<Patch> patchList, int lgorowLength) {
    int size = patchList.size();
    int lgorowWidth = size / lgorowLength;
    Patch[][] patchArray = new Patch[lgorowLength][lgorowWidth];
    int index = 0;
    for (int x = 0; x < lgorowLength; x++) {
      for (int y = 0; y < lgorowWidth; y++) {
        patchArray[x][y] = patchList.get(index++);
      }
    }
    Object[][] transpose = Utils.transpose(patchArray);
    index = 0;
    for (int x = 0; x < lgorowWidth; x++) {
      for (int y = 0; y < lgorowLength; y++) {
        patchList.set(index++, (Patch) transpose[x][y]);
      }
    }
    return patchList;
  }

  public String getName() {
    return name;
  }

  public Spectra getSpectra() {
    return spectra;
  }

  public Spectra getReflectSpectra() {
    return reflectSpectra;
  }

  public CIEXYZ getNormalizedXYZ() {
    return normalizedXYZ;
  }

  public RGB getOriginalRGB() {
    return originalRGB;
  }

  public RGB getRGB() {
    return rgb;
  }

  public CIEXYZ getXYZ() {
    return XYZ;
  }

  /**
   * Compares this object with the specified object for order.
   *
   * @param o the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *   less than, equal to, or greater than the specified object.
   */
  public int compareTo(Object o) {
    if ( ( (Patch) o).getRGB() != null) {
      RGB thisRGB = getRGB();
      RGB thatRGB = ( (Patch) o).getRGB();
      return thisRGB.compareTo(thatRGB);
    }
    else {
      return 0;
    }
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return super.hashCode();
  }

  public static class Filter {

    /**
     * 將rgb範圍在startRange~endRange之間者過濾出來
     * @param source List
     * @param filtered Set
     * @param channel Channel
     * @param startRange double
     * @param endRange double
     */
    public final static void RGBInRange(List<Patch> source,
        Set<Patch> filtered, RGBBase.Channel channel, double startRange,
        double endRange) {
      for (Patch p : source) {
        RGB rgb = p.getRGB();
        //是灰色,那ch也要是W
        //不是灰色,那ch不能是W
        if ( ( (rgb.isGray() && channel == RGBBase.Channel.W) ||
              (!rgb.isGray() && channel != RGBBase.Channel.W &&
               rgb.isPrimaryChannel())) &&
            rgb.getValue(channel) <= endRange &&
            rgb.getValue(channel) >= startRange &&
            rgb.getValue(channel) != 0) {

          RGB cloneRGB = (RGB) rgb.clone();

          //======================================================================
          // 重新正規化
          //======================================================================
//          double value = cloneRGB.getValue(channel);
//          value = ( (value - startRange) / (endRange - startRange)) *
//              rgb.getMaxValue().max;
//          if (value < 1. / RGB.MaxValue.Integer65280.max) {
//            value = 0;
//          }
//          cloneRGB.setValue(channel, value);
          //======================================================================

          CIEXYZ cloneXYZ = (CIEXYZ) weakClone(p.getXYZ());
          CIEXYZ cloneNormalizedXYZ = (CIEXYZ) weakClone(p.getNormalizedXYZ());
          CIELab cloneLab = (CIELab) weakClone(p.getLab());
          Spectra spectra = (Spectra) weakClone(p.getSpectra());
          Spectra reflectSpectra = (Spectra) weakClone(p.getReflectSpectra());

          Patch newPatch = new Patch(p.getName(), cloneXYZ, cloneNormalizedXYZ,
                                     cloneLab,
                                     cloneRGB, spectra, reflectSpectra);
          filtered.add(newPatch);
        }
        else {
          continue;
        }

      }
    }

    private final static ColorSpace weakClone(ColorSpace colorSpace) {
      if (colorSpace == null) {
        return null;
      }
      else {
        return (ColorSpace) colorSpace.clone();
      }
    }

    private final static Spectra weakClone(Spectra spectra) {
      if (spectra == null) {
        return null;
      }
      else {
        return (Spectra) spectra.clone();
      }
    }

    public final static List<Patch> patch(List<Patch> source,
        RGBBase.Channel channel, double value) {
      List<Patch> filtered = new LinkedList<Patch> ();
      for (Patch p : source) {
        if (p.getRGB().getValue(channel) == value) {
          filtered.add(p);
        }
      }
      return filtered;
    }

    /**
     * 將 單一頻道 有值的色塊過濾出來
     * @param patchList List
     * @param filtered List
     * @param channel Channel
     */
    public final static void oneValueChannel(Collection<Patch>
        patchList, Collection<Patch> filtered, RGBBase.Channel channel) {
      for (Patch p : patchList) {
        if (Patch.hasOnlyOneValue(p) &&
            p.getRGB().getValue(channel) != 0) {
          filtered.add(p);
        }
      }
    }

    /**
     * 將 單一頻道為0(兩兩頻道互相混合)
     * @param source List
     * @param filtered List
     * @param withWhite boolean 白一併過濾出來
     */
    public final static void leastOneZero(List<Patch> source,
        List<Patch> filtered, boolean withWhite) {
      for (Patch p : source) {
        if (p.getRGB().hasZeroChannel()) {
          filtered.add(p);
        }
        else if (withWhite && p.getRGB().isWhite()) {
          filtered.add(p);
        }
      }
    }

    public final static List<RGB> rgbList(List<Patch> patchList) {
      List<RGB> rgbList = new ArrayList<RGB> (patchList.size());
      for (Patch p : patchList) {
        rgbList.add(p.getRGB());
      }
      return rgbList;
    }

    public final static List<CIELab> LabList(List<Patch> patches) {
      int size = patches.size();
      List<CIELab> labs = new ArrayList<CIELab> (size);
      for (int x = 0; x < size; x++) {
        Patch patch = patches.get(x);
        labs.add(patch.getLab());
      }
      return labs;
    }

    public final static List<CIEXYZ> XYZList(List<Patch> patches) {
      int size = patches.size();
      List<CIEXYZ> XYZs = new ArrayList<CIEXYZ> (size);
      for (int x = 0; x < size; x++) {
        Patch patch = patches.get(x);
        XYZs.add(patch.getXYZ());
      }
      return XYZs;
    }

    public final static Patch whitePatch(List<Patch> patchList) {
      for (int x = 0; x < patchList.size(); x++) {
        Patch p = patchList.get(x);
        if (p.getRGB().isWhite()) {
          return p;
        }
      }
      return null;
    }

    public final static Patch whitestPatch(List<Patch> patchList) {
      Patch whitestPatch = null;
      for (Patch p : patchList) {
        if (null == whitestPatch || p.getXYZ().Y > whitestPatch.getXYZ().Y) {
          whitestPatch = p;
        }
      }
      return whitestPatch;
    }

    /**
     * 過濾出灰階(不含黑)
     * @param patchList List
     * @param filtered Collection
     */
    public final static void grayPatch(List<Patch>
        patchList, Collection<Patch> filtered) {
      for (Patch p : patchList) {
        RGB rgb = p.getRGB();
        if (rgb.isGray() && !rgb.isBlack()) {
          filtered.add(p);
        }
      }
    }

  }

  /**
   * 是否有 至少單一頻道為0
   * @param patch Patch
   * @return boolean
   * @deprecated
   */
  public final static boolean hasLeastOneValue0(Patch patch) {
    double[] RGBValues = patch.getRGB().getValues();
    for (double d : RGBValues) {
      if (d == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * 只有單一頻道的值 (note:{0,0,0} 也包括)
   * @param patch Patch
   * @return boolean
   */
  public final static boolean hasOnlyOneValue(Patch patch) {
    RGB rgb = patch.getRGB();
    return rgb.isPrimaryChannel();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *   <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    return compareTo(obj) == 0;
  }

  public static class Operator {
    public static void setLab(Patch p, CIELab Lab) {
      p._Lab = Lab;
    }

    public static void setXYZ(Patch p, CIEXYZ XYZ) {
      p.XYZ = XYZ;
    }

    public static void setNormalizedXYZ(Patch p, CIEXYZ normalizedXYZ) {
      p.normalizedXYZ = normalizedXYZ;
    }

    public static void setName(Patch p, String name) {
      p.name = name;
    }

    public static void setRGB(Patch p, RGB rgb) {
      p.rgb = rgb;
    }

    public static void setOriginalRGB(Patch p, RGB originalRGB) {
      p.originalRGB = originalRGB;
    }

    public static void setSpectra(Patch p, Spectra spectra) {
      p.spectra = spectra;
    }

    public static void setReflectSpectra(Patch p, Spectra reflectSpectra) {
      p.reflectSpectra = reflectSpectra;
    }
  }

  public final static void setUseContentMap(boolean useContentMap) {
    UseContentMap = useContentMap;
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support
   *   the <code>Cloneable</code> interface. Subclasses that override the
   *   <code>clone</code> method can also throw this exception to indicate
   *   that an instance cannot be cloned.
   */
  protected Object clone() throws CloneNotSupportedException {
    Patch clone = new Patch(this.name, this.XYZ, this.normalizedXYZ, this._Lab,
                            this.rgb, this.spectra, this.reflectSpectra,
                            this.getContents());
    return clone;
  }

}
