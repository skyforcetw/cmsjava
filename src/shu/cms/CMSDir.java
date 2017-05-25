package shu.cms;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 紀錄cms的目錄常數
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class CMSDir {
  public interface Measure {
    String Dir = "Measurement Files";
    String Monitor = Dir + "/Monitor";
    String Camera = Dir + "/Camera";
    String Calibration = Dir + "/Calibration";
  }

  public interface Reference {
    String Dir = "Reference Files";
    String Monitor = Dir + "/Monitor";
    String Camera = Dir + "/Camera";
    String Spectra = Dir + "/Spectra Database";
    String GBD = Dir + "/GamutBoundaryDescriptor";
    String Munsell = Dir + "/Munsell";
  }

  public static void main(String[] args) {
    System.out.println(CMSDir.Measure.Monitor);
  }
}
