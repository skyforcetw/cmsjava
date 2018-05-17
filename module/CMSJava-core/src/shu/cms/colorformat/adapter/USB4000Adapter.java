package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.lcd.*;
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
public class USB4000Adapter
    extends TargetAdapter {
  protected File dir;
  private boolean fileMode = true;

  public USB4000Adapter(String dirName) {
    this(dirName, false);
  }

  public USB4000Adapter(String filename, boolean fileMode) {
    dir = new File(filename);
    this.fileMode = fileMode;
  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  public String getFilename() {
    return dir.getName();
  }

  public String getAbsolutePath() {
    return dir.getAbsolutePath();
  }

  public List<String> getPatchNameList() {
    return null;
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
      File[] files = null;
      if (fileMode) {
        files = new File[] {
            dir};
      }
      else {
        files = dir.listFiles();
      }
      int size = files.length;
      spectraList = new ArrayList<Spectra> (size);

      try {
        for (File file : files) {
          BufferedReader reader = new BufferedReader(new FileReader(file));
          double[] data = new double[471];
          int index = 0;
          int nmIndex = 360;
          int skip = 0;
          int firstSkip = FIRST_SKIP_LINE;

          while (reader.ready()) {
            String line = reader.readLine();

            if (firstSkip > 0) {
              //360nm以前的資料都略過
              firstSkip--;
              continue;
            }

            if (skip > 0) {
              //接下來都為相同波長的資料,所以可以略過
              skip--;
              continue;
            }

            if (line.charAt(0) < '3') {
              //只要找300以上
              continue;
            }
            else if (line.charAt(0) > '7') {
              //只要找700以下
              break;
            }
            int tabIndex = line.indexOf('\t');
            double nm = Double.parseDouble(line.substring(0, tabIndex));

            if (nm >= nmIndex) {
              nmIndex++;
              skip = EACH_SKIP_LINE;
              double power = Double.parseDouble(line.substring(tabIndex + 1));
              data[index++] = power;
            }
          }

          Spectra s = new Spectra(file.getName(), Spectra.SpectrumType.EMISSION,
                                  360, 830, 1, data);
          spectraList.add(s);
          reader.close();

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

  protected final static int EACH_SKIP_LINE = 3;
  protected final static int FIRST_SKIP_LINE = 857;

  public static void main(String[] args) {
//    USB4000Adapter adapter = new USB4000Adapter(
//        "Measurement Files/Monitor/cpt_17inch_demo2/usb4000/darkroom/native/1021");
    USB4000Adapter adapter = new USB4000Adapter(
        "C://Documents and Settings//skyforce//My Documents//Case//宏瀨科技//LED.ProcSpec", true);
    List<Spectra> spectraList = adapter.getSpectraList();
    System.out.println(spectraList.size());
    for (Spectra s : spectraList) {
      System.out.println(s);
    }
  }

  public Style getStyle() {
    return Style.RGBSpectra;
  }

  public String getFileNameExtension() {
    return null;
  }

  public String getFileDescription() {
    return null;
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    return LCDTargetBase.Number.getNumber(spectraList.size());
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
