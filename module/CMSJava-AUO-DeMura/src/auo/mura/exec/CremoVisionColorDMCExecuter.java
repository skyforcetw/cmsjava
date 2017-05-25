package auo.mura.exec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import auo.cms.ed.EDPaternGen;
import auo.mura.CorrectionData;
import auo.mura.MuraCompensationProducer;
import jxl.read.biff.BiffException;

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
public class CremoVisionColorDMCExecuter {
    public CremoVisionColorDMCExecuter() {
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        if (args.length != 4) {
            System.out.println(
                    "Usage: demura [parameter & table filename] [-graylevel | image filename] [ideal | hw | hw2] [output filename]");
            return;
        }
        String tablefilename = args[0];
        String imagefilename = args[1];
        String ed = args[2];
        String outputfilename = args[3];

        MuraCompensationProducer.DitheringType edtype = null;
        if (ed.equalsIgnoreCase("ideal")) {
            edtype = MuraCompensationProducer.DitheringType.FloydSteinbergIdeal;
        } else if (ed.equalsIgnoreCase("hw")) {
            edtype = MuraCompensationProducer.DitheringType.Hardware_2_;
        } else if (ed.equalsIgnoreCase("hw2")) {
            edtype = MuraCompensationProducer.DitheringType.HardwareThresholdModulation;
        } else {
            System.out.println("Unknow ED type: " + edtype);
            return;
        }

        System.out.println("parameter & table filename: \t" + tablefilename);
        int graylevel = -1;
        if (imagefilename.indexOf('-') != -1) {
            graylevel = Integer.parseInt(imagefilename.substring(1, imagefilename.length()));
            System.out.println("graylevel: \t\t\t" + graylevel);
        } else {
            System.out.println("image filename: \t\t" + imagefilename);
        }

        System.out.println("output filename: \t\t" + outputfilename);
        System.out.println("ED type: \t\t\t" + edtype.name());

        try {
            CorrectionData d = new CorrectionData(tablefilename, CorrectionData.Type.Floating10bit);
            d.produceParameterFromSelf();

            if ( -1 == graylevel) {
                String outputdirname = new File(outputfilename).getParent();
                MonoDMCSimulator.simulate(d, imagefilename, outputdirname,
                        MuraCompensationProducer.DitheringType.Hardware_2_, true, false, 1
                        );
            } else {
                short[][][] image10Bit = EDPaternGen.getUniformPattern((short) (graylevel * 4), 2160, 3840);
                execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                        );
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (BiffException ex) {
            ex.printStackTrace();
        }
        System.out.println("Usage time: " + (System.currentTimeMillis() - start));
    }

    public final static void execute(CorrectionData correctiondata,
                                     short[][][] image10Bit,
                                     String outputFilename,
                                     MuraCompensationProducer.
                                     DitheringType type, boolean DMC,
                                     boolean DG,
                                     boolean ED, int edbit) throws
            IOException {
        MuraCompensationProducer muracompensationproducer = new
                MuraCompensationProducer(correctiondata);
        muracompensationproducer.setDitheringType(type);
        muracompensationproducer.setDG(DG);
        muracompensationproducer.setStore16BitImage(false);
        muracompensationproducer.setDMC(DMC);
        muracompensationproducer.setED(ED);
        muracompensationproducer.setEDBit(edbit);

        int h = image10Bit[0].length;
        int w = image10Bit[0][0].length;
        muracompensationproducer.setImageResolution(w, h);
        String dmcDir = "./";
        muracompensationproducer.produceCompensationImage(image10Bit,
                dmcDir + "/16bit.tiff", outputFilename);
        System.out.println("Compensation Checksum: " +
                           muracompensationproducer.getCompensationDataCheckSum());
        System.out.println("Dithering Checksum: " +
                           muracompensationproducer.getDitheringCheckSum());
    }


}
