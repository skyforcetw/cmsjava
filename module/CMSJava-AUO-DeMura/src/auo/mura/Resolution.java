package auo.mura;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public enum Resolution {
    FHD(1920, 1080), WQHD(2560, 1440), _4K2K(3840, 2160), _5120(5120, 2160);

    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width;
    public int height;
}
