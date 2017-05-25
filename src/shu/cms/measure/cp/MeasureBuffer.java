package shu.cms.measure.cp;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MeasureBuffer
    implements Serializable {

  public static enum BufferMode {
    Patch, CIEXYZ;
  }

  private Map<RGB, Patch> patchMap = new HashMap<RGB, Patch> ();
  private Map<RGB, CIEXYZ> XYZMap = new HashMap<RGB, CIEXYZ> ();

  private Map<RGB, Patch> freshPatchMap = new HashMap<RGB, Patch> ();
  private Map<RGB, CIEXYZ> freshXYZMap = new HashMap<RGB, CIEXYZ> ();

  void clearFreshBuffer() {
    freshPatchMap.clear();
    freshXYZMap.clear();
  }

  protected MeasureBuffer(BufferMode mode) {
    this.bufferMode = mode;
  }

  protected void clear() {
    patchMap.clear();
    XYZMap.clear();
    clearFreshBuffer();
  }

  private BufferMode bufferMode = BufferMode.Patch;

  protected void putToBuffer(List<Patch> patchList) {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();

      if (patchMap.containsKey(rgb)) {
        patchMap.remove(rgb);
      }
      if (XYZMap.containsKey(rgb)) {
        XYZMap.remove(rgb);
      }

      patchMap.put(rgb, p);
      XYZMap.put(rgb, p.getXYZ());
      freshPatchMap.put(rgb, p);
      freshXYZMap.put(rgb, p.getXYZ());
    }
  }

  private List<RGB> getMustMeasureRGBListInPatchBuffer(List<RGB> rgbList) {
    List<RGB> canMeasureList = new ArrayList<RGB> (rgbList.size());
    for (RGB rgb : rgbList) {
      if (!patchMap.containsKey(rgb)) {
        //如果沒有bufferMeasure, 則一定會進來
        canMeasureList.add(rgb);
      }
    }
    return canMeasureList;
  }

  private List<RGB> getMustMeasureRGBListInXYZBuffer(List<RGB> rgbList) {
    List<RGB> canMeasureList = new ArrayList<RGB> (rgbList.size());
    for (RGB rgb : rgbList) {
      if (!XYZMap.containsKey(rgb)) {
        //如果沒有bufferMeasure, 則一定會進來
        canMeasureList.add(rgb);
      }
    }
    return canMeasureList;
  }

  private static boolean BufferMeasure = true;
  void setBufferMeasure(boolean bufferMeasure) {
    BufferMeasure = bufferMeasure;
  }

  /**
   * 將rgbList中過濾出還沒有量測過的rgb數值.
   * 是否已經量測的依據是以map的內容物為主
   * @param rgbList List
   * @return List
   */
  protected List<RGB> getMustMeasureRGBList(List<RGB> rgbList) {
    if (!BufferMeasure) {
      return rgbList;
    }
    switch (bufferMode) {
      case Patch:
        return getMustMeasureRGBListInPatchBuffer(rgbList);
      case CIEXYZ:
        return getMustMeasureRGBListInXYZBuffer(rgbList);
      default:
        return null;
    }
  }

  private int patchIndex = 0;

  protected Patch getPatchFromFreshBuffer(RGB rgb) {
    switch (bufferMode) {
      case Patch: {
        Patch p = freshPatchMap.get(rgb);
        return p;
      }
      case CIEXYZ: {
        CIEXYZ XYZ = freshXYZMap.get(rgb);
        if (XYZ != null) {
          Patch p = new Patch("B" + (patchIndex++), XYZ, null, rgb);
          return p;
        }
      }
    }
    return null;
  }

  protected Patch getPatchFromBuffer(RGB rgb) {
    switch (bufferMode) {
      case Patch: {
        Patch p = patchMap.get(rgb);
        return p;
      }
      case CIEXYZ: {
        CIEXYZ XYZ = XYZMap.get(rgb);
        if (XYZ != null) {
          Patch p = new Patch("B" + (patchIndex++), XYZ, null, rgb);
          return p;
        }
      }
    }
    return null;
  }
}
