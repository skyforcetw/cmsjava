package sky4s.tracking;

import shu.io.ascii.ASCIIFileFormatParser;
import shu.io.ascii.ASCIIFileFormat;
import java.io.IOException;
import shu.plot.Plot2D;
import java.util.ArrayList;

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
public class CircleTracker {

    static int[][] getTrackData(String filename) throws IOException {
        ASCIIFileFormatParser parser = new ASCIIFileFormatParser(filename);
        ASCIIFileFormat format = parser.parse();
        int size = format.size();
        int[][] data = new int[size][2];
        for (int x = 0; x < size; x++) {
            ASCIIFileFormat.LineObject lo = format.getLine(x);
            int v0 = Integer.parseInt(lo.stringArray[0]);
            int v1 = Integer.parseInt(lo.stringArray[1]);
            data[x][0] = v0;
            data[x][1] = v1;
        }
        return data;
    }

    public static void main(String[] args) throws IOException {
        int[][] trackdata = getTrackData("tracking/track1.txt");
        int tracksize = trackdata.length;
        int[] destpoint = trackdata[tracksize - 1];
        Plot2D plot = Plot2D.getInstance();
//        double[][] circledata = new double[tracksize - 1][];
        ArrayList<double[]> doubleArray = new java.util.ArrayList<double[]>();

        for (int x = 0; x < tracksize; x++) {
            int[] data = trackdata[x];
            plot.addCacheScatterLinePlot("", data[0], data[1]);
            if (x < tracksize - 1 && x % 8 == 0 && x < tracksize / 4) {
                double dist = Math.sqrt(Math.pow(destpoint[0] - data[0], 2) + Math.pow(destpoint[1] - data[1], 2));
//                circledata[x] = new double[] {data[0], data[1], dist, dist};
                doubleArray.add(new double[] {data[0], data[1], dist * 2, dist * 2});
            }
        }
        double[][] circledata = doubleArray.toArray(new double[doubleArray.size()][]);
        plot.addCirclePlot("", circledata);

        plot.setVisible();
    }
}
