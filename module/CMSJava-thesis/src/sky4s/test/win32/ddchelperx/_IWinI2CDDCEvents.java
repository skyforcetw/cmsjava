/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package sky4s.test.win32.ddchelperx;

import com.jacob.com.*;

public class _IWinI2CDDCEvents
    extends Dispatch {

  public static final String componentName = "DDCHelperXLib._IWinI2CDDCEvents";

  public _IWinI2CDDCEvents() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public _IWinI2CDDCEvents(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public _IWinI2CDDCEvents(String compName) {
    super(compName);
  }

}
