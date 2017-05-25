package shu.cms.colorformat.file;

import java.io.*;

import shu.cms.*;
import shu.io.ascii.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.*;
//import shu.plot.*;



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
public class SpectraSuiteFile {
  public SpectraSuiteFile(String filename) {
    parser = new ASCIIFileFormatParser(filename);
  }

  public double[] getWaveLenthArray() {
    return wavelengrhArray;
  }

  public double[] getContsArray() {
    return contsArray;
  }

  private double[] wavelengrhArray = null;
  private double[] contsArray = null;

  public void parse() throws IOException {
    format = parser.parse();
    int size = format.size();
    boolean beginData = false;
    int index = 0;
    int datasize = -1;

    for (int x = 0; x < size; x++) {
      ASCIIFileFormat.LineObject lo = format.getLine(x);
      if (lo.line.equals(">>>>>Begin Processed Spectral Data<<<<<")) {
        beginData = true;
        x = x + 1;
        datasize = size - x - 2;
        wavelengrhArray = new double[datasize];
        contsArray = new double[datasize];
        continue;
      }

      if (lo.line.equals(">>>>>End Processed Spectral Data<<<<<")) {
        break;
      }
      if (beginData) {
        String[] array = lo.stringArray;
        wavelengrhArray[index] = Double.parseDouble(array[0]);
        contsArray[index] = Double.parseDouble(array[1]);
        index++;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    SpectraSuiteFile file = new SpectraSuiteFile(
        "C://Documents and Settings//skyforce//My Documents//Case//§»Ãu¬ì§Þ//LED.ProcSpec");
    file.parse();

    for (Interpolation.Algo algo :
         new Interpolation.Algo[] {Interpolation.Algo.Linear,
         Interpolation.Algo.CubicPolynomial,
         Interpolation.Algo.QuadraticPolynomial, Interpolation.Algo.Spline2}) {
      double[] result = file.evaluateInterpolation(380, 730, 10, algo, true);
      System.out.println(algo.name() + " rmsd:" + result[0] + " r2:" + result[1]);
    }

//    Spectra s = file.getSpectra(380, 730, 10, Interpolation.Algo.Linear);
//
//    Plot2D p = Plot2D.getInstance();
//    p.addSpectra("", s);
//    p.setVisible();
  }

  public Spectra getSpectra(int start, int end, int interval,
                            Interpolation.Algo algo) {
    double[] data = getSpectraData(start, end, interval, algo);
    Spectra s = new Spectra(null, Spectra.SpectrumType.NO_ASSIGN, start, end,
                            interval, data);
    return s;
  }

  /**
   *
   * @param start int
   * @param end int
   * @param interval int
   * @param algo Algo
   * @param plot boolean
   * @return double[] {rmsd, rSquare}
   */
  public double[] evaluateInterpolation(int start, int end, int interval,
                                        Interpolation.Algo algo, boolean plot) {
    int startIndex = Searcher.leftNearBinarySearch(this.wavelengrhArray, start) +
        1;
    int endIndex = Searcher.leftNearBinarySearch(this.wavelengrhArray, end);
    double[] orgWavelenthArray = DoubleArray.getRangeCopy(this.
        wavelengrhArray, startIndex, endIndex);
    double[] orgConts = DoubleArray.getRangeCopy(this.contsArray, startIndex,
                                                 endIndex);

    double[] interpData = getSpectraData(start, end, interval, algo);
    double[] interpWavelenth = DoubleArray.buildX(start, end, interpData.length);
    Interpolation interp = new Interpolation(interpWavelenth, interpData);
    double[] recoverConts = new double[orgConts.length];

    for (int x = 0; x < orgWavelenthArray.length; x++) {
      double wavelenth = orgWavelenthArray[x];
      recoverConts[x] = interp.interpolate(wavelenth, algo);
    }
    double rmsd = Maths.RMSD(orgConts, recoverConts);
    double r2 = Maths.rSquare(orgConts, recoverConts);

    if (plot) {
      Plot2D p = Plot2D.getInstance(algo.name());
      p.addLinePlot("original", new double[][] {orgWavelenthArray,
                    orgConts});
      p.addLinePlot("recover", new double[][] {orgWavelenthArray, recoverConts});
      p.setVisible();
    }
    double[] result = new double[] {
        rmsd, r2};
    return result;
  }

  protected double[] getSpectraData(int start, int end, int interval,
                                    Interpolation.Algo algo) {
    int size = (end - start) / interval + 1;
    double[] data = new double[size];
    Interpolation interp = new Interpolation(wavelengrhArray, contsArray);

    for (int x = 0; x < size; x++) {
      int wavelenth = start + interval * x;
      double conts = interp.interpolate(wavelenth, algo);
//      double conts = Interpolation.cubicPolynomial(wavelengrhArray, contsArray,
//                                          wavelenth);
//      double conts = Interpolation.lagrange(wavelengrhArray, contsArray,
//                                            wavelenth);
      data[x] = conts;
    }
    return data;
  }

  private ASCIIFileFormatParser parser;
  private ASCIIFileFormat format;
}
