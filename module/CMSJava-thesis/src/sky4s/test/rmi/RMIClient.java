package sky4s.test.rmi;

import java.rmi.*;

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
public class RMIClient {
  public static void main(String[] args) {
    try {
      String url = "//localhost:8808/RMI";
      RMIInterface RMIObject = (RMIInterface) Naming.lookup(url); // ´M±o¹ï¹³
      System.out.println(" 1 + 2 = " + RMIObject.sum(1, 2));
    }
    catch (RemoteException exc) {
      System.out.println("Lookup error: " + exc.toString());
    }
    catch (java.net.MalformedURLException ex) {
      System.out.println("Malformed URL: " + ex.toString());
    }
    catch (java.rmi.NotBoundException e) {
      System.out.println("Not Bound: " + e.toString());
    }
  }
}
