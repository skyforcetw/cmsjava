package auo.mura.etc;

import java.io.FileNotFoundException;
import java.io.IOException;

import shu.io.files.CSVFile;

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
public class PortDataRender {

    public static short[][] fourPortToOneImage(String dir, String port1filename,
                                               String port2filename,
                                               String port3filename,
                                               String port4filename) throws
            FileNotFoundException, IOException {
        CSVFile port1 = new CSVFile(dir + port1filename);
        CSVFile port2 = new CSVFile(dir + port2filename);
        CSVFile port3 = new CSVFile(dir + port3filename);
        CSVFile port4 = new CSVFile(dir + port4filename);

        int rows = port1.getRows();
        int height = rows / 480;
        int width = 1920;
        short[][] image = new short[height][width];
        int index = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 4) {

                image[h][w] = (short) Integer.parseInt(port1.getCellAsString(
                        index, 0), 16);
                image[h][w + 1]
                        = (short) Integer.parseInt(port2.getCellAsString(
                                index, 0), 16);
                image[h][w + 2]
                        = (short) Integer.parseInt(port3.getCellAsString(
                                index, 0), 16);
                image[h][w + 3]
                        = (short) Integer.parseInt(port4.getCellAsString(
                                index, 0), 16);
//                if (image[h][w] != 0) {
//                    int a = 1;
//                }

                index++;
            }
        }
        return image;
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        String dir = "D:\\My Documents\\工作\\Project\\ED\\ed_12411_data (位於 chihchehsu)\\1920x1080_8b_single\\";
        short[][] image12411 = fourPortToOneImage(dir, "12411_ed_r1_mst.txt",
                                                  "12411_ed_r2_mst.txt",
                                                  "12411_ed_r3_mst.txt",
                                                  "12411_ed_r4_mst.txt");
        dir =
                "D:\\WorkSpace\\CMSJava2\\module\\CMSJava-AUO-DeMura\\workdir\\ED 12411 Check\\";
        short[][] imageed = fourPortToOneImage(dir, "r1.txt",
                                               "r2.txt",
                                               "r3.txt",
                                               "r4.txt");
//        ArrayUtils.checkColumn1(image, 0);
        compare(image12411, imageed);
    }


    public static void compare(short[][] r0, short[][] r1) {
        int d0 = r0.length;
        int d1 = r0[0].length;
//        int index=0;
        for (int c0 = 0; c0 < d0; c0++) {
            for (int c1 = 0; c1 < d1; c1 += 1) {
//                index++;
                if (r0[c0][c1] != r1[c0][c1]) {
                    int index = (c0 * 480 + (c1 / 4) + 1);
                    int v0 = r0[c0][c1];
                    int v1 = r1[c0][c1];
                    System.out.println(c0 + ":" + c1 + " " + v0 + " " +
                                       v1 + " - " + index + " (p" + (c1 % 4 + 1) +
                                       ")");
                }

            }

        }
//        return true;
    }

    public static void compare(short[][] r0, short[][] r1,
                               CompareCallback callback) {
        int d0 = r0.length;
        int d1 = r0[0].length;
        for (int c0 = 0; c0 < d0; c0++) {
            for (int c1 = 0; c1 < d1; c1++) {

                if (r0[c0][c1] != r1[c0][c1]) {
//                System.out.println(c0 + ":" + c1 + " " + r0[c0][c1] + " " +
//                                   r1[c0][c1]);
                    callback.callback(c0, c1, r0[c0][c1], r1[c0][c1]);
                }

            }

        }
//        return true;
    }


    public interface CompareCallback {
        public void callback(int h, int w, int value1, int value2);
    }

}
