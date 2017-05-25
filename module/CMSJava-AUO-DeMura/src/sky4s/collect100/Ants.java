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
public class Ants {
    public static void main(String[] args) {
        ants(10, 3, new int[] {2, 6, 7});
    }

    static void ants(int L, int n, int[] x) {
        if (n != x.length) {
            throw new IllegalArgumentException();
        }
        int max = 0;
        int maxIndex = 0;
        int direction = 0;
        for (int i = 0; i < x.length; i++) {
//            System.out.println(x[i] + ":" + (10 - x[i]));
            int left = x[i];
            int right = L - x[i];
            int min = Math.min(left, right);
//            max = Math.max(min, max);
            if (min > max) {
                max = min;
                maxIndex = i;
                if (min == right) {
                    direction = 1;
                } else {
                    direction = 0;
                }
            }
        }
        System.out.println(maxIndex + " " + max + " " + (direction == 1 ? "еk" : "ек"));
    }
}
