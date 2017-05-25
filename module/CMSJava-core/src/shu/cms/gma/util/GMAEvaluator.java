package shu.cms.gma.util;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.cms.profile.*;
import shu.cms.reference.spectra.*;
import shu.math.*;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * ����M�t��k������
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GMAEvaluator {
  protected GamutMappingAlgorithm gma;
//  protected CIEXYZ GMAWorkingWhitePoint;
//  protected CAMConst.CATType workingCATType;
  protected final static Illuminant IlluminantD50 = new Illuminant(Illuminant.
      D50.getSpectra().reduce(400, 700, 10));
//  protected ProfileColorSpace pcs;

  public GMAEvaluator(GamutMappingAlgorithm gma) {
    this.gma = gma;
//    this.workingCATType = workingCATType;
  }

  public DeltaEReport evaluate(SpectraDatabase.Content type) {
    CIEXYZ white = IlluminantD50.getNormalizeXYZ();
    //����XYZ�}�C
    double[][] XYZValuesArray = produceXYZValuesArray(type);
    CIELCh.XYZ2LChabValues(XYZValuesArray,
                           white.getValues());
    double[][] LChValuesArray = XYZValuesArray;
    //���ͫݴ���Lab��l���
    List<CIELab> originalLabList = produceCIELabList(LChValuesArray);

    //�ؼЦ�쬰sRGB
    ProfileColorSpace sRGB = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    ClippingGMA gma = new ClippingGMA(sRGB, FocalPoint.FocalType.MultiByKMeans);
    //�i�������Y
    gma.gamutMapping(LChValuesArray);

    //�p�����Y�᪺Lab
    List<CIELab> mappingLabList = produceCIELabList(LChValuesArray);
    //��t�p��
    DeltaEReport report = DeltaEReport.Instance.CIELabReport(originalLabList,
        mappingLabList);
    return report;
  }

  public DeltaEReport evaluate2(SpectraDatabase.Content type) {
    CIEXYZ white = IlluminantD50.getNormalizeXYZ();
    //����XYZ�}�C
    double[][] XYZValuesArray = produceXYZValuesArray(type);
    //���ͫݴ���Lab��l���
    List<CIELab> originalLabList = produceCIELabList(XYZValuesArray, white);

    if (true) {
      IPT.fromXYZValues(XYZValuesArray, true);
      CIELCh.fromLabValues(XYZValuesArray);
    }
    else {
      CIELCh.XYZ2LChabValues(XYZValuesArray,
                             white.getValues());
    }

    //�ؼЦ�쬰sRGB
    ProfileColorSpace sRGB = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    ClippingGMA gma = new ClippingGMA(sRGB, FocalPoint.FocalType.MultiByKMeans);
    //�i�������Y
    gma.gamutMapping(XYZValuesArray);
    if (true) {
      CIELCh.toLabValues(XYZValuesArray);
      IPT.toXYZValues(XYZValuesArray, true);
    }
    else {
      CIELCh.LChab2XYZValues(XYZValuesArray, white.getValues());
    }

    //�p�����Y�᪺Lab
    List<CIELab> mappingLabList = produceCIELabList(XYZValuesArray, white);
    //��t�p��
    DeltaEReport report = DeltaEReport.Instance.CIELabReport(originalLabList,
        mappingLabList);
    return report;
  }

  /**
   * ���ͥ��и�Ʈw��XYZ�}�C(�ĥ�PCS,�]�N�OD50��XYZ)
   * @param type Type
   * @return double[][]
   */
  protected final double[][] produceXYZValuesArray(SpectraDatabase.Content type) {
    List<Spectra>
        spectraList = SpectraDatabase.getSpectraList(type);

    spectraList = Spectra.produceSpectraPowerList(spectraList,
                                                  IlluminantD50.getSpectra());
    CIEXYZ normal = IlluminantD50.getSpectra().getXYZ();
    double[][] XYZValuesArray = produceXYZValuesArray(spectraList, normal);
    for (int x = 0; x < XYZValuesArray.length; x++) {
      if (XYZValuesArray[x][1] > 1) {
        System.out.println(x + " " + DoubleArray.toString(XYZValuesArray[x]));
      }
    }
    return XYZValuesArray;
  }

  public static void main(String[] args) {
    ProfileColorSpace sRGB = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        AppleRGB);
    ClippingGMA gma = new ClippingGMA(sRGB, FocalPoint.FocalType.MultiByKMeans);

    GMAEvaluator evaluator = new GMAEvaluator(gma);
    DeltaEReport report = evaluator.evaluate2(SpectraDatabase.Content.SOCS);
    System.out.println(report);
  }

  protected static List<CIELab> produceCIELabList(final double[][]
                                                  XYZValuesArray, CIEXYZ white) {
    int size = XYZValuesArray.length;
    List<CIELab> LabList = new ArrayList<CIELab> (size);
    CIEXYZ XYZ = new CIEXYZ();

    for (int x = 0; x < size; x++) {
      double[] XYZValues = XYZValuesArray[x];
      XYZ.setValues(XYZValues);
      CIELab Lab = CIELab.fromXYZ(XYZ, white);
      LabList.add(Lab);
    }

    return LabList;
  }

  protected static List<CIELab> produceCIELabList(final double[][]
                                                  LChValuesArray) {
    int size = LChValuesArray.length;
    List<CIELab> LabList = new ArrayList<CIELab> (size);
    CIELCh LCh = new CIELCh();

    for (int x = 0; x < size; x++) {
      double[] LChValues = LChValuesArray[x];
      LCh.setValues(LChValues);
      CIELab Lab = new CIELab(LCh);
      LabList.add(Lab);
    }

    return LabList;
  }

  protected static double[][] produceXYZValuesArray(List<Spectra> spectraList,
      CIEXYZ normal) {
    int size = spectraList.size();
    double[][] XYZValuesArray = new double[size][];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = spectraList.get(x).getXYZ();
      XYZ.normalize(normal);
      XYZValuesArray[x] = XYZ.getValues();
    }
    return XYZValuesArray;
  }
}
