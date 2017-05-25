package shu.cms.applet.benchmark;

import java.lang.reflect.*;
import java.util.*;

import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ParameterBlock {

  public final static void setParameterBlock(Object object,
                                             ParameterBlock parameterBlock) {
    for (Parameter p : parameterBlock.getParameterList()) {
      setParameterBlock(object, p);
    }
  }

  public final static void setParameterBlock(Object object, Parameter p) {
    Class c = object.getClass();

    String name = p.name;
    String methodName = "set" +
        Character.toUpperCase(p.name.charAt(0)) +
        name.substring(1, name.length());
    Method method = null;

    try {
      switch (p.type) {
        case Double:
          method = c.getMethod(methodName, Double.TYPE);
          break;
        case String:
          method = c.getMethod(methodName, String.class);
          break;
        case Integer:
          method = c.getMethod(methodName, Integer.TYPE);
          break;
      }
      Object[] paramObjs1 = {
          p.value};
      method.invoke(object, paramObjs1);
    }
    catch (SecurityException ex) {
      Logger.log.error("", ex);
    }
    catch (NoSuchMethodException ex) {
      Logger.log.error("", ex);
    }
    catch (InvocationTargetException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalArgumentException ex) {
      Logger.log.error("", ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.error("", ex);
    }

  }

  public static class Parameter {
    public String name;
    public Object value;
    public Type type;
    Parameter(String name, Object value) {
      this(name, value, null);
    }

    private Parameter(String name, Object value, Type type) {
      this.name = name;
      this.value = value;
      this.type = type;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return name + " = " + value + " [" + type + "]";
    }
  }

  public static enum Type {
    Double, String, Integer
  }

  private List<Parameter> parameterList = new ArrayList<Parameter> ();

  public void set(String parameterName, double value) {
    set(parameterName, (Object) value, Type.Double);
  }

  private void set(String parameterName, Object value, Type type) {
    parameterList.add(new Parameter(parameterName, value, type));
  }

  public void set(String parameterName, String value) {
    set(parameterName, (Object) value, Type.String);
  }

  public void set(String parameterName, int value) {
    set(parameterName, (Object) value, Type.Integer);
  }

  List<Parameter> getParameterList() {
    return parameterList;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Parameter p : parameterList) {
      buf.append(p);
      buf.append('\n');
    }
    return buf.toString();
  }

  public static void main(String[] args) {
    ParameterBlock block = new ParameterBlock();
    block.set("d1", 1.0);
    block.set("i2", 1);
    block.set("s3", "1234");
    System.out.println(block);
  }
}
