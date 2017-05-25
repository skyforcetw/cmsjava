package shu.cms.measure.meterapi.hubbleapi;

import java.util.ArrayList;
import java.util.List;
import org.xvolks.jnative.JNative;
import org.xvolks.jnative.exceptions.NativeException;

import org.xvolks.jnative.util.Callback;

public abstract class DataPackageCallback
    implements Callback {
  private final List<Long> dataPackage;

  public DataPackageCallback() {
    dataPackage = new ArrayList<Long> ();
  }

  public List getCallbackDataPackage() {

    ProcessCallbackDataPackage(dataPackage);
    return dataPackage;

  }

  public int callback(long[] values) {
    // TODO Auto-generated method stub

    if (values == null) {
      return 3;
    }
    if (values.length == 2) {
      try {
        if (values[0] > 0) {
          dataPackage.add(values[0]);
          System.out.println(values[1]);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      return 1;
    }
    else {
      return 2;
    }
  }

//分配?存
  public int getCallbackAddress() throws NativeException {
    // TODO Auto-generated method stub
    return JNative.createCallback(2, this);
  }

//??方法在子???
  protected abstract void ProcessCallbackDataPackage(List<Long> dataPackage);
}
