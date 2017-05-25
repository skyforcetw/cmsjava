package sky4s.hud;

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
public class ELMTester {
    public static void main(String[] args) {
        byte v0 = (byte) Integer.parseInt("D8", 16);
        int v00 = Integer.parseInt("D8", 16);
        byte v1 = (byte) Integer.parseInt("AC", 16);
        int v11 = Integer.parseInt("AC", 16);
        System.out.println(v0 + " " + v1 + " " + v00);
        System.out.println((v0 * 256 + v1) / 4);
        int rpm = (v00 * 256 + v11) / 4;
//        rpm = (rpm < 0) ? 16385 + rpm : rpm;
        System.out.println(rpm);
    }
}
