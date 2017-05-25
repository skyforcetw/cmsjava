package auo.applet.eeprom;

import java.io.*;
import java.util.*;

import jxl.*;
import jxl.read.biff.*;
import shu.io.files.ExcelFile;

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
 *
 * 指定左上和右下, 撈出所有欄位.
 * 不管有沒有merge, 先以所在的欄位換算的位址為主
 * 去對應merge cells, 若有對應到, 根據merge換算實際所佔位置長度
 */
public class EEPROMAnalyzer {
  public final static int[] findTopLeft(ExcelFile xls, String settingMapName) {
    xls.selectSheet(settingMapName);
    Cell decCell = xls.findCell("dec");
    String seven = xls.getCellAsString(decCell.getColumn() + 1, decCell.getRow());
    String zero = xls.getCellAsString(decCell.getColumn(), decCell.getRow() + 1);
    if (seven.equals("7") && zero.equals("0")) {
      return new int[] {
          decCell.getColumn() + 1, decCell.getRow() + 1};
    }
    else {
      return null;
    }
  }

  private int[] topLeft, bottomRight;
  private ExcelFile xls;
  private int totalSize;
  private final static int ByteLength = 8;
  public EEPROMAnalyzer(String filename, String settingMapName,
                        String topLeft,
                        String bottomRight) throws
      Exception {
    this.topLeft = getIndex(topLeft);
    this.bottomRight = getIndex(bottomRight);

    xls = new ExcelFile(filename);
    xls.selectSheet(settingMapName);
    scanMergeCells();
  }

  public void writeToTextFile(String filename) throws IOException {
    scan();
    processCrossByte();
    write(filename);
  }

  private void write(String filename) throws IOException {
    Collections.sort(registerList);
    PrintStream print = new PrintStream(filename);
    for (TemporaryRegister reg : registerList) {
      print.println(reg.register);
    }
  }

  private boolean[] mergedArray;
  private Map<Integer, Range> mergedMap = new HashMap<Integer, Range> ();

  private boolean isInSettingMap(Cell cell) {
    int row = cell.getRow();
    int col = cell.getColumn();
    return row >= topLeft[1] && row <= bottomRight[1] && col >= topLeft[0] &&
        col <= bottomRight[0];
  }

  private void scanMergeCells() {
    totalSize = (bottomRight[1] - topLeft[1] + 1) * ByteLength;
    mergedArray = new boolean[totalSize];

    /*
         range是一個範圍, 不見得每個register都會落在bound
     */
    Range[] ranges = xls.getMergedCells();
    for (Range range : ranges) {
      Cell topLeft = range.getTopLeft();
      if (isInSettingMap(topLeft)) {
        int bitCount = getBitCount(topLeft);
        mergedArray[bitCount] = true;
        mergedMap.put(bitCount, range);
      }
    }
  }

  private boolean isInRange(Cell cell, Range range) {
    boolean inY = cell.getRow() >= range.getTopLeft().getRow() &&
        cell.getRow() <= range.getBottomRight().getRow();
    boolean inX = cell.getColumn() >= range.getTopLeft().getColumn() &&
        cell.getColumn() <= range.getBottomRight().getColumn();
    return inY && inX;
  }

  private final int getBitCount(Cell cell) {
    int y = cell.getRow() - topLeft[1];
    int x = getBitInByte(cell);
    return x + y * ByteLength;
  }

  private final int getBitInByte(Cell cell) {
    return 7 - (cell.getColumn() - topLeft[0]);
  }

  private Range cellInRange;
  private boolean hasMerge(Cell cell) {
    int bitCount = getBitCount(cell);
    int distanceToLeft = getBitInByte(cell);
    boolean merge = false;
    cellInRange = null;

    for (int x = bitCount - distanceToLeft; x <= bitCount; x++) {
      if (true == mergedArray[x]) {
        Range range = mergedMap.get(x);
        boolean inRange = isInRange(cell, range);
        merge = merge || inRange;
        if (inRange) {
          cellInRange = range;
        }
      }
    }
    return merge;
  }

  private final static boolean isAllSpace(String string) {
    boolean allSpace = true;
    for (int x = 0; x < string.length(); x++) {
      allSpace = allSpace && (string.charAt(x) == ' ');
    }
    return allSpace;
  }

  private final static String getRegisterNameOnly(String contents) {
    int startIndex = contents.indexOf("[");
    return contents.substring(0, startIndex);
  }

  private boolean hasStartAndEndBit(String contents) {
    int startIndex = contents.indexOf("[");
    return -1 != startIndex;
  }

  private static int[] getStartAndEndBit(String contents) {
    int startIndex = contents.indexOf("[");
    if ( -1 == startIndex) {
      return null;
    }
    int endIndex = contents.indexOf("]");
    String bitzone = contents.substring(startIndex + 1, endIndex);
    StringTokenizer token = new StringTokenizer(bitzone, ":");
    int end = Integer.parseInt(token.nextToken());
    int start = Integer.parseInt(token.nextToken());
    return new int[] {
        start, end};
  }

  private Map<String,
              List<TemporaryRegister>> crossByteMap = new HashMap<String,
      List<TemporaryRegister>> ();
  private List<TemporaryRegister> registerList = new LinkedList<
      TemporaryRegister> ();

  private static class RegisterEndComparator
      implements Comparator {
    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      TemporaryRegister r1 = (TemporaryRegister) o1;
      TemporaryRegister r2 = (TemporaryRegister) o2;
      int[] r1se = getStartAndEndBit(r1.contents);
      int[] r2se = getStartAndEndBit(r2.contents);
      return r2se[1] - r1se[1];
    }

    public boolean equals(Object obj) {
      return false;
    }

  }

  private void processCrossByte() {
    RegisterEndComparator comparator = new RegisterEndComparator();
    for (List<TemporaryRegister> list : crossByteMap.values()) {
      TemporaryRegister reg0 = list.get(0);
      String regname = getRegisterNameOnly(reg0.contents);

      if (1 == list.size()) {
        //雖然有註明start and end, 但是沒有跨byte, 不用特別處理

        int[] startAndEnd = getStartAndEndBit(reg0.contents);

        int bit = getBitInByte(reg0.cellInRange.getBottomRight());
        int length = startAndEnd[1] - startAndEnd[0] + 1;
        String result = String.format("%s,%d,%d,%d", regname,
                                      reg0.bitCount / 8,
                                      bit, length);
        TemporaryRegister newreg = new TemporaryRegister(reg0.bitCount,
            result);
        registerList.add(newreg);

      }
      else {
        //end少的在前面
        Collections.sort(list, comparator);
        StringBuilder buf = new StringBuilder();
        buf.append(regname);
        for (TemporaryRegister reg : list) {
          int bit = getBitInByte(reg.cellInRange.getBottomRight());
          int[] startAndEnd = getStartAndEndBit(reg.contents);
          int length = startAndEnd[1] - startAndEnd[0] + 1;
          String temp = String.format(",%d,%d,%d", reg.bitCount / 8, bit,
                                      length);
          buf.append(temp);
        }
        TemporaryRegister newreg = new TemporaryRegister(reg0.bitCount,
            buf.toString());
        registerList.add(newreg);

      }
    }
  }

  private static class TemporaryRegister
      implements Comparable {
    /**
     * Compares this object with the specified object for order.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *   is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
      TemporaryRegister oo = (TemporaryRegister) o;
      return this.bitCount - oo.bitCount;
    }

    public int bitCount;
    public String register;
    public Range cellInRange;
    public String contents;
    public TemporaryRegister(int bitCount, String contents, Range cellInRange) {
      this.bitCount = bitCount;
      this.contents = contents;
      this.cellInRange = cellInRange;
    }

    public TemporaryRegister(int bitCount, String register) {
      this.bitCount = bitCount;
      this.register = register;

    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return "#" + bitCount + " " +
          ( (register != null) ? register : contents);
    }
  }

  private void scan() {

    for (int y = topLeft[1]; y <= bottomRight[1]; y++) {
      Cell[] cells = xls.getRow(y);
      for (int x = topLeft[0]; x <= bottomRight[0]; x++) {
        Cell cell = cells[x];
        String contents = cell.getContents();

        if (contents.length() != 0 && !contents.equals("-") &&
            !isAllSpace(contents)) {
          contents = contents.replaceAll("\n", "");

          if (true == hasMerge(cell)) {
            int bitCount = getBitCount(cellInRange.getBottomRight());
            //可能是8bit, 甚至是跨byte
            if (hasStartAndEndBit(contents)) {
              //有標註bit範圍, 是跨byte的
              String registerName = getRegisterNameOnly(contents);
              List<TemporaryRegister>
                  registerList = crossByteMap.get(registerName);
              if (null == registerList) {
                registerList = new ArrayList<TemporaryRegister> ();
              }

              TemporaryRegister register = new TemporaryRegister(bitCount,
                  contents, cellInRange);
              registerList.add(register);
              crossByteMap.put(registerName, registerList);
            }
            else {
              //1byte以內, 此處計算的length即為正確值
              int length = cellInRange.getBottomRight().getColumn() -
                  cellInRange.getTopLeft().getColumn() + 1;
              int bit = getBitInByte(cellInRange.getBottomRight());
              String result = String.format("%s,%d,%d,%d", contents,
                                            bitCount / 8, bit, length);
              TemporaryRegister reg = new TemporaryRegister(bitCount, result);
              registerList.add(reg);
            }
          }
          else {
            //一定是1bit
            int bitCount = getBitCount(cell);
            int bit = getBitInByte(cell);
            String result = String.format("%s,%d,%d,%d", contents,
                                          bitCount / 8, bit, 1);
            TemporaryRegister reg = new TemporaryRegister(bitCount, result);
            registerList.add(reg);
          }
        }
      }
    }

  }

  private final static int[] getIndex(String xlsCellPos) {
    int x = CellReferenceHelper.getColumn(xlsCellPos);
    int y = CellReferenceHelper.getRow(xlsCellPos);
//    int numberIndex = getFirstNumber(xlsCellPos);
//    String letter = xlsCellPos.substring(0, numberIndex);
//    int x = (numberIndex == 1) ? (letter.charAt(0) - 'A') :
//        (letter.charAt(0) - 'A' + 1) * 26 + (letter.charAt(1) - 'A');
//    int y = Integer.parseInt(xlsCellPos.substring(numberIndex,
//                                                  xlsCellPos.length())) - 1;
    return new int[] {
        x, y};
  }

  /**
   *
   * @param str String
   * @return int
   * @deprecated
   */
  private final static int getFirstNumber(String str) {
    for (int x = 0; x < str.length(); x++) {
      char c = str.charAt(x);
      if (Character.isDigit(c)) {
        return x;
      }
    }
    return -1;
  }

  public static void main(String[] args) throws Exception {
    System.out.println("DIM_LUT_0".hashCode());
    System.out.println("DIM_LUT_11".hashCode());

//    EEPROMAnalyzer analyzer = new EEPROMAnalyzer("AUO12307_EEPROM_Setting_15.6_HSD_1920X1080_checksum04503E_20111102_Color_OK.xls",
//                                                 "Setting Map", "E17", "L3184");
//    analyzer.writeToTextFile("12307.txt");
    EEPROMAnalyzer analyzer = new EEPROMAnalyzer(
        "AUO11301_EEPROM_Setting_V1.0.xls",
        "Setting", "Y193", "AF960");
    int[] topLeft = analyzer.findTopLeft(analyzer.xls, "Setting");

//    System.out.println(Arrays.toString(analyzer.topLeft));
//    System.out.println(Arrays.toString(analyzer.bottomRight));
  }

  public static void main2(String[] args) {
    System.out.println(Arrays.toString(getIndex("AA15")));
    try {
      ExcelFile xls = new ExcelFile("AUO12307_EEPROM_Setting_15.6_HSD_1920X1080_checksum04503E_20111102_Color_OK.xls");
      xls.selectSheet("Setting Map");
      int rows = xls.getRows();
      int finalRow = -1;
      for (int y = rows - 1; y >= 0; y--) {
        String s = xls.getCellAsString(2, y);
        if (s.length() != 0 && !s.equals("0")) {
          finalRow = y;
          break;
        }
      }
      System.out.println(finalRow);
      Range[] merges = xls.getMergedCells();
      for (Range r : merges) {
        System.out.println(r);

      }

      for (int y = 16; y <= finalRow; y++) {
        Cell[] cells = xls.getRow(y);
        for (int x = 4; x <= 11; x++) {
          Cell c = cells[x];
          System.out.println(c.getCellFeatures());
//          System.out.println(c.getCellFormat());
          jxl.format.CellFormat fmt = c.getCellFormat();
          System.out.println(fmt.getAlignment());
          System.out.println(fmt.getFormat());
          System.out.println(fmt.getIndentation());

          System.out.println(c.getContents());
          System.out.println("////");
        }

        return;
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
  }
}
