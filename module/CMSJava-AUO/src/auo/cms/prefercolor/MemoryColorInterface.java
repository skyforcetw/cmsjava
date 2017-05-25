package auo.cms.prefercolor;

import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public interface MemoryColorInterface {
  public CIEXYZ getReferenceWhiteXYZ();

  public CIELab getSkin();

  public CIELab getSky();

  public CIELab getGrass();

  public CIELab getFoliage();

  public CIELab getOrange();

  public CIELab getBanana();

}
