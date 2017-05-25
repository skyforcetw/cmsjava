package shu.cms.plot;

import java.util.*;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 可用來畫各種不特定的色彩空間, 只要定義好與RGB相對應轉換後的色彩空間,
 *  GamutPlot就可以繪出
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class RGB2ColorSpaceTransfer {
  /**
   * 轉換出與RGB對應的色彩空間
   * @param rgb RGB
   * @return ColorSpace
   */
  public ColorSpace getColorSpace(RGB rgb) {
    ColorSpace colorSpace = _getColorSpace(rgb);
    return getColorSpaceByTransferFilter(colorSpace);
  }

  public abstract ColorSpace _getColorSpace(RGB rgb);

  public abstract RGB getRGB(double[] colorspaceValues);

  public void addTransferFilter(ColorSpaceTransfer transfer) {
    transferList.add(transfer);
  }

  private ColorSpace getColorSpaceByTransferFilter(ColorSpace colorSpace) {
    ColorSpace result = colorSpace;
    for (ColorSpaceTransfer transfer : transferList) {
      result = transfer.transfer(result);
    }
    return result;
  }

  private List<ColorSpaceTransfer> transferList = new LinkedList<
      ColorSpaceTransfer> ();

}
