package shu.cms.lcd.material;

import shu.cms.lcd.*;

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
 * @deprecated
 */
public class LCDTargetMaterial {
  public final static LCDTarget getLCDTarget(String device, String dirtag,
                                             String filetag,
                                             LCDTargetBase.Number number) {
    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
    LCDTarget.Source source = LCDTarget.Source.CA210;

    LCDTarget lcdTarget = LCDTarget.Instance.get(device, source,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native, number,
                                                 fileType, dirtag, filetag);
    return lcdTarget;
  }

  public final static LCDTarget getCPT320WF01SC_0227() {
    return getLCDTarget("cpt_320WF01SC", "0227", "",
                        LCDTargetBase.Number.Complex1021_4096_4108);
  }

  public final static LCDTarget getCPT320WF01SC_0520() {
    return getLCDTarget("cpt_320WF01SC", "0520", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WF01SC_0805() {
    return getLCDTarget("cpt_320WF01SC", "0805", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WF01SC_0825() {
    return getLCDTarget("cpt_320WF01SC", "0825", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WF01SC_0904() {
    return getLCDTarget("cpt_320WF01SC", "0904", "",
                        LCDTargetBase.Number.Complex1021_4096_769);
  }

  public final static LCDTarget getCPT320WF01C_1008() {
    return getLCDTarget("cpt_320WF01C", "091008", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WA01C_0717() {
    return getLCDTarget("cpt_320WA01C", "090717", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WA01C_0731() {
    return getLCDTarget("cpt_320WA01C", "090731", "",
                        LCDTargetBase.Number.Complex1021_4096_769);
  }

  public final static LCDTarget getCPT320WA01C_0716() {
    return getLCDTarget("cpt_320WA01C", "090716", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT320WA01C_0703() {
    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
    LCDTarget.Source source = LCDTarget.Source.CA210;

    LCDTarget lcdTarget1790 = LCDTarget.Instance.get("cpt_320WA01C", source,
        LCDTarget.Room.Light, LCDTarget.TargetIlluminant.Native,
        LCDTargetBase.Number.Complex1021_769,
        fileType, "090703", "");
    return lcdTarget1790;
  }

  public final static LCDTarget getCPT370WF02() {
    return getLCDTarget("cpt_370WF02", "0119", "",
                        LCDTargetBase.Number.Complex1021_4096_4108);
  }

  public final static LCDTarget getCPT370WF02C() {
    return getLCDTarget("cpt_370WF02C", "091007", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT170EA() {
    return getLCDTarget("cpt_170EA", "080611", "",
                        LCDTargetBase.Number.Complex1021_4096_4333);
  }

  public final static LCDTarget getCMO315inch() {
    return getLCDTarget("cmo_31.5inch", "091012", "",
                        LCDTargetBase.Number.Complex1021_769);
  }

  public final static LCDTarget getCPT17inchNo3() {
    return getLCDTarget("cpt_17inch No.3", "080506", "",
                        LCDTargetBase.Number.Complex1021_4096_729);
  }

  public final static LCDTarget getCPT32inchNo2() {
    return getLCDTarget("cpt_32inch No.2", "080616", "",
                        LCDTargetBase.Number.Complex1021_4096_4333);
  }

  public final static LCDTarget getAUO_B156HW01() {
    return getLCDTarget("auo_B156HW01", "091116", "",
                        LCDTargetBase.Number.Complex1021_769);
  }
}
