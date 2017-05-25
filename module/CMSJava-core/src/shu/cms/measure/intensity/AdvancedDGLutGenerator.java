package shu.cms.measure.intensity;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;

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
public class AdvancedDGLutGenerator {
    private List<Component> componentList;
    private ComponentFetcher fetcher;
    private List<CIEXYZ> targetXYZList;
    public AdvancedDGLutGenerator(List<Component> componentList,
            ComponentFetcher fetcher) {
        this.componentList = componentList;
        this.fetcher = fetcher;
    }

    public void setTargetXYZList(List<CIEXYZ> targetXYZList) {
        this.targetXYZList = targetXYZList;
    }

    /**
     * plot送進來的componentList基於target對應的intensity
     * @param plotIntensity boolean
     */
    public void setPlotIncomingIntensity(boolean plotIntensity) {
        this.plotIncomingIntensity = plotIntensity;
    }

    public void setPlotTargetIntensity(boolean plotIntensity) {
        this.plotTargetIntensity = plotIntensity;
    }

    public List<RGB> produce() {
        return produceDGLut(targetXYZList, componentList, fetcher.getAnalyzer());
    }

    private boolean plotIncomingIntensity = false;
    private boolean plotTargetIntensity = false;

    private List<RGB> produceDGLut(List<CIEXYZ> targetXYZList,
            List<Component> componentList, IntensityAnalyzerIF analyzer) {
        int size = targetXYZList.size();
        List<RGB> result = new ArrayList<RGB>(size);

        double rTargetIntensity = 100;
        double gTargetIntensity = 100;
        double bTargetIntensity = 100;

        Plot2D iplot = plotIncomingIntensity ?
                       Plot2D.getInstance("Incoming Intensity") : null;
        Plot2D tplot = plotTargetIntensity ?
                       Plot2D.getInstance("Target Intensity") : null;
        for (int x = 0; x < size; x++) {

            CIEXYZ targetXYZ = targetXYZList.get(x);
            MaxMatrixIntensityAnalyzer ma = MaxMatrixIntensityAnalyzer.
                                            getReadyAnalyzer(analyzer,
                    targetXYZ);

            List<Component>
                    newcomponentList = fetchNewComponent(ma, componentList);
            //==
            if (x != 0 && plotIncomingIntensity) {
                RGB newintensity = newcomponentList.get(size - 1 - x).intensity;
                iplot.addCacheScatterLinePlot("W", java.awt.Color.black, x,
                                              (newintensity.R + newintensity.G +
                                               newintensity.B) / 3.);

                iplot.addCacheScatterLinePlot(x, newintensity.getValues());

            }
//==


            DGLutGenerator lutgen = new DGLutGenerator(newcomponentList);
            RGB rgb = lutgen.getDGCode(rTargetIntensity, gTargetIntensity,
                                       bTargetIntensity);
            double[] targetIntensity = lutgen.getTargetIntensit();
            result.add(rgb);

            if (plotTargetIntensity) {
                tplot.addCacheScatterLinePlot(x, targetIntensity);
            }

        }
        if (plotIncomingIntensity) {
            iplot.setVisible();
            iplot.setFixedBounds(0, 0, 255);
            iplot.setFixedBounds(1, 98, 102);
        }
        if (plotTargetIntensity) {
            tplot.setVisible();
            tplot.setFixedBounds(0, 0, 255);
            tplot.setFixedBounds(1, 98, 102);
        }

        return result;
    }

    public static List<Component> fetchNewComponent(MaxMatrixIntensityAnalyzer
            analyzer,
            List<Component>
            componentVector) {
        int size = componentVector.size();
        List<Component> result = new ArrayList<Component>(size);
        for (Component c : componentVector) {
            RGB intensity = analyzer.getIntensity(c.XYZ);
            Component component = new Component(c.rgb, intensity, c.XYZ);
            result.add(component);
        }
        return result;
    }

}
