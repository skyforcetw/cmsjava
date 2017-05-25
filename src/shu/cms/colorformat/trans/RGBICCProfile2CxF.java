package shu.cms.colorformat.trans;

import java.io.*;
import javax.xml.bind.*;

import java.awt.color.*;

import shu.cms.colorformat.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.legend.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �i�H�N�ѼƦ�۾����ͪ�ICC Profile����DevD tag���X,
 * �t�~�s��CxF��
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RGBICCProfile2CxF
    extends TransformTask {
  public final static int DevD_TAG = 0x44657644;

  public void transforming(String dir, String outputFilename) throws
      Exception {

  }

  public void transform(String ICCProfileDir) throws IOException {
    File dir = new File(ICCProfileDir);
    String[] filenames = dir.list(new Filter());

    int count = 1, len = filenames.length;
    int total = (int) (lengthOfTask * .9);

    for (String s : filenames) {
      current = (int) (total * ( ( (double) count++) / len));

      String inputFilename = dir.getPath() + "/" + s;
      String outputFilename = dir.getPath() + "/" + s.replaceAll("icc", "cxf");
      transform(inputFilename, outputFilename);
    }

    //����
    current = lengthOfTask;
    done = true;
  }

  public static void transform(String ICCProfileFilename,
                               String outputCxFFilename) throws IOException {
    ICC_Profile profile = ICC_Profile.getInstance(ICCProfileFilename);
    byte[] devD = profile.getData(DevD_TAG);

    //���F�n�F�LParser�o�O�@��TestChart,�_�h�N�n�h��Parser�������{���X
    //���F�D�ֳt,�ҥH�γo�ا�J����k
    String devDString = new String(devD);
    devDString = "Logo TestChart\n" + devDString;

    GretagMacbethAsciiParser parser = new GretagMacbethAsciiParser(devDString.
        getBytes());
    GretagMacbethAsciiFile asciiFile = parser.getGretagMacbethAsciiFile();
    CXF cxf = CxFTransformer.RGBICCProfileToCxF(asciiFile);

    try {
      JAXBContext jc = JAXBContext.newInstance(
          "shu.cms.colorformat.cxf");

      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);

      m.marshal(cxf, new BufferedWriter(new FileWriter(outputCxFFilename)));
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
  }

  public static void main(String[] args) throws Exception {
    transform("M1210_20070430.icc", "test.cxf");
//    transform("Measurement Files/Camera/D70/CWF");

  }

  protected static class Filter
      implements FilenameFilter {
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be included in
     *   the file list; <code>false</code> otherwise.
     */
    public boolean accept(File dir, String name) {
      if (name.endsWith("icc")) {
        return true;
      }
      else {
        return false;
      }
    }

  }
}
