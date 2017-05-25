package sky4s.test;

import com.ice.jni.registry.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RegistryTester {
  public RegistryTester() {
  }

  public static void main(String[] args) throws Exception {

    RegistryKey powerCfgKey = Registry.HKEY_CURRENT_USER.openSubKey(
        "Control Panel").openSubKey(
            "PowerCfg");
    RegistryValue currentPowerPolicyVal = powerCfgKey.getValue(
        "CurrentPowerPolicy");
    System.out.println(currentPowerPolicyVal);
    byte[] data = currentPowerPolicyVal.getByteData();

    System.out.println(data.length);
    System.out.println(data[0]);
    System.out.println( (int) '7');
  }
}
