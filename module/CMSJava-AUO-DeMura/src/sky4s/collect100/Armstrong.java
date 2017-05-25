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
public class Armstrong {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int x = 0; x < 300000; x++) {
            ans_1();
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int x = 0; x < 300000; x++) {
            ans_2();
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int x = 0; x < 300000; x++) {
            answer();
        }
        System.out.println(System.currentTimeMillis() - start);

        System.out.println(ans_1());
        System.out.println(ans_2());
        System.out.println(answer());
    }

    static int ans_1() {
        int count = 0;
        for (short num = 100; num <= 999; num++) {
            int digit0 = num % 10;
            int digit10 = num / 10 % 10;
            int digit100 = num / 100 % 10;
            int cubesum = digit0 * digit0 * digit0 + digit10 * digit10 * digit10 + digit100 * digit100 * digit100;

            if (num == cubesum) {
                count++;
            }
        }
        return count;
    }

    static int ans_2() {
        int count = 0;
        for (int d100 = 1; d100 <= 9; d100++) {
            int d100cube = d100 * d100 * d100;
            for (int d10 = 0; d10 <= 9; d10++) {
                int d10cube = d10 * d10 * d10;
                for (int d0 = 0; d0 <= 9; d0++) {
                    int cubesum = d100cube + d10cube + d0 * d0 * d0;
                    int num = d100 * 100 + d10 * 10 + d0;
                    if (cubesum == num) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    static int answer() {
        int count = 0;
        for (short num = 100; num <= 999; num++) {
            int digit0 = num % 10;
            int digit10 = num % 100 / 10;
            int digit100 = num / 100;
            int cubesum = digit0 * digit0 * digit0 + digit10 * digit10 * digit10 + digit100 * digit100 * digit100;

            if (num == cubesum) {
                count++;
            }
        }
        return count;
    }


}
