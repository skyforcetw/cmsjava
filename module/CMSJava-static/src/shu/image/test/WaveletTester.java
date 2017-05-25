package shu.image.test;

import shu.image.IntegerImage;
import shu.image.ImageUtils;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * <p>Title: Colour Management System - static</p>
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
public class WaveletTester {

  static BufferedImage fromWavelet(BufferedImage img) {
    IntegerImage image = new IntegerImage(img);
//    for (int x = 0; x < 4; x++) {
//      for (int y = 0; y < 4; y++) {
//        int g = image.getPixel(x, y, 1);
//        image.setPixel(x, y, 1, g / 2);
//      }
//    }
//    int[][] result = new int[4][4];
    for (int y = 0; y < 4; y++) {
      int W = image.getPixel(0, y, 1) / 2;
      int X = image.getPixel(1, y, 1) / 2;
      int Y = image.getPixel(2, y, 1) / 2;
      int Z = image.getPixel(3, y, 1) / 2;
//      result[0][y] = W + Y;
//      result[1][y] = W - Y;
//      result[2][y] = X + Z;
//      result[3][y] = X - Z;
      image.setPixel(0, y, 1, W + Y);
      image.setPixel(1, y, 1, W - Y);
      image.setPixel(2, y, 1, X + Z);
      image.setPixel(3, y, 1, X - Z);
    }
    for (int x = 0; x < 4; x++) {
      int W = image.getPixel(x, 0, 1) / 2;
      int X = image.getPixel(x, 1, 1) / 2;
      int Y = image.getPixel(x, 2, 1) / 2;
      int Z = image.getPixel(x, 3, 1) / 2;
      int W_Y = Math.abs(W - Y);
      int X_Z = Math.abs(X - Z);
      image.setPixel(x, 0, 1, W + Y);
      image.setPixel(x, 1, 1, W_Y);
      image.setPixel(x, 2, 1, X + Z);
      image.setPixel(x, 3, 1, X_Z);
    }
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int g = image.getPixel(x, y, 1);
        image.setPixel(x, y, 0, g);
        image.setPixel(x, y, 2, g);
      }
    }
    return image.getBufferedImage();
  }

  static BufferedImage toWavelet(BufferedImage img) {
    IntegerImage image = new IntegerImage(img);
    image.setAutoRationalize(true);
    for (int y = 0; y < 4; y++) {
      int A = image.getPixel(0, y, 1);
      int B = image.getPixel(1, y, 1);
      int C = image.getPixel(2, y, 1);
      int D = image.getPixel(3, y, 1);
      int A_B = Math.abs(A - B);
      int C_D = Math.abs(C - D);
      image.setPixel(0, y, new int[] {A + B, A + B, A + B});
      image.setPixel(1, y, new int[] {C + D, C + D, C + D});
      image.setPixel(2, y, new int[] {A_B, A_B, A_B});
      image.setPixel(3, y, new int[] {C_D, C_D, C_D});
    }
    int[][] result = new int[4][4];
    int index = 0;
    for (int x = 0; x < 4; x += 2) {
      for (int y = 0; y < 2; y++) {
        int A = image.getPixel(x, y, 1);
        int B = image.getPixel(x + 1, y, 1);
        int C = image.getPixel(x, y + 2, 1);
        int D = image.getPixel(x + 1, y + 2, 1);
        int A_B = Math.abs(A - B);
        int C_D = Math.abs(C - D);
        result[index % 2][index / 2] = A + B;
        result[index % 2 + 2][index / 2] = A_B;
        result[index % 2][index / 2 + 2] = C + D;
        result[index % 2 + 2][index / 2 + 2] = C_D;
        index++;
      }
    }
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        result[x][y] = (result[x][y] > 255) ? 255 : result[x][y];
        result[x][y] = (result[x][y] < 0) ? 0 : result[x][y];
        image.setPixel(x, y, 0, result[x][y]);
        image.setPixel(x, y, 1, result[x][y]);
        image.setPixel(x, y, 2, result[x][y]);
      }
    }
    BufferedImage resultImage = image.getBufferedImage();
    return resultImage;
  }

  public static void main(String[] args) throws IOException {
    BufferedImage earth = ImageUtils.loadImageByJAI("earth.bmp");
    earth = earth.getSubimage(0, 0, 4, 4);
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int[] pixel = earth.getRaster().getPixel(x, y, new int[3]);
        int g = pixel[1];
        earth.getRaster().setPixel(x, y, new int[] {g, g, g});
      }
    }
    ImageUtils.storeBMPImage("earth4x4.bmp", earth);
    BufferedImage resultImage = toWavelet(earth);
    ImageUtils.storeBMPImage("result.bmp", resultImage);

    BufferedImage original = fromWavelet(resultImage);
    ImageUtils.storeBMPImage("original.bmp", original);
//    IntegerImage image = new IntegerImage(earth);
//    for (int x = 0; x < 4; x++) {
//      for (int y = 0; y < 4; y++) {
//        int g = image.getPixel(x, y, 1);
////        System.out.println(x + " " + y + " " + g);
//        image.setPixel(x, y, 0, g);
//        image.setPixel(x, y, 2, g);
//      }
//    }
////    showImage(image);
//    ImageUtils.storeBMPFImage("earth4x4.bmp", image.getBufferedImage());
//    image.setAutoRationalize(true);
//
//    for (int y = 0; y < 4; y++) {
//      int A = image.getPixel(0, y, 1);
//      int B = image.getPixel(1, y, 1);
//      int C = image.getPixel(2, y, 1);
//      int D = image.getPixel(3, y, 1);
//      image.setPixel(0, y, new int[] {A + B, A + B, A + B});
//      image.setPixel(1, y, new int[] {C + D, C + D, C + D});
//      image.setPixel(2, y, new int[] {A - B, A - B, A - B});
//      image.setPixel(3, y, new int[] {C - D, C - D, C - D});
//    }
//    int[][] result = new int[4][4];
//    int index = 0;
//    for (int x = 0; x < 4; x += 2) {
//      for (int y = 0; y < 2; y++) {
//        int A = image.getPixel(x, y, 1);
//        int B = image.getPixel(x + 1, y, 1);
//        int C = image.getPixel(x, y + 2, 1);
//        int D = image.getPixel(x + 1, y + 2, 1);
//        result[index % 2][index / 2] = A + B;
//        result[index % 2 + 2][index / 2] = A - B;
//        result[index % 2][index / 2 + 2] = C + D;
//        result[index % 2 + 2][index / 2 + 2] = C - D;
//        index++;
//      }
//    }
//    for (int x = 0; x < 4; x++) {
//      for (int y = 0; y < 4; y++) {
//        result[x][y] = (result[x][y] > 255) ? 255 : result[x][y];
//        result[x][y] = (result[x][y] < 0) ? 0 : result[x][y];
//        image.setPixel(x, y, 0, result[x][y]);
//        image.setPixel(x, y, 1, result[x][y]);
//        image.setPixel(x, y, 2, result[x][y]);
//      }
//    }
////    showImage(image);
//    BufferedImage resultImage = image.getBufferedImage();
//    ImageUtils.storeBMPFImage("result.bmp", resultImage);
  }

  static void showImage(IntegerImage image) {
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int g = image.getPixel(x, y, 1);
        System.out.println(x + " " + y + " " + g);
      }
    }

  }
}
