/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.bigatti.it/projects/jacobgen)
 */
package shu.util.dll.test;

import com.jacob.com.*;

public class _ALU
    extends Dispatch {

  public static final String componentName = "ALUDemo._ALU";

  public _ALU() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public _ALU(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public _ALU(String compName) {
    super(compName);
  }

  public Variant add(short x, short lastParam) {
    return Dispatch.call(this, "Add", new Variant(x), new Variant(lastParam));
  }

  public void hello(String lastParam) {
    Dispatch.call(this, "hello", lastParam);
  }

  public String getHelloWorld() {
    return Dispatch.get(this, "HelloWorld").toString();
  }

}
