package shu.cms.measure.remote;

import java.rmi.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.logo.*;
import shu.cms.measure.meter.*;

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
public interface MeterInterface
    extends Remote {
  public void close() throws RemoteException;

  public boolean isConnected() throws RemoteException;

  public void calibrate() throws RemoteException;

  public String getCalibrationDescription() throws RemoteException;

  public void setPatchIntensity(Meter.PatchIntensity patchIntensity) throws
      RemoteException;

  public double[] triggerMeasurementInXYZ() throws RemoteException;

  public double[] triggerMeasurementInSpectrum() throws RemoteException;

  public Spectra triggerMeasurementInSpectra() throws RemoteException;

  public String getLastCalibration() throws RemoteException;

  public String getCalibrationCount() throws RemoteException;

  public void setScreenType(Meter.ScreenType screenType) throws RemoteException;

  public Meter.Instr getType() throws RemoteException;

  public void setLogoFileData(LogoFile logo, List<Patch> patchList) throws
      RemoteException;

  public int getSuggestedWaitTimes() throws RemoteException;

  public String getMeterClassName() throws RemoteException;

  public String getMeterTypeName() throws RemoteException;
}
