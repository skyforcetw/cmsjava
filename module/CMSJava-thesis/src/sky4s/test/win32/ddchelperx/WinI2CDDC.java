/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package sky4s.test.win32.ddchelperx;

import com.jacob.com.*;

public class WinI2CDDC
    extends IWinI2CDDC {

  public static final String componentName = "DDCHelperX.WinI2CDDC";

  public WinI2CDDC() {
    super(componentName);
  }

  public WinI2CDDC(Dispatch d) {
    super(d);
  }

  public static void main(String[] args) {
    WinI2CDDC ddc = new WinI2CDDC();
    int result = ddc.initDDCHelper();
    System.out.println("msg:" + result);
    int[] parm = new int[100];
    result = ddc.enumGetFirst(parm);
    System.out.println("msg:" + result);
    Variant EDID = new Variant();
    result = ddc.readEDID(parm[0], EDID);
    System.out.println("msg:" + result);
    System.out.println(EDID);
    String desc = "";
    result = ddc.getEDIDOption(EDID, 6, desc, 256);
    System.out.println(desc);
    System.out.println("msg:" + result);

  }
}
