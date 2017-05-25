package shu.cms.util;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Division {
  protected boolean checkLowermost;
  protected boolean checkUppermost;
  protected double lowermost;
  protected double uppermost;

  public Division(boolean checkLowermost, boolean checkUppermost,
                  double lowermost, double uppermost) {
    this.checkLowermost = checkLowermost;
    this.checkUppermost = checkUppermost;
    this.lowermost = lowermost;
    this.uppermost = uppermost;
  }

  public boolean isValid(double value) {
    if (checkLowermost && value < lowermost) {
      return false;
    }
    if (checkUppermost && value > uppermost) {
      return false;
    }
    return true;
  }

  public boolean isValid(double value, double looseRate) {
    if (checkLowermost && value < lowermost / looseRate) {
      return false;
    }
    if (checkUppermost && value > uppermost * looseRate) {
      return false;
    }
    return true;
  }
}
