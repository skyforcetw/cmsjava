package shu.cms.measure.meterapi.hubbleapi.test;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;
import org.jawin.io.LittleEndianOutputStream;
import org.jawin.io.NakedByteStream;
import org.xvolks.jnative.*;
import org.xvolks.jnative.exceptions.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class HubbleTester {
  public static void main(String[] args) {
//  dllTesteR(args);
    System.out.printf("%4d", 23);
  }

  public static void dllTester(String[] args) {
//    FuncPtr sdk = null;
//    try {
//      sdk = new FuncPtr("SpotMeterSDK.dll", "_gmbSpotInitialize@0");
//      int result = sdk.invoke_I(ReturnFlags.CHECK_NONE);
//      System.out.println(result);
//    }
//    catch (COMException ex) {
//      ex.printStackTrace();
//    }

    try {
      JNative sdk2 = new JNative("SpotMeterSDK.dll",
                                 "_gmbSpotInitialize@0");
      sdk2.setRetVal(Type.LONG);
      sdk2.invoke();
//      System.out.println("\"" + sdk2.getRetVal() + "\"");
      System.out.println(sdk2.getRetValAsInt());
      System.out.println(sdk2.getRetVal());
    }
    catch (NativeException ex) {
      ex.printStackTrace();
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }

  }
}
