package shu.cms.measure.intensity;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: CMSJava-core</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ComponentFetcher {
  private MaxMatrixIntensityAnalyzer analyzer;
  public ComponentFetcher(MaxMatrixIntensityAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  public static void main(String[] args) {
//        ComponentFetcher componentfetcher = new ComponentFetcher();
  }

  public List<Component> fetchComponent(List<CIEXYZ> XYZList) {
    return fetchComponent(null, XYZList);
  }

  private boolean minusMode = false;

  public List<Component> fetchComponent(List<RGB> rgbList,
      List<CIEXYZ> XYZList) {
    List<Component> componentList = new ArrayList<Component> ();
    int size = minusMode ? XYZList.size() - 1 : XYZList.size();
    for (int x = 0; x < size; x++) {
      int code = size - 1 - x;
      RGB rgb = null != rgbList ? rgbList.get(x) :
          new RGB(code, code, code);
      CIEXYZ XYZ = XYZList.get(x);
      RGB intensity = analyzer.getIntensity(XYZ);
      Component c = new Component(rgb, intensity, XYZ);
      componentList.add(c);
    }
    return componentList;
  }

  public MaxMatrixIntensityAnalyzer getAnalyzer() {
    return analyzer;
  }

  public void setMinusMode(boolean minusMode) {
    this.minusMode = minusMode;
  }

}
