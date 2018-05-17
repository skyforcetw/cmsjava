package auo.mura.verify;

import auo.mura.CorrectionData;
import auo.mura.DeMuraParameter;
import java.io.IOException;
import auo.mura.MuraCompensationProducer;
import jxl.read.biff.BiffException;
import auo.mura.Dithering;
import java.io.FileNotFoundException;
import java.io.File;

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
public class PatternResultSingleProducer {

//  private static short[][][] getFullPattern(short grayLevel) {
//    short[][][] image = new short[3][1080][1920];
//    for (int h = 0; h < 1080; h++) {
//      for (int w = 0; w < 1920; w++) {
//        image[0][h][w] = grayLevel;
//        image[1][h][w] = grayLevel;
//        image[2][h][w] = grayLevel;
//      }
//    }
//    return image;
//  }

  public static void specialProduce(short grayLevel) throws
      FileNotFoundException,
      IOException {
    String inputdir =
//        "Y:/Verify Items/Simulation Result/(3003)35.limit1_4 32(X0-0-0)";
//        "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/13.limit2_8 16(X2-1-0)";
//        "\\\\labd3c-e7f-021\\DeMura\\Verify Items\\Verify LUT\\Limit Case\\1920x1080\\low level debug";
        "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/7.limit2_8 8(X2-1-0)/";

//    String imageFilename =
//        "Y:/Verify Items/Limit Pattern/(3)pattern_11level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(1)pattern_256level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(2)pattern_2vertical1920x1080.bmp";
    short[][][] imageData = getFHDPattern(grayLevel);

    String parameterFilename =
//        "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Real Case/No.1/1920x1080/real_8x8/par.csv";
        inputdir + "/par.csv";
    String correctFilename =
        inputdir + "/lut.csv";

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);

//    parameter.dataOffset1 = 4021;
//    parameter.dataMag1 = 0;
    parameter.blackLimit = 0;
    parameter.planeB1Coef = 928;

    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);
    String outputdirname = inputdir + Integer.toString(grayLevel);
    new File(outputdirname).mkdir();
//        "D:/軟體/nobody zone/Mura/Verify Simulation Experiment/";
//        "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 4";
//        inputdir;

//    MuraCompensationExecuter.execute(correctiondata, imageFilename, outputdirname,
//                                     MuraCompensationProducer.DitheringType.
//                                     FloydSteinberg_Wiki_LineBased, true);
    MuraCompensationExecuter.execute(correctiondata, imageData,
                                     outputdirname,
                                     MuraCompensationProducer.DitheringType.
                                     FloydSteinberg_Wiki_LineBased, false, false,
                                     true, 4);
  }

  public static void main(String[] args) throws IOException, BiffException {
    normalProduce(args);
//    for (short grayLevel = 1019; grayLevel <= 1021; grayLevel++) {
//      specialProduce(grayLevel);
//    }
  }

  public static void normalProduce(String[] args) throws BiffException,
      IOException {
    Dithering.Debug = false;
    MuraCompensationProducer.SingleChannelProcess = false;
    simulate();
//    for (int x = 1; x <= 26; x++) {
//      main(x);
//    }
//    main(2);
  }

  static void convertTo8Bit(short[][][] imageData) {
    int chs = imageData.length;
    int height = imageData[0].length;
    int width = imageData[0][0].length;
    int data;
    for (int ch = 0; ch < chs; ch++) {
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          data = imageData[ch][h][w] / 4;
          imageData[ch][h][w] = (short) data;
        }
      }
    }
  }

  static void simulate() throws IOException {
//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit2_8 8(X0)";
//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit3_8 8(X0)";
    String basedir = "Y:/Verify Items/Simulation Result/";
    String inputdir =
        basedir + "(0014) - 2Vertical";
//        "Y:/實驗數據/Item4 - Plane Number(精簡Plane數以及測試對應Level值)/STEP2 - DeMura/10+50+50(1)_data(final)/";
//    "Y:/實驗數據/Item4 - Plane Number(精簡Plane數以及測試對應Level值)/STEP2 - DeMura/10+10+10(1)_data(final)/";
//        "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/7.limit2_8 8(X2-1-0)";
//         "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 4";
//        "Y:/Verify Items/Simulation Result/1.Real 8x8 - 11Level";
//        "Y:/Verify Items/Simulation Result/(0002)1.Real 8x8 - 11Level";
//        "Y:/Verify Items/Simulation Result/(3002)31.limit2_16 16(X1-1-0)";
//    "Y:/Verify Items/Simulation Result/(3003)32.limit2_16 16(X2-2-1)";
//        "Y:/Verify Items/Simulation Result/(3004)26.limit2_16 8(X1-1-0)";

//        "Y:/Verify Items/Simulation Result/(0004)7.limit2_8 8(X2-1-0)";
//        "Y:/Verify Items/Simulation Result/(3003)35.limit1_4 32(X0-0-0)";

    String imageFilename =
        "Y:/Verify Items/Limit Pattern/(3)pattern_11level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(1)pattern_256level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(2)pattern_2vertical1920x1080.bmp";

//    short[][][] imageData = getWQHDPattern2_1023L_2Vertical();
//    short[][][] imageData = get4K2KPattern2_1023L_2Vertical();
//    short[][][] imageData = getFHDPattern3_11L_();
    short[][][] imageData = getFHDPattern5_FRC();
//    short[][][] imageData = getFHDPattern2_1023L_2Vertical();
//    short[][][] imageData = getFHDPattern1_1023L();
//    short[][][] imageData = getWQHDPattern1_1023L();
//    short[][][] imageData = getFHDPattern( (short) 10);

//    MuraCompensationProducer.storeFullFrame(imageData[0], "wqhd.txt");

    String parameterFilename = inputdir + "/par.csv";
    String correctFilename = inputdir + "/lut.csv";

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);
    String outputdirname = inputdir;
    boolean fromBMP = false;
    boolean fromPG8Bit = false;
    if (true == fromBMP && true == fromPG8Bit) {
      fromBMP = false;
      convertTo8Bit(imageData);
    }
    int port = 2;

    boolean useDMC = false;
    boolean useDG = false;
    boolean useFRC = true;
    if (fromBMP) {
      MuraCompensationExecuter.execute(correctiondata, imageFilename,
                                       outputdirname,
                                       MuraCompensationProducer.DitheringType.
                                       FloydSteinberg_Wiki_LineBased, useDG,
                                       useFRC,
                                       port);

    }
    else {
      MuraCompensationExecuter.execute(correctiondata, imageData,
                                       outputdirname,
                                       MuraCompensationProducer.DitheringType.
                                       FloydSteinberg_Wiki_LineBased, useDG,
                                       useFRC, useDMC, port);

    }

  }

  public static short[][][] getFHDPattern1_1023L() {
    short[][][] pattern = new short[3][1080][1920];

    for (short x = 0; x <= 127; x++) {
      for (int h = 0; h < 1080; h++) {
        pattern[0][h][x] = x;
        pattern[1][h][x] = x;
        pattern[2][h][x] = x;
      }
    }
    for (short x = 128; x <= 1023; x++) {
      int w = 128 + (x - 128) * 2;
      for (int h = 0; h < 1080; h++) {
        pattern[0][h][w] = x;
        pattern[1][h][w] = x;
        pattern[2][h][w] = x;
        pattern[0][h][w + 1] = x;
        pattern[1][h][w + 1] = x;
        pattern[2][h][w + 1] = x;

      }
    }

    return pattern;
  }

  public static short[][][] getFHDPattern2_1023L_2Vertical() {
    short[][][] pattern = new short[3][1080][1920];
    int halfWidth = 1920 / 2;
//    short[] check = new short[1080];

    for (short gl = 0; gl <= 454; gl++) {
      int L1 = gl * 2;
      for (int w = 0; w < halfWidth; w++) {
        pattern[0][L1][w] = gl;
        pattern[1][L1][w] = gl;
        pattern[2][L1][w] = gl;
        pattern[0][L1 + 1][w] = gl;
        pattern[1][L1 + 1][w] = gl;
        pattern[2][L1 + 1][w] = gl;

        pattern[0][L1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1][halfWidth + w] = (short) (512 + gl);
        pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);

      }
    }

    for (short gl = 455; gl <= 511; gl++) {
      int L1 = 455 * 2 + (gl - 455) * 3;
      for (int w = 0; w < halfWidth; w++) {
        pattern[0][L1][w] = gl;
        pattern[1][L1][w] = gl;
        pattern[2][L1][w] = gl;
        pattern[0][L1 + 1][w] = gl;
        pattern[1][L1 + 1][w] = gl;
        pattern[2][L1 + 1][w] = gl;

        pattern[0][L1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1][halfWidth + w] = (short) (512 + gl);
        pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);
        if (L1 + 2 < 1080) {

          pattern[0][L1 + 2][w] = gl;
          pattern[1][L1 + 2][w] = gl;
          pattern[2][L1 + 2][w] = gl;

          pattern[0][L1 + 2][halfWidth + w] = (short) (512 + gl);
          pattern[1][L1 + 2][halfWidth + w] = (short) (512 + gl);
          pattern[2][L1 + 2][halfWidth + w] = (short) (512 + gl);
        }
      }
    }

//    for (int x = 0; x < 1080; x++) {
//      check[x] = pattern[0][x][0];
//    }

    return pattern;
  }

  public static short[][][] getFHDPattern5_FRC() {
    short[][][] pattern = new short[3][1080][1920];
    int halfWidth = 1920 / 2;

    for (short gl = 0; gl <= 67; gl++) {
      int L1 = gl * 16;
      for (int w = 0; w < halfWidth; w++) {

        for (int x = 0; x < 16; x++) {
          if ( (L1 + x) >= 1080) {
            break;
          }
          for (int ch = 0; ch < 3; ch++) {
            pattern[0][L1 + x][w] = gl;
            pattern[0][L1 + x][w + halfWidth] = (short) (gl + 957);
            pattern[0][L1 + x][w +
                halfWidth] = (pattern[0][L1 + x][w + halfWidth] > 1023) ? 1023 :
                pattern[0][L1 + x][w + halfWidth];
          }

        }
      }
    }

    short[] check1 = new short[1080];
    short[] check2 = new short[1080];
    for (int x = 0; x < 1080; x++) {
      check1[x] = pattern[0][x][0];
      check2[x] = pattern[0][x][1919];
    }

    return pattern;
  }

  public static short[][][] get4K2KPattern2_1023L_2Vertical() {
    short[][][] pattern = new short[3][2160][3840];
    int halfWidth = 3840 / 2;
    for (short gl = 0; gl <= 399; gl++) {
      int L1 = gl * 4;
      for (int w = 0; w < halfWidth; w++) {
        for (int x = 0; x < 4; x++) {
          pattern[0][L1 + x][w] = gl;
          pattern[1][L1 + x][w] = gl;
          pattern[2][L1 + x][w] = gl;

          pattern[0][L1 + x][halfWidth + w] = (short) (512 + gl);
          pattern[1][L1 + x][halfWidth + w] = (short) (512 + gl);
          pattern[2][L1 + x][halfWidth + w] = (short) (512 + gl);
        }

      }
    }

    for (short gl = 400; gl <= 511; gl++) {
      int L1 = 400 * 4 + (gl - 400) * 5;
      for (int w = 0; w < halfWidth; w++) {
        for (int x = 0; x < 5; x++) {
          pattern[0][L1 + x][w] = gl;
          pattern[1][L1 + x][w] = gl;
          pattern[2][L1 + x][w] = gl;

          pattern[0][L1 + x][halfWidth + w] = (short) (512 + gl);
          pattern[1][L1 + x][halfWidth + w] = (short) (512 + gl);
          pattern[2][L1 + x][halfWidth + w] = (short) (512 + gl);
        }

      }
    }

    return pattern;
  }

  public static short[][][] getWQHDPattern1_1023L() {
    short[][][] pattern = new short[3][1440][2560];

    for (short x = 0; x <= 511; x++) {
      for (int h = 0; h < 1440; h++) {
        pattern[0][h][x * 2] = x;
        pattern[1][h][x * 2] = x;
        pattern[2][h][x * 2] = x;
        pattern[0][h][x * 2 + 1] = x;
        pattern[1][h][x * 2 + 1] = x;
        pattern[2][h][x * 2 + 1] = x;

      }
    }
    for (short x = 512; x <= 1023; x++) {
      int w = 512 * 2 + (x - 512) * 3;
      for (int h = 0; h < 1440; h++) {
        pattern[0][h][w] = x;
        pattern[1][h][w] = x;
        pattern[2][h][w] = x;
        pattern[0][h][w + 1] = x;
        pattern[1][h][w + 1] = x;
        pattern[2][h][w + 1] = x;
        pattern[0][h][w + 2] = x;
        pattern[1][h][w + 2] = x;
        pattern[2][h][w + 2] = x;
      }
    }

    return pattern;
  }

  public static short[][][] getWQHDPattern2_1023L_2Vertical() {
    short[][][] pattern = new short[3][1440][2560];
    int halfWidth = 2560 / 2;
    for (short gl = 0; gl <= 95; gl++) {
      int L1 = gl * 2;
      for (int w = 0; w < halfWidth; w++) {
        pattern[0][L1][w] = gl;
        pattern[1][L1][w] = gl;
        pattern[2][L1][w] = gl;
        pattern[0][L1 + 1][w] = gl;
        pattern[1][L1 + 1][w] = gl;
        pattern[2][L1 + 1][w] = gl;

        pattern[0][L1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1][halfWidth + w] = (short) (512 + gl);
        pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);

      }
    }

    for (short gl = 96; gl <= 511; gl++) {
      int L1 = 96 * 2 + (gl - 96) * 3;
      for (int w = 0; w < halfWidth; w++) {
        pattern[0][L1][w] = gl;
        pattern[1][L1][w] = gl;
        pattern[2][L1][w] = gl;
        pattern[0][L1 + 1][w] = gl;
        pattern[1][L1 + 1][w] = gl;
        pattern[2][L1 + 1][w] = gl;
        pattern[0][L1 + 2][w] = gl;
        pattern[1][L1 + 2][w] = gl;
        pattern[2][L1 + 2][w] = gl;

        pattern[0][L1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1][halfWidth + w] = (short) (512 + gl);
        pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);
        pattern[0][L1 + 2][halfWidth + w] = (short) (512 + gl);
        pattern[1][L1 + 2][halfWidth + w] = (short) (512 + gl);
        pattern[2][L1 + 2][halfWidth + w] = (short) (512 + gl);

      }
    }

    return pattern;
  }

  public static short[][][] getFHDPattern(short grayLevel) {
    short[][][] pattern = new short[3][1080][1920];
    for (int ch = 0; ch < 3; ch++) {
      for (int h = 0; h < 1080; h++) {
        java.util.Arrays.fill(pattern[ch][h], grayLevel);
      }
    }
    return pattern;
  }

  public static short[][][] getFHDPattern3_11L_() {
    short[][][] pattern = new short[3][1080][1920];
    short[] grayLevel = new short[] {
        0, 31, 48, 100, 200, 304, 508, 712, 864, 883, 1023};
    short[] levelWidth = new short[] {
        176, 172, 172, 172, 172, 172, 172, 172, 172, 172, 196};

    int piece = grayLevel.length;
    int start = 0, end = 0;
    for (int p = 0; p < piece; p++) {

      start = end;
      int width = levelWidth[p];
      end += width;

      for (int w = start; w < end; w++) {
        for (int h = 0; h < 1080; h++) {
          pattern[0][h][w] = grayLevel[p];
          pattern[1][h][w] = grayLevel[p];
          pattern[2][h][w] = grayLevel[p];
        }
      }

    }
    return pattern;
  }

  public static short[][][] getWQHDPattern3_11L_() {
    short[][][] pattern = new short[3][2560][1440];
    short[] grayLevel = new short[] {
        0, 31, 48, 100, 200, 304, 508, 712, 864, 883, 1023};
    short[] levelWidth = new short[] {
        232, 232, 232, 233, 233, 233, 233, 233, 233, 233, 233};

    int piece = grayLevel.length;
    int start = 0, end = 0;
    for (int p = 0; p < piece; p++) {

      start = end;
      int width = levelWidth[p];
      end += width;

      for (int w = start; w < end; w++) {
        for (int h = 0; h < 1440; h++) {
          pattern[0][h][w] = grayLevel[p];
          pattern[1][h][w] = grayLevel[p];
          pattern[2][h][w] = grayLevel[p];
        }
      }

    }
    return pattern;
  }

  public static short[][][] getFHDPattern4_12L() {
    short[][][] pattern = new short[3][1080][1920];
    short[] grayLevel = new short[] {
        0, 1, 2, 3, 4, 5, 6, 7, 1020, 1021, 1022, 1023};

    int piece = grayLevel.length;
    int pieceWidth = 1920 / piece;
    for (int p = 0; p < piece; p++) {
      for (int w = p * pieceWidth; w < (p + 1) * pieceWidth; w++) {
        for (int h = 0; h < 1080; h++) {
          pattern[0][h][w] = grayLevel[p];
          pattern[1][h][w] = grayLevel[p];
          pattern[2][h][w] = grayLevel[p];
        }
      }
    }
    return pattern;
  }

  /**
   *
   * @param inputdir String
   * @param outputdir String
   * @param patternFilename String
   * @param DG boolean
   * @throws IOException
   * @deprecated
   */
  public static void simulate(String inputdir, String outputdir,
                              String patternFilename, boolean DG) throws
      IOException {

    String parameterFilename =
        inputdir + "/par.csv";
    String correctFilename =
        inputdir + "/lut.csv";

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);
//    String outputdirname =
//        "D:/軟體/nobody zone/Mura/Verify Simulation Experiment/";

    MuraCompensationExecuter.execute(correctiondata, patternFilename, outputdir,
                                     MuraCompensationProducer.DitheringType.
                                     FloydSteinberg_Wiki_LineBased, DG, false,
                                     4);

  }
}
