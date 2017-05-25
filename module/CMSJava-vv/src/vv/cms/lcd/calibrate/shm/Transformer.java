package vv.cms.lcd.calibrate.shm;

import java.io.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class Transformer {

  private static ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
  private static DataOutputStream dos = new DataOutputStream(bos);

  public final static DataInputStream getDataInputStream(final byte[] byteArray) {
    ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bis);
    return dis;
  }

  public final static DataOutputStream getDataOutputStream(int size) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
    DataOutputStream dos = new DataOutputStream(bos);
    return dos;
  }

  public final static int readInt(DataInputStream dis) throws
      IOException {
    byte[] ba = new byte[4];
    dis.read(ba);
    int result = Transformer.byteArray2Int(ba);
    return result;
  }

  public final static int readShort(DataInputStream dis) throws
      IOException {
    byte[] ba = new byte[2];
    dis.read(ba);
    int result = Transformer.byteArray2Short(ba);
    return result;
  }

  public final static int byteArray2Int(final byte[] byteArray) throws
      IOException {
    DataInputStream dis = getDataInputStream(byteArray);
    return dis.readInt();
  }

  public final static short byteArray2Short(final byte[] byteArray) throws
      IOException {
    DataInputStream dis = getDataInputStream(byteArray);
    return dis.readShort();
  }

  public final static byte[] int2ByteArray(final int integer) {
    try {
      bos.reset();
      dos.writeInt(integer);
      dos.flush();
    }
    catch (IOException ex) {
      return null;
    }
    return bos.toByteArray();
  }

  public final static byte[] short2ByteArray(final short value) {

    try {
      bos.reset();
      dos.writeShort(value);
      dos.flush();
    }
    catch (IOException ex) {
      return null;
    }
    return bos.toByteArray();
  }

  public static void main(String[] args) {
//    byte[] ba = int2ByteArray(258);
//    int i = byteArray2Int(ba);
//    System.out.println(i);
//    System.out.println(Arrays.toString(int2ByteArray(258)));
//    System.out.println(Arrays.toString(short2ByteArray( (short) 258)));
//    int i = 7;
    int i = 161245152;
    System.out.println(Integer.toString(i).length());
    System.out.println(Integer.toBinaryString(i));
    System.out.println(Integer.highestOneBit(i));
    System.out.println(Integer.lowestOneBit(i));
    System.out.println(Integer.numberOfLeadingZeros(i));
    System.out.println(Integer.numberOfTrailingZeros(i));
    System.out.println(Integer.bitCount(i));
  }
}
