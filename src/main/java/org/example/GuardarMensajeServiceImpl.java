package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GuardarMensajeServiceImpl extends UnicastRemoteObject implements GuardarMensajeService {
    protected GuardarMensajeServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void guardarMensajeEnArchivo(String nombreArchivo, String mensaje) throws RemoteException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            writer.write(mensaje + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

