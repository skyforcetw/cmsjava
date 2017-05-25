package shu.cms.hvs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shu.cms.DeltaE;
import shu.cms.DeltaEReport;
//import shu.cms.applet.gradient.*;
import shu.cms.colorspace.depend.RGBBase;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.CAMConst;
import shu.cms.image.*;
import shu.cms.profile.ProfileColorSpace;
import shu.math.Maths;
import shu.math.Matlab;
import shu.math.array.DoubleArray;
import shu.image.*;
//import shu.plot.*;

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
public class SCIELAB {

    private ProfileColorSpace pcs;
    private CAMConst.CATType catType;
    private double sampPerDeg = 23;

    public SCIELAB(ProfileColorSpace pcs,
                   CAMConst.CATType catType, double sampPerDeg
            ) {
        this.pcs = pcs;
        this.catType = catType;
        this.sampPerDeg = sampPerDeg;
    }

    /**
     *
     * @param pcs ProfileColorSpace
     * @param catType CATType
     */
    public SCIELAB(ProfileColorSpace pcs,
                   CAMConst.CATType catType
            ) {
        this(pcs, catType, 23);
    }

    /**
     *
     * @param halfWidth double
     * @param width int
     * @return double[]
     */
    private static double[] gauss(double halfWidth, int width) {
        double alpha = 2 * Math.sqrt(Math.log(2)) / (halfWidth - 1);
        double[] array = new double[width];

        double c = Math.round(width / 2.);
        for (int x = 0; x < width; x++) {
            array[x] = x + 1 - c;
        }

        double[] gauss = Matlab.dotTimes(array, array);
        array = null;

        c = -alpha * alpha;
        for (int x = 0; x < width; x++) {
            gauss[x] = Math.exp(c * gauss[x]);
        }

        double sum = Maths.sum(gauss);
        for (int x = 0; x < width; x++) {
            gauss[x] /= sum;
        }
        return gauss;
    }

    /**
     * 產生gaussian filter
     * @param sampPerDeg double
     * @param dimension int
     * @return double[][][]
     */
    private static double[][][] separableFilters(double sampPerDeg,
                                                 int dimension) {

        if (dimension != 3) {
            throw new IllegalArgumentException("dimension must equal to 3");
        }

        double minSAMPPERDEG = 224;

        // if sampPerDeg is smaller than minSAMPPERDEG, need to upsample image data before
        // doing the filtering. This can be done equivalently by convolving
        // the filters with the upsampling matrix, then downsample it.
        if ((sampPerDeg < minSAMPPERDEG) & dimension == 2) {
            throw new IllegalArgumentException(
                    "sampPerDeg should be greater than or equal to" +
                    minSAMPPERDEG);
        }

        int uprate = 0;
        if ((sampPerDeg < minSAMPPERDEG) & dimension != 2) {
            uprate = (int) Matlab.ceil(minSAMPPERDEG / sampPerDeg);
            sampPerDeg = sampPerDeg * uprate;
        } else {
            uprate = 1;
        }

        // These are the parameters for generating the filters,
        // expressed as weighted sum of two or three gaussians,
        // in the format [halfwidth weight halfwidth weight ...].
        // The halfwidths are in degrees of visual angle.
        //x1 = [0.05      0.9207    0.225    0.105    7.0   -0.1080];
        //x2 = [0.0685    0.5310    0.826    0.33];
        //x3 = [0.0920    0.4877    0.6451    0.3711];
        // these are the same filter parameters, except that the weights
        // are normalized to sum to 1 -- this eliminates the need to
        // normalize after the filters are generated
        double[] x1 = new double[] {
                      0.05, 1.00327, 0.225, 0.114416, 7.0, -0.117686};
        double[] x2 = new double[] {
                      0.0685, 0.616725, 0.826, 0.383275};
        double[] x3 = new double[] {
                      0.0920, 0.567885, 0.6451, 0.432115};

        //Convert the unit of halfwidths from visual angle to pixels.
        x1[0] *= sampPerDeg;
        x1[2] *= sampPerDeg;
        x1[4] *= sampPerDeg;

        x2[0] *= sampPerDeg;
        x2[2] *= sampPerDeg;
        x3[0] *= sampPerDeg;
        x3[2] *= sampPerDeg;

        double[][] k1 = null, k2 = null, k3 = null;

        // Limit the width of filters to 1 degree visual angle, and
        // odd number of sampling points (so that the gaussians generated
        // from Rick's gauss routine are symmetric).
        int width = (int) Matlab.ceil(sampPerDeg / 2) * 2 - 1;

        // Generate the filters
        if (dimension < 3) {
            //不存在
        } else {
            // In this case, we do not compute the filter. We compute the
            // individual Gaussians rather than the sums of Gaussians.  These Gaussians
            // are used in the row and col separable convolutions.

            // k1 contains the three 1-d kernels that are used by the light/dark
            // system

            double[] k11 = gauss(x1[0], width);
            k11 = DoubleArray.times(k11,
                                    Math.sqrt(Math.abs(x1[1])) *
                                    Math.signum(x1[1]));

            double[] k13 = gauss(x1[2], width);
            k13 = DoubleArray.times(k13,
                                    Math.sqrt(Math.abs(x1[3])) *
                                    Math.signum(x1[3]));
            double[] k15 = gauss(x1[4], width);
            k15 = DoubleArray.times(k15,
                                    Math.sqrt(Math.abs(x1[5])) *
                                    Math.signum(x1[5]));
            k1 = new double[][] {
                 k11, k13, k15};

            // These are the two 1-d kernels used by red/green
            double[] k21 = gauss(x2[0], width);
            k21 = DoubleArray.times(k21,
                                    Math.sqrt(Math.abs(x2[1])) *
                                    Math.signum(x2[1]));
            double[] k23 = gauss(x2[2], width);
            k23 = DoubleArray.times(k23,
                                    Math.sqrt(Math.abs(x2[3])) *
                                    Math.signum(x2[3]));
            k2 = new double[][] {
                 k21, k23};

            // These are the two 1-d kernels used by blue/yellow
            double[] k31 = gauss(x3[0], width);
            k31 = DoubleArray.times(k31,
                                    Math.sqrt(Math.abs(x3[1])) *
                                    Math.signum(x3[1]));
            double[] k33 = gauss(x3[2], width);
            k33 = DoubleArray.times(k33,
                                    Math.sqrt(Math.abs(x3[3])) *
                                    Math.signum(x3[3]));
            k3 = new double[][] {
                 k31, k33};
        }
        // upsample and downsample
        // More explanation
        if ((dimension != 2) & uprate > 1) {
            double[] upcol = Matlab.concatArray(Matlab.makeArrayByRange(1, 1,
                    uprate),
                                                Matlab.makeArrayByRange(uprate -
                    1,
                    -1, 1));
            upcol = DoubleArray.times(upcol, 1. / uprate);
            int s = upcol.length;
            upcol = DoubleArray.to1DDoubleArray(resize(upcol, new int[] {1,
                    s + width - 1}));
            double[][] up1 = Matlab.conv2(k1, new double[][] {upcol},
                                          Matlab.Conv2Type.Same);
            double[][] up2 = Matlab.conv2(k2, new double[][] {upcol},
                                          Matlab.Conv2Type.Same);
            double[][] up3 = Matlab.conv2(k3, new double[][] {upcol},
                                          Matlab.Conv2Type.Same);
            s = up1[0].length;
            int mid = (int) Matlab.ceil(s / 2.);
            double[] downs = Matlab.concatArray(Matlab.flip(Matlab.
                    makeArrayByRange(
                            mid, -uprate, 1)),
                                                Matlab.makeArrayByRange(mid +
                    uprate,
                    uprate, up1[0].length));
            downs = DoubleArray.minus(downs, 1);
            int[] idowns = DoubleArray.toIntArray(downs);
            k1 = Matlab.makeArrayByColumnIndex(up1, idowns);
            k2 = Matlab.makeArrayByColumnIndex(up2, idowns);
            k3 = Matlab.makeArrayByColumnIndex(up3, idowns);
            up1 = up2 = up3 = null;
        }
        return new double[][][] {
                k1, k2, k3};
    }

    private static double[][] resize(double[] orig, int[] newSize) {
        return resize(new double[][] {orig}, newSize, new double[] {0, 0}, 0);
    }

    private static double[][] resize(double[][] orig, int[] newSize) {
        return resize(orig, newSize, new double[] {0, 0}, 0);
    }

    private static double[][] resize(double[][] orig, int[] newSize,
                                     double[] align, int padding) {

        if (newSize.length == 1) {
            newSize = Matlab.concatArray(newSize, newSize);
        } else {
            align = Matlab.concatArray(align, align);
        }

        int m1 = orig.length;
        int n1 = orig[0].length;
        int m2 = newSize[0];
        int n2 = newSize[1];
        double m = Math.min(m1, m2);
        double n = Math.min(n1, n2);

        double[][] result = Matlab.ones(m2, n2);
        result = DoubleArray.times(result, padding);

        double[] start1 = new double[] {
                          Math.floor((m1 - m) / 2 * (1 + align[0])),
                          Math.floor((n1 - n) / 2 * (1 + align[1]))
        };
        start1 = DoubleArray.plus(start1, 1);
        double[] start2 = new double[] {
                          Math.floor((m2 - m) / 2 * (1 + align[0])),
                          Math.floor((n2 - n) / 2 * (1 + align[1]))
        };
        start2 = DoubleArray.plus(start2, 1);

        Matlab.assign(result, (int) start2[0], (int) (start2[0] + m - 1),
                      (int) start2[1], (int) (start2[1] + n - 1), orig,
                      (int) start1[0], (int) (start1[0] + m - 1),
                      (int) start1[1],
                      (int) (start1[1] + n - 1));
        start1 = start2 = null;
        return result;
    }

    /**
     *
     * @param bufferedImage1 BufferedImage
     * @param bufferedImage2 BufferedImage
     * @param formula Formula
     * @return double[][] deltaE陣列
     */
    public double[][] lightscielab(BufferedImage bufferedImage1,
                                   BufferedImage bufferedImage2,
                                   DeltaE.Formula formula) {
        double[][][] k = initFilterK();
        return lightscielab(k, bufferedImage1, bufferedImage2, pcs,
                            catType, formula, PlaneImage.Domain.OPP);
    }

    private double[][][] filterK = null;

    /**
     * 直接計算所要的deltaE, 而不出DeltaEReport, 減少耗費的記憶體空間, 以便計算大圖檔.
     * @param k double[][][]
     * @param bufferedImage1 BufferedImage
     * @param bufferedImage2 BufferedImage
     * @param pcs ProfileColorSpace
     * @param catType CATType
     * @param formula Formula
     * @param domain Domain 運算所採用的Domain(建議OPP or IPT)
     * @return double[][] deltaE陣列
     */
    protected static double[][] lightscielab(double[][][] k,
                                             BufferedImage bufferedImage1,
                                             BufferedImage bufferedImage2,
                                             ProfileColorSpace pcs,
                                             CAMConst.CATType catType,
                                             DeltaE.Formula formula,
                                             PlaneImage.Domain domain
            ) {
        DeviceIndependentImage image1 = DeviceIndependentImage.getInstance(
                bufferedImage1, pcs, catType);
        DeviceIndependentImage image2 = DeviceIndependentImage.getInstance(
                bufferedImage2, pcs, catType);

        if (image1.getHeight() != image2.getHeight() ||
            image1.getWidth() != image2.getWidth()) {
            throw new IllegalArgumentException(
                    "size of image1 & image2 is not match");
        }

        //k是正確的
        double[][][] XYZImage1 = _scielab(image1, k, domain);
        double[][][] XYZImage2 = _scielab(image2, k, domain);
        int height = XYZImage1.length;
        int width = XYZImage1[0].length;

        double[][] deltaE = new double[height][width];
        CIEXYZ white = image1.getReferenceWhite();
        OPP oppWhite = new OPP(OPP.fromXYZValues(white.getValues()));
        CIEXYZ newWhite = oppWhite.toXYZ();
        double[] newWhiteValues = newWhite.getValues();
        image1 = image2 = null;
        System.gc();

        CIELab Lab1 = new CIELab();
        CIELab Lab2 = new CIELab();

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                //======================================================================
                // 為了減少記憶體的浪費, 所採用的冗長Lab計算方式
                //======================================================================
                double[] Lab1Values = CIELab.fromXYZValues(XYZImage1[x][y],
                        newWhiteValues);
                double[] Lab2Values = CIELab.fromXYZValues(XYZImage2[x][y],
                        newWhiteValues);
                Lab1.setValues(Lab1Values);
                Lab2.setValues(Lab2Values);
                //======================================================================

                deltaE[x][y] = DeltaE.getDeltaE(Lab1, Lab2, formula);
                Lab1Values = Lab2Values = null;
            }
        }
        XYZImage1 = XYZImage2 = null;
        return deltaE;
    }

    public DeltaEReport scielab(
            BufferedImage bufferedImage1, BufferedImage bufferedImage2,
            PlaneImage.Domain domain) {
        DeviceIndependentImage image1 = DeviceIndependentImage.getInstance(
                bufferedImage1, pcs, catType);
        DeviceIndependentImage image2 = DeviceIndependentImage.getInstance(
                bufferedImage2, pcs, catType);

        double[][][] k = initFilterK();
        return scielab(k, image1, image2, domain);
    }

    protected static DeltaEReport scielab(double[][][] k,
                                          DeviceIndependentImage image1,
                                          DeviceIndependentImage image2,
                                          PlaneImage.Domain domain
            ) {
        CIEXYZ white = image1.getReferenceWhite();
        if (!white.equals(image2.getReferenceWhite())) {
            throw new IllegalStateException(
                    "image1.referenceWhite != image2.referenceWhite");
        }

        if (image1.getHeight() != image2.getHeight() ||
            image1.getWidth() != image2.getWidth()) {
            throw new IllegalArgumentException(
                    "size of image1 & image2 is not match");
        }

        //將image以k filter處理過, 得到新的XYZ Image
        double[][][] XYZImage1 = _scielab(image1, k, domain);
        double[][][] XYZImage2 = _scielab(image2, k, domain);
        int height = XYZImage1.length;
        int width = XYZImage1[0].length;

        int size = height * width;
        List<CIELab> LabList1 = new ArrayList<CIELab>(size);
        List<CIELab> LabList2 = new ArrayList<CIELab>(size);
        int index = 0;
        OPP oppWhite = new OPP(OPP.fromXYZValues(white.getValues()));
        CIEXYZ newWhite = oppWhite.toXYZ();
        double[] newWhiteValues = newWhite.getValues();
        image1 = image2 = null;
        System.gc();

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double[] Lab1Values = CIELab.fromXYZValues(XYZImage1[x][y],
                        newWhiteValues);
                double[] Lab2Values = CIELab.fromXYZValues(XYZImage2[x][y],
                        newWhiteValues);
                CIELab Lab1 = new CIELab(Lab1Values, newWhite);
                CIELab Lab2 = new CIELab(Lab2Values, newWhite);
                LabList1.add(Lab1);
                LabList2.add(Lab2);
                index++;
            }
        }
        return DeltaEReport.Instance.CIELabReport(LabList1, LabList2);
    }

    private static boolean filtering = true;
    public final static void setFiltering(boolean filter) {
        filtering = filter;
    }

    private static double[][][] _scielab(DeviceIndependentImage image,
                                         double[][][] separableFilters,
                                         PlaneImage.Domain domain) {
        PlaneImage oppImage = PlaneImage.getInstance(image, domain);

        if (filtering) {
            double[][] k1 = separableFilters[0];
            double[][] k2 = separableFilters[1];
            double[][] k3 = separableFilters[2];
            double[][] w1 = oppImage.getPlaneImage(0);
            double[][] w2 = oppImage.getPlaneImage(1);
            double[][] w3 = oppImage.getPlaneImage(2);
            double[][] p1 = separableConv(w1, k1, Matlab.abs(k1));
            double[][] p2 = separableConv(w2, k2, Matlab.abs(k2));
            double[][] p3 = separableConv(w3, k3, Matlab.abs(k3));
            k1 = k2 = k3 = null;
            w1 = w2 = w3 = null;

            oppImage.setPlaneImage(0, p1);
            oppImage.setPlaneImage(1, p2);
            oppImage.setPlaneImage(2, p3);
        }

        double[][][] XYZImage = oppImage.getCIEXYZImage();
        oppImage = null;
        return XYZImage;
    }

    /**
     *
     * @param bufferedImage BufferedImage
     * @param domain Domain
     * @return double[][][] CIEXYZ色空間影像
     */
    public double[][][] scielab(BufferedImage bufferedImage,
                                PlaneImage.Domain domain) {
        DeviceIndependentImage image = DeviceIndependentImage.getInstance(
                bufferedImage, pcs, catType);
        double[][][] k = initFilterK();
        return _scielab(image, k, domain);
    }

    protected double[][][] initFilterK() {
        if (sampPerDeg > 0 && filterK == null && filtering) {
            filterK = separableFilters(sampPerDeg, 3);
        }
        return filterK;
    }

//  protected static double[][][] scielab(double sampPerDeg,
//                                        DeviceIndependentImage image,
//                                        PlaneImage.Domain domain) {
//    double[][][] k = separableFilters(sampPerDeg, 3);
//    return _scielab(image, k, domain);
//  }

//  public static BufferedImage scielab(double sampPerDeg,
//                                      BufferedImage image,
//                                      ProfileColorSpace pcs,
//                                      CAMConst.CATType catType) {
//    DeviceIndependentImage DIImage = DeviceIndependentImage.getInstance(
//        image, pcs, catType);
//    double[][][] XYZValuesImage = scielab(sampPerDeg, DIImage);
//    DIImage.setXYZValuesImage(XYZValuesImage);
//    return DIImage.getBufferedImage();
//  }

    public BufferedImage scielab(BufferedImage image) {

        DeviceIndependentImage DIImage = DeviceIndependentImage.getInstance(
                image, pcs, catType);
        double[][][] k = initFilterK();
        double[][][] XYZValuesImage = _scielab(DIImage, k,
                                               PlaneImage.Domain.OPP);
        DIImage.setXYZValuesImage(XYZValuesImage);
        return DIImage.getBufferedImage();
    }

    public DeviceIndependentImage scielab(DeviceIndependentImage DIImage) {
        double[][][] k = initFilterK();
        double[][][] XYZValuesImage = _scielab(DIImage, k,
                                               PlaneImage.Domain.OPP);
        DIImage.setXYZValuesImage(XYZValuesImage);
        return DIImage;
    }

    /**
     * ok
     * @param im double[][]
     * @param xkernels double[][]
     * @param ykernels double[][]
     * @return double[][]
     */
    private static double[][] separableConv(double[][] im, double[][] xkernels,
                                            double[][] ykernels) {
        int[] imsize = new int[] {
                       im.length, im[0].length};
        double[][] w1 = pad4conv(im, new int[] {xkernels[0].length}, 2);
        double[][] result = null;

        for (int j = 0; j < xkernels.length; j++) {
            double[][] p = Matlab.conv2(w1, new double[][] {xkernels[j]},
                                        Matlab.Conv2Type.Full);
            p = resize(p, imsize);

            double[][] w2 = pad4conv(p, new int[] {ykernels[0].length}, 1);
            p = Matlab.conv2(w2,
                             DoubleArray.transpose(new double[][] {ykernels[j]}),
                             Matlab.Conv2Type.Full);
            p = resize(p, imsize);

            if (result == null) {
                result = p;
            } else {
                result = DoubleArray.plus(result, p);
            }

        }
        w1 = null;
        return result;
    }

    private static double[][] pad4conv(double[][] im, int[] kernelsize, int dim) {

        if (kernelsize.length == 1) {
            kernelsize = Matlab.concatArray(kernelsize, kernelsize);
        }

        int m = im.length;
        int n = im[0].length;
        int h = 0;
        int w = 0;

        if (kernelsize[0] >= m) {
            h = (int) Math.floor(m / 2.);
        } else {
            h = (int) Math.floor(kernelsize[0] / 2.);
        }

        if (kernelsize[1] >= n) {
            w = (int) Math.floor(n / 2.);
        } else {
            w = (int) Math.floor(kernelsize[1] / 2.);
        }

        if (h != 0 && dim != 2) {
            im = Matlab.concatArrayAtNextRow(im,
                                             Matlab.flipud(Matlab.
                    getSubMatrixRangeCopy(im, m - h, m - 1, 0, n - 1)));
            im = Matlab.concatArrayAtNextRow(Matlab.flipud(Matlab.
                    getSubMatrixRangeCopy(im, 0, h - 1, 0, n - 1)), im);
        }

        if (w != 0 && dim != 1) {
            im = Matlab.concatArrayAtNextColumn(im,
                                                Matlab.fliplr(Matlab.
                    getSubMatrixRangeCopy(im, 0, m - 1, n - w, n - 1)));
            im = Matlab.concatArrayAtNextColumn(Matlab.fliplr(Matlab.
                    getSubMatrixRangeCopy(im, 0, m - 1, 0, w - 1)), im);
        }

        return im;
    }

    public static void example1(String[] args) throws IOException {
        //    BufferedImage hats = ImageUtils.loadImage(
//        "Reference Files/RGB Reference Images/Crosfield/Crosfield_ski.jpg");
//    BufferedImage hatsc = ImageUtils.loadImage(
//        "Reference Files/RGB Reference Images/Crosfield/Crosfield_ski.tif");

        BufferedImage hatsc = ImageUtils.loadImage(
                "Image/S-CIELAB/07_s4_frame1.bmp");
        BufferedImage hats = ImageUtils.loadImage(
                "Image/S-CIELAB/07.bmp");

        ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGBBase.
                ColorSpace.sRGB_gamma22);

        SCIELAB scielab = new SCIELAB(pcs, CAMConst.CATType.vonKries, 30);
//    DeltaE.setMeasuredInCIE2000DeltaE(false);
////    DeltaEReport deltaEReport = SCIELAB.scielab(23, hatsDI, hatscDI);
        long start = System.currentTimeMillis();
        double[][] deltaE = scielab.lightscielab(hats, hatsc,
                                                 DeltaE.Formula.CIE2000);
        System.out.println("mean: " + Maths.mean(deltaE));
        System.out.println("max: " + Maths.max(deltaE));
//    System.out.println( (System.currentTimeMillis() - start) / 1000.);
        System.out.println("over");

    }

//  public static void example2(String[] args) {
////    LCDTarget.setRGBNormalize(false);
//    LCDTarget target = LCDTarget.Instance.get("cpt_32inch",
//                                              LCDTarget.Source.K10,
//                                              LCDTargetBase.Number.Ramp1024,
//                                              LCDTarget.FileType.VastView,
//                                              null, "org");
//    LCDTarget.Operator.gradationReverseFix(target);
//
//    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(target,"");
//
//    SCIELAB scielab = new SCIELAB(pcs, CAMConst.CATType.vonKries, 45);
//    BufferedImage hats = GradientShowerFrame.getImage(new Dimension(1366, 12),
//        0, 64, false, false, true, false, false, false, 256);
////    scielab.filtering = false;
//    double[][][] XYZValuesImage = scielab.scielab(hats,
//                                                  PlaneImage.Domain.OPP);
//
//    int h = XYZValuesImage.length;
//    double[][] row = XYZValuesImage[h / 2];
//    row = DoubleArray.transpose(row);
//    double[] Y = row[1];
//    int size = Y.length;
//    double[] gsdf = new double[size];
//
//    for (int x = 0; x < size; x++) {
//      gsdf[x] = GSDF.DICOM.getJNDIndex(Y[x]);
//    }
//
//    Plot2D plot2 = Plot2D.getInstance("JND");
//    plot2.setVisible(true);
//    plot2.addLegend();
//    plot2.addLinePlot("gsdf", 0, 1365, gsdf);
////    plot2.addLinePlot("Y", 0, 1279, Y);
//
//    double[] signal = Maths.firstOrderDerivatives(gsdf);
//    double[] signal2 = Maths.firstOrderDerivatives(signal);
//
//    Plot2D plot3 = Plot2D.getInstance("gsdf'");
//    plot3.setVisible(true);
//    plot3.addLinePlot("gsdf'", 0, signal.length - 1, signal);
//
//    Plot2D plot4 = Plot2D.getInstance("gsdf''");
//    plot4.setVisible(true);
//    plot4.addLinePlot("gsdf''", 0, signal2.length - 1, signal2);
//
////    Plot2D plot1 = Plot2D.getInstance("upsignal'");
////    plot1.addLegend();
////    plot1.setVisible(true);
////    plot1.addLinePlot("upsignal", .5, upsignal.length - .5, upsignal);
////    final double[] upsignalp = Maths.firstOrderDerivatives(upsignal);
////    plot1.addLinePlot("upsignal'", 1, upsignalp.length, upsignalp);
//
//  }

    public static void main(String[] args) throws IOException {
        example1(args);
//    example2(args);
    }

}
