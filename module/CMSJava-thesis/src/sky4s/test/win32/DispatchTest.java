package sky4s.test.win32;

import com.jacob.activeX.*;
import com.jacob.com.*;

//import com.ms.com.*;
//import com.ms.activeX.*;

public class DispatchTest {
  public static void main(String[] args) {
    ActiveXComponent xl = new ActiveXComponent("Excel.Application");
    Dispatch xlo = (Dispatch) xl.getObject();
    try {
      System.out.println("version=" + xl.getProperty("Version"));
      System.out.println("version=" + Dispatch.get(xlo, "Version"));
      xl.setProperty("Visible", new Variant(true));
      Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
      Dispatch workbook = Dispatch.get(workbooks, "Add").toDispatch();
      Dispatch sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
      Dispatch a1 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
                                    new Object[] {"A1"},
                                    new int[1]).toDispatch();
      Dispatch a2 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
                                    new Object[] {"A2"},
                                    new int[1]).toDispatch();
      Dispatch.put(a1, "Value", "123.456");
      Dispatch.put(a2, "Formula", "=A1*2");
      System.out.println("a1 from excel:" + Dispatch.get(a1, "Value"));
      System.out.println("a2 from excel:" + Dispatch.get(a2, "Value"));
      Variant f = new Variant(false);
      Dispatch.call(workbook, "Close", f);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      xl.invoke("Quit", new Variant[] {});
    }
  }
}
