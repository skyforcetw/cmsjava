package shu.cms.measure.util;

import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;

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
public class MonitorContrastMeasureTool {
  protected MeterMeasurement mt;
  protected Meter meter;

  public MonitorContrastMeasureTool(Meter meter) {
    this.meter = meter;
    mt = new MeterMeasurement(meter, true);
  }

  public List<Patch> measure() {
    JOptionPane.showMessageDialog(mt.getMeasureWindow(),
                                  "�бN" + meter.getType().name() +
                                  "��m�b�ù��W,�N�ù��G�׽ը�̤j, �ë��U <�T�w> �i��q��.",
                                  "���q���T�{",
                                  JOptionPane.INFORMATION_MESSAGE);
    List<Patch> patchList = new ArrayList<Patch> ();

    for (int x = 1; ; x++) {
      Patch whitePatch = mt.measure(white, String.valueOf(x) + "-white");
      Patch blackPatch = mt.measure(black, String.valueOf(x) + "-black");

      patchList.add(whitePatch);
      patchList.add(blackPatch);

      int result = JOptionPane.showConfirmDialog(mt.getMeasureWindow(),
                                                 "���C�ù��G���~��U�@���q�� (�Ϋ������������q��)",
                                                 "�ù��G�׽վ�T�{",
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);

      if (result == 2) {
        break;
      }
    }
    mt.setMeasureWindowsVisible(false);
    return patchList;

  }

  public static void main(String[] args) {
    DummyMeter meter = new DummyMeter();
    MonitorContrastMeasureTool tool = new MonitorContrastMeasureTool(meter);
    for (Patch p : tool.measure()) {
      System.out.println(p);
    }
  }

  protected final static RGB black = new RGB(RGB.ColorSpace.unknowRGB,
                                             Color.black);
  protected final static RGB white = new RGB(RGB.ColorSpace.unknowRGB,
                                             Color.white);
}
