package org.jzy3d.demos.histogram.barchart;

import java.awt.Graphics2D;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.tooltips.TextTooltipRenderer;

/**
 *
 * @author ao
 */
class ToggleTextTooltipRenderer extends TextTooltipRenderer {

    private final BarChartBar ad;
    private boolean visible = false;

    public ToggleTextTooltipRenderer(String text, final BarChartBar ad) {
        super(text, new IntegerCoord2d(), ad.getBounds().getCenter());
        this.ad = ad;
    }

    @Override
    public void render(Graphics2D g2d) {
        if (visible) {
            updateTargetCoordinate(ad.getBounds().getCenter());
            IntegerCoord2d c2d = ad.getCenterToScreenProj();
            updateScreenPosition(c2d);

            this.text = ad.getInfo();

            super.render(g2d);
        }
    }

    void setVisible(boolean b) {
        visible = b;
    }
}
