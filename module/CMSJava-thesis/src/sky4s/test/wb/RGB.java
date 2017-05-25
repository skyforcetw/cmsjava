package sky4s.test.wb;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class RGB {
  public static final RGB PURE_WHITE = new RGB(new int[] {255, 255, 255});

  private int[] rgb = new int[3];

  public RGB() {
  }

  public RGB(int r, int g, int b) {
    setRGB(new int[] {r, g, b});
  }

  public RGB(int[] rgb) {
    setRGB(rgb);
  }

  public void setRGB(int[] rgb) {
    this.rgb[0] = rgb[0];
    this.rgb[1] = rgb[1];
    this.rgb[2] = rgb[2];
  }

  public int[] getRGB() {
    return rgb;
  }

  public String toString() {
    return rgb[0] + "," + rgb[1] + "," + rgb[2];
  }

}
