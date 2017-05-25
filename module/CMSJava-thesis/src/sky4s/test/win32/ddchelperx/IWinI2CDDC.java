/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package sky4s.test.win32.ddchelperx;

import com.jacob.com.*;

public class IWinI2CDDC
    extends Dispatch {

  public static final String componentName = "DDCHelperX.WinI2CDDC";

  public IWinI2CDDC() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   */
  public IWinI2CDDC(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public IWinI2CDDC(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getLibraryVersion() {
    return Dispatch.call(this, "GetLibraryVersion").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param logPath an input-parameter of type String
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int enableLog(String logPath, int lastParam) {
    return Dispatch.call(this, "EnableLog", logPath, new Variant(lastParam)).
        toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int disableLog() {
    return Dispatch.call(this, "DisableLog").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getLibraryOption(int lastParam) {
    return Dispatch.call(this, "GetLibraryOption", new Variant(lastParam)).
        toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param optionIndex an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setLibraryOption(int optionIndex, int lastParam) {
    return Dispatch.call(this, "SetLibraryOption", new Variant(optionIndex),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param folderPath an input-parameter of type String
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int installDriver(String folderPath, int lastParam) {
    return Dispatch.call(this, "InstallDriver", folderPath,
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int uninstallDriver() {
    return Dispatch.call(this, "UninstallDriver").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int initDDCHelper() {
    return Dispatch.call(this, "InitDDCHelper").toInt();
  }

  public static void main(String[] args) {
    IWinI2CDDC ddc = new IWinI2CDDC();
    ddc.initDDCHelper();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int deinitDDCHelper() {
    return Dispatch.call(this, "DeinitDDCHelper").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int enumGetFirst(int lastParam) {
    return Dispatch.call(this, "EnumGetFirst", new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int enumGetFirst(int[] lastParam) {
    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_EnumGetFirst = Dispatch.call(this, "EnumGetFirst",
                                               vnt_lastParam).toInt();

    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_EnumGetFirst;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int enumGetNext(int lastParam) {
    return Dispatch.call(this, "EnumGetNext", new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int enumGetNext(int[] lastParam) {
    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_EnumGetNext = Dispatch.call(this, "EnumGetNext",
                                              vnt_lastParam).toInt();

    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_EnumGetNext;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type Variant
   * @return the result is of type int
   */
  public int readEDID(int device, Variant lastParam) {
    return Dispatch.call(this, "ReadEDID", new Variant(device), lastParam).
        toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int readEDID(int device, Variant[] lastParam) {
    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam = lastParam[0];
    }

    int result_of_ReadEDID = Dispatch.call(this, "ReadEDID", new Variant(device),
                                           vnt_lastParam).toInt();

    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam;
    }

    return result_of_ReadEDID;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type Variant
   * @return the result is of type int
   */
  public int readEDID256(int device, Variant lastParam) {
    return Dispatch.call(this, "ReadEDID256", new Variant(device), lastParam).
        toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int readEDID256(int device, Variant[] lastParam) {
    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam = lastParam[0];
    }

    int result_of_ReadEDID256 = Dispatch.call(this, "ReadEDID256",
                                              new Variant(device),
                                              vnt_lastParam).toInt();

    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam;
    }

    return result_of_ReadEDID256;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int testAddr(int device, int lastParam) {
    return Dispatch.call(this, "TestAddr", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param command an input-parameter of type int
   * @param currentValue an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getCIValue(int device, int command, int currentValue,
                        int lastParam) {
    return Dispatch.call(this, "GetCIValue", new Variant(device),
                         new Variant(command), new Variant(currentValue),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param command an input-parameter of type int
   * @param currentValue is an one-element array which sends the input-parameter
   *                     to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getCIValue(int device, int command, int[] currentValue,
                        int[] lastParam) {
    Variant vnt_currentValue = new Variant();
    if (currentValue == null || currentValue.length == 0) {
      vnt_currentValue.noParam();
    }
    else {
      vnt_currentValue.putIntRef(currentValue[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetCIValue = Dispatch.call(this, "GetCIValue",
                                             new Variant(device),
                                             new Variant(command),
                                             vnt_currentValue, vnt_lastParam).
        toInt();

    if (currentValue != null && currentValue.length > 0) {
      currentValue[0] = vnt_currentValue.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetCIValue;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param command an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setCIValue(int device, int command, int lastParam) {
    return Dispatch.call(this, "SetCIValue", new Variant(device),
                         new Variant(command), new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param addr an input-parameter of type int
   * @param dataBuffer an input-parameter of type Variant
   * @param dataLength an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int readI2CBuf(int device, int addr, Variant dataBuffer,
                        int dataLength, int lastParam) {
    return Dispatch.call(this, "ReadI2CBuf", new Variant(device),
                         new Variant(addr), dataBuffer, new Variant(dataLength),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param addr an input-parameter of type int
   * @param dataBuffer is an one-element array which sends the input-parameter
   *                   to the ActiveX-Component and receives the output-parameter
   * @param dataLength an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int readI2CBuf(int device, int addr, Variant[] dataBuffer,
                        int dataLength, int lastParam) {
    Variant vnt_dataBuffer = new Variant();
    if (dataBuffer == null || dataBuffer.length == 0) {
      vnt_dataBuffer.noParam();
    }
    else {
      vnt_dataBuffer = dataBuffer[0];
    }

    int result_of_ReadI2CBuf = Dispatch.call(this, "ReadI2CBuf",
                                             new Variant(device),
                                             new Variant(addr), vnt_dataBuffer,
                                             new Variant(dataLength),
                                             new Variant(lastParam)).toInt();

    if (dataBuffer != null && dataBuffer.length > 0) {
      dataBuffer[0] = vnt_dataBuffer;
    }

    return result_of_ReadI2CBuf;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param addr an input-parameter of type int
   * @param dataBuffer an input-parameter of type Variant
   * @param dataLength an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int writeI2CBuf(int device, int addr, Variant dataBuffer,
                         int dataLength, int lastParam) {
    return Dispatch.call(this, "WriteI2CBuf", new Variant(device),
                         new Variant(addr), dataBuffer, new Variant(dataLength),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param dataBuffer an input-parameter of type Variant
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int dDCCIRead(int device, Variant dataBuffer, int lastParam) {
    return Dispatch.call(this, "DDCCIRead", new Variant(device), dataBuffer,
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param dataBuffer is an one-element array which sends the input-parameter
   *                   to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int dDCCIRead(int device, Variant[] dataBuffer, int[] lastParam) {
    Variant vnt_dataBuffer = new Variant();
    if (dataBuffer == null || dataBuffer.length == 0) {
      vnt_dataBuffer.noParam();
    }
    else {
      vnt_dataBuffer = dataBuffer[0];
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_DDCCIRead = Dispatch.call(this, "DDCCIRead",
                                            new Variant(device), vnt_dataBuffer,
                                            vnt_lastParam).toInt();

    if (dataBuffer != null && dataBuffer.length > 0) {
      dataBuffer[0] = vnt_dataBuffer;
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_DDCCIRead;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param dataBuffer an input-parameter of type Variant
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int dDCCIWrite(int device, Variant dataBuffer, int lastParam) {
    return Dispatch.call(this, "DDCCIWrite", new Variant(device), dataBuffer,
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getSDA(int lastParam) {
    return Dispatch.call(this, "GetSDA", new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setSDA(int device, int lastParam) {
    return Dispatch.call(this, "SetSDA", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getSCL(int lastParam) {
    return Dispatch.call(this, "GetSCL", new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setSCL(int device, int lastParam) {
    return Dispatch.call(this, "SetSCL", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param capString an input-parameter of type String
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorCapabilitesStr(int device, String capString,
                                      int lastParam) {
    return Dispatch.call(this, "GetMonitorCapabilitesStr", new Variant(device),
                         capString, new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param capString is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorCapabilitesStr(int device, String[] capString,
                                      int lastParam) {
    Variant vnt_capString = new Variant();
    if (capString == null || capString.length == 0) {
      vnt_capString.noParam();
    }
    else {
      vnt_capString.putStringRef(capString[0]);
    }

    int result_of_GetMonitorCapabilitesStr = Dispatch.call(this,
        "GetMonitorCapabilitesStr", new Variant(device), vnt_capString,
        new Variant(lastParam)).toInt();

    if (capString != null && capString.length > 0) {
      capString[0] = vnt_capString.toString();
    }

    return result_of_GetMonitorCapabilitesStr;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorCapabilitesStrLen(int lastParam) {
    return Dispatch.call(this, "GetMonitorCapabilitesStrLen",
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int degaussMonitor(int lastParam) {
    return Dispatch.call(this, "DegaussMonitor", new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param minBrightness an input-parameter of type int
   * @param curBrightness an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorBrightness(int device, int minBrightness,
                                  int curBrightness, int lastParam) {
    return Dispatch.call(this, "GetMonitorBrightness", new Variant(device),
                         new Variant(minBrightness), new Variant(curBrightness),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param minBrightness is an one-element array which sends the input-parameter
   *                      to the ActiveX-Component and receives the output-parameter
   * @param curBrightness is an one-element array which sends the input-parameter
   *                      to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getMonitorBrightness(int device, int[] minBrightness,
                                  int[] curBrightness, int[] lastParam) {
    Variant vnt_minBrightness = new Variant();
    if (minBrightness == null || minBrightness.length == 0) {
      vnt_minBrightness.noParam();
    }
    else {
      vnt_minBrightness.putIntRef(minBrightness[0]);
    }

    Variant vnt_curBrightness = new Variant();
    if (curBrightness == null || curBrightness.length == 0) {
      vnt_curBrightness.noParam();
    }
    else {
      vnt_curBrightness.putIntRef(curBrightness[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetMonitorBrightness = Dispatch.call(this,
        "GetMonitorBrightness", new Variant(device), vnt_minBrightness,
        vnt_curBrightness, vnt_lastParam).toInt();

    if (minBrightness != null && minBrightness.length > 0) {
      minBrightness[0] = vnt_minBrightness.toInt();
    }
    if (curBrightness != null && curBrightness.length > 0) {
      curBrightness[0] = vnt_curBrightness.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetMonitorBrightness;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorBrightness(int device, int lastParam) {
    return Dispatch.call(this, "SetMonitorBrightness", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param minContrast an input-parameter of type int
   * @param curContrast an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorContrast(int device, int minContrast, int curContrast,
                                int lastParam) {
    return Dispatch.call(this, "GetMonitorContrast", new Variant(device),
                         new Variant(minContrast), new Variant(curContrast),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param minContrast is an one-element array which sends the input-parameter
   *                    to the ActiveX-Component and receives the output-parameter
   * @param curContrast is an one-element array which sends the input-parameter
   *                    to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getMonitorContrast(int device, int[] minContrast,
                                int[] curContrast, int[] lastParam) {
    Variant vnt_minContrast = new Variant();
    if (minContrast == null || minContrast.length == 0) {
      vnt_minContrast.noParam();
    }
    else {
      vnt_minContrast.putIntRef(minContrast[0]);
    }

    Variant vnt_curContrast = new Variant();
    if (curContrast == null || curContrast.length == 0) {
      vnt_curContrast.noParam();
    }
    else {
      vnt_curContrast.putIntRef(curContrast[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetMonitorContrast = Dispatch.call(this, "GetMonitorContrast",
        new Variant(device), vnt_minContrast, vnt_curContrast, vnt_lastParam).
        toInt();

    if (minContrast != null && minContrast.length > 0) {
      minContrast[0] = vnt_minContrast.toInt();
    }
    if (curContrast != null && curContrast.length > 0) {
      curContrast[0] = vnt_curContrast.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetMonitorContrast;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorContrast(int device, int lastParam) {
    return Dispatch.call(this, "SetMonitorContrast", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param posType an input-parameter of type int
   * @param minPosition an input-parameter of type int
   * @param curPosition an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorDisplayAreaPosition(int device, int posType,
                                           int minPosition, int curPosition,
                                           int lastParam) {
    return Dispatch.call(this, "GetMonitorDisplayAreaPosition",
                         new Variant(device), new Variant(posType),
                         new Variant(minPosition), new Variant(curPosition),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param posType an input-parameter of type int
   * @param minPosition is an one-element array which sends the input-parameter
   *                    to the ActiveX-Component and receives the output-parameter
   * @param curPosition is an one-element array which sends the input-parameter
   *                    to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getMonitorDisplayAreaPosition(int device, int posType,
                                           int[] minPosition, int[] curPosition,
                                           int[] lastParam) {
    Variant vnt_minPosition = new Variant();
    if (minPosition == null || minPosition.length == 0) {
      vnt_minPosition.noParam();
    }
    else {
      vnt_minPosition.putIntRef(minPosition[0]);
    }

    Variant vnt_curPosition = new Variant();
    if (curPosition == null || curPosition.length == 0) {
      vnt_curPosition.noParam();
    }
    else {
      vnt_curPosition.putIntRef(curPosition[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetMonitorDisplayAreaPosition = Dispatch.call(this,
        "GetMonitorDisplayAreaPosition", new Variant(device),
        new Variant(posType), vnt_minPosition, vnt_curPosition, vnt_lastParam).
        toInt();

    if (minPosition != null && minPosition.length > 0) {
      minPosition[0] = vnt_minPosition.toInt();
    }
    if (curPosition != null && curPosition.length > 0) {
      curPosition[0] = vnt_curPosition.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetMonitorDisplayAreaPosition;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param posType an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorDisplayAreaPosition(int device, int posType,
                                           int lastParam) {
    return Dispatch.call(this, "SetMonitorDisplayAreaPosition",
                         new Variant(device), new Variant(posType),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param sizeType an input-parameter of type int
   * @param minSize an input-parameter of type int
   * @param curSize an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorDisplayAreaSize(int device, int sizeType, int minSize,
                                       int curSize, int lastParam) {
    return Dispatch.call(this, "GetMonitorDisplayAreaSize", new Variant(device),
                         new Variant(sizeType), new Variant(minSize),
                         new Variant(curSize), new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param sizeType an input-parameter of type int
   * @param minSize is an one-element array which sends the input-parameter
   *                to the ActiveX-Component and receives the output-parameter
   * @param curSize is an one-element array which sends the input-parameter
   *                to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getMonitorDisplayAreaSize(int device, int sizeType, int[] minSize,
                                       int[] curSize, int[] lastParam) {
    Variant vnt_minSize = new Variant();
    if (minSize == null || minSize.length == 0) {
      vnt_minSize.noParam();
    }
    else {
      vnt_minSize.putIntRef(minSize[0]);
    }

    Variant vnt_curSize = new Variant();
    if (curSize == null || curSize.length == 0) {
      vnt_curSize.noParam();
    }
    else {
      vnt_curSize.putIntRef(curSize[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetMonitorDisplayAreaSize = Dispatch.call(this,
        "GetMonitorDisplayAreaSize", new Variant(device), new Variant(sizeType),
        vnt_minSize, vnt_curSize, vnt_lastParam).toInt();

    if (minSize != null && minSize.length > 0) {
      minSize[0] = vnt_minSize.toInt();
    }
    if (curSize != null && curSize.length > 0) {
      curSize[0] = vnt_curSize.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetMonitorDisplayAreaSize;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param sizeType an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorDisplayAreaSize(int device, int sizeType, int lastParam) {
    return Dispatch.call(this, "SetMonitorDisplayAreaSize", new Variant(device),
                         new Variant(sizeType), new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param gainType an input-parameter of type int
   * @param minGain an input-parameter of type int
   * @param curGain an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorGain(int device, int gainType, int minGain, int curGain,
                            int lastParam) {
    return Dispatch.call(this, "GetMonitorGain", new Variant(device),
                         new Variant(gainType), new Variant(minGain),
                         new Variant(curGain), new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param device an input-parameter of type int
   * @param gainType an input-parameter of type int
   * @param minGain is an one-element array which sends the input-parameter
   *                to the ActiveX-Component and receives the output-parameter
   * @param curGain is an one-element array which sends the input-parameter
   *                to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @return the result is of type int
   */
  public int getMonitorGain(int device, int gainType, int[] minGain,
                            int[] curGain, int[] lastParam) {
    Variant vnt_minGain = new Variant();
    if (minGain == null || minGain.length == 0) {
      vnt_minGain.noParam();
    }
    else {
      vnt_minGain.putIntRef(minGain[0]);
    }

    Variant vnt_curGain = new Variant();
    if (curGain == null || curGain.length == 0) {
      vnt_curGain.noParam();
    }
    else {
      vnt_curGain.putIntRef(curGain[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putIntRef(lastParam[0]);
    }

    int result_of_GetMonitorGain = Dispatch.call(this, "GetMonitorGain",
                                                 new Variant(device),
                                                 new Variant(gainType),
                                                 vnt_minGain, vnt_curGain,
                                                 vnt_lastParam).toInt();

    if (minGain != null && minGain.length > 0) {
      minGain[0] = vnt_minGain.toInt();
    }
    if (curGain != null && curGain.length > 0) {
      curGain[0] = vnt_curGain.toInt();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toInt();
    }

    return result_of_GetMonitorGain;
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param gainType an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorGain(int device, int gainType, int lastParam) {
    return Dispatch.call(this, "SetMonitorGain", new Variant(device),
                         new Variant(gainType), new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getMonitorTechnologyType(int lastParam) {
    return Dispatch.call(this, "GetMonitorTechnologyType",
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int restoreMonitorFactoryDefaults(int lastParam) {
    return Dispatch.call(this, "RestoreMonitorFactoryDefaults",
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int restoreMonitorFactoryColorDefaults(int lastParam) {
    return Dispatch.call(this, "RestoreMonitorFactoryColorDefaults",
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int saveCurrentMonitorSettings(int lastParam) {
    return Dispatch.call(this, "SaveCurrentMonitorSettings",
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param device an input-parameter of type int
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int setMonitorPowerMode(int device, int lastParam) {
    return Dispatch.call(this, "SetMonitorPowerMode", new Variant(device),
                         new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type String
   * @return the result is of type int
   */
  public int writeLog(String lastParam) {
    return Dispatch.call(this, "WriteLog", lastParam).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param eDID an input-parameter of type Variant
   * @param optionIndex an input-parameter of type int
   * @param value an input-parameter of type String
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getEDIDOption(Variant eDID, int optionIndex, String value,
                           int lastParam) {
    return Dispatch.call(this, "GetEDIDOption", eDID, new Variant(optionIndex),
                         value, new Variant(lastParam)).toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param eDID an input-parameter of type Variant
   * @param optionIndex an input-parameter of type int
   * @param value is an one-element array which sends the input-parameter
   *              to the ActiveX-Component and receives the output-parameter
   * @param lastParam an input-parameter of type int
   * @return the result is of type int
   */
  public int getEDIDOption(Variant eDID, int optionIndex, String[] value,
                           int lastParam) {
    Variant vnt_value = new Variant();
    if (value == null || value.length == 0) {
      vnt_value.noParam();
    }
    else {
      vnt_value.putStringRef(value[0]);
    }

    int result_of_GetEDIDOption = Dispatch.call(this, "GetEDIDOption", eDID,
                                                new Variant(optionIndex),
                                                vnt_value,
                                                new Variant(lastParam)).toInt();

    if (value != null && value.length > 0) {
      value[0] = vnt_value.toString();
    }

    return result_of_GetEDIDOption;
  }

}
