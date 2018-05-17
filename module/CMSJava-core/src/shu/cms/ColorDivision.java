package shu.cms;

import java.util.*;

import shu.cms.colorspace.independ.*;
import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 這裡的分區是為了回歸分析避免太過絕對,
 * 所以採用的是鬆散式分區(分區之間彼此有重疊).
 * 需要另外一種緊密式分區(沒有重疊),用作資料分析使用.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @todo M 鬆散式分區/緊密式分區 (loose/tight)
 */
public final class ColorDivision {
  private final static ColorDivision[] divisionArray = new ColorDivision[Zone.
      values().length];

  static {
    Division lowChroma = new Division(false, true, 0, 30.);
    Division lowChroma20 = new Division(false, true, 0, 20.);
    Division lowLightness = new Division(false, true, 0, 50.);
    Division highLightness = new Division(true, false, 50, 0.);
    Division lightness25 = new Division(true, true, 0., 25.);
    Division lightness50 = new Division(true, true, 25., 50.);
    Division lightness75 = new Division(true, true, 50., 75.);
    Division lightness100 = new Division(true, true, 75., 100.);

//    int size = Type.values().length;
//    divisionArray = new ColorDivision[size];
    divisionArray[0] = new ColorDivision("GLOBAL");
    divisionArray[1] = new ColorDivision("LOW_CHROMA", null, lowChroma);
    divisionArray[2] = new ColorDivision("LOW_LIGHTNESS_LOW_CHROMA",
                                         lowLightness, lowChroma);
    divisionArray[3] = new ColorDivision("HIGH_LIGHTNESS_LOW_CHROMA",
                                         highLightness, lowChroma);
    divisionArray[4] = new ColorDivision("LIGHTNESS_25", lightness25, null);
    divisionArray[5] = new ColorDivision("LIGHTNESS_50", lightness50, null);
    divisionArray[6] = new ColorDivision("LIGHTNESS_75", lightness75, null);
    divisionArray[7] = new ColorDivision("LIGHTNESS_100", lightness100, null);
    divisionArray[8] = new ColorDivision("LOW_CHROMA_20", null, lowChroma20);
  }

  public static final ColorDivision GLOBAL = divisionArray[0];
  public static final ColorDivision LOW_CHROMA = divisionArray[1];
  public static final ColorDivision LOW_LIGHTNESS_LOW_CHROMA =
      divisionArray[2];
  public static final ColorDivision HIGH_LIGHTNESS_LOW_CHROMA =
      divisionArray[3];
  public static final ColorDivision LIGHTNESS_25 = divisionArray[4];
  public static final ColorDivision LIGHTNESS_50 = divisionArray[5];
  public static final ColorDivision LIGHTNESS_75 = divisionArray[6];
  public static final ColorDivision LIGHTNESS_100 = divisionArray[7];
  public static final ColorDivision LOW_CHROMA_20 = divisionArray[8];

  public static enum Zone {
    GLOBAL, LOW_CHROMA, LOW_CHROMA_LOW_LIGHTNESS, LOW_CHROMA_HIGH_LIGHTNESS,
    LIGHTNESS_25, LIGHTNESS_50, LIGHTNESS_75, LIGHTNESS_100, LOW_CHROMA_20;
  };

  public final static ColorDivision getInstance(Zone type) {
    switch (type) {
      case GLOBAL:
        return GLOBAL;
      case LOW_CHROMA:
        return LOW_CHROMA;
      case LOW_CHROMA_LOW_LIGHTNESS:
        return LOW_LIGHTNESS_LOW_CHROMA;
      case LOW_CHROMA_HIGH_LIGHTNESS:
        return HIGH_LIGHTNESS_LOW_CHROMA;
      case LIGHTNESS_25:
        return LIGHTNESS_25;
      case LIGHTNESS_50:
        return LIGHTNESS_50;
      case LIGHTNESS_75:
        return LIGHTNESS_75;
      case LIGHTNESS_100:
        return LIGHTNESS_100;
      case LOW_CHROMA_20:
        return LOW_CHROMA_20;
    }
    return null;
  }

  protected String name;
  protected Division CDivision;
  protected Division LDivision;

  protected ColorDivision(String name) {
    this.name = name;
  }

  protected ColorDivision(String name, Division lightnessDivision,
                          Division chromaDivision) {
    this.name = name;
    this.LDivision = lightnessDivision;
    this.CDivision = chromaDivision;
  }

  /**
   *
   * @param name String
   * @param checkLLowermost boolean
   * @param checkLUppermost boolean
   * @param LLowermost double
   * @param LUppermost double
   * @param checkCLowermost boolean
   * @param checkCUppermost boolean
   * @param CLowermost double
   * @param CUppermost double
   * @deprecated
   */
  protected ColorDivision(String name,
                          boolean checkLLowermost, boolean checkLUppermost,
                          double LLowermost, double LUppermost,
                          boolean checkCLowermost, boolean checkCUppermost,
                          double CLowermost, double CUppermost) {
    this(name);
    if (checkLLowermost || checkLUppermost) {
      LDivision = new Division(checkLLowermost, checkLUppermost, LLowermost,
                               LUppermost);
    }

    if (checkCLowermost || checkCUppermost) {
      CDivision = new Division(checkCLowermost, checkCUppermost, CLowermost,
                               CUppermost);
    }
  }

  public static void main(String[] args) {
    ColorDivision cd = ColorDivision.getInstance(ColorDivision.Zone.
                                                 LIGHTNESS_50);
    System.out.println(cd.getName());
//      ColorDivision.TYPE.LOW_CHROMA_HIGH_LIGHTNESS.x=1;
  }

  public String getName() {
    return name;
  }

  public boolean isValid(CIELab Lab) {
    return isValid(new CIELCh(Lab));
  }

  /**
   *
   * @param LCh CIELCh
   * @return boolean
   */
  public boolean isValid(CIELCh LCh) {
    double c = LCh.C;
    double l = LCh.L;
    if (CDivision != null && !CDivision.isValid(c)) {
      return false;
    }

    if (LDivision != null && !LDivision.isValid(l)) {
      return false;
    }
    return true;
  }

  public List<CIELab>[] filterCIELab(final List<CIELab> LabList1,
                                     final List<CIELab> LabList2) {
    return filterCIELab(LabList1, LabList2, this);
  }

  /**
   *
   * @param LabList1 List
   * @param LabList2 List
   * @param division ColorDivision
   * @return List[]
   */
  @SuppressWarnings( {"unchecked"})
  protected static List<CIELab>[] filterCIELab(final List<CIELab> LabList1,
                                               final List<CIELab> LabList2,
                                               final ColorDivision division) {
    if (LabList1.size() != LabList2.size()) {
      throw new IllegalArgumentException(
          "patchList1.size() != patchList2.size()");
    }

    List<CIELab> result1 = new ArrayList<CIELab> ();
    List<CIELab> result2 = new ArrayList<CIELab> ();

    int size = LabList1.size();
    for (int x = 0; x < size; x++) {
      CIELab p1 = LabList1.get(x);
      if (division.isValid(p1)) {
        result1.add(p1);
        CIELab p2 = LabList2.get(x);
        result2.add(p2);
      }
    }
    List<CIELab> [] result = new List[] {
        result1, result2};
    return result;
  }

  public List<CIELab> filterCIELab(final List<CIELab> LabList) {
    return filterCIELab(LabList, this);
  }

  protected static List<CIELab> filterCIELab(final List<CIELab> LabList,
                                             final ColorDivision division) {
    List<CIELab> result = new ArrayList<CIELab> ();
    for (CIELab Lab : LabList) {
      if (division.isValid(Lab)) {
        result.add(Lab);
      }
    }
    return result;
  }

  public List<Patch> filterPatch(final List<Patch> patchList) {
    return filterPatch(patchList, this);
  }

  /**
   * 過濾patch
   * @param patchList List
   * @param division ColorDivision
   * @return List
   */
  protected static List<Patch> filterPatch(final List<Patch> patchList,
                                           final ColorDivision division) {
    List<Patch> result = new ArrayList<Patch> ();
    for (Patch p : patchList) {
      if (division.isValid(p.getLab())) {
        result.add(p);
      }
    }
    return result;
  }

  public List<Patch>[] filterPatch(final List<Patch> patchList1,
                                   final List<Patch> patchList2) {
    return filterPatch(patchList1, patchList2, this);
  }

  /**
   * 過濾patch
   * 會以patchList1為主,根據division來過濾patch
   * @param patchList1 List
   * @param patchList2 List
   * @param division ColorDivision
   * @return List[]
   */
  @SuppressWarnings( {"unchecked"})
  protected static List<Patch>[] filterPatch(final List<Patch> patchList1,
                                             final List<Patch> patchList2,
                                             final ColorDivision division) {
    if (patchList1.size() != patchList2.size()) {
      throw new IllegalArgumentException(
          "patchList1.size() != patchList2.size()");
    }

    List<Patch> result1 = new ArrayList<Patch> ();
    List<Patch> result2 = new ArrayList<Patch> ();

    int size = patchList1.size();
    for (int x = 0; x < size; x++) {
      Patch p1 = patchList1.get(x);
      if (division.isValid(p1.getLab())) {
        result1.add(p1);
        Patch p2 = patchList2.get(x);
        result2.add(p2);
      }
    }
    List<Patch> [] result = new List[] {
        result1, result2};
    return result;
  }

}
