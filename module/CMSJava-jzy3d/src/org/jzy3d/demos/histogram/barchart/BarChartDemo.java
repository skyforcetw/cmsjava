package org.jzy3d.demos.histogram.barchart;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.demos.AbstractDemo;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class BarChartDemo extends AbstractDemo {

    private final String TITLE = "Feature complexity view";

    public static void main(String[] args) throws Exception {
        new BarChartDemo();
    }
    private LabeledMouseSelector mouseSelection;
    private CustomMouseControl mouseCamera;
    private Frame fr;

    public BarChartDemo() {
//        Settings.getInstance().setHardwareAccelerated(true);
        chart = new Chart(Quality.Nicest);
        setupTitle();
        setupAxes();
        setupMouseNavigation();
        setupKeyboardNavigation();
        setupKeyboardSave();
        setupMouseSelection();
        setupLegend();

        Scene scene = chart.getScene();
        for (int cu = 0; cu < 4; cu++) {
            for (int f = 0; f < 6; f++) {
                scene.add(addBar(cu, f, Math.random() * (5f / (cu + 1) + 7.f / (f + 1))));
            }
        }

        chart.getView().setMaximized(true);
        if(chart.getCanvas() instanceof CanvasSwing){
            fr = new FrameSwing(chart, new Rectangle(0, 0, 400, 400), "Feature complexity view 3d");
        }else{
            fr = new FrameAWT(chart, new Rectangle(0, 0, 400, 400), "Feature complexity view 3d");
        }
    }

    private void setupMouseSelection() {
        mouseSelection = new LabeledMouseSelector(chart);
        chart.getCanvas().addKeyListener(mouseSelection);
    }

    private void setupTitle() {
        Renderer2d messageRenderer = new Renderer2d() {

            public void paint(Graphics g) {
                g.setColor(java.awt.Color.BLUE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
                g.drawString(TITLE,
                        (int) (15 + 0.05d * chart.getCanvas().getRendererWidth()),
                        (int) (15 + 0.05d * chart.getCanvas().getRendererHeight()));
            }
        };
        chart.addRenderer(messageRenderer);
    }

    private void setupMouseNavigation() {
        mouseCamera = new CustomMouseControl(chart);
        mouseCamera.install();
    }

    private int getFeatureIndex(float figYCenter) {
        return (int) ((figYCenter) / (2 * (BarChartBar.BAR_FEAT_BUFFER_RADIUS + BarChartBar.BAR_RADIUS)));
    }

    private void setupAxes() {
        chart.getAxeLayout().setXAxeLabel("Scattering");
        chart.getAxeLayout().setYAxeLabel("Feature");
        chart.getAxeLayout().setYTickRenderer(new ITickRenderer() {

            public String format(float value) {
                return "f" + getFeatureIndex(value);
            }
        });
        chart.getAxeLayout().setYTickProvider(new DiscreteTickProvider());
        chart.getAxeLayout().setZAxeLabel("Tangling");
//        chart.getAxeLayout().setZTickRenderer( new ScientificNotationTickRenderer(2) );
//                float[] ticks = {0f, 0.5f, 1f};
//                chart.getAxeLayout().setZTickProvider(new StaticTickProvider(ticks));

        chart.getView().setViewPositionMode(ViewPositionMode.FREE);
//        chart.getView().setAxeSquared(false);
    }

    public AbstractDrawable addBar(int compUnit, int feature, double height) {
        // compUnit, feature numbered form 0!
        Color color = Color.random();
        color.a = 1f;

        BarChartBar bar = new BarChartBar(chart, "f" + feature + ",c" + compUnit);

        bar.setData(compUnit, feature, (float) height, color);
//        if (!a) {
//            bar.setColorMapper(new ColorMapper(new AffinityColorGen(), 0f, 2.0f));
//            bar.setLegend(new ColorbarLegend(bar, chart.getAxeLayout()));
//            bar.setLegendDisplayed(true);
//            a = true;
//        }

        return bar;
    }

    public Chart getChart() {
        return chart;
    }

    private Chart chart;

    private void setupLegend() {
        chart.addRenderer(new CustomLegendRenderer(chart.getCanvas()));
    }

    private void setupKeyboardNavigation() {
        chart.getCanvas().addKeyListener(new CustomKeyboardControl(chart));
    }

    private void setupKeyboardSave() {
        chart.getCanvas().addKeyListener(new SVGKeyboardSaver(chart));
    }
}
