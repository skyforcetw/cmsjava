package auo.mura.interp;

import auo.math.lut.CubeTable;
import shu.cms.colorspace.depend.RGBBase;

/**
 * <p>Title: Colour Management System</p>
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
public interface Interpolation3DIF {

//  CubeTable.KeyValue[] getKeyValueInterpolateCell(short[] key);

//    short[] interpolateValue(short[] key, CubeTable.KeyValue[] cell);

    short[] interpolateValue(short[] key, CubeTable.KeyValue[] cell, RGBBase.Channel ch);
}
