/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;

public class ICa200
    extends Dispatch {

  public static final String componentName = "CA200SRVR.ICa200";

  public ICa200() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public ICa200(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public ICa200(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type ICas
   */
  public ICas getCas() {
    return new ICas(Dispatch.get(this, "Cas").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param caNumberVal an input-parameter of type int
   * @param connecStringVal an input-parameter of type String
   * @param portVal an input-parameter of type int
   * @param lastParam an input-parameter of type int
   */
//	public void setConfiguration(int caNumberVal, String connecStringVal, int portVal, int lastParam) {
//		Dispatch.call(this, "SetConfiguration", new Variant(caNumberVal), connecStringVal, new Variant(portVal), new Variant(lastParam));
//	}

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param caNumberVal an input-parameter of type int
   * @param connecStringVal an input-parameter of type String
   * @param portVal an input-parameter of type int
   */
  public void setConfiguration(int caNumberVal, String connecStringVal,
                               int portVal) {
    Dispatch.call(this, "SetConfiguration", new Variant(caNumberVal),
                  connecStringVal, new Variant(portVal));
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param caNumberVal an input-parameter of type int
   * @param connecStringVal an input-parameter of type String
   * @param portVal an input-parameter of type int
   * @param lastParam an input-parameter of type int
   */
  public void setConfiguration(int caNumberVal, String connecStringVal,
                               int portVal, int lastParam) {
    Dispatch.call(this, "SetConfiguration", new Variant(caNumberVal),
                  connecStringVal, new Variant(portVal), new Variant(lastParam));

  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void autoConnect() {
    Dispatch.call(this, "AutoConnect");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type ICa
   */
  public ICa getSingleCa() {
    return new ICa(Dispatch.get(this, "SingleCa").toDispatch());
  }

}
