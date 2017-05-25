package sky4s.test.win32;

import java.io.*;

import com.jacob.activeX.*;
import com.jacob.com.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author vastview.com.tw
 * @version 1.0
 */
public class Exam_2 {

  public static void main(String[] args) {

    ActiveXComponent app = new ActiveXComponent("Word.Application"); //啟動word
    String inFile = "c:/test.doc"; //要轉換的word文件
    String tpFile = "c:/test.htm"; //臨時文件
    String otFile = "c:/test.xml"; //目標文件
    boolean flag = false;
    try {
      app.setProperty("Visible", new Variant(false)); //設置word不可見
      Dispatch docs = app.getProperty("Documents").toDispatch();
      Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                                     new Object[] {inFile, new Variant(false),
                                     new Variant(true)}
                                     , new int[1]).toDispatch(); //打開word文件
      Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {tpFile,
                      new Variant(8)}
                      , new int[1]); //作為html格式保存到臨時文件
      Variant f = new Variant(false);
      Dispatch.call(doc, "Close", f);
      flag = true;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      app.invoke("Quit", new Variant[] {});
    }

    if (flag) {
      app = new ActiveXComponent("Excel.Application"); //啟動excel
      try {
        app.setProperty("Visible", new Variant(false)); //設置excel不可見
        Dispatch workbooks = app.getProperty("Workbooks").toDispatch();
        Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method,
                                            new Object[] {tpFile, new Variant(false),
                                            new Variant(true)}
                                            , new int[1]).toDispatch(); //打開臨時文件
        Dispatch.invoke(workbook, "SaveAs", Dispatch.Method,
                        new Object[] {otFile, new Variant(46)}
                        , new int[1]); //以xml格式保存到目標文件
        Variant f = new Variant(false);
        Dispatch.call(workbook, "Close", f);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        app.invoke("Quit", new Variant[] {});
        try {
          File file = new File(tpFile);
          boolean result = file.delete();
        }
        catch (Exception e) {
        }
      }
    }
  }
}
