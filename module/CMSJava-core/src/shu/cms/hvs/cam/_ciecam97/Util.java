package shu.cms.hvs.cam._ciecam97;

public class Util {

  public static XYZ RGBtoXYZ(RGB rgb) {

    double mon2xyz[][] = new double[3][3]; //mon2xyz[column][row]
    double rgbarr[] = new double[3];
    double xyzarr[] = new double[3];
    XYZ xyz = new XYZ();
    int inputerr = 0;

    for (int i = 0; i < 3; i++) {
      xyzarr[i] = 0;
      rgbarr[i] = 0;
      for (int j = 0; j < 3; j++) {
        mon2xyz[i][j] = 0;
      }
    }

    inputerr = 0;
    rgbarr[0] = rgb.r + 0.5;
    rgbarr[1] = rgb.g + 0.5;
    rgbarr[2] = rgb.b + 0.5;

    for (int i = 0; i < 3; i++) {
      if (rgbarr[i] < 0 || rgbarr[i] > 255.5) {
        inputerr = 1;
      }
    }
    if (inputerr != 0) {
      System.out.println("Input Error: Util Rin=" + rgb.r +
                         "Util Gin=" + rgb.g + "Util Bin=" + rgb.b);
    }

    //  Check to make sure green value is not zero
    /*if(rgbarr[1] == 0)
     rgbarr[1] = 0.1; */

    // scale from 0-255 to 0-1 to use this mon2xyz
    for (int i = 0; i < 3; i++) {
      rgbarr[i] = rgbarr[i] / 255.;
    }

    mon2xyz[0][0] = 25.9232;
    mon2xyz[0][1] = 14.6733;
    mon2xyz[0][2] = 1.3834;

    mon2xyz[1][0] = 36.1360;
    mon2xyz[1][1] = 69.9554;
    mon2xyz[1][2] = 12.3235;

    mon2xyz[2][0] = 22.9809;
    mon2xyz[2][1] = 11.4580;
    mon2xyz[2][2] = 119.3425;

    double a = 0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = mon2xyz[k][j] * rgbarr[k];
        xyzarr[j] = xyzarr[j] + a;
      }
    }

    xyz.x = xyzarr[0];
    xyz.y = xyzarr[1];
    xyz.z = xyzarr[2];

    /*System.out.println("Util Xout = " + xyz.x);
       System.out.println("Util Yout = " + xyz.y);
       System.out.println("Util Zout = " + xyz.z);*/



    return xyz;
  }

  public static RGB XYZtoRGB(XYZ xyz) {

    double xyz2mon[][] = new double[3][3];
    double rgbarr[] = new double[3];
    double xyzarr[] = new double[3];
    RGB rgb = new RGB();

    for (int i = 0; i < 3; i++) {
      xyzarr[i] = 0;
      rgbarr[i] = 0;
      for (int j = 0; j < 3; j++) {
        xyz2mon[i][j] = 0;
      }
    }

    xyzarr[0] = xyz.x;
    xyzarr[1] = xyz.y;
    xyzarr[2] = xyz.z;

    /*System.out.println("Util Xin = " + xyz.x);
       System.out.println("Util Yin = " + xyz.y);
       System.out.println("Util Zin = " + xyz.z);
     */


    xyz2mon[0][0] = 0.0540006;
    xyz2mon[0][1] = -0.0114173;
    xyz2mon[0][2] = 0.0005530;

    xyz2mon[1][0] = -0.0265110;
    xyz2mon[1][1] = 0.0201460;
    xyz2mon[1][2] = -0.0017730;

    xyz2mon[2][0] = -0.0078532;
    xyz2mon[2][1] = 0.0002644;
    xyz2mon[2][2] = 0.0084430;

    double a = 0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = xyz2mon[k][j] * xyzarr[k];
        rgbarr[j] = rgbarr[j] + a;
      }
    }

    // scale back from 0-1 to 0-255
    for (int i = 0; i < 3; i++) {
      rgbarr[i] = rgbarr[i] * 255.;
    }

    // Check to make sure colors are in gamut; if not, fit to gamut:
    /*for(int i=0; i<3; i++)
       {	
     if(rgbarr[i] > 255)
      rgbarr[i] = 255;
     if(rgbarr[i] < 0 && rgbarr[i] > -1)
      rgbarr[i] = 0;
       }*/

    rgb.r = rgbarr[0];
    rgb.g = rgbarr[1];
    rgb.b = rgbarr[2];

    /*System.out.println("Util Rout = " + rgb.r);
       System.out.println("Util Gout = " + rgb.g);
       System.out.println("Util Bout = " + rgb.b); */

    return rgb;
  }
}