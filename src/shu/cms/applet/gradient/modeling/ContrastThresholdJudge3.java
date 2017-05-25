package shu.cms.applet.gradient.modeling;

import java.io.*;

import jxl.read.biff.*;
import jxl.write.*;
import shu.cms.applet.gradient.*;
import shu.cms.applet.gradient.modeling.ContrastThresholdJudge2.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.experiment.*;
import shu.cms.measure.cp.*;
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
public class ContrastThresholdJudge3
    extends ContrastThresholdJudge2 {
  /**
   * ContrastThresholdJudge3
   *
   * @param lcdTarget LCDTarget
   * @param p Parameter
   */
  public ContrastThresholdJudge3(LCDTarget lcdTarget, Parameter p) {
    super(lcdTarget, p);
  }

  protected void initUI() {
    frame = new GradientShowerFrame(true);
    d = new AnswerDialog(frame, "", false);
    d.setLocation(frame.getWidth() - d.getWidth(),
                  frame.getHeight() - d.getHeight());
    d.setActionListener(this);
    d.setVisible(true);
    this.inverseMode = p.inverse;
  }

  protected boolean inverseMode;
  protected ExcelFile excelFile2;
  protected int excelIndex2 = 0;

  protected ExcelFile getExcelFile() {
    ExcelFile excel = doInverse ? excelFile2 : excelFile;
    return excel;
  }

  protected void addExcelIndex() {
    if (doInverse) {
      excelIndex2++;
    }
    else {
      excelIndex++;
    }
  }

  protected int getExcelIndex() {
    return doInverse ? excelIndex2 : excelIndex;
  }

  protected void initExcel(ExcelFile excelFile, boolean inverse) {
    try {
//      excelFile = new ExcelFile(Long.toString(System.currentTimeMillis()) +
//                                ".xls", true);
      excelFile.setColumnView(0, 25);
      excelFile.setColumnView(7, 25);
      excelFile.setColumnView(9, 25);
      excelFile.setCell(0, excelIndex, this.getJNDI());

      excelFile.setCell(7, 0, "JNDI_Start");
      excelFile.setCell(8, 0, p.jndiStart);
      excelFile.setCell(7, 1, "JNDI_Interval");
      excelFile.setCell(8, 1, p.jndiInterval);
      excelFile.setCell(7, 2, "JNDI_End");
      excelFile.setCell(8, 2, p.jndiEnd);
      excelFile.setCell(9, 0, "Delta_JNDI_Interval");
      excelFile.setCell(10, 0, p.deltajndiInterval);
      excelFile.setCell(9, 1, "End_Code");
      excelFile.setCell(10, 1, p.endCode);
      excelFile.setCell(9, 2, "Max_Code");
      excelFile.setCell(10, 2, p.maxcode);
      excelFile.setCell(9, 3, "Inverse");
      excelFile.setCell(10, 3, inverse ? "Yes" : "No");
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }

  }

  protected void initRecord() {
    try {
      excelFile = new ExcelFile(Long.toString(System.currentTimeMillis()) +
                                ".xls", true);

      initExcel(excelFile, false);
      if (inverseMode) {
        excelFile2 = new ExcelFile(Long.toString(System.currentTimeMillis()) +
                                   "i.xls", true);
        initExcel(excelFile2, true);
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }

  protected void next(Answer answer) {
    ExcelFile excel = getExcelFile();
    try {
      switch (answer) {
        case CannotSee:

          //看不到就增加delta jndi
          addDeltaJNDI();
          break;
        case Accept:
          if (!accepted) {
            //標示已經到了第一次可以接受的程度
            accepted = true;
            //記錄下來
            excel.setCell(1, getExcelIndex(), actualDeltajndi);
            excel.setCell(3, getExcelIndex(), this.getDeltaJNDI());
            this.d.jButton_cannotsee.setEnabled(false);
          }

          //增加delta jndi
          addDeltaJNDI();
          break;
        case Unaccept:
          if (!accepted) {
            return;
          }

          //記錄下來
          excel.setCell(2, getExcelIndex(), actualDeltajndi);
          excel.setCell(4, getExcelIndex(), this.getDeltaJNDI());
          addExcelIndex();

          if (inverseMode) {
            //如果處在inverse Mode
            if (this.doInverse) {
              //已經inverse, 所以關掉inverse
              this.doInverse = false;
              //增加jndi
              addJNDI();
            }
            else {
              //還沒inverse, 所以要inverse
              this.doInverse = true;
            }
            excel = getExcelFile();
          }

          if (this.getJNDI() > p.jndiEnd) {
            this.closeRecord();
            d.setVisible(false);
          }

          //增加jndi
          excel.setCell(0, getExcelIndex(), this.getJNDI());

          //delta jndi歸零
          resetDeltaJNDI();
          accepted = false;
          this.d.jButton_cannotsee.setEnabled(true);

          break;
        case Again:

          //delta jndi歸零
          resetDeltaJNDI();
          accepted = false;
          this.d.jButton_cannotsee.setEnabled(true);
          break;
      }
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }
    if (this.getJNDI() <= p.jndiEnd) {
      loadCPCode();
      frame.setInverse(doInverse);
    }
  }

  protected void closeRecord() {
    try {
      excelFile.close();
      excelFile2.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }
  }

  public static void main(String[] args) {
    LCDTarget target = getLCDTarget();
    Parameter p = new Parameter();
    DICOMCalibrator calibrator = new DICOMCalibrator(target);
    calibrator.produceJNDILUT(254);

    if (args.length < 6) {
      System.out.println(
          "Usage: ContrastThresholdJudge2 JNDI_Start JNDI_Interval Delta_JNDI_Interval End_Code Max_Code Inverse[T|F]");
      System.out.println("JNDI_Start");
      System.out.println("\t實驗起始的JNDI.\n");
      System.out.println("JNDI_Interval");
      System.out.println("\t實驗JNDI的間隔.\n");
      System.out.println("Delta_JNDI_Interval");
      System.out.println("\t增加的Delta JNDI量.\n");
      System.out.println("End_Code");
      System.out.println("\t進行實驗的最後一個code(原始code).\n");
      System.out.println("Max_Code");
      System.out.println("\t實驗時採用的最大code(如有反轉則用254).\n");
      System.out.println("Inverse");
      System.out.println("\t漸層是否對調\n");
      System.out.println("Center");
      System.out.println("\t是否置中\n");

      System.out.println("minJNDI(code 0): " + calibrator.getJNDI(0));
      System.out.println("maxJNDI(code 254): " + calibrator.getJNDI(254));
      return;
    }

    p.jndiStart = Double.parseDouble(args[0]);
    p.jndiInterval = Double.parseDouble(args[1]);
    p.deltajndiInterval = Double.parseDouble(args[2]);
    p.endCode = Double.parseDouble(args[3]);
    p.jndiEnd = calibrator.getJNDI(p.endCode);
    p.maxcode = Double.parseDouble(args[4]);
    if (!args[5].equals("T") && !args[5].equals("F")) {
      throw new IllegalArgumentException("Inverse != T or F");
    }
    p.inverse = args[5].equals("T");
//    if (args.length >= 7) {
//      if (!args[6].equals("T") && !args[6].equals("F")) {
//        throw new IllegalArgumentException("Center != T or F");
//      }
//      p.center = args[6].equals("T");
//    }
    ContrastThresholdJudge3 judge = new ContrastThresholdJudge3(target, p);
  }

  protected void loadCPCode() {
//    if (p.center) {
//      calibrator.produceExperimentTarget(RGBBase.Channel.W, RGB.MaxValue.Int12Bit,
//                                         p.maxcode, this.getJNDI(),
//                                         this.getDeltaJNDI(), 32);
//    }
//    else {
    RGB[] result = calibrator.produceExperimentTarget(RGBBase.Channel.W,
        RGB.MaxValue.Int12Bit,
        p.maxcode, this.getJNDI(),
        this.getDeltaJNDI(), this.getPreDeltaJNDI());
//      double preDeltaJNDI = this.getDeltaJNDI();
    while (result == null) {
      this.addDeltaJNDI();
      result = calibrator.produceExperimentTarget(RGBBase.Channel.W,
                                                  RGB.MaxValue.Int12Bit,
                                                  p.maxcode, this.getJNDI(),
                                                  this.getDeltaJNDI(),
                                                  this.getPreDeltaJNDI());
    }
//    }

    actualDeltajndi = calibrator.getExperimentDeltaJNDI();
    //設定有調整delta jndi的code到dialog去顯示
    d.setCode(calibrator.getFirstAdjustIndex());
    calibrator.storeExperimentTargetAsExcel("judge.xls");
    Thread t = new Thread() {
      public void run() {
        frame.setChannel(false, false, false);
        try {
          Thread.sleep(16);
        }
        catch (InterruptedException ex) {
          Logger.log.error("", ex);
        }
        frame.setToolBarVisible(false);
        d.setVisible(false);
        CPCodeLoader.load("judge.xls", RGB.MaxValue.Int12Bit);
        d.setVisible(true);
        frame.setChannel(true, true, true);
        frame.setToolBarVisible(true);
      }
    };
    t.start();
  }

}
