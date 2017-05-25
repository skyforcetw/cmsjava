package sky4s.test.icc;

import java.util.*;

import java.awt.color.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ICC_ProfileTester {
  public static void main(String[] args) {
    ICC_ProfileRGB profile = (ICC_ProfileRGB) ICC_Profile.getInstance(java.awt.
        color.ColorSpace.CS_sRGB);
    ICC_ColorSpace cs = new ICC_ColorSpace(profile);
    float[] rgb = new float[] {
        1, 1, 1};
    float[] xyz = cs.toCIEXYZ(rgb);
    rgb = cs.fromCIEXYZ(xyz);
    System.out.println(Arrays.toString(rgb));
    System.out.println(Arrays.toString(profile.getMediaWhitePoint()));
  }
}
