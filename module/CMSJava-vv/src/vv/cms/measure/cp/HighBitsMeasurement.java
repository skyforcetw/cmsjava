package vv.cms.measure.cp;

import java.io.*;
import java.util.*;

import shu.cms.*;
//import shu.cms.applet.measure.tool.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class HighBitsMeasurement {

  private MeterMeasurement meterMeasurement;
  private MeasureBits icBits;

  public HighBitsMeasurement(MeterMeasurement meterMeasurement,
                             MeasureBits icBits) {
    this.meterMeasurement = meterMeasurement;
    this.icBits = icBits;
    produceRGBArray(icBits);
  }

  protected void replaceRGBAndName(double c, char replace, List<Patch>
      patchList) {
    //更動為實際的RGB以及名字
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = (RGB) p.getRGB().clone();
      if (rgb.isBlack()) {
        Patch nextp = patchList.get(x + 1);
        RGB nextRGB = nextp.getRGB();
        RGBBase.Channel ch = RGBBase.Channel.W;
        if (nextRGB.isPrimaryChannel()) {
          ch = nextRGB.getMaxChannel();
        }
        rgb.setValue(ch, rgb.getValue(ch) + c);
      }
      else {
        rgb.R = rgb.R != 0 ? rgb.R + c : 0;
        rgb.G = rgb.G != 0 ? rgb.G + c : 0;
        rgb.B = rgb.B != 0 ? rgb.B + c : 0;
      }

      rgb.rationalize();

      Patch.Operator.setRGB(p, rgb);
      Patch.Operator.setName(p, p.getName().replace('A', replace));

    }

  }

  public List<Patch> measure(MeasureBits measureBits,
                             LCDTargetBase.Number number) {
//    LCDTargetBase.Number number = whiteChannelOnly ? LCDTargetBase.Number.Ramp256 :
//        LCDTargetBase.Number.Ramp1024;

    //量測的導具
    List<RGB> ramp =
        LCDTarget.Instance.getRGBList(number);

    //載入code rgb0: 0 1 2 3..
    CPCodeLoader.load(rgb0, icBits.getMaxValue());
    //量測
    List<Patch> patchList0 = meterMeasurement.measure(ramp, null);

    List<Patch> patchList = new ArrayList<Patch> (patchList0);

    if (measureBits == MeasureBits.TenBits) {
      //載入code rgb1: 0.25 1.25 2.25...
      CPCodeLoader.load(rgb1, icBits.getMaxValue());
      //量測
      List<Patch> patchList1 = meterMeasurement.measure(ramp, null);
      //更動為實際的RGB以及名字
      replaceRGBAndName(0.25, 'B', patchList1);
      patchList.addAll(patchList1);
    }

    CPCodeLoader.load(rgb2, icBits.getMaxValue());
    List<Patch> patchList2 = meterMeasurement.measure(ramp, null);
    //更動為實際的RGB以及名字
    replaceRGBAndName(0.5, 'C', patchList2);
    patchList.addAll(patchList2);

    if (measureBits == MeasureBits.TenBits) {
      CPCodeLoader.load(rgb3, icBits.getMaxValue());
      List<Patch> patchList3 = meterMeasurement.measure(ramp, null);
      //更動為實際的RGB以及名字
      replaceRGBAndName(0.75, 'D', patchList3);
      patchList.addAll(patchList3);
    }

    return patchList;
  }

  protected List<String> produceNameList(List<RGB> rgbList) {
    int size = rgbList.size();
    List<String> nameList = new ArrayList<String> (size);
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      nameList.add(rgb.toString());
    }
    return nameList;
  }

  protected List<String> produceNameList(RGB[] rgbArray) {
    int size = rgbArray.length;
    List<String> nameList = new ArrayList<String> (size);

    //W
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      nameList.add(rgb.R + "," + rgb.G + "," + rgb.B);
    }

    //RGB
    for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
      for (int x = 1; x < size; x++) {
        RGB rgb = (RGB) rgbArray[x].clone();
        rgb.reserveValue(ch);
        nameList.add(rgb.R + "," + rgb.G + "," + rgb.B);
      }
    }

    return nameList;
  }

  protected void produceRGBArray(MeasureBits icBits) {
    int size = 0;
    switch (icBits) {
      case TenBits:
        size = 256;
        break;
      case TwelveBits:
        size = 257;
        break;
    }
    rgb0 = new RGB[size];
    rgb1 = new RGB[size];
    rgb2 = new RGB[size];
    rgb3 = new RGB[size];

    for (int x = 0; x < size; x++) {
      double c0 = x;
      double c1 = x + 0.25;
      double c2 = x + 0.5;
      double c3 = x + 0.75;
      rgb0[x] = new RGB(RGB.ColorSpace.unknowRGB, new double[] {c0, c0, c0},
                        RGB.MaxValue.Double255);
      rgb0[x].rationalize();
      rgb1[x] = new RGB(RGB.ColorSpace.unknowRGB, new double[] {c1, c1, c1},
                        RGB.MaxValue.Double255);
      rgb1[x].rationalize();
      rgb2[x] = new RGB(RGB.ColorSpace.unknowRGB, new double[] {c2, c2, c2},
                        RGB.MaxValue.Double255);
      rgb2[x].rationalize();
      rgb3[x] = new RGB(RGB.ColorSpace.unknowRGB, new double[] {c3, c3, c3},
                        RGB.MaxValue.Double255);
      rgb3[x].rationalize();

      if (meterMeasurement.isDo255InverseMode()) {
        rgb0[x].R = rgb0[x].R > 254 ? 254 : rgb0[x].R;
        rgb0[x].G = rgb0[x].G > 254 ? 254 : rgb0[x].G;
        rgb0[x].B = rgb0[x].B > 254 ? 254 : rgb0[x].B;
        rgb1[x].R = rgb1[x].R > 254 ? 254 : rgb1[x].R;
        rgb1[x].G = rgb1[x].G > 254 ? 254 : rgb1[x].G;
        rgb1[x].B = rgb1[x].B > 254 ? 254 : rgb1[x].B;
        rgb2[x].R = rgb2[x].R > 254 ? 254 : rgb2[x].R;
        rgb2[x].G = rgb2[x].G > 254 ? 254 : rgb2[x].G;
        rgb2[x].B = rgb2[x].B > 254 ? 254 : rgb2[x].B;
        rgb3[x].R = rgb3[x].R > 254 ? 254 : rgb3[x].R;
        rgb3[x].G = rgb3[x].G > 254 ? 254 : rgb3[x].G;
        rgb3[x].B = rgb3[x].B > 254 ? 254 : rgb3[x].B;
      }
    }
  }

  protected RGB[] rgb0 = null;
  protected RGB[] rgb1 = null;
  protected RGB[] rgb2 = null;
  protected RGB[] rgb3 = null;

  public static void main(String[] args) {
    final DummyMeter meter = new DummyMeter();
    final MeterMeasurement mt = new MeterMeasurement(meter, 24, true);
//    mt.setWaitTimes(0);

    HighBitsMeasurement tbm = new HighBitsMeasurement(mt,
        MeasureBits.TwelveBits);
    List<Patch>
        patchList = tbm.measure(MeasureBits.TenBits,
                                LCDTargetBase.Number.Ramp256W);
    System.out.println(patchList.size());
    try {
      LogoFile logoFile = new LogoFile("4084.logo", patchList, meter);
      logoFile.save();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    LCDTarget lcdTarget = LCDTarget.Instance.getFromLogo("4084.logo");
    for (Patch p : lcdTarget.getPatchList()) {
      System.out.println(p);
    }
    mt.close();
  }
}
