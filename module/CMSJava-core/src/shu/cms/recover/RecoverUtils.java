package shu.cms.recover;

import java.io.*;

import shu.cms.*;
import shu.cms.reference.spectra.*;

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
public final class RecoverUtils {
  private RecoverUtils() {

  }

  public static double[][] getIlluminantSpectraData(SpectraDatabase.Content
      source, double[][] spectraData, Spectra illuminant) {
    int size = spectraData.length;
    int width = spectraData[0].length;
    double[][] result = new double[size][width];
    Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, source.start,
                             source.end, source.interval, null);
    illuminant = illuminant.reduce(source.start, source.end, source.interval);

    //==========================================================================
    // 資料初始化
    //==========================================================================
    for (int x = 0; x < size; x++) {
      System.arraycopy(spectraData[x], 0, result[x], 0, width);
      sw.setData(result[x]);
      sw.times(illuminant);
      result[x] = sw.getData();
    }

    return result;
  }

  /**
   * 檢查目錄是否存在, 不存在就建立
   * @param dirName String
   * @return boolean
   */
  public final static boolean checkAndMkdir(String dirName) {
    File dir = new File(dirName);
    if (!dir.exists()) {
      return dir.mkdir();
    }
    else {
      return dir.isDirectory();
    }
  }

  /**
   * 檢查目錄是否存在
   * @param dirName String
   * @return boolean
   */
  public final static boolean checkDir(String dirName) {
    File dir = new File(dirName);
    if (!dir.exists()) {
      return false;
    }
    else {
      return dir.isDirectory();
    }
  }

  public final static boolean deleteFile(String filename) {
    File file = new File(filename);
    if (file.exists() && file.isFile()) {
      return file.delete();
    }
    else {
      return true;
    }

  }
}
