package shu.cms.colororder;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;

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
public class PCCSold {
  private final static double[][][] XYZValues = {
      {
      {
      65.5, 61.9, 43.5}, {
      49.5, 48.6, 36.8}, {
      14, 13, 8.5}, {
      4.7, 4.5, 3.3}, {
      61.6, 50.7, 30.8}, {
      41.2, 33.9, 19.5}, {
      24.3, 19.2, 11.4}, {
      8.7, 6.3, 4}, {
      52.1, 36.2, 18.9}, {
      21.9, 13.2, 5.7}, {
      35.9, 19.1, 7.5},
  }, {
      {
      66.5, 63.2, 40.5}, {
      47.7, 47, 32.1}, {
      15.5, 14.5, 8.9}, {
      5.1, 4.8, 3.7}, {
      65.5, 55.8, 27}, {
      48.8, 41.1, 18.6}, {
      29.1, 24, 11.9}, {
      10.4, 7.9, 3.5}, {
      56.8, 42.7, 13.9}, {
      30.9, 21.8, 5.2}, {
      49.6, 31.7, 5.1},
  }, {
      {
      71.5, 70.7, 38.3}, {
      50.3, 50.8, 31.5}, {
      15.9, 15.4, 9}, {
      6.2, 6.1, 4}, {
      67.7, 62.7, 21.6}, {
      51.9, 47.2, 16.3}, {
      31.8, 28, 10.5}, {
      12.6, 11, 3.4}, {
      63.2, 55.3, 11.1}, {
      37.7, 30.9, 5.8}, {
      60.4, 51.2, 7.6},
  }, {
      {
      73.2, 76, 39}, {
      50.6, 52.2, 32.2}, {
      15.5, 15.7, 8.8}, {
      5.1, 5.4, 3.8}, {
      71.9, 74, 22.4}, {
      53.3, 54.5, 19.4}, {
      31.1, 30.9, 9.9}, {
      16.1, 16.1, 4.3}, {
      70.5, 71.1, 12.2}, {
      38.6, 36.1, 5.8}, {
      67.2, 67.8, 7.8},
  }, {
      {
      63.2, 68.3, 40.1}, {
      47.3, 50.9, 31.9}, {
      15, 16.2, 9.3}, {
      5, 5.4, 3.9}, {
      55.6, 63.1, 20.3}, {
      39.5, 44.8, 15.9}, {
      24, 27.3, 10}, {
      10.5, 12.4, 3.2}, {
      44.2, 51.8, 10}, {
      21.4, 25.9, 5.5}, {
      35.3, 43.9, 6.4},
  }, {
      {
      51.2, 59.5, 40.9}, {
      41.1, 45.9, 32.5}, {
      12.3, 14, 9.9}, {
      3.7, 4.2, 3.2}, {
      42.6, 54, 34.6}, {
      30.7, 38.1, 23.6}, {
      18.7, 24, 15.5}, {
      6.4, 8.6, 5.3}, {
      25.8, 38, 21.9}, {
      9.2, 15.3, 9.1}, {
      13.5, 25.4, 11.5},
  }, {
      {
      48.8, 54.9, 49.5}, {
      40.2, 44.3, 40.8}, {
      11.8, 12.9, 11.9}, {
      3, 3.5, 3}, {
      32.3, 40.6, 46.4}, {
      22.2, 27.3, 30.2}, {
      9.9, 12.3, 15}, {
      4.9, 6.1, 8.1}, {
      22.5, 30.8, 40.2}, {
      6.6, 9.2, 14}, {
      8.6, 13.1, 23.1},
  }, {
      {
      49.4, 53.4, 49.1}, {
      41.1, 43.1, 49.5}, {
      12.3, 13.1, 12}, {
      3.5, 3.6, 3.5}, {
      32.6, 36.7, 37.1}, {
      21.6, 23.8, 28.2}, {
      12.1, 13.1, 17.1}, {
      4.9, 5.5, 8.3}, {
      22.9, 26.4, 45.2}, {
      7.6, 8.4, 15.7}, {
      11.9, 12.6, 30.1},
  }, {
      {
      50.3, 51.6, 47.2}, {
      40.3, 41.2, 37.4}, {
      11.8, 12, 11.4}, {
      3.7, 3.7, 3.5}, {
      37.5, 37.8, 43}, {
      24.3, 24.2, 24.8}, {
      11.5, 11.1, 14.2}, {
      4.8, 4.6, 7}, {
      26.6, 25.5, 39.4}, {
      9.8, 7.8, 17.8}, {
      13.1, 11.1, 24.9},
  }, {
      {
      54.6, 54.7, 45.8}, {
      41.4, 41.7, 35.4}, {
      11.9, 11.6, 10.3}, {
      3.2, 3.1, 3.1}, {
      46.8, 42.3, 42.7}, {
      28.9, 25.3, 24.4}, {
      13.2, 11.6, 12.6}, {
      4.9, 4.3, 4.8}, {
      30.7, 25.8, 31.8}, {
      14.3, 9.1, 17.1}, {
      15.7, 11, 20.4},
  }
  };

  private final static String[] TONE = {
      "p", "ltg", "g", "dkg", "lt", "sf", "d", "dk", "b", "dp", "v"
  };

  private final static String[] HUE = {
      "2:R", "4:rO", "6:yO", "8:Y", "10:YG", "12:G", "16:gB", "18:B", "20:V",
      "22:P",
  };

  protected final static double[][] getHuePlane(int hueIndex) {
    return XYZValues[hueIndex];
  }

  protected final static String[] getHueAndTone(int hueIndex, int index) {
    return new String[] {
        HUE[hueIndex], TONE[index]};
  }

  protected final static List<Patch> getHuePlanePatchList(int hueIndex) {
    double[][] huePlaneXYZValues = getHuePlane(hueIndex);
    int size = huePlaneXYZValues.length;
    List<Patch> patchList = new ArrayList<Patch> (size);
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = new CIEXYZ(huePlaneXYZValues[x]);
      XYZ.times(1. / 100);
      RGB rgb = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB);
      String[] hueAndTone = getHueAndTone(hueIndex, x);
      String name = hueAndTone[0] + "-" + hueAndTone[1];
      Patch p = new Patch(name, XYZ, null, rgb);
      patchList.add(p);
    }
    return Patch.Produce.LabPatches(patchList,
                                    Illuminant.D65.getNormalizeXYZ());
//    return patchList;
  }

  public static void main(String[] args) {

    List<Patch> patchList = getHuePlanePatchList(8);
    int size = patchList.size();
    double[] LChabH = new double[size];
    double[] LChptH = new double[size];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIELab Lab = p.getLab();
      CIELCh LCh = new CIELCh(Lab);
      LChabH[x] = LCh.C;
      RGB rgb = p.getRGB();
      rgb.changeMaxValue(RGB.MaxValue.Double255);

      CIEXYZ XYZ = p.getXYZ();
      IPT ipt = IPT.fromXYZ(XYZ);
      ipt.setScale(IPT.Scale.CIELab);
      CIELCh LChpt = new CIELCh(ipt);
      LChptH[x] = LChpt.C;
//      System.out.println(p.getName() + " " + LCh + "\t" + LChpt);
    }
    Plot2D plot = Plot2D.getInstance();
    plot.addLinePlot(null, LChabH);
    plot.addLinePlot(null, LChptH);
    plot.setVisible(true);
  }
}
