package shu.cms.applet.measure.tool;

import shu.cms.lcd.*;
import shu.cms.measure.calibrate.*;

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
public class Calibrator {

  public static enum Operator {
    Reference, Compute, GetCalibrator, Calibrate, NoCalibrate, Delete
  }

  protected static interface CalibratorBinder {
    public void send(Operator op, Object obj);

    public void receive(Operator op, Object obj);
  }

  public static abstract class ClientBinder
      implements CalibratorBinder {
    protected Calibrator calibrator;

    public ClientBinder(Calibrator calibrator) {
      this.calibrator = calibrator;
    }

    public final void send(Operator op, Object obj) {
      switch (op) {
        case Reference:
          if (calibrator.serverBinder != null) {
            //通知可以compute matrix
            calibrator.serverBinder.receive(op, obj);
          }
          break;
        case Compute:
          break;
        case Calibrate:
          if (calibrator.colorCalibrator != null) {
            this.receive(Operator.Calibrate, calibrator.colorCalibrator);
          }
          break;
        case GetCalibrator:
          this.receive(Operator.GetCalibrator, calibrator.colorCalibrator);
          break;
      }
    }

    public abstract void receive(Operator op, Object obj);
  }

  private ServerBinder serverBinder;

  public static abstract class ServerBinder
      implements CalibratorBinder {
    protected Calibrator calibrator;
    public final void send(Operator op, Object obj) {
      switch (op) {
        case Compute:
          LCDTarget[] targets = (LCDTarget[]) obj;
          calibrator.calibrate(targets[0], targets[1]);
          MeasuredInternalFrame.setFramesCalibrateEnable(true);
          break;
        case Calibrate:
          ClientBinder binder = (ClientBinder) obj;
          binder.receive(op, calibrator.colorCalibrator);
          break;
        case Delete:
          MeasuredInternalFrame.removeFramesCalibrate();
          break;
      }
    }

    public abstract void receive(Operator op, Object obj);
  }

  public Calibrator(ServerBinder serverBinder) {
    setServerBinder(serverBinder);
  }

  protected void setServerBinder(ServerBinder serverBinder) {
    this.serverBinder = serverBinder;
    this.serverBinder.calibrator = this;
  }

//  public double[][] getCalibrationMatrix() {
//    return calibrationMatrix;
//  }

  public void setCalibrateMatrix(double[][] calibrateMatrix) {
    colorCalibrator = new FourColorCalibrator(calibrateMatrix);
  }

  private FourColorCalibrator colorCalibrator;
//  private double[][] calibrationMatrix = new double[][] {
//      {
//      1, 0, 0}, {
//      0, 1, 0}, {
//      0, 0, 1}
//  };
  protected void calibrate(LCDTarget reference, LCDTarget lcdTarget) {
    colorCalibrator = new FourColorCalibrator(reference,
                                              lcdTarget);
//    calibrationMatrix = colorCalibrator.getCalibrateMatrix();
  }
}
