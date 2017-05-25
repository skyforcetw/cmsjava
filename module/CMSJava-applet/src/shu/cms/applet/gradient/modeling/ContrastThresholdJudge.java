package shu.cms.applet.gradient.modeling;

import java.io.*;
import java.io.File;
import java.util.*;

import java.awt.event.*;

import jxl.read.biff.*;
import jxl.write.*;
import shu.cms.applet.gradient.*;
import shu.io.files.ExcelFile;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.experiment.*;
//import vv.cms.measure.cp.*;
import shu.cms.util.*;
import shu.ui.*;
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
 * @deprecated
 */
public class ContrastThresholdJudge
    implements ActionListener, PixelSelectedListener {

  private GradientShowerFrame frame = new GradientShowerFrame(true);
  private TinyDialog.Dialog d;
  private List<CPCodeObject> list = new LinkedList<CPCodeObject> ();
  private ExcelFile excelFile;

  public ContrastThresholdJudge(String dirname) {
    frame.setPixelSelectedListener(this);
    loadExcelFiles(dirname);
    initDialog();
    initRecord();
  }

  public static class CPCodeObject {
    public CPCodeObject(RGB[] rgbArray, double[] jndiArray, String filename,
                        int index,
                        boolean inverse) {
      parseFilename(filename);
      this.rgbArray = rgbArray;
      this.jndiArray = jndiArray;
      this.index = index;
      this.inverse = inverse;
    }

    public double[] jndiArray;
    public RGB[] rgbArray;
    public String filename;
    public RGBBase.Channel ch;
    public int occur;
    public double delta;
    public int index;
    public double maxCode;
    public boolean inverse = false;

    protected void parseFilename(String filename) {
      this.filename = filename;
      switch (filename.charAt(4)) {
        case 'R':
          ch = RGBBase.Channel.R;
          break;
        case 'G':
          ch = RGBBase.Channel.G;
          break;
        case 'B':
          ch = RGBBase.Channel.B;
          break;
        case 'W':
          ch = RGBBase.Channel.W;
          break;
      }
      maxCode = Double.valueOf(filename.substring(0, 3));
      delta = Double.valueOf(filename.substring(6, 9));
      occur = Integer.valueOf(filename.substring(10, 12));
    }
  }

  protected void loadExcelFiles(String dirname) {
    File dir = new File(dirname);
    String[] filenames = dir.list();
    int size = filenames.length;
    int index = 0;

    for (int x = 0; x < size; x++) {
      String filename = filenames[x];
      if (filename.lastIndexOf(".xls") != -1) {
        String absfilename = dir.getAbsolutePath() + "\\" + filename;
        RGB[] rgbArray = null;
        try {
          rgbArray = RGBArray.loadVVExcel(absfilename);
        }
        catch (BiffException ex) {
          Logger.log.error("", ex);
        }
        catch (IOException ex) {
          Logger.log.error("", ex);
        }
        double[] jndiArray = getJNDIArray(absfilename);
        CPCodeObject obj1 = new CPCodeObject(rgbArray, jndiArray, filename,
                                             index, false);
        list.add(obj1);
        index++;
        CPCodeObject obj2 = new CPCodeObject(rgbArray, jndiArray, filename,
                                             index, true);
        list.add(obj2);
        index++;
      }
    }

  }

  protected double[] getJNDIArray(String excelfilename) {
    ExcelFile excel = null;
    try {
      excel = new ExcelFile(excelfilename);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }

    int size = excel.getRows();
    double[] jndiArray = new double[size - 1];
    for (int x = 0; x < size - 1; x++) {
      jndiArray[x] = excel.getCell(4, x);
    }
    return jndiArray;
  }

  protected void initDialog() {
    d = TinyDialog.getStartDialogInstance(frame, "@", this);
    d.setLocation(frame.getWidth() - d.getWidth(),
                  frame.getHeight() - d.getHeight());
    d.setVisible(true);
  }

  public static void main(String[] args) {
//    String device = "cpt_370WF02";
//    String tag = "";
//    String dirtag = "1126";
//    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
//    LCDTarget.Source source = LCDTarget.Source.CA210;
//
//    LCDTarget target = LCDTarget.Instance.get(device,
//                                              source,
//                                              LCDTarget.Room.Dark,
//                                              LCDTarget.TargetIlluminant.
//                                              Native,
//                                              LCDTargetBase.Number.Ramp1021,
//                                              fileType,
//                                              dirtag, tag);

    new ContrastThresholdJudge("Experiment");
//    TreeSet<Integer> set = new TreeSet<Integer>();
//    set.add(1);
//    set.add(1);
//    for(int i:set) {
//      System.out.println(i);
//    }
  }

  protected void initRecord() {
    try {
      excelFile = new ExcelFile(Long.toString(System.currentTimeMillis()) +
                                ".xls", true);
      excelFile.setColumnView(0, 25);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }

  }

  protected void closeRecord() {
    try {
      excelFile.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }
  }

  protected double[] getJNDIAndDelta(int barcode, CPCodeObject obj) {
    int size = obj.jndiArray.length;
    if (barcode <= 0 || barcode == size - 1) {
      return null;
    }
    double j0 = obj.jndiArray[barcode - 1];
    double j1 = obj.jndiArray[barcode];
    double j2 = obj.jndiArray[barcode + 1];
    double d0 = j1 - j0;
    double d1 = j2 - j1;
    double delta = d1 - d0;
    double[] result = new double[] {
        j1, delta};
    return result;
  }

  protected int getBarcode(int code, int step) {
    int round = (int) Math.round( ( (double) code) / step);
    int result = step * round;
    double delta = Math.abs(code - result);
    if (delta > 5) {
      return -1;
    }
    else {
      return result;
    }
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e ActionEvent
   */
  public void actionPerformed(ActionEvent e) {

    if (preCPCodeObject != null) {
      int index = preCPCodeObject.index * 2;
      int occur = preCPCodeObject.occur;
      int step = DICOMCalibrator.getOccurStep(occur);
      int id = 1;
      try {
        excelFile.setCell(0, index,
                          preCPCodeObject.filename +
                          (preCPCodeObject.inverse ? " inverse" : ""));

        for (int code : okSet) {
          int barcode = getBarcode(code, step);
          double[] jndind = getJNDIAndDelta(barcode, preCPCodeObject);
          if (jndind == null) {
            continue;
          }
          excelFile.setCell(id, index, jndind[0]);
          excelFile.setCell(id, index + 1, jndind[1]);
          id++;
        }

        excelFile.setCell(id, index, "ok<>non");
        id++;

        for (int code : nonokSet) {
          int barcode = getBarcode(code, step);
          double[] jndind = getJNDIAndDelta(barcode, preCPCodeObject);
          if (jndind == null) {
            continue;
          }
          excelFile.setCell(id, index, jndind[0]);
          excelFile.setCell(id, index + 1, jndind[1]);
          id++;
        }
      }
      catch (WriteException ex) {
        Logger.log.error("", ex);
      }

    }

    int size = list.size();
    if (list.size() == 0) {
      d.setEnabled(false);
      closeRecord();
      return;
    }

    okSet = new TreeSet<Integer> ();
    nonokSet = new TreeSet<Integer> ();

    int index = (int) (Math.random() * size);
    CPCodeObject obj = list.get(index);
    preCPCodeObject = obj;
    list.remove(obj);
//    CPCodeLoader.load(obj.rgbArray, RGB.MaxValue.Int12Bit);
    boolean R = (obj.ch == RGBBase.Channel.R || obj.ch == RGBBase.Channel.W);
    boolean G = (obj.ch == RGBBase.Channel.G || obj.ch == RGBBase.Channel.W);
    boolean B = (obj.ch == RGBBase.Channel.B || obj.ch == RGBBase.Channel.W);
    frame.setupImage(R, G, B, obj.inverse);
    d.setMessage(Integer.toString(obj.index + 1));
    d.pack();
  }

  private CPCodeObject preCPCodeObject;

  /**
   * actionPerformed
   *
   * @param code int
   */
  public void actionPerformed(int code) {
//    if (codeList != null) {
//      codeList.add(code);
//    }
  }

  public void actionPerformed(int code, MouseEvent e) {
    int button = e.getButton();
    if (button == 3) {
      if (okSet != null) {
        okSet.add(code);
      }
    }
    else {
      if (nonokSet != null) {
        nonokSet.add(code);
      }

    }

  }

//  private List<Integer> codeList = null;
//  private List<Integer> okCodeList = null;
//  private List<Integer> nonokCodeList = null;
  private TreeSet<Integer> okSet = null;
  private TreeSet<Integer> nonokSet = null;
}
