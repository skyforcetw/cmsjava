package shu.cms.measure.meter;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

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
public abstract class Meter {
  public static enum Instr {
    i1Display2(2.5, 3.3), i1Pro(2.6, 6), Spyder2(4.25, 4.7), Spyder3(3.5, 4),
    CA210(1.9, 1.9), Dummy(2.5, 3.3), Argyll(4.25, 4.7), Platform(0, 0);

    Instr(double width, double length) {
      this.widthInch = width;
      this.lengthInch = length;
    }

    public double widthInch;
    public double lengthInch;
  }

  public static enum PatchIntensity {
    Bleak, Bright, Auto;
  }

  public static enum ScreenType {
    LCD, CRT;
  }

  public abstract void close();

  public abstract boolean isConnected();

  public abstract void calibrate();

  public abstract String getCalibrationDescription();

  public abstract void setPatchIntensity(PatchIntensity patchIntensity);

  public abstract double[] triggerMeasurementInXYZ();

  /**
   *
   * @return double[]
   * @deprecated
   */
  public abstract double[] triggerMeasurementInSpectrum();

  public abstract Spectra triggerMeasurementInSpectra();

  public abstract String getLastCalibration();

  public abstract String getCalibrationCount();

  public abstract void setScreenType(ScreenType screenType);

  public abstract Instr getType();

  /**
   * setLogoFileHeader
   *
   * @param logo LogoFile
   */
  public void setLogoFileHeader(LogoFile logo) {
    logo.setHeader(LogoFile.Reserved.Created, new Date().toString());
    logo.setHeader(LogoFile.Reserved.Instrumentation, getType().name());
    logo.setHeader(LogoFile.Reserved.MeasurementSource,
                   "Illumination=Unknown	ObserverAngle=Unknown	WhiteBase=Abs	Filter=Unknown");
    logo.setNumberOfFields(8);
    logo.addKeyword("SampleID");
    logo.addKeyword("SAMPLE_NAME");
    logo.setDataFormat(
        "SampleID	SAMPLE_NAME	RGB_R	RGB_G	RGB_B	XYZ_X	XYZ_Y	XYZ_Z");
  }

  public final static void setLogoPatchList(LogoFile logo, List<Patch>
      patchList) {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      RGB rgb = p.getRGB();
      String pName = p.getName();
      int index = x + 1;
      String name = (pName == null || pName.length() == 0) ?
          String.valueOf(index) : pName;
      String data = index + "\t" + name + "\t" + rgb.R + "\t" + rgb.G +
          "\t" + rgb.B + "\t" + XYZ.X + "\t" + XYZ.Y + " " + XYZ.Z;
      logo.addData(data);
    }

  }

  public void setLogoFileData(LogoFile logo, List<Patch> patchList) {
    setLogoPatchList(logo, patchList);
  }

  public int getSuggestedWaitTimes() {
    return 300;
  }

}
