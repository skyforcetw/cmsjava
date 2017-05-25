package auo.cms.frc;

import java.io.File;
import shu.io.files.CSVFile;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AUOFRC {
  public AUOFRC(String frcFilename) throws FileNotFoundException, IOException {
    init(frcFilename);
  }

  public static void main(String[] args) throws FileNotFoundException,
      IOException {
    new AUOFRC("frc/auofrc.csv");
  }

  private boolean[][][] FRC_1_8;
  private boolean[][][] FRC_2_8;
  private boolean[][][] FRC_3_8;
  private boolean[][][] FRC_4_8;
  private boolean[][][] FRC_5_8;
  private boolean[][][] FRC_6_8;
  private boolean[][][] FRC_7_8;
  private boolean[][][][] FRC; //level,frame,h,w

  static boolean[][][] parse(int hoffset, CSVFile csv) {
    boolean[][][] result = new boolean[4][8][8];
    for (int count = 0; count < 4; count++) {
      for (int h = 0; h < 8; h++) {
        for (int w = 0; w < 8; w++) {
          int y = hoffset + h;
          int x = count * 9 + w;
          boolean b = (csv.getCell(y, x) == 1) ? true : false;
          result[count][h][w] = b;
//          System.out.print( (int) csv.getCell(y, x) + " ");
        }
//        System.out.println("");
      }
    }
    return result;
  }

  private void init(String filename) throws FileNotFoundException, IOException {
    CSVFile csv = new CSVFile(filename, (char) 9);
    FRC_1_8 = parse(2, csv);
    FRC_2_8 = parse(15, csv);
    FRC_3_8 = parse(28, csv);
    FRC_4_8 = parse(42, csv);
    FRC_5_8 = inverse(FRC_3_8);
    FRC_6_8 = inverse(FRC_2_8);
    FRC_7_8 = inverse(FRC_1_8);
    FRC = new boolean[][][][] {
        FRC_1_8, FRC_2_8, FRC_3_8, FRC_4_8, FRC_5_8, FRC_6_8, FRC_7_8};
  }

  public short[][][] frc8bit(short[][] image12bit) {
    int height = image12bit.length;
    int width = image12bit[0].length;
    short[][][] result = new short[4][height][width];
    for (int frame = 0; frame < 4; frame++) {
      frc8Bit(image12bit, frame, result[frame]);
    }
    //frame, h ,w

    return result;
  }

  private void frc8Bit(short[][] image12bit, int frame, short[][] frcImage8Bit) {
    int height = image12bit.length;
    int width = image12bit[0].length;
    short d, d2;
    int h_, w_;

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        d = image12bit[h][w];

        d2 = (short) (d / 2); //11bit
        short level = (short) (d2 & 7);
        d2 = (short) (d2 - level); //low
        d2 = (short) (d2 >> 3); //8bit

        frcImage8Bit[h][w] = d2;

        h_ = h & 7;
        w_ = w & 7;
        //FRC: level,frame,h,w
        if (level != 0 && FRC[level - 1][frame][h_][w_]) {
          frcImage8Bit[h][w]++;
        }
        if (256 == frcImage8Bit[h][w]) {
          int a = 1;
        }

      }
    }
  }

  private boolean[][][] inverse(boolean[][][] pattern) {
    boolean[][][] result = new boolean[4][8][8];
    for (int count = 0; count < 4; count++) {
      for (int h = 0; h < 8; h++) {
        for (int w = 0; w < 8; w++) {
          result[count][h][w] = !pattern[count][h][w];
        }
      }
    }

    return result;
  }

}
