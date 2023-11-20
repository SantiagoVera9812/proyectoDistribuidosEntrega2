package org.example;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RmiServer {
    public static void main(String[] args) {
        try {
            GuardarMensajeService guardarMensajeService = new GuardarMensajeServiceImpl();

            LocateRegistry.createRegistry(1237);
            
            Naming.rebind("rmi://localhost:1237/GuardarMensajeService", guardarMensajeService);

            System.out.println("RMI Server is running on port 1237");

            while (true) {
                Thread.sleep(1000); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

