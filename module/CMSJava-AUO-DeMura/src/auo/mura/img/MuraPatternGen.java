package auo.mura.img;

import java.util.Random;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MuraPatternGen {
    public static short[][][] getRandomMuraPattern(short grayLevel, int height, int width,
            short maxMuraHeight) {
        Random r = new Random(12345678L);
        int muracount = r.nextInt(20);
        return getRandomMuraPattern(grayLevel, height, width, maxMuraHeight, muracount, 0);
    }

    public static short[][][] getRandomMuraPattern(short grayLevel, int height, int width,
            short maxMuraHeight, int muraCount, int maxMuraRadius) {
        int area = height * width;
        Random r = new Random(12345678L);
//        int muracount = r.nextInt(20);
        int smallLength = height < width ? height : width;
        maxMuraRadius = (maxMuraRadius == 0) ? smallLength : maxMuraRadius;

        short[][][] result = new short[3][height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[0][h][w] = grayLevel;
            }
        }

        final short maxMuraGrayLevel = (short) (grayLevel + maxMuraHeight);

        for (int x = 0; x < muraCount; x++) {
            int murapos = r.nextInt(area);
            int centerh = murapos / width;
            int centerw = murapos % width;
            int muraradius = r.nextInt(maxMuraRadius);
            int murahiehgt = r.nextInt(maxMuraHeight);

            int starth = centerh - muraradius;
            int startw = centerw - muraradius;
            starth = starth < 0 ? 0 : starth;
            startw = startw < 0 ? 0 : startw;
            int endh = centerh + muraradius;
            int endw = centerw + muraradius;
            endh = endh > height ? height : endh;
            endw = endw > width ? width : endw;

            double[] tinySquare = polar2cartesianCoordinatesValues(muraradius, 45);
            int tinySquareHalfLength = (int) Math.round(tinySquare[0]) - 1;
//            int tinystarth = centerh - tinySquareHalfLength;
//            int tinystartw = centerw - tinySquareHalfLength;
//            tinystarth = tinystarth < 0 ? 0 : tinystarth;
//            tinystartw = tinystartw < 0 ? 0 : tinystartw;
//            int tinyendh = centerh + tinySquareHalfLength;
//            int tinyendw = centerw + tinySquareHalfLength;
//            tinyendh = tinyendh > height ? height : tinyendh;
//            tinyendw = tinyendw > width ? width : tinyendw;

            for (int h = starth; h < endh; h++) {
                for (int w = startw; w < endw; w++) {

                    int hofcircle = h - centerh;
                    int wofcircle = w - centerw;
                    boolean inSquare = hofcircle > ( -tinySquareHalfLength) &&
                                       hofcircle < tinySquareHalfLength &&
                                       wofcircle > ( -tinySquareHalfLength) &&
                                       wofcircle < tinySquareHalfLength;
//                    if (hofcircle > -tinySquareHalfLength && hofcircle < tinySquareHalfLength &&
//                        wofcircle > -tinySquareHalfLength && wofcircle < tinySquareHalfLength) {
//                        continue;
//                    }


                    double radius = 0;

//                    if (inSquare ||
//                        (radius = cartesian2polarCoordinatesValues(wofcircle, hofcircle)[0]) <=
//                                  muraradius) {
//                        int error = (int) Math.round((muraradius - radius) / muraradius *
//                                murahiehgt);
//                        if ((result[0][h][w] + error) < maxMuraGrayLevel) {
//                            result[0][h][w] += error;
//                        }
//
//                    }
                    double[] polar = cartesian2polarCoordinatesValues(wofcircle, hofcircle);
                    if (polar[0] <= muraradius) { //mura circle¤º

                        int error = (int) Math.round((muraradius - polar[0]) / muraradius *
                                murahiehgt);
                        if ((result[0][h][w] + error) < maxMuraGrayLevel) {
                            result[0][h][w] += error;
                        }
                    }

                }
            }

        }
        for (int ch = 1; ch < 3; ch++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[ch][h][w] = result[0][h][w];
                }
            }

        }
        return result;
    }

    private static final double[] polar2cartesianCoordinatesValues(final double
            distance, final double angle) {

        double t = (angle * Math.PI) / 180.0;

        double[] cartesianValues = new double[2];
        cartesianValues[0] = distance * Math.cos(t);
        cartesianValues[1] = distance * Math.sin(t);

        return cartesianValues;
    }

    private static final double[] cartesian2polarCoordinatesValues(final double x,
            final double y) {
        double[] polarValues = new double[2];

        double t1 = x;
        double t2 = y;
        polarValues[0] = Math.sqrt(Math.pow(t1, 2)
                                   + Math.pow(t2, 2));
        if (t1 == 0 && t2 == 0) {
            polarValues[1] = 0;
        } else {
            polarValues[1] = Math.atan2(t2, t1);
        }
        polarValues[1] *= (180.0 / Math.PI);
        while (polarValues[1] >= 360.0) { // Not necessary, but included as a check.
            polarValues[1] -= 360.0;
        } while (polarValues[1] < 0) {
            polarValues[1] += 360.0;
        }
        return polarValues;
    }

    public static void main(String[] args) {
        int height = 40;
        int width = 1920;
        short[][][] pattern = getRandomMuraPattern((short) 64, height, width, (short) 32);
//        for (int x = 0; x < 360; x++) {
//            double[] p = polar2cartesianCoordinatesValues(100, x);
//            System.out.println(x + " " + p[0] + " " + p[1]);
//        }
    }
}
