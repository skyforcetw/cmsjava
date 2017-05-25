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
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

/**
 * \u8BE5\u7C7B用于演示最\u7B80\u5355的柱\u72B6\u56FE生成
 * @author Winter Lau
 */
public class BarChartDemo {
  public static void main(String[] args) throws Exception {
    CategoryDataset dataset = getDataSet2();
    JFreeChart chart = ChartFactory.createBarChart3D(
        "水果\u4EA7量\u56FE", // \u56FE表\u6807\u9898
        "水果", // 目\u5F55\u8F74的\u663E示\u6807\u7B7E
        "\u4EA7量", // \u6570值\u8F74的\u663E示\u6807\u7B7E
        dataset, // \u6570据集
        PlotOrientation.VERTICAL, // \u56FE表方向：水平、垂直
        true, // 是否\u663E示\u56FE例(\u5BF9于\u7B80\u5355的柱\u72B6\u56FE必\u987B是false)
        false, // 是否生成工具
        false // 是否生成URL\u94FE接
        );

    ChartFrame frame = new ChartFrame("First", chart);
    frame.pack();
    frame.setVisible(true);

    Thread.sleep(2000);

    getDataSet3( (DefaultCategoryDataset) dataset);

//    FileOutputStream fos_jpg = null;
//    try {
//      fos_jpg = new FileOutputStream("D:\\fruit.jpg");
//      ChartUtilities.writeChartAsJPEG(fos_jpg, 1, chart, 400, 300, null);
//    }
//    finally {
//      try {
//        fos_jpg.close();
//      }
//      catch (Exception e) {}
//    }
  }

  /**
   * \u83B7取一\u4E2A演示用的\u7B80\u5355\u6570据集\u5BF9象
   * @return
   */
  private static CategoryDataset getDataSet() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    dataset.addValue(100, null, "苹果");
    dataset.addValue(200, null, "梨子");
    dataset.addValue(300, null, "葡萄");
    dataset.addValue(400, null, "香蕉");
    dataset.addValue(500, null, "荔枝");
    return dataset;
  }

  /**
   * \u83B7取一\u4E2A演示用的\u7EC4合\u6570据集\u5BF9象
   * @return
   */
  private static CategoryDataset getDataSet2() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    dataset.addValue(100, "北京", "苹果");
//    dataset.addValue(100, "上海", "苹果");
//    dataset.addValue(100, "\u5E7F州", "苹果");
//    dataset.addValue(200, "北京", "梨子");
//    dataset.addValue(200, "上海", "梨子");
//    dataset.addValue(200, "\u5E7F州", "梨子");
//    dataset.addValue(300, "北京", "葡萄");
//    dataset.addValue(300, "上海", "葡萄");
//    dataset.addValue(300, "\u5E7F州", "葡萄");
//    dataset.addValue(400, "北京", "香蕉");
//    dataset.addValue(400, "上海", "香蕉");
//    dataset.addValue(400, "\u5E7F州", "香蕉");
//    dataset.addValue(500, "北京", "荔枝");
//    dataset.addValue(500, "上海", "荔枝");
//    dataset.addValue(500, "\u5E7F州", "荔枝");
    return dataset;
  }

  private static void getDataSet3(DefaultCategoryDataset dataset) {
    dataset.addValue(100, "上海", "苹果");
    dataset.addValue(100, "\u5E7F州", "苹果");
    dataset.addValue(200, "北京", "梨子");
    dataset.addValue(200, "上海", "梨子");
    dataset.addValue(200, "\u5E7F州", "梨子");
    dataset.addValue(300, "北京", "葡萄");
    dataset.addValue(300, "上海", "葡萄");
    dataset.addValue(300, "\u5E7F州", "葡萄");
    dataset.addValue(400, "北京", "香蕉");
    dataset.addValue(400, "上海", "香蕉");
    dataset.addValue(400, "\u5E7F州", "香蕉");
    dataset.addValue(500, "北京", "荔枝");
    dataset.addValue(500, "上海", "荔枝");
    dataset.addValue(500, "\u5E7F州", "荔枝");

  }
}
