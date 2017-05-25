package shu.cms.measure.cp;

import java.util.*;

import shu.cms.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MeasureResult {
  public List<Patch> result;
  public int practicalMeasureCount;
  MeasureResult(List<Patch> result, int practicalMeasureCount) {
    this.result = result;
    this.practicalMeasureCount = practicalMeasureCount;
  }
}
