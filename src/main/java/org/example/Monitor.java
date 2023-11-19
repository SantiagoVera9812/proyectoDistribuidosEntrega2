package org.example;
import java.rmi.Naming;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso incorrecto. Debes proporcionar el tipo de sensor que vs a monitorear");
            System.exit(1);
        }
        String tipoSensor = args[0];
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB)) {
            subscriber.connect("tcp://localhost:5556"); // Se conecta al broker
            subscriber.subscribe(tipoSensor.getBytes()); // Suscribe a mensajes con la etiqueta "topicA"
            while (true) {
                String mensaje = new String(subscriber.recv());
                System.out.println("Mensaje recibido: " + mensaje);

                callRmiGuardarMensaje("archivo.txt", mensaje);
            }
        }
    }
    
     private static void callRmiGuardarMensaje(String nombreArchivo, String mensaje) {
        try {
            String serverUrl = "rmi://localhost:1237/GuardarMensajeService";
            GuardarMensajeService guardarMensajeService = (GuardarMensajeService) Naming.lookup(serverUrl);

            // Call the RMI method to save the message on the server
            guardarMensajeService.guardarMensajeEnArchivo(nombreArchivo, mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
