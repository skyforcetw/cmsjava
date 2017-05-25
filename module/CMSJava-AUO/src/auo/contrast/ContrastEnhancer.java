package auo.contrast;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.cms.image.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ContrastEnhancer {
  public ContrastEnhancer() {
    super();
  }

  private int averageDiv;
  int getFrameAverage(int pixelAverage, short lumiMedian,
                      int contrastStr, int darkMaxAdjust,
                      int brightMaxAdjust) {
    double str = contrastStr * .25;
    averageDiv = (int) Math.round(Math.abs(pixelAverage - lumiMedian) * str);
    boolean darkScene = isDarkScene(pixelAverage, lumiMedian);
    int maxAdjustTmp = darkScene ? darkMaxAdjust : brightMaxAdjust;
    int frameAverage = Math.min(maxAdjustTmp, averageDiv);
    return frameAverage;
  }

  private static boolean isDarkScene(int pixelAverage, short lumiMedian) {
    boolean darkScene = lumiMedian > pixelAverage;
    return darkScene;
  }

  public boolean isDarkScene() {
    return isDarkScene(pixelAverage, lumiMedian);
  }

  static int getPixelAverage(short[][] YImage, boolean pixelFilter,
                             short lumiMedian) {
    int h = YImage.length;
    int w = YImage[0].length;
    long average = 0;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        short v = YImage[y][x];
        if (pixelFilter && (v == 0 || v == 1020)) {
          average += lumiMedian * 4;
        }
        else {
          average += v;
        }
      }
    }
    double resultD = average / (h * w * 1023.);
    int resultI = (int) (resultD * 256);
    return resultI;
  }

  static int getPoint1Tmp(int darkTurnPoint, int darkOffset) {
    if (0 == darkTurnPoint) {
      if (darkOffset >= 32) {
        return 0;
      }
      else {
        return 32 - darkOffset;
      }
    }
    else if (1 == darkTurnPoint) {
      return 64 - darkOffset;
    }
    else {
      return -1;
    }
  }

  static int getPoint2Tmp(int brightTurnPoint, int brightOffset) {
    if (0 == brightTurnPoint) {
      if (brightOffset >= 32) {
        return 255;
      }
      else {
        return 224 + brightOffset;
      }
    }
    else if (1 == brightTurnPoint) {
      return 192 + brightOffset;
    }
    else {
      return -1;
    }
  }

//  static boolean InversePoint1 = false;
//  public void setInversePoint1(boolean inverse) {
//    InversePoint1 = inverse;
//  }

  static int getPoint1(boolean darkScene, int point1Tmp, int frameAverage,
                       int darkDR, int brightDR) {
    if (darkScene) {
      return point1Tmp + (int) Math.round(frameAverage * darkDR / 8.);
    }
    else {
      return point1Tmp - (int) Math.round(frameAverage * brightDR / 8.);
    }
  }

  static int getPoint2(boolean darkScene, int point2Tmp, int frameAverage) {
    if (darkScene) {
      return point2Tmp + frameAverage;
    }
    else {
      return point2Tmp - frameAverage;
    }
  }

  public void setLumiMedian(short lumiMedian) {
    this.lumiMedian = lumiMedian;
    recalculatePixelAverage();
    recalculateFrameAverage();
  }

  private short[][] YImage;
  private int pixelAverage;
  private int frameAverage;

  private short lumiMedian = 128;
  private int contrastStr = 4;
  private int darkMaxAdjust = 15;
  private int brightMaxAdjust = 20;
  private int darkTurnPoint = 0;
  private int brightTurnPoint = 1;
  private int darkDR = 2;
  private int brightDR = 2;
  private int darkOffset = 10;
  private int brightOffset = 15;
  public void setDarkTurnPoint(int tp) {
    this.darkTurnPoint = tp;
  }

  public void setBrightTurnPoint(int tp) {
    this.brightTurnPoint = tp;
  }

  public void setContrastStr(int str) {
    this.contrastStr = str;
    recalculateFrameAverage();
  }

  public void setDarkMaxAdjust(int adjust) {
    this.darkMaxAdjust = adjust;
    recalculateFrameAverage();
  }

  public void setBrightMaxAdjust(int adjust) {
    this.brightMaxAdjust = adjust;
    recalculateFrameAverage();
  }

  public boolean isYImageEmpty() {
    return YImage == null;
  }

  private void recalculatePixelAverage() {
    if (null == YImage) {
      return;
    }
    pixelAverage = getPixelAverage(YImage, pixelFilter, lumiMedian);

  }

  private void recalculateFrameAverage() {
    if (null == YImage) {
      return;
    }

    frameAverage = getFrameAverage(pixelAverage, lumiMedian, contrastStr,
                                   darkMaxAdjust, brightMaxAdjust);

  }

  public void setYImage(short[][] YImage) {
    this.YImage = YImage;
    recalculatePixelAverage();
    recalculateFrameAverage();
  }

  int[] getPoint12() {
    boolean darkScene = isDarkScene(pixelAverage, lumiMedian);
    int p1t = getPoint1Tmp(darkTurnPoint, darkOffset);
    int p2t = getPoint2Tmp(brightTurnPoint, brightOffset);
    int p1 = getPoint1(darkScene, p1t, frameAverage, darkDR, brightDR);
    int p2 = getPoint2(darkScene, p2t, frameAverage);
    p1 = p1 < 0 ? 0 : p1;
    p2 = p2 < 0 ? 0 : p2;
    p1 = p1 > 255 ? 255 : p1;
    p2 = p2 > 255 ? 255 : p2;
    return new int[] {
        p1, p2};
  }

  public int[] getP1P2InOut() {
    return new int[] {
        p1In, p1Out, p2In, p2Out};
  }

  private int p1In, p1Out, p2In, p2Out;
  public int[] getMappingCurve() {
    int[] p1p2 = getPoint12();
    final int Length = 256;
    int[] curve = new int[Length];
    int x1 = p1In = darkTurnPoint == 1 ? 64 : 32;
    int x2 = p2In = brightTurnPoint == 1 ? 192 : 224;
    p1Out = p1p2[0];
    p2Out = p1p2[1];

    for (int x = 0; x <= x1; x++) {
      double v = Interpolation.linear(0, x1, 0, p1p2[0], x);
      curve[x] = ( (int) Math.round(v));
    }
    for (int x = x1 + 1; x <= x2; x++) {
      double v = Interpolation.linear(x1 + 1, x2, p1p2[0], p1p2[1], x);
      curve[x] = ( (int) Math.round(v));
    }
    for (int x = x2 + 1; x < Length; x++) {
      double v = Interpolation.linear(x2 + 1, Length - 1, p1p2[1], 255, x);
      curve[x] = ( (int) Math.round(v));
    }
    return curve;
  }

  private boolean pixelFilter;
  public void setPixelFilter(boolean pixelFilter) {
    this.pixelFilter = pixelFilter;
    recalculatePixelAverage();
    recalculateFrameAverage();
  }

  public void setDarkOffset(int darkOffset) {
    this.darkOffset = darkOffset;
  }

  public void setBrightOffset(int brightOffset) {
    this.brightOffset = brightOffset;
  }

  public void setBrightDR(int brightDR) {
    this.brightDR = brightDR;
  }

  public void setDarkDR(int darkDR) {
    this.darkDR = darkDR;
  }

  public void setPixelAverage(int pixelAverage) {
    this.pixelAverage = pixelAverage;
    recalculateFrameAverage();
  }

  public static short[][] getYImage(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    WritableRaster raster = image.getRaster();
    int[] rgbValues = new int[3];
    short[][] YImage = new short[h][w];

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        raster.getPixel(x, y, rgbValues);
        short v = (short) (rgbValues[0] + rgbValues[1] * 2 + rgbValues[2]);
        YImage[y][x] = v;

      }
    }
    return YImage;
  }

  public static void main(String[] args) {
    BufferedImage image = null;
    try {
      image = ImageUtils.loadImage("contrast/015.jpg");
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    ContrastEnhancer ce = new ContrastEnhancer();
    ce.setYImage(getYImage(image));
    int[] curve = ce.getMappingCurve();
    System.out.println(Arrays.toString(ce.getP1P2InOut()));
    Plot2D p = Plot2D.getInstance();
    for (int x = 0; x < curve.length; x++) {
      int v = curve[x];
      p.addCacheScatterLinePlot("", x, v);
    }
    p.setVisible();
    p.setFixedBounds(0, 0, 255);
    p.setFixedBounds(1, 0, 255);
  }

  public int getFrameAverage() {
    return frameAverage;
  }

  public int getPixelAverage() {
    return pixelAverage;
  }

  public int getAverageDiv() {
    return averageDiv;
  }
}
