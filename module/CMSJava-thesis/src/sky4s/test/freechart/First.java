package sky4s.test.freechart;

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
import org.jfree.chart.*;
import org.jfree.data.general.*;

/**
 * A simple introduction to using JFreeChart. This demo is described in the
 * JFreeChart Developer Guide.
 */
public class First {
  /**
   * The starting point for the demo.
   *
   * @param args ignored.
   */
  public static void main(String[] args) {
// create a dataset...
    DefaultPieDataset data = new DefaultPieDataset();
    data.setValue("Category 1", 43.2);
    data.setValue("Category 2", 27.9);
    data.setValue("Category 3", 79.5);
// create a chart...
    JFreeChart chart = ChartFactory.createPieChart(
        "Sample Pie Chart",
        data,
        true, // legend?
        true, // tooltips?
        false // URLs?
        );
// create and display a frame...

    ChartFrame frame = new ChartFrame("First", chart);
    frame.pack();
    frame.setVisible(true);
  }
}
