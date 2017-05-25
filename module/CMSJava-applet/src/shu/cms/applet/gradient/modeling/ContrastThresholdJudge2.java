package shu.cms.applet.gradient.modeling;

import java.io.*;

import java.awt.event.*;

import jxl.read.biff.*;
import jxl.write.*;
import shu.cms.applet.gradient.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.experiment.*;
//import vv.cms.measure.cp.*;
import shu.util.log.*;
import shu.io.files.ExcelFile;
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
 * @deprecated2222222222222
 */
public class ContrastThresholdJudge2 extends JudgeBase implements
        ActionListener {
    protected DICOMCalibrator calibrator;
    protected GradientShowerFrame frame;
    protected AnswerDialog d;
//  protected double deltajndi = 0;
    protected double actualDeltajndi = 0;
//  protected double jndi;
    protected Parameter p;
    protected ExcelFile excelFile;
    protected int excelIndex = 0;

    public static class Parameter {
        double jndiStart = 40;
        double jndiInterval = 10;
        double jndiEnd = 500;
        double startDeltajndi = 0.2;
        double deltajndiInterval = 0.1;
        double maxcode = 254;
        double endCode;
        boolean inverse = false;
        boolean center = false;
    }


    public ContrastThresholdJudge2(LCDTarget lcdTarget, Parameter p) {
        this.calibrator = new DICOMCalibrator(lcdTarget);
        this.p = p;
        initUI();
        this.setJNDI(p.jndiStart);
        this.setDeltaJNDI(p.startDeltajndi);
        initRecord();
        loadCPCode();
        frame.setVisible(true);
    }

    protected static enum Answer {
        CannotSee, Accept, Unaccept, Again
    }


    //�P�_�O�_�I�LAccept
    protected boolean accepted = false;

    protected void next(Answer answer) {
        try {
            switch (answer) {
            case CannotSee:

                //�ݤ���N�W�[delta jndi
                this.addDeltaJNDI();
                break;
            case Accept:
                if (!accepted) {
                    //�Хܤw�g��F�Ĥ@���i�H�������{��
                    accepted = true;
                    //�O���U��
                    excelFile.setCell(1, excelIndex, actualDeltajndi);
                    excelFile.setCell(3, excelIndex, this.getDeltaJNDI());
                }

                //�W�[delta jndi
                this.addDeltaJNDI();
                break;
            case Unaccept:
                if (!accepted) {
                    return;
                }

                //�O���U��
                excelFile.setCell(2, excelIndex, actualDeltajndi);
                excelFile.setCell(4, excelIndex, this.getDeltaJNDI());
                excelIndex++;

                //�W�[jndi
                this.addJNDI();
                excelFile.setCell(0, excelIndex, this.getJNDI());

                //delta jndi�k�s
                resetDeltaJNDI();
                accepted = false;

                break;
            case Again:

                //delta jndi�k�s
                resetDeltaJNDI();
                accepted = false;
                break;
            }
        } catch (WriteException ex) {
            Logger.log.error("", ex);
        }

        if (this.getJNDI() <= p.jndiEnd) {
            loadCPCode();
        } else {
            this.closeRecord();
            d.setVisible(false);
        }
    }

    protected void loadCPCode() {
        calibrator.produceExperimentTarget(RGBBase.Channel.W,
                                           RGB.MaxValue.Int12Bit,
                                           p.maxcode, this.getJNDI(),
                                           this.getDeltaJNDI());
        actualDeltajndi = calibrator.getExperimentDeltaJNDI();
        //�]�w���վ�delta jndi��code��dialog�h���
        d.setCode(calibrator.getFirstAdjustIndex());
        calibrator.storeExperimentTargetAsExcel("judge.xls");
        Thread t = new Thread() {
            public void run() {
                frame.setToolBarVisible(false);
                frame.setChannel(false, false, false);
                d.setVisible(false);
//                CPCodeLoader.load("judge.xls", RGB.MaxValue.Int12Bit);
                d.setVisible(true);
                frame.setChannel(true, true, true);
                frame.setToolBarVisible(true);
            }
        };
        t.start();
    }

    protected void addJNDI() {
        this.addJNDI(p.jndiInterval);
    }

    protected void addDeltaJNDI() {
        this.addDeltaJNDI(p.deltajndiInterval);
    }

    protected void resetDeltaJNDI() {
        this.setDeltaJNDI(p.startDeltajndi);
    }

    protected void initUI() {
        frame = new GradientShowerFrame(true);
        frame.setInverse(p.inverse);
        d = new AnswerDialog(frame, "", false);
        d.setLocation(frame.getWidth() - d.getWidth(),
                      frame.getHeight() - d.getHeight());
        d.setActionListener(this);
        d.setVisible(true);
    }

    public final static LCDTarget getLCDTarget() {
        InputStream is = ContrastThresholdJudge2.class.getResourceAsStream(
                "1021.logo");
        LogoFileAdapter adapter = new LogoFileAdapter(new InputStreamReader(is));
        LCDTarget target = LCDTarget.Instance.get(adapter);
        return target;
    }

    public static void main(String[] args) {
        LCDTarget target = getLCDTarget();
        Parameter p = new Parameter();
        DICOMCalibrator calibrator = new DICOMCalibrator(target);
        calibrator.produceJNDILUT(254);

        if (args.length != 6) {
            System.out.println(
                    "Usage: ContrastThresholdJudge2 JNDI_Start JNDI_Interval Delta_JNDI_Interval End_Code Max_Code Inverse[T|F]");
            System.out.println("JNDI_Start");
            System.out.println("\t����_�l��JNDI.\n");
            System.out.println("JNDI_Interval");
            System.out.println("\t����JNDI�����j.\n");
            System.out.println("Delta_JNDI_Interval");
            System.out.println("\t�W�[��Delta JNDI�q.\n");
            System.out.println("End_Code");
            System.out.println("\t�i����窺�̫�@��code(��lcode).\n");
            System.out.println("Max_Code");
            System.out.println("\t����ɱĥΪ��̤jcode(�p������h��254).\n");
            System.out.println("Inverse");
            System.out.println("\t���h�O�_���\n");

            System.out.println("minJNDI(code 0): " + calibrator.getJNDI(0));
            System.out.println("maxJNDI(code 254): " + calibrator.getJNDI(254));
            return;
        }

        p.jndiStart = Double.parseDouble(args[0]);
        p.jndiInterval = Double.parseDouble(args[1]);
        p.deltajndiInterval = Double.parseDouble(args[2]);
//    p.jndiEnd = Double.parseDouble(args[2]);
        p.endCode = Double.parseDouble(args[3]);
        p.jndiEnd = calibrator.getJNDI(p.endCode);
        p.maxcode = Double.parseDouble(args[4]);
        if (!args[5].equals("T") && !args[5].equals("F")) {
            throw new IllegalArgumentException("Inverse != T or F");
        }

        p.inverse = args[5].equals("T");

        ContrastThresholdJudge2 judge = new ContrastThresholdJudge2(target, p);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("�L�k����")) {
            next(Answer.CannotSee);
        } else if ("�i����".equals(cmd)) {
            next(Answer.Accept);
        } else if ("���i����".equals(cmd)) {
            next(Answer.Unaccept);
        } else if ("����".equals(cmd)) {
            next(Answer.Again);
        }
    }

    protected void initRecord() {
        try {
            excelFile = new ExcelFile(Long.toString(System.currentTimeMillis()) +
                                      ".xls", true);
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
            excelFile.setCell(10, 3, p.inverse ? "Yes" : "No");
        } catch (IOException ex) {
            Logger.log.error("", ex);
        } catch (BiffException ex) {
            Logger.log.error("", ex);
        } catch (WriteException ex) {
            Logger.log.error("", ex);
        }

    }

    protected void closeRecord() {
        try {
            excelFile.close();
        } catch (IOException ex) {
            Logger.log.error("", ex);
        } catch (WriteException ex) {
            Logger.log.error("", ex);
        }
    }

    protected boolean doInverse = false;
//  private final static boolean USE_ACTUAL_JNDI = false;
}
