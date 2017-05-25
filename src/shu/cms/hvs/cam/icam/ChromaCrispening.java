package shu.cms.hvs.cam.icam;

import java.io.*;
import java.util.*;
import javax.imageio.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ChromaCrispening
    extends iCAMFramework {

  public ChromaCrispening(String filename, ProfileColorSpace pcs) throws
      IOException {
    super(filename, pcs);
  }

  public DeviceIndependentImage chromaCrispening() throws IOException {
    DeviceIndependentImage whiteImage = determineImageWhite(DIImage, 15);
    DeviceIndependentImage adaptImage = chromaticAdaptation(whiteImage);
    DeviceIndependentImage IPT = convert2IPT(adaptImage);
    return IPT;
  }

  public static void main(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
//    try {
//      ChromaCrispening chromaCrispening = new ChromaCrispening(
//          "Image/iCAM/chroma-crisp.tiff",
//          pcs);
//
//      DeviceIndependentImage di = chromaCrispening.chromaCrispening();
//      BufferedImage bi = di.getBufferedImage();
//      ImageIO.write(bi, "tif", new File("ChromaCrispening.tif"));
//
//      BufferedImage I = getIBufferedImage(di);
//      ImageIO.write(I, "tif", new File("ChromaCrispening-I.tif"));
//      BufferedImage chroma = getChromaBufferedImage(di);
//      ImageIO.write(chroma, "tif", new File("ChromaCrispening-C.tif"));
//
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }

    try {
      ChromaCrispening chromaCrispening = new ChromaCrispening(
          "portrait.jpg", pcs);
      DeviceIndependentImage di = chromaCrispening.chromaticAdaptationImage();
      BufferedImage bi = di.getBufferedImage();
      ImageIO.write(bi, "tif", new File("cc-ca.tif"));
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  protected DeviceIndependentImage convert2IPT(DeviceIndependentImage
                                               adaptImage) {
    DeviceIndependentImage lmsImage = change2HuntCATType(adaptImage);
    PlaneImage planeLMSImage = PlaneImage.getInstance(lmsImage,
        PlaneImage.Domain.LMS);

    //Determine the exponents , based on the luminance of the input image
    double[][] iptKern = kernel(7);
    PlaneImage planeDIImage = PlaneImage.getInstance(DIImage,
        PlaneImage.Domain.XYZ);
    double[][] yIm = planeDIImage.getPlaneImage(1);
    double[][] yLow = Matlab.conv2(yIm, iptKern, Matlab.Conv2Type.Same);

    double expScale = 1.0 / 2.0;
    double[][] iptExp = iptExp(yIm, expScale, yLow);
//    minThresh(iptExp);
    lmsImNL(planeLMSImage, iptExp);

    DeviceIndependentImage IPTImage = DeviceIndependentImage.
        cloneDeviceIndependentImage(DIImage);
    planeLMSImage.restoreToDeviceIndependentImage(IPTImage);
    return IPTImage;
  }

  protected double[][] kernel(int kernelSize) {
    double[][] kern = table(kernelSize);
    double divisor = Math.pow(kernelSize, 2);
    kern = DoubleArray.times(kern, 1 / divisor);
    return kern;
  }

  protected static double[][] table(int kernelSize) {
    double[][] table = new double[kernelSize][kernelSize];
    int height = table.length;
    for (int x = 0; x < height; x++) {
      Arrays.fill(table[x], 1);
    }
    return table;
  }

}
