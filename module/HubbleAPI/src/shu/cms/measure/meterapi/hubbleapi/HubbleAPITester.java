package shu.cms.measure.meterapi.hubbleapi;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.exceptions.*;
import shu.util.log.Logger;
import org.xvolks.jnative.*;
import org.xvolks.jnative.exceptions.*;

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
public class HubbleAPITester {

  public final static String HUBBLE_DLLNAME = "SpotMeterSDK.dll";
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
    FootLts, /**< Foot Lamberts		*/
    Nits
        /**< Candelas/m^2		*/
  }

  public HubbleAPITester() {
    status = initialize();
  }

  private Status status;
  public Status getStatus() {
    return status;
  }

  public static void main(String[] args) {
    HubbleAPITester h = new HubbleAPITester();
    System.out.println(h.getStatus());

  }

  Status initialize() {
    try {
      JNative jnative = new JNative("SpotMeterSDK.dll",
                                    "_gmbSpotInitialize@0");
      jnative.setRetVal(Type.LONG);
      jnative.invoke();
      int result = jnative.getRetValAsInt();
      return Status.getStatus(result);
    }
    catch (NativeException ex) {
      Logger.log.debug(ex);
    }
    catch (IllegalAccessException ex) {
      Logger.log.debug(ex);
    }
    return null;
  }

  Status destroy() {
    return null;
  }

  int getNumberOfDevices() {
    return -1;
  }

  Status getDeviceHandle(int whichDevice, int devHndl) {
    return null;
  }

  String getToolkitVersion() {
    return null;
  }

  Status deviceOpen(int devHndl) {
    return null;
  }

  Status deviceClose(int devHndl) {
    return null;
  }

  Status getSerialNumber(int devHndl, int ulSerialNum) {
    return null;
  }

  Status setIntegrationTime(int devHndl, double dSeconds) {
    return null;
  }

  Status getIntegrationTime(int devHndl, double dSeconds) {
    return null;
  }

  Status getLastMeasurementData(int devHndl, int dXYZLast, int dRGBLast) {
    return null;
  }

  Status setLuminanceUnits(int devHndl, LumUnits lumUnits) {
    return null;
  }

  Status getLuminanceUnits(int devHndl, int lumUnits) {
    return null;
  }

  Status getTemperature(int devHndl, int dTempC) {
    return null;
  }

  Status getNumberOfCalibrations(int devHndl, int num) {
    return null;
  }

  Status getCalibrationName(int devHndl, int slotNum, int name, int stringSize) {
    return null;
  }

  Status setCalibrationName(int devHndl, int name) {
    return null;
  }

  Status setActiveCalibrationByName(int devHndl, int name) {
    return null;
  }

  Status setActiveCalibrationByNumber(int devHndl, int slot) {
    return null;
  }

  Status getRGBFromYxy(int devHndl, int dYxyTarget, int dRGBEquiv) {
    return null;
  }

  Status setLowLightAveraging(int devHndl, boolean enable) {
    return null;
  }

  //Status setAveragingParms (int devHndl, SPOT_MEASURE_AVERAGING_PARMS *parms)

  Status measureDarkCurrent(int devHndl) {
    return null;
  }

  Status measureYxy(int devHndl, int dYxyMeas) {
    return null;
  }

  Status getCalibrationDataBySlot(int devHndl, int calData,
                                  int slotNum) {
    return null;
  }

  Status getCalibrationDataByName(int devHndl,
                                  int calData, int name) {
    return null;
  }

  Status getAvailableSlot(int devHndl, int slotNum) {
    return null;
  }

  Status saveCalibrationByName(int devHndl, int calData, boolean override) {
    return null;
  }

  Status saveCalibrationBySlot(int devHndl, int calData, boolean override) {
    return null;
  }

  Status computeCalibrationMatrix(int devHndl, int dYxyReference,
                                  int dRGBMeasured, int sNumColors, int matrix) {
    return null;
  }

}
