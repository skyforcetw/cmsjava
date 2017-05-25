package auo.cms.test;

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
public class NumberSearcher {
  public static void main(String[] args) {
    int[] numberArrary = new int[] {
        0, 0, 1, 2, 3, 6, 6, 7};
    int size = numberArrary.length;
    for (int x = 0, number = x; x < size; x++) {
//      number = x;
      if (numberArrary[x] != number) {
        if (numberArrary[x] == numberArrary[x - 1]) {
          System.out.println(number + " duplicated");
//          number--;
        }
        else {
          System.out.println(number + " lost");
          number = numberArrary[x];
        }

      }
      else {
        number++;
      }
    }
  }
}
