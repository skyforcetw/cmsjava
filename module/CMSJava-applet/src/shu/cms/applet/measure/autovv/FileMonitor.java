package shu.cms.applet.measure.autovv;

import java.io.*;

import shu.cms.applet.measure.auto.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �ʱ��ɮת��A�����O,�O�@�Ӱ����.
 * �W�߹B�@��PatchShower���~,�ݭn�^�I��,
 * �z�LCallBack�����^�ǻݭn��T.
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

  public static enum MonitorType {
    Normal, Training, Measuring
  }

  public FileMonitor(File file, CallBack callback) {
    lastModified = failed = count = 0;
    this.monitorFile = file;
    this.callback = callback;
  }

  public FileMonitor(File file, CallBack callback, MonitorType type) {
    this(file, callback);
    this.monitorType = type;
  }

  MonitorType monitorType = MonitorType.Normal;
  CallBack callback;
  File monitorFile;
  static long lastModified = 0;
  static int count = 0;
  static int failed = 0;
  static long totalTime = 0;
  static double averageTime = 0.;
  /**
   * �}�l�p��ɶ�
   */
  static boolean updateStart = false;
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

      try {
        switch (monitorType) {
          case Normal:
            lastModified = monitorFile.lastModified();
            callback.callback();
            count++;
            break;
          case Training:
            if (!updateStart && totalTime == 0) {
              //�Ĥ@�������
              updateStart = true;
            }
            else {
              totalTime += monitorFile.lastModified() - lastModified;
              count++;
            }

            lastModified = monitorFile.lastModified();
            System.out.println(totalTime + " " + count);
            break;
        }

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
          if (monitorType == MonitorType.Training) {
            averageTime = ( (double) totalTime) / count;
//            System.out.println(averageTime);
//            notify();
          }
          return;
        }

        sleep(INTERVAL);
        monitoring();
        yield();

        //�����Ȱ��\��
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

//  protected Thread trainingStopThread;

  public synchronized void trainingStop() {
    try {
      this.stop = true;
//      System.out.println("stop!");
      wait();
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  public void reset() {
    lastModified = count = failed = 0;
  }

}
