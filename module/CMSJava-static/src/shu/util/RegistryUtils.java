package shu.util;

import java.io.*;
import java.util.*;

import com.ice.jni.registry.*;
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
public class RegistryUtils {

  public final static int getDataAsInteger(String registryKey,
                                           String registryVal) {
    byte[] data = getData(registryKey, registryVal);
    return data[0] - '0';
  }

  protected final static void setData(String registryKey, String registryVal,
                                      byte[] data) {
    RegistryKey key = getRegistryKey(registryKey, true);
    try {
      key.setValue(new RegStringValue(key, registryVal, new String(data)));
      key.closeKey();
    }
    catch (NoSuchValueException ex) {
      Logger.log.error("", ex);
    }
    catch (RegistryException ex) {
      Logger.log.error("", ex);
    }
  }

  protected final static void setData(String registryKey, String registryVal,
                                      int data) {
    byte[] byteData = new byte[] {
        (byte) (data + '0')};
    setData(registryKey, registryVal, byteData);
  }

  public final static RegistryKey getRegistryKey(String registryKey,
                                                 boolean writing) {
    Registry registry = new Registry();
    StringTokenizer tokenizer = new StringTokenizer(registryKey, "\\");
    RegistryKey key = null;
    int access = writing ? RegistryKey.ACCESS_ALL : RegistryKey.ACCESS_DEFAULT;

    if (tokenizer.hasMoreTokens()) {
      key = registry.getTopLevelKey(tokenizer.nextToken());
    }
    else {
      throw new IllegalArgumentException("registryKey has no TopLevelKey");
    }

    try {
      while (tokenizer.hasMoreTokens()) {
        String next = tokenizer.nextToken();
        key = key.openSubKey(next, access);
      }
      return key;
    }
    catch (NoSuchKeyException ex) {
      Logger.log.error("", ex);
    }
    catch (RegistryException ex) {
      Logger.log.error("", ex);
    }
    return null;
  }

  public final static byte[] getData(String registryKey, String registryVal) {
    RegistryKey key = getRegistryKey(registryKey, false);
    try {
      RegistryValue val = key.getValue(registryVal);
      return val.getByteData();
    }
    catch (NoSuchValueException ex) {
      Logger.log.error("", ex);
    }
    catch (RegistryException ex) {
      Logger.log.error("", ex);
    }
    return null;
  }

  public static void main(String[] args) {
    String registryKey =
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ICM\\mntr";
    RegistryKey key = getRegistryKey(registryKey, false);

    try {
//      System.out.println(key);

      Enumeration enumer = key.valueElements();
//      System.out.println(key.getName());

      System.out.println(key.getNumberValues());
      while (enumer.hasMoreElements()) {
        Object o = enumer.nextElement();
//        System.out.println(o instanceof String);
        String value = (String) o;
        try {
//          value.getBytes("unicode");
          value = new String(value.getBytes("ISO-8859-1"), "ms950");
        }
        catch (UnsupportedEncodingException ex1) {
          ex1.printStackTrace();
        }
        System.out.println(value);

//        RegistryValue rv = key.getValue( (String) value);
//        System.out.println(rv);
      }
    }
    catch (RegistryException ex) {
      ex.printStackTrace();
    }
  }

}
