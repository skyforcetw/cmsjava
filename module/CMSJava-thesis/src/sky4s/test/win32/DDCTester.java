package sky4s.test.win32;

import org.jawin.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DDCTester {

  public static void main(String[] args) {
    try {
      FuncPtr initDDCHelper = new FuncPtr("DDCHelperLT.dll", "InitDDCHelper");
      initDDCHelper.invoke_I(ReturnFlags.CHECK_W32);
      initDDCHelper.close();

      FuncPtr enumGetFirst = new FuncPtr("DDCHelperLT.dll", "EnumGetFirst");
      enumGetFirst.invoke_I(ReturnFlags.CHECK_W32);

      DispatchPtr ptr = new DispatchPtr();
    }
    catch (COMException ex) {
      ex.printStackTrace();
    }
  }
}
