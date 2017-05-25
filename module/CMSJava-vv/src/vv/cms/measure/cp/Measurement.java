package vv.cms.measure.cp;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class Measurement {

    public abstract List<Patch> measure(List<RGB> rgbList);

    public final Patch measure(RGB rgb) {
        List<RGB> rgbList = new ArrayList<RGB>(1);
        rgbList.add(rgb);
        return measure(rgbList).get(0);
    }

    protected static class CPCodeInstance extends Measurement {

        /**
         * CPCodeInstance
         * @param cpm CPCodeMeasurement
         */
        public CPCodeInstance(CPCodeMeasurement cpm) {
            this.cpm = cpm;
        }

        private CPCodeMeasurement cpm;

        /**
         * measure
         *
         * @param rgbList List
         * @return List
         */
        public List<Patch> measure(List<RGB> rgbList) {
            return cpm.measure(rgbList);
        }

    }


    protected static class MeterMeasurementInstance extends Measurement {

        /**
         * MeterMeasurementInstance
         * @param mm MeterMeasurement
         */
        public MeterMeasurementInstance(MeterMeasurement mm) {
            this.mm = mm;
        }

        private MeterMeasurement mm;

        /**
         * measure
         *
         * @param rgbList List
         * @return List
         */
        public List<Patch> measure(List<RGB> rgbList) {
            return mm.measure(rgbList, null);
        }

    }


    public final static Measurement getInstance(CPCodeMeasurement cpm) {
        return null;
    }

    public final static Measurement getInstance(MeterMeasurement mm) {
        return null;
    }
}
