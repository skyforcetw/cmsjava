package shu.cms.applet.test;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.ui.*;
import shu.ui.*;

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
public class ColorMapShowerFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected DitherCanvas ditherCanvas1 = new DitherCanvas();

  public ColorMapShowerFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    getContentPane().setLayout(borderLayout1);
    GUIUtils.fullScreen(this);
    getContentPane().add(ditherCanvas1, java.awt.BorderLayout.CENTER);
//    ditherCanvas1.setImage(calculateHSB2Image(true, true, true, true,
//                                              this.getSize()));
    this.setVisible(true);
//    for (double L = 0; L <= 100; L += 5) {
    double L = 65;
    ditherCanvas1.setBufferedImage(calculateLCh2Image(L, true, true, true,
        this.getSize()));
    this.setTitle(String.valueOf(L));
    Thread.currentThread().sleep(50);
//    }

  }

  static Image calculateHSB2Image(boolean saturationChange, boolean R,
                                  boolean G, boolean B, Dimension size) {
    int height = size.height;
    int width = size.width;

    BufferedImage HSB2Image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_RGB);

    double[] hsbValues = new double[3];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {

        hsbValues[0] = ( ( (double) x) / width) * 360;
        if (saturationChange) {
          hsbValues[1] = ( ( (double) y) / height) * 100;
          hsbValues[2] = 100;
        }
        else {
          hsbValues[2] = ( ( (double) y) / height) * 100;
          hsbValues[1] = 100;
        }

        double[] rgbValues = HSV.toRGBValues(hsbValues);
        int codeR = R ? (int) (rgbValues[0] * 255) : 0;
        int codeG = G ? (int) (rgbValues[1] * 255) : 0;
        int codeB = B ? (int) (rgbValues[2] * 255) : 0;
        HSB2Image.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));
      }
    }

    return HSB2Image;
  }

  static BufferedImage calculateLCh2Image(double L, boolean R,
                                          boolean G, boolean B, Dimension size) {
    int height = size.height;
    int width = size.width;

    BufferedImage LCh2Image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_RGB);
    CIEXYZ white = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    double[] whiteValues = white.getValues();

    double[] LChValues = new double[3];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {

        LChValues[2] = ( ( (double) x) / width) * 360;
        LChValues[1] = ( ( (double) y) / height) * 120;
        LChValues[0] = L;

        double[] XYZValues = CIELCh.LChab2XYZValues(LChValues,
            whiteValues);
        double[] rgbValues = RGB.fromXYZValues(XYZValues,
                                               RGB.ColorSpace.sRGB);
        int codeR = R ? (int) (rgbValues[0] * 255) : 0;
        int codeG = G ? (int) (rgbValues[1] * 255) : 0;
        int codeB = B ? (int) (rgbValues[2] * 255) : 0;
        if (! (RGB.isLegal(codeR, RGB.MaxValue.Int8Bit) &&
               RGB.isLegal(codeG, RGB.MaxValue.Int8Bit) &&
               RGB.isLegal(codeB, RGB.MaxValue.Int8Bit))) {
          codeR = codeG = codeB = 0;
        }
//        else {
//          codeR = codeR > 255 || codeR < 0 ? 0 : codeR;
//          codeG = codeG > 255 || codeG < 0 ? 0 : codeG;
//          codeB = codeB > 255 || codeB < 0 ? 0 : codeB;
//        }

        LCh2Image.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));
      }
    }

    return LCh2Image;
  }

  public static void main(String[] args) {
    ColorMapShowerFrame colormapshowerframe = new ColorMapShowerFrame();
//    colormapshowerframe.setVisible(true);
  }
}
