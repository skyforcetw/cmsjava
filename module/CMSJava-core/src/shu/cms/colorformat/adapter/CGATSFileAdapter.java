package shu.cms.colorformat.adapter;

import java.io.*;
import java.util.*;

import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.independ.*;

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
public class CGATSFileAdapter
    extends LogoFileAdapter {
  public CGATSFileAdapter() {
    super();
  }

  public CGATSFileAdapter(String filename) {
    super(filename);
  }

  public CGATSFileAdapter(Reader reader, String resource) {
    super(reader, resource);
  }

  public CGATSFileAdapter(Reader reader) {
    super(reader);
  }

  public static void main(String[] args) {
    CGATSFileAdapter cgatsfileadapter = new CGATSFileAdapter(
        "ArgyllMeter.logo.ti3");
    for (CIEXYZ XYZ : cgatsfileadapter.getXYZList()) {
      System.out.println(XYZ);
    }
  }

  public List<CIEXYZ> getXYZList() {
    List<CIEXYZ> XYZList = super.getXYZList();
    String luminanceString = logoFile.getHeader(LogoFile.Reserved.LuminanceXYZ);
    int firstSpace = luminanceString.indexOf(' ');
    int secondSpace = luminanceString.indexOf(' ', firstSpace + 1);
    double luminance = Double.parseDouble(luminanceString.substring(firstSpace,
        secondSpace));
    for (CIEXYZ XYZ : XYZList) {
      XYZ.times(luminance / 100.);
    }
    return XYZList;
  }
}
