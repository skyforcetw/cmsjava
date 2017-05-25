package auo.mura.exec2;

import java.io.File;
import java.io.IOException;

import shu.util.Utils;
import auo.mura.verify.*;
import auo.mura.exec.*;

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
public class PatternResultBatchProducer {

  static String pattern256L =
      "Y:/Verify Items/Limit Pattern/pattern_256level_1920x1080.bmp";
  static String pattern11L =
      "Y:/Verify Items/Limit Pattern/pattern_11level_1920x1080.bmp";
  static String pattern2v =
      "Y:/Verify Items/Limit Pattern/pattern_2vertical1920x1080.bmp";

  public static void _main(String[] args) throws IOException {
    String outputdir = "Y:/Verify Items/Simulation Result";
    String realdir = "Y:/Verify Items/Verify LUT/Real Case/No.1/1920x1080";
    String limitdir = "Y:/Verify Items/Verify LUT/Limit Case/1920x1080";

    int[][] blockCase = new int[][] {
        {
        8, 8}, {
        2, 32}, {
        4, 16}, {
        16, 4}, {
        32, 2}
    };
//    int serialNo = 4;
    String input;
    String output;

    int[][] magArray = new int[][] {
        {
        0, 0, 0}, {
        1, 1, 1}, {
        2, 2, 2}, {
        1, 1, 0}, {
        2, 2, 1}, {
        2, 1, 0}
    };
    int magindex = 0;
    int no = 2;
//2

    input = limitdir + "/" + no + ".limit2_8 8(X" + magArray[magindex][0] +
        "-" +
        magArray[magindex][1] + "-" + magArray[magindex][2] +
        ")";
    output = "Y:/Verify Items/Simulation Result/" + (no) +
        ".Limit2 8x8(X" + magArray[magindex][0] + "-" +
        magArray[magindex][1] + "-" + magArray[magindex][2] +
        ") - 256Level";
    makeOutputdir(output);
    copyData(input, output);
    MonoDMCSimulator.run(input, output, pattern256L, false);

//    for (int[] block : blockCase) {
//      //========================================================================
//      // real
//      //========================================================================
//      input = realdir + "/real_" + block[0] + "x" + block[1];
//      output = "Y:/Verify Items/Simulation Result/" + (serialNo++) + ".Real " +
//          block[0] + "x" + block[1] + " - 256Level";
//      makeOutputdir(output);
//      copyData(input, output);
//      MuraCompensationExecuter.simulate(input, output, pattern11L);
//      System.out.println(input);
//
//      for (int mag = 0; mag < 3; mag++) {
//        //========================================================================
//        // limit2
//        //========================================================================
//        input = limitdir + "/limit2_" + block[0] + " " + block[1] + "(X" + mag +
//            ")";
//        output = "Y:/Verify Items/Simulation Result/" + (serialNo++) +
//            ".Limit2 " + block[0] + "x" + block[1] + "(X" + mag +
//            ") - 11Level";
//        makeOutputdir(output);
//        copyData(input, output);
//        MuraCompensationExecuter.simulate(input, output, pattern256L);
//      }
//      for (int mag = 0; mag < 3; mag++) {
//        //========================================================================
//        // limit3
//        //========================================================================
//        input = limitdir + "/limit3_" + block[0] + " " + block[1] + "(X" + mag +
//            ")";
//        output = "Y:/Verify Items/Simulation Result/" + (serialNo++) +
//            ".Limit3 " + block[0] + "x" + block[1] + "(X" + mag +
//            ") - 2Vertical";
//        makeOutputdir(output);
//        copyData(input, output);
//        MuraCompensationExecuter.simulate(input, output, pattern2v);
//      }
//    }
  }

  public static void main(String[] args) throws IOException {
    String sourcedir = "Y:/Verify Items/Verify LUT/Limit Case/1920x1080";
    String destdir = "Y:/Verify Items/Simulation Result";
    File dir = new File(sourcedir);
//        int index = 2;
    String output = null, input = null, pattern = null;
//        for (String dirname : dir.list()) {
    for (File f : dir.listFiles()) {
      String dirname = f.getAbsolutePath();

      input = dirname;
      output = destdir + "/" + f.getName();
      String name = f.getName();
      int index = Integer.parseInt(name.substring(0, name.indexOf(".")));

      if (index >= 2 && index <= 4) {
        pattern = pattern256L;
      }
      else if (index >= 5 && index <= 7) {
        pattern = pattern2v;
      }
      else {
        if (index % 2 == 0) {
          pattern = pattern256L;
        }
        else {
          pattern = pattern2v;
        }
      }
      boolean DG = (index <= 9) ? false : true;

      makeOutputdir(output);
      copyData(input, output);
      Utils.copyFile(new File(pattern),
                     new File(output + "/pattern.bmp"));
      MonoDMCSimulator.run(input, output, pattern, DG);
      System.out.println(f.getName() + " " + pattern);

    }
  }

  static void produce() {
    String sourcedir = "Y:/Verify Items/Verify LUT/Limit Case/1920x1080";
    File dir = new File(sourcedir);
    for (String dirname : dir.list()) {
      System.out.println(dirname);
      String no = dirname.substring(0, dirname.indexOf("."));
      String limit = dirname.substring(dirname.indexOf("_") - 1,
                                       dirname.indexOf("_"));
      int indexX = dirname.indexOf("X");
      int m1 = Integer.parseInt(dirname.substring(indexX + 1, indexX + 2));
      int m2 = Integer.parseInt(dirname.substring(indexX + 3, indexX + 4));
      int m3 = Integer.parseInt(dirname.substring(indexX + 5, indexX + 6));
      System.out.println(no + " " + limit + " " + m1 + " " + m2 + " " + m3);
//          Limit l = (limit.equals("1")) ? Limit.L1 : Limit.L2;
      int[] mag = new int[] {
          m1, m2, m3};

//          String m1 = dirname.substring(dirname.substring("X")
    }
  }

  static void makeOutputdir(String output) {
    File f = new File(output);
    if (!f.exists()) {
      f.mkdir();
    }
  }

  static void copyData(String input, String output) throws IOException {
    Utils.copyFile(new File(input + "/lut.csv"),
                   new File(output + "/lut.csv"));
    Utils.copyFile(new File(input + "/par.csv"),
                   new File(output + "/par.csv"));
  }
}
