package shu.cms.devicemodel;

import java.io.*;
import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.util.*;
import shu.util.*;
import shu.util.log.*;

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
public abstract class DeviceCharacterizationModel {

  protected DeviceCharacterizationModel() {

  }

  public DeviceCharacterizationModel(ModelFactor modelFactor) {
    this.modelFactor = modelFactor;
    this.evaluationMode = false;
  }

  /**
   * 是否為求值模式
   * @return boolean
   */
  public final boolean isEvaluationMode() {
    return evaluationMode;
  }

  /**
   * 是否為求值模式
   */
  protected boolean evaluationMode = false;

  /**
   * 將RGBpatchList的色塊,經由前導模式計算出XYZ,回傳成List<Patch>
   * @param RGBpatchList List
   * @return List
   */
  public abstract List<Patch> produceForwardModelPatchList(final List<Patch>
      RGBpatchList);

  /**
   * 將XYZpatchList的色塊,經由反推模式計算出RGB,回傳成List<Patch>
   * @param XYZpatchList List
   * @return List
   */
  public abstract List<Patch> produceReverseModelPatchList(final List<Patch>
      XYZpatchList);

  public abstract CIEXYZ getXYZ(RGB rgb, boolean relativeXYZ);

  public abstract RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ);

  public abstract String getDescription();

  /**
   * 以導具測試前導模式的正確性
   * @param targetLabPatchList List
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testForwardModel(List<Patch>
      targetLabPatchList, boolean doColorDividing) {
    List<Patch>
        LabPatchList = produceForwardModelPatchList(targetLabPatchList);

    DeltaEReport[] reports = DeltaEReport.Instance.
        patchReport(targetLabPatchList,
                    LabPatchList, doColorDividing);

    return reports;
  }

  /**
   * 以導具測試前導模式的正確性
   * @param targetPatch LCDTarget
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testForwardModel(Target targetPatch,
                                               boolean doColorDividing) {
    return testForwardModel(targetPatch.getLabPatchList(),
                            doColorDividing);
  }

  /**
   * 以導具測試反推模式的正確性
   * @param targetPatch LCDTarget
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testReverseModel(Target targetPatch,
                                               boolean doColorDividing) {
    return testReverseModel(targetPatch.getLabPatchList(),
                            doColorDividing);
  }

  /**
   * 以導具測試反推模式的正確性
   * @param targetLabPatchList List
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testReverseModel(List<Patch>
      targetLabPatchList, boolean doColorDividing) {
    List<Patch>
        LabPatchList = produceReverseModelPatchList(targetLabPatchList);

    DeltaEReport[] reports = DeltaEReport.Instance.
        patchReport(targetLabPatchList,
                    LabPatchList, doColorDividing);
    for (DeltaEReport report : reports) {
      report.setReverseReport(true);
    }
    return reports;
  }

  public static class Factor
      implements Serializable {

  }

  public static class ModelFactor
      implements Serializable {
    public String device;
    public String description;
    public String create;
    protected final static DecimalFormat df = new DecimalFormat("00");

    public ModelFactor() {
      Calendar rightNow = Calendar.getInstance();
      create = rightNow.get(Calendar.YEAR) +
          df.format(rightNow.get(Calendar.MONTH) + 1) +
          df.format(rightNow.get(Calendar.DAY_OF_MONTH));
    }

    public String getModelFactorFilename() {
      return device + "_" + description + "_" + create;
    }
  }

  protected ModelFactor modelFactor;
  protected transient TimeConsumption timeConsumption = new TimeConsumption();

  public final long getCostTime() {
    return timeConsumption.getCostTime();
  }

  public DeviceCharacterizationModel.ModelFactor
      getModelFactor() {
    return modelFactor;
  }

  protected void produceStart() {
    timeConsumption.start();
    Logger.log.trace(this.getClass().getName() + " start");
  }

  protected void produceEnd() {
    timeConsumption.end();
    Logger.log.trace(this.getClass().getName() + " end");
    Logger.log.info("costTime: " + timeConsumption.getCostTime());
    this.evaluationMode = false;
  }

  protected abstract String getStoreFilename();

  public Store store = new Store();
  public class Store {
    public void modelFactorFile(Object modelFactor, String filename) {
      Persistence.writeObject(modelFactor, filename);
    }

    public final void model(String filename) {
      File file = new File(filename);
      if (!file.exists()) {
        String xmlString = Persistence.writeObjectAsXML(this);
        byte[] input = xmlString.getBytes();
        byte[] compressedData = Persistence.compress(input);
        Persistence.writeObject(compressedData, filename);
      }
      file = null;
    }

    public final void model() {
      String filename = getStoreFilename();
      model(filename);
    }

    public final void modelAsXML() {
      String filename = getStoreFilename() + ".xml";
      File file = new File(filename);
      if (!file.exists()) {
        modelAsXML(filename);
      }
      file = null;
    }

    /**
     * 儲存model為xml
     * @param filename String
     */
    public final void modelAsXML(String filename) {
      Persistence.writeObjectAsXML(this, filename);
    }

  }

  public static class Load {
    public final static Object modelFactorFile(String filename) {
      return Persistence.readObject(filename);
    }

    public final static DeviceCharacterizationModel model(String filename) {
      byte[] compressedData = (byte[]) Persistence.readObject(filename);
      byte[] input = Persistence.decompress(compressedData);
      String xmlString = new String(input);
      DeviceCharacterizationModel dcm = (DeviceCharacterizationModel)
          Persistence.
          readObjectAsXMLString(xmlString);
      return dcm;
    }

    /**
     * 從xml格式載入回Model
     * @param filename String
     * @return DeviceCharacterizationModel
     */
    public final static DeviceCharacterizationModel
        modelAsXML(String filename) {
      Object obj = Persistence.readObjectAsXML(filename);
      if (obj == null) {
        throw new IllegalStateException("obj == null");
      }
      if (obj instanceof DeviceCharacterizationModel) {
        return (DeviceCharacterizationModel) obj;
      }
      else {
        throw new IllegalArgumentException(filename +
                                           " is not instanceof DeviceCharacterizationModel.");
      }
    }
  }

}
