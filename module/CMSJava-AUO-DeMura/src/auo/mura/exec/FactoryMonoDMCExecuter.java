package auo.mura.exec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import auo.mura.CorrectionData;
import auo.mura.DeMuraParameter;
import auo.mura.MuraCompensationProducer;
import jxl.read.biff.BiffException;
import shu.io.files.CSVFile;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FactoryMonoDMCExecuter {
    public FactoryMonoDMCExecuter() {
        super();
    }

    private static DeMuraParameter getDeMuraParameter(String tablefilename) throws java.io.
            FileNotFoundException, java.io.IOException {
        DeMuraParameter p = new DeMuraParameter();
        CSVFile csv = new CSVFile(tablefilename);
        int index = 10;
        index++;
//        double version = csv.getCell(index++, 1);
        p.layerNumber = (int) csv.getCell(index++, 1);
        p.blackLimit = (short) csv.getCell(index++, 1);
        p.planeLevel[0] = (short) csv.getCell(index++, 1);
        p.planeLevel[1] = (short) csv.getCell(index++, 1);
        p.planeLevel[2] = (short) csv.getCell(index++, 1);
        p.whiteLimit = (short) csv.getCell(index++, 1);
        p.hBlockSize = (int) Math.pow(2, csv.getCell(index++, 1));
        p.vBlockSize = (int) Math.pow(2, csv.getCell(index++, 1));
        p.hLutNumber = (int) csv.getCell(index++, 1);
        p.vLutNumber = (int) csv.getCell(index++, 1);
        p.blockArea = (int) csv.getCell(index++, 1);
        p.planeCoef[0] = (short) csv.getCell(index++, 1);
        p.planeCoef[1] = (short) csv.getCell(index++, 1);
        p.planeCoef[2] = (short) csv.getCell(index++, 1);
        p.planeCoef[3] = (short) csv.getCell(index++, 1);
        p.dataMag[0] = (short) csv.getCell(index++, 1);
        p.dataMag[1] = (short) csv.getCell(index++, 1);
        p.dataMag[2] = (short) csv.getCell(index++, 1);
        p.dataOffset[0] = (short) csv.getCell(index++, 1);
        p.dataOffset[1] = (short) csv.getCell(index++, 1);
        p.dataOffset[2] = (short) csv.getCell(index++, 1);
        p.dataOffset[0] = (short) (Short.valueOf((short) (p.dataOffset[0] * 16)) / 16);
        p.dataOffset[1] = (short) (Short.valueOf((short) (p.dataOffset[1] * 16)) / 16);
        p.dataOffset[2] = (short) (Short.valueOf((short) (p.dataOffset[2] * 16)) / 16);

        return p;
    }

    private static CorrectionData getCorrectionData(String tablefilename, DeMuraParameter p) throws
            BiffException, java.io.IOException {
        CorrectionData d = new CorrectionData(tablefilename, p, CorrectionData.Type.AUOHex2);
        return d;
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println(
                    "Usage: demura [parameter & table filename] [image filename] [output dirname]");
            return;
        }
        String tablefilename = args[0];
        String imagefilename = args[1];
        String outputdirname = args[2];
        System.out.println("parameter & table filename: \t" + tablefilename);
        System.out.println("image filename: \t\t" + imagefilename);
        System.out.println("output dirname: \t\t" + outputdirname);

        try {
            DeMuraParameter p = getDeMuraParameter(tablefilename);
            CorrectionData d = getCorrectionData(tablefilename, p);

//            String outputdirname = "output";
            File dir = new File(outputdirname);
            if (!dir.exists()) {
                dir.mkdir();
            }
            MonoDMCSimulator.simulate(d, imagefilename, outputdirname,
                                                  MuraCompensationProducer.DitheringType.Hardware_2_, true, false, 1
                                                  );

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (BiffException ex) {
            ex.printStackTrace();
        }
    }
}
