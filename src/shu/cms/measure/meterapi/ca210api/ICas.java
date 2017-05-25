/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;

public class ICas
    extends Dispatch {

  public static final String componentName = "CA200SRVR.ICas";

  public ICas() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public ICas(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public ICas(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type Variant
   * @return the result is of type ICa
   */
  public ICa getItem(Variant lastParam) {
    return new ICa(Dispatch.call(this, "Item", lastParam).toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type Variant
   */
  public Variant get_NewEnum() {
    return Dispatch.get(this, "_NewEnum");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getCount() {
    return Dispatch.get(this, "Count").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void sendMsr() {
    Dispatch.call(this, "SendMsr");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void receiveMsr() {
    Dispatch.call(this, "ReceiveMsr");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type ICa
   */
  public ICa getItemOfNumber(int lastParam) {
    return new ICa(Dispatch.call(this, "ItemOfNumber", new Variant(lastParam)).
                   toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param caNumberVal an input-parameter of type int
   * @param lastParam an input-parameter of type String
   */
  public void setCaID(int caNumberVal, String lastParam) {
    Dispatch.call(this, "SetCaID", new Variant(caNumberVal), lastParam);
  }

}
