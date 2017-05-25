package auo.cms.hsv.autotune;

import shu.cms.profile.ColorSpaceConnectedLUT;
import shu.cms.profile.ProfileColorSpace;

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
public class PreferredColorSpace {
  public ColorSpaceConnectedLUT clut;
  public ProfileColorSpace pcs;
  public PreferredColorSpace(ProfileColorSpace pcs,
                             ColorSpaceConnectedLUT clut) {
    this.pcs = pcs;
    this.clut = clut;
  }
}
