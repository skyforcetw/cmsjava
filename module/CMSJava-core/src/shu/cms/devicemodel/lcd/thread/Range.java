package shu.cms.devicemodel.lcd.thread;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來表示係數範圍
 *
 * 用來配合迭代求解時,指定細述範圍的類別.
 * 每次迭代完後,配合Cooperation的介面,可以計算出下一次的迭代範圍.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Range {
  public double start;
  public double end;
  public double step;

  public Range() {

  }

  public Range(double start, double end, double stepRate) {
    this.start = start;
    this.end = end;
    this.step = determineStep(start, end, stepRate);
  }

  public String toString() {
    return "start[" + start + "] end[" + end + "] step[" + step + "]";
  }

  public static Range determineRange(double original, Range old,
                                     ThreadCalculator.Cooperation c) {
    return determineRange(original, old, c.getStepRate(), c.getRangeRate());
  }

  public static Range determineRange(double original, Range old,
                                     double stepRate, double rangeRate) {
    if (original == old.start || original == old.end) {
      //維持不變
      return old;
    }

    Range newRange = new Range();
    newRange.start = determineStart(original, old.step, rangeRate);
//    newRange.start = newRange.start > old.start ? newRange.start : old.start;

    newRange.end = determineEnd(original, old.step, rangeRate);
//    newRange.end = newRange.end < old.end ? newRange.end : old.end;

    newRange.step = determineStep(newRange.start, newRange.end, stepRate);

    return newRange;
  }

  /**
   *
   * @param start double
   * @param end double
   * @param c Cooperation
   * @return double
   * @deprecated
   */
  public static double determineStep(double start, double end,
                                     ThreadCalculator.Cooperation c) {
    return determineStep(start, end, c.getStepRate());
  }

  public static double determineStep(double start, double end, double stepRate) {
    double step = (end - start) * stepRate;
    step = (step == 0) ? Double.MAX_VALUE - 1 : step;
    return step;
  }

  /**
   *
   * @param stepArray double[]
   * @param index int
   * @return double
   * @deprecated
   */
  public static double determineStep(double[] stepArray, int index) {
    return stepArray[index];
  }

  /**
   *
   * @param original double
   * @param previousStep double
   * @param c Cooperation
   * @return double
   * @deprecated
   */
  public static double determineStart(double original, double previousStep,
                                      ThreadCalculator.Cooperation c) {
    return determineStart(original, previousStep, c.getRangeRate());
  }

  public static double determineStart(double original, double previousStep,
                                      double rangeRate
      ) {
    return original - previousStep * rangeRate;
  }

  /**
   *
   * @param original double
   * @param previousStep double
   * @param c Cooperation
   * @return double
   * @deprecated
   */
  public static double determineEnd(double original, double previousStep,
                                    ThreadCalculator.Cooperation c) {
    return determineEnd(original, previousStep, c.getRangeRate());
  }

  public static double determineEnd(double original, double previousStep,
                                    double rangeRate
      ) {
    return original + previousStep * rangeRate;
  }

}
