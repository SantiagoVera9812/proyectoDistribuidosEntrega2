package org.example;

import org.zeromq.ZMQ;

public class SistemaPubSus {
    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket frontend = context.socket(ZMQ.SUB);
             ZMQ.Socket backend = context.socket(ZMQ.PUB)) {

            frontend.bind("tcp://*:5555");  // Espera conexiones de los publicadores
            backend.bind("tcp://*:5553");   // Espera conexiones de los suscriptores

            // Suscribirse a todos los mensajes de los publicadores
            frontend.subscribe("".getBytes());

            while (true) {
                // Recibe mensaje del publicador
                byte[] message = frontend.recv();
                String mensaje = new String(message);
                String[] splittedMessage = mensaje.split(" ");
                if (splittedMessage.length > 0) {
                    String tipoSensor = splittedMessage[0];
                    System.out.println(splittedMessage);
                    if(tipoSensor.equals("temperatura")){
                        backend.send("temperatura " + splittedMessage[1]);
                    } else if(tipoSensor.equals("PH")){
                        backend.send("PH " + splittedMessage[1]);
                    } else if(tipoSensor.equals("oxigeno")){
                        backend.send("oxigeno " + splittedMessage[1]);
                    } else{
                        System.out.println("Tipo de sensor inválido");
                    }
                } else {
                    System.out.println("El mensaje no contiene nada");
                }
            }
        }
    }
}
