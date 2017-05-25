package shu.cms.colorformat.adapter;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.TargetAdapter.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.hvs.cam.*;
import shu.cms.lcd.LCDTargetBase.Number;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008 skyforce</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DCChartAdapter
    extends TargetAdapter {

  public static void main(String[] args) {
    DCChartAdapter chart = new DCChartAdapter(CMSDir.Reference.Camera +
                                              "/IT8 E3199808.cxf",
                                              LightSource.CIE.D65,
                                              RGB.ColorSpace.sRGB);
    List<RGB> rgbList = chart.getRGBList();
    List<String> nameList = chart.getPatchNameList();
    List<CIEXYZ> xyzList = chart.getXYZList();
    int size = rgbList.size();
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      CIEXYZ XYZ = xyzList.get(x);
      System.out.println(nameList.get(x) + " " + rgb + " " + XYZ);
    }
  }

  /**
   * 由chart取得光譜反射率, 並且以lightSource為光源計算出光譜能量值
   * @param chart Chart
   * @param lightSource Type
   */
  public DCChartAdapter(DCTarget.Chart chart, LightSource.Source lightSource) {
    DCTarget.TargetData targetData = DCTarget.TargetData.getInstance(chart);

    String targetSpectraCxFFilename = DCTarget.Filename.
        produceSpectraCxFFilename(targetData);
    Illuminant illuminant = LightSource.getIlluminant(lightSource);
    CXFOperator targetSpectraCxF = new CXFOperator(targetSpectraCxFFilename);
    targetReflectSpectra = targetSpectraCxF.getSpectraList();
    targetSpectra = Spectra.produceSpectraPowerList(targetReflectSpectra,
        illuminant);
    patchNameList = Utils.filterNameList(targetSpectra);

    style = Style.Spectra;
  }

  /**
   * 由Lab的CxF檔(Lab的白點預設是D50), 產生XYZ, 且色適應到lightSource光源下.
   * @param LabCxFFilename String
   * @param lightSource Type
   */
  public DCChartAdapter(String LabCxFFilename, LightSource.Source lightSource) {
    CXFOperator targetSpectraCxF = new CXFOperator(LabCxFFilename);
    Illuminant illuminant = LightSource.getIlluminant(lightSource);
    white = illuminant.getSpectra().getXYZ();
    targetCIEXYZ = produceXYZList(targetSpectraCxF.getCIELabList(), white);
    patchNameList = targetSpectraCxF.getSampleNameList();
    style = Style.XYZ;
  }

  public DCChartAdapter(String LabCxFFilename, LightSource.Source lightSource,
                        RGB.ColorSpace rgbColorSpace) {
    this(LabCxFFilename, lightSource);
    targetRGB = produceRGBList(targetCIEXYZ, rgbColorSpace, white);
    style = Style.RGBXYZ;
  }

  /**
   * 由D50的Lab, 轉回到D50的XYZ, 然後再色適應到white下, 得到white下的XYZ
   * @param D50LabList List
   * @param white CIEXYZ
   * @return List
   */
  protected final static List<CIEXYZ> produceXYZList(List<CIELab> D50LabList,
      CIEXYZ white) {
    int size = D50LabList.size();
    List<CIEXYZ> XYZList = new ArrayList<CIEXYZ> (size);
    CIEXYZ D50 = Illuminant.D50WhitePoint;
    ChromaticAdaptation ca = new ChromaticAdaptation(D50, white,
        CAMConst.CATType.Bradford);

    for (int x = 0; x < size; x++) {
      CIELab Lab = D50LabList.get(x);
      CIEXYZ XYZ = CIELab.toXYZ(Lab, D50);
      XYZ = ca.getDestinationColor(XYZ);
      XYZList.add(XYZ);
    }
    return XYZList;
  }

  protected final static List<RGB> produceRGBList(List<CIEXYZ> XYZList,
      RGB.ColorSpace rgbColorSpace, CIEXYZ white) {
    int size = XYZList.size();
    List<RGB> rgbList = new ArrayList<RGB> (size);
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = XYZList.get(x);
      CIEXYZ clone = (CIEXYZ) XYZ.clone();
      clone.normalize(white);
      RGB rgb = RGB.fromXYZ(clone, rgbColorSpace);
      rgbList.add(rgb);
    }
    return rgbList;
  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  protected DCTarget.Chart chart;
  protected List<Spectra> targetSpectra;
  protected List<Spectra> targetReflectSpectra;
  protected List<CIEXYZ> targetCIEXYZ;
  protected Style style;
  protected CIEXYZ white;
  protected List<RGB> targetRGB;
  protected List<String> patchNameList;

  /**
   * estimateLCDTargetNumber
   *
   * @return Number
   */
  public Number estimateLCDTargetNumber() {
    throw new UnsupportedOperationException();
  }

  /**
   * getAbsolutePath
   *
   * @return String
   */
  public String getAbsolutePath() {
    throw new UnsupportedOperationException();
  }

  /**
   * getFileDescription
   *
   * @return String
   */
  public String getFileDescription() {
    return "DC Chart";
  }

  /**
   * getFileNameExtension
   *
   * @return String
   */
  public String getFileNameExtension() {
    return null;
  }

  /**
   * getFilename
   *
   * @return String
   */
  public String getFilename() {
    return chart.name();
  }

  /**
   * getPatchNameList
   *
   * @return List
   */
  public List<String> getPatchNameList() {
    return patchNameList;
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    return targetRGB;
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    return targetSpectra;
  }

  public List<Spectra> getReflectSpectraList() {
    return targetReflectSpectra;
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    return style;
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    return targetCIEXYZ;
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
