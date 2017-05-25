package shu.cms.util;

import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 計算導表與相機之間距離的工具.
 * 但是不太準確,不建議使用.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class HeightCalculator {
  public static final double CHART_ANGLE = 45;
  public HeightCalculator(Chart chart,
                          double flatTopHeight,
                          double viewAngleOfLens,
                          double fitPercent
      ) {
    double halfOfChartHeight = calChartHeight(chart.h) / 2;
    double chartDiagonalLength = calDiagonalLength(chart.w, chart.h);
    double directDistance2Chart = calDirectDistanceOfCamera2Chart(
        viewAngleOfLens, fitPercent / 100, chartDiagonalLength);

    distanceOfCamera2ChartBottom = directDistance2Chart * cos(CHART_ANGLE) -
        halfOfChartHeight / tan(CHART_ANGLE);
//    System.out.println(directDistance2Chart);
    cameraHeight = flatTopHeight + halfOfChartHeight +
        directDistance2Chart * sin(CHART_ANGLE);
    focusDistance = directDistance2Chart;
  }

  public static void main(String[] args) {
    HeightCalculator cal = new HeightCalculator(new Chart(50, 50), 100, 90, 100);
    System.out.println(cal.getDistanceOfCamera2ChartBottom());
    System.out.println(cal.getCameraHeight());
  }

  protected static double sin(double degrees) {
    return Math.sin(Math.toRadians(degrees));
  }

  protected static double cos(double degrees) {
    return Math.cos(Math.toRadians(degrees));
  }

  protected static double tan(double degrees) {
    return Math.tan(Math.toRadians(degrees));
  }

  protected double cameraHeight;
  protected double distanceOfCamera2ChartBottom;
  protected double focusDistance;

  public static class Chart {
    public Chart(double w, double h) {
      this.w = w;
      this.h = h;
    }

    double w, h;
  }

  protected static double calDiagonalLength(double w, double h) {
    return Math.sqrt(Maths.sqr(w) + Maths.sqr(h));
  }

  protected static double calChartHeight(double h) {
    return h / cos(CHART_ANGLE);
  }

  protected static double calHorizontalDistanceOfCamera2ChartCenter(
      double horizontalDistanceOfCamera2ChartBottom,
      double chartHeight) {
    double height = calChartHeight(chartHeight);
    return horizontalDistanceOfCamera2ChartBottom +
        height / tan(CHART_ANGLE);
  }

  protected static double calDirectDistanceOfCamera2Chart(
      double viewAngleOfLens,
      double fitRate,
      double chartDiagonalLength) {
    double diagonalLength = chartDiagonalLength / fitRate;
    return diagonalLength / 2 / tan(viewAngleOfLens / 2);
  }

  public double getCameraHeight() {
    return cameraHeight;
  }

  public double getDistanceOfCamera2ChartBottom() {
    return distanceOfCamera2ChartBottom;
  }

  public double getFocusDistance() {
    return focusDistance;
  }

}
