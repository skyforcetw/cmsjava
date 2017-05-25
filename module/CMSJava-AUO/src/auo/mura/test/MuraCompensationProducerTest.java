package auo.mura.test;

import auo.mura.CorrectionData;
import java.io.IOException;
import shu.image.ImageUtils;
import jxl.read.biff.BiffException;
import auo.mura.MuraCompensationProducer;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import shu.image.IntegerImage;

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
public class MuraCompensationProducerTest {
  public static void example(String[] args) throws BiffException, IOException {
    String filename =
        "24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv";
    CorrectionData correctiondata = new CorrectionData(filename);

    MuraCompensationProducer muracompensationproducer = new
        MuraCompensationProducer(correctiondata);
    muracompensationproducer.setBlockInterpolation(true);
    short[][] result = muracompensationproducer.getCompensationData(25);
    System.out.println(muracompensationproducer.getCompensationDataCheckSum());

//    muracompensationproducer.setOrderDithering(true);
//    muracompensationproducer.setOrderDithering(false);
    result = muracompensationproducer.getDitheringCompensationData(result);
//    BufferedImage image = muracompensationproducer.getMuraImageUtils().
//        getBufferedImage(result);
//    ImageUtils.storeTIFFImage("demura/demura basic dithering(old).tif", image);

  }

  static boolean compare(short[][] a, short[][] b) {
    int h = a.length;
    int w = a[0].length;
    boolean equal = true;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        equal = equal & (a[y][x] == b[y][x]);
      }
    }
    return equal;
  }

  public static void compare(String[] args) throws IOException, BiffException {
    String filename =
        "24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv";
    CorrectionData correctiondata = new CorrectionData(filename);

    MuraCompensationProducer muracompensationproducer = new
        MuraCompensationProducer(correctiondata);
    muracompensationproducer.setBlockInterpolation(false);
    short[][] compensation = muracompensationproducer.getCompensationData(25);
    short[][] compensation0 = muracompensationproducer.getCompensationData(25);
    boolean a = compare(compensation, compensation0);
//   System.out.println(muracompensationproducer.getCompensationDataCheckSum());

//    muracompensationproducer.setOrderDithering(true);
//    muracompensationproducer.setDitheringType(MuraCompensationProducer.
//                                              DitheringType.Order);
    compensation = muracompensationproducer.getDitheringCompensationData(
        compensation);
//    BufferedImage img0 = muracompensationproducer.getMuraImageUtils().
//        getBufferedImage(compensation);
    a = compare(compensation, compensation0);
//    muracompensationproducer.setOrderDithering(false);
    muracompensationproducer.setDitheringType(MuraCompensationProducer.
                                              DitheringType.FloydSteinberg);

    compensation0 = muracompensationproducer.getDitheringCompensationData(
        compensation0);
//    BufferedImage img1 = muracompensationproducer.getMuraImageUtils().
//        getBufferedImage(
//            compensation0);
    a = compare(compensation, compensation0);

//    int w = img0.getWidth();
//    int h = img0.getHeight();
    int[] pixel0 = new int[3];
    int[] pixel1 = new int[3];
    boolean equal = true;

//    IntegerImage intimage = new IntegerImage(w, h);

//    for (int y = 0; y < h; y++) {
//      for (int x = 0; x < w; x++) {
//        img0.getRaster().getPixel(x, y, pixel0);
//        img1.getRaster().getPixel(x, y, pixel1);
//        boolean eq = Arrays.equals(pixel0, pixel1);
//        equal = eq && equal;
//        if (!eq) {
//          intimage.setPixel(x, y, new int[] {128, 128, 128});
//        }
//        if( pixel0[0]!=19 && pixel0[0]!=20) {
//          int a=1;
//        }
//      }
//    }
//    System.out.println(equal);
//    ImageUtils.storeTIFFImage("demura/diff3.tif", intimage.getBufferedImage());
  }

}
