package shu.cms.measure.intensity;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

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
 */
public interface IntensityAnalyzerIF {
    RGB getIntensity(RGB rgb);

    CIEXYZ getCIEXYZ();

    void setupComponent(RGB.Channel ch,
                        RGB rgb);

    void enter();

    void beginAnalyze();

    void endAnalyze();

    CIEXYZ getReferenceColor();

    CIEXYZ getPrimaryColor(final RGB.Channel ch);
}
