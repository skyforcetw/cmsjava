package shu.cms.profile.test;

import java.io.*;

import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.profile.*;

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
public class BatchProfileMaker {
  public static void main(String[] args) {

    CIEXYZ whiteXYZ = LightSource.getIlluminant(LightSource.i1Pro.D50).
        getNormalizeXYZ();
//    CIEXYZ whiteXYZ = LightSource.getIlluminant(LightSource.i1Pro.F12).
//        getNormalizeXYZ();
    whiteXYZ.times(100.);
    ViewingConditions vc = new ViewingConditions(whiteXYZ,
                                                 .11, 20,
                                                 Surround.
                                                 Dim, "");
//    ViewingConditions vc = new ViewingConditions(whiteXYZ,
//                                                 .07, 20,
//                                                 ViewingConditions.Surround.
//                                                 Dim);

    makeAllTypeDCProfile("factor/D200Raw_D50 CCSG_PolyBy20_20070605.factor", vc);
//    batch("factor/", vc, CAMConst.CATType.Bradford);
  }

  /**
   * 產生所有型態的Profile
   * @param factorFilename String
   * @param vc ViewingConditions
   */
  public static void makeAllTypeDCProfile(String factorFilename,
                                          ViewingConditions vc) {
    DCModel model = new DCPolynomialRegressionModel(factorFilename);

    String modelFactorFilename = model.getModelFactor().
        getModelFactorFilename();

    DCProfileMaker profileMaker = new DCProfileMaker();

    CAMConst.CATType[] catTypeArray = new CAMConst.CATType[] {
        CAMConst.CATType.vonKries, CAMConst.CATType.Bradford,
//        CAMConst.CATType.CAT02
    };

    for (CAMConst.CATType catType : catTypeArray) {
      profileMaker.setCATType(catType);
      Profile p = profileMaker.makeLabProfile(model);

      String profileFilename = "Profile/Camera/" + ProfileMaker.SoftwareName +
          "_" + modelFactorFilename + catType.name() + ".icc";
      System.out.println(profileFilename);
      iccessAdapter.storeICCProfileLutByLut16(p, profileFilename);
    }

    Profile p = profileMaker.makeJabLabProfile(model, vc);
    String profileFilename = "Profile/Camera/" + ProfileMaker.SoftwareName +
        "_" + modelFactorFilename + "CAM.icc";
    System.out.println(profileFilename);
    iccessAdapter.storeICCProfileLutByLut16(p, profileFilename);
//    System.out.println("errJab2XYZ: " + profileMaker.getErrorJab2XYZCount());
//    System.out.println("errXYZ2Jab: " + profileMaker.getErrorXYZ2JabCount());

    System.out.println(profileMaker.getReport());

    /*Profile p2 = profileMaker.makeJabLabModifiedProfile(model, vc);
     String profileFilename2 = "Profile/Camera/jCMS_" + modelFactorFilename +
        "CAMm.icc";
         System.out.println(profileFilename2);
         iccessAdapter.storeICCDisplayProfileLutByLut16(p2, profileFilename2);
     System.out.println("errJab2XYZ: " + profileMaker.getErrorJab2XYZCount());
     System.out.println("errXYZ2Jab: " + profileMaker.getErrorXYZ2JabCount());*/
  }

  /**
   * 將factorDirName下的factor全部產生為Profile
   * @param factorDirName String
   * @param vc ViewingConditions
   * @param catType CATType
   */
  public static void batch(String factorDirName, ViewingConditions vc,
                           CAMConst.CATType catType) {
    File dir = new File(factorDirName);
    for (File f : dir.listFiles(new ProfileUtils.FactorFilter())) {

      String filename = f.getPath();
      DCModel model = new DCPolynomialRegressionModel(filename);

      String modelFactorFilename = model.getModelFactor().
          getModelFactorFilename();
      String profileFilename = "Profile/Camera/" + ProfileMaker.SoftwareName +
          "_" + modelFactorFilename + ".icc";
      System.out.println(profileFilename);

      DCProfileMaker profileMaker = new DCProfileMaker();
      profileMaker.setCATType(catType);

      Profile p = profileMaker.makeJabLabProfile(model, vc);
//        Profile p = profileMaker.makeLabProfile(model);
      iccessAdapter.storeICCProfileLutByLut16(p, profileFilename);
//      System.out.println("errJab2XYZ: " + profileMaker.getErrorJab2XYZCount());
//      System.out.println("errXYZ2Jab: " + profileMaker.getErrorXYZ2JabCount());

      System.out.println(profileMaker.getReport());
    }

  }
}
