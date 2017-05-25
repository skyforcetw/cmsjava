package auo.mura.evaluate;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DeltaEvaluator {

  public static void main(String[] args) {
    int[] blocks = new int[] {
        4,
        8,
        12,
        16,
        20,
        24,
        28,
        30,
        32,
        48,
        54,
        64,
    };

    for (int block : blocks) {
//      for (int c = 1; c < 1024; c++) {
          for (int c = 701; c < 702; c++) {
        for (int h = 1; h < block; h++) {
          double d = ( (double) h) / block * c;

          int piece = 1024/block;

          short dx= (short) ((piece << 10 / block) & 1023) ;
          int i = dx* c;
          System.out.println(d-i);
//          h*c
        }
      }
    }
  }
}
