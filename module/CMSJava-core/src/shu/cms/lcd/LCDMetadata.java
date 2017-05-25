package shu.cms.lcd;

import java.io.*;

import shu.util.*;

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
public class LCDMetadata
    implements Serializable {
  public LCDMetadata(String model, Type type, Backlight backlight) {
    this.model = model;
    this.type = type;
    this.backlight = backlight;
  }

  /**
   * ¿Ã¹õ/panel ªºmodel
   */
  protected String model;
  /**
   * ¸É¥R±Ô­z
   */
  protected String description = "";
  /**
   * ²G´¹ªºdrive type
   */
  protected Type type;
  /**
   * ±Ä¥Îªº­I¥ú·½
   */
  protected Backlight backlight;

  public static enum Type {
    TN, VA, IPS
  }

  public static enum Backlight {
    NTSC45, NTSC72, NTSC92, LED
  }

  public static void main(String[] args) {
    LCDMetadata meta = new LCDMetadata("dell 2407wfp-hc", Type.VA,
                                       Backlight.NTSC92);
//    meta.setDescription("");
//    meta.setColorSetting("Normal");
//    meta.setContrast(52);
//    meta.setBrightness(40);
    Persistence.writeObjectAsXML(meta, "lcd.meta");
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
