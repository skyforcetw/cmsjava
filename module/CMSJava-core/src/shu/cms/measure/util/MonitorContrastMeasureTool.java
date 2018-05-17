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
                                  "請將" + meter.getType().name() +
                                  "放置在螢幕上,將螢幕亮度調到最大, 並按下 <確定> 進行量測.",
                                  "對比量測確認",
                                  JOptionPane.INFORMATION_MESSAGE);
    List<Patch> patchList = new ArrayList<Patch> ();

    for (int x = 1; ; x++) {
      Patch whitePatch = mt.measure(white, String.valueOf(x) + "-white");
      Patch blackPatch = mt.measure(black, String.valueOf(x) + "-black");

      patchList.add(whitePatch);
      patchList.add(blackPatch);

      int result = JOptionPane.showConfirmDialog(mt.getMeasureWindow(),
                                                 "降低螢幕亮度繼續下一次量測 (或按取消結束對比量測)",
                                                 "螢幕亮度調整確認",
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
