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

  // �b�o���Ӥ�k�i��F��{
  public int sum(int a, int b) throws RemoteException {
    return a + b;
  }
}
