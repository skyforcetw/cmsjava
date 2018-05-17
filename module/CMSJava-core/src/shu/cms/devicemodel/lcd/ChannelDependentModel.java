package shu.cms.devicemodel.lcd;

import java.io.*;
import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.*;
import shu.cms.lcd.*;
import shu.util.*;

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
public abstract class ChannelDependentModel
    extends LCDModel {
  protected LCDTarget xtalkLCDTarget;

  /**
   * 如果有已儲存的model存檔則載入, 否則不動作
   * @return ChannelDependentModel
   */
  public final ChannelDependentModel loadStoreInstanceWhileExist() {
    ChannelDependentModel store = getStoreInstance();
    if (store != null) {
      return store;
    }
    else {
      return this;
    }
  }

  /**
   * loading已儲存的model存檔
   * @return ChannelDependentModel
   */
  public final ChannelDependentModel getStoreInstance() {
    String storefilename = getStoreFilename();
    String[] filenames = new String[] {
        storefilename, storefilename + ".xml"};

    for (String filename : filenames) {
      File file = new File(filename);
      if (file.exists()) {
        file = null;
        DeviceCharacterizationModel dcm = null;
        if (filename.lastIndexOf(".xml") != -1) {
          dcm = Load.modelAsXML(filename);
        }
        else {
          dcm = Load.model(filename);
        }
        if (dcm instanceof ChannelDependentModel) {
          return (ChannelDependentModel) dcm;
        }
        else {
          return null;
        }
      }
      else {
        file = null;
//        return null;
      }
    }
    return null;
  }

  public ChannelDependentModel(LCDModelFactor factor) {
    super(factor);
  }

  public ChannelDependentModel(LCDTarget lcdTarget, LCDTarget xtalkLCDTarget) {
    super(lcdTarget);
    this.xtalkLCDTarget = xtalkLCDTarget;
  }

  public ChannelDependentModel(LCDTarget lcdTarget) {
    this(lcdTarget, lcdTarget);
  }

  protected final String getStoreFilename() {
    if (xtalkLCDTarget != null) {
      return lcdTarget.getDescription() +
          "+" + xtalkLCDTarget.getDescription() + "." +
          Utils.getAcronym(getDescription());
    }
    else {
      return lcdTarget.getDescription() + "." +
          Utils.getAcronym(getDescription());

    }
  }

  /**
   * 從xtalkLCDTarget取得ch的elementValues(所有的code)
   * @param ch Channel
   * @return double[]
   */
  protected double[] getXTalkElementValues(RGBBase.Channel ch) {
    return getElementValues(this.xtalkLCDTarget, ch);
  }

  /**
   * 取得pixel element value的變化間隔
   * @param lcdTarget LCDTarget
   * @param ch Channel
   * @return double[]
   */
  private static double[] getElementValues(LCDTarget lcdTarget,
                                           RGBBase.Channel ch) {
    Set<Double> doubleSet = new TreeSet<Double> ();
    int size = lcdTarget.size();
    for (int x = 0; x < size; x++) {
      double v = lcdTarget.getPatch(x).getRGB().getValue(ch);
      doubleSet.add(v);
    }

    int setSize = doubleSet.size();
    double[] values = new double[setSize];
    int index = 0;
    for (double d : doubleSet) {
      values[index++] = d;
    }
    return values;
  }

  protected String getTrainingTarget() {
    StringBuilder buf = new StringBuilder();
    buf.append("Training Target1: " + lcdTarget.getDescription() + "\n");
    if (xtalkLCDTarget != null) {
      buf.append("Training Target2: " + this.xtalkLCDTarget.getDescription() +
                 "\n");
    }
    return buf.toString();
  }

  public LCDTarget getXtalkLCDTarget() {
    return xtalkLCDTarget;
  }

}
