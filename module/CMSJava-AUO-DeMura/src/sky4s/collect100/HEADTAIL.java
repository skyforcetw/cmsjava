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
public class HEADTAIL {
    public static void main(String[] args) {
        int[] x = {3, 6, 2, 1, 4, 5, 2};
        System.out.println(ans_1(x));
        System.out.println(answer(x));
    }

    static int ans_1(int[] x) {

        int length = x.length;
        int headindex = 0;
        int tailindex = length - 1;
        int head = x[headindex];
        int tail = x[tailindex];
        int pair = 0;

        while (true) {
            if (head == tail) {
                pair++;
                System.out.println(head + " " + tail);
            }
            if (head > tail) {
                tailindex--;
                if (tailindex < 0) {
                    break;
                }
                tail += x[tailindex];
            } else {
                headindex++;
                if (headindex >= length) {
                    break;
                }
                head += x[headindex];
            }
        }
        return pair;
    }

    static int answer(int[] x) {

        int length = x.length;
        int headindex = 0;
        int tailindex = length - 1;
        int head = 0;
        int tail = 0;
        int pair = 0;

        while (headindex < length && tailindex >= 0) {
            if (head == tail) {
                pair++;
                head += x[headindex++];
                tail += x[tailindex--];
            } else if (head > tail) {
                tail += x[tailindex--];
            } else if (head < tail) {
                head += x[headindex++];
            }
        }
        return pair;
    }

}
