package shu.util.shm;

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
public class ShareMemory {

  static {
    System.loadLibrary("ShareMemoryLib");
  }

  public native void writeToMem(byte[] content);

  public native byte[] readByteArrayFromMem();

  public native byte[] readByteArrayFromMem(int datasize);

  private ShareMemoryObserver observer;
  public ShareMemory(ShareMemoryObserver observer) {
    this.observer = observer;
    init();
  }

  public ShareMemory() {
    this(null);
  }

  public void fireDataReadyEvent() {
    if (observer != null) {
      observer.onDataReady();
    }
    else {
      throw new IllegalStateException("ShareMemoryObserver is null.");
    }
  }

  public void fireDataReadyEvent(int datasize) {
    if (observer != null) {
      observer.onDataReady(datasize);
    }
    else {
      throw new IllegalStateException("ShareMemoryObserver is null.");
    }

  }

  private native boolean init();

  public native void destroy();
}
