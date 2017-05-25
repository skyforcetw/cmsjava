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
public interface RMIInterface
    extends Remote {
  // 實現一個加法
  public int sum(int a, int b) throws RemoteException;
}
