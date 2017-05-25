package auo.mura;

import shu.image.IntegerImage;
import java.awt.image.BufferedImage;
import com.sun.media.jai.codec.ImageCodec;
import java.io.FileNotFoundException;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.awt.image.ComponentColorModel;
import java.io.File;
import java.awt.image.WritableRaster;
import com.sun.media.jai.codec.ImageEncoder;
import java.awt.Transparency;
import java.awt.image.DataBuffer;
import com.sun.media.jai.codec.TIFFEncodeParam;
import java.io.FileOutputStream;
import java.util.Arrays;
import shu.image.ImageUtils;

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
public class MuraImageUtils {
  public static void main(String[] args) throws IOException {
    generatePattern_11Level(1920, 1080);
    generatePattern_2Vertical(1920, 1080);
    generatePattern_11Level(2560, 1440);
    generatePattern_2Vertical(2560, 1440);
    generatePattern_11Level(3840, 2160);
    generatePattern_2Vertical(3840, 2160);
  }

  public static void generatePattern_2Vertical(int width, int height) throws
      IOException {
//    int height = 1080;
//    int width = 1920;
    MuraImageUtils utils = new MuraImageUtils(8, width, height);
    short[][][] data = utils.getPlaneImageData(2, 2, 2);
    int halfWidth = width / 2;
    int patternHeight = height / 128;
    int start = 0, end = 0;
    for (int x = 0; x <= 127; x++) {
      start = x * patternHeight;
      end = (x + 1) * patternHeight;
      if (x == 127) {
        end = height;
      }
      for (int w = 0; w < halfWidth; w++) {
        for (int h = start; h < end; h++) {
          data[0][h][w] = data[1][h][w] = data[2][h][w] = (short) x;
        }
      }

    }

    for (int x = 128; x <= 255; x++) {
      start = (x - 128) * patternHeight;
      end = (x - 128 + 1) * patternHeight;
      if (x == 255) {
        end = height;
      }
      for (int w = halfWidth; w < width; w++) {
        for (int h = start; h < end; h++) {
          data[0][h][w] = data[1][h][w] = data[2][h][w] = (short) x;
        }
      }

    }

//    short[] grayLevelArray = new short[] {
//        0, 7, 12, 25, 50, 76, 127, 178, 216, 220, 255};
//    int piece = grayLevelArray.length;
//    int pieceWidth = (int) Math.round( (double) width / piece);
//    int start = 0, end = 0;
//    for (int h = 0; h < height; h++) {
//      for (int p = 0; p < piece; p++) {
//        if (p == (piece - 1)) {
//
//          end = width;
//        }
//        else {
//          end = (p + 1) * pieceWidth;
//        }
//        start = p * pieceWidth;
//        short grayLevel = grayLevelArray[p];
//        for (int w = start; w < end; w++) {
//          data[0][h][w] = grayLevel;
//          data[1][h][w] = grayLevel;
//          data[2][h][w] = grayLevel;
//        }
//      }
//
//    }
    BufferedImage image = utils.getBufferedImage(data);
    ImageUtils.storeTIFFImage("demura/pattern_2vertical" + width + "x" + height +
                              ".tiff", image);
  }

  public static void generatePattern_11Level(int width, int height) throws
      IOException {

    MuraImageUtils utils = new MuraImageUtils(8, width, height);
    short[][][] data = utils.getPlaneImageData(2, 2, 2);
    short[] grayLevelArray = new short[] {
        0, 7, 12, 25, 50, 76, 127, 178, 216, 220, 255};
    int piece = grayLevelArray.length;
    int pieceWidth = (int) Math.round( (double) width / piece);
    int start = 0, end = 0;
    for (int h = 0; h < height; h++) {
      for (int p = 0; p < piece; p++) {
        if (p == (piece - 1)) {

          end = width;
        }
        else {
          end = (p + 1) * pieceWidth;
        }
        start = p * pieceWidth;
        short grayLevel = grayLevelArray[p];
        for (int w = start; w < end; w++) {
          data[0][h][w] = grayLevel;
          data[1][h][w] = grayLevel;
          data[2][h][w] = grayLevel;
        }
      }

    }
    BufferedImage image = utils.getBufferedImage(data);
    ImageUtils.storeTIFFImage("demura/pattern_11level_" + width + "x" + height +
                              ".tiff", image);
//    ImageUtils.storeBMPFImage("demura/pattern_11level_" + width + "x" + height +
//                              ".bmp", image);
  }

  public MuraImageUtils(int dataBit, int imageWidth, int imageHeight) {
    this.dataBit = dataBit;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }

  private int getDataMax(int dataBit) {
    int max = (int) (255 * Math.pow(2, dataBit - 8));
//    int max = (int) (Math.pow(2, dataBit) - 1);
    return max;
  }

  private int dataBit = 12;
  private int imageWidth = 1920;
  private int imageHeight = 1080;

//  public BufferedImage getBufferedImage(short[][] compensationData) {
//    IntegerImage image = new IntegerImage(imageWidth, imageHeight);
//    int max = getDataMax(this.dataBit);
//    for (int y = 0; y < imageHeight; y++) {
//      for (int x = 0; x < imageWidth; x++) {
//        short v = compensationData[y][x];
//        int data = (int) ( ( (double) v / max) * 255.);
//        image.setPixel(x, y, new int[] {data, data, data});
//      }
//    }
//    return image.getBufferedImage();
//  }

  public BufferedImage getBufferedImage(short[][][] compensationData) {
    IntegerImage image = new IntegerImage(imageWidth, imageHeight);
    int max = getDataMax(this.dataBit);
    for (int y = 0; y < imageHeight; y++) {
      for (int x = 0; x < imageWidth; x++) {
        short v0 = compensationData[0][y][x];
        short v1 = compensationData[1][y][x];
        short v2 = compensationData[2][y][x];
        int data0 = (int) ( ( (double) v0 / max) * 255.);
        int data1 = (int) ( ( (double) v1 / max) * 255.);
        int data2 = (int) ( ( (double) v2 / max) * 255.);
        image.setPixel(x, y, new int[] {data0, data1, data2});
      }
    }
    return image.getBufferedImage();
  }

  public final void store16BitImage(int[][][] imageData, String filename) throws
      FileNotFoundException, IOException {
    ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.
        CS_sRGB), new int[] {16, 16, 16}, false, false, Transparency.BITMASK,
                                            DataBuffer.TYPE_USHORT);

    int height = imageData[0].length;
    int width = imageData[0][0].length;

    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
//    raster.setPixel();
    int offset = 0;
    int[] pixels = new int[3];
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        pixels[0] = imageData[0][h][w] + offset;
        pixels[1] = imageData[1][h][w] + offset;
        pixels[2] = imageData[2][h][w] + offset;
        raster.setPixel(w, h, pixels);
      }
    }

    File file = new File(filename);
    FileOutputStream fileoutput = new FileOutputStream(file);

    TIFFEncodeParam encParam = null;

    ImageEncoder enc = ImageCodec.createImageEncoder("tiff", fileoutput,
        encParam);
    enc.encode(raster, cm);

    fileoutput.close();

  }

  public final short[][][] getImageData(BufferedImage image,
                                        boolean reverseRead) {
    IntegerImage intimage = new IntegerImage(image);
    int w = intimage.getWidth();
    int h = intimage.getHeight();
    short[][][] imageData = new short[3][h][w];
    int[] pixel = new int[3];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        intimage.getPixel(x, y, pixel);
        if (!reverseRead) {
          imageData[0][y][x] = (short) (pixel[0] * 4);
          imageData[1][y][x] = (short) (pixel[1] * 4);
          imageData[2][y][x] = (short) (pixel[2] * 4);
        }
        else {
          imageData[0][h - y - 1][x] = (short) (pixel[0] * 4);
          imageData[1][h - y - 1][x] = (short) (pixel[1] * 4);
          imageData[2][h - y - 1][x] = (short) (pixel[2] * 4);

        }
      }
    }
    return imageData;
  }

  public short[][][] getPlaneImageData(int rGrayLevel, int gGrayLevel,
                                       int bGrayLevel) {
    int h = imageHeight;
    int w = imageWidth;
    short[][][] imageData = new short[3][h][w];

    for (int y = 0; y < h; y++) {
      Arrays.fill(imageData[0][y], (short) rGrayLevel);
      Arrays.fill(imageData[1][y], (short) gGrayLevel);
      Arrays.fill(imageData[2][y], (short) bGrayLevel);

    }
    return imageData;
  }

}
