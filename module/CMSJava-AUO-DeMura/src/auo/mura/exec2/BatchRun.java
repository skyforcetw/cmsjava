package auo.mura.exec2;

import java.io.File;
import java.util.StringTokenizer;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.IOException;
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
public class BatchRun {
    static class CSVFilter implements FileFilter {
//        public boolean accept(File dir, String name) {
//            return name.indexOf(".csv") != -1;
//        }
        File newestFile;
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return name.indexOf(".csv") != -1 && name.indexOf("5_data") != -1;

//            if (null != newestFile) {
//                if (newestFile.lastModified() < pathname.lastModified()) {
//                    newestFile = pathname;
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                newestFile = pathname;
//                return true;
//            }

        }

    }


    public static void main(String[] args) throws IOException {
//        String parentdir = "DMC/executer/colordmc/20140627_Color_De-Mura/";
        String parentdir = "D:/ณnล้/nobody zone/DMC Experiment/20140627_Color_De-Mura/";
//        String[] dirs = {"CCT10000_table", "CCT15000_Table", "CCT6500_table"};
        String[] dirs = {"test"};
        for (String dir : dirs) {
            String targetdir = parentdir + "/" + dir;
            main(targetdir);
        }
    }

    public static void main(String targetdirname) throws IOException {
//    int[] grayLevel = {40 ,100 ,128,  ,304, 712
//        File targetdir = new java.io.File("DMC/executer/colordmc/20140627_Color_De-Mura/Default_DG_table/");
        File targetdir = new File(targetdirname);
        for (File dir : targetdir.listFiles()) {
            File file = dir.listFiles(new CSVFilter())[0];
            String tablefilename = file.getCanonicalPath();
            String ed = "hw2";

            StringTokenizer token = new java.util.StringTokenizer(dir.getName(), " _");
            while (token.hasMoreTokens()) {
                int graylevel = Integer.parseInt(token.nextToken()) / 4;
                String input = "-" + graylevel;
                String outputfilename = dir.getCanonicalPath() + "\\" + graylevel + ".bmp";
                String[] execargs = {tablefilename, input, ed, outputfilename};
                CremoVisionColorDMCExecuter.main(execargs);
                System.gc();
            }
//            for(String t:token.nextToken()) {
//
//            }
        }

    }
}
