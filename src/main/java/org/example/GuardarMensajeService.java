package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GuardarMensajeService extends Remote {
    void guardarMensajeEnArchivo(String nombreArchivo, String mensaje) throws RemoteException;
}

