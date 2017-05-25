package auo.cms.hsv.value.experiment;

import java.awt.*;

import shu.cms.plot.*;
import shu.math.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ValueAdjustmentPlotter {

  public static void main(String[] args) {
    Plot3D plot = Plot3D.getInstance();
    Plot3D plot2 = Plot3D.getInstance();
//    Plot3D plotani = Plot3D.getInstance();
//    plotani.setAxeLabel(0, "v");
//    plotani.setAxeLabel(1, "s");
//    plotani.setAxeLabel(2, "v");
//    plotani.setVisible();
//    plotani.addLegend();

    double scale = 20;
//    double[][] histdata = new double[11 * 11][5];
    double[][] dgriddata1 = new double[ (int) scale + 1][ (int) scale + 1];
    double[][] dgriddata2 = new double[ (int) scale + 1][ (int) scale + 1];
    double[][] griddata0 = new double[ (int) scale + 1][ (int) scale + 1];
    double[][] griddata1 = new double[ (int) scale + 1][ (int) scale + 1];
    double[][] griddata2 = new double[ (int) scale + 1][ (int) scale + 1];
    double[] X_S = new double[ (int) scale + 1];
    double[] X_V = new double[ (int) scale + 1];
    for (int x = 0; x < (int) scale + 1; x++) {
      X_S[x] = x / scale;
      X_V[x] = (int) x / scale * 255;
    }

    int voffset = 20;

//    double cratio = 1.5;
    double cratio = 1;
//        double sgap = 0;
    double sgap = 0.15625;

//    try {
//      for (int voffset = 0; voffset <= 255; voffset += 5) {

    for (int s = 0; s <= scale; s++) {
      for (int v = 0; v <= scale; v++) {
        double ss = s / scale;
        int vv = (int) (v / scale * 255);
//        int vv2 = 255 - vv;
//        int vv2 = vv;
        double c = ss * vv; //Max-min
//        double c2 = ss * vv2; //Max-min
//        double c2 = ss * vv; //Max-min
//        double voffset1 = voffset * c / 256;
        double cp = (c * cratio > 255) ? 255 : c * cratio;
//        double voffset2 = voffset * cp * (255 - vv) /
//            128 / 128;
//        double voffset1 = voffset * c * c * (255 - vv) /
//            128 / 128 / 128;
        int intc = (int) c;
        int intc2 = intc >> 3;
        int newintc = intc * (intc2 * intc2);

        int M = vv;
        int m = M - intc;
//        int newintc = intc;
//        intc = intc << ( ( ( (int) (vv * c))) - 8);

//        intc = (int) (intc * ss);
//        System.out.println(intc);
//        double doffset2=(intc * intc * (255 - vv) * (1. + sgap - ss));
//        int intc2 = (intc * intc) >> 15;

//        int displace = (int) (255 * (1 + sgap) - ss * 256 -
//                              (int) (vv * (1 + sgap)) + c);
//        int displace = (int) ( (255 - vv) * (1. + sgap) - 256 * ss + c);
//        int displace = (int) ( (255 - vv) * (1. + sgap) - 256 * ss + c);
//                int displace = (int) ((255 - vv) * (1. + sgap - ss)); //original

        //                int voffset1 = ((int) (intc * intc * displace)) >> 15;
        //        int voffset2 = ( (int) (newintc * displace)) >> 8;
        //                int voffset2 = ((int) (newintc * displace)) >> 16;
//                int voffset1 = (int) (voffset * intc * intc * ((255 - vv) * (1. + sgap - ss))) >> 19;
        int voffset1 = (int) (voffset * intc * ( (255 - vv) * (1. + sgap - ss))) >>
            13;
//                int voffset2 = (int) (voffset * intc * (255 - vv) * (1. + sgap - ss)) >> 13;
//                int voffset2 = (int) (voffset * ((M << 8) - M * M - (m << 8) + M * m) * (1. + sgap - ss)) >> 13;
        int voffset2 = (int) (voffset * intc * ( (255 - vv) * (1. + sgap - ss))) >>
            13;
//                int voffset2 = (int) (voffset * (M * (255 - M + m) - (m << 8)) * (1. + sgap - ss)) >> 13;
//                int voffset2 = (int) (voffset * (M * (255 - M) - m * (255 - M)) * (1. + sgap - ss)) >> 13;
//                int voffset2 =


//        int voffset2 = (voffset2p >> 15);
//        int voffset2 = (voffset2p >> 15);
//        int voffset2 = voffset2p;

//        int voffset2 = voffset2p;
//        double voffset2 = voffset * c * c * (255 - vv) * (1 + sgap - ss) /
//            128 / 128 / 128 * 4;

//        double voffset2 = voffset * c2 * cp * vv * (255 - ss) /
//            128 / 128 / 128 / 128/4;
        double v1 = vv + voffset1;
        double v2 = vv + voffset2;
        v1 = v1 > 255 ? 255 : v1;
        v2 = v2 > 255 ? 255 : v2;
        double deltav1 = v1 - vv;
        double deltav2 = v2 - vv;

        dgriddata1[v][s] = deltav1;
        dgriddata2[v][s] = deltav2;
        griddata0[s][v] = vv;
        griddata1[s][v] = v1;
        griddata2[s][v] = v2;
      }
    }
    //.66, 192; 1.0, 128
//    System.out.println(dgriddata2[15][13] / dgriddata2[10][dgriddata2[0].length -
//                       1]);
    System.out.println(Maths.max(dgriddata2));

//    int ssize = dgriddata2.length;
//    double[] svalues = new double[ssize];
//    for (int x = 0; x < ssize; x++) {
//      svalues[x] = dgriddata2[x][dgriddata2[0].length - 1];
//    }
//    System.out.println(Maths.max(svalues));
//    System.out.println(Maths.max(dgriddata2));
//    System.out.println(Maths.max(dgriddata2) / Maths.max(svalues));
//    plotani.setTitle(Integer.toString(voffset));

//    plotani.addGridPlot("new", Color.blue, X, X, griddata2);

//        Thread.sleep(300);
//        plotani.removeAllPlots();
//      }
//    }
//    catch (InterruptedException ex) {
//      ex.printStackTrace();
//    }
    plot.addGridPlot("org", Color.red, X_S, X_V, dgriddata1);
    plot.addGridPlot("new", Color.blue, X_S, X_V, dgriddata2);
    plot.setAxeLabel(0, "s");
    plot.setAxeLabel(1, "v");
    plot.setAxeLabel(2, "dv");
    plot.setVisible();
    plot.addLegend();
    plot.rotate( -60, -50);
    plot.setFixedBounds(1, 0, 255);
    plot.setFixedBounds(2, 0, 20);
    plot.rotate(180, 0);

    plot2.addGridPlot("0", Color.black, X_S, X_V, griddata0);
    plot2.addGridPlot("org", Color.red, X_S, X_V, griddata1);
    plot2.addGridPlot("new", Color.blue, X_S, X_V, griddata2);
    plot2.setAxeLabel(0, "v");
    plot2.setAxeLabel(1, "s");
    plot2.setAxeLabel(2, "v");
    plot2.setVisible();
    plot2.addLegend();

  }
}
