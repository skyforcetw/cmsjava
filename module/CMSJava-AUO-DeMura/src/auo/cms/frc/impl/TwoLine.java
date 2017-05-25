package auo.cms.frc.impl;

import java.io.Serializable;

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
public class TwoLine implements Serializable {
    public TwoLine(byte[][] position, int index) {
        this.position = position;
        this.index = index;
    }

    public byte[][] position;
    public int index;
    public int[] nonOverlapIndex;
}
