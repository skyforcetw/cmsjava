package auo.mura.exec;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import auo.mura.CorrectionData;
import auo.mura.MuraCompensationProducer;
import auo.mura.img.MuraImageUtils;
import auo.mura.img.PatternGen;
import jxl.read.biff.BiffException;
import shu.image.GradientImage;
import shu.image.ImageUtils;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * XXXXSimulator�MXXXXExecuter���t�O
 * XXXXSimulator�w�藍�P�Ѽ�, ���H"��{���X"���覡�F��, �ҥH�u�ʤj, �������ϥ�, �n�F�ѵ{���X.
 * XXXXExecuter���ѤFcommand line������, �ҥH�i�H�bcmd�U�����N�ѼƶǤJ, ���O�i�H�ǤJ���ѼƤw�g�q��, �]���u�ʤp.
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ColorDMCSimulator {
    /**
     * ColorDMCSimulator���ѤT�ذ����k
     * 1.����256�i���@�Ƕ�pattern, �ó�����dmc����
     * 2.���w�~��������, �åB�i��dmc����
     * 3.���ͤH�upattern, �p256�Ƕ�pattern��, �öi��dmc����
     *
     * @param args String[]
     * @throws BiffException
     * @throws IOException
     */
    public static void main(String[] args) throws BiffException, IOException {

        //==============================================================================================================
        // ed type�]�w & image filename
        //==============================================================================================================
        MuraCompensationProducer.DitheringType edtype = MuraCompensationProducer.DitheringType.Hardware_2_;
        //==============================================================================================================


        //==============================================================================================================
        // ��X���覡���, ��ĳ�@���u����@�اY�i
        //==============================================================================================================
        boolean produce256ImagePattern = false;
        boolean fromImagePattern = false;
        boolean fromRegularPattern = true;
        //==============================================================================================================

        if (produce256ImagePattern) {
            //==============================================================================================================
            // dir, dmc table�Ҧb�ؿ�
            //==============================================================================================================
            String inputdir = "DMC Example/color/256image/";
            String outputdir = inputdir;
            // table, dmc table�ɦW
            String tablefilename = inputdir + "/DefaultDG_10,25,34,112,255.csv";
            // correction data, �Ndmc table�নCorrectionData
            CorrectionData d = new CorrectionData(tablefilename, CorrectionData.Type.Floating10bit);
            d.produceParameterFromSelf();
            //==============================================================================================================

            simulate256ImagePattern(edtype, d, 0, 255, 3840, 2560, outputdir);
        }

        if (fromImagePattern) {
            //==============================================================================================================
            // dir, dmc table�Ҧb�ؿ�
            //==============================================================================================================
            String inputdir = "DMC Example/color/from image/";
            // table, dmc table�ɦW
            String tablefilename = inputdir + "/DefaultDG_10,25,34,112,255.csv";
            // correction data, �Ndmc table�নCorrectionData
            CorrectionData d = new CorrectionData(tablefilename, CorrectionData.Type.Floating10bit);
            d.produceParameterFromSelf();
            //==============================================================================================================

            String imagefilename = inputdir + "/rect-hue.bmp";
            simulateImagePattern(edtype, d, inputdir, imagefilename);
        }

        if (fromRegularPattern) {
            //==============================================================================================================
            // dir, dmc table�Ҧb�ؿ�
            //==============================================================================================================
            String inputdir = "DMC Example/color/regular/";
            String outputdir = inputdir;
            // table, dmc table�ɦW
            String tablefilename = inputdir + "/DefaultDG_10,25,34,112,255.csv";
            // correction data, �Ndmc table�নCorrectionData
            CorrectionData d = new CorrectionData(tablefilename, CorrectionData.Type.Floating10bit);
            d.produceParameterFromSelf();
            //==============================================================================================================

            //Regular Pattern�� 1. �@�iRGB  2. �|�i���O�OR/G/B/W , 2�Y��fourColor
            boolean fourColor = true;
            if (fourColor) {
                simulate4kRGBWPattern(edtype, d, tablefilename, outputdir);
            } else {
                int width = 3840;
                int height = 2560;
                short[][][] image10Bit = getRGB256Pattern(width, height);

                boolean storeOriginalPattern = true;
                if (storeOriginalPattern) {
                    MuraImageUtils fullutils = new MuraImageUtils(10, width, height);
                    fullutils.store8BitImageBMP(image10Bit, outputdir + "rgb_grayscale.bmp");
                }

                String condition = tablefilename.substring(tablefilename.lastIndexOf("/") + 1,
                        tablefilename.indexOf(".csv"));
                String outputfilename = outputdir + condition + "_rgb_grayscale.bmp";
                simulateImagePattern(edtype, d, outputfilename, image10Bit);
            }
        }
    }

    /**
     * �P�ɨ㦳R/G/B�Ƕ���pattern
     * @param edtype DitheringType
     * @param d CorrectionData
     * @param tablefilename String
     * @param imgWidth int
     * @param imgHeight int
     * @param outputdir String
     * @throws IOException
     * @deprecated
     */
    static void simulateRGB256Pattern(MuraCompensationProducer.DitheringType edtype, CorrectionData d,
                                      String tablefilename, int imgWidth, int imgHeight, String outputdir) throws
            IOException {
        //==============================================================================================================
        // �ͦ�pattern
        //==============================================================================================================
        int height_13 = imgHeight / 3;
        Dimension dimension = new Dimension(imgWidth, imgHeight);
        BufferedImage r = GradientImage.getImage(dimension, 0, 255, true, false, false, false, false, 256, true, null);
        BufferedImage g = GradientImage.getImage(dimension, 0, 255, false, true, false, false, false, 256, true, null);
        BufferedImage b = GradientImage.getImage(dimension, 0, 255, false, false, true, false, false, 256, true, null);
        MuraImageUtils utils = new MuraImageUtils(12, imgWidth, height_13);
        short[][][] imager = utils.get10BitImageData(r, false);
        short[][][] imageg = utils.get10BitImageData(g, false);
        short[][][] imageb = utils.get10BitImageData(b, false);
        short[][][] image10Bit = new short[3][imgHeight][imgWidth];
        for (int ch = 0; ch < 3; ch++) {
            for (int h = 0; h < height_13; h++) {
                for (int w = 0; w < imgWidth; w++) {
                    image10Bit[ch][h][w] = imager[ch][h][w];
                    image10Bit[ch][h + height_13][w] = imageg[ch][h][w];
                    image10Bit[ch][h + height_13 * 2][w] = imageb[ch][h][w];
                }
            }
        }
        PatternGen.clip(image10Bit, (short) 1020);
        //==============================================================================================================

        MuraImageUtils fullutils = new MuraImageUtils(10, imgWidth, imgHeight);
        fullutils.store8BitImageBMP(image10Bit, outputdir + "rgb_grayscale.bmp");

        String condition = tablefilename.substring(tablefilename.lastIndexOf("/") + 1, tablefilename.indexOf(".csv"));
        String outputfilename = outputdir + condition + "_rgb_grayscale.bmp";

        simulateImagePattern(edtype, d, outputfilename, image10Bit);

    }

    static short[][][] getRGB256Pattern(int imgWidth, int imgHeight) throws IOException {
        //==============================================================================================================
        // �ͦ�pattern
        //==============================================================================================================
        int height_13 = imgHeight / 3;
        Dimension dimension = new Dimension(imgWidth, imgHeight);
        BufferedImage r = GradientImage.getImage(dimension, 0, 255, true, false, false, false, false, 256, true, null);
        BufferedImage g = GradientImage.getImage(dimension, 0, 255, false, true, false, false, false, 256, true, null);
        BufferedImage b = GradientImage.getImage(dimension, 0, 255, false, false, true, false, false, 256, true, null);
        MuraImageUtils utils = new MuraImageUtils(12, imgWidth, height_13);
        short[][][] imager = utils.get10BitImageData(r, false);
        short[][][] imageg = utils.get10BitImageData(g, false);
        short[][][] imageb = utils.get10BitImageData(b, false);
        short[][][] image10Bit = new short[3][imgHeight][imgWidth];
        for (int ch = 0; ch < 3; ch++) {
            for (int h = 0; h < height_13; h++) {
                for (int w = 0; w < imgWidth; w++) {
                    image10Bit[ch][h][w] = imager[ch][h][w];
                    image10Bit[ch][h + height_13][w] = imageg[ch][h][w];
                    image10Bit[ch][h + height_13 * 2][w] = imageb[ch][h][w];
                }
            }
        }
        PatternGen.clip(image10Bit, (short) 1020);
        //==============================================================================================================
        return image10Bit;
//        MuraImageUtils fullutils = new MuraImageUtils(10, imgWidth, imgHeight);
//        fullutils.store8BitImageBMP(image10Bit, outputfilename);

    }

    static void simulateImagePattern(MuraCompensationProducer.DitheringType edtype, CorrectionData d,
                                     String outputdir, String imagefilename) throws IOException {
        BufferedImage image = ImageUtils.loadImage(imagefilename);
        MuraImageUtils utils = new MuraImageUtils(12, image.getWidth(), image.getHeight());
        short[][][] image10Bit = utils.get10BitImageData(image, false);

        String imageonlyname = imagefilename.substring(imagefilename.lastIndexOf('/'), imagefilename.lastIndexOf(".bmp"));
        String outputfilename = outputdir + imageonlyname + "_dmc.bmp";
        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );

    }

    static void simulateImagePattern(MuraCompensationProducer.DitheringType edtype, CorrectionData d,
                                     String outputfilename, short[][][] image10Bit) throws IOException {

        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );

    }


    static void simulate256ImagePattern(MuraCompensationProducer.DitheringType edtype, CorrectionData d,
                                        int startGraylevel, int endGraylevel, int imgWidth, int imgHeight,
                                        String outputdir) throws
            IOException {
        for (int gl = startGraylevel; gl <= endGraylevel; gl++) {
            short[][][] image10Bit = PatternGen.getWholeFramePattern((short) (gl * 4), imgWidth, imgHeight);
            PatternGen.clip(image10Bit, (short) 1020);
            String outputfilename = outputdir + "/" + gl + ".bmp";
            CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4);

        }

    }


    /**
     * 256�Ƕ���R/G/B/W Pattern
     * @param edtype DitheringType
     * @param d CorrectionData
     * @param tablefilename String
     * @param outputdir String
     * @throws IOException
     */
    static void simulate4kRGBWPattern(MuraCompensationProducer.DitheringType edtype, CorrectionData d,
                                      String tablefilename, String outputdir) throws IOException {
        //W
        short[][][] image10Bit = PatternGen.get4K2KPattern_256L(true, true, true);

        PatternGen.clip(image10Bit, (short) 1020);
        String outputfilename = outputdir + tablefilename.substring(tablefilename.lastIndexOf('/') + 1,
                tablefilename.indexOf(".csv")) + ".bmp";
        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );

        //R
        image10Bit = PatternGen.get4K2KPattern_256L(true, false, false);
        PatternGen.clip(image10Bit, (short) 1020);
        outputfilename = outputdir + tablefilename.substring(tablefilename.lastIndexOf('/') + 1,
                tablefilename.indexOf(".csv")) + "r.bmp";
        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );
        //G
        image10Bit = PatternGen.get4K2KPattern_256L(false, true, false);
        PatternGen.clip(image10Bit, (short) 1020);
        outputfilename = outputdir + tablefilename.substring(tablefilename.lastIndexOf('/') + 1,
                tablefilename.indexOf(".csv")) + "g.bmp";
        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );
        //B
        image10Bit = PatternGen.get4K2KPattern_256L(false, false, true);
        PatternGen.clip(image10Bit, (short) 1020);
        outputfilename = outputdir + tablefilename.substring(tablefilename.lastIndexOf('/') + 1,
                tablefilename.indexOf(".csv")) + "b.bmp";
        CremoVisionColorDMCExecuter.execute(d, image10Bit, outputfilename, edtype, true, false, true, 4
                );

    }
}
