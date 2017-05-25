package shu.cms.recover.convert;

import java.io.*;

import shu.io.files.*;

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
public class MunsellMattTXT2BinFileConverter {
  public static void main(String[] args) {
    try {
      File i = new File(
          "Reference Files/Spectra Database/Munsell/munsell380_800_matt.txt");
      BufferedReader br = new BufferedReader(new FileReader(i));
      File o = new File("munsell380_780_5_matt.dat");
      BinaryFile bf = new BinaryFile(o, BinaryFile.LITTLE_ENDIAN);

      double[][] result = new double[1269][421];

      for (int x = 0; x < 1269; x++) {
        for (int y = 0; y < 421; y++) {
          String line = br.readLine();
          line = line.trim();
          result[x][y] = Double.parseDouble(line);
        }
      }

      int size = result.length;
      for (int x = 0; x < size; x++) {
        double[] d = result[x];
        double[] fit = new double[81];
        for (int y = 0; y < fit.length; y++) {
          fit[y] = d[y * 5 + 0];
        }
        result[x] = fit;
      }

      bf.writeDoubleArray(result);
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }
}
