package shu.cms.colorformat.legend;

import java.io.*;

import org.math.io.parser.*;
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
public class TRDatabaseParser
    extends Parser {
  public TRDatabaseParser(byte[] byteArray) {
    super(byteArray);
  }

  public TRDatabaseParser(String filename) {
    super(filename);
  }

  public final static String BEGIN_RECORD = "Wave length";
  public final static int WAVE_LENGTH_START = 400;
  public final static int WAVE_LENGTH_END = 700;
  public final static int WAVE_LENGTH_INTERVAL = 10;

  /**
   * parsing
   *
   */
  public void _parsing() {
    StringBuilder str = new StringBuilder();
    try {
      while (breader.ready()) {
        String line = breader.readLine();
        if (line == null) {
          break;
        }
        if (line.startsWith(BEGIN_RECORD)) {
          continue;
        }
        else {
          line = line.replace(',', '.');
          line = line.replaceAll(" \t", " ");
          line = line.replaceAll("\t", " ");
          line = line.trim();
          str.append(line + '\n');
        }
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    spectraArray = ArrayString.readStringDouble(str.toString());
  }

  protected double[][] spectraArray;

  public static void main(String[] args) {
    TRDatabaseParser parser = new TRDatabaseParser(
        "Reference Files/Spectra Database/SOCS/TRDatabase/TYPICAL/oil.txt");
  }

  protected TRDatabaseFile file;
  public TRDatabaseFile getTRDatabaseFile() {
    if (file == null) {
      parsing();
      file = new TRDatabaseFile();
      double[][] data = DoubleArray.transpose(spectraArray);
      data = DoubleArray.getRowsRangeCopy(data, 1, data.length - 1);

      for (int x = 0; x < data.length; x++) {
        data[x] = DoubleArray.times(data[x], 1. / 100);
      }

      file.data = data;
      file.waveLengthStart = 400;
      file.waveLengthEnd = 700;
      file.waveLengthInterval = 10;
      file.filename = this.filename;
    }

    return file;
  }
}
