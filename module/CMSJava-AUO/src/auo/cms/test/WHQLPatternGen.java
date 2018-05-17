package auo.cms.test;

import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import shu.image.ImageUtils;
import java.io.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class WHQLPatternGen {
  public static void main(String[] args) {
//    whql(args);
//    hue(args);
    duplicate(args);
  }

  public static void duplicate(String[] args) {
    try {
      File dir = new File(
          "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect\\Demo");
      String targetdir =
          "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect\\Demo\\Duplicate";
      for (File f : dir.listFiles()) {
        if (f.isFile() && f.getName().indexOf(".db") == -1 ) {
          BufferedImage img = ImageUtils.loadImage(f.getAbsolutePath());
          int width = img.getWidth();
          BufferedImage img2 = new BufferedImage(width * 2,
                                                 img.getHeight(),
                                                 BufferedImage.TYPE_INT_RGB);
          Graphics g = img2.getGraphics();
          g.drawImage(img, 0, 0, null);
          g.drawImage(img, width, 0, null);

          ImageUtils.storeJPEGImage(targetdir + "\\" + f.getName(), img2);
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void hue(String[] args) {
    double[][] svValues = new double[][] {
        {
        33.333333333333336, 75.29411764705883}, {
        50.0, 75.29411764705883}, {
        58.333333333333336, 75.29411764705883}, {
        66.66666666666667, 75.29411764705883}, {
        100.0, 50.19607843137255}, {
        49.80392156862745, 100.0}, {
        100.0, 100.0}
    };
    int clips = svValues.length;

    final int imgwidth = 256;
    final int imgheight = 768;
    final double percent = 0.9;
//    int clips = 6;
    int gap = (int) (imgheight * (1 - percent) / clips + 1);
    final int height = (imgheight - gap) / clips;

    try {
      for (int h = 0; h < 360; h += 15) {
        System.out.println(h + ":");
        BufferedImage img = new BufferedImage(imgwidth, imgheight,
                                              BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, imgwidth, imgheight);
        for (int x = 0; x < clips; x++) {
//        for (double[] sv : svValues) {
          double[] sv = svValues[x];
          HSV hsv2 = new HSV(RGB.ColorSpace.sRGB, new double[] {h, sv[0], sv[1]});
          RGB rgb = hsv2.toRGB();
//          System.out.println(rgb);
//        System.out.println(rgb.getColor());

          int yaxis = height * x;
          int yend = height - gap;
          g.setColor(rgb.getColor());
          g.fillRect(0, yaxis + gap, imgwidth, yend);

        }

//        ImageUtils.storeTIFFImage("hue" + h + ".tif", img);
        ImageUtils.storeTIFFImage("hue" + String.format("%03d", h) + ".tif",
                                  img);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void whql(String[] args) {
    //LCDTarget.Instance.get(LCDTarget.Number.WHQL);
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    System.out.println(rgbList.size());
    HashMap<Integer, List<RGB>> map = new HashMap<Integer, List<RGB>> ();

    for (RGB rgb : rgbList) {
      rgb.setRGBColorSpace(RGB.ColorSpace.sRGB);
      CylindricalColorSpace hsv = new CylindricalColorSpace(rgb,
          CylindricalColorSpace.Strategy.H,
          CylindricalColorSpace.Strategy.S_HSV,
          CylindricalColorSpace.Strategy.V);

      if (hsv.S_HSV != 0) {
        List<RGB> values = map.get( (int) hsv.H);
        if (null == values) {
          values = new ArrayList<RGB> ();
        }
        values.add(rgb);
        map.put( (int) hsv.H, values);
      }
    }

    final int imgwidth = 256;
    final int imgheight = 768;
    final double percent = 0.9;
    int clips = 6;
    int gap = (int) (imgheight * (1 - percent) / clips + 1);
    final int height = (imgheight - gap) / clips;

    try {
      for (int key : map.keySet()) {
        List<RGB> values = map.get(key);
        System.out.println(key + ":");
        Collections.sort(values);
        BufferedImage img = new BufferedImage(imgwidth, imgheight,
                                              BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, imgwidth, imgheight);

        for (int x = 0; x < clips; x++) {
          RGB rgb = values.get(x);
          CylindricalColorSpace hsv = new CylindricalColorSpace(rgb,
              CylindricalColorSpace.Strategy.H,
              CylindricalColorSpace.Strategy.S_HSV,
              CylindricalColorSpace.Strategy.V);
          HSV hsv2 = new HSV(rgb);
//          System.out.println(rgb + " " + hsv + " " + hsv2);
          g.setColor(rgb.getColor());

          int yaxis = height * x;
          int yend = height - gap;

          g.fillRect(0, yaxis + gap, imgwidth, yend);
        }
        ImageUtils.storeTIFFImage("hue" + key + ".tif", img);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
