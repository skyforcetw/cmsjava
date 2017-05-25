/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;

public class _ICaEvents
    extends Dispatch {

  public static final String componentName = "CA200SRVR._ICaEvents";

  public _ICaEvents() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public _ICaEvents(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public _ICaEvents(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void exeCalZero() {
    Dispatch.call(this, "ExeCalZero");
  }

}
