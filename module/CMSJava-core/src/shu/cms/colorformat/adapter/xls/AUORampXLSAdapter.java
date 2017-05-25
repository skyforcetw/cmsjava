package shu.cms.colorformat.adapter.xls;

import java.io.*;
import java.io.File;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.adapter.TargetAdapter.Style;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.lcd.LCDTargetBase.Number;
import shu.util.log.*;
import shu.io.files.ExcelFile;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AUORampXLSAdapter extends TargetAdapter {
    private File file;
    private InputStream inputStream;
//  private ExcelFile xls;
    private LCDTargetBase.Number number = LCDTargetBase.Number.Ramp1024;

    public static void main(String[] args) throws FileNotFoundException {
        AUORampXLSAdapter adapter = new AUORampXLSAdapter(
                "D:\\ณnล้\\nobody zone\\exp data\\CCTv3\\111118\\probility test\\Measurement01.xls");
        for (CIEXYZ XYZ : adapter.getXYZList()) {
            System.out.println(XYZ);
        }
    }

    public AUORampXLSAdapter(String filename) throws FileNotFoundException {
        this(filename, LCDTargetBase.Number.Ramp1024);
    }

    private List<RGB> rgbList = null;
    public AUORampXLSAdapter(String filename, List<RGB> rgbList) throws
            FileNotFoundException {
        this(filename, LCDTargetBase.Number.Unknow);
        this.rgbList = rgbList;
    }

    public AUORampXLSAdapter(String filename, LCDTargetBase.Number number) throws
            FileNotFoundException {
        this(new FileInputStream(filename), number);
        file = new File(filename);
    }

    public AUORampXLSAdapter(InputStream inputStream) {
        this(inputStream, LCDTargetBase.Number.Ramp1024);
    }

    public AUORampXLSAdapter(InputStream inputStream,
                             LCDTargetBase.Number number) {
        this.inputStream = inputStream;
        this.number = number;
    }

    /**
     * estimateLCDTargetNumber
     *
     * @return Number
     */
    public Number estimateLCDTargetNumber() {
        return number;
    }

    /**
     * getAbsolutePath
     *
     * @return String
     */
    public String getAbsolutePath() {
        if (null != file) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * getFileDescription
     *
     * @return String
     */
    public String getFileDescription() {
        return "AUO Ramp Excel File";
    }

    /**
     * getFileNameExtension
     *
     * @return String
     */
    public String getFileNameExtension() {
        return "xls";
    }

    /**
     * getFilename
     *
     * @return String
     */
    public String getFilename() {
        if (null != file) {
            return file.getName();
        } else {
            return null;
        }

    }

    /**
     * getPatchNameList
     *
     * @return List
     */
    public List getPatchNameList() {
        LCDTarget target = LCDTargetBase.Instance.get(number);
        if (target != null) {
            return target.filter.nameList();
        } else {
            return null;
        }
    }

    /**
     * getRGBList
     *
     * @return List
     */
    public List<RGB> getRGBList() {
        if (null == rgbList) {
            rgbList = LCDTargetBase.Instance.getRGBList(number);
        }
        return rgbList;
    }

    /**
     * getReflectSpectraList
     *
     * @return List
     */
    public List getReflectSpectraList() {
        throw new UnsupportedOperationException();
    }

    /**
     * getSpectraList
     *
     * @return List
     */
    public List getSpectraList() {
        throw new UnsupportedOperationException();
    }

    /**
     * getStyle
     *
     * @return Style
     */
    public Style getStyle() {
        return Style.RGBXYZ;
    }

    /**
     * getXYZList
     *
     * @return List
     */
    public List<CIEXYZ> getXYZList() {
        if (XYZList == null) {
            int patchCount = (number != Number.Unknow) ? number.getPatchCount() :
                             getRGBList().size();
            XYZList = new ArrayList<CIEXYZ>(patchCount);
            int[][] indexArray = new int[][] { {
                                 9, 10, 11}, {
                                 12, 13, 14}, {
                                 15, 16, 17}, {
                                 1, 2, 3}
            };
//      int[][] indexArray = new int[][] {
//          {
//          10, 11, 12}, {
//          13, 14, 15}, {
//          16, 17, 18}, {
//          1, 2, 3}
//      };

            try {
                double[] xyYValues = new double[3];
                ExcelFile xls = new ExcelFile(inputStream);
                int rows = xls.getRows();

                {
                    //white
                    int[] index = indexArray[3];
                    for (int x = rows - 1; x > 0; x--) {
                        xyYValues[0] = xls.getCell(index[0], x);
                        xyYValues[1] = xls.getCell(index[1], x);
                        xyYValues[2] = xls.getCell(index[2], x);
                        CIExyY xyY = new CIExyY(xyYValues);
                        XYZList.add(xyY.toXYZ());
                    }
                }
                for (RGB.Channel ch : RGB.Channel.RGBChannel) {
                    //r g b
                    int[] index = indexArray[ch.getArrayIndex()];

                    for (int x = rows - 1; x > 0; x--) {
                        CIEXYZ XYZ = null;
                        if (!xls.isEmpty(index[0], x)) {
                            xyYValues[0] = xls.getCell(index[0], x);
                            xyYValues[1] = xls.getCell(index[1], x);
                            xyYValues[2] = xls.getCell(index[2], x);
                            if (0 == xyYValues[2]) {
                                continue;
                            }
                            XYZ = new CIExyY(xyYValues).toXYZ();
                        } else {
                            XYZ = new CIEXYZ();
                        }
                        XYZList.add(XYZ);
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.log.error("", ex);
            } catch (IOException ex) {
                Logger.log.error("", ex);
            } catch (BiffException ex) {
                Logger.log.error("", ex);
            }

        }
        return XYZList;
    }

    private List<CIEXYZ> XYZList = null;

    /**
     * isInverseModeMeasure
     *
     * @return boolean
     */
    public boolean isInverseModeMeasure() {
        return false;
    }

    /**
     * probeParsable
     *
     * @return boolean
     */
    public boolean probeParsable() {
        return false;
    }
}
