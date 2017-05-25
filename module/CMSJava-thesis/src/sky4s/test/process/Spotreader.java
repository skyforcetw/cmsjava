package sky4s.test.process;

import java.io.*;

import shu.cms.measure.meterapi.argyll.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class Spotreader {
//  public final static String ARGYLL_DIR =
//      "..\\lib\\Argyll\\bin";

  public static void main(String[] args) {
    String exec = Argyll.DIR + "\\spotread -yl";

//    String cmd = "cmd start " + exec;
    System.out.println(exec);
    Runtime rt = Runtime.getRuntime();
    try {
      Process p = rt.exec(exec);

      final InputStream is = p.getInputStream();
      final OutputStream os = p.getOutputStream();
      final InputStream es = p.getErrorStream();

      new Thread() {
        public void run() {
          while (true) {
            try {
//              if (is.available() != 0) {
//              System.out.println(is.available());
              System.out.print( (char) is.read());
//              is.reset();
//              }
            }
            catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        }
      }.start();

      new Thread() {
        public void run() {
          while (true) {
            try {
//              if (es.available() != 0) {

              System.out.print( (char) es.read());
//              es.reset();
//              }
            }
            catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        }
      }.start();

      new Thread() {
        int index = 0;
        public void run() {
//          while (true) {
          try {
            Thread.currentThread().sleep(6000);
          }
          catch (InterruptedException ex) {
            ex.printStackTrace();
          }
          try {
            System.out.println("press key ' '");
            os.flush();
//              if (index++ == 5) {
//                os.write('q');
//                os.write('q');
//              }
//              else {
            os.write(' ');
//              }

            os.flush();

          }
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
//        }
      }.start();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
