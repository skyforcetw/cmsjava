package shu.cms.applet.gradient.modeling;

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
public abstract class JudgeBase {
  private double deltajndi;
  private double jndi;
  public double getDeltaJNDI() {
    return deltajndi;
  }

  public double getJNDI() {
    return jndi;
  }

  public void setDeltaJNDI(double deltajndi) {
    this.deltajndi = deltajndi;
  }

  public void setJNDI(double jndi) {
    this.jndi = jndi;
  }

  protected void addJNDI(double val) {
    jndi += val;
  }

  private double preDeltaJNDI;
  protected double getPreDeltaJNDI() {
    return preDeltaJNDI;
  }

  protected void addDeltaJNDI(double val) {
    preDeltaJNDI = deltajndi;
    deltajndi += val; ;
  }

}
