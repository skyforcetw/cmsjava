package auo.mura;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import auo.math.lut.CubeTable;
import auo.mura.interp.ThreeDInterpolation;
import auo.mura.util.UnsignedByte;
import jxl.read.biff.BiffException;
import shu.io.files.CSVFile;
import shu.io.files.FileExtractIF;
import shu.util.Searcher;

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
public class CorrectionData {

    /**
     * 僅建議使用Floating8bit, Floating10bit
     * 其餘type因為格式不斷的變化, 不確定是否可以支援,
     */
    public static enum Type {
        Floating8bit, Floating10bit, /*Integer1, Integer2,*/ Integer, AUOHex, AUOHex2
    }


    /**
     *
     * @param filename String
     * @throws IOException
     * @throws BiffException
     * @deprecated
     */
    public CorrectionData(String filename) throws IOException, BiffException {
        this(filename, Type.Floating8bit);
        this.deMuraParameter = new DeMuraParameter();
    }

    private DeMuraParameter deMuraParameter;

    public CorrectionData(String filename, DeMuraParameter deMuraParameter, Type type) throws
            FileNotFoundException, IOException {
        this.deMuraParameter = deMuraParameter;

        this.type = type;
        this.filename = filename;
        dataFile = new CSVFile(filename);
        init();

    }

    public CorrectionData(String filename, DeMuraParameter deMuraParameter) throws
            FileNotFoundException, IOException {
        this(filename, deMuraParameter, Type.AUOHex);
    }

    public Type getType() {
        return type;
    }


    public CorrectionData(String filename, Type type) throws IOException,
            BiffException {
        this.type = type;
        this.filename = filename;
        dataFile = new CSVFile(filename);
        init();
    }

    private FileExtractIF dataFile;
    private String filename;
    private Type type;
    private int wblock, hblock;
    private int level;
    private double[][][][] data; //data[levelcount][rgb][h][w] = cell;
    private short[][][][] shortdata; //data[levelcount][rgb][h][w] = cell;
    private short[] grayLevelOfData;
//    private short[] grayLevelOfIntegerCorrectData;
    private short maxOutput = 4080;

    public int getGrayLevelOfData(int indexOfLevel) {
        return grayLevelOfData[indexOfLevel];
    }

    public short[] getGrayLevelOfData() {
        return grayLevelOfData;
    }

    private static int setValue2KeyValue(short[] xKeys, short[] yKeys,
                                         int index, short key, short value,
                                         CubeTable.KeyValue[] keyValues) {
        for (short y : yKeys) {
            for (short x : xKeys) {

                CubeTable.KeyValue keyValue = new CubeTable.KeyValue(new short[] {
                        x, y,
                        key}, new short[] {value, value, value});
                keyValues[index++] = keyValue;
            }
        }

        return index;
    }

    public boolean isIntegerType() {
        boolean integer = type == Type.Integer || type == Type.AUOHex || type == Type.AUOHex2;
        return integer;
    }

    public CubeTable getCubeTable(short lowerBound, short upperBound,
                                  int width, int height, int startIndex,
                                  int inputBit, int outputBit) {
        final short[] xKeys = new short[wblock];
        final short[] yKeys = new short[hblock];
        int wpiece = (int) Math.ceil((double) width / (wblock - 1));
        int hpiece = (int) Math.ceil((double) height / (hblock - 1));
        for (int x = 0; x < wblock; x++) {
            xKeys[x] = (short) (wpiece * x + startIndex);
        }
        for (int x = 0; x < hblock; x++) {
            yKeys[x] = (short) (hpiece * x + startIndex);
        }

        final short max = (inputBit == 10) ? (short) 1023 : 255;
        final short[] zKeys = getZKeys(lowerBound, upperBound, max);
        int index = 0;
        int size = zKeys.length * xKeys.length * yKeys.length;
        CubeTable.KeyValue[] keyValues = new CubeTable.KeyValue[size];

        boolean addLowerLevel = lowerBound != 0;
        boolean addUpperBound = upperBound != max;
        //0
        index = setValue2KeyValue(xKeys, yKeys, index, (short) 0, (short) 0, keyValues);
        int tableBit = (type == Type.Integer || type == Type.AUOHex) ? 12 : 8;
        boolean integer = isIntegerType();
        short gain = integer ? 1 : (short) Math.pow(2, outputBit - tableBit);
        //lowerLevel
        if (addLowerLevel) {
            index = setValue2KeyValue(xKeys, yKeys, index, (short) (lowerBound * gain),
                                      (short) lowerBound, keyValues);
        }

        int level = 0;
        for (short grayLevel : grayLevelOfData) {
            int yindex = 0;
            for (short y : yKeys) {
                int xindex = 0;
                for (short x : xKeys) {

                    //data[levelcount][rgb][h][w] = cell;
                    short r = shortdata[level][0][yindex][xindex];
                    short g = shortdata[level][1][yindex][xindex];
                    short b = shortdata[level][2][yindex][xindex];

                    CubeTable.KeyValue keyValue = new CubeTable.KeyValue(new short[] {
                            x, y, grayLevel}, new short[] {r, g, b});
                    keyValues[index++] = keyValue;
                    xindex++;
                }
                yindex++;
            }
            level++;
        }

        //upperLevel
        if (addUpperBound) {
            index = setValue2KeyValue(xKeys, yKeys, index, upperBound,
                                      (short) (upperBound * gain), keyValues);
        }

        //255
        index = setValue2KeyValue(xKeys, yKeys, index, max, maxOutput,
                                  keyValues);

        CubeTable cubeTable = new CubeTable(keyValues, xKeys, yKeys, zKeys);
        cubeTable.registerIndexFinderIF(new CubeTable.IndexFinderIF() {
            public int getIndex(short[] gridKey) {
                int xIndex = Searcher.leftBinarySearch(xKeys, gridKey[0]);
                int yIndex = Searcher.leftBinarySearch(yKeys, gridKey[1]);
                int zIndex = Searcher.leftBinarySearch(zKeys, gridKey[2]);
                int index = zIndex * xyPlaneSize + xKeys.length * yIndex +
                            xIndex;
                return index;
            }

            private final int xyPlaneSize = xKeys.length * yKeys.length;
            public int getIndex(int[] gridKeyIndex) {
                int xIndex = gridKeyIndex[0];
                int yIndex = gridKeyIndex[1];
                int zIndex = gridKeyIndex[2];
                int index = zIndex * xyPlaneSize + xKeys.length * yIndex +
                            xIndex;
                return index;
            }
        });
        return cubeTable;
    }

    private short[] getZKeys(short lowerBound, short upperBound, short max) {
        boolean addLowerLevel = lowerBound != 0;
        boolean addUpperBound = upperBound != max;
        int totalLevel = level + 2 + (addLowerLevel ? 1 : 0) +
                         (addUpperBound ? 1 : 0);
        short[] zKeys = new short[totalLevel];
        int zIndex = 0;
        zKeys[zIndex++] = 0;
        if (addLowerLevel) {
            zKeys[zIndex++] = lowerBound;
        }

        for (int x = 0; x < level; zIndex++, x++) {
            zKeys[zIndex] = grayLevelOfData[x];
        }
        if (addUpperBound) {
            zKeys[zIndex++] = upperBound;
        }
        zKeys[zIndex++] = max;
        return zKeys;
    }


    private void parseFloating(int bit) {
        level = (int) dataFile.getCell(1, 0);
        wblock = (int) dataFile.getCell(2, 0);
        hblock = (int) dataFile.getCell(2, 1);
        shortdata = new short[level][3][hblock][wblock];
        grayLevelOfData = new short[level];

        int hOfChannel = 1 + hblock;
        int hOfLevel = 1 + hOfChannel * 3;
        int yOfLevelIndex = 2;
        int gain = -1;
        switch (bit) { //目標10bit
        case 8:
            gain = 4;
            break;
        case 10:
            gain = 1;
            break;
        default:
            throw new IllegalArgumentException();
        }

        for (int levelcount = 0; levelcount < level; levelcount++) {
            int xOfLevelIndex = 2 + levelcount * hOfLevel + 1;
            double grayLevel = dataFile.getCell(xOfLevelIndex, yOfLevelIndex);
            grayLevelOfData[levelcount] = (short) (grayLevel * gain); //目標10bit
            short grayLevel12bit = (short) (grayLevelOfData[levelcount] * 4); //轉成12bit

            for (int rgb = 0; rgb < 3; rgb++) {
                for (int h = 0; h < hblock; h++) {
                    for (int w = 0; w < wblock; w++) {

                        int x = 2 + levelcount * hOfLevel + rgb * hOfChannel +
                                2 + h;
                        int y = w;
                        double cell = dataFile.getCell(x, y);
                        /**
                         * 必須轉成12bit補償值
                         */
                        short comp12bit = (short) Math.round(cell * gain * 4 - grayLevel12bit);
                        shortdata[levelcount][rgb][h][w] = comp12bit;
                    }
                }
            }
        }

    }

    private void parseInteger2() {
        level = 3; //(int) dataFile.getCell(1, 0);
        wblock = (int) dataFile.getCell(1, 0);
        hblock = (int) dataFile.getCell(1, 1);
        shortdata = new short[level][3][hblock][wblock];
        grayLevelOfData = new short[level];

        int hOfLevel = 1 + hblock;
        int yOfLevelIndex = 1;

        for (int levelcount = 0; levelcount < level; levelcount++) {
            int xOfLevelIndex = 2 + levelcount * hOfLevel;
            double grayLevel = dataFile.getCell(xOfLevelIndex, yOfLevelIndex);
            grayLevelOfData[levelcount] = (short) grayLevel;

            for (int h = 0; h < hblock; h++) {
                for (int w = 0; w < wblock; w++) {

                    int x = 3 + levelcount * hOfLevel + h;
                    int y = w;
                    short cell = (short) dataFile.getCell(x, y);
                    shortdata[levelcount][0][h][w] = cell;
                }
            }

            for (int ch = 1; ch <= 2; ch++) {
                for (int h = 0; h < hblock; h++) {
                    System.arraycopy(shortdata[levelcount][0][h], 0,
                                     shortdata[levelcount][ch][h],
                                     0, wblock);
                }
            }

        }
        throw new IllegalStateException("");
    }

    /**
     * @deprecated
     */
    private void parseInteger1() {
        level = 3; //(int) dataFile.getCell(1, 0);
        wblock = (int) dataFile.getCell(0, 1);
        hblock = (int) dataFile.getCell(0, 2);
        shortdata = new short[level][3][hblock][wblock];
        grayLevelOfData = new short[] {
                          100, 204, 502};

        int hOfLevel = hblock;

        for (int levelcount = 0; levelcount < level; levelcount++) {
            for (int h = 0; h < hblock; h++) {
                for (int w = 0; w < wblock; w++) {
                    int x = 1 + levelcount * hOfLevel + h;
                    int y = w;
                    short cell = (short) dataFile.getCell(x, y);
                    shortdata[levelcount][0][h][w] = cell;
                }
            }
        }

        for (int ch = 1; ch <= 2; ch++) {
            for (int levelcount = 0; levelcount < level; levelcount++) {
                for (int h = 0; h < hblock; h++) {
                    System.arraycopy(shortdata[levelcount][0][h], 0,
                                     shortdata[levelcount][ch][h],
                                     0, wblock);
                }
            }

        }
        throw new IllegalStateException("");
    }


    private void parseAUOHex() {
        level = deMuraParameter.layerNumber;
        wblock = deMuraParameter.hLutNumber;
        hblock = deMuraParameter.vLutNumber;
        shortdata = new short[level][3][hblock][wblock];
        grayLevelOfData = deMuraParameter.planeLevel;

        int yindex = 0;

        for (int levelcount = 0; levelcount < level; levelcount++) {

            for (int h = 0; h < hblock; h++) {
                for (int w = 0; w < wblock; w++) {
                    String cellString = dataFile.getCellAsString(yindex++, 0);
                    byte b = UnsignedByte.valueOf(cellString, 16);
                    shortdata[levelcount][0][h][w] = b;
                }
            }

        }

        for (int ch = 1; ch <= 2; ch++) {
            for (int levelcount = 0; levelcount < level; levelcount++) {
                for (int h = 0; h < hblock; h++) {
                    System.arraycopy(shortdata[levelcount][0][h], 0,
                                     shortdata[levelcount][ch][h],
                                     0, wblock);
                }
            }
        }
//        throw new IllegalStateException("");
    }

    private void parseAUOHex2() {
        level = deMuraParameter.layerNumber;
        wblock = deMuraParameter.hLutNumber;
        hblock = deMuraParameter.vLutNumber;
        shortdata = new short[level][3][hblock][wblock];
        grayLevelOfData = deMuraParameter.planeLevel;

        int heightOfOneLevel = hblock + 1;
        for (int levelcount = 0; levelcount < level; levelcount++) {
            for (int h = 0; h < hblock; h++) {
                for (int w = 0; w < wblock; w++) {
                    int hindex = AUOHex2StartIndex + heightOfOneLevel * levelcount + h;
                    String cellString = dataFile.getCellAsString(hindex, w);
                    byte b = UnsignedByte.valueOf(cellString, 16);
                    shortdata[levelcount][0][h][w] = b;
                }
            }

        }

        for (int ch = 1; ch <= 2; ch++) {
            for (int levelcount = 0; levelcount < level; levelcount++) {
                for (int h = 0; h < hblock; h++) {
                    System.arraycopy(shortdata[levelcount][0][h], 0,
                                     shortdata[levelcount][ch][h],
                                     0, wblock);
                }
            }
        }
//        throw new IllegalStateException("");
    }


    private static int AUOHex2StartIndex = 36;
    public static void setAUOHex2StartIndex(int startIndex) {
        AUOHex2StartIndex = startIndex;
    }


    private void init() {
        if (type == Type.Floating8bit) {
            parseFloating(8);
        } else if (type == Type.Floating10bit) {
            parseFloating(10);
        } else if (type == Type.Integer) {
            parseInteger2();
        } else if (type == Type.AUOHex) {
            if (null == deMuraParameter) {
                throw new IllegalStateException("null == deMuraParameter");
            }
            parseAUOHex();
        } else if (type == Type.AUOHex2) {
            if (null == deMuraParameter) {
                throw new IllegalStateException("null == deMuraParameter");
            }
            parseAUOHex2();
        }

    }

    public int getBlockWCount() {
        return wblock;
    }

    public int getBlockHCount() {
        return hblock;
    }


    public static void main(String[] args) throws BiffException, IOException {
        CorrectionData correctiondata = new CorrectionData(
                "DMC/executer/cremovision/20140530--0007_2_data.csv", Type.Floating10bit);

    }

    public int getLevel() {
        return level;
    }


    public DeMuraParameter getDeMuraParameter() {
        return deMuraParameter;
    }

    public void produceParameterFromSelf() {
        this.deMuraParameter = new DeMuraParameter(this);
    }

    /**
     *
     * @param filename String
     * @param muraCompensationProducer MuraCompensationProducer
     * @throws IOException
     * @deprecated
     */
    public void storeToFlashFormat(String filename,
                                   MuraCompensationProducer
                                   muraCompensationProducer) throws
            IOException {
        byte[] header = getFlashFormatHeader(muraCompensationProducer);
        byte[] lut = getFlashFormatLUT();
        StringBuilder buf = new StringBuilder();
        for (byte b : header) {
            String hex = toHexString(b);
            buf.append(hex);
            buf.append('\n');
        }
        for (byte b : lut) {
            String hex = toHexString(b);
            buf.append(hex);
            buf.append('\n');
        }

        Writer writer = new BufferedWriter(new FileWriter(filename));
        writer.write(buf.toString());
        writer.flush();
        writer.close();
    }

    /**
     *
     * @param muraCompensationProducer MuraCompensationProducer
     * @return byte[]
     * @deprecated
     */
    private byte[] getFlashFormatHeader(MuraCompensationProducer
                                        muraCompensationProducer) {
        byte[] header = new byte[33];
        int layerNum = this.getGrayLevelOfData().length;
        header[0] = (byte) layerNum;

        int hLutNum = hblock;
        int vLutNum = wblock;
        header[1] = (byte) (hLutNum >> 4);
        header[2] = (byte) (((hLutNum & 15) << 4) + ((vLutNum >> 8) & 15));
        header[3] = (byte) (vLutNum & 255);

        int hblocksize0 = muraCompensationProducer.imageHeight / (hblock - 1);
        int wblocksize0 = muraCompensationProducer.imageWidth / (wblock - 1);
        int hblocksize = (int) (Math.log10(hblocksize0) / 0.3) - 1;
        int wblocksize = (int) (Math.log10(wblocksize0) / 0.3) - 1;
        header[4] = (byte) ((hblocksize << 4) + wblocksize);

        int level1 = grayLevelOfData[0] * 4;
        int level2 = grayLevelOfData[1] * 4;
        int level3 = grayLevelOfData[2] * 4;

        header[9] = (byte) (level1 >> 2);
        header[10] = (byte) (((level1 & 3) << 6) + (level2 >> 4));
        header[11] = (byte) (((level2 & 15) << 4) + (level3 >> 8));
        header[12] = (byte) (level3 & 255);

        ThreeDInterpolation interp = muraCompensationProducer.
                                     threeDInterpolation;
        short[] coefs = interp.getCoefs();

        header[15] = (byte) (coefs[0] >> 8);
        header[16] = (byte) (coefs[0] & 255);
        header[17] = (byte) (coefs[1] >> 8);
        header[18] = (byte) (coefs[1] & 255);
        header[19] = (byte) (coefs[2] >> 8);
        header[20] = (byte) (coefs[2] & 255);
        header[21] = (byte) (coefs[3] >> 8);
        header[22] = (byte) (coefs[3] & 255);

        int blackLimit = interp.getBlackLimitGrayLevel() * 4;
        int whiteLimit = interp.getWhiteLimitGrayLevel() * 4;
        header[23] = (byte) (blackLimit >> 4);
        header[24] = (byte) (((blackLimit & 15) << 4) + ((whiteLimit >> 8) & 15));
        header[25] = (byte) (whiteLimit & 255);

        short[] magnitude = interp.getMagnitude();
        header[27] = (byte) (((magnitude[2] & 3) << 4) +
                             ((magnitude[1] & 3) << 2) + ((magnitude[0] & 3)));

        short[] offset = interp.getOffset();

        header[28] = (byte) ((offset[0] >> 2) & 255);
        header[29] = (byte) (((offset[0] & 3) << 6) + ((offset[1] >> 4) & 63));
        header[30] = (byte) (((offset[1] & 15) << 4) + ((offset[2] >> 6) & 15));
        header[31] = (byte) ((offset[2] & 63) << 2);

        return header;
    }

    private static String toHexString(byte b) {
        int b0 = b;
        if (b < 0) {
            b0 = 255 + b + 1;
        }

        if (b0 < 16) {
            return "0" + Integer.toHexString(b0);
        } else {
            return Integer.toHexString(b0);
        }

    }

    /**
     *
     * @return byte[]
     * @deprecated
     */
    private byte[] getFlashFormatLUT() {
        int size = level * hblock * wblock;
        byte[] lut = new byte[size];
        int index = 0;
        for (int l = 0; l < this.level; l++) {
            for (int h = 0; h < this.hblock; h++) {
                for (int w = 0; w < this.wblock; w++) {
                    lut[index++] = (byte) data[l][0][h][w];
                }
            }
        }
        throw new IllegalStateException("");
    }
}
