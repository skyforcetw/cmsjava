package shu.thesis.recover;

import java.util.List;

import java.awt.*;

import shu.cms.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;
import shu.math.array.*;

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
public class SpectraPlotter {
  public static void main(String[] args) {
    double[][] spectralData = SpectraDatabase.SOCS.getTypical();
    Illuminant illuminant = Illuminant.A;
    Spectra illuminantSpectra = illuminant.getSpectra().reduce(400, 700,
        10);

    IdealDigitalCamera camera = Materials.getSunCamera();

    int count = spectralData.length;

    //==========================================================================
    // 資料初始化
    //==========================================================================
    List<Spectra>
        spectraListOrg = Materials.getIlluminantSpectraList(spectralData,
        illuminantSpectra);
    double[][] rgbData = Materials.getRGBValues(spectraListOrg, camera);
    rgbData = Materials.getOriginalOutputRGBValues(rgbData, camera);

//    List<Patch> deltaEDataOrg = Patch.produceLabPatches(spectraListOrg,
//        ColorMatchingFunction.
//        CIE_1931_2DEG_XYZ, illuminantSpectra);
    //==========================================================================

    int k = 3;

//    List<Spectra> spectraListEst = new ArrayList<Spectra> (count);
//    Wiener wiener = new Wiener(camera, MunsellSVD.Type.Glossy, k,illuminantSpectra);
    Wiener wiener = new Wiener(camera, SpectraDatabase.Content.MunsellGlossy, k);
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    for (int x = 0; x < count; x++) {
//      double[] estimate = wiener.estimateSpectraData(rgbData[x]);
//      Spectra sw = Materials.getSpectra(estimate);
      Spectra sw = wiener.estimateSpectra(rgbData[x]);
//      spectraListEst.add(sw);
      plot.addSpectra(null, Color.RED, sw);
      plot.addSpectra(null, Color.GREEN, spectraListOrg.get(x));
      plot.setFixedBounds(1, 0, 1);
      System.out.println(DoubleArray.toString(sw.getData()));
      System.out.println(DoubleArray.toString(spectraListOrg.get(x).getData()));
      System.out.println("");

      try {
        Thread.sleep(500);
      }
      catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      plot.removeAllPlots();
    }

//    List<Patch> deltaEDataEst = Patch.produceLabPatches(spectraListEst,
//        ColorMatchingFunction.CIE_1931_2DEG_XYZ, illuminantSpectra);
//    DeltaEReport[] reports = DeltaEReport.report(deltaEDataOrg, deltaEDataEst, false);
//    System.out.println(reports[0]);

  }
}
