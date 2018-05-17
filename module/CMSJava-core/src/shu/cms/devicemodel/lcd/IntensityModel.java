package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.lcd.*;
import shu.cms.measure.intensity.ComponentLUT;
import shu.cms.measure.intensity.Component;
import java.util.List;
import shu.cms.colorformat.adapter.xls.AUOCPTableXLSAdapter;
import shu.cms.colorformat.adapter.xls.AUOPropertyExtractor;
import shu.cms.measure.intensity.ComponentFetcher;
import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;
import jxl.read.biff.*;
import java.io.*;
import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import shu.cms.Patch;
import java.util.Collections;

/**
 * <p>Title: CMSJava-core</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class IntensityModel extends LCDModel {
    private ComponentLUT componentLUT;
    private CIEXYZ rMaxXYZ;
    private CIEXYZ gMaxXYZ;
    private CIEXYZ bMaxXYZ;
    private MaxMatrixIntensityAnalyzer analyzer;

    private void init(AUOCPTableXLSAdapter adapter) {
        AUOPropertyExtractor property = new AUOPropertyExtractor(adapter);
        List<CIEXYZ> XYZList = adapter.getXYZList();
        int size = XYZList.size();
        CIEXYZ whiteXYZ = XYZList.get(0);
        CIEXYZ blackXYZ = XYZList.get(size - 2);
        rMaxXYZ = property.getNativePrimaryColor(RGB.Channel.R).toXYZ();
        gMaxXYZ = property.getNativePrimaryColor(RGB.Channel.G).toXYZ();
        bMaxXYZ = property.getNativePrimaryColor(RGB.Channel.B).toXYZ();
//        double Z = bMaxXYZ.Z;
//
//        rMaxXYZ.Z += 5;
//        gMaxXYZ.Z += 5;
//        bMaxXYZ.Z = Z -10;
//        CIEXYZ temp = CIEXYZ.plus(CIEXYZ.plus(rMaxXYZ, gMaxXYZ), bMaxXYZ);
//        bMaxXYZ = new CIEXYZ(127*2, 34.5, 715 / 5);

        super.init(blackXYZ, rMaxXYZ, gMaxXYZ, bMaxXYZ, null);

        analyzer = new MaxMatrixIntensityAnalyzer();
        analyzer.setupComponent(RGB.Channel.R, rMaxXYZ);
        analyzer.setupComponent(RGB.Channel.G, gMaxXYZ);
        analyzer.setupComponent(RGB.Channel.B, bMaxXYZ);
        analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
        analyzer.enter();
        ComponentFetcher fetcher = new ComponentFetcher(analyzer);
        List<Component> componentList = fetcher.fetchComponent(XYZList);
        componentLUT = new ComponentLUT(componentList);

        this.luminance = whiteXYZ;
        this.targetWhitePoint = whiteXYZ;
        this.setMaxValue(RGB.MaxValue.Double255);
    }

    public IntensityModel(AUOCPTableXLSAdapter adapter) {
        init(adapter);
    }

    public IntensityModel(AUORampXLSAdapter adapter) {
        init(adapter);
    }

    private void init(AUORampXLSAdapter adapter) {
//      AUORampXLSAdapter adapter = new AUORampXLSAdapter("hook/Measurement00.xls");
        LCDTarget lcdtarget = LCDTarget.Instance.get(adapter);

//    AUOPropertyExtractor property = new AUOPropertyExtractor(adapter);
        List<Patch> patchList = lcdtarget.filter.grayPatch(true);
        List<CIEXYZ> XYZList = Patch.Filter.XYZList(patchList);
//    List<CIEXYZ> XYZList = adapter.getXYZList();
        int size = XYZList.size();
        CIEXYZ blackXYZ = XYZList.get(0);
        CIEXYZ whiteXYZ = XYZList.get(size - 1);
        Collections.reverse(XYZList);
        rMaxXYZ = lcdtarget.getPatch(255, 0, 0).getXYZ();
        gMaxXYZ = lcdtarget.getPatch(0, 255, 0).getXYZ();
        bMaxXYZ = lcdtarget.getPatch(0, 0, 255).getXYZ();
//        rMaxXYZ = property.getNativePrimaryColor(RGB.Channel.R).toXYZ();
//        gMaxXYZ = property.getNativePrimaryColor(RGB.Channel.G).toXYZ();
//        bMaxXYZ = property.getNativePrimaryColor(RGB.Channel.B).toXYZ();
//        double Z = bMaxXYZ.Z;
//
//        rMaxXYZ.Z += 5;
//        gMaxXYZ.Z += 5;
//        bMaxXYZ.Z = Z -10;
//        CIEXYZ temp = CIEXYZ.plus(CIEXYZ.plus(rMaxXYZ, gMaxXYZ), bMaxXYZ);
//        bMaxXYZ = new CIEXYZ(127*2, 34.5, 715 / 5);

        super.init(blackXYZ, rMaxXYZ, gMaxXYZ, bMaxXYZ, null);

        analyzer = new MaxMatrixIntensityAnalyzer();
        analyzer.setupComponent(RGB.Channel.R, rMaxXYZ);
        analyzer.setupComponent(RGB.Channel.G, gMaxXYZ);
        analyzer.setupComponent(RGB.Channel.B, bMaxXYZ);
        analyzer.setupComponent(RGB.Channel.W, whiteXYZ);
        analyzer.enter();
        ComponentFetcher fetcher = new ComponentFetcher(analyzer);
        List<Component> componentList = fetcher.fetchComponent(XYZList);
        componentLUT = new ComponentLUT(componentList);

        this.luminance = whiteXYZ;
        this.targetWhitePoint = whiteXYZ;
        this.setMaxValue(RGB.MaxValue.Double255);
    }

    /**
     * 計算RGB,反推模式
     *
     * @param relativeXYZ CIEXYZ
     * @param factor Factor[]
     * @return RGB
     */
    protected RGB _getRGB(CIEXYZ relativeXYZ, Factor[] factor) {
        CIEXYZ absoluteXYZ = CIEXYZ.plus(relativeXYZ, this.flare.flareXYZ);
        RGB intensity = analyzer.getIntensity(absoluteXYZ);
        double r = componentLUT.getCode(RGB.Channel.R, intensity.R);
        double g = componentLUT.getCode(RGB.Channel.G, intensity.G);
        double b = componentLUT.getCode(RGB.Channel.B, intensity.B);
        return new RGB(r, g, b);
    }

    /**
     * 計算XYZ,前導模式
     *
     * @param rgb RGB
     * @param factor Factor[]
     * @return CIEXYZ
     */
    protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
        double rIntensity = componentLUT.getIntensity(RGB.Channel.R,
                rgb.getValue(RGB.Channel.R,
                             RGB.MaxValue.Double255));
        double gIntensity = componentLUT.getIntensity(RGB.Channel.G,
                rgb.getValue(RGB.Channel.G,
                             RGB.MaxValue.Double255));
        double bIntensity = componentLUT.getIntensity(RGB.Channel.B,
                rgb.getValue(RGB.Channel.B,
                             RGB.MaxValue.Double255));

        double[] targetRatio = this.analyzer.getTargetRatio();
        rIntensity = rIntensity * targetRatio[0];
        gIntensity = gIntensity * targetRatio[1];
        bIntensity = bIntensity * targetRatio[2];
        CIEXYZ rXYZ = (CIEXYZ) rMaxXYZ.clone();
        rXYZ.times(rIntensity / 100.);
        CIEXYZ gXYZ = (CIEXYZ) gMaxXYZ.clone();
        gXYZ.times(gIntensity / 100.);
        CIEXYZ bXYZ = (CIEXYZ) bMaxXYZ.clone();
        bXYZ.times(bIntensity / 100.);
        CIEXYZ XYZ = CIEXYZ.plus(CIEXYZ.plus(rXYZ, gXYZ), bXYZ);
        return CIEXYZ.minus(XYZ, this.flare.flareXYZ);
    }

    /**
     * 求係數
     *
     * @return Factor[]
     */
    protected Factor[] _produceFactor() {
        return new Factor[3];
    }

    /**
     * getDescription
     *
     * @return String
     */
    public String getDescription() {
        return "Intensity Model";
    }

    public static void main(String[] args) {
        try {
            AUOCPTableXLSAdapter adapter = new AUOCPTableXLSAdapter("debug.xls");
            AUOPropertyExtractor property = new AUOPropertyExtractor(adapter);
            List<CIEXYZ> XYZList = adapter.getXYZList();
            CIEXYZ whiteXYZ = XYZList.get(0);

            IntensityModel model = new IntensityModel(adapter);
            model.produceFactor();
            System.out.println(whiteXYZ);
            System.out.println(model.getRGB(whiteXYZ, false));
            System.out.println(model.getXYZ(new RGB(255, 255, 255), false));
            System.out.println(model.getXYZ(new RGB(255, 255, 241.0431521537135), false));

            ComponentLUT lut = model.componentLUT;
            for (int x = 0; x < 256; x++) {
                double rintensity = lut.getIntensity(RGB.Channel.R, x);
                double gintensity = lut.getIntensity(RGB.Channel.G, x);
                double bintensity = lut.getIntensity(RGB.Channel.B, x);

                System.out.println(rintensity + " " + gintensity + " " +
                                   bintensity);
            }

        } catch (BiffException ex) {
        } catch (IOException ex) {
        }
//        IntensityModel intensitymodel = new IntensityModel();
    }
}
