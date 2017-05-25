/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;

public class IOutputProbes
    extends Dispatch {

  public static final String componentName = "CA200SRVR.IOutputProbes";

  public IOutputProbes() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public IOutputProbes(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public IOutputProbes(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type Variant
   * @return the result is of type IProbe
   */
  public IProbe getItem(Variant lastParam) {
    return new IProbe(Dispatch.call(this, "Item", lastParam).toDispatch());
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
   * @param lastParam an input-parameter of type String
   */
  public void add(String lastParam) {
    Dispatch.call(this, "Add", lastParam);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type IProbe
   */
  public IProbe getItemOfNumber(int lastParam) {
    return new IProbe(Dispatch.call(this, "ItemOfNumber", new Variant(lastParam)).
                      toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void addAll() {
    Dispatch.call(this, "AddAll");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type IOutputProbes
   */
  public IOutputProbes clone1() {
    return new IOutputProbes(Dispatch.call(this, "Clone").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void removeAll() {
    Dispatch.call(this, "RemoveAll");
  }

}
