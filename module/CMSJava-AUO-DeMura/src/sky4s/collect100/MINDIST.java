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
public class MINDIST {
    public static void main(String[] args) {
        int[] x = {1, 3, 5, 7, 9};
        int[] y = {2, 6, 8};

//        int[] y = {1, 3, 5, 7, 9, 10, 11, 14, 15};
//        int[] x = {2, 6, 8, 12, 13, 17};

        System.out.println(mindist_1(x, y));
        System.out.println(mindist_2(x, y));
        System.out.println(mindist_ans(x, y));
        if (true) {
            return;
        }
        int n = 30000000;

        long start = System.currentTimeMillis();
        for (int m = 0; m < n; m++) {
            mindist_1(x, y);
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int m = 0; m < n; m++) {
            mindist_2(x, y);
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int m = 0; m < n; m++) {
            mindist_ans(x, y);
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    static int mindist_1(int[] x, int[] y) {
        int mindist = Integer.MAX_VALUE;
        for (int m = 0; m < x.length; m++) {
            for (int n = 0; n < y.length; n++) {
                mindist = Math.min(mindist, Math.abs(x[m] - y[n]));
            }
        }

        return mindist;
    }

    static int mindist_2(int[] x, int[] y) {
        int indexx = 0, indexy = 0;
        int mindist = Integer.MAX_VALUE;
        int count = 0;
        while (indexx < x.length && indexy < y.length) {
            int dist = Math.abs(x[indexx] - y[indexy]);
            if (dist <= mindist) {
                mindist = dist;
                indexx++;
                indexy++;
            } else {
                indexx++;
            }
            System.out.println("count: " + count++ +" " + indexx + " " + indexy);
        }
        return mindist;
    }

    static int mindist_ans(int[] x, int[] y) {
        int indexx = 0, indexy = 0;
        int mindist = Integer.MAX_VALUE;
        int count = 0;
        while (indexx < x.length && indexy < y.length) {

            if (x[indexx] >= y[indexy]) {
                int dist = x[indexx] - y[indexy];
                mindist = mindist < dist ? mindist : dist;
                indexy++;
            } else {
                int dist = y[indexy] - x[indexx];
                mindist = mindist < dist ? mindist : dist;
                indexx++;
            }
            System.out.println("count: " + count++ +" " + indexx + " " + indexy);
        }
        return mindist;
    }

}
