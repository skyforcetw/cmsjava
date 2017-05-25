package shu.cms.colorformat.file;

import java.io.*;
import java.text.*;

import javax.swing.filechooser.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

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
public class VastViewFile {
  protected String filename;
  protected LCDTarget lcdTarget;
//  protected List<Patch> patchList;

  public VastViewFile(String filename, LCDTarget lcdTarget) {
    this.filename = filename;
    this.lcdTarget = lcdTarget;
    if (!checkLCDTarget(lcdTarget)) {
      throw new IllegalArgumentException(
          "lcdTarget is not Ramp1021 or Ramp1024");
    }
  }

  public final static boolean checkLCDTarget(LCDTarget lcdTarget) {
    if (lcdTarget.getNumber() == LCDTargetBase.Number.Ramp1021 ||
        lcdTarget.getNumber() == LCDTargetBase.Number.Ramp1024 ||
        lcdTarget.getNumber() == LCDTargetBase.Number.Ramp256W) {
      return true;
    }
    else {
      return false;
    }
  }

  public void save() throws IOException {
    save(this.filename);
  }

  private DecimalFormat df = new DecimalFormat(".###############");

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (RGBBase.Channel ch : RGBBase.Channel.RGBWChannel) {
      for (int x = 0; x < 256; x++) {
        Patch p = lcdTarget.getPatch(ch, x, RGB.MaxValue.Int8Bit);
        if (p == null) {
          buf.append("0,0,0,0,0,0\n");
        }
        else {
          CIEXYZ XYZ = p.getXYZ();
          CIExyY xyY = new CIExyY(XYZ);
          buf.append(' ');
          buf.append(df.format(XYZ.X));
          buf.append(",");
          buf.append(df.format(XYZ.Y));
          buf.append(",");
          buf.append(df.format(XYZ.Z));
          buf.append(",");
          buf.append(df.format(xyY.x));
          buf.append(",");
          buf.append(df.format(xyY.y));
          buf.append(",");
          buf.append(df.format(xyY.Y));
          buf.append("\n\r");
        }
      }
    }
    String str = buf.toString();
    return str.trim();
  }

  public void save(String filename) throws IOException {
    String fmt = toString();
    BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
    writer.write(fmt);
    writer.flush();
    writer.close();
  }

  protected final static FileNameExtensionFilter filter = new
      FileNameExtensionFilter("VastView File" + " (*.txt)", "txt");

  public final static FileNameExtensionFilter getFileNameExtensionFilter() {
    return filter;
  }

}
