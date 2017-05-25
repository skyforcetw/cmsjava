package shu.cms.measure.calibrate;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 產生校正儀器用的色塊資料
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class SampleMaker {
  public SampleMaker() {
  }

  public static String getStandardTrainingSample(int[] values) {
    StringBuilder buf = new StringBuilder();
    int count = 1;
    int length = values.length;
    for (int x = 0; x < length; x++) {
      for (int y = 0; y < length; y++) {
        for (int z = 0; z < length; z++) {
          buf.append("A" + count++ +'\t');
          buf.append(values[x] + "\t" + values[y] + "\t" + values[z]);
          buf.append('\n');
        }
      }
    }

    buf.append("A" + count++ +"\t63 63 63\n");
    buf.append("A" + count++ +"\t191 191 191\n");

    return buf.toString();

  }

  /**
   * 參考自阿吉學長的訓練色塊
   * @return String
   */
  public static String getStandardTrainingSample() {
    int[] values = new int[] {
        0, 127, 255};
    return getStandardTrainingSample(values);
  }

  /**
   * 分的更細的訓練色塊
   * @return String
   */
  public static String getAdvancedTrainingSample() {
    int[] values = new int[] {
        0, 64, 127, 191, 255};
    return getStandardTrainingSample(values);
  }

  protected static String getTestSample(int count) {
    StringBuilder buf = new StringBuilder();
    int index = 1;
    for (int x = 0; x < count; x++) {
      long r = Math.round(Math.random() * 255);
      long g = Math.round(Math.random() * 255);
      long b = Math.round(Math.random() * 255);
      buf.append("A" + index++ +"\t" + r + " " + g + " " + b + "\n");
    }

    return buf.toString();
  }

  /**
   * 參考自阿吉學長的測試色塊
   * @return String
   */
  public static String getStandardTestSample() {
    return getTestSample(20);
  }

  public static void main(String[] args) {
    System.out.println("StandardTrainingSample");
    System.out.println(SampleMaker.getStandardTrainingSample());
    System.out.println("AdvancedTrainingSample");
    System.out.println(SampleMaker.getAdvancedTrainingSample());
    System.out.println("TestSample");
    System.out.println(SampleMaker.getTestSample(40));
  }
}
