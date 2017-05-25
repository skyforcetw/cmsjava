package shu.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

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
 * @author not attributable
 * @version 1.0
 */
public class OptionsFile {

  private Properties properties = new Properties();
  private File xmlFile;
  private File propertyFile;
  private String description;
  private Class targetClass;
  private final static String True = "true";
  private final static String False = "false";

  private final void setXMLFilename(String filename) {
    xmlFile = new File(filename);
  }

  private final void setPropertyFilename(String filename) {
    propertyFile = new File(filename);
  }

  public OptionsFile(String xmlFilename, String propertyFilename, String desc,
                     Class c) {
    init(xmlFilename, propertyFilename, desc, c);
  }

  private final void init(String xmlFilename, String propertyFilename,
                          String desc, Class c) {
    setXMLFilename(xmlFilename);
    setPropertyFilename(propertyFilename);
    description = desc;
    targetClass = c;
    initFields();
    properties.setProperty("ClassName", c.getName());
  }

  private final void initFields() {
    //若xml檔存在則從xml檔載入
    if (xmlFile.exists()) {
      Logger.log.info(xmlFile.getName() +
                      " exists, options prefer load from file.");
      try {
        //先從xml載入到properties
        properties.loadFromXML(new FileInputStream(xmlFile));
        //再從properties載到成員中
        loadFromProperties();
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }
    else {
      Logger.log.info(xmlFile.getName() +
                      " is not exists, options load from class.");
    }
  }

  /**
   * @deprecated
   */
  public void store() {
    storeToProperties();

    //==========================================================================
    // 再將properties存到xml
    //==========================================================================
    try {
//      properties.storeToXML(new FileOutputStream(xmlFile), description);
      properties.store(new FileOutputStream(propertyFile), description);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================
  }

  private void storeToProperties() {
    //==========================================================================
    // 先從成員轉到properties
    //==========================================================================
    Class c = targetClass;
    try {
      for (Field f : c.getDeclaredFields()) {
        f.setAccessible(true);
        Class type = f.getType();
        String name = getPropertyName(f);
//        System.out.println(name);

        if (type == Boolean.TYPE) {
          boolean b = f.getBoolean(c);
          properties.setProperty(name, b ? True : False);
        }
        else if (type == String.class) {
          String s = (String) f.get(c);
          properties.setProperty(name, s);
        }

      }
    }
    catch (IllegalAccessException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalArgumentException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================
  }

  public void storeToXML() {
    storeToProperties();

    //==========================================================================
    // 再將properties存到xml
    //==========================================================================
    try {
      properties.storeToXML(new FileOutputStream(xmlFile), description);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================
  }

  private void loadFromProperties() {
    if (xmlFile.exists()) {
      Class c = targetClass;
      try {
        for (Field f : c.getDeclaredFields()) {
          f.setAccessible(true);
          Class type = f.getType();
          String name = getPropertyName(f);
          String property = properties.getProperty(name);

          if (property != null) {
            if (type == Boolean.TYPE) {
              boolean b = getBoolean(property);
              f.setBoolean(c, b);
//              f.setAccessible(false);
//              f.set(c, b);
//              System.out.println(f.isAccessible());
            }
            else if (type == String.class) {
              f.set(c, property);
            }
          }
        }
      }
      catch (IllegalAccessException ex) {
        Logger.log.error("", ex);
      }
      catch (IllegalArgumentException ex) {
        Logger.log.error("", ex);
      }
    }
  }

  private final static String getPropertyName(Field f) {
    String name = f.getName();
    name = name.replace('_', '.');
    return name;
  }

  private final static boolean getBoolean(String property) {
    return property.equals(True);
  }

  public final String getString(String name) {
    return getString(name, targetClass);
  }

  private final static String getString(String name, Class c) {
    try {
      Field f = c.getDeclaredField(name);
      f.setAccessible(true);
      return (String) f.get(c);
    }
    catch (SecurityException ex) {
      Logger.log.error("", ex);
    }
    catch (NoSuchFieldException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalArgumentException ex) {
      Logger.log.error("", ex);
    }
    throw new IllegalArgumentException("");

  }

  public final boolean get(String name) {
    return get(name, targetClass);
  }

  private final static boolean get(String name, Class c) {
    try {
      Field f = c.getDeclaredField(name);
      f.setAccessible(true);
      return f.getBoolean(c);
    }
    catch (SecurityException ex) {
      Logger.log.error("", ex);
    }
    catch (NoSuchFieldException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalArgumentException ex) {
      Logger.log.error("", ex);
    }
    throw new IllegalArgumentException("");
  }
}
