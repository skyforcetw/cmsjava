package shu.cms.measure.remote;

import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.List;

import java.awt.*;

import shu.cms.colorformat.logo.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.meter.*;
import shu.cms.measure.meter.Meter.*;
import shu.math.array.*;
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
public class RemoteMeterServer
    extends UnicastRemoteObject implements MeterInterface {
  private TrayIcon trayIcon;
  private Image meterIcon = Toolkit.getDefaultToolkit()
      .getImage(RemoteMeterServer.class.getResource(
          "kllckety.png"));

  private Meter meter;
  private String url;
//  private MeterMeasurement mm;

  public RemoteMeterServer(int port, String bindname,
                           Meter meter) throws RemoteException,
      java.net.MalformedURLException {
    url = RemoteMeterServer.getURL("localhost", port, bindname);
    this.meter = meter;
//    mm = new MeterMeasurement(meter,false);

    LocateRegistry.createRegistry(port);
    Naming.rebind(url, this); // 將實現類綁到一個名字上去
    initTrayIcon();
    Logger.log.info("Bind at " + url);
  }

  private void initTrayIcon() {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      trayIcon =
          new TrayIcon(meterIcon,
                       "Remote Meter: " + url + " " + meter.getType().name());
      PopupMenu popupMenu = new PopupMenuFactory(meter).getPopupMenu();
      trayIcon.setPopupMenu(popupMenu);
      try {
        tray.add(trayIcon);
      }
      catch (AWTException e) {
        e.printStackTrace();
      }
    }
    else {
      Logger.log.error("無法取得系統工具列");
    } //工具列圖示

  }

  public final static String getURL(String hostname, int port, String bindname) {
    return "//" + hostname + ":" + port + "/" + bindname;
  }

  /**
   * close
   *
   */
  public void close() {
    meter.close();
    Logger.log.info("close");
  }

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    return meter.isConnected();
  }

  /**
   * calibrate
   *
   */
  public void calibrate() {
    meter.calibrate();
    Logger.log.info("calibrate");
  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
    return meter.getCalibrationDescription();
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
    meter.setPatchIntensity(patchIntensity);
  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    double[] result = meter.triggerMeasurementInXYZ();
//    Logger.log.info("measure XYZ: " + DoubleArray.toString(result));
    System.out.println("measure XYZ: " + DoubleArray.toString(result));
    return result;
  }
  /**
   *
   * @return double[]
   * @deprecated
   */
  public double[] triggerMeasurementInSpectrum() {
    double[] result = meter.triggerMeasurementInSpectrum();
    return result;
  }

  public Spectra triggerMeasurementInSpectra() {
    return meter.triggerMeasurementInSpectra();
  }

  /**
   * getLastCalibration
   *
   * @return String
   */
  public String getLastCalibration() {
    return meter.getLastCalibration();
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return meter.getCalibrationCount();
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   */
  public void setScreenType(ScreenType screenType) {
    meter.setScreenType(screenType);
  }

  /**
   * getType
   *
   * @return Instr
   */
  public Instr getType() {
    return meter.getType();
  }

  /**
   * setLogoFileData
   *
   * @param logo LogoFile
   * @param patchList List
   */
  public void setLogoFileData(LogoFile logo, List patchList) {
    meter.setLogoFileData(logo, patchList);
  }

  /**
   * getSuggestedWaitTimes
   *
   * @return int
   */
  public int getSuggestedWaitTimes() {
    return meter.getSuggestedWaitTimes();
  }

  public final static String HostName = "localhost";
  public final static int Port = 9999;
  public final static String BindName = "rm";

  public static void main(String[] args) {
    boolean test = AutoCPOptions.get("DummyMeter") &&
        !AutoCPOptions.get("MeasuredModel");
    Meter meter = null;
    if (test) {
//      LCDModel model = Material.getLCDModel();
//      model.produceFactor();
      meter = new DummyMeter();
    }
    else {
      meter = new CA210();
    }

    try {
      new RemoteMeterServer(Port, BindName, meter);
//      System.out.println("bind");
    }
    catch (MalformedURLException ex) {
      ex.printStackTrace();
    }
    catch (RemoteException ex) {
      ex.printStackTrace();
    }

  }

  public String getMeterClassName() throws RemoteException {
    return meter.getClass().getSimpleName();
  }

  public String getMeterTypeName() throws RemoteException {
    return meter.getType().name();
  }
}
