/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jzy3d.demos.histogram.barchart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.primitives.HistogramBar;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 *
 * @author ao
 */
public class BarChartBar extends HistogramBar {

    public static final float BAR_RADIUS = 20;
    public static final float BAR_FEAT_BUFFER_RADIUS = 7;
    private ToggleTextTooltipRenderer tr;
    private final Chart chart;
    private Shape shape;
    // HACK! -> the view class in API does not expose GLU object!
    public GLU gluObj;
    private final String info;

    public String getInfo() {
        return info;
    }

    public IntegerCoord2d getCenterToScreenProj() {
        Coord3d co = chart.getView().getCamera().modelToScreen(
                chart.getView().getCurrentGL(),
                gluObj,
                getBounds().getCenter());

        IntegerCoord2d c2d = new IntegerCoord2d((int) co.x, (int) chart.flip(co.y));
        return c2d;
    }

    public List<Coord2d> getBoundsToScreenProj() {
        Coord3d[] co = chart.getView().getCamera().modelToScreen(
                chart.getView().getCurrentGL(),
                gluObj,
                getShape().getBounds().getVertices().toArray(new Coord3d[]{}));
        List<Coord2d> l = new ArrayList<Coord2d>();
        for (Coord3d c3 : co) {
            l.add(new Coord2d((int) c3.x, (int) chart.flip(c3.y)));
        }
        return l;
    }

    public BarChartBar(Chart c, String info) {
        super();
        this.chart = c;
        this.info = info;
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public BoundingBox3d getBounds() {
        return getShape().getBounds();
    }

    @Override
    public void draw(GL gl, GLU glu, Camera camera) {
        super.draw(gl, glu, camera);
    }

    public void setData(int compUnit, int feature, float height, Color color) {
        shape = getBar(
                new Coord3d((compUnit) * BarChartBar.BAR_RADIUS * 2,
                feature * (BarChartBar.BAR_RADIUS + BarChartBar.BAR_FEAT_BUFFER_RADIUS) * 2, 0),
                BAR_RADIUS, height, color);
        add(shape);
    }

    private Quad getZQuad(Coord3d position, float radius, Color color) {
        Quad q = new Quad();
        q.add(new Point(new Coord3d(position.x + radius, position.y + radius, position.z)));
        q.add(new Point(new Coord3d(position.x + radius, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x - radius, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x - radius, position.y + radius, position.z)));
        q.setColor(color);
        q.setWireframeColor(Color.BLACK);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Quad getYQuad(Coord3d position, float radius, float height, Color color) {
        Quad q = new Quad();
        q.add(new Point(new Coord3d(position.x + radius, position.y, position.z + height)));
        q.add(new Point(new Coord3d(position.x + radius, position.y, position.z)));
        q.add(new Point(new Coord3d(position.x - radius, position.y, position.z)));
        q.add(new Point(new Coord3d(position.x - radius, position.y, position.z + height)));
        q.setColor(color);
        q.setWireframeColor(Color.BLACK);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Quad getXQuad(Coord3d position, float radius, float height, Color color) {
        Quad q = new Quad() {

            @Override
            public void draw(GL gl, GLU glu, Camera cam) {
                super.draw(gl, glu, cam);
                gluObj = glu;
            }
        };
        q.add(new Point(new Coord3d(position.x, position.y + radius, position.z + height)));
        q.add(new Point(new Coord3d(position.x, position.y + radius, position.z)));
        q.add(new Point(new Coord3d(position.x, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x, position.y - radius, position.z + height)));
        q.setColor(color);
        q.setWireframeColor(Color.BLACK);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Shape getBar(Coord3d position, float radius, float height, Color color) {
        Coord3d p1 = position.clone();
        Coord3d p2 = position.clone();
        p2.z += height;
        Coord3d p3 = position.clone();
        p3.y -= radius;
        Coord3d p4 = position.clone();
        p4.y += radius;
        Coord3d p5 = position.clone();
        p5.x -= radius;
        Coord3d p6 = position.clone();
        p6.x += radius;

        List<Polygon> ps = new LinkedList<Polygon>();

        ps.add(getZQuad(p1, radius, color));
        ps.add(getZQuad(p2, radius, color));

        ps.add(getYQuad(p3, radius, height, color));
        ps.add(getYQuad(p4, radius, height, color));

        ps.add(getXQuad(p5, radius, height, color));
        ps.add(getXQuad(p6, radius, height, color));

        return new Shape(ps) {

            @Override
            public boolean isDisplayed() {
                BoundingBox3d ba = chart.getView().getAxe().getBoxBounds();
                BoundingBox3d bs = getBounds();
                return (ba.getXmax() >= bs.getXmax()) && (ba.getYmax() >= bs.getYmax())
                        && bs.getXmin() >= ba.getXmin() && bs.getYmin() >= ba.getYmin();
            }
        };
    }

    void setSelected(boolean selected) {
        if (tr == null) {
            tr = new ToggleTextTooltipRenderer(info, this);
            chart.getView().addTooltip(tr);
        }
        if (selected) {
            setWireframeWidth(3);
            tr.setVisible(true);
        } else {
            setWireframeWidth(1);
            tr.setVisible(false);
        }
    }
}
