package shu.cms.applet.measure.tool;

import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorformat.logo.*;
import shu.cms.lcd.*;
import shu.cms.measure.*;
//import vv.cms.measure.cp.*;
import shu.cms.measure.meter.*;
import shu.cms.ui.*;
import shu.ui.*;
import shu.util.log.*;
import shu.cms.colorspace.depend.RGBBase;
import shu.cms.colorspace.depend.DeviceDependentSpace;
//import vv.cms.measure.cp.MeasureBits;

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
public class MeasureToolFrame extends JFrame {
    private final static MeterMeasurement getMeterMeasurement(
            AppMeasureParameter mp) {
        MeterMeasurement mm = new MeterMeasurement(mp.meter, mp.dicomMode,
                mp.size, mp.calibration);
        Color bg = mm.getBackgroundColor();
        mm.setDo255InverseMode(mp.inverseMode);
        mm.setBlankAndBackground(mp.blank, bg);
        mm.setBlankTimes((int) mp.blankTimes);
        mm.setDoBlankInsert(mp.blankInsert);
        mm.setWaitTimes((int) mp.delayTimes);
        return mm;
    }

    static List<String> TargetNumberList;
    static List<String> RampTargetNumberList;
    {
        LCDTargetBase.Number[] numbers = LCDTargetBase.Number.values();
        //全部number
        TargetNumberList = new ArrayList<String>();
        //只有ramp
        RampTargetNumberList = new ArrayList<String>();

        for (LCDTargetBase.Number number : numbers) {
            String filename = number.getReferenceFilename();
            if (filename != null) {
                String item = (!number.getDescription().equals("") &&
                               number.getDescription() != null) ?
                              number.getDescription() : filename;
                TargetNumberList.add(item);
                if (number.isRamp()) {
                    RampTargetNumberList.add(item);
                }
            }
        }
    }

    protected JPanel contentPane;
    protected BorderLayout borderLayout1 = new BorderLayout();
    protected JMenuBar jMenuBar1 = new JMenuBar();
    protected JMenu jMenu_File = new JMenu();

    protected JMenuItem jMenuItem_Exit = new JMenuItem();
    protected JMenu jMenuHelp = new JMenu();
    protected JMenuItem jMenuItem_About = new JMenuItem();
    protected JToolBar jToolBar = new JToolBar();

    protected JMenu jMenu_Calibrate = new JMenu();
    protected JMenuItem jMenuItem_ComputeMatrix = new JMenuItem();
    protected JMenuItem jMenuItem_DeleteMatrix = new JMenuItem();

    protected ImageIcon image_Configuring = new ImageIcon(shu.cms.applet.
            measure.
            tool.MeasureToolFrame.class.getResource("usb.png"));
    protected ImageIcon image_Measuring = new ImageIcon(shu.cms.applet.measure.
            tool.MeasureToolFrame.class.getResource("colorset.png"));
    protected ImageIcon image_HiBitsMeasuring = new ImageIcon(shu.cms.applet.
            measure.
            tool.MeasureToolFrame.class.getResource("kcmmemory.png"));

    protected ImageIcon image_Characterize = new ImageIcon(shu.cms.applet.
            measure.tool.MeasureToolFrame.class.getResource("smserver.png"));
    protected ImageIcon image_Batch = new ImageIcon(shu.cms.applet.
            measure.tool.MeasureToolFrame.class.
            getResource("player_play.png"));

    private Binder binder = new Binder();
    Calibrator calibrator = new Calibrator(binder);

    protected JLabel statusBar = new JLabel();
    protected JMenuItem jMenuItem_Open = new JMenuItem();
    protected JMenuItem jMenuItem_SaveAs = new JMenuItem();
    protected JMenuItem jMenuItem_SaveAsVastView = new JMenuItem();
    protected JMenuItem jMenuItemClose = new JMenuItem();
    protected JMenu jMenu_Tools = new JMenu();
    protected JMenuItem jMenuItem_InstrConfig = new JMenuItem();
    protected JMenuItem jMenuItem_TestchartMeasure = new JMenuItem();
    protected JMenuItem jMenuItem_HiBitsMeasure = new JMenuItem();
    protected JMenuItem jMenuItem_BatchMeasure = new JMenuItem();
    protected JFileChooser jFileChooser;
    protected JFileChooser jFileChooserSave;
    protected JFileChooser jFileChooserSaveVastView;
    protected JButton jButton_Configuring = new JButton();
    protected JButton jButton_Measuring = new JButton();
    protected JButton jButton_HiBitsMeasuring = new JButton();
    protected JButton jButton_Batch = new JButton();
    protected JButton jButton_Chracterize = new JButton();
    protected JDesktopPane desktopPane;
    protected MDIUtils mdi;
    protected InstrumentConfigurationFrame instrConfigFrame;
    protected CharacterizeFrame characterizeFrame;

    private ConfigAction configAction = new ConfigAction("Configuring",
            image_Configuring, this);
    private MeasureAction measureAction = new MeasureAction("Measuring",
            image_Measuring, this, false);
    private MeasureAction hiBitsMeasureAction = new MeasureAction(
            "HiBits", image_HiBitsMeasuring, this, true);
    private CharacterizeAction characterizeAction = new CharacterizeAction(
            "Characterize", image_Characterize);
    private BatchAction batchAction = new BatchAction(
            "Batch", image_Batch);
    private ComputeCalibrateAction computeCalibrateAction = new
            ComputeCalibrateAction("Compute Calibration Matrix");
    private DeleteCalibrateAction deleteCalibrateAction = new
            DeleteCalibrateAction("Delete Calibrate Matrix");
    private SaveAsAction saveAsAction = new SaveAsAction("Save As");
    private SaveAsVastViewAction saveAsVastViewAction = new
            SaveAsVastViewAction(
                    "Save As VasiView");

    private List<SwingWorker> workerList = new ArrayList<SwingWorker>();
    private MeasuredInternalFrame calibrateRefFrame;

    protected class Binder extends Calibrator.ServerBinder {

        public void receive(Calibrator.Operator op, Object obj) {
            switch (op) {
            case Reference:
                boolean ref = ((Boolean) obj).booleanValue();
                computeCalibrateAction.setEnabled(ref);

                //現在選取的frame就是參考frame
                calibrateRefFrame = (MeasuredInternalFrame) desktopPane.
                                    getSelectedFrame();
                break;
            }
        }
    }


    public MeasureToolFrame() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
//    System.setProperty("awt.useSystemAAFontSettings", "lcd");
//    System.setProperty("swing.aatext", "true");

        new MeasureToolFrame();
    }

    private CxFAdapter CxFAdapter = new CxFAdapter();
    private LogoFileAdapter LogoFileAdapter = new LogoFileAdapter();

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        GUIUtils.setLookAndFeel();
        GUIUtils.fullScreen(this);

        String userdir = System.getProperty("user.dir");

        //==========================================================================
        // file choose init
        //==========================================================================
        jFileChooser = new JFileChooser();
        jFileChooser.addChoosableFileFilter(CxFAdapter.
                                            getFileNameExtensionFilter());
        jFileChooser.addChoosableFileFilter(LogoFileAdapter.
                                            getFileNameExtensionFilter());
        jFileChooser.setFileFilter(jFileChooser.getAcceptAllFileFilter());
        jFileChooser.setCurrentDirectory(new File(userdir));
        //==========================================================================
        jFileChooserSave = new JFileChooser();
        jFileChooserSave.removeChoosableFileFilter(jFileChooserSave.
                getAcceptAllFileFilter());
        jFileChooserSave.addChoosableFileFilter(CxFAdapter.
                                                getFileNameExtensionFilter());
        jFileChooserSave.addChoosableFileFilter(LogoFileAdapter.
                                                getFileNameExtensionFilter());
        jFileChooserSave.setCurrentDirectory(new File(userdir));
        //==========================================================================
        jFileChooserSaveVastView = new JFileChooser();
        jFileChooserSaveVastView.removeChoosableFileFilter(
                jFileChooserSaveVastView.
                getAcceptAllFileFilter());
        jFileChooserSaveVastView.addChoosableFileFilter(VastViewFile.
                getFileNameExtensionFilter());
        jFileChooserSaveVastView.setCurrentDirectory(new File(userdir));
        //==========================================================================

        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);

        desktopPane = new JDesktopPane();
        mdi = new MDIUtils(desktopPane);
        desktopPane.putClientProperty("JDesktopPane.dragMode", "outline");

        jMenuItem_SaveAsVastView.setEnabled(false);
        jMenuItemClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                jMenuItemClose_actionPerformed(actionEvent);
            }
        });
        setTitle("MeasureTool");
        statusBar.setText("");
        jMenu_File.setText("File");
        jMenuItem_Exit.setText("Exit");
        jMenuItem_Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                jMenuFileExit_actionPerformed(actionEvent);
            }
        });
        jMenuHelp.setText("Help");
        jMenuItem_About.setText("About");
        jMenuItem_About.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                jMenuHelpAbout_actionPerformed(actionEvent);
            }
        });
        jMenuItem_Open.setText("Open");
        jMenuItem_Open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                jMenuItem_Open_actionPerformed(actionEvent);
            }
        });

        jMenuItemClose.setText("Close");
        jMenu_Tools.setText("Tools");

        jMenuItem_ComputeMatrix.setAction(computeCalibrateAction);
        computeCalibrateAction.setEnabled(false);

        jMenuItem_DeleteMatrix.setAction(deleteCalibrateAction);
        deleteCalibrateAction.setEnabled(false);

        jMenu_Calibrate.setText("Calibrate");
        desktopPane.setBounds(new Rectangle(0, 0, 1, 1));

        jMenu_Calibrate.add(jMenuItem_ComputeMatrix);
        jMenu_Calibrate.add(jMenuItem_DeleteMatrix);

        //==========================================================================
        // config
        //==========================================================================
        jButton_Configuring.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton_Configuring.setVerticalTextPosition(SwingConstants.TOP);
        jButton_Configuring.setAction(configAction);
        jMenuItem_InstrConfig.setAction(configAction);
        jMenuItem_InstrConfig.setIcon(null);
        //==========================================================================

        //==========================================================================
        // measure
        //==========================================================================
        measureAction.setEnabled(false);
        jButton_Measuring.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton_Measuring.setVerticalTextPosition(SwingConstants.TOP);
        jButton_Measuring.setAction(measureAction);
        jMenuItem_TestchartMeasure.setAction(measureAction);
        jMenuItem_TestchartMeasure.setIcon(null);
        //==========================================================================

        //==========================================================================
        // high bits measure
        //==========================================================================
        hiBitsMeasureAction.setEnabled(false);
        jButton_HiBitsMeasuring.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton_HiBitsMeasuring.setVerticalTextPosition(SwingConstants.TOP);
        jButton_HiBitsMeasuring.setAction(hiBitsMeasureAction);
        jMenuItem_HiBitsMeasure.setAction(hiBitsMeasureAction);
        jMenuItem_HiBitsMeasure.setIcon(null);
//    jMenuItem_TestchartMeasure.setAction(measureAction);
//    jMenuItem_TestchartMeasure.setIcon(null);
        //==========================================================================

        //==========================================================================
        // batch
        //==========================================================================
        batchAction.setEnabled(false);
        jButton_Batch.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton_Batch.setVerticalTextPosition(SwingConstants.TOP);
        jButton_Batch.setAction(batchAction);
        jMenuItem_BatchMeasure.setAction(batchAction);
        jMenuItem_BatchMeasure.setIcon(null);
        //==========================================================================

        //==========================================================================
        // chracterization
        //==========================================================================
        characterizeAction.setEnabled(false);
        jButton_Chracterize.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton_Chracterize.setVerticalTextPosition(SwingConstants.TOP);
        jButton_Chracterize.setAction(characterizeAction);
        //==========================================================================

        //==========================================================================
        // save as
        //==========================================================================
        jMenuItem_SaveAs.setAction(saveAsAction);
        saveAsAction.setEnabled(false);
        //==========================================================================

        //==========================================================================
        // save as vastview
        //==========================================================================
        jMenuItem_SaveAsVastView.setAction(saveAsVastViewAction);
        saveAsVastViewAction.setEnabled(false);
        //==========================================================================


        jMenuBar1.add(jMenu_File);
        jMenuBar1.add(jMenu_Tools);
        jMenu_File.add(jMenuItem_Open);
        jMenu_File.add(jMenuItem_SaveAs);
        jMenu_File.add(jMenuItem_SaveAsVastView);
        jMenu_File.add(jMenuItemClose);
        jMenu_File.addSeparator();
        jMenu_File.add(jMenuItem_Exit);
        jMenuBar1.add(jMenuHelp);
        jMenuHelp.add(jMenuItem_About);
        setJMenuBar(jMenuBar1);
        jToolBar.add(jButton_Configuring);
        jToolBar.add(jButton_Measuring);
        jToolBar.add(jButton_HiBitsMeasuring);
        jToolBar.add(jButton_Batch);
        jToolBar.add(jButton_Chracterize);

        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(desktopPane);
        jMenu_Tools.add(jMenuItem_InstrConfig);
        jMenu_Tools.add(jMenuItem_TestchartMeasure);
        jMenu_Tools.add(jMenuItem_HiBitsMeasure);
        jMenu_Tools.add(jMenuItem_BatchMeasure);
        jMenu_Tools.add(jMenu_Calibrate);
        setVisible(true);
    }

    /**
     * File | Exit action performed.

     * @param actionEvent ActionEvent
     */
    protected void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Help | About action performed.
     * @param actionEvent ActionEvent
     */
    protected void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
        MeasureToolFrame_AboutBox dlg = new MeasureToolFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                        (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.setVisible(true);
    }

//  private final int maxFrameOfDesktopPane = 10;
//  private int internalFrameCount = 0;
//  protected int getInternalFrameWidth() {
//    return (int) (desktopPane.getWidth() /
//                  ( (9. + maxFrameOfDesktopPane) / maxFrameOfDesktopPane));
//  }
//
//  protected int getInternalFramHeight() {
//    return (int) (desktopPane.getHeight() /
//                  ( (9. + maxFrameOfDesktopPane) / maxFrameOfDesktopPane));
//  }

//  /**
//   * 取得InternalFrame的座標
//   * @return int[]
//   */
//  protected int[] getInternalFrameBounds() {
//    int x = (int) (desktopPane.getWidth() /
//                   (9. + maxFrameOfDesktopPane) * internalFrameCount);
//    int y = (int) (desktopPane.getHeight() /
//                   (9. + maxFrameOfDesktopPane) * internalFrameCount);
//    internalFrameCount = (internalFrameCount + 1) % 10;
//    return new int[] {
//        x, y};
//  }

    public void jMenuItem_Open_actionPerformed(ActionEvent e) {
        if (jFileChooser == null) {
            return;
        }
        this.jFileChooser.showOpenDialog(this);
        File file = this.jFileChooser.getSelectedFile();
        if (file != null) {
            LCDTarget.setRGBNormalize(false);
            LCDTarget lcdTarget = null;
            String filename = file.getName();
            if (filename.indexOf(".logo") != -1) {
                lcdTarget = LCDTarget.Instance.getFromLogo(file.getAbsolutePath());
            } else if (filename.indexOf(".txt") != -1) {
                lcdTarget = LCDTarget.Instance.getFromVastView(file.
                        getAbsolutePath());
            } else if (filename.indexOf(".cxf") != -1) {
                lcdTarget = LCDTarget.Instance.getFromSpectroPhotometerCxF(file.
                        getAbsolutePath());
            }

            else {
                return;
            }

            newTableFrame(file.getAbsolutePath(), lcdTarget.getInstrument(),
                          lcdTarget, false);
        }
    }

    protected class DeleteCalibrateAction extends AbstractAction {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a default icon.
         *
         * @param name String
         */
        public DeleteCalibrateAction(String name) {
            super(name);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showInternalConfirmDialog(desktopPane,
                    "Remove calibration matrix on current target?",
                    "Measure Tool",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                deleteCalibrateAction.setEnabled(false);
                binder.send(Calibrator.Operator.Delete, null);
            }
        }

    }


    protected class ComputeCalibrateAction extends AbstractAction {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a default icon.
         *
         * @param name String
         */
        public ComputeCalibrateAction(String name) {
            super(name);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
            if (!(selectedFrame instanceof MeasuredInternalFrame)) {
                //要選取量測資料的window
                JOptionPane.showInternalMessageDialog(desktopPane,
                        "Please select a measured data window.",
                        "Measure Tool",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            MeasuredInternalFrame selectedInternalFrame = (
                    MeasuredInternalFrame)
                    selectedFrame;

            if (selectedInternalFrame == calibrateRefFrame) {
                //不可以對同一個target做校正
                JOptionPane.showInternalMessageDialog(desktopPane,
                        "Cannot compute calibration matrix with same target.",
                        "Measure Tool",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                //======================================================================
                // 計算校正
                //======================================================================
                //取得校正需要的LCDTarget
                LCDTarget refTarget = calibrateRefFrame.lcdTarget;
                LCDTarget target = selectedInternalFrame.lcdTarget;
                if (!refTarget.hasRGBWPatch() || !target.hasRGBWPatch()) {
                    //至少要三個主色和白才能計算
                    JOptionPane.showInternalMessageDialog(desktopPane,
                            "target is not satisfy \"RGBW\" patches.",
                            "Measure Tool",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LCDTarget[] targets = new LCDTarget[] {
                                      refTarget, target};
                //送出校正所需LCDTarget
                binder.send(Calibrator.Operator.Compute, targets);
                //======================================================================

                //======================================================================
                // 進行校正
                //======================================================================
                //是否要進行校正?
                int result = JOptionPane.showInternalConfirmDialog(desktopPane,
                        "Apply calibration matrix to this target?",
                        "Measure Tool",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {

                    //對目前的frame進行校正
                    //pass給Calibrator去呼叫client做校正
                    binder.send(Calibrator.Operator.Calibrate,
                                selectedInternalFrame.binder);
                    deleteCalibrateAction.setEnabled(true);
                }
                //======================================================================
            }
        }

    }


    protected class BatchAction extends AbstractAction implements
            PropertyChangeListener {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a the specified icon.
         *
         * @param name String
         * @param icon Icon
         */
        public BatchAction(String name, Icon icon) {
            super(name, icon);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            batchMeasure();
            this.setEnabled(false);
        }

        protected void batchMeasure() {
            if (workerList.size() != 0) {
                SwingWorker worker = workerList.get(0);
                worker.addPropertyChangeListener(this);
                worker.execute();
                workerList.remove(worker);
            }
        }

        /**
         * This method gets called when a bound property is changed.
         *
         * @param evt A PropertyChangeEvent object describing the event source and
         *   the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() == SwingWorker.StateValue.STARTED &&
                evt.getNewValue() == SwingWorker.StateValue.DONE) {
                batchMeasure();
            }

        }

    }


    protected class CharacterizeAction extends AbstractAction {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a the specified icon.
         *
         * @param name String
         * @param icon Icon
         */
        public CharacterizeAction(String name, Icon icon) {
            super(name, icon);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            if (characterizeFrame == null) {
                characterizeFrame = new CharacterizeFrame();
                desktopPane.add(characterizeFrame);
                characterizeFrame.pack();
//        instrConfigFrame.addVetoableChangeListener(this);
            }
            characterizeFrame.setVisible(true);
        }

    }


    protected class MeasureAction extends AbstractAction implements
            ComponentListener {

        /**
         * Sets the enabled state of the <code>Action</code>.
         *
         * @param b true to enable this <code>Action</code>, false to disable it
         */
        public void setEnabled(boolean b) {
            if (hiBits) {
                if (b) {
//                    if (CPCodeLoader.exists()) {
//                        //程式存在才可以讓這個action enable
//                        super.setEnabled(b);
//                    }
                } else {
                    super.setEnabled(b);
                }
            } else {
                super.setEnabled(b);
            }
        }

        /**
         *
         * @param name String
         * @param icon Icon
         * @param owner Frame
         * @param hiBits boolean
         */
        public MeasureAction(String name, Icon icon, Frame owner,
                             boolean hiBits) {
            super(name, icon);
            this.owner = owner;
            this.hiBits = hiBits;
        }

        private Frame owner;
        private Meter meter;
        private boolean hiBits = false;

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            if (lcdTargetFrame != null && lcdTargetFrame.isVisible()) {
                return;
            }
            if (hiBits) {
                lcdTargetFrame = new LCDTargetFrame("Test Chart Measurement",
                        300,
                        LCDTargetFrame.Mode.HiBits);
            } else if (instrConfigFrame.meter instanceof DummyMeter) {
                lcdTargetFrame = new LCDTargetFrame("Test Chart Measurement", 0,
                        LCDTargetFrame.Mode.Dummy);
            } else if (instrConfigFrame.meter instanceof ArgyllDispMeter) {
                lcdTargetFrame = new LCDTargetFrame("Test Chart Measurement", 0,
                        LCDTargetFrame.Mode.Argyll);

            } else {
                lcdTargetFrame = new LCDTargetFrame("Test Chart Measurement",
                        300);
            }

            lcdTargetFrame.pack();
            lcdTargetFrame.addComponentListener(this);
            this.meter = instrConfigFrame.meter;
//        desktopPane.add(lcdTargetFrame);
//      }
            if (!lcdTargetFrame.isVisible()) {
                desktopPane.add(lcdTargetFrame);
                lcdTargetFrame.setVisible(true);
            }
        }

        /**
         * Invoked when the component's size changes.
         *
         * @param e ComponentEvent
         */
        public void componentResized(ComponentEvent e) {
        }

        /**
         * Invoked when the component's position changes.
         *
         * @param e ComponentEvent
         */
        public void componentMoved(ComponentEvent e) {
        }

        /**
         * Invoked when the component has been made visible.
         *
         * @param e ComponentEvent
         */
        public void componentShown(ComponentEvent e) {
        }


        protected SwingWorker getSwingWorker(final AppMeasureParameter mp) {
            //======================================================================
            // 利用SwingWorker, 工作結束後可以call back 進行其他處理
            //======================================================================
            SwingWorker worker = new SwingWorker<List<Patch>, Void>() {
                @Override
                public List<Patch> doInBackground() {
                    MeterMeasurement mm = null;

                    mm = getMeterMeasurement(mp);
                    mm.setMeasureWindowsVisible(true);

                    if (mp.firstBatch) {
                        //如果是batch的第一個
                        final Object lock = new Object();
                        TinyDialog.Dialog d = TinyDialog.getStartDialogInstance(
                                owner,
                                "Start Measuring",
                                new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                synchronized (lock) {
                                    lock.notify();
                                }
                            }
                        });
                        d.setVisible(true);

                        synchronized (lock) {
                            try {
                                lock.wait();
                                d.setVisible(false);
                                d.dispose();

                            } catch (InterruptedException ex) {
                                Logger.log.error("", ex);
                            }
                        }
                    }

                    List<Patch> result = null;
//                    if (mp.measureBits != MeasureBits.EightBits) {
//                        HighBitsMeasurement hbm = new HighBitsMeasurement(mm,
//                                mp.icBits);
//                        result = hbm.measure(mp.measureBits, mp.targetNumber);
//                    } else {
                        mm.setWaitTimes(mp.delayTimes);
                        result = mm.measure(mp.lcdTarget);
//                    }

                    mm.setMeasureWindowsVisible(false);
                    return result;
                }

                @Override
                protected void done() {
                    List<Patch> result = null;
                    try {
                        //取得 result
                        result = get();
                    } catch (ExecutionException ex) {
                        Logger.log.error("", ex);
                    } catch (InterruptedException ex) {
                        Logger.log.error("", ex);
                    }
                    String title = mp.targetNumber.name();
                    if (result != null) {
                        Patch whitePatch = Patch.Filter.whitePatch(result);
                        if (whitePatch != null) {
                            result = Patch.Produce.LabPatches(result,
                                    whitePatch.getXYZ());
                        }
                        LCDTargetBase.Number number = (mp.lcdTarget != null) ?
                                mp.lcdTarget.getNumber() : mp.targetNumber;
                        LCDTarget lcdTarget = LCDTarget.Instance.get(result,
                                number,
                                mp.inverseMode);

                        newTableFrame(title, mp.meter.getType(), lcdTarget, true);
//            jMenuItem_SaveAs.setEnabled(true);
                        saveAsAction.setEnabled(true);
                    }
                }

            };

            return worker;
        }

        /**
         * Invoked when the component has been made invisible.
         *
         * @param e ComponentEvent
         */
        public void componentHidden(ComponentEvent e) {
            //========================================================================
            // 設定結束, 從UI撈設定值然後開始準備量測
            //========================================================================
            if (e.getSource() == lcdTargetFrame) {
                //======================================================================
                // 前置設定
                //======================================================================
                AppMeasureParameter mp = lcdTargetFrame.getAppMeasureParameter(new
                        AppMeasureParameter());
                if (mp.measureDisplay2) {
                    ((ArgyllDispMeter) instrConfigFrame.meter).setDisplay(2);
                }
                mp.meter = instrConfigFrame.meter;
                mp.firstBatch = (workerList.size() == 0);
                //======================================================================

                //======================================================================
                // 載入LCDTarget
                //======================================================================
                LCDTargetBase.Number number = mp.targetNumber;
                LCDTarget.setRGBNormalize(false);
                mp.lcdTarget = LCDTarget.Instance.get(number);
                //======================================================================

                SwingWorker worker = getSwingWorker(mp);

                if (mp.batch) {
                    batchAction.setEnabled(true);
                    workerList.add(worker);
                } else {
                    worker.execute();
                }

            }
        }

    }


    protected class SaveAsAction extends AbstractAction {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a default icon.
         *
         * @param name String
         */
        public SaveAsAction(String name) {
            super(name);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            MeasuredInternalFrame frame = (MeasuredInternalFrame) desktopPane.
                                          getSelectedFrame();
            if (frame != null) {
                PatchListTablePanel panel = frame.getPatchListTablePanel();

                jFileChooserSave.showSaveDialog(null);
                File saveFile = jFileChooserSave.getSelectedFile();
                if (saveFile != null) {
                    FileFilter filter = jFileChooserSave.getFileFilter();
                    String saveFilename = saveFile(saveFile.getAbsolutePath(),
                            filter,
                            frame.lcdTarget);
                    frame.setTitle(saveFilename);
                    frame.setSaved(true);
                }
            }
        }

    }


    protected class SaveAsVastViewAction extends AbstractAction {

        /**
         * Defines an <code>Action</code> object with the specified description
         * string and a default icon.
         *
         * @param name String
         */
        public SaveAsVastViewAction(String name) {
            super(name);
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            MeasuredInternalFrame frame = (MeasuredInternalFrame) desktopPane.
                                          getSelectedFrame();
            if (frame != null) {
//        PatchListTablePanel panel = frame.getPatchListTablePanel();

                jFileChooserSaveVastView.showSaveDialog(null);
                File saveFile = jFileChooserSaveVastView.getSelectedFile();
                if (saveFile != null) {
                    FileFilter filter = jFileChooserSaveVastView.getFileFilter();
                    String saveFilename = saveFile(saveFile.getAbsolutePath(),
                            filter,
                            frame.lcdTarget);
                    frame.setTitle(saveFilename);
                    frame.setSaved(true);
                }
            }
        }

        public boolean canEnable() {
            MeasuredInternalFrame frame = (MeasuredInternalFrame) desktopPane.
                                          getSelectedFrame();
            if (frame != null) {
                return VastViewFile.checkLCDTarget(frame.lcdTarget);
            } else {
                return false;
            }
        }

    }


    /**
     * 將patchList以tableFrame的方式show出來
     * @param title String
     * @param instrument Instr
     * @param lcdTarget LCDTarget
     * @param openAndSave boolean
     */
    protected void newTableFrame(String title, Meter.Instr instrument,
                                 LCDTarget lcdTarget, boolean openAndSave) {
        String instrname = instrument != null ? instrument.name() : null;

        //設定歸屬frame
        MeasuredInternalFrame frame = MeasuredInternalFrame.getInstance(title,
                instrname, lcdTarget, this);
        desktopPane.add(frame);

        //設定位置
        int[] bounds = mdi.getInternalFrameBounds();
        frame.setLocation(bounds[0], bounds[1]);
        frame.setPreferredSize(new Dimension(mdi.getInternalFrameWidth(),
                                             mdi.getInternalFramHeight()));
        //打包&顯示
        frame.pack();
        frame.setVisible(true);
//    jMenuItem_SaveAs.setEnabled(true);
        saveAsAction.setEnabled(true);
        saveAsVastViewAction.setEnabled(saveAsVastViewAction.canEnable());

        //==========================================================================
        // 預先儲存起來, 避免量測結果遺失
        //==========================================================================
        if (openAndSave) {

            File saveFile = new File(System.currentTimeMillis() + ".logo");
//      List<Patch> patchList = lcdTarget.getPatchList();
            saveFile(saveFile.getAbsolutePath(),
                     LogoFileAdapter.getFileNameExtensionFilter(), lcdTarget);
        }
        //==========================================================================
    }

    protected class ConfigAction extends AbstractAction implements
            VetoableChangeListener {

        private MeasureToolFrame measureToolFrame;

        public ConfigAction(String name, Icon icon,
                            MeasureToolFrame measureToolFrame) {
            super(name, icon);
            this.measureToolFrame = measureToolFrame;
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            if (instrConfigFrame == null) {
                instrConfigFrame = new InstrumentConfigurationFrame();
                desktopPane.add(instrConfigFrame);
                instrConfigFrame.pack();
                instrConfigFrame.addVetoableChangeListener(this);
            }
            instrConfigFrame.setVisible(true);

        }

        /**
         * This method gets called when a constrained property is changed.
         *
         * @param evt a <code>PropertyChangeEvent</code> object describing the
         *   event source and the property that has changed.
         * @throws PropertyVetoException if the recipient wishes the property
         *   change to be rolled back.
         */
        public void vetoableChange(PropertyChangeEvent evt) throws
                PropertyVetoException {
            if (evt.getSource() == instrConfigFrame) {
                if (instrConfigFrame.instrumentReady) {
                    measureAction.setEnabled(true);
                    hiBitsMeasureAction.setEnabled(true);
//          batchAction.setEnabled(true);
                    characterizeAction.setEnabled(true);
                } else {
                    measureAction.setEnabled(false);
                    hiBitsMeasureAction.setEnabled(false);
                    batchAction.setEnabled(false);
                    characterizeAction.setEnabled(false);
                }
            }

        }

    }


    protected LCDTargetFrame lcdTargetFrame;

    protected String saveFile(String filename, FileFilter filter,
                              LCDTarget lcdTarget) {
        if (filter == jFileChooser.getAcceptAllFileFilter()) {
            //未選擇檔案類型, 掠過
            return null;
        }

        FileNameExtensionFilter extFilter = (FileNameExtensionFilter) filter;
        String[] exts = extFilter.getExtensions();

        int lastSlash = filename.lastIndexOf('/');
        if (filename.indexOf('.', lastSlash) == -1) {
            filename = filename + "." + exts[0];
        }

        if (extFilter == CxFAdapter.getFileNameExtensionFilter()) {

        } else if (extFilter == VastViewFile.getFileNameExtensionFilter()) {
            VastViewFile file = new VastViewFile(filename, lcdTarget);
            try {
                file.save();
            } catch (IOException ex) {
                Logger.log.error("", ex);
            }
        } else if (extFilter == LogoFileAdapter.getFileNameExtensionFilter()) {
            LogoFile file = null;
            try {
                file = new LogoFile(filename, true);
            } catch (IOException ex) {
                Logger.log.error("", ex);
            }
            //如果是從檔案load進來的, 就不會有meter, 所以用dummy的
            Meter meter = instrConfigFrame != null ? instrConfigFrame.meter :
                          new DummyMeter();
            meter.setLogoFileHeader(file);
            meter.setLogoFileData(file, lcdTarget.getPatchList());
            if (lcdTargetFrame != null &&
                lcdTargetFrame.jCheckBox_255InverseMode.isSelected()) {
                file.setHeader(LogoFile.Reserved.InverseModeMeasure, "Yes");
            }

            try {
                file.save();
            } catch (IOException ex) {
                Logger.log.error("", ex);
            }
        }
        return filename;
    }

    public void jMenuItemClose_actionPerformed(ActionEvent e) {
        JInternalFrame frame = desktopPane.getSelectedFrame();
        if (frame != null) {
            frame.dispose();
        }
    }

}

enum MeasureBits {
   //12位元
   TwelveBits(RGBBase.MaxValue.Int12Bit),
   //10位元
   TenBits(RGBBase.MaxValue.Int10Bit),
   //9位元
   NineBits(RGBBase.MaxValue.Int9Bit),
   //8位元
   EightBits(RGBBase.MaxValue.Int8Bit);

   public RGBBase.MaxValue getMaxValue() {
       return maxValue;
   }

   MeasureBits(RGBBase.MaxValue maxValue) {
       this.maxValue = maxValue;
   }

   private RGBBase.MaxValue maxValue;
}
