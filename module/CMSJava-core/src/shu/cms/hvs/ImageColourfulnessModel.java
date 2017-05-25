package shu.cms.hvs;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.image.*;
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
public class ImageColourfulnessModel {

  public static enum Metric {
    One, Two, Three;
  }

  public static enum Attribute {
    NotColourful(1), SlightlyColourful(2), ModeratelyColourful(3),
    AveragelyColourful(4),
    QuiteColourful(5), HighlyColourful(6), ExtremelyColourful(7);

    Attribute(int grade) {
      this.grade = grade;
    }

    int grade;
  }

  public ImageColourfulnessModel(ProfileColorSpace pcs,
                                 CAMConst.CATType catType) {
    this.pcs = pcs;
    this.catType = catType;
  }

  protected ProfileColorSpace pcs;
  protected CAMConst.CATType catType;

  public Attribute getAttribute(Metric metric, double v) {
    switch (metric) {
      case One:
        if (v >= 42) {
          return Attribute.ExtremelyColourful;
        }
        else if (v >= 32) {
          return Attribute.HighlyColourful;
        }
        else if (v >= 24) {
          return Attribute.QuiteColourful;
        }
        else if (v >= 19) {
          return Attribute.AveragelyColourful;
        }
        else if (v >= 13) {
          return Attribute.ModeratelyColourful;
        }
        else if (v >= 6) {
          return Attribute.SlightlyColourful;
        }
        else {
          return Attribute.NotColourful;
        }

      case Two:
        if (v >= 54) {
          return Attribute.ExtremelyColourful;
        }
        else if (v >= 43) {
          return Attribute.HighlyColourful;
        }
        else if (v >= 32) {
          return Attribute.QuiteColourful;
        }
        else if (v >= 25) {
          return Attribute.AveragelyColourful;
        }
        else if (v >= 18) {
          return Attribute.ModeratelyColourful;
        }
        else if (v >= 8) {
          return Attribute.SlightlyColourful;
        }
        else {
          return Attribute.NotColourful;
        }

      case Three:
        if (v >= 109) {
          return Attribute.ExtremelyColourful;
        }
        else if (v >= 82) {
          return Attribute.HighlyColourful;
        }
        else if (v >= 59) {
          return Attribute.QuiteColourful;
        }
        else if (v >= 45) {
          return Attribute.AveragelyColourful;
        }
        else if (v >= 33) {
          return Attribute.ModeratelyColourful;
        }
        else if (v >= 15) {
          return Attribute.SlightlyColourful;
        }
        else {
          return Attribute.NotColourful;
        }

      default:
        return null;
    }

  }

  public Attribute getAttribute(Metric metric, BufferedImage bufferedImage) {
    double v = getMetric(bufferedImage, metric);
    System.out.println(v);
    return getAttribute(metric, v);
  }

  public double getMetric(BufferedImage bufferedImage, Metric metric) {
    switch (metric) {
      case One:
        return getMetric1(bufferedImage);
      case Two:
        return getMetric2(bufferedImage);
      case Three:
        return getMetric3(bufferedImage);
    }
    return -1;
  }

  public double getMetric1(BufferedImage bufferedImage) {
    DeviceIndependentImage diImage = DeviceIndependentImage.getInstance(
        bufferedImage, pcs, catType);
    return -1;
  }

  public double getMetric2(BufferedImage bufferedImage) {
    return -1;
  }

  public double getMetric3(BufferedImage bufferedImage) {
    int h = bufferedImage.getHeight();
    int w = bufferedImage.getWidth();
    double[] rgbValues = new double[3];
    RGB rgb = new RGB(RGB.ColorSpace.sRGB, RGB.MaxValue.Int8Bit);
    double[] rg = new double[h * w];
    double[] yb = new double[h * w];

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        bufferedImage.getRaster().getPixel(x, y, rgbValues);
        rgb.setValues(rgbValues);
        rg[x + y * w] = rgb.getRG();
        yb[x + y * w] = rgb.getYB();
      }
    }

    double sigmarg = Maths.std(rg);
    double sigmayb = Maths.std(yb);
    double murg = Maths.mean(rg);
    double muyb = Maths.mean(yb);
    double sigmargyb = Math.sqrt(Maths.sqr(sigmarg) + Maths.sqr(sigmayb));
    double murgyb = Math.sqrt(Maths.sqr(murg) + Maths.sqr(muyb));
    return sigmargyb + 0.3 * murgyb;
  }

  public static void main(String[] args) throws Exception {
    BufferedImage hats = ImageUtils.loadImage(
        "DSC0449.jpg");
    ImageColourfulnessModel icm = new ImageColourfulnessModel(null, null);
//   double m=icm.getMetric3(hats);
//   System.out.println(m+" "+ );
    Attribute a = icm.getAttribute(Metric.Three, hats);
    System.out.println(a);
  }
}
