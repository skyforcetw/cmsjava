package sky4s.collect100;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Digits {
    public static void main(String[] args) {
//        int number = 12345;

//        for (int x = 0; x < 10; x++) {
//            System.out.println(x + " " + Integer.toBinaryString(x));
//        }


        for (int x = 1; x <= 5; x++) {
            int num = (int) (Math.pow(10, x) - 1);
            System.out.println(num + " " + Integer.toBinaryString(num));
        }
    }
}
