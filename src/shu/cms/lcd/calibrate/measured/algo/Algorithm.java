package shu.cms.lcd.calibrate.measured.algo;

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
public class Algorithm {

  public static enum Mode {
    White, Green, Normal;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  void setForceTrigger(boolean forceTrigger) {
    this.forceTrigger = forceTrigger;
  }

  private Mode mode = Mode.Normal;
  private boolean forceTrigger = false;

  protected boolean isWhitePointMode() {
    return mode == Mode.White;
  }

  protected boolean isGreenMode() {
    return mode == Mode.Green;
  }

  protected boolean isForceTrigger() {
    return forceTrigger || isWhitePointMode();
  }

}
