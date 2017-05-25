package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.lcd.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 從CS1000的檔案讀取光譜資料
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CS1000Adapter
    extends TargetAdapter {
  protected File file;

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  public CS1000Adapter() {

  }

  public CS1000Adapter(String filename) {
    file = new File(filename);
  }

  public String getFilename() {
    return file.getName();
  }

  public String getAbsolutePath() {
    return file.getAbsolutePath();
  }

  public List<String> getPatchNameList() {
    if (patchNameList == null) {
      List<Spectra> list = getSpectraList();
      patchNameList = new ArrayList<String> (list.size());
      for (Spectra s : list) {
        String name = s.getName();
        patchNameList.add(name);
      }
    }
    return patchNameList;
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List getRGBList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List getXYZList() {
    throw new UnsupportedOperationException();
  }

  protected List<Spectra> spectraList = null;
  protected List<String> patchNameList = null;

  public List<Spectra> getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    if (spectraList == null) {
      double[] data = new double[401];
      String name = null;

      try {
        LineNumberReader reader = new LineNumberReader(new FileReader(file));

        while (reader.ready()) {
          String line = reader.readLine();
          if (line == null) {
            break;
          }
          int index = (reader.getLineNumber() - 1) % 413;
          if (index == 1) {
            name = line.trim();
          }
          if (index >= 12 && index <= 412) {
            double power = Double.parseDouble(line.trim());
            data[index - 12] = power;
          }
          if (index == 412) {
            Spectra s = new Spectra(name, Spectra.SpectrumType.EMISSION,
                                    380, 780, 1, data);
            spectraList.add(s);
            data = new double[401];
          }
        }
      }
      catch (FileNotFoundException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }

    }

    return spectraList;
  }

  public static void main(String[] args) {
    CS1000Adapter adapter = new CS1000Adapter(
        "C:/CSS1W/CSINI.LMT");
    List<Spectra> spectraList = adapter.getSpectraList();
    for (Spectra s : spectraList) {
      System.out.println(s.getXYZ());
    }

  }

  public Style getStyle() {
    return Style.RGBSpectra;
  }

  public String getFileNameExtension() {
    return "lmt";
  }

  public String getFileDescription() {
    return "CS1000 LMT File";
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    List<Spectra> list = getSpectraList();
    return LCDTargetBase.Number.getNumber(list.size());
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
