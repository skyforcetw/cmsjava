package shu.cms.recover.convert;

import java.io.*;

import org.math.io.parser.*;
import shu.io.files.*;
import shu.math.*;
import shu.math.array.DoubleArray;

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
public class MunsellGlossyTXT2BinFileConverter {

  public static void main(String[] args) {
    try {
      File i = new File(
          "Reference Files/Spectra Database/Munsell/munsell380_780_1_glossy.txt");
      BufferedReader br = new BufferedReader(new FileReader(i));
      File o = new File("munsell380_780_5_glossy.dat");
      BinaryFile bf = new BinaryFile(o, BinaryFile.LITTLE_ENDIAN);

      double[][] result = new double[401][];

      int index = 0;
      while (br.ready()) {
        String line = br.readLine();
        if (line == null) {
          break;
        }
        line = line.trim();

        double[] array = ArrayString.readString1DDouble(line, "   ", "\n");
        result[index++] = array;
      }
      result = DoubleArray.transpose(result);

      int size = result.length;
      for (int x = 0; x < size; x++) {
        double[] d = result[x];
        double[] fit = new double[81];
        for (int y = 0; y < fit.length; y++) {
          fit[y] = d[y * 5];
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
