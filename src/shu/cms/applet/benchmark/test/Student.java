package shu.cms.applet.benchmark.test;

import java.lang.reflect.*;

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
public class Student {
  private String name;
  private int score;

  public Student() {
    name = "N/A";
  }

  public Student(String name, int score) {
    this.name = name;
    this.score = score;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public String getName() {
    return name;
  }

  public int getScore() {
    return score;
  }

  public void showData() {
    System.out.println("name: " + name);
    System.out.println("score: " + score);
  }

  public static void main(String[] args) {
    try {
//      Class c = Class.forName(args[0]);
//      Object targetObj = c.newInstance();
      Object targetObj = new Student();
      Class c = targetObj.getClass();

//      Class[] param1 = {
//          String.class};
//      Method setName = c.getMethod("setName", param1);
//
//      Object[] paramObjs1 = {
//          "caterpillar"};
//      setName.invoke(targetObj, paramObjs1);

      Class[] param2 = {
          Integer.TYPE};
      Method setScore =
          c.getMethod("setScore", param2);

      Object[] paramObjs2 = {
          new Integer(90)};
      setScore.invoke(targetObj, paramObjs2);

//      Method showData =
//          c.getMethod("showData", new Class[0]);
//      showData.invoke(targetObj, new Object[0]);

    }
//    catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
    catch (SecurityException e) {
      e.printStackTrace();
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
    }
//    catch (InstantiationException e) {
//      e.printStackTrace();
//    }
  }

}
