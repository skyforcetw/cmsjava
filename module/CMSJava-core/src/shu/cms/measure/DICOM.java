package shu.cms.measure;

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
public enum DICOM {
  //非DICOM模式, 全螢幕
  None,
  //正規DICOM模式, 背景為中性灰
  Normal,
  //同正規DICOM模式, 但背景為黑色
//  Black,
  //無視窗邊框的DICOM Black模式
//  UndecoratedBlack,
  //無視窗邊框的DICOM
  Undecorated
}
