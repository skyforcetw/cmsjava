package shu.util.shm;

import java.io.*;

import org.apache.commons.collections.primitives.*;
import shu.util.log.*;

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
public class ShareMemoryAdapter
    implements ShareMemoryObserver {
  /**
   * onDataReady
   *
   */
  public void onDataReady() {
    onDataReady( -1);
//    onDataReady(881);
  }

  public static interface DataReadyListener {
    public void dataReady();
  }

  private DataReadyListener dataReadyListener;
  public void setDataReadyListener(DataReadyListener listener) {
    this.dataReadyListener = listener;
  }

  /**
   * onDataReady
   *
   * @param datasize int
   */
  public void onDataReady(int datasize) {
    if (datasize != -1) {
      inputBuffer = shm.readByteArrayFromMem(datasize);
//      Logger.log.trace("Read from shm.");
    }
    else {
      inputBuffer = shm.readByteArrayFromMem();
//      Logger.log.trace("Read from shm.");
    }
    inputBufferIndex = 0;
    if (dataReadyListener != null) {
      dataReadyListener.dataReady();
    }
  }

  private int inputBufferIndex;
  private byte[] inputBuffer;

  public ShareMemoryAdapter() {
    shm = new ShareMemory(this);
  }

  private AdapterInputStream inputStream = new AdapterInputStream();
  private AdapterOutputStream outputStream = new AdapterOutputStream();

  private class AdapterInputStream
      extends InputStream {
    /**
     * ConnectorInputStream
     *
     */
    public AdapterInputStream() {
      super();
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *   stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read() throws IOException {
      if (inputBuffer == null) {
        throw new IOException("inputBuffer == null");
      }
      else {
        return inputBuffer[inputBufferIndex++];
      }
    }

    public int read(byte b[], int off, int len) throws IOException {
      if (b == null) {
        throw new NullPointerException();
      }
      else if (off < 0 || len < 0 || len > b.length - off) {
        throw new IndexOutOfBoundsException();
      }
      else if (len == 0) {
        return 0;
      }

      int c = read();
      b[off] = (byte) c;

      int i = 1;
      try {
        for (; i < len; i++) {
          c = read();
          b[off + i] = (byte) c;
        }
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
      return i;
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking by the next invocation of
     * a method for this input stream.
     *
     * @return an estimate of the number of bytes that can be read (or skipped
     *   over) from this input stream without blocking or {@code 0} when it
     *   reaches the end of the input stream.
     * @throws IOException if an I/O error occurs.
     */
    public int available() throws IOException {
      if (inputBuffer == null) {
        return 0;
      }
      else {
        return inputBuffer.length - inputBufferIndex;
      }
    }

    public void reset() {
      inputBufferIndex = 0;
    }

  }

  private class AdapterOutputStream
      extends OutputStream {
    /**
     * ConnectorOutputStream
     *
     */
    public AdapterOutputStream() {
      super();
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b the <code>byte</code>.
     * @throws IOException if an I/O error occurs. In particular, an
     *   <code>IOException</code> may be thrown if the output stream has been
     *   closed.
     */
    public void write(int b) throws IOException {
      outputBuffer.add( (byte) b);
    }

    private ArrayByteList outputBuffer = new ArrayByteList();

    /**
     * Flushes this stream by writing any buffered output to the underlying
     * stream.
     *
     * @throws IOException If an I/O error occurs
     */
    public void flush() throws IOException {
      byte[] byteArray = outputBuffer.toArray();
      if (byteArray.length != 0) {
        shm.writeToMem(byteArray);
//        Logger.log.trace("Write to shm.");
        outputBuffer.clear();
      }
    }

  }

  private ShareMemory shm;
  public static void main(String[] args) throws IOException {
//    ShareMemoryAdapter c = new ShareMemoryAdapter();
//    OutputStream o = c.getOutputStream();
//    InputStream i = c.getInputStream();
    byte[] ba = new byte[] {
        0, 0, 0, (byte) 0xb4};
    int i = ba[3];
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    int i2 = dis.readInt();
    dis.reset();
    for (int x = 0; x < 4; x++) {
//      byte b = (byte) dis.read();
      int b = dis.read();
      System.out.println(b);
    }
    System.out.println(i + " " + i2);
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public boolean isInputStreamAvailable() {
    return inputBuffer != null;
  }

  public DataInputStream getDataInputStream() {
    return new DataInputStream(inputStream);
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public DataOutputStream getDataOutputStream() {
    return new DataOutputStream(outputStream);
  }
}
