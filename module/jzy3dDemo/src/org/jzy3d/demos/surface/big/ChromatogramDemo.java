package org.jzy3d.demos.surface.big;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.demos.AbstractDemo;
import org.jzy3d.demos.Launcher;
import org.jzy3d.plot3d.builder.concrete.BufferedImageMapper;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.rendering.canvas.Quality;


public class ChromatogramDemo extends AbstractDemo{
	public static void main(String[] args) throws Exception {
		System.err.println("This demo may need to run with -Xmx1024m as VM arguments");
		Launcher.openDemo(new ChromatogramDemo());
	}

	public ChromatogramDemo(){
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(ChromatogramDemo.class.getClassLoader().getResourceAsStream("org/jzy3d/demos/surface/big/chromatogram.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

        // Define a part of data to pick from the image to create our mapper
        BufferedImageMapper mapper = new BufferedImageMapper(bi);
        Rectangle roi = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());

        // Build a big surface
        SurfaceFactory sf = new SurfaceFactory();
        CompileableComposite cc = sf.createSurface(mapper.getClippedViewport(roi), mapper, roi.width/2, roi.height/2);

        System.out.println("Compilable has " + cc.size() + " polygons");
        System.out.println("------------------------------------");

        // Open the chart with the big surface
        chart = new Chart();
        chart.getScene().getGraph().add(cc);
	}

//	@Override
	public Chart getChart(){
        return chart;
	}
	protected Chart chart;
}
