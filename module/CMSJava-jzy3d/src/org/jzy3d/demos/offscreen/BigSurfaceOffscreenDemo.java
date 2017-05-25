package org.jzy3d.demos.offscreen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jzy3d.chart.Chart;
import org.jzy3d.demos.AbstractDemo;
import org.jzy3d.demos.surface.big.SurfaceFactory;
import org.jzy3d.plot3d.builder.concrete.BufferedImageMapper;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

public class BigSurfaceOffscreenDemo extends AbstractDemo {
    public static void main(String[] args) throws Exception {
        System.err.println("This demo may need to run with -Xmx1024m as VM arguments");
        new BigSurfaceOffscreenDemo();
    }

    public BigSurfaceOffscreenDemo() {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(BigSurfaceOffscreenDemo.class.getClassLoader().getResourceAsStream("org/jzy3d/demos/surface/big/chromatogram.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImageMapper mapper = new BufferedImageMapper(bi);
        Rectangle roi = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());

        SurfaceFactory sf = new SurfaceFactory();
        CompileableComposite cc = sf.createSurface(mapper.getClippedViewport(roi), mapper, roi.width / 2, roi.height / 2);


        // Define an offscreen chart with desired output image dimension
        // On can only specify "offscreen", which defaults image to (800,600)
        chart = new Chart("offscreen,400,400");
        chart.getScene().getGraph().add(cc);
        createScreenshot(chart.getCanvas(), 0);
    }

    protected void createScreenshot(ICanvas ic, int id) {
        File output = new File("./data/screenshots",getClass().getSimpleName()+"_"+id+".png");
        if (!output.getParentFile().exists()) {
            output.mkdirs();
        }
        try {
            ImageIO.write(ic.screenshot(), "png", output);
            System.out.println("Dumped screenshot in: " + output);
        } catch (IOException ex) {
            Logger.getLogger(BigSurfaceOffscreenDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
    public Chart getChart() {
        return chart;
    }
    protected Chart chart;
}
