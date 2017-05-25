package shu.cms.dc.ideal;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;
import shu.math.*;
import shu.math.array.*;
import shu.cms.plot.*;
import java.awt.Color;
import shu.math.lut.Interpolation1DLUT;

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
public class IdealDigitalCamera {
  protected Spectra[] sensors;
  protected String name;
  protected Illuminant referenceWhite;
//  protected Interpolation1DLUT luminanceLut;

  /**
   *
   * @param luminanceLut Interpolation1DLUT
   * @deprecated
   */
//  public void setLuminanceLut(Interpolation1DLUT luminanceLut) {
//    this.luminanceLut = luminanceLut;
//  }

  /**
   *
   * @return Interpolation1DLUT
   * @deprecated
   */
//  public Interpolation1DLUT getLuminanceLut() {
//    return luminanceLut;
//  }

  public IdealDigitalCamera(Spectra[] sensor) {
    this(sensor, "IdealDigitalCamera", Illuminant.E);
  }

  public IdealDigitalCamera(Spectra[] sensor, String name) {
    this(sensor, name, Illuminant.E);
  }

  public IdealDigitalCamera(Spectra[] sensor, String name,
                            Illuminant referenceWhite) {
    this.sensors = sensor;
    this.name = name;
    this.referenceWhite = referenceWhite;
    initMaximunRGBValues();
  }

  /**
   * EstimatedD200_1 : 較早實驗產生的,用在實際光譜模式上
   * EstimatedD200_2 : r=4產生的,根據rgb誤差選出較準者,但用在光譜模式不準
   */
  public enum Source {
    CIEXYZ, Sun, EstimatedD200_1, TestD200, EstimatedD200_2, BestEstimatedD200,
    AdobeRGB
  }

  /**
   * 依照spectras中最大的Spectra之peak為max,讓spectras皆進行正規化
   * @param spectras Spectra[]
   * @return Spectra[]
   */
  protected static Spectra[] normalize(Spectra[] spectras) {
    double max = Double.MIN_VALUE;
    for (int x = 0; x < 3; x++) {
      double[] data = spectras[x].getData();
      double maxone = Maths.max(data);
      max = Math.max(max, maxone);
    }

    for (int x = 0; x < 3; x++) {
      Maths.normalize(spectras[x].getData(), max);
    }
    return spectras;
  }

  public static void main(String[] args) {

    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(Source.Sun);
//    IdealDigitalCamera camera = IdealDigitalCamera.getSunInstance(Illuminant.E);
    Plot2D plot = Plot2D.getInstance();
    Spectra[] sensors = camera.getSensors();
    plot.addSpectra("R", Color.red, sensors[0]);
    plot.addSpectra("G", Color.green, sensors[1]);
    plot.addSpectra("B", Color.blue, sensors[2]);
    plot.setVisible();

//    Plot2D plot = Plot2D.getInstance();
//    plot.addSpectra("R", camera.getSensors()[0]);
//    plot.addSpectra("G", camera.getSensors()[1]);
//    plot.addSpectra("B", camera.getSensors()[2]);
//    plot.setVisible(true);
  }

  public final static IdealDigitalCamera getSunInstance(Illuminant illuminant) {
    SunOptimalSensor sunSensor = new SunOptimalSensor();
    Spectra[] sensor = new Spectra[3];
    sensor[0] = sunSensor.getSpectralSensitivityFunctions()[0];
    sensor[1] = sunSensor.getSpectralSensitivityFunctions()[1];
    sensor[2] = sunSensor.getSpectralSensitivityFunctions()[2];
    sensor = normalize(sensor);

    IdealDigitalCamera camera = null;
    if (illuminant != null) {
      camera = new IdealDigitalCamera(sensor,
                                      "SunOptimalCamera-" +
                                      illuminant.getSpectra().getName());
    }
    else {
      return new IdealDigitalCamera(sensor,
                                    "SunOptimalCamera");
    }

    if (illuminant == Illuminant.D50) {
      camera.setSensorFactor(0, 0.5691111111111112);
      camera.setSensorFactor(1, 0.6508888888888894);
      camera.setSensorFactor(2, 1);
      //13: 0.20208533529296008(minRMS) 0.4000000000000002(R) 0.5200000000000002(G) 1.0(B)
    }
    else if (illuminant == Illuminant.D55) {
      camera.setSensorFactor(0, 0.5708888888888893);
      camera.setSensorFactor(1, 0.6588888888888894);
      camera.setSensorFactor(2, 1);
    }
    else if (illuminant == Illuminant.D65) {
      camera.setSensorFactor(0, 0.5588888888888913);
      camera.setSensorFactor(1, 0.6511111111111003);
      camera.setSensorFactor(2, 1);
    }
    else if (illuminant == Illuminant.E) {
      camera.setSensorFactor(0, 0.5699988888890903);
      camera.setSensorFactor(1, 0.6588888888888894);
      camera.setSensorFactor(2, 1);
    }

    return camera;
  }

  protected final static double[] TEST_D200_R = {
      8.653195611189798E-4, 0.002614420092368568, 0.005802199330463242,
      0.00287454476585459, 0.014223413705144104, 0.0, 0.0, 0.03020865664345063,
      0.04328798412177606, 0.049101008980054844, 0.03257419300761817,
      0.011574527687267439, 0.02060728824887035, 0.0011346716881942822, 0.0,
      0.0, 0.08090137464091954, 0.009304771026125827, 0.01503909728966346,
      0.06170649522441487, 0.09558500729304488, 0.1688596932430345,
      0.22424691903662095, 0.3389371548987051, 0.2718178018686679,
      0.13437642756704124, 0.05004342290047347, 0.030852924697439377,
      0.021334870944973175, 0.01886122405233155, 0.021918277817175966,
      0.029366553149753075, 0.03551462445857167, 0.08183349887669247,
      0.03594176195278676, 0.016677864938946886};
  protected final static double[] TEST_D200_G = {
      0.0026496014041573167, 0.006860052062986139, 0.008413270972138693, 0.0,
      0.031839861575858844, 0.0, 0.0, 0.08212952644763587, 0.12588007256434616,
      0.15915268109515796, 0.14978944853195852, 0.15421285239907795,
      0.20746761179636286, 0.20799537304274643, 0.23695479122452706,
      0.2726377561124781, 0.5452073630519602, 0.36512540085781603,
      0.20197480729250403, 0.22160077402232445, 0.10879256557052983,
      0.07029759923801981, 0.14696883953587456, 0.024635123346964414,
      0.050886853599507585, 0.0, 0.0, 0.0, 0.0, 0.0, 0.009476230069474137,
      0.03381909963771709, 0.05172628222546264, 0.08491343255496822,
      0.06370248854257242, 0.06564227310839538};
  protected final static double[] TEST_D200_B = {
      0.0018011979560376078, 0.005309815052008217, 0.048800111493560304,
      0.09006259665176017, 0.07372612688273014, 0.2608808703358504,
      0.4101335195594612, 0.1911988877892392, 0.1884263719625698,
      0.18494138207819585, 0.23465352814201926, 0.41099536617295634,
      0.23538078490128392, 0.06418162407897209, 0.007365418549114376, 0.0,
      0.07413075919744883, 0.0, 0.0028402012176825054, 0.036081439762314906,
      0.0, 0.0, 0.010187843615078292, 0.00524575899141895, 0.011637858462166684,
      0.0, 0.0, 0.0, 0.0, 0.001239093910537894, 0.00729706440511371,
      0.011901341958815115, 0.015054069350774567, 0.020482460340576658,
      0.02064050525378903, 0.026582481247958135};

  private final static double[] ESTIMATED_D200_1_R = {
      1.9206866220272483E-4, 3.9616156205297276E-4, 0.002743724824316867,
      0.0045659925493730085, 0.004081048116896677, 0.013236998290551475,
      0.01916416800919375, 0.011396610653471442, 0.0075842822057756165,
      0.0033573140616864595, 0.0010678331497417709, 4.667765424662898E-4, 0.0,
      0.0, 0.0, 0.0, 0.001278284051809985, 0.018937571406879936,
      0.029410988962120205, 0.04452094781124841, 0.06409602849809837,
      0.07386119133038445, 0.07024130865266442, 0.08865426285099544,
      0.07909085539951215, 0.056459268689890346, 0.04101378722048149,
      0.0359552227126732, 0.0312535961451652, 0.023992753019905156,
      0.01917329683755283, 0.015829180493987807, 0.013687440103284043,
      0.015036731155557866, 0.010449391543300249, 0.011281220185633224};
  private final static double[] ESTIMATED_D200_1_G = {
      9.246582768518978E-4, 0.002427221172689326, 0.01074003984370884,
      0.01937453625409783, 0.011631377815017399, 0.02013637128573783,
      0.02733936970983069, 0.0014795718741479381, 0.015276406170808795,
      0.03293892925411035, 0.0417575714168676, 0.04016306661647624,
      0.07149675951411996, 0.10598148609649306, 0.12420278048090704,
      0.12219628177941959, 0.13330030429241557, 0.15387902842620732,
      0.09949925090146224, 0.09021678925499386, 0.07661562110709626,
      0.04135264499674404, 0.02972304450457264, 0.0, 0.0, 0.002747690173568342,
      0.01226209123954077, 0.010561605953296558, 0.008606756086487458,
      0.01064892320064832, 0.011834568078091967, 0.010772614069136913,
      0.008972375558884822, 0.0029153628464273925, 0.005908397361459406,
      0.007845366453819494};
  private final static double[] ESTIMATED_D200_1_B = {
      8.466421185055089E-4, 0.0021479720775096525, 0.01498801527244721,
      0.030736814048727454, 0.032261962717750864, 0.10547675183227355,
      0.16823471989941172, 0.11329874000141983, 0.10304243664872512,
      0.08789504731476394, 0.08458112334924443, 0.10011882472755358,
      0.06568299033319808, 0.03416848766630367, 0.01706994557367644,
      0.006148847442303856, 0.013931223720276506, 0.003159871261920496, 0.0,
      5.446868647269313E-4, 0.0, 0.0, 0.0017615635248401612, 0.0, 0.0,
      1.979855129675584E-4, 0.0037959257590047943, 0.008161907601412046,
      0.013192315311797716, 0.005812218801518574, 8.947740381160538E-4, 0.0,
      0.0, 0.0, 0.0, 3.3078744389306307E-4};

  private final static double[] ESTIMATED_D200_2_R = {
      2.8169492495518872E-5, 7.267709637968446E-5, 0.001449368215126346,
      0.003508016545226787, 0.003551898874633765, 0.029499897310160433,
      0.05082940588681613, 0.0320318282179247, 0.03766476116460484,
      0.03257829713550776, 0.016894150759603353, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.010947846002907548, 0.04817908260402573, 0.06624858034156592,
      0.10947669456940003, 0.16464165101072048, 0.17896908045066476,
      0.18758074666035407, 0.17813714114302248, 0.15533259673361788,
      0.12872920479784686, 0.108432511668014, 0.09372288220155307,
      0.0771703475130316, 0.06009078380536774, 0.0467860197950814,
      0.03456454892954273, 0.02428797237547083, 0.017114589938758112,
      0.012382450924578734, 0.010080857352348139
  };

  private final static double[] ESTIMATED_D200_2_G = {
      1.3412417373022612E-4, 1.225418093567791E-4, 7.939688656613177E-4,
      9.643000176358066E-4, 9.784608057066854E-4, 0.010039650398810994,
      0.021872245466725355, 0.02008555443486954, 0.04214129689533059,
      0.07870450321991919, 0.12633029857872016, 0.17790634542980185,
      0.2208082138885729, 0.24955529639516533, 0.2759240485661467,
      0.289378383556936, 0.36144829885718144, 0.4082288307659948,
      0.23894545589078642, 0.2036824624514813, 0.1705186416711311,
      0.09578524023050133, 0.04264438854905492, 0.01189184023151257,
      0.0028098680235534514, 0.003495992316063242, 0.003284008543778289,
      0.0013487964074449603, 0.0017698982264430778, 0.006235586719885515,
      0.012360938725463674, 0.01779289846093158, 0.02119199769682869,
      0.02160813836874128, 0.019563126274256836, 0.021084990042713376

  };

  private final static double[] ESTIMATED_D200_2_B = {
      3.143252922758097E-4, 5.593822238443093E-4, 0.008518077522356888,
      0.018844663178023056, 0.019976107281365323, 0.17559508663787854,
      0.3153738235556666, 0.2107843114049149, 0.2799072734901661,
      0.31795992655288263, 0.31334917401447143, 0.27800639014230694,
      0.2266922432727182, 0.1602350467535901, 0.08459568890472255,
      0.020697406071250365, 0.0, 0.0, 0.0, 0.0, 0.0, 0.009421956384720495,
      0.02147360964462813, 0.021202294344702576, 0.012106355394224394,
      0.0037058060713780747, 0.002088924674802744, 0.0049789943987796435,
      0.007064258438910467, 0.004893338437661039, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
  };

  private final static double[] BEST_ESTIMATED_D200_R = {
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.01643611207412361, 0.04126756476338874, 0.05987086601773663,
      0.07699700232324203, 0.10053045202404637, 0.12097935395795414,
      0.13972339107072237, 0.15395960082622054, 0.15556592402727276,
      0.1507406445072817, 0.1496173602162369, 0.13611956544375545,
      0.11891334708490617, 0.1120565344588203, 0.09260985608043772,
      0.08226226715587083, 0.07994363838439043, 0.07281328881034198,
      0.03199441055714586, 0.030380011642884997, 0.012335662476172975, 0.0
  };

  private final static double[] BEST_ESTIMATED_D200_G = {
      0.00322316257810452, 7.065028271842774E-4, 0.0, 0.0, 0.0, 0.0,
      0.005995584082644515, 0.016845130919272002, 0.03861926104478992,
      0.052813951453594826, 0.0781614352351137, 0.12526430302442018,
      0.1558418797295635, 0.20644084246960845, 0.2440694429298496,
      0.32086198165456115, 0.3399430613159271, 0.3487200969316052,
      0.35556871390463085, 0.3161004684954977, 0.26397084114860125,
      0.17898349097847568, 0.09590764460222213, 0.023532914561103114, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.04638690936772883, 0.04706920823077983,
      0.07594813737872322, 0.10970055332911066

  };

  private final static double[] BEST_ESTIMATED_D200_B = {
      0.002092680559561246, 0.009223588859060298, 0.039174644041672715,
      0.10112002710282514, 0.14972210005946107, 0.16229132684568628,
      0.18506826996042217, 0.21080655635310463, 0.24135244713671045,
      0.2604585952776644, 0.2361996179765288, 0.22603165781876897,
      0.19111492778097186, 0.1584788545217288, 0.12679314724486673,
      0.08282813656342637, 0.05365192252339625, 0.023702167255976767, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 6.919185912154526E-4, 5.533278219424077E-4,
      0.004085864772836746, 0.0074410060093318305, 0.0023328068336044634,
      0.009404445353148074, 0.017936480361920887, 0.021203832922711913, 0.0,
      0.002211629839204811, 4.985603542025412E-4, 0.0156864826586267
  };

  private final static double[] ADOBE_RGB_R = {
      0.017761000079846403, 0.016960785625610503, 0.015555701858186553,
      0.013502298028921207, 0.015519973077887128, 0.01667375095342326,
      0.01720605416521656, 0.018086031943889934, 0.021098316762108136,
      0.02481629120974827, 0.03189021960029201, 0.03932102086349147,
      0.04567277574331709, 0.05176940077181937, 0.05279521471418749,
      0.05582618450350056, 0.05921006157426044, 0.063693324335104,
      0.0740687692536937, 0.08458328279976367, 0.10307904570565725,
      0.11650512492606852, 0.12289201263628956, 0.1212887367718214,
      0.1241277265249314, 0.11952608165884164, 0.12068302445067118,
      0.1245065984481773, 0.11929683359292792, 0.10720880255918395,
      0.11103834728664429};

  private final static double[] ADOBE_RGB_G = {
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.015629582418213397, 0.038482939137362206,
      0.06953727695554308, 0.10113145299774666, 0.13625821469047714,
      0.15823073658556266, 0.16665196498665344, 0.1733817796171423,
      0.15807189420000436, 0.13594859293680753, 0.10354078829784436,
      0.07720909113202784, 0.05919649872992251, 0.04478323130270107,
      0.041894276093487906, 0.04258191660611148, 0.04419852928298758,
      0.042886338439514504, 0.041680522520577315, 0.03689101875995425,
      0.034753279226553184, 0.034863629177798526, 0.034016413260084,
      0.03185994260267928, 0.03440961375326936
  };

  private final static double[] ADOBE_RGB_B = {
      0.06608129696863757, 0.09245686008206816, 0.10176643576864709,
      0.09818714876702403, 0.12337711992641848, 0.14294551468644767,
      0.1494253328959652, 0.15333273418144597, 0.16149567500093076,
      0.15527760513950564, 0.15013914557295488, 0.12196474307418656,
      0.07364147520154228, 0.03159972393514169, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.010980047241909157, 0.03403529324672787, 0.045791902033405676,
      0.05474521184047344, 0.058232762880827176, 0.06374437852108009,
      0.06903011268433573, 0.06795535688017794, 0.061999648875707705,
      0.06486230385818983
  };

  public final static IdealDigitalCamera getInstance(Source type,
      Illuminant referenceWhite) {
    Spectra[] sensor = new Spectra[3];
    String name = "IdealDigitalCamera";
    switch (type) {
      case CIEXYZ:
        sensor[0] = (Spectra) ColorMatchingFunction.CIE_1931_2DEG_XYZ.
            getSpectra(0).clone();
        sensor[1] = (Spectra) ColorMatchingFunction.CIE_1931_2DEG_XYZ.
            getSpectra(1).clone();
        sensor[2] = (Spectra) ColorMatchingFunction.CIE_1931_2DEG_XYZ.
            getSpectra(2).clone();
        sensor = normalize(sensor);
        name = "CIEXYZ";
        break;
      case Sun:
        SunOptimalSensor sunSensor = new SunOptimalSensor();
        sensor[0] = sunSensor.getSpectralSensitivityFunctions()[0];
        sensor[1] = sunSensor.getSpectralSensitivityFunctions()[1];
        sensor[2] = sunSensor.getSpectralSensitivityFunctions()[2];
        sensor = normalize(sensor);
        name = "SunOptimalCamera";
        break;
      case EstimatedD200_1:
        sensor[0] = new Spectra("EstimatedD200_1-R",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_1_R);
        sensor[1] = new Spectra("EstimatedD200_1-G",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_1_G);
        sensor[2] = new Spectra("EstimatedD200_1-B",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_1_B);
        name = "EstimatedD200_1";
        break;

      case EstimatedD200_2:
        sensor[0] = new Spectra("EstimatedD200_2-R",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_2_R);
        sensor[1] = new Spectra("EstimatedD200_2-G",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_2_G);
        sensor[2] = new Spectra("EstimatedD200_2-B",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                ESTIMATED_D200_2_B);
        name = "EstimatedD200_2";
        break;
      case TestD200:
        sensor[0] = new Spectra("TestD200-R",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                TEST_D200_R);
        sensor[1] = new Spectra("TestD200-G",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                TEST_D200_G);
        sensor[2] = new Spectra("TestD200-B",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                TEST_D200_B);
        name = "TestD200";
        break;
      case BestEstimatedD200:
        sensor[0] = new Spectra("BestEstimatedD200-R",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                BEST_ESTIMATED_D200_R);
        sensor[1] = new Spectra("BestEstimatedD200-G",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                BEST_ESTIMATED_D200_G);
        sensor[2] = new Spectra("BestEstimatedD200-B",
                                Spectra.SpectrumType.FUNCTION, 380, 730, 10,
                                BEST_ESTIMATED_D200_B);
        name = "BestEstimatedD200";
        break;
      case AdobeRGB:
        sensor[0] = new Spectra("AdobeRGB-R",
                                Spectra.SpectrumType.FUNCTION, 400, 700, 10,
                                ADOBE_RGB_R);
        sensor[1] = new Spectra("AdobeRGB-G",
                                Spectra.SpectrumType.FUNCTION, 400, 700, 10,
                                ADOBE_RGB_G);
        sensor[2] = new Spectra("AdobeRGB-B",
                                Spectra.SpectrumType.FUNCTION, 400, 700, 10,
                                ADOBE_RGB_B);
        name = "AdobeRGB";
        referenceWhite = Illuminant.D65;
        break;

    }

    IdealDigitalCamera camera = new IdealDigitalCamera(sensor, name,
        referenceWhite);

    if (type == Source.Sun) {
      camera.setSensorFactor(0, 0.5693999999999998);
      camera.setSensorFactor(1, 0.6539999999999998);
      camera.setSensorFactor(2, 1);
    }

    return camera;
  }

  public final static IdealDigitalCamera getInstance(Source type) {
    return getInstance(type, Illuminant.E);
  }

  public Spectra[] getSensors() {
    return sensors;
  }

  public void reduceSensors(int start, int end, int interval) {
//    for (Spectra sensor : sensors) {
//      sensor = sensor.reduceSpectra(start, end, interval);
//    }
    for (int x = 0; x < sensors.length; x++) {
      Spectra sensor = sensors[x];
      sensors[x] = sensor.reduce(start, end, interval);
    }
  }

  public String getName() {
    return name;
  }

  public double[] getMaximunRGBValues() {
    return maximunRGBValues;
  }

  public List<RGB> produceRGBList(List<Spectra> spectraList) {
    int size = spectraList.size();
    List<RGB> rgbList = new ArrayList<RGB> (size);

    for (int x = 0; x < size; x++) {
      Spectra s = spectraList.get(x);
      double[] RGBValues = capture(s);
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGBValues,
                        RGB.MaxValue.Double1);
      rgbList.add(rgb);
    }

    return rgbList;
  }

  protected double[] maximunRGBValues;

  /**
   * 數位相機對光譜的反應, 原始訊號正規化後輸出
   * @param spectra Spectra
   * @return double[]
   */
  public double[] capture(Spectra spectra) {
    double[] originalOutput = captureOriginalOutput(spectra);
    if (!originalOutputOnly) {
      Maths.normalize(originalOutput, maximunRGBValues);
    }
    return originalOutput;
  }

  private boolean originalOutputOnly = false;
  public void setOriginalOutputOnly(boolean originalOutputOnly) {
    this.originalOutputOnly = originalOutputOnly;
  }

//  public double[] captureWithLuminanceCorrect(Spectra spectra) {
//    return null;
//  }

  /**
   * 數位相機對光譜的反應, 原始訊號的輸出
   * @param spectra Spectra
   * @return double[]
   */
  public double[] captureOriginalOutput(Spectra spectra) {
    double[] rgbValues = spectra.getRGBValues(this);
    return rgbValues;
  }

  public double[] captureWithGreenCorrect(Spectra spectra) {
    if ( -1 == greenCorrectFactor) {
      throw new IllegalStateException("-1 == greenCorrectFactor");
    }
    double[] rgbValues = captureOriginalOutput(spectra);
    double[] correctRGBValues = DoubleArray.times(rgbValues, greenCorrectFactor);
    return correctRGBValues;
  }

  private double greenCorrectFactor = -1;
  public void setGreenCorrectFactor(double factor) {
    this.greenCorrectFactor = factor;
  }

  protected void initMaximunRGBValues() {
    if (maximunRGBValues == null) {
      maximunRGBValues = referenceWhite.getSpectra().getRGBValues(this);
    }
  }

  /**
   * 推回尚未正規化的數位相機Digital Value
   * @param normalizeRGBValues double[]
   * @return double[]
   */
  public double[] getOriginalOutputRGBValues(double[] normalizeRGBValues) {
    Maths.undoNormalize(normalizeRGBValues, maximunRGBValues);
    return normalizeRGBValues;
  }

  public void setSensors(Spectra[] sensors) {
    this.sensors = sensors;
  }

  public void setSensorFactor(int sensorIndex, double factor) {
    sensors[sensorIndex].times(factor);
  }

  public void setSensorFactor(double[] factors) {
    if (factors.length != 3) {
      throw new IllegalArgumentException("factors.length != 3");
    }
    setSensorFactor(0, factors[0]);
    setSensorFactor(1, factors[1]);
    setSensorFactor(2, factors[2]);
  }

  /**
   *
   * @param camera IdealDigitalCamera
   * @param factorStart double[]
   * @param factorEnd double[]
   * @param trainingData double[][]
   * @return double[]
   * @deprecated
   */
  public static double[] sensorFactorOptimum(IdealDigitalCamera camera,
                                             double[] factorStart,
                                             double[] factorEnd,
                                             double[][] trainingData) {
    double[][] spectralData = trainingData;

    int count = spectralData.length;
    double[][] rmsData1 = new double[count][];
    double[][] rmsData2 = new double[count][];

    double minRMS = Double.MAX_VALUE;
    double r = 0, g = 0, b = 0;
    Spectra[] sensors = camera.getSensors();
    double[] s0 = sensors[0].getData();
    double[] s1 = sensors[1].getData();
    double[] s2 = sensors[2].getData();

    Spectra[] munsellSpectralData = new Spectra[count];
    for (int x = 0; x < count; x++) {
      munsellSpectralData[x] = new Spectra(null, Spectra.SpectrumType.EMISSION,
                                           400,
                                           700,
                                           10,
                                           spectralData[x]);
      rmsData1[x] = spectralData[x];

    }

    for (double rFactor = factorStart[0]; rFactor <= factorEnd[0];
         rFactor += 0.001) {
      for (double gFactor = factorStart[1]; gFactor <= factorEnd[1];
           gFactor += 0.001) {
        for (double bFactor = factorStart[2]; bFactor <= factorEnd[2];
             bFactor += 0.001) {
          sensors[0].setData(DoubleArray.times(s0, rFactor));
          sensors[1].setData(DoubleArray.times(s1, gFactor));
          sensors[2].setData(DoubleArray.times(s2, bFactor));

          Wiener wiener = new Wiener(camera, SpectraDatabase.Content.MunsellAll,
                                     3);

          for (int x = 0; x < count; x++) {
            double[] rgb = munsellSpectralData[x].getRGBValues(camera);
            double[] R = wiener.estimateSpectraData(rgb);
            rmsData2[x] = R;
          }

          double rmsd = Maths.RMSD(rmsData1, rmsData2);
          System.out.println(rmsd);
          if (rmsd < minRMS) {
            minRMS = rmsd;
            r = rFactor;
            g = gFactor;
            b = bFactor;
          }
        }
      }
    }

    return new double[] {
        r, g, b};
  }
}
