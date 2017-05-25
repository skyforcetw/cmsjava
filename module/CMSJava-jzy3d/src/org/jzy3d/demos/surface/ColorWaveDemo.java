package org.jzy3d.demos.surface;


import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.ChartKeyController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.demos.AbstractDemo;
import org.jzy3d.demos.Launcher;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;


public class ColorWaveDemo extends AbstractDemo{
	public static void main(String[] args) throws Exception {
		Launcher.openDemo(new ColorWaveDemo());
	}

	public ColorWaveDemo(){
		// Define a function to plot
		Mapper mapper = new Mapper(){
			public double f(double x, double y) {
				return x * Math.sin( x * y );
			}
		};

		// Define range and precision for the function to plot
		Range range = new Range(-3,3);
		int steps   = 80;

		// Create the object to represent the function over the given range.
		final Shape surface = (Shape)Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setColorMapper(new ColorMapper(new ColorMapGrayscale(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1,1,1,.5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
//                surface.setColor(Color.GREEN);

		// Create a chart
		chart = new Chart(Quality.Advanced);

		// Setup a colorbar for the surface object and add it to the scene
		chart.getScene().getGraph().add(surface);
		ColorbarLegend cbar = new ColorbarLegend(surface, chart.getView().getAxe().getLayout());
		surface.setLegend(cbar);
		chart.addController( new ChartKeyController());
	}
	public Chart getChart(){
		return chart;
	}
	protected Chart chart;
}
