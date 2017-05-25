package auo.mura.test;

import auo.mura.interp.ThreeDInterpolation;
import shu.math.lut.CubeTable;
import shu.math.lut.CubeTable.KeyValue;
import shu.plot.Plot2D;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LimitTester {

  /**
            Case BlackLimitPictureLevel To PlaneLevel1 - 1


                TotaldeMuraValue(Color) = deMuravalue(1, Color) _
                                            - (PlaneLevel1 - InputPicture(i, j, Color)) * PlaneB1Coef * deMuravalue(1, Color) / (2 ^ 15)


            Case PlaneLevel1 To PlaneLevel2 - 1


                TotaldeMuraValue(Color) = ((PlaneLevel2 - InputPicture(i, j, Color)) * CLng(deMuravalue(1, Color)) _
   + (InputPicture(i, j, Color) - PlaneLevel1) * CLng(deMuravalue(2, Color))) _
   * (Plane12Coef * 2 + 1) \ (2 ^ 17)


            Case PlaneLevel2 To PlaneLevel3 - 1

                TotaldeMuraValue(Color) = ((PlaneLevel3 - InputPicture(i, j, Color)) * CLng(deMuravalue(2, Color)) _
   + (InputPicture(i, j, Color) - PlaneLevel2) * CLng(deMuravalue(3, Color))) _
   * (Plane23Coef * 2 + 1) \ (2 ^ 17)



            Case PlaneLevel3 To WhiteLimitPictureLevel

                TotaldeMuraValue(Color) = deMuravalue(3, Color) _
                                            - (InputPicture(i, j, Color) - PlaneLevel3) * Plane3WCoef * deMuravalue(3, Color) \ (2 ^ 15)

   * @param args String[]
   */

  public static void main(String[] args) {
    int blackLimit = 0;
    int layer1 = 100;
    int layer2 = 204;
    int layer3 = 502;
    int layer1Value = 40;
    int layer2Value = 40;
    int layer3Value = 40;
    int whiteLimit = 1023;
    int endGrayLevel = 1023;

//    ThreeDInterpolation interp = new ThreeDInterpolation(1, 1,
//        new int[] {0,
//        layer1, layer2, layer3, endGrayLevel});
    ThreeDInterpolation interp = null;

    interp.setBlockSize(8, 8);
    interp.setOffset(new short[] {0, 0, 0});
    interp.setMagnitude(new short[] {0, 0, 0});

    ThreeDInterpolation.YagiTriLinearInterpolation3D yagi = interp.
        getYagiTriLinearInterpolation3D();
    Plot2D plot = Plot2D.getInstance(Integer.toString(blackLimit));

    for (int x = 0; x < 1024; x++) {
      KeyValue[] cell = new KeyValue[8];

      if (x < layer1) {
        for (int y = 0; y < 4; y++) {
          cell[y] = new KeyValue(new double[] {0, 0, 0}, new double[] {0});
          cell[4 + y] = new KeyValue(new double[] {0, 0, layer1},
                                     new double[] {layer1Value});
        }
      }
      else if (x < layer2) {
        for (int y = 0; y < 4; y++) {
          cell[y] = new KeyValue(new double[] {0, 0, layer1},
                                 new double[] {layer1Value});
          cell[4 +
              y] = new KeyValue(new double[] {0, 0, layer2},
                                new double[] {layer2Value});
        }
      }
      else if (x < layer3) {
        for (int y = 0; y < 4; y++) {
          cell[y] = new KeyValue(new double[] {0, 0, layer2},
                                 new double[] {layer2Value});
          cell[4 +
              y] = new KeyValue(new double[] {0, 0, layer3},
                                new double[] {layer3Value});
        }
      }
      else {
        for (int y = 0; y < 4; y++) {
          cell[y] = new KeyValue(new double[] {0, 0, layer3},
                                 new double[] {layer3Value});
          cell[4 +
              y] = new KeyValue(new double[] {0, 0, endGrayLevel},
                                new double[] {0});
        }

      }
      short[] key = {
          0, 0, (short) x};
      short[] result = yagi.interpolateValue(key, cell);
      plot.addCacheScatterLinePlot("", x, result[0]);
//      System.out.println(x + " " + (result[0] + x * 4) + " " + result[0]);
      System.out.println(x + " " + result[0]);
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 1023);
  }
}
