package auo.rmi.test;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

//import rmi.model.PersonEntity;
//import rmi.service.*;

//此為遠程對象的實現類，須繼承UnicastRemoteObject
public class PersonServiceImpl extends UnicastRemoteObject implements PersonService {

    public PersonServiceImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

//    @Override
    public List<PersonEntity> GetList() throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("Get Person Start!");
        List<PersonEntity> personList = new LinkedList<PersonEntity>();

        PersonEntity person1 = new PersonEntity();
        person1.setAge(25);
        person1.setId(0);
        person1.setName("Leslie");
        personList.add(person1);

        PersonEntity person2 = new PersonEntity();
        person2.setAge(25);
        person2.setId(1);
        person2.setName("Rose");
        personList.add(person2);

        return personList;
    }
}
