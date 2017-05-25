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

  static int setBit(int source, int bit, int data) { // �n��ʪ��ȡA��ʪ��ĴX�Ӧ줸�ơA��ʪ��Ʀr
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
   * @param source int �n���obits data���ӷ�
   * @param offset int �n���obits���_�I, ��lsb�}�l�p�⪺�첾
   * @param length int �n���obits������
   * @return int �^�Ǫ�bits data, �@�v�Hlsb���̥k�䪺�줸
   */
  public static int getBits(int source, int offset, int length) {
    int mask = getMask(length); //�إ߾B�n
    mask = mask << offset; //�N�B�n���ʨ�ؼгB
    int result = source & mask; //�N�ӷ��@�B�n, ���X���
    result = result >> offset; //�N��Ʀ첾�^�_�I
    return result;
  }

  /**
   *
   * @param source int �n�Q�]�wbits data���ӷ�
   * @param setdata int �n�]�w��data
   * @param offset int �n���source�����_�I, ��lsb�}�l�p�⪺�첾
   * @param length int �n�]�w��data����
   * @return int �]�w�n��bits data
   */
  public static int setBits(int source, int setdata, int offset,
                            int length) {
    int mask = getMask(length); //�إ߾B�n
    mask = ~ (mask << offset) & 255; //�N�B�n�@�ϾB�n,�åB�첾��ؼгB
    int result = source & mask; //��source���}�@�Ӭ}, ��K����
    result |= setdata << offset; //���Ƨ@�첾�åB��i�h
    return result;
  }

  public static void main(String[] args) {
    int data = 12; //                         00001100
    int data1 = getBits(data, 2, 2); //           ^^<<  ^�N������줸, <�N��첾�q
    System.out.println(data1); //���o 00000011, �ҥH�O3
    int data2 = setBits(data, data1, 0, 2); // 00001100 + 00000011 = 00001111 =15
    System.out.println(data2);

  }
}
