package sky4s.test.imageio;

import java.io.*;
import java.util.*;

import java.awt.color.*;

import org.apache.sanselan.*;

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
public class SanselanTester {
  public SanselanTester() {
    super();
  }

  public static void main(String[] args) {
//    BufferedImage someImage = null;
//    byte someBytes[] = null;
//    File someFile = null;
//    InputStream someInputStream = null;
//    OutputStream someOutputStream = null;

    try {
      ICC_Profile profile = Sanselan.getICCProfile(new File(
          "BGR-Red-Ducati_WCS-Test-TriState.jpg"));
      ICC_Profile profile2 = ICC_Profile.getInstance(
          "Profile/Monitor from ProfileMaker/CE240W.icc");
      ICC_Profile profile3 = ICC_Profile.getInstance(
          "Profile/Monitor/jCMS_skyRGB_RGBColorSpace_20070716 Lab-Bradford.icc");
      ICC_ColorSpace cs = new ICC_ColorSpace(profile3);
      float[] result = cs.fromRGB(new float[] {.2f, .3f, .4f});
      System.out.println(Arrays.toString(result));
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (ImageReadException ex) {
      ex.printStackTrace();
    }
//
//// The Sanselan class provides a simple interface to the library.
//
//// how to read an image:
//    byte imageBytes[] = someBytes;
//    BufferedImage image_1 = Sanselan.getBufferedImage(imageBytes);
//
//// methods of Sanselan usually accept files, byte arrays, or inputstreams as arguments.
//    BufferedImage image_2 = Sanselan.getBufferedImage(imageBytes);
//    File file = someFile;
//    BufferedImage image_3 = Sanselan.getBufferedImage(file);
//    InputStream is = someInputStream;
//    BufferedImage image_4 = Sanselan.getBufferedImage(is);
//
//// Write an image.
//    BufferedImage image = someImage;
//    File dst = someFile;
//    ImageFormat format = ImageFormat.IMAGE_FORMAT_PNG;
//    Map optional_params = new Hashtable();
//    Sanselan.write(image, dst, format, optional_params);
//
//    OutputStream os = someOutputStream;
//    Sanselan.write(image, os, format, optional_params);
//
//// get the image's embedded ICC Profile, if it has one.
//    byte icc_profile_bytes[] = Sanselan.getICCProfileBytes(imageBytes);
//
//    ICC_Profile icc_profile = Sanselan.getICCProfile(imageBytes);
  }
}
