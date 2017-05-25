package sky4s.test.icc;

import java.awt.color.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ICC {
  public ICC() {
  }

  protected static final void printProfileClass(ICC_Profile profile) {
    System.out.print("ProfileClass: ");
    switch (profile.getProfileClass()) {
      case ICC_Profile.CLASS_ABSTRACT:
        System.out.println("CLASS_ABSTRACT");
        break;
      case ICC_Profile.CLASS_COLORSPACECONVERSION:
        System.out.println("CLASS_COLORSPACECONVERSION");
        break;
      case ICC_Profile.CLASS_DEVICELINK:
        System.out.println("CLASS_DEVICELINK");
        break;
      case ICC_Profile.CLASS_DISPLAY:
        System.out.println("CLASS_DISPLAY");
        break;
      case ICC_Profile.CLASS_INPUT:
        System.out.println("CLASS_INPUT");
        break;
      case ICC_Profile.CLASS_NAMEDCOLOR:
        System.out.println("CLASS_NAMEDCOLOR");
        break;
      case ICC_Profile.CLASS_OUTPUT:
        System.out.println("CLASS_OUTPUT");
        break;
      default:
        System.out.println("unknow");
    }

  }

  protected static final void printColorSpaceType(ICC_Profile profile) {
    System.out.print("ColorSpaceType: ");
    switch (profile.getColorSpaceType()) {
      case ColorSpace.TYPE_CMY:
        System.out.println("CMY");
        break;
      case ColorSpace.TYPE_CMYK:
        System.out.println("CMYK");
        break;
      case ColorSpace.TYPE_GRAY:
        System.out.println("Gray");
        break;
      case ColorSpace.TYPE_HLS:
        System.out.println("Hls");
        break;
      case ColorSpace.TYPE_HSV:
        System.out.println("Hsv");
        break;
      case ColorSpace.TYPE_Lab:
        System.out.println("Lab");
        break;
      case ColorSpace.TYPE_Luv:
        System.out.println("Luv");
        break;
      case ColorSpace.TYPE_RGB:
        System.out.println("RGB");
        break;
      case ColorSpace.TYPE_XYZ:
        System.out.println("XYZ");
        break;
      case ColorSpace.TYPE_YCbCr:
        System.out.println("YCbCr");
        break;
      case ColorSpace.TYPE_Yxy:
        System.out.println("Yxy");
        break;
      default:
        System.out.println("unknow or others (" + profile.getColorSpaceType() +
                           ")");
    }
//    ColorSpace colorSpace = ColorSpace.getInstance(profile.getColorSpaceType());
//    System.out.println(colorSpace.getName(colorSpace.getType()));
  }

  public static final void print(ICC_Profile profile) {
    printColorSpaceType(profile);
    printProfileClass(profile);
  }

  public static void main(String[] args) {
    try {
      ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
//      ICC_Profile profile = ICC_Profile.getInstance("E70f0705.icm");
      if (profile instanceof ICC_ProfileRGB) {
        System.out.println("ICC_ProfileRGB");
        ICC_ProfileRGB profileRGB = (ICC_ProfileRGB) profile;
        float[][] matrix = profileRGB.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
          float[] row = matrix[i];
          for (int j = 0; j < row.length; j++) {
            System.out.print(matrix[i][j] + " ");
          }
          System.out.println("");
        }
      }
//      System.out.println("getColorSpaceType(): " + profile.getColorSpaceType());
//      System.out.println("getData(): " + profile.getData());
      System.out.println("getMajorVersion(): " + profile.getMajorVersion());
      System.out.println("getMinorVersion(): " + profile.getMinorVersion());
      System.out.println("getNumComponents(): " + profile.getNumComponents());
      System.out.println("getPCSType(): " + profile.getPCSType());
//      System.out.println("getProfileClass(): " + profile.getProfileClass());
      print(profile);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
