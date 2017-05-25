package shu.cms.colorformat.cxf.attr;

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
public class Colorimetric
    extends ConditionsAttributes {
  public final static String ILLUMINATION_D50 = "D50";
  public final static String OBSERVER_TWODEGREE = "TwoDegree";

  public String illumination;
  public String observer;

  public Colorimetric() {
  }

}
