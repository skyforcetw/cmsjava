package auo.mura.util;

import java.io.*;

import shu.io.files.*;
import shu.math.lut.*;

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
public class DigitalGamma {
    public DigitalGamma(String calTableFilename) throws FileNotFoundException,
            IOException {
        init(calTableFilename);
    }

    public DigitalGamma() throws FileNotFoundException, IOException {
        this("Y:/Verify Items/Cal_Table01.txt");
    }

    private void init(String filename) throws FileNotFoundException,
            IOException {
        CSVFile csv = new CSVFile(filename, '\t');
//    System.out.println(csv.getRows());
        int rows = csv.getRows();
        dgTable = new double[3][rows];
        double[] input = new double[rows];

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < 3; y++) {
                double dg = csv.getCell(x, y);
//        System.out.print(csv.getCell(x, y));
                input[x] = x;
                dgTable[y][x] = dg;
            }
        }
        rlut = new Interpolation1DLUT(input, dgTable[0],
                                      Interpolation1DLUT.Algo.LINEAR);
        glut = new Interpolation1DLUT(input, dgTable[1],
                                      Interpolation1DLUT.Algo.LINEAR);
        blut = new Interpolation1DLUT(input, dgTable[2],
                                      Interpolation1DLUT.Algo.LINEAR);
//    Interpolation1DLUT lut
//    System.out.println(Arrays.toString(dgTable));
//    System.out.println(IntegerArray.toString(dgTable));
    }

    public int gammaB(double b) {
        double v = blut.getValue(b);
        return (int) Math.floor(v);
    }

    public int gammaG(double g) {
        double v = glut.getValue(g);
        return (int) Math.floor(v);
    }

    public int gammaR(double r) {
        double v = rlut.getValue(r);
        return (int) Math.floor(v);
    }

    public int gammaR12Bit(int r12bit) {
        double r = r12bit / 16.;
        return gammaR(r);
    }

    public int gammaG12Bit(int g12bit) {
        double g = g12bit / 16.;
        return gammaG(g);
    }

    public int gammaB12Bit(int b12bit) {
        double b = b12bit / 16.;
        return gammaB(b);
    }

    private double[][] dgTable;
    private Interpolation1DLUT rlut;
    private Interpolation1DLUT glut;
    private Interpolation1DLUT blut;

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        DigitalGamma dg = new DigitalGamma("demura/Cal_Table00.txt");
        System.out.println(dg.gammaR(255.75));
        System.out.println(dg.gammaG(255.75));
        System.out.println(dg.gammaB(255.75));
        System.out.println(dg.gammaR12Bit(4092));
        System.out.println(dg.gammaG12Bit(4092));
        System.out.println(dg.gammaB12Bit(4092));
    }
}
