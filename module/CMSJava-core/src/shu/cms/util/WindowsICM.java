package shu.cms.util;

import java.io.*;

import com.ice.jni.registry.*;
import shu.util.*;
import shu.util.log.*;

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
public class WindowsICM {
//  public final static String PROFILE_DIRECTORY ="


  public final static File[] getMonitorProfileName() {
    String dirname = getProfileDirectory();
    File dir = new File(dirname);
    return dir.listFiles();
  }

  public final static String getProfileDirectory() {
    RegistryKey key = RegistryUtils.getRegistryKey(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", false);
    try {
      return new String(key.getValue("SystemRoot").getByteData()) +
          "\\system32\\spool\\drivers\\color";
    }
    catch (NoSuchValueException ex) {
      Logger.log.error("", ex);
      return null;
    }
    catch (RegistryException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    for (File f : getMonitorProfileName()) {
      System.out.println(f);
      System.out.println(f.getName());
    }
  }

}
