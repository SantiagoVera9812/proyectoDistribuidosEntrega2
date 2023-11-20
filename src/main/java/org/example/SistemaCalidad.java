package org.example;

import org.zeromq.ZMQ;

public class SistemaCalidad {
    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB)) {
            subscriber.bind("tcp://*:5557"); // Se conecta a los monitores
            subscriber.subscribe("".getBytes()); // Suscribe a mensajes con la etiqueta "topicA"
            while (true) {
                byte[] message = subscriber.recv();
                String mensaje = new String(message);
                System.out.println(mensaje);
            }
        }
    }
}