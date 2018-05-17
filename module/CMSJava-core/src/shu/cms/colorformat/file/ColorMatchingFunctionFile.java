package shu.cms.colorformat.file;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.legend.*;
import shu.cms.reference.cie.*;
import shu.math.array.*;
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
public final class ColorMatchingFunctionFile
    extends Parser {
  public ColorMatchingFunctionFile(String filename) {
    super(filename);
  }

  public ColorMatchingFunctionFile(InputStream in) {
    super(in);
  }

  public static void main(String[] args) throws IOException {
    ColorMatchingFunctionFile parser = new
        ColorMatchingFunctionFile(
            "Reference Files/CIE/Color Matching Functions/ciexyz31_1.txt");
    parser.parsing();
    ColorMatchingFunction cmf = parser.getColorMatchingFunction();

    InputStream is = CIE.class.getResourceAsStream("ciexyz31_1.txt");
    parser = new
        ColorMatchingFunctionFile(is);
    parser.parsing();
    parser.getColorMatchingFunction();
  }

  protected double[] parseData(String line) {
    StringTokenizer token = new StringTokenizer(line, " ,");
    int tokens = token.countTokens();
    if (tokens != 4) {
      throw new IllegalArgumentException("format error");
    }

    double[] data = new double[4];
    for (int x = 0; token.hasMoreTokens(); x++) {
      String str = token.nextToken();
      data[x] = Double.parseDouble(str.replace(',', ' ').trim());
    }
    return data;
  }

  protected double[][] parseData(ArrayList section) {
    int size = section.size();
    double[][] datas = new double[size][4];

    for (int x = 0; x < size; x++) {
      String line = (String) section.get(x);
      datas[x] = parseData(line);
    }

    return datas;
  }

  protected double[][] CMF;
  public double[][] getCMF() {
    return CMF;
  }

  /**
   * 將波長去掉,只留下cmf,並且作轉置
   * @param cmf double[][]
   * @return double[][]
   */
  protected double[][] filterCMFnTranspose(double[][] cmf) {
    int size = cmf.length;
    double[][] filtered = new double[size][3];
    for (int x = 0; x < size; x++) {
      System.arraycopy(cmf[x], 1, filtered[x], 0, 3);
    }
    return DoubleArray.transpose(filtered);
  }

  public ColorMatchingFunction getColorMatchingFunction() {
    ColorMatchingFunction cmf = new ColorMatchingFunction(getSpectraArray());
    return cmf;
  }

  /*public Spectra getSpectra() {
    int start = (int) CMF[0][0];
    int end = (int) CMF[CMF.length - 1][0];
    int interval = (int) (CMF[1][0] - CMF[0][0]);
//    System.out.println(Arrays.deepToString(CMF));
    double[][] data=filterCMFnTranspose(CMF);
   Spectra spectra = new Spectra("", Spectra.SpectrumType.FUNCTION, start, end,
                                  interval,
                                  filterCMFnTranspose(CMF));
    return spectra;
     }*/

  /**
   *
   * @return Spectra[]
   */
  public Spectra[] getSpectraArray() {
    int start = (int) CMF[0][0];
    int end = (int) CMF[CMF.length - 1][0];
    int interval = (int) (CMF[1][0] - CMF[0][0]);
    double[][] data = filterCMFnTranspose(CMF);

    Spectra[] spectraArray = new Spectra[3];
    spectraArray[0] = new Spectra("", Spectra.SpectrumType.FUNCTION, start, end,
                                  interval, data[0]);
    spectraArray[1] = new Spectra("", Spectra.SpectrumType.FUNCTION, start, end,
                                  interval, data[1]);
    spectraArray[2] = new Spectra("", Spectra.SpectrumType.FUNCTION, start, end,
                                  interval, data[2]);
    return spectraArray;

  }

  protected static boolean isIntervalInteger(double[][] data) {
    double iInterval = (int) (data[1][0] - data[0][0]);
    double dInterval = (data[1][0] - data[0][0]);

    if (dInterval != iInterval) {
      return false;
    }
    else {
      return true;
    }
  }

  /**
   * parsing
   */
  public void _parsing() {
    ArrayList<String> sec = new ArrayList<String> ();
    try {
      while (breader.ready()) {
        String line = breader.readLine();
        if (line == null) {
          break;
        }
        if (line.indexOf(',') != -1) {
          sec.add(line);
        }

      }
      CMF = parseData(sec);
      if (!isIntervalInteger(CMF)) {
        throw new IllegalArgumentException("interval is not integer");
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

  }
}
