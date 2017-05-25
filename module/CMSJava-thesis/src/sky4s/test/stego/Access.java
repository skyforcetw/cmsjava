package sky4s.test.stego;

/**
 * <p>Title: Colour Management System - thesis</p>
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
public class Access {

  static int setBit(int source, int bit, int data) { // 要更動的值，更動的第幾個位元數，更動的數字
    return data << (bit - 1) & 255 | ~ (1 << (bit - 1)) & source;
  }

  static int getBit(int source, int bit) {
    return source >> (bit - 1) & 1;
  }

  private static int getMask(int length) {
    return (int) Math.pow(2, length) - 1;
  }

  /**
   *
   * @param source int 要取得bits data的來源
   * @param offset int 要取得bits的起點, 由lsb開始計算的位移
   * @param length int 要取得bits的長度
   * @return int 回傳的bits data, 一率以lsb為最右邊的位元
   */
  public static int getBits(int source, int offset, int length) {
    int mask = getMask(length); //建立遮罩
    mask = mask << offset; //將遮罩移動到目標處
    int result = source & mask; //將來源作遮罩, 挖出資料
    result = result >> offset; //將資料位移回起點
    return result;
  }

  /**
   *
   * @param source int 要被設定bits data的來源
   * @param setdata int 要設定的data
   * @param offset int 要放到source中的起點, 由lsb開始計算的位移
   * @param length int 要設定的data長度
   * @return int 設定好的bits data
   */
  public static int setBits(int source, int setdata, int offset,
                            int length) {
    int mask = getMask(length); //建立遮罩
    mask = ~ (mask << offset) & 255; //將遮罩作反遮罩,並且位移到目標處
    int result = source & mask; //把source挖開一個洞, 方便填資料
    result |= setdata << offset; //把資料作位移並且填進去
    return result;
  }

  public static void main(String[] args) {
    int data = 12; //                         00001100
    int data1 = getBits(data, 2, 2); //           ^^<<  ^代表取的位元, <代表位移量
    System.out.println(data1); //取得 00000011, 所以是3
    int data2 = setBits(data, data1, 0, 2); // 00001100 + 00000011 = 00001111 =15
    System.out.println(data2);

  }
}
