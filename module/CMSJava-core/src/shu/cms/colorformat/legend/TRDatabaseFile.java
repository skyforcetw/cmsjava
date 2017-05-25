package shu.cms.colorformat.legend;

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
public class TRDatabaseFile
    extends AsciiFile {

  public TRDatabaseFile() {

  }

  public int waveLengthStart;
  public int waveLengthEnd;
  public int waveLengthInterval;
  public String filename;

  public double[][] data;
}
