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
   * 將RGBpatchList的色塊,經由前導模式計算出XYZ,回傳成List<Patch>
   * @param RGBpatchList List
   * @return List
   */
  public List<Patch> produceForwardModelPatchList(final List<Patch>
                                                  RGBpatchList);

  /**
   * 將XYZpatchList的色塊,經由反推模式計算出RGB,回傳成List<Patch>
   * @param XYZpatchList List
   * @return List
   */
  public List<Patch> produceReverseModelPatchList(final List<Patch>
                                                  XYZpatchList);

}