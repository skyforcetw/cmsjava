package auo.rmi.test;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

//�������{��H�եΪ����f�A�����~��Remote��
public interface PersonService extends Remote {
    public List<PersonEntity> GetList() throws RemoteException;
}
