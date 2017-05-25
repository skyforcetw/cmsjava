package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CheckResult {
  public CheckResult(List<Patch> patchList, boolean allQualify,
      boolean allNonQualify) {
    this.patchList = patchList;
    this.passAllQualify = allQualify;
    this.allQualifyNonPass = allNonQualify;
  }

  CIEXYZ[] toCIEXYZArray() {
    List<CIEXYZ> XYZList = Patch.Filter.XYZList(patchList);
    int size = patchList.size();
    CIEXYZ[] XYZArray = XYZList.toArray(new CIEXYZ[size]);
    return XYZArray;
  }

  RGB[] toRGBArray() {
    List<RGB> rgbList = Patch.Filter.rgbList(patchList);
    int size = patchList.size();
    RGB[] rgbArray = rgbList.toArray(new RGB[size]);
    return rgbArray;
  }

  public List<Patch> patchList;
  /**
   * 通過所有的qualify(資格)
   */
  public boolean passAllQualify;
  /**
   * 所有的資格都沒有通過
   */
  public boolean allQualifyNonPass;
}
