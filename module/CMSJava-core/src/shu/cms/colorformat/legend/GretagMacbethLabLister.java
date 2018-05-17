package shu.cms.colorformat.legend;

import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來將GretagMacbethAsciiFile的Lab列出
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GretagMacbethLabLister {
  public static CIELab[] getLabs(GretagMacbethAsciiFile file) {
    GretagMacbethAsciiFile.DataSet dataSets = file.getDataSet();
    int size = dataSets.size();
    CIELab[] Labs = new CIELab[size];

    for (int x = 0; x < size; x++) {
      GretagMacbethAsciiFile.LabData labData = dataSets.getLabData(x);
      Labs[x] = new CIELab(labData._Lab);
    }

    return Labs;
  }

  public static void main(String[] args) {
  }
}
