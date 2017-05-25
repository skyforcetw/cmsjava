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
public class GT_COUNT {
    public static void main(String[] args) {
        int[] f = {1, 3, 5, 7, 9};
//        int[] f = {1, 3, 9, 12, 15};
        int[] g = {2, 3, 4, 7, 8};
//        System.out.println(gtcount(f, g));

        System.out.println("2: " + gtcount2(f, g));
        System.out.println("ans: " + gtcount_ans(f, g));

        int n = 10000000;
        long start = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            gtcount2(f, g);
        }
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            gtcount_ans(f, g);
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static int gtcount(int[] f, int[] g) {
        int flength = f.length;
        int glength = g.length;

        int count = 0;
        for (int x = 0; x < flength; x++) {
            int f0 = f[x];
            for (int y = 0; y < glength; y++) {
                if (f0 > g[y]) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int gtcount2(int[] f, int[] g) {
        int count = 0;
        int yindex = 0;

        for (int x = 0; x < f.length; x++) {
            int f0 = f[x];
            count += (yindex);
            for (; yindex < g.length && f0 > g[yindex]; yindex++) {
                count++;
            }
        }
        return count;
    }

    private static int gtcount_ans(int[] f, int[] g) {
        int count = 0;
        int indexf = 0, indexg = 0;
        while (indexf < f.length && indexg < g.length) {
            if (f[indexf] <= g[indexg]) {
                indexf++;
            } else {
                indexg++;
                count += f.length - indexf;
            }

        }
        return count;

    }
}
