package shu.cms.measure.intensity;

import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.CIEXYZ;
import shu.cms.Patch;

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
public class Component {
    public Component(RGB rgb, RGB intensity) {
        this.rgb = rgb;
        this.intensity = intensity;
    }

    public Component(RGB rgb, RGB intensity, CIEXYZ XYZ) {
        this.rgb = rgb;
        this.intensity = intensity;
        this.XYZ = XYZ;
    }

    public Component(RGB rgb, RGB intensity, CIEXYZ XYZ, RGB gamma) {
        this.rgb = rgb;
        this.intensity = intensity;
        this.XYZ = XYZ;
        this.gamma = gamma;
    }

    public Component(Component c) {
        this.rgb = c.rgb;
        this.intensity = c.intensity;
        this.XYZ = c.XYZ;
    }

//    public Component(Patch p) {}

    public RGB rgb;
    public RGB intensity;
    public CIEXYZ XYZ;
    public RGB gamma;
}
