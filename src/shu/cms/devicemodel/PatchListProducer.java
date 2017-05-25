package shu.cms.devicemodel;

import java.util.*;

import shu.cms.*;

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
public interface PatchListProducer {
  /**
   * �NRGBpatchList�����,�g�ѫe�ɼҦ��p��XXYZ,�^�Ǧ�List<Patch>
   * @param RGBpatchList List
   * @return List
   */
  public List<Patch> produceForwardModelPatchList(final List<Patch>
                                                  RGBpatchList);

  /**
   * �NXYZpatchList�����,�g�Ѥϱ��Ҧ��p��XRGB,�^�Ǧ�List<Patch>
   * @param XYZpatchList List
   * @return List
   */
  public List<Patch> produceReverseModelPatchList(final List<Patch>
                                                  XYZpatchList);

}