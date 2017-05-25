package shu.cms.measure.meterapi.hubbleapi;

import java.util.List;

import org.xvolks.jnative.misc.basicStructures.HWND;

import org.xvolks.jnative.util.User32;

//import com.syct.jnative.wrapper.DataPackageCallback;

public class MyEnumCallback
    extends DataPackageCallback {

  StringBuffer sb = new StringBuffer();
  public String getWindowEnumList() {
    return sb.toString();
  }

  /**
   * MyEnumCallback要?承DataPackageCallback
   * ??方法得到回???的值
   */
  @Override
  protected void ProcessCallbackDataPackage(List<Long> dataPackage) {
    // TODO Auto-generated method stub

    for (Long key : dataPackage) {
      try {
        //System.err.println("Handle : " + key);
        String name = User32.GetWindowText(new HWND(key.intValue()));
        //  System.err.println("Name : " + name);
        if (name == null || name.length() == 0) {
          //  System.err.println("Skipping handle " + key);
        }
        else {
          sb.append(name).append("\n");
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
