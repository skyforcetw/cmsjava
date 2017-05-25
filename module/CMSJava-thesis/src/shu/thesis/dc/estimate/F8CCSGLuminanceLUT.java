package shu.thesis.dc.estimate;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class F8CCSGLuminanceLUT {
  public static void main(String[] args) {
    DCTarget D50ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             LightSource.i1Pro.D50,
                                             1, DCTarget.Chart.CCSG);

    DCTarget D65ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             LightSource.i1Pro.D65,
                                             1, DCTarget.Chart.CCSG);

    DCTarget F8ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            LightSource.i1Pro.F8,
                                            1, DCTarget.Chart.CCSG);

    DCTarget F12ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             LightSource.i1Pro.F12,
                                             1, DCTarget.Chart.CCSG);

    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(F8ccsg, true,
        Polynomial.COEF_3.BY_3, false);
    model.produceFactor();

    System.out.println(model.gammaCorrect(D50ccsg.getPatch(44).getRGB().
                                          getValues())[1]);
    System.out.println(model.gammaCorrect(D65ccsg.getPatch(44).getRGB().
                                          getValues())[1]);
    System.out.println(model.gammaCorrect(F8ccsg.getPatch(44).getRGB().
                                          getValues())[1]);
    System.out.println(model.gammaCorrect(F12ccsg.getPatch(44).getRGB().
                                          getValues())[1]);

//    Plot2D plot = Plot2D.getInstance();
//    plot.setVisible(true);
    List<Patch> gray = F8ccsg.filter.grayScale();
    int size = gray.size();
    double[] Y = new double[size];
    double[] G = new double[size];
    for (int x = 0; x < size; x++) {
      Patch p = gray.get(x);
      RGB rgb = p.getRGB();
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      G[x] = p.getRGB().G;
      Y[x] = p.getXYZ().Y;
    }
    System.out.println(DoubleArray.toString(Y));
    System.out.println(DoubleArray.toString(G));
//    plot.addLinePlot(null, Y);

//    for(int x=0;x<256;x++) {
//      double v = x/255.;
//      double[] rgb = new double[]{v,v,v};
//    }
  }
}
