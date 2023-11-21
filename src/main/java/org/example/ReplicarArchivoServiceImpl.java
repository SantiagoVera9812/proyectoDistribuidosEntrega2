package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReplicarArchivoServiceImpl extends UnicastRemoteObject implements ReplicarArchivoService {
    public ReplicarArchivoServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void replicarContenido(String archivoOrigen, String archivoDestino) throws RemoteException {
        try {

    
            Files.write(Paths.get(archivoDestino), "".getBytes());

            // Lee el contenido del archivo origen
            byte[] contenido = Files.readAllBytes(Paths.get(archivoOrigen));

            // Escribe el contenido en el archivo destino
            Files.write(Paths.get(archivoDestino), contenido);

           
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error al replicar el contenido: " + e.getMessage());
        }
    }
}

