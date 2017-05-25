package shu.cms.measure.remote;

import java.awt.*;
import java.awt.event.*;

import shu.cms.measure.*;
import shu.cms.measure.meter.*;
import shu.math.array.*;

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
public class PopupMenuFactory {
  private MeterMeasurement mm;

  public PopupMenuFactory(Meter meter) {
    this.mm = new MeterMeasurement(meter, false);
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    menuItem_Calibrate.setLabel("Calibrate");
    menuItem_Calibrate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuItem_Calibrate_actionPerformed(e);
      }
    });
    menuItem_Exit.setLabel("Exit");
    menuItem_Exit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuItem_Exit_actionPerformed(e);
      }
    });
    menuItem_Measure.setLabel("Measure");
    menuItem_Measure.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuItem_Measure_actionPerformed(e);
      }
    });
    checkboxMenuItem_Continue.setLabel("Continue Measure");
    checkboxMenuItem_Continue.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkboxMenuItem_Continue_itemStateChanged(e);
      }
    });
    popupMenu.add(menuItem_Calibrate);
    popupMenu.add(menuItem_Measure);
    popupMenu.add(checkboxMenuItem_Continue);
    popupMenu.add(menuItem_Exit);
  }

  protected PopupMenu popupMenu = new PopupMenu();
  protected MenuItem menuItem_Calibrate = new MenuItem();
  protected MenuItem menuItem_Exit = new MenuItem();
  protected MenuItem menuItem_Measure = new MenuItem();
  protected CheckboxMenuItem checkboxMenuItem_Continue = new CheckboxMenuItem();
  public void menuItem_Exit_actionPerformed(ActionEvent e) {
    mm.close();
    System.exit(1);
  }

  public void menuItem_Calibrate_actionPerformed(ActionEvent e) {
    mm.calibrate();
  }

  public PopupMenu getPopupMenu() {
    return popupMenu;
  }

  public void menuItem_Measure_actionPerformed(ActionEvent e) {
    double[] XYZValues = mm.getMeter().triggerMeasurementInXYZ();
//    Logger.log.info("measure XYZ: " + DoubleArray.toString(XYZValues));
    System.out.println("measure XYZ: " + DoubleArray.toString(XYZValues));
  }

  private boolean continueMeasure = false;
  public void checkboxMenuItem_Continue_itemStateChanged(ItemEvent e) {
    switch (e.getStateChange()) {
      case ItemEvent.SELECTED:
        continueMeasure = true;
        new Thread() {
          public void run() {
            while (continueMeasure) {
              menuItem_Measure_actionPerformed(null);
            }
          }
        }
        .start();
        break;
      case ItemEvent.DESELECTED:
        continueMeasure = false;
        break;
    }
  }

}
