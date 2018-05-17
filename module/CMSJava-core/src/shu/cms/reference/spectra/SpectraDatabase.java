package shu.cms.reference.spectra;

import java.io.*;
import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.trans.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.io.files.*;
import shu.math.*;
import shu.util.log.*;
import shu.math.array.*;
//import shu.plot.*;
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
public final class SpectraDatabase {

  public final static class MunsellBook {

    protected static String parseMunsell(String code) {
      if (code.indexOf("NEUT") != -1) {
        String val = Double.toString(Double.parseDouble(code.substring(4).trim()));
        return Double.toString(Double.parseDouble(val) /
                               Math.pow(10, val.length() - 3));
      }

      String hue = null;
      switch (code.charAt(0)) {
        case 'A':
          hue = "2.5";
          break;
        case 'B':
          hue = "5";
          break;
        case 'C':
          hue = "7.5";
          break;
        case 'D':
          hue = "10";
          break;
        case 'E':
          hue = "1.25";
          break;
        case 'F':
          hue = "3.75";
          break;
        case 'G':
          hue = "6.25";
          break;
        case 'H':
          hue = "8.75";
          break;
      }
      String hue2 = null;
      if (code.charAt(1) == code.charAt(2)) {
        hue2 = String.valueOf(code.charAt(1));
      }
      else {
        hue2 = code.substring(1, 3);
      }

      double val = Double.parseDouble(code.substring(3, 5));
//      String value = Double.toString(val / 10.);
      String value = fmt.format(val / 10.);

      double chr = Double.parseDouble(code.substring(5, 7));
      String chroma = fmt.format(chr);
      return hue + hue2 + " " + value + "/" + chroma;
    }

    protected final static DecimalFormat fmt = new DecimalFormat("###.#");

    /**
     * 載入glossy版本的patch name
     * @return String[]
     */
    public static String[] getGlossyPatchName() {
      File i = new File(
          "Reference Files/Spectra Database/Munsell/munsell_glossy_patchname.txt");
      String[] result = new String[1600];
      try {
        BufferedReader br = new BufferedReader(new FileReader(i));
        for (int x = 0; x < 1600; x++) {
          String line = br.readLine();
          result[x] = parseMunsell(line.trim());
        }
      }
      catch (FileNotFoundException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }

      return result;
    }

    public static double[][] getMattEdition() {
      //讀取資料
      double[][] matt = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/Munsell/munsell400_700_10_matt.dat",
          31);
      return matt;
    }

//    public static List<Spectra> getGlossyEditionSpectra() {
//      double[][] data = getGlossyEdition();
//      String[] patchname = getGlossyPatchName();
//      int size = data.length;
//      List<Spectra> spectraList = new ArrayList<Spectra> (size);
//      for (int x = 0; x < size; x++) {
//        Spectra s = new Spectra(patchname[x], Spectra.SpectrumType.REFLECTANCE,
//                                SPECTRA_START, SPECTRA_END,
//                                SPECTRA_INTERVAL, data[x]);
//        spectraList.add(s);
//      }
//      return spectraList;
//
//    }

    public static double[][] getGlossyEdition() {
      //讀取資料
      double[][] glossy = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/Munsell/munsell400_700_10_glossy.dat",
          31);
      return glossy;
    }

    public static double[][] getGlossyEditionPrecise() {
      //讀取資料
      double[][] glossy = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/Munsell/munsell380_780_5_glossy.dat",
          81);
      return glossy;
    }

    public static double[][] getAll() {
      //讀取資料
      double[][] matt = getMattEdition();
      double[][] glossy = getGlossyEdition();

      //合併
      double[][] spectraData = DoubleArray.mergeRows(matt, glossy);
      return spectraData;
    }

  }

  public final static class SOCS {
    /**
     * 從原始SOCS的資料(PDDE format)轉成double array並且儲存起來
     * @param filename String
     */
    private final static void produceSOCSData(String filename) {
      CXFOperator.produceDoubleArrayData(DIRECTORY, filename);
    }

    private final static void produceSOCSTypicalData(String filename) {
      CXFOperator.produceDoubleArrayData(TRDatabase_DIRECTORY + '/' +
                                         SOCS2CxF.TRDatabase.Typical.toString(),
                                         filename);
    }

    private final static void produceSOCSDifferenceData(String filename) {
      CXFOperator.produceDoubleArrayData(TRDatabase_DIRECTORY + '/' +
                                         SOCS2CxF.TRDatabase.Difference.
                                         toString(),
                                         filename);
    }

    public final static String DIRECTORY =
        "Reference Files/Spectra Database/SOCS/SourceData/int";

    public final static String TRDatabase_DIRECTORY =
        "Reference Files/Spectra Database/SOCS/TRDatabase";

    public final static double[][] getSOCS() {
      double[][] socsData = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/SOCS/socs.dat", 31);
      return socsData;
    }

    public final static double[][] getTypical() {
      double[][] socsData = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/SOCS/socsTypical.dat", 31);
      return socsData;
    }

    public final static double[][] getDifference() {
      double[][] socsData = BinaryFile.readDoubleArray(
          "Reference Files/Spectra Database/SOCS/socsDifference.dat", 31);
      return socsData;
    }

    /**
     * 同色異譜檢驗(取typical和diff相同者)
     * @return double[][]
     */
    public final static double[][] getMetamers() {
      //讀取資料
      double[][] typical = getTypical();
      double[][] difference = getDifference();
      typical = DoubleArray.getRowsRangeCopy(typical, 0, difference.length - 1);

      //合併
      double[][] spectraData = DoubleArray.mergeRows(typical, difference);
      return spectraData;
    }

    public final static double[][] getTRDatabase() {
      //讀取資料
      double[][] typical = getTypical();
      double[][] difference = getDifference();

      //合併
      double[][] spectraData = DoubleArray.mergeRows(typical, difference);
      return spectraData;
    }

  }

  private SpectraDatabase() {

  }

  protected static List<Spectra> getSpectraList(double[][] spectraData,
                                                Content content) {
    int size = spectraData.length;
    List<Spectra> spectraList = new ArrayList<Spectra> (size);
    String[] patchArray = null;
    if (content.munsellGlossy) {
      patchArray = MunsellBook.getGlossyPatchName();
    }

    for (int x = 0; x < size; x++) {
      String name = null;
      if (content.munsellGlossy == true) {
        name = patchArray[x];
      }
      Spectra s = new Spectra(name, Spectra.SpectrumType.REFLECTANCE,
                              content.start, content.end,
                              content.interval, spectraData[x]);
      spectraList.add(s);
    }
    return spectraList;
  }

  public static enum Content {
    MunsellMatt(400, 700, 10, 1269, false),
    MunsellGlossy(400, 700, 10, 1600, true),
    MunsellAll(400, 700, 10, 2869, false),
    SOCS(400, 700, 10, -1, false),
    SOCSMetamers(400, 700, 10, 260, false),
    SOCSTypical(400, 700, 10, 235, false),
    SOCSDifference(400, 700, 10, 130, false),
    SOCSTR(400, 700, 10, 365, false),
    MunsellGlossyPrecise(380, 780, 5, 1600, true),
    MunsellMattPrecise(380, 780, 5, 1269, false),
    MunsellGlossyHighPrecise(380, 780, 2, 1600, true), ;

    Content(int start, int end, int interval, int count, boolean munsellGlossy) {
      this.start = start;
      this.end = end;
      this.interval = interval;
      this.count = count;
      this.munsellGlossy = munsellGlossy;
    }

    public int start, end, interval, count;
    boolean munsellGlossy = false;
  }

  public static List<Spectra> getSpectraList(Content type) {
    return getSpectraList(getSpectraData(type), type);
  }

  public static double[][] getSpectraData(Content type) {
    switch (type) {
      case MunsellMatt:
        return MunsellBook.getMattEdition();
      case MunsellGlossy:
        return MunsellBook.getGlossyEdition();
      case MunsellAll:
        return MunsellBook.getAll();
      case MunsellGlossyPrecise:
        return MunsellBook.getGlossyEditionPrecise();
      case SOCS:
        return SOCS.getSOCS();
      case SOCSMetamers:
        return SOCS.getMetamers();
      case SOCSTypical:
        return SOCS.getTypical();
      case SOCSDifference:
        return SOCS.getDifference();
      case SOCSTR:
        return SOCS.getTRDatabase();

      default:
        return null;
    }
  }

  public static void main(String[] args) {

    List<Spectra> spectraList = getSpectraList(Content.MunsellGlossy);
    Spectra ss = spectraList.get(0);
    Spectra c = Illuminant.C.getSpectra();
    c = c.fillAndInterpolate(ss);
    CIEXYZ white = c.getXYZ();

    for (Spectra s : spectraList) {
//      System.out.println(s.getName());
      if (s.getName().equals("5R 4/12")) {
        Plot2D p = Plot2D.getInstance();
        p.setVisible();
        p.addSpectra("", s);
        CIEXYZ XYZ = s.getXYZ(c);
        XYZ.normalize(white);
//        CIELab Lab = new CIELab(XYZ, white);
        System.out.println(XYZ + " " + new CIExyY(XYZ));
      }
    }
  }

}
