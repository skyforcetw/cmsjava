package shu.cms.applet.measure.tool;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.*;
import shu.cms.lcd.*;
import shu.cms.measure.calibrate.*;
import shu.cms.ui.*;

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
public class MeasuredInternalFrame
    extends JInternalFrame {
//    extends JFrame {

  private FourColorCalibrator colorCalibrator;
  protected void setColorCalibrator(FourColorCalibrator colorCalibrator) {
    this.colorCalibrator = colorCalibrator;
    this.calibratedPatchList = Patch.Produce.copyOf(originalPatchList);
    this.colorCalibrator.calibrate(calibratedPatchList);
  }

  protected class Binder
      extends Calibrator.ClientBinder {
    public void receive(Calibrator.Operator op, Object obj) {
      switch (op) {
        case GetCalibrator:
          FourColorCalibrator colorCalibrator = (FourColorCalibrator) obj;
          setColorCalibrator(colorCalibrator);
          break;
        case Calibrate:
          setCalibrate(true);
          break;
      }
    }

    public Binder(Calibrator calibrator) {
      super(calibrator);
    }
  }

  private static ArrayList<MeasuredInternalFrame> frameArrayList = new
      ArrayList<
          MeasuredInternalFrame> ();
  public final static void setFramesCalibrateEnable(boolean enable) {
    for (MeasuredInternalFrame frame : frameArrayList) {
      frame.calibrateAction.setEnabled(enable);
    }
  }

  public final static void removeFramesCalibrate() {
    for (MeasuredInternalFrame frame : frameArrayList) {
      frame.calibrateAction.setEnabled(false);
      frame.setCalibrate(false);
    }

  }

  public final static MeasuredInternalFrame getInstance(String title,
      String instrument, LCDTarget lcdTarget, MeasureToolFrame measureToolFrame) {
    MeasuredInternalFrame frame = new MeasuredInternalFrame(title, instrument,
        lcdTarget, measureToolFrame);
    frameArrayList.add(frame);
    return frame;
  }

  public LCDTarget lcdTarget;

  private MeasuredInternalFrame(String title, String instrument,
                                LCDTarget lcdTarge,
                                MeasureToolFrame measureToolFrame) {
    super(title, true, true, true, true);
    this.lcdTarget = lcdTarge;
    this.originalPatchList = lcdTarge.getPatchList();
    this.instrument = instrument;
    binder = new Binder(measureToolFrame.calibrator);
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected class ReferenceAction
      extends AbstractAction {

    /**
     * Defines an <code>Action</code> object with the specified description
     * string and a default icon.
     *
     * @param name String
     */
    public ReferenceAction(String name) {
      super(name);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
      JCheckBox jCheckBox_Reference = (JCheckBox) e.getSource();

      boolean selected = jCheckBox_Reference.isSelected();
      if (selected) {
        //把所有的frame的ref checkbox設為未選擇
        for (MeasuredInternalFrame frame : frameArrayList) {
          if (frame != null) {
            frame.jCheckBox_Reference.setSelected(false);
          }
        }
        jCheckBox_Reference.setSelected(true);
      }
      binder.send(Calibrator.Operator.Reference, Boolean.valueOf(selected));
    }

  }

  protected void setCalibrate(boolean calibrate) {
    jCheckBox_Calibrate.setSelected(calibrate);
    //取得calibrator
    if (calibrate) {
      binder.send(Calibrator.Operator.GetCalibrator, null);
      patchListTablePanel.setPatchList(this.calibratedPatchList);
    }
    else {
      patchListTablePanel.setPatchList(this.originalPatchList);
    }

  }

  protected class CalibrateAction
      extends AbstractAction {

    /**
     * Defines an <code>Action</code> object with the specified description
     * string and a default icon.
     *
     * @param name String
     */
    public CalibrateAction(String name) {
      super(name);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
      JCheckBox jCheckBox = (JCheckBox) e.getSource();
      boolean selected = jCheckBox.isSelected();
      setCalibrate(selected);
    }

  }

  private ReferenceAction referenceAction = new ReferenceAction(
      "Reference");
  private CalibrateAction calibrateAction = new CalibrateAction(
      "Calibrate");
  private boolean saved = false;
  private String instrument;
  private List<Patch> originalPatchList;
  private List<Patch> calibratedPatchList;
//  private boolean calibrated = false;
  protected JPanel jPanel1 = new JPanel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JLabel jLabel_Status = new JLabel();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected JPanel jPanel2 = new JPanel();
  protected JCheckBox jCheckBox_Calibrate = new JCheckBox();
  protected JCheckBox jCheckBox_Reference = new JCheckBox();
  protected PatchListTablePanel patchListTablePanel;

  private void jbInit() throws Exception {

    //tabbed
    JTabbedPane tabbedPane = new JTabbedPane();
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
//    jCheckBox_Calibrate.setEnabled(false);
    jCheckBox_Calibrate.setAction(calibrateAction);
    calibrateAction.setEnabled(false);
    jCheckBox_Calibrate.setText("Calibrate");
    jCheckBox_Reference.setAction(referenceAction);
    jCheckBox_Reference.setText("Reference"); //tabular data
    patchListTablePanel = new PatchListTablePanel(originalPatchList);
    patchListTablePanel.setOpaque(true); //content panes must be opaque

    PatchCanvas patchCanvas = new PatchCanvas();
    patchCanvas.setReferenceRGBList(Patch.Filter.rgbList(originalPatchList));

    tabbedPane.add("Tabular Data", patchListTablePanel);
    tabbedPane.add("Patch", patchCanvas);
    jLabel_Status.setText("Instrument: " + instrument);
    jPanel1.add(jLabel_Status, java.awt.BorderLayout.WEST);
    jPanel1.add(jPanel2, java.awt.BorderLayout.EAST);
    jPanel2.add(jCheckBox_Calibrate);
    jPanel2.add(jCheckBox_Reference);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);
    this.getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);
  }

  public boolean isSaved() {
    return saved;
  }

  public PatchListTablePanel getPatchListTablePanel() {
    return patchListTablePanel;
  }

  public void setSaved(boolean saved) {
    this.saved = saved;
  }

  public Binder binder;
}
