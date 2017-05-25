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
public class PRIME1 {
    public static void main(String[] args) {
//        answer();
//        for (int d : ans_1(200)) {
//            System.out.println(d);
//        }
//        System.out.println(Math.sqrt(121));
        long start = System.currentTimeMillis();
        for (int x = 0; x < 100000; x++) {
            ans_1(200);
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int x = 0; x < 100000; x++) {
            prime1();
        }
        System.out.println(System.currentTimeMillis() - start);

    }

    static void prime1() {
        final int MAXSIZE = 200;
        int prime[] = new int[MAXSIZE];
        int count = 1;
        int i, j, is_prime;

        prime[0] = 2;
        for (i = 3; i <= 1223; i++) {
            is_prime = 1;
            for (j = 0; prime[j] * prime[j] <= i && 1 == is_prime; j++) {
                if (i % prime[j] == 0) {
                    is_prime = 0;
                }
            }
            if (1 == is_prime) {
                prime[count++] = i;
            }
        }
    }

    static int[] ans_1(int n) {
        int[] primearray = new int[n];
        int primeindex = 0;
        primearray[primeindex++] = 2;
        primearray[primeindex++] = 3;
        primearray[primeindex++] = 5;

        for (int x = 1; primeindex < n; x++) {
            int num1 = x * 6 + 1;

            if (isPrime(num1, primearray, primeindex)) {
                primeindex++;
            }
            int num2 = x * 6 + 5;
            if (primeindex < n && isPrime(num2, primearray, primeindex)) {
                primeindex++;
            }

        }
        return primearray;
    }

    static boolean isPrime(int num, int[] primearray, int primeindex) {
//        double sqrt = Math.sqrt(num);
        for (int prime : primearray) {
            if (prime * prime > num) {
                primearray[primeindex] = num;
                return true;
            } else if (0 == num % prime) {
                return false;
            }
        }
        primearray[primeindex] = num;
        return true;
    }
}
