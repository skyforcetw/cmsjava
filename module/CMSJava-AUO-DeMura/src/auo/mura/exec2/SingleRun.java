package auo.mura.exec2;

import auo.mura.exec.*;

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
public class SingleRun {

    static void exec(String dir, String tablefilename, int graylevel) {
        CremoVisionColorDMCExecuter.main(new String[] {tablefilename, "-" + (graylevel), "hw2",
                                         dir + (graylevel) + "+.bmp"});
    }

    public static void main(String[] args) {
        String parentdir = "D:/ณnล้/nobody zone/DMC Experiment/20140627_Color_De-Mura/Default_DG_table/";
//        String dir = "D:/ณnล้/nobody zone/DMC Experiment/20140627_Color_De-Mura/Default_DG_table/100 304 712/";
//        String dir = parentdir + "/100 304 712/";
        String dir = parentdir + "/##100 136 448/";
//        String dir = parentdir + "/##100 136 448/";
//        String dir = parentdir + "/##100 448 1020=0/";
//        String dir = parentdir + "/##512 712 1020=0/";
        String tablefilename = dir + "20140627--0004_5_data.csv";
//        exec(dir, tablefilename, 34);
//        exec(dir, tablefilename, 50);
//        exec(dir, tablefilename, 86);
        exec(dir, tablefilename, 178);
    }
}
