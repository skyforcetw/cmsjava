package shu.cms.colorformat.trans;

import java.io.*;
import java.util.*;
import javax.xml.bind.*;

import shu.cms.colorformat.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.legend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將SpectraWin軟體產生的.txt資料檔,轉換成CxF
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SpectraWinAsciiFile2CxF
    extends TransformTask {
  public final static String FROM_SPECTRAWIN = "from SpectraWin";

  public void transforming(String dir, String outputFilename) throws
      Exception {

    File dirFile = new File(dir);
    ArrayList<SpectraWinAsciiFile> swList = new ArrayList<SpectraWinAsciiFile> ();
    String[] fileList = dirFile.list();
    Arrays.sort(fileList, new FilenameComparator());
    int count = 1, len = fileList.length;
    int total = (int) (lengthOfTask * .9);

    for (String filename : fileList) {
      current = (int) (total * ( ( (double) count++) / len));

      SpectraWinAsciiParser parser = new SpectraWinAsciiParser(dirFile.
          getAbsolutePath() +
          "/" + filename);
      swList.add(parser.getSpectraWinAsciiFile());
    }
    SpectraWinAsciiFile[] swFiles = swList.toArray(new SpectraWinAsciiFile[
        swList.size()]);
    CXF cxf = CxFTransformer.spectraWinToCxF(swFiles);
    JAXBContext jc = JAXBContext.newInstance(
        "shu.cms.colorformat.cxf");

    Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                  Boolean.TRUE);

    m.marshal(cxf, new BufferedWriter(new FileWriter(outputFilename)));

    //結束
    current = lengthOfTask;
    done = true;
  }

  public static void main(String[] args) throws Exception {
    SpectraWinAsciiFile2CxF transfer = new SpectraWinAsciiFile2CxF();
    transfer.transforming("Reference Files/CIE/Illuminant",
                          "Reference Files/CIE/Illuminant/F.cxf");

  }
}

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 實作此Comparator的原因在於:
 * file回傳的fileList,其排序不符合PatchShower所擷取的檔案順序
 * 因此需要重新排序以恢復原本的順序
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
class FilenameComparator
    implements Comparator<String> {

  public int compare(String s1, String s2) {
    if (s1.length() != s2.length()) {
      return s1.length() - s2.length();
    }
    int len = Math.min(s1.length(), s2.length());
    for (int x = 0; x < len; x++) {
      char c1 = s1.charAt(x);
      char c2 = s2.charAt(x);
      if (c1 != c2) {
        return c1 - c2;

      }
    }
    return 0;
  }

}
