package shu.cms.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.*;

/**
 * <p>Title: CMSJava-core</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LUTManager {
    public static void main(String[] args) {
//    BufferedReader reader = new BufferedReader(new FileReader("a.lut"));
        try {
            BufferedInputStream is = new BufferedInputStream(new
                    FileInputStream("a.lut"));
//            System.out.println(is.available());

            while (is.available() != 0) {
                System.out.println(is.read());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
        }

    }
}
