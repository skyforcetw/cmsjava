package sky4s.test;

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
public class StaticClassTester {
  protected static int x = 0;
  public static void fun(int x) {

  }

  public static class A {
    public static void fun() {
      x = 1;
//      fun(3);
    }
  }

  public static void main(String[] args) {
    StaticClassTester staticclasstester = new StaticClassTester();
  }
}
