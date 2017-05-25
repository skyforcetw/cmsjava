package shu.cms.image.test;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorformat.adapter.*;
import shu.cms.image.*;

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
public class DCAppearanceImageConvertTester {
  protected static String[] profiles = new String[] {
      "s5pro/s5p-f1.icc",
      "s5pro/s5p-f1a.icc",
      "s5pro/s5p-f1b.icc",
      "s5pro/s5p-f1c.icc",
      "s5pro/s5p-f2.icc",
      "e1/e1-def.icc",
      "e1/e1-por.icc",
      "e3/e3-nat.icc",
      "e3/e3-por.icc",
      "e3/e3-vid.icc",
  };

  protected static String[] images = new String[] {
      "d100/DSC_0084.JPG",
      "d100/DSC_0293.JPG",
      "d100/DSC_0437.JPG",
      "d100/DSC_2190.JPG",
      "d100/DSC_4956.JPG",
  };

  public static void main(String[] args) throws IOException {
    GMBICCProfileAdapter source = new GMBICCProfileAdapter(
        "Measurement Files/Camera/d100/d100.icc");

    for (String imgName : images) {
      BufferedImage img = ImageUtils.loadImage("Image/Profile/" + imgName);
      for (String profileName : profiles) {
        GMBICCProfileAdapter target = new GMBICCProfileAdapter(
            "Measurement Files/Camera/" + profileName);

        DCAppearanceImageConverter converter = new DCAppearanceImageConverter(
            source, target);
        converter.produceLUT();
        converter.storeLUT();
        BufferedImage result = converter.convert(img);
        String resultFilename = imgName.substring(imgName.lastIndexOf('/') + 1);
        resultFilename = resultFilename.substring(0,
                                                  resultFilename.indexOf(".JPG"));
        profileName = profileName.substring(profileName.indexOf('/') + 1);
        profileName = profileName.substring(0, profileName.indexOf(".icc"));
        resultFilename = resultFilename + "-" + profileName + ".jpg";
//        System.out.println(resultFilename);

        ImageUtils.storeJPEGImage(resultFilename, result);
      }
    }
  }
}
