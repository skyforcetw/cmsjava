/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;

public class IProbe
    extends Dispatch {

  public static final String componentName = "CA200SRVR.IProbe";

  public IProbe() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public IProbe(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public IProbe(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getX() {
    return Dispatch.get(this, "X").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getY() {
    return Dispatch.get(this, "Y").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getZ() {
    return Dispatch.get(this, "Z").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getFlckrJEITA() {
    return Dispatch.get(this, "FlckrJEITA").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getNumber() {
    return Dispatch.get(this, "Number").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getID() {
    return Dispatch.get(this, "ID").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type String
   */
  public void setID(String lastParam) {
    Dispatch.call(this, "ID", lastParam);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getSerialNO() {
    return Dispatch.get(this, "SerialNO").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getLv() {
    return Dispatch.get(this, "Lv").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getud() {
    return Dispatch.get(this, "ud").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getvd() {
    return Dispatch.get(this, "vd").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getT() {
    return Dispatch.get(this, "T").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getdEUser() {
    return Dispatch.get(this, "dEUser").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getFlckrFMA() {
    return Dispatch.get(this, "FlckrFMA").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getsy() {
    return Dispatch.get(this, "sy").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getsx() {
    return Dispatch.get(this, "sx").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getduv() {
    return Dispatch.get(this, "duv").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getusUser() {
    return Dispatch.get(this, "usUser").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getvsUser() {
    return Dispatch.get(this, "vsUser").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getLsUser() {
    return Dispatch.get(this, "LsUser").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getLvfL() {
    return Dispatch.get(this, "LvfL").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getR() {
    return Dispatch.get(this, "R").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getG() {
    return Dispatch.get(this, "G").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getB() {
    return Dispatch.get(this, "B").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getRD() {
    return Dispatch.get(this, "RD").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getRAD() {
    return Dispatch.get(this, "RAD").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getRFMA() {
    return Dispatch.get(this, "RFMA").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getRJEITA() {
    return Dispatch.get(this, "RJEITA").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type float
   */
  public float getSpectrum(int lastParam) {
    return Dispatch.call(this, "GetSpectrum", new Variant(lastParam)).toFloat();
  }

}
