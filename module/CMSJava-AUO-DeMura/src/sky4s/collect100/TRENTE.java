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
public class TRENTE {
    public static void main(String[] args) {
//        ans_1();
        long start = System.currentTimeMillis();
        ans_2();
        System.out.println(System.currentTimeMillis() - start);
    }

    static void ans_2() {
        for (int V = 1; V <= 9; V++) {
            for (int I = 0; I <= 9; I++) {
                if (I == V) {
                    continue;
                }

                for (int N = 0; N <= 9; N++) {
                    if (N == I || N == V) {
                        continue;
                    }

                    for (int G = 0; G <= 9; G++) {
                        if (G == N || G == N || G == I || G == V) {
                            continue;
                        }

                        for (int T = 1; T <= 9; T++) {
                            if (T == G || T == G || T == N || T == I || T == V) {
                                continue;
                            }

                            int VINGT = V * 10000 + I * 1000 + N * 100 + G * 10 + T;

                            for (int C = 1; C <= 9; C++) {
                                if (C == T || C == G || C == N || C == I || C == V) {
                                    continue;
                                }
                                for (int Q = 0; Q <= 9; Q++) {
                                    if (Q == C || Q == T || Q == G || Q == N || Q == I || Q == V) {
                                        continue;
                                    }
                                    int CINQ = C * 1000 + I * 100 + N * 10 + Q;

                                    for (int R = 0; R <= 9; R++) {
                                        if (R == Q || R == C || R == T || R == G || R == N || R == I || R == V) {
                                            continue;
                                        }
                                        for (int E = 0; E <= 9; E++) {
                                            if (E == R || E == Q || E == C || E == T || E == G || E == N || E == I ||
                                                E == V) {
                                                continue;
                                            }
                                            int TRENTE = T * 100000 + R * 10000 + E * 1000 + N * 100 + T * 10 + E;

                                            if ((VINGT + 2 * CINQ) == TRENTE) {
                                                System.out.println(VINGT + " " + CINQ + " " + TRENTE);
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }
    }

    static void ans_1() {
        for (int v = 1; v <= 9; v++) {
            for (int i = 0; i <= 9 && i != v; i++) {
                for (int n = 0; n <= 9 && n != i; n++) {
                    for (int g = 0; g <= 9 && g != n; g++) {
                        for (int t = 1; t <= 9 && t != g; t++) {

                            for (int c = 1; c <= 9 && c != t; c++) {
                                for (int q = 0; q <= 0 && q != c; q++) {

                                    for (int r = 0; r <= 9 && r != q; r++) {
                                        for (int e = 0; e <= 9 && e != r; e++) {

                                            int vingt = v * 10000 + i * 1000 + n * 100 + g * 10 + t;
                                            int cinq = c * 1000 + i * 100 + n * 10 + q;
                                            int trente = t * 100000 + r * 10000 + e * 1000 + n * 100 + t * 10 + e;

                                            if ((vingt + 2 * cinq) == trente) {
                                                System.out.println(vingt + " " + cinq + " " + trente);
//                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
