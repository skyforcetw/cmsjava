package shu.cms.plot;

import shu.cms.colorspace.depend.*;
import shu.math.array.*;
import shu.cms.Spectra;

/*************************************************
 * File:	Diagram.java
 * Description:	Computes chromaticity diagram
 *
 * Author: Gene Vishnevsky  Oct. 15, 1997
 *************************************************/

/**
 * This class computes the chromaticity diagram data.
 */
public class Diagram {

  //sRGB
//  protected final static float XR = 0.64f, YR = 0.33f, XG = 0.29f, YG = 0.60f,
//  XB = 0.15f, YB = 0.06f, XW = 0.3127f, YW = 0.3291f;
  //gamma(?)
  protected final static float GAMMA = 0.8f;
  protected final static RGB.ColorSpace colorSpace = RGB.ColorSpace.sRGB;

//  protected final static float ZR = 1.0f - (XR + YR),
//  ZG = 1.0f - (XG + YG),
//  ZB = 1.0f - (XB + YB),
//  ZW = 1.0f - (XW + YW);

  //圖的size
  protected int M = 300, N = 300;
  //紀錄不同x座標的上下界
  private short range[][];

  /**
   * Chromaticity values
   */
//  protected final static ChromaticityData data = new ChromaticityData();
  protected final static ChromaticityData data = ChromaticityData.
      getInstanceFromSpectra();

//  protected float R, G, B;

  /**
   * This constructor simply initialized the size variables.
   * @param m width of the diagram in pixels
   * @param n height of the diagram in pixels
   */
  public Diagram(int m, int n) {
    M = m;
    N = n;
    range = new short[m][2];
  }

  public final static int[][] compute(int M, int N,
                                      RGB.ColorSpace rgbColorSpace) {

    boolean fillColor = true;
    int icv[][] = new int[M][N];
    int j1, j2 = 0;
    float s;
    int size = data.wxy.length - 1;
    int black = toRGB(1, 1, 1);
    int white = toRGB(255, 255, 255);

    for (int j = 0; j < size; j++) { //DO J=1,81
      //寬度乘上色度, 恐怕是把色度轉到畫素座標
      //這一個x
      float s1 = (float) M * data.wxy[j].x;
      //下一個x
      float s2 = (float) M * data.wxy[j + 1].x;
      //這一個y
      float t1 = (float) N * (1.f - data.wxy[j].y);
      //下一個y
      float t2 = (float) N * (1.f - data.wxy[j + 1].y);
      //飯團外圍的斜率
      float slope = (t2 - t1) / (s2 - s1);
      int i1 = (int) (s1 + 0.5f);
      int i2 = (int) (s2 + 0.5f);

//      if (true) {
      //把色度座標轉換成畫素座標
      if (i1 < i2) {
        //x增加
        for (int ii = i1; ii <= i2; ii++) {
          s = (float) ii;
          j1 = j2;
          j2 = (int) (t1 + slope * (s - s1) + 0.5f);
          if (j1 != 0 && j2 != 0) {
            if (j1 < j2) {
              for (int jj = j1; jj <= j2; jj++) {
                icv[ii - 1][jj - 1] = black; //was 255,1,1
              }
            }
            else {
              for (int jj = j2; jj <= j1; jj++) {
                icv[ii - 1][jj - 1] = black; // was 1,255,1
              }
            }
          }
        }
      }
      else {
        //這一段是畫持平的那一段
        //x減少
        for (int ii = i1; ii > i2; ii--) {
          s = (float) ii;
          j1 = j2;
          j2 = (int) (t1 + slope * (s - s1) + 0.5f);
          if (j1 != 0 && j2 != 0) {
            if (j1 < j2) {
              for (int jj = j1; jj <= j2; jj++) {
                icv[ii - 1][jj - 1] = black; //was 200,200,0
              }
            }
            else {
              for (int jj = j2; jj <= j1; jj++) {
                icv[ii - 1][jj - 1] = black; //was 1, 1, 255
              }
            }
          }
        }
      }
//      } //for( j=0..81 )
    }

    if (fillColor) {
      // Calculate RGB Values for x and y coordinates
      // 著色 由上而下, 由左而右
      float gamma = 1f / (float) rgbColorSpace.gamma;
      for (int j = 1; j <= N; j++) {
        int jtest = 0;
        for (int i = 1; i <= M; i++) {
          if ( (icv[i - 1][j - 1] & (255 << 16)) == (1 << 16) &&
              (icv[i][j - 1] & (255 << 16)) == 0) {
            //是黑線就++ (嚴格來說, 是R=G=B=1的線)
            jtest++;
          }
        }
        if (jtest == 2) {
          //x方向有兩條線
          int itest = 0;
          for (int i = 1; i <= M; i++) {
            if ( (icv[i - 1][j - 1] & (255 << 16)) == (1 << 16) &&
                (icv[i][j - 1] & (255 << 16)) == 0) {
              //再做一次一樣的測試?
              itest++;
            }
            if (itest == 1) {
              //因為由左而右, 如果itest==1 就是在兩個黑線之間, 就算RGB並且填值
              //==================================================================
              // XYZ to RGB
              //==================================================================
              float Xc = (float) i / (float) M;
              float Yc = 1.f - (float) j / (float) N;
              float Zc = 1.f - (Xc + Yc);
              float[] RGB = XYZ2RGB(Xc, Yc, Zc, rgbColorSpace);
              float R = RGB[0];
              float G = RGB[1];
              float B = RGB[2];
              float RMAX = 0.0000000001f;
              if (R > RMAX) {
                RMAX = R;
              }
              if (G > RMAX) {
                RMAX = G;
              }
              if (B > RMAX) {
                RMAX = B;
              }
              //==================================================================

              //==================================================================
              // RGB生成
              //==================================================================
              int ired = (int) (255. * Math.pow(R / RMAX, gamma));
              int igreen = (int) (255. * Math.pow(G / RMAX, gamma));
              int iblue = (int) (255. * Math.pow(B / RMAX, gamma));
              icv[i - 1][j - 1] = toRGB(ired, igreen, iblue);
              //==================================================================
            }
          }
        }
      }
//int white =
      for (int j = 1; j < N; j++) {
        for (int i = 1; i < M; i++) {
          //把黑色圖成白色
          if (icv[i][j] == black) {
            icv[i][j] = white;
          }

        }
      }

    }
    return icv;
  }

  /**
   * Computes the array of pixels representing the diagram.
   * @return int[][] preallocated array to place computed pixels.
   */
  public int[][] compute() {
    int icv[][] = new int[M][N];
    int j1, j2 = 0;
    float s;

    //==========================================================================
    //	Draw tongue outline and init range array
    // 拿掉也沒有影響(?)
    //==========================================================================
    for (int i = 0; i < M; i++) {
      range[i][0] = Short.MAX_VALUE; // min value
      range[i][1] = Short.MIN_VALUE; // max value
      for (int j = 0; j < N; j++) {
        icv[i][j] = 0;
      }
    }
    //==========================================================================
    int size = data.wxy.length - 1;
    for (int j = 0; j < size; j++) { //DO J=1,81
      //寬度乘上色度, 恐怕是把色度轉到畫素座標
      //這一個x
      float s1 = (float) M * data.wxy[j].x;
      //下一個x
      float s2 = (float) M * data.wxy[j + 1].x;
      //這一個y
      float t1 = (float) N * (1.f - data.wxy[j].y);
      //下一個y
      float t2 = (float) N * (1.f - data.wxy[j + 1].y);
      //飯團外圍的斜率
      float slope = (t2 - t1) / (s2 - s1);
      int i1 = (int) (s1 + 0.5f);
      int i2 = (int) (s2 + 0.5f);

      //把色度座標轉換成畫素座標
      if (i1 < i2) {
        //x增加
        for (int ii = i1; ii <= i2; ii++) {
          s = (float) ii;
          j1 = j2;
          j2 = (int) (t1 + slope * (s - s1) + 0.5f);
          if (j1 != 0 && j2 != 0) {
            if (j1 < j2) {
              for (int jj = j1; jj <= j2; jj++) {
                icv[ii - 1][jj - 1] = toRGB(1, 1, 1); //was 255,1,1
                updateRange(ii, jj);
              }
            }
            else {
              for (int jj = j2; jj <= j1; jj++) {
                icv[ii - 1][jj - 1] = toRGB(1, 1, 1); // was 1,255,1
                updateRange(ii, jj);
              }
            }
          }
        }
      }
      else {
        //x減少
        for (int ii = i1; ii > i2; ii--) {
          s = (float) ii;
          j1 = j2;
          j2 = (int) (t1 + slope * (s - s1) + 0.5f);
          if (j1 != 0 && j2 != 0) {
            if (j1 < j2) {
              for (int jj = j1; jj <= j2; jj++) {
                icv[ii - 1][jj - 1] = toRGB(1, 1, 1); //was 200,200,0
                updateRange(ii, jj);
              }
            }
            else {
              for (int jj = j2; jj <= j1; jj++) {
                icv[ii - 1][jj - 1] = toRGB(1, 1, 1); //was 1, 1, 255
                updateRange(ii, jj);
              }
            }
          }
        }
      }
    } //for( j=0..81 )

    // Calculate RGB Values for x and y coordinates
    // 著色 由上而下, 由左而右
    for (int j = 1; j <= N; j++) {
      int jtest = 0;
      for (int i = 1; i <= M; i++) {
        if ( (icv[i - 1][j - 1] & (255 << 16)) == (1 << 16) &&
            (icv[i][j - 1] & (255 << 16)) == 0) {
          //是黑線就++ (嚴格來說, 是RGB=1的線)
          jtest++;
        }
      }
      if (jtest == 2) {
        //x方向有兩條線
        int itest = 0;
        for (int i = 1; i <= M; i++) {
          if ( (icv[i - 1][j - 1] & (255 << 16)) == (1 << 16) &&
              (icv[i][j - 1] & (255 << 16)) == 0) {
            //再做一次一樣的測試?
            itest++;
          }
          if (itest == 1) {
            //因為由左而右, 如果itest==1 就是在兩個黑線之間, 就算RGB並且填值
            //==================================================================
            // XYZ to RGB
            //==================================================================
            float Xc = (float) i / (float) M;
            float Yc = 1.f - (float) j / (float) N;
            float Zc = 1.f - (Xc + Yc);
            float[] RGB = XYZ2RGB(Xc, Yc, Zc);
            float R = RGB[0];
            float G = RGB[1];
            float B = RGB[2];
            float RMAX = 0.0000000001f;
            if (R > RMAX) {
              RMAX = R;
            }
            if (G > RMAX) {
              RMAX = G;
            }
            if (B > RMAX) {
              RMAX = B;
            }
            //==================================================================

            //==================================================================
            // RGB生成
            //==================================================================
            int ired = (int) (255. * Math.pow(R / RMAX, GAMMA));
            int igreen = (int) (255. * Math.pow(G / RMAX, GAMMA));
            int iblue = (int) (255. * Math.pow(B / RMAX, GAMMA));
            icv[i - 1][j - 1] = toRGB(ired, igreen, iblue);
            //==================================================================
          }
        }
      }
    }

    return icv;
  }

  /**
   * 將RGB以int表示
   * @param red int
   * @param green int
   * @param blue int
   * @return int
   */
  private final static int toRGB(int red, int green, int blue) {
    return ( (255 << 24) | (red << 16) | (green << 8) | blue);
  }

  private void updateRange(int i, int j) {
    // The values passed are 1 bigger, so decrement
    i--;
    j--;
    if (range[i][0] > j) {
      range[i][0] = (short) j; // new min
    }
    if (range[i][1] < j) {
      range[i][1] = (short) j; // new max
    }
  }

  /**
   * Checks if a point belongs to the chromaticity diagram.
   * @param i the absciss pixel number [0,m]
   * @param j the ordinate pixel number [0,n]
   * @return true if pixel belongs to the chromaticity diagram;
   * false otherwise.
   */
  public boolean isInRange(int i, int j) {
    if (i < 0 || i >= M) {
      return false;
    }
    if (j < range[i][0] || j > range[i][1]) {
      return false;
    }
    return true;
  }

  private final static float[] XYZ2RGB(float Xc, float Yc, float Zc,
                                       RGB.ColorSpace rgbColorSpace) {
    double[] RGBValues = DeviceDependentSpace.XYZ2LinearRGBValues(new double[] {
        Xc, Yc, Zc}, rgbColorSpace);
    RGBValues = RGB.rationalize(RGBValues, RGB.MaxValue.Double1);
    return DoubleArray.toFloatArray(RGBValues);
  }

  private final static float[] XYZ2RGB(float Xc, float Yc, float Zc) {
    return XYZ2RGB(Xc, Yc, Zc, colorSpace);
  }

  /**
   * This class contains the chromaticity coordinates of spectral stimuli.
   * (x,y) only; z = 1 - x - y.
   */
  protected static class ChromaticityData {

    /**
     * Array containing chromaticity coordinates at 5-nm intervals.
     */
    public XY wxy[];

    public ChromaticityData(XY wxy[]) {
      this.wxy = wxy;
    }

    static ChromaticityData getInstanceFromSpectra() {
      int startWavelength = 360;
      int endWavelength = 830;
      int size = endWavelength - startWavelength + 1;
      XY wxy[] = new XY[size + 1];
      for (int x = startWavelength; x <= endWavelength; x++) {
        Spectra s = LocusPlot.getSpectra(x);
        double[] xyValues = s.getXYZ().getxyValues();
        wxy[x - startWavelength] =
            new XY( (float) xyValues[0], (float) xyValues[1]);

      }
      wxy[size] = wxy[0];
      ChromaticityData cd = new ChromaticityData(wxy);
      return cd;
    }

    /**
     * Constructs the chromaticity coordinates of spectral stimuli.
     * 馬蹄圖外圍的色度座標(?)
     */
    public ChromaticityData() {
      wxy = new XY[66];

      wxy[0] = new XY(0.1741f, 0.0050f); //左下角, y逼近0
      wxy[1] = new XY(0.1740f, 0.0050f);
      wxy[2] = new XY(0.1738f, 0.0049f);
      wxy[3] = new XY(0.1736f, 0.0049f);
      wxy[4] = new XY(0.1733f, 0.0048f);
      wxy[5] = new XY(0.1730f, 0.0048f);
      wxy[6] = new XY(0.1726f, 0.0048f);
      wxy[7] = new XY(0.1721f, 0.0048f);
      wxy[8] = new XY(0.1714f, 0.0051f);
      wxy[9] = new XY(0.1703f, 0.0058f);
      wxy[10] = new XY(0.1689f, 0.0069f);
      wxy[11] = new XY(0.1669f, 0.0086f);
      wxy[12] = new XY(0.1644f, 0.0109f);
      wxy[13] = new XY(0.1611f, 0.0138f);
      wxy[14] = new XY(0.1566f, 0.0177f);
      wxy[15] = new XY(0.1510f, 0.0227f);
      wxy[16] = new XY(0.1440f, 0.0297f);
      wxy[17] = new XY(0.1355f, 0.0399f);
      wxy[18] = new XY(0.1241f, 0.0578f);
      wxy[19] = new XY(0.1096f, 0.0868f);
      wxy[20] = new XY(0.0913f, 0.1327f);
      wxy[21] = new XY(0.0687f, 0.2007f);
      wxy[22] = new XY(0.0454f, 0.2950f);
      wxy[23] = new XY(0.0235f, 0.4127f);
      wxy[24] = new XY(0.0082f, 0.5384f);
      wxy[25] = new XY(0.0039f, 0.6548f); //x逼近0, 鄰接y axis
      wxy[26] = new XY(0.0139f, 0.7502f);
      wxy[27] = new XY(0.0389f, 0.8120f);
      wxy[28] = new XY(0.0743f, 0.8338f); //y最大值
      wxy[29] = new XY(0.1142f, 0.8262f);
      wxy[30] = new XY(0.1547f, 0.8059f);
      wxy[31] = new XY(0.1929f, 0.7816f);
      wxy[32] = new XY(0.2296f, 0.7543f);
      wxy[33] = new XY(0.2658f, 0.7243f);
      wxy[34] = new XY(0.3016f, 0.6923f);
      wxy[35] = new XY(0.3373f, 0.6589f);
      wxy[36] = new XY(0.3731f, 0.6245f);
      wxy[37] = new XY(0.4087f, 0.5896f);
      wxy[38] = new XY(0.4441f, 0.5547f);
      wxy[39] = new XY(0.4788f, 0.5202f);
      wxy[40] = new XY(0.5125f, 0.4866f);
      wxy[41] = new XY(0.5448f, 0.4544f);
      wxy[42] = new XY(0.5752f, 0.4242f);
      wxy[43] = new XY(0.6029f, 0.3965f);
      wxy[44] = new XY(0.6270f, 0.3725f);
      wxy[45] = new XY(0.6482f, 0.3514f);
      wxy[46] = new XY(0.6658f, 0.3340f);
      wxy[47] = new XY(0.6801f, 0.3197f);
      wxy[48] = new XY(0.6915f, 0.3083f);
      wxy[49] = new XY(0.7006f, 0.2993f);
      wxy[50] = new XY(0.7079f, 0.2920f);
      wxy[51] = new XY(0.7140f, 0.2859f);
      wxy[52] = new XY(0.7190f, 0.2809f);
      wxy[53] = new XY(0.7230f, 0.2770f);
      wxy[54] = new XY(0.7260f, 0.2740f);
      wxy[55] = new XY(0.7283f, 0.2717f);
      wxy[56] = new XY(0.7300f, 0.2700f);
      wxy[57] = new XY(0.7311f, 0.2689f);
      wxy[58] = new XY(0.7320f, 0.2680f);
      wxy[59] = new XY(0.7327f, 0.2673f);
      wxy[60] = new XY(0.7334f, 0.2666f);
      wxy[61] = new XY(0.7340f, 0.2660f); //600下為轉折
      wxy[62] = new XY(0.7344f, 0.2656f);
      wxy[63] = new XY(0.7346f, 0.2654f);
      wxy[64] = new XY(0.7347f, 0.2653f); //x最大值
//      wxy[65] = new XY(0.7347f, 0.2653f);
//      wxy[66] = new XY(0.7347f, 0.2653f);
//      wxy[67] = new XY(0.7347f, 0.2653f);
//      wxy[68] = new XY(0.7347f, 0.2653f);
//      wxy[69] = new XY(0.7347f, 0.2653f);
//      wxy[70] = new XY(0.7347f, 0.2653f);
//      wxy[71] = new XY(0.7347f, 0.2653f);
//      wxy[72] = new XY(0.7347f, 0.2653f);
//      wxy[73] = new XY(0.7347f, 0.2653f);
//      wxy[74] = new XY(0.7347f, 0.2653f);
//      wxy[75] = new XY(0.7347f, 0.2653f);
//      wxy[76] = new XY(0.7347f, 0.2653f);
//      wxy[77] = new XY(0.7347f, 0.2653f);
//      wxy[78] = new XY(0.7347f, 0.2653f);
//      wxy[79] = new XY(0.7347f, 0.2653f);
//      wxy[80] = new XY(0.7347f, 0.2653f);
//      wxy[81] = new XY(0.1741f, 0.0050f);
      wxy[65] = new XY(0.1741f, 0.0050f);

    }

  }

  /**
   * This class implements a pair of floating-point coordinates.
   */
  protected static class XY {

    /**
     * x - coordinate
     */
    protected float x;

    /**
     * y - coordinate
     */
    protected float y;

    /**
     * Constructs a coordinate pair.
     * @param x float x coordinates
     * @param y float y coordinates
     */
    public XY(float x, float y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Converts the coordinate pair to string.
     * @return String
     */
    public String toString() {
      return ("[XY: " + x + ", " + y + " ]");
    }
  }

}
