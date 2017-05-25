package shu.cms.plot;

import java.text.*;
import java.util.*;

import java.awt.*;

import org.math.plot.plots.*;
import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.profile.*;
import shu.plot.jzy3D;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.maths.Coord3d;
import shu.math.array.DoubleArray;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來顯示Gamut的類別
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GamutPlot {

  private Plot3D plot3D;
  private float alpha = 0.4f;
  private boolean paintRGBColor = true;
  private Color paintColor = Color.gray;
  private boolean fillShape = true;
  private boolean polygonDrawLine = false;
  private int step = 17;
  private Shape shape = Shape.Quadrangle;

  /**
   * 將六面畫完(RG GB RB CM MY YC)
   * @param step int
   * @param transfer RGB2ColorSpaceTransfer
   * @param skipBlack boolean 黑色是否略過不畫
   */
  protected void drawRGB2ColorSpace(int step, RGB2ColorSpaceTransfer transfer,
                                    boolean skipBlack) {
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.R, RGBBase.Channel.G, 0,
                        step, transfer, skipBlack);
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.G, RGBBase.Channel.B, 0,
                        step, transfer, skipBlack);
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.B, RGBBase.Channel.R, 0,
                        step, transfer, skipBlack);
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.R, RGBBase.Channel.G, 255,
                        step, transfer, skipBlack);
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.G, RGBBase.Channel.B, 255,
                        step, transfer, skipBlack);
    drawRGB2ColorSpace0(plot3D, RGBBase.Channel.B, RGBBase.Channel.R, 255,
                        step, transfer, skipBlack);
    setAxeLabel(transfer);
  }

  protected void setAxeLabel(RGB2ColorSpaceTransfer transfer) {
    ColorSpace cs = transfer.getColorSpace(new RGB());
    String[] names = cs.getBandNames();
    boolean isLumaFirst = isLumaFirst(cs);
    if (isLumaFirst) {
      this.plot3D.setAxeLabel(0, names[1]);
      this.plot3D.setAxeLabel(1, names[2]);
      this.plot3D.setAxeLabel(2, names[0]);
    }
    else {
      this.plot3D.setAxeLabel(0, names[0]);
      this.plot3D.setAxeLabel(1, names[1]);
      this.plot3D.setAxeLabel(2, names[2]);
    }
  }

  public GamutPlot() {
    this("GamutPlot", 600, 600);
  }

  /**
   * Gamut繪圖視窗的建構
   * @param title String
   * @param width int
   * @param height int
   */
  public GamutPlot(String title, int width, int height) {
//    plot3D = Plot3D.getInstance(title, width, height,
//                                Plot3D.Instance.JMathPlot3D);
    plot3D = Plot3D.getInstance(title, width, height,
                                Plot3D.Instance.LiveGraphics3D);
  }

  public GamutPlot(Plot3D plot3D) {
    this.plot3D = plot3D;
  }

  private final static double[] getDrawValues(ColorSpace colorSpace) {
    double[] values = colorSpace.getValues();
    double[] drawValues = new double[] {
        values[1], values[2], values[0]};
    return drawValues;
  }

  private final static double[] getDrawValues(ColorSpace colorSpace,
                                              boolean isLumaFirst) {
    if (isLumaFirst) {
      double[] values = colorSpace.getValues();
      double[] drawValues = new double[] {
          values[1], values[2], values[0]};
      return drawValues;
    }
    else {
      return colorSpace.getValues();
    }
  }

//  protected void drawShape(Plot3D p, Color c, ColorSpace lastLab,
//                           ColorSpace lab, ColorSpace preLab,
//                           ColorSpace nextPreLab) {
//    double[] lastValues = getDrawValues(lastLab);
//    double[] values = getDrawValues(lab);
//    double[] preValues = getDrawValues(preLab);
//    double[] nextPreValues = getDrawValues(nextPreLab);
//
//    switch (shape) {
//      case Triangle: {
//        //第一個polygon
//        PolygonPlot polygon = new PolygonPlot(null, c, alpha,
//                                              lastValues, values, preValues);
//        polygon.fill_shape = fillShape;
//        polygon.draw_lines = polygonDrawLine;
//        polygon.lineColor = fillShape ? Color.gray : c;
//        p.addPlot(polygon);
//        //第二個polygon
//        polygon = new PolygonPlot(null, c, alpha, preValues, values,
//                                  nextPreValues);
//        polygon.fill_shape = fillShape;
//        polygon.draw_lines = polygonDrawLine;
//        polygon.lineColor = fillShape ? Color.gray : c;
//        p.addPlot(polygon);
//      }
//      break;
//      case Dot: {
//        double[] v = values;
//        p.addScatterPlot("", c, v[0], v[1], v[2]);
//      }
//
//      break;
//      case Quadrangle: {
//        PolygonPlot polygon = new PolygonPlot(null, c, alpha,
//                                              lastValues, values, nextPreValues,
//                                              preValues);
//        polygon.fill_shape = fillShape;
//        polygon.draw_lines = polygonDrawLine;
//        polygon.lineColor = fillShape ? Color.gray : c;
//        p.addPlot(polygon);
//      }
//      break;
//    }
//
//  }

  private final static float[] floatArray(ColorSpace cs) {
    return DoubleArray.toFloatArray(cs.getValues());
  }

  private final static float[] floatArray(double[] values) {
    return DoubleArray.toFloatArray(values);
  }

  private final static boolean isLumaFirst(ColorSpace c) {
    boolean isLumaFirst = c instanceof LChConvertible ||
        c instanceof OpponentColorBase;
    return isLumaFirst;
  }

  protected void drawShape(Plot3D p, Color lastColor, Color c, Color preColor,
                           Color nextPreColor, ColorSpace last,
                           ColorSpace now, ColorSpace pre,
                           ColorSpace nextPre) {
    boolean isLumaFirst = isLumaFirst(now);
    double[] lastValues = getDrawValues(last, isLumaFirst);
    double[] values = getDrawValues(now, isLumaFirst);
    double[] preValues = getDrawValues(pre, isLumaFirst);
    double[] nextPreValues = getDrawValues(nextPre, isLumaFirst);
    boolean isjzy3D = ( (PlotWrapperInterface) p).getOriginalPlot() instanceof
        jzy3D;
//    isjzy3D = false;
    float jzyalpha = fillShape ? alpha : 0;

    switch (shape) {
      case Triangle: {
        if (isjzy3D) {
          jzy3D pjzy = (jzy3D) ( (PlotWrapperInterface) p).getOriginalPlot();

          Polygon polygon = new Polygon();

          polygon.add(new Point(new Coord3d(floatArray(lastValues)),
                                jzy3D.jzy3DColor(lastColor, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(values)),
                                jzy3D.jzy3DColor(c, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(nextPreValues)),
                                jzy3D.jzy3DColor(nextPreColor, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(preValues)),
                                jzy3D.jzy3DColor(preColor, jzyalpha)));

          polygon.setWireframeDisplayed(polygonDrawLine);
          polygon.setWireframeColor(jzy3D.jzy3DColor(fillShape ? Color.gray : c));
          pjzy.addDrawable(polygon);
        }
        else {
          //第一個polygon
          PolygonPlot polygon = new PolygonPlot(null, c, alpha,
                                                lastValues, values, preValues);
          polygon.fill_shape = fillShape;
          polygon.draw_lines = polygonDrawLine;
          polygon.lineColor = fillShape ? Color.gray : c;
          p.addPlot(polygon);
          //第二個polygon
          polygon = new PolygonPlot(null, c, alpha, preValues, values,
                                    nextPreValues);
          polygon.fill_shape = fillShape;
          polygon.draw_lines = polygonDrawLine;
          polygon.lineColor = fillShape ? Color.gray : c;
          p.addPlot(polygon);
        }
      }
      break;
      case Dot: {
        double[] v = values;
        p.addScatterPlot("", c, v[0], v[1], v[2]);
      }

      break;
      case Quadrangle: {
        if (isjzy3D) {
          jzy3D pjzy = (jzy3D) ( (PlotWrapperInterface) p).getOriginalPlot();

          Polygon polygon = new Polygon();
          polygon.add(new Point(new Coord3d(floatArray(lastValues)),
                                jzy3D.jzy3DColor(lastColor, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(values)),
                                jzy3D.jzy3DColor(c, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(nextPreValues)),
                                jzy3D.jzy3DColor(nextPreColor, jzyalpha)));
          polygon.add(new Point(new Coord3d(floatArray(preValues)),
                                jzy3D.jzy3DColor(preColor, jzyalpha)));

          polygon.setWireframeDisplayed(polygonDrawLine);
          polygon.setWireframeColor(jzy3D.jzy3DColor(fillShape ? Color.gray : c));
          pjzy.addDrawable(polygon);
        }
        else {
          PolygonPlot polygon = new PolygonPlot(null, c, alpha,
                                                lastValues, values,
                                                nextPreValues,
                                                preValues);
          polygon.fill_shape = fillShape;
          polygon.draw_lines = polygonDrawLine;
          polygon.lineColor = fillShape ? Color.gray : c;
          p.addPlot(polygon);
        }
      }
      break;
    }

  }

  /**
   * 一次畫一面
   * @param p Plot3D
   * @param first Channel 第一個channel
   * @param second Channel 第二個channel
   * @param thirdValue int 第三個channel的數值
   * @param step int
   * @param transfer RGB2ColorSpaceTransfer
   * @param skipBlack boolean 黑色是否略過不畫
   */
  protected void drawRGB2ColorSpace0(Plot3D p, RGBBase.Channel first,
                                     RGBBase.Channel second,
                                     int thirdValue, int step,
                                     RGB2ColorSpaceTransfer transfer,
                                     boolean skipBlack) {
    RGB rgb = new RGB();
    RGBBase.Channel third = RGBBase.Channel.getBesidePrimaryChannel(first,
        second);
    ArrayList<ColorSpace> preArray = null;
    ArrayList<Color> preCArray = null;
//    boolean skipBlack = true;

    for (int f = 0; f < 256; f += step) {
      ColorSpace last = null;
      Color lastC = null;
      int index = 0;
      ArrayList<ColorSpace> nowArray = new ArrayList<ColorSpace> ();
      ArrayList<Color> nowCArray = new ArrayList<Color> ();
      for (int s = 0; s < 256; s += step) {
        rgb.setValue(first, f, RGB.MaxValue.Int8Bit);
        rgb.setValue(second, s, RGB.MaxValue.Int8Bit);
        rgb.setValue(third, thirdValue, RGB.MaxValue.Int8Bit);
        Color c = paintRGBColor ? rgb.getColor() : paintColor;
        ColorSpace now = transfer.getColorSpace(rgb);

        if (preArray != null && last != null) {
          ColorSpace pre = preArray.get(index);
          Color preC = preCArray.get(index);
          ColorSpace nextPre = preArray.get(index + 1);
          Color nextPreC = preCArray.get(index + 1);
          index++;

          if (! (checkPoint(last) && checkPoint(now) && checkPoint(pre)
                 && checkPoint(nextPre))) {
            //有不合理的點就略過
            continue;
          }

          if (!skipBlack || !preC.equals(Color.black)) {
            drawShape(p, lastC, c, preC,
                      nextPreC, last, now, pre, nextPre);
          }

        }

        nowArray.add(now);
        nowCArray.add(c);
        last = now;
        lastC = c;
      }
      preArray = nowArray;
      preCArray = nowCArray;
    }
  }

  private double[] tmpValues = new double[3];

  /**
   * 檢查該色彩空間點是否正常
   * @param colorSpace ColorSpace
   * @return boolean
   */
  protected final boolean checkPoint(ColorSpace colorSpace) {
    colorSpace.getValues(tmpValues);
    if (Double.isNaN(tmpValues[0]) || Double.isNaN(tmpValues[1])
        || Double.isNaN(tmpValues[2])) {
      return false;
    }
    else {
      return true;
    }
  }

  public final static class RGB2XYZTransfer
      extends RGB2ColorSpaceTransfer {

    private RGB2XYZTransfer(ProfileColorSpace pcs) {
      this.pcs = pcs;
    }

    private ProfileColorSpace pcs;

    /**
     * 轉換出與RGB對應的色彩空間
     *
     * @param rgb RGB
     * @return ColorSpace
     */
    public ColorSpace _getColorSpace(RGB rgb) {
      double[] values = pcs.toD65CIEXYZValues(rgb.getValues());
      CIEXYZ XYZ = new CIEXYZ(values);
      return XYZ;
    }

    /**
     * getRGB
     *
     * @param colorspaceValues double[]
     * @return RGB
     */
    public RGB getRGB(double[] colorspaceValues) {
      throw new UnsupportedOperationException();
    }
  }

  public void drawCIELabGamut(ProfileColorSpace pcs) {
    RGB2XYZTransfer transfer = new RGB2XYZTransfer(pcs);
    transfer.addTransferFilter(new XYZ2LabTransfer(pcs.getD65ReferenceWhite()));
    drawRGB2ColorSpace(step, transfer, false);
  }

  public void drawCIExyYGamut(ProfileColorSpace pcs) {
    RGB2XYZTransfer transfer = new RGB2XYZTransfer(pcs);
    transfer.addTransferFilter(new XYZ2xyYTransfer());
    drawRGB2ColorSpace(step, transfer, true);
  }

//  public void drawGamut(RGB2ColorSpaceTransfer rgb2transfer,
//                        ColorSpaceTransfer ...transfers) {
//    for (ColorSpaceTransfer filter : transfers) {
//      rgb2transfer.addTransferFilter(filter);
//    }
//    drawRGB2ColorSpace(step, rgb2transfer);
//  }
  public void drawGamut(RGB2ColorSpaceTransfer rgb2transfer) {
    drawRGB2ColorSpace(step, rgb2transfer, false);
  }

  public void drawHSV() {
    drawHSV(plot3D, 6, 10, 5);
  }

  private void drawHSV(Plot3D p, int hStep, int sStep, int vStep) {
    ArrayList<OpponentColorAttribute> preArray = null;
    ArrayList<OpponentColorAttribute> preArray2 = null;

    for (int h = 0; h <= 360; h += hStep) {
      ArrayList<OpponentColorAttribute>
          nowArray = new ArrayList<OpponentColorAttribute> ();
      OpponentColorAttribute last = null;
      int index = 0;

      //畫柱子, s=100
      for (int v = 0; v <= 100; v += vStep) {
        HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h % 360, 100, v});
        OpponentColorAttribute now = new OpponentColorAttribute(hsv);

        if (preArray != null && last != null) {
          OpponentColorAttribute pre = preArray.get(index);
          OpponentColorAttribute nextPre = preArray.get(index + 1);
          index++;

          drawShape(p, last.toRGB().getColor(), now.toRGB().getColor(),
                    pre.toRGB().getColor(), nextPre.toRGB().getColor(),
                    last, now, pre, nextPre);
        }

        nowArray.add(now);
        last = now;
      }
      preArray = nowArray;

      ArrayList<OpponentColorAttribute>
          nowArray2 = new ArrayList<OpponentColorAttribute> ();
      OpponentColorAttribute last2 = null;
      int index2 = 0;

      //畫蓋子, v=100
      for (int s = 0; s <= 100; s += sStep) {
        HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h % 360, s, 100});
        OpponentColorAttribute now = new OpponentColorAttribute(hsv);

        if (preArray2 != null && last2 != null) {
          OpponentColorAttribute pre = preArray2.get(index2);
          OpponentColorAttribute nextPre = preArray2.get(index2 + 1);
          index2++;
          //畫上蓋
          drawShape(p, last2.toRGB().getColor(), now.toRGB().getColor(),
                    pre.toRGB().getColor(), nextPre.toRGB().getColor(),
                    last2, now, pre, nextPre);

          OpponentColorAttribute c0 = (OpponentColorAttribute) last2.clone();
          OpponentColorAttribute c1 = (OpponentColorAttribute) now.clone();
          OpponentColorAttribute c2 = (OpponentColorAttribute) pre.clone();
          OpponentColorAttribute c3 = (OpponentColorAttribute) nextPre.clone();

          double[] values = null;
          values = c0.getValues();
          values[0] = 0;
          c0.setValues(values);

          values = c1.getValues();
          values[0] = 0;
          c1.setValues(values);

          values = c2.getValues();
          values[0] = 0;
          c2.setValues(values);

          values = c3.getValues();
          values[0] = 0;
          c3.setValues(values);

          //畫下蓋, Luma都是黑的
          drawShape(p, Color.black, Color.black, Color.black, Color.black,
                    c0, c1, c2, c3);
        }

        nowArray2.add(now);
        last2 = now;
      }
      preArray2 = nowArray2;

    }
    String[] names = new HSV(RGB.ColorSpace.unknowRGB).getBandNames();
    p.setAxeLabel(0, "");
    p.setAxeLabel(1, "");
    p.setAxeLabel(2, names[2]);
//    p.setAxisVisible(false);
  }

  public void drawGamut(ProfileColorSpace pcs,
                        ColorSpaceTransfer ...transfers) {
    RGB2XYZTransfer transfer = new RGB2XYZTransfer(pcs);
    for (ColorSpaceTransfer filter : transfers) {
      transfer.addTransferFilter(filter);
    }
    drawRGB2ColorSpace(step, transfer, false);
  }

  public final static class XYZ2LabTransfer
      implements ColorSpaceTransfer {

    private CIEXYZ white;

    private XYZ2LabTransfer(CIEXYZ white) {
      this.white = white;
    }

    public ColorSpace transfer(ColorSpace colorSpace) {
      CIELab Lab = new CIELab( (CIEXYZ) colorSpace, white);
      return Lab;
    }
  };

  public final static class XYZ2xyYTransfer
      implements ColorSpaceTransfer {

//        @Override
    public ColorSpace transfer(ColorSpace colorSpace) {
      CIExyY xyY = new CIExyY( (CIEXYZ) colorSpace);
      if (xyY.x == 0 && xyY.y == 0) {
        return xyY;
      }
//      else {
      return xyY;
//      }
    }
  };

  public void drawRGBCubeInCIELab(RGB.ColorSpace rgbColorSpace) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(rgbColorSpace);
    drawCIELabGamut(pcs);
  }

  public void setVisible(boolean visible) {
    plot3D.setVisible(visible);
  }

  public void setVisible() {
    plot3D.setVisible(true);
  }

  public Plot3D getPlot3D() {
    return plot3D;
  }

  public static enum Shape {
    //三角形

    Triangle,
    //點
    Dot,
    //四邊形
    Quadrangle
  }

  public static void main(String[] args) {
//    Plot3D plot3D = Plot3D.getInstance("", Plot3D.Instance.JMathPlot3D);
//    Plot3D plot3D = Plot3D.getInstance("", Plot3D.Instance.LiveGraphics3D);
    Plot3D plot3D = Plot3D.getInstance("", Plot3D.Instance.jzy3D);
//
//    GamutPlot plot = new GamutPlot(Plot3D.getInstance());
//    GamutPlot plot = new GamutPlot("", 600, 600);
    GamutPlot plot = new GamutPlot(plot3D);

//    plot.setAlpha(.6f);
    plot.setAlpha(1f);
//    plot.setPaintColor(Color.red);
//    plot.setFillShape(false);
//    plot.setPolygonDrawLine(true);
//    plot.setShape(Shape.Triangle);
//    plot.setPaintRGBColor(false);
//    plot.setShape(Shape.Dot);
//    plot.setStep(5);

    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.sRGB);
    final DecimalFormat df = new DecimalFormat("##.##");
//    plot.drawCIELabGamut(pcs);

//    plot.setPolygonDrawLine(false);

    plot.drawRGBCubeInCIELab(RGB.ColorSpace.sRGB);

    plot.setAlpha(0f);
    plot.setFillShape(false);
    plot.setPolygonDrawLine(true);
    plot.setPaintRGBColor(false);
    plot.drawRGBCubeInCIELab(RGB.ColorSpace.AdobeRGB);
//    plot.drawCIExyYGamut(pcs);
//    plot.setPolygonDrawLine(false);
//    plot.setFillShape(false);

//    plot.drawGamut(new RGB2ColorSpaceAdapter() {
//      public ColorSpace _getColorSpace(RGB rgb) {
//        rgb.setRGBColorSpace(RGB.ColorSpace.sRGB);
//        HSV c = new HSV(rgb);
//        OpponentColorAttribute result = new OpponentColorAttribute(c);
//
//        return result;
//      }
//    });

//    plot.drawHSV();

//    plot.drawGamut(pcs,new ColorSpaceTransfer() {});

//    plot.drawGamut(pcs, new ColorSpaceTransfer() {
//      public ColorSpace transfer(ColorSpace colorSpace) {
//        IPT ipt = new IPT( (CIEXYZ) colorSpace);
//        return ipt;
//      }
//    });
//    plot3D.setAxisVisible(false);
    plot.setVisible();

  }

  /**
   * 是不是要畫線
   * @param drawLine boolean
   */
  public void setPolygonDrawLine(boolean drawLine) {
    this.polygonDrawLine = drawLine;
  }

  /**
   * 每個面是否要以顏色填滿
   * @param fillShape boolean
   */
  public void setFillShape(boolean fillShape) {
    this.fillShape = fillShape;
  }

  /**
   * 填上的顏色
   * @param paintColor Color
   */
  public void setPaintColor(Color paintColor) {
    this.paintColor = paintColor;
  }

  /**
   * 是否要以每個面本身的顏色去填滿
   * @param paintRGBColor boolean
   */
  public void setPaintRGBColor(boolean paintRGBColor) {
    this.paintRGBColor = paintRGBColor;
  }

  /**
   * RGB變化的step
   * @param step int
   */
  public void setStep(int step) {
    this.step = step;
  }

  public void setShape(Shape shape) {
    this.shape = shape;
  }

  /**
   * 設定透明程度
   * @param alpha float
   */
  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }
}
