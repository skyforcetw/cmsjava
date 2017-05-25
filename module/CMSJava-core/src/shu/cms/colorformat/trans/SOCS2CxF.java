package shu.cms.colorformat.trans;

import java.io.*;

import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.legend.*;
import shu.cms.reference.spectra.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將SOCS的原始資料格式轉換到CxF
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SOCS2CxF {
  public static enum TRDatabase {
    Typical, Difference
  }

  public final static String SOCS_Dir = SpectraDatabase.SOCS.DIRECTORY;
  public final static String TRDatabase_Dir = SpectraDatabase.SOCS.
      TRDatabase_DIRECTORY;

  public void processSOCS() {
    processSOCS(new File(SOCS_Dir));
  }

  public void processTRDatabase(TRDatabase type) {
    processTRDatabase(new File(TRDatabase_Dir + '/' + type.toString()));
  }

  protected void processTRDatabase(File file) {
    File[] files = file.listFiles();

    try {

      for (int x = 0; x < files.length; x++) {
        if (files[x].isDirectory()) {
          processTRDatabase(files[x]);

        }
        else if (files[x].isFile() && files[x].getName().endsWith(".txt")) {
          transferTRDatabase(files[x]);
        }
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  protected void processSOCS(File file) {
    File[] files = file.listFiles();

    try {

      for (int x = 0; x < files.length; x++) {
        if (files[x].isDirectory()) {
          processSOCS(files[x]);

        }
        else if (files[x].isFile() && files[x].getName().endsWith(".int")) {
          transferSOCS(files[x]);
        }
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  protected void transferTRDatabase(File file) throws IOException {
    String filename = file.getCanonicalPath();
    int dot = filename.lastIndexOf(".txt");
    String cxfFilename = filename.substring(0, dot) + ".cxf";
    System.out.println(cxfFilename);

    TRDatabaseParser parser = new TRDatabaseParser(filename);
    TRDatabaseFile tr = parser.getTRDatabaseFile();
    CXF cxf = CxFTransformer.TRDatabaseToCxF(tr);
    CXFOperator.saveCXF(cxf, cxfFilename);
  }

  protected void transferSOCS(File file) throws IOException {
    String filename = file.getCanonicalPath();
    int dot = filename.lastIndexOf(".int");
    String cxfFilename = filename.substring(0, dot) + ".cxf";
    System.out.println(cxfFilename);

    PrepressDigitalDataExchangeParser parser = new
        PrepressDigitalDataExchangeParser(filename);
    PrepressDigitalDataExchangeFile pdde = parser.
        getPrepressDigitalDataExchangeFile();
    CXF cxf = CxFTransformer.PDDEToCxF(pdde);
    CXFOperator.saveCXF(cxf, cxfFilename);
  }

  public static void main(String[] args) {
    //將SOCS的三個資料集轉換到CxF 1.SOCS 2.Typical 3. Difference
    SOCS2CxF socs2cxf = new SOCS2CxF();
//    socs2cxf.processSOCS();
    socs2cxf.processTRDatabase(TRDatabase.Typical);
    socs2cxf.processTRDatabase(TRDatabase.Difference);
  }
}
