package shu.cms.dc.raw;

import java.util.*;

import shu.cms.image.*;
import shu.math.*;

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
 * @deprecated
 */
public class CFAInterpolator {
  public final static IntegerImage laplacianLinearInterpolation(IntegerImage
      im) {

    int m = im.getHeight();
    int n = im.getWidth();
//    IntegerImage im = (IntegerImage) im.clone();

    //==========================================================================
    //% G channel
    //==========================================================================
    for (int i = 3; i < m - 2; i += 2) {
      for (int j = 2; j < n - 3; j += 2) {
        int delta_H = Math.abs(im.getPixelB(j - 2, i) + im.getPixelB(j + 2, i) -
                               2 * im.getPixelB(j, i)) +
            Math.abs(im.getPixelG(j - 1, i) - im.getPixelG(j + 1, i));
        int delta_V = Math.abs(im.getPixelB(j, i - 2) + im.getPixelB(j, i + 2) -
                               2 * im.getPixelB(j, i)) +
            Math.abs(im.getPixelG(j, i - 1) - im.getPixelG(j, i + 1));
        if (delta_H < delta_V) {
          im.setPixelG(j, i, 1. / 2. *
                       (im.getPixelG(j - 1, i) + im.getPixelG(j + 1, i)) +
                       1. / 4. *
                       (2 * im.getPixelB(j, i) - im.getPixelB(j - 2, i) -
                        im.getPixelB(j + 2, i)));
        }
        else if (delta_H > delta_V) {
          im.setPixelG(j, i, 1. / 2. *
                       (im.getPixelG(j, i - 1) + im.getPixelG(j, i + 1)) +
                       1. / 4. *
                       (2 * im.getPixelB(j, i) - im.getPixelB(j, i - 2) -
                        im.getPixelB(j, i + 2)));
        }
        else {
          im.setPixelG(j, i,
                       1. / 4. *
                       (im.getPixelG(j - 1, i) + im.getPixelG(j + 1, i) +
                        im.getPixelG(j, i - 1) + im.getPixelG(j, i + 1)) +
                       1. / 8. *
                       (4 * im.getPixelB(j, i) - im.getPixelB(j - 2, i) -
                        im.getPixelB(j + 2, i) - im.getPixelB(j, i - 2) -
                        im.getPixelB(j, i + 2)));
        }
      }
    }

    for (int i = 2; i < m - 3; i += 2) {
      for (int j = 3; j < n - 2; j += 2) {
        int delta_H = Math.abs(im.getPixelR(j - 2, i) + im.getPixelR(j + 2, i) -
                               2 * im.getPixelR(j, i)) +
            Math.abs(im.getPixelG(j - 1, i) - im.getPixelG(j + 1, i));
        int delta_V = Math.abs(im.getPixelR(j, i - 2) + im.getPixelR(j, i + 2) -
                               2 * im.getPixelR(j, i)) +
            Math.abs(im.getPixelG(j, i - 1) - im.getPixelG(j, i + 1));
        if (delta_H < delta_V) {
          im.setPixelG(j, i, 1. / 2. *
                       (im.getPixelG(j - 1, i) + im.getPixelG(j + 1, i)) +
                       1. / 4. *
                       (2 * im.getPixelR(j, i) - im.getPixelR(j - 2, i) -
                        im.getPixelR(j + 2, i)));
        }
        else if (delta_H > delta_V) {
          im.setPixelG(j, i, 1. / 2. *
                       (im.getPixelG(j, i - 1) + im.getPixelG(j, i + 1)) +
                       1. / 4. *
                       (2 * im.getPixelR(j, i) - im.getPixelR(j, i - 2) -
                        im.getPixelR(j, i + 2)));
        }
        else {
          im.setPixelG(j, i,
                       1. / 4. *
                       (im.getPixelG(j - 1, i) + im.getPixelG(j + 1, i) +
                        im.getPixelG(j, i - 1) + im.getPixelG(j, i + 1)) +
                       1. / 8. *
                       (4 * im.getPixelR(j, i) - im.getPixelR(j - 2, i) -
                        im.getPixelR(j + 2, i) - im.getPixelR(j, i - 2) -
                        im.getPixelR(j, i + 2)));
        }
      }
    }
//    im.rationalize_(1);
    //==========================================================================

    //==========================================================================
    //% R channel
    //==========================================================================
    for (int i = 0; i < m - 1; i += 2) {
      for (int j = 2; j < n - 1; j += 2) {
        im.setPixelR(j, i,
                     1. / 2. *
                     (im.getPixelR(j - 1, i) + im.getPixelR(j + 1, i)) +
                     1. / 4. *
                     (2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i) -
                      im.getPixelG(j + 1, i)));
      }
    }

    for (int i = 1; i < m - 2; i += 2) {
      for (int j = 1; j < n; j += 2) {
        im.setPixelR(j, i,
                     1. / 2. *
                     (im.getPixelR(j, i - 1) + im.getPixelR(j, i + 1)) +
                     1. / 4. *
                     (2 * im.getPixelG(j, i) - im.getPixelG(j, i - 1) -
                      im.getPixelG(j, i + 1)));
      }
    }

    for (int i = 1; i < m - 2; i += 2) {
      for (int j = 2; j < n - 1; j += 2) {
        int delta_P = Math.abs(im.getPixelR(j + 1, i - 1) -
                               im.getPixelR(j - 1, i + 1)) +
            Math.abs(2 * im.getPixelG(j, i) - im.getPixelG(j + 1, i - 1) -
                     im.getPixelG(j - 1, i + 1));
        int delta_N = Math.abs(im.getPixelR(j - 1, i - 1) -
                               im.getPixelR(j + 1, i + 1)) +
            Math.abs(2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                     im.getPixelG(j + 1, i + 1));
        if (delta_N < delta_P) {
          //outR(i,j) = 1/2*(inR(i-1,j-1)+inR(i+1,j+1))+1/2*(2*outG(i,j)-outG(i-1,j-1)-outG(i+1,j+1));
          im.setPixelR(j, i, 1. / 2. *
                       (im.getPixelR(j - 1, i - 1) +
                        im.getPixelR(j + 1, i + 1)) +
                       1. / 2. *
                       (2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                        im.getPixelG(j + 1, i + 1)));
        }
        else if (delta_N > delta_P) {
          im.setPixelR(j, i, 1. / 2. *
                       (im.getPixelR(j + 1, i - 1) + im.getPixelR(j - 1, i + 1)) +
                       1. / 2. *
                       (2 * im.getPixelG(j, i) - im.getPixelG(j + 1, i - 1) -
                        im.getPixelG(j - 1, i + 1)));
        }
        else {
          im.setPixelR(j, i,
                       1. / 4. *
                       (im.getPixelR(j - 1, i - 1) + im.getPixelR(j + 1, i - 1) +
                        im.getPixelR(j - 1, i + 1) + im.getPixelR(j + 1, i + 1)) +
                       1. / 4. *
                       (4 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                        im.getPixelG(j + 1, i - 1) - im.getPixelG(j - 1, i + 1) -
                        im.getPixelG(j + 1, i + 1)));
        }

      }
    }
//    im.rationalize_(0);
    //==========================================================================

    //==========================================================================
    //% B channel
    //==========================================================================
    for (int i = 1; i < m; i += 2) {
      for (int j = 1; j < n - 2; j += 2) {
        im.setPixelB(j, i,
                     1. / 2. * (im.getPixelB(j - 1, i) + im.getPixelB(j + 1, i)) +
                     1. / 4. *
                     (2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i) -
                      im.getPixelG(j + 1, i)));
      }
    }

    for (int i = 2; i < m - 1; i += 2) {
      for (int j = 0; j < n - 1; j += 2) {
        im.setPixelB(j, i,
                     1. / 2. *
                     (im.getPixelB(j, i - 1) + im.getPixelB(j, i + 1)) +
                     1. / 4. *
                     (2 * im.getPixelG(j, i) - im.getPixelG(j, i - 1) -
                      im.getPixelG(j, i + 1)));
      }
    }

    for (int i = 2; i < m - 1; i += 2) {
      for (int j = 1; j < n - 21; j += 2) {
        int delta_P = Math.abs(im.getPixelB(j + 1, i - 1) -
                               im.getPixelB(j - 1, i + 1)) +
            Math.abs(2 * im.getPixelG(j, i) - im.getPixelG(j + 1, i - 1) -
                     im.getPixelG(j - 1, i + 1));
        int delta_N = Math.abs(im.getPixelB(j - 1, i - 1) -
                               im.getPixelB(j + 1, i + 1)) +
            Math.abs(2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                     im.getPixelG(j + 1, i + 1));
        if (delta_N < delta_P) {
          im.setPixelB(j, i, 1. / 2. *
                       (im.getPixelB(j - 1, i - 1) + im.getPixelB(j + 1, i + 1)) +
                       1. / 2. *
                       (2 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                        im.getPixelG(j + 1, i + 1)));
        }
        else if (delta_N > delta_P) {
          im.setPixelB(j, i, 1. / 2. *
                       (im.getPixelB(j + 1, i - 1) + im.getPixelB(j - 1, i + 1)) +
                       1. / 2. *
                       (2 * im.getPixelG(j, i) - im.getPixelG(j + 1, i - 1) -
                        im.getPixelG(j - 1, i + 1)));
        }
        else {
          im.setPixelB(j, i,
                       1. / 4. *
                       (im.getPixelB(j - 1, i - 1) + im.getPixelB(j + 1, i - 1) +
                        im.getPixelB(j - 1, i + 1) + im.getPixelB(j + 1, i + 1)) +
                       1. / 4. *
                       (4 * im.getPixelG(j, i) - im.getPixelG(j - 1, i - 1) -
                        im.getPixelG(j + 1, i - 1) - im.getPixelG(j - 1, i + 1) -
                        im.getPixelG(j + 1, i + 1)));
        }

      }
    }
//    im.rationalize_(2);
    //==========================================================================
    return im;
  }

  public final static IntegerImage variableNumberGradientsMethod(
      IntegerImage
      im) {
    /**
     * % Assumptions : in has following color patterns
     * %  ------------------> x
     * %  |  G R G R ...
     * %  |  B G B G ...
     * %  |  G R G R ...
     * %  |  B G B G ...
     * %  V y
     *
     * R G R G
     * G B G B
     * R G R G
     * G B G B
     */
    //==========================================================================
    // 把计非称
    //==========================================================================
    int m = im.getHeight();
    int n = im.getWidth();
//    IntegerImage out = (IntegerImage) image.clone();

    float k1 = 1.5f;
    float k2 = 0.5f;
    int[] Rave = new int[8];
    int[] Gave = new int[8];
    int[] Bave = new int[8];
    int[][] ave = new int[][] {
        Rave, Gave, Bave};
    //==========================================================================

    //==========================================================================
    // Estimate the missing color values at a non-green pixel
    // First : consider at red pixels
    //==========================================================================
    for (int i = 2; i < m - 3; i += 2) {
      for (int j = 3; j < n - 2; j += 2) {
        //form 8 gradients : N,E,S,W,NE,SE,NW,SW
        int[] gra = gradientsRB(i, j, im, 0, 2);

        // determine thresholds
        int gramax = Maths.max(gra);
        int gramin = Maths.min(gra);
        int T = Math.round(k1 * gramin + k2 * (gramax - gramin));
        int[] ind = Matlab.find(Matlab.less(gra, T));
        Arrays.fill(Rave, 0);
        Arrays.fill(Gave, 0);
        Arrays.fill(Bave, 0);
        determineThresholdsRB(i, j, im, ave, ind, 0, 2);

        int Rsum = Maths.sum(Rave);
        int Gsum = Maths.sum(Gave);
        int Bsum = Maths.sum(Bave);

        im.setPixelG(j, i, im.getPixelR(j, i) + (Gsum - Rsum) / ind.length);
        im.setPixelB(j, i, im.getPixelR(j, i) + (Bsum - Rsum) / ind.length);
      }
    }
    //==========================================================================

    //==========================================================================
    // Estimate the missing color values at a non-green pixel
    // Second : consider at blue pixels
    //==========================================================================
    for (int i = 3; i < m - 2; i += 2) {
      for (int j = 2; j < n - 3; j += 2) {
        //form 8 gradients : N,E,S,W,NE,SE,NW,SW
        int[] gra = gradientsRB(i, j, im, 2, 0);

        // determine thresholds
        int gramax = Maths.max(gra);
        int gramin = Maths.min(gra);
        int T = Math.round(k1 * gramin + k2 * (gramax - gramin));
        int[] ind = Matlab.find(Matlab.less(gra, T));
        Arrays.fill(Rave, 0);
        Arrays.fill(Gave, 0);
        Arrays.fill(Bave, 0);
        determineThresholdsRB(i, j, im, ave, ind, 2, 0);

        int Rsum = Maths.sum(Rave);
        int Gsum = Maths.sum(Gave);
        int Bsum = Maths.sum(Bave);

        im.setPixelG(j, i, im.getPixelB(j, i) + (Gsum - Bsum) / ind.length);
        im.setPixelR(j, i, im.getPixelB(j, i) + (Rsum - Bsum) / ind.length);
      }
    }
    //==========================================================================

    //==========================================================================
    // Estimating the missing color values at the green pixel location
    // First : consider those green pixels at upper-left 2x2 corner
    //==========================================================================
    for (int i = 2; i < m - 3; i += 2) {
      for (int j = 2; j < n - 3; j += 2) {
        //form 8 gradients : N,E,S,W,NE,SE,NW,SW
        int[] gra = gradientsG(i, j, im, 0, 2);

        // determine thresholds
        int gramax = Maths.max(gra);
        int gramin = Maths.min(gra);
        int T = Math.round(k1 * gramin + k2 * (gramax - gramin));
        int[] ind = Matlab.find(Matlab.less(gra, T));
        Arrays.fill(Rave, 0);
        Arrays.fill(Gave, 0);
        Arrays.fill(Bave, 0);
        determineThresholdsG(i, j, im, ave, ind, 0, 2);

        int Rsum = Maths.sum(Rave);
        int Gsum = Maths.sum(Gave);
        int Bsum = Maths.sum(Bave);

        im.setPixelR(j, i, im.getPixelG(j, i) + (Rsum - Gsum) / ind.length);
        im.setPixelB(j, i, im.getPixelG(j, i) + (Bsum - Gsum) / ind.length);
      }
    }
    //==========================================================================

    //==========================================================================
    // Estimating the missing color values at the green pixel location
    // Second : consider those green pixels at the lower-right corner
    //==========================================================================
    for (int i = 3; i < m - 2; i += 2) {
      for (int j = 3; j < n - 2; j += 2) {
        //form 8 gradients : N,E,S,W,NE,SE,NW,SW
        int[] gra = gradientsG(i, j, im, 2, 0);

        // determine thresholds
        int gramax = Maths.max(gra);
        int gramin = Maths.min(gra);
        int T = Math.round(k1 * gramin + k2 * (gramax - gramin));
        int[] ind = Matlab.find(Matlab.less(gra, T));
        Arrays.fill(Rave, 0);
        Arrays.fill(Gave, 0);
        Arrays.fill(Bave, 0);
        determineThresholdsG(i, j, im, ave, ind, 2, 0);

        int Rsum = Maths.sum(Rave);
        int Gsum = Maths.sum(Gave);
        int Bsum = Maths.sum(Bave);

        im.setPixelR(j, i, im.getPixelG(j, i) + (Rsum - Gsum) / ind.length);
        im.setPixelB(j, i, im.getPixelG(j, i) + (Bsum - Gsum) / ind.length);
      }
    }
    //==========================================================================
//    im.rationalize();
    return im;
  }

  protected final static void determineThresholdsG(int i, int j,
      IntegerImage im, int[][] ave,
      int[] ind, int ch0index,
      int ch2index) {
    for (int k = 0; k < ind.length; k++) {
      switch (ind[k]) {
        case 0:
          ave[1][0] = Math.round(1f / 2f *
                                 (im.getPixelG(j, i) + im.getPixelG(j, i - 2)));
          ave[ch2index][0] = im.getPixel(j, i - 1, ch2index);
          ave[ch0index][0] = Math.round(1f / 4f *
                                        (im.getPixel(j - 1, i - 2, ch0index) +
                                         im.getPixel(j + 1, i - 2, ch0index) +
                                         im.getPixel(j - 1, i, ch0index) +
                                         im.getPixel(j + 1, i, ch0index)));
          break;
        case 1:
          ave[1][1] = Math.round(1f / 2f *
                                 (im.getPixelG(j, i) + im.getPixelG(j + 2, i)));
          ave[ch0index][1] = im.getPixel(j + 1, i, ch0index);
          ave[ch2index][1] = Math.round(1f / 4f *
                                        (im.getPixel(j, i - 1, ch2index) +
                                         im.getPixel(j, i + 1, ch2index) +
                                         im.getPixel(j + 2, i - 1, ch2index) +
                                         im.getPixel(j + 2, i + 1, ch2index)));
          break;
        case 2:
          ave[1][2] = Math.round(1f / 2f *
                                 (im.getPixelG(j, i) + im.getPixelG(j, i + 2)));
          ave[ch2index][2] = im.getPixel(j, i + 1, ch2index);
          ave[ch0index][2] = Math.round(1f / 4f *
                                        (im.getPixel(j - 1, i, ch0index) +
                                         im.getPixel(j + 1, i, ch0index) +
                                         im.getPixel(j - 1, i + 2, ch0index) +
                                         im.getPixel(j + 1, i + 2, ch0index)));
          break;
        case 3:
          ave[1][3] = Math.round(1f / 2f *
                                 (im.getPixelG(j, i) + im.getPixelG(j - 2, i)));
          ave[ch0index][3] = im.getPixel(j - 1, i, ch0index);
          ave[ch2index][3] = Math.round(1f / 4f *
                                        (im.getPixel(j - 2, i - 1, ch2index) +
                                         im.getPixel(j, i - 1, ch2index) +
                                         im.getPixel(j - 2, i + 1, ch2index)
                                         + im.getPixel(j, i + 1, ch2index)));
          break;
        case 4:
          ave[ch0index][4] = Math.round(1f / 2f *
                                        (im.getPixel(j + 1, i - 2, ch0index) +
                                         im.getPixel(j + 1, i, ch0index)));
          ave[ch2index][4] = Math.round(1f / 2f *
                                        (im.getPixel(j, i - 1, ch2index) +
                                         im.getPixel(j + 2, i - 1, ch2index)));
          ave[1][4] = im.getPixelG(j + 1, i - 1);
          break;
        case 5:
          ave[ch0index][5] = Math.round(1f / 2f *
                                        (im.getPixel(j + 1, i, ch0index) +
                                         im.getPixel(j + 1, i + 2, ch0index)));
          ave[ch2index][5] = Math.round(1f / 2f *
                                        (im.getPixel(j, i + 1, ch2index) +
                                         im.getPixel(j + 2, i + 1, ch2index)));
          ave[1][5] = im.getPixelG(j + 1, i + 1);
          break;
        case 6:
          ave[ch0index][6] = Math.round(1f / 2f *
                                        (im.getPixel(j - 1, i, ch0index) +
                                         im.getPixel(j - 1, i - 2, ch0index)));
          ave[ch2index][6] = Math.round(1f / 2f *
                                        (im.getPixel(j - 2, i - 1, ch2index) +
                                         im.getPixel(j, i - 1, ch2index)));
          ave[1][6] = im.getPixelG(j - 1, i - 1);
          break;
        case 7:
          ave[ch0index][7] = Math.round(1f / 2f *
                                        (im.getPixel(j - 1, i, ch0index) +
                                         im.getPixel(j - 1, i + 2, ch0index)));
          ave[ch2index][7] = Math.round(1f / 2f *
                                        (im.getPixel(j - 2, i + 1, ch2index) +
                                         im.getPixel(j, i + 1, ch2index)));
          ave[1][7] = im.getPixelG(j - 1, i + 1);
          break;
      }
    }
  }

  protected final static void determineThresholdsRB(int i, int j,
      IntegerImage im, int[][] ave,
      int[] ind, int ch0Index,
      int ch2Index) {
    for (int k = 0; k < ind.length; k++) {
      switch (ind[k]) {
        case 0:
          ave[ch0Index][0] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j, i - 2, ch0Index)));
          ave[1][0] = im.getPixelG(j, i - 1);
          ave[ch2Index][0] = Math.round(1f / 2f *
                                        (im.getPixel(j - 1, i - 1, ch2Index) +
                                         im.getPixel(j + 1, i - 1, ch2Index)));
          break;
        case 1:
          ave[ch0Index][1] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j + 2, i, ch0Index)));
          ave[1][1] = im.getPixelG(j + 1, i);
          ave[ch2Index][1] = Math.round(1f / 2f *
                                        (im.getPixel(j + 1, i - 1, ch2Index) +
                                         im.getPixel(j + 1, i + 1, ch2Index)));
          break;
        case 2:
          ave[ch0Index][2] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j, i + 2, ch0Index)));
          ave[1][2] = im.getPixelG(j, i + 1);
          ave[ch2Index][2] = Math.round(1f / 2f *
                                        (im.getPixel(j - 1, i + 1, ch2Index) +
                                         im.getPixel(j + 1, i + 1, ch2Index)));
          break;
        case 3:
          ave[ch0Index][3] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j - 2, i, ch0Index)));
          ave[1][3] = im.getPixelG(j - 1, i);
          ave[ch2Index][3] = Math.round(1f / 2f *
                                        (im.getPixel(j - 1, i - 1, ch2Index) +
                                         im.getPixel(j - 1, i + 1, ch2Index)));
          break;
        case 4:
          ave[ch0Index][4] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j + 2, i - 2, ch0Index)));
          ave[1][4] = Math.round(1f / 4f *
                                 (im.getPixelG(j + 1, i) +
                                  im.getPixelG(j + 2, i - 1) +
                                  im.getPixelG(j, i - 1) +
                                  im.getPixelG(j + 1, i - 2)));
          ave[ch2Index][4] = im.getPixel(j + 1, i - 1, ch2Index);
          break;
        case 5:
          ave[ch0Index][5] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j + 2, i + 2, ch0Index)));
          ave[1][5] = Math.round(1f / 4f *
                                 (im.getPixelG(j + 1, i) +
                                  im.getPixelG(j + 2, i + 1) +
                                  im.getPixelG(j, i + 1) +
                                  im.getPixelG(j + 1, i + 2)));
          ave[ch2Index][5] = im.getPixel(j + 1, i + 1, ch2Index);
          break;
        case 6:
          ave[ch0Index][6] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j - 2, i - 2, ch0Index)));
          ave[1][6] = Math.round(1f / 4f *
                                 (im.getPixelG(j - 1, i) +
                                  im.getPixelG(j - 2, i - 1) +
                                  im.getPixelG(j, i - 1) +
                                  im.getPixelG(j - 1, i - 2)));
          ave[ch2Index][6] = im.getPixel(j - 1, i - 1, ch2Index);
          break;
        case 7:
          ave[ch0Index][7] = Math.round(1f / 2f *
                                        (im.getPixel(j, i, ch0Index) +
                                         im.getPixel(j - 2, i + 2, ch0Index)));
          ave[1][7] = Math.round(1f / 4f *
                                 (im.getPixelG(j - 1, i) +
                                  im.getPixelG(j - 2, i + 1) +
                                  im.getPixelG(j, i + 1) +
                                  im.getPixelG(j - 1, i + 2)));
          ave[ch2Index][7] = im.getPixel(j - 1, i + 1, ch2Index);
          break;
      }
    }
  }

  protected final static int[] gradientsG(int i, int j, IntegerImage im,
                                          int ch0Index, int ch2Index) {
    int gra_N = Math.round(Math.abs(im.getPixel(j, i - 1, ch2Index) -
                                    im.getPixel(j, i + 1, ch2Index)) +
                           Math.abs(im.getPixelG(j, i - 2) -
                                    im.getPixelG(j, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i - 1) -
                                    im.getPixelG(j - 1, i + 1)) +
                           1f / 2f * Math.abs(im.getPixelG(j + 1, i - 1) -
                                              im.getPixelG(j + 1, i + 1)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i - 2, ch0Index) -
                                    im.getPixel(j - 1, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i - 2, ch0Index) -
                                    im.getPixel(j + 1, i, ch0Index)));

    int gra_E = Math.round(Math.abs(im.getPixel(j + 1, i, ch0Index) -
                                    im.getPixel(j - 1, i, ch0Index)) +
                           Math.abs(im.getPixelG(j + 2, i) -
                                    im.getPixelG(j, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 1, i - 1) -
                                    im.getPixelG(j - 1, i - 1)) +
                           1f / 2f * Math.abs(im.getPixelG(j + 1, i + 1) -
                                              im.getPixelG(j - 1, i + 1)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 2, i - 1, ch2Index) -
                                    im.getPixel(j, i - 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 2, i + 1, ch2Index) -
                                    im.getPixel(j, i + 1, ch2Index)));

    int gra_S = Math.round(Math.abs(im.getPixel(j, i + 1, ch2Index) -
                                    im.getPixel(j, i - 1, ch2Index)) +
                           Math.abs(im.getPixelG(j, i + 2) -
                                    im.getPixelG(j, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 1, i + 1) -
                                    im.getPixelG(j + 1, i - 1)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i + 1) -
                                    im.getPixelG(j - 1, i - 1)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i + 2, ch0Index) -
                                    im.getPixel(j + 1, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i + 2, ch0Index) -
                                    im.getPixel(j - 1, i, ch0Index)));

    int gra_W = Math.round(Math.abs(im.getPixel(j - 1, i,
                                                ch0Index) -
                                    im.getPixel(j + 1, i, ch0Index)) +
                           Math.abs(im.getPixelG(j - 2, i) -
                                    im.getPixelG(j, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i + 1) -
                                    im.getPixelG(j + 1, i + 1)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i - 1) -
                                    im.getPixelG(j + 1, i - 1)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 2, i + 1, ch2Index) -
                                    im.getPixel(j, i + 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 2, i - 1, ch2Index) -
                                    im.getPixel(j, i - 1, ch2Index)));

    int gra_NE = Math.abs(im.getPixelG(j + 1,
                                       i - 1) -
                          im.getPixelG(j - 1, i + 1)) +
        Math.abs(im.getPixelG(j + 2, i - 2) -
                 im.getPixelG(j, i)) +
        Math.abs(im.getPixel(j + 1, i - 2, ch0Index) -
                 im.getPixel(j - 1, i, ch0Index)) +
        Math.abs(im.getPixel(j + 2, i - 1, ch2Index) -
                 im.getPixel(j, i + 1, ch2Index));

    int gra_SE = Math.abs(im.getPixelG(j + 1, i + 1) -
                          im.getPixelG(j - 1, i - 1)) +
        Math.abs(im.getPixelG(j + 2, i + 2) -
                 im.getPixelG(j, i)) +
        Math.abs(im.getPixel(j + 2, i + 1, ch2Index) -
                 im.getPixel(j, i - 1, ch2Index)) +
        Math.abs(im.getPixel(j + 1, i + 2, ch0Index) -
                 im.getPixel(j - 1, i, ch0Index));

    int gra_NW = Math.abs(im.getPixelG(j - 1, i - 1) -
                          im.getPixelG(j + 1, i + 1)) +
        Math.abs(im.getPixelG(j - 2, i - 2) -
                 im.getPixelG(j, i)) +
        Math.abs(im.getPixel(j - 1, i - 2, ch0Index) -
                 im.getPixel(j + 1, i, ch0Index)) +
        Math.abs(im.getPixel(j - 2, i - 1, ch2Index) -
                 im.getPixel(j, i + 1, ch2Index));

    int gra_SW = Math.abs(im.getPixelG(j - 1, i + 1) -
                          im.getPixelG(j + 1, i - 1)) +
        Math.abs(im.getPixelG(j - 2, i + 2) -
                 im.getPixelG(j, i)) +
        Math.abs(im.getPixel(j - 1, i + 2, ch0Index) -
                 im.getPixel(j + 1, i, ch0Index)) +
        Math.abs(im.getPixel(j - 2, i + 1, ch2Index) -
                 im.getPixelG(j, i - 1));

    int[] gra = new int[] {
        gra_N, gra_E, gra_S, gra_W, gra_NE, gra_SE, gra_NW, gra_SW};
    return gra;
  }

  protected final static int[] gradientsRB(int i, int j, IntegerImage im,
                                           int ch0Index, int ch2Index) {
    int gra_N = Math.round(Math.abs(im.getPixelG(j, i - 1) -
                                    im.getPixelG(j, i + 1)) +
                           Math.abs(im.getPixel(j, i - 2, ch0Index) -
                                    im.getPixel(j, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i - 1, ch2Index) -
                                    im.getPixel(j - 1, i + 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i - 1, ch2Index) -
                                    im.getPixel(j + 1, i + 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i - 2) -
                                    im.getPixelG(j - 1, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 1, i - 2) -
                                    im.getPixelG(j + 1, i)));

    int gra_E = Math.round(Math.abs(im.getPixelG(j + 1, i) -
                                    im.getPixelG(j - 1, i)) +
                           Math.abs(im.getPixel(j + 2, i, ch0Index) -
                                    im.getPixel(j, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i - 1, ch2Index) -
                                    im.getPixel(j - 1, i - 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i + 1, ch2Index) -
                                    im.getPixel(j - 1, i + 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 2, i - 1) -
                                    im.getPixelG(j, i - 1)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 2, i + 1) -
                                    im.getPixelG(j, i + 1)));

    int gra_S = Math.round(Math.abs(im.getPixelG(j, i + 1) -
                                    im.getPixelG(j, i - 1)) +
                           Math.abs(im.getPixel(j, i + 2, ch0Index) -
                                    im.getPixel(j, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j + 1, i + 1, ch2Index) -
                                    im.getPixel(j + 1, i - 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i + 1, ch2Index) -
                                    im.getPixel(j - 1, i - 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j + 1, i + 2) -
                                    im.getPixelG(j + 1, i)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 1, i + 2) -
                                    im.getPixelG(j - 1, i)));

    int gra_W = Math.round(Math.abs(im.getPixelG(j - 1, i) -
                                    im.getPixelG(j + 1, i)) +
                           Math.abs(im.getPixel(j - 2, i, ch0Index) -
                                    im.getPixel(j, i, ch0Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i + 1, ch2Index) -
                                    im.getPixel(j + 1, i + 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixel(j - 1, i - 1, ch2Index) -
                                    im.getPixel(j + 1, i - 1, ch2Index)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 2, i + 1) -
                                    im.getPixelG(j, i + 1)) +
                           1f / 2f *
                           Math.abs(im.getPixelG(j - 2, i - 1) -
                                    im.getPixelG(j, i - 1)));

    int gra_NE = Math.round(Math.abs(im.getPixel(j + 1, i - 1, ch2Index) -
                                     im.getPixel(j - 1, i + 1, ch2Index)) +
                            Math.abs(im.getPixel(j + 2, i - 2, ch0Index) -
                                     im.getPixel(j, i, ch0Index)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j, i - 1) -
                                     im.getPixelG(j - 1, i)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 1, i) -
                                     im.getPixelG(j, i + 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 1, i - 2) -
                                     im.getPixelG(j, i - 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 2, i - 1) -
                                     im.getPixelG(j + 1, i)));

    int gra_SE = Math.round(Math.abs(im.getPixel(j + 1, i + 1, ch2Index) -
                                     im.getPixel(j - 1, i - 1, ch2Index)) +
                            Math.abs(im.getPixel(j + 2, i + 2, ch0Index) -
                                     im.getPixel(j, i, ch0Index)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 1, i) -
                                     im.getPixelG(j, i - 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j, i + 1) -
                                     im.getPixelG(j - 1, i)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 2, i + 1) -
                                     im.getPixelG(j + 1, i)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j + 1, i + 2) -
                                     im.getPixelG(j, i + 1)));

    int gra_NW = Math.round(Math.abs(im.getPixel(j - 1, i - 1, ch2Index) -
                                     im.getPixel(j + 1, i + 1, ch2Index)) +
                            Math.abs(im.getPixel(j - 2, i - 2, ch0Index) -
                                     im.getPixel(j, i, ch0Index)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j, i - 1) -
                                     im.getPixelG(j + 1, i)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 1, i) -
                                     im.getPixelG(j, i + 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 1, i - 2) -
                                     im.getPixelG(j, i - 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 2, i - 1) -
                                     im.getPixelG(j - 1, i)));

    int gra_SW = Math.round(Math.abs(im.getPixel(j - 1, i + 1, ch2Index) -
                                     im.getPixel(j + 1, i - 1, ch2Index)) +
                            Math.abs(im.getPixel(j - 2, i + 2, ch0Index) -
                                     im.getPixel(j, i, ch0Index)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j, i + 1) -
                                     im.getPixelG(j + 1, i)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 1, i) -
                                     im.getPixelG(j, i - 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 1, i + 2) -
                                     im.getPixelG(j, i + 1)) +
                            1f / 2f *
                            Math.abs(im.getPixelG(j - 2, i + 1) -
                                     im.getPixelG(j - 1, i)));

    int[] gra = new int[] {
        gra_N, gra_E, gra_S, gra_W, gra_NE, gra_SE, gra_NW, gra_SW};
    return gra;
  }

//  public static enum Direction {
//    Upper, Lower, Left, Right
//  }

  public final static IntegerImage nearestNeighborReplication(IntegerImage im) {
    /**
     * % Assumptions : in has following color patterns
     * %  ------------------> x
     * %  |  G R G R ...
     * %  |  B G B G ...
     * %  |  G R G R ...
     * %  |  B G B G ...
     * %  V y
     *
     * R G R G
     * G B G B
     * R G R G
     * G B G B
     */
    //==========================================================================
    // 把计非称
    //==========================================================================
    int m = im.getHeight();
    int n = im.getWidth();
    //==========================================================================
    int size = n - 1;
    //% R&B channel
    for (int i = 0; i < m - 1; i += 2) {
      for (int j = 0; j < size; j += 2) {
        im.setPixelR(j, i, im.getPixelR(j + 1, i)); // (1,0) => (0,0)
        im.setPixelB(j, i, im.getPixelB(j, i + 1)); // (0,1) => (0,0)
        im.setPixelB(j + 1, i, im.getPixelB(j, i + 1)); // (0,1) => (1,0)
      }
    }
    for (int i = 1; i < m; i += 2) {
      for (int j = 0; j < size; j += 2) {
        im.setPixelR(j, i, im.getPixelR(j + 1, i - 1)); // (1,0) => (0,1)
        im.setPixelR(j + 1, i, im.getPixelR(j + 1, i - 1)); // (1,0) => (1,1)
        im.setPixelG(j, i, im.getPixelG(j, i - 1)); // (0,0) => (0,1)
        im.setPixelB(j + 1, i, im.getPixelB(j, i)); // (0,1) => (1,1)
      }
    }

    //% G channel
    for (int i = 2; i < m - 1; i += 2) {
      for (int j = 0; j < size; j += 2) {
        im.setPixelG(j + 1, i, im.getPixelG(j + 1, i - 1)); // (1,1) => (1,2)
      }
    }

    for (int i = 1; i < n; i += 2) {
      im.setPixelG(i, 0, im.getPixelG(i, 1)); // (1,1) => (1,0)
    }

    return im;
  }
}
