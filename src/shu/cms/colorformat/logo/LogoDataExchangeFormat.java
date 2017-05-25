package shu.cms.colorformat.logo;

import java.io.*;
import java.util.*;

import shu.cms.colorformat.ascii.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LogoDataExchangeFormat {

  public static enum DataType {
    Real, Integer, String, NonQuotedString
  }

  public String getFilename() {
    return asciiFileFormat.getFilename();
  }

  protected ASCIIFileFormat asciiFileFormat;

  public LogoDataExchangeFormat(ASCIIFileFormat asciiFileFormat) {
    this.asciiFileFormat = asciiFileFormat;
    parse();
  }

  public LogoDataExchangeFormat() {
    header = new Header();
    dataBlock = new DataBlock();
  }

  public LogoDataExchangeFormat(String filename) throws IOException {
    this(new ASCIIFileFormatParser(filename).parse());
  }

  public LogoDataExchangeFormat(Reader reader) throws IOException {
    this(new ASCIIFileFormatParser(reader).parse());
  }

  private Header header;
  private DataBlock dataBlock;

  protected void parse() {
    header = headerBlockParse();
    dataBlock = dataBlockParse(header);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder(header.toString());
    builder.append(dataBlock.toString());
    return builder.toString();
  }

  public int getHeaderValueAsInteger(String key) {
    return stringToInteger(header.getValue(key));
  }

  public String getHeaderValueAsString(String key) {
    return header.getValue(key);
  }

  protected static int stringToInteger(String value) {
    if (value != null) {
      int result = Integer.parseInt(value);
      return result;
    }
    else {
      return -1;
    }
  }

  protected Header headerBlockParse() {
    //==========================================================================
    // normal parse
    //==========================================================================
    int beginDataLine = asciiFileFormat.getLine(Reserved.
                                                BEGIN_DATA).lineNumber;
    Header header = new Header();
    boolean dataFormat = false;
    for (int x = 0; x < beginDataLine; x++) {
      ASCIIFileFormat.LineObject lo = asciiFileFormat.getLine(x);
      String key = lo.firstField;
      String value = lo.line.substring(lo.firstField.length()).trim();

      if (key.equals(Reserved.END_DATA_FORMAT)) {
        dataFormat = false;
        continue;
      }
      else if (dataFormat) {
        header._dataFormat = lo;
      }
      else if (key.equals(Reserved.KEYWORD)) {
        header.keywordList.add(value);
      }
      else if (key.equals(Reserved.BEGIN_DATA_FORMAT)) {
        dataFormat = true;
        continue;
      }
      else if (key.equals(Reserved.BEGIN_DATA) || key.equals("#")) {
        continue;
      }
      else {
        header.map.put(key, value);
      }
    }
    //==========================================================================
    return header;
  }

  public static class Header {
    public Header() {
      map = new LinkedHashMap<String, String> ();
      keywordList = new ArrayList<String> ();
    }

    protected Map<String, String> map;
    public String getValue(String key) {
      return map.get(key);
    }

    public void putKeyAndValue(String key, String value) {
      map.put(key, value);
    }

    protected ArrayList<String> keywordList;
    protected ASCIIFileFormat.LineObject _dataFormat;

    public ArrayList<String> getKeywordList() {
      return keywordList;
    }

    public void addKeyword(String keyword) {
      keywordList.add(keyword);
    }

    private ASCIIFileFormat.LineObject setDataFormat(String dataFormat) {
      ASCIIFileFormat.LineObject lo = ASCIIFileFormatParser.toLineObject(
          dataFormat);
      _dataFormat = lo;
      return lo;
    }

    protected boolean argyllEmulated = false;
    public void setArgyllEmulated(boolean emulated) {
      this.argyllEmulated = emulated;
    }

    public String toString() {
      StringBuilder builder = new StringBuilder();
      if (argyllEmulated) {
        builder.append("CTI1\n");
      }
      for (Map.Entry<String, String> ent : map.entrySet()) {
        if (ent.getKey().length() == 0) {
          continue;
        }
        if (ent.getKey().equals(Reserved.NUMBER_OF_FIELDS) ||
            ent.getKey().equals(Reserved.NUMBER_OF_SETS)) {
          builder.append(ent.getKey());
          builder.append('\t');
          builder.append(ent.getValue());
          builder.append('\n');
        }
        else {
          builder.append(ent.getKey());
          builder.append('\t');
          if (ent.getValue().length() != 0) {
//            builfer.append('\"');
            builder.append(ent.getValue());
//            builfer.append('\"');
          }
          builder.append('\n');
        }
      }
      for (String s : keywordList) {
        builder.append(Reserved.KEYWORD);
        builder.append('\t');
//        builfer.append('\"');
        builder.append(s);
//        builfer.append('\"');
        builder.append('\n');
      }
      if (_dataFormat != null) {
        builder.append(Reserved.BEGIN_DATA_FORMAT);
        builder.append('\n');
        builder.append(_dataFormat.line);
        builder.append('\n');
        builder.append(Reserved.END_DATA_FORMAT);
        builder.append('\n');
      }
      return builder.toString();
    }

  }

  public static class DataBlock {

    public DataBlock(String[] dataFormat, String[][] data) {
      initDataList(data);
      setDataFormat(dataFormat);
    }

    private void setDataFormat(String[] dataFormat) {
      dataFormatIndexMap = new HashMap<String, Integer> (dataFormat.length);
      int size = dataFormat.length;
      for (int x = 0; x < size; x++) {
        dataFormatIndexMap.put(dataFormat[x], Integer.valueOf(x));
      }
    }

    public DataBlock() {
      dataList = new ArrayList<String[]> ();
      dataFormatIndexMap = new HashMap<String, Integer> ();
    }

    protected List<String[]> dataList;
    protected void initDataList(String[][] data) {
      int size = data.length;
      dataList = new ArrayList<String[]> (size);
      for (int x = 0; x < size; x++) {
        dataList.add(data[x]);
      }
    }

    protected Map<String, Integer> dataFormatIndexMap;

    public int size() {
      return dataList.size();
    }

    /**
     * 取得索引值為index的該筆data
     * @param index int
     * @return String[]
     */
    public String[] getData(int index) {
      return dataList.get(index);
    }

    public void addData(String[] data) {
      dataList.add(data);
    }

    public void addData(String data) {
      ASCIIFileFormat.LineObject lo = ASCIIFileFormatParser.toLineObject(
          data);
      dataList.add(lo.stringArray);
    }

    /**
     * 取得索引值為index的該筆data,且欄位名稱為fieldName
     * @param index int
     * @param fieldName String
     * @return String
     */
    public String getDataField(int index, String fieldName) {
      Integer dataFormatIndex = dataFormatIndexMap.get(fieldName);
      if (dataFormatIndex == null) {
        return null;
      }
      return dataList.get(index)[dataFormatIndex];
    }

    public String toString() {
      int size = dataList.size();
      StringBuilder builder = new StringBuilder();
      builder.append(Reserved.BEGIN_DATA);
      builder.append('\n');
      for (int x = 0; x < size; x++) {
        String[] line = dataList.get(x);
        int lineSize = line.length;
        builder.append(line[0]);
        builder.append(' ');
        for (int y = 1; y < lineSize - 1; y++) {
          builder.append(line[y]);
          builder.append('\t');
        }
        builder.append(line[lineSize - 1]);
        builder.append('\n');
      }
      builder.append(Reserved.END_DATA);
      return builder.toString();
    }
  }

  protected DataBlock dataBlockParse(Header header) {
    int start = asciiFileFormat.getLine(Reserved.BEGIN_DATA).lineNumber;
    int end = asciiFileFormat.size() - 1;
    int size = end - start;

    String[][] data = new String[size][];
    for (int x = start; x < end; x++) {
      ASCIIFileFormat.LineObject lo = asciiFileFormat.getLine(x);
      data[x - start] = lo.stringArray;
    }
    DataBlock dataBlock = new DataBlock(header._dataFormat.stringArray, data);

    return dataBlock;
  }

  protected static interface Reserved {
//    String ORIGINATOR = "ORIGINATOR";
//    String DESCRIPTOR = "DESCRIPTOR";
//    String CREATED = "CREATED";
//    String MANUFACTURER = "MANUFACTURER";
//    String PROD_DATE = "PROD_DATE";
//    String SERIAL = "SERIAL";
//    String MATERIAL = "MATERIAL";
//    String INSTRUMENTATION = "INSTRUMENTATION";
//    String MEASUREMENT_SOURCE = "MEASUREMENT_SOURCE";
//    String PRINT_CONDITIONS = "PRINT_CONDITIONS";

    String NUMBER_OF_FIELDS = "NUMBER_OF_FIELDS";
    String BEGIN_DATA_FORMAT = "BEGIN_DATA_FORMAT";
    String END_DATA_FORMAT = "END_DATA_FORMAT";
    String NUMBER_OF_SETS = "NUMBER_OF_SETS";
    String BEGIN_DATA = "BEGIN_DATA";
    String END_DATA = "END_DATA";
    String KEYWORD = "KEYWORD";

    String OBSERVER_ANGLE = "OBSERVER_ANGLE";
    String LGOROWLENGTH = "LGOROWLENGTH";
  }

  public static void main(String[] args) throws IOException {
    LogoDataExchangeFormat logo = new LogoDataExchangeFormat(
        "cgats.template.ti1");
    System.out.println(logo);
  }

  DataBlock getDataBlock() {
    return dataBlock;
  }

  Header getHeader() {
    return header;
  }

  public void setDataFormat(String dataFormat) {
    ASCIIFileFormat.LineObject lo = header.setDataFormat(dataFormat);
    this.dataBlock.setDataFormat(lo.stringArray);
    this.header.setDataFormat(dataFormat);
  }

  public String getDataFormat() {
    return this.header._dataFormat.line;
  }
}
