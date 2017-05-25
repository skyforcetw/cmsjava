package shu.cms.recover;

import java.io.*;

import shu.cms.*;
import shu.cms.reference.spectra.*;
import shu.io.files.*;
import shu.math.array.DoubleArray;
import shu.math.SVDLib;


/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * (1) 將SOCS的光譜反射比資料進行SVD,
 * 但是SOCS資料筆數太多,所以無法運算(須高達20G以上的記憶體=.=) *
 *
 * (2) 將兩種Munsell資料 Matt/Glossy 合併,並且進行SVD
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public final class SVD {
  public final static String U_FILE = "U.dat";
  public final static String sv_FILE = "sv.dat";
  public final static String V_FILE = "V.dat";

  public SVD(SpectraDatabase.Content source, Spectra illuminant) {
    this.source = source;
    this.illuminant = illuminant;

    if (!checkMunsellUSV(source, illuminant)) {
      //USV檔案不存在就產生他
      produceMunsellUSV(source, illuminant);
    }
  }

  protected SpectraDatabase.Content source;
  protected Spectra illuminant;
  protected double[][] U;
  protected double[] sv;
  protected double[][] V;

  /**
   * 檢查Munsell的USV檔案是否存在
   * @param source Content
   * @param illuminant Spectra
   * @return boolean
   */
  protected final static boolean checkMunsellUSV(SpectraDatabase.Content source,
                                                 Spectra illuminant) {
    String UFilename = getPath(source.name(), illuminant, U_FILE);
    String svFilename = getPath(source.name(), illuminant, sv_FILE);
    String VFilename = getPath(source.name(), illuminant, V_FILE);
    return new File(UFilename).exists() && new File(svFilename).exists() &&
        new File(VFilename).exists();
  }

  private final static void produceMunsellUSV(SpectraDatabase.Content source,
                                              Spectra illuminant) {
    //==========================================================================
    // 讀取munsell book的資料
    //==========================================================================
    double[][] data = SpectraDatabase.getSpectraData(source);

    if (illuminant != null) {
      //如果有光源, 將munsell的光譜反射率乘上光譜能量值.
      data = RecoverUtils.getIlluminantSpectraData(source, data,
          illuminant);
    }
    data = DoubleArray.transpose(data);

    //進行SVD
    SVDLib svd = new SVDLib(data);
    double[][] u = svd.getU();
    double[] sv = svd.getSingularValues();
    double[][] v = svd.getV();

    String UFilename = getPath(source.name(), illuminant, U_FILE);
    String svFilename = getPath(source.name(), illuminant, sv_FILE);
    String VFilename = getPath(source.name(), illuminant, V_FILE);
    RecoverUtils.deleteFile(UFilename);
    RecoverUtils.deleteFile(svFilename);
    RecoverUtils.deleteFile(VFilename);

    BinaryFile.writeDoubleArray(UFilename, u);
    BinaryFile.writeDoubleArray(svFilename, sv);
    BinaryFile.writeDoubleArray(VFilename, v);

  }

  protected String getPath(String filename) {
    return getPath(source.name(), illuminant, filename);
  }

  private static String getPath(String pathname, Spectra illuminant,
                                String filename) {
    RecoverUtils.checkAndMkdir("spectra");
    RecoverUtils.checkAndMkdir("spectra/" + pathname);

    if (illuminant == null) {
      return "spectra/" + pathname + "/" + filename;
    }
    else {
      String illuminantDirname = "spectra/" + pathname + "/" +
          illuminant.getName();
      RecoverUtils.checkAndMkdir(illuminantDirname);
      return illuminantDirname + "/" +
          filename;
    }
  }

  /**
   * @deprecated
   */
  private final static void produceSOCSTypicalUSV() {
    double[][] data = SpectraDatabase.SOCS.getTypical();
    data = DoubleArray.transpose(data);
    System.out.println(DoubleArray.dimension(data));

    //進行SVD
    SVDLib svd = new SVDLib(data);
    double[][] u = svd.getU();
    double[] sv = svd.getSingularValues();
    double[][] v = svd.getV();

    String UFilename = "Spectra/SOCSTypical/" + U_FILE;
    String svFilename = "Spectra/SOCSTypical/" + sv_FILE;
    String VFilename = "Spectra/SOCSTypical/" + V_FILE;

    BinaryFile.writeDoubleArray(UFilename, u);
    BinaryFile.writeDoubleArray(svFilename, sv);
    BinaryFile.writeDoubleArray(VFilename, v);
  }

  /**
   * @deprecated
   */
  private final static void produceSOCSDifferenceUSV() {
    double[][] data = SpectraDatabase.SOCS.getDifference();
    data = DoubleArray.transpose(data);
    System.out.println(DoubleArray.dimension(data));

    //進行SVD
    SVDLib svd = new SVDLib(data);
    double[][] u = svd.getU();
    double[] sv = svd.getSingularValues();
    double[][] v = svd.getV();

    String UFilename = "Spectra/SOCSDifference/" + U_FILE;
    String svFilename = "Spectra/SOCSDifference/" + sv_FILE;
    String VFilename = "Spectra/SOCSDifference/" + V_FILE;

    BinaryFile.writeDoubleArray(UFilename, u);
    BinaryFile.writeDoubleArray(svFilename, sv);
    BinaryFile.writeDoubleArray(VFilename, v);
  }

  /**
   * @deprecated
   */
  private final static void produceSOCSTRDatabaseUSV() {
    double[][] typical = SpectraDatabase.SOCS.getTypical();
    double[][] difference = SpectraDatabase.SOCS.getDifference();
    double[][] data = DoubleArray.mergeRows(typical, difference);

    data = DoubleArray.transpose(data);
    System.out.println(DoubleArray.dimension(data));

    //進行SVD
    SVDLib svd = new SVDLib(data);
    double[][] u = svd.getU();
    double[] sv = svd.getSingularValues();
    double[][] v = svd.getV();

    String UFilename = "Spectra/SOCSTRDatabase/" + U_FILE;
    String svFilename = "Spectra/SOCSTRDatabase/" + sv_FILE;
    String VFilename = "Spectra/SOCSTRDatabase/" + V_FILE;

    BinaryFile.writeDoubleArray(UFilename, u);
    BinaryFile.writeDoubleArray(svFilename, sv);
    BinaryFile.writeDoubleArray(VFilename, v);
  }

  public double[] getSv() {
    if (sv == null) {
      sv = BinaryFile.readDoubleArray(getPath(source.name(), illuminant,
                                              sv_FILE));
    }
    return sv;
  }

  public double[][] getV() {
    if (V == null) {
      V = BinaryFile.readDoubleArray(getPath(source.name(), illuminant, V_FILE),
                                     source.count);
    }
    return V;
  }

  public double[][] getU() {
    if (U == null) {
      int cols = (source.end - source.start) / source.interval + 1;
      U = BinaryFile.readDoubleArray(getPath(source.name(), illuminant, U_FILE),
                                     cols);
    }
    return U;
  }
}
