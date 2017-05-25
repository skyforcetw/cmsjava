package org.jzy3d.demos.scatter;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.demos.AbstractDemo;
import org.jzy3d.demos.Launcher;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.MultiColorScatter;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;


public class MultiColorScatterDemo extends AbstractDemo{
	public static void main(String[] args) throws Exception {
		Launcher.openDemo(new MultiColorScatterDemo());
	}
	
	public MultiColorScatterDemo(){
		// Create the dot cloud scene and fill with data
		int size = 100000;
		float x;
		float y;
		float z;;
		
		Coord3d[] points = new Coord3d[size];
		
		for(int i=0; i<size; i++){
			x = (float)Math.random() - 0.5f;
			y = (float)Math.random() - 0.5f;
			z = (float)Math.random() - 0.5f;
			points[i] = new Coord3d(x, y, z);
		}		

		MultiColorScatter scatter = new MultiColorScatter( points, new ColorMapper( new ColorMapRainbow(), -0.5f, 0.5f ) );

		Color mainColor = Color.BLACK;
		Color backroundColor = Color.WHITE;
		chart = new Chart();
		chart.getAxeLayout().setMainColor(mainColor);
		chart.getView().setBackgroundColor(backroundColor);
		chart.getScene().add(scatter);
		scatter.setLegend( new ColorbarLegend(scatter, chart.getView().getAxe().getLayout(), mainColor, null) );
		scatter.setLegendDisplayed(true);
	}
	public Chart getChart(){
		return chart;
	}
	protected Chart chart;
}
