package shu.cms.hvs.pca;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;

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
public class ViewingConditions
    implements CAMViewingConditions {

  public ViewingConditions(CIEXYZ displayWhite, CIEXYZ ambientWhite,
                           Surround surround) {
    this(displayWhite, ambientWhite, displayWhite.Y / 5., surround);
  }

  public ViewingConditions(CIEXYZ displayWhite, CIEXYZ ambientWhite, double LA,
                           Surround surround) {
    this.displayWhite = displayWhite;
    this.ambientWhite = ambientWhite;
    this.LA = LA;
    this.surround = surround;
  }

  protected double LA;
  protected CIEXYZ displayWhite;
  protected CIEXYZ ambientWhite;
  protected Surround surround;
  protected double Radp = 0.6;

}
