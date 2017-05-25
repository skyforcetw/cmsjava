package auo.rmi.test;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

//此為遠程對象調用的接口，必須繼承Remote類
public interface PersonService extends Remote {
    public List<PersonEntity> GetList() throws RemoteException;
}
