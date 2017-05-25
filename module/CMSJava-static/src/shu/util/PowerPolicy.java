package shu.util;

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
public final class PowerPolicy {
  protected static int screenSaveActivePersistence;
  protected static int currentPowerPolicyPersistence;
  public final static void restorePowerPolicyAndScreenSaver() {
    if (ENABLE_POWER_POLICY) {
      RegistryUtils.setData("HKEY_CURRENT_USER\\Control Panel\\PowerCfg",
                            "CurrentPowerPolicy",
                            currentPowerPolicyPersistence);
      RegistryUtils.setData("HKEY_CURRENT_USER\\Control Panel\\Desktop",
                            "ScreenSaveActive", screenSaveActivePersistence);
    }
  }

  public static boolean ENABLE_POWER_POLICY = false;

  public final static void pausePowerPolicyAndScreenSaver() {
    if (ENABLE_POWER_POLICY) {
      currentPowerPolicyPersistence = RegistryUtils.getDataAsInteger(
          "HKEY_CURRENT_USER\\Control Panel\\PowerCfg",
          "CurrentPowerPolicy");
      RegistryUtils.setData("HKEY_CURRENT_USER\\Control Panel\\PowerCfg",
                            "CurrentPowerPolicy", 3);

      screenSaveActivePersistence = RegistryUtils.getDataAsInteger(
          "HKEY_CURRENT_USER\\Control Panel\\Desktop",
          "ScreenSaveActive");
      RegistryUtils.setData("HKEY_CURRENT_USER\\Control Panel\\Desktop",
                            "ScreenSaveActive", 0);
    }
  }
}
