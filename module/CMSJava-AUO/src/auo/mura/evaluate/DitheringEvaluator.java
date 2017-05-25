package auo.mura.evaluate;

import java.util.*;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.*;

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
public class DitheringEvaluator {

  static int checkSum(short[][] data) {
    int h = data.length;
    int w = data[0].length;
    int checksum = 0;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        checksum += data[y][x];
      }
    }
    return checksum;
  }

  static short[][] getData(short graylevel) {
    int height = 4;
    int width = 12;
    short[][] data = new short[height][width];
//for (int x = 400; x < 416; x++) {
    for (int h = 0; h < height; h++) {
      Arrays.fill(data[h], graylevel);
    }

//}
    return data;
  }

  public static void main(String[] args) {
    for (short grayLevel = 1; grayLevel < 16; grayLevel++) {
      yagi(grayLevel);
    }

  }

  public static void yagi(short grayLevel) {
    int width = 1920;
    int height = 1080;
//    short grayLevel = 1;
    short[][] image = new short[height][width];
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        image[h][w] = grayLevel;
      }
    }

    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
          "t.tmp"));
      short[][] c1 = (short[][]) ois.readObject();
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          image[h][w] = c1[h][w];
        }
      }

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }

    System.out.print(grayLevel + ": " + checkSum(image) / 16 + " ");

    short[][] diza = new short[height][width];
    short[][] sky = new short[height][width];
    short[][] skyerror = new short[height][width];
    diza[0][0] = (short) (image[0][0] / 16);

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        int error = image[h][w] % 16;
        int orgerror = error;
        if (error >= 8) {
          diza[h][w] = (short) ( (image[h][w] / 16) + 1);
          error = error - 16;
        }
        else {
          diza[h][w] = (short) (image[h][w] / 16);
        }

        skyerror[h][w] += (short) orgerror;
        int ii = 0, jj = 0, tmperror = 0;
        double omomi = 0;
        int skyomomi = 0;
        for (int c = 1; c <= 4; c++) {

          switch (c) {
            case 1:
              ii = 1;
              jj = 0;
              omomi = 7 / 16.;
              skyomomi = (int) Math.round(orgerror * 1. / 16);
              break;
            case 2:
              ii = -1;
              jj = 1;
              omomi = 3 / 16.;
              skyomomi = (orgerror == 8) ? 0 :
                  (int) Math.round(orgerror * 3. / 16);
              break;
            case 3:
              ii = 0;
              jj = 1;
              omomi = 5 / 16.;
              skyomomi = (int) Math.round(orgerror * 5. / 16);
              break;
            case 4:
              ii = 1; //width
              jj = 1; //height
              omomi = 1 / 16.;
              skyomomi = (orgerror == 1 || orgerror == 7) ? 1 :
                  (orgerror == 9 || orgerror == 15) ? 0 :
                  (int) Math.round(orgerror * 1. / 16);

              break;
          }
          if (h + jj >= height || w + ii >= width || w + ii < 0) {
            continue;
          }
          short erroromomi = (short) (error * omomi);
//           short erroromomi = (short)  Math.round(error * omomi);//bad
//          skyerror[h + jj][w + ii] += erroromomi;

          image[h + jj][w + ii] += erroromomi;
          tmperror = image[h + jj][w + ii] % 16;
          if (tmperror >= 8) {
            diza[h + jj][w + ii]
                = (short) ( (int) (image[h + jj][w + ii] / 16) + 1);
          }
          else {
            diza[h + jj][w + ii]
                = (short) ( (int) (image[h + jj][w + ii] / 16));
          }

          skyerror[h + jj][w + ii] += skyomomi * 16;
        }

      }
    }

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        sky[h][w] = (short) ( (image[h][w] / 16) + skyerror[h][w] / 16);
      }
    }

    int a = 0;

    System.out.println("dize:" + checkSum(diza) + " sky:" + checkSum(sky));

  }
}
