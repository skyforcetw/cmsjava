package auo.mura;

import shu.io.files.FileExtractIF;
import shu.io.files.CSVFile;
import java.io.*;
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
public class DeMuraParameter {

  /**
   Layer Number [0:1] 3
   Horizontal LUT number [0:11] 12
   Vertical LUT number [0:11] 12
   Horizontal Block Size [0:2] 3
   Vertical Block Size [0:2] 3
   Plane_Level 1 [0:11] 12
   Plane_Level 2 [0:11] 12
   Plane_Level 3 [0:11] 12
   Plane_b1_coef [0:15] 16
   Plane_12_coef [0:15] 16
   Plane_23_coef [0:15] 16
   Plane_3w_coef [0:15] 16
   Black_limit [0:11] 12
   White_limit [0:11] 12
   Data_mag_1 [0:1] 2
   Data_mag_2 [0:1] 2
   Data_mag_3 [0:1] 2
   Data_offset_1 [0:9] 10
   Data_offset_2 [0:9] 10
   Data_offset_3 [0:9] 10

   */
  public DeMuraParameter() {
    layerNumber = 3;
    planeLevel1 = 100;
    planeLevel2 = 304;
    planeLevel3 = 712;

//    hLutNumber = 241;
//    vLutNumber = 136;
//
//    hBlockSize = 8;
//    vBlockSize = 8;

    hLutNumber = 961;
    vLutNumber = 35;

    hBlockSize = 2;
    vBlockSize = 32;

    blackLimit = 0;
    whiteLimit = 1023;
    double blackSlope = 1;
    double whiteSlope = 1;

    planeB1Coef = (int) Math.round(blackSlope * Math.pow(2, 15) / planeLevel1);
    plane12Coef = (int) Math.round(Math.pow(2, 16) / (planeLevel2 - planeLevel1));
    plane23Coef = (int) Math.round(Math.pow(2, 16) / (planeLevel3 - planeLevel2));
    plane3WCoef = (int) Math.round(whiteSlope / (whiteLimit - planeLevel3) *
                                   Math.pow(2, 15));

    planeB1Coef = 655 / 2;
    plane12Coef = 321;
    plane23Coef = 160;
    plane3WCoef = 210 / 2;

  }

  public static enum Version {
    v0, v1} ;
    private Version version;
    public DeMuraParameter(String filename, Version version) throws java.io.
        FileNotFoundException,
        java.io.IOException {
      this.version = version;
      dataFile = new CSVFile(filename);
      init();
    }

    public DeMuraParameter(String filename) throws java.io.
        FileNotFoundException,
        java.io.IOException {
      this(filename, Version.v1);
    }

    private void init() {
      if (version == Version.v0) {
        int index = 0;
        layerNumber = (int) dataFile.getCell(index++, 0);
        hLutNumber = (int) dataFile.getCell(index++, 0);
        vLutNumber = (int) dataFile.getCell(index++, 0);
        hBlockSize = (int) dataFile.getCell(index++, 0);
        vBlockSize = (int) dataFile.getCell(index++, 0);

        blockArea = (int) dataFile.getCell(index++, 0);
        blockArea = (int) (32 * Math.pow(2, blockArea));

        blackLimit = (int) dataFile.getCell(index++, 0);
        planeLevel1 = (int) dataFile.getCell(index++, 0);
        planeLevel2 = (int) dataFile.getCell(index++, 0);
        planeLevel3 = (int) dataFile.getCell(index++, 0);
        whiteLimit = (int) dataFile.getCell(index++, 0);
        planeB1Coef = (int) dataFile.getCell(index++, 0);
        plane12Coef = (int) dataFile.getCell(index++, 0);
        plane23Coef = (int) dataFile.getCell(index++, 0);
        plane3WCoef = (int) dataFile.getCell(index++, 0);

        dataMag1 = (int) dataFile.getCell(index++, 0);
        dataMag2 = (int) dataFile.getCell(index++, 0);
        dataMag3 = (int) dataFile.getCell(index++, 0);
        dataOffset1 = (int) dataFile.getCell(index++, 0);
        dataOffset2 = (int) dataFile.getCell(index++, 0);
        dataOffset3 = (int) dataFile.getCell(index++, 0);
        dataOffset1 = Short.valueOf( (short) (dataOffset1 * 16)) / 16;
        dataOffset2 = Short.valueOf( (short) (dataOffset2 * 16)) / 16;
        dataOffset3 = Short.valueOf( (short) (dataOffset3 * 16)) / 16;
//    int a=1;
      }
      else if (version == Version.v1) {
        int index = 1;

        layerNumber = (int) dataFile.getCell(index++, 1);
        blockArea = (int) dataFile.getCell(index++, 1);
        blockArea = (int) (32 * Math.pow(2, blockArea));
        hBlockSize = (int) dataFile.getCell(index++, 1);
        hBlockSize = (int) (Math.pow(2, hBlockSize));
        vBlockSize = (int) dataFile.getCell(index++, 1);
        vBlockSize = (int) (Math.pow(2, vBlockSize));
        hLutNumber = (int) dataFile.getCell(index++, 1);
        vLutNumber = (int) dataFile.getCell(index++, 1);

        blackLimit = (int) dataFile.getCell(index++, 1);
        planeLevel1 = (int) dataFile.getCell(index++, 1);
        planeLevel2 = (int) dataFile.getCell(index++, 1);
        planeLevel3 = (int) dataFile.getCell(index++, 1);
        whiteLimit = (int) dataFile.getCell(index++, 1);

        planeB1Coef = (int) dataFile.getCell(index++, 1);
        plane12Coef = (int) dataFile.getCell(index++, 1);
        plane23Coef = (int) dataFile.getCell(index++, 1);
        plane3WCoef = (int) dataFile.getCell(index++, 1);

        dataMag1 = (int) dataFile.getCell(index++, 1);
        dataMag2 = (int) dataFile.getCell(index++, 1);
        dataMag3 = (int) dataFile.getCell(index++, 1);
        dataOffset1 = (int) dataFile.getCell(index++, 1);
        dataOffset2 = (int) dataFile.getCell(index++, 1);
        dataOffset3 = (int) dataFile.getCell(index++, 1);
        dataOffset1 = Short.valueOf( (short) (dataOffset1 * 16)) / 16;
        dataOffset2 = Short.valueOf( (short) (dataOffset2 * 16)) / 16;
        dataOffset3 = Short.valueOf( (short) (dataOffset3 * 16)) / 16;

      }
    }

    private FileExtractIF dataFile;

    public static void main(String[] args) throws java.io.FileNotFoundException,
        java.io.IOException {
//    DeMuraParameter parameter = new DeMuraParameter(
//        "demura sim/sim case1/par_NO4.csv");
      new DeMuraParameter();
    }

//  public double blackSlope;
//  public double whiteSlope;

    public int layerNumber;
    public int hLutNumber;
    public int vLutNumber;
    public int blockArea;
    public int hBlockSize;
    public int vBlockSize;
    public int planeLevel1;
    public int planeLevel2;
    public int planeLevel3;
    public int planeB1Coef;
    public int plane12Coef;
    public int plane23Coef;
    public int plane3WCoef;
    public int blackLimit;
    public int whiteLimit;
    public int dataMag1;
    public int dataMag2;
    public int dataMag3;
    public int dataOffset1;
    public int dataOffset2;
    public int dataOffset3;

  }
