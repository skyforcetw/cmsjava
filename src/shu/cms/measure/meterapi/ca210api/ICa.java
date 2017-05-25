/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package shu.cms.measure.meterapi.ca210api;

import com.jacob.com.*;
import shu.util.log.*;

public class ICa
    extends Dispatch {

  public static final String componentName = "CA200SRVR.ICa";

  public ICa() {
    super(componentName);
  }

  /**
   * This constructor is used instead of a case operation to
   * turn a Dispatch object into a wider object - it must exist
   * in every wrapper class whose instances may be returned from
   * method calls wrapped in VT_DISPATCH Variants.
   * @param d Dispatch
   */
  public ICa(Dispatch d) {
    // take over the IDispatch pointer
    m_pDispatch = d.m_pDispatch;
    // null out the input's pointer
    d.m_pDispatch = 0;
  }

  public ICa(String compName) {
    super(compName);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type IProbes
   */
  public IProbes getProbes() {
    return new IProbes(Dispatch.get(this, "Probes").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type IOutputProbes
   */
  public IOutputProbes getOutputProbes() {
    return new IOutputProbes(Dispatch.get(this, "OutputProbes").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type IMemory
   */
  public IMemory getMemory() {
    return new IMemory(Dispatch.get(this, "Memory").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getDisplayProbe() {
    return Dispatch.get(this, "DisplayProbe").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type String
   */
  public void setDisplayProbe(String lastParam) {
    Dispatch.call(this, "DisplayProbe", lastParam);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type IProbe
   */
  public IProbe getSingleProbe() {
    return new IProbe(Dispatch.get(this, "SingleProbe").toDispatch());
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type float
   */
  public float getSyncMode() {
    return Dispatch.get(this, "SyncMode").toFloat();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type float
   */
  public void setSyncMode(float lastParam) {
    Dispatch.call(this, "SyncMode", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getDisplayMode() {
    return Dispatch.get(this, "DisplayMode").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setDisplayMode(int lastParam) {
    Dispatch.call(this, "DisplayMode", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getDisplayDigits() {
    return Dispatch.get(this, "DisplayDigits").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setDisplayDigits(int lastParam) {
    Dispatch.call(this, "DisplayDigits", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getAveragingMode() {
    return Dispatch.get(this, "AveragingMode").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setAveragingMode(int lastParam) {
    Dispatch.call(this, "AveragingMode", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getBrightnessUnit() {
    return Dispatch.get(this, "BrightnessUnit").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setBrightnessUnit(int lastParam) {
    Dispatch.call(this, "BrightnessUnit", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getCAType() {
    return Dispatch.get(this, "CAType").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getCAVersion() {
    return Dispatch.get(this, "CAVersion").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getNumber() {
    return Dispatch.get(this, "Number").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getPortID() {
    return Dispatch.get(this, "PortID").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type String
   */
  public String getID() {
    return Dispatch.get(this, "ID").toString();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type String
   */
  public void setID(String lastParam) {
    Dispatch.call(this, "ID", lastParam);
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void calZero() {
    try {
      Dispatch.call(this, "CalZero");
    }
    catch (ComFailException ex) {
      Logger.log.error("calZero exception", ex);
    }
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
//	public void measure(int lastParam) {
//		Dispatch.call(this, "Measure", new Variant(lastParam));
//	}

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void measure() {
    Dispatch.call(this, "Measure");
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void measure(int lastParam) {
    Dispatch.call(this, "Measure", new Variant(lastParam));

  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setAnalyzerCalMode() {
    Dispatch.call(this, "SetAnalyzerCalMode");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void enter() {
    Dispatch.call(this, "Enter");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param range1Val an input-parameter of type float
   * @param lastParam an input-parameter of type float
   */
  public void setAnalogRange(float range1Val, float lastParam) {
    Dispatch.call(this, "SetAnalogRange", new Variant(range1Val),
                  new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setPWROnStatus() {
    Dispatch.call(this, "SetPWROnStatus");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @return the result is of type int
   */
  public int getCalStandard() {
    return Dispatch.get(this, "CalStandard").toInt();
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setCalStandard(int lastParam) {
    Dispatch.call(this, "CalStandard", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void resetAnalyzerCalMode() {
    Dispatch.call(this, "ResetAnalyzerCalMode");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setLvxyCalMode() {
    Dispatch.call(this, "SetLvxyCalMode");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void resetLvxyCalMode() {
    Dispatch.call(this, "ResetLvxyCalMode");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setAnalyzerCalData() {
    Dispatch.call(this, "SetAnalyzerCalData");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setDisplayProbe() {
    Dispatch.call(this, "SetDisplayProbe");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   */
  public void setLvxyCalData() {
    Dispatch.call(this, "SetLvxyCalData");
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type int
   */
  public void setRemoteMode(int lastParam) {
    Dispatch.call(this, "RemoteMode", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type float
   */
  public void setFMAAnalogRange(float lastParam) {
    Dispatch.call(this, "SetFMAAnalogRange", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param range1Val an input-parameter of type float
   * @param lastParam an input-parameter of type float
   */
  public void getAnalogRange(float range1Val, float lastParam) {
    Dispatch.call(this, "GetAnalogRange", new Variant(range1Val),
                  new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param range1Val is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   */
  public void getAnalogRange(float[] range1Val, float[] lastParam) {
    Variant vnt_range1Val = new Variant();
    if (range1Val == null || range1Val.length == 0) {
      vnt_range1Val.noParam();
    }
    else {
      vnt_range1Val.putFloatRef(range1Val[0]);
    }

    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putFloatRef(lastParam[0]);
    }

    Dispatch.call(this, "GetAnalogRange", vnt_range1Val, vnt_lastParam);

    if (range1Val != null && range1Val.length > 0) {
      range1Val[0] = vnt_range1Val.toFloat();
    }
    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toFloat();
    }
  }

  /**
   * Wrapper for calling the ActiveX-Method with input-parameter(s).
   * @param lastParam an input-parameter of type float
   */
  public void getFMAAnalogRange(float lastParam) {
    Dispatch.call(this, "GetFMAAnalogRange", new Variant(lastParam));
  }

  /**
   * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
   * @param lastParam is an one-element array which sends the input-parameter
   *                  to the ActiveX-Component and receives the output-parameter
   */
  public void getFMAAnalogRange(float[] lastParam) {
    Variant vnt_lastParam = new Variant();
    if (lastParam == null || lastParam.length == 0) {
      vnt_lastParam.noParam();
    }
    else {
      vnt_lastParam.putFloatRef(lastParam[0]);
    }

    Dispatch.call(this, "GetFMAAnalogRange", vnt_lastParam);

    if (lastParam != null && lastParam.length > 0) {
      lastParam[0] = vnt_lastParam.toFloat();
    }
  }

}
