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
public class PlateAU {
    public static void main(String[] args) {
        int[] array = {1, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5};
        String strarray = arrayToString(array);
//        System.out.println(strarray);
        System.out.println(plateau1(array));
        System.out.println(plateau2(array));
        System.out.println(plateau_ans(array));
        System.out.println(plateau3(array));
        System.out.println(plateauString(strarray));
    }

    static int plateauString(String str) {
        int length = 1;
        for (int x = 1; x < str.length(); x++) {
            if (str.charAt(x) == str.charAt(x - length)) {
                length++;
            }
        }
        return length;
    }

    static String arrayToString(int[] array) {
        char[] chararray = new char[array.length];
        for (int x = 0; x < array.length; x++) {
            chararray[x] = Character.forDigit(array[x], 10);
        }
        return new String(chararray);
    }


    public static int plateau_ans(int[] array) {
        int length = 1;

        for (int x = 1; x < array.length; x++) {
            if (array[x] == array[x - length]) {
                length++;
            }
        }
        return length;
    }

    public static int plateau3(int[] array) {
        int length = 1;

        for (int x = 1; x < array.length; x++) {
            if (array[x] == array[x - length]) {
                length++;
            }
        }
        return length;
    }


    public static int plateau2(int[] array) {
        int maxlength = 0;
        int lastchange = 0;

        for (int x = 1; x < array.length; x++) {
            if (array[x] != array[x - 1]) {
                maxlength = maxlength < (x - lastchange) ? x - lastchange : maxlength;
                lastchange = x;
            }
        }
        return maxlength;
    }

    public static int plateau1(int[] array) {

        int length = 0;
        int maxlength = 0;
        for (int x = 1; x < array.length; x++) {
            if (array[x] == array[x - 1]) {
                length++;
                maxlength = maxlength < length ? length : maxlength;
            } else {
                length = 1;
            }
        }
        return maxlength;
    }
}
