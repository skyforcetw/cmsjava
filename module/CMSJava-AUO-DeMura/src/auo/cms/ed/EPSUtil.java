package auo.cms.ed;

//import shu.cms.colorformat.ascii.ASCIIFileFormatParser;
import java.awt.image.BufferedImage;
import java.io.IOException;

import shu.image.ImageUtils;
import shu.image.IntegerImage;
//import shu.cms.colorformat.ascii.ASCIIFileFormat;
import shu.io.ascii.ASCIIFileFormat;
import shu.io.ascii.ASCIIFileFormatParser;

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
public class EPSUtil {

    public static BufferedImage epsToBufferedImage(String epsfilename) throws
            IOException {
        ASCIIFileFormatParser parser = new ASCIIFileFormatParser(epsfilename);
        ASCIIFileFormat format = parser.parse();
//        int size = format.size();
        ASCIIFileFormat.LineObject line4 = format.getLine(4);
        int size = Integer.parseInt(line4.stringArray[2]);
        ASCIIFileFormat.LineObject line1 = format.getLine(1);
        int height = Integer.parseInt(line1.stringArray[3]);
        int width = Integer.parseInt(line1.stringArray[3]);

//        int[][] dots = new int[size][2];
        int scale = 1;
        IntegerImage image = new IntegerImage(width * scale, height * scale);
        int[] black = new int[] {255, 255, 255};

        for (int x = 0; x < size; x++) {
            ASCIIFileFormat.LineObject line = format.getLine(x + 12);
            double dotx = Double.parseDouble(line.stringArray[0]);
            double doty = Double.parseDouble(line.stringArray[1]);
            int idotx = (int) Math.round(dotx);
            int idoty = (int) Math.round(doty);
            image.setPixel(idotx % height, idoty % width, black);
            for (int h = 0; h < scale; h++) {
                for (int w = 0; w < scale; w++) {
                    image.setPixel(idotx % height + height * h,
                                   idoty % width + width * w, black);
                }
            }
        }
        BufferedImage bimage = image.getBufferedImage();
        return bimage;
    }

    public static void epsToTIF(String epsfilename, String tiffilename) throws
            IOException {
        BufferedImage bimage = epsToBufferedImage(epsfilename);
        ImageUtils.storeTIFFImage(tiffilename, bimage);

    }

    public static void main(String[] args) throws IOException {
        String dir =
                "D:\\My Documents\\my doc\\Error Diffusion\\ccvt-src-0.2\\";
//          String dir = "D:\\WorkSpace\\ccvt-src-0.2\\";
        for (int err = 3; err <= 3; err++) {

            epsToTIF(dir + "err" + err + ".eps", dir + "err" + err + ".tif");
        }
    }
}
