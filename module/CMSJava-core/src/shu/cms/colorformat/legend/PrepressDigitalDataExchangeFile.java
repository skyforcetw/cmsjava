package shu.cms.colorformat.legend;

import java.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * SOCS的資料格式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PrepressDigitalDataExchangeFile
    extends GretagMacbethAsciiFile {
  public String filename;
  public PrepressDigitalDataExchangeFile.Header pddeHeader;
  public PrepressDigitalDataExchangeFile.DataSet pddeDataSet;

  PrepressDigitalDataExchangeFile() {

  }

  public static class Header
      extends GretagMacbethAsciiFile.Header {
    public String originator;
    public String descriptor;
  }

  public static class DataSet
      extends GretagMacbethAsciiFile.DataSet {
    public boolean isSpectraData() {
      return numberOfFields == 2;
    }

    protected PrepressDigitalDataExchangeFile pddeMotherFile;

    public void setMotherFile(PrepressDigitalDataExchangeFile file) {
      this.pddeMotherFile = file;
//      System.out.println(motherFile.header);
      this.numberOfFields = pddeMotherFile.pddeHeader.numberOfFields;
    }

    public SpectraData getSpectraData(int index) {
      if (!isSpectraData()) {
        return null;
      }
      SpectraData spectraData = new SpectraData();
      ArrayList data = (ArrayList) dataSet.get(index);
//      System.out.println(data);
      spectraData.sampleID = Integer.parseInt( ( (String) data.get(0)).
                                              trim());
      spectraData.sampleName = ( (String) data.get(1)).trim();
      int count = 2;
//      System.out.println(data);

      double[] spectra = new double[31];
      for (int x = 0; x < 31; x++) {
        spectra[x] = Double.parseDouble( ( (String) data.get(x + count)).trim()) /
            100.;
      }
      spectraData.spectra = spectra;

      return spectraData;
    }
  }

}
