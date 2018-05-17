package shu.cms.measure.meterapi.argyll;

import java.io.*;

import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * wrapper架構圖:
 * spotread <=exec=> SpotreadWrapper    <=exec=>    caller
 *                                   <-ctr signal<-
 * 傳統架構圖:
 * spotread <=exec=> caller
 *
 * 加上SpotreadWrapper的用意在於, 為了避免在傳統架構下, 如果caller無預警的中斷後,
 * 會造成執行spotread的java.exe其cpu使用率滿載. 為了解決這樣的問題, 加入一Wrapper介入其中,
 * Wrapper需要定時接受一個控制訊號, 要是控制訊號過久沒有傳送進來, 就會判定caller已經停止.
 * 此時Wrapper會先關閉spotread對應的java.exe, 然後再自殺.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SpotreadWrapper {
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 自動發送 Control Signal的Thread
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static class SignalTransmitter
      extends Thread {
    private OutputStream os;

    public SignalTransmitter(OutputStream os) {
      this.os = os;
    }

    public void close() {
      closeFlag = true;
    }

    private boolean closeFlag = false;

    public void run() {
      try {
        while (true) {
          if (closeFlag) {
            return;
          }
          Thread.sleep(CtrlSigMaxWaitTime);
          if (closeFlag) {
            return;
          }
          os.write(CTRL_SIGNAL);
          if (closeFlag) {
            return;
          }
          os.flush();
          Thread.yield();
        }
      }
      catch (InterruptedException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }
  }

//  public final static String ARGYLL_DIR = "..\\lib\\Argyll\\bin";
  private final static String ArgyllExec = Argyll.DIR + "\\spotread -yl";
  private final static String FakeExec = "java -jar " + Argyll.DIR +
      "\\SpotreadSimulator.jar  -yl";
  public final static char CTRL_SIGNAL = '%';

  private boolean fake = false;
  public SpotreadWrapper(boolean fake) {
    this.fake = fake;
    init();
  }

  private final static long CtrlSigMaxWaitTime = 3000;

  public static void main(String[] args) {
    boolean fake = false;
    if (args.length == 1 && args[0].equals("fake")) {
      fake = true;
    }

    SpotreadWrapper wrapper = new SpotreadWrapper(fake);
//    SignalTransmitter trans = new SignalTransmitter(System.out);
//    trans.start();
  }

  private long ctrlSigRecvTime;

  protected void connectAndListener(final Process p) {
    ctrlSigRecvTime = System.currentTimeMillis();
    final InputStream ais = p.getInputStream();
    final OutputStream aos = p.getOutputStream();
    final InputStream aes = p.getErrorStream();

    final InputStream bis = System.in;
    final OutputStream bos = System.out;
    final OutputStream bes = System.err;

    Thread listener = new Thread() {
      public void run() {
        while (true) {
          try {
            Thread.sleep(CtrlSigMaxWaitTime);
            long diff = System.currentTimeMillis() - ctrlSigRecvTime;
            if (diff > CtrlSigMaxWaitTime) {
              p.destroy();
              System.exit(1);
            }
            Thread.yield();
          }
          catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
      }
    };
    listener.start();

    //==========================================================================
    // 讀console輸入 => spotread
    //==========================================================================
    Thread ist = new Thread() {
      public void run() {

        while (true) {
          try {
            int read = bis.read();
            if (read == -1) {
              return;
            }
            if (read == CTRL_SIGNAL) {
//              bis.read();
              ctrlSigRecvTime = System.currentTimeMillis();
              continue;
            }
            aos.write(read);
            aos.flush();
          }
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    };
    ist.start();
    //==========================================================================

    //==========================================================================
    // 讀spotread輸出 => console
    //==========================================================================
    Thread ost = new Thread() {
      public void run() {
        while (true) {
          try {
            int read = ais.read();
            if (read == -1) {
              return;
            }
            bos.write(read);
            bos.flush();
          }
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    };
    ost.start();
    //==========================================================================

    //==========================================================================
    // 讀spotread err => console
    //==========================================================================
    Thread est = new Thread() {
      public void run() {
        while (true) {
          try {
            int read = aes.read();
            if (read == -1) {
              return;
            }
            bes.write(read);
            bes.flush();
          }
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    };
    est.start();
    //==========================================================================
  }

  protected void init() {
    Runtime rt = Runtime.getRuntime();
    Process p = null;
    String exec = fake ? FakeExec : ArgyllExec;
    try {
      p = rt.exec(exec);
      connectAndListener(p);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
//    Thread thread = new Thread();
//    thread.start();
  }
}
