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
public class EQ_COUNT {
    public static void main(String[] args) {
        int[] f = {1, 3, 4, 7, 9};
        int[] g = {3, 5, 7, 8, 10};

        System.out.println(eq_count(f, g));
        System.out.println(eq_count2(f, g));
        System.out.println(eq_count_ans(f, g));

        int n = 20000000;
        long start = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            eq_count(f, g);
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            eq_count2(f, g);
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            eq_count_ans(f, g);
        }
        System.out.println(System.currentTimeMillis() - start);

    }


    static int eq_count2(int[] f, int[] g) {
        int x = 0, y = 0;
        int count = 0;
        while (x < f.length && y < g.length) {
            if (f[x] == g[y]) {
                count++;
                x++;
                y++;
            } else if (g[y] < f[x]) {
                y++;
            } else {
                x++;
            }

        }

        return count;
    }

    static int eq_count_ans(int[] f, int[] g) {
        int indexf, indexg;
        int count;
        count = indexf = indexg = 0;
        while (indexf < f.length && indexg < g.length) {
            if (f[indexf] < g[indexg]) {
                indexf++;
            } else if (f[indexf] > g[indexg]) {
                indexg++;
            } else {
                count++;
                indexf++;
                indexg++;
            }

        }
        return count;
    }

    static int eq_count(int[] f, int[] g) {
        int y = 0;
        int count = 0;
        for (int x = 0; x < f.length && y < g.length; x++) {

            if (f[x] == g[y]) {
                count++;
                y++;
            }
            for (; y < g.length && g[y] <= f[x]; y++) {
                if (f[x] == g[y]) {
                    count++;
                }
            }

        }
        return count;
    }
}
