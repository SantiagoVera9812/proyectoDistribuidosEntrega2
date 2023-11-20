package org.example;
import java.rmi.Naming;
import java.util.Random;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso incorrecto. Debes proporcionar el tipo de sensor que vas a monitorear");
            System.exit(1);
        }
        String tipoSensor = args[0];
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
             ZMQ.Socket publisher = context.socket(ZMQ.PUB)) {
            subscriber.connect("tcp://localhost:5556"); // Se conecta al broker
            subscriber.subscribe(tipoSensor.getBytes()); // Suscribe a mensajes con la etiqueta "topicA"
            while (true) {
                String mensaje = new String(subscriber.recv());
                System.out.println("Mensaje recibido: " + mensaje);
                String [] splittedMessage = mensaje.split(" ");
                publisher.connect("tcp://10.43.101.128:5557"); // Se conecta al sistema calidad
                double medicion = Double.parseDouble(splittedMessage[1]);
                switch(tipoSensor){
                    case "temperatura":
                        if(medicion<68 || medicion>89){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    case "PH":
                        if(medicion<6.0 || medicion>8.0){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    case "oxigeno":
                        if(medicion<2 || medicion > 11){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    default:
                        System.out.println("Tipo de sensor no valido");
                        break;
                }
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
