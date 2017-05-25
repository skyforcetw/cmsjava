package sky4s.test;

import java.util.*;

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
public class ListTester {

  public static void main(String[] args) {
    List<Integer> list = new ArrayList<Integer> ();
    list.add(1);
    list.add(10);
    list.add(3);
    Collections.sort(list);
    System.out.println(Arrays.toString(list.toArray()));
  }
}
