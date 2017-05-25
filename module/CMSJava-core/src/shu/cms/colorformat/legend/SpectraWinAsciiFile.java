package shu.cms.colorformat.legend;

import java.util.*;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來代表SpectraWin軟體產生的.txt資料檔
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SpectraWinAsciiFile
    extends AsciiFile {

  protected Header header;
  protected Spectral spectral;
  protected Calculated calculated;

  public Header getHeader() {
    return header;
  }

  public Spectral getSpectral() {
    return spectral;
  }

  public Calculated getCalculated() {
    return calculated;
  }

  public SpectraWinAsciiFile() {
  }

  public static void main(String[] args) {
  }

  public static class Header {
    public String title;
    public String description;
    public String modelNum;
    public String dateTime;
    public Map info = new HashMap();

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Title: ");
      buf.append(title);
      buf.append('\n');
      buf.append("Description: ");
      buf.append(description);
      buf.append('\n');
      buf.append("ModelNum: ");
      buf.append(modelNum);
      buf.append('\n');
      buf.append("DateTime: ");
      buf.append(dateTime);
      buf.append('\n');
      buf.append(info.toString());
      return buf.toString();
    }
  }

  public static class Calculated {
    public double[] luminance = new double[2];
    public double X;
    public double Z;
    public double radiance;
    public double photonRad;
    public int CCT;
    public String illuminant;
    public double x, y;

    public double[] _Lab = new double[3];
    public double[] ch = new double[2];

    public ArrayList<String> info = new ArrayList<String> ();

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Luminance: " + luminance[0] + " cd/m2 " + luminance[1] +
                 " fl\n");
      buf.append("X: " + X + "\n");
      buf.append("Z: " + Z + "\n");
      buf.append("Radiance: " + radiance + " watts/sr/m2\n");
      buf.append("Photon Rad: " + photonRad + " photons/sr/m2/sec\n");
      buf.append("CCT: " + CCT + "\n");
      buf.append("Illuminant: " + illuminant + "\n");
      buf.append("x: " + x + "\n");
      buf.append("y: " + y + "\n");
      buf.append("Lab*: ");
      buf.append(DoubleArray.toString(_Lab) + "\n");
      buf.append("Ch*: ");
      buf.append(DoubleArray.toString(ch) + "\n");
      buf.append(info.toString());
      return buf.toString();
    }

  }

  public static class Spectral {
    public int start;
    public int end;
    public int interval;
    public ArrayList<Double> info = new ArrayList<Double> ();

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Start: ");
      buf.append(start);
      buf.append("nm\n");
      buf.append("End: ");
      buf.append(end);
      buf.append("nm\n");
      buf.append("Interval: ");
      buf.append(interval);
      buf.append("nm\n");
      buf.append(info.toString());

      return buf.toString();
    }
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(header);
    buf.append('\n');
    buf.append(spectral);
    buf.append('\n');
    buf.append(calculated);
    return buf.toString();
  }
}
