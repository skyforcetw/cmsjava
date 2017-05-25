package shu.io.files;

/**
 * <p>Title: Colour Management System - static</p>
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
public interface FileExtractIF {
  public double getCell(int x, int y);

  public String getCellAsString(int x, int y);

  public int getRows();
}
