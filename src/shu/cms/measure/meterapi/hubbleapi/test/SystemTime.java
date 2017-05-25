package shu.cms.measure.meterapi.hubbleapi.test;

import org.xvolks.jnative.*;
import org.xvolks.jnative.exceptions.*;
import org.xvolks.jnative.misc.basicStructures.*;
import org.xvolks.jnative.pointers.*;
import org.xvolks.jnative.pointers.memory.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SystemTime
    extends AbstractBasicData<SystemTime> {
  public short wYear;
  public short wMonth;
  public short wDayOfWeek;
  public short wDay;
  public short wHour;
  public short wMinute;
  public short wSecond;
  public short wMilliseconds;

  public Pointer createPointer() throws NativeException {
    pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(getSizeOf()));
    return pointer;
  }

  public int getSizeOf() {
    return 8 * 2; //8 WORDS of 2 bytes
  }

  public SystemTime getValueFromPointer() throws NativeException {
    wYear = getNextShort();
    wMonth = getNextShort();
    wDayOfWeek = getNextShort();
    wDay = getNextShort();
    wHour = getNextShort();
    wMinute = getNextShort();
    wSecond = getNextShort();
    wMilliseconds = getNextShort();
    return this;
  }

  public SystemTime() throws NativeException {
    super(null);
    createPointer();
    mValue = this;
  }

  @Override
  public String toString() {
    return wYear + "/" + wMonth + "/" + wDay + " at + " + wHour + ":" + wMinute +
        ":" + wSecond + ":" + wMilliseconds;
  }

  public static SystemTime GetSystemTime() throws Exception {
    //Create a JNative object called nGetSystemTime : here
    JNative nGetSystemTime = new JNative("Kernel32.dll", "GetSystemTime");
    //Create a SystemTime object that holds the native out structure
    SystemTime systemTime = new SystemTime();
    //Pass its pointer address as the first parameter (first is zero !!!)
    nGetSystemTime.setParameter(0, systemTime.getPointer());
    //Invoke the native GetSystemTime function
    nGetSystemTime.invoke();
    //Return the populated SystemTime object
    return systemTime.getValueFromPointer();
  }

  public static void main(String[] args) throws NativeException,
      IllegalAccessException {
    try {
      System.err.println(GetSystemTime());
    }
    catch (Exception ex) {
      ex.printStackTrace();

    }
  }

}
