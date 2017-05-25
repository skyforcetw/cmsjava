package shu.image;

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
public interface ImageInterface {

  public int getHeight();

  public int getWidth();

  public double[] getPixel(int x, int y, double dArray[]);

  public void setPixel(int x, int y, double dArray[]);

  public int[] getPixel(int x, int y, int iArray[]);

  public void setPixel(int x, int y, int iArray[]);
}
