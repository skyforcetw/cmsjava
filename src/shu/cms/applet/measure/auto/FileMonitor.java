package shu.cms.applet.measure.auto;

import java.io.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 監控檔案狀態的類別,是一個執行緒.
 * 獨立運作於PatchShower之外,需要回呼時,
 * 透過CallBack介面回傳需要資訊.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class FileMonitor
    extends Thread {

  public FileMonitor(File file, CallBack callback) {
    lastModified = failed = count = 0;
    this.monitorFile = file;
    this.callback = callback;
  }

  CallBack callback;
  File monitorFile;
  static long lastModified = 0;
  static int count = 0;
  static int failed = 0;
  volatile boolean goFlag = true;
  boolean debugMode = false;
  static int INTERVAL = 10;
  static int FILE_SIZE_THRESHOLD = 1024;
  public volatile boolean stop;

  protected void monitoring() {

    if (lastModified == 0) {
      lastModified = monitorFile.lastModified();
    }
    else if (monitorFile.length() > FILE_SIZE_THRESHOLD &&
             monitorFile.lastModified() > lastModified) {
      lastModified = monitorFile.lastModified();
//      System.out.print(".");
      try {
        callback.callback();
        count++;
      }
      catch (FileNotFoundException ex) {
        ex.printStackTrace();
        failed++;
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }

    }
  }

  public void run() {
    try {
      while (true) {
        if (stop) {
          return;
        }

        sleep(INTERVAL);
        monitoring();
        yield();

        //取消暫停功能
        synchronized (this) {
          while (!goFlag) {
            wait();
          }
        }
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }

  public void pause() {
    goFlag = false;
  }

  public void reset() {
    lastModified = count = failed = 0;
  }

}
