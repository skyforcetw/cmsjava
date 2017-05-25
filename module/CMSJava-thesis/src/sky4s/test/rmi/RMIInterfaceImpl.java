package sky4s.test.rmi;

import java.rmi.*;
import java.rmi.server.*;

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
@SuppressWarnings( {"serial"})
public class RMIInterfaceImpl
    extends UnicastRemoteObject implements RMIInterface {
  public RMIInterfaceImpl() throws RemoteException {
    super();
  }

  // 在這兒對該方法進行了實現
  public int sum(int a, int b) throws RemoteException {
    return a + b;
  }
}
