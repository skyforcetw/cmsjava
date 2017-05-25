package shu.cms.lcd.benchmark.verify;

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
public abstract class Verifier {
  public static class VerifierReport {
    protected VerifierReport(boolean status) {
      this(status, null, null);
    }

    protected VerifierReport(boolean status, String description) {
      this(status, description, null);
    }

    protected VerifierReport(String description) {
      this(description, null);
    }

    protected VerifierReport(Object result) {
      this(null, result);
    }

    protected VerifierReport(boolean status, String description, Object result) {
      this.status = status;
      this.description = description;
      this.result = result;
    }

    protected VerifierReport(String description, Object result) {
      this.reportStatus = false;
      this.description = description;
      this.result = result;
    }

    public boolean status = false;
    private boolean reportStatus = true;
    public String description;
    public Object result;
    public String toString() {
      StringBuilder buf = new StringBuilder();
      if (reportStatus) {
        buf.append("Verify: " + status + "\n");
      }
      if (description != null) {
        buf.append("Description: " + description + "\n");
      }
      if (result != null) {
        buf.append("Result: " + result + "\n");
      }
      return buf.toString();
    }
  }

  protected LCDTarget lcdTarget;
  public Verifier(LCDTarget lcdTarget) {
    if (!checkLCDTarget(lcdTarget)) {
      throw new IllegalArgumentException("lcdTarget is invalid.");
    }
    this.lcdTarget = lcdTarget;
  }

  protected abstract boolean checkLCDTarget(LCDTarget lcdTarget);
}
