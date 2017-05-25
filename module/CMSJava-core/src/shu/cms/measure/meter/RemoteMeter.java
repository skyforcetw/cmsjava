package shu.cms.measure.meter;

import java.net.*;
import java.rmi.*;
import java.util.*;

import shu.cms.measure.remote.*;
import shu.util.log.*;
import shu.cms.Spectra;

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
public class RemoteMeter
    extends Meter {

  public static void main(String[] args) {
    RemoteMeter meter = new RemoteMeter();
    System.out.println(Arrays.toString(meter.triggerMeasurementInXYZ()));
  }

  /**
   * RemoteMeter
   * @param hostname String
   * @param port int
   * @param bindname String
   * @throws RemoteException
   * @throws MalformedURLException
   * @throws NotBoundException
   */
  public RemoteMeter(String hostname, int port, String bindname) throws
      RemoteException, java.net.MalformedURLException,
      java.rmi.NotBoundException {
    url = RemoteMeterServer.getURL(hostname, port, bindname);
    meter = (MeterInterface) Naming.lookup(url); // 尋得對像
  }

  public final static RemoteMeter getDefaultInstance() {
    RemoteMeter meter = new RemoteMeter();
    return meter;
  }

  private RemoteMeter() {
    url = RemoteMeterServer.getURL(RemoteMeterServer.HostName,
                                   RemoteMeterServer.Port,
                                   RemoteMeterServer.BindName);
    try {
      meter = (MeterInterface) Naming.lookup(url); // 尋得對像
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      throw new IllegalStateException(ex);
    }
    catch (MalformedURLException ex) {
      Logger.log.error("", ex);
      throw new IllegalStateException(ex);
    }
    catch (NotBoundException ex) {
      Logger.log.error("", ex);
      throw new IllegalStateException(ex);
    }
  }

  private MeterInterface meter;
  private String url;

  /**
   * calibrate
   *
   */
  public void calibrate() {
    try {
      meter.calibrate();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * close
   *
   */
  public void close() {
    try {
      meter.close();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    try {
      return meter.getCalibrationCount();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
    try {
      return meter.getCalibrationDescription();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  /**
   * getLastCalibration
   *
   * @return String
   */
  public String getLastCalibration() {
    try {
      return meter.getLastCalibration();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  /**
   * getType
   *
   * @return Instr
   */
  public Instr getType() {
    try {
      return meter.getType();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    try {
      return meter.isConnected();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return false;
    }
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
    try {
      meter.setPatchIntensity(patchIntensity);
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   */
  public void setScreenType(ScreenType screenType) {
    try {
      meter.setScreenType(screenType);
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
    }

  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    try {
      return meter.triggerMeasurementInXYZ();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }
  /**
   *
   * @return double[]
   * @deprecated
   */
  public double[] triggerMeasurementInSpectrum() {
    try {
      return meter.triggerMeasurementInXYZ();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public Spectra triggerMeasurementInSpectra() {
    try {
      return meter.triggerMeasurementInSpectra();
    }
    catch (RemoteException ex) {
      Logger.log.error("", ex);
      return null;
    }

  }
}
