package shu.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

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
public class LCDTargetUtils {
  /**
   * 從logoFilename載入成LCDTarget
   * @param logoFilename String
   * @return LCDTarget
   */
  public final static LCDTarget getLogoLCDTarget(String logoFilename) {
    return LCDTarget.Instance.getFromLogo(logoFilename);
  }

  /**
   * 從logoFilename載入成LCDTarget, 且把RGB替換成number內的Linear RGB
   * @param lcdTarget LCDTarget
   * @param number Number
   * @return LCDTarget
   */
  public final static LCDTarget getLCDTargetWithLinearRGB(LCDTarget
      lcdTarget, LCDTargetBase.Number number) {
    List<CIEXYZ> XYZList = lcdTarget.filter.XYZList();

    return getLCDTarget(XYZList, number);
  }

  /**
   * 將logoFilename的內容讀出, 但是以正常的0~255 rgb替換.
   * 是為了模仿已經載入LUT內容的顯示器.
   * @param logoFilename String
   * @return LCDTarget
   */
  public final static LCDTarget getLogoLCDTargetWithLinearRGB(String
      logoFilename) {
    LogoFileAdapter logo = new LogoFileAdapter(logoFilename);
    List<CIEXYZ> XYZList = logo.getXYZList();
    if (XYZList.size() != 256) {
      throw new IllegalArgumentException("logoFilename(" + logoFilename +
                                         ")'s XYZList.size() != 256");
    }
    return getLCDTarget(XYZList, LCDTargetBase.Number.Ramp256W);
  }

  /**
   * 以number的rgb及XYZList產生出LCDTarget
   * @param XYZList List
   * @param number Number
   * @return LCDTarget
   */
  protected final static LCDTarget getLCDTarget(List<CIEXYZ> XYZList,
      LCDTarget.Number number) {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(number);
    List<Patch>
        patchList = Patch.Produce.XYZRGBPatches(XYZList, rgbList);
    LCDTarget target = LCDTarget.Instance.get(patchList, number, false);
    return target;
  }

  /**
   * 以number的RGB替換lcdTarget內的RGB
   * @param lcdTarget LCDTarget
   * @param number Number
   * @return LCDTarget
   */
  protected final static LCDTarget getReplacedLCDTarget(LCDTarget lcdTarget,
      LCDTarget.Number number) {
    return getLCDTarget(lcdTarget.filter.XYZList(), number);
  }

  /**
   * 將rgbArray替代掉LCDTarget裡的Patch, 成為一個新的Patch List
   * @param target LCDTarget
   * @param rgbArray RGB[]
   * @return List
   */
  protected final static List<Patch> getReplacedPatchList(LCDTarget target,
      RGB[] rgbArray) {
    List<Patch> patchList = Patch.Produce.copyOf(target.getPatchList());
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = rgbArray[x];
      Patch.Operator.setRGB(p, rgb);
    }
    return patchList;
  }

  /**
   * 將target裡面的RGB替換成number裡的RGB
   * @param target LCDTarget
   * @param number Number
   * @return List
   */
  protected final static List<Patch> getReplacedPatchList(LCDTarget target,
      LCDTarget.Number number) {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(number);
    RGB[] rgbArray = new RGB[rgbList.size()];
    return getReplacedPatchList(target, rgbArray);
  }

}
