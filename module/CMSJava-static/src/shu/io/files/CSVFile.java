package shu.io.files;

import java.io.*;
import au.com.bytecode.opencsv.*;

/**
 * <p>Title: Colour Management System - static</p>
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
public class CSVFile
    implements FileExtractIF {
  public CSVFile(String filename) throws FileNotFoundException, IOException {
//    this.filename = filename;
//    reader = new FileReader(filename);
//    csvReader = new CSVReader(reader);
//    init();
    this(filename, CSVParser.DEFAULT_SEPARATOR);
  }

  public CSVFile(String filename, char separator) throws FileNotFoundException,
      IOException {
    this.filename = filename;
    reader = new FileReader(filename);
    csvReader = new CSVReader(reader, separator);
    init();
  }

  private void init() throws IOException {
    content = csvReader.readAll();
  }

  private java.util.List<java.lang.String[]> content;
  private String filename;
  private Reader reader;
  private CSVReader csvReader;

  public static void main(String[] args) {
//    CSVFile csvfile = new CSVFile();
  }

  public double getCell(int x, int y) {
    String string = getCellAsString(x, y);
    return Double.parseDouble(string);
  }

  public String getCellAsString(int x, int y) {
    String string = content.get(x)[y];
    return string;
  }

  public void setCell(int x, int y, String value) {

  }

  public void setCell(int x, int y, double value) {

  }

  public int getRows() {
    return content.size();
  }
}
