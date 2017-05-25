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

    ActiveXComponent app = new ActiveXComponent("Word.Application"); //�Ұ�word
    String inFile = "c:/test.doc"; //�n�ഫ��word���
    String tpFile = "c:/test.htm"; //�{�ɤ��
    String otFile = "c:/test.xml"; //�ؼФ��
    boolean flag = false;
    try {
      app.setProperty("Visible", new Variant(false)); //�]�mword���i��
      Dispatch docs = app.getProperty("Documents").toDispatch();
      Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                                     new Object[] {inFile, new Variant(false),
                                     new Variant(true)}
                                     , new int[1]).toDispatch(); //���}word���
      Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {tpFile,
                      new Variant(8)}
                      , new int[1]); //�@��html�榡�O�s���{�ɤ��
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
      app = new ActiveXComponent("Excel.Application"); //�Ұ�excel
      try {
        app.setProperty("Visible", new Variant(false)); //�]�mexcel���i��
        Dispatch workbooks = app.getProperty("Workbooks").toDispatch();
        Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method,
                                            new Object[] {tpFile, new Variant(false),
                                            new Variant(true)}
                                            , new int[1]).toDispatch(); //���}�{�ɤ��
        Dispatch.invoke(workbook, "SaveAs", Dispatch.Method,
                        new Object[] {otFile, new Variant(46)}
                        , new int[1]); //�Hxml�榡�O�s��ؼФ��
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
