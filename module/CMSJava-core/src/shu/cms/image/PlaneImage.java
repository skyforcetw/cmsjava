package shu.cms.image;

import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.independ.IPT;
import shu.cms.hvs.cam.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 配合S-CIELAB和iCAM的影像處理方式的物件.
 * 以平面的方式去表示每一頻道的影像.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PlaneImage {
  public static enum Domain {
    XYZ, LMS, OPP, IPTfromXYZ, IPTfromLMS, RGB
  }

  protected Domain type;
  protected CAMConst.CATType catType;

  public Domain getType() {
    return type;
  }

  protected double[][][] planeImage;
//  protected DeviceIndependentImage DIImage;

  public double[][][] getPlaneImage() {
    return planeImage;
  }

  public double[][] getPlaneImage(int index) {
    return planeImage[index];
  }

  public void setPlaneImage(int index, double[][] planeImage) {
    this.planeImage[index] = planeImage;
  }

  public int getWidth() {
    return planeImage[0][0].length;
  }

  public int getHeight() {
    return planeImage[0].length;
  }

  /**
   *
   * @param DIImage DeviceIndependentImage
   * @param type Domain
   * @return PlaneImage
   * @deprecated
   */
  public static PlaneImage getInstance(DeviceIndependentImageOld DIImage,
                                       Domain type) {
    double[][][] planeImage = toPlaneImage(DIImage, type);
    PlaneImage pImage = new PlaneImage(DIImage, type, planeImage);
    return pImage;
  }

  public static PlaneImage getInstance(DeviceIndependentImage DIImage,
                                       Domain type) {
    double[][][] planeImage = toPlaneImage(DIImage, type);
    PlaneImage pImage = new PlaneImage(DIImage, type, planeImage);
    return pImage;
  }

  /**
   * 轉換到planeImage
   * @param image DeviceIndependentImageNew
   * @param type Type
   * @return double[][][]
   */
  protected static double[][][] toPlaneImage(DeviceIndependentImage image,
                                             Domain type) {
    int width = image.getWidth();
    int height = image.getHeight();
    double[][][] planeImage = new double[3][height][width];
    double[] values = new double[3];

    switch (type) {
      case RGB:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getRGBValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case XYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getXYZValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case LMS:
        if (image.getCatType() == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getLMSValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case OPP:
        if (image.getCatType() == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getOPPValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;

      case IPTfromXYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getIPTValuesFromXYZ(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;

      case IPTfromLMS:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getIPTValuesFromLMS(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      default:
        return null;
    }
    return planeImage;
  }

  /**
   * 轉換到planeImage
   * @param image DeviceIndependentImage
   * @param type Type
   * @return double[][][]
   * @deprecated
   */
  protected static double[][][] toPlaneImage(DeviceIndependentImageOld image,
                                             Domain type) {
    int width = image.getWidth();
    int height = image.getHeight();
    double[][][] planeImage = new double[3][height][width];
    double[] values = new double[3];

    switch (type) {
      case RGB:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getRGBValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case XYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getXYZValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case LMS:
        if (image.getCatType() == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getLMSValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      case OPP:
        if (image.getCatType() == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getOPPValues(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;

      case IPTfromXYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getIPTValuesFromXYZ(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;

      case IPTfromLMS:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values = image.getIPTValuesFromLMS(x, y, values);

            planeImage[0][y][x] = values[0];
            planeImage[1][y][x] = values[1];
            planeImage[2][y][x] = values[2];
          }
        }

        break;
      default:
        return null;
    }
    return planeImage;
  }

  /**
   *
   * @param DIImage DeviceIndependentImage
   * @param type Domain
   * @param planeImage double[][][]
   * @deprecated
   */
  protected PlaneImage(DeviceIndependentImageOld DIImage,
                       Domain type, double[][][] planeImage) {
    this.type = type;
    this.catType = DIImage.getCatType();
    this.planeImage = planeImage;
  }

  protected PlaneImage(DeviceIndependentImage DIImage,
                       Domain type, double[][][] planeImage) {
    this.type = type;
    this.catType = DIImage.getCatType();
    this.planeImage = planeImage;
  }

  public double[][][] getCIEXYZImage() {
    return toCIEXYZImage(planeImage, type, catType);
  }

  /**
   *
   * @param DIImage DeviceIndependentImage
   * @deprecated
   */
  public void restoreToDeviceIndependentImage(DeviceIndependentImageOld DIImage) {
    if (DIImage.getWidth() != this.getWidth() ||
        DIImage.getHeight() != this.getHeight()) {
      throw new IllegalArgumentException("width or height is not equal.");
    }
    double[][][] CIEXYZImage = getCIEXYZImage();
    int width = this.getWidth();
    int height = this.getHeight();

    switch (type) {
      case RGB:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            DIImage.setRGBValues(x, y, CIEXYZImage[y][x]);
          }
        }
        break;
      default:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            DIImage.setXYZValues(x, y, CIEXYZImage[y][x]);
          }
        }
        break;
    }

  }

  public void restoreToDeviceIndependentImage(DeviceIndependentImage DIImage) {
    if (DIImage.getWidth() != this.getWidth() ||
        DIImage.getHeight() != this.getHeight()) {
      throw new IllegalArgumentException("width or height is not equal.");
    }
    double[][][] CIEXYZImage = getCIEXYZImage();
    int width = this.getWidth();
    int height = this.getHeight();

    switch (type) {
      case RGB:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            DIImage.setRGBValues(x, y, CIEXYZImage[y][x]);
          }
        }
        break;
      default:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            DIImage.setXYZValues(x, y, CIEXYZImage[y][x]);
          }
        }
        break;
    }

  }

  protected static double[][][] toCIEXYZImage(double[][][]
                                              planeImage, Domain type,
                                              CAMConst.CATType catType) {
    int height = planeImage[0].length;
    int width = planeImage[0][0].length;
    double[][][] XYZImage = new double[height][width][3];
    double[] values = new double[3];

    switch (type) {
      case RGB:
      case XYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            XYZImage[y][x][0] = planeImage[0][y][x];
            XYZImage[y][x][1] = planeImage[1][y][x];
            XYZImage[y][x][2] = planeImage[2][y][x];
          }
        }

        break;
      case LMS:
        if (catType == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values[0] = planeImage[0][y][x];
            values[1] = planeImage[1][y][x];
            values[2] = planeImage[2][y][x];

            double[] XYZValues = LMS.toXYZValues(values, catType);

            XYZImage[y][x][0] = XYZValues[0];
            XYZImage[y][x][1] = XYZValues[1];
            XYZImage[y][x][2] = XYZValues[2];
          }
        }

        break;
      case OPP:
        if (catType == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values[0] = planeImage[0][y][x];
            values[1] = planeImage[1][y][x];
            values[2] = planeImage[2][y][x];

            //OPP->LMS->XYZ
//            double[] LMSValues = OPP.toLMSValues(values);
//            double[] XYZValues = LMS.toXYZValues(LMSValues, catType);

            //OPP->XYZ
            double[] XYZValues = OPP.toXYZValues(values);

            XYZImage[y][x][0] = XYZValues[0];
            XYZImage[y][x][1] = XYZValues[1];
            XYZImage[y][x][2] = XYZValues[2];
          }
        }

        break;

      case IPTfromXYZ:

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values[0] = planeImage[0][y][x];
            values[1] = planeImage[1][y][x];
            values[2] = planeImage[2][y][x];

            double[] XYZValues = IPT.toXYZValues(values);

            XYZImage[y][x][0] = XYZValues[0];
            XYZImage[y][x][1] = XYZValues[1];
            XYZImage[y][x][2] = XYZValues[2];
          }
        }

        break;

      case IPTfromLMS:
        if (catType == null) {
          throw new IllegalArgumentException("image.getCatType() == null");
        }

        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            values[0] = planeImage[0][y][x];
            values[1] = planeImage[1][y][x];
            values[2] = planeImage[2][y][x];

            double[] LMSValues = IPT.toLMSValues(values);
            double[] XYZValues = LMS.toXYZValues(LMSValues, catType);

            XYZImage[y][x][0] = XYZValues[0];
            XYZImage[y][x][1] = XYZValues[1];
            XYZImage[y][x][2] = XYZValues[2];
          }
        }

        break;
      default:
        return null;
    }
    return XYZImage;
  }

  public void setPlaneImage(double[][][] planeImage) {
    this.planeImage = planeImage;
  }

}
