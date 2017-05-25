package sky4s.test.rmi;

import java.rmi.*;
import java.rmi.registry.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RMIServer {
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(8808); // 註冊端口
      RMIInterfaceImpl server = new RMIInterfaceImpl();
      // Binding
      Naming.rebind("//localhost:8808/RMI", server); // 將實現類綁到一個名字上去
      System.out.println("bind");
    }
    catch (java.net.MalformedURLException me) {
      System.out.println("Malformed URL:" + me.toString());
    }
    catch (RemoteException e) {
      System.out.println("Remote Exception:" + e.toString());
    }
  }
}
