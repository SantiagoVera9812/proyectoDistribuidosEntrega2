package org.example;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicarArchivoService extends Remote{

    void replicarContenido(String archivoOrigen, String archivoDestino) throws RemoteException;
} 
