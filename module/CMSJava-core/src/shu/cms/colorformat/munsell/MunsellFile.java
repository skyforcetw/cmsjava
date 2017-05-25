package shu.cms.colorformat.munsell;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.io.ascii.*;
import shu.math.array.*;

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
public class MunsellFile {
  private ASCIIFileFormat asciiFileFormat;
  private String filename;

  public MunsellFile(String filename) throws IOException {
    ASCIIFileFormatParser parser = new ASCIIFileFormatParser(filename);
    asciiFileFormat = parser.parse();
    this.filename = filename;
  }

  public String[] gethVC(int index) {
    ASCIIFileFormat.LineObject lo = asciiFileFormat.getLine(index + 1);
//    double h = Double.valueOf(lo.stringArray[0]);
//    double V = Double.valueOf(lo.stringArray[1]);
//    double C = Double.valueOf(lo.stringArray[2]);
//    String[] hvC=new String[3];
    String[] hvC = Arrays.copyOf(lo.stringArray, 3);
    return hvC;
  }

  public double[] getxyYValues(int index) {
    ASCIIFileFormat.LineObject lo = asciiFileFormat.getLine(index + 1);
    double x = Double.valueOf(lo.stringArray[3]);
    double y = Double.valueOf(lo.stringArray[4]);
    double Y = Double.valueOf(lo.stringArray[5]);
    return new double[] {
        x, y, Y};
  }

  public int size() {
    return asciiFileFormat.size() - 1;
  }

  public String getFilename() {
    return filename;
  }

  public static void main(String[] args) throws Exception {
    MunsellFile file = new MunsellFile(CMSDir.Reference.Munsell + "/real.dat");
    int size = file.size();
    for (int x = 0; x < size; x++) {
      System.out.println(Arrays.toString(file.gethVC(x)) + " " +
                         DoubleArray.toString(file.getxyYValues(x)));
    }
  }
}
