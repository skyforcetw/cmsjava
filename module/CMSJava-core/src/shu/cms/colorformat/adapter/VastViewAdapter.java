package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class VastViewAdapter
    extends TargetAdapter {
  public static enum PatchType {
    RGBCMYW_256, //1792
    RGBW_256, //1024
    RGBW_1024, //4096
    RGBCMYW_1024 //7168
  }

  /**
   *
   * @return boolean
   * @todo M probeParsable
   */
  public boolean probeParsable() {
    return false;
  }

  protected File file;
  protected PatchType type;

  public String getFilename() {
    return file.getName();
  }

  public String getAbsolutePath() {
    return file.getAbsolutePath();
  }

  protected final static PatchType getPatchType(LCDTargetBase.Number number) {
    switch (number) {
      case Ramp1792:
        return PatchType.RGBCMYW_256;
      case Ramp1024:
        return PatchType.RGBW_256;
      case Ramp4096:
        return PatchType.RGBW_1024;
      case Ramp7168:
        return PatchType.RGBCMYW_1024;
      default:
        return null;
    }
  }

  protected final static LCDTargetBase.Number getNumber(PatchType patchType) {
    switch (patchType) {
      case RGBCMYW_256:
        return LCDTargetBase.Number.Ramp1792;
      case RGBW_256:
        return LCDTargetBase.Number.Ramp1024;
      case RGBCMYW_1024:
        return LCDTargetBase.Number.Ramp7147;
      default:
        return null;
    }
  }

  public VastViewAdapter(String filename, LCDTargetBase.Number number) {
    this(filename, getPatchType(number));
  }

  public VastViewAdapter() {

  }

  public VastViewAdapter(String filename) {
    file = new File(filename);
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("!file.exists() || !file.isFile()");
    }
  }

  public VastViewAdapter(String filename, PatchType type) {
    file = new File(filename);
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("!file.exists() || !file.isFile()");
    }
    this.type = type;
  }

  protected List<RGB> rgbList;

  public List<String> getPatchNameList() {
    return null;
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {

    if (rgbList == null) {
      if (type == null) {
        estimateLCDTargetNumber();
      }

      boolean[][] chIndex = null;
      switch (type) {
        case RGBCMYW_256:
        case RGBCMYW_1024: {
          chIndex = new boolean[][] {
              {
              true, false, false}, {
              false, true, false}, {
              false, false, true}, {
              false, true, true}, {
              true, false, true}, {
              true, true, false}, {
              true, true, true}
          };

          break;
        }
        case RGBW_1024:
        case RGBW_256: {
          chIndex = new boolean[][] {
              {
              true, false, false}, {
              false, true, false}, {
              false, false, true}, {
              true, true, true}
          };

          break;
        }
      }

      if (type == PatchType.RGBW_1024) {
        int size = 1024 * chIndex.length;
        rgbList = new ArrayList<RGB> (size);

        for (int ch = 0; ch < chIndex.length; ch++) {
          boolean[] primaryColor = chIndex[ch];
          for (double x = 0; x < 256; x += 0.25) {
            RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                              RGB.MaxValue.Double255);
            if (primaryColor[0] == true) {
              rgb.setValue(RGBBase.Channel.R, x);
            }
            if (primaryColor[1] == true) {
              rgb.setValue(RGBBase.Channel.G, x);
            }
            if (primaryColor[2] == true) {
              rgb.setValue(RGBBase.Channel.B, x);
            }
            rgbList.add(rgb);
          }
        }
      }
      else {
        int size = 256 * chIndex.length;
        rgbList = new ArrayList<RGB> (size);

        for (int ch = 0; ch < chIndex.length; ch++) {
          boolean[] primaryColor = chIndex[ch];
          for (int x = 0; x < 256; x++) {
            RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                              RGB.MaxValue.Double255);
            if (primaryColor[0] == true) {
              rgb.setValue(RGBBase.Channel.R, x);
            }
            if (primaryColor[1] == true) {
              rgb.setValue(RGBBase.Channel.G, x);
            }
            if (primaryColor[2] == true) {
              rgb.setValue(RGBBase.Channel.B, x);
            }
            rgbList.add(rgb);
          }
        }

        if (type == PatchType.RGBCMYW_1024) {
          for (double code = .25; code <= .75; code += .25) {
            int index = 0;

            for (int ch = 0; ch < chIndex.length; ch++) {
              boolean[] primaryColor = chIndex[ch];
              for (int x = 0; x < 256; x++) {
                RGB rgb = rgbList.get(index++);
                RGB newRGB = new RGB(rgb.getRGBColorSpace(), rgb.getValues(),
                                     RGB.MaxValue.Double255);
                if (primaryColor[0] == true) {
                  newRGB.R = newRGB.R < 255 ? newRGB.R + code : newRGB.R;
                }
                if (primaryColor[1] == true) {
                  newRGB.G = newRGB.G < 255 ? newRGB.G + code : newRGB.G;
                }
                if (primaryColor[2] == true) {
                  newRGB.B = newRGB.B < 255 ? newRGB.B + code : newRGB.B;
                }
                rgbList.add(newRGB);
              }
            }
          }
        }
      }

    }

    return rgbList;
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List<Spectra> getSpectraList() {
    throw new UnsupportedOperationException();
  }

  public List<Spectra> getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  protected List<CIEXYZ> XYZList = null;

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    if (XYZList == null) {
      XYZList = new LinkedList<CIEXYZ> ();
      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
          String line = reader.readLine();
          if (line == null) {
            break;
          }
          StringTokenizer toker = new StringTokenizer(line, ",");
          String X = toker.nextToken();
          String Y = toker.nextToken();
          String Z = toker.nextToken();
          double[] XYZValues = new double[3];
          XYZValues[0] = Double.parseDouble(X);
          XYZValues[1] = Double.parseDouble(Y);
          XYZValues[2] = Double.parseDouble(Z);
          CIEXYZ XYZ = new CIEXYZ(XYZValues);
          XYZList.add(XYZ);
        }
        reader.close();
      }
      catch (FileNotFoundException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }

    }

    return XYZList;
  }

  public static void main(String[] args) {
//    LCDTarget.setRGBNormalize(false);
//    LCDTarget target = LCDTarget.Instance.getFromVastView("7168.txt",
//        LCDTargetBase.Number.Patch7147);
//    for (Patch p : target.getPatchList()) {
//      System.out.println(p.getRGB());
//    }

    LCDTargetBase.Number number = LCDTargetBase.Number.getNumber(7168);
    System.out.println(number);
  }

  public Style getStyle() {
    return Style.RGBXYZ;
  }

  public String getFileNameExtension() {
    return "txt";
  }

  public String getFileDescription() {
    return "VastView File";
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    if (type == null) {
      List<CIEXYZ> list = getXYZList();
      LCDTargetBase.Number number = LCDTargetBase.Number.getNumber(list.size());
      type = getPatchType(number);
    }
    return getNumber(type);
  }

  public final boolean isInverseModeMeasure() {
    return false;
  }

}
