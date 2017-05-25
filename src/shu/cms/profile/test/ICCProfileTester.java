package shu.cms.profile.test;

import java.io.*;
import java.util.*;

import java.awt.color.*;

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
public class ICCProfileTester {

  public static void main(String[] args) throws IOException {
//    ICC_Profile profile = ICC_Profile.getInstance(
//        "C:/WINDOWS/system32/spool/drivers/color/sRGB Color Space Profile.icm");
    ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
    ICC_ColorSpace cs = new ICC_ColorSpace(profile);

    float[] xyz = cs.toCIEXYZ(new float[] {.2f, .3f, .4f});
    System.out.println(Arrays.toString(xyz));
    float[] rgb = cs.fromCIEXYZ(xyz);
    System.out.println(Arrays.toString(rgb));
    System.out.println(rgb[0] * 255 + " " + rgb[1] * 255 + " " + rgb[2] * 255);
    float[] xyz2 = cs.toCIEXYZ(rgb);
    System.out.println(Arrays.toString(xyz2));

  }
}
