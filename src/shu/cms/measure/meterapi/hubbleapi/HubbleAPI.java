package shu.cms.measure.meterapi.hubbleapi;

import org.xvolks.jnative.*;
import org.xvolks.jnative.exceptions.*;
import org.xvolks.jnative.misc.basicStructures.*;
import org.xvolks.jnative.pointers.*;
import org.xvolks.jnative.pointers.memory.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.util.log.*;
import org.xvolks.jnative.util.*;

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
public class HubbleAPI {
  public final static String HUBBLE_DLLNAME = "SpotMeterSDK.dll";

  private static Status getNonParameterCall(String functionName) throws
      NativeException, IllegalAccessException {
    JNative jnative = getJNative(functionName);
    jnative.setRetVal(Type.LONG);
    jnative.invoke();
    Status status = Status.getStatus(jnative.getRetValAsInt());
    return status;
  }

  private static JNative getJNative(String functionName) throws NativeException {
    JNative jnative = new JNative(HUBBLE_DLLNAME, functionName);
    return jnative;
  }

  static int errIndex = -100;
  static enum Status {

    Success(0), /**< Function Succeeded 		*/
    Err(errIndex++), /**< Nonspecific error 			*/
    ErrInvalidDevicePtr(errIndex++), /**< Device pointer is NULL		*/
    ErrNoDeviceFound(errIndex++), /**< No device found			*/
    ErrDeviceOpenErr(errIndex++), /**< Error opening the device 	*/
    ErrDeviceLocked(errIndex++), /**< Locked device				*/
    ErrDeviceNotOpen(errIndex++), /**< Specified device not open 	*/
    ErrFunctionNotSupported(errIndex++),
    /**< The requested Function is not supported by this device 	*/
    ErrInvalidInput(errIndex++), /**< Invalid input to function */
    ErrStringTooShort(errIndex++), /**< String address null or too short */
    ErrInvalidMatrixData(errIndex++),
    /**< Invalid calibration data location */
    ErrSaturation(errIndex++), /**< Sensor is in saturation	*/
    ErrTooFewColors(errIndex++), /**< Matrix calc. requires >3 colors */
    ErrTooManyColors(errIndex++),
    /**< Matrix calc. requires <= GMB_SPOT_MAX_COLORS colors */
    ErrNoSuchCalibration(errIndex++),
    /**< Either slot is empty or no matching name */
    ErrWrongSlot(errIndex++),
    /**< Can't write to a new slot with same name string */
    ErrAlreadyExists(errIndex++),
    /**< Can't overwrite unless requested in calling parameters */
    ErrReadOnly(errIndex++), /**< Calibration is READ-ONLY */
    ErrNoSlotsLeft(errIndex++), /**< Calibration memory is full */
    EndStatus(errIndex++); /**< High end of enumeration    */

    Status(int value) {
      this.value = value;
    }

    public final int value;
    public final static Status getStatus(int value) {
      for (Status s : Status.values()) {
        if (value == s.value) {
          return s;
        }
      }
      return null;
    }
  }

  enum LumUnits {
    FootLts(0), /**< Foot Lamberts		*/
    Nits(1);

    /**< Candelas/m^2		*/
    LumUnits(int value) {
      this.value = value;
    }

    public final int value;
    public final static LumUnits getLumUnits(int value) {
      for (LumUnits s : LumUnits.values()) {
        if (value == s.value) {
          return s;
        }
      }
      return null;
    }
  }

  public HubbleAPI() {
    initialize();
  }

  private void initialize() {
    try {
      status = getNonParameterCall("_gmbSpotInitialize@0");
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    status = null;
  }

  public void destroy() {
    try {
      status = getNonParameterCall("_gmbSpotDestroy@0");
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    status = null;
  }

  private Status status;
  public Status getStatus() {
    return status;
  }

  public int getNumberOfDevices() {
    try {
      JNative jnative = getJNative("_gmbSpotGetNumberOfDevices@0");
      jnative.setRetVal(Type.INT);
      jnative.invoke();
      int result = jnative.getRetValAsInt();
      return result;
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
    return -1;
  }

  public void setDevice(int whichDevice) {
    deviceHandle = getDeviceHandle(whichDevice);
    open();
  }

  private Pointer deviceHandle;
  private Pointer getDeviceHandle(int whichDevice) {
    try {
      JNative jnative = getJNative("_gmbSpotGetDeviceHandle@8");
      jnative.setParameter(0, whichDevice);
      Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
      jnative.setParameter(1, handle);

      jnative.setRetVal(Type.INT);
      jnative.invoke();
      status = Status.getStatus(jnative.getRetValAsInt());
      return handle;
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
//    catch (NoSuchMethodException ex) {
//      Logger.log.debug(ex);
//    }
    return null;
  }

  public String getToolkitVersion() {
    try {
      JNative jnative = getJNative("_gmbSpotGetToolkitVersion@4");
      Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(80));
      jnative.setParameter(0, handle);
      jnative.setRetVal(Type.STRING);
      jnative.invoke();
      return handle.getAsString();
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
    return null;
  }

  public void open() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotDeviceOpen@4");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
  }

  public void close() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotDeviceClose@4");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
  }

  public double getIntegrationTime() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetIntegrationTime@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(8));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return handle.getAsDouble(0);
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return -1;
  }

  public LumUnits getLuminanceUnits() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetLuminanceUnits@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return LumUnits.getLumUnits(handle.getAsInt(0));
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return null;
  }

  public void setActiveCalibrationByNumber(int number) {
    if (deviceHandle != null) {
      try {

        JNative jnative = getJNative("_gmbSpotSetActiveCalibrationByNumber@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setParameter(1, Type.INT, Integer.toString(number));
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  public void setActiveCalibrationByName(String name) {
    if (deviceHandle != null) {
      try {

        JNative jnative = getJNative("_gmbSpotSetActiveCalibrationByName@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
        handle.setStringAt(0, name);
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  public void setIntegrationTime(double seconds) {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotSetIntegrationTime@12");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setParameter(1, Type.DOUBLE,
                             new DOUBLE(seconds).getValueAsString());
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  public void setLowLightAveraging(boolean enable) {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotSetLowLightAveraging@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setParameter(0, enable ? 1 : 0);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  public boolean isButtonPressed() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotButtonIsPressed@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(1));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        boolean result = handle.getAsByte(0) == 1 ? true : false;
        return result;
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return false;
  }

  public void setLuminanceUnits(LumUnits units) {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotSetLuminanceUnits@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setParameter(1, units.value);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  public static void main(String[] args) {
    HubbleAPI hubble = new HubbleAPI();
    int n = hubble.getNumberOfDevices();
    System.out.println(n);
    hubble.setDevice(0);
//    System.out.println(hubble.getStatus());
//    hubble.open();
//    System.out.println(hubble.getStatus());
//    System.out.println(hubble.getIntegrationTime());
//    System.out.println(hubble.getStatus());
//    hubble.setIntegrationTime(5);
//    System.out.println(hubble.getIntegrationTime());
    System.out.println(hubble.getSerialNumber());
//    System.out.println(hubble.getStatus());
//    System.out.println(hubble.getToolkitVersion());
//


    System.out.println("cals: " + hubble.getNumberOfCalibrations());
    hubble.setActiveCalibrationByNumber(0);
    hubble.measureDarkCurrent();
    System.out.println("cali end");
    System.out.println("tem: " + hubble.getTemperature());
    try {
      Thread.sleep(5000);
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    System.out.println("status: " + hubble.getStatus());
    System.out.println(hubble.measureYxy());
  }

  public long getSerialNumber() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetSerialNumber@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(8));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return handle.getAsLong(0);
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
    return -1;
  }

  public CIExyY measureYxy() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotMeasureYxy@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(32));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        double Y = handle.getAsDouble(0);
        double x = handle.getAsDouble(8);
        double y = handle.getAsDouble(16);
        CIExyY xyY = new CIExyY(x, y, Y);
        return xyY;
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return null;
  }

  public double getTemperature() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetTemperature@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(8));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return handle.getAsDouble(0);
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return -1;
  }

  public String getCalibrationName(int slotNumber) {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetCalibrationName@16");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle1 = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
        handle1.setIntAt(0, slotNumber);
        jnative.setParameter(1, handle1);
        Pointer handle2 = new Pointer(MemoryBlockFactory.createMemoryBlock(80));
        jnative.setParameter(2, handle2);
        jnative.setParameter(3, 80);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return handle2.getAsString();
//      return handle.getAsDouble(0);
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return null;
  }

  public void measureDarkCurrent() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotMeasureDarkCurrent@4");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
  }

  public int getNumberOfCalibrations() {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetNumberOfCalibrations@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        return handle.getAsInt(0);
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return -1;
  }

  public RGB getRGBFromYxy(CIExyY xyY) {
    if (deviceHandle != null) {
      try {
        JNative jnative = getJNative("_gmbSpotGetRGBFromYxy@12");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        Pointer handle1 = new Pointer(MemoryBlockFactory.createMemoryBlock(32));
        jnative.setParameter(1, handle1);
        handle1.setDoubleAt(0, xyY.Y);
        handle1.setDoubleAt(8, xyY.x);
        handle1.setDoubleAt(16, xyY.y);
        Pointer handle2 = new Pointer(MemoryBlockFactory.createMemoryBlock(32));
        jnative.setParameter(2, handle2);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
        double r = handle2.getAsDouble(0);
        double g = handle2.getAsDouble(8);
        double b = handle2.getAsDouble(16);
        RGB rgb = new RGB(r, g, b);
        return rgb;
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }

    }
    return null;
  }

  public void registerButtonCallback(Callback c) {
    if (deviceHandle != null) {
      try {

        JNative jnative = getJNative("_gmbSpotRegisterButtonCallback@8");
        jnative.setParameter(0, deviceHandle.getAsInt(0));
        int callbackHandle = JNative.createCallback(1, c);
//        Pointer handle = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
        Pointer handle = Pointer.createPointer(4);
        handle.setIntAt(0, callbackHandle);
        jnative.setParameter(1, handle);
        jnative.setRetVal(Type.INT);
        jnative.invoke();
        status = Status.getStatus(jnative.getRetValAsInt());
      }
      catch (IllegalAccessException ex) {
        Logger.log.debug(ex);
      }
      catch (NativeException ex) {
        Logger.log.debug(ex);
      }
    }
  }

  /*public interface Callback {
    public void callback();
     }
   }*/
}
