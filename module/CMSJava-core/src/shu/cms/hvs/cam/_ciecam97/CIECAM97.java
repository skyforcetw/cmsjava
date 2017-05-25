package shu.cms.hvs.cam._ciecam97;

/* The Main function of the CIECAM97s Color Appearance Model that
 * Calculates lightness, brightness, saturation, chroma, and colorfulness
 * from XYZ inputs and viewing conditions.
 */



public class CIECAM97 {

  public static JCH XYZtoJCH(XYZ in, View view, Scene scene) {
    //declare and initialize variables needed to calculate outputs
    JCH output = new JCH();
    Background bg = new Background();
    double rgb[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgb[i] = 0;
    }
    double rgbw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbw[i] = 0;
    }
    double rgbc[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbc[i] = 0;
    }
    double rgbcw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbcw[i] = 0;
    }
    double rgbprime[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbprime[i] = 0;
    }
    double rgbprimew[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbprimew[i] = 0;
    }
    double rgba[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgba[i] = 0;
    }
    double rgbaw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbaw[i] = 0;
    }
    double D = 0.0;
    double p = 0.0;
    double a = 0.0;
    double b = 0.0;
    double h = 0.0;
    double e = 0.0;
    double e1 = 0.0;
    double e2 = 0.0;
    double h1 = 0.0;
    double h2 = 0.0;
    double A = 0.0;
    double Aw = 0.0;
    double S = 0.0;

    // CIECAM97s XYZ to JCh
    rgb = convToRGB(in.x, in.y, in.z);
    rgbw = convToRGB(scene.w.x, scene.w.y, scene.w.z);

    /* Debug Print Statements:
      for(int i=0; i<3; i++){
     System.out.println("rgb["+i+"] = "+rgb[i]);
     }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbw["+i+"] = "+rgbw[i]);
     }
      System.out.println(" ");
     */



    D = view.F - (view.F / (1.0 + 2.0 * Math.pow(scene.La, 0.25) +
                            Math.pow(scene.La, 2.0) / 300.0));
    p = Math.pow(rgbw[2], 0.0834);

    rgbc[0] = (D * (1.0 / rgbw[0]) + 1.0 - D) * rgb[0];
    rgbc[1] = (D * (1.0 / rgbw[1]) + 1.0 - D) * rgb[1];
    rgbc[2] = (rgb[2] / Math.abs(rgb[2])) *
        (D * (1.0 / Math.pow(rgbw[2], p)) + 1.0 - D) *
        Math.pow(Math.abs(rgb[2]), p);

    rgbcw[0] = (D * (1.0 / rgbw[0]) + 1.0 - D) * rgbw[0];
    rgbcw[1] = (D * (1.0 / rgbw[1]) + 1.0 - D) * rgbw[1];
    rgbcw[2] = (rgbw[2] / Math.abs(rgbw[2])) *
        (D * (1.0 / Math.pow(rgbw[2], p)) + 1.0 - D) *
        Math.pow(Math.abs(rgbw[2]), p);

    /* Debug Print Statements:
      for(int i=0; i<3; i++){
     System.out.println("rgbc["+i+"] = "+rgbc[i]);
     }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbcw["+i+"] = "+rgbcw[i]);
     }
      System.out.println(" ");
     */

    bg.getBackground(scene.La, scene.Yb, scene.w.y, view.Fll);

    rgbprime = convToPrime(rgbc, in.y);
    rgbprimew = convToPrime(rgbcw, scene.w.y);

    /* Debug Print Statements:
      for(int i=0; i<3; i++) {
     System.out.println("rgbprime[" + i + "] = " + rgbprime[i]);
      }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbprimew["+i+"] = "+rgbprimew[i]);
     }
      System.out.println(" ");
     */


    for (int i = 0; i < 3; i++) {
      rgba[i] = (40.0 * Math.pow( (bg.Fl * rgbprime[i] / 100.0), 0.73)) /
          (Math.pow( (bg.Fl * rgbprime[i] / 100.0), 0.73) + 2.0) + 1.0;
      rgbaw[i] = (40.0 * Math.pow( (bg.Fl * rgbprimew[i] / 100.0), 0.73)) /
          (Math.pow( (bg.Fl * rgbprimew[i] / 100.0), 0.73) + 2.0) + 1.0;
    }

    /* Debug Print Statements:
      for(int i=0; i<3; i++) {
     System.out.println("rgba[" + i + "] = " + rgba[i]);
      }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbaw["+i+"] = "+rgbaw[i]);
     }
      System.out.println(" ");
     */


    a = rgba[0] - 12.0 * rgba[1] / 11.0 + rgba[2] / 11.0;
    b = (1.0 / 9.0) * (rgba[0] + rgba[1] - 2.0 * rgba[2]);

    h = (180.0 / Math.PI) * Math.atan2(b, a);

    /* Debug Print Statements:
      System.out.println("a = " + a);
      System.out.println("b = " + b);*/


    if (h < 0) {
      h = 360.0 + h;
    }

    output.h = h;

    if (h < 0 || h > 360) {
      System.out.println("h = " + h);
    }

    if (h <= 20.14) {
      e1 = 0.8565;
      e2 = 0.8;
      h1 = 0.0;
      h2 = 20.14;
    }
    else if (h <= 90) {
      e1 = 0.8;
      e2 = 0.7;
      h1 = 20.14;
      h2 = 90.0;
    }
    else if (h <= 164.25) {
      e1 = 0.7;
      e2 = 1.0;
      h1 = 90.0;
      h2 = 164.25;
    }
    else if (h <= 237.53) {
      e1 = 1.0;
      e2 = 1.2;
      h1 = 164.25;
      h2 = 237.53;
    }
    else {
      e1 = 1.2;
      e2 = 0.8565;
      h1 = 237.53;
      h2 = 360.0;
    }

    e = e1 + (e2 - e1) * (h - h1) / (h2 - h1);

    A = (2 * rgba[0] + rgba[1] + (1.0 / 20.0) * rgba[2] - 2.05) * bg.Nbb;
    Aw = (2 * rgbaw[0] + rgbaw[1] + (1.0 / 20.0) * rgbaw[2] - 2.05) * bg.Nbb;

    /* Debug Print Statements:
      System.out.println("e = " + e);
      System.out.println("A = " + A);
      System.out.println("Aw = " + Aw);
     */


    output.J = 100 * Math.pow(A / Aw, view.c * bg.z);
    S = (50.0 * (Math.sqrt(a * a + b * b)) * 100.0 * e * (10.0 / 13.0) *
         view.Nc * bg.Ncb) /
        (rgba[0] + rgba[1] + (21.0 / 20.0) * rgba[2]);
    output.C = 2.44 * Math.pow(S, 0.69) *
        Math.pow( (output.J / 100.0), (0.67 * bg.n)) *
        (1.64 - Math.pow(0.29, bg.n));

    return output;
  }

  static double[] convToPrime(double[] rgbval, double y) {

    double Mh[][] = new double[3][3]; //MatrixName[column][row]
    double MhInv[][] = new double[3][3];
    double Mb[][] = new double[3][3];
    double MbInv[][] = new double[3][3];
    double MhMbInv[][] = new double[3][3];
    double MbInvTimesRGB[] = new double[3];
    double prime[] = new double[3];
    double a = 0;

    for (int i = 0; i < 3; i++) {
      MbInvTimesRGB[i] = 0.0;
      prime[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        Mh[i][j] = 0.0;
        MhInv[i][j] = 0.0;
        Mb[i][j] = 0.0;
        MbInv[i][j] = 0.0;
        MhMbInv[i][j] = 0.0;
      }
    }

    /*****************************************************/
    Mb[0][0] = 0.8951;
    Mb[0][1] = -0.7502;
    Mb[0][2] = 0.0389;

    Mb[1][0] = 0.2664;
    Mb[1][1] = 1.7135;
    Mb[1][2] = -0.0685;

    Mb[2][0] = -0.1614;
    Mb[2][1] = 0.0367;
    Mb[2][2] = 1.0296;
    /*****************************************************/


    /*****************************************************/
    MbInv[0][0] = 9.8699290546671220e-001;
    MbInv[0][1] = 4.3230526972339440e-001;
    MbInv[0][2] = -8.5286645751773210e-003;

    MbInv[1][0] = -1.4705425642099010e-001;
    MbInv[1][1] = 5.1836027153677740e-001;
    MbInv[1][2] = 4.0042821654084860e-002;

    MbInv[2][0] = 1.5996265166373120e-001;
    MbInv[2][1] = 4.9291228212855590e-002;
    MbInv[2][2] = 9.6848669578755000e-001;
    /*****************************************************/



    /*****************************************************/
    Mh[0][0] = 0.38971;
    Mh[0][1] = -0.22981;
    Mh[0][2] = 0.0;

    Mh[1][0] = 0.68898;
    Mh[1][1] = 1.1834;
    Mh[1][2] = 0.0;

    Mh[2][0] = -0.07868;
    Mh[2][1] = 0.04641;
    Mh[2][2] = 1.0;
    /*****************************************************/

    /*****************************************************/
    MhInv[0][0] = 1.9102;
    MhInv[0][1] = 0.3710;
    MhInv[0][2] = 0.0;

    MhInv[1][0] = -1.1121;
    MhInv[1][1] = 0.6291;
    MhInv[1][2] = 0.0;

    MhInv[2][0] = 0.2019;
    MhInv[2][1] = 0.0;
    MhInv[2][2] = 1.0;
    /*****************************************************/

    for (int i = 0; i < 3; i++) {
      rgbval[i] = rgbval[i] * y;
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = MbInv[k][j] * rgbval[k];
        MbInvTimesRGB[j] = MbInvTimesRGB[j] + a;
      }
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = Mh[k][j] * MbInvTimesRGB[k];
        prime[j] = prime[j] + a;
      }
    }

    return prime;

  }

  static double[] convToRGB(double x, double y, double z) {

    double Mb[][] = new double[3][3];
    double xyz[] = new double[3];
    double rgbs[] = new double[3];

    for (int i = 0; i < 3; i++) {
      xyz[i] = 0.0;
      rgbs[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        Mb[i][j] = 0.0;
      }
    }

    /*****************************************************/
    Mb[0][0] = 0.8951;
    Mb[0][1] = -0.7502;
    Mb[0][2] = 0.0389;

    Mb[1][0] = 0.2664;
    Mb[1][1] = 1.7135;
    Mb[1][2] = -0.0685;

    Mb[2][0] = -0.1614;
    Mb[2][1] = 0.0367;
    Mb[2][2] = 1.0296;
    /*****************************************************/

    xyz[0] = x / y;
    xyz[1] = y / y;
    xyz[2] = z / y;

    double a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = Mb[k][j] * xyz[k];
        rgbs[j] = rgbs[j] + a;
      }
    }

    return rgbs;
  }

  /******************************************************************/
  /***********          End Forward Model              **************/
  /******************************************************************/


  /*******************************************************************/
  /****************** Begin Inverse Model ****************************/
  /*******************************************************************/

  public static XYZ JCHtoXYZ(JCH in, View view, Scene scene) {

    XYZ output = new XYZ();
    Background bg = new Background();

    /******************** Variable Declarations ************************/
    double rgbw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbw[i] = 0;
    }
    double rgbcw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbcw[i] = 0;
    }
    double rgbprimew[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbprimew[i] = 0;
    }
    double rgbaw[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbaw[i] = 0;
    }
    double rgba[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgba[i] = 0;
    }
    double rgbprime[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbprime[i] = 0;
    }
    double rgbcY[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbcY[i] = 0;
    }
    double rgbc[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgbc[i] = 0;
    }
    double rgb[] = new double[3];
    for (int i = 0; i < 3; i++) {
      rgb[i] = 0;
    }
    double xyz[] = new double[3];
    for (int i = 0; i < 3; i++) {
      xyz[i] = 0;
    }

    double D = 0.0;
    double p = 0.0;
    double a = 0.0;
    double b = 0.0;
    double h = 0.0;
    double e = 0.0;
    double e1 = 0.0;
    double e2 = 0.0;
    double h1 = 0.0;
    double h2 = 0.0;
    double A = 0.0;
    double Aw = 0.0;
    double s = 0.0;
    double sign = 0.0;
    double Yc = 0.0;
    double Yprime = 0.0;
    /***************  End Variable Declarations *************************/


    /********************* Begin Inverse Model Algorithm ****************/
    rgbw = convXYZToRGB(scene.w.x, scene.w.y, scene.w.z);

    //if (D == 1.0)
    D = view.F - (view.F / (1. + 2. * Math.pow(scene.La, 0.25)
                            + Math.pow(scene.La, 2.0) / 300.0));
    p = Math.pow(rgbw[2], 0.0834);

    /* Debug Print Statements:
      System.out.println("D = " + D);
      System.out.println("p = " + p);
     */

    rgbcw[0] = (D * (1.0 / rgbw[0]) + 1.0 - D) * rgbw[0];
    rgbcw[1] = (D * (1.0 / rgbw[1]) + 1.0 - D) * rgbw[1];
    rgbcw[2] = (rgbw[2] / Math.abs(rgbw[2])) *
        ( (D * (1.0 / (Math.pow(rgbw[2], p))) + 1.0 - D) *
         Math.pow(Math.abs(rgbw[2]), p));

    rgbprimew = convCToPrime(rgbcw, scene.w.y);

    bg.getBackground(scene.La, scene.Yb, scene.w.y, view.Fll);

    /* Background vars calculated:
     k FL n Nbb Ncb z			*/

    for (int i = 0; i < 3; i++) {
      rgbaw[i] = (40.0 * Math.pow( (bg.Fl * rgbprimew[i] / 100.0), 0.73)) /
          (Math.pow( (bg.Fl * rgbprimew[i] / 100.0), 0.73) + 2.0) + 1.0;
    }
    Aw = (2 * rgbaw[0] + rgbaw[1] + (1.0 / 20.0) * rgbaw[2] - 2.05) * bg.Nbb;

    h = in.h;

    if (h <= 20.14) {
      e1 = 0.8565;
      e2 = 0.8;
      h1 = 0.0;
      h2 = 20.14;
    }
    else if (h <= 90.0) {
      e1 = 0.8;
      e2 = 0.7;
      h1 = 20.14;
      h2 = 90.0;
    }
    else if (h <= 164.25) {
      e1 = 0.7;
      e2 = 1.0;
      h1 = 90.0;
      h2 = 164.25;
    }
    else if (h <= 237.53) {
      e1 = 1.0;
      e2 = 1.2;
      h1 = 164.25;
      h2 = 237.53;
    }
    else {
      e1 = 1.2;
      e2 = 0.8565;
      h1 = 237.53;
      h2 = 360.0;
    }

    e = e1 + (e2 - e1) * (h - h1) / (h2 - h1);

    A = (Math.pow( (in.J / 100.0), (1.0 / (view.c * bg.z)))) * Aw;

    s = (Math.pow(in.C, (1.0 / 0.69))) /
        (Math.pow( (2.44 * Math.pow( (in.J / 100.0), (0.67 * bg.n)) *
                    (1.64 - Math.pow(0.29, bg.n))), (1.0 / 0.69)));

    if ( (h > 90.0) && (h <= 270.0)) {
      sign = -1.0;
    }
    else {
      sign = 1.0;
    }

    a = (s * ( (A / bg.Nbb) + 2.05)) /
        (sign * Math.sqrt(1.0 + Math.pow( (Math.tan(Math.PI * h / 180.0)), 2.0)) *
         (50000.0 * e * view.Nc * bg.Ncb / 13.0) + (s * ( (11.0 / 23.0) +
        (108.0 / 23.0) * Math.tan(Math.PI * h / 180.0))));
    b = a * Math.tan(Math.PI * h / 180.0);

    rgba[0] = (20.0 / 61.0) * ( (A / bg.Nbb) + 2.05) +
        (41.0 / 61.0) * (11.0 / 23.0) * a +
        (288.0 / 61.0) * (1.0 / 23.0) * b;
    rgba[1] = (20.0 / 61.0) * ( (A / bg.Nbb) + 2.05) -
        (81.0 / 61.0) * (11.0 / 23.0) * a -
        (288.0 / 61.0) * (1.0 / 23.0) * b;
    rgba[2] = (20.0 / 61.0) * ( (A / bg.Nbb) + 2.05) -
        (20.0 / 61.0) * (11.0 / 23.0) * a -
        (20.0 / 61.0) * (315.0 / 23.0) * b;

    /* Debug Print Statements:
      for(int i=0; i<3; i++) {
     System.out.println("rgba[" + i + "] = " + rgba[i]);
      }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbaw["+i+"] = "+rgbaw[i]);
     }
      System.out.println(" ");
     */


    if ( (rgba[0] - 1.0) < 0.0) {
      rgbprime[0] = -100.0 *
          Math.pow( ( (2.0 - 2.0 * rgba[0]) / (39.0 + rgba[0])), (1.0 / 0.73));
    }
    else {
      rgbprime[0] = 100.0 *
          Math.pow( ( (2.0 * rgba[0] - 2.0) / (41.0 - rgba[0])), (1.0 / 0.73));
    }

    if ( (rgba[1] - 1.0) < 0.0) {
      rgbprime[1] = -100.0 *
          Math.pow( ( (2.0 - 2.0 * rgba[1]) / (39.0 + rgba[1])), (1.0 / 0.73));
    }
    else {
      rgbprime[1] = 100.0 *
          Math.pow( ( (2.0 * rgba[1] - 2.0) / (41.0 - rgba[1])), (1.0 / 0.73));
    }

    if ( (rgba[2] - 1.0) < 0.0) {
      rgbprime[2] = -100.0 *
          Math.pow( ( (2.0 - 2.0 * rgba[2]) / (39.0 + rgba[2])), (1.0 / 0.73));
    }
    else {
      rgbprime[2] = 100.0 *
          Math.pow( ( (2.0 * rgba[2] - 2.0) / (41.0 - rgba[2])), (1.0 / 0.73));
    }

    rgbcY = convPrimeToCY(rgbprime, bg.Fl);

    /* Debug Print Statements:
      for(int i=0; i<3; i++) {
     System.out.println("rgbprime[" + i + "] = " + rgbprime[i]);
      }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbprimew["+i+"] = "+rgbprimew[i]);
     }
      System.out.println(" ");
     */


    Yc = 0.43231 * rgbcY[0] + 0.51836 * rgbcY[1] + 0.04929 * rgbcY[2];

    rgbc[0] = (rgbcY[0] / Yc) / (D * (1.0 / rgbw[0]) + 1.0 - D);
    rgbc[1] = (rgbcY[1] / Yc) / (D * (1.0 / rgbw[1]) + 1.0 - D);
    rgbc[2] = ( (rgbcY[2] / Yc) / (Math.abs(rgbcY[2] / Yc))) *
        (Math.pow( (Math.abs(rgbcY[2] / Yc)), (1.0 / p))) /
        (Math.pow( (D * (1.0 / Math.pow(rgbw[2], p)) + 1.0 - D), (1.0 / p)));

    /* Debug Print Statements:
      for(int i=0; i<3; i++){
     System.out.println("rgbc["+i+"] = "+rgbc[i]);
     }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbcw["+i+"] = "+rgbcw[i]);
     }
      System.out.println(" ");
     */

    Yprime = 0.43231 * rgbc[0] * Yc + 0.51836 * rgbc[1] * Yc +
        0.04929 * rgbc[2] * Yc;

    rgb[0] = rgbc[0] * Yc;
    rgb[1] = rgbc[1] * Yc;
    rgb[2] = (rgbc[2] * Yc) / (Math.pow( (Yprime / Yc), ( (1.0 / p) - 1.0)));

    /* Debug Print Statements:
      for(int i=0; i<3; i++){
     System.out.println("rgb["+i+"] = "+rgb[i]);
     }
      System.out.println(" ");
      for(int i=0; i<3; i++){
     System.out.println("rgbw["+i+"] = "+rgbw[i]);
     }
      System.out.println(" ");
     */

    xyz = convRGBToXYZ(rgb);

    output.x = xyz[0];
    output.y = xyz[1];
    output.z = xyz[2];

    return output;
  }

  /************* Begin Methods Called by Inverse Model *******************/

  static double[] convXYZToRGB(double x, double y, double z) {

    double Mb[][] = new double[3][3];
    double xyzs[] = new double[3];
    double rgbs[] = new double[3];

    double a = 0;

    for (int i = 0; i < 3; i++) {
      rgbs[i] = 0.0;
      xyzs[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        Mb[i][j] = 0.0;
      }
    }

    xyzs[0] = x;
    xyzs[1] = y;
    xyzs[2] = z;

    /*****************************************************/
    Mb[0][0] = 0.8951;
    Mb[0][1] = -0.7502;
    Mb[0][2] = 0.0389;

    Mb[1][0] = 0.2664;
    Mb[1][1] = 1.7135;
    Mb[1][2] = -0.0685;

    Mb[2][0] = -0.1614;
    Mb[2][1] = 0.0367;
    Mb[2][2] = 1.0296;
    /*****************************************************/

    for (int i = 0; i < 3; i++) {
      xyzs[i] = xyzs[i] / y;
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = Mb[k][j] * xyzs[k];
        rgbs[j] = rgbs[j] + a;
      }
    }

    return rgbs;
  }

  static double[] convCToPrime(double[] rgbval, double y) {

    double Mh[][] = new double[3][3];
    double MbInv[][] = new double[3][3];
    double MbInvTimesRGB[] = new double[3];
    double prime[] = new double[3];

    double a = 0.0;

    for (int i = 0; i < 3; i++) {
      MbInvTimesRGB[i] = 0.0;
      prime[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        Mh[i][j] = 0.0;
        MbInv[i][j] = 0.0;
      }
    }

    /*****************************************************/
    Mh[0][0] = 0.38971;
    Mh[0][1] = -0.22981;
    Mh[0][2] = 0.0;

    Mh[1][0] = 0.68898;
    Mh[1][1] = 1.1834;
    Mh[1][2] = 0.0;

    Mh[2][0] = -0.07868;
    Mh[2][1] = 0.04641;
    Mh[2][2] = 1.0;
    /*****************************************************/

    /*****************************************************/
    MbInv[0][0] = 9.8699290546671220e-001;
    MbInv[0][1] = 4.3230526972339440e-001;
    MbInv[0][2] = -8.5286645751773210e-003;

    MbInv[1][0] = -1.4705425642099010e-001;
    MbInv[1][1] = 5.1836027153677740e-001;
    MbInv[1][2] = 4.0042821654084860e-002;

    MbInv[2][0] = 1.5996265166373120e-001;
    MbInv[2][1] = 4.9291228212855590e-002;
    MbInv[2][2] = 9.6848669578755000e-001;
    /*****************************************************/

    for (int i = 0; i < 3; i++) {
      rgbval[i] = rgbval[i] * y;
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = MbInv[k][j] * rgbval[k];
        MbInvTimesRGB[j] = MbInvTimesRGB[j] + a;
      }
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = Mh[k][j] * MbInvTimesRGB[k];
        prime[j] = prime[j] + a;
      }
    }

    return prime;
  }

  static double[] convPrimeToCY(double[] rgbval, double Fl) {

    double Mb[][] = new double[3][3];
    double MhInv[][] = new double[3][3];
    double MhInvTimesRGB[] = new double[3];
    double cY[] = new double[3];

    double a = 0.0;

    for (int i = 0; i < 3; i++) {
      MhInvTimesRGB[i] = 0.0;
      cY[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        Mb[i][j] = 0.0;
        MhInv[i][j] = 0.0;
      }
    }

    /*****************************************************/
    Mb[0][0] = 0.8951;
    Mb[0][1] = -0.7502;
    Mb[0][2] = 0.0389;

    Mb[1][0] = 0.2664;
    Mb[1][1] = 1.7135;
    Mb[1][2] = -0.0685;

    Mb[2][0] = -0.1614;
    Mb[2][1] = 0.0367;
    Mb[2][2] = 1.0296;
    /*****************************************************/

    /*****************************************************/
    MhInv[0][0] = 1.9102;
    MhInv[0][1] = 0.371;
    MhInv[0][2] = 0.0;

    MhInv[1][0] = -1.1121;
    MhInv[1][1] = 0.6291;
    MhInv[1][2] = 0.0;

    MhInv[2][0] = 0.2019;
    MhInv[2][1] = 0.0;
    MhInv[2][2] = 1.0;
    /*****************************************************/

    for (int i = 0; i < 3; i++) {
      rgbval[i] = rgbval[i] / Fl;
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = MhInv[k][j] * rgbval[k];
        MhInvTimesRGB[j] = MhInvTimesRGB[j] + a;
      }
    }

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = Mb[k][j] * MhInvTimesRGB[k];
        cY[j] = cY[j] + a;
      }
    }

    return cY;
  }

  static double[] convRGBToXYZ(double[] rgbs) {

    double MbInv[][] = new double[3][3];
    double xyzs[] = new double[3];

    double a = 0.0;

    for (int i = 0; i < 3; i++) {
      xyzs[i] = 0.0;
      for (int j = 0; j < 3; j++) {
        MbInv[i][j] = 0.0;
      }
    }

    /*****************************************************/
    MbInv[0][0] = 9.8699290546671220e-001;
    MbInv[0][1] = 4.3230526972339440e-001;
    MbInv[0][2] = -8.5286645751773210e-003;

    MbInv[1][0] = -1.4705425642099010e-001;
    MbInv[1][1] = 5.1836027153677740e-001;
    MbInv[1][2] = 4.0042821654084860e-002;

    MbInv[2][0] = 1.5996265166373120e-001;
    MbInv[2][1] = 4.9291228212855590e-002;
    MbInv[2][2] = 9.6848669578755000e-001;
    /*****************************************************/

    a = 0.0;
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        a = MbInv[k][j] * rgbs[k];
        xyzs[j] = xyzs[j] + a;
      }
    }

    return xyzs;

  }

}
