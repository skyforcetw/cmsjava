package auo.cms.test;

//import shu.cms.colorformat.file.ExcelFile;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorformat.file.AUORampXLSFile;
import java.io.*;
import shu.cms.*;
import java.util.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;

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
public class MeasurementAverager {

  public static void main(String[] args) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    for (int x = 0; x <= 50; x++) {
      RGB rgb = new RGB(x, x, x);
      rgbList.add(rgb);
    }

    String dir = "D:/ณnล้/nobody zone/exp data/CCTv3/111118/probility test/";
//    String[] filenames = new String[] {
//        "Measurement01.xls", "Measurement02.xls", "Measurement03.xls"};
    try {
      String[] filenames = new String[] {
          "Measurement01.xls", "Measurement02.xls", "Measurement03.xls"};
      average(dir, filenames, "01.xls", rgbList);

      filenames = new String[] {
          "Measurement04.xls", "Measurement05.xls", "Measurement06.xls"};
      average(dir, filenames, "01.xls", rgbList);

      filenames = new String[] {
          "Measurement07.xls", "Measurement08.xls", "Measurement09.xls"};
      average(dir, filenames, "01.xls", rgbList);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  static void average(String dir, String[] filenames, String output, List<RGB>
      rgbList) throws
      IOException {
    int size = filenames.length;
    LCDTarget[] targets = new LCDTarget[size];
    for (int x = 0; x < size; x++) {
//      targets[x] = LCDTarget.Instance.getFromAUOXLS(dir + filenames[x]);
//      targets[x] = LCDTarget.Instance.getFromAUORampXLS(dir + filenames[x]);
      AUORampXLSAdapter adapter = new AUORampXLSAdapter(dir + filenames[x],
          rgbList);
      targets[x] = LCDTarget.Instance.get(adapter);

    }
    LCDTarget result = LCDTarget.Operator.average(targets);
    List<Patch> patchList = result.getPatchList();
    int listsize = patchList.size();
    for (int x = 1; x < listsize; x++) {
      Patch p0 = patchList.get(x - 1);
      Patch p1 = patchList.get(x);
      CIEXYZ XYZ0 = p0.getXYZ();
      CIEXYZ XYZ1 = p1.getXYZ();
      CIExyY xyY0 = new CIExyY(XYZ0);
      CIExyY xyY1 = new CIExyY(XYZ1);
      double[] dxy = xyY1.getDeltaxy(xyY0);
      if (dxy[0] < 0 || dxy[1] < 0) {
        System.out.println(x + " " + dxy[0] + " " + dxy[1]);
      }
//      XYZ1.
    }
//    ExcelFile xls = new
//    AUORampXLSFile ramp = new AUORampXLSFile(output, result);
//    ramp.save();
  }
}
