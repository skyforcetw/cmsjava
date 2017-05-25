package auo.rmi.test;


import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

//import rmi.service.*;
//import rmi.serviceImpl.*;

public class Server {

    public static void main(String[] args) {
        try {
            PersonService personService = new PersonServiceImpl();
            //註冊通訊端口
            LocateRegistry.createRegistry(6600);
            //註冊通訊路徑
            Naming.rebind("rmi://127.0.0.1:6600/PersonService", personService);
            System.out.println("Service Start!");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
