package shu.cms.util;

import java.awt.*;

import shu.math.*;

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

public enum Resolution {

  XGA(1024, 768),
  XGAplus("XGA+", 1152, 864),
  SXGA(1280, 1024),
  WXGAplus("WXGA+", 1440, 900),
  SXGAplus("SXGA+", 1400, 1050),
  WSXGA(1600, 1024),
  WSXGAplus("WSXGA+", 1680, 1050),
  UXGA(1600, 1200),
  WUXGA(1920, 1200),
  QXGA(2048, 1536),
  WQXGA(2560, 1600),
  QSXGA(2560, 1600),
  WXGA_1280x720("WXGA 1280x720", 1280, 720),
  WXGA_1280x768("WXGA 1280x768", 1280, 768),
  WXGA_1280x800("WXGA 1280x800", 1280, 800),
  WXGA_1360x768("WXGA 1360x768", 1360, 768),
  WXGA_1366x768("WXGA 1366x768", 1366, 768);

  Resolution(int width, int height) {
    this.name = name();
    this.width = width;
    this.height = height;
  }

  Resolution(String name, int width, int height) {
    this.name = name;
    this.width = width;
    this.height = height;
  }

  String name;
  int width;
  int height;

  public final int getWidth() {
    return width;
  }

  public final int getHeight() {
    return height;
  }

  public final String getName() {
    return name;
  }

  public int getDPI(double inch) {
    double dot = Maths.sqr(inch) / (Maths.sqr(width) + Maths.sqr(height));
    double widthInch = width * Math.sqrt(dot);
    return (int) (width / widthInch);
  }

  public final static int getDPI(double inch, Dimension screenSize) {
    int width = screenSize.width;
    int height = screenSize.height;
    double dot = Maths.sqr(inch) / (Maths.sqr(width) + Maths.sqr(height));
    double widthInch = width * Math.sqrt(dot);
    return (int) (width / widthInch);
  }

  public String toString() {
    return name + " " + width + "x" + height;
  }

  public final static Resolution getResolution(Dimension dimension) {
    for (Resolution r : Resolution.values()) {
      if (r.width == dimension.width && r.height == dimension.height) {
        return r;
      }
    }
    return null;
  }

  public final static Resolution getScreenResolution() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    return Resolution.getResolution(screenSize);
  }

  public static void main(String[] args) {
    Resolution r = getResolution(new Dimension(1366, 768));
    for (double dist = 10; dist < 50; dist++) {
      double dpi = r.getDPI(32);
      double pixels = ViewingParameters.numberPixels(dist, dpi, 1);
      System.out.println(pixels / 2.);
    }
  }
}
