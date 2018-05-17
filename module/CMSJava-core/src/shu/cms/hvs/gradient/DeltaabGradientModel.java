package shu.cms.hvs.gradient;

import java.util.*;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 計算色階之間的delta ab, 支援多種不同的色彩空間
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DeltaabGradientModel {
  public static enum DeltaabBy {
    xy, uvPrime, Lab, Jab, IPT, Luv
  }

  private GSDFGradientModel model;
  private CIEXYZ white = null;
  private CIECAM02 cam = null;

  public DeltaabGradientModel(GSDFGradientModel model) {
    this.model = model;
    this.white = model.white;
    this.cam = model.cam;
  }

  /**
   * 與白點的色偏計算
   * @param style Style
   * @return double[][]
   */
  public double[][] getBorderWhiteDeltaab(DeltaabBy style) {
    double[][] XYZValues = model.getBorderXYZValues();
    int size = XYZValues.length;
    double[][] deltaabArray = new double[size][];
    CIEXYZ XYZ = new CIEXYZ();
//    CIEXYZ D65WhiteXYZ = ca.getDestinationColor(white);
//    IPT iptWhite = new IPT(D65WhiteXYZ);
    IPT iptWhite = IPT.fromXYZ(white, white);
    CIEXYZ normalizedWhite = (CIEXYZ) white.clone();
    normalizedWhite.normalize(white);
    CIECAM02Color whiteJab = cam.forward(normalizedWhite);

    for (int x = 0; x < size; x++) {
      XYZ.setValues(XYZValues[x]);

      switch (style) {
        case xy: {
          deltaabArray[x]
              = DoubleArray.minus(XYZ.getxyValues(), white.getxyValues());
          break;
        }
        case uvPrime: {
          deltaabArray[x]
              = DoubleArray.minus(XYZ.getuvPrimeValues(),
                                  white.getuvPrimeValues());
          break;
        }
        case Lab: {
          CIELab Lab = new CIELab(XYZ, white);
          deltaabArray[x] = new double[] {
              Lab.a, Lab.b};
          break;
        }
        case Luv: {
          CIELuv Luv = new CIELuv(XYZ, white);
          deltaabArray[x] = new double[] {
              Luv.u, Luv.v};
          break;
        }
        case IPT: {
//          CIEXYZ D65XYZ = ca.getDestinationColor(XYZ);
//          IPT ipt = new IPT(D65XYZ);
          IPT ipt = IPT.fromXYZ(XYZ, white);
          deltaabArray[x] = new double[] {
              ipt.P - iptWhite.P, ipt.T - iptWhite.T};
          break;
        }
        case Jab: {
          CIEXYZ normalizedXYZ = (CIEXYZ) XYZ.clone();
          normalizedXYZ.normalize(white);
          CIECAM02Color Jab = cam.forward(normalizedXYZ);
          deltaabArray[x] = new double[] {
              Jab.ac - whiteJab.ac, Jab.bc - whiteJab.bc};
//          deltaabArray[x] = new double[] {
//              Jab.ac, Jab.bc};
          break;
        }
        default:
          return null;
      }
    }

    return deltaabArray;
  }

  public Plot2D plotWhiteDeltaab(DeltaabBy style) {
    Plot2D plotab = Plot2D.getInstance("White Delta ab -" + style.name());
    plotab.addLegend();

    double[][] wdeltaab = getBorderWhiteDeltaab(style);
    for (double[] dab : wdeltaab) {
      plotab.addCacheLinePlot("da", Color.red, 0, 254, dab[0]);
      plotab.addCacheLinePlot("db", Color.blue, 0, 254, dab[1]);
    }
    plotab.drawCachePlot();

    plotab.setVisible(true);
    return plotab;
  }

  public Plot2D plotAdjacentDeltaab(DeltaabBy style) {
    Plot2D plotab = Plot2D.getInstance("Adjacent Delta ab -" + style.name());
    plotab.addLegend();

    double[][][] deltaab = this.getBorderAdjacentDeltaab(style);
    for (double[][] dab : deltaab) {
      plotab.addCacheLinePlot("pre-da", Color.red, 0, 253, dab[0][0]);
      plotab.addCacheLinePlot("pre-db", Color.blue, 0, 253, dab[0][1]);
      plotab.addCacheLinePlot("next-da", Color.green, 0, 253, dab[1][0]);
      plotab.addCacheLinePlot("next-db", Color.black, 0, 253, dab[1][1]);
    }
    plotab.drawCachePlot();

    plotab.setVisible(true);
    return plotab;
  }

  /**
   * 相鄰接的色偏計算
   * @param style Style
   * @return double[][][]
   */
  public double[][][] getBorderAdjacentDeltaab(DeltaabBy style) {
    double[][] XYZValues = model.getBorderXYZValues();
    int size = XYZValues.length;
    double[][][] deltaabArray = new double[size - 2][2][];
    CIEXYZ XYZ1 = new CIEXYZ();
    CIEXYZ XYZ2 = new CIEXYZ();
    CIEXYZ XYZ3 = new CIEXYZ();

    for (int x = 1; x < size - 1; x++) {
      //前一個
      XYZ1.setValues(XYZValues[x - 1]);
      XYZ2.setValues(XYZValues[x]);
      //下一個
      XYZ3.setValues(XYZValues[x + 1]);

      switch (style) {
        case xy:
          deltaabArray[x - 1][0]
              = DoubleArray.minus(XYZ2.getxyValues(), XYZ1.getxyValues());
          deltaabArray[x - 1][1]
              = DoubleArray.minus(XYZ2.getxyValues(), XYZ3.getxyValues());
          break;
        case uvPrime:
          deltaabArray[x - 1][0]
              = DoubleArray.minus(XYZ2.getuvPrimeValues(),
                                  XYZ1.getuvPrimeValues());
          deltaabArray[x - 1][1]
              = DoubleArray.minus(XYZ2.getuvPrimeValues(),
                                  XYZ3.getuvPrimeValues());
          break;
        case Lab:
          CIELab Lab1 = new CIELab(XYZ1, white);
          CIELab Lab2 = new CIELab(XYZ2, white);
          CIELab Lab3 = new CIELab(XYZ3, white);
          deltaabArray[x - 1][0] = Lab2.getDeltaab(Lab1);
          deltaabArray[x - 1][1] = Lab2.getDeltaab(Lab3);
          break;
        case IPT:
          IPT ipt1 = IPT.fromXYZ(XYZ1, white);
          IPT ipt2 = IPT.fromXYZ(XYZ2, white);
          IPT ipt3 = IPT.fromXYZ(XYZ3, white);
          deltaabArray[x - 1][0]
              = Arrays.copyOfRange(DoubleArray.minus(ipt2.getValues(),
              ipt1.getValues()), 1, 2);
          deltaabArray[x - 1][1]
              = Arrays.copyOfRange(DoubleArray.minus(ipt2.getValues(),
              ipt3.getValues()), 1, 2);
          break;
        case Jab: {
          CIEXYZ normalXYZ1 = (CIEXYZ) XYZ1.clone();
          normalXYZ1.normalize(white);
          CIEXYZ normalXYZ2 = (CIEXYZ) XYZ2.clone();
          normalXYZ2.normalize(white);
          CIEXYZ normalXYZ3 = (CIEXYZ) XYZ3.clone();
          normalXYZ3.normalize(white);

          CIECAM02Color Jab1 = cam.forward(normalXYZ1);
          CIECAM02Color Jab2 = cam.forward(normalXYZ2);
          CIECAM02Color Jab3 = cam.forward(normalXYZ3);
          deltaabArray[x - 1][0] = new double[] {
              Jab2.ac - Jab1.ac, Jab2.bc - Jab1.bc};
          deltaabArray[x - 1][1] = new double[] {
              Jab2.ac - Jab3.ac, Jab2.bc - Jab3.bc};
          break;
        }
        default:
          return null;
      }
    }

    return deltaabArray;
  }

  public static void main(String[] args) {
    //==========================================================================
    // xtalk LCD model
    //==========================================================================
    LCDTarget.setRGBNormalize(false);
    String device = "cpt_320WF01SC";
//    String tag = "cut_9300k";
//    String tag = "cut_9300k";
    String tag = "ch1_1021";
    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
//    LCDTarget.FileType fileType = LCDTarget.FileType.XLS;
    LCDTarget.Source source = LCDTarget.Source.CA210;
//    LCDTarget.Source source = LCDTarget.Source.K10;

    LCDTarget lcdTarget9225 = LCDTarget.Instance.get(device,
        source,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.
        Native,
        LCDTargetBase.Number.Complex1021_4096_4108,
        fileType,
        null, tag);

    LCDTarget lcdTarget = lcdTarget9225.targetFilter.getRamp1021();
    //==========================================================================

    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);
//    gm.setSpatialAppearance(false);
//    gam.setHelmholtzKohlrauschMode(true);
//    gam.setHKStrategy(HKStrategy.Nayatani);
    gm.setHKStrategy(GSDFGradientModel.HKStrategy.CIECAM02);

    gm.setupImage(0, 255, RGBBase.Channel.W, 256, 256);
    DeltaabGradientModel dgm = new DeltaabGradientModel(gm);

    for (DeltaabBy s : DeltaabBy.values()) {
      dgm.plotWhiteDeltaab(s);
    }

  }
}
