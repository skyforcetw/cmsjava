package shu.cms.measure.meterapi.argyll;

import java.io.*;
import java.util.*;

import shu.util.log.*;

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
public class SpotreadAPI {
  public SpotreadAPI() {
    initMeter();
  }

  public static void main(String[] args) {
    SpotreadAPI spotread = new SpotreadAPI();
    for (int x = 0; x < 10; x++) {
      double[] result = spotread.triggerMeasurement();
      System.out.println(Arrays.toString(result));
    }

//    StringBuffer buf = new StringBuffer(
//        "5416reading:3252reading:reading:reading:#@324324reading:reading:");
//    System.out.println(countHitKey(buf));
//    System.out.println(buf.toString());
  }

  private final static String exec = "java -jar " + Argyll.DIR +
      "/SpotreadWrapper.jar";
  private final static String RESULT_KEY = "Result is XYZ:";
  private final static String RESULT_END_KEY = "Place";

//  private final static String HIT_KEY = "reading:";
  private Process p;
  private SpotreadWrapper.SignalTransmitter sigCtrl;
  private InputStream is;
  private OutputStream os;
  private InputStream es;
  private StringBuffer buf = new StringBuffer();
  private Listener listener;
  private Listener errListener;
  private int receiveCount = 0;
  private boolean receiveNew = false;
  private boolean fake = false;

  protected void initMeter() {
    Runtime rt = Runtime.getRuntime();
    try {
      String cmd = exec + (fake ? " fake" : "");
      p = rt.exec(cmd);
      is = p.getInputStream();
      os = p.getOutputStream();
      es = p.getErrorStream();

      //========================================================================
      // 送出控制訊號
      //========================================================================
      sigCtrl = new SpotreadWrapper.SignalTransmitter(os);
      sigCtrl.start();
      //========================================================================

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    //==========================================================================
    // 量測的監控
    //==========================================================================
    listener = new Listener(is, false);
    listener.start();
    //==========================================================================
    //==========================================================================
    // 錯誤的監控
    //==========================================================================
    errListener = new Listener(es, true);
    errListener.start();
    //==========================================================================
  }

  protected final static int countKey(StringBuffer buf, String key) {
    int startindex = 0;
    int index = 0;
    int count = -1;
    for (; index != -1; count++) {
      index = buf.indexOf(key, startindex);
      startindex = index + 1;
    }
    return count;
  }

  private ArrayList<double[]> measureList = new ArrayList<double[]> ();

  protected String popResult() {
    int start = buf.indexOf(RESULT_KEY);
    int end = buf.indexOf(RESULT_END_KEY, start);
    String result = buf.substring(start, end).trim();
    buf.delete(0, end);
    return result;
  }

  protected double[] parseResult(String result) {
    int index = result.indexOf(RESULT_KEY);
    String measureStr = result.substring(index + RESULT_KEY.length() + 1);
    StringTokenizer tokenizer = new StringTokenizer(measureStr);
    int tokens = tokenizer.countTokens();
    if (tokens < 3) {
      return null;
    }
    double[] measure = new double[3];
    for (int x = 0; x < 3; x++) {
      String token = tokenizer.nextToken();
      int dotIndex = token.indexOf(',');
      if (dotIndex != -1) {
        token = token.substring(0, dotIndex);
      }
      measure[x] = Double.parseDouble(token);
    }
//    System.out.println(Arrays.toString(measure));
    return measure;
  }

  protected class Listener
      extends Thread {
    private InputStream is;
    private boolean errListen = false;
    protected Listener(InputStream is, Boolean errListen) {
      this.is = is;
      this.errListen = errListen;
    }

    private boolean stop = false;
    public void close() {
      stop = true;
    }

    public void run() {
      try {
        while (true) {
          int read = is.read();
          buf.append( (char) read);

          if (errListen) {
            if (read == -1) {
              sigCtrl.close();
              throw new IOException(buf.toString());
            }
          }
          else {
            int count = countKey(buf, RESULT_KEY);
            int resultIndex = buf.indexOf(RESULT_KEY);

            if (count != 0 && buf.indexOf(RESULT_END_KEY, resultIndex) != -1) {
              receiveCount = count;
              String result = popResult();
              double[] measure = parseResult(result);
              measureList.add(measure);
              receiveNew = true;
            }
          }

          if (stop) {
            break;
          }
        }
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }

  }

  public double[] triggerMeasurement() {
    receiveNew = false;
    try {
      os.write('\n');
      os.flush();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

    //==========================================================================
    // 等待結果回傳
    //==========================================================================
    while (true) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException ex) {
        Logger.log.error("", ex);
      }
      if (receiveNew) {
        break;
      }
      Thread.yield();
    }
    //==========================================================================

    double[] measure = measureList.get(0);
    measureList.remove(0);
//    float[] result = DoubleArray.toFloatArray(measure);
    return measure;
  }

  public void close() {
    if (listener != null) {
      listener.close();
    }
    if (sigCtrl != null) {
      sigCtrl.close();
    }
    if (p != null) {
      p.destroy();
    }
  }
}
