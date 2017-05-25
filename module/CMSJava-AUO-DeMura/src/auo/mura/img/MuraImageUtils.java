package auo.mura.img;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;
import shu.image.ImageUtils;
import shu.image.IntegerImage;

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
//        generatePattern_11Level(1920, 1080);
//        generatePattern_2Vertical(1920, 1080);
//        generatePattern_11Level(2560, 1440);
        generatePattern_2Vertical(2560, 1440);
//        generatePattern_11Level(3840, 2160);
//        generatePattern_2Vertical(3840, 2160);
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
        ImageUtils.storeTIFFImage("DMC/pattern_2vertical" + width + "x" +
                                  height +
                                  ".tiff", image);
    }

    public static void generatePattern_11Level(int width, int height) throws
            IOException {

        MuraImageUtils utils = new MuraImageUtils(8, width, height);
        short[][][] data = utils.getPlaneImageData(2, 2, 2);
        short[] grayLevelArray = new short[] {
                                 0, 7, 12, 25, 50, 76, 127, 178, 216, 220, 255};
        int piece = grayLevelArray.length;
        int pieceWidth = (int) Math.round((double) width / piece);
        int start = 0, end = 0;
        for (int h = 0; h < height; h++) {
            for (int p = 0; p < piece; p++) {
                if (p == (piece - 1)) {

                    end = width;
                } else {
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
        ImageUtils.storeTIFFImage("demura/pattern_11level_" + width + "x" +
                                  height +
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
                int data0 = (int) (((double) v0 / max) * 255.);
                int data1 = (int) (((double) v1 / max) * 255.);
                int data2 = (int) (((double) v2 / max) * 255.);
                image.setPixel(x, y, new int[] {data0, data1, data2});
            }
        }
        return image.getBufferedImage();
    }

    public final void store8BitImageTiff(short[][][] imageData, String filename) throws
            IOException {
        BufferedImage image = getBufferedImage(
                imageData);
        ImageUtils.storeTIFFImage(filename, image);

    }

    public final void store8BitImageBMP(short[][][] imageData, String filename) throws
            IOException {
        System.gc();
        BufferedImage image = getBufferedImage(
                imageData);
        ImageUtils.storeBMPImage(filename, image);

    }

    public final void store16BitImageTiff(int[][][] imageData, String filename) throws
            FileNotFoundException, IOException {
//        ImageUtils.store


        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(
                ColorSpace.
                CS_sRGB), new int[] {16, 16, 16}, false, false,
                                                Transparency.BITMASK,
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

    public final short[][][] get10BitImageData(BufferedImage image,
                                          boolean reverseRead) {
        return getImageData(image, reverseRead, 2);
    }

    public final short[][][] getImageData(BufferedImage image,
                                          boolean reverseRead, int bitPush) {
        IntegerImage intimage = new IntegerImage(image);
        int w = intimage.getWidth();
        int h = intimage.getHeight();
        short[][][] imageData = new short[3][h][w];
        int[] pixel = new int[3];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                intimage.getPixel(x, y, pixel);
                if (!reverseRead) {
                    imageData[0][y][x] = (short) (pixel[0] << bitPush);
                    imageData[1][y][x] = (short) (pixel[1] << bitPush);
                    imageData[2][y][x] = (short) (pixel[2] << bitPush);
                } else {
                    imageData[0][h - y - 1][x] = (short) (pixel[0] << bitPush);
                    imageData[1][h - y - 1][x] = (short) (pixel[1] << bitPush);
                    imageData[2][h - y - 1][x] = (short) (pixel[2] << bitPush);

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

    public final static void storeDecFullFrame(short[][] imageData,
                                               String filename,
                                               int shrinkBit) throws
            IOException {
        int width = imageData[0].length; /// port;
        int height = imageData.length;
        Writer frameR = new BufferedWriter(new FileWriter(filename));
        double base = Math.pow(2, shrinkBit);

        for (int h = 0; h < height; h++) { //高
            for (int w = 0; w < width; w++) { //寬
                short value = (short) (imageData[h][w] / base);
                String dec = Integer.toString(value);
//        String hex = Integer.toHexString(value);
                dec = fillZero(dec, 3);
                frameR.write(dec);
                frameR.write(" ");
            }
            frameR.write("\r\n");
        }
        frameR.flush();
        frameR.close();
    }


    public static String fillZero(String hex, int fill) {
        int fillCount = fill - hex.length();
        String result = hex;
        if (fillCount > 0) {

            for (int x = 0; x < fillCount; x++) {
                result = "0" + result;
            }
        }
        return result;
    }

    public static boolean store12BitFullFrame = false;
    public static boolean store8BitFullFrame = false;
    public final static void storeImageToHexFormat(short[][][]
            imageData, String dirname, String prefix,
            int port) throws
            IOException {
        if (null == imageData) {
            return;
        }

        int width = imageData[0][0].length; /// port;
        int height = imageData[0].length;

        if (8 == port) {
            //dual chip, 需要先把畫面拆成 A | B, 再分別存4port輸出
            int halfWidth = width / 2;
            short[][][] imageDataA = new short[3][height][halfWidth];
            short[][][] imageDataB = new short[3][height][halfWidth];
            for (int ch = 0; ch < 3; ch++) {
                for (int h = 0; h < height; h++) {
                    for (int w = 0; w < halfWidth; w++) {
                        imageDataA[ch][h][w] = imageData[ch][h][w];
                        imageDataB[ch][h][w] = imageData[ch][h][halfWidth + w];
                    }
                }
            }

            storeImageToHexFormat(imageDataA, dirname,
                                  prefix + "A_",
                                  4);
            storeImageToHexFormat(imageDataB, dirname,
                                  prefix + "B_",
                                  4);
            return;
        }

        Writer[] writerR = new Writer[port];
        Writer[] writerG = new Writer[port];
        Writer[] writerB = new Writer[port];

        String fileprefix = prefix.length() != 0 ?
                            dirname + "/" + prefix :
                            dirname + "/";
        boolean storeFullFrameRGB = false;
        Writer writerRGB = null;
        if (storeFullFrameRGB) {
            writerRGB = new BufferedWriter(new FileWriter(fileprefix +
                    "rgb.txt"));
        }
        if (store8BitFullFrame) {
            MuraImageUtils.storeDecFullFrame(imageData[0],
                                             fileprefix + "frameR_8.txt", 4);
        }
        if (store12BitFullFrame) {
            MuraImageUtils.storeDecFullFrame(imageData[0],
                                             fileprefix + "frameR_12.txt", 0);
        }
        for (int p = 0; p < port; p++) {
            writerR[p] = new BufferedWriter(new FileWriter(fileprefix + "r" +
                    (p + 1) + ".txt"));
            writerG[p] = new BufferedWriter(new FileWriter(fileprefix + "g" +
                    (p + 1) + ".txt"));
            writerB[p] = new BufferedWriter(new FileWriter(fileprefix + "b" +
                    (p + 1) + ".txt"));

        }
        short r, g, b;
        for (int h = 0; h < height; h++) { //高
            for (int w = 0; w < width; w++) { //寬

                int p = w % port;

                r = imageData[0][h][w];
                String hexr = Integer.toHexString(r);
                hexr = MuraImageUtils.fillZero(hexr, 3);

                writerR[p].write(hexr);
                writerR[p].write("\r\n");

                g = imageData[1][h][w];
                String hexg = Integer.toHexString(g);
                hexg = MuraImageUtils.fillZero(hexg, 3);
                writerG[p].write(hexg);
                writerG[p].write("\r\n");

                b = imageData[2][h][w];
                String hexb = Integer.toHexString(b);
                hexb = MuraImageUtils.fillZero(hexb, 3);
                writerB[p].write(hexb);
                writerB[p].write("\r\n");

                if (storeFullFrameRGB) {
                    writerRGB.write("(" + (w + 1) + ")");
                    writerRGB.write(hexr);
                    writerRGB.write(" ");
                    writerRGB.write(hexg);
                    writerRGB.write(" ");
                    writerRGB.write(hexb);
                    writerRGB.write("|");
                }
            }
            if (storeFullFrameRGB) {
                writerRGB.write("\r\n");
            }
        }

        for (int p = 0; p < port; p++) {
            writerR[p].flush();
            writerR[p].close();
            writerG[p].flush();
            writerG[p].close();
            writerB[p].flush();
            writerB[p].close();

        }
        if (storeFullFrameRGB) {
            writerRGB.flush();
            writerRGB.close();
        }
    }

}
