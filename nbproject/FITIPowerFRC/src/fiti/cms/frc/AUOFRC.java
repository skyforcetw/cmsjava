package fiti.cms.frc;

import java.io.FileNotFoundException;
import java.io.IOException;

import auo.mura.util.ArrayUtils;
import shu.io.files.CSVFile;

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
 * @deprecated
 */
public class AUOFRC {
    public AUOFRC(String frcFilename, PatternCount count) throws
            FileNotFoundException, IOException {
        this.patternCount = count;
        this.frameCount = 4;
        init(frcFilename, count);
    }


    public AUOFRC(boolean[][][][] FRC, boolean mapping) {

        if (mapping) {
            this.FRC = mapping(FRC);
        } else {
            this.FRC = FRC;
        }
        int count = FRC.length;
        this.patternCount = count == 8 ? PatternCount.FRC8 : count == 16 ?
                            PatternCount.FRC16 : null;
        this.frameCount = 4;
    }

    public AUOFRC(FRCPattern frc, boolean mapping) {
        this(frc.pattern, mapping);
    }


    private PatternCount patternCount;
    private int frameCount;


    public static enum PatternCount {
        FRC8, FRC16
    }


//    /**
//     *
//     * @param image12bit short[][][]
//     * @param type Type
//     * @return short[][][][] frame, ch, h, w
//     */
//    public short[][][][] frc8bit(short[][][] image12bit, PatternType type) {
//        switch (type) {
//        case PixelBase:
//            return pixelBaseFRC8bit(image12bit);
//        case SubPixelBase:
//            return subPixelBaseFRC8bit(image12bit);
//        default:
//            return null;
//        }
//    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
//        System.out.println("1234");
//        String filename = "D:/My Documents/工作/Project/FRC/FRC Research/NewFRC/AllNewFRC2.txt";
        String filename = "frc/auofrc16.csv";
//        AUOFRC frc = new AUOFRC(filename, PatternCount.FRC16);
        FRCPattern frc = new FRCPattern(filename);
        System.out.println(frc);
//        FRCPattern frcpat = new FRCPattern(frc.FRC);

//        for (int x = 0; x <= 8; x++) {
//            FRCPattern frcpat = new FRCPattern(filename, x);
//            ArtifactsAnalyzer.analyze(frcpat, x);
////        frcpat.caculateInfo();
////            System.out.println(frcpat);
//            System.out.println(x);
//            System.out.println(frcpat.getPolarityString(0, 0));
//            System.out.println(frcpat.getPolarityString(1, 1));
//
//            System.out.println(frcpat.getPolarityString(0, 1));
//            System.out.println(frcpat.getPolarityString(1, 0));
//        }
//   boolean[][][] frc4_8=  frc.getFRCPattern(3);
//        new AUOFRC("frc/auofrc16.csv", PatternCount.FRC16);
    }


    public boolean[][][] getFRCPattern(int level) {
        return FRC[level];
    }


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
                }
            }
        }
        return result;
    }

    private static boolean[][][][] mapping(boolean[][][][] input) {
        int size = input.length;
        if (size == 4) {
            boolean[][][] FRC_5_8 = ArrayUtils.inverse(input[2]);
            boolean[][][] FRC_6_8 = ArrayUtils.inverse(input[1]);
            boolean[][][] FRC_7_8 = ArrayUtils.inverse(input[0]);

            return new boolean[][][][] {
                    input[0], input[1], input[2], input[3], FRC_5_8, FRC_6_8,
                    FRC_7_8};
        } else if (size == 8) {

        }
        return null;
    }

    private void init(String filename, PatternCount count) throws
            FileNotFoundException,
            IOException {
        CSVFile csv = new CSVFile(filename, (char) 9);
        if (count == PatternCount.FRC8) {
            boolean[][][] FRC_1_8 = parse(2, csv);
            boolean[][][] FRC_2_8 = parse(15, csv);
            boolean[][][] FRC_3_8 = parse(28, csv);
            boolean[][][] FRC_4_8 = parse(42, csv);
            FRC = mapping(new boolean[][][][] {FRC_1_8, FRC_2_8, FRC_3_8,
                          FRC_4_8});
//            boolean[][][] FRC_5_8 = ArrayUtils.inverse(FRC_3_8);
//            boolean[][][] FRC_6_8 = ArrayUtils.inverse(FRC_2_8);
//            boolean[][][] FRC_7_8 = ArrayUtils.inverse(FRC_1_8);
//            FRC = new boolean[][][][] {
//                  FRC_1_8, FRC_2_8, FRC_3_8, FRC_4_8, FRC_5_8, FRC_6_8, FRC_7_8};
        } else if (count == PatternCount.FRC16) {
            boolean[][][] FRC_1_16 = parse(2, csv);
            boolean[][][] FRC_2_16 = parse(16, csv);
            boolean[][][] FRC_3_16 = parse(30, csv);
            boolean[][][] FRC_4_16 = parse(44, csv);
            boolean[][][] FRC_5_16 = parse(57, csv);
            boolean[][][] FRC_6_16 = parse(70, csv);
            boolean[][][] FRC_7_16 = parse(83, csv);
            boolean[][][] FRC_8_16 = parse(97, csv);
            boolean[][][] FRC_9_16 = ArrayUtils.inverse(FRC_7_16);
            boolean[][][] FRC_10_16 = ArrayUtils.inverse(FRC_6_16);
            boolean[][][] FRC_11_16 = ArrayUtils.inverse(FRC_5_16);
            boolean[][][] FRC_12_16 = ArrayUtils.inverse(FRC_4_16);
            boolean[][][] FRC_13_16 = ArrayUtils.inverse(FRC_3_16);
            boolean[][][] FRC_14_16 = ArrayUtils.inverse(FRC_2_16);
            boolean[][][] FRC_15_16 = ArrayUtils.inverse(FRC_1_16);
            FRC = new boolean[][][][] {
                  FRC_1_16, FRC_2_16, FRC_3_16, FRC_4_16, FRC_5_16, FRC_6_16,
                  FRC_7_16, FRC_8_16, FRC_9_16, FRC_10_16, FRC_11_16, FRC_12_16,
                  FRC_13_16, FRC_14_16, FRC_15_16};
        }
    }

    public short[][][][] pixelBaseFRC8bit(short[][][] rgbImage12bit) {
        short[][][] r = pixelBaseFRC8bit(rgbImage12bit[0]);
        short[][][] g = pixelBaseFRC8bit(rgbImage12bit[1]);
        short[][][] b = pixelBaseFRC8bit(rgbImage12bit[2]);
        return rgb2Frame(r, g, b);
    }

    private static short[][][][] rgb2Frame(short[][][] r, short[][][] g,
                                           short[][][] b) {
        int height = r[0].length;
        int width = r[0][0].length;
        short[][][][] result = new short[4][3][height][width];
        for (int frame = 0; frame < 4; frame++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[frame][0][h][w] = r[frame][h][w];
                    result[frame][1][h][w] = g[frame][h][w];
                    result[frame][2][h][w] = b[frame][h][w];
                }
            }
        }
        return result;
    }

    public short[][][] pixelBaseFRC8bit(short[][] image12bit) {
        int height = image12bit.length;
        int width = image12bit[0].length;
        short[][][] result = new short[4][height][width];
        for (int frame = 0; frame < 4; frame++) {
            frc8Bit0(image12bit, frame, result[frame]);
        }
        //frame, h ,w

        return result;
    }

    /**
     *
     * @param image12bit short[][][] ch/h/w
     * @return short[][][][] frame, ch, h, w
     */
    public short[][][][] subPixelBaseFRC8bit(short[][][] image12bit) {
        short[][] oneImage = ArrayUtils.rgb2One(image12bit);
        ////frame, h ,w
        short[][][] pixelBaseFRC = pixelBaseFRC8bit(oneImage);
        short[][][] f0 = ArrayUtils.one2RGB(pixelBaseFRC[0]);
        short[][][] f1 = ArrayUtils.one2RGB(pixelBaseFRC[1]);
        short[][][] f2 = ArrayUtils.one2RGB(pixelBaseFRC[2]);
        short[][][] f3 = ArrayUtils.one2RGB(pixelBaseFRC[3]);
        short[][][][] result = new short[][][][] {f0, f1, f2, f3};
        return result;
    }


    private void frc8Bit0(short[][] image12bit, int frame,
                          short[][] frcImage8Bit) {
        int height = image12bit.length;
        int width = image12bit[0].length;
        short d, d2 = 0, level = 0;
        int h_, w_;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
//                if (h == 16) {
//                    int a = 1;
//                }

                d = image12bit[h][w];

                if (patternCount == PatternCount.FRC8) {
                    d2 = (short) (d / 2); //11bit
                    level = (short) (d2 & 7);
                    d2 = (short) (d2 - level); //low
                    d2 = (short) (d2 >> 3); //8bit
                } else if (patternCount == PatternCount.FRC16) {
                    level = (short) (d & 15);
                    d2 = (short) (d - level);
                    d2 = (short) (d2 >> 4); //8bit
                }

                frcImage8Bit[h][w] = d2;

                h_ = h & 7;
                w_ = w & 7;

                //FRC: level,frame,h,w
                if (level != 0 && FRC[level - 1][frame][h_][w_]) {
                    frcImage8Bit[h][w]++;
                    frcImage8Bit[h][w] = (frcImage8Bit[h][w] > 255) ? 255 :
                                         frcImage8Bit[h][w];
                }

            }
        }
    }

    public int getFrameCount() {
        return frameCount;
    }

}
