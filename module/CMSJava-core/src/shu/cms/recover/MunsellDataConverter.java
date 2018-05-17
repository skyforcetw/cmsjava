package shu.cms.recover;

import java.io.*;

import org.math.io.parser.*;
import shu.cms.*;
import shu.io.files.*;
import shu.math.*;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 將原始的Munsell的光譜資料, 轉成CMSJava可以處理的資料格式.
 * 為的是減低資料維度以及減少轉換所需要的時間與空間.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MunsellDataConverter {
  public static enum Edition {
    Glossy("munsell380_780_1_glossy.txt", 380, 780),
    Matt("munsell380_800_matt.txt", 380, 800);

    Edition(String filename, int start, int end) {
      this.filename = filename;
      this.start = start;
      this.end = end;
    }

    String filename;
    int start;
    int end;
  }

  protected final static String MUNSELL_DIRNAME = CMSDir.Reference.Spectra +
      "/Munsell";
  protected final static int MUNSELL_INTERVAL = 1;

  protected String filename;
  protected int start, end, interval;
  protected Edition edition;

  public MunsellDataConverter(Edition edition) {
    this.filename = MUNSELL_DIRNAME + "/" + edition.filename;
    this.start = edition.start;
    this.end = edition.end;
    this.interval = MUNSELL_INTERVAL;
    this.edition = edition;
  }

  protected static double[][] loadMattData(String filename) throws Exception {
    File input = new File(filename);
    BufferedReader reader = new BufferedReader(new FileReader(input));

    double[][] result = new double[1269][421];

    for (int x = 0; x < 1269; x++) {
      for (int y = 0; y < 421; y++) {
        String line = reader.readLine();
        line = line.trim();
        result[x][y] = Double.parseDouble(line);
      }
    }

    return result;
  }

  protected static double[][] loadGlossyData(String filename) throws Exception {
    File input = new File(filename);
    BufferedReader reader = new BufferedReader(new FileReader(input));

    double[][] result = new double[401][];

    int index = 0;
    while (reader.ready()) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      line = line.trim();

      double[] array = ArrayString.readString1DDouble(line, "   ", "\n");
      result[index++] = array;
    }
    result = DoubleArray.transpose(result);

    return result;
  }

  public final void convert(int start, int end, int interval,
                            String outputFilename) throws Exception {
    if (start < this.start || end > this.end) {
      throw new IllegalArgumentException(
          "start < this.start || end  > this.end");
    }

    double[][] result = null;
    switch (edition) {
      case Glossy:
        result = loadGlossyData(filename);
        break;
      case Matt:
        result = loadMattData(filename);
    }

    int dataSize = result.length;
    int size = (end - start) / interval + 1;
    int offset = start - this.start;

    for (int x = 0; x < dataSize; x++) {
      double[] d = result[x];
      double[] fit = new double[size];
      for (int y = 0; y < fit.length; y++) {
        fit[y] = d[y * interval + offset];
      }
      result[x] = fit;
    }

    File output = new File(outputFilename);
    output.delete();
    BinaryFile writer = new BinaryFile(output, BinaryFile.LITTLE_ENDIAN);
    writer.writeDoubleArray(result);
  }

  public static void main(String[] args) {
    MunsellDataConverter converter = new MunsellDataConverter(
        MunsellDataConverter.Edition.
        Matt);
    try {
      converter.convert(400, 700, 10, "test.txt");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
