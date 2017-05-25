package org.jzy3d.demos.surface.big;

import java.awt.Rectangle;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.Tesselator;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTesselator;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;

/**
 * Factory for various surface related tasks and the creation
 * of CompileableComposite objects.
 * Obtain an instance of the factory and use the set methods
 * to configure the factory appropriately, before calling any
 * of the create or build methods.
 * @author Nils Hoffmann
 */
public class SurfaceFactory {

    private IColorMap colorMap = new ColorMapRainbow();
    private Color colorFactor = new Color(1, 1, 1, 1f);
    private boolean faceDisplayed = true;
    private boolean wireframeDisplayed = false;
    private Color wireframeColor = Color.BLACK;

    public void setColorMap(IColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public void setColorFactor(Color c) {
        this.colorFactor = c;
    }

    public void setFaceDisplayed(boolean faceDisplayed) {
        this.faceDisplayed = faceDisplayed;
    }

    public void setWireframeColor(Color wireframeColor) {
        this.wireframeColor = wireframeColor;
    }

    public void setWireframeDisplayed(boolean wireframeDisplayed) {
        this.wireframeDisplayed = wireframeDisplayed;
    }

    public CompileableComposite createSurface(final Rectangle r, final Mapper m) {
        return createSurface(r, m, -1, -1);
    }

    public CompileableComposite createSurface(final Rectangle r, final Mapper m, int stepsx, int stepsy) {
        final int columns = r.width - 1;
        final int rows = r.height - 1;
        Range rangex = new Range(r.x, r.x + columns);
        Range rangey = new Range(r.y, r.y + rows);
        final int sx = stepsx == -1 ? columns : stepsx;
        final int sy = stepsy == -1 ? rows : stepsy;
        return createSurface(rangex, sx, rangey, sy, m);
    }

    public CompileableComposite createSurface(Range rangex, int stepsx, Range rangey, int stepsy, Mapper mapper) {
        long start = System.nanoTime();
        Tesselator tesselator = new OrthonormalTesselator();
        Shape s1 = (Shape) tesselator.build(new OrthonormalGrid(rangex, stepsx, rangey, stepsy).apply(mapper));
        System.out.println("Tesselation completed in " + ((float) (System.nanoTime() - start)) / (1000.0f * 1000.0f * 1000.0f) + " s");
        return buildComposite(applyStyling(s1));
    }

    public Shape applyStyling(Shape s) {
        s.setColorMapper(new ColorMapper(this.colorMap, s.getBounds().getZmin(), s.getBounds().getZmax()));
        s.setFaceDisplayed(this.faceDisplayed);
        s.setWireframeDisplayed(this.wireframeDisplayed);
        s.setWireframeColor(this.wireframeColor);
        return s;
    }

    public CompileableComposite buildComposite(Shape s) {
        CompileableComposite sls = new CompileableComposite();
        sls.add(s.getDrawables());
        sls.setColorMapper(new ColorMapper(this.colorMap, sls.getBounds().getZmin(), sls.getBounds().getZmax(), this.colorFactor));
        sls.setFaceDisplayed(s.getFaceDisplayed());
        sls.setWireframeDisplayed(s.getWireframeDisplayed());
        sls.setWireframeColor(s.getWireframeColor());
        return sls;
    }
}
