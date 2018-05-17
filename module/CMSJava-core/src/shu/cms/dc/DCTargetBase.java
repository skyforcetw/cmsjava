package shu.cms.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.ideal.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class DCTargetBase
    extends Target {
  protected DCTargetBase(List patchList) {
    super(patchList);
  }

  protected DCTargetBase(List patchList, boolean RGBNormalizing) {
    super(patchList, RGBNormalizing);
  }

  public static enum Camera
      implements Device {
    D70, D2H, D200Raw, D200Jpg, D200RawNormal;
    public String getName() {
      return name();
    }

  }

  public static enum Films
      implements Device {
    Ektachrome, Provia, Velvia;
    public String getName() {
      return name();
    }

  }

  public static interface Device {
    public String getName();
  }

  public static enum Chart {
    MiniCC24, CC24, CCDC, CCSG, GrayCard, IT8, MunsellGlossy,
    MunsellMatt, UserCustom, Unknow;

    public static Chart getChart(String filename) {
      int lastSlash = filename.lastIndexOf('/');
      int lastDot = filename.lastIndexOf('.');
      String onlyFilename = filename;
      if (lastSlash != -1 && lastDot != -1) {
        onlyFilename = onlyFilename.substring(lastSlash + 1, lastDot);
      }
      for (TargetData data : TargetData.dataArray) {
        if (data.RGBFilename.equalsIgnoreCase(onlyFilename)) {
          return data.chart;
        }
      }
      return null;
    }
  }

  /**
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來儲存導表相關數據
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public final static class TargetData {
    public final static int[] CC24InCCSGIndex = new int[] {
        41, 42, 43, 44, 51, 52, 53, 54, 61, 62, 63, 64, 71, 72, 73, 74, 81, 82,
        83, 84, 91, 92, 93, 94};

    final static double GRAY_Y_VALUE = 18;
    final static TargetData CC24 = new TargetData();
    final static TargetData MiniCC24 = new TargetData();
    final static TargetData CCDC = new TargetData();
    final static TargetData CCSG = new TargetData();
    final static TargetData IT8 = new TargetData();
    protected final static TargetData[] dataArray = new TargetData[] {
        CC24, CCDC, CCSG, IT8, MiniCC24};

    public final static TargetData getInstance(Chart type) {
      for (TargetData data : dataArray) {
        if (data.chart.equals(type)) {
          return data;
        }
      }
      return null;
    }

    static {
      CC24.width = 27.73;
      CC24.height = 18;
      CC24.bottomBorder = 1.24;
      CC24.xShift = 0;
      CC24.lgorowLength = 4;
      CC24.spectraCxFFilename = "ColorChecker 24.cxf";
      CC24.RGBFilename = "CC24";
      CC24.grayScaleIndex = new int[] {
          23, 19, 15, 11, 7, 3};
      CC24.sameHueIndexInProfile = new int[][] {
          {
          0, 4}, {
          2, 5}
      };
      CC24.chart = Chart.CC24;

//      MiniCC24.width = 27.73;
//      MiniCC24.height = 18;
//      MiniCC24.bottomBorder = 1.24;
//      MiniCC24.xShift = 0;
//      MiniCC24.lgorowLength = 4;
      MiniCC24.spectraCxFFilename = "CC24.cxf";
      MiniCC24.RGBFilename = "MiniCC24";
      MiniCC24.grayScaleIndex = new int[] {
          23, 19, 15, 11, 7, 3};
      MiniCC24.sameHueIndexInProfile = new int[][] {
          {
          0, 4}, {
          2, 5}
      };
      MiniCC24.chart = Chart.MiniCC24;

      CCDC.width = 30.5;
      CCDC.height = 18.39;
      CCDC.bottomBorder = 1.4;
      CCDC.xShift = 0;
      CCDC.lgorowLength = 12;
      CCDC.spectraCxFFilename = "ColorChecker DC.cxf";
      CCDC.RGBFilename = "CCDC";
      CCDC.grayScaleIndex = new int[] {
          139, 127, 115, 103, 138, 102, 137, 101, 136, 124, 112, 100};
      CCDC.chart = Chart.CCDC;

      CCSG.width = 25.26;
      CCSG.height = 18.01;
      CCSG.bottomBorder = 1.84;
      CCSG.xShift = 0;
      CCSG.lgorowLength = 10;
      CCSG.spectraCxFFilename = "Digital ColorChecker SG.cxf";
      CCSG.RGBFilename = "CCSG";
      CCSG.grayScaleIndex = new int[] {
          45, 94, 107, 55, 84, 65, 106, 74, 75, 64, 105, 85, 54, 95, 44};
      CCSG.chart = Chart.CCSG;

      /**
       * IT8導表的幾何數據
       * 寬15.45 高10.2 border 1.52
       * 彩色塊 14.15 7.68 border 3.45
       * GS 15.45 1.25 border 1.52
       * 一個彩色色塊是0.64見方
       */
      IT8.width = 14.15;
      IT8.height = 7.68;
      IT8.bottomBorder = 3.45;
      IT8.xShift = 0;
      IT8.lgorowLength = 12;
      IT8.spectraCxFFilename = "IT8.cxf";
      IT8.RGBFilename = "IT8";
//      IT8.grayScaleIndex = new int[] {
//          257, 235, 213, 191, 169, 147, 125, 103, 81, 59, 37, 15};
      IT8.grayScaleIndex = new int[] {
          287, 286, 285, 284, 283, 282, 281, 280, 279, 278, 277, 276, 275, 274,
          273, 272, 271, 270, 269, 268, 267, 266, 265, 264};
      IT8.chart = Chart.IT8;
      IT8.subTargetStart = 264;
      IT8.subTargetEnd = 287;
      IT8.sameHueIndexInProfile = new int[][] {
          {
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {
          22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33}, {
          44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55}, {
          66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77}, {
          88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99}, {
          110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121}, {
          132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143}, {
          154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165}, {
          176, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165}, {
          198, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165}, {
          220, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165}, {
          242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253},
      };

    }

    double height;
    double width;
    double bottomBorder;
    double xShift;
    int lgorowLength;
    String spectraCxFFilename;
    String RGBFilename;

    int subTargetStart;
    int subTargetEnd;

    int[] grayScaleIndex;
    public int[][] sameHueIndexInProfile;
    Chart chart;
  }

  public final static class Filename {
    protected final static String produceDeviceDirname(Device device) {
      String sourceDir = null;
      if (device instanceof Films) {
        sourceDir = "Films";
      }
      else if (device instanceof Camera) {
        sourceDir = "Camera";
      }

      return sourceDir + "/" + device.getName();
    }

    protected final static String produceRGBFilename(Device device,
        LightSource.Source illuminant, TargetData targetData) {
      String illuminantDir = (device instanceof Camera) ? illuminant.getName() :
          "";
      String deviceDir = produceDeviceDirname(device);
      return "Measurement Files/" + deviceDir + "/" +
          illuminantDir + "/" + targetData.RGBFilename;
    }

    protected final static String produceRGBFilename(String deviceDir,
        LightSource.Source illuminant, TargetData targetData) {
      String illuminantDir = illuminant.getName();
      return "Measurement Files/" + deviceDir + "/" +
          illuminantDir + "/" + targetData.RGBFilename;
    }

    public final static String produceSpectraCxFFilename(TargetData
        targetData) {
      return "Reference Files/Camera/" + targetData.spectraCxFFilename;
    }
  }

  public final static class Instance {
    public final static DCTarget get(TargetAdapter rgbTarget,
                                     TargetAdapter XYZSpectraTarget,
                                     LightSource.Source lightSource,
                                     Chart chart) {
      if (rgbTarget.getStyle() == TargetAdapter.Style.Spectra ||
          XYZSpectraTarget.getStyle() == TargetAdapter.Style.RGB) {
        //先檢查Target的Style
        throw new IllegalArgumentException("Illegal target style.");
      }
      Illuminant illuminant = getIlluminant(lightSource);
      DCTarget dcTarget = null;
      switch (XYZSpectraTarget.getStyle()) {

        case RGBXYZ:
        case XYZ:
          List<Patch>
              patchList = Patch.Produce.XYZRGBPatches(XYZSpectraTarget.
              getXYZList(), rgbTarget.getRGBList());
          dcTarget = new DCTarget(patchList, illuminant);
          dcTarget.setPatchName(rgbTarget.getPatchNameList());
          break;
        case RGBSpectra:
        case RGBXYZSpectra:
        case Spectra:
          List<Spectra> targetReflectSpectra = XYZSpectraTarget.
              getReflectSpectraList();

          List<Spectra>
              targetSpectra = Spectra.produceSpectraPowerList(
                  targetReflectSpectra, illuminant, true);
          dcTarget = new DCTarget(targetSpectra, targetReflectSpectra,
                                  rgbTarget.getRGBList(), illuminant);
          break;
        default:
          return null;
      }
      TargetData targetData = DCTarget.TargetData.getInstance(chart);
      setMetadata(dcTarget, rgbTarget.getFileDescription(), illuminant,
                  chart, targetData);
      return dcTarget;
    }

    public final static DCTarget get(Device device,
                                     LightSource.Source
                                     lightSource,
                                     double lightSourceFactor,
                                     Chart chart) {
      return get(device, lightSource, lightSourceFactor, chart, FileType.CxF);
    }

    public final static DCTarget get(List<Patch> patchList,
        Illuminant illuminant) {
      DCTarget dcTarget = new DCTarget(patchList, illuminant);
      dcTarget.chart = Chart.UserCustom;
      return dcTarget;
    }

    public final static DCTarget get(LightSource.Source
                                     lightSource, double lightSourceFactor,
                                     Chart chart, FileType fileType,
                                     String filename) {
      List<RGB> rgbList = null;

      switch (fileType) {
        case CxF: {
          CXFOperator rgbCxF = new CXFOperator(filename);
          rgbList = rgbCxF.getRGBList();
        }
        break;
        case ICC: {
          GMBICCProfileAdapter adapter = new GMBICCProfileAdapter(
              filename);
          rgbList = adapter.getRGBList();
        }
        break;
      }
      //=========================================================================
      // 產生DCTarget所需各項參數
      //=========================================================================
//      TargetData targetData = TargetData.getInstance(chart);
//      List<Spectra>
//          targetReflectSpectra = getTargetReflectSpectra(targetData);
      List<Spectra>
          targetReflectSpectra = getTargetReflectSpectra(chart);
      //=========================================================================

      DCTarget dcTarget = get(filename, rgbList, targetReflectSpectra,
                              lightSource, lightSourceFactor, chart);
      return dcTarget;
    }

    public final static DCTarget get(String devicedir,
                                     LightSource.Source
                                     lightSource,
                                     double lightSourceFactor,
                                     Chart chart, FileType fileType) {
      TargetData targetData = TargetData.getInstance(chart);
      String rgbFilename = Filename.produceRGBFilename(devicedir,
          lightSource, targetData);

      String fullRGBFilename = null;
      switch (fileType) {
        case CxF: {
          fullRGBFilename = rgbFilename + ".cxf";
        }
        break;
        case ICC: {
          fullRGBFilename = rgbFilename + ".icc";
        }
        break;
      }

      return get(lightSource, lightSourceFactor, chart, fileType,
                 fullRGBFilename);
    }

    public final static DCTarget get(Device device,
                                     LightSource.Source
                                     lightSource,
                                     double lightSourceFactor,
                                     Chart chart, FileType fileType) {
      return get(Filename.produceDeviceDirname(device), lightSource,
                 lightSourceFactor, chart, fileType);
    }

    protected final static DCTarget get(String source, List<RGB> rgbList,
        List<Spectra> targetReflectSpectra, LightSource.Source lightSource,
        double lightSourceFactor, Chart chart) {
      //=========================================================================
      // 處理光源
      //=========================================================================
      Illuminant illuminant = getIlluminant(lightSource);
//      Illuminant illuminant = LightSource.getIlluminant(lightSource);
      Spectra illuminantSpectra = illuminant.getSpectra();
      illuminantSpectra.times(lightSourceFactor);
      illuminant = new Illuminant(illuminantSpectra);
      //=========================================================================

      //=========================================================================
      // 產生光譜能量
      //=========================================================================
      List<Spectra>
          targetSpectra = Spectra.produceSpectraPowerList(
              targetReflectSpectra, illuminant, true);
      //=========================================================================

      DCTarget dcTarget = new DCTarget(targetSpectra, targetReflectSpectra,
                                       rgbList, illuminant);
      TargetData targetData = TargetData.getInstance(chart);
      setMetadata(dcTarget, source, illuminant, chart, targetData);
      return dcTarget;
    }

    private final static Illuminant getIlluminant(LightSource.Source
                                                  lightSource) {
      Illuminant illuminant = null;
      if (lightSource instanceof LightSource.Illuminantable) {
        illuminant = ( (LightSource.Illuminantable) lightSource).getIlluminant();
      }
      else {
        illuminant = LightSource.getIlluminant(LightSource.
                                               WhitePatchType.CCDCWhite,
                                               lightSource);
      }
      return illuminant;
    }

    private final static void setMetadata(DCTarget dcTarget, String device,
                                          Illuminant illuminant,
                                          Chart targetType,
                                          TargetData targetData) {
      dcTarget.setDevice(device);
      dcTarget.setDescription(illuminant.toString() + " " + targetType);
      dcTarget.targetData = targetData;
      dcTarget.chart = targetType;
    }

    public final static List<Spectra> getTargetReflectSpectra(Chart chart) {
      TargetData targetData = TargetData.getInstance(chart);
      List<Spectra>
          targetReflectSpectra = getTargetReflectSpectra(targetData);
      return targetReflectSpectra;
    }

    private final static List<Spectra> getTargetReflectSpectra(TargetData
        targetData) {
      //=========================================================================
      // 設定檔案路徑
      //=========================================================================
      String targetSpectraCxFFilename = Filename.produceSpectraCxFFilename(
          targetData);
      CXFOperator targetSpectraCxF = new CXFOperator(targetSpectraCxFFilename);

      //=========================================================================

      //=========================================================================
      // 產生LCDTarget所需各項參數
      //=========================================================================
      List<Spectra>
          targetReflectSpectra = targetSpectraCxF.getSpectraList();
      return targetReflectSpectra;
    }

    /**
     * 以IdealDigitalCamera所計算得之數位相機導表
     * @param camera IdealDigitalCamera
     * @param illuminant Illuminant
     * @param chart Chart
     * @return DCTarget
     */
    public final static DCTarget get(IdealDigitalCamera camera,
                                     Illuminant illuminant,
                                     Chart chart) {
      //=========================================================================
      // 產生LCDTarget所需各項參數
      //=========================================================================
      TargetData targetData = TargetData.getInstance(chart);
      List<Spectra>
          targetReflectSpectra = getTargetReflectSpectra(targetData);

      List<Spectra>
          targetPowerSpectra = Spectra.produceSpectraPowerList(
              targetReflectSpectra, illuminant, true);
      //=========================================================================

      DCTarget dcTarget = new DCTarget(targetPowerSpectra,
                                       targetReflectSpectra,
                                       illuminant, camera);
      setMetadata(dcTarget, camera.getName(), illuminant, chart,
                  targetData);

      return dcTarget;
    }

    /**
     * 以IdealDigitalCamera所計算得之數位相機導表
     * @param lightSource Type
     * @param chart Chart
     * @param camera IdealDigitalCamera
     * @return DCTarget
     */
    public final static DCTarget get(IdealDigitalCamera camera,
                                     LightSource.Source lightSource,
                                     Chart chart) {
      Illuminant illuminant = getIlluminant(lightSource);
      return get(camera, illuminant, chart);
    }
  }

  public static enum FileType {
    CxF("cxf"), ICC("icc");

    FileType(String extFilename) {
      this.extFilename = extFilename;
    }

    String extFilename;
  }

}
