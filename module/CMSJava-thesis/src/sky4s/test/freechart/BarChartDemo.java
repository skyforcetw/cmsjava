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
 * \u8BE5\u7C7B�Τ_�t�ܳ�\u7B80\u5355���W\u72B6\u56FE�ͦ�
 * @author Winter Lau
 */
public class BarChartDemo {
  public static void main(String[] args) throws Exception {
    CategoryDataset dataset = getDataSet2();
    JFreeChart chart = ChartFactory.createBarChart3D(
        "���G\u4EA7�q\u56FE", // \u56FE��\u6807\u9898
        "���G", // ��\u5F55\u8F74��\u663E��\u6807\u7B7E
        "\u4EA7�q", // \u6570��\u8F74��\u663E��\u6807\u7B7E
        dataset, // \u6570�u��
        PlotOrientation.VERTICAL, // \u56FE���V�G�����B����
        true, // �O�_\u663E��\u56FE��(\u5BF9�_\u7B80\u5355���W\u72B6\u56FE��\u987B�Ofalse)
        false, // �O�_�ͦ��u��
        false // �O�_�ͦ�URL\u94FE��
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
   * \u83B7���@\u4E2A�t�ܥΪ�\u7B80\u5355\u6570�u��\u5BF9�H
   * @return
   */
  private static CategoryDataset getDataSet() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    dataset.addValue(100, null, "�m�G");
    dataset.addValue(200, null, "���l");
    dataset.addValue(300, null, "����");
    dataset.addValue(400, null, "����");
    dataset.addValue(500, null, "��K");
    return dataset;
  }

  /**
   * \u83B7���@\u4E2A�t�ܥΪ�\u7EC4�X\u6570�u��\u5BF9�H
   * @return
   */
  private static CategoryDataset getDataSet2() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    dataset.addValue(100, "�_��", "�m�G");
//    dataset.addValue(100, "�W��", "�m�G");
//    dataset.addValue(100, "\u5E7F�{", "�m�G");
//    dataset.addValue(200, "�_��", "���l");
//    dataset.addValue(200, "�W��", "���l");
//    dataset.addValue(200, "\u5E7F�{", "���l");
//    dataset.addValue(300, "�_��", "����");
//    dataset.addValue(300, "�W��", "����");
//    dataset.addValue(300, "\u5E7F�{", "����");
//    dataset.addValue(400, "�_��", "����");
//    dataset.addValue(400, "�W��", "����");
//    dataset.addValue(400, "\u5E7F�{", "����");
//    dataset.addValue(500, "�_��", "��K");
//    dataset.addValue(500, "�W��", "��K");
//    dataset.addValue(500, "\u5E7F�{", "��K");
    return dataset;
  }

  private static void getDataSet3(DefaultCategoryDataset dataset) {
    dataset.addValue(100, "�W��", "�m�G");
    dataset.addValue(100, "\u5E7F�{", "�m�G");
    dataset.addValue(200, "�_��", "���l");
    dataset.addValue(200, "�W��", "���l");
    dataset.addValue(200, "\u5E7F�{", "���l");
    dataset.addValue(300, "�_��", "����");
    dataset.addValue(300, "�W��", "����");
    dataset.addValue(300, "\u5E7F�{", "����");
    dataset.addValue(400, "�_��", "����");
    dataset.addValue(400, "�W��", "����");
    dataset.addValue(400, "\u5E7F�{", "����");
    dataset.addValue(500, "�_��", "��K");
    dataset.addValue(500, "�W��", "��K");
    dataset.addValue(500, "\u5E7F�{", "��K");

  }
}
