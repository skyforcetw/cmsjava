package shu.cms.colororder;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.independ.*;

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
public class PCCS {
  protected final static String PCCS_FILENAME =
      "Measurement Files/PCCS/PCCS129a.cxf";

  protected final static List<Spectra> reflectSpectra;
  private static List<Patch> D50LabPatchList;
  private static List<Patch> D65LabPatchList;

  public static enum Tone {
    v("v", 0), dp("dp", 1), dk("dk", 2), p("p", 3), lt("lt", 4), b("b", 5),
    d("d", 6), ltg("ltg", 7), g("g", 8);

    String name;
    int index;
    Tone(String name, int index) {
      this.name = name;
      this.index = index;
    }
  }

  public static enum Hue {
    R("R", 2, 0), rO("rO", 4, 1), yO("yO", 6, 2), Y("Y", 8, 3), YG("YG", 10, 4),
    G("G", 12, 5), GB("GB", 14, 6), gB("gB", 16, 7), B("B", 18, 8), V("V", 20,
        9), P("P", 22,
              10), RP("RP", 24, 11);

    String name;
    int index;
    int hueNumber;
    Hue(String name, int hueNumber, int index) {
      this.name = name;
      this.hueNumber = hueNumber;
      this.index = index;
    }

  }

  static {
    CXFOperator cxf = new CXFOperator(PCCS_FILENAME);
    reflectSpectra = cxf.getSpectraList();

    int size = reflectSpectra.size();
    for (int x = 0; x < size; x++) {
      int hueIndex = x / 9;
      int toneIndex = x % 9;
      String name = "(" + Hue.values()[hueIndex].name + ") " +
          Tone.values()[toneIndex].name + (hueIndex * 2 + 2);
      Spectra s = reflectSpectra.get(x);
      s.setName(name);
    }
  }

  protected final static List<Patch> getHuePlane(int hueIndex, List<Patch>
      PCCSPachList) {
    int start = 9 * hueIndex;
    List<Patch> result = PCCSPachList.subList(start, start + 9);
    return result;
  }

  public final static List<Patch> getD50Plane(Hue hue) {
    if (D50LabPatchList == null) {
      D50LabPatchList = getLabPatchList(Illuminant.D50);
    }
    return getHuePlane(hue.index, D50LabPatchList);
  }

  public final static List<Patch> getD65Plane(Hue hue) {
    if (D65LabPatchList == null) {
      D65LabPatchList = getLabPatchList(Illuminant.D65);
    }
    return getHuePlane(hue.index, D65LabPatchList);
  }

  protected final static List<Patch> getLabPatchList(Illuminant illuminant) {
    Spectra illuminantSpectra = illuminant.getSpectra();
    illuminantSpectra.normalizeDataToMax();
    List<Spectra>
        SPDList = Spectra.produceSpectraPowerList(reflectSpectra, illuminant);
    List<Patch>
        LabPatchList = Patch.Produce.LabPatches(SPDList,
                                                ColorMatchingFunction.
                                                CIE_1931_2DEG_XYZ,
                                                illuminantSpectra);

//    Patch.transpose(LabPatchList, 12);
    return LabPatchList;
  }

  public static void main(String[] args) {
    List<Patch> D50 = getD50Plane(Hue.V);
    for (Patch p : D50) {
      CIELab Lab = p.getLab();
      CIELCh LCh = new CIELCh(Lab);
      System.out.println(p.getName() + " " + LCh);
    }
  }
}
