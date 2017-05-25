package auo.mura;

import shu.io.files.CSVFile;
import shu.io.files.FileExtractIF;

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
    private FileExtractIF dataFile;
    public int layerNumber;
    public int hLutNumber;
    public int vLutNumber;
    public int blockArea;
    public int hBlockSize;
    public int vBlockSize;
    public short blackLimit;
    public short whiteLimit;
    public short planeLevel[];
    public short planeCoef[];
    public short dataMag[];
    public short dataOffset[];

    /**
     * 僅供測試用
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
        initLayer(layerNumber);
        planeLevel[0] = 100;
        planeLevel[1] = 304;
        planeLevel[2] = 712;

        hLutNumber = 961;
        vLutNumber = 35;

        hBlockSize = 2;
        vBlockSize = 32;

        blackLimit = 0;
        whiteLimit = 1023;
        double blackSlope = 1;
        double whiteSlope = 1;

        planeCoef[0] = (short) Math.round(blackSlope * Math.pow(2, 15) / planeLevel[0]);
        planeCoef[1] = (short) Math.round(Math.pow(2, 16) / (planeLevel[1] - planeLevel[0]));
        planeCoef[2] = (short) Math.round(Math.pow(2, 16) / (planeLevel[2] - planeLevel[1]));
        planeCoef[3] = (short) Math.round(whiteSlope / (whiteLimit - planeLevel[2]) *
                                          Math.pow(2, 15));

    }

    /**
     * 供CorrectionData呼叫使用
     * @param c CorrectionData
     */
    DeMuraParameter(CorrectionData c) {
        layerNumber = c.getLevel();
        initLayer(layerNumber);
        for (int layer = 0; layer < layerNumber; layer++) {
            planeLevel[layer] = (short) c.getGrayLevelOfData(layer);
        }

        hLutNumber = c.getBlockWCount();
        vLutNumber = c.getBlockHCount();

        hBlockSize = 8;
        vBlockSize = 8;

        blackLimit = 0;
        whiteLimit = 1020;

        planeCoef[0] = (short) Math.ceil(Math.pow(2, 16) / (planeLevel[0] - (blackLimit - 1)));
        for (int n = 1; n < layerNumber; n++) {
            planeCoef[n] = (short) Math.floor(Math.pow(2, 16) / (planeLevel[n] - planeLevel[n - 1]));
        }
        planeCoef[layerNumber] = (short) Math.ceil(Math.pow(2, 16) / ((whiteLimit + 1) - planeLevel[layerNumber - 1]));

    }

    public static enum Version {
        v0, v1, v2} ;

        private Version version;

        /**
         * 建議採用此建構式
         * @param filename String
         * @param version Version
         * @throws FileNotFoundException
         * @throws IOException
         */
        public DeMuraParameter(String filename, Version version) throws java.io.
                FileNotFoundException,
                java.io.IOException {
            this.version = version;
            dataFile = new CSVFile(filename);
            init();
        }

        /**
         * 預設採用v1版本的parameter
         * @param filename String
         * @throws FileNotFoundException
         * @throws IOException
         */
        public DeMuraParameter(String filename) throws java.io.
                FileNotFoundException,
                java.io.IOException {
            this(filename, Version.v1);
        }

        private void init() {
            if (version == Version.v0) {
                int index = 0;
                layerNumber = (int) dataFile.getCell(index++, 0);
                initLayer(layerNumber);
                hLutNumber = (int) dataFile.getCell(index++, 0);
                vLutNumber = (int) dataFile.getCell(index++, 0);
                hBlockSize = (int) dataFile.getCell(index++, 0);
                vBlockSize = (int) dataFile.getCell(index++, 0);

                blockArea = (int) dataFile.getCell(index++, 0);
                blockArea = (int) (32 * Math.pow(2, blockArea));

                blackLimit = (short) dataFile.getCell(index++, 0);
                planeLevel[0] = (short) dataFile.getCell(index++, 0);
                planeLevel[1] = (short) dataFile.getCell(index++, 0);
                planeLevel[2] = (short) dataFile.getCell(index++, 0);
                whiteLimit = (short) dataFile.getCell(index++, 0);
                planeCoef[0] = (short) dataFile.getCell(index++, 0);
                planeCoef[1] = (short) dataFile.getCell(index++, 0);
                planeCoef[2] = (short) dataFile.getCell(index++, 0);
                planeCoef[3] = (short) dataFile.getCell(index++, 0);

                dataMag[0] = (short) dataFile.getCell(index++, 0);
                dataMag[1] = (short) dataFile.getCell(index++, 0);
                dataMag[2] = (short) dataFile.getCell(index++, 0);
                dataOffset[0] = (short) dataFile.getCell(index++, 0);
                dataOffset[1] = (short) dataFile.getCell(index++, 0);
                dataOffset[2] = (short) dataFile.getCell(index++, 0);
                dataOffset[0] = (short) (Short.valueOf((short) (dataOffset[0] * 16)) / 16);
                dataOffset[1] = (short) (Short.valueOf((short) (dataOffset[1] * 16)) / 16);
                dataOffset[2] = (short) (Short.valueOf((short) (dataOffset[2] * 16)) / 16);

            } else if (version == Version.v1) {
                int index = 1;

                layerNumber = (int) dataFile.getCell(index++, 1);
                initLayer(layerNumber);
                blockArea = (int) dataFile.getCell(index++, 1);
                blockArea = (int) (32 * Math.pow(2, blockArea));
                hBlockSize = (int) dataFile.getCell(index++, 1);
                hBlockSize = (int) (Math.pow(2, hBlockSize));
                vBlockSize = (int) dataFile.getCell(index++, 1);
                vBlockSize = (int) (Math.pow(2, vBlockSize));
                hLutNumber = (int) dataFile.getCell(index++, 1);
                vLutNumber = (int) dataFile.getCell(index++, 1);

                blackLimit = (short) dataFile.getCell(index++, 1);
                planeLevel[0] = (short) dataFile.getCell(index++, 1);
                planeLevel[1] = (short) dataFile.getCell(index++, 1);
                planeLevel[2] = (short) dataFile.getCell(index++, 1);
                whiteLimit = (short) dataFile.getCell(index++, 1);

                planeCoef[0] = (short) dataFile.getCell(index++, 1);
                planeCoef[1] = (short) dataFile.getCell(index++, 1);
                planeCoef[2] = (short) dataFile.getCell(index++, 1);
                planeCoef[3] = (short) dataFile.getCell(index++, 1);

                dataMag[0] = (short) dataFile.getCell(index++, 1);
                dataMag[1] = (short) dataFile.getCell(index++, 1);
                dataMag[2] = (short) dataFile.getCell(index++, 1);
                dataOffset[0] = (short) dataFile.getCell(index++, 1);
                dataOffset[1] = (short) dataFile.getCell(index++, 1);
                dataOffset[2] = (short) dataFile.getCell(index++, 1);
                dataOffset[0] = (short) (Short.valueOf((short) (dataOffset[0] * 16)) / 16);
                dataOffset[1] = (short) (Short.valueOf((short) (dataOffset[1] * 16)) / 16);
                dataOffset[2] = (short) (Short.valueOf((short) (dataOffset[2] * 16)) / 16);

            } else if (version == Version.v2) {
                int index = 1;

                layerNumber = (int) dataFile.getCell(index++, 1);
                initLayer(layerNumber);
                blackLimit = (short) dataFile.getCell(index++, 1);
                planeLevel[0] = (short) dataFile.getCell(index++, 1);
                planeLevel[1] = (short) dataFile.getCell(index++, 1);
                planeLevel[2] = (short) dataFile.getCell(index++, 1);
                whiteLimit = (short) dataFile.getCell(index++, 1);

                hBlockSize = (int) dataFile.getCell(index++, 1);
                hBlockSize = (int) (Math.pow(2, hBlockSize));
                vBlockSize = (int) dataFile.getCell(index++, 1);
                vBlockSize = (int) (Math.pow(2, vBlockSize));
                hLutNumber = (int) dataFile.getCell(index++, 1);
                vLutNumber = (int) dataFile.getCell(index++, 1);
                blockArea = (int) dataFile.getCell(index++, 1);
                blockArea = (int) (32 * Math.pow(2, blockArea));

                planeCoef[0] = (short) dataFile.getCell(index++, 1);
                planeCoef[1] = (short) dataFile.getCell(index++, 1);
                planeCoef[2] = (short) dataFile.getCell(index++, 1);
                planeCoef[3] = (short) dataFile.getCell(index++, 1);

                dataMag[0] = (short) dataFile.getCell(index++, 1);
                dataMag[1] = (short) dataFile.getCell(index++, 1);
                dataMag[2] = (short) dataFile.getCell(index++, 1);
                dataOffset[0] = (short) dataFile.getCell(index++, 1);
                dataOffset[1] = (short) dataFile.getCell(index++, 1);
                dataOffset[2] = (short) dataFile.getCell(index++, 1);
                dataOffset[0] = (short) (Short.valueOf((short) (dataOffset[0] * 16)) / 16);
                dataOffset[1] = (short) (Short.valueOf((short) (dataOffset[1] * 16)) / 16);
                dataOffset[2] = (short) (Short.valueOf((short) (dataOffset[2] * 16)) / 16);
            }
        }


        private void initLayer(int layerNumber) {
            planeLevel = new short[layerNumber];
            planeCoef = new short[layerNumber + 1];
            dataMag = new short[layerNumber];
            dataOffset = new short[layerNumber];
        }


        public static short[] toShortArray(int[] parameters) {
            int length = parameters.length;
            short[] result = new short[length];
            for (int x = 0; x < length; x++) {
                result[x] = (short) parameters[x];
            }
            return result;
        }


    }
